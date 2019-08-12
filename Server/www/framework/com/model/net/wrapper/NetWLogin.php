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

namespace com\model\net\wrapper;

use com\model\db\wrapper\WUtente;
use InvalidArgumentException;

class NetWLogin implements NetWrapper
{

    // public const PASSWORD_MIN = 4;
    public static function of($array)
    {
        if (is_null($array) || ! is_array($array))
            throw new InvalidArgumentException("Array nullo o non valido.");
        
        if (! array_key_exists("username", $array))
            throw new InvalidArgumentException("Dato username non trovato.");
        
        if (! array_key_exists("password", $array))
            throw new InvalidArgumentException("Dato password non trovato.");
        
        return self::make($array["username"], $array["password"]);
    }

    private static function make(string $username, string $password): NetWLogin
    {
        if (is_null($username) || is_null($password))
            throw new InvalidArgumentException("Uno o più parametri nulli");
        
        if (! is_string($username) || ! is_string($password))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");
        
        if (strlen($username) > WUtente::USERNAME_MAX)
            throw new InvalidArgumentException("Username non valido (MAX)");
        
        // if(strlen($password) < self::PASSWORD_MIN)
        // throw new InvalidArgumentException("Password non valida (MIN)");
        
        return new NetWLogin($username, $password);
    }

    /**
     * Username per il login.
     *
     * @var string
     */
    private $username;

    /**
     * Password per il login.
     *
     * @var string
     */
    private $password;

    private function __construct($username, $password)
    {
        $this->username = $username;
        $this->password = $password;
    }

    /**
     *
     * @return string
     */
    public function getUsername()
    {
        return $this->username;
    }

    /**
     *
     * @return string
     */
    public function getPassword()
    {
        return $this->password;
    }

    /**
     * Pulisce i campi.
     */
    public function clear()
    {
        unset($this->username);
        unset($this->password);
    }
}

