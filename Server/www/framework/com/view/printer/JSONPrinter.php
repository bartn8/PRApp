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

use \InvalidArgumentException;

class JsonPrinter implements Printer
{
    
    private $cache;
    private $resultsCache;
    private $exceptionsCache;
    
    private $command;
    private $status;
    
    
    public function __construct()
    {
        $this->cache = array();
        $this->resultsCache = array();
        $this->exceptionsCache = array();
        $this->status = Printer::STATUS_INDEFINITO;
        $this->command = Printer::COMMAND_INDEFINITO;
    }
    
    public function reset()
    {
        $this->cache = array();
    }
    
	public function setHTTPHeader()
	{
		header('Content-Type: application/json; charset=utf-8');
	}
	
    public function resetResults()
    {
        $this->resultsCache = array();
    }
    
    public function resetExceptions()
    {
        $this->exceptionsCache = array();
    }
    
    public function setCommand($cmd)
    {
        if(!is_int($cmd))
        {
            throw new InvalidArgumentException("Command non è intero.");
        }
        
        $this->command = $cmd;
    }
    
    public function setStatus($status)
    {
        if(!is_int($status))
        {
            throw new InvalidArgumentException("Status non è intero.");
        }
        
        $this->status = $status;
    }
    
    public function print($key, $obj)
    {
        if(!is_string($key))
        {
            throw new InvalidArgumentException("Chiave non di tipo stringa");
        }
        
        //if($key === Printer::DATO_COMANDO || $key === Printer::DATO_STATUS) Da scrivere.... per evitare bug infami
        
        $this->cache[$key] = $obj;
    }
    
    public function addResult($obj)
    {
        //Faccio una piccola verifica per evitare scitture inutili.
        if($obj !== NULL)
            \array_push($this->resultsCache, $obj);
    }
    
    public function addResults($array)
    {
        $this->resultsCache = array_merge($this->resultsCache, $array);
    }
    
    public function addException($ex)
    {
        //Line e file aggiunti per deubg.
        \array_push($this->exceptionsCache, array("file" => $ex->getFile(), "line" => $ex->getLine(), "type" => get_class($ex), 'msg'=>$ex->getMessage()));
    }
    
    public function flush()
    {
        $this->cache[Printer::DATO_COMANDO] = $this->command;
        $this->cache[Printer::DATO_STATUS] = $this->status;
        $this->cache[Printer::DATO_RISULTATI] = $this->resultsCache;
        $this->cache[Printer::DATO_ECCEZIONI] = $this->exceptionsCache;
        
        echo \json_encode($this->cache, \JSON_UNESCAPED_UNICODE | \JSON_UNESCAPED_SLASHES);
    }
    
}

