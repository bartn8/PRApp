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

namespace com\control;

use com\handler\Retriver;
use com\model\handler\Command;
use com\view\printer\Printer;
// use com\model\handler\Argument;
use \InvalidArgumentException;

abstract class Controller
{
    
    public const CMD_INDEFINITO = -1;

    /**
     * Oggetto per stampare.
     * @var Printer
     */
    private $printer;
    
    /**
     * Oggetto per recuperare argomenti aggiuntivi.
     * @var Retriver
     */
    private $retriver;
        
    protected function __construct($printer, $retriver)
    {
        if(!($printer instanceof Printer))
            throw new InvalidArgumentException("Printer non valido");
        
        if(!($retriver instanceof Retriver))
            throw new InvalidArgumentException("Retriver non valido");
            
        $this->printer = $printer;
        $this->retriver = $retriver;
    }
    
    /**
     * Restituisce la stampante. 
     * @return \com\view\printer\Printer
     */
    protected function getPrinter() 
    {
        return $this->printer;
    }
    
    /**
     * Restitisce il retriver.
     * @return \com\handler\Retriver
     */
    protected function getRetriver() 
    {
        return $this->retriver;        
    }
            
    /**
     * Gestisce un comando.
     *
     * @param Command $command
     * @param array $args Array dei parametri grezzo
     */
    public abstract function handle($command);
            
}

