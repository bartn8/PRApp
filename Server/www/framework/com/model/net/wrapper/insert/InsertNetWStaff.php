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

use com\model\db\wrapper\WStaff;
use com\model\net\wrapper\NetWrapper;
use InvalidArgumentException;

class InsertNetWStaff implements NetWrapper
{

    /**
     * Metodo factory per l'inserimento.
     *
     * @param string $nome
     * @throws InvalidArgumentException
     * @return InsertNetWStaff
     */
    private static function make($nome, $codiceAccesso)
    {
        if (is_null($nome) || is_null($codiceAccesso))
            throw new InvalidArgumentException("Uno o più parametri nulli");

        if (! is_string($nome) || ! is_string($codiceAccesso))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");

        if (strlen($nome) > WStaff::NOME_MAX)
            throw new InvalidArgumentException("Nome non valido (MAX)");

        if ($nome === "")
            throw new InvalidArgumentException("Nome vuoto.");

        if ($codiceAccesso === "")
            throw new InvalidArgumentException("Codice accesso vuoto.");

        return new InsertNetWStaff($nome, $codiceAccesso);
    }

    public static function of($array)
    {
        if (is_null($array) || ! is_array($array))
            throw new InvalidArgumentException("Array nullo o non valido.");

        if (! array_key_exists("nome", $array))
            throw new InvalidArgumentException("Dato id non trovato.");

        if (! array_key_exists("codiceAccesso", $array))
            throw new InvalidArgumentException("Dato codiceAccesso non trovato.");

        return self::make($array["nome"], $array["codiceAccesso"]);
    }

    /**
     * Nome dello staff.
     *
     * @var string
     */
    private $nome;

    /**
     * Codice per accedere allo staff.
     *
     * @var string
     */
    private $codiceAccesso;

    private function __construct($nome, $codiceAccesso)
    {
        $this->nome = $nome;
        $this->codiceAccesso = $codiceAccesso;
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
     * Restituisce il codice di accesso.
     *
     * @return string
     */
    public function getCodiceAccesso()
    {
        return $this->codiceAccesso;
    }

    /**
     * Pulisce i campi a rischio.
     */
    public function clear()
    {
        unset($this->codiceAccesso);
    }
    
    public function getWStaff($id, $timestampCreazione) : WStaff
    {
        return WStaff::make($id, self::getNome(), $timestampCreazione);
    }
}

