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

use ReflectionClass;
use ReflectionException;
use InvalidArgumentException;
use com\model\net\wrapper\NetWrapper;

class Argument
{

    public static function of($arg)
    {
        if (! is_string($arg)) {
            throw new InvalidArgumentException("Errore argomento non è stringa");
        }

        $decoded = \json_decode($arg, true, 512, \JSON_UNESCAPED_UNICODE);

        if ($decoded === NULL || $decoded === TRUE || $decoded === FALSE) {
            throw new InvalidArgumentException("Errore argomento non è JSON");
        }

        $name = $decoded['name'];
        $type = $decoded['type'];
        $value = $decoded['value'];

        if (! is_string($name) || ! is_string($type) || ! is_array($value)) {
            throw new InvalidArgumentException("Errore parametri non del tipo giusto");
        }

        try {
            $rClass = new ReflectionClass($type);

            if ($rClass->implementsInterface(NetWrapper::NAME)) {
                $rOf = $rClass->getMethod(NetWrapper::METHOD);
                return new Argument($name, $type, $rOf->invoke(null, $value));
            }

            throw new InvalidArgumentException("Tipo non valido");
        } catch (ReflectionException $ex) {
            throw new InvalidArgumentException("Tipo non esistente");
        }
    }

    public static function ofs($args)
    {
        if (! is_string($args))
            throw new InvalidArgumentException("Errore argomento non è stringa");

        //Se stringa vuota non ci sono argomenti
        if($args === "")
            return array();
            
        $decoded = \json_decode($args, true, 512, \JSON_UNESCAPED_UNICODE);

        if ($decoded === NULL || $decoded === TRUE || $decoded === FALSE)
            throw new InvalidArgumentException("Errore argomento non è JSON");

        $cache = array();

        foreach ($decoded as $element) {
            $name = $element['name'];
            $type = $element['type'];
            $value = $element['value'];

            if (! is_string($name) || ! is_string($type) || ! is_array($value)) {
                throw new InvalidArgumentException("Errore parametri non del tipo giusto");
            }

            try {
                $rClass = new ReflectionClass($type);

                if ($rClass->implementsInterface(NetWrapper::NAME)) {
                    $rOf = $rClass->getMethod(NetWrapper::METHOD);

                    $tmpArg = new Argument($name, $type, $rOf->invoke(null, $value));
                    $cache[$tmpArg->getName()] = $tmpArg;
                } else {
                    throw new InvalidArgumentException("Tipo non valido");
                }
            } catch (ReflectionException $ex) {
                throw new InvalidArgumentException("Tipo non esistente");
            }
        }

        return $cache;
    }

    /**
     * Nome dell'argomento.
     *
     * @var string
     */
    private $name;

    /**
     * Tipo dell'argomento.
     *
     * @var string
     */
    private $type;

    /**
     * Oggetto contenuto.
     *
     * @var object
     */
    private $value;

    private function __construct($name, $type, $value)
    {
        $this->name = $name;
        $this->type = $type;
        $this->value = $value;
    }

    /**
     *
     * @return string
     */
    public function getName()
    {
        return $this->name;
    }

    /**
     *
     * @return string
     */
    public function getType()
    {
        return $this->type;
    }

    /**
     *
     * @return object
     */
    public function getValue()
    {
        return $this->value;
    }
}

