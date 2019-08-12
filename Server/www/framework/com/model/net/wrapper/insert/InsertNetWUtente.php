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

use com\model\db\wrapper\WUtente;
use InvalidArgumentException;
use com\model\net\serialize\ArrayDeserializable;
use com\model\net\wrapper\NetWrapper;

class InsertNetWUtente implements NetWrapper
{

    /**
     * Metodo factory per l'inserimento.
     *
     * @param string $nome
     * @param string $cognome
     * @param string $telefono
     * @param string $username
     * @param string $password
     * @throws InvalidArgumentException
     * @return \com\model\db\wrapper\WUtente
     */
    private static function make($nome, $cognome, $telefono, $username, $password)
    {
        if (is_null($nome) || is_null($cognome) || is_null($telefono) || is_null($username) || is_null($password))
            throw new InvalidArgumentException("Uno o più parametri nulli");

        if (! is_string($nome) || ! is_string($cognome) || ! is_string($telefono) || ! is_string($username) || ! is_string($password))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");

        if (strlen($nome) > WUtente::NOME_MAX)
            throw new InvalidArgumentException("Nome non valido (MAX)");

        if (strlen($cognome) > WUtente::COGNOME_MAX)
            throw new InvalidArgumentException("Cognome non valido (MAX)");

        if (strlen($telefono) > WUtente::TELEFONO_MAX)
            throw new InvalidArgumentException("Telefono non valido (MAX)");

        /*
        if (preg_match(WUtente::TELEFONO_REGEX, $telefono) !== 1)
            throw new InvalidArgumentException("Telefono non valido (REGEX)");

        */

        if (strlen($username) > WUtente::USERNAME_MAX)
            throw new InvalidArgumentException("Username non valido (MAX)");

        if ($password === "")
            throw new InvalidArgumentException("Password vuota");

        return new InsertNetWUtente($nome, $cognome, $telefono, $username, $password);
    }

    public static function of($array)
    {
        if (is_null($array) || ! is_array($array))
            throw new InvalidArgumentException("Array nullo o non valido.");

        if (! array_key_exists("nome", $array))
            throw new InvalidArgumentException("Dato nome non trovato.");

        if (! array_key_exists("cognome", $array))
            throw new InvalidArgumentException("Dato cognome non trovato.");

        if (! array_key_exists("telefono", $array))
            throw new InvalidArgumentException("Dato telefono non trovato.");

        if (! array_key_exists("username", $array))
            throw new InvalidArgumentException("Dato username non trovato.");

        if (! array_key_exists("password", $array))
            throw new InvalidArgumentException("Dato password non trovato.");

        return self::make($array["nome"], $array["cognome"], $array["telefono"], $array["username"], $array["password"]);
    }

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

    /**
     * Username utilizzato dall'utente nuovo.
     *
     * @var string
     */
    private $username;

    /**
     * Password che verrà messa in hash.
     *
     * @var string
     */
    private $password;

    private function __construct($nome, $cognome, $telefono, $username, $password)
    {
        $this->nome = $nome;
        $this->cognome = $cognome;
        $this->telefono = $telefono;
        $this->username = $username;
        $this->password = $password;
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

    /**
     * Restituisce l'username da inserire.
     *
     * @return string
     */
    public function getUsername()
    {
        return $this->username;
    }

    /**
     * Restituisce la password da inserire.
     *
     * @return string
     */
    public function getPassword()
    {
        return $this->password;
    }

    /**
     * Pulisce i campi.
     */
    public function clear()
    {
        unset($this->username);
        unset($this->password);
    }

    public function getWUtente($id): WUtente
    {
        return WUtente::makeNoChecks($id, self::getNome(), self::getCognome(), self::getTelefono());
    }
}

