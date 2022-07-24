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

use com\model\Context;
use com\handler\Command;
use com\control\Controller;
use com\view\printer\Printer;
use \InvalidArgumentException;
use com\model\net\wrapper\NetWId;
use com\model\db\table\Amministratore;
use com\control\ControllerAmministratore;
use com\model\db\exception\AuthorizationException;
use com\model\net\wrapper\insert\InsertNetWEvento;
use com\model\net\wrapper\update\UpdateNetWEvento;
use com\model\net\wrapper\update\UpdateNetWRuoliMembro;
use com\model\db\exception\NotAvailableOperationException;
use com\model\net\wrapper\insert\InsertNetWTipoPrevendita;

class ControllerAmministratore extends Controller
{

    // Divisione dei comandi: (1-100 utente) (101-200 membro) (201-300 pr) (301-400 cassiere) (401-500 amministratore)
//     const CMD_IS_AMMINISTRATORE = 401;

    //Rimosso perché tabella eliminata
    //public const CMD_RIMUOVI_CLIENTE = 402;

    public const CMD_AGGIUNGI_EVENTO = 403;

    public const CMD_MODIFICA_EVENTO = 404;

    public const CMD_AGGIUNGI_TIPO_PREVENDITA = 405;

    public const CMD_MODIFICA_TIPO_PREVENDITA = 406;

    public const CMD_ELIMINA_TIPO_PREVENDITA = 407;

    public const CMD_MODIFICA_RUOLI_MEMBRO = 408;

    public const CMD_RESTITUISCI_STATISTICHE_PR = 409;

    public const CMD_RESTITUISCI_STATISTICHE_CASSIERE = 410;

    public const CMD_RESTITUISCI_STATISTICHE_EVENTO = 411;

    public const CMD_RESTITUISCI_PREVENDITE = 412;
    
    public const CMD_RIMUOVI_MEMBRO = 413;
    
    public const CMD_MODIFICA_CODICE_ACCESSO = 414;

    public const CMD_RESTITUISCI_STATISTICHE_PR_EVENTO = 415;

    public const CMD_RESTITUISCI_STATISTICHE_CASSIERE_EVENTO = 416;

    public const CMD_RESTITUISCI_RUOLI_MEMBRO = 417;

    public function __construct($printer, $retriver)
    {
        parent::__construct($printer, $retriver);
    }

    protected function internalHandle(Command $command, Context $context)
    {
        switch ($command->getCommand()) {                        
            case ControllerAmministratore::CMD_AGGIUNGI_EVENTO:
                $this->cmd_aggiungi_evento($command, $context);
                break;
            
            case ControllerAmministratore::CMD_MODIFICA_EVENTO:
                $this->cmd_modifica_evento($command, $context);
                break;
            
            case ControllerAmministratore::CMD_AGGIUNGI_TIPO_PREVENDITA:
                $this->cmd_aggiungi_tipo_prevendita($command, $context);
                break;
            
            case ControllerAmministratore::CMD_MODIFICA_TIPO_PREVENDITA:
                $this->cmd_modifica_tipo_prevendita($command, $context);
                break;
                
            case ControllerAmministratore::CMD_ELIMINA_TIPO_PREVENDITA:
                $this->cmd_elimina_tipo_prevendita($command, $context);
                break;
            
            case ControllerAmministratore::CMD_MODIFICA_RUOLI_MEMBRO:
                $this->cmd_modifica_ruoli_membro($command, $context);
                break;
            
            case ControllerAmministratore::CMD_RESTITUISCI_STATISTICHE_PR:
                $this->cmd_restituisci_statistiche_pr($command, $context);
                break;
            
            case ControllerAmministratore::CMD_RESTITUISCI_STATISTICHE_CASSIERE:
                $this->cmd_restituisci_statistiche_cassiere($command, $context);
                break;
            
            case ControllerAmministratore::CMD_RESTITUISCI_STATISTICHE_EVENTO:
                $this->cmd_restituisci_statistiche_evento($command, $context);
                break;
            
            case ControllerAmministratore::CMD_RESTITUISCI_PREVENDITE:
                $this->cmd_restituisci_prevendite($command, $context);
                break;
            
            case ControllerAmministratore::CMD_RIMUOVI_MEMBRO:
                $this->cmd_rimuovi_membro($command, $context);
                break;
                
            case ControllerAmministratore::CMD_MODIFICA_CODICE_ACCESSO:
                $this->cmd_modifica_codice_accesso($command, $context);
                break;

            case ControllerAmministratore::CMD_RESTITUISCI_STATISTICHE_PR_EVENTO:
                $this->cmd_restituisci_statistiche_pr_evento($command, $context);
                break;
            
            case ControllerAmministratore::CMD_RESTITUISCI_STATISTICHE_CASSIERE_EVENTO:
                $this->cmd_restituisci_statistiche_cassiere_evento($command, $context);
                break;
                
            case ControllerAmministratore::CMD_RESTITUISCI_RUOLI_MEMBRO:
                $this->cmd_restituisci_ruoli_membro($command, $context);
                break;

            default:
                break;
        }
        
        switch ($command->getCommand()) {
            case ControllerAmministratore::CMD_AGGIUNGI_EVENTO:
            case ControllerAmministratore::CMD_MODIFICA_EVENTO:
            case ControllerAmministratore::CMD_AGGIUNGI_TIPO_PREVENDITA:                
            case ControllerAmministratore::CMD_MODIFICA_TIPO_PREVENDITA:
            case ControllerAmministratore::CMD_MODIFICA_RUOLI_MEMBRO:
            case ControllerAmministratore::CMD_RESTITUISCI_STATISTICHE_PR:
            case ControllerAmministratore::CMD_RESTITUISCI_STATISTICHE_CASSIERE:
            case ControllerAmministratore::CMD_RESTITUISCI_STATISTICHE_EVENTO:
            case ControllerAmministratore::CMD_RESTITUISCI_PREVENDITE:
            case ControllerAmministratore::CMD_RIMUOVI_MEMBRO:
            case ControllerAmministratore::CMD_MODIFICA_CODICE_ACCESSO:
            case ControllerAmministratore::CMD_RESTITUISCI_STATISTICHE_PR_EVENTO:
            case ControllerAmministratore::CMD_RESTITUISCI_STATISTICHE_CASSIERE_EVENTO:
            case ControllerAmministratore::CMD_RESTITUISCI_RUOLI_MEMBRO:                
                parent::getPrinter()->setStatus(Printer::STATUS_OK);
                break;
                
            default:
                parent::getPrinter()->setStatus(Printer::STATUS_NON_TROVATO);
                break;
        }
    }

    private function cmd_aggiungi_evento(Command $command, Context $context)
    {
        if(!array_key_exists("evento", $command->getArgs())) {
            throw new InvalidArgumentException("Argomenti non validi");
        }

        // Verifico che si è loggati nel sistema.
        if (! $context->isLogged()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        if (! $context->getUserSession()->isStaffScelto()){
            throw new NotAvailableOperationException("Non hai scelto lo staff");            
        }        

        $utente = $context->getUserSession()->getUtente();
        $staff = $context->getUserSession()->getStaffScelto();
        $ruoliMembro = $context->getUserSession()->getRuoliMembro();    

        $evento = $command->getArgs()['evento']->getValue();

        if(! $ruoliMembro->isAmministratore()){
            throw new AuthorizationException("L'utente non è Amministratore dello staff.");
        }

        if (! ($evento instanceof InsertNetWEvento)){
            throw new InvalidArgumentException("Parametri non validi.");
        }
        
        parent::getPrinter()->addResult(Amministratore::aggiungiEvento($evento, $utente->getId(), $staff->getId()));
    }

    private function cmd_modifica_evento(Command $command, Context $context)
    {
        if(!array_key_exists("evento", $command->getArgs())) {
            throw new InvalidArgumentException("Argomenti non validi");
        }

        // Verifico che si è loggati nel sistema.
        if (! $context->isLogged()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        if (! $context->getUserSession()->isEventoScelto()){
            throw new NotAvailableOperationException("Non hai scelto l'evento");            
        }        

        $utente = $context->getUserSession()->getUtente();
        $staffScelto = $context->getUserSession()->getStaffScelto();
        $eventoScelto = $context->getUserSession()->getEventoScelto();
        $ruoliMembro = $context->getUserSession()->getRuoliMembro();

        $evento = $command->getArgs()['evento']->getValue();

        if(! $ruoliMembro->isAmministratore()){
            throw new AuthorizationException("L'utente non è Amministratore dello staff.");
        }

        if (! ($evento instanceof UpdateNetWEvento)){
            throw new InvalidArgumentException("Parametri non validi.");
        }        
        
        parent::getPrinter()->addResult(Amministratore::modificaEvento($evento, $utente->getId(), $staffScelto->getId(), $eventoScelto->getId()));
    }

    private function cmd_aggiungi_tipo_prevendita(Command $command, Context $context)
    {
        if(!array_key_exists("tipoPrevendita", $command->getArgs())) {
            throw new InvalidArgumentException("Argomenti non validi");
        }

        // Verifico che si è loggati nel sistema.
        if (! $context->isLogged()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        if (! $context->getUserSession()->isEventoScelto()){
            throw new NotAvailableOperationException("Non hai scelto l'evento");            
        }        

        $utente = $context->getUserSession()->getUtente();
        $eventoScelto = $context->getUserSession()->getEventoScelto();
        $ruoliMembro = $context->getUserSession()->getRuoliMembro();

        $tipoPrevendita = $command->getArgs()['tipoPrevendita']->getValue();       

        if(! $ruoliMembro->isAmministratore()){
            throw new AuthorizationException("L'utente non è Amministratore dello staff.");
        }

        if (! ($tipoPrevendita instanceof InsertNetWTipoPrevendita)){
            throw new InvalidArgumentException("Parametri non validi.");
        }
        
        parent::getPrinter()->addResult(Amministratore::aggiungiTipoPrevendita($tipoPrevendita, $utente->getId(), $eventoScelto->getId()));
    }

    private function cmd_modifica_tipo_prevendita(Command $command, Context $context)
    {
        if(!array_key_exists("tipoPrevendita", $command->getArgs())) {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        // Verifico che si è loggati nel sistema.
        if (! $context->isLogged()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        if (! $context->getUserSession()->isEventoScelto()){
            throw new NotAvailableOperationException("Non hai scelto lo staff");            
        }        

        $utente = $context->getUserSession()->getUtente();
        $eventoScelto = $context->getUserSession()->getEventoScelto();
        $ruoliMembro = $context->getUserSession()->getRuoliMembro();

        $tipoPrevendita = $command->getArgs()['tipoPrevendita']->getValue();    

        if(! $ruoliMembro->isAmministratore()){
            throw new AuthorizationException("L'utente non è Amministratore dello staff.");
        }

        if (! ($tipoPrevendita instanceof UpdateNetWTipoPrevendita)){
            throw new InvalidArgumentException("Parametri non validi.");
        }

        //Mi serve un check ulteriore SQL sull'idEvento del tipo prevendita
        parent::getPrinter()->addResult(Amministratore::modificaTipoPrevendita($tipoPrevendta, $utente->getId(), $eventoScelto->getId()));
    }
    
    private function cmd_elimina_tipo_prevendita(Command $command, Context $context)
    {
        if(!array_key_exists("tipoPrevendita", $command->getArgs())) {
            throw new InvalidArgumentException("Argomenti non validi");
        }

        // Verifico che si è loggati nel sistema.
        if (! $context->isLogged()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        if (! $context->getUserSession()->isEventoScelto()){
            throw new NotAvailableOperationException("Non hai scelto l'evento");            
        }        

        $eventoScelto = $context->getUserSession()->getEventoScelto();
        $ruoliMembro = $context->getUserSession()->getRuoliMembro();

        $tipoPrevendita = $command->getArgs()['tipoPrevendita']->getValue();       

        if(! $ruoliMembro->isAmministratore()){
            throw new AuthorizationException("L'utente non è Amministratore dello staff.");
        }

        if (! ($tipoPrevendita instanceof NetWId)){
            throw new InvalidArgumentException("Parametri non validi.");
        }       
        
        //Mi serve un check ulteriore SQL sull'idEvento del tipo prevendita
        parent::getPrinter()->addResult(Amministratore::eliminaTipoPrevendita($evento->getId(), $eventoScelto->getId()));
    }

    private function cmd_modifica_ruoli_membro(Command $command, Context $context)
    {
        if(!array_key_exists("ruoliMembro", $command->getArgs())) {
            throw new InvalidArgumentException("Argomenti non validi");
        }

        // Verifico che si è loggati nel sistema.
        if (! $context->isLogged()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        if (! $context->getUserSession()->isStaffScelto()){
            throw new NotAvailableOperationException("Non hai scelto lo staff");            
        }        

        $staff = $context->getUserSession()->getStaffScelto();
        $mieiRuoliMembro = $context->getUserSession()->getRuoliMembro();    

        if(! $mieiRuoliMembro->isAmministratore()){
            throw new AuthorizationException("L'utente non è Amministratore dello staff.");
        }

        $ruoliMembro = $command->getArgs()['ruoliMembro']->getValue();

        if (! ($ruoliMembro instanceof UpdateNetWRuoliMembro)){
            throw new InvalidArgumentException("Parametri non validi.");
        }
        
        Amministratore::modificaRuoliMembro($evento, $staff->getId());
    }

    private function cmd_restituisci_statistiche_pr(Command $command, Context $context)
    {
        //Prima richiedeva lo staff: ora uso quello selezionato

        if(!array_key_exists("pr", $command->getArgs())) {
            throw new InvalidArgumentException("Argomenti non validi");
        }

        // Verifico che si è loggati nel sistema.
        if (! $context->isLogged()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        if (! $context->getUserSession()->isStaffScelto()){
            throw new NotAvailableOperationException("Non hai scelto lo staff");            
        }        

        $staff = $context->getUserSession()->getStaffScelto();
        $mieiRuoliMembro = $context->getUserSession()->getRuoliMembro();    

        if(! $mieiRuoliMembro->isAmministratore()){
            throw new AuthorizationException("L'utente non è Amministratore dello staff.");
        }

        $pr = $command->getArgs()['pr']->getValue();

        if (! ($pr instanceof NetWId)){
            throw new InvalidArgumentException("Parametri non validi.");
        }       
        
        parent::getPrinter()->addResult(Amministratore::getStatistichePR($pr->getId(), $staff->getId()));
    }

    private function cmd_restituisci_statistiche_cassiere(Command $command, Context $context)
    {
        //Prima richiedeva lo staff: ora uso quello selezionato

        if(!array_key_exists("cassiere", $command->getArgs())) {
            throw new InvalidArgumentException("Argomenti non validi");
        }

        // Verifico che si è loggati nel sistema.
        if (! $context->isLogged()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        if (! $context->getUserSession()->isStaffScelto()){
            throw new NotAvailableOperationException("Non hai scelto lo staff");            
        }        

        $staff = $context->getUserSession()->getStaffScelto();
        $mieiRuoliMembro = $context->getUserSession()->getRuoliMembro();    

        if(! $mieiRuoliMembro->isAmministratore()){
            throw new AuthorizationException("L'utente non è Amministratore dello staff.");
        }

        $cassiere = $command->getArgs()['cassiere']->getValue();

        if (! ($cassiere instanceof NetWId)){
            throw new InvalidArgumentException("Parametri non validi.");
        }       

        parent::getPrinter()->addResult(Amministratore::getStatisticheCassiere($cassiere->getId(), $staff->getId()));
    }

    private function cmd_restituisci_statistiche_evento(Command $command, Context $context)
    {
        //Prima richiedeva l'evento: ora uso quello selezionato

        // Verifico che si è loggati nel sistema.
        if (! $context->isLogged()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        if (! $context->getUserSession()->isEventoScelto()){
            throw new NotAvailableOperationException("Non hai scelto l'evento");            
        }        

        $eventoScelto = $context->getUserSession()->getEventoScelto();
        $ruoliMembro = $context->getUserSession()->getRuoliMembro();

        if(! $ruoliMembro->isAmministratore()){
            throw new AuthorizationException("L'utente non è Amministratore dello staff.");
        }        
        
        parent::getPrinter()->addResults(Amministratore::getStatisticheEvento($eventoScelto->getId()));
    }

    private function cmd_restituisci_prevendite(Command $command, Context $context)
    {
        //Prima richiedeva l'evento: ora uso quello selezionato

        // Verifico che si è loggati nel sistema.
        if (! $context->isLogged()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        if (! $context->getUserSession()->isEventoScelto()){
            throw new NotAvailableOperationException("Non hai scelto l'evento");            
        }        

        $eventoScelto = $context->getUserSession()->getEventoScelto();
        $ruoliMembro = $context->getUserSession()->getRuoliMembro();

        if(! $ruoliMembro->isAmministratore()){
            throw new AuthorizationException("L'utente non è Amministratore dello staff.");
        }
        
        parent::getPrinter()->addResults(Amministratore::getPrevendite($eventoScelto->getId()));
    }
    
    private function cmd_rimuovi_membro(Command $command, Context $context)
    {
        if(!array_key_exists("membro", $command->getArgs())) {
            throw new InvalidArgumentException("Argomenti non validi");
        }

        // Verifico che si è loggati nel sistema.
        if (! $context->isLogged()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        if (! $context->getUserSession()->isStaffScelto()){
            throw new NotAvailableOperationException("Non hai scelto lo staff");            
        }        

        $staff = $context->getUserSession()->getStaffScelto();
        $mieiRuoliMembro = $context->getUserSession()->getRuoliMembro();    

        if(! $mieiRuoliMembro->isAmministratore()){
            throw new AuthorizationException("L'utente non è Amministratore dello staff.");
        }        
        
        $membroElimina = $command->getArgs()['membro']->getValue();

        if (! ($membroElimina instanceof NetWId)){
            throw new InvalidArgumentException("Parametri non validi.");
        }  

        parent::getPrinter()->addResult(Amministratore::rimuoviMembro($membroElimina->getId(), $staff->getId()));
    }
    
    private function cmd_modifica_codice_accesso(Command $command, Context $context)
    {
        if(!array_key_exists("staff", $command->getArgs())) {
            throw new InvalidArgumentException("Argomenti non validi");
        }

        // Verifico che si è loggati nel sistema.
        if (! $context->isLogged()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        if (! $context->getUserSession()->isStaffScelto()){
            throw new NotAvailableOperationException("Non hai scelto lo staff");            
        }        

        $staffScelto = $context->getUserSession()->getStaffScelto();
        $mieiRuoliMembro = $context->getUserSession()->getRuoliMembro();    

        if(! $mieiRuoliMembro->isAmministratore()){
            throw new AuthorizationException("L'utente non è Amministratore dello staff.");
        }        
        
        $staff = $command->getArgs()['staff']->getValue();

        if (! ($staff instanceof UpdateNetWStaff)){
            throw new InvalidArgumentException("Parametri non validi.");
        }  
        
        parent::getPrinter()->addResult(Amministratore::modificaCodiceAccesso($staff, $staffScelto->getId()));
    }

    private function cmd_restituisci_statistiche_pr_evento(Command $command, Context $context)
    {
        if(!array_key_exists("pr", $command->getArgs())){
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        //Prima richiedeva l'evento: ora uso quello selezionato

        // Verifico che si è loggati nel sistema.
        if (! $context->isLogged()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        if (! $context->getUserSession()->isEventoScelto()){
            throw new NotAvailableOperationException("Non hai scelto l'evento");            
        }        

        $eventoScelto = $context->getUserSession()->getEventoScelto();
        $ruoliMembro = $context->getUserSession()->getRuoliMembro();

        if(! $ruoliMembro->isAmministratore()){
            throw new AuthorizationException("L'utente non è Amministratore dello staff.");
        }

        $pr = $command->getArgs()['pr']->getValue();

        if (! ($pr instanceof NetWId)){
            throw new InvalidArgumentException("Parametri non validi.");
        }  

        parent::getPrinter()->addResults(Amministratore::getStatistichePREvento($eventoScelto->getId(), $pr->getId()));
    }

    private function cmd_restituisci_statistiche_cassiere_evento(Command $command, Context $context)
    {
        if(!array_key_exists("cassiere", $command->getArgs())){
            throw new InvalidArgumentException("Argomenti non validi");
        }

        //Prima richiedeva l'evento: ora uso quello selezionato

        // Verifico che si è loggati nel sistema.
        if (! $context->isLogged()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        if (! $context->getUserSession()->isEventoScelto()){
            throw new NotAvailableOperationException("Non hai scelto l'evento");            
        }        

        $eventoScelto = $context->getUserSession()->getEventoScelto();
        $ruoliMembro = $context->getUserSession()->getRuoliMembro();

        if(! $ruoliMembro->isAmministratore()){
            throw new AuthorizationException("L'utente non è Amministratore dello staff.");
        }        

        $cassiere = $command->getArgs()['cassiere']->getValue();

        if (! ($cassiere instanceof NetWId)){
            throw new InvalidArgumentException("Parametri non validi.");
        }  

        parent::getPrinter()->addResult(Amministratore::getStatisticheCassiereEvento($eventoScelto->getId(), $cassiere->getId()));
    }

    private function cmd_restituisci_ruoli_membro(Command $command, Context $context)
    {
        //Prima richiedeva lo staff: ora uso quello selezionato

        if(!array_key_exists("membro", $command->getArgs())){
            throw new InvalidArgumentException("Argomenti non validi");
        }

        // Verifico che si è loggati nel sistema.
        if (! $context->isLogged()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        if (! $context->getUserSession()->isStaffScelto()){
            throw new NotAvailableOperationException("Non hai scelto lo staff");            
        }        

        $staffScelto = $context->getUserSession()->getStaffScelto();
        $mieiRuoliMembro = $context->getUserSession()->getRuoliMembro();    

        if(! $mieiRuoliMembro->isAmministratore()){
            throw new AuthorizationException("L'utente non è Amministratore dello staff.");
        }      

        $membro = $command->getArgs()['membro']->getValue();

        if (! ($membro instanceof NetWId)){
            throw new InvalidArgumentException("Parametri non validi.");
        }  
        
        parent::getPrinter()->addResult(Amministratore::getRuoli($membro->getId(), $staffScelto->getId()));
    }

}

