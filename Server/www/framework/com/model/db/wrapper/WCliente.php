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

class WCliente implements DatabaseWrapper
{

    const NOME_MAX = 150;

    const COGNOME_MAX = 150;

    const TELEFONO_MAX = 80;

    const CODICEFISCALE_MAX = 16;

    const TELEFONO_REGEX = "/\\+(9[976]\\d|8[987530]\\d|6[987]\\d|5[90]\\d|42\\d|3[875]\\d|2[98654321]\\d|9[8543210]|8[6421]|6[6543210]|5[87654321]|4[987654310]|3[9643210]|2[70]|7|1)\\d{1,14}$/";

    /**
     * Converte un array di stringhe in un wrapper.
     *
     * @param array $array
     * @throws InvalidArgumentException i dati dell'array non sono validi oppure l'array stesso non è valido
     * @return WCliente wrapper convertito
     */
    public static function of($array)
    {
        if (is_null($array) || ! is_array($array))
            throw new InvalidArgumentException("Array nullo o non valido.");

        if (! array_key_exists("id", $array))
            throw new InvalidArgumentException("Dato id non trovato.");

        if (! array_key_exists("idStaff", $array))
            throw new InvalidArgumentException("Dato idStaff non trovato.");

        if (! array_key_exists("nome", $array))
            throw new InvalidArgumentException("Dato nome non trovato.");

        if (! array_key_exists("cognome", $array))
            throw new InvalidArgumentException("Dato cognome non trovato.");

        if (! array_key_exists("telefono", $array))
			$array["telefono"] = null;
            //throw new InvalidArgumentException("Dato telefono non trovato.");

        if (! array_key_exists("dataDiNascita", $array))
            $array["dataDiNascita"] = null;
            //throw new InvalidArgumentException("Dato dataDiNascita non trovato.");

        if (! array_key_exists("codiceFiscale", $array))
			$array["codiceFiscale"] = null;
            //throw new InvalidArgumentException("Dato codiceFiscale non trovato.");

        if (! array_key_exists("timestampInserimento", $array))
            throw new InvalidArgumentException("Dato timestampInserimento non trovato.");

        $tmpDataDiNascita = !is_null($array["dataDiNascita"]) ? new DateTimeImmutableAdapterJSON(\DateTimeImmutable::createFromFormat(DateTimeImmutableAdapterJSON::MYSQL_DATE, $array["dataDiNascita"])) : NULL;

        return self::make((int) $array["id"], (int) $array["idStaff"], $array["nome"], $array["cognome"], $array["telefono"], $tmpDataDiNascita, $array["codiceFiscale"], new DateTimeImmutableAdapterJSON(\DateTimeImmutable::createFromFormat(DateTimeImmutableAdapterJSON::MYSQL_TIMESTAMP, $array["timestampInserimento"])));
    }

    /**
     * Metodo factory con controlli.
     *
     * @param int $id
     * @param int $idStaff
     * @param string $nome
     * @param string $cognome
     * @param string $telefono
     * @param DateTimeImmutableAdapterJSON $dataDiNascita
     * @param string $codiceFiscale
     * @param DateTimeImmutableAdapterJSON $timestampInserimento
     * @throws InvalidArgumentException
     * @return \com\model\db\wrapper\WCliente
     */
    public static function make($id, $idStaff, $nome, $cognome, $telefono, $dataDiNascita, $codiceFiscale, $timestampInserimento)
    {
        if (is_null($id) ||  is_null($nome) || is_null($cognome) ||  is_null($idStaff) || /*is_null($dataDiNascita) || */ is_null($timestampInserimento))
            throw new InvalidArgumentException("Uno o più parametri nulli");

        if (! is_int($id) || ! is_int($idStaff) || (!is_null($nome) && ! is_string($nome)) ||  (! is_null($cognome) && ! is_string($cognome)) || (! is_null($dataDiNascita) && ! ($dataDiNascita instanceof DateTimeImmutableAdapterJSON)) || ! ($timestampInserimento instanceof DateTimeImmutableAdapterJSON))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");

        if (! is_null($telefono)) {
            if (! is_string($telefono))
                throw new InvalidArgumentException("Telefono non del tipo giusto");

            if (strlen($telefono) > self::TELEFONO_MAX)
                throw new InvalidArgumentException("Telefono non valido (MAX)");

            // if (preg_match(self::TELEFONO_REGEX, $telefono) !== 1)
            // throw new InvalidArgumentException("Telefono non valido (REGEX)");
        }

        if (! is_null($codiceFiscale)) {
            if (! is_string($codiceFiscale))
                throw new InvalidArgumentException("Codice fiscale non del tipo giusto");

            if (strlen($codiceFiscale) > self::CODICEFISCALE_MAX)
                throw new InvalidArgumentException("Codice fiscale non valido (MAX)");
        }

        if (strlen($nome) > self::NOME_MAX && !is_null($nome))
            throw new InvalidArgumentException("Nome non valido (MAX)");

        if (strlen($cognome) > self::COGNOME_MAX && !is_null($cognome))
            throw new InvalidArgumentException("Cognome non valido (MAX)");

        if ($id <= 0)
            throw new InvalidArgumentException("ID non valido");

        if ($idStaff <= 0)
            throw new InvalidArgumentException("ID Staff non valido");

        // Imposto il formato data per la data di nascita.
        if(!is_null($dataDiNascita))
            $dataDiNascita->setFormatType(DateTimeImmutableAdapterJSON::MYSQL_DATE);

        return new WCliente($id, $idStaff, $nome, $cognome, $telefono, $dataDiNascita, $codiceFiscale, $timestampInserimento);
    }

    public static function makeNoChecks($id, $idStaff, $nome, $cognome, $telefono, $dataDiNascita, $codiceFiscale, $timestampInserimento)
    {
        return new WCliente($id, $idStaff, $nome, $cognome, $telefono, $dataDiNascita, $codiceFiscale, $timestampInserimento);
    }

    /**
     * Identificativo del cliente.
     * (>0).
     *
     * @var int
     */
    private $id;

    /**
     * Identificativo dello staff associato al cliente.
     * (>0).
     *
     * @var int
     */
    private $idStaff;

    /**
     * Nome del cliente.
     *
     * @var string
     */
    private $nome;

    /**
     * Cognome del cliente.
     *
     * @var string
     */
    private $cognome;

    /**
     * telefono del cliente in formato esteso (con prefisso) (OPZIONALE).
     *
     * @var string|NULL
     */
    private $telefono;

    /**
     * Data di nascita del cliente.
     *
     * @var DateTimeImmutableAdapterJSON | NULL
     */
    private $dataDiNascita;

    /**
     * Codice fiscale del cliente, utilizzato per una ricerca più veloce.
     *
     * @var string|NULL
     */
    private $codiceFiscale;

    /**
     * Timestamp di inserimento del cliente.
     *
     * @var DateTimeImmutableAdapterJSON
     */
    private $timestampInserimento;

    protected function __construct($id, $idStaff, $nome, $cognome, $telefono, $dataDiNascita, $codiceFiscale, $timestampInserimento)
    {
        $this->id = $id;
        $this->idStaff = $idStaff;
        $this->nome = $nome;
        $this->cognome = $cognome;
        $this->telefono = $telefono;
        $this->dataDiNascita = $dataDiNascita;
        $this->codiceFiscale = $codiceFiscale;
        $this->timestampInserimento = $timestampInserimento;
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
    public function getCognome()
    {
        return $this->cognome;
    }

    /**
     *
     * @return boolean|string Restituisce FALSE se non c'è il telefono.
     */
    public function getTelefono()
    {
        return ! is_null($this->telefono) ? $this->telefono : FALSE;
    }

    /**
     *
     * @return DateTimeImmutableAdapterJSON|boolean Restituisce FALSE se non c'è la DataDiNascita.
     */
    public function getDataDiNascita()
    {
        return ! is_null($this->dataDiNascita) ? $this->dataDiNascita : FALSE;
    }

    /**
     *
     * @return boolean|string Restituisce FALSE se non c'è il codiceFiscale.
     */
    public function getCodiceFiscale()
    {
        return ! is_null($this->codiceFiscale) ? $this->codiceFiscale : FALSE;
    }

    /**
     *
     * @return DateTimeImmutableAdapterJSON
     */
    public function getTimestampInserimento()
    {
        return $this->timestampInserimento;
    }

    /**
     * Restituisce un nuovo wrapper con l'id modificato.
     *
     * @param int $id
     * @throws \InvalidArgumentException
     * @return \com\model\db\wrapper\WCliente
     */
    public function changeId($id)
    {
        if (is_null($id))
            throw new InvalidArgumentException("parametro ID nullo");

        if (! is_int($id))
            throw new InvalidArgumentException("Parametro ID non int.");

        if ($id <= 0)
            throw new InvalidArgumentException("ID non valido");

        return new WCliente($id, $this->idStaff, $this->nome, $this->cognome, $this->telefono, $this->dataDiNascita, $this->codiceFiscale, $this->timestampInserimento);
    }

    public function changeTimestampInserimento($timestampInserimento)
    {
        if (is_null($timestampInserimento))
            throw new InvalidArgumentException("parametro timestampInserimento nullo");

        if (! ($timestampInserimento instanceof DateTimeImmutableAdapterJSON))
            throw new InvalidArgumentException("parametro timestampInserimento è del tipo giusto.");

        return new WCliente($this->id, $this->idStaff, $this->nome, $this->cognome, $this->telefono, $this->dataDiNascita, $this->codiceFiscale, $timestampInserimento);
    }
}

