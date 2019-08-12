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

class WStatistichePRTotali implements DatabaseWrapper
{

    /**
     * Converte un array di stringhe in un wrapper.
     *
     * @param array $array
     * @throws InvalidArgumentException i dati dell'array non sono validi oppure l'array stesso non è valido
     * @return WStatistichePRTotali wrapper convertito
     */
    public static function of($array)
    {
        if (is_null($array) || ! is_array($array))
            throw new InvalidArgumentException("Array nullo o non valido.");
        
        if (! array_key_exists("idUtente", $array))
            throw new InvalidArgumentException("Dato idUtente non trovato.");
        
        if (! array_key_exists("prevenditeVendute", $array))
            throw new InvalidArgumentException("Dato prevenditeVendute non trovato.");
        
        if (! array_key_exists("ricavo", $array))
            throw new InvalidArgumentException("Dato ricavo non trovato.");
        
        return self::make((int) $array["idUtente"], (int) $array["prevenditeVendute"], (float) $array["ricavo"]);
    }

    /**
     * Metodo factory con controlli.
     *
     * @param int $idUtente
     * @param int $prevenditeVendute
     * @param float $ricavo
     * @throws InvalidArgumentException
     * @return \com\model\db\wrapper\WStatistichePRTotali
     */
    public static function make($idUtente, $prevenditeVendute, $ricavo)
    {
        if (is_null($idUtente) || is_null($prevenditeVendute) || is_null($ricavo))
            throw new InvalidArgumentException("Uno o più parametri nulli");
        
        if (! is_int($idUtente) || ! is_int($prevenditeVendute) || ! is_float($ricavo))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");
        
        if ($idUtente <= 0)
            throw new InvalidArgumentException("ID Utente non valido");
        
        if ($prevenditeVendute < 0)
            throw new InvalidArgumentException("Prevendite vendute non valide");
        
        if ($ricavo < 0.0)
            throw new InvalidArgumentException("Ricavo non valido");
        
        return new WStatistichePRTotali($idUtente, $prevenditeVendute, $ricavo);
    }

    /**
     * Identificativo dell'utente.
     * (>0).
     *
     * @var int
     */
    private $idUtente;

    /**
     * Quantità di prevendite del tipo vendute.
     * (>=0).
     *
     * @var int
     */
    private $prevenditeVendute;

    /**
     * Ricavo ottenuto dalla vendita delle prevendite.
     *
     * @var float
     */
    private $ricavo;

    protected function __construct($idUtente, $prevenditeVendute, $ricavo)
    {
        $this->idUtente = $idUtente;
        $this->prevenditeVendute = $prevenditeVendute;
        $this->ricavo = $ricavo;
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
    public function getPrevenditeVendute()
    {
        return $this->prevenditeVendute;
    }

    /**
     *
     * @return number
     */
    public function getRicavo()
    {
        return $this->ricavo;
    }
}

