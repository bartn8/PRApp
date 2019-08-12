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
use com\model\db\enum\StatoPrevendita;
use com\model\net\serialize\ArrayDeserializable;
use com\utils\DateTimeImmutableAdapterJSON;

class WPrevendita implements DatabaseWrapper
{

    const CODICE_MAX = 10;

    /**
     * Meotodo factory.
     *
     * @param int $id
     * @param int $idEvento
     * @param int $idPR
     * @param int $idCliente
     * @param int $idTipoPrevendita
     * @param string $codice
     * @param StatoPrevendita $stato
     * @throws InvalidArgumentException
     * @return \com\model\db\wrapper\WPrevendita
     */
    public static function make($id, $idEvento, $idPR, $idCliente, $idTipoPrevendita, $codice, $stato, $timestampUltimaModifica)
    {
        if (is_null($id) || is_null($idEvento) || is_null($idPR) || is_null($idTipoPrevendita) || is_null($codice) || is_null($stato) || is_null($timestampUltimaModifica))
            throw new InvalidArgumentException("Uno o più parametri nulli");

        if (! is_int($id) || ! is_int($idEvento) || ! is_int($idPR) || ! is_int($idTipoPrevendita) || ! ($stato instanceof StatoPrevendita) || ! ($timestampUltimaModifica instanceof DateTimeImmutableAdapterJSON))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");

        if (! is_null($idCliente)) {
            if (! is_int($idCliente))
                throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");

            if ($idCliente <= 0)
                throw new InvalidArgumentException("ID Cliente non valido: ".$idCliente);
        }

        if (strlen($codice) > self::CODICE_MAX)
            throw new InvalidArgumentException("Codice non valido (MAX)");

        if ($id <= 0)
            throw new InvalidArgumentException("ID non valido");

        if ($idEvento <= 0)
            throw new InvalidArgumentException("ID Evento non valido");

        if ($idPR <= 0)
            throw new InvalidArgumentException("ID PR non valido");

        if ($idTipoPrevendita <= 0)
            throw new InvalidArgumentException("ID Tipo Prevendita non valido");

        return new WPrevendita($id, $idEvento, $idPR, $idCliente, $idTipoPrevendita, $codice, $stato, $timestampUltimaModifica);
    }

    /**
     * Converte un array di stringhe in un wrapper.
     *
     * @param array $array
     * @throws InvalidArgumentException i dati dell'array non sono validi oppure l'array stesso non è valido
     * @throws \com\model\exception\ParseException
     * @return WPrevendita wrapper convertito
     */
    public static function of($array)
    {
        if (is_null($array) || ! is_array($array))
            throw new InvalidArgumentException("Array nullo o non valido.");
        if (! array_key_exists("id", $array))
            throw new InvalidArgumentException("Dato id non trovato.");

        if (! array_key_exists("idEvento", $array))
            throw new InvalidArgumentException("Dato idEvento non trovato.");

        if (! array_key_exists("idPR", $array))
            throw new InvalidArgumentException("Dato idPR non trovato.");

        if (! array_key_exists("idCliente", $array))
			$array["idCliente"] = null;
            //throw new InvalidArgumentException("Dato idCliente non trovato.");

        if (! array_key_exists("idTipoPrevendita", $array))
            throw new InvalidArgumentException("Dato idTipoPrevendita non trovato.");

        if (! array_key_exists("codice", $array))
            throw new InvalidArgumentException("Dato codice non trovato.");

        if (! array_key_exists("stato", $array))
            throw new InvalidArgumentException("Dato stato non trovato.");

        if (! array_key_exists("timestampUltimaModifica", $array))
            throw new InvalidArgumentException("Dato timestampUltimaModifica non trovato.");

        $idCliente = is_null($array["idCliente"]) ? NULL : (int) $array["idCliente"];

        return self::make((int) $array["id"], (int) $array["idEvento"], (int) $array["idPR"], $idCliente, (int) $array["idTipoPrevendita"], $array["codice"], StatoPrevendita::parse($array["stato"]), new DateTimeImmutableAdapterJSON(\DateTimeImmutable::createFromFormat(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP, $array["timestampUltimaModifica"])));
    }

    /**
     * Identificativo della prevendita.
     * (>0).
     *
     * @var int
     */
    private $id;

    /**
     * Identificativo dell'evento.
     * (>0).
     *
     * @var int
     */
    private $idEvento;

    /**
     * Identificativo del PR.
     * (>0).
     *
     * @var int
     */
    private $idPR;

    /**
     * Identificativo del cliente che ha comprato la prevendita.
     * (>0). (OZPTIONALE)
     *
     * @var int|NULL
     */
    private $idCliente;

    /**
     * Identificativo del tipo della prevendita.(>0).
     *
     * @var int
     */
    private $idTipoPrevendita;

    /**
     * Codice di verifica della prevendita.
     *
     * @var string
     */
    private $codice;

    /**
     * Rappresenta lo stato della prevendita.
     *
     * @var StatoPrevendita
     */
    private $stato;

    /**
     * Indica l'ultima modifica.
     *
     * @var DateTimeImmutableAdapterJSON
     */
    private $timestampUltimaModifica;

    protected function __construct($id, $idEvento, $idPR, $idCliente, $idTipoPrevendita, $codice, $stato, $timestampUltimaModifica)
    {
        $this->id = $id;
        $this->idEvento = $idEvento;
        $this->idPR = $idPR;
        $this->idCliente = $idCliente;
        $this->idTipoPrevendita = $idTipoPrevendita;
        $this->codice = $codice;
        $this->stato = $stato;
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
    public function getIdEvento()
    {
        return $this->idEvento;
    }

    /**
     *
     * @return number
     */
    public function getIdPR()
    {
        return $this->idPR;
    }

    /**
     *
     * @return number|boolean
     */
    public function getIdCliente()
    {
        return is_null($this->idCliente) ? NULL : $this->idCliente;
    }

    /**
     *
     * @return number
     */
    public function getIdTipoPrevendita()
    {
        return $this->idTipoPrevendita;
    }

    /**
     *
     * @return string
     */
    public function getCodice()
    {
        return $this->codice;
    }

    /**
     *
     * @return \com\model\db\enum\StatoPrevendita
     */
    public function getStato()
    {
        return $this->stato;
    }

    /**
     *
     * @return DateTimeImmutableAdapterJSON
     */
    public function getTimestampUltimaModifica()
    {
        return $this->timestampUltimaModifica;
    }

    /**
     * Modifica l'identificativo della prevendita da utilizzare solo per inserimento.
     *
     * @param int $id
     * @throws InvalidArgumentException
     * @return \com\model\db\wrapper\WPrevendita
     */
    public function changeId($id)
    {
        if (is_null($id))
            throw new InvalidArgumentException("Uno o più parametri nulli");

        if (! is_int($id))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");

        if ($id <= 0)
            throw new InvalidArgumentException("ID non valido");

        return new WPrevendita($id, $this->idEvento, $this->idPR, $this->idCliente, $this->idTipoPrevendita, $this->codice, $this->stato, $this->timestampUltimaModifica);
    }

    /**
     * Modifica il tipo di prevendita.
     *
     * @param int $idTipoPrevendita
     * @throws InvalidArgumentException
     * @return \com\model\db\wrapper\WPrevendita
     */
    public function changeTipoPrevendita($idTipoPrevendita)
    {
        if (is_null($idTipoPrevendita))
            throw new InvalidArgumentException("Uno o più parametri nulli");

        if (! is_int($idTipoPrevendita))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");

        if ($idTipoPrevendita <= 0)
            throw new InvalidArgumentException("ID Tipo Prevendita non valido");

        return new WPrevendita($this->id, $this->idEvento, $this->idPR, $this->idCliente, $idTipoPrevendita, $this->codice, $this->stato, $this->timestampUltimaModifica);
    }

    /**
     * Cambia lo stato attuale della prevendita.
     *
     * @param StatoPrevendita $stato
     * @throws InvalidArgumentException
     * @return \com\model\db\wrapper\WPrevendita
     */
    public function changeStato($stato)
    {
        if (is_null($stato))
            throw new InvalidArgumentException("Uno o più parametri nulli");

        if (! ($stato instanceof StatoPrevendita))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");

        return new WPrevendita($this->id, $this->idEvento, $this->idPR, $this->idCliente, $this->idTipoPrevendita, $this->codice, $stato, $this->timestampUltimaModifica);
    }

    /**
     * Cambia il timestamp di modifica.
     *
     * @param DateTimeImmutableAdapterJSON $timestampUltimaModifica
     * @throws InvalidArgumentException
     * @return \com\model\db\wrapper\WPrevendita
     */
    public function changeTimestampUltimaModifica($timestampUltimaModifica)
    {
        if (is_null($timestampUltimaModifica))
            throw new InvalidArgumentException("Parametro timestampUltimaModifica nullo!");

        if (! ($timestampUltimaModifica instanceof DateTimeImmutableAdapterJSON))
            throw new InvalidArgumentException("Parametro timestampUltimaModifica non del tipo giusto!");

        return new WPrevendita($this->id, $this->idEvento, $this->idPR, $this->idCliente, $this->idTipoPrevendita, $this->codice, $this->stato, $timestampUltimaModifica);
    }
}

