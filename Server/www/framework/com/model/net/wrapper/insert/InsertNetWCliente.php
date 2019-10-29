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

use InvalidArgumentException;
use com\utils\DateTimeImmutableAdapterJSON;
use com\model\net\wrapper\NetWrapper;
use com\model\db\wrapper\WCliente;

class InsertNetWCliente implements NetWrapper
{

    public static function of($array)
    {
        if (is_null($array) || ! is_array($array))
            throw new InvalidArgumentException("Array nullo o non valido.");

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
            throw new InvalidArgumentException("Dato dataDiNascita non trovato.");

        if (! array_key_exists("codiceFiscale", $array))
			$array["codiceFiscale"] = null;
			//throw new InvalidArgumentException("Dato codiceFiscale non trovato.");
		
		$dataDiNascita = !is_null($array["dataDiNascita"]) ? new DateTimeImmutableAdapterJSON(new \DateTimeImmutable($array["dataDiNascita"])) : NULL;   //ISO8061
		//new DateTimeImmutableAdapterJSON(\DateTimeImmutable::createFromFormat(DateTimeImmutableAdapterJSON::MYSQL_DATE, $array["dataDiNascita"]))
		
        return self::make((int) $array["idStaff"], $array["nome"], $array["cognome"], $array["telefono"], $dataDiNascita, $array["codiceFiscale"]);
    }

    /**
     * Metodo factory senza l'id, utlizzato quando si deve inserire un cliente.
     *
     * @param int $idStaff
     * @param string $nome
     * @param string $cognome
     * @param string $telefono
     * @param DateTimeImmutableAdapterJSON $dataDiNascita
     * @param string $codiceFiscale
     * @throws InvalidArgumentException
     * @return InsertNetWCliente
     */
    private static function make($idStaff, $nome, $cognome, $telefono, $dataDiNascita, $codiceFiscale)
    {
        if (is_null($nome) || is_null($cognome) ||  is_null($idStaff) /*|| is_null($dataDiNascita)*/)
            throw new InvalidArgumentException("Uno o più parametri nulli");

        if (! is_int($idStaff) || (!is_null($nome) && ! is_string($nome)) ||  (! is_null($cognome) && ! is_string($cognome)) || (!is_null($dataDiNascita) && ! ($dataDiNascita instanceof DateTimeImmutableAdapterJSON)))
            throw new InvalidArgumentException("Uno o più parametri non del tipo giusto");

        if (! is_null($telefono)) {
            if (! is_string($telefono))
                throw new InvalidArgumentException("Telefono non del tipo giusto");

            if (strlen($telefono) > WCliente::TELEFONO_MAX)
                throw new InvalidArgumentException("Telefono non valido (MAX)");

            if (preg_match(WCliente::TELEFONO_REGEX, $telefono) !== 1)
                throw new InvalidArgumentException("Telefono non valido (REGEX)");
        }

        if (! is_null($codiceFiscale)){
			if(! is_string($codiceFiscale))
				throw new InvalidArgumentException("Codice fiscale non del tipo giusto");
			
			if (strlen($codiceFiscale) > WCliente::CODICEFISCALE_MAX)
				throw new InvalidArgumentException("Codice fiscale non valido (MAX)");
		}
            
        if (strlen($nome) > WCliente::NOME_MAX && !is_null($nome))
            throw new InvalidArgumentException("Nome non valido (MAX)");

        if (strlen($cognome) > WCliente::COGNOME_MAX && !is_null($cognome))
            throw new InvalidArgumentException("Cognome non valido (MAX)");

        if ($idStaff <= 0)
            throw new InvalidArgumentException("ID Staff non valido");

        return new InsertNetWCliente($idStaff, $nome, $cognome, $telefono, $dataDiNascita, $codiceFiscale);
    }

    /**
     *
     * @var int
     */
    private $idStaff;

    /**
     *
     * @var string
     */
    private $nome;

    /**
     *
     * @var string
     */
    private $cognome;

    /**
     *
     * @var string
     */
    private $telefono;

    /**
     *
     * @var DateTimeImmutableAdapterJSON|NULL
     */
    private $dataDiNascita;

    /**
     *
     * @var string
     */
    private $codiceFiscale;

    private function __construct($idStaff, $nome, $cognome, $telefono, $dataDiNascita, $codiceFiscale)
    {
        $this->idStaff = $idStaff;
        $this->nome = $nome;
        $this->cognome = $cognome;
        $this->telefono = $telefono;
        $this->dataDiNascita = $dataDiNascita;
        $this->codiceFiscale = $codiceFiscale;
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
     * @return string
     */
    public function getTelefono()
    {
        return $this->telefono;
    }

    /**
     *
     * @return \com\utils\DateTimeImmutableAdapterJSON
     */
    public function getDataDiNascita()
    {
        return $this->dataDiNascita;
    }

    /**
     *
     * @return string
     */
    public function getCodiceFiscale()
    {
        return $this->codiceFiscale;
    }

}

