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
use com\model\db\enum\StatoPrevendita;
use com\model\net\serialize\ArrayDeserializable;

class WPrevenditaPlus implements DatabaseWrapper
{

    const CODICE_MAX = 10;

    /**
     * Meotodo factory.
     *
     * @param int $id
     * @param int $idEvento
     * @param int $idPR
     * @param int $idTipoPrevendita
     * @param string $codice
     * @param StatoPrevendita $stato
     * @throws InvalidArgumentException
     * @return \com\model\db\wrapper\WPrevenditaPlus
     */
    //TODO: Anche qui bisogna aggiungere qualche controllino che non ho voglia di aggiungere
    public static function make($id, $idEvento, $nomeEvento, $idPR, $nomePR, $cognomePR, $nomeCliente, $cognomeCliente, $idTipoPrevendita, $nomeTipoPrevendita, $prezzoTipoPrevendita, $codice, $stato)
    {
        if (is_null($id) || is_null($idEvento) || is_null($idPR) || is_null($idTipoPrevendita) || is_null($codice) || is_null($stato))
            throw new InvalidArgumentException("Uno o più parametri nulli");

        if (! is_int($id) || ! is_int($idEvento) || ! is_int($idPR) || ! is_int($idTipoPrevendita) || ! ($stato instanceof StatoPrevendita))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");

        if (strlen($codice) > self::CODICE_MAX)
            throw new InvalidArgumentException("Codice non valido (MAX)");

        if ($id <= 0)
            throw new InvalidArgumentException("ID non valido");

        if ($idEvento <= 0)
            throw new InvalidArgumentException("ID Evento non valido");

        if ($idPR <= 0)
            throw new InvalidArgumentException("ID PR non valido");

        if ($idTipoPrevendita <= 0)
            throw new InvalidArgumentException("ID Tipo Prevendita non valido");

        return new WPrevenditaPlus($id, $idEvento, $nomeEvento, $idPR, $nomePR, $cognomePR, $nomeCliente, $cognomeCliente, $idTipoPrevendita, $nomeTipoPrevendita, $prezzoTipoPrevendita, $codice, $stato);
    }

    /**
     * Converte un array di stringhe in un wrapper.
     *
     * @param array $array
     * @throws InvalidArgumentException i dati dell'array non sono validi oppure l'array stesso non è valido
     * @throws \com\model\exception\ParseException
     * @return WPrevenditaPlus wrapper convertito
     */
    public static function of($array)
    {
        if (is_null($array) || ! is_array($array))
            throw new InvalidArgumentException("Array nullo o non valido.");
        if (! array_key_exists("id", $array))
            throw new InvalidArgumentException("Dato id non trovato.");

        if (! array_key_exists("idEvento", $array))
            throw new InvalidArgumentException("Dato idEvento non trovato.");

        if (! array_key_exists("nomeEvento", $array))
            throw new InvalidArgumentException("Dato nomeEvento non trovato.");

        if (! array_key_exists("idPR", $array))
            throw new InvalidArgumentException("Dato idPR non trovato.");

        if (! array_key_exists("nomePR", $array))
            throw new InvalidArgumentException("Dato nomePR non trovato.");

        if (! array_key_exists("cognomePR", $array))
            throw new InvalidArgumentException("Dato cognomePR non trovato.");

        if (! array_key_exists("nomeCliente", $array))
            throw new InvalidArgumentException("Dato nomeCliente non trovato.");

        if (! array_key_exists("cognomeCliente", $array))
			throw new InvalidArgumentException("Dato cognomeCliente non trovato.");

        if (! array_key_exists("idTipoPrevendita", $array))
            throw new InvalidArgumentException("Dato idTipoPrevendita non trovato.");

        if (! array_key_exists("nomeTipoPrevendita", $array))
            throw new InvalidArgumentException("Dato nomeTipoPrevendita non trovato.");

        if (! array_key_exists("prezzoTipoPrevendita", $array))
            throw new InvalidArgumentException("Dato prezzoTipoPrevendita non trovato.");

        if (! array_key_exists("codice", $array))
            throw new InvalidArgumentException("Dato codice non trovato.");

        if (! array_key_exists("stato", $array))
            throw new InvalidArgumentException("Dato stato non trovato.");

        $prezzoTipoPrevendita = is_null($array["prezzoTipoPrevendita"]) ? NULL : (float) $array["prezzoTipoPrevendita"];

        return self::make((int) $array["id"], (int) $array["idEvento"], $array["nomeEvento"], (int) $array["idPR"], $array["nomePR"], $array["cognomePR"], $array["nomeCliente"], $array["cognomeCliente"], (int) $array["idTipoPrevendita"], $array["nomeTipoPrevendita"], $prezzoTipoPrevendita, $array["codice"], StatoPrevendita::parse($array["stato"]));
    }

    /**
     * Identificativo della prevendita.
     * (>0).
     *
     * @var int
     */
    private $id;

    /**
     * Identificativo dell'evento.
     * (>0).
     *
     * @var int
     */
    private $idEvento;

    /**
     * Nome dell'evento.
     *
     * @var string
     */
    private $nomeEvento;

    /**
     * Identificativo del PR.
     * (>0).
     *
     * @var int
     */
    private $idPR;

    /**
     * Nome del pr.
     *
     * @var string
     */
    private $nomePR;

    /**
     * Cognome del pr.
     *
     * @var string
     */
    private $cognomePR;

    /**
     * Nome del cliente.(OZPTIONALE)
     *
     * @var string|NULL
     */
    private $nomeCliente;

    /**
     * Cognome del cliente.(OZPTIONALE)
     *
     * @var string|NULL
     */
    private $cognomeCliente;

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
     * Prezzo del tipo prevendita.
     *
     * @var float
     */
    private $prezzoTipoPrevendita;

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


    protected function __construct($id, $idEvento, $nomeEvento, $idPR, $nomePR, $cognomePR, $nomeCliente, $cognomeCliente, $idTipoPrevendita, $nomeTipoPrevendita, $prezzoTipoPrevendita, $codice, $stato)
    {
        $this->id = $id;
        $this->idEvento = $idEvento;
        $this->nomeEvento = $nomeEvento;
        $this->idPR = $idPR;
        $this->nomePR = $nomePR;
        $this->cognomePR = $cognomePR;
        $this->nomeCliente = $nomeCliente;
        $this->cognomeCliente = $cognomeCliente;
        $this->idTipoPrevendita = $idTipoPrevendita;
        $this->nomeTipoPrevendita = $nomeTipoPrevendita;
        $this->prezzoTipoPrevendita = $prezzoTipoPrevendita;
        $this->codice = $codice;
        $this->stato = $stato;
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

    //TODO: si dovrebbero aggiungere i getters ma non servono.

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
    public function getIdPR()
    {
        return $this->idPR;
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
    public function getCodice()
    {
        return $this->codice;
    }

    /**
     *
     * @return \com\model\db\enum\StatoPrevendita
     */
    public function getStato()
    {
        return $this->stato;
    }

 

}

