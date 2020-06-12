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

use DateTimeImmutable;
use InvalidArgumentException;
use com\model\db\wrapper\WEvento;
use com\model\db\enum\StatoEvento;
use com\model\net\wrapper\NetWrapper;
use com\utils\DateTimeImmutableAdapterJSON;
use com\model\net\wrapper\update\UpdateNetWEvento;

class UpdateNetWEvento implements NetWrapper
{

    /**
     * Metodo factory utlizzato quando si deve modificare un evento.
     *
     * @param string $descrizione
     * @param DateTimeImmutableAdapterJSON $inizio
     * @param DateTimeImmutableAdapterJSON $fine
     * @param string $indirizzo
     * @param StatoEvento $stato
     * @throws InvalidArgumentException
     * @return UpdateNetWEvento
     */
    public static function make($descrizione, $inizio, $fine, $indirizzo, $stato)
    {
        if (is_null($inizio) || is_null($fine) || is_null($indirizzo) || is_null($stato))
            throw new InvalidArgumentException("Uno o più parametri nulli");

        if ((! is_null($descrizione) && ! is_string($descrizione)) || ! ($inizio instanceof DateTimeImmutableAdapterJSON) || ! ($fine instanceof DateTimeImmutableAdapterJSON) || ! is_string($indirizzo) || ! ($stato instanceof StatoEvento))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");

        if ($id <= 0)
            throw new InvalidArgumentException("ID non valido");

        if (strlen($indirizzo) > WEvento::INDIRIZZO_MAX)
            throw new InvalidArgumentException("Indirizzo non valido (MAX)");

        if (strlen($descrizione) > WEvento::DESCRIZIONE_MAX)
            throw new InvalidArgumentException("Descrizione non valida (MAX)");

        return new UpdateNetWEvento($descrizione, $inizio, $fine, $indirizzo, $stato);
    }

    public static function of($array)
    {
        if (is_null($array) || ! is_array($array))
            throw new InvalidArgumentException("Array nullo o non valido.");

        if (! array_key_exists("descrizione", $array))
            throw new InvalidArgumentException("Dato descrizione non trovato.");

        if (! array_key_exists("inizio", $array))
            throw new InvalidArgumentException("Dato inizio non trovato.");

        if (! array_key_exists("fine", $array))
            throw new InvalidArgumentException("Dato fine non trovato.");

        if (! array_key_exists("indirizzo", $array))
            throw new InvalidArgumentException("Dato indirizzo non trovato.");

        if (! array_key_exists("stato", $array))
            throw new InvalidArgumentException("Dato stato non trovato.");

        return self::make($array["descrizione"], new DateTimeImmutableAdapterJSON(DateTimeImmutable::createFromFormat(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP, $array["inizio"])), new DateTimeImmutableAdapterJSON(DateTimeImmutable::createFromFormat(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP, $array["fine"])), $array["indirizzo"], StatoEvento::of($array["stato"]));
    }

    //Rimosso idEvento: Si prende da evento scelto.

    /**
     * Descrizione dell'evento.
     * (OPZIONALE).
     *
     * @var string
     */
    private $descrizione;

    /**
     * Inizio dell'evento.
     *
     * @var DateTimeImmutableAdapterJSON
     */
    private $inizio;

    /**
     * Fine dell'evento.
     *
     * @var DateTimeImmutableAdapterJSON
     */
    private $fine;

    /**
     * Indirizzo dove si terrà l'evento.
     *
     * @var string
     */
    private $indirizzo;

    /**
     * Stato dell'evento.
     *
     * @var StatoEvento
     */
    private $stato;

    private function __construct($descrizione, $inizio, $fine, $indirizzo, $stato)
    {
        $this->descrizione = $descrizione;
        $this->inizio = $inizio;
        $this->fine = $fine;
        $this->indirizzo = $indirizzo;
        $this->stato = $stato;
    }

    /**
     *
     * @return string|bool
     */
    public function getDescrizione()
    {
        return is_null($this->descrizione) ? FALSE : $this->descrizione;
    }

    /**
     *
     * @return DateTimeImmutableAdapterJSON
     */
    public function getInizio()
    {
        return $this->inizio;
    }

    /**
     *
     * @return DateTimeImmutableAdapterJSON
     */
    public function getFine()
    {
        return $this->fine;
    }

    /**
     *
     * @return string
     */
    public function getIndirizzo()
    {
        return $this->indirizzo;
    }

    /**
     *
     * @return \com\model\db\enum\StatoEvento
     */
    public function getStato()
    {
        return $this->stato;
    }

    public function getWEvento($idEvento, $idStaff, $idCreatore, $nome, $idModificatore, $timestampUltimaModifica) : WEvento
    {
        return WEvento::make($idEvento, $idStaff, $idCreatore, $nome, self::getDescrizione(), self::getInizio(), self::getFine(), self::getIndirizzo(), self::getStato(), $idModificatore, $timestampUltimaModifica);
    }
}

