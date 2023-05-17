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

use \PDO;
use DateTime;
use \PDOException;
use com\model\Hash;
use com\model\Context;
use DateTimeImmutable;
use com\model\db\table\Table;
use \InvalidArgumentException;
use com\model\db\table\Utente;
use com\model\db\enum\StatoLog;
use com\model\db\wrapper\WStaff;
use com\model\db\wrapper\WToken;
use com\model\db\wrapper\WUtente;
use com\model\net\wrapper\NetWPwd;
use com\model\net\wrapper\NetWLogin;
use com\model\net\wrapper\NetWToken;
use com\model\net\wrapper\NetWStaffAccess;
use com\utils\DateTimeImmutableAdapterJSON;
use com\model\db\exception\InsertUpdateException;
use com\model\net\wrapper\insert\InsertNetWStaff;
use com\model\db\exception\AuthorizationException;
use com\model\net\wrapper\insert\InsertNetWUtente;
use com\model\db\exception\NotAvailableOperationException;

class Utente extends Table
{

    /**
     * Crea un nuovo utente.
     *
     * @param InsertNetWUtente $utente Informazioni sull'utente
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws InsertUpdateException username già utilizzato
     *        
     * @return WUtente utente registrato
     */
    public static function registrazione(InsertNetWUtente $utente) : WUtente
    {
        // Preparo l'hash da inserire...
        $hash = Hash::getSingleton()->hashPassword($utente->getPassword());

        $conn = parent::getConnection();

        $stmtInserimentoUtente = $conn->prepare("INSERT INTO utente (nome, cognome, telefono, username, hash) VALUES (:nome, :cognome, :telefono, :username, :hash)");
        //$stmtInserimentoUtente->bindValue(":tipologiaUtente", $utente->getTipologiaUtente()->toString(), PDO::PARAM_STR);
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
     * @param int $tentativiLogin
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws AuthorizationException login errato
     * @return WUtente dati dell'utente.
     */
    public static function login(NetWLogin $login, int $tentativiLogin) : WUtente
    {
        // Prima devo recuperare tutti i dati dell'utente, la password dell'utente.
        $msgError = "Dati di login non corretti.";

        $conn = parent::getConnection();

        $stmtSelezione = $conn->prepare("SELECT id, nome, cognome, telefono, hash, tentativiLogin FROM utente WHERE username = :username");
        $stmtSelezione->bindValue(":username", $login->getUsername(), PDO::PARAM_STR);
        $stmtSelezione->execute();

        // Prendo la prima riga, dato che username univoco.
        $riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC);

        // Se riga falsa restituisco falso.
        if ($riga !== FALSE) {
            //Controllo tentativi
            if($riga["tentativiLogin"] < $tentativiLogin){
                //Controllo se l'utente è attivo.
                if($riga["hash"] !== ""){
                    // Controllo della password.
                    if (Hash::getSingleton()->evalutatePassword($login->getPassword(), $riga["hash"])) {

                        //Pulizia tentativi login
                        $stmtUpdate = $conn->prepare("UPDATE utente SET tentativiLogin = 0 WHERE username = :username");
                        $stmtUpdate->bindValue(":username", $login->getUsername(), PDO::PARAM_STR);
                        $stmtUpdate->execute();

                        // Pulisco i campi di login.
                        $login->clear();

                        return WUtente::of($riga);
                    }
                }else{
                    $stmtInserimentoLog = $conn->prepare("INSERT INTO tabellaLog (livello, messaggio) VALUES (:livello, :messaggio)");
                    $stmtInserimentoLog->bindValue(":livello", StatoLog::of(StatoLog::WARNING)->toString(), PDO::PARAM_STR);
                    $stmtInserimentoLog->bindValue(":messaggio", "Accesso a account disabilitato: ".$login->getUsername(), PDO::PARAM_STR);
                    $stmtInserimentoLog->execute();
                    
                    $msgError = "Account disabilitato: contatta un amministratore";
                }
            }else{
                $stmtInserimentoLog = $conn->prepare("INSERT INTO tabellaLog (livello, messaggio) VALUES (:livello, :messaggio)");
                $stmtInserimentoLog->bindValue(":livello", StatoLog::of(StatoLog::WARNING)->toString(), PDO::PARAM_STR);
                $stmtInserimentoLog->bindValue(":messaggio", "Troppi tentativi per ".$login->getUsername(), PDO::PARAM_STR);
                $stmtInserimentoLog->execute();
                
                $msgError = "Troppi tentativi di login: contatta un amministratore";
            }
        }

        //Devo aggiornare i tentativi di login
        $stmtUpdate = $conn->prepare("UPDATE utente SET tentativiLogin = tentativiLogin + 1 WHERE username = :username");
        $stmtUpdate->bindValue(":username", $login->getUsername(), PDO::PARAM_STR);
        $stmtUpdate->execute();

        // Mi assicuro di chiudere la connessione. Anche se teoricamente lo scope cancellerebbe comunque i riferimenti.
        $conn = NULL;

        // Pulisco i campi di login.
        $login->clear();

        throw new AuthorizationException($msgError);
    }

    /**
     * Effettua il cambio password.
     *
     * @param int $idUtente
     * @param NetWPwd $changepwd
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws AuthorizationException login errato
     * @return WUtente dati dell'utente.
     */
    public static function cambiaPassword(int $idUtente, NetWPwd $changepwd) : WUtente
    {
        // Prima devo recuperare tutti i dati dell'utente, la password dell'utente.
        $msgError = "Dati di login non corretti.";

        $conn = parent::getConnection();

        $stmtSelezione = $conn->prepare("SELECT id, nome, cognome, telefono, hash, tentativiLogin FROM utente WHERE id = :id");
        $stmtSelezione->bindValue(":id", $idUtente, PDO::PARAM_INT);
        $stmtSelezione->execute();

        // Prendo la prima riga, dato che username univoco.
        $riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC);

        // Se riga falsa restituisco falso.
        if ($riga !== FALSE) {
            //Controllo se l'utente è attivo.
            if($riga["hash"] !== ""){
                // Controllo della password.
                if (Hash::getSingleton()->evalutatePassword($changepwd->getOldpwd(), $riga["hash"])) {

                    // Creo l'hash relativo alla nuova password.
                    $newhash = Hash::getSingleton()->hashPassword($changepwd->getNewpwd());

                    //Pulizia tentativi login
                    $stmtUpdate = $conn->prepare("UPDATE utente SET tentativiLogin = 0, hash = :hash, ultimoChangePwd = CURRENT_TIMESTAMP WHERE id = :id");
                    $stmtUpdate->bindValue(":id", $idUtente, PDO::PARAM_INT);
                    $stmtUpdate->bindValue(":hash", $newhash, PDO::PARAM_STR);
                    $stmtUpdate->execute();

                    // Pulisco i campi di login.
                    $changepwd->clear();

                    $stmtInserimentoLog = $conn->prepare("INSERT INTO tabellaLog (livello, messaggio) VALUES (:livello, :messaggio)");
                    $stmtInserimentoLog->bindValue(":livello", StatoLog::of(StatoLog::WARNING)->toString(), PDO::PARAM_STR);
                    $stmtInserimentoLog->bindValue(":messaggio", "Utente ha cambiato password ID: ".$idUtente, PDO::PARAM_STR);
                    $stmtInserimentoLog->execute();

                    return WUtente::of($riga);
                }
            }else{
                $stmtInserimentoLog = $conn->prepare("INSERT INTO tabellaLog (livello, messaggio) VALUES (:livello, :messaggio)");
                $stmtInserimentoLog->bindValue(":livello", StatoLog::of(StatoLog::WARNING)->toString(), PDO::PARAM_STR);
                $stmtInserimentoLog->bindValue(":messaggio", "Accesso a account disabilitato ID: ".$idUtente, PDO::PARAM_STR);
                $stmtInserimentoLog->execute();
                
                $msgError = "Account disabilitato: contatta un amministratore";
            }
        }

        // Mi assicuro di chiudere la connessione. Anche se teoricamente lo scope cancellerebbe comunque i riferimenti.
        $conn = NULL;

        // Pulisco i campi di login.
        $changepwd->clear();

        throw new AuthorizationException($msgError);
    }

    /**
     * Crea un nuovo staff e inserisce automaticamente l'utente come amministratore.
     *
     * @param int $idUtente
     * @param InsertNetWStaff $staff
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return \com\model\db\wrapper\WStaff Wrapper dello staff appena creato. Il timestamp non è corretto
     */
    public static function creaStaff(int $idUtente, InsertNetWStaff $staff) : WStaff
    {
        // Creo l'hash relativo al codice.
        $hash = Hash::getSingleton()->hashPassword($staff->getCodiceAccesso());

        // Pulisco il campo codiceAccesso
        $staff->clear();

        $conn = parent::getConnection();

        try {
            // L'operazione è atomica.
            $conn->beginTransaction();

            // --OPERAZIONE DI INSERIMENTO DELLO STAFF--

            {
                $stmtInserimentoStaff = $conn->prepare("INSERT INTO staff (idCreatore, nome, hash) VALUES (:idCreatore, :nome, :hash)");
                $stmtInserimentoStaff->bindValue(":idCreatore", $idUtente, PDO::PARAM_INT);
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

            if ($ex->getCode() == Utente::VINCOLO_CODE){
                throw new InsertUpdateException("Hai già creato uno staff");
            }

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
     * @param int $idUtente
     * @param NetWStaffAccess $wrapper
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @throws InsertUpdateException si è già inscritti nello staff
     * @throws AuthorizationException codice errato
     * 
     * @return WStaff Staff a cui abbiamo avuto accesso.
     */
    public static function accediStaff(int $idUtente, NetWStaffAccess $staff) : WStaff
    {
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

                    if ($ex->getCode() == Utente::UNIQUE_CODE || $ex->getCode() == Utente::INTEGRITY_CODE){ // Codice di integrità.
                        throw new InsertUpdateException("Sei già membro dello staff.");
                    }

                    throw $ex;
                }

                // Pulisco il campo codiceAccesso
                $staff->clear();

                //Recupero i dati dello staff e li restituisco.

                $stmtSelezione = $conn->prepare("SELECT id, idCreatore, nome, timestampCreazione FROM staff WHERE id = :idStaff");
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
     * @return WStaff[] Lista degli staff
     */
    public static function getListaStaff() : array
    {
        // Recupero i dati degli staff.
        $conn = parent::getConnection();

        $stmtSelezione = $conn->prepare("SELECT id, idCreatore, nome, timestampCreazione FROM staff");
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
     * @param int $idUtente
     * 
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return WStaff[] Lista degli staff di cui si è membro
     */
    public static function getListaStaffMembri(int $idUtente) : array
    {
        // Recupero i dati degli staff.
        $conn = parent::getConnection();

        $stmtSelezione = $conn->prepare("SELECT id, idCreatore, nome, timestampCreazione FROM staff INNER JOIN membro ON membro.idStaff = staff.id WHERE membro.idUtente = :idUtente");
        $stmtSelezione->bindValue(":idUtente", $idUtente, PDO::PARAM_INT);
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
     * @param int $idUtente
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return WToken token di accesso
     */
    public static function renewToken(int $idUtente) : WToken
    {
        $newToken = Hash::getSingleton()->getToken();

        // Recupero i dati degli staff.
        $conn = parent::getConnection();

        //https://stackoverflow.com/questions/8070765/adding-12-hours-to-mysql-current-timestamp
        $stmtUpdate = $conn->prepare("UPDATE utente SET token = :token, scadenzaToken = (CURRENT_TIMESTAMP + INTERVAL :giorni DAY) WHERE id = :idUtente");
        $stmtUpdate->bindValue(":giorni", $GLOBALS["scadenzaTokenGiorni"], PDO::PARAM_INT);
        $stmtUpdate->bindValue(":token", $newToken, PDO::PARAM_STR);
        $stmtUpdate->bindValue(":idUtente", $idUtente, PDO::PARAM_INT);

        try {
            $stmtUpdate->execute();
        } catch (PDOException $ex) {

            // Mi assicuro di chiudere la connessione. Anche se teoricamente lo scope cancellerebbe comunque i riferimenti.
            $conn = NULL;

            if ($ex->getCode() == Utente::DATA_NON_VALIDA_CODE){
                throw new InsertUpdateException("Renew non valido: data scadenza non valida.");
            }

            if ($ex->getCode() == Utente::VINCOLO_CODE){
                throw new InsertUpdateException("Renew non valido: token non scaduto o inserimento non valido.");
            }

            throw $ex;
        }

        //Ricavo la scadenza.
        $stmtSelezione = $conn->prepare("SELECT scadenzaToken FROM utente WHERE id = :idUtente");
        $stmtSelezione->bindValue(":idUtente", $idUtente, PDO::PARAM_INT);
        $stmtSelezione->execute();

        //Non dovrebbe mai accadere che la scadenza a questo punto sia nulla:
        //Sia perché la query di sopra non lo prevede, sia perché ci sono trigger che lo impediscono.
        //Per sicurezza dobbiamo comunque andare avanti.
        $wrapper = NULL;

        if (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            if(!is_null($riga["scadenzaToken"]))
                $wrapper = \DateTimeImmutable::createFromFormat(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP, $riga["scadenzaToken"]);
        }

        $conn = NULL;

        return WToken::makeNoChecks($idUtente, $newToken, is_null($wrapper) ? NULL : new DateTimeImmutableAdapterJSON($wrapper));
    }

    /**
     * Restituisce il token di accesso se presente.
     *
     * @param int $utente
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * @return WToken|NULL token di accesso
     */
    public static function getToken(int $idUtente) : ?WToken
    {
        // Recupero i dati degli staff.
        $conn = parent::getConnection();

        $stmtSelezione = $conn->prepare("SELECT id, token, scadenzaToken FROM utente WHERE id = :idUtente");
        $stmtSelezione->bindValue(":idUtente", $idUtente, PDO::PARAM_INT);
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
     * @throws AuthorizationException token non valido
     * @return WUtente utente a cui si è avuto accesso.
     */
    public static function loginToken(NetWToken $token) : WUtente
    {
        // Prima devo recuperare tutti i dati dell'utente, la password dell'utente.

        $conn = parent::getConnection();

        //Check della scadenza qui.
        $stmtSelezione = $conn->prepare("SELECT id, nome, cognome, telefono, token, scadenzaToken FROM utente WHERE username = :username AND token = :token AND scadenzaToken > CURRENT_TIMESTAMP");
        $stmtSelezione->bindValue(":username", $token->getUsername(), PDO::PARAM_STR);
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
                // Pulisco i campi di login.
                $token->clear();

                return WUtente::of($riga);
            }
        }

        // Pulisco i campi di login.
        $token->clear();

        throw new AuthorizationException("Token non valido.");
    }
    
    /**
     * Restituisce lo staff dall'id.
     * Restitusice solo se l'utente fa parte dello staff.
     * 
     * @param int $idUtente
     * @param int $idStaff
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * 
     * @return WStaff staff richiesto
     */
    public static function getStaff(int $idUtente, int $idStaff) : ?WStaff {
        // Recupero i dati degli staff.
        $conn = parent::getConnection();
        
        $stmtSelezione = $conn->prepare("SELECT s.id, s.idCreatore, s.nome, s.timestampCreazione FROM staff AS s INNER JOIN membro AS m ON s.id = m.idStaff WHERE s.id = :idStaff AND m.idUtente = :idUtente");
        $stmtSelezione->bindValue(":idUtente", $idUtente, PDO::PARAM_INT);
        $stmtSelezione->bindValue(":idStaff", $idStaff, PDO::PARAM_INT);
        $stmtSelezione->execute();
        
        $result = NULL;
        
        if (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $result = WStaff::of($riga);
        }
        
        $conn = NULL;
        
        return $result;
    }

    /**
     * Restituisce l'ultimo cambio password dell'utente
     * 
     * @param int $idUtente
     * @throws PDOException problemi del database (errore di connessione, errore nel database)
     * 
     * @return DateTimeImmutableAdapterJSON data di ultimo cambio password
     */
    public static function getUltimoCambioPassword(int $idUtente) : DateTimeImmutableAdapterJSON {
        // Recupero i dati degli staff.
        $conn = parent::getConnection();
        
        $stmtSelezione = $conn->prepare("SELECT ultimoChangePwd FROM utente WHERE id = :idUtente");
        $stmtSelezione->bindValue(":idUtente", $idUtente, PDO::PARAM_INT);
        $stmtSelezione->execute();
        
        $result = NULL;
        
        if (($riga = $stmtSelezione->fetch(PDO::FETCH_ASSOC))) {
            $result = new DateTimeImmutableAdapterJSON(\DateTimeImmutable::createFromFormat(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP, $riga["ultimoChangePwd"]));
        }
        
        $conn = NULL;
        
        return $result;
    }    

    private function __construct()
    {}
}

