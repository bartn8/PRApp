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

// https://stackoverflow.com/questions/254514/php-and-enumerations
namespace com\model\db\enum;

use com\model\exception\ParseException;
use InvalidArgumentException;
use ReflectionClass;
use JsonSerializable;

abstract class BasicEnum implements JsonSerializable
{

    private static $classCacheArray = NULL;

    /**
     *
     * @return \ReflectionClass
     * @throws \ReflectionException
     */
    private static function getClass()
    {
        if (self::$classCacheArray == NULL) {
            self::$classCacheArray = [];
        }
        
        $calledClass = get_called_class();
        
        if (! array_key_exists($calledClass, self::$classCacheArray)) {
            self::$classCacheArray[$calledClass] = new ReflectionClass($calledClass);
        }
        
        return self::$classCacheArray[$calledClass];
    }

    private static function getConstants()
    {
        return self::getClass()->getConstants();
    }

    private static function isValidName($name, $strict = false)
    {
        $constants = self::getConstants();
        
        if ($strict) {
            return array_key_exists($name, $constants);
        }
        
        $keys = array_map('strtolower', array_keys($constants));
        
        return in_array(strtolower($name), $keys);
    }

    private static function isValidId($id)
    {
        $constants = self::getConstants();
        return in_array($id, array_values($constants));
    }

    public static function of($value)
    {
        $class = self::getClass();
        $consts = $class->getConstants();
        
        $const = array_search($value, $consts);
        
        if($const === FALSE)
        {
            throw new ParseException("ID enumeratore non valido.");
        }
        
        return $class->newInstanceArgs([
            $const,
            $consts[$const]
        ]);
    }

    public static function ofArray($array)
    {
        $parsedArray = array();
        
        foreach ($array as $i) {
            $parsedArray[] = self::of($i);
        }
        
        return $parsedArray;
    }

    public static function parse($name)
    {
        if(!self::isValidName($name))
            throw new ParseException("Nome non valido");
        
        $class = self::getClass();
        $consts = $class->getConstants();
        
        return $class->newInstanceArgs([
            $name,
            $consts[$name]
        ]);
    }

    public static function values()
    {
        $class = self::getClass();
        $const = $class->getConstants();
        $values = array();
        
        foreach (array_keys($const) as $key) {
            $values[] = $class->newInstanceArgs([
                $key,
                $const[$key]
            ]);
        }
        
        return $values;
    }

    public static function complement($array)
    {
        if (! is_array($array)) {
            throw new InvalidArgumentException("Array non valido");
        }
        
        $result = array();
        $values = self::values();
        
        foreach ($values as $value) {
            $found = FALSE;
            
            foreach ($array as $element) {
                if ($value == $element) {
                    $found = TRUE;
                    break;
                }
            }
            
            if (! $found) {
                $result[] = $value;
            }
        }
        
        return $result;
    }

    protected $name;

    protected $id;

    protected function __construct($name, $id)
    {        
        $this->name = $name;
        $this->id = $id;
    }

    public function jsonSerialize()
    {
        return $this->id;
    }
    
    
    
    /**
     * @return mixed
     */
    public function getId()
    {
        return $this->id;
    }

    /**
     *
     * @return string
     */
    public function toString()
    {
        return $this->name;
    }
}

