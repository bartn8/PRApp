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
use com\model\Hash;
use com\model\db\enum\Diritto;
use com\model\db\exception\AuthorizationException;
use com\model\db\exception\InsertUpdateException;
use com\model\db\exception\NotAvailableOperationException;
use com\model\db\wrapper\WCliente;
use com\model\db\wrapper\WDirittiUtente;
use com\model\db\wrapper\WEvento;
use com\model\db\wrapper\WPrevendita;
use com\model\db\wrapper\WStaff;
use com\model\db\wrapper\WStatisticheCassiereEvento;
use com\model\db\wrapper\WStatisticheCassiereStaff;
use com\model\db\wrapper\WStatisticheEvento;
use com\model\db\wrapper\WStatistichePREvento;
use com\model\db\wrapper\WStatistichePRStaff;
use com\model\db\wrapper\WTipoPrevendita;
use com\model\db\wrapper\WUtente;
use com\model\net\wrapper\NetWId;
use com\model\net\wrapper\insert\InsertNetWEvento;
use com\model\net\wrapper\insert\InsertNetWTipoPrevendita;
use com\model\net\wrapper\update\UpdateNetWDirittiUtente;
use com\model\net\wrapper\update\UpdateNetWEvento;
use com\model\net\wrapper\update\UpdateNetWStaff;
use com\model\net\wrapper\update\UpdateNetWTipoPrevendita;
use com\utils\DateTimeImmutableAdapterJSON;
use InvalidArgumentException;
use PDO;
use PDOException;

class Amministratore extends Table
{

    // TODO: Rivedere i controlli query: $count restituisce COUNT(*) !!

    /**
     * Verifica se l'utente è un amministratore dello staff.
     *
     * @param int $idStaff
     * @return boolean
     * @throws \Exception
     */
    private static function _isAmministratore(int $idStaff): bool
    {
        $conn = parent::getConnection();

        $stmt = $conn->prepare("SELECT COUNT(*) AS conto FROM amministratore WHERE idUtente = :idUtente AND idStaff = :idStaff");
        $stmt->bindValue(":idUtente", Context::getContext()->getUtente()
            ->getId(), PDO::PARAM_INT);
        $stmt->bindValue(":idStaff", $idStaff, PDO::PARAM_INT);
        $stmt->execute();

        $count = (int) $stmt->fetch(PDO::FETCH_ASSOC)["conto"];

        $conn = NULL;

        return $count === 1;
    }

    /**
     * Verifica se l'utente è un amministratore dello staff.
     *
     * @param int $idCliente
     * @return boolean
     */
    private static function _isAmministratoreByCliente(int $idCliente): bool
    {
        $conn = parent::getConnection();

        $stmt = $conn->prepare("SELECT COUNT(*) AS conto FROM amministratore INNER JOIN cliente ON cliente.idStaff = amministratore.idStaff WHERE amministratore.idUtente = :idUtente AND cliente.id = :idCliente");
        $stmt->bindValue(":idUtente", Context::getContext()->getUtente()
            ->getId(), PDO::PARAM_INT);
        $stmt->bindValue(":idCliente", $idCliente, PDO::PARAM_INT);
        $stmt->execute();

        $count = (int) $stmt->fetch(PDO::FETCH_ASSOC)["conto"];

        $conn = NULL;

        return $count === 1;
    }

    /**
     * Verifica se l'utente è un amministratore dello staff.
     *
     * @param int $idEvento
     * @return boolean
     */
    private static function _isAmministratoreByEvento(int $idEvento): bool
    {
        $conn = parent::getConnection();

        $stmt = $conn->prepare("SELECT COUNT(*) AS conto FROM amministratore INNER JOIN evento ON evento.idStaff = amministratore.idStaff WHERE amministratore.idUtente = :idUtente AND evento.id = :idEvento");
        $stmt->bindValue(":idUtente", Context::getContext()->getUtente()
            ->getId(), PDO::PARAM_INT);
        $stmt->bindValue(":idEvento", $idEvento, PDO::PARAM_INT);
        $stmt->execute();

        $count = (int) $stmt->fetch(PDO::FETCH_ASSOC)["conto"];

        $conn = NULL;

        return $count === 1;
    }

    /**
     * Verifica se l'utente è un amministratore dello staff.
     *
     * @param int $idTipoPrevendita
     * @return boolean
     */
    private static function _isAmministratoreByTipoPrevendita(int $idTipoPrevendita): bool
    {
        $conn = parent::getConnection();

        $stmt = $conn->prepare("SELECT COUNT(*) AS conto FROM amministratore INNER JOIN evento ON evento.idStaff = amministratore.idStaff INNER JOIN tipoPrevendita ON tipoPrevendita.idEvento = evento.id WHERE amministratore.idUtente = :idUtente AND tipoPrevendita.id = :idTipoPrevendita");
        $stmt->bindValue(":idUtente", Context::getContext()->getUtente()
            ->getId(), PDO::PARAM_INT);
        $stmt->bindValue(":idTipoPrevendita", $idTipoPrevendita, PDO::PARAM_INT);
        $stmt->execute();

        $count = (int) $stmt->fetch(PDO::FETCH_ASSOC)["conto"];

        $conn = NULL;

        return $count === 1;
    }

    // /**
    // * Verifica se l'utente è un amministratore dello staff.
    // *
    // * @param WStaff $staff
    // * @throws InvalidArgumentException parametri nulli o non validi
    // * @throws NotAvailableOperationException non si è loggati nel sistema
    // * @throws PDOException problemi del database (errore di connessione, errore nel database)
    // * @return boolean
    // */
    // public static function isAmministratore(WStaff $staff): bool
    // {
    // if (is_null($staff))
    // throw new InvalidArgumentException("Parametri nulli.");

    // if (! ($staff instanceof WStaff))
    // throw new InvalidArgumentException("Parametri non validi.");

    // // Verifico che si è loggati nel sistema.
    // if (!Context::getContext()->isValid())
    // throw new NotAvailableOperationException("Utente non loggato.");

    // return self::_isAmministratore($staff->getId());
    // }

    /**
     * Rimuove un cliente dallo staff.
     *
     * @param NetWId $cliente
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws NotAvailableOperationException non si è loggati nel sistema
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws AuthorizationException l'utente non è amministratore per lo staff
     * 
     * @return WCliente cliente eliminato
     */
    public static function rimuoviCliente(NetWId $cliente) : WCliente
    {
        if (is_null($cliente))
            throw new InvalidArgumentException("Parametri nulli.");

        if (! ($cliente instanceof NetWId))
            throw new InvalidArgumentException("Parametri non validi.");

        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        // Verifico che l'utente sia membro dello staff.
        if (! self::_isAmministratoreByCliente($cliente->getId()))
            throw new AuthorizationException("L'utente non è un amministratore dello staff.");

        $conn = parent::getConnection();

        //Prima ricavo i dati poi elimino.

        $stmtSelezione = $conn->prepare("SELECT id, idStaff, nome, cognome, telefono, dataDiNascita, codiceFiscale, timestampInserimento FROM cliente WHERE id = :idCliente");
        $stmtSelezione->bindValue(":idCliente", $cliente->getId());
        $stmtSelezione->execute();

        $result = NULL;

        if (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $result = WCliente::of($riga);
        }


        $stmtRimozione = $conn->prepare("DELETE FROM cliente WHERE id = :idCliente");
        $stmtRimozione->bindValue(":idCliente", $cliente->getId(), PDO::PARAM_INT);
        $stmtRimozione->execute();

        $conn = NULL;

        return $result;
    }

    /**
     * Aggiunge un evento dello staff.
     *
     * @param InsertNetWEvento $evento
     * @throws InsertUpdateException evento già presente
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws NotAvailableOperationException non si è loggati nel sistema
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws AuthorizationException l'utente non è amministratore per lo staff
     * @return WEvento Wrapper completo con timestamp ultima modifica fasullo
     */
    public static function aggiungiEvento(InsertNetWEvento $evento): WEvento
    {
        if (is_null($evento))
            throw new InvalidArgumentException("Parametri nulli.");

        if (! ($evento instanceof InsertNetWEvento))
            throw new InvalidArgumentException("Parametri non validi.");

        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        // Verifico che l'utente sia membro dello staff.
        if (! self::_isAmministratore($evento->getIdStaff()))
            throw new AuthorizationException("L'utente non è un amministratore dello staff.");

        $idUtente = Context::getContext()->getUtente()->getId();

        $conn = parent::getConnection();

        $stmtInserimento = $conn->prepare("INSERT INTO evento (idCreatore, idStaff, nome, descrizione, inizio, fine, indirizzo, stato, idModificatore) VALUES (:idCreatore, :idStaff, :nome, :descrizione, :inizio, :fine, :indirizzo, :stato, :idModificatore)");
        $stmtInserimento->bindValue(":idCreatore", $idUtente, PDO::PARAM_INT);
        $stmtInserimento->bindValue(":idModificatore", $idUtente, PDO::PARAM_INT);
        $stmtInserimento->bindValue(":idStaff", $evento->getIdStaff(), PDO::PARAM_INT);
        $stmtInserimento->bindValue(":nome", $evento->getNome(), PDO::PARAM_STR);
        $stmtInserimento->bindValue(":descrizione", $evento->getDescrizione(), PDO::PARAM_STR);
        $stmtInserimento->bindValue(":inizio", $evento->getInizio()
            ->getDateTimeImmutable()
            ->format(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP), PDO::PARAM_STR);
        $stmtInserimento->bindValue(":fine", $evento->getFine()
            ->getDateTimeImmutable()
            ->format(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP), PDO::PARAM_STR);
        $stmtInserimento->bindValue(":indirizzo", $evento->getIndirizzo(), PDO::PARAM_STR);
        $stmtInserimento->bindValue(":stato", $evento->getStato()
            ->toString(), PDO::PARAM_STR);

        try {
            $stmtInserimento->execute();
        } catch (PDOException $ex) {
            // Mi assicuro di chiudere la connessione. Anche se teoricamente lo scope cancellerebbe comunque i riferimenti.
            $conn = NULL;

            if ($ex->getCode() == Amministratore::UNIQUE_CODE || $ex->getCode() == Amministratore::INTEGRITY_CODE) // Codice di integrità.
                throw new InsertUpdateException("Evento già presente.");

            throw $ex;
        }

        $id = (int) $conn->lastInsertId();

        $conn = NULL;

        return $evento->getWEvento($id, $idUtente, $idUtente, new DateTimeImmutableAdapterJSON(new \DateTimeImmutable()));
    }

    /**
     * Modifica un evento dello staff.
     *
     * @param UpdateNetWEvento $evento
     * @throws InsertUpdateException evento coincide con un altro
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws NotAvailableOperationException non si è loggati nel sistema
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws AuthorizationException l'utente non è amministratore per lo staff
     * @return WEvento Wrapper completo con timestamp ultima modifica fasullo
     */
    public static function modificaEvento(UpdateNetWEvento $evento): WEvento
    {
        if (is_null($evento))
            throw new InvalidArgumentException("Parametri nulli.");

        if (! ($evento instanceof UpdateNetWEvento))
            throw new InvalidArgumentException("Parametri non validi.");

        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        // Verifico che l'utente sia membro dello staff.
        if (! self::_isAmministratoreByEvento($evento->getId()))
            throw new AuthorizationException("L'utente non è un amministratore dello staff.");

        $idUtente = Context::getContext()->getUtente()->getId();

        $conn = parent::getConnection();

        $stmtModifica = $conn->prepare("UPDATE evento SET idModificatore = :idModificatore, descrizione = :descrizione, inizio = :inizio, fine = :fine, indirizzo = :indirizzo, stato = :stato, timestampUltimaModifica = CURRENT_TIMESTAMP WHERE id = :idEvento");
        $stmtModifica->bindValue(":idEvento", $evento->getId(), PDO::PARAM_INT);
        $stmtModifica->bindValue(":idModificatore", $idUtente, PDO::PARAM_INT);
        $stmtModifica->bindValue(":descrizione", $evento->getDescrizione(), PDO::PARAM_STR);
        $stmtModifica->bindValue(":inizio", $evento->getInizio()
            ->getDateTimeImmutable()
            ->format(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP), PDO::PARAM_STR);
        $stmtModifica->bindValue(":fine", $evento->getFine()
            ->getDateTimeImmutable()
            ->format(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP), PDO::PARAM_STR);
        $stmtModifica->bindValue(":indirizzo", $evento->getIndirizzo(), PDO::PARAM_STR);
        $stmtModifica->bindValue(":stato", $evento->getStato()
            ->toString(), PDO::PARAM_STR);

        try {
            $stmtModifica->execute();
        } catch (PDOException $ex) {
            // Mi assicuro di chiudere la connessione. Anche se teoricamente lo scope cancellerebbe comunque i riferimenti.
            $conn = NULL;

            if ($ex->getCode() == Amministratore::UNIQUE_CODE || $ex->getCode() == Amministratore::INTEGRITY_CODE) // Codice di integrità.
                throw new InsertUpdateException("evento coincide con un altro.");

            throw $ex;
        }

        // Recupero i dati aggiuntivi per restituire un WEvento.
        // Mi serve il nome, etc

        $stmtRecuperoDati = $conn->prepare("SELECT nome, idStaff, idCreatore FROM evento WHERE id = :id");
        $stmtRecuperoDati->bindValue(":id", $evento->getId());

        $stmtRecuperoDati->execute();

        $fetch = $stmtRecuperoDati->fetch(PDO::FETCH_ASSOC);

        if (! $fetch) {
            throw new NotAvailableOperationException("Impossibile recuperare l'evento");
        }

        $idStaff = (int) $fetch["idStaff"];
        $nome = $fetch["nome"];
        $idCreatore = (int) $fetch["idCreatore"];

        $conn = NULL;

        return $evento->getWEvento($idStaff, $idCreatore, $nome, $idUtente, new DateTimeImmutableAdapterJSON(new \DateTimeImmutable()));
    }

    /**
     * Aggiunge un nuovo tipo di prevendita per un evento.
     *
     * @param InsertNetWTipoPrevendita $tipoPrevendita
     * @throws InsertUpdateException tipo di prevendita già presente
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws NotAvailableOperationException non si è loggati nel sistema
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws AuthorizationException l'utente non è amministratore per lo staff
     * @return WTipoPrevendita
     */
    public static function aggiungiTipoPrevendita(InsertNetWTipoPrevendita $tipoPrevendita): WTipoPrevendita
    {
        if (is_null($tipoPrevendita))
            throw new InvalidArgumentException("Parametri nulli.");

        if (! ($tipoPrevendita instanceof InsertNetWTipoPrevendita))
            throw new InvalidArgumentException("Parametri non validi.");

        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        // Verifico che l'utente sia membro dello staff.
        if (! self::_isAmministratoreByEvento($tipoPrevendita->getIdEvento()))
            throw new AuthorizationException("L'utente non è un amministratore dello staff.");

        $idUtente = Context::getContext()->getUtente()->getId();

        // Devo essere sincronizzato con il database riguardo il fuso orario: aperturaVendite e chiusuraVendite sono influenzate dal fuso orario.
        $conn = parent::getConnection(TRUE);

        var_dump($tipoPrevendita->getAperturaVendite()
            ->getDateTimeImmutable()
            ->format(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP));
        
        $stmtInserimento = $conn->prepare("INSERT INTO tipoPrevendita (idEvento, nome, descrizione, prezzo, aperturaPrevendite, chiusuraPrevendite, idModificatore) VALUES (:idEvento, :nome, :descrizione, :prezzo, :aperturaPrevendite, :chiusuraPrevendite, :idModificatore)");
        $stmtInserimento->bindValue(":idEvento", $tipoPrevendita->getIdEvento(), PDO::PARAM_INT);
        $stmtInserimento->bindValue(":nome", $tipoPrevendita->getNome(), PDO::PARAM_STR);
        $stmtInserimento->bindValue(":descrizione", $tipoPrevendita->getDescrizione(), PDO::PARAM_STR);
        $stmtInserimento->bindValue(":prezzo", strval($tipoPrevendita->getPrezzo()), PDO::PARAM_STR);
        $stmtInserimento->bindValue(":aperturaPrevendite", $tipoPrevendita->getAperturaVendite()
            ->getDateTimeImmutable()
            ->format(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP), PDO::PARAM_STR);
        $stmtInserimento->bindValue(":chiusuraPrevendite", $tipoPrevendita->getChiusuraVendite()
            ->getDateTimeImmutable()
            ->format(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP), PDO::PARAM_STR);
        $stmtInserimento->bindValue(":idModificatore", $idUtente);

        try {
            $stmtInserimento->execute();
        } catch (PDOException $ex) {
            // Mi assicuro di chiudere la connessione. Anche se teoricamente lo scope cancellerebbe comunque i riferimenti.
            $conn = NULL;

            if ($ex->getCode() == Amministratore::UNIQUE_CODE || $ex->getCode() == Amministratore::INTEGRITY_CODE) // Codice di integrità.
                throw new InsertUpdateException("tipo di prevenidita già presente.");

            if ($ex->getCode() == Amministratore::DATA_NON_VALIDA_CODE)
                throw new InsertUpdateException("La data d'inizio è nel passato. (".$tipoPrevendita->getAperturaVendite()->getDateTimeImmutable()->format(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP).")");

            throw $ex;
        }

        $id = (int) $conn->lastInsertId();

        $conn = NULL;

        return $tipoPrevendita->getWTipoPrevendita($id, $idUtente, new DateTimeImmutableAdapterJSON(new \DateTimeImmutable()));
    }

    /**
     * Modifica il tipo prevendita secondo le neccesità.
     *
     * @param UpdateNetWTipoPrevendita $tipoPrevendita
     * @throws InsertUpdateException tipo di prevendita già presente
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws NotAvailableOperationException non si è loggati nel sistema
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws AuthorizationException l'utente non è amministratore per lo staff
     *        
     * @return WTipoPrevendita prevendita modificata
     */
    public static function modificaTipoPrevendita(UpdateNetWTipoPrevendita $tipoPrevendita): WTipoPrevendita
    {
        if (is_null($tipoPrevendita))
            throw new InvalidArgumentException("Parametri nulli.");

        if (! ($tipoPrevendita instanceof UpdateNetWTipoPrevendita))
            throw new InvalidArgumentException("Parametri non validi.");

        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        // Verifico che l'utente sia membro dello staff.
        if (! self::_isAmministratoreByTipoPrevendita($tipoPrevendita->getId()))
            throw new AuthorizationException("L'utente non è un amministratore dello staff.");

        $idUtente = Context::getContext()->getUtente()->getId();

        $conn = parent::getConnection();

        $stmtModifica = $conn->prepare("UPDATE tipoPrevendita SET nome = :nome, descrizione = :descrizione, prezzo = :prezzo, aperturaPrevendite = :aperturaPrevendite, chiusuraPrevendite = :chiusuraPrevendite, idModificatore = :idModificatore, timestampUltimaModifica = CURRENT_TIMESTAMP WHERE id = :id");
        $stmtModifica->bindValue(":id", $tipoPrevendita->getId(), PDO::PARAM_INT);
        $stmtModifica->bindValue(":nome", $tipoPrevendita->getNome(), PDO::PARAM_STR);
        $stmtModifica->bindValue(":descrizione", $tipoPrevendita->getDescrizione(), PDO::PARAM_STR);
        $stmtModifica->bindValue(":prezzo", strval($tipoPrevendita->getPrezzo()), PDO::PARAM_STR);
        $stmtModifica->bindValue(":aperturaPrevendite", $tipoPrevendita->getAperturaPrevendite()
            ->getDateTimeImmutable()
            ->format(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP), PDO::PARAM_STR);
        $stmtModifica->bindValue(":chiusuraPrevendite", $tipoPrevendita->getChiusuraPrevendite()
            ->getDateTimeImmutable()
            ->format(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP), PDO::PARAM_STR);
        $stmtModifica->bindValue(":idModificatore", $idUtente);

        try {
            $stmtModifica->execute();
        } catch (PDOException $ex) {
            // Mi assicuro di chiudere la connessione. Anche se teoricamente lo scope cancellerebbe comunque i riferimenti.
            $conn = NULL;

            if ($ex->getCode() == Amministratore::UNIQUE_CODE || $ex->getCode() == Amministratore::INTEGRITY_CODE) // Codice di integrità.
                throw new InsertUpdateException("tipo di prevenidita già presente.");

            throw $ex;
        }

        // Recupero i dati aggiuntivi per restituire un WTipoPrevenidta.
        // Mi serve il nome, etc

        $stmtRecuperoDati = $conn->prepare("SELECT idEvento, timestampUltimaModifica FROM tipoPrevendita WHERE id = :id");
        $stmtRecuperoDati->bindValue(":id", $tipoPrevendita->getId());

        $stmtRecuperoDati->execute();

        $fetch = $stmtRecuperoDati->fetch(PDO::FETCH_ASSOC);

        if (! $fetch) {
            throw new NotAvailableOperationException("Impossibile recuperare la prevendita");
        }

        $idEvento = (int) $fetch["idEvento"];
        $timestampUltimaModifica = $fetch["timestampUltimaModifica"];

        $conn = NULL;

        return $tipoPrevendita->getWTipoPrevendita($idEvento, $idUtente, $timestampUltimaModifica);
    }

    /**
     * Elimina un tipo di prevendita, solo se non sono state vendute prevendite di questo tipo.
     *
     * @param NetWId $tipoPrevendita
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws NotAvailableOperationException non si è loggati nel sistema oppure esistono prevendite che impediscono l'eliminazione
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws AuthorizationException l'utente non è amministratore per lo staff
     * 
     * @return WTipoPrevendita tipo prevendita eliminata
     */
    public static function eliminaTipoPrevendita(NetWId $tipoPrevendita): WTipoPrevendita
    {
        if (is_null($tipoPrevendita))
            throw new InvalidArgumentException("Parametri nulli.");

        if (! ($tipoPrevendita instanceof NetWId))
            throw new InvalidArgumentException("Parametri non validi.");

        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        // Verifico che l'utente sia membro dello staff.
        if (! self::_isAmministratoreByTipoPrevendita($tipoPrevendita->getId()))
            throw new AuthorizationException("L'utente non è un amministratore dello staff.");

        $conn = parent::getConnection();

        // Devo verificare prima che non ci siano prevendite per questo tipo di prevendita.
        // Fatto un trigger per evitare
        // $stmtVerifica = $conn->prepare("SELECT COUNT(*) AS conto FROM prevendita WHERE idTipoPrevendita = :idTipoPrevendita");
        // $stmtVerifica->bindValue(":idTipoPrevendita", $tipoPrevendita->getId(), PDO::PARAM_INT);
        // $stmtVerifica->execute();

        // $count = (int) $stmtVerifica->fetch(PDO::FETCH_ASSOC)["conto"];

        // if ($count > 0) {
        //     $conn = NULL;

        //     throw new NotAvailableOperationException("Esistono prevendite di questo tipo.");
        // }

        //Ricavo i dati del tipo prevendita.
        $stmtSelezione = $conn->prepare("SELECT id, idEvento, nome, descrizione, prezzo, aperturaPrevendite, chiusuraPrevendite, idModificatore, timestampUltimaModifica FROM tipoPrevendita WHERE id = :idTipoPrevendita");
        $stmtSelezione->bindValue(":idTipoPrevendita", $tipoPrevendita->getId(), PDO::PARAM_INT);
        $stmtSelezione->execute();

        $result = NULL;

        if (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $result = WTipoPrevendita::of($riga);
        }

        // Posso procedere: elimino il tipo di prevendita.

        $stmtElimina = $conn->prepare("DELETE FROM tipoPrevendita WHERE id = :idTipoPrevendita");
        $stmtElimina->bindValue(":idTipoPrevendita", $tipoPrevendita->getId(), PDO::PARAM_INT);

        try {
            $stmtElimina->execute();
        } catch (PDOException $ex) {
            // Mi assicuro di chiudere la connessione. Anche se teoricamente lo scope cancellerebbe comunque i riferimenti.
            $conn = NULL;

            if ($ex->getCode() == Amministratore::VINCOLO_CODE) // Codice di integrità.
                throw new InsertUpdateException("Esistono prevendite già vendute.");

            throw $ex;
        }

        $conn = NULL;

        return $result;
    }

    /**
     * Modifica i diritti di un utente.
     *
     * @param UpdateNetWDirittiUtente $dirittiUtente
     * @return WDirittiUtente
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws NotAvailableOperationException non si è loggati nel sistema
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws AuthorizationException l'utente non è amministratore per lo staff
     * @throws InsertUpdateException non è possibile modificare diritti di un non-membro
     */
    public static function modificaDirittiUtente(UpdateNetWDirittiUtente $dirittiUtente): WDirittiUtente
    {
        if (is_null($dirittiUtente))
            throw new InvalidArgumentException("Parametri nulli.");

        if (! ($dirittiUtente instanceof UpdateNetWDirittiUtente))
            throw new InvalidArgumentException("Parametri non validi.");

        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        // Verifico che l'utente sia membro dello staff.
        if (! self::_isAmministratore($dirittiUtente->getIdStaff()))
            throw new AuthorizationException("L'utente non è un amministratore dello staff.");

        $conn = parent::getConnection();

        // Dato che non mi fido dei dati, controllo che l'utente faccia parte dello staff.
        $stmtVerifica = $conn->prepare("SELECT COUNT(*) AS conto FROM membro WHERE idUtente = :idUtente AND idStaff = :idStaff");
        $stmtVerifica->bindValue(":idUtente", $dirittiUtente->getIdUtente(), PDO::PARAM_INT);
        $stmtVerifica->bindValue(":idStaff", $dirittiUtente->getIdStaff(), PDO::PARAM_INT);
        $stmtVerifica->execute();

        $count = (int) $stmtVerifica->fetch(PDO::FETCH_ASSOC)["conto"];

        if ($count === 0) {
            $conn = NULL;

            throw new InsertUpdateException("Non è possibile modificare diritti di un non-membro.");
        }

        $queryInserimento = "INSERT INTO :tabella: (idUtente, idStaff) VALUES (:idUtente, :idStaff)";
        $queryRimozione = "DELETE FROM :tabella: WHERE idUtente = :idUtente AND idStaff = :idStaff";

        // Procedo con la modifica.
        // La modifica risulta un po' particolare...
        // Inoltre deve essere svolta in modalità atomica.

        try {
            $conn->beginTransaction();

            // Posso scrivere i diritti che sono presenti.
            foreach ($dirittiUtente->getDiritti() as $diritto) {
                $stmtInserimento = $conn->prepare(str_replace(":tabella:", $diritto->toString(), $queryInserimento));
                $stmtInserimento->bindValue(":idStaff", $dirittiUtente->getIdStaff(), PDO::PARAM_INT);
                $stmtInserimento->bindValue(":idUtente", $dirittiUtente->getIdUtente(), PDO::PARAM_INT);

                try {
                    $stmtInserimento->execute();
                } catch (PDOException $ex) {
                    // Se restituisce UNIQUE exception vuol dire che il diritto non è stato modificato.
                    // Teoricamente non serve il rollback...
                    if ($ex->getCode() != Amministratore::UNIQUE_CODE && $ex->getCode() != Amministratore::INTEGRITY_CODE) // Codice di integrità.
                    {
                        // Qui serve il rollback...
                        $conn->rollBack();

                        // Mi assicuro di chiudere la connessione. Anche se teoricamente lo scope cancellerebbe comunque i riferimenti.
                        $conn = NULL;

                        throw $ex;
                    }
                }
            }

            // Passo all'eliminazione dei diritti non presenti.
            foreach (Diritto::complement($dirittiUtente->getDiritti()) as $diritto) {
                $stmtRimozione = $conn->prepare(str_replace(":tabella:", $diritto->toString(), $queryRimozione));
                $stmtRimozione->bindValue(":idStaff", $dirittiUtente->getIdStaff(), PDO::PARAM_INT);
                $stmtRimozione->bindValue(":idUtente", $dirittiUtente->getIdUtente(), PDO::PARAM_INT);
                $stmtRimozione->execute();
            }
        } catch (\PDOException $ex) {
            // Annullo le modifiche
            $conn->rollBack();
            throw $ex;
        }

        $conn->commit();
        $conn = NULL;

        return $dirittiUtente->getWDirittiUtente();
    }

    /**
     * Restituisce le statistiche di un PR dello staff selezionato.
     *
     * @param NetWId $pr
     * @param NetWId $staff
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws NotAvailableOperationException non si è loggati nel sistema
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws AuthorizationException l'utente non è amministratore per lo staff
     * @return ?WStatistichePRStaff
     */
    public static function getStatistichePR(NetWId $pr, NetWId $staff): ?WStatistichePRStaff
    {
        if (is_null($pr) || is_null($staff))
            throw new InvalidArgumentException("Parametri nulli.");

        if (! ($pr instanceof NetWId) || ! ($staff instanceof NetWId))
            throw new InvalidArgumentException("Parametri non validi.");

        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        // Verifico che l'utente sia membro dello staff.
        if (! self::_isAmministratore($staff->getId()))
            throw new AuthorizationException("L'utente non è un amministratore dello staff.");

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
        $stmtSelezione->bindValue(":idUtente", $pr->getId(), PDO::PARAM_INT);
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
     * Restituisce le statistiche di un cassiere dello staff selezionato.
     *
     * @param NetWId $cassiere
     * @param NetWId $staff
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws NotAvailableOperationException non si è loggati nel sistema
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws AuthorizationException l'utente non è amministratore per lo staff
     * @return NULL|WStatisticheCassiereStaff Restituisce un array con le statistiche
     */
    public static function getStatisticheCassiere(NetWId $cassiere, NetWId $staff): ?WStatisticheCassiereStaff
    {
        if (is_null($cassiere) || is_null($staff))
            throw new InvalidArgumentException("Parametri nulli.");

        if (! ($cassiere instanceof NetWId) || ! ($staff instanceof NetWId))
            throw new InvalidArgumentException("Parametri non validi.");

        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        // Verifico che l'utente sia membro dello staff.
        if (! self::_isAmministratore($staff->getId()))
            throw new AuthorizationException("L'utente non è un amministratore dello staff.");

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
        $stmtSelezione->bindValue(":idUtente", $cassiere->getId(), PDO::PARAM_INT);
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
     * Restituisce le statistiche di un evento.
     *
     * @param NetWId $evento
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws NotAvailableOperationException non si è loggati nel sistema
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws AuthorizationException l'utente non è amministratore per lo staff
     * @return WStatisticheEvento[] Restituisce un array con le statistiche
     */
    public static function getStatisticheEvento(NetWId $evento): array
    {
        if (is_null($evento))
            throw new InvalidArgumentException("Parametri nulli.");

        if (! ($evento instanceof NetWId))
            throw new InvalidArgumentException("Parametri non validi.");

        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        // Verifico che l'utente sia membro dello staff.
        if (! self::_isAmministratoreByEvento($evento->getId()))
            throw new AuthorizationException("L'utente non è un amministratore dello staff.");

        $conn = parent::getConnection();

        //Query XAMPP:
        //$query = "SELECT idEvento, idTipoPrevendita, prevenditeVendute, ricavo FROM statisticheEvento WHERE idEvento = :idEvento";

        //Query ALTERVISTA:
        $query = <<<EOT
        SELECT T.idEvento, T.idTipoPrevendita, T.nomeTipoPrevendita, T.prevenditeVendute, T.ricavo, T.prevenditeEntrate, T.prevenditeNonEntrate
        FROM (SELECT evento.id AS idEvento, prevendita.idTipoPrevendita AS idTipoPrevendita, tipoPrevendita.nome AS nomeTipoPrevendita, COUNT(prevendita.id) AS prevenditeVendute, SUM(tipoPrevendita.prezzo) AS ricavo, COUNT(entrata.seq) AS prevenditeEntrate, COUNT(prevendita.id)-COUNT(entrata.seq) AS prevenditeNonEntrate
            FROM evento 
            INNER JOIN prevendita ON prevendita.idEvento = evento.id 
            INNER JOIN tipoPrevendita ON tipoPrevendita.idEvento = evento.id AND tipoPrevendita.id = prevendita.idTipoPrevendita 
            LEFT JOIN entrata ON entrata.idPrevendita = prevendita.id
            GROUP BY evento.id, prevendita.idTipoPrevendita) AS T
        WHERE T.idEvento = :idEvento
EOT;

        $stmtSelezione = $conn->prepare($query);
        $stmtSelezione->bindValue(":idEvento", $evento->getId(), PDO::PARAM_INT);
        $stmtSelezione->execute();

        $result = array();

        while (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $result[] = WStatisticheEvento::of($riga);
        }

        $conn = NULL;

        return $result;
    }

    /**
     * Restituisce le prevendite di un evento.
     *
     * @param NetWId $evento
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws NotAvailableOperationException non si è loggati nel sistema
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws AuthorizationException l'utente non è amministratore per lo staff
     * @return array
     */
    public static function getPrevendite(NetWId $evento): array
    {
        if (is_null($evento))
            throw new InvalidArgumentException("Parametri nulli.");

        if (! ($evento instanceof NetWId))
            throw new InvalidArgumentException("Parametri non validi.");

        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        // Verifico che l'utente sia membro dello staff.
        if (! self::_isAmministratoreByEvento($evento->getId()))
            throw new AuthorizationException("L'utente non è un amministratore dello staff.");

        $conn = parent::getConnection();

        // Posso direttamente selezionare le prevendite dell'utente.

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

    /**
     * Rimuove un utente membro.
     *
     * @param NetWId $membro membro da rimuovere
     * @param NetWId $staff staff di cui fa parte il membro
     * @throws InvalidArgumentException Parametri non validi
     * @throws NotAvailableOperationException Utente amministratore non loggato
     * @throws AuthorizationException L'utente non è amministratore dello staff
     * @throws InsertUpdateException Non è stato possibile eliminare il membro
     * 
     * @return WUtente utente eliminato.
     */
    public static function rimuoviMembro(NetWId $membro, NetWId $staff) : WUtente
    {
        if (is_null($membro) || is_null($staff))
            throw new InvalidArgumentException("Parametri nulli.");

        if (! ($membro instanceof NetWId) || ! (($staff instanceof NetWId)))
            throw new InvalidArgumentException("Parametri non validi.");

        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        // Verifico che l'utente sia membro dello staff.
        if (! self::_isAmministratore($staff->getId()))
            throw new AuthorizationException("L'utente non è un amministratore dello staff.");

        $conn = parent::getConnection();

        // Verifico che il membro sia effettivamente un membro.

        $stmtSelezione = $conn->prepare("SELECT * FROM membro WHERE idUtente = :idUtente AND idStaff = :idStaff");
        $stmtSelezione->bindValue(":idUtente", $membro->getId(), PDO::PARAM_INT);
        $stmtSelezione->bindValue(":idStaff", $staff->getId(), PDO::PARAM_INT);
        $stmtSelezione->execute();

        if (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {

            // Procedo con l'eliminazione.

            $stmtDelete = $conn->prepare("DELETE FROM membro WHERE idUtente = :idUtente AND idStaff = :idStaff");
            $stmtDelete->bindValue(":idUtente", $membro->getId(), PDO::PARAM_INT);
            $stmtDelete->bindValue(":idStaff", $staff->getId(), PDO::PARAM_INT);
            $stmtDelete->execute();

            //Ricavo i dati dell'utente eliminato.
            $stmtSelezione = $conn->prepare("SELECT id, nome, cognome, telefono FROM utente WHERE id = :idUtente");
            $stmtSelezione->bindValue(":idUtente", $membro->getId(), PDO::PARAM_INT);
            $stmtSelezione->execute();
    
            $result = NULL;
    
            if (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
                $result = WUtente::of($riga);
            }

            $conn = NULL;

            return $result;
        }

        $conn = NULL;

        throw new InsertUpdateException("Il membro non fa parte dello staff.");
    }

    /**
     * Cambia il codice di accesso per lo staff
     *
     * @param UpdateNetWStaff $staff
     *            Staff a cui cambiare il codice
     * @throws InvalidArgumentException Parametri non validi
     * @throws NotAvailableOperationException Utente non loggato
     * @throws AuthorizationException L'utente non è amministratore dello staff designato
     */
    public static function modificaCodiceAccesso(UpdateNetWStaff $staff)
    {
        if (is_null($staff))
            throw new InvalidArgumentException("Parametri nulli.");

        if (! ($staff instanceof UpdateNetWStaff))
            throw new InvalidArgumentException("Parametro non valido (UpdateNetWStaff).");

        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        // Verifico che l'utente sia membro dello staff.
        if (! self::_isAmministratore($staff->getId()))
            throw new AuthorizationException("L'utente non è un amministratore dello staff.");

        // Preparo l'hash da inserire...
        $hash = Hash::getSingleton()->hashPassword($staff->getCodiceAccesso());

        $conn = parent::getConnection();

        // Cambio il codice....

        $stmtUpdate = $conn->prepare("UPDATE staff SET hash = :hash WHERE id = :idStaff");
        $stmtUpdate->bindValue(":idStaff", $staff->getId(), PDO::PARAM_INT);
        $stmtUpdate->bindValue(":hash", $hash, PDO::PARAM_STR);
        $stmtUpdate->execute();

        $conn = NULL;

        $staff->clear();
    }

    /**
     * Restituisce le statistiche del PR in un evento.
     *
     * @param NetWId $evento
     * @param NetWId $pr
     * @throws AuthorizationException l'utente che ha richiesto le statistiche non è amministratore.
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws NotAvailableOperationException l'utente non è loggato nel sistema.
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return WStatistichePREvento[] Restituisce un array con le statistiche
     */
    public static function getStatistichePREvento(NetWId $evento, NetWId $pr): array
    {
        // Verifico i parametri
        if (is_null($evento))
            throw new InvalidArgumentException("Parametro nullo.");

        if (! ($evento instanceof NetWId))
            throw new InvalidArgumentException("Parametro evento non valido.");

        if (! ($pr instanceof NetWId))
            throw new InvalidArgumentException("Parametro pr non valido.");

        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        //Verifico che l'utente sia un amministratore dello staff dell'evento
        if (! self::_isAmministratoreByEvento($evento->getId()))
            throw new AuthorizationException("L'utente non è un amministratore dello staff.");


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
            WHERE prevendita.stato = 'PAGATA'
            GROUP BY pr.idUtente, pr.idStaff, prevendita.idEvento, prevendita.idTipoPrevendita) AS T
        WHERE T.idUtente = :idUtente AND T.idEvento = :idEvento
EOT;

        $stmtSelezione = $conn->prepare($query);
        $stmtSelezione->bindValue(":idUtente", $pr->getId(), PDO::PARAM_INT);
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
     * Restituisce le statistiche del cassiere in un evento.
     *
     * @param NetWId $evento
     * @throws AuthorizationException l'utente che ha richiesto le statistiche non è amministratore.
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws NotAvailableOperationException l'utente non è loggato nel sistema.
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return ?WStatisticheCassiereEvento Restituisce il wrapper. Se non sono disponibili statistiche restituisce NULL.
     */
    public static function getStatisticheCassiereEvento(NetWId $evento, NetWId $cassiere): ?WStatisticheCassiereEvento
    {
        // Verifico i parametri
        if (is_null($evento))
            throw new InvalidArgumentException("Parametri nulli.");

        if (! ($evento instanceof NetWId))
            throw new InvalidArgumentException("Parametro evento non valido.");

        if (! ($cassiere instanceof NetWId))
            throw new InvalidArgumentException("Parametro cassiere non valido.");


        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        //Verifico che l'utente sia un amministratore dello staff dell'evento
        if (! self::_isAmministratoreByEvento($evento->getId()))
            throw new AuthorizationException("L'utente non è un amministratore dello staff.");

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
        $stmtSelezione->bindValue(":idUtente", $cassiere->getId(), PDO::PARAM_INT);
        $stmtSelezione->bindValue(":idEvento", $evento->getId(), PDO::PARAM_INT);
        $stmtSelezione->execute();

        $result = NULL;

        if (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $result = WStatisticheCassiereEvento::of($riga);
        }

        $conn = NULL;

        return $result;
    }

    private function __construct()
    {}
}

