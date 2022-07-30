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
use com\model\db\table\PR;
use com\model\db\table\Table;
use InvalidArgumentException;
use com\model\net\wrapper\NetWId;
use com\model\db\wrapper\WCliente;
use com\model\db\wrapper\WPrevendita;
use com\model\db\wrapper\WPrevenditaPlus;
use com\utils\DateTimeImmutableAdapterJSON;
use com\model\db\wrapper\WStatistichePRStaff;
use com\model\db\wrapper\WStatistichePREvento;
use com\model\db\wrapper\WStatistichePRTotali;
use com\model\db\wrapper\insert\InsertWCliente;
use com\model\db\exception\InsertUpdateException;
use com\model\db\exception\AuthorizationException;
use com\model\db\wrapper\insert\InsertWPrevendita;
use com\model\db\wrapper\update\UpdateWPrevendita;
use com\model\net\wrapper\insert\InsertNetWCliente;
use com\model\net\wrapper\NetWFiltriStatoPrevendita;
use com\model\net\wrapper\insert\InsertNetWPrevendita;
use com\model\net\wrapper\update\UpdateNetWPrevendita;
use com\model\db\exception\NotAvailableOperationException;
use com\model\db\wrapper\custom\CustomWFiltroStatoPrevendita;

class PR extends Table
{
    //Rimosso aggiungiCliente

    /**
     * Aggiunge una prevendita per un cliente.
     *
     * @param InsertNetWPrevendita $prevendita
     * @param int $idEvento
     * @param int $idUtente pr che sta aggiungendo la prevendita
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws InsertUpdateException la prevendita non è valida per l'inserimento
     * @return WPrevendita Prevendita completa
     */
    public static function aggiungiPrevendita(InsertNetWPrevendita $prevendita, int $idEvento, int $idUtente): WPrevendita
    {
        $conn = parent::getConnection(true); // Richiedo sincronismo con il timezone

        //Verifica tipo prevendita, data inserimento, cliente, validità evento fatto dal db.

        $stmtInserimento = $conn->prepare("INSERT INTO prevendita (idEvento, idPR, nomeCliente, cognomeCliente, idTipoPrevendita, codice, stato, timestampCreazione) VALUES (:idEvento, :idPR, :nomeCliente, :cognomeCliente, :idTipoPrevendita, :codice, :stato, CURRENT_TIMESTAMP)");
        $stmtInserimento->bindValue(":idEvento", $idEvento, PDO::PARAM_INT);
        $stmtInserimento->bindValue(":idPR", $idUtente, PDO::PARAM_INT);
        $stmtInserimento->bindValue(":nomeCliente", $prevendita->getNomeCliente(), PDO::PARAM_STR);
        $stmtInserimento->bindValue(":cognomeCliente", $prevendita->getCognomeCliente(), PDO::PARAM_STR);
        $stmtInserimento->bindValue(":idTipoPrevendita", $prevendita->getIdTipoPrevendita(), PDO::PARAM_INT);
        $stmtInserimento->bindValue(":codice", $prevendita->getCodice(), PDO::PARAM_STR);
        $stmtInserimento->bindValue(":stato", $prevendita->getStato()->toString(), PDO::PARAM_STR);
        
        try {
            $stmtInserimento->execute();
        } catch (PDOException $ex) {
            // Mi assicuro di chiudere la connessione. Anche se teoricamente lo scope cancellerebbe comunque i riferimenti.
            $conn = NULL;

            if ($ex->getCode() == PR::DATI_INCONGRUENTI_CODE || $ex->getCode() == PR::DATA_NON_VALIDA_CODE || $ex->getCode() == PR::STATO_NON_VALIDO_CODE) // Codici di integrità.
                throw new InsertUpdateException($ex->getMessage());

            throw $ex;
        }

        $id = (int) $conn->lastInsertId();

        $conn = NULL;

        return $prevendita->getWPrevendita($id, $idEvento, $idUtente);
    }

    /**
     * Modifica una prevendita già creata.
     * Si possono modificare lo stato e il tipo di prevendita.
     *
     * @param UpdateNetWPrevendita $prevendita prevendita già provvista delle modifiche.
     * @param int $idPR
     * @param bool $isAmministratore
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws AuthorizationException prevendita non del pr
     * @throws InsertUpdateException la prevendita non è valida per la modifica
     * 
     * @return WPrevendita prevendita modificata.
     */
    public static function modificaPrevendita(UpdateNetWPrevendita $prevendita, int $idPR, bool $isAmministratore) : WPrevendita
    {
        $conn = parent::getConnection(true);

        //Devo fare un check per vedere il pr è abilitato per la prevendita.
        //Devo fare il check se non ho chiuso la finestra di vendita.
        //Almeno che non sia amministratore
        if(!$isAmministratore){     
             
            $stmtVerifica = $conn->prepare("SELECT p.idPR, (CURRENT_TIMESTAMP <= t.chiusuraPrevendite) AS modificabile FROM prevendita p, tipoPrevendita t WHERE p.idTipoPrevendita = t.id AND p.id = :idPrevendita");
            $stmtVerifica->bindValue(":idPrevendita", $prevendita->getId(), PDO::PARAM_INT);
            $stmtVerifica->execute();

            if ($stmtVerifica->rowCount() > 0) {
                $riga = $stmtVerifica->fetch(PDO::FETCH_ASSOC);
                $idPRDB = $riga["idPR"];
                $modificabile = $riga["modificabile"];

                if($idPR != $idPRDB){
                    throw new AuthorizationException("Non puoi modificare una prevendita non tua.");
                }

                if(!$modificabile){
                    throw new AuthorizationException("Periodo di vendita chiuso: solo l'amministratore può modificare");
                }
            }else{
                throw new NotAvailableOperationException("Non puoi modificare una prevendita non tua.");
            }
        }
        
        // posso inserire i nuovi dati.
        $stmtModifica = $conn->prepare("UPDATE prevendita SET stato = :stato, timestampUltimaModifica = CURRENT_TIMESTAMP WHERE id = :idPrevendita");
        $stmtModifica->bindValue(":stato", $prevendita->getStato()->toString(), PDO::PARAM_STR);
        $stmtModifica->bindValue(":idPrevendita", $prevendita->getId(), PDO::PARAM_INT);

        try {
            $stmtModifica->execute();
        } catch (PDOException $ex) {
            // Mi assicuro di chiudere la connessione. Anche se teoricamente lo scope cancellerebbe comunque i riferimenti.
            $conn = NULL;

            if ($ex->getCode() == PR::DATI_INCONGRUENTI_CODE || $ex->getCode() == PR::DATA_NON_VALIDA_CODE || $ex->getCode() == PR::STATO_NON_VALIDO_CODE) // Codici di integrità.
                throw new InsertUpdateException($ex->getMessage());

            throw $ex;
        }

        //Restituisco la prevendita modificata.
        $stmtSelezione = $conn->prepare("SELECT id, idEvento, idPR, nomeCliente, cognomeCliente, idTipoPrevendita, codice, stato, timestampUltimaModifica FROM prevendita WHERE id = :idPrevendita");
        $stmtSelezione->bindValue(":idPrevendita", $prevendita->getId(), PDO::PARAM_INT);
        $stmtSelezione->execute();

        $result = NULL;

        if (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $result = WPrevendita::of($riga);
        }

        $conn = NULL;

        return $result;
    }

    /**
     * Restituisce la lista delle prevendite prodotte dall'utente.
     *
     * @param NetWFiltriStatoPrevendita $filtri
     * @param int $idPR
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return \com\model\db\wrapper\WPrevendita[] lista delle prevendite
     */
    public static function getPrevendite(NetWFiltriStatoPrevendita $filtri, int $idPR): array
    {
        // Verifico la query a seconda del filtro
        $query = "SELECT id, idEvento, idPR, nomeCliente, cognomeCliente, idTipoPrevendita, codice, stato, timestampUltimaModifica FROM prevendita WHERE idPR = :idPR";

        if (count($filtri->getFiltri()) > 0) {
            $query .= " AND (";

            $i = 0;

            foreach ($filtri->getFiltri() as $filtro) {
                $query .= "stato = :stato";
                $query .= $i;
                $query .= " OR ";

                $i ++;
            }

            $query .= " 0)";
        }

        $conn = parent::getConnection();

        // Posso direttamente selezionare le prevendite dell'utente.
        $stmtSelezione = $conn->prepare($query);
        $stmtSelezione->bindValue(":idPR", $idPR, PDO::PARAM_INT);

        if (count($filtri->getFiltri()) > 0) {
            $i = 0;

            foreach ($filtri->getFiltri() as $filtro) {
                $stmtSelezione->bindValue(":stato" . $i, $filtro->toString(), PDO::PARAM_STR);
                $i ++;
            }
        }
        
        $stmtSelezione->execute();

        $result = array();

        while (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $result[] = WPrevendita::of($riga);
        }

        $conn = NULL;

        return $result;
    }

    /**
     * Restituisce le statistiche totali del PR.
     *
     * @param int $idUtente pr associato
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return NULL|WStatistichePRTotali Restituisce il wrapper. Se non sono disponibili statistiche restituisce NULL.
     */
    public static function getStatistichePRTotali(int $idUtente): ?WStatistichePRTotali
    {
        $conn = parent::getConnection();

        //Query XAMPP:
        //$query = "SELECT idUtente, prevenditeVendute, ricavo FROM statistichePRTotali WHERE idUtente = :idUtente";

        //Query ALTERVISTA:
        $query = <<<EOT
        SELECT T.idUtente, T.prevenditeVendute, T.ricavo 
        FROM (SELECT T1.idUtente, SUM(T1.prevenditeVendute) AS prevenditeVendute, SUM(T1.ricavo) AS ricavo
            FROM (SELECT T2.idUtente, T2.idStaff, SUM(T2.prevenditeVendute) AS prevenditeVendute, SUM(T2.ricavo) AS ricavo
                FROM (SELECT pr.idUtente as idUtente, pr.idStaff AS idStaff, prevendita.idEvento AS idEvento, prevendita.idTipoPrevendita AS idTipoPrevendita, COUNT(prevendita.id) AS prevenditeVendute, SUM(tipoPrevendita.prezzo) AS ricavo
                    FROM pr
                    INNER JOIN evento ON evento.idStaff = pr.idStaff
                    INNER JOIN prevendita ON prevendita.idEvento = evento.id AND prevendita.idPR = pr.idUtente
                    INNER JOIN tipoPrevendita ON tipoPrevendita.idEvento = evento.id AND tipoPrevendita.id = prevendita.idTipoPrevendita
                    WHERE prevendita.stato = 'VALIDA'
                    GROUP BY pr.idUtente, pr.idStaff, prevendita.idEvento, prevendita.idTipoPrevendita) AS T2
                GROUP BY T2.idUtente, T2.idStaff) AS T1
            GROUP BY T1.idUtente) AS T
        WHERE T.idUtente = :idUtente
EOT;

        $stmtSelezione = $conn->prepare($query);
        $stmtSelezione->bindValue(":idUtente", $idUtente, PDO::PARAM_INT);
        $stmtSelezione->execute();

        $result = NULL;

        if (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $result = WStatistichePRTotali::of($riga);
        }

        $conn = NULL;

        return $result;
    }

    /**
     * Restituisce le statistiche del PR in uno staff.
     *
     * @param int $idUtente pr associato
     * @param int $idStaff
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return NULL|WStatistichePRStaff Restituisce il wrapper. Se non sono disponibili statistiche restituisce NULL.
     */
    public static function getStatistichePRStaff(int $idUtente, int $idStaff): ?WStatistichePRStaff
    {
        $conn = parent::getConnection();

        //Query XAMPP:
        //$query = "SELECT idUtente, idStaff, prevenditeVendute, ricavo FROM statistichePRStaff WHERE idUtente = :idUtente AND idStaff = :idStaff";

        //Query ALTERVISTA:
        $query = <<<EOT
        SELECT T.idUtente, T.idStaff, T.prevenditeVendute, T.ricavo 
        FROM (SELECT T1.idUtente, T1.idStaff, SUM(T1.prevenditeVendute) AS prevenditeVendute, SUM(T1.ricavo) AS ricavo
            FROM (SELECT pr.idUtente as idUtente, pr.idStaff AS idStaff, prevendita.idEvento AS idEvento, prevendita.idTipoPrevendita AS idTipoPrevendita, COUNT(prevendita.id) AS prevenditeVendute, SUM(tipoPrevendita.prezzo) AS ricavo
                FROM pr
                INNER JOIN evento ON evento.idStaff = pr.idStaff
                INNER JOIN prevendita ON prevendita.idEvento = evento.id AND prevendita.idPR = pr.idUtente
                INNER JOIN tipoPrevendita ON tipoPrevendita.idEvento = evento.id AND tipoPrevendita.id = prevendita.idTipoPrevendita
                WHERE prevendita.stato = 'VALIDA'
                GROUP BY pr.idUtente, pr.idStaff, prevendita.idEvento, prevendita.idTipoPrevendita) AS T1
            GROUP BY idUtente, idStaff) AS T
        WHERE T.idUtente = :idUtente AND T.idStaff = :idStaff
EOT;

        $stmtSelezione = $conn->prepare($query);
        $stmtSelezione->bindValue(":idUtente", $idUtente, PDO::PARAM_INT);
        $stmtSelezione->bindValue(":idStaff", $idStaff, PDO::PARAM_INT);
        $stmtSelezione->execute();

        $result = NULL;

        if (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $result = WStatistichePRStaff::of($riga);
        }

        $conn = NULL;

        return $result;
    }

    /**
     * Restituisce le statistiche del PR in un evento.
     *
     * @param int $idUtente pr associato
     * @param int $idEvento
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return WStatistichePREvento[] Restituisce un array con le statistiche
     */
    public static function getStatistichePREvento(int $idUtente, int $idEvento): array
    {
        $conn = parent::getConnection();

        //Query XAMPP:
        //$query = "SELECT idUtente, idStaff, idEvento, idTipoPrevendita, prevenditeVendute, ricavo FROM statistichePREvento WHERE idUtente = :idUtente AND idEvento = :idEvento";

        //Query ALTERVISTA:
        $query = <<<EOT
        SELECT T.idUtente, T.idStaff, T.idEvento, T.idTipoPrevendita, T.nomeTipoPrevendita, T.prevenditeVendute, T.ricavo 
        FROM (SELECT pr.idUtente as idUtente, pr.idStaff AS idStaff, prevendita.idEvento AS idEvento, prevendita.idTipoPrevendita AS idTipoPrevendita, tipoPrevendita.nome AS nomeTipoPrevendita, COUNT(prevendita.id) AS prevenditeVendute, SUM(tipoPrevendita.prezzo) AS ricavo
            FROM pr
            INNER JOIN evento ON evento.idStaff = pr.idStaff
            INNER JOIN prevendita ON prevendita.idEvento = evento.id AND prevendita.idPR = pr.idUtente
            INNER JOIN tipoPrevendita ON tipoPrevendita.idEvento = evento.id AND tipoPrevendita.id = prevendita.idTipoPrevendita
            WHERE prevendita.stato = 'VALIDA'
            GROUP BY pr.idUtente, pr.idStaff, prevendita.idEvento, prevendita.idTipoPrevendita) AS T
        WHERE T.idUtente = :idUtente AND T.idEvento = :idEvento
EOT;

        $stmtSelezione = $conn->prepare($query);
        $stmtSelezione->bindValue(":idUtente", $idUtente, PDO::PARAM_INT);
        $stmtSelezione->bindValue(":idEvento", $idEvento, PDO::PARAM_INT);
        $stmtSelezione->execute();

        $result = array();

        while (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $result[] = WStatistichePREvento::of($riga);
        }

        $conn = NULL;

        return $result;
    }

    /**
     * Restituisce la lista delle prevendite prodotte dall'utente.
     *
     * @param int $idUtente pr associato
     * @param int $idEvento
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return \com\model\db\wrapper\WPrevenditaPlus[] lista delle prevendite plus
     */
    public static function getPrevenditeEvento(int $idUtente, int $idEvento): array
    {
        //Vecchia Query:
        //$query = "SELECT id, idEvento, idPR, nomeCliente, cognomeCliente, idTipoPrevendita, codice, stato, timestampUltimaModifica FROM prevendita WHERE idEvento = :idEvento AND idPR = :idPR";

        //Nuova query per Wprevendita plus:
        $query = <<<EOT
        SELECT prevendita.id AS id, prevendita.idEvento AS idEvento, evento.nome AS nomeEvento, prevendita.idPR AS idPR, 
        utente.nome AS nomePR, utente.cognome AS cognomePR, prevendita.nomeCliente AS nomeCliente, 
        prevendita.cognomeCliente AS cognomeCliente, prevendita.idTipoPrevendita AS idTipoPrevendita, tipoPrevendita.nome AS nomeTipoPrevendita, 
        tipoPrevendita.prezzo AS prezzoTipoPrevendita, prevendita.codice AS codice, prevendita.stato AS stato 
        FROM prevendita 
        INNER JOIN evento ON evento.id = prevendita.idEvento 
        INNER JOIN utente ON utente.id = prevendita.idPR 
        INNER JOIN tipoPrevendita ON tipoPrevendita.id = prevendita.idTipoPrevendita 
        WHERE prevendita.idEvento = :idEvento AND prevendita.idPR = :idPR
EOT;

        $conn = parent::getConnection();

        // Posso direttamente selezionare le prevendite dell'utente.
        $stmtSelezione = $conn->prepare($query);
        $stmtSelezione->bindValue(":idPR", $idUtente, PDO::PARAM_INT);
        $stmtSelezione->bindValue(":idEvento", $idEvento, PDO::PARAM_INT);       
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

