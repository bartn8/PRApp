<?php

/*
 * PRApp  Copyright (C) 2020  Luca Bartolomei
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

use DateTimeImmutable;
use InvalidArgumentException;
use com\model\net\wrapper\NetWId;
use com\utils\DateTimeImmutableAdapterJSON;

class NetWTimestamp implements NetWrapper
{

    public static function of($array)
    {
        if (is_null($array) || ! is_array($array))
            throw new InvalidArgumentException("Array nullo o non valido.");

        if (! array_key_exists("timestamp", $array))
            throw new InvalidArgumentException("Dato id non trovato.");

        $timestamp = new DateTimeImmutableAdapterJSON(\DateTimeImmutable::createFromFormat(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP, $array["timestamp"]));

        return self::make(timestamp);
    }

    private static function make(int $id): NetWId
    {
        if (is_null($timestamp))
            throw new InvalidArgumentException("Uno o più parametri nulli");

        if (! ($timestamp instanceof DateTimeImmutableAdapterJSON))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");

        return new NetWTimestamp($timestamp);
    }

    /**
     * Timestamp
     *
     * @var DateTimeImmutableAdapterJSON
     */
    private $timestamp;

    private function __construct($timestamp)
    {
        $this->timestamp = $timestamp;
    }

    /**
     *
     * @return DateTimeImmutableAdapterJSON
     */
    public function getTimestamp()
    {
        return $this->timestamp;
    }
}

