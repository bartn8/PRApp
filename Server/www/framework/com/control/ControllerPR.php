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

use com\model\db\table\PR;
use com\view\printer\Printer;
use \InvalidArgumentException;

class ControllerPR extends Controller
{

    // Divisione dei comandi: (1-100 utente) (101-200 membro) (201-300 pr) (301-400 cassiere) (401-500 amministratore)
    //const CMD_IS_PR = 201;

    const CMD_AGGIUNGI_CLIENTE = 202;

    const CMD_AGGIUNGI_PREVENDITA = 203;

    const CMD_MODIFICA_PREVENDITA = 204;

    const CMD_RESTITUISCI_PREVENDITE = 205;

    const CMD_RESTITUISCI_STATISTICHE_PR_TOTALI = 206;

    const CMD_RESTITUISCI_STATISTICHE_PR_STAFF = 207;

    const CMD_RESTITUISCI_STATISTICHE_PR_EVENTO = 208;

    const CMD_RESTITUISCI_PREVENDITE_EVENTO = 209;

    public function __construct($printer, $retriver)
    {
        parent::__construct($printer, $retriver);
    }
    
    public function handle($command)
    {
        switch ($command->getCommand()) {
//             case ControllerPR::CMD_IS_PR:
//                 $this->cmd_is_pr($command);
//                 break;
            
            case ControllerPR::CMD_AGGIUNGI_CLIENTE:
                $this->cmd_aggiungi_cliente($command);
                break;
            
            case ControllerPR::CMD_AGGIUNGI_PREVENDITA:
                $this->cmd_aggiungi_prevendita($command);
                break;
            
            case ControllerPR::CMD_MODIFICA_PREVENDITA:
                $this->cmd_modifica_prevendita($command);
                break;
            
            case ControllerPR::CMD_RESTITUISCI_PREVENDITE:
                $this->cmd_restituisci_prevedite($command);
                break;
            
            case ControllerPR::CMD_RESTITUISCI_STATISTICHE_PR_TOTALI:
                $this->cmd_restituisci_statistiche_pr_totali($command);
                break;
            
            case ControllerPR::CMD_RESTITUISCI_STATISTICHE_PR_STAFF:
                $this->cmd_restituisci_statistiche_pr_staff($command);
                break;
            
            case ControllerPR::CMD_RESTITUISCI_STATISTICHE_PR_EVENTO:
                $this->cmd_restituisci_statistiche_pr_evento($command);
                break;
            
            case ControllerPR::CMD_RESTITUISCI_PREVENDITE_EVENTO:
                $this->cmd_restituisci_prevedite_evento($command);
                break;

            default:
                break;
        }
        
        switch ($command->getCommand()) {
//             case ControllerPR::CMD_IS_PR:
            case ControllerPR::CMD_AGGIUNGI_CLIENTE:
            case ControllerPR::CMD_AGGIUNGI_PREVENDITA:
            case ControllerPR::CMD_MODIFICA_PREVENDITA:
            case ControllerPR::CMD_RESTITUISCI_PREVENDITE:
            case ControllerPR::CMD_RESTITUISCI_STATISTICHE_PR_TOTALI:
            case ControllerPR::CMD_RESTITUISCI_STATISTICHE_PR_STAFF:
            case ControllerPR::CMD_RESTITUISCI_STATISTICHE_PR_EVENTO:
            case ControllerPR::CMD_RESTITUISCI_PREVENDITE_EVENTO:
                parent::getPrinter()->setStatus(Printer::STATUS_OK);
                break;
            default:
                parent::getPrinter()->setStatus(Printer::STATUS_NON_TROVATO);
                break;
        }
    }

//     private function cmd_is_pr($command)
//     {
//         if(!array_key_exists("staff", $command->getArgs()))
//         {
//             throw new InvalidArgumentException("Argomenti non validi");
//         }
        
//         parent::getPrinter()->addResult(PR::isPR($command->getArgs()['staff']->getValue()));
//     }

    private function cmd_aggiungi_cliente($command)
    {
        if(!array_key_exists("cliente", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomento cliente non esistente");
        }
        
        parent::getPrinter()->addResult(PR::aggiungiCliente($command->getArgs()['cliente']->getValue()));
    }

    private function cmd_aggiungi_prevendita($command)
    {
        if(!array_key_exists("prevendita", $command->getArgs()))
        {
            throw new \InvalidArgumentException("Argomento prevendita non esistente");
        }
        
        parent::getPrinter()->addResult(PR::aggiungiPrevendita($command->getArgs()['prevendita']->getValue()));
    }

    private function cmd_modifica_prevendita($command)
    {
        if(!array_key_exists("prevendita", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        parent::getPrinter()->addResult(PR::modificaPrevendita($command->getArgs()['prevendita']->getValue()));
    }

    private function cmd_restituisci_prevedite($command)
    {
        if(!array_key_exists("filtri", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        parent::getPrinter()->addResults(PR::getPrevendite($command->getArgs()['filtri']->getValue()));
    }

    private function cmd_restituisci_statistiche_pr_totali($command)
    {
        parent::getPrinter()->addResult(PR::getStatistichePRTotali());
    }

    private function cmd_restituisci_statistiche_pr_staff($command)
    {
        if(!array_key_exists("staff", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        parent::getPrinter()->addResult(PR::getStatistichePRStaff($command->getArgs()['staff']->getValue()));
    }

    private function cmd_restituisci_statistiche_pr_evento($command)
    {
        if(!array_key_exists("evento", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        parent::getPrinter()->addResults(PR::getStatistichePREvento($command->getArgs()['evento']->getValue()));
    }

    private function cmd_restituisci_prevedite_evento($command)
    {
        if(!array_key_exists("evento", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        parent::getPrinter()->addResults(PR::getPrevenditeEvento($command->getArgs()['evento']->getValue()));
    }
}

