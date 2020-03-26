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

use com\model\db\exception\NotAvailableOperationException;
use com\model\Context;
use com\model\db\table\Utente;
use \InvalidArgumentException;
use com\view\printer\Printer;

class ControllerUtente extends Controller
{

    // Divisione dei comandi: (1-100 utente) (101-200 membro) (201-300 pr) (301-400 cassiere) (401-500 amministratore) (950-1000 manutenzione)
    public const CMD_REGISTRAZIONE = 1;
    public const CMD_REGISTRAZIONE_ARG_0 = "utente";

    public const CMD_LOGIN = 2;
    public const CMD_LOGIN_ARG_0 = "login";


    public const CMD_LOGOUT = 3;

    public const CMD_CREA_STAFF = 4;
    public const CMD_CREA_STAFF_ARG_0 = "staff";

    public const CMD_ACCEDI_STAFF = 5;
    public const CMD_ACCEDI_STAFF_ARG_0 = "staff";

    public const CMD_RESTITUISCI_LISTA_STAFF = 6;

    public const CMD_RESTITUISCI_LISTA_STAFF_MEMBRI = 7;

    public const CMD_RENEW_TOKEN = 8;

    public const CMD_GET_TOKEN = 9;

    public const CMD_LOGIN_TOKEN = 10;
    public const CMD_LOGIN_TOKEN_ARG_0 = "token";

    public const CMD_RESTITUISCI_UTENTE = 11;
	
	public const CMD_SCEGLI_STAFF = 8;

    public function __construct($printer, $retriver)
    {
        parent::__construct($printer, $retriver);
    }

    public function handle($command)
    {
        
        // Effettuo l'operazione.
        switch ($command->getCommand()) {
            case ControllerUtente::CMD_REGISTRAZIONE:
                $this->cmd_registrazione($command);
                break;
            
            case ControllerUtente::CMD_LOGIN:
                $this->cmd_login($command);
                break;
            
            case ControllerUtente::CMD_LOGOUT:
                $this->cmd_logout($command);
                break;
            
            case ControllerUtente::CMD_CREA_STAFF:
                $this->cmd_crea_staff($command);
                break;
            
            case ControllerUtente::CMD_ACCEDI_STAFF:
                $this->cmd_accedi_staff($command);
                break;
            
            case ControllerUtente::CMD_RESTITUISCI_LISTA_STAFF:
                $this->cmd_restituisci_lista_staff($command);
                break;
            
            case ControllerUtente::CMD_RESTITUISCI_LISTA_STAFF_MEMBRI:
                $this->cmd_restituisci_lista_staff_membri($command);
                break;

            case ControllerUtente::CMD_RENEW_TOKEN:
                $this->cmd_renew_token($command);
                break;

            case ControllerUtente::CMD_GET_TOKEN:
                $this->cmd_get_token($command);
                break;

            case ControllerUtente::CMD_LOGIN_TOKEN:
                $this->cmd_login_token($command);
                break;
            
            case ControllerUtente::CMD_RESTITUISCI_UTENTE:
                $this->cmd_restituisci_utente($command);
                break;
				
            case ControllerUtente::CMD_SCEGLI_STAFF:
                $this->cmd_scegli_staff($command);
                break;

            default:
                break;
        }
        
        // Stampo lo stato.
        switch ($command->getCommand()) {
            case ControllerUtente::CMD_REGISTRAZIONE:
            case ControllerUtente::CMD_LOGIN:
            case ControllerUtente::CMD_LOGOUT:
            case ControllerUtente::CMD_CREA_STAFF:
            case ControllerUtente::CMD_ACCEDI_STAFF:
            case ControllerUtente::CMD_RESTITUISCI_LISTA_STAFF:
            case ControllerUtente::CMD_RESTITUISCI_LISTA_STAFF_MEMBRI:
            case ControllerUtente::CMD_RENEW_TOKEN:
            case ControllerUtente::CMD_GET_TOKEN:
            case ControllerUtente::CMD_LOGIN_TOKEN:
            case ControllerUtente::CMD_RESTITUISCI_UTENTE:
			case ControllerUtente::CMD_SCEGLI_STAFF:
                parent::getPrinter()->setStatus(Printer::STATUS_OK);
                break;
            
            default:
                parent::getPrinter()->setStatus(Printer::STATUS_NON_TROVATO);
                break;
        }
        
    }

    private function cmd_registrazione($command)
    {
        if(!array_key_exists(ControllerUtente::CMD_REGISTRAZIONE_ARG_0, $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        parent::getPrinter()->addResult(Utente::registrazione($command->getArgs()[ControllerUtente::CMD_REGISTRAZIONE_ARG_0]->getValue()));
    }

    private function cmd_login($command)
    {       
        if(!array_key_exists(ControllerUtente::CMD_LOGIN_ARG_0, $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        //Da modificare
        parent::getPrinter()->addResult(Utente::login($command->getArgs()[ControllerUtente::CMD_LOGIN_ARG_0]->getValue()));
    }

    private function cmd_logout($command)
    {
                // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");

        // Elimino il contesto.
        Context::deleteContext();
    }

    private function cmd_crea_staff($command)
    {
        if(!array_key_exists(ControllerUtente::CMD_CREA_STAFF_ARG_0, $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomento non valido");
        }
        
        parent::getPrinter()->addResult(Utente::creaStaff($command->getArgs()[ControllerUtente::CMD_CREA_STAFF_ARG_0]->getValue()));
    }

    private function cmd_accedi_staff($command)
    {
        if(!array_key_exists(ControllerUtente::CMD_ACCEDI_STAFF_ARG_0, $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        parent::getPrinter()->addResult(Utente::accediStaff($command->getArgs()[ControllerUtente::CMD_ACCEDI_STAFF_ARG_0]->getValue()));
    }

    private function cmd_restituisci_lista_staff($command)
    {
        parent::getPrinter()->addResults(Utente::getListaStaff());
    }

    private function cmd_restituisci_lista_staff_membri($command)
    {
        parent::getPrinter()->addResults(Utente::getListaStaffMembri());
    }

    private function cmd_renew_token($command)
    {
        parent::getPrinter()->addResult(Utente::renewToken());
    }

    private function cmd_get_token($command)
    {
        parent::getPrinter()->addResult(Utente::getToken());
    }

    private function cmd_login_token($command)
    {
        if(!array_key_exists(ControllerUtente::CMD_LOGIN_TOKEN_ARG_0, $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }

        parent::getPrinter()->addResult(Utente::loginToken($command->getArgs()[ControllerUtente::CMD_LOGIN_TOKEN_ARG_0]->getValue()));
    }

    private function cmd_restituisci_utente($command)
    {
        $context = Context::getContext();

        if(!$context->isValid())
        {
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        parent::getPrinter()->addResult($context->getUtente());
    }
	
	private function cmd_scegli_staff($command){
        if(!array_key_exists("staff", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        // Verifico che si è loggati nel sistema.
        if (! Context::getContext()->isValid())
            throw new NotAvailableOperationException("Utente non loggato.");
            
        $utente =  Context::getContext()->getUserSession()->getUtente();
        $staff =  $command->getArgs()['staff']->getValue();
        
        if (! ($staff instanceof NetWId))
            throw new InvalidArgumentException("Parametri non validi. (staff)");
                
        $staffScelto = Utente::getStaff($utente->getId(), $staff->getId());
               
        if($staffScelto == NULL){
            throw new AuthorizationException("Non puoi accedere allo staff");
        }else{
            Context::getContext()->getUserSession()->setStaffScelto($staffScelto);
            
            //Ricavo anche i diritti dell'utente
            
            
            parent::getPrinter()->addResults([$staffScelto, $dirittiStaff]);
        }
        
    }
	
}

