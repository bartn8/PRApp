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

use com\model\db\enum\Diritto;
use com\model\db\wrapper\WDirittiUtente;
use InvalidArgumentException;
use com\model\net\serialize\ArrayDeserializable;
use com\model\net\wrapper\NetWrapper;

class UpdateNetWDirittiUtente implements NetWrapper
{

    /**
     * Metodo factory.
     *
     * @param int $idUtente
     * @param int $idStaff
     * @param Diritto[] $diritti
     * @throws InvalidArgumentException
     * @return UpdateNetWDirittiUtente
     */
    private static function make($idUtente, $idStaff, $diritti)
    {
        if (is_null($idUtente) || is_null($idStaff) || is_null($diritti))
            throw new InvalidArgumentException("Uno o pi� parametri nulli");

        if (! is_int($idUtente) || ! is_int($idStaff) || ! is_array($diritti))
            throw new InvalidArgumentException("Uno o pi� parametri non del tipo giusto");

        if ($idUtente <= 0)
            throw new InvalidArgumentException("ID Utente non valido");

        if ($idStaff <= 0)
            throw new InvalidArgumentException("ID Staff non valido");

        foreach ($diritti as $diritto) {
            if (! ($diritto instanceof Diritto))
                throw new InvalidArgumentException("Uno o pi� parametri non del tipo giusto");
        }

        return new UpdateNetWDirittiUtente($idUtente, $idStaff, $diritti);
    }

    public static function of($array)
    {
        if (is_null($array) || ! is_array($array))
            throw new InvalidArgumentException("Array nullo o non valido.");

        if (! array_key_exists("idUtente", $array))
            throw new InvalidArgumentException("Dato id non trovato.");

        if (! array_key_exists("idStaff", $array))
            throw new InvalidArgumentException("Dato id non trovato.");

        if (! array_key_exists("diritti", $array))
            throw new InvalidArgumentException("Dato id non trovato.");

        return self::make((int) $array["idUtente"], (int) $array["idStaff"], Diritto::ofArray($array["diritti"]));
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

    private function __construct($idUtente, $idStaff, $diritti)
    {
        $this->idUtente = $idUtente;
        $this->idStaff = $idStaff;
        $this->diritti = $diritti;
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
     * @return int
     */
    public function getIdStaff()
    {
        return $this->idStaff;
    }

    /**
     *
     * @return Diritto[]
     */
    public function getDiritti()
    {
        return $this->diritti;
    }

    public function getWDirittiUtente()
    {
        return WDirittiUtente::make(self::getIdUtente(), self::getIdStaff(), self::getDiritti());
    }
}

