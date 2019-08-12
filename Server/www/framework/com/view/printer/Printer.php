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

namespace com\view\printer;

use Exception;

/**
 * Interfaccia per comunicare. Di default lo stato è STATUS_INDEFINITO.
 * 
 * @author Luca Bartolomei
 */
interface Printer
{
    //Possibili dati stampabili
    const DATO_COMANDO = "command";     //Restituisce il comando di richiesta.
    const DATO_STATUS  = "status";      //Indica lo stato dell'operazione.
    const DATO_RISULTATI = "results";    //Risultato dell'operazione.
    const DATO_ECCEZIONI = "exceptions"; //Se lo stato è in eccezione ha come valore l'eccezione.
 
    //Comandi di default.
    const COMMAND_INDEFINITO = -1;
    
    //Stati interni che posso essere stampati.
    const STATUS_INDEFINITO = -1;
    const STATUS_OK = 0;                //Operazione conclusa con successo.
    const STATUS_NON_TROVATO = 1;       //Il comando non è stato trovato.
    const STATUS_ECCEZIONE = 2;         //L'esecuzione del comando ha generato un'eccezione.
    
    /**
     * Resetta il buffer.
     */
    public function reset();
	
	/**
	 * Imposta correttamente degli header aggiuntivi.
	 */
	public function setHTTPHeader();
    
    /**
     * Pulisce il campo dei risultati.
     */
    public function resetResults();
    
    /**
     * Pulisce il campo delle eccezioni.
     */
    public function resetExceptions();
    
    /**
     * Imposta il comando con cui l'utente ha fatto richiesta.
     * @param int $command
     */
    public function setCommand($command);
    
    /**
     * Imposta lo stato di risposta.
     * @param int $status
     */
    public function setStatus($status);
    
    /**
     * Memorizza un oggetto nel buffer, in attesa di stampa. 
     * @param string $key
     * @param object $obj
     */
    public function print($key, $obj);
    
    /**
     * Aggiunge un risultato. Se è un NULL, viene scartato.
     * @param mixed $obj
     */
    public function addResult($obj);
    
    /**
     * Aggiunge una serie di risultati.
     * @param array $array
     */
    public function addResults($array);
        
    /**
     * Aggiunge un'eccezione.
     * @param Exception $ex
     */
    public function addException($ex);
    
    /**
     * Stampa il buffer.
     */
    public function flush();
}

