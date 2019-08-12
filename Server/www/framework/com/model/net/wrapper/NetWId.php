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

class NetWId implements NetWrapper
{

    public static function of($array)
    {
        if (is_null($array) || ! is_array($array))
            throw new InvalidArgumentException("Array nullo o non valido.");

        if (! array_key_exists("id", $array))
            throw new InvalidArgumentException("Dato id non trovato.");

        return self::make((int) $array["id"]);
    }

    private static function make(int $id): NetWId
    {
        if (is_null($id))
            throw new InvalidArgumentException("Uno o più parametri nulli");

        if (! is_int($id))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");

        if ($id <= 0)
            throw new InvalidArgumentException("ID non valido");

        return new NetWId($id);
    }

    /**
     * Id da utilizzare
     *
     * @var int
     */
    private $id;

    private function __construct($id)
    {
        $this->id = $id;
    }

    /**
     *
     * @return number
     */
    public function getId()
    {
        return $this->id;
    }
}

