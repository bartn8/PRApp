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

use com\model\db\wrapper\WTipoPrevendita;
use com\model\net\wrapper\NetWrapper;
use InvalidArgumentException;
use com\model\net\serialize\ArrayDeserializable;
use com\utils\DateTimeImmutableAdapterJSON;

class UpdateNetWTipoPrevendita implements NetWrapper
{

    /**
     * Metodo factory per la modifica di un tipo di prevendita.
     *
     * @param string $nome
     * @param string $descrizione
     * @param float $prezzo
     * @param DateTimeImmutableAdapterJSON $aperturaPrevendite
     * @param DateTimeImmutableAdapterJSON $chiusuraPrevendite
     * @throws InvalidArgumentException
     * @return UpdateNetWTipoPrevendita
     */
    private static function make($id, $nome, $descrizione, $prezzo, $aperturaPrevendite, $chiusuraPrevendite)
    {
        if (is_null($id) || is_null($nome) || is_null($prezzo) || is_null($aperturaPrevendite) || is_null($chiusuraPrevendite))
            throw new InvalidArgumentException("Uno o più parametri nulli");

            if (! is_int($id) || ! is_string($nome) || (/*!is_null($descrizione) &&*/ !is_string($descrizione)) || ! is_float($prezzo) || ! ($aperturaPrevendite instanceof DateTimeImmutableAdapterJSON) || ! ($chiusuraPrevendite instanceof DateTimeImmutableAdapterJSON))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");

        if ($id <= 0)
            throw new InvalidArgumentException("ID non valido");

        if (strlen($nome) == 0)
            throw new InvalidArgumentException("Nome non valido (=0)");

        if (strlen($nome) > WTipoPrevendita::NOME_MAX)
            throw new InvalidArgumentException("Nome non valido (MAX)");

        // Nessun limite sul prezzo (Prevendita omaggio o buono che ne so?)

        if ($aperturaPrevendite >= $chiusuraPrevendite)
            throw new InvalidArgumentException("L'apertura e la chisura di vendite non rispettanon i vincoli di tempo.");

        if (strlen($descrizione) > WTipoPrevendita::DESCRIZIONE_MAX)
            throw new InvalidArgumentException("Descrizione non valida (MAX)");

        return new UpdateNetWTipoPrevendita($id, $nome, $descrizione, $prezzo, $aperturaPrevendite, $chiusuraPrevendite);
    }

    public static function of($array)
    {
        if (is_null($array) || ! is_array($array))
            throw new InvalidArgumentException("Array nullo o non valido.");

        if (! array_key_exists("id", $array))
            throw new InvalidArgumentException("Dato id non trovato.");

        if (! array_key_exists("nome", $array))
            throw new InvalidArgumentException("Dato nome non trovato.");

        if (! array_key_exists("descrizione", $array))
            //$array["descrizione"] = NULL;
            throw new InvalidArgumentException("Dato descrizione non trovato.");

        if (! array_key_exists("prezzo", $array))
            throw new InvalidArgumentException("Dato prezzo non trovato.");

        if (! array_key_exists("aperturaPrevendite", $array))
            throw new InvalidArgumentException("Dato aperturaPrevendite non trovato."); // TODO modificare aperturaPrevendite

        if (! array_key_exists("chiusuraPrevendite", $array))
            throw new InvalidArgumentException("Dato chiusuraPrevendite non trovato.");

        $aperturaPrevendite = new DateTimeImmutableAdapterJSON(new \DateTimeImmutable($array["aperturaPrevendite"]));   //ISO8061
        $chiusuraPrevendite = new DateTimeImmutableAdapterJSON(new \DateTimeImmutable($array["chiusuraPrevendite"]));   //ISO8061

        return self::make((int) $array["id"], $array["nome"], $array["descrizione"], (float) $array["prezzo"], $aperturaPrevendite, $chiusuraPrevendite);
    }

    /**
     * Identificativo del tipo di prenveidta.
     * (>0).
     *
     * @var int|NULL
     */
    private $id;

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

    private function __construct($id, $nome, $descrizione, $prezzo, $aperturaPrevendite, $chiusuraPrevendite)
    {
        $this->id = $id;
        $this->nome = $nome;
        $this->descrizione = $descrizione;
        $this->prezzo = $prezzo;
        $this->aperturaPrevendite = $aperturaPrevendite;
        $this->chiusuraPrevendite = $chiusuraPrevendite;
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
    
    public function getWTipoPrevendita($idEvento, $idModificatore, $timestampUltimaModificaString) : WTipoPrevendita
    {
        $timestampUltimaModifica = new DateTimeImmutableAdapterJSON(\DateTimeImmutable::createFromFormat(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP, $timestampUltimaModificaString));

        return WTipoPrevendita::makeNoChecks(self::getId(), $idEvento, self::getNome(), self::getDescrizione(), self::getPrezzo(), self::getAperturaPrevendite(), self::getChiusuraPrevendite(), $idModificatore, $timestampUltimaModifica);
    }
}

