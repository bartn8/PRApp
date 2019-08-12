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

class WStatistichePREvento implements DatabaseWrapper
{

    /**
     * Converte un array di stringhe in un wrapper.
     *
     * @param array $array
     * @throws InvalidArgumentException i dati dell'array non sono validi oppure l'array stesso non è valido
     * @return WStatistichePREvento wrapper convertito
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
        
        if (! array_key_exists("idTipoPrevendita", $array))
            throw new InvalidArgumentException("Dato idTipoPrevendita non trovato.");
        
        if (! array_key_exists("nomeTipoPrevendita", $array))
            throw new InvalidArgumentException("Dato nomeTipoPrevendita non trovato.");

        if (! array_key_exists("prevenditeVendute", $array))
            throw new InvalidArgumentException("Dato prevenditeVendute non trovato.");
        
        if (! array_key_exists("ricavo", $array))
            throw new InvalidArgumentException("Dato ricavo non trovato.");
        
        return self::make((int) $array["idUtente"], (int) $array["idStaff"], (int) $array["idEvento"], (int) $array["idTipoPrevendita"], $array["nomeTipoPrevendita"],  (int) $array["prevenditeVendute"], (float) $array["ricavo"]);
    }

    /**
     * Metodo factory con controlli.
     *
     * @param int $idUtente
     * @param int $idStaff
     * @param int $idEvento
     * @param int $idTipoPrevendita
     * @param string $nomeTipoPrevendita
     * @param int $prevenditeVendute
     * @param float $ricavo
     * @throws InvalidArgumentException
     * @return \com\model\db\wrapper\WStatistichePREvento
     */
    public static function make($idUtente, $idStaff, $idEvento, $idTipoPrevendita, $nomeTipoPrevendita, $prevenditeVendute, $ricavo)
    {
        if (is_null($idUtente) || is_null($idStaff) || is_null($idEvento) || is_null($idTipoPrevendita) || is_null($nomeTipoPrevendita) || is_null($prevenditeVendute) || is_null($ricavo))
            throw new InvalidArgumentException("Uno o più parametri nulli");
        
        if (! is_int($idUtente) || ! is_int($idStaff) || ! is_int($idEvento) || ! is_int($idTipoPrevendita) || !is_string($nomeTipoPrevendita) || ! is_int($prevenditeVendute) || ! is_float($ricavo))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");
        
        if ($idUtente <= 0)
            throw new InvalidArgumentException("ID Utente non valido");
        
        if ($idStaff <= 0)
            throw new InvalidArgumentException("ID Staff non valido");
        
        if ($idEvento <= 0)
            throw new InvalidArgumentException("ID Evento non valido");
        
        if ($idTipoPrevendita <= 0)
            throw new InvalidArgumentException("ID Tipo Prevendita non valido");
        
        if ($prevenditeVendute < 0)
            throw new InvalidArgumentException("Prevendite vendute non valide");
        
        if ($ricavo < 0.0)
            throw new InvalidArgumentException("Ricavo non valido");
        
        return new WStatistichePREvento($idUtente, $idStaff, $idEvento, $idTipoPrevendita, $nomeTipoPrevendita, $prevenditeVendute, $ricavo);
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
     * Identificativo del tipo di prevendita venduta.
     * (>0).
     *
     * @var int
     */
    private $idTipoPrevendita;

    /**
     * Nome del tipo di prevendita venduta.
     *
     * @var string
     */
    private $nomeTipoPrevendita;

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

    protected function __construct($idUtente, $idStaff, $idEvento, $idTipoPrevendita, $nomeTipoPrevendita, $prevenditeVendute, $ricavo)
    {
        $this->idUtente = $idUtente;
        $this->idStaff = $idStaff;
        $this->idEvento = $idEvento;
        $this->idTipoPrevendita = $idTipoPrevendita;
        $this->nomeTipoPrevendita = $nomeTipoPrevendita;
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
}

