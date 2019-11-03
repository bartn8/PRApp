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

class WStatisticheEvento implements DatabaseWrapper
{

    /**
     * Converte un array di stringhe in un wrapper.
     *
     * @param array $array
     * @throws InvalidArgumentException i dati dell'array non sono validi oppure l'array stesso non è valido
     * @return WStatisticheEvento wrapper convertito
     */
    public static function of($array)
    {
        if (is_null($array) || ! is_array($array))
            throw new InvalidArgumentException("Array nullo o non valido.");
        
        if (! array_key_exists("idEvento", $array))
            throw new InvalidArgumentException("Dato idEvento non trovato.");
        
        if (! array_key_exists("idTipoPrevendita", $array))
            throw new InvalidArgumentException("Dato idTipoPrevendita non trovato.");
        
        if (! array_key_exists("nomeTipoPrevendita", $array))
            throw new InvalidArgumentException("Dato nomeTipoPrevendita non trovato.");

        if (! array_key_exists("prevenditeVendute", $array))
            throw new InvalidArgumentException("Dato prevenditeVendute non trovato.");
        
        if (! array_key_exists("ricavo", $array))
            throw new InvalidArgumentException("Dato ricavo non trovato.");
        
        if (! array_key_exists("prevenditeEntrate", $array))
            throw new InvalidArgumentException("Dato prevenditeEntrate non trovato.");

        if (! array_key_exists("prevenditeNonEntrate", $array))
            throw new InvalidArgumentException("Dato prevenditeNonEntrate non trovato.");

        return self::make((int) $array["idEvento"], (int) $array["idTipoPrevendita"], $array["nomeTipoPrevendita"], (int) $array["prevenditeVendute"], (float) $array["ricavo"], (int) $array["prevenditeEntrate"], (int) $array["prevenditeNonEntrate"]);
    }

    /**
     * Metodo factory con controlli.
     *
     * @param int $idEvento
     * @param int $idTipoPrevendita
     * @param string $nomeTipoPrevendita
     * @param int $prevenditeVendute
     * @param float $ricavo
     * @param int $prevenditeEntrate
     * @param int $prevenditeNonEntrate
     * @throws InvalidArgumentException
     * @return WStatisticheEvento
     */
    public static function make($idEvento, $idTipoPrevendita, $nomeTipoPrevendita, $prevenditeVendute, $ricavo, $prevenditeEntrate, $prevenditeNonEntrate)
    {
        if (is_null($idEvento) || is_null($idTipoPrevendita) || is_null($nomeTipoPrevendita) || is_null($prevenditeVendute) || is_null($ricavo) || is_null($prevenditeEntrate) || is_null($prevenditeNonEntrate))
            throw new InvalidArgumentException("Uno o più parametri nulli");
        
        if (! is_int($idEvento) || ! is_int($idTipoPrevendita) || !is_string($nomeTipoPrevendita) || ! is_int($prevenditeVendute) || ! is_float($ricavo) || ! is_int($prevenditeEntrate) || ! is_int($prevenditeNonEntrate))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");
        
        if ($idEvento <= 0)
            throw new InvalidArgumentException("ID Evento non valido");
        
        if ($idTipoPrevendita <= 0)
            throw new InvalidArgumentException("ID Tipo Prevendita non valido");
        
        if ($prevenditeVendute < 0)
            throw new InvalidArgumentException("Prevendite vendute non valido");
        
        if ($prevenditeEntrate < 0)
            throw new InvalidArgumentException("Prevendite entrate non valido");

        if ($prevenditeNonEntrate < 0)
            throw new InvalidArgumentException("Prevendite non entrate non valido");

        return new WStatisticheEvento($idEvento, $idTipoPrevendita, $nomeTipoPrevendita, $prevenditeVendute, $ricavo, $prevenditeEntrate, $prevenditeNonEntrate);
    }

    /**
     * Identificativo dell'evento.
     * (>0).
     *
     * @var int
     */
    private $idEvento;

    /**
     * Identificativo del tipo della prevendita.(>0).
     *
     * @var int
     */
    private $idTipoPrevendita;

    /**
     * Nome del tipo prevendita.
     *
     * @var string
     */
    private $nomeTipoPrevendita;

    /**
     * Numero di prevendite vendute.
     *
     * @var int
     */
    private $prevenditeVendute;

    /**
     * Ricavo ottenuto dalle prevendite vendute.
     *
     * @var float
     */
    private $ricavo;

    /**
     * Numero di prevendite entrate.
     *
     * @var int
     */
    private $prevenditeEntrate;

    /**
     * Numero di prevendite non entrate.
     *
     * @var int
     */
    private $prevenditeNonEntrate;

    protected function __construct($idEvento, $idTipoPrevendita, $nomeTipoPrevendita, $prevenditeVendute, $ricavo, $prevenditeEntrate, $prevenditeNonEntrate)
    {
        $this->idEvento = $idEvento;
        $this->idTipoPrevendita = $idTipoPrevendita;
        $this->nomeTipoPrevendita = $nomeTipoPrevendita;
        $this->prevenditeVendute = $prevenditeVendute;
        $this->ricavo = $ricavo;
        $this->prevenditeEntrate = $prevenditeEntrate;
        $this->prevenditeNonEntrate = $prevenditeNonEntrate;
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
    public function getIdEvento()
    {
        return $this->idEvento;
    }

    /**
     *
     * @return number
     */
    public function getIdTipoPrevendita()
    {
        return $this->idTipoPrevendita;
    }

    /**
     *
     * @return string
     */
    public function getNomeTipoPrevendita()
    {
        return $this->nomeTipoPrevendita;
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


    /**
     *
     * @return number
     */
    public function getPrevenditeEntrate()
    {
        return $this->prevenditeEntrate;
    }

    /**
     *
     * @return number
     */
    public function getPrevenditeNonEntrate()
    {
        return $this->prevenditeNonEntrate;
    }

}

