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

namespace com\utils;

use JsonSerializable;
use DateTime;

class DateTimeImmutableAdapterJSON implements JsonSerializable
{

    const MYSQL_TIMESTAMP = "Y-m-d H:i:s";
    const MYSQL_DATE = "Y-m-d";
    
    /**
     * Data incapsulata.
     * @var \DateTimeImmutable
     */
    private $dateTimeImmutable;
    
    /**
     * Formato della data per la scrittura JSON.
     * @var string
     */
    private $formatType = DateTime::ATOM;   //ISO 8061
    
    public function __construct($dateTimeImmutable, $formatType = DateTime::ATOM)
    {
        //Devo convertire la data nel fuso orario del server.
        $this->dateTimeImmutable = $dateTimeImmutable->setTimezone(new \DateTimeZone(TIMEZONE));       
        $this->formatType = $formatType;
    }

    public function jsonSerialize()
    {
        return $this->dateTimeImmutable->format($this->formatType);
    }
    
    /**
     * @return string
     */
    public function getFormatType()
    {
        return $this->formatType;
    }

    /**
     * @param string $formatType
     */
    public function setFormatType($formatType)
    {
        $this->formatType = $formatType;
    }
    /**
     * @return mixed
     */
    public function getDateTimeImmutable()
    {
        return $this->dateTimeImmutable;
    }

}

