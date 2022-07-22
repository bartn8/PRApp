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

namespace com\handler;

use InvalidArgumentException;

class Command
{

    // POST SEQUENCE
    // cmd=.... &
    // args=[{name: '', type:'', value: JSON OBJ}, ...]
    public static function of($command, $args)
    {
        if (! is_int($command) || ! is_string($args)) {
            throw new InvalidArgumentException("Parametri non validi.");
        }
        
        return new Command($command, Argument::ofs($command, $args));
    }

    /**
     * Intero che rappresenta il comando.
     * 
     * @var int
     */
    private $command;

    /**
     * Array con gli argomenti richiesti dal comando.
     * @var Argument[]
     */
    private $args;

    private function __construct($command, $args)
    {
        $this->command = $command;
        $this->args = $args;
    }

    /**
     *
     * @return int
     */
    public function getCommand()
    {
        return $this->command;
    }

    /**
     *
     * @return Argument[]
     */
    public function getArgs()
    {
        return $this->args;
    }
}

