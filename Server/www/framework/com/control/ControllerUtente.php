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
use com\model\db\table\Utente;
use com\model\db\wrapper\WStaff;
use com\model\db\wrapper\WToken;
use com\control\ControllerUtente;
use com\model\db\wrapper\WUtente;
use com\model\net\wrapper\NetWLogin;
use com\model\net\wrapper\NetWToken;
use com\model\net\wrapper\NetWStaffAccess;
use com\model\net\wrapper\insert\InsertNetWStaff;
use com\model\db\exception\AuthorizationException;
use com\model\net\wrapper\insert\InsertNetWUtente;
use com\model\db\exception\NotAvailableOperationException;

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
    public const CMD_SCEGLI_STAFF_ARG_0 = "staff";

    public function __construct($printer, $retriver)
    {
        parent::__construct($printer, $retriver);
    }

    public function internalHandle(Command $command, Context $context)
    { 
        // Effettuo l'operazione.
        switch ($command->getCommand()) {
            case ControllerUtente::CMD_REGISTRAZIONE:
                $this->cmd_registrazione($command, $context);
                break;
            
            case ControllerUtente::CMD_LOGIN:
                $this->cmd_login($command, $context);
                break;
            
            case ControllerUtente::CMD_LOGOUT:
                $this->cmd_logout($command, $context);
                break;
            
            case ControllerUtente::CMD_CREA_STAFF:
                $this->cmd_crea_staff($command, $context);
                break;
            
            case ControllerUtente::CMD_ACCEDI_STAFF:
                $this->cmd_accedi_staff($command, $context);
                break;
            
            case ControllerUtente::CMD_RESTITUISCI_LISTA_STAFF:
                $this->cmd_restituisci_lista_staff($command, $context);
                break;
            
            case ControllerUtente::CMD_RESTITUISCI_LISTA_STAFF_MEMBRI:
                $this->cmd_restituisci_lista_staff_membri($command, $context);
                break;

            case ControllerUtente::CMD_RENEW_TOKEN:
                $this->cmd_renew_token($command, $context);
                break;

            case ControllerUtente::CMD_GET_TOKEN:
                $this->cmd_get_token($command, $context);
                break;

            case ControllerUtente::CMD_LOGIN_TOKEN:
                $this->cmd_login_token($command, $context);
                break;
            
            case ControllerUtente::CMD_RESTITUISCI_UTENTE:
                $this->cmd_restituisci_utente($command, $context);
                break;
				
            case ControllerUtente::CMD_SCEGLI_STAFF:
                $this->cmd_scegli_staff($command, $context);
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

    private function cmd_registrazione(Command $command, Context $context)
    {
        if(!array_key_exists(ControllerUtente::CMD_REGISTRAZIONE_ARG_0, $command->getArgs())){
            throw new InvalidArgumentException("Argomenti non validi");
        }

        // Verifico che non si è loggati nel sistema.
        if ($context->isValid()){
            throw new NotAvailableOperationException("Utente loggato.");
        }
    
        $registrazione = $command->getArgs()[ControllerUtente::CMD_REGISTRAZIONE_ARG_0]->getValue();

        // Verifico i parametri
        if (! ($registrazione instanceof InsertNetWUtente))
            throw new InvalidArgumentException("Parametro non valido.");

        //eccezione in caso di errore.
        parent::getPrinter()->addResult($Utente::registrazione($registrazione));
    }

    private function cmd_login(Command $command, Context $context)
    {       
        if(!array_key_exists(ControllerUtente::CMD_LOGIN_ARG_0, $command->getArgs())) {
            throw new InvalidArgumentException("Argomenti non validi");
        }

        // Verifico che non si è loggati nel sistema.
        if ($context->isValid()){
            throw new NotAvailableOperationException("Utente loggato.");
        }

        $login = $command->getArgs()[ControllerUtente::CMD_LOGIN_ARG_0]->getValue();

        // Verifico i parametri
        if (! ($login instanceof NetWLogin))
            throw new InvalidArgumentException("Parametri non validi.");

        //Effettuo il login da db: eccezione se non fa il login.
        $utente = Utente::login($login);

        // Inizializzo il contesto.
        Context::createContext($utente);

        parent::getPrinter()->addResult($utente);
    }

    private function cmd_logout(Command $command, Context $context)
    {
        // Verifico che si è loggati nel sistema.
        if (! $context->isValid()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        // Elimino il contesto.
        Context::deleteContext();
    }

    private function cmd_crea_staff(Command $command, Context $context)
    {
        if(!array_key_exists(ControllerUtente::CMD_CREA_STAFF_ARG_0, $command->getArgs())){
            throw new InvalidArgumentException("Argomento non valido");
        }

        // Verifico che si è loggati nel sistema.
        if (! $context->isValid()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }
    
        $staff = $command->getArgs()[ControllerUtente::CMD_CREA_STAFF_ARG_0]->getValue();
        $utente = $context->getUserSession()->getUtente();

        // Verifico i parametri
        if (! ($staff instanceof InsertNetWStaff)){
            throw new InvalidArgumentException("Parametro non valido.");
        }
        
        parent::getPrinter()->addResult(Utente::creaStaff($utente, $staff));
    }

    private function cmd_accedi_staff(Command $command, Context $context)
    {
        if(!array_key_exists(ControllerUtente::CMD_ACCEDI_STAFF_ARG_0, $command->getArgs())){
            throw new InvalidArgumentException("Argomenti non validi");
        }

        // Verifico che si è loggati nel sistema.
        if (! $context->isValid()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        $staff = $command->getArgs()[ControllerUtente::CMD_ACCEDI_STAFF_ARG_0]->getValue();
        $utente = $context->getUserSession()->getUtente();

        // Verifico i parametri
        if (! ($staff instanceof NetWStaffAccess)){
            throw new InvalidArgumentException("Parametri non validi.");        
        }
        
        parent::getPrinter()->addResult(Utente::accediStaff($utente, $staff));
    }

    private function cmd_restituisci_lista_staff(Command $command, Context $context)
    {
        // Verifico che si è loggati nel sistema.
        if (! $context->isValid()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        parent::getPrinter()->addResults(Utente::getListaStaff());
    }

    private function cmd_restituisci_lista_staff_membri(Command $command, Context $context)
    {
        // Verifico che si è loggati nel sistema.
        if (! $context->isValid()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        $utente = $context->getUserSession()->getUtente();

        parent::getPrinter()->addResults(Utente::getListaStaffMembri($utente));
    }

    private function cmd_renew_token(Command $command, Context $context)
    {
        // Verifico che si è loggati nel sistema.
        if (! $context->isValid()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        $utente = $context->getUserSession()->getUtente();
        
        parent::getPrinter()->addResult(Utente::renewToken($utente));
    }

    private function cmd_get_token(Command $command, Context $context)
    {
        // Verifico che si è loggati nel sistema.
        if (! $context->isValid()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        $utente = $context->getUserSession()->getUtente();

        parent::getPrinter()->addResult(Utente::getToken($utente));
    }

    private function cmd_login_token(Command $command, Context $context)
    {
        if(!array_key_exists(ControllerUtente::CMD_LOGIN_TOKEN_ARG_0, $command->getArgs())){
            throw new InvalidArgumentException("Argomenti non validi");
        }

        // Verifico che non si è loggati nel sistema.
        if ($context->isValid()){
            throw new NotAvailableOperationException("Utente loggato.");
        }

        $token = $command->getArgs()[ControllerUtente::CMD_LOGIN_TOKEN_ARG_0]->getValue();

        // Verifico i parametri
        if (! ($token instanceof NetWToken)){
            throw new InvalidArgumentException("Parametri non validi.");
        }

        $utente = Utente::loginToken($token);

        //Creo il contesto dal token.
        Context::createContext($utente);

        parent::getPrinter()->addResult($utente);
    }

    private function cmd_restituisci_utente(Command $command, Context $context)
    {
        if(!$context->isValid()) {
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        parent::getPrinter()->addResult($context->getUserSession()->getUtente());
    }
	
	private function cmd_scegli_staff(Command $command, Context $context){
        if(!array_key_exists(ControllerUtente::CMD_SCEGLI_STAFF_ARG_0, $command->getArgs())){
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        // Verifico che si è loggati nel sistema.
        if(!$context->isValid()) {
            throw new NotAvailableOperationException("Utente non loggato.");
        }
            
        $utente = $context->getUserSession()->getUtente();
        $staff = $command->getArgs()[ControllerUtente::CMD_SCEGLI_STAFF_ARG_0]->getValue();
        
        if (! ($staff instanceof NetWId)){
            throw new InvalidArgumentException("Parametri non validi. (staff)");
        }
                
        $staffScelto = Utente::getStaff($utente->getId(), $staff->getId());
               
        if($staffScelto == NULL){
            throw new AuthorizationException("Non puoi accedere allo staff");
        }else{
            //Tecnicamente se vieni cacciato, rimani nello staff fino a quando non aggiorno la sessione.
            $context->getUserSession()->setStaffScelto($staffScelto);
            
            //Ricavo anche i diritti dell'utente
            //Tecnicamente se vengono aggiornati i diritti, la modifica non viene vista fino all'aggiornamento della sessione.
            $dirittiPersonali = Membro::getDiritti($utente->getId(), $staffScelto->getId());

            $context->getUserSession()->setDirittiUtente($dirittiPersonali);
            
            parent::getPrinter()->addResults([$staffScelto, $dirittiStaff]);
        }
        
    }
	
}

