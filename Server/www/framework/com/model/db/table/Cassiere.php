<?php

/*
 * PRApp  Copyright (C) 2019  Luca Bartolomei
 *
 * This file is part of PRApp.
 *
 *     PRApp is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     PRApp is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with PRApp.  If not, see <http://www.gnu.org/licenses/>.
 */

namespace com\model\db\table;

use PDO;
use PDOException;
use com\model\Context;
use com\model\db\table\Table;
use InvalidArgumentException;
use com\model\db\table\Cassiere;
use com\model\db\wrapper\WStaff;
use com\model\db\wrapper\WEvento;
use com\model\net\wrapper\NetWId;
use com\model\db\enum\StatoEvento;
use com\model\db\wrapper\WCliente;
use com\model\db\wrapper\WEntrata;
use com\model\db\wrapper\WPrevendita;
use com\model\db\enum\StatoPrevendita;
use com\model\net\wrapper\NetWEntrata;
use com\model\db\wrapper\WPrevenditaPlus;
use com\utils\DateTimeImmutableAdapterJSON;
use com\model\db\exception\InsertUpdateException;
use com\model\db\exception\AuthorizationException;
use com\model\db\wrapper\WStatisticheCassiereStaff;
use com\model\db\wrapper\WStatisticheCassiereEvento;
use com\model\db\wrapper\WStatisticheCassiereTotali;
use com\model\db\exception\NotAvailableOperationException;

class Cassiere extends Table
{

    /**
     * Timbra una prevendita.
     *
     * @param NetWEntrata $entrata
     * @param int $utente
     * @throws InsertUpdateException la prevendita è già stata timbrata oppure non valida
     * @throws NotAvailableOperationException dati non congruenti
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return \com\model\db\wrapper\WEntrata Timbro d'entrata
     */
    public static function timbraEntrata(NetWEntrata $entrata, int $utente, int $idEvento): WEntrata
    {
        $ora = new \DateTimeImmutable("now");

        // Richiedo connessione con timestamp sincronizzato.
        $conn = parent::getConnection(true);

        // Controllo che sia l'evento giusto e che sia ancora aperto

        // Devo verificare il codice della prevendita.
        // Devo anche verificare che la prevendita sia valida oppure consegnata.
        $stmtVerifica = $conn->prepare("SELECT codice, idEvento, stato FROM prevendita WHERE id = :idPrevendita");
        $stmtVerifica->bindValue(":idPrevendita", $entrata->getIdPrevendita(), PDO::PARAM_INT);
        $stmtVerifica->execute();

        if ($stmtVerifica->rowCount() > 0) {
            $fetch = $stmtVerifica->fetch(PDO::FETCH_ASSOC);

            if ($fetch["idEvento"] != $idEvento) {
                $conn = NULL;
                //Ho usato NotAvailableOperationException perché:
                //AuthorizationException si riferisce all'accesso di dati non consentito
                //InsertUpdateException riguarda quando il db restituisce errori di integrità
                throw new NotAvailableOperationException("L'evento non corrisponde a quello selezionato");
            }
    
            if ($fetch["codice"] !== $entrata->getCodiceAccesso()) {
                $conn = NULL;
                throw new NotAvailableOperationException("Prevendita non valida: Codice non valido.");
            }
        }else{
            throw new NotAvailableOperationException("Prevendita non trovata");
        }

        //data timbratura e verifica stato già verificati da db.

        // Ora che ho verificato il codice posso TIMBRARE la prevendita.
        $stmtTimbro = $conn->prepare("INSERT INTO entrata (idCassiere, idPrevendita) VALUES (:idCassiere, :idPrevendita)");
        $stmtTimbro->bindValue(":idCassiere", $utente, PDO::PARAM_INT);
        $stmtTimbro->bindValue(":idPrevendita", $entrata->getIdPrevendita(), PDO::PARAM_INT);

        // Verifico che la prevendita non sia già stata inserita.
        try {
            $stmtTimbro->execute();
        } catch (PDOException $ex) {
            // Mi assicuro di chiudere la connessione. Anche se teoricamente lo scope cancellerebbe comunque i riferimenti.
            $conn = NULL;

            if ($ex->getCode() == Cassiere::UNIQUE_CODE || $ex->getCode() == Cassiere::INTEGRITY_CODE) // Codice di integrità.
                throw new InsertUpdateException("Prevendita già timbrata.");

                if ($ex->getCode() == Cassiere::DATA_NON_VALIDA_CODE || $ex->getCode() == Cassiere::STATO_NON_VALIDO_CODE) // Codice di integrità.
                throw new InsertUpdateException($ex->getMessage());

            throw $ex;
        }

        $conn = NULL;

        return $entrata->getWEntrata($utente, new DateTimeImmutableAdapterJSON(new \DateTimeImmutable("now")));
    }

    //getDatiCliente rimosso

    /**
     * Restituisce le statistiche totali del cassiere.
     *
     * @param int $utente membro che ha richiesto le statistiche
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return ?WStatisticheCassiereTotali Restituisce il wrapper. Se non sono disponibili statistiche restituisce NULL.
     */
    public static function getStatisticheCassiereTotali(int $utente): ?WStatisticheCassiereTotali
    {
        $conn = parent::getConnection();

        //Query XAMPP:
        //$query = "SELECT idUtente, entrate FROM statisticheCassiereTotali WHERE idUtente = :idUtente";

        //Query ALTERVISTA:
        $query = <<<EOT
        SELECT T.idUtente, T.entrate 
        FROM (SELECT T1.idUtente, SUM(T1.entrate) AS entrate
            FROM (SELECT T2.idUtente, T2.idStaff, SUM(T2.entrate) AS entrate
                FROM (SELECT cassiere.idUtente AS idUtente, cassiere.idStaff AS idStaff, evento.id AS idEvento, COUNT(entrata.seq) AS entrate
                    FROM cassiere
                    INNER JOIN entrata ON entrata.idCassiere = cassiere.idUtente
                    INNER JOIN prevendita ON prevendita.id = entrata.idPrevendita
                    INNER JOIN evento ON evento.idStaff = cassiere.idStaff AND prevendita.idEvento = evento.id
                    GROUP BY cassiere.idUtente, cassiere.idStaff, evento.id) AS T2
                GROUP BY T2.idUtente, T2.idStaff) AS T1
            GROUP BY T1.idUtente) AS T
        WHERE T.idUtente = :idUtente
        
EOT;

        $stmtSelezione = $conn->prepare($query);
        $stmtSelezione->bindValue(":idUtente", $utente, PDO::PARAM_INT);
        $stmtSelezione->execute();

        $result = NULL;

        if (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $result = WStatisticheCassiereTotali::of($riga);
        }

        $conn = NULL;

        return $result;
    }

    /**
     * Restituisce le statistiche del cassiere in uno staff.
     *
     * @param int $utente
     * @param int $staff
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return ?WStatisticheCassiereStaff Restituisce il wrapper. Se non sono disponibili statistiche restituisce NULL.
     */
    public static function getStatisticheCassiereStaff(int $utente, int $staff): ?WStatisticheCassiereStaff
    {
        $conn = parent::getConnection();

        //Query XAMPP:
        //$query = "SELECT idUtente, idStaff, entrate FROM statisticheCassiereStaff WHERE idUtente = :idUtente AND idStaff = :idStaff";

        //Query ALTERVISTA:
        $query = <<<EOT
        SELECT T.idUtente, T.idStaff, T.entrate 
        FROM (SELECT T1.idUtente, T1.idStaff, SUM(T1.entrate) AS entrate
            FROM (SELECT cassiere.idUtente AS idUtente, cassiere.idStaff AS idStaff, evento.id AS idEvento, COUNT(entrata.seq) AS entrate
                FROM cassiere
                INNER JOIN entrata ON entrata.idCassiere = cassiere.idUtente
                INNER JOIN prevendita ON prevendita.id = entrata.idPrevendita
                INNER JOIN evento ON evento.idStaff = cassiere.idStaff AND prevendita.idEvento = evento.id
                GROUP BY cassiere.idUtente, cassiere.idStaff, evento.id) AS T1
            GROUP BY T1.idUtente, T1.idStaff) AS T
        WHERE T.idUtente = :idUtente AND T.idStaff = :idStaff
EOT;

        $stmtSelezione = $conn->prepare($query);
        $stmtSelezione->bindValue(":idUtente", $utente, PDO::PARAM_INT);
        $stmtSelezione->bindValue(":idStaff", $staff, PDO::PARAM_INT);
        $stmtSelezione->execute();

        $result = NULL;

        if (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $result = WStatisticheCassiereStaff::of($riga);
        }

        $conn = NULL;

        return $result;
    }

    /**
     * Restituisce le statistiche del cassiere in un evento.
     *
     * @param int $utente
     * @param int $evento
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return ?WStatisticheCassiereEvento Restituisce il wrapper. Se non sono disponibili statistiche restituisce NULL.
     */
    public static function getStatisticheCassiereEvento(int $utente, int $evento): ?WStatisticheCassiereEvento
    {
        $conn = parent::getConnection();

        //Query XAMPP:
        //$query = "SELECT idUtente, idStaff, idEvento, entrate FROM statisticheCassiereEvento  WHERE idUtente = :idUtente AND idEvento = :idEvento";

        //Query ALTERVISTA:
        $query = <<<EOT
        SELECT T.idUtente, T.idStaff, T.idEvento, T.entrate 
        FROM (SELECT cassiere.idUtente AS idUtente, cassiere.idStaff AS idStaff, evento.id AS idEvento, COUNT(entrata.seq) AS entrate
            FROM cassiere
            INNER JOIN entrata ON entrata.idCassiere = cassiere.idUtente
            INNER JOIN prevendita ON prevendita.id = entrata.idPrevendita
            INNER JOIN evento ON evento.idStaff = cassiere.idStaff AND prevendita.idEvento = evento.id
            GROUP BY cassiere.idUtente, cassiere.idStaff, evento.id)  AS T
        WHERE T.idUtente = :idUtente AND T.idEvento = :idEvento
EOT;

        $stmtSelezione = $conn->prepare($query);
        $stmtSelezione->bindValue(":idUtente", $utente, PDO::PARAM_INT);
        $stmtSelezione->bindValue(":idEvento", $evento, PDO::PARAM_INT);
        $stmtSelezione->execute();

        $result = NULL;

        if (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $result = WStatisticheCassiereEvento::of($riga);
        }

        $conn = NULL;

        return $result;
    }

    /**
     * Restitusice la lista delle entrate svolte dal cassire.
     *
     * @param int $utente
     * @param int $evento
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return WEntrata[] Lista con le entrate per l'evento svolte
     */
    public static function getEntrateSvolte(int $utente, int $evento): array
    {
        $conn = parent::getConnection();

        $stmtSelezione = $conn->prepare("SELECT idCassiere, idPrevendita, timestampEntrata FROM entrata INNER JOIN prevendita ON prevendita.id = entrata.idPrevendita WHERE entrata.idCassiere = :idCassiere AND prevendita.idEvento = :idEvento");
        $stmtSelezione->bindValue(":idCassiere", $utente, PDO::PARAM_INT);
        $stmtSelezione->bindValue(":idEvento", $evento, PDO::PARAM_INT);
        $stmtSelezione->execute();

        $result = array();

        while (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $result[] = WEntrata::of($riga);
        }

        $conn = NULL;

        return $result;
    }

    /**
     * Restituisce la lista delle prevendite di un evento.
     * Da utilizzare in caso di inserimento manuale.
     *
     * @param int $evento
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return array Lista delle prevendite dell'evento.
     */
    public static function getPrevenditeEvento(int $evento): array
    {
        $conn = parent::getConnection();
 
        $query = "SELECT id, idEvento, idPR, nomeCliente, cognomeCliente, idTipoPrevendita, codice, stato, timestampUltimaModifica FROM prevendita WHERE idEvento = :idEvento";


        //Query modificata con nome e cognome cliente: nuova versione WPrevendita.
        /*
        $query = <<<EOT
        SELECT p.id, p.idEvento, p.idPR, p.idCliente, c.nome AS nomeCliente, c.cognome AS cognomeCliente, p.idTipoPrevendita, p.codice, p.stato, p.timestampUltimaModifica
        FROM prevendita AS p 
        LEFT JOIN cliente AS c ON  p.idCliente = c.id
        WHERE idEvento = :idEvento
EOT;
*/

        $stmtSelezione = $conn->prepare($query);
        $stmtSelezione->bindValue(":idEvento", $evento, PDO::PARAM_INT);
        $stmtSelezione->execute();

        $result = array();

        while (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $result[] = WPrevendita::of($riga);
        }

        $conn = NULL;

        return $result;
    }

    /**
     * Restituisce le informazioni su una prevendita.
     * Non effettua alcun controllo.
     * 
     * @param int $prevendita
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return WPrevenditaPlus|NULL prevendita con info aggiuntive
     */
    public static function getInformazioniPrevendita(int $prevendita) : ?WPrevenditaPlus
    {
        $conn = parent::getConnection();

        $query = <<<EOT
        SELECT prevendita.id AS id, prevendita.idEvento AS idEvento, evento.nome AS nomeEvento, prevendita.idPR AS idPR, 
        utente.nome AS nomePR, utente.cognome AS cognomePR, prevendita.nomeCliente AS nomeCliente, prevendita.cognomeCliente AS cognomeCliente, 
        prevendita.idTipoPrevendita AS idTipoPrevendita, tipoPrevendita.nome AS nomeTipoPrevendita, 
        tipoPrevendita.prezzo AS prezzoTipoPrevendita, prevendita.codice AS codice, prevendita.stato AS stato 
        FROM prevendita 
        INNER JOIN evento ON evento.id = prevendita.idEvento 
        INNER JOIN utente ON utente.id = prevendita.idPR 
        INNER JOIN tipoPrevendita ON tipoPrevendita.id = prevendita.idTipoPrevendita 
        WHERE prevendita.id = :idPrevendita
EOT;

        $stmtSelezione = $conn->prepare($query);
        $stmtSelezione->bindValue(":idPrevendita", $prevendita, PDO::PARAM_INT);
        $stmtSelezione->execute();

        $result = NULL;

        if (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $result = WPrevenditaPlus::of($riga);
        }

        $conn = NULL;

        return $result;
    }

    /**
     * Restituisce la lista delle prevendite entrate
     * 
     * @param int $evento
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return WPrevenditaPlus[] Lista delle prevendite dell'evento.
     */
    public static function getListaPrevenditeEntrate(int $evento) : array
    {
        $conn = parent::getConnection();

        //Query: 
        $query = <<<EOT
        SELECT prevendita.id AS id, prevendita.idEvento AS idEvento, evento.nome AS nomeEvento, prevendita.idPR AS idPR, 
        utente.nome AS nomePR, utente.cognome AS cognomePR, prevendita.nomeCliente AS nomeCliente, prevendita.cognomeCliente AS cognomeCliente, 
        prevendita.idTipoPrevendita AS idTipoPrevendita, tipoPrevendita.nome AS nomeTipoPrevendita, 
        tipoPrevendita.prezzo AS prezzoTipoPrevendita, prevendita.codice AS codice, prevendita.stato AS stato 
        FROM prevendita 
        INNER JOIN evento ON evento.id = prevendita.idEvento 
        INNER JOIN utente ON utente.id = prevendita.idPR 
        INNER JOIN tipoPrevendita ON tipoPrevendita.id = prevendita.idTipoPrevendita 
        INNER JOIN entrata ON entrata.idPrevendita = prevendita.id
        WHERE evento.id = :idEvento 
        ORDER BY entrata.timestampEntrata ASC
EOT;

        $stmtSelezione = $conn->prepare($query);
        $stmtSelezione->bindValue(":idEvento", $evento, PDO::PARAM_INT);
        $stmtSelezione->execute();

        $result = array();

        while (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $result[] = WPrevenditaPlus::of($riga);
        }

        $conn = NULL;

        return $result;
    }

    /**
     * Restituisce la lista delle prevendite non entrate
     * 
     * @param int $evento
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return WPrevenditaPlus[] Lista delle prevendite non dell'evento.
     */
    public static function getListaPrevenditeNonEntrate(int $evento) : array
    {
        $conn = parent::getConnection();

        //Query: 
        $query = <<<EOT
        SELECT prevendita.id AS id, prevendita.idEvento AS idEvento, evento.nome AS nomeEvento, prevendita.idPR AS idPR, 
        utente.nome AS nomePR, utente.cognome AS cognomePR, prevendita.nomeCliente AS nomeCliente, prevendita.cognomeCliente AS cognomeCliente, 
        prevendita.idTipoPrevendita AS idTipoPrevendita, tipoPrevendita.nome AS nomeTipoPrevendita, 
        tipoPrevendita.prezzo AS prezzoTipoPrevendita, prevendita.codice AS codice, prevendita.stato AS stato 
        FROM prevendita 
        INNER JOIN evento ON evento.id = prevendita.idEvento 
        INNER JOIN utente ON utente.id = prevendita.idPR 
        INNER JOIN tipoPrevendita ON tipoPrevendita.id = prevendita.idTipoPrevendita 
        WHERE evento.id = :idEvento AND prevendita.id NOT IN (SELECT entrata.idPrevendita FROM entrata)
EOT;

        $stmtSelezione = $conn->prepare($query);
        $stmtSelezione->bindValue(":idEvento", $evento, PDO::PARAM_INT);
        $stmtSelezione->execute();

        $result = array();

        while (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $result[] = WPrevenditaPlus::of($riga);
        }

        $conn = NULL;

        return $result;
    }

    private function __construct()
    {}
}
