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
use com\model\db\table\Cassiere;
use com\model\net\wrapper\NetWId;
use com\control\ControllerCassiere;
use com\model\net\wrapper\NetWEntrata;
use com\model\db\exception\AuthorizationException;
use com\model\db\exception\NotAvailableOperationException;

class ControllerCassiere extends Controller
{

    // Divisione dei comandi: (1-100 utente) (101-200 membro) (201-300 pr) (301-400 cassiere) (401-500 amministratore)

    const CMD_TIMBRA_ENTRATA = 302;

    const CMD_RESTITUISCI_DATI_CLIENTE = 303;

    const CMD_RESTITUISCI_STATISTICHE_CASSIERE_TOTALI = 304;

    const CMD_RESTITUISCI_STATISTICHE_CASSIERE_STAFF = 305;

    const CMD_RESTITUISCI_STATISTICHE_CASSIERE_EVENTO = 306;

    const CMD_RESTITUISCI_ENTRATE_SVOLTE = 307;

    const CMD_RESTITUISCI_PREVENDITE_EVENTO = 308;

    const CMD_RESTITUISCI_INFORMAZIONI_PREVENDITA = 309;

    const CMD_RESTITUISCI_LISTA_PREVENDITE_TIMBRATE = 310;

    const CMD_RESTITUISCI_LISTA_PREVENDITE_NON_TIMBRATE = 311;


    public function __construct($printer, $retriver)
    {
        parent::__construct($printer, $retriver);
    }

    public function internalHandle(Command $command, Context $context)
    {
        switch ($command->getCommand()) {
            case ControllerCassiere::CMD_TIMBRA_ENTRATA:
                $this->cmd_timbra_entrata($command, $context);
                break;
            
            case ControllerCassiere::CMD_RESTITUISCI_DATI_CLIENTE:
                $this->cmd_restituisci_dati_cliente($command, $context);
                break;
            
            case ControllerCassiere::CMD_RESTITUISCI_STATISTICHE_CASSIERE_TOTALI:
                $this->cmd_restituisci_statistiche_cassiere_totali($command, $context);
                break;
            
            case ControllerCassiere::CMD_RESTITUISCI_STATISTICHE_CASSIERE_STAFF:
                $this->cmd_restituisci_statistiche_cassiere_staff($command, $context);
                break;
            
            case ControllerCassiere::CMD_RESTITUISCI_STATISTICHE_CASSIERE_EVENTO:
                $this->cmd_restituisci_statistiche_cassiere_evento($command, $context);
                break;
            
            case ControllerCassiere::CMD_RESTITUISCI_ENTRATE_SVOLTE:
                $this->cmd_restituisci_entrate_svolte($command, $context);
                break;
            
            case ControllerCassiere::CMD_RESTITUISCI_PREVENDITE_EVENTO:
                $this->cmd_restituisci_prevendite_evento($command, $context);
                break;
            
            case ControllerCassiere::CMD_RESTITUISCI_INFORMAZIONI_PREVENDITA:
                $this->cmd_restituisci_informazioni_prevendita($command, $context);
                break;

            case ControllerCassiere::CMD_RESTITUISCI_LISTA_PREVENDITE_TIMBRATE:
                $this->cmd_restituisci_lista_prevendite_entrate($command, $context);
                break;

            case ControllerCassiere::CMD_RESTITUISCI_LISTA_PREVENDITE_NON_TIMBRATE:
                $this->cmd_restituisci_lista_prevendite_non_entrate($command, $context);
                break;

            default:
                break;
        }
        
        switch ($command->getCommand()) {
            case ControllerCassiere::CMD_TIMBRA_ENTRATA:
            case ControllerCassiere::CMD_RESTITUISCI_DATI_CLIENTE:
            case ControllerCassiere::CMD_RESTITUISCI_STATISTICHE_CASSIERE_TOTALI:
            case ControllerCassiere::CMD_RESTITUISCI_STATISTICHE_CASSIERE_STAFF:
            case ControllerCassiere::CMD_RESTITUISCI_STATISTICHE_CASSIERE_EVENTO:
            case ControllerCassiere::CMD_RESTITUISCI_ENTRATE_SVOLTE:
            case ControllerCassiere::CMD_RESTITUISCI_PREVENDITE_EVENTO:
            case ControllerCassiere::CMD_RESTITUISCI_INFORMAZIONI_PREVENDITA:
            case ControllerCassiere::CMD_RESTITUISCI_LISTA_PREVENDITE_TIMBRATE:
            case ControllerCassiere::CMD_RESTITUISCI_LISTA_PREVENDITE_NON_TIMBRATE:
                parent::getPrinter()->setStatus(Printer::STATUS_OK);
                break;
            
            default:
                parent::getPrinter()->setStatus(Printer::STATUS_NON_TROVATO);
                break;
        }
    }

    private function cmd_timbra_entrata(Command $command, Context $context)
    {
        if(!array_key_exists("entrata", $command->getArgs())){
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        // Verifico che si è loggati nel sistema.
        if (! $context->isValid()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        if (! $context->getUserSession()->isEventoScelto()){
            throw new NotAvailableOperationException("Non hai scelto l'evento");
        }
        
        $entrata = $command->getArgs()['entrata']->getValue();

        if (! ($entrata instanceof NetWEntrata)){
            throw new InvalidArgumentException("Parametri non validi.");
        }
        
        $utente = $context->getUserSession()->getUtente();

        //Controllo che la prevendita abbia una corrispondenza con l'evento selezionato
        //Poi viene controllato successivamente che i dati siano congrui.
        $eventoSelezionato = $context->getUserSession()->getEventoScelto();

        if($entrata->getIdEvento() != $eventoSelezionato->getId()){
            throw new NotAvailableOperationException("La prevendita non appartiene all'evento selezionato");
        }

        //Controllo i diritti dell'utente.
        $dirittiUtente = $context->getUserSession()->getDirittiUtente();

        if(! $dirittiUtente->isCassiere()){
            throw new AuthorizationException("L'utente non è cassiere dello staff.");
        }

        //Passo anche l'evento per un check sulla prevendita.
        parent::getPrinter()->addResult(Cassiere::timbraEntrata($entrata, $utente->getId(), $eventoSelezionato->getId()));
    }

    private function cmd_restituisci_dati_cliente(Command $command, Context $context)
    {
        if(!array_key_exists("prevendita", $command->getArgs())){
            throw new InvalidArgumentException("Argomenti non validi");
        }

        // Verifico che si è loggati nel sistema.
        if (! $context->isValid()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        if (! $context->getUserSession()->isEventoScelto()){
            throw new NotAvailableOperationException("Non hai scelto l'evento");
        }

        $prevendita = $command->getArgs()['prevendita']->getValue();

        if (! ($prevendita instanceof NetWId)){
            throw new InvalidArgumentException("Parametri non validi.");        
        }

        //Controllo i diritti dell'utente.
        $dirittiUtente = $context->getUserSession()->getDirittiUtente();

        //Usato per un check sul membro che richiede i dati del cliente.
        $eventoSelezionato = $context->getUserSession()->getEventoScelto();

        if(! $dirittiUtente->isCassiere()){
            throw new AuthorizationException("L'utente non è cassiere dello staff.");
        }
        
        parent::getPrinter()->addResult(Cassiere::getDatiCliente($prevendita->getId(), $eventoSelezionato->getId()));
    }

    private function cmd_restituisci_statistiche_cassiere_totali(Command $command, Context $context)
    {
        // Verifico che si è loggati nel sistema.
        if (! $context->isValid()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        $utente = $context->getUserSession()->getUtente();

        parent::getPrinter()->addResult(Cassiere::getStatisticheCassiereTotali($utente->getId()));
    }

    private function cmd_restituisci_statistiche_cassiere_staff(Command $command, Context $context)
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
        
        parent::getPrinter()->addResult(Cassiere::getStatisticheCassiereStaff($utente->getId(), $staffSelezionato->getId()));
    }

    private function cmd_restituisci_statistiche_cassiere_evento(Command $command, Context $context)
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

        parent::getPrinter()->addResult(Cassiere::getStatisticheCassiereEvento($utente->getId(), $eventoSelezionato->getId()));
    }

    private function cmd_restituisci_entrate_svolte(Command $command, Context $context)
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

        parent::getPrinter()->addResults(Cassiere::getEntrateSvolte($utente->getId(), $eventoSelezionato->getId()));
    }

    private function cmd_restituisci_prevendite_evento(Command $command, Context $context)
    {
        //Prima richiedeva l'evento: ora uso quello selezionato

        // Verifico che si è loggati nel sistema.
        if (! $context->isValid()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        if (! $context->getUserSession()->isEventoScelto()){
            throw new NotAvailableOperationException("Non hai scelto l'evento");
        }

        $eventoSelezionato = $context->getUserSession()->getEventoScelto();

        //Controllo i diritti dell'utente.
        $dirittiUtente = $context->getUserSession()->getDirittiUtente();

        if(! $dirittiUtente->isCassiere()){
            throw new AuthorizationException("L'utente non è cassiere dello staff.");
        }

        parent::getPrinter()->addResult(Cassiere::getPrevenditeEvento($eventoSelezionato->getId()));
    }

    private function cmd_restituisci_informazioni_prevendita(Command $command, Context $context)
    {
        if(!array_key_exists("prevendita", $command->getArgs()))
        {
            throw new InvalidArgumentException("Argomenti non validi");
        }
        
        // Verifico che si è loggati nel sistema.
        if (! $context->isValid()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        if (! $context->getUserSession()->isEventoScelto()){
            throw new NotAvailableOperationException("Non hai scelto l'evento");
        }

        $eventoSelezionato = $context->getUserSession()->getEventoScelto();

        //Controllo i diritti dell'utente.
        $dirittiUtente = $context->getUserSession()->getDirittiUtente();

        if(! $dirittiUtente->isCassiere()){
            throw new AuthorizationException("L'utente non è cassiere dello staff.");
        }

        $prevendita = $command->getArgs()['prevendita']->getValue();
       
        if (! ($prevendita instanceof NetWId)){
            throw new InvalidArgumentException("Parametri non validi.");
        }   

        $infoPrevendita = Cassiere::getInformazioniPrevendita($prevendita->getId());

        //Check sull'evento della prevendita
        if($infoPrevendita->getIdEvento() != $eventoSelezionato->getId()){
            throw new NotAvailableOperationException("Prevendita richiesta non corrisponde con l'evento selezionato");
        }

        parent::getPrinter()->addResult();
    }

    private function cmd_restituisci_lista_prevendite_entrate(Command $command, Context $context){
        //Prima richiedeva l'evento: ora uso quello selezionato

        // Verifico che si è loggati nel sistema.
        if (! $context->isValid()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        if (! $context->getUserSession()->isEventoScelto()){
            throw new NotAvailableOperationException("Non hai scelto l'evento");
        }

        $eventoSelezionato = $context->getUserSession()->getEventoScelto();

        //Controllo i diritti dell'utente.
        $dirittiUtente = $context->getUserSession()->getDirittiUtente();

        if(! $dirittiUtente->isCassiere()){
            throw new AuthorizationException("L'utente non è cassiere dello staff.");
        }

        parent::getPrinter()->addResults(Cassiere::getListaPrevenditeEntrate($eventoSelezionato->getId()));
    }

    private function cmd_restituisci_lista_prevendite_non_entrate(Command $command, Context $context){
        //Prima richiedeva l'evento: ora uso quello selezionato

        // Verifico che si è loggati nel sistema.
        if (! $context->isValid()){
            throw new NotAvailableOperationException("Utente non loggato.");
        }

        if (! $context->getUserSession()->isEventoScelto()){
            throw new NotAvailableOperationException("Non hai scelto l'evento");
        }

        $eventoSelezionato = $context->getUserSession()->getEventoScelto();

        //Controllo i diritti dell'utente.
        $dirittiUtente = $context->getUserSession()->getDirittiUtente();

        if(! $dirittiUtente->isCassiere()){
            throw new AuthorizationException("L'utente non è cassiere dello staff.");
        }

        parent::getPrinter()->addResults(Cassiere::getListaPrevenditeNonEntrate($eventoSelezionato->getId()));
    }

}

