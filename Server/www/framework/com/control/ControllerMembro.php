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
use com\model\db\table\Membro;
use com\control\ControllerMembro;
use com\model\net\wrapper\NetWId;
use com\model\db\exception\AuthorizationException;
use com\model\db\exception\NotAvailableOperationException;

class ControllerMembro extends Controller
{

    // Divisione dei comandi: (1-100 utente) (101-200 membro) (201-300 pr) (301-400 cassiere) (401-500 amministratore)

    public const CMD_RESTITUISCI_LISTA_UTENTI = 102;

    public const CMD_RESTITUISCI_RUOLI_PERSONALI = 103;

    public const CMD_RESTITUISCI_LISTA_EVENTI = 105;

    public const CMD_RESTITUISCI_TIPI_PREVENDITA = 106;

    public const CMD_SCEGLI_EVENTO = 108;	

    public const CMD_GET_EVENTO_SCELTO = 109;	

    public function __construct($printer, $retriver)
    {
        parent::__construct($printer, $retriver);
    }

    protected function internalHandle(Command $command, Context $context)
    {
        switch ($command->getCommand()) {

            case ControllerMembro::CMD_RESTITUISCI_LISTA_UTENTI:
                $this->cmd_restituisci_lista_utenti($command, $context);
                break;
            
            case ControllerMembro::CMD_RESTITUISCI_RUOLI_PERSONALI:
                $this->cmd_restiutisci_ruoli_personali($command, $context);
                break;
                        
            case ControllerMembro::CMD_RESTITUISCI_LISTA_EVENTI:
                $this->cmd_restituisci_lista_eventi($command, $context);
                break;
            
            case ControllerMembro::CMD_RESTITUISCI_TIPI_PREVENDITA:
                $this->cmd_restituisci_tipi_prevendita($command, $context);
                break;
            				
            case ControllerMembro::CMD_SCEGLI_EVENTO:
                $this->cmd_scegli_evento($command, $context);
                break;	
                			
            case ControllerMembro::CMD_GET_EVENTO_SCELTO:
                $this->cmd_get_evento_scelto($command, $context);
                break;	            

            default:
                break;
        }
        
        switch ($command->getCommand()) {
            case ControllerMembro::CMD_RESTITUISCI_LISTA_UTENTI:
            case ControllerMembro::CMD_RESTITUISCI_RUOLI_PERSONALI:
            case ControllerMembro::CMD_RESTITUISCI_LISTA_EVENTI:
            case ControllerMembro::CMD_RESTITUISCI_TIPI_PREVENDITA:
            case ControllerMembro::CMD_SCEGLI_EVENTO:
            case ControllerMembro::CMD_GET_EVENTO_SCELTO:                
                parent::getPrinter()->setStatus(Printer::STATUS_OK);
                break;
            
            default:
                parent::getPrinter()->setStatus(Printer::STATUS_NON_TROVATO);
                break;
        }
        
    }
    
    private function cmd_restituisci_lista_utenti(Command $command, Context $context)
    {
        //Precedentemente richiedevo all'utente lo staff: ora deve sceglierlo prima

        // Verifico che si è loggati nel sistema.
        if (! $context->isLogged()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        if (! $context->getUserSession()->isStaffScelto()){
            throw new NotAvailableOperationException("Non hai scelto lo staff");            
        }
            
        $staff = $context->getUserSession()->getStaffScelto();

        parent::getPrinter()->addResults(Membro::getListaUtenti($staff));
    }

    private function cmd_restiutisci_ruoli_personali(Command $command, Context $context)
    {
        //Precedentemente richiedevo all'utente lo staff: ora deve sceglierlo prima
        
        // Verifico che si è loggati nel sistema.
        if (! $context->isLogged()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        if (! $context->getUserSession()->isStaffScelto()){
            throw new NotAvailableOperationException("Non hai scelto lo staff");            
        }

        $utente = $context->getUserSession()->getUtente();
        $staff = $context->getUserSession()->getStaffScelto();

        parent::getPrinter()->addResult(Membro::getRuoliPersonali($utente->getId(), $staff->getId()));
    }

    private function cmd_restituisci_lista_eventi(Command $command, Context $context)
    {
        //Precedentemente richiedevo all'utente lo staff: ora deve sceglierlo prima
        
        // Verifico che si è loggati nel sistema.
        if (! $context->isLogged()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        if (! $context->getUserSession()->isStaffScelto()){
            throw new NotAvailableOperationException("Non hai scelto lo staff");            
        }

        $staff = $context->getUserSession()->getStaffScelto();

        $risultati = Membro::getListaEventi($staff->getId());
        
        parent::getPrinter()->addResults($risultati);
    }

    private function cmd_restituisci_tipi_prevendita(Command $command, Context $context)
    {
        // Verifico che si è loggati nel sistema.
        if (! $context->isLogged()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        if (! $context->getUserSession()->isEventoScelto()){
            throw new NotAvailableOperationException("Non hai scelto l'evento");
        }
        
        $evento = $context->getUserSession()->getEventoScelto();

        parent::getPrinter()->addResults(Membro::getTipiPrevendita($evento->getId()));
    }
	
	private function cmd_scegli_evento(Command $command, Context $context){
        if(!array_key_exists("evento", $command->getArgs())){
            throw new InvalidArgumentException("Argomenti non validi");
        }
                
        // Verifico che si è loggati nel sistema.
        if (! $context->isLogged()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        //Prima devo aver scelto lo staff.
        if (! $context->getUserSession()->isStaffScelto()){
            throw new NotAvailableOperationException("Non hai scelto lo staff");            
        }
            
        $utente = $context->getUserSession()->getUtente();
        $staff = $context->getUserSession()->getStaffScelto();
        $evento = $command->getArgs()['evento']->getValue();
            
        if (! ($evento instanceof NetWId))
            throw new InvalidArgumentException("Parametri non validi. (evento)");
        
        $eventoScelto = Membro::getEvento($utente->getId(), $staff->getId(), $evento->getId());
        
        if($eventoScelto == NULL){
            throw new AuthorizationException("Non puoi accedere all'evento");
        }else{
            $context->getUserSession()->setEventoScelto($eventoScelto);
            parent::getPrinter()->addresult($eventoScelto);
        }
    }

    
	private function cmd_get_evento_scelto(Command $command, Context $context){        
        // Verifico che si è loggati nel sistema.
        if(!$context->isLogged()) {
            throw new NotAvailableOperationException("Utente non loggato.");
        }
            
        if (! $context->getUserSession()->isEventoScelto()){
            throw new NotAvailableOperationException("Non hai scelto l'evento");
        }
            
        $eventoScelto = $context->getUserSession()->getEventoScelto();           
        parent::getPrinter()->addResult($eventoScelto);   
    }
}

