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

namespace com\model\db\wrapper;

use com\model\net\serialize\NetSerializable;
use com\model\serialize\ArrayDeserializable;

/**
 * Un DatabaseWrapper è un involucro che continene una tabella del database.
 * Alcuni campi potrebbero essere nascosti per ragioni di sicurezza.
 * 
 * @author Luca Bartolomei
 */
interface DatabaseWrapper extends NetSerializable, ArrayDeserializable
{
}

