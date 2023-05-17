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
namespace com\model;

use Exception;
use DateTimeImmutable;
use InvalidArgumentException;
use com\model\db\wrapper\WUtente;
use com\model\session\UserSession;
use com\utils\DateTimeImmutableAdapterJSON;

/**
 * Gestisce il contesto di un utente.
 * Ha una factory interna per la creazione, restituzione e cancellazione del contesto.
 * Contiene un watchdog per l'aggiornamento durante la sessione.
 * Dopo ogni aggiornamento bisogna utilizzare il metodo apply per salvare le modifche
 *
 * @author Luca Bartolomei bartn8@hotmail.it
 */
class Context
{

    /**
     * Indica dopo quando far entrare in funzione il watchdog.
     */
    private static $watchdogThreshold = 5;

    public static function loadParameters(){
        if(isset($GLOBALS['watchdogThreshold']))
            Context::$watchdogThreshold = $GLOBALS['watchdogThreshold'];
    }

    /**
     * Crea un contesto.
     *
     * @throws Exception Lanciata se la sessione non é attiva
     * @throws InvalidArgumentException Lanciata se l'identificativo non é valido
     */
    public static function createContext()
    {
        if (session_status() != PHP_SESSION_ACTIVE)
            throw new Exception("Sessione non attiva");
        
        $context = new Context(NULL, TRUE, new DateTimeImmutableAdapterJSON(new DateTimeImmutable("now")));

        $_SESSION["context"] = $context;
    }

    /**
     * Restituisce il contesto corrente.
     *
     * @throws Exception Sessione non attiva
     * @return \com\model\Context Contesto
     */
    public static function getContext()
    {
        if (session_status() != PHP_SESSION_ACTIVE)
            throw new Exception("Sessione non attiva");
        
        if (array_key_exists("context", $_SESSION)) {
            $context = $_SESSION["context"];

            if($context instanceof Context){
                //Ogni volta che si richiede il contesto aggiorno il watchdog.
                $context->updateWatchdog();
                $context->apply();

                return $context;
            }
        }
        
        return new Context(NULL, FALSE, new DateTimeImmutableAdapterJSON(new DateTimeImmutable("now")));
    }

    /**
     * Elimina il contesto.
     * Se non presente fa un semplice reset.
     *
     * @throws Exception Sessione non attiva
     */
    public static function deleteContext()
    {
        if (session_status() != PHP_SESSION_ACTIVE)
            throw new Exception("Sessione non attiva");

        $context = Context::getContext();

        if($context->isValid()){
            $context->logout();
        }
        
        $_SESSION['context'] = NULL;
    }

    /**
     * Utente della sessione.
     *
     * @var UserSession
     */
    private $userSession;

    /**
     * Indica se il contesto è valido.
     *
     * @var bool
     */
    private $valid;

    /**
     * Se superato, la sessione va aggiornata.
     * 
     * @var int
     */
    private $watchdogCounter;

    /**
     * Data di avvio della sessione
     * @var DateTimeImmutableAdapterJSON
     */
    private $dataSessione;

    /**
     * Genera un contesto.
     * Utilizzato solo da factory.
     *
     * @param UserSession $userSession
     * @param bool $valid
     */
    private function __construct($userSession, $valid, $dataSessione)
    {
        $this->userSession = $userSession;
        $this->valid = $valid;
        $this->dataSessione = $dataSessione;
    }

    /**
     * Restituisce la sessione dell'utente.
     *
     * @return UserSession
     */
    public function getUserSession()
    {
        return $this->userSession;
    }

    /**
     * Restituisce vero se il contesto è utilizzabile.
     *
     * @return bool
     */
    public function isValid()
    {
        return $this->valid;
    }

    /**
     * Effettua il login.
     */
    public function login($utente){
        if (! ($utente instanceof WUtente))
            throw new InvalidArgumentException("utente non valido.");

        $this->userSession = new UserSession($utente);
    }

    /**
     * Effettua il logout.
     */
    public function logout(){
        $this->userSession = NULL;
    }

    /**
     * Restituisce vero se l'utente è loggato
     */
    public function isLogged() : bool {
        return !is_null($this->userSession);
    }

    /**
     * Indica se il watchdog è stato attivato.
     * 
     * @return bool
     */
    public function isWatchdogTriggered(){
        return $this->watchdogCounter >= Context::$watchdogThreshold;
    }

    /**
     * Aggiorna il watchdog
     */
    public function updateWatchdog(){
        $this->watchdogCounter++;
    }

    /**
     * Resetta il watchdog.
     */
    public function resetWatchdog(){
        $this->watchdogCounter = 0;
    }

    /**
     * Utilizzato quando si vuole salvare le modifiche al contesto.
     */
    public function apply(){
        if (session_status() != PHP_SESSION_ACTIVE)
            throw new Exception("Sessione non attiva");

        if($this->isValid()){
            $_SESSION["context"] = $this;
        }else{
            throw new Exception("Contesto non valido");
        }
            
    }

    /**
     * Restituisce la data di avvio della sessione
     * 
     * @return DateTimeImmutableAdapterJSON
     */
    public function getDataSessione(){
        return $this->dataSessione;
    }

}

//Caricamento statico dei parametri.
Context::loadParameters();