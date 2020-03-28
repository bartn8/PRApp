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
use com\model\db\table\PR;
use com\control\Controller;
use com\control\ControllerPR;
use com\view\printer\Printer;
use \InvalidArgumentException;
use com\model\db\exception\AuthorizationException;
use com\model\net\wrapper\insert\InsertNetWCliente;
use com\model\net\wrapper\NetWFiltriStatoPrevendita;
use com\model\net\wrapper\update\UpdateNetWPrevendita;
use com\model\db\exception\NotAvailableOperationException;

class ControllerPR extends Controller
{

    // Divisione dei comandi: (1-100 utente) (101-200 membro) (201-300 pr) (301-400 cassiere) (401-500 amministratore)
    //const CMD_IS_PR = 201;

    //Comando rimosso: integrato in aggiungi prevedita.
    //const CMD_AGGIUNGI_CLIENTE = 202;

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
    
    public function internalHandle(Command $command, Context $context)
    {
        switch ($command->getCommand()) {               
            case ControllerPR::CMD_AGGIUNGI_PREVENDITA:
                $this->cmd_aggiungi_prevendita($command, $context);
                break;
            
            case ControllerPR::CMD_MODIFICA_PREVENDITA:
                $this->cmd_modifica_prevendita($command, $context);
                break;
            
            case ControllerPR::CMD_RESTITUISCI_PREVENDITE:
                $this->cmd_restituisci_prevedite($command, $context);
                break;
            
            case ControllerPR::CMD_RESTITUISCI_STATISTICHE_PR_TOTALI:
                $this->cmd_restituisci_statistiche_pr_totali($command, $context);
                break;
            
            case ControllerPR::CMD_RESTITUISCI_STATISTICHE_PR_STAFF:
                $this->cmd_restituisci_statistiche_pr_staff($command, $context);
                break;
            
            case ControllerPR::CMD_RESTITUISCI_STATISTICHE_PR_EVENTO:
                $this->cmd_restituisci_statistiche_pr_evento($command, $context);
                break;
            
            case ControllerPR::CMD_RESTITUISCI_PREVENDITE_EVENTO:
                $this->cmd_restituisci_prevedite_evento($command, $context);
                break;

            default:
                break;
        }
        
        switch ($command->getCommand()) {
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

    private function cmd_aggiungi_prevendita(Command $command, Context $context)
    {
        if(!array_key_exists("prevendita", $command->getArgs())) {
            throw new \InvalidArgumentException("Argomento prevendita non esistente");
        }
        
        // Verifico che si è loggati nel sistema.
        if (! $context->isValid()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }             

        if (! $context->getUserSession()->isEventoScelto()){
            throw new NotAvailableOperationException("Non hai scelto l'evento");
        }

        $utente = $context->getUserSession()->getUtente();
        $eventoSelezionato = $context->getUserSession()->getEventoScelto();

        //Controllo i diritti dell'utente.
        $dirittiUtente = $context->getUserSession()->getDirittiUtente();

        if(! $dirittiUtente->isPR()){
            throw new AuthorizationException("L'utente non è PR dello staff.");
        }

        $prevendita = $command->getArgs()['prevendita']->getValue();

        if (! ($prevendita instanceof InsertNetWPrevendita)){
            throw new InvalidArgumentException("Parametri non validi.");
        }
            
        parent::getPrinter()->addResult(PR::aggiungiPrevendita($prevendita, $eventoSelezionato->getId(), $utente->getId()));
    }

    private function cmd_modifica_prevendita(Command $command, Context $context)
    {
        if(!array_key_exists("prevendita", $command->getArgs())) {
            throw new InvalidArgumentException("Argomenti non validi");
        }

        // Verifico che si è loggati nel sistema.
        if (! $context->isValid()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        //Controllo i diritti dell'utente.
        $utente = $context->getUserSession()->getUtente();
        $dirittiUtente = $context->getUserSession()->getDirittiUtente();

        if(! $dirittiUtente->isPR()){
            throw new AuthorizationException("L'utente non è PR dello staff.");
        }

        $prevendita = $command->getArgs()['prevendita']->getValue();

        if (! ($prevendita instanceof UpdateNetWPrevendita)){
            throw new InvalidArgumentException("Parametri non validi.");
        }

        parent::getPrinter()->addResult(PR::modificaPrevendita($prevendita, $utente->getId()));
    }

    private function cmd_restituisci_prevedite(Command $command, Context $context)
    {
        if(!array_key_exists("filtri", $command->getArgs())){
            throw new InvalidArgumentException("Argomenti non validi");
        }

        // Verifico che si è loggati nel sistema.
        if (! $context->isValid()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }        
        
        $filtri = $command->getArgs()['filtri']->getValue();

        if (! ($filtri instanceof NetWFiltriStatoPrevendita))
            throw new InvalidArgumentException("Parametro filtri non valido.");        


        $utente = $context->getUserSession()->getUtente();

        parent::getPrinter()->addResults(PR::getPrevendite($filtri, $utente->getId()));
    }

    private function cmd_restituisci_statistiche_pr_totali(Command $command, Context $context)
    {
        // Verifico che si è loggati nel sistema.
        if (! $context->isValid()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }     

        $utente = $context->getUserSession()->getUtente();

        parent::getPrinter()->addResult(PR::getStatistichePRTotali($utente->getId()));
    }

    private function cmd_restituisci_statistiche_pr_staff(Command $command, Context $context)
    {
        //Prima richiedeva lo staff: ora uso quello selezionato

        // Verifico che si è loggati nel sistema.
        if (! $context->isValid()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }             

        if (! $context->getUserSession()->isStaffScelto()){
            throw new NotAvailableOperationException("Non hai scelto lo staff");
        }

        $utente = $context->getUserSession()->getUtente();
        $staffSelezionato = $context->getUserSession()->getStaffScelto();
        
        parent::getPrinter()->addResult(PR::getStatistichePRStaff($utente->getId(), $staffSelezionato->getId()));
    }

    private function cmd_restituisci_statistiche_pr_evento(Command $command, Context $context)
    {
        //Prima richiedeva l'evento: ora uso quello selezionato
    
        // Verifico che si è loggati nel sistema.
        if (! $context->isValid()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        if (! $context->getUserSession()->isEventoScelto()){
            throw new NotAvailableOperationException("Non hai scelto l'evento");
        }

        $utente = $context->getUserSession()->getUtente();
        $eventoSelezionato = $context->getUserSession()->getEventoScelto();

        parent::getPrinter()->addResults(PR::getStatistichePREvento($utente->getId(), $eventoSelezionato->getId()));
    }

    private function cmd_restituisci_prevedite_evento(Command $command, Context $context)
    {
        //Prima richiedeva l'evento: ora uso quello selezionato
    
        // Verifico che si è loggati nel sistema.
        if (! $context->isValid()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        if (! $context->getUserSession()->isEventoScelto()){
            throw new NotAvailableOperationException("Non hai scelto l'evento");
        }

        $utente = $context->getUserSession()->getUtente();
        $eventoSelezionato = $context->getUserSession()->getEventoScelto();

        parent::getPrinter()->addResults(PR::getPrevenditeEvento($utente->getId(), $eventoSelezionato->getId()));
    }
}

