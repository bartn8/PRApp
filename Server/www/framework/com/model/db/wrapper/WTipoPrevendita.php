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
use ReflectionClass;
use com\model\net\serialize\ArrayDeserializable;
use com\utils\DateTimeImmutableAdapterJSON;

class WTipoPrevendita implements DatabaseWrapper
{

    const NOME_MAX = 150;
    
    CONST DESCRIZIONE_MAX = 500;

    /**
     * Converte un array di stringhe in un wrapper.
     *
     * @param array $array
     * @throws InvalidArgumentException i dati dell'array non sono validi oppure l'array stesso non è valido
     * @return WTipoPrevendita wrapper convertito
     */
    public static function of($array)
    {
        if (is_null($array) || ! is_array($array))
            throw new InvalidArgumentException("Array nullo o non valido.");

        if (! array_key_exists("id", $array))
            throw new InvalidArgumentException("Dato id non trovato.");

        if (! array_key_exists("idEvento", $array))
            throw new InvalidArgumentException("Dato idEvento non trovato.");

        if (! array_key_exists("nome", $array))
            throw new InvalidArgumentException("Dato nome non trovato.");

        if (! array_key_exists("descrizione", $array))
            throw new InvalidArgumentException("Dato descrizione non trovato.");

        if (! array_key_exists("prezzo", $array))
            throw new InvalidArgumentException("Dato prezzo non trovato.");

        if (! array_key_exists("aperturaPrevendite", $array))
            throw new InvalidArgumentException("Dato aperturaPrevendite non trovato.");

        if (! array_key_exists("chiusuraPrevendite", $array))
            throw new InvalidArgumentException("Dato chiusuraPrevendite non trovato.");

        if (! array_key_exists("idModificatore", $array))
            throw new InvalidArgumentException("Dato idModificatore non trovato.");

        if (! array_key_exists("timestampUltimaModifica", $array))
            throw new InvalidArgumentException("Dato timestampUltimaModifica non trovato.");

        $aperturaPrevendite = new DateTimeImmutableAdapterJSON(\DateTimeImmutable::createFromFormat(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP, $array["aperturaPrevendite"]));
        $chiusuraPrevendite = new DateTimeImmutableAdapterJSON(\DateTimeImmutable::createFromFormat(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP, $array["chiusuraPrevendite"]));
        $timestampUltimaModifica = new DateTimeImmutableAdapterJSON(\DateTimeImmutable::createFromFormat(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP, $array["timestampUltimaModifica"]));
        $idModificatore = !is_null($array["idModificatore"]) ? (int) $array["idModificatore"] : NULL;


        return self::make((int) $array["id"], (int) $array["idEvento"], $array["nome"], $array["descrizione"], (float) $array["prezzo"], $aperturaPrevendite, $chiusuraPrevendite, $idModificatore, $timestampUltimaModifica);
    }

    /**
     * Metodo factory con controlli.
     *
     * @param int $id
     * @param int $idEvento
     * @param string $nome
     * @param string $descrizione
     * @param float $prezzo
     * @param DateTimeImmutableAdapterJSON $aperturaPrevendite
     * @param DateTimeImmutableAdapterJSON $chiusuraPrevendite
     * @throws InvalidArgumentException
     * @return WTipoPrevendita
     */
    public static function make($id, $idEvento, $nome, $descrizione, $prezzo, $aperturaPrevendite, $chiusuraPrevendite, $idModificatore, $timestampUltimaModifica)
    {
        if (is_null($id) || is_null($idEvento) || is_null($nome) || is_null($prezzo) || is_null($aperturaPrevendite) || is_null($chiusuraPrevendite) || is_null($timestampUltimaModifica))
            throw new InvalidArgumentException("Uno o più parametri nulli");

        if (! is_int($id) || ! is_int($idEvento) || ! is_string($nome) || ! is_string($descrizione) || ! is_float($prezzo) || ! ($aperturaPrevendite instanceof DateTimeImmutableAdapterJSON) || ! ($chiusuraPrevendite instanceof DateTimeImmutableAdapterJSON) || ! ($timestampUltimaModifica instanceof DateTimeImmutableAdapterJSON) || (! is_null($idModificatore) && ! is_int($idModificatore)))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");

        if ($id <= 0)
            throw new InvalidArgumentException("ID non valido");

        if ($idEvento <= 0)
            throw new InvalidArgumentException("ID Evento non valido");

        if (strlen($nome) == 0)
            throw new InvalidArgumentException("Nome non valido (=0)");

        if (strlen($nome) > self::NOME_MAX)
            throw new InvalidArgumentException("Nome non valido (MAX)");

        if(strlen($descrizione) > self::DESCRIZIONE_MAX)
            throw new InvalidArgumentException("Descrizione non valida (MAX)");
            
        // Nessun limite sul prezzo (Prevendita omaggio o buono che ne so?)

        if ($aperturaPrevendite->getDateTimeImmutable() >= $chiusuraPrevendite->getDateTimeImmutable())
            throw new InvalidArgumentException("L'apertura e la chisura delle prevendite non rispettanon i vincoli di tempo.");

        if (is_int($idModificatore) && $idModificatore <= 0)
            throw new InvalidArgumentException("ID Modificatore non valido");

        return new WTipoPrevendita($id, $idEvento, $nome, $descrizione, $prezzo, $aperturaPrevendite, $chiusuraPrevendite, $idModificatore, $timestampUltimaModifica);
    }

    public static function makeNoChecks($id, $idEvento, $nome, $descrizione, $prezzo, $aperturaPrevendite, $chiusuraPrevendite, $idModificatore, $timestampUltimaModifica)
    {
        return new WTipoPrevendita($id, $idEvento, $nome, $descrizione, $prezzo, $aperturaPrevendite, $chiusuraPrevendite, $idModificatore, $timestampUltimaModifica);
    }

    /**
     * Identificativo del tipo di prenveidta.
     * (>0).
     *
     * @var int|NULL
     */
    private $id;

    /**
     * Identificativo dell'evento associato.
     * (>0).
     *
     * @var int|NULL
     */
    private $idEvento;

    /**
     * Nome del tipo di prevendita.
     *
     * @var string
     */
    private $nome;

    /**
     * Descrizione del tipo di prevendita.
     *
     * @var string
     */
    private $descrizione;

    /**
     * Prezzo della prevendita.
     *
     * @var float
     */
    private $prezzo;

    /**
     * Istante del tempo dal quale la prevendita è vendibile.
     *
     * @var DateTimeImmutableAdapterJSON
     */
    private $aperturaPrevendite;

    /**
     * Istante del tempo dal quale la prevendita non è più vendibile.
     *
     * @var DateTimeImmutableAdapterJSON
     */
    private $chiusuraPrevendite;

    /**
     * Id del modificatore.
     *
     * @var int|NULL
     */
    private $idModificatore;

    /**
     * Timestamp di ultima modifica.
     *
     * @var DateTimeImmutableAdapterJSON
     */
    private $timestampUltimaModifica;

    private function __construct($id, $idEvento, $nome, $descrizione, $prezzo, $aperturaPrevendite, $chiusuraPrevendite, $idModificatore, $timestampUltimaModifica)
    {
        $this->id = $id;
        $this->idEvento = $idEvento;
        $this->nome = $nome;
        $this->descrizione = $descrizione;
        $this->prezzo = $prezzo;
        $this->aperturaPrevendite = $aperturaPrevendite;
        $this->chiusuraPrevendite = $chiusuraPrevendite;
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
     * @return number|NULL
     */
    public function getIdEvento()
    {
        return $this->idEvento;
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
     * @return string
     */
    public function getDescrizione()
    {
        return $this->descrizione;
    }

    /**
     *
     * @return number
     */
    public function getPrezzo()
    {
        return $this->prezzo;
    }

    /**
     *
     * @return DateTimeImmutableAdapterJSON
     */
    public function getAperturaPrevendite()
    {
        return $this->aperturaPrevendite;
    }

    /**
     *
     * @return DateTimeImmutableAdapterJSON
     */
    public function getChiusuraPrevendite()
    {
        return $this->chiusuraPrevendite;
    }

    /**
     *
     * @return int|NULL
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

