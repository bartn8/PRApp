<?php

/*
 * PRApp  Copyright (C) 2020  Luca Bartolomei
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

use InvalidArgumentException;
use com\model\db\enum\BasicEnum;
use com\model\db\enum\TipologiaUtente;

class TipologiaUtente extends BasicEnum
{

    public static function ofPCA($pca)
    {
        if (! is_int($pca))
            throw new InvalidArgumentException("PCA non è int.");
            
        $tmp = array();

        if ((($pca >> 1) & 1) == 1)
            $tmp[] = TipologiaUtente::of(TipologiaUtente::NORMALE);

        if ((($pca) & 1) == 1)
            $tmp[] = TipologiaUtente::of(TipologiaUtente::AMMINISTRATORE_SISTEMA);

        return $tmp;
    }

    public const NORMALE = 0;

    public const AMMINISTRATORE_SISTEMA = 1;

    public function __construct($name, $id)
    {
        parent::__construct($name, $id);
    }

}

