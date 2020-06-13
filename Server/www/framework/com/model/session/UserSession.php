<?php
/*
 * Copyright 2018 Luca Bartolomei bartn8@hotmail.it.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

namespace com\model\session;

use com\model\db\wrapper\WUtente;
use com\model\db\wrapper\WEvento;
use com\model\db\wrapper\WStaff;
use com\model\db\wrapper\WRuoliMembro;

class UserSession {
        
    
    /**
     * Utente della sessione.
     * @var WUtente
     */
    private $utente;
    
    /**
     * Staff scelto dall'utente.
     * @var WStaff|NULL
     */
    private $staffScelto;
    
    /**
     * Evento scelto dall'utente
     * @var WEvento|NULL
     */
    private $eventoScelto;
    
    /**
     * Ruoli del membro nello staff scelto.
     * @var WRuoliMembro|NULL
     */
    private $ruoliMembro;
    
    public function __construct($utente){
        $this->utente = $utente;
    }
    
    /**
     * @return \com\model\db\wrapper\WUtente|NULL
     */
    public function getUtente()
    {
        return $this->utente;
    }

    /**
     * @return \com\model\db\wrapper\WStaff|NULL
     */
    public function getStaffScelto()
    {
        return $this->staffScelto;
    }

    /**
     * @return \com\model\db\wrapper\WEvento|NULL
     */
    public function getEventoScelto()
    {
        return $this->eventoScelto;
    }

    //Non metto il setter dell'utente, unico per la sessione.
    
    /**
     * @param \com\model\db\wrapper\WStaff $staffScelto
     */
    public function setStaffScelto($staffScelto)
    {
        $this->staffScelto = $staffScelto;
    }

    /**
     * @param \com\model\db\wrapper\WEvento $eventoScelto
     */
    public function setEventoScelto($eventoScelto)
    {
        $this->eventoScelto = $eventoScelto;
    }
    
    /**
     * @return Ambigous <\com\model\db\wrapper\WRuoliMembro, NULL>
     */
    public function getRuoliMembro()
    {
        return $this->ruoliMembro;
    }

    /**
     * @param \com\model\db\wrapper\WRuoliMembro $ruoliMembro
     */
    public function setRuoliMembro($ruoliMembro)
    {
        $this->ruoliMembro = $ruoliMembro;
    }

    /**
     * Indica se l'utente ha scelto l'evento di sessione.
     */
    public function isEventoScelto(){
        return $this->eventoScelto != NULL;
    }
    
    /**
     * Indica se l'utente ha scelto lo staff di sessione
     */
    public function isStaffScelto(){
        return $this->staffScelto != NULL;
    }
    
}