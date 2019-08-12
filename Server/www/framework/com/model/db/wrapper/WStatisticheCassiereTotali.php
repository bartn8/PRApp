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

class WStatisticheCassiereTotali implements DatabaseWrapper
{

    /**
     * Converte un array di stringhe in un wrapper.
     *
     * @param array $array
     * @throws InvalidArgumentException i dati dell'array non sono validi oppure l'array stesso non è valido
     * @return WStatisticheCassiereTotali wrapper convertito
     */
    public static function of($array)
    {
        if (is_null($array) || ! is_array($array))
            throw new InvalidArgumentException("Array nullo o non valido.");
        
        if (! array_key_exists("idUtente", $array))
            throw new InvalidArgumentException("Dato idUtente non trovato.");
        
        if (! array_key_exists("entrate", $array))
            throw new InvalidArgumentException("Dato entrate non trovato.");
        
        return self::make((int) $array["idUtente"], (int) $array["entrate"]);
    }

    /**
     * Metodo factory con controlli.
     *
     * @param int $idUtente
     * @param int $idStaff
     * @param int $idEvento
     * @param int $entrate
     * @throws InvalidArgumentException
     * @return \com\model\db\wrapper\WStatisticheCassiereEvento
     */
    public static function make($idUtente, $entrate)
    {
        if (is_null($idUtente) || is_null($entrate))
            throw new InvalidArgumentException("Uno o più parametri nulli");
        
        if (! is_int($idUtente) || ! is_int($entrate))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");
        
        if ($idUtente <= 0)
            throw new InvalidArgumentException("ID Utente non valido");
        
        if ($entrate < 0)
            throw new InvalidArgumentException("Entrate non valide");
        
        return new WStatisticheCassiereTotali($idUtente, $entrate);
    }

    /**
     * Identificativo dell'utente.
     * (>0).
     *
     * @var int
     */
    private $idUtente;

    /**
     * Entrate effettuate dal cassiere in totale.
     * (>=0).
     *
     * @var int
     */
    private $entrate;

    protected function __construct($idUtente, $entrate)
    {
        $this->idUtente = $idUtente;
        $this->entrate = $entrate;
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
    public function getIdUtente()
    {
        return $this->idUtente;
    }

    /**
     *
     * @return number
     */
    public function getEntrate()
    {
        return $this->entrate;
    }
}
