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
use com\model\db\exception\InsertUpdateException;
use com\model\db\exception\AuthorizationException;
use com\model\db\exception\NotAvailableOperationException;
use com\model\db\wrapper\WPrevendita;
use com\model\db\wrapper\WPrevenditaPlus;
use com\model\db\wrapper\WCliente;
use com\model\db\wrapper\WStatistichePREvento;
use com\model\db\wrapper\WStatistichePRStaff;
use com\model\db\wrapper\WStatistichePRTotali;
use com\model\db\wrapper\insert\InsertWPrevendita;
use com\model\db\wrapper\update\UpdateWPrevendita;
use InvalidArgumentException;
use PDO;
use PDOException;
use com\model\db\wrapper\insert\InsertWCliente;
use com\utils\DateTimeImmutableAdapterJSON;
use com\model\db\wrapper\custom\CustomWFiltroStatoPrevendita;
use com\model\net\wrapper\insert\InsertNetWCliente;
use com\model\net\wrapper\NetWFiltriStatoPrevendita;
use com\model\net\wrapper\update\UpdateNetWPrevendita;
use com\model\net\wrapper\NetWId;
use com\model\net\wrapper\insert\InsertNetWPrevendita;

class PR extends Table
{

    /**
     * Verifica se l'utente è un PR dello staff.
     *
     * @param int $idStaff
     * @return boolean
     * @throws \Exception
     */
    private static function _isPR(int $idStaff): bool
    {
        $conn = parent::getConnection();

        $stmt = $conn->prepare("SELECT COUNT(*) AS conto FROM pr WHERE idUtente = :idUtente AND idStaff = :idStaff");
        $stmt->bindValue(":idUtente", Context::getContext()->getUtente()
            ->getId(), PDO::PARAM_INT);
        $stmt->bindValue(":idStaff", $idStaff, PDO::PARAM_INT);
        $stmt->execute();

        $count = (int) $stmt->fetch(PDO::FETCH_ASSOC)["conto"];

        $conn = NULL;

        return $count === 1;
    }

    /**
     * Verifica se l'utente è un PR dello staff.
     *
     * @param int $idEvento
     * @return boolean
     * @throws \Exception
     */
    private static function _isPRByEvento(int $idEvento): bool
    {
        $conn = parent::getConnection();

        $stmt = $conn->prepare("SELECT COUNT(*) AS conto FROM pr INNER JOIN evento ON evento.idStaff = pr.idStaff WHERE pr.idUtente = :idUtente AND evento.id = :idEvento");
        $stmt->bindValue(":idUtente", Context::getContext()->getUtente()
            ->getId(), PDO::PARAM_INT);
        $stmt->bindValue(":idEvento", $idEvento, PDO::PARAM_INT);
        $stmt->execute();

        $count = (int) $stmt->fetch(PDO::FETCH_ASSOC)["conto"];

        $conn = NULL;

        return $count === 1;
    }

    /**
     * Verifica se l'utente è un PR dello staff.
     *
     * @param int $idPrevendita
     * @return boolean
     * @throws \Exception
     */
    private static function _isPRByPrevendita(int $idPrevendita): bool
    {
        $conn = parent::getConnection();

        $stmt = $conn->prepare("SELECT COUNT(*) AS conto FROM pr INNER JOIN evento ON evento.idStaff = pr.idStaff INNER JOIN prevendita ON prevendita.idEvento = evento.id WHERE pr.idUtente = :idUtente AND prevendita.id = :idPrevendita");
        $stmt->bindValue(":idUtente", Context::getContext()->getUtente()
            ->getId(), PDO::PARAM_INT);
        $stmt->bindValue(":idPrevendita", $idPrevendita, PDO::PARAM_INT);
        $stmt->execute();

        $count = (int) $stmt->fetch(PDO::FETCH_ASSOC)["conto"];

        $conn = NULL;

        return $count === 1;
    }

    // /**
    // * Verifica se l'utente è un PR dello staff.
    // *
    // * @param WStaff $staff
    // * @throws InvalidArgumentException parametri nulli o non validi
    // * @throws NotAvailableOperationException non si è loggati nel sistema
    // * @throws PDOException problemi del database (errore di connessione, errore nel database)
    // * @return boolean
    // */
    // public static function isPR(WStaff $staff): bool
    // {
    // if (is_null($staff))
    // throw new InvalidArgumentException("Parametri nulli.");

    // if (! ($staff instanceof WStaff))
    // throw new InvalidArgumentException("Parametri non validi.");

    // // Verifico che si è loggati nel sistema.
    // if (Context::getContext()->isValid())
    // throw new NotAvailableOperationException("Utente non loggato.");

    // return self::_isPR($staff->getId());
    // }

    /**
     * Aggiunge un cliente per lo staff.
     *
     * @param InsertNetWCliente $cliente
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws NotAvailableOperationException non si è loggati nel sistema
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws AuthorizationException l'utente non è PR per lo staff
     * @throws InsertUpdateException cliente già inserito
     * @return \com\model\db\wrapper\WCliente
     */
    public static function aggiungiCliente(InsertNetWCliente $cliente): WCliente
    {
        if (is_null($cliente))
            throw new InvalidArgumentException("Parametri nulli.");

        if (! ($cliente instanceof InsertNetWCliente))
            throw new InvalidArgumentException("Parametri non validi.");

        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        // Verifico che l'utente sia membro dello staff.
        if (! self::_isPR($cliente->getIdStaff()))
            throw new AuthorizationException("L'utente non è un pr dello staff.");

        $conn = parent::getConnection();

        $stmtInserimento = $conn->prepare("INSERT INTO cliente (idStaff, nome, cognome, telefono, dataDiNascita, codiceFiscale) VALUES (:idStaff, :nome, :cognome, :telefono, :dataDiNascita, :codiceFiscale)");
        $stmtInserimento->bindValue(":idStaff", $cliente->getIdStaff(), PDO::PARAM_INT);
        $stmtInserimento->bindValue(":nome", $cliente->getNome(), PDO::PARAM_STR);
        $stmtInserimento->bindValue(":cognome", $cliente->getCognome(), PDO::PARAM_STR);
        $stmtInserimento->bindValue(":telefono", $cliente->getTelefono(), PDO::PARAM_STR);

        //Data di nascita opzionale:
        if(!is_null($cliente->getDataDiNascita())){
            $stmtInserimento->bindValue(":dataDiNascita", $cliente->getDataDiNascita()
            ->getDateTimeImmutable()
            ->format(DateTimeImmutableAdapterJSON::MYSQL_DATE), PDO::PARAM_STR);
        }else{
            $stmtInserimento->bindValue(":dataDiNascita", null, PDO::PARAM_STR);
        }
        
        $stmtInserimento->bindValue(":codiceFiscale", $cliente->getCodiceFiscale(), PDO::PARAM_STR);

        try {
            $stmtInserimento->execute();
        } catch (PDOException $ex) {
            // Mi assicuro di chiudere la connessione. Anche se teoricamente lo scope cancellerebbe comunque i riferimenti.
            $conn = NULL;

            if ($ex->getCode() == PR::UNIQUE_CODE || $ex->getCode() == PR::INTEGRITY_CODE) // Codici di integrità.
                throw new InsertUpdateException("Cliente già inserito.");

            throw $ex;
        }

        $id = (int) $conn->lastInsertId();

        $conn = NULL;

        return WCliente::makeNoChecks($id, $cliente->getIdStaff(), $cliente->getNome(), $cliente->getCognome(), $cliente->getTelefono(), $cliente->getDataDiNascita(), $cliente->getCodiceFiscale(), new DateTimeImmutableAdapterJSON(new \DateTimeImmutable()));
    }

    /**
     * Aggiunge una prevendita per un cliente.
     *
     * @param InsertNetWPrevendita $prevendita
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws NotAvailableOperationException non si è loggati nel sistema
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws AuthorizationException l'utente non è PR per lo staff
     * @throws InsertUpdateException la prevendita non è valida per l'inserimento
     * @return WPrevendita Prevendita completa
     */
    public static function aggiungiPrevendita(InsertNetWPrevendita $prevendita): WPrevendita
    {
        if (is_null($prevendita))
            throw new InvalidArgumentException("Parametri nulli.");

        if (! ($prevendita instanceof InsertNetWPrevendita))
            throw new InvalidArgumentException("Parametri non validi.");

        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        // Verifico che l'utente sia membro dello staff.
        if (! self::_isPRByEvento($prevendita->getIdEvento()))
            throw new AuthorizationException("L'utente non è un pr dello staff.");

        $conn = parent::getConnection(true); // Richiedo sincronismo con il timezone

        // // Prima devo verificare che il tipo di prevendita e il cliente sia compatibile.
        // $stmtVerificaTipoPrevendita = $conn->prepare("SELECT COUNT(*) AS conto FROM tipoPrevendita WHERE idEvento = :idEvento AND id = :idTipoPrevendita");
        // $stmtVerificaTipoPrevendita->bindValue(":idEvento", $prevendita->getIdEvento(), PDO::PARAM_INT);
        // $stmtVerificaTipoPrevendita->bindValue(":idTipoPrevendita", $prevendita->getIdTipoPrevendita(), PDO::PARAM_INT);
        // $stmtVerificaTipoPrevendita->execute();
        // $contoTipoPrevendita = (int) $stmtVerificaTipoPrevendita->fetch(PDO::FETCH_ASSOC)["conto"];

        // $stmtVerificaCliente = $conn->prepare("SELECT COUNT(*) AS conto FROM cliente INNER JOIN evento ON evento.idStaff = cliente.idStaff WHERE evento.id = :idEvento AND cliente.id = :idCliente");
        // $stmtVerificaCliente->bindValue(":idEvento", $prevendita->getIdEvento(), PDO::PARAM_INT);
        // $stmtVerificaCliente->bindValue(":idCliente", $prevendita->getIdCliente(), PDO::PARAM_INT);
        // $stmtVerificaCliente->execute();
        // $contoCliente = (int) $stmtVerificaCliente->fetch(PDO::FETCH_ASSOC)["conto"];

        // if (1 !== $contoTipoPrevendita || 1 !== $contoCliente) {
        // $conn = NULL;
        // throw new InsertUpdateException("Prevendita non valida.");
        // }

        // // Devo verificare che l'evento sia valido.

        // $stmtVerificaValiditàEvento = $conn->prepare("SELECT statoEvento FROM evento WHERE id = :idEvento");
        // $stmtVerificaValiditàEvento->bindValue(":idEvento", $prevendita->getIdEvento(), PDO::PARAM_INT);
        // $stmtVerificaValiditàEvento->execute();

        // if ((StatoEvento::parse($stmtVerificaValiditàEvento->fetch(PDO::FETCH_ASSOC)["statoEvento"]))->getId() != (StatoEvento::VALIDO)) {
        // $conn = NULL;
        // throw new InsertUpdateException("Evento annullato.");
        // }

        // // Dovrei verificare che le vendite siano nell'intervallo di vendibilità.

        // $stmtVerificaTempoVendite = $conn->prepare("SELECT aperturaPrevendite, chiusuraPrevendite FROM tipoPrevendita WHERE id = :idTipoPrevendita");
        // $stmtVerificaTempoVendite->bindValue(":idTipoPrevendita", $prevendita->getIdTipoPrevendita(), PDO::PARAM_INT);
        // $stmtVerificaTempoVendite->execute();

        // $tmp1 = $stmtVerificaTempoVendite->fetch(PDO::FETCH_ASSOC);

        // $aperturaPrevendite = \DateTimeImmutable::createFromFormat(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP, $tmp1["aperturaPrevendite"]);
        // $chiusuraPrevendite = \DateTimeImmutable::createFromFormat(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP, $tmp1["chiusuraPrevendite"]);

        // if ($ora < $aperturaPrevendite || $ora > $chiusuraPrevendite) {
        // $conn = NULL;
        // throw new InsertUpdateException("Tipo di prevendita non disponibile.");
        // }

        $idUtente = Context::getContext()->getUtente()->getId();

        $stmtInserimento = $conn->prepare("INSERT INTO prevendita (idEvento, idPR, idCliente, idTipoPrevendita, codice, stato, timestampCreazione) VALUES (:idEvento, :idPR, :idCliente, :idTipoPrevendita, :codice, :stato, CURRENT_TIMESTAMP)");
        $stmtInserimento->bindValue(":idEvento", $prevendita->getIdEvento(), PDO::PARAM_INT);
        $stmtInserimento->bindValue(":idPR", $idUtente, PDO::PARAM_INT);
        $stmtInserimento->bindValue(":idTipoPrevendita", $prevendita->getIdTipoPrevendita(), PDO::PARAM_INT);
        $stmtInserimento->bindValue(":codice", $prevendita->getCodice(), PDO::PARAM_STR);
        $stmtInserimento->bindValue(":stato", $prevendita->getStato()
            ->toString(), PDO::PARAM_STR);
        $stmtInserimento->bindValue(":idCliente", $prevendita->getIdCliente(), PDO::PARAM_INT);

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

        return WPrevendita::make($id, $prevendita->getIdEvento(), $idUtente, $prevendita->getIdCliente(), $prevendita->getIdTipoPrevendita(), $prevendita->getCodice(), $prevendita->getStato(), new DateTimeImmutableAdapterJSON(new \DateTimeImmutable()));
    }

    /**
     * Modifica una prevendita già creata.
     * Si possono modificare lo stato e il tipo di prevendita.
     *
     * @param UpdateNetWPrevendita $prevendita
     *            prevendita già provvista delle modifiche.
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws NotAvailableOperationException non si è loggati nel sistema
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws AuthorizationException l'utente non è PR per lo staff
     * @throws InsertUpdateException la prevendita non è valida per la modifica
     * 
     * @return WPrevendita prevendita modificata.
     */
    public static function modificaPrevendita(UpdateNetWPrevendita $prevendita) : WPrevendita
    {
        if (is_null($prevendita))
            throw new InvalidArgumentException("Parametri nulli.");

        if (! ($prevendita instanceof UpdateNetWPrevendita))
            throw new InvalidArgumentException("Parametri non validi.");

        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        // Verifico che l'utente sia membro dello staff.
        if (! self::_isPRByPrevendita($prevendita->getId()))
            throw new AuthorizationException("L'utente non è un pr dello staff.");

        $conn = parent::getConnection(true);

        // posso inserire i nuovi dati.
        $stmtModifica = $conn->prepare("UPDATE prevendita SET stato = :stato, timestampUltimaModifica = CURRENT_TIMESTAMP WHERE id = :idPrevendita");
        $stmtModifica->bindValue(":stato", $prevendita->getStato()
            ->toString(), PDO::PARAM_STR);
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

        //Vecchia Query: 
        $query = "SELECT id, idEvento, idPR, idCliente, idTipoPrevendita, codice, stato, timestampUltimaModifica FROM prevendita WHERE id = :idPrevendita";

        //Query modificata con nome e cognome cliente: nuova versione WPrevendita.
        /*
        $query = <<<EOT
        SELECT p.id, p.idEvento, p.idPR, p.idCliente, c.nome AS nomeCliente, c.cognome AS cognomeCliente, p.idTipoPrevendita, p.codice, p.stato, p.timestampUltimaModifica
        FROM prevendita AS p 
        LEFT JOIN cliente AS c ON  p.idCliente = c.id
        WHERE id = :idPrevendita
EOT;
*/

        $stmtSelezione = $conn->prepare($query);
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
     * @param NetWFiltriStatoPrevendita|NULL $filtri
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws NotAvailableOperationException non si è loggati nel sistema
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return \com\model\db\wrapper\WPrevendita[] lista delle prevendite
     */
    public static function getPrevendite(NetWFiltriStatoPrevendita $filtri): array
    {
        if (is_null($filtri) || ! ($filtri instanceof NetWFiltriStatoPrevendita))
            throw new InvalidArgumentException("Parametro filtri non valido.");

        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        // Verifico la query a seconda del filtro


        //Vecchia Query: 
        $query = "SELECT id, idEvento, idPR, idCliente, idTipoPrevendita, codice, stato, timestampUltimaModifica FROM prevendita WHERE idPR = :idPR";

        //Query modificata con nome e cognome cliente: nuova versione WPrevendita.
        /*
        $query = <<<EOT
        SELECT p.id, p.idEvento, p.idPR, p.idCliente, c.nome AS nomeCliente, c.cognome AS cognomeCliente, p.idTipoPrevendita, p.codice, p.stato, p.timestampUltimaModifica
        FROM prevendita AS p 
        LEFT JOIN cliente AS c ON  p.idCliente = c.id
        WHERE idPR = :idPR
EOT;
*/        

        //if(!is_null($filtri)){
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
        //}

        $conn = parent::getConnection();

        // Posso direttamente selezionare le prevendite dell'utente.
        $stmtSelezione = $conn->prepare($query);
        $stmtSelezione->bindValue(":idPR", Context::getContext()->getUtente()
            ->getId(), PDO::PARAM_INT);

        //if(!is_null($filtri)){
            if (count($filtri->getFiltri()) > 0) {
                $i = 0;

                foreach ($filtri->getFiltri() as $filtro) {
                    $stmtSelezione->bindValue(":stato" . $i, $filtro->toString(), PDO::PARAM_STR);
                    $i ++;
                }
            }
        //}
        
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
     * @throws NotAvailableOperationException l'utente non è loggato nel sistema.
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return NULL|WStatistichePRTotali Restituisce il wrapper. Se non sono disponibili statistiche restituisce NULL.
     */
    public static function getStatistichePRTotali(): ?WStatistichePRTotali
    {
        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

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
                    WHERE prevendita.stato = 'PAGATA'
                    GROUP BY pr.idUtente, pr.idStaff, prevendita.idEvento, prevendita.idTipoPrevendita) AS T2
                GROUP BY T2.idUtente, T2.idStaff) AS T1
            GROUP BY T1.idUtente) AS T
        WHERE T.idUtente = :idUtente
EOT;

        $stmtSelezione = $conn->prepare($query);
        $stmtSelezione->bindValue(":idUtente", Context::getContext()->getUtente()
            ->getId(), PDO::PARAM_INT);
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
     * @param NetWId $staff
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws NotAvailableOperationException l'utente non è loggato nel sistema.
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return NULL|WStatistichePRStaff Restituisce il wrapper. Se non sono disponibili statistiche restituisce NULL.
     */
    public static function getStatistichePRStaff(NetWId $staff): ?WStatistichePRStaff
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
                WHERE prevendita.stato = 'PAGATA'
                GROUP BY pr.idUtente, pr.idStaff, prevendita.idEvento, prevendita.idTipoPrevendita) AS T1
            GROUP BY idUtente, idStaff) AS T
        WHERE T.idUtente = :idUtente AND T.idStaff = :idStaff
EOT;

        $stmtSelezione = $conn->prepare($query);
        $stmtSelezione->bindValue(":idUtente", Context::getContext()->getUtente()
            ->getId(), PDO::PARAM_INT);
        $stmtSelezione->bindValue(":idStaff", $staff->getId(), PDO::PARAM_INT);
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
     * @param NetWId $evento
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws NotAvailableOperationException l'utente non è loggato nel sistema.
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return WStatistichePREvento[] Restituisce un array con le statistiche
     */
    public static function getStatistichePREvento(NetWId $evento): array
    {
        // Verifico i parametri
        if (is_null($evento))
            throw new InvalidArgumentException("Parametro nullo.");

        if (! ($evento instanceof NetWId))
            throw new InvalidArgumentException("Parametro non valido.");

        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        $conn = parent::getConnection();

        //Query XAMPP:
        //TODO: Da riscrivere lato view.
        //$query = "SELECT idUtente, idStaff, idEvento, idTipoPrevendita, prevenditeVendute, ricavo FROM statistichePREvento WHERE idUtente = :idUtente AND idEvento = :idEvento";

        //Query ALTERVISTA:
        $query = <<<EOT
        SELECT T.idUtente, T.idStaff, T.idEvento, T.idTipoPrevendita, T.nomeTipoPrevendita, T.prevenditeVendute, T.ricavo 
        FROM (SELECT pr.idUtente as idUtente, pr.idStaff AS idStaff, prevendita.idEvento AS idEvento, prevendita.idTipoPrevendita AS idTipoPrevendita, tipoPrevendita.nome AS nomeTipoPrevendita, COUNT(prevendita.id) AS prevenditeVendute, SUM(tipoPrevendita.prezzo) AS ricavo
            FROM pr
            INNER JOIN evento ON evento.idStaff = pr.idStaff
            INNER JOIN prevendita ON prevendita.idEvento = evento.id AND prevendita.idPR = pr.idUtente
            INNER JOIN tipoPrevendita ON tipoPrevendita.idEvento = evento.id AND tipoPrevendita.id = prevendita.idTipoPrevendita
            WHERE prevendita.stato IN ('PAGATA')
            GROUP BY pr.idUtente, pr.idStaff, prevendita.idEvento, prevendita.idTipoPrevendita) AS T
        WHERE T.idUtente = :idUtente AND T.idEvento = :idEvento
EOT;

        $stmtSelezione = $conn->prepare($query);
        $stmtSelezione->bindValue(":idUtente", Context::getContext()->getUtente()
            ->getId(), PDO::PARAM_INT);
        $stmtSelezione->bindValue(":idEvento", $evento->getId(), PDO::PARAM_INT);
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
     * @param NetWId $evento evento considerato
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws NotAvailableOperationException non si è loggati nel sistema
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return \com\model\db\wrapper\WPrevenditaPlus[] lista delle prevendite plus
     */
    public static function getPrevenditeEvento(NetWId $evento): array
    {
        if (! ($evento instanceof NetWId))
            throw new InvalidArgumentException("Parametro filtro non valido.");

        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        //Non c'è bisogno di controllare se l'utente è pr: magari non è più dello staff.

    
        //Vecchia Query:
        //$query = "SELECT id, idEvento, idPR, idCliente, idTipoPrevendita, codice, stato, timestampUltimaModifica FROM prevendita WHERE idEvento = :idEvento AND idPR = :idPR";


        //Query modificata con nome e cognome cliente: nuova versione WPrevendita.
        /*
        $query = <<<EOT
        SELECT p.id, p.idEvento, p.idPR, p.idCliente, c.nome AS nomeCliente, c.cognome AS cognomeCliente, p.idTipoPrevendita, p.codice, p.stato, p.timestampUltimaModifica
        FROM prevendita AS p 
        LEFT JOIN cliente AS c ON  p.idCliente = c.id
        WHERE idEvento = :idEvento AND idPR = :idPR
EOT;
*/

        //Nuova query per Wprevendita plus:
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
        WHERE prevendita.idEvento = :idEvento AND prevendita.idPR = :idPR
EOT;

        $conn = parent::getConnection();

        // Posso direttamente selezionare le prevendite dell'utente.
        $stmtSelezione = $conn->prepare($query);
        $stmtSelezione->bindValue(":idPR", Context::getContext()->getUtente()->getId(), PDO::PARAM_INT);
        $stmtSelezione->bindValue(":idEvento", $evento->getId(), PDO::PARAM_INT);       
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

