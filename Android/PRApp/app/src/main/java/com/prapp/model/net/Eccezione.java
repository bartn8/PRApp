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

package com.prapp.model.net;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Eccezione {

    private static HashMap<String, Class<? extends Exception>> mapper = new HashMap<>();

    public static void register(String classPath, Class<? extends Exception> clazz)
    {
        mapper.put(classPath, clazz);
    }

    public static List<Exception> convertiInExceptions(@NotNull List<Eccezione> excpz)
    {
        //Converto la lista di eccezioni in Exception
        List<Exception> exceptions = new ArrayList<>();

        try {
            for (Eccezione eccezione : excpz) {
                exceptions.add(eccezione.getException());
            }
        } catch (InvocationTargetException | InstantiationException | NoSuchMethodException | IllegalAccessException e) {
            exceptions.add(e);
        }

        return exceptions;
    }

    @SerializedName("file")
    private String file;

    @SerializedName("line")
    private int linea;

    @SerializedName("type")
    private String tipo;

    @SerializedName("msg")
    private String messaggio;

    public Exception getException() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<? extends Exception> clazz = mapper.get(tipo);

        //Default value
        if(clazz == null)
            clazz = Exception.class;

        Constructor<? extends Exception> constructor = clazz.getConstructor(String.class);
        return constructor.newInstance(messaggio);
    }

}
