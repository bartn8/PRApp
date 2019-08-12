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

namespace com\model\net\wrapper;

use InvalidArgumentException;

class NetWStaffAccess implements NetWrapper
{

    // public const CODE_MIN = 4;
    public static function of($array)
    {
        if (is_null($array) || ! is_array($array))
            throw new InvalidArgumentException("Array nullo o non valido.");

        if (! array_key_exists("idStaff", $array))
            throw new InvalidArgumentException("Dato codiceAccesso non trovato.");

        if (! array_key_exists("codiceAccesso", $array))
            throw new InvalidArgumentException("Dato codiceAccesso non trovato.");

        return self::make((int) $array["idStaff"], $array["codiceAccesso"]);
    }

    private static function make($idStaff, $codiceAccesso)
    {
        if (is_null($idStaff) || is_null($codiceAccesso))
            throw new InvalidArgumentException("Uno o più parametri nulli");

        if (! is_int($idStaff) || ! is_string($codiceAccesso))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");

        if ($idStaff <= 0)
            throw new InvalidArgumentException("ID Staff non valido.");

        if ($codiceAccesso === "")
            throw new InvalidArgumentException("Codice accesso vuoto");

        // if(strlen($codiceAccesso) < self::CODE_MIN)
        // throw new InvalidArgumentException("CodiceAccesso non valido (MIN)");

        return new NetWStaffAccess($idStaff, $codiceAccesso);
    }

    /**
     * Id dello staff su cui accedere.
     *
     * @var int
     */
    private $idStaff;

    /**
     * Codice per accedere allo staff.
     *
     * @var string
     */
    private $codiceAccesso;

    private function __construct($idStaff, $codiceAccesso)
    {
        $this->idStaff = $idStaff;
        $this->codiceAccesso = $codiceAccesso;
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
     * @return string
     */
    public function getCodiceAccesso()
    {
        return $this->codiceAccesso;
    }

    /**
     * Pulisce i campi.
     */
    public function clear()
    {
        unset($this->codiceAccesso);
    }
}

