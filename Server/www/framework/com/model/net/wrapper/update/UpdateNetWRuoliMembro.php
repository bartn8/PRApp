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

use com\model\db\enum\Ruolo;
use InvalidArgumentException;
use com\model\net\wrapper\NetWrapper;
use com\model\db\wrapper\WRuoliMembro;
use com\model\net\serialize\ArrayDeserializable;
use com\model\net\wrapper\update\UpdateNetWRuoliMembro;

class UpdateNetWRuoliMembro implements NetWrapper
{

    /**
     * Metodo factory.
     *
     * @param int $idUtente
     * @param Ruolo[] $ruoli
     * @throws InvalidArgumentException
     * @return UpdateNetWRuoliMembro
     */
    private static function make($idUtente, $ruoli)
    {
        if (is_null($idUtente) || is_null($ruoli))
            throw new InvalidArgumentException("Uno o più parametri nulli");

        if (! is_int($idUtente) || ! is_array($ruoli))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");

        if ($idUtente <= 0)
            throw new InvalidArgumentException("ID Utente non valido");

        foreach ($ruoli as $ruolo) {
            if (! ($ruolo instanceof Ruolo))
                throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");
        }

        return new UpdateNetWRuoliMembro($idUtente, $ruoli);
    }

    public static function of($array)
    {
        if (is_null($array) || ! is_array($array))
            throw new InvalidArgumentException("Array nullo o non valido.");

        if (! array_key_exists("idUtente", $array))
            throw new InvalidArgumentException("Dato id non trovato.");

        if (! array_key_exists("ruoli", $array))
            throw new InvalidArgumentException("Dato ruoli non trovato.");

        return self::make((int) $array["idUtente"], Ruolo::ofArray($array["ruoli"]));
    }

    /**
     * Identificativo del membro.
     *
     * @var int
     */
    private $idUtente;

    //Prima c'era idStaff: sostituito dallo staff selezionato

    /**
     * Ruoli del membro nello staff.
     *
     * @var Ruolo[]
     */
    private $ruoli;

    private function __construct($idUtente, $idStaff, $ruoli)
    {
        $this->idUtente = $idUtente;
        $this->idStaff = $idStaff;
        $this->ruoli = $ruoli;
    }

    /**
     *
     * @return int
     */
    public function getIdUtente()
    {
        return $this->idUtente;
    }

    /**
     *
     * @return Ruolo[]
     */
    public function getRuoli()
    {
        return $this->ruoli;
    }

    public function getWRuoliMembro($idStaff)
    {
        return WRuoliMembro::make(self::getIdUtente(), $idStaff, self::getRuoli());
    }
}

