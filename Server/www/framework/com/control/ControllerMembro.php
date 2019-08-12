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

use com\model\db\table\Membro;
use com\view\printer\Printer;
use \InvalidArgumentException;

class ControllerMembro extends Controller
{

    // Divisione dei comandi: (1-100 utente) (101-200 membro) (201-300 pr) (301-400 cassiere) (401-500 amministratore)
//     const CMD_IS_MEMBRO = 101;

    const CMD_RESTITUISCI_LISTA_UTENTI = 102;

    const CMD_RESTITUISCI_DIRITTI_PERSONALI = 103;

    const CMD_RESTITUISCI_DIRITTI_UTENTE = 104;

    const CMD_RESTITUISCI_LISTA_EVENTI = 105;

    const CMD_RESTITUISCI_TIPI_PREVENDITA = 106;

    const CMD_RESTITUISCI_LISTA_CLIENTI = 107;

    public function __construct($printer, $retriver)
    {
        parent::__construct($printer, $retriver);
    }

    public function handle($command)
    {
        
        switch ($command->getCommand()) {
//             case ControllerMembro::CMD_IS_MEMBRO:
//                 $this->cmd_is_membro($command);
//                 break;
            
            case ControllerMembro::CMD_RESTITUISCI_LISTA_UTENTI:
                $this->cmd_restituisci_lista_utenti($command);
                break;
            
            case ControllerMembro::CMD_RESTITUISCI_DIRITTI_PERSONALI:
                $this->cmd_restiutisci_diritti_personali($command);
                break;
            
            case ControllerMembro::CMD_RESTITUISCI_DIRITTI_UTENTE:
                $this->cmd_restituisci_diritti_utente($command);
                break;
            
            case ControllerMembro::CMD_RESTITUISCI_LISTA_EVENTI:
                $this->cmd_restituisci_lista_eventi($command);
                break;
            
            case ControllerMembro::CMD_RESTITUISCI_TIPI_PREVENDITA:
                $this->cmd_restituisci_tipi_prevendita($command);
                break;
            
            case ControllerMembro::CMD_RESTITUISCI_LISTA_CLIENTI:
                $this->cmd_restituisci_lista_clienti($command);
                break;
            
            default:
                break;
        }
        
        switch ($command->getCommand()) {
//             case ControllerMembro::CMD_IS_MEMBRO:
            case ControllerMembro::CMD_RESTITUISCI_LISTA_UTENTI:
            case ControllerMembro::CMD_RESTITUISCI_DIRITTI_PERSONALI:
            case ControllerMembro::CMD_RESTITUISCI_DIRITTI_UTENTE:
            case ControllerMembro::CMD_RESTITUISCI_LISTA_EVENTI:
            case ControllerMembro::CMD_RESTITUISCI_TIPI_PREVENDITA:
            case ControllerMembro::CMD_RESTITUISCI_LISTA_CLIENTI:
                parent::getPrinter()->setStatus(Printer::STATUS_OK);
                break;
            
            default:
                parent::getPrinter()->setStatus(Printer::STATUS_NON_TROVATO);
                break;
        }
        
    }

//     private function cmd_is_membro($command)
//     {
//         if(!array_key_exists("staff", $command->getArgs()))
//         {
//             throw new InvalidArgumentException("Argomenti non validi");
//         }
        
//         parent::getPrinter()->addResult(Membro::isMembro($command->getArgs()['staff']->getValue()));
//     }
    
    private function cmd_restituisci_lista_utenti($command)
    {
        if(!array_key_exists("staff", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        parent::getPrinter()->addResults(Membro::getListaUtenti($command->getArgs()['staff']->getValue()));
    }

    private function cmd_restiutisci_diritti_personali($command)
    {
        if(!array_key_exists("staff", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        parent::getPrinter()->addResult(Membro::getDirittiPersonali($command->getArgs()['staff']->getValue()));
    }

    private function cmd_restituisci_diritti_utente($command)
    {
        if(!array_key_exists("utente", $command->getArgs()) || !array_key_exists("staff", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        parent::getPrinter()->addResult(Membro::getDirittiUtente($command->getArgs()['utente']->getValue(), $command->getArgs()['staff']->getValue()));
    }

    private function cmd_restituisci_lista_eventi($command)
    {
        if(!array_key_exists("staff", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        $risultati = Membro::getListaEventi($command->getArgs()['staff']->getValue());

//        var_dump($risultati);

        parent::getPrinter()->addResults($risultati);

        
//        echo "\n------------------------------\n";

//        var_dump(parent::getPrinter());

    }

    private function cmd_restituisci_tipi_prevendita($command)
    {
        if(!array_key_exists("evento", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        parent::getPrinter()->addResults(Membro::getTipiPrevendita($command->getArgs()['evento']->getValue()));
    }

    private function cmd_restituisci_lista_clienti($command)
    {
        if(!array_key_exists("staff", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        parent::getPrinter()->addResults(Membro::getListaClienti($command->getArgs()['staff']->getValue()));
    }
}

