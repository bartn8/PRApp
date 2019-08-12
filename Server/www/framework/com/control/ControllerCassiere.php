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

use com\model\db\table\Cassiere;
use com\view\printer\Printer;
use \InvalidArgumentException;

class ControllerCassiere extends Controller
{

    // Divisione dei comandi: (1-100 utente) (101-200 membro) (201-300 pr) (301-400 cassiere) (401-500 amministratore)
//     const CMD_IS_CASSIERE = 301;

    const CMD_TIMBRA_ENTRATA = 302;

    const CMD_RESTITUISCI_DATI_CLIENTE = 303;

    const CMD_RESTITUISCI_STATISTICHE_CASSIERE_TOTALI = 304;

    const CMD_RESTITUISCI_STATISTICHE_CASSIERE_STAFF = 305;

    const CMD_RESTITUISCI_STATISTICHE_CASSIERE_EVENTO = 306;

    const CMD_RESTITUISCI_ENTRATE_SVOLTE = 307;

    const CMD_RESTITUISCI_PREVENDITE_EVENTO = 308;

    const CMD_RESTITUISCI_INFORMAZIONI_PREVENDITA = 309;

    public function __construct($printer, $retriver)
    {
        parent::__construct($printer, $retriver);
    }

    public function handle($command)
    {
        switch ($command->getCommand()) {
//             case ControllerCassiere::CMD_IS_CASSIERE:
//                 $this->cmd_is_cassiere($command);
//                 break;
            
            case ControllerCassiere::CMD_TIMBRA_ENTRATA:
                $this->cmd_timbra_entrata($command);
                break;
            
            case ControllerCassiere::CMD_RESTITUISCI_DATI_CLIENTE:
                $this->cmd_restituisci_dati_cliente($command);
                break;
            
            case ControllerCassiere::CMD_RESTITUISCI_STATISTICHE_CASSIERE_TOTALI:
                $this->cmd_restituisci_statistiche_cassiere_totali($command);
                break;
            
            case ControllerCassiere::CMD_RESTITUISCI_STATISTICHE_CASSIERE_STAFF:
                $this->cmd_restituisci_statistiche_cassiere_staff($command);
                break;
            
            case ControllerCassiere::CMD_RESTITUISCI_STATISTICHE_CASSIERE_EVENTO:
                $this->cmd_restituisci_statistiche_cassiere_evento($command);
                break;
            
            case ControllerCassiere::CMD_RESTITUISCI_ENTRATE_SVOLTE:
                $this->cmd_restituisci_entrate_svolte($command);
                break;
            
            case ControllerCassiere::CMD_RESTITUISCI_PREVENDITE_EVENTO:
                $this->cmd_restituisci_prevendite_evento($command);
                break;
            
            case ControllerCassiere::CMD_RESTITUISCI_INFORMAZIONI_PREVENDITA:
                $this->cmd_restituisci_informazioni_prevendita($command);
                break;

            default:
                break;
        }
        
        switch ($command->getCommand()) {
//             case ControllerCassiere::CMD_IS_CASSIERE:
            case ControllerCassiere::CMD_TIMBRA_ENTRATA:
            case ControllerCassiere::CMD_RESTITUISCI_DATI_CLIENTE:
            case ControllerCassiere::CMD_RESTITUISCI_STATISTICHE_CASSIERE_TOTALI:
            case ControllerCassiere::CMD_RESTITUISCI_STATISTICHE_CASSIERE_STAFF:
            case ControllerCassiere::CMD_RESTITUISCI_STATISTICHE_CASSIERE_EVENTO:
            case ControllerCassiere::CMD_RESTITUISCI_ENTRATE_SVOLTE:
            case ControllerCassiere::CMD_RESTITUISCI_PREVENDITE_EVENTO:
            case ControllerCassiere::CMD_RESTITUISCI_INFORMAZIONI_PREVENDITA:
                parent::getPrinter()->setStatus(Printer::STATUS_OK);
                break;
            
            default:
                parent::getPrinter()->setStatus(Printer::STATUS_NON_TROVATO);
                break;
        }
    }

//     private function cmd_is_cassiere($command)
//     {
//         if(!array_key_exists("staff", $command->getArgs()))
//         {
//             throw new InvalidArgumentException("Argomenti non validi");
//         }
        
//         parent::getPrinter()->addResult(Cassiere::isCassiere($command->getArgs()['staff']->getValue()));
//     }

    private function cmd_timbra_entrata($command)
    {
        if(!array_key_exists("entrata", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        parent::getPrinter()->addResult(Cassiere::timbraEntrata($command->getArgs()['entrata']->getValue()));
    }

    private function cmd_restituisci_dati_cliente($command)
    {
        if(!array_key_exists("prevendita", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        parent::getPrinter()->addResult(Cassiere::getDatiCliente($command->getArgs()['prevendita']->getValue()));
    }

    private function cmd_restituisci_statistiche_cassiere_totali($command)
    {
        parent::getPrinter()->addResult(Cassiere::getStatisticheCassiereTotali());
    }

    private function cmd_restituisci_statistiche_cassiere_staff($command)
    {
        if(!array_key_exists("staff", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        parent::getPrinter()->addResult(Cassiere::getStatisticheCassiereStaff($command->getArgs()['staff']->getValue()));
    }

    private function cmd_restituisci_statistiche_cassiere_evento($command)
    {
        if(!array_key_exists("evento", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
    
        parent::getPrinter()->addResult(Cassiere::getStatisticheCassiereEvento($command->getArgs()['evento']->getValue()));
    }

    private function cmd_restituisci_entrate_svolte($command)
    {
        if(!array_key_exists("evento", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        parent::getPrinter()->addResults(Cassiere::getEntrateSvolte($command->getArgs()['evento']->getValue()));
    }

    private function cmd_restituisci_prevendite_evento($command)
    {
        if(!array_key_exists("evento", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        parent::getPrinter()->addResult(Cassiere::getPrevenditeEvento($command->getArgs()['evento']->getValue()));
    }

    private function cmd_restituisci_informazioni_prevendita($command)
    {
        if(!array_key_exists("prevendita", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        parent::getPrinter()->addResult(Cassiere::getInformazioniPrevendita($command->getArgs()['prevendita']->getValue()));
    }

}

