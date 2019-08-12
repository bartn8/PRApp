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

namespace com\model\net\wrapper\update;

use com\model\net\wrapper\NetWrapper;

class UpdateNetWStaff implements NetWrapper
{

    public static function make($id, $codiceAccesso)
    {
        if (is_null($id) || is_null($codiceAccesso))
            throw new InvalidArgumentException("Uno o più parametri nulli");

        if (! is_int($id) || ! is_string($codiceAccesso))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");

        if ($id <= 0)
            throw new InvalidArgumentException("ID non valido");

        return new UpdateNetWStaff($id, $codiceAccesso);
    }

    public static function of($array)
    {
        if (is_null($array) || ! is_array($array))
            throw new InvalidArgumentException("Array nullo o non valido.");

        if (! array_key_exists("id", $array))
            throw new InvalidArgumentException("Dato id non trovato.");

        if (! array_key_exists("codiceAccesso", $array))
            throw new InvalidArgumentException("Dato codiceAccesso non trovato.");

        return self::make((int) $array["id"], $array["codiceAccesso"]);
    }

    /**
     * Id dello staff.
     *
     * @var int
     */
    private $id;

    /**
     * Codice per accedere allo staff.
     *
     * @var string
     */
    private $codiceAccesso;

    private function __construct($id, $codiceAccesso)
    {
        $this->id = $id;
        $this->codiceAccesso = $codiceAccesso;
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
}

