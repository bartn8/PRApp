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
use DateTimeImmutable;
use InvalidArgumentException;
use com\model\db\wrapper\WStaff;
use com\utils\DateTimeImmutableAdapterJSON;
use com\model\net\serialize\ArrayDeserializable;

class WStaff implements DatabaseWrapper
{

    public const NOME_MAX = 150;

    /**
     * Converte un array di stringhe in un wrapper.
     *
     * @param array $array
     * @throws InvalidArgumentException i dati dell'array non sono validi oppure l'array stesso non è valido
     * @return WStaff wrapper convertito
     */
    public static function of($array)
    {
        if (is_null($array) || ! is_array($array))
            throw new InvalidArgumentException("Array nullo o non valido.");

        if (! array_key_exists("id", $array))
            throw new InvalidArgumentException("Dato id non trovato.");

        if (! array_key_exists("idCreatore", $array))
            throw new InvalidArgumentException("Dato idCreatore non trovato.");

        if (! array_key_exists("nome", $array))
            throw new InvalidArgumentException("Dato id non trovato.");

        if (! array_key_exists("timestampCreazione", $array))
            throw new InvalidArgumentException("Dato timestampCreazione non trovato.");

        return self::make((int) $array["id"], (int) $array["idCreatore"], $array["nome"], new DateTimeImmutableAdapterJSON(DateTimeImmutable::createFromFormat(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP, $array["timestampCreazione"])));
    }

    /**
     * Metodo factory con controlli.
     *
     * @param int $id
     * @param int $idCreatore
     * @param string $nome
     * @param DateTimeImmutableAdapterJSON $timestampCreazione
     * @throws InvalidArgumentException
     * @return \com\model\db\wrapper\WStaff
     */
    public static function make($id, $idCreatore, $nome, $timestampCreazione)
    {
        if (is_null($id) || is_null($id) || is_null($nome) || is_null($timestampCreazione))
            throw new InvalidArgumentException("Uno o più parametri nulli");

        if (! is_int($id) || ! is_int($idCreatore) || ! is_string($nome) || ! ($timestampCreazione instanceof DateTimeImmutableAdapterJSON))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");

        if ($id <= 0)
            throw new InvalidArgumentException("ID non valido");

        if ($idCreatore <= 0)
            throw new InvalidArgumentException("ID creatore non valido");            

        if (strlen($nome) > self::NOME_MAX)
            throw new InvalidArgumentException("Nome non valido (MAX)");

        return new WStaff($id, $idCreatore, $nome, $timestampCreazione);
    }

    /**
     * Identificativo dello staff.
     * (>0).
     *
     * @var int
     */
    private $id;

    /**
     * Identificativo del'utente che ha creato lo staff.
     * (>0).
     *
     * @var int
     */
    private $idCreatore;

    /**
     * Nome dello staff.
     *
     * @var string
     */
    private $nome;

    /**
     * Indica quando è stato creato lo staff.
     *
     * @var DateTimeImmutableAdapterJSON
     */
    private $timestampCreazione;

    protected function __construct($id, $idCreatore, $nome, $timestampCreazione)
    {
        $this->id = $id;
        $this->idCreatore = $idCreatore;
        $this->nome = $nome;
        $this->timestampCreazione = $timestampCreazione;
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
     * @return int
     */
    public function getId()
    {
        return $this->id;
    }

    /**
     *
     * @return int
     */
    public function getIdCreatore()
    {
        return $this->idCreatore;
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
     * @return DateTimeImmutableAdapterJSON
     */
    public function getTimestampCreazione()
    {
        return $this->timestampCreazione;
    }

    public function changeId(int $id)
    {
        if (! is_int($id))
            throw new InvalidArgumentException("Parametro ID non valido");

        if ($id <= 0)
            throw new InvalidArgumentException("ID non valido");

        return new WStaff($id, $this->idCreatore, $this->nome, $this->timestampCreazione);
    }

    public function changeTimestampCreazione($timestamp)
    {
        if (! ($timestamp instanceof DateTimeImmutableAdapterJSON))
            throw new InvalidArgumentException("Parametro timestampCreazione non valido");

        return new WStaff($this->id, $this->idCreatore, $this->nome, $timestamp);
    }
}

