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
use com\handler\Retriver;
use com\view\printer\Printer;
use InvalidArgumentException;
use com\model\db\table\Membro;
use com\model\db\table\Utente;
use com\model\session\exception\SessionExpiredException;

abstract class Controller
{
    
    public const CMD_INDEFINITO = -1;

    /**
     * Oggetto per stampare.
     * @var Printer
     */
    private $printer;
    
    /**
     * Oggetto per recuperare argomenti aggiuntivi.
     * @var Retriver
     */
    private $retriver;
        
    protected function __construct( $printer, $retriver)
    {
        if(!($printer instanceof Printer))
            throw new InvalidArgumentException("Printer non valido");
        
        if(!($retriver instanceof Retriver))
            throw new InvalidArgumentException("Retriver non valido");
            
        $this->printer = $printer;
        $this->retriver = $retriver;
    }
    
    /**
     * Restituisce la stampante. 
     * @return \com\view\printer\Printer
     */
    protected function getPrinter() 
    {
        return $this->printer;
    }
    
    /**
     * Restitisce il retriver.
     * @return \com\handler\Retriver
     */
    protected function getRetriver() 
    {
        return $this->retriver;        
    }
            
    /**
     * Gestisce un comando.
     *
     * @param Command $command
     * @param array $args Array dei parametri grezzo
     */
    public function handle(Command $command){
        //Ricavo il contesto: verrà utilizzato sotto dai sotto controllori.
        $context = Context::getContext();

        if(!$context->isValid()){
            Context::createContext();
            //TRE ore di debug per sta linea fanculo
            $context = Context::getContext();
        }

        //Prima devo verificare se devo aggiornare il contesto.
        if($context->isWatchdogTriggered() && $context->isLogged()){
            $utente = $context->getUserSession()->getUtente();

            //Per prima cosa guardo se sono ancora dentro lo staff.
            //Dopo aver controllato che abbia scelto uno staff

            if($context->getUserSession()->isStaffScelto()){
                $staffScelto = $context->getUserSession()->getStaffScelto();
                $richiestaStaff = Utente::getStaff($utente->getId(), $staffScelto->getId());

                if($richiestaStaff == NULL){
                    //Sessione da aggiornare
                    $context->logout();
                    throw new SessionExpiredException("La sessione è scaduta (staff non disponibile)");
                }

                //Adesso controllo i ruoli: qui non è necessario rimuovere la sessione
                $ruoliAggiornati = Membro::getRuoliPersonali($utente->getId(), $staffScelto->getId());
                $context->getUserSession()->setRuoliMembro($ruoliAggiornati);


                //Poi controllo l'evento.
                //Dopo aver controllato che abbia scelto un evento

                if($context->getUserSession()->isEventoScelto()){
                    $eventoScelto = $context->getUserSession()->getEventoScelto();
                    $richiestaEvento = Membro::getEvento($utente->getId(), $staffScelto->getId(), $eventoScelto->getId());

                    if($richiestaEvento == NULL){
                        //Sessione da aggiornare
                        $context->logout();
                        throw new SessionExpiredException("La sessione è scaduta (evento non disponibile)");
                    }
                }
            }
            
            //Applico le modifiche.
            $context->apply();

            //Reset trigger
            $context->resetWatchdog();
        }

        $this->internalHandle($command, $context);
    }

    /**
     * Metodo implementato dalle classi controllori reali.
     * Passa il comando da elaborare e il contesto corrente.
     * 
     * @param Command comando da elaborare
     * @param Context contesto attuale.
     */
    protected abstract function internalHandle(Command $command, Context $context);
            
}

