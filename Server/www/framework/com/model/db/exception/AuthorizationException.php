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

namespace com\model\db\exception;

use Exception;

/**
 * Classe exception usata quando si cerca di accedere a dati autorizzati.
 */
class AuthorizationException extends Exception
{

    // Redefine the exception so message isn't optional
    public function __construct($message, $code = 0, Exception $previous = null)
    {
        // some code
        
        // make sure everything is assigned properly
        parent::__construct($message, $code, $previous);
    }

    // custom string representation of object
    public function __toString()
    {
        return __CLASS__ . ": [{$this->code}]: {$this->message}\n";
    }
}

