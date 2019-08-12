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

class WStatisticheCassiereEvento implements DatabaseWrapper
{

    /**
     * Converte un array di stringhe in un wrapper.
     *
     * @param array $array
     * @throws InvalidArgumentException i dati dell'array non sono validi oppure l'array stesso non è valido
     * @return WStatisticheCassiereEvento wrapper convertito
     */
    public static function of($array)
    {
        if (is_null($array) || ! is_array($array))
            throw new InvalidArgumentException("Array nullo o non valido.");
        
        if (! array_key_exists("idUtente", $array))
            throw new InvalidArgumentException("Dato idUtente non trovato.");
        
        if (! array_key_exists("idStaff", $array))
            throw new InvalidArgumentException("Dato idStaff non trovato.");
        
        if (! array_key_exists("idEvento", $array))
            throw new InvalidArgumentException("Dato idEvento non trovato.");
        
        if (! array_key_exists("entrate", $array))
            throw new InvalidArgumentException("Dato entrate non trovato.");
        
        return self::make((int) $array["idUtente"], (int) $array["idStaff"], (int) $array["idEvento"], (int) $array["entrate"]);
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
    public static function make($idUtente, $idStaff, $idEvento, $entrate)
    {
        if (is_null($idUtente) || is_null($idStaff) || is_null($idEvento) || is_null($entrate))
            throw new InvalidArgumentException("Uno o più parametri nulli");
        
        if (! is_int($idUtente) || ! is_int($idStaff) || ! is_int($idEvento) || ! is_int($entrate))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");
        
        if ($idUtente <= 0)
            throw new InvalidArgumentException("ID Utente non valido");
        
        if ($idStaff <= 0)
            throw new InvalidArgumentException("ID Staff non valido");
        
        if ($idEvento <= 0)
            throw new InvalidArgumentException("ID Evento non valido");
        
        if ($entrate < 0)
            throw new InvalidArgumentException("Entrate non valide");
        
        return new WStatisticheCassiereEvento($idUtente, $idStaff, $idEvento, $entrate);
    }

    /**
     * Identificativo dell'utente.
     * (>0).
     *
     * @var int
     */
    private $idUtente;

    /**
     * Identificativo dello staff.
     * (>0).
     *
     * @var int
     */
    private $idStaff;

    /**
     * Identificativo dell'evento.
     * (>0).
     *
     * @var int
     */
    private $idEvento;

    /**
     * Entrate effettuate dal cassiere nell'evento.
     * (>=0).
     *
     * @var int
     */
    private $entrate;

    protected function __construct($idUtente, $idStaff, $idEvento, $entrate)
    {
        $this->idUtente = $idUtente;
        $this->idStaff = $idStaff;
        $this->idEvento = $idEvento;
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
    public function getIdStaff()
    {
        return $this->idStaff;
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
    public function getEntrate()
    {
        return $this->entrate;
    }
}


