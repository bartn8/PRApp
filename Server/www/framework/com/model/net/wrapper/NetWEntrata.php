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
use com\model\db\wrapper\WPrevendita;
use com\model\db\wrapper\WEntrata;
use com\utils\DateTimeImmutableAdapterJSON;

class NetWEntrata implements NetWrapper
{

    public static function of($array)
    {
        if (is_null($array) || ! is_array($array))
            throw new InvalidArgumentException("Array nullo o non valido.");

        if (! array_key_exists("idPrevendita", $array))
            throw new InvalidArgumentException("Dato idPrevendita non trovato.");

        if (! array_key_exists("idEvento", $array))
            throw new InvalidArgumentException("Dato idEvento non trovato.");

        if (! array_key_exists("codiceAccesso", $array))
            throw new InvalidArgumentException("Dato codiceAccesso non trovato.");

        return self::make((int) $array["idPrevendita"], (int) $array["idEvento"], $array["codiceAccesso"]);
    }

    public static function make(int $idPrevendita, int $idEvento, string $codiceAccesso): NetWEntrata
    {
        if (is_null($idPrevendita) || is_null($idEvento) || is_null($codiceAccesso))
            throw new InvalidArgumentException("Uno o più parametri nulli");

        if (! is_int($idPrevendita) || ! is_int($idEvento) || ! is_string($codiceAccesso))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");

        if (strlen($codiceAccesso) > WPrevendita::CODICE_MAX_LENGTH)
            throw new InvalidArgumentException("Username non valido (MAX)");

        return new NetWEntrata($idPrevendita, $idEvento, $codiceAccesso);
    }

    /**
     * Identificativo della prevendita.
     *
     * @var int
     */
    private $idPrevendita;

    /**
     * Identificativo dell'evento a cui risponde il cassiere.
     *
     * @var int
     */
    private $idEvento;

    /**
     * Codice di accesso della prevendita.
     *
     * @var string|NULL
     */
    private $codiceAccesso;

    private function __construct($idPrevendita, $idEvento, $codiceAccesso)
    {
        $this->idPrevendita = $idPrevendita;
        $this->idEvento = $idEvento;
        $this->codiceAccesso = $codiceAccesso;
    }

    /**
     * Restituisce l'id della prevendita associata all'entrata
     * @return number
     */
    public function getIdPrevendita()
    {
        return $this->idPrevendita;
    }

    /**
     * Restituisce il codice di accesso.
     * Potrebbe essere 
     * 
     * @return string|NULL
     */
    public function getCodiceAccesso()
    {
        return $this->codiceAccesso;
    }

    /**
     * Pulisce i campi.
     */
    public function clear()
    {
        $this->codiceAccesso = NULL;
    }

    /**
     * Restituisce il corrispondente WEntrata.
     * @param int $idCassiere
     * @param DateTimeImmutableAdapterJSON $timestampEntrata
     * @return \com\model\db\wrapper\WEntrata
     */
    public function getWEntrata(int $idCassiere, DateTimeImmutableAdapterJSON $timestampEntrata)
    {
        return WEntrata::make($idCassiere, self::getIdPrevendita(), $timestampEntrata);
    }

    /**
     * Get identificativo dell'evento a cui risponde il cassiere.
     *
     * @return  int
     */ 
    public function getIdEvento()
    {
        return $this->idEvento;
    }

}

