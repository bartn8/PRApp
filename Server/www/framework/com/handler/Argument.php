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

    public const CLASS_TABLE = array(
        1 => array("utente" => "com\\model\\net\\wrapper\\insert\\InsertNetWUtente"),
        2 => array("login" => "com\\model\\net\\wrapper\\NetWLogin"),
        4 => array("staff" => "com\\model\\net\\wrapper\\insert\\InsertNetWStaff"),
        5 => array("staff" => "com\\model\\net\\wrapper\\NetWStaffAccess"),
        10 => array("token" => "com\\model\\net\\wrapper\\NetWToken"),
        12 => array("staff" => "com\\model\\net\\wrapper\\NetWId"),
        108 => array("evento" => "com\\model\\net\\wrapper\\NetWId"),
        203 => array("prevendita" => "com\\model\\net\\wrapper\\insert\\InsertNetWPrevendita"),
        204 => array("prevendita" => "com\\model\\net\\wrapper\\update\\UpdateNetWPrevendita"),
        205 => array("filtri" => "com\\model\\net\\wrapper\\NetWFiltriStatoPrevendita"),
        302 => array("entrata" => "com\\model\\net\\wrapper\\NetWEntrata"),
        309 => array("prevendita" => "com\\model\\net\\wrapper\\NetWId"),
        403 => array("evento" => "com\\model\\net\\wrapper\\insert\\InsertNetWEvento"),
        404 => array("evento" => "com\\model\\net\\wrapper\\update\\UpdateNetWEvento"),
        405 => array("tipoPrevendita" => "com\\model\\net\\wrapper\\insert\\InsertNetWTipoPrevendita"),
        406 => array("tipoPrevendita" => "com\\model\\net\\wrapper\\update\\UpdateNetWTipoPrevendita"),
        407 => array("tipoPrevendita" => "com\\model\\net\\wrapper\\NetWId"),
        408 => array("dirittiUtente" => "com\\model\\net\\wrapper\\update\\UpdateNetWRuoliMembro"),
        409 => array("pr" => "com\\model\\net\\wrapper\\NetWId"),
        410 => array("cassiere" => "com\\model\\net\\wrapper\\NetWId"),
        413 => array("membro" => "com\\model\\net\\wrapper\\NetWId"),
        414 => array("staff" => "com\\model\\net\\wrapper\\update\\UpdateNetWStaff"),
        415 => array("pr" => "com\\model\\net\\wrapper\\NetWId"),
        416 => array("cassiere" => "com\\model\\net\\wrapper\\NetWId"),
        417 => array("membro" => "com\\model\\net\\wrapper\\NetWId"),
        418 => array("prevendita" => "com\\model\\net\\wrapper\\update\\UpdateNetWPrevendita"),
    );

    public static function of($command, $arg)
    {
        if (!is_int($command) || !is_string($arg)) {
            throw new InvalidArgumentException("Errore argomento non è stringa o comando non valido");
        }

        $decoded = \json_decode($arg, true, 512, \JSON_UNESCAPED_UNICODE);

        if ($decoded === NULL || $decoded === TRUE || $decoded === FALSE) {
            throw new InvalidArgumentException("Errore argomento non è JSON");
        }

        if(array_key_exists($command, Argument::CLASS_TABLE)){
            $name = $decoded['name'];

            if(array_key_exists($name, Argument::CLASS_TABLE[$command])){
                //$type = $decoded['type'];
                $type = Argument::CLASS_TABLE[$command][$name];
                $value = $decoded['value'];

                if (! is_string($name) || ! is_array($value)) {
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
        }
    }

    public static function ofs($command, $args)
    {
        if (!is_int($command) || ! is_string($args))
            throw new InvalidArgumentException("Errore argomenti non è stringa o comando non valido");

        //Se stringa vuota non ci sono argomenti
        if($args === "")
            return array();
            
        $decoded = \json_decode($args, true, 512, \JSON_UNESCAPED_UNICODE);

        if ($decoded === NULL || $decoded === TRUE || $decoded === FALSE)
            throw new InvalidArgumentException("Errore argomenti non è JSON");

        $cache = array();

        if(array_key_exists($command, Argument::CLASS_TABLE)){
            foreach ($decoded as $element) {
                $name = $element['name'];

                if(array_key_exists($name, Argument::CLASS_TABLE[$command])){
                    //$type = $element['type'];
                    $type = Argument::CLASS_TABLE[$command][$name];
                    $value = $element['value'];
        
                    if (! is_string($name) || ! is_array($value)) {
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

