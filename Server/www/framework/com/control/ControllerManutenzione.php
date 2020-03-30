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

use com\view\printer\Printer;
use com\utils\DateTimeImmutableAdapterJSON;

class ControllerManutenzione extends Controller
{
    // Divisione dei comandi: (1-100 utente) (101-200 membro) (201-300 pr) (301-400 cassiere) (401-500 amministratore) (951-1000 manutenzione)
    public const CMD_ECHO = 951;
    
    public const CMD_TIMESTAMP = 952;
   

    public function __construct($printer, $retriver)
    {
        parent::__construct($printer, $retriver);
    }
    
    public function internalHandle(Command $command, Context $context)
    {
        // Effettuo l'operazione.
        switch ($command->getCommand()) {
            case ControllerManutenzione::CMD_ECHO:
                $this->cmd_echo($command);
                break;
                
            case ControllerManutenzione::CMD_TIMESTAMP:
                $this->cmd_timestamp($command);
                break;
                        
            default:
                break;
        }
        
        // Stampo lo stato.
        switch ($command->getCommand()) {
            case ControllerManutenzione::CMD_ECHO:
            case ControllerManutenzione::CMD_TIMESTAMP:
                parent::getPrinter()->setStatus(Printer::STATUS_OK);
                break;
                
            default:
                parent::getPrinter()->setStatus(Printer::STATUS_NON_TROVATO);
                break;
        }
    }
    
    private function cmd_echo($command)
    {
        //Non fa nulla.
    }
    
    private function cmd_timestamp($command)
    {
        parent::getPrinter()->addResult(new DateTimeImmutableAdapterJSON(new \DateTimeImmutable()));
    }
    
}

