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

namespace com\model\serialize;

/**
 * Indica che la classe possiede un metodo (statico) per costruire l'oggetto da un array.
 * 
 * @author Luca Bartolomei
 */
interface ArrayDeserializable
{
    //Costanti per la reflection.
    public const NAME = "com\\model\\serialize\\ArrayDeserializable";
    
    public const METHOD = "of";
    
    /**
     * Converte un array di stringhe in un oggetto ArrayDeserializable.
     *
     * @param array $array
     * @throws InvalidArgumentException i dati dell'array non sono validi oppure l'array stesso non � valido
     * @return ArrayDeserializable involucro dei dati convertito.
     */
    public static function of($array);
}

