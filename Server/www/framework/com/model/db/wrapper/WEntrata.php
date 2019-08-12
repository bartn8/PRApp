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
use com\model\net\serialize\ArrayDeserializable;
use com\utils\DateTimeImmutableAdapterJSON;

class WEntrata implements DatabaseWrapper
{

    /**
     * Converte un array di stringhe in un wrapper.
     *
     * @param array $array
     * @throws InvalidArgumentException i dati dell'array non sono validi oppure l'array stesso non è valido
     * @return WEntrata wrapper convertito
     */
    public static function of($array)
    {
        if (is_null($array) || ! is_array($array))
            throw new InvalidArgumentException("Array nullo o non valido.");

        if (! array_key_exists("idCassiere", $array))
            throw new InvalidArgumentException("Dato idCassiere non trovato.");

        if (! array_key_exists("idPrevendita", $array))
            throw new InvalidArgumentException("Dato idPrevendita non trovato.");

        if (! array_key_exists("timestampEntrata", $array))
            throw new InvalidArgumentException("Dato timestampEntrata non trovato.");

        return self::make((int) $array["idCassiere"], (int) $array["idPrevendita"], new DateTimeImmutableAdapterJSON(\DateTimeImmutable::createFromFormat(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP, $array["timestampEntrata"])));
    }

    /**
     * Metodo factory con controlli.
     *
     * @param int $idCassiere
     * @param int $idPrevendita
     * @param DateTimeImmutableAdapterJSON $dateTimeEntrata
     * @return \com\model\db\wrapper\WEntrata
     */
    public static function make($idCassiere, $idPrevendita, $timestampEntrata)
    {
        if (is_null($idCassiere) || is_null($idPrevendita) || is_null($timestampEntrata))
            throw new InvalidArgumentException("Uno o più parametri nulli");

        if (! is_int($idCassiere) || ! is_int($idPrevendita) || ! ($timestampEntrata instanceof DateTimeImmutableAdapterJSON))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");

        if ($idCassiere <= 0)
            throw new InvalidArgumentException("ID Cassiere non valido");

        if ($idPrevendita <= 0)
            throw new InvalidArgumentException("ID Prevendita non valido");

        return new WEntrata($idCassiere, $idPrevendita, $timestampEntrata);
    }

    //TODO: da sparare agli altri wrapper.
    public static function makeNoChecks($idCassiere, $idPrevendita, $timestampEntrata)
    {
        return new WEntrata($idCassiere, $idPrevendita, $timestampEntrata);
    }

    /**
     * Identificativo del cassiere che ha timbrato.
     * (>0).
     *
     * @var int
     */
    private $idCassiere;

    /**
     * Identificativo della prevendita timbrata.
     * (>0).
     *
     * @var int
     */
    private $idPrevendita;

    /**
     * Data e tempo di timbraggio.
     *
     * @var DateTimeImmutableAdapterJSON
     */
    private $timestampEntrata;

    protected function __construct($idCassiere, $idPrevendita, $timestampEntrata)
    {
        $this->idCassiere = $idCassiere;
        $this->idPrevendita = $idPrevendita;
        $this->timestampEntrata = $timestampEntrata;
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
    public function getIdCassiere()
    {
        return $this->idCassiere;
    }

    /**
     *
     * @return number
     */
    public function getIdPrevendita()
    {
        return $this->idPrevendita;
    }

    /**
     *
     * @return DateTimeImmutableAdapterJSON
     */
    public function getTimestampEntrata()
    {
        return $this->timestampEntrata;
    }
}

