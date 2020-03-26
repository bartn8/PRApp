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

use com\model\db\wrapper\WStaff;
use com\model\db\wrapper\WUtente;
use com\model\db\wrapper\WToken;
use InvalidArgumentException;
use PDO;
use PDOException;
use com\model\db\exception\InsertUpdateException;
use com\model\db\exception\NotAvailableOperationException;
use com\model\Context;
use com\model\Hash;
use com\model\db\exception\AuthorizationException;
use com\model\net\wrapper\NetWLogin;
use com\model\net\wrapper\NetWToken;
use com\model\net\wrapper\insert\InsertNetWStaff;
use com\utils\DateTimeImmutableAdapterJSON;
use com\model\net\wrapper\NetWStaffAccess;
use com\model\net\wrapper\insert\InsertNetWUtente;

class Utente extends Table
{

    /**
     * Crea un nuovo utente.
     *
     * @param InsertNetWUtente $utente
     *            Informazioni sull'utente
     * @param CustomWLogin $login
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws InsertUpdateException username già utilizzato
     * @throws NotAvailableOperationException si è loggati nel sistema
     *        
     * @return WUtente utente registrato
     */
    public static function registrazione($utente)
    {
        // Verifico che non si è loggati nel sistema.
        if (Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente loggato.");

        // Verifico i parametri
        if (! ($utente instanceof InsertNetWUtente))
            throw new InvalidArgumentException("Parametro non valido.");

        // Il bello che non devo verificare i wrapper perchè i dati sono già confermati :)

        // Preparo l'hash da inserire...
        $hash = Hash::getSingleton()->hashPassword($utente->getPassword());

        $conn = parent::getConnection();

        $stmtInserimentoUtente = $conn->prepare("INSERT INTO utente (nome, cognome, telefono, username, hash) VALUES (:nome, :cognome, :telefono, :username, :hash)");
        $stmtInserimentoUtente->bindValue(":nome", $utente->getNome(), PDO::PARAM_STR);
        $stmtInserimentoUtente->bindValue(":cognome", $utente->getCognome(), PDO::PARAM_STR);
        $stmtInserimentoUtente->bindValue(":telefono", $utente->getTelefono(), PDO::PARAM_STR);
        $stmtInserimentoUtente->bindValue(":username", $utente->getUsername(), PDO::PARAM_STR);
        $stmtInserimentoUtente->bindValue(":hash", $hash, PDO::PARAM_STR);

        try {
            $stmtInserimentoUtente->execute();
        } catch (PDOException $ex) {
            // Mi assicuro di chiudere la connessione. Anche se teoricamente lo scope cancellerebbe comunque i riferimenti.
            $conn = NULL;

            // Pulisco i campi di login.
            $utente->clear();

            if ($ex->getCode() == Utente::UNIQUE_CODE) // Codice di integrità UNIQUE.
                throw new InsertUpdateException("Username già utilizzato.");

            throw $ex;
        }

        // Da utilizzare successivamente per la creazione del wrapper.
        $idUtente = (int)$conn->lastInsertId();

        // Mi assicuro di chiudere la connessione. Anche se teoricamente lo scope cancellerebbe comunque i riferimenti.
        $conn = NULL;

        // Pulisco i campi di login.
        $utente->clear();

        return $utente->getWUtente($idUtente);
    }

    /**
     * Effettua il login.
     * Aggiorna il contesto di conseguenza.
     *
     * @param NetWLogin $login
     * @param string $password
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws NotAvailableOperationException si è loggati nel sistema
     * @throws AuthorizationException login errato
     * @return WUtente dati dell'utente.
     */
    public static function login($login)
    {
        // Verifico che non si è loggati nel sistema.
        if (Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente loggato.");

        // Verifico i parametri
        if (! ($login instanceof NetWLogin))
            throw new InvalidArgumentException("Parametri non validi.");

        // Prima devo recuperare tutti i dati dell'utente, la password dell'utente.

        $conn = parent::getConnection();

        $stmtSelezione = $conn->prepare("SELECT id, nome, cognome, telefono, hash FROM utente WHERE username = :username");
        $stmtSelezione->bindValue(":username", $login->getUsername(), PDO::PARAM_STR);
        $stmtSelezione->execute();

        // Prendo la prima riga, dato che username univoco.
        $riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC);

        // Mi assicuro di chiudere la connessione. Anche se teoricamente lo scope cancellerebbe comunque i riferimenti.
        // Posso già chiudere qua la connessione, dato che il fetch è già stato fatto.
        $conn = NULL;

        // Se riga falsa restituisco falso.
        if ($riga !== FALSE) {
            // Controllo della password.
            if (Hash::getSingleton()->evalutatePassword($login->getPassword(), $riga["hash"])) {
                // Inizializzo il contesto.
                Context::createContext(WUtente::of($riga));

                // Pulisco i campi di login.
                $login->clear();

                return Context::getContext()->getUtente();
            }
        }

        // Pulisco i campi di login.
        $login->clear();

        throw new AuthorizationException("Dati di login non corretti.");
    }

    /**
     * Crea un nuovo staff e inserisce automaticamente l'utente come amministratore.
     *
     * @param InsertNetWStaff $staff
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws NotAvailableOperationException non si è loggati nel sistema
     * @return \com\model\db\wrapper\WStaff Wrapper dello staff appena creato. Il timestamp non è corretto
     */
    public static function creaStaff($staff)
    {
        // Verifico che si è loggati nel sistema.
        if (! (Context::getContext()->isValid()))
            throw new NotAvailableOperationException("Utente non loggato.");

        // Verifico i parametri
        if (! ($staff instanceof InsertNetWStaff))
            throw new InvalidArgumentException("Parametro non valido.");

        // Creo l'hash relativo al codice.
        $hash = Hash::getSingleton()->hashPassword($staff->getCodiceAccesso());

        // Pulisco il campo codiceAccesso
        $staff->clear();

        // Ricavo l'idUtente che mi servirà successivamente.
        $idUtente = Context::getContext()->getUtente()->getId();

        $conn = parent::getConnection();

        try {
            // L'operazione è atomica.
            $conn->beginTransaction();

            // --OPERAZIONE DI INSERIMENTO DELLO STAFF--

            {
                $stmtInserimentoStaff = $conn->prepare("INSERT INTO staff (nome, hash) VALUES (:nome, :hash)");
                $stmtInserimentoStaff->bindValue(":nome", $staff->getNome(), PDO::PARAM_STR);
                $stmtInserimentoStaff->bindValue(":hash", $hash, PDO::PARAM_STR);
                $stmtInserimentoStaff->execute();
            }

            // Da utilizzare successivamente per la creazione del wrapper.
            $idStaff = (int) $conn->lastInsertId();

            // --OPERAZIONE DI INSERIMENTO DEL MEMBRO--
            {
                $stmtInserimentoMembro = $conn->prepare("INSERT INTO membro (idUtente, idStaff) VALUES(:idUtente, :idStaff)");
                $stmtInserimentoMembro->bindValue(":idUtente", $idUtente, PDO::PARAM_INT); // Sessione già verificata precedentemente.
                $stmtInserimentoMembro->bindValue(":idStaff", $idStaff, PDO::PARAM_INT);
                $stmtInserimentoMembro->execute();
            }

            // --OPERAZIONE DI INSERIMENTO DELL'AMMINISTRATORE--
            {
                $stmtInserimentoAmministratore = $conn->prepare("INSERT INTO amministratore (idUtente, idStaff) VALUES(:idUtente, :idStaff)");
                $stmtInserimentoAmministratore->bindValue(":idUtente", $idUtente, PDO::PARAM_INT); // Sessione già verificata precedentemente.
                $stmtInserimentoAmministratore->bindValue(":idStaff", $idStaff, PDO::PARAM_INT);
                $stmtInserimentoAmministratore->execute();
            }
        } catch (\PDOException $ex) {
            //Annullo le modifiche
            $conn->rollBack();
            throw $ex;
        }

        // Concludo l'operazione.
        $conn->commit();

        // Mi assicuro di chiudere la connessione. Anche se teoricamente lo scope cancellerebbe comunque i riferimenti.
        $conn = NULL;

        // Tutto è andato a buon fine, posso restituire il wrapper WStaff.
        // Il timestamp è fasullo ma chissene.
        return $staff->getWStaff($idStaff, new DateTimeImmutableAdapterJSON(new \DateTimeImmutable()));
    }

    /**
     * Permette all'utente di accedere come membro allo staff se il codice è corretto.
     *
     * @param NetWStaffAccess $wrapper
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws InvalidArgumentException parametri nulli o non validi
     * @throws InsertUpdateException si è già inscritti nello staff
     * @throws NotAvailableOperationException non si è loggati nel sistema
     * @throws AuthorizationException codice errato
     * 
     * @return WStaff Staff a cui abbiamo avuto accesso.
     */
    public static function accediStaff($staff)
    {
        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        // Verifico i parametri
        if (! ($staff instanceof NetWStaffAccess))
            throw new InvalidArgumentException("Parametri non validi.");

        $idUtente = Context::getContext()->getUtente()->getId();

        // Recupero i dati dello staff.
        $conn = parent::getConnection();

        $stmtSelezione = $conn->prepare("SELECT hash FROM staff WHERE id = :idStaff");
        $stmtSelezione->bindValue(":idStaff", $staff->getIdStaff(), PDO::PARAM_INT);
        $stmtSelezione->execute();

        // Prendo la prima riga.
        $riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC);

        // Se riga falsa restituisco falso.
        if ($riga !== FALSE) {
            // Controllo dell'Hash.
            if (Hash::getSingleton()->evalutatePassword($staff->getCodiceAccesso(), $riga["hash"])) {
                // Hash verificato! Inserisco l'utente nei membri.
                $stmtInserimento = $conn->prepare("INSERT INTO membro (idUtente, idStaff) VALUES (:idUtente, :idStaff)");
                $stmtInserimento->bindValue(":idUtente", $idUtente, PDO::PARAM_INT);
                $stmtInserimento->bindValue(":idStaff", $staff->getIdStaff(), PDO::PARAM_INT);

                try {
                    $stmtInserimento->execute();
                } catch (PDOException $ex) {

                    // Pulisco il campo codiceAccesso
                    $staff->clear();

                    // Mi assicuro di chiudere la connessione. Anche se teoricamente lo scope cancellerebbe comunque i riferimenti.
                    $conn = NULL;

                    if ($ex->getCode() == Utente::UNIQUE_CODE || $ex->getCode() == Utente::INTEGRITY_CODE) // Codice di integrità.
                        throw new InsertUpdateException("Sei già membro dello staff.");

                    throw $ex;
                }

                // Pulisco il campo codiceAccesso
                $staff->clear();

                //Recupero i dati dello staff e li restituisco.

                $stmtSelezione = $conn->prepare("SELECT id, nome, timestampCreazione FROM staff WHERE id = :idStaff");
                $stmtSelezione->bindValue(":idStaff", $staff->getIdStaff(), PDO::PARAM_INT);
                $stmtSelezione->execute();

                $getStaff = WStaff::of($stmtSelezione->fetch(PDO::FETCH_ASSOC));

                // Mi assicuro di chiudere la connessione. Anche se teoricamente lo scope cancellerebbe comunque i riferimenti.
                $conn = NULL;

                return $getStaff;
            }
        }

        // Pulisco il campo codiceAccesso
        $staff->clear();

        // Mi assicuro di chiudere la connessione. Anche se teoricamente lo scope cancellerebbe comunque i riferimenti.
        $conn = NULL;

        throw new AuthorizationException("Codice di accesso non corretto.");
    }

    /**
     * Restituisce la lista degli staff.
     *
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws NotAvailableOperationException non si è loggati nel sistema
     * @return WStaff[] Lista degli staff
     */
    public static function getListaStaff()
    {
        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        // Recupero i dati degli staff.
        $conn = parent::getConnection();

        $stmtSelezione = $conn->prepare("SELECT id, nome, timestampCreazione FROM staff");
        $stmtSelezione->execute();

        $lista = array();

        while (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $lista[] = WStaff::of($riga);
        }

        $conn = NULL;

        return $lista;
    }

    /**
     * Restituisce la lista degli staff di cui si è membro.
     *
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws NotAvailableOperationException non si è loggati nel sistema
     * @return WStaff[] Lista degli staff di cui si è membro
     */
    public static function getListaStaffMembri()
    {
        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        // Recupero i dati degli staff.
        $conn = parent::getConnection();

        $stmtSelezione = $conn->prepare("SELECT id, nome, timestampCreazione FROM staff INNER JOIN membro ON membro.idStaff = staff.id WHERE membro.idUtente = :idUtente");
        $stmtSelezione->bindValue(":idUtente", Context::getContext()->getUtente()
            ->getId(), PDO::PARAM_INT);
        $stmtSelezione->execute();

        $lista = array();

        while (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $lista[] = WStaff::of($riga);
        }

        $conn = NULL;

        return $lista;
    }

    /**
     * Rinnova il token di accesso.
    *
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws NotAvailableOperationException non si è loggati nel sistema
     * @return WToken token di accesso
     */
    public static function renewToken()
    {
        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        $newToken = Hash::getSingleton()->getToken();

        // Recupero i dati degli staff.
        $conn = parent::getConnection();

        //https://stackoverflow.com/questions/8070765/adding-12-hours-to-mysql-current-timestamp
        $stmtUpdate = $conn->prepare("UPDATE utente SET token = :token, scadenzaToken = (CURRENT_TIMESTAMP + INTERVAL :giorni DAY) WHERE id = :idUtente");
        $stmtUpdate->bindValue(":giorni", $GLOBALS["scadenzaTokenGiorni"], PDO::PARAM_INT);
        $stmtUpdate->bindValue(":token", $newToken, PDO::PARAM_STR);
        $stmtUpdate->bindValue(":idUtente", Context::getContext()->getUtente()->getId(), PDO::PARAM_INT);
        $stmtUpdate->execute();

        //Ricavo la scadenza.
        $stmtSelezione = $conn->prepare("SELECT scadenzaToken FROM utente WHERE id = :idUtente");
        $stmtSelezione->bindValue(":idUtente", Context::getContext()->getUtente()->getId(), PDO::PARAM_INT);
        $stmtSelezione->execute();

        $wrapper = NULL;

        if (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            if(!is_null($riga["scadenzaToken"]))
                $wrapper = \DateTimeImmutable::createFromFormat(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP, $riga["scadenzaToken"]);
        }

        $conn = NULL;

        return WToken::makeNoChecks(Context::getContext()->getUtente()->getId(), $newToken, is_null($wrapper) ? NULL : new DateTimeImmutableAdapterJSON($wrapper));
    }

    /**
     * Restituisce il token di accesso se presente.
     *
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws NotAvailableOperationException non si è loggati nel sistema
     * @return WToken token di accesso
     */
    public static function getToken()
    {
        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        // Recupero i dati degli staff.
        $conn = parent::getConnection();

        $stmtSelezione = $conn->prepare("SELECT id, token, scadenzaToken FROM utente WHERE id = :idUtente");
        $stmtSelezione->bindValue(":idUtente", Context::getContext()->getUtente()->getId(), PDO::PARAM_INT);
        $stmtSelezione->execute();

        $result = NULL;

        if(($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $result = WToken::of($riga);
        }

        $conn = NULL;

        return $result;
    }

    /**
     * Effettua il login tramite token.
     *
     * @param NetWToken $token token di accesso.
     * 
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws NotAvailableOperationException non si è loggati nel sistema
     * @throws AuthorizationException token non valido
     * @return WUtente utente a cui si è avuto accesso.
     */
    public static function loginToken($token)
    {
        // Verifico che non si è loggati nel sistema.
        if (Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente loggato.");

        // Verifico i parametri
        if (! ($token instanceof NetWToken))
            throw new InvalidArgumentException("Parametri non validi.");

        //Mi serve un DateTime per la verifica della scadenza del token.
        $dateNow = new \DateTime();

        // Prima devo recuperare tutti i dati dell'utente, la password dell'utente.

        $conn = parent::getConnection();

        $stmtSelezione = $conn->prepare("SELECT id, nome, cognome, telefono, token, scadenzaToken FROM utente WHERE token = :token");
        $stmtSelezione->bindValue(":token", $token->getToken(), PDO::PARAM_STR);
        $stmtSelezione->execute();

        // Prendo la prima riga, dato che username univoco.
        $riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC);

        // Mi assicuro di chiudere la connessione. Anche se teoricamente lo scope cancellerebbe comunque i riferimenti.
        // Posso già chiudere qua la connessione, dato che il fetch è già stato fatto.
        $conn = NULL;

        // Se riga falsa restituisco falso.
        if ($riga !== FALSE) {
            //Controllo che ci sia un token
            if(!is_null($riga["token"])){
                // Controllo scadenza token.
                // Nel DB non è prevista una data nulla se il token non è nullo: nessuna eccezione da format strano.
                $scadenzaToken = \DateTime::createFromFormat(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP, $riga["scadenzaToken"]);

                if ($dateNow < $scadenzaToken) {
                    // Inizializzo il contesto.
                    Context::createContext(WUtente::of($riga));

                    // Pulisco i campi di login.
                    $token->clear();

                    return Context::getContext()->getUtente();
                }
            }
        }

        // Pulisco i campi di login.
        $token->clear();

        throw new AuthorizationException("Token non valido.");
    }
    
    public static function getStaff(int $utente, int $staff){
        // Recupero i dati degli staff.
        $conn = parent::getConnection();
        
        $stmtSelezione = $conn->prepare("SELECT s.id, s.nome, s.timestampCreazione FROM staff AS s INNER JOIN membro AS m ON s.id = m.idStaff WHERE s.id = :idStaff AND m.idUtente = :idUtente");
        $stmtSelezione->bindValue(":idUtente", $utente, PDO::PARAM_INT);
        $stmtSelezione->bindValue(":idStaff", $staff, PDO::PARAM_INT);
        $stmtSelezione->execute();
        
        $result = NULL;
        
        if (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $result = WStaff::of($riga);
        }
        
        $conn = NULL;
        
        return $result;
    }

    private function __construct()
    {}
}

