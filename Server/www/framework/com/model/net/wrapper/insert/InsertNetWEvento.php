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

namespace com\model\net\wrapper\insert;

use com\model\db\enum\StatoEvento;
use com\model\db\wrapper\WEvento;
use com\model\net\wrapper\NetWrapper;
use com\utils\DateTimeImmutableAdapterJSON;
use DateTimeImmutable;
use InvalidArgumentException;

class InsertNetWEvento implements NetWrapper
{

    /**
     * Metodo factory utlizzato quando si deve inserire un evento.
     *
     * @param int $idStaff
     * @param string $nome
     * @param string $descrizione
     * @param DateTimeImmutableAdapterJSON $inizio
     * @param DateTimeImmutableAdapterJSON $fine
     * @param string $indirizzo
     * @param string $provincia
     * @param string $stato
     * @param StatoEvento $statoEvento
     * @throws InvalidArgumentException
     * @return InsertNetWEvento
     */
    private static function make($idStaff, $nome, $descrizione, $inizio, $fine, $indirizzo, $stato)
    {
        if (is_null($idStaff) || is_null($nome) || is_null($inizio) || is_null($fine) || is_null($indirizzo) || is_null($stato))
            throw new InvalidArgumentException("Uno o più parametri nulli");

        if (! is_int($idStaff) || ! is_string($nome) || (! is_null($descrizione) && ! is_string($descrizione)) || ! ($inizio instanceof DateTimeImmutableAdapterJSON) || ! ($fine instanceof DateTimeImmutableAdapterJSON) || ! is_string($indirizzo) || ! ($stato instanceof StatoEvento))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");

        if (strlen($nome) > WEvento::NOME_MAX)
            throw new InvalidArgumentException("Nome non valido (MAX)");

        if (strlen($indirizzo) > WEvento::INDIRIZZO_MAX)
            throw new InvalidArgumentException("Indirizzo non valido (MAX)");

        if ($idStaff <= 0)
            throw new InvalidArgumentException("ID Staff non valido");
        
            if (strlen($descrizione) > WEvento::DESCRIZIONE_MAX)
                throw new InvalidArgumentException("Descrizione non valida (MAX)");

        return new InsertNetWEvento($idStaff, $nome, $descrizione, $inizio, $fine, $indirizzo, $stato);
    }

    public static function of($array)
    {
        if (is_null($array) || ! is_array($array))
            throw new InvalidArgumentException("Array nullo o non valido.");

        if (! array_key_exists("idStaff", $array))
            throw new InvalidArgumentException("Dato idStaff non trovato.");

        if (! array_key_exists("nome", $array))
            throw new InvalidArgumentException("Dato nome non trovato.");

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

        //TODO: da fare su tutti gli insert e update.

        $inizio = new DateTimeImmutableAdapterJSON(new \DateTimeImmutable($array["inizio"]));   //ISO8061
        $fine = new DateTimeImmutableAdapterJSON(new \DateTimeImmutable($array["fine"]));       //ISO8061

        return self::make((int) $array["idStaff"], $array["nome"], $array["descrizione"], $inizio, $fine, $array["indirizzo"], StatoEvento::of($array["stato"]));
    }

    /**
     * Identificativo dello staff che ha creato l'evento.
     * (>0).
     *
     * @var int|NULL
     */
    private $idStaff;

    /**
     * Nome dell'evento.
     *
     * @var string|NULL
     */
    private $nome;

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

    private function __construct($idStaff, $nome, $descrizione, $inizio, $fine, $indirizzo, $stato)
    {
        $this->idStaff = $idStaff;
        $this->nome = $nome;
        $this->descrizione = $descrizione;
        $this->inizio = $inizio;
        $this->fine = $fine;
        $this->indirizzo = $indirizzo;
        $this->stato = $stato;
    }

    /**
     *
     * @return Ambigous <number, NULL>
     */
    public function getIdStaff()
    {
        return $this->idStaff;
    }

    /**
     *
     * @return Ambigous <string, NULL>
     */
    public function getNome()
    {
        return $this->nome;
    }

    /**
     *
     * @return string
     */
    public function getDescrizione()
    {
        return $this->descrizione;
    }

    /**
     *
     * @return \com\utils\DateTimeImmutableAdapterJSON
     */
    public function getInizio()
    {
        return $this->inizio;
    }

    /**
     *
     * @return \com\utils\DateTimeImmutableAdapterJSON
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
    
    public function getWEvento($id, $idCreatore, $idModificatore, $timestampUltimaModifica){
        return WEvento::make($id, self::getIdStaff(), $idCreatore, self::getNome(), self::getDescrizione(), self::getInizio(), self::getFine(), self::getIndirizzo(), self::getStato(), $idModificatore, $timestampUltimaModifica);
    }

}

