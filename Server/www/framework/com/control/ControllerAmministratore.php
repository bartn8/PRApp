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

use com\model\db\table\Amministratore;
use com\view\printer\Printer;
use \InvalidArgumentException;

class ControllerAmministratore extends Controller
{

    // Divisione dei comandi: (1-100 utente) (101-200 membro) (201-300 pr) (301-400 cassiere) (401-500 amministratore)
//     const CMD_IS_AMMINISTRATORE = 401;

    const CMD_RIMUOVI_CLIENTE = 402;

    const CMD_AGGIUNGI_EVENTO = 403;

    const CMD_MODIFICA_EVENTO = 404;

    const CMD_AGGIUNGI_TIPO_PREVENDITA = 405;

    const CMD_MODIFICA_TIPO_PREVENDITA = 406;

    const CMD_ELIMINA_TIPO_PREVENDITA = 407;

    const CMD_MODIFICA_DIRITTI_UTENTE = 408;

    const CMD_RESTITUISCI_STATISTICHE_PR = 409;

    const CMD_RESTITUISCI_STATISTICHE_CASSIERE = 410;

    const CMD_RESTITUISCI_STATISTICHE_EVENTO = 411;

    const CMD_RESTITUISCI_PREVENDITE = 412;
    
    const CMD_RIMUOVI_MEMBRO = 413;
    
    const CMD_MODIFICA_CODICE_ACCESSO = 414;

    const CMD_RESTITUISCI_STATISTICHE_PR_EVENTO = 415;

    const CMD_RESTITUISCI_STATISTICHE_CASSIERE_EVENTO = 416;

    public function __construct($printer, $retriver)
    {
        parent::__construct($printer, $retriver);
    }

    public function internalHandle($command)
    {
        switch ($command->getCommand()) {            
            case ControllerAmministratore::CMD_RIMUOVI_CLIENTE:
                $this->cmd_rimuovi_cliente($command);
                break;
            
            case ControllerAmministratore::CMD_AGGIUNGI_EVENTO:
                $this->cmd_aggiungi_evento($command);
                break;
            
            case ControllerAmministratore::CMD_MODIFICA_EVENTO:
                $this->cmd_modifica_evento($command);
                break;
            
            case ControllerAmministratore::CMD_AGGIUNGI_TIPO_PREVENDITA:
                $this->cmd_aggiungi_tipo_prevendita($command);
                break;
            
            case ControllerAmministratore::CMD_MODIFICA_TIPO_PREVENDITA:
                $this->cmd_modifica_tipo_prevendita($command);
                break;
                
            case ControllerAmministratore::CMD_ELIMINA_TIPO_PREVENDITA:
                $this->cmd_elimina_tipo_prevendita($command);
                break;
            
            case ControllerAmministratore::CMD_MODIFICA_DIRITTI_UTENTE:
                $this->cmd_modifica_diritti_utente($command);
                break;
            
            case ControllerAmministratore::CMD_RESTITUISCI_STATISTICHE_PR:
                $this->cmd_restituisci_statistiche_pr($command);
                break;
            
            case ControllerAmministratore::CMD_RESTITUISCI_STATISTICHE_CASSIERE:
                $this->cmd_restituisci_statistiche_cassiere($command);
                break;
            
            case ControllerAmministratore::CMD_RESTITUISCI_STATISTICHE_EVENTO:
                $this->cmd_restituisci_statistiche_evento($command);
                break;
            
            case ControllerAmministratore::CMD_RESTITUISCI_PREVENDITE:
                $this->cmd_restituisci_prevendite($command);
                break;
            
            case ControllerAmministratore::CMD_RIMUOVI_MEMBRO:
                $this->cmd_rimuovi_membro($command);
                break;
                
            case ControllerAmministratore::CMD_MODIFICA_CODICE_ACCESSO:
                $this->cmd_modifica_codice_accesso($command);
                break;

            case ControllerAmministratore::CMD_RESTITUISCI_STATISTICHE_PR_EVENTO:
                $this->cmd_restituisci_statistiche_pr_evento($command);
                break;
            
            case ControllerAmministratore::CMD_RESTITUISCI_STATISTICHE_CASSIERE_EVENTO:
                $this->cmd_restituisci_statistiche_cassiere_evento($command);
                break;
                
            default:
                break;
        }
        
        switch ($command->getCommand()) {
            case ControllerAmministratore::CMD_RIMUOVI_CLIENTE:
            case ControllerAmministratore::CMD_AGGIUNGI_EVENTO:
            case ControllerAmministratore::CMD_MODIFICA_EVENTO:
            case ControllerAmministratore::CMD_AGGIUNGI_TIPO_PREVENDITA:                
            case ControllerAmministratore::CMD_MODIFICA_TIPO_PREVENDITA:
            case ControllerAmministratore::CMD_MODIFICA_DIRITTI_UTENTE:
            case ControllerAmministratore::CMD_RESTITUISCI_STATISTICHE_PR:
            case ControllerAmministratore::CMD_RESTITUISCI_STATISTICHE_CASSIERE:
            case ControllerAmministratore::CMD_RESTITUISCI_STATISTICHE_EVENTO:
            case ControllerAmministratore::CMD_RESTITUISCI_PREVENDITE:
            case ControllerAmministratore::CMD_RIMUOVI_MEMBRO:
            case ControllerAmministratore::CMD_MODIFICA_CODICE_ACCESSO:
            case ControllerAmministratore::CMD_RESTITUISCI_STATISTICHE_PR_EVENTO:
            case ControllerAmministratore::CMD_RESTITUISCI_STATISTICHE_CASSIERE_EVENTO:
                parent::getPrinter()->setStatus(Printer::STATUS_OK);
                break;
                
            default:
                parent::getPrinter()->setStatus(Printer::STATUS_NON_TROVATO);
                break;
        }
    }

    private function cmd_rimuovi_cliente($command)
    {
        if(!array_key_exists("cliente", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        parent::getPrinter()->addResult(Amministratore::rimuoviCliente($command->getArgs()['cliente']->getValue()));
    }

    private function cmd_aggiungi_evento($command)
    {
        if(!array_key_exists("evento", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        parent::getPrinter()->addResult(Amministratore::aggiungiEvento($command->getArgs()['evento']->getValue()));
    }

    private function cmd_modifica_evento($command)
    {
        if(!array_key_exists("evento", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        parent::getPrinter()->addResult(Amministratore::modificaEvento($command->getArgs()['evento']->getValue()));
    }

    private function cmd_aggiungi_tipo_prevendita($command)
    {
        if(!array_key_exists("tipoPrevendita", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        parent::getPrinter()->addResult(Amministratore::aggiungiTipoPrevendita($command->getArgs()['tipoPrevendita']->getValue()));
    }

    private function cmd_modifica_tipo_prevendita($command)
    {
        if(!array_key_exists("tipoPrevendita", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        parent::getPrinter()->addResult(Amministratore::modificaTipoPrevendita($command->getArgs()['tipoPrevendita']->getValue()));
    }
    
    private function cmd_elimina_tipo_prevendita($command)
    {
        if(!array_key_exists("tipoPrevendita", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        parent::getPrinter()->addResult(Amministratore::eliminaTipoPrevendita($command->getArgs()['tipoPrevendita']->getValue()));
    }

    private function cmd_modifica_diritti_utente($command)
    {
        if(!array_key_exists("dirittiUtente", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        Amministratore::modificaDirittiUtente($command->getArgs()['dirittiUtente']->getValue());
    }

    private function cmd_restituisci_statistiche_pr($command)
    {
        if(!array_key_exists("staff", $command->getArgs()) || !array_key_exists("pr", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        parent::getPrinter()->addResult(Amministratore::getStatistichePR($command->getArgs()['pr']->getValue(), $command->getArgs()['staff']->getValue()));
    }

    private function cmd_restituisci_statistiche_cassiere($command)
    {
        if(!array_key_exists("staff", $command->getArgs()) || !array_key_exists("cassiere", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        parent::getPrinter()->addResult(Amministratore::getStatisticheCassiere($command->getArgs()['cassiere']->getValue(), $command->getArgs()['staff']->getValue()));
    }

    private function cmd_restituisci_statistiche_evento($command)
    {
        if(!array_key_exists("evento", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        parent::getPrinter()->addResults(Amministratore::getStatisticheEvento($command->getArgs()['evento']->getValue()));
    }

    private function cmd_restituisci_prevendite($command)
    {
        if(!array_key_exists("evento", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        parent::getPrinter()->addResult(Amministratore::getPrevendite($command->getArgs()['evento']->getValue()));
    }
    
    private function cmd_rimuovi_membro($command)
    {
        if(!array_key_exists("staff", $command->getArgs()) || !array_key_exists("membro", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        parent::getPrinter()->addResult(Amministratore::rimuoviMembro($command->getArgs()['membro']->getValue(), $command->getArgs()['staff']->getValue()));
    }
    
    private function cmd_modifica_codice_accesso($command)
    {
        if(!array_key_exists("staff", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        parent::getPrinter()->addResult(Amministratore::modificaCodiceAccesso($command->getArgs()['staff']->getValue()));
    }

    private function cmd_restituisci_statistiche_pr_evento($command)
    {
        if(!array_key_exists("evento", $command->getArgs()) || !array_key_exists("pr", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        parent::getPrinter()->addResults(Amministratore::getStatistichePREvento($command->getArgs()['evento']->getValue(), $command->getArgs()['pr']->getValue()));
    }

    private function cmd_restituisci_statistiche_cassiere_evento($command)
    {
        if(!array_key_exists("evento", $command->getArgs()) || !array_key_exists("cassiere", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        parent::getPrinter()->addResult(Amministratore::getStatisticheCassiereEvento($command->getArgs()['evento']->getValue(), $command->getArgs()['cassiere']->getValue()));
    }

    private function cmd_restituisci_diritti_utente(Command $command, Context $context)
    {
        if(!array_key_exists("utente", $command->getArgs()) || !array_key_exists("staff", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        parent::getPrinter()->addResult(Membro::getDirittiUtente($command->getArgs()['utente']->getValue(), $command->getArgs()['staff']->getValue()));
    }

}

