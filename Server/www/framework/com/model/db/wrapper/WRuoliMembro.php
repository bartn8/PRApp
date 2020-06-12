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

use ReflectionClass;
use com\model\db\enum\Ruolo;
use InvalidArgumentException;
use com\model\handler\Transportable;
use com\model\db\wrapper\WRuoliMembro;
use com\model\net\serialize\ArrayDeserializable;

class WRuoliMembro implements DatabaseWrapper
{

    /**
     * Metodo factory.
     *
     * @param int $idUtente
     * @param int $idStaff
     * @param Ruolo[] $ruoli
     * @throws InvalidArgumentException
     * @return \com\model\db\wrapper\WRuoliMembro
     */
    public static function make($idUtente, $idStaff, $ruoli)
    {
        if (is_null($idUtente) || is_null($idStaff) || is_null($ruoli))
            throw new InvalidArgumentException("Uno o più parametri nulli");

        if (! is_int($idUtente) || ! is_int($idStaff) || ! is_array($ruoli))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");

        if ($idUtente <= 0)
            throw new InvalidArgumentException("ID Utente non valido");

        if ($idStaff <= 0)
            throw new InvalidArgumentException("ID Staff non valido");

        foreach ($ruoli as $ruolo) {
            if (! ($ruolo instanceof Ruolo))
                throw new InvalidArgumentException("Ruoli non del tipo giusto");
        }

        return new WRuoliMembro($idUtente, $idStaff, $ruoli);
    }

    public static function makeNoChecks($idUtente, $idStaff, $ruoli)
    {
        return new WRuoliMembro($idUtente, $idStaff, $ruoli);
    }

    /**
     * Tenta di fare il parsing dei ruoli su un array di stringhe.
     *
     * @param int $idUtente
     * @param int $idStaff
     * @param array $arrayString
     * @return WRuoliMembro
     * @throws \com\model\exception\ParseException
     */
    public static function parse($idUtente, $idStaff, $arrayString)
    {
        $ruoli = array();

        foreach ($arrayString as $stringa) {
            $ruoli[] = Ruolo::parse(strtoupper($stringa));
        }

        return self::make($idUtente, $idStaff, $ruoli);
    }

    /**
     * Converte un array di stringhe in un wrapper.
     *
     * @param array $array
     * @throws InvalidArgumentException i dati dell'array non sono validi oppure l'array stesso non è valido
     * @return WRuoliMembro wrapper convertito
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
        $array["ruoli"] = 0;
        $array["ruoli"] = ("1" == $array["pr"]) << 2 | ("1" == $array["cassiere"]) << 1 | ("1" == $array["amministratore"]);

        return self::make((int) $array["idUtente"], (int) $array["idStaff"], Ruolo::ofPCA($array["ruoli"]));
    }

    /**
     * Identificativo del membro.
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
     * Ruoli del membro nello staff.
     *
     * @var Ruolo[]
     */
    private $ruoli;

    protected function __construct($idUtente, $idStaff, $ruoli)
    {
        $this->idUtente = $idUtente;
        $this->idStaff = $idStaff;
        $this->ruoli = $ruoli;
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
     * Restituisce l'id del membro a cui sono associati i ruoli nello staff
     * @return int
     */
    public function getIdUtente()
    {
        return $this->idUtente;
    }

    /**
     * Restituisce l'id dello staff a cui sono associati i ruoli del membro
     * @return int
     */
    public function getIdStaff()
    {
        return $this->idStaff;
    }

    /**
     * Restituisce i ruoli del membro.
     * @return Ruolo[]
     */
    public function getRuoli() : array
    {
        return $this->ruoli;
    }

    /**
     * Dice se il membro è cassiere nello staff.
     * 
     * @return bool
     */
    public function isCassiere() : bool
    {
        foreach($this->ruoli as $ruolo){
            if(Ruolo::CASSIERE == $ruolo->getId()){
                return TRUE;
            }
        }

        return FALSE;
    }

    /**
     * Dice se il membro è PR nello staff.
     * 
     * @return bool
     */
    public function isPR() : bool
    {
        foreach($this->ruoli as $ruolo){
            if(Ruolo::PR == $ruolo->getId()){
                return TRUE;
            }
        }

        return FALSE;
    }

    /**
     * Dice se il membro è amministratore nello staff.
     * 
     * @return bool
     */
    public function isAmministratore() : bool
    {
        foreach($this->ruoli as $ruolo){
            if(Ruolo::AMMINISTRATORE == $ruolo->getId()){
                return TRUE;
            }
        }

        return FALSE;
    }
}

