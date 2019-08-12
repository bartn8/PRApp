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

use com\model\db\wrapper\WTipoPrevendita;
use InvalidArgumentException;
use com\model\net\serialize\ArrayDeserializable;
use com\model\net\wrapper\NetWrapper;
use com\utils\DateTimeImmutableAdapterJSON;

class InsertNetWTipoPrevendita implements NetWrapper
{

    /**
     * Metodo factory senza l'id, utlizzato quando si deve inserire un tipo di prevendita.
     *
     * @param int $idEvento
     * @param string $nome
     * @param string $descrizione
     * @param float $prezzo
     * @param DateTimeImmutableAdapterJSON $aperturaVendite
     * @param DateTimeImmutableAdapterJSON $chiusuraVendite
     * @throws InvalidArgumentException
     * @return InsertNetWTipoPrevendita
     */
    private static function make($idEvento, $nome, $descrizione, $prezzo, $aperturaVendite, $chiusuraVendite)
    {
        if (is_null($idEvento) || is_null($nome) || is_null($prezzo) || is_null($aperturaVendite) || is_null($chiusuraVendite))
            throw new InvalidArgumentException("Uno o più parametri nulli");

        if (! is_int($idEvento) || ! is_string($nome) || ! is_string($descrizione) || ! is_float($prezzo) || ! ($aperturaVendite instanceof DateTimeImmutableAdapterJSON) || ! ($chiusuraVendite instanceof DateTimeImmutableAdapterJSON))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");

        if ($idEvento <= 0)
            throw new InvalidArgumentException("ID Evento non valido");

        if (strlen($nome) > WTipoPrevendita::NOME_MAX)
            throw new InvalidArgumentException("Nome non valido (MAX)");

        // Nessun limite sul prezzo (Prevendita omaggio o buono che ne so?)

        if ($aperturaVendite >= $chiusuraVendite)
            throw new InvalidArgumentException("L'apertura e la chisura di vendite non rispettanon i vincoli di tempo.");

        if (strlen($descrizione) > WTipoPrevendita::DESCRIZIONE_MAX)
            throw new InvalidArgumentException("Descrizione non valida (MAX)");

        return new InsertNetWTipoPrevendita($idEvento, $nome, $descrizione, $prezzo, $aperturaVendite, $chiusuraVendite);
    }

    public static function of($array)
    {
        if (is_null($array) || ! is_array($array))
            throw new InvalidArgumentException("Array nullo o non valido.");

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

        $aperturaPrevendite = new DateTimeImmutableAdapterJSON(new \DateTimeImmutable($array["aperturaPrevendite"]));   //ISO8061
        $chiusuraVendite = new DateTimeImmutableAdapterJSON(new \DateTimeImmutable($array["chiusuraPrevendite"]));      //ISO8061

        return self::make((int) $array["idEvento"], $array["nome"], $array["descrizione"], (float) $array["prezzo"], $aperturaPrevendite, $chiusuraVendite);
    }

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
    private $aperturaVendite;

    /**
     * Istante del tempo dal quale la prevendita non è più vendibile.
     *
     * @var DateTimeImmutableAdapterJSON
     */
    private $chiusuraVendite;

    private function __construct($idEvento, $nome, $descrizione, $prezzo, $aperturaVendite, $chiusuraVendite)
    {
        $this->idEvento = $idEvento;
        $this->nome = $nome;
        $this->descrizione = $descrizione;
        $this->prezzo = $prezzo;
        $this->aperturaVendite = $aperturaVendite;
        $this->chiusuraVendite = $chiusuraVendite;
    }

    /**
     *
     * @return Ambigous <number, NULL>
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
    public function getAperturaVendite()
    {
        return $this->aperturaVendite;
    }

    /**
     *
     * @return DateTimeImmutableAdapterJSON
     */
    public function getChiusuraVendite()
    {
        return $this->chiusuraVendite;
    }

    public function getWTipoPrevendita($id, $idModificatore, $timestampUltimaModifica): WTipoPrevendita
    {
        return WTipoPrevendita::make($id, self::getIdEvento(), self::getNome(), self::getDescrizione(), self::getPrezzo(), self::getAperturaVendite(), self::getChiusuraVendite(), $idModificatore, $timestampUltimaModifica);
    }
}

