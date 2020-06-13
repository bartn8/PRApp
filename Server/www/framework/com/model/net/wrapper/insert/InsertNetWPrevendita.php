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

use DateTimeImmutable;
use InvalidArgumentException;
use com\model\db\wrapper\WPrevendita;
use com\model\net\wrapper\NetWrapper;
use com\model\db\enum\StatoPrevendita;
use com\utils\DateTimeImmutableAdapterJSON;
use com\model\net\serialize\ArrayDeserializable;

class InsertNetWPrevendita implements NetWrapper
{

    /**
     * Meotodo factory per l'inserimento.
     *
     * @param string $nomeCliente
     * @param string $cognomeCliente
     * @param int $idTipoPrevendita
     * @param string $codice
     * @param StatoPrevendita $stato
     * @throws InvalidArgumentException
     * @return insertWPrevendita
     */
    private static function make($nomeCliente, $cognomeCliente, $idTipoPrevendita, $codice, $stato)
    {
        if (is_null($nomeCliente) || is_null($cognomeCliente) || is_null($idTipoPrevendita) || is_null($codice) || is_null($stato))
            throw new InvalidArgumentException("Uno o più parametri nulli");

        if (! is_int($idTipoPrevendita) || ! is_string($nomeCliente) || ! is_string($cognomeCliente) ||  ! ($stato instanceof StatoPrevendita) || ! is_string($codice))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");

        if (strlen($codice) > WPrevendita::CODICE_MAX_LENGTH)
            throw new InvalidArgumentException("Codice non valido (MAX)");

        if (strlen($nomeCliente) > WPrevendita::NOME_MAX_LENGTH)
            throw new InvalidArgumentException("Nome cliente non valido (MAX)");            

        if (strlen($cognomeCliente) > WPrevendita::COGNOME_MAX_LENGTH)
            throw new InvalidArgumentException("Cognome cliente non valido (MAX)");               

        if ($idTipoPrevendita <= 0)
            throw new InvalidArgumentException("ID Tipo Prevendita non valido");

        return new InsertNetWPrevendita($nomeCliente, $cognomeCliente, $idTipoPrevendita, $codice, $stato);
    }

    public static function of($array)
    {
        if (is_null($array) || ! is_array($array))
            throw new InvalidArgumentException("Array nullo o non valido.");

        if (! array_key_exists("nomeCliente", $array))
            throw new InvalidArgumentException("Dato nomeCliente non trovato.");

        if (! array_key_exists("cognomeCliente", $array))
            throw new InvalidArgumentException("Dato cognomeCliente non trovato.");   

        if (! array_key_exists("idTipoPrevendita", $array))
            throw new InvalidArgumentException("Dato idTipoPrevendita non trovato.");

        if (! array_key_exists("codice", $array))
            throw new InvalidArgumentException("Dato codice non trovato.");

        if (! array_key_exists("stato", $array))
            throw new InvalidArgumentException("Dato stato non trovato.");

        return self::make($array["nomeCliente"], $array["cognomeCliente"], (int) $array["idTipoPrevendita"], $array["codice"], StatoPrevendita::of($array["stato"]));
    }
    
    //Prima c'era idEvento: ora sostituito da evento selezionato.
    //Prima c'era idPR: ora sostituito da membro che ha inserito la prevendita
        
    //Tabella cliente integrata.
    
    /**
     * Nome del cliente
     * @var string
     */
    private $nomeCliente;

    /**
     * Cognome del cliente
     * @var string
     */
    private $cognomeCliente;

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
    
    
    private function __construct($nomeCliente, $cognomeCliente, $idTipoPrevendita, $codice, $stato)
    {
        $this->nomeCliente = $nomeCliente;
        $this->cognomeCliente = $cognomeCliente;
        $this->idTipoPrevendita = $idTipoPrevendita;
        $this->codice = $codice;
        $this->stato = $stato;
    }

    /**
     * Get nome del cliente
     *
     * @return  string
     */ 
    public function getNomeCliente()
    {
        return $this->nomeCliente;
    }

    /**
     * Get cognome del cliente
     *
     * @return  string
     */ 
    public function getCognomeCliente()
    {
        return $this->cognomeCliente;
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

    public function getWPrevendita($id, $idEvento, $idPR) : WPrevendita{
        return WPrevendita::make($id, $idEvento, $idPR, $this->getNomeCliente(), $this->getCognomeCliente(), $this->getIdTipoPrevendita(), $this->getCodice(), $this->getStato(), new DateTimeImmutableAdapterJSON(new \DateTimeImmutable()));
    }
    
    
}

