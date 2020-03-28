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

namespace com\model\db\wrapper;

use com\model\db\enum\Diritto;
use ReflectionClass;
use InvalidArgumentException;
use com\model\handler\Transportable;
use com\model\net\serialize\ArrayDeserializable;

class WDirittiUtente implements DatabaseWrapper
{

    /**
     * Metodo factory.
     *
     * @param int $idUtente
     * @param int $idStaff
     * @param Diritto[] $diritti
     * @throws InvalidArgumentException
     * @return \com\model\db\wrapper\WDirittiUtente
     */
    public static function make($idUtente, $idStaff, $diritti)
    {
        if (is_null($idUtente) || is_null($idStaff) || is_null($diritti))
            throw new InvalidArgumentException("Uno o più parametri nulli");

        if (! is_int($idUtente) || ! is_int($idStaff) || ! is_array($diritti))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");

        if ($idUtente <= 0)
            throw new InvalidArgumentException("ID Utente non valido");

        if ($idStaff <= 0)
            throw new InvalidArgumentException("ID Staff non valido");

        foreach ($diritti as $diritto) {
            if (! ($diritto instanceof Diritto))
                throw new InvalidArgumentException("Diritti non del tipo giusto");
        }

        return new WDirittiUtente($idUtente, $idStaff, $diritti);
    }

    public static function makeNoChecks($idUtente, $idStaff, $diritti)
    {
        return new WDirittiUtente($idUtente, $idStaff, $diritti);
    }

    /**
     * Tenta di fare il parsing dei diritti su un array di stringhe.
     *
     * @param int $idUtente
     * @param int $idStaff
     * @param array $arrayString
     * @return WDirittiUtente
     * @throws \com\model\exception\ParseException
     */
    public static function parse($idUtente, $idStaff, $arrayString)
    {
        $diritti = array();

        foreach ($arrayString as $stringa) {
            $diritti[] = Diritto::parse(strtoupper($stringa));
        }

        return self::make($idUtente, $idStaff, $diritti);
    }

    /**
     * Converte un array di stringhe in un wrapper.
     *
     * @param array $array
     * @throws InvalidArgumentException i dati dell'array non sono validi oppure l'array stesso non è valido
     * @return WDirittiUtente wrapper convertito
     */
    public static function of($array)
    {
        if (is_null($array) || ! is_array($array))
            throw new InvalidArgumentException("Array nullo o non valido.");

        if (! array_key_exists("idUtente", $array))
            throw new InvalidArgumentException("Dato idUtente non trovato.");

        if (! array_key_exists("idStaff", $array))
            throw new InvalidArgumentException("Dato idStaff non trovato.");

        if (! array_key_exists("pr", $array))
            throw new InvalidArgumentException("Dato pr non trovato.");

        if (! array_key_exists("cassiere", $array))
            throw new InvalidArgumentException("Dato cassiere non trovato.");

        if (! array_key_exists("amministratore", $array))
            throw new InvalidArgumentException("Dato amministratore non trovato.");
        
        //Piccolo stratagemma (In memoria di UGO)....
        $array["diritti"] = 0;
        $array["diritti"] = ("1" == $array["pr"]) << 2 | ("1" == $array["cassiere"]) << 1 | ("1" == $array["amministratore"]);

        return self::make((int) $array["idUtente"], (int) $array["idStaff"], Diritto::ofPCA($array["diritti"]));
    }

    /**
     * Identificativo dell'utente.
     *
     * @var int
     */
    private $idUtente;

    /**
     * Identificativo dello staff.
     *
     * @var int
     */
    private $idStaff;

    /**
     * Diritti dell'utente nello staff.
     *
     * @var Diritto[]
     */
    private $diritti;

    protected function __construct($idUtente, $idStaff, $diritti)
    {
        $this->idUtente = $idUtente;
        $this->idStaff = $idStaff;
        $this->diritti = $diritti;
    }

    /**
     * Serve per serializzare il wrapper in JSON.
     */
    public function jsonSerialize()
    {
        $thisClass = new ReflectionClass(get_class());
        $props = $thisClass->getProperties();
        $array = array();

        foreach ($props as $prop) {
            $array[$prop->getName()] = $this->{$prop->getName()};
        }

        return $array;
    }

    /**
     * Restituisce l'id dell'utente a cui sono associati i diritti nello staff
     * @return int
     */
    public function getIdUtente()
    {
        return $this->idUtente;
    }

    /**
     * Restituisce l'id dello staff a cui sono associati i diritti dell'utente
     * @return int
     */
    public function getIdStaff()
    {
        return $this->idStaff;
    }

    /**
     * Restituisce i diritti dell'utente.
     * @return Diritto[]
     */
    public function getDiritti() : array
    {
        return $this->diritti;
    }

    /**
     * Dice se l'utente è cassiere nello staff.
     * 
     * @return boolean
     */
    public function isCassiere() : boolean
    {
        foreach($this->diritti as $diritto){
            if(Diritto::CASSIERE == $diritto->getId()){
                return TRUE;
            }
        }

        return FALSE;
    }

    /**
     * Dice se l'utente è PR nello staff.
     * 
     * @return boolean
     */
    public function isPR() : boolean
    {
        foreach($this->diritti as $diritto){
            if(Diritto::PR == $diritto->getId()){
                return TRUE;
            }
        }

        return FALSE;
    }

    /**
     * Dice se l'utente è amministratore nello staff.
     * 
     * @return boolean
     */
    public function isAmministratore() : boolean
    {
        foreach($this->diritti as $diritto){
            if(Diritto::AMMINISTRATORE == $diritto->getId()){
                return TRUE;
            }
        }

        return FALSE;
    }
}

