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

package com.prapp.model.net.manager;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prapp.PRAppApplication;
import com.prapp.model.net.Eccezione;
import com.prapp.model.net.Richiesta;
import com.prapp.model.net.RichiestaVolley;
import com.prapp.model.net.enums.Comando;
import com.prapp.model.net.serialize.adapter.DateTimeAdapter;

import org.joda.time.DateTime;

import java.net.URL;
import java.util.List;

public class ManagerManutenzione extends Manager {

    private static ManagerManutenzione singleton;

    public static synchronized ManagerManutenzione getInstance() {
        if (singleton == null)
            singleton = new ManagerManutenzione();

        return singleton;
    }

    private ManagerManutenzione() {
        super();
    }

    public ManagerManutenzione(URL indirizzo) {
        super(indirizzo);
    }

    public void echo(final Response.Listener<List<Eccezione>> onException)  {
        Richiesta richiesta = new Richiesta(Comando.COMANDO_MANUTENZIONE_ECHO);

        ResponseListener listener = new ResponseListener(Comando.COMANDO_MANUTENZIONE_ECHO, element -> element.intValue() == 0, element -> {
            //Non fare nulla.
        }, onException, errorListener);


        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

    public void restituisciDateTimeServer(final Response.Listener<DateTime> onSuccess, final Response.Listener<List<Eccezione>> onException)  {
        final Richiesta richiesta = new Richiesta(Comando.COMANDO_MANUTENZIONE_TIMESTAMP);

        ResponseListener listener = new ResponseListener(Comando.COMANDO_MANUTENZIONE_TIMESTAMP, element -> element.intValue() == 1, element -> {
            Gson myGson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeAdapter()).create();
            DateTime dateTime = element.get(0).castRisultato(myGson, DateTime.class);
            onSuccess.onResponse(dateTime);
        }, onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

}
