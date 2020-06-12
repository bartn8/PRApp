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

use ReflectionClass;
use InvalidArgumentException;
use com\model\db\wrapper\WUtente;
use com\model\net\serialize\ArrayDeserializable;

class WUtente implements DatabaseWrapper
{

    const NOME_MAX = 150;

    const COGNOME_MAX = 150;

    const TELEFONO_MAX = 80;

    const USERNAME_MAX = 60;

    /**
     * Metodo factory con controlli.
     *
     * @param int $id
     * @param string $nome
     * @param string $cognome
     * @param string $telefono
     * @throws InvalidArgumentException
     * @return \com\model\db\wrapper\WUtente
     */
    public static function make($id, $nome, $cognome, $telefono)
    {
        if (is_null($id) || is_null($nome) || is_null($cognome) || is_null($telefono))
            throw new InvalidArgumentException("Uno o più parametri nulli");

        if (! is_int($id) || ! is_string($nome) || ! is_string($cognome) || ! is_string($telefono))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");

        if ($id <= 0)
            throw new InvalidArgumentException("ID non valido");

        if (strlen($nome) > self::NOME_MAX)
            throw new InvalidArgumentException("Nome non valido (MAX)");

        if (strlen($cognome) > self::COGNOME_MAX)
            throw new InvalidArgumentException("Cognome non valido (MAX)");

        if (strlen($telefono) > self::TELEFONO_MAX)
            throw new InvalidArgumentException("Telefono non valido (MAX)");

        return new WUtente($id, $nome, $cognome, $telefono);
    }

    public static function makeNoChecks($id, $nome, $cognome, $telefono)
    {
        return new WUtente($id, $nome, $cognome, $telefono);
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

        if (! array_key_exists("nome", $array))
            throw new InvalidArgumentException("Dato nome non trovato.");

        if (! array_key_exists("cognome", $array))
            throw new InvalidArgumentException("Dato cognome non trovato.");

        if (! array_key_exists("telefono", $array))
            throw new InvalidArgumentException("Dato telefono non trovato.");

        return self::make((int) $array["id"], $array["nome"], $array["cognome"], $array["telefono"]);
    }

    /**
     * Identificativo dell'utente. (>0).
     *
     * @var int
     */
    private $id;

    /**
     * Nome dell'utente.
     *
     * @var string
     */
    private $nome;

    /**
     * Cognome dell'utente.
     *
     * @var string
     */
    private $cognome;

    /**
     * telefono dell'utente in formato esteso (con prefisso).
     *
     * @var string
     */
    private $telefono;

    protected function __construct($id, $nome, $cognome, $telefono)
    {
        $this->id = $id;
        $this->nome = $nome;
        $this->cognome = $cognome;
        $this->telefono = $telefono;
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
    public function getNome()
    {
        return $this->nome;
    }

    /**
     *
     * @return string
     */
    public function getCognome()
    {
        return $this->cognome;
    }

    /**
     *
     * @return string
     */
    public function getTelefono()
    {
        return $this->telefono;
    }

}

