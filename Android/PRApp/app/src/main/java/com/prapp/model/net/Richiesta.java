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

import android.util.Log;

import com.google.gson.Gson;
import com.prapp.model.net.enums.Comando;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Richiesta {

    private static final String TAG = Richiesta.class.getSimpleName();

    private static final Gson GSON_OBJECT = new Gson();

    private Comando comando;
    private List<Argomento> argomenti;


    public Richiesta(Comando comando, List<Argomento> argomenti) {
        this.comando = comando;
        this.argomenti = argomenti;
    }

    public Richiesta(Comando comando) {
        this(comando, new ArrayList<Argomento>());
    }

    public Richiesta(Comando comando, Argomento[] argomenti) {
        this(comando, Arrays.asList(argomenti));
    }

    public void aggiungiArgomento(Argomento arg)
    {
        argomenti.add(arg);
    }

    public Comando getComando() {
        return comando;
    }

    public int getComandoAsInt() {
        return comando.getComando();
    }

    public List<Argomento> getArgomenti() {
        return argomenti;
    }

    public String getArgomentiAsJsonString()
    {
        return getArgomentiAsJsonString(GSON_OBJECT);
    }

    public String getArgomentiAsJsonString(Gson gson)
    {
        return gson.toJson(argomenti);
    }

    //https://stackoverflow.com/questions/9767952/how-to-add-parameters-to-httpurlconnection-using-post-using-namevaluepair
    public String generatePOSTQuery(Gson gson) {
        StringBuilder result = new StringBuilder();

        result.append("command=").append(comando.getComando()).append("&");

        try {
            result.append("args=").append(URLEncoder.encode(getArgomentiAsJsonString(gson), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "UTF-8 not recognised", e);
        }

        return result.toString();
    }

    public String generatePOSTQuery() {
        return generatePOSTQuery(GSON_OBJECT);
    }


}
