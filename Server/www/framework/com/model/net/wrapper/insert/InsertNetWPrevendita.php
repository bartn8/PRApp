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

namespace com\model\net\wrapper\insert;

use com\model\db\enum\StatoPrevendita;
use com\model\db\wrapper\WPrevendita;
use InvalidArgumentException;
use com\model\net\serialize\ArrayDeserializable;
use com\model\net\wrapper\NetWrapper;

class InsertNetWPrevendita implements NetWrapper
{

    /**
     * Meotodo factory per l'inserimento.
     *
     * @param int $idEvento
     * @param int $idPR
     * @param int $idCliente
     * @param int $idTipoPrevendita
     * @param string $codice
     * @param StatoPrevendita $stato
     * @throws InvalidArgumentException
     * @return insertWPrevendita
     */
    private static function make($idEvento, $idCliente, $idTipoPrevendita, $codice, $stato)
    {
        if (is_null($idCliente) || is_null($idEvento) || is_null($idTipoPrevendita) || is_null($codice) || is_null($stato))
            throw new InvalidArgumentException("Uno o più parametri nulli");

        if (! is_int($idCliente) || ! is_int($idEvento) || ! is_int($idTipoPrevendita) || ! ($stato instanceof StatoPrevendita) || ! is_string($codice))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");

        if ($idCliente <= 0)
            throw new InvalidArgumentException("ID Cliente non valido");

        if (strlen($codice) > WPrevendita::CODICE_MAX)
            throw new InvalidArgumentException("Codice non valido (MAX)");

        if ($idEvento <= 0)
            throw new InvalidArgumentException("ID Evento non valido");

        if ($idTipoPrevendita <= 0)
            throw new InvalidArgumentException("ID Tipo Prevendita non valido");

        return new InsertNetWPrevendita($idEvento, $idCliente, $idTipoPrevendita, $codice, $stato);
    }

    public static function of($array)
    {
        if (is_null($array) || ! is_array($array))
            throw new InvalidArgumentException("Array nullo o non valido.");

        if (! array_key_exists("idEvento", $array))
            throw new InvalidArgumentException("Dato idEvento non trovato.");

        if (! array_key_exists("idCliente", $array))
            throw new InvalidArgumentException("Dato idCliente non trovato.");

        if (! array_key_exists("idTipoPrevendita", $array))
            throw new InvalidArgumentException("Dato idTipoPrevendita non trovato.");

        if (! array_key_exists("codice", $array))
            throw new InvalidArgumentException("Dato codice non trovato.");

        if (! array_key_exists("stato", $array))
            throw new InvalidArgumentException("Dato stato non trovato.");

        return self::make((int) $array["idEvento"], (int) $array["idCliente"], (int) $array["idTipoPrevendita"], $array["codice"], StatoPrevendita::of($array["stato"]));
    }
    
    /**
     * Identificativo dell'evento.
     * (>0).
     *
     * @var int
     */
    private $idEvento;
        
    /**
     * Identificativo del cliente che ha comprato la prevendita.
     * (>0). (OZPTIONALE)
     *
     * @var int|NULL
     */
    private $idCliente;
    
    /**
     * Identificativo del tipo della prevendita.(>0).
     *
     * @var int
     */
    private $idTipoPrevendita;
    
    /**
     * Codice di verifica della prevendita.
     *
     * @var string
     */
    private $codice;
    
    /**
     * Rappresenta lo stato della prevendita.
     *
     * @var StatoPrevendita
     */
    private $stato;
    
    
    private function __construct($idEvento, $idCliente, $idTipoPrevendita, $codice, $stato)
    {
        $this->idEvento = $idEvento;
        $this->idCliente = $idCliente;
        $this->idTipoPrevendita = $idTipoPrevendita;
        $this->codice = $codice;
        $this->stato = $stato;
    }
    /**
     * @return number
     */
    public function getIdEvento()
    {
        return $this->idEvento;
    }

    /**
     * @return Ambigous <number, NULL>
     */
    public function getIdCliente()
    {
        return $this->idCliente;
    }

    /**
     * @return number
     */
    public function getIdTipoPrevendita()
    {
        return $this->idTipoPrevendita;
    }

    /**
     * @return string
     */
    public function getCodice()
    {
        return $this->codice;
    }

    /**
     * @return \com\model\db\enum\StatoPrevendita
     */
    public function getStato()
    {
        return $this->stato;
    }

    
    
    
}

