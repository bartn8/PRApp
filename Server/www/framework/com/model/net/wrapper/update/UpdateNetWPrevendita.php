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

namespace com\model\net\wrapper\update;

use InvalidArgumentException;
use com\model\db\enum\StatoPrevendita;
use com\model\db\wrapper\WPrevendita;
use com\model\net\serialize\ArrayDeserializable;
use com\model\net\wrapper\NetWrapper;

class UpdateNetWPrevendita implements NetWrapper
{

    /**
     * Meotodo factory per la modifica.
     *
     * @param int $idEvento
     * @param int $idPR
     * @param int $idCliente
     * @param int $idTipoPrevendita
     * @param string $codice
     * @param StatoPrevendita $stato
     * @throws InvalidArgumentException
     * @return UpdateNetWPrevendita
     */
    private static function make($id, $stato)
    {
        if (is_null($id) || is_null($stato))
            throw new InvalidArgumentException("Uno o più parametri nulli");

        if (! is_int($id) || ! ($stato instanceof StatoPrevendita))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");

        if ($id <= 0)
            throw new InvalidArgumentException("ID non valido");

        return new UpdateNetWPrevendita($id, $stato);
    }

    public static function of($array)
    {
        if (is_null($array) || ! is_array($array))
            throw new InvalidArgumentException("Array nullo o non valido.");

        if (! array_key_exists("id", $array))
            throw new InvalidArgumentException("Dato id non trovato.");

        if (! array_key_exists("stato", $array))
            throw new InvalidArgumentException("Dato stato non trovato.");

        return self::make((int) $array["id"], StatoPrevendita::of($array["stato"]));
    }

    /**
     * Identificativo della prevendita.
     * (>0).
     *
     * @var int|NULL
     */
    private $id;

    /**
     * Rappresenta lo stato della prevendita.
     *
     * @var StatoPrevendita
     */
    private $stato;

    private function __construct($id, $stato)
    {
        $this->id = $id;
        $this->stato = $stato;
    }

    /**
     *
     * @return Ambigous <number, NULL>
     */
    public function getId()
    {
        return $this->id;
    }

    /**
     *
     * @return \com\model\db\enum\StatoPrevendita
     */
    public function getStato()
    {
        return $this->stato;
    }
    
    public function getWPrevendita($idEvento, $idPR, $idCliente, $idTipoPrevendita, $codice, $timestampUltimaModifica) : WPrevendita
    {
        return WPrevendita::make(self::getId(), $idEvento, $idPR, $idCliente, $idTipoPrevendita, $codice, self::getStato(), $timestampUltimaModifica);
    }
}

