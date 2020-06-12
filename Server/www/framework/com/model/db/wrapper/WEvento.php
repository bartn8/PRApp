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

namespace com\model\db\wrapper;

use InvalidArgumentException;
use com\model\db\enum\StatoEvento;
use ReflectionClass;
use com\model\net\serialize\ArrayDeserializable;
use DateTimeImmutable;
use com\utils\DateTimeImmutableAdapterJSON;

class WEvento implements DatabaseWrapper
{

    const NOME_MAX = 150;

    const INDIRIZZO_MAX = 150;

    const DESCRIZIONE_MAX = 500;

    /**
     * Converte un array di stringhe in un wrapper.
     *
     * @param array $array
     * @throws InvalidArgumentException i dati dell'array non sono validi oppure l'array stesso non è valido
     * @throws \com\model\exception\ParseException
     * @return WUtente wrapper convertito
     */
    public static function of($array)
    {
        if (is_null($array) || ! is_array($array))
            throw new InvalidArgumentException("Array nullo o non valido.");

        if (! array_key_exists("id", $array))
            throw new InvalidArgumentException("Dato id non trovato.");

        if (! array_key_exists("idStaff", $array))
            throw new InvalidArgumentException("Dato idStaff non trovato.");

        if (! array_key_exists("idCreatore", $array))
            throw new InvalidArgumentException("Dato idCreatore non trovato.");

        if (! array_key_exists("nome", $array))
            throw new InvalidArgumentException("Dato nome non trovato.");

        if (! array_key_exists("descrizione", $array))
			$array["descrizione"] = null;
            //throw new InvalidArgumentException("Dato descrizione non trovato.");

        if (! array_key_exists("inizio", $array))
            throw new InvalidArgumentException("Dato inizio non trovato.");

        if (! array_key_exists("fine", $array))
            throw new InvalidArgumentException("Dato fine non trovato.");

        if (! array_key_exists("indirizzo", $array))
            throw new InvalidArgumentException("Dato indirizzo non trovato.");

        if (! array_key_exists("stato", $array))
            throw new InvalidArgumentException("Dato stato non trovato.");

        if (! array_key_exists("idModificatore", $array))
            $array["idModificatore"] = null;
            //throw new InvalidArgumentException("Dato idModificatore non trovato.");

        if (! array_key_exists("timestampUltimaModifica", $array))
            throw new InvalidArgumentException("Dato timestampUltimaModifica non trovato.");

        
        $inizio = new DateTimeImmutableAdapterJSON(\DateTimeImmutable::createFromFormat(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP, $array["inizio"]));                                      //MYSQL TIMESTAMP
        $fine = new DateTimeImmutableAdapterJSON(\DateTimeImmutable::createFromFormat(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP, $array["fine"]));                                          //MYSQL TIMESTAMP
        $timestampUltimaModifica = new DateTimeImmutableAdapterJSON(\DateTimeImmutable::createFromFormat(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP, $array["timestampUltimaModifica"]));    //MYSQL TIMESTAMP
        $stato = StatoEvento::parse($array["stato"]);                                                   //INT VALUE
        $idModificatore = is_null($array["idModificatore"]) ? NULL : ((int) $array["idModificatore"]);  //NULL PARSE


        return self::make((int) $array["id"], (int) $array["idStaff"], (int) $array["idCreatore"], $array["nome"], $array["descrizione"], $inizio, $fine, $array["indirizzo"], $stato, $idModificatore, $timestampUltimaModifica);
    }

    /**
     * Metodo factory con controlli.
     *
     * @param int $id
     * @param int $idStaff
     * @param int $idCreatore
     * @param string $nome
     * @param string $descrizione
     * @param \DateTimeImmutableAdapterJSON $inizio
     * @param \DateTimeImmutableAdapterJSON $fine
     * @param string $indirizzo
     * @param string $città
     * @param string $provincia
     * @param string $stato
     * @param StatoEvento $statoEvento
     * @param int $idModificatore
     * @param \DateTimeImmutableAdapterJSON $timestampUltimaModifica
     * @throws InvalidArgumentException
     * @return \com\model\db\wrapper\WEvento
     */
    public static function make($id, $idStaff, $idCreatore, $nome, $descrizione, $inizio, $fine, $indirizzo, $stato, $idModificatore, $timestampUltimaModifica)
    {
        if (is_null($id) || is_null($idStaff) || is_null($idCreatore) || is_null($nome) || is_null($inizio) || is_null($fine) || is_null($indirizzo) || is_null($stato) || is_null($timestampUltimaModifica))
            throw new InvalidArgumentException("Uno o più parametri nulli");

        if (! is_int($id) || ! is_int($idStaff) || ! is_int($idCreatore) || ! is_string($nome) || (! is_null($descrizione) && ! is_string($descrizione)) || ! ($inizio instanceof DateTimeImmutableAdapterJSON) || ! ($fine instanceof DateTimeImmutableAdapterJSON) || ! is_string($indirizzo) || ! ($stato instanceof StatoEvento) || ! ($timestampUltimaModifica instanceof DateTimeImmutableAdapterJSON) || (! is_null($idModificatore) && ! is_int($idModificatore)))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");

        if (strlen($nome) > self::NOME_MAX)
            throw new InvalidArgumentException("Nome non valido (MAX)");

        if (strlen($indirizzo) > self::INDIRIZZO_MAX)
            throw new InvalidArgumentException("Indirizzo non valido (MAX)");

        if ($id <= 0)
            throw new InvalidArgumentException("ID non valido");

        if ($idStaff <= 0)
            throw new InvalidArgumentException("ID Staff non valido");

        if ($idCreatore <= 0)
            throw new InvalidArgumentException("ID Creatore non valido");

        if (! is_null($idModificatore) && $idModificatore <= 0)
            throw new InvalidArgumentException("ID Modificatore non valido");

        if ($inizio->getDateTimeImmutable() > $fine->getDateTimeImmutable())
            throw new InvalidArgumentException("Ordine cronologico date non valido");

        if (!is_null($descrizione)){
			if(strlen($descrizione) > self::DESCRIZIONE_MAX)
				throw new InvalidArgumentException("Descrizione non valida (MAX)");
		}
		

        return new WEvento($id, $idStaff, $idCreatore, $nome, $descrizione, $inizio, $fine, $indirizzo, $stato, $idModificatore, $timestampUltimaModifica);
    }

    public static function makeNoChecks($id, $idStaff, $idCreatore, $nome, $descrizione, $inizio, $fine, $indirizzo, $stato, $idModificatore, $timestampUltimaModifica)
    {
        return new WEvento($id, $idStaff, $idCreatore, $nome, $descrizione, $inizio, $fine, $indirizzo, $stato, $idModificatore, $timestampUltimaModifica);
    }

    /**
     * Identificativo dell'evento.
     * (>0).
     *
     * @var int|NULL
     */
    private $id;

    /**
     * Identificativo dello staff che ha creato l'evento.
     * (>0).
     *
     * @var int|NULL
     */
    private $idStaff;

    /**
     * Identificativo dell'utente che ha creato l'evento.
     *
     * @var int|NULL
     */
    private $idCreatore;

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

    /**
     * Identificativo dell'ultimo amministratore che ha modificato l'evento.
     * (>0).
     *
     * @var int|NULL
     */
    private $idModificatore;

    /**
     * Indica quando è stata effettuata l'ultima modifica.
     *
     * @var DateTimeImmutableAdapterJSON
     */
    private $timestampUltimaModifica;

    protected function __construct($id, $idStaff, $idCreatore, $nome, $descrizione, $inizio, $fine, $indirizzo, $stato, $idModificatore, $timestampUltimaModifica)
    {
        $this->id = $id;
        $this->idStaff = $idStaff;
        $this->idCreatore = $idCreatore;
        $this->nome = $nome;
        $this->descrizione = $descrizione;
        $this->inizio = $inizio;
        $this->fine = $fine;
        $this->indirizzo = $indirizzo;
        $this->stato = $stato;
        $this->idModificatore = $idModificatore;
        $this->timestampUltimaModifica = $timestampUltimaModifica;
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
     *
     * @return number|NULL
     */
    public function getId()
    {
        return $this->id;
    }

    /**
     *
     * @return number
     */
    public function getIdStaff()
    {
        return $this->idStaff;
    }

    /**
     *
     * @return number|NULL
     */
    public function getIdCreatore()
    {
        return $this->idCreatore;
    }

    /**
     *
     * @return string
     */
    public function getNome()
    {
        return $this->nome;
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

    /**
     *
     * @return number|NULL
     */
    public function getIdModificatore()
    {
        return $this->idModificatore;
    }

    /**
     *
     * @return DateTimeImmutableAdapterJSON
     */
    public function getTimestampUltimaModifica()
    {
        return $this->timestampUltimaModifica;
    }

}

