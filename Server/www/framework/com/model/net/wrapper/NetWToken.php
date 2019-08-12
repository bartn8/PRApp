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

use InvalidArgumentException;

class NetWToken implements NetWrapper
{
    public static function of($array)
    {
        if (is_null($array) || ! is_array($array))
            throw new InvalidArgumentException("Array nullo o non valido.");
        
        if (! array_key_exists("token", $array))
            throw new InvalidArgumentException("Dato token non trovato.");
                
        return self::make($array["token"]);
    }

    private static function make(string $token): NetWToken
    {
        if (is_null($token))
            throw new InvalidArgumentException("Uno o più parametri nulli");
        
        if (! is_string($token))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");
                
        return new NetWToken($token);
    }

    /**
     * Token per il login.
     *
     * @var string
     */
    private $token;


    private function __construct($token)
    {
        $this->token = $token;
    }

    /**
     *
     * @return string
     */
    public function getToken()
    {
        return $this->token;
    }

    /**
     * Pulisce i campi.
     */
    public function clear()
    {
        unset($this->token);
    }
}

