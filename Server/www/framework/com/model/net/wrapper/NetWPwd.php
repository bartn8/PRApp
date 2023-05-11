<?php

/*
 * PRApp  Copyright (C) 2023  Luca Bartolomei
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

class NetWPwd implements NetWrapper
{

    // public const PASSWORD_MIN = 4;
    public static function of($array)
    {
        if (is_null($array) || ! is_array($array))
            throw new InvalidArgumentException("Array nullo o non valido.");
        
        if (! array_key_exists("oldpwd", $array))
            throw new InvalidArgumentException("Dato vecchia password non trovato.");
        
        if (! array_key_exists("newpwd", $array))
            throw new InvalidArgumentException("Dato nuova password non trovato.");
        
        return self::make($array["oldpwd"], $array["newpwd"]);
    }

    private static function make(string $oldpwd, string $newpwd): NetWPwd
    {
        if (is_null($oldpwd) || is_null($newpwd))
            throw new InvalidArgumentException("Uno o più parametri nulli");
        
        if (! is_string($oldpwd) || ! is_string($newpwd))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");
        
        // if(strlen($password) < self::PASSWORD_MIN)
        // throw new InvalidArgumentException("Password non valida (MIN)");
        
        return new NetWPwd($oldpwd, $newpwd);
    }

    /**
     * Vecchia password per il login.
     *
     * @var string
     */
    private $oldpwd;

    /**
     * Nuova password per il login.
     *
     * @var string
     */
    private $newpwd;

    private function __construct($oldpwd, $newpwd)
    {
        $this->oldpwd = $oldpwd;
        $this->newpwd = $newpwd;
    }

    /**
     *
     * @return string
     */
    public function getOldpwd()
    {
        return $this->oldpwd;
    }

    /**
     *
     * @return string
     */
    public function getNewpwd()
    {
        return $this->newpwd;
    }

    /**
     * Pulisce i campi.
     */
    public function clear()
    {
        unset($this->oldpwd);
        unset($this->newpwd);
    }
}

