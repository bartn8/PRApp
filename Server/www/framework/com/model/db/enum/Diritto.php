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

use InvalidArgumentException;

class Diritto extends BasicEnum
{

    public static function ofPCA($pca)
    {
        if (! is_int($pca))
            throw new InvalidArgumentException("PCA non è int.");
            
        $tmp = array();

        if ((($pca >> 2) & 1) == 1)
            $tmp[] = Diritto::of(Diritto::PR);

        if ((($pca >> 1) & 1) == 1)
            $tmp[] = Diritto::of(Diritto::CASSIERE);

        if ((($pca) & 1) == 1)
            $tmp[] = Diritto::of(Diritto::AMMINISTRATORE);

        return $tmp;
    }

    public const PR = 0;

    public const CASSIERE = 1;

    public const AMMINISTRATORE = 2;

    public function __construct($name, $id)
    {
        parent::__construct($name, $id);
    }

}

