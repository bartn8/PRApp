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

use com\model\Context;
use com\model\db\exception\AuthorizationException;
use com\model\db\exception\InsertUpdateException;
use com\model\db\exception\NotAvailableOperationException;
use com\model\db\wrapper\WCliente;
use com\model\db\wrapper\WEntrata;
use com\model\db\wrapper\WEvento;
use com\model\db\wrapper\WPrevendita;
use com\model\db\wrapper\WPrevenditaPlus;
use com\model\db\wrapper\WStaff;
use com\model\db\wrapper\WStatisticheCassiereEvento;
use com\model\db\wrapper\WStatisticheCassiereStaff;
use com\model\db\wrapper\WStatisticheCassiereTotali;
use com\model\net\wrapper\NetWEntrata;
use com\utils\DateTimeImmutableAdapterJSON;
use InvalidArgumentException;
use PDO;
use PDOException;
use com\model\net\wrapper\NetWId;

class Cassiere extends Table
{

    /**
     * Verifica se l'utente è un cassiere dello staff.
     *
     * @param int $idStaff
     * @return boolean
     * @throws \Exception
     */
    private static function _isCassiere(int $idStaff): bool
    {
        $conn = parent::getConnection();

        $stmt = $conn->prepare("SELECT COUNT(*) AS conto FROM cassiere WHERE idUtente = :idUtente AND idStaff = :idStaff");
        $stmt->bindValue(":idUtente", Context::getContext()->getUtente()
            ->getId(), PDO::PARAM_INT);
        $stmt->bindValue(":idStaff", $idStaff, PDO::PARAM_INT);
        $stmt->execute();

        $count = (int) $stmt->fetch(PDO::FETCH_ASSOC)["conto"];

        $conn = NULL;

        return $count === 1;
    }

    /**
     * Verifica se l'utente è un cassiere dello staff.
     *
     * @param int $idPrevendita
     * @return boolean
     * @throws \Exception
     */
    private static function _isCassiereByPrevendita(int $idPrevendita): bool
    {
        $conn = parent::getConnection();

        $stmt = $conn->prepare("SELECT COUNT(*) AS conto FROM cassiere INNER JOIN evento ON evento.idStaff = cassiere.idStaff INNER JOIN prevendita ON prevendita.idEvento = evento.id WHERE cassiere.idUtente = :idUtente AND prevendita.id = :idPrevendita");
        $stmt->bindValue(":idUtente", Context::getContext()->getUtente()
            ->getId(), PDO::PARAM_INT);
        $stmt->bindValue(":idPrevendita", $idPrevendita, PDO::PARAM_INT);
        $stmt->execute();

        $count = (int) $stmt->fetch(PDO::FETCH_ASSOC)["conto"];

        $conn = NULL;

        return $count === 1;
    }

    /**
     * Verifica se l'utente è un cassiere dello staff.
     *
     * @param int $idEvento
     * @return boolean
     * @throws \Exception
     */
    private static function _isCassiereByEvento(int $idEvento): bool
    {
        $conn = parent::getConnection();

        $stmt = $conn->prepare("SELECT COUNT(*) AS conto FROM cassiere INNER JOIN evento ON evento.idStaff = cassiere.idStaff WHERE cassiere.idUtente = :idUtente AND evento.id = :idEvento");
        $stmt->bindValue(":idUtente", Context::getContext()->getUtente()
            ->getId(), PDO::PARAM_INT);
        $stmt->bindValue(":idEvento", $idEvento, PDO::PARAM_INT);
        $stmt->execute();

        $count = (int) $stmt->fetch(PDO::FETCH_ASSOC)["conto"];

        $conn = NULL;

        return $count === 1;
    }

    // /**
    // * Verifica se l'utente è un cassiere dello staff.
    // *
    // * @param WStaff $staff
    // * @throws InvalidArgumentException parametri nulli o non validi
    // * @throws NotAvailableOperationException non si è loggati nel sistema
    // * @throws PDOException problemi del database (errore di connessione, errore nel database)
    // * @return boolean
    // */
    // public static function isCassiere(WStaff $staff) : bool
    // {
    // if (is_null($staff))
    // throw new InvalidArgumentException("Parametri nulli.");

    // if (! ($staff instanceof WStaff))
    // throw new InvalidArgumentException("Parametri non validi.");

    // // Verifico che si è loggati nel sistema.
    // if (!Context::getContext()->isValid())
    // throw new NotAvailableOperationException("Utente non loggato.");

    // return self::_isCassiere($staff->getId());
    // }

    /**
     * Timbra una prevendita.
     *
     * @param NetWEntrata $entrata
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws NotAvailableOperationException l'utente non è loggato nel sistema.
     * @throws AuthorizationException l'utente non è cassiere per lo staff
     * @throws InsertUpdateException la prevendita è già stata timbrata
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return \com\model\db\wrapper\WEntrata Timbro d'entrata
     */
    public static function timbraEntrata(NetWEntrata $entrata): WEntrata
    {
        // Verifico i parametri
        if (is_null($entrata))
            throw new InvalidArgumentException("Parametri nulli.");

        if (! ($entrata instanceof NetWEntrata))
            throw new InvalidArgumentException("Parametri non validi.");

        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        // Verifico che l'utente sia cassiere dello staff.
        if (! self::_isCassiereByPrevendita($entrata->getIdPrevendita()))
            throw new AuthorizationException("L'utente non è cassiere dello staff.");

        // ---------------------------------------------------------------------------

        $ora = new \DateTimeImmutable("now");

        // Richiedo connessione con timestamp sincronizzato.
        $conn = parent::getConnection(true);

        // Controllo che sia l'evento giusto e che sia ancora aperto

        // Devo verificare il codice della prevendita.
        $stmtVerifica = $conn->prepare("SELECT codice, idEvento FROM prevendita WHERE id = :idPrevendita");
        $stmtVerifica->bindValue(":idPrevendita", $entrata->getIdPrevendita(), PDO::PARAM_INT);
        $stmtVerifica->execute();

        $fetch = $stmtVerifica->fetch(PDO::FETCH_ASSOC);

        if ($fetch["idEvento"] != $entrata->getIdEvento()) {
            $conn = NULL;
            throw new InsertUpdateException("Prevendita non valida: Evento non corrispondente.");
        }

        if ($fetch["codice"] !== $entrata->getCodiceAccesso()) {
            $conn = NULL;
            throw new InsertUpdateException("Prevendita non valida: Codice non valido.");
        }

        // Verifico la data di timbratura.
        $stmtVerificaTempo = $conn->prepare("SELECT inizio, fine FROM evento WHERE id = :idEvento");
        $stmtVerificaTempo->bindValue(":idEvento", $fetch["idEvento"], PDO::PARAM_INT);
        $stmtVerificaTempo->execute();

        $fetch = $stmtVerificaTempo->fetch(PDO::FETCH_ASSOC);

        $inizio = new DateTimeImmutableAdapterJSON(\DateTimeImmutable::createFromFormat(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP, $fetch["inizio"]));
        $fine = new DateTimeImmutableAdapterJSON(\DateTimeImmutable::createFromFormat(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP, $fetch["fine"]));

        if ($ora < $inizio->getDateTimeImmutable() || $ora > $fine->getDateTimeImmutable()) {
            $conn = NULL;
            //throw new InsertUpdateException("Prevendita non valida: Evento finito o non ancora iniziato. (".$inizio->getDateTimeImmutable()->format(\DateTime::ATOM)." < ".$ora->format(\DateTime::ATOM)." < ".$fine->getDateTimeImmutable()->format(\DateTime::ATOM).")");
            throw new InsertUpdateException("Prevendita non valida: Evento finito o non ancora iniziato.");
        }

        // Ora che ho verificato il codice posso TIMBRARE la prevendita.
        $stmtTimbro = $conn->prepare("INSERT INTO entrata (idCassiere, idPrevendita) VALUES (:idCassiere, :idPrevendita)");
        $stmtTimbro->bindValue(":idCassiere", Context::getContext()->getUtente()
            ->getId(), PDO::PARAM_INT);
        $stmtTimbro->bindValue(":idPrevendita", $entrata->getIdPrevendita(), PDO::PARAM_INT);

        // Verifico che la prevendita non sia già stata inserita.
        try {
            $stmtTimbro->execute();
        } catch (PDOException $ex) {
            // Mi assicuro di chiudere la connessione. Anche se teoricamente lo scope cancellerebbe comunque i riferimenti.
            $conn = NULL;

            if ($ex->getCode() == Cassiere::UNIQUE_CODE || $ex->getCode() == Cassiere::INTEGRITY_CODE) // Codice di integrità.
                throw new InsertUpdateException("Prevendita già timbrata.");

            throw $ex;
        }

        $conn = NULL;

        return $entrata->getWEntrata(Context::getContext()->getUtente()
            ->getId(), new DateTimeImmutableAdapterJSON(new \DateTimeImmutable("now")));
    }

    /**
     * Restituisce i dati del cliente associati alla prevendita inserita.
     *
     * @param
     *            NetWId prevendita
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws NotAvailableOperationException l'utente non è loggato nel sistema.
     * @throws AuthorizationException l'utente non è cassiere per lo staff
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return ?WCliente Restituisce i dati del cliente associati. Restituisce NULL se i dati non sono stati trovati.
     */
    public static function getDatiCliente(NetWId $prevendita): ?WCliente
    {
        // Verifico i parametri
        if (is_null($prevendita))
            throw new InvalidArgumentException("Parametri nulli.");

        if (! ($prevendita instanceof NetWId))
            throw new InvalidArgumentException("Parametri non validi.");

        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        // Verifico che l'utente sia cassiere dello staff.
        if (! self::_isCassiereByPrevendita($prevendita->getId()))
            throw new AuthorizationException("L'utente non è cassiere dello staff.");

        // ---------------------------------------------------------------------------

        $conn = parent::getConnection();

        // Prima recupero l'identificativo del cliente dal database. Non mi fido del wrapper.
        $stmtRecuperoDati = $conn->prepare("SELECT idCliente FROM prevendita WHERE id = :idPrevendita");
        $stmtRecuperoDati->bindValue(":idPrevendita", $prevendita->getId(), PDO::PARAM_INT);
        $stmtRecuperoDati->execute();

        $idCliente = $stmtRecuperoDati->fetch(PDO::FETCH_ASSOC)["idCliente"];

        $stmtSelezione = $conn->prepare("SELECT id, idStaff, nome, cognome, telefono, dataDiNascita, codiceFiscale, timestampInserimento FROM cliente WHERE id = :idCliente");
        $stmtSelezione->bindValue(":idCliente", $idCliente);
        $stmtSelezione->execute();

        $result = NULL;

        if (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $result = WCliente::of($riga);
        }

        $conn = NULL;

        return $result;
    }

    /**
     * Restituisce le statistiche totali del cassiere.
     *
     * @throws NotAvailableOperationException l'utente non è loggato nel sistema.
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return ?WStatisticheCassiereTotali Restituisce il wrapper. Se non sono disponibili statistiche restituisce NULL.
     */
    public static function getStatisticheCassiereTotali(): ?WStatisticheCassiereTotali
    {
        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

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

        $stmtSelezione = $conn->prepare("SELECT idUtente, entrate FROM statisticheCassiereTotali WHERE idUtente = :idUtente");
        $stmtSelezione->bindValue(":idUtente", Context::getContext()->getUtente()
            ->getId(), PDO::PARAM_INT);
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
     * @param NetWId $staff
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws NotAvailableOperationException l'utente non è loggato nel sistema.
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return ?WStatisticheCassiereStaff Restituisce il wrapper. Se non sono disponibili statistiche restituisce NULL.
     */
    public static function getStatisticheCassiereStaff(NetWId $staff): ?WStatisticheCassiereStaff
    {
        // Verifico i parametri
        if (is_null($staff))
            throw new InvalidArgumentException("Parametri nulli.");

        if (! ($staff instanceof NetWId))
            throw new InvalidArgumentException("Parametri non validi.");

        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

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
        $stmtSelezione->bindValue(":idUtente", Context::getContext()->getUtente()
            ->getId(), PDO::PARAM_INT);
        $stmtSelezione->bindValue(":idStaff", $staff->getId(), PDO::PARAM_INT);
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
     * @param NetWId $evento
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws NotAvailableOperationException l'utente non è loggato nel sistema.
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return ?WStatisticheCassiereEvento Restituisce il wrapper. Se non sono disponibili statistiche restituisce NULL.
     */
    public static function getStatisticheCassiereEvento(NetWId $evento): ?WStatisticheCassiereEvento
    {
        // Verifico i parametri
        if (is_null($evento))
            throw new InvalidArgumentException("Parametri nulli.");

        if (! ($evento instanceof NetWId))
            throw new InvalidArgumentException("Parametri non validi.");

        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

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
        $stmtSelezione->bindValue(":idUtente", Context::getContext()->getUtente()
            ->getId(), PDO::PARAM_INT);
        $stmtSelezione->bindValue(":idEvento", $evento->getId(), PDO::PARAM_INT);
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
     * @param NetWId $evento
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws NotAvailableOperationException l'utente non è loggato nel sistema.
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return WEntrata[] Lista con le entrate per l'evento svolte
     */
    public static function getEntrateSvolte(NetWId $evento): array
    {
        // Verifico i parametri
        if (is_null($evento))
            throw new InvalidArgumentException("Parametri nulli.");

        if (! ($evento instanceof NetWId))
            throw new InvalidArgumentException("Parametri non validi.");

        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        $conn = parent::getConnection();

        $stmtSelezione = $conn->prepare("SELECT idCassiere, idPrevendita, timestampEntrata FROM entrata INNER JOIN prevendita ON prevendita.id = entrata.idPrevendita WHERE entrata.idCassiere = :idCassiere AND prevendita.idEvento = :idEvento");
        $stmtSelezione->bindValue(":idCassiere", Context::getContext()->getUtente()
            ->getId(), PDO::PARAM_INT);
        $stmtSelezione->bindValue(":idEvento", $evento->getId(), PDO::PARAM_INT);
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
     * @param NetWId $evento
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws NotAvailableOperationException l'utente non è loggato nel sistema.
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws AuthorizationException l'utente non è cassiere per lo staff
     * @return array Lista delle prevendite dell'evento.
     */
    public static function getPrevenditeEvento(NetWId $evento): array
    {
        // Verifico i parametri
        if (is_null($evento))
            throw new InvalidArgumentException("Parametri nulli.");

        if (! ($evento instanceof NetWId))
            throw new InvalidArgumentException("Parametri non validi.");

        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        // Verifico che l'utente sia cassiere dello staff.
        if (! self::_isCassiereByEvento($evento->getId()))
            throw new AuthorizationException("L'utente non è cassiere dello staff.");

        $conn = parent::getConnection();

        //Vecchia Query: 
        $query = "SELECT id, idEvento, idPR, idCliente, idTipoPrevendita, codice, stato, timestampUltimaModifica FROM prevendita WHERE idEvento = :idEvento";


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
        $stmtSelezione->bindValue(":idEvento", $evento->getId(), PDO::PARAM_INT);
        $stmtSelezione->execute();

        $result = array();

        while (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $result[] = WPrevendita::of($riga);
        }

        $conn = NULL;

        return $result;
    }


    public static function getInformazioniPrevendita(NetWId $prevendita) : ?WPrevenditaPlus
    {
        // Verifico i parametri
        if (is_null($prevendita))
            throw new InvalidArgumentException("Parametri nulli.");

        if (! ($prevendita instanceof NetWId))
            throw new InvalidArgumentException("Parametri non validi.");

        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        // Verifico che l'utente sia cassiere dello staff.
        if (! self::_isCassiereByPrevendita($prevendita->getId()))
            throw new AuthorizationException("L'utente non è cassiere dello staff.");

        $conn = parent::getConnection();

        $query = <<<EOT
        SELECT prevendita.id AS id, prevendita.idEvento AS idEvento, evento.nome AS nomeEvento, prevendita.idPR AS idPR, 
        utente.nome AS nomePR, utente.cognome AS cognomePR, prevendita.idCliente AS idCliente, cliente.nome AS nomeCliente, 
        cliente.cognome AS cognomeCliente, prevendita.idTipoPrevendita AS idTipoPrevendita, tipoPrevendita.nome AS nomeTipoPrevendita, 
        tipoPrevendita.prezzo AS prezzoTipoPrevendita, prevendita.codice AS codice, prevendita.stato AS stato 
        FROM prevendita 
        LEFT JOIN cliente ON cliente.id = prevendita.idCliente 
        INNER JOIN evento ON evento.id = prevendita.idEvento 
        INNER JOIN utente ON utente.id = prevendita.idPR 
        INNER JOIN tipoPrevendita ON tipoPrevendita.id = prevendita.idTipoPrevendita 
        WHERE prevendita.id = :idPrevendita
EOT;

        $stmtSelezione = $conn->prepare($query);
        $stmtSelezione->bindValue(":idPrevendita", $prevendita->getId(), PDO::PARAM_INT);
        $stmtSelezione->execute();

        $result = NULL;

        if (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $result = WPrevenditaPlus::of($riga);
        }

        $conn = NULL;

        return $result;
    }

    private function __construct()
    {}
}
