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

use com\model\db\enum\StatoPrevendita;
use InvalidArgumentException;

class NetWFiltriStatoPrevendita implements NetWrapper
{

    public static function of($array)
    {
        if (is_null($array) || ! is_array($array))
            throw new InvalidArgumentException("Array nullo o non valido.");

        if (! array_key_exists("filtri", $array))
            throw new InvalidArgumentException("Dato filtri non trovato.");

        if (! is_array($array["filtri"])) {
            throw new \InvalidArgumentException("Il parametro filtri non è array");
        }

        //Levo i doppioni
        $array["filtri"] = array_unique($array["filtri"]);
        
        $filtri = array();

        foreach ($array["filtri"] as $value) {
            $filtri[] = StatoPrevendita::of($value);
        }

        return self::make($filtri);
    }

    public static function make($filtri)
    {
        if (is_null($filtri)) {
            throw new \InvalidArgumentException("Array dei filtri nullo");
        }

        if (! is_array($filtri)) {
            throw new \InvalidArgumentException("Argomento filtri non è array");
        }

        foreach ($filtri as $filtro) {
            if (! ($filtro instanceof StatoPrevendita))
                throw new \InvalidArgumentException("Array misto (StatoPrevendita)");
        }

        return new NetWFiltriStatoPrevendita($filtri);
    }

    /**
     * Filtri da applicare
     *
     * @var StatoPrevendita[]
     */
    private $filtri;

    private function __construct($filtri)
    {
        $this->filtri = $filtri;
    }

    /**
     *
     * @return StatoPrevendita[]
     */
    public function getFiltri()
    {
        return $this->filtri;
    }
}

