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

namespace com\model\db\enum;

use com\model\net\serialize\ArrayDeserializable;

class StatoPrevendita extends BasicEnum /*implements JsonSerializable,ArrayDeserializable*/ 
{

    public const CONSEGNATA = 0;

    public const PAGATA = 1;

    public const ANNULLATA = 2;

    public const RIMBORSATA = 3;

    public function __construct($name, $id)
    {
        parent::__construct($name, $id);
    }

}
