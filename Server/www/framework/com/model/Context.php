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
use InvalidArgumentException;
use com\model\db\wrapper\WUtente;
use com\model\session\UserSession;

/**
 * Gestisce il contesto di un utente.
 * L'oggetto restituito è immutabile.
 * Ha una factory interna per la creazione, restituzione e cancellazione del contesto.
 *
 * @author Luca Bartolomei bartn8@hotmail.it
 */
class Context
{

    /**
     * Crea un contesto.
     *
     * @param WUtente $utente utente
     * @throws Exception Lanciata se la sessione non é attiva
     * @throws InvalidArgumentException Lanciata se l'identificativo non é valido
     */
    public static function createContext($utente)
    {
        if (session_status() != PHP_SESSION_ACTIVE)
            throw new Exception("Sessione non attiva");
        
        if (! ($utente instanceof WUtente))
            throw new InvalidArgumentException("utente non valido.");
        
        $_SESSION["valid"] = TRUE;
        $_SESSION["userSession"] = new UserSession($utente);
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
        
        if (array_key_exists("valid", $_SESSION) && array_key_exists("userSession", $_SESSION)) {
            return new Context($_SESSION["userSession"], $_SESSION["valid"]);
        }
        
        return new Context(NULL, FALSE);
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
        
        $_SESSION["valid"] = FALSE;
        $_SESSION["userSession"] = NULL;
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
     * @var boolean
     */
    private $valid;

    /**
     * Genera un contesto.
     * Utilizzato solo da factory.
     *
     * @param int $userID
     * @param boolean $valid
     */
    private function __construct($utente, $valid)
    {
        $this->utente = $utente;
        $this->valid = $valid;
    }

    /**
     * Restituisce la sessione dell'utente.
     *
     * @return UserSession
     */
    function getUserSession()
    {
        return $this->userSession;
    }

    /**
     * Restituisce vero se il contesto è utilizzabile.
     *
     * @return boolean
     */
    function isValid()
    {
        return $this->valid;
    }
}