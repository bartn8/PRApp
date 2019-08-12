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

namespace com\model\db\wrapper;

use InvalidArgumentException;
use ReflectionClass;
use com\utils\DateTimeImmutableAdapterJSON;
use com\model\net\serialize\ArrayDeserializable;

//TODO: campo id serve?

class WToken implements DatabaseWrapper
{

    /**
     * Metodo factory con controlli.
     *
     * @param int $id
     * @param string $token
     * @throws InvalidArgumentException
     * @return \com\model\db\wrapper\WToken
     */
    public static function make($id, $token, $scadenzaToken)
    {
        if (is_null($id))
            throw new InvalidArgumentException("Uno o più parametri nulli");

        if (! is_int($id) || (!is_null($token) && ! is_string($token)) || (!is_null($scadenzaToken) && !($scadenzaToken instanceof DateTimeImmutableAdapterJSON)))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");

        if ($id <= 0)
            throw new InvalidArgumentException("ID non valido");

        return new WToken($id, $token, $scadenzaToken);
    }

    public static function makeNoChecks($id, $token, $scadenzaToken)
    {
        return new WToken($id, $token, $scadenzaToken);
    }

    /**
     * Converte un array di stringhe in un wrapper.
     *
     * @param array $array
     * @throws InvalidArgumentException i dati dell'array non sono validi oppure l'array stesso non è valido
     * @return WUtente wrapper convertito
     */
    public static function of($array)
    {
        if (is_null($array) || ! is_array($array))
            throw new InvalidArgumentException("Array nullo o non valido.");

        if (! array_key_exists("id", $array))
            throw new InvalidArgumentException("Dato id non trovato.");

        if (! array_key_exists("token", $array))
            $array["token"] = null;
            //throw new InvalidArgumentException("Dato token non trovato.");

        if (! array_key_exists("scadenzaToken", $array))
            $array["scadenzaToken"] = null;
            //throw new InvalidArgumentException("Dato scadenzaToken non trovato.");

        $scadenzaToken = new DateTimeImmutableAdapterJSON(\DateTimeImmutable::createFromFormat(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP, $array["scadenzaToken"]));    //MYSQL TIMESTAMP

        return self::make((int) $array["id"], $array["token"], $scadenzaToken);
    }

    /**
     * Identificativo dell'utente.
     * (>0).
     *
     * @var int
     */
    private $id;

    /**
     * Token di accesso.
     *
     * @var string | NULL
     */
    private $token;

    /**
     * Scadenza del token.
     * 
     * @var DateTimeImmutableAdapterJSON | NULL
     */
    private $scadenzaToken;

    protected function __construct($id, $token, $scadenzaToken)
    {
        $this->id = $id;
        $this->token = $token;
        $this->scadenzaToken = $scadenzaToken;
    }

    /**
     * Serve per serializzare il wrapper in JSON.
     */
    public function jsonSerialize()
    {
        $thisClass = new ReflectionClass(get_class());
        $props = $thisClass->getProperties();
        $array = array();

        foreach ($props as $prop) {
            $array[$prop->getName()] = $this->{$prop->getName()};
        }

        return $array;
    }

    /**
     *
     * @return number
     */
    public function getId()
    {
        return $this->id;
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
     *
     * @return DateTimeImmutableAdapterJSON
     */
    public function getScadenzaToken()
    {
        return $this->scadenzaToken;
    }

}

