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


namespace com\handler;

use com\control\ControllerUtente;
use com\control\ControllerMembro;
use com\control\ControllerPR;
use com\control\ControllerCassiere;
use com\control\ControllerAmministratore;
use InvalidArgumentException;
use com\view\printer\Printer;
use com\model\exception\ParseException;
use com\model\db\exception\AuthorizationException;
use com\model\db\exception\NotAvailableOperationException;
use com\control\ControllerManutenzione;
use com\model\db\exception\DatabaseException;
use com\model\db\exception\InsertUpdateException;

class HTTPHandler implements Retriver
{

    // Divisione dei comandi: (1-100 utente) (101-200 membro) (201-300 pr) (301-400 cassiere) (401-500 amministratore) (951-1000 manutenzione)
    private $rangeUtente;

    private $rangeMembro;

    private $rangePR;

    private $rangeCassiere;

    private $rangeAmministratore;

    private $rangeManutenzione;

    private $printer;

    public function __construct($printer)
    {
        if (! ($printer instanceof Printer))
            throw new InvalidArgumentException("Printer non valida.");
        
        $this->rangeUtente = range(1, 100);
        $this->rangeMembro = range(101, 200);
        $this->rangePR = range(201, 300);
        $this->rangeCassiere = range(301, 400);
        $this->rangeAmministratore = range(401, 500);
        $this->rangeManutenzione = range(951, 1000);
        
        $this->printer = $printer;
    }

    public function handle()
    {
        // TODO: Bisogna fare un catch multiplo per le eccezioni perchè non le becca.
        $this->printer->reset();
		$this->printer->setHTTPHeader();
        
        $command = $this->retrive("command");
        $args = $this->retrive("args");
        
        $command = filter_var($command, FILTER_SANITIZE_NUMBER_INT);
        
        // Controllo sul comando.
        if ($command === FALSE || $command === NULL) {
            $this->printer->setCommand(Controller::CMD_INDEFINITO);
            $this->printer->setStatus(Printer::STATUS_ECCEZIONE);
            $this->printer->addException(new InvalidArgumentException("Comando non inserito o non corretto"));
            $this->printer->flush();
            return;
        }
        
        // Parsing del comando
        $command = (int) $command;
       
        // Posso digitare di già il comando.
        $this->printer->setCommand($command);
        
        // Controllo sugli argomenti.
        if ($args === FALSE) {
            $this->printer->setStatus(Printer::STATUS_ECCEZIONE);
            $this->printer->addException(new InvalidArgumentException("Args non corretti"));
            $this->printer->flush();
            return;
        }
        
        // Se non ci sono argomenti stringa vuota
        if ($args === NULL) {
            $args = "";
        }
                       
        try {
            $parsedCommand = Command::of($command, $args);
            
            if (in_array($command, $this->rangeUtente)) {
                $utente = new ControllerUtente($this->printer, $this);
                $utente->handle($parsedCommand);
            } 
            else if (in_array($command, $this->rangeMembro)) {
                $membro = new ControllerMembro($this->printer, $this);
                $membro->handle($parsedCommand);
            } 
            else if (in_array($command, $this->rangePR)) {
                $pr = new ControllerPR($this->printer, $this);
                $pr->handle($parsedCommand);
            } 
            else if (in_array($command, $this->rangeCassiere)) {
                $cassiere = new ControllerCassiere($this->printer, $this);
                $cassiere->handle($parsedCommand);
            } 
            else if (in_array($command, $this->rangeAmministratore)) {
                $amministratore = new ControllerAmministratore($this->printer, $this);
                $amministratore->handle($parsedCommand);
            } 
            else if (in_array($command, $this->rangeManutenzione)) {
                $manutenzione = new ControllerManutenzione($this->printer, $this);
                $manutenzione->handle($parsedCommand);
            } 
            else {
                $this->printer->setStatus(Printer::STATUS_NON_TROVATO);
            }
        } catch (DatabaseException $ex) {
            $this->printer->setStatus(Printer::STATUS_ECCEZIONE);
            $this->printer->addException(new DatabaseException("Errore nel database"));/*$ex->getMessage()*/
            
        } catch (SessionExpiredException | ParseException | InvalidArgumentException | AuthorizationException | InsertUpdateException | NotAvailableOperationException | \PDOException $ex) {//TODO: da spostare PDO sopra!
            $this->printer->setStatus(Printer::STATUS_ECCEZIONE);
            $this->printer->addException($ex);
            
        } finally {
            //var_dump($this->printer);
            $this->printer->flush();
        }
    }

    public function retrive($arg)
    {
        return filter_input(INPUT_POST, $arg, FILTER_DEFAULT);
    }

    public function retriveArray($arg)
    {
        return filter_input(INPUT_POST, $arg, FILTER_DEFAULT, FILTER_REQUIRE_ARRAY);
    }
}

