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

import android.content.Context;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prapp.model.net.CodaRichiesteSingleton;
import com.prapp.model.net.Eccezione;
import com.prapp.model.net.Richiesta;
import com.prapp.model.net.RichiestaVolley;
import com.prapp.model.net.Risultato;
import com.prapp.model.net.enums.Comando;
import com.prapp.model.net.serialize.adapter.DateTimeAdapter;
import com.prapp.model.util.functional.Consumer;
import com.prapp.model.util.functional.Predicate;

import org.joda.time.DateTime;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;

public class ManagerManutenzione extends Manager {

    private static ManagerManutenzione singleton;

    public static synchronized ManagerManutenzione getInstance(Context context)
    {
        if(singleton == null)
            singleton = new ManagerManutenzione(context);

        return singleton;
    }

    public ManagerManutenzione(Context context) {
        super(context);
    }

    public ManagerManutenzione(URL indirizzo, Context context) {
        super(indirizzo, context);
    }

    public void echo(final Response.Listener<List<Eccezione>> onException) throws UnsupportedEncodingException {
        Richiesta richiesta = new Richiesta(Comando.COMANDO_MANUTENZIONE_ECHO);

        ResponseListener listener = new ResponseListener(Comando.COMANDO_MANUTENZIONE_ECHO, new Predicate<Integer>() {
            @Override
            public boolean predict(Integer element) {
                return element.intValue() == 0;
            }
        }, new Consumer<List<Risultato>>() {
            @Override
            public void supply(List<Risultato> element) {
                //Non fare nulla.
            }
        }, onException, errorListener);


        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        CodaRichiesteSingleton.getInstance(context).addToRequestQueue(richiestaVolley);
    }

    public void restituisciDateTimeServer(final Response.Listener<DateTime> onSuccess, final Response.Listener<List<Eccezione>> onException) throws UnsupportedEncodingException {
        final Richiesta richiesta = new Richiesta(Comando.COMANDO_MANUTENZIONE_TIMESTAMP);

        ResponseListener listener = new ResponseListener(Comando.COMANDO_MANUTENZIONE_TIMESTAMP, new Predicate<Integer>() {
            @Override
            public boolean predict(Integer element) {
                return element.intValue() == 1;
            }
        }, new Consumer<List<Risultato>>() {
            @Override
            public void supply(List<Risultato> element) {
                Gson myGson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeAdapter()).create();
                DateTime dateTime = element.get(0).castRisultato(myGson, DateTime.class);
                onSuccess.onResponse(dateTime);
            }
        }, onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        CodaRichiesteSingleton.getInstance(context).addToRequestQueue(richiestaVolley);
    }

}
