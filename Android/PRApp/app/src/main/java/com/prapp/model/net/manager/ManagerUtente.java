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
import com.prapp.model.db.wrapper.WStaff;
import com.prapp.model.db.wrapper.WToken;
import com.prapp.model.db.wrapper.WUtente;
import com.prapp.model.net.Argomento;
import com.prapp.model.net.CodaRichiesteSingleton;
import com.prapp.model.net.Eccezione;
import com.prapp.model.net.Richiesta;
import com.prapp.model.net.RichiestaVolley;
import com.prapp.model.net.Risultato;
import com.prapp.model.net.enums.Comando;
import com.prapp.model.net.wrapper.NetWLogin;
import com.prapp.model.net.wrapper.NetWToken;
import com.prapp.model.util.functional.Consumer;
import com.prapp.model.util.functional.Predicate;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ManagerUtente extends Manager {

    public static final String LOGIN_ARG_LOGIN = "login";
    public static final String LOGIN_TOKEN_ARG_TOKEN = "token";

    private static ManagerUtente singleton;

    public static synchronized ManagerUtente getInstance(Context context)
    {
        if(singleton == null)
            singleton = new ManagerUtente(context);

        return singleton;
    }

    public ManagerUtente(Context context) {
        super(context);
    }

    public ManagerUtente(URL indirizzo, Context context) {
        super(indirizzo, context);
    }

    public void registrazione()
    {
        //Non disponibile nella versione attuale di android
        throw new UnsupportedOperationException();
    }

    public void creaStaff()
    {
        //Non disponibile nella versione attuale di android
        throw new UnsupportedOperationException();
    }

    public void accediStaff()
    {
        //Non disponibile nella versione attuale di android
        throw new UnsupportedOperationException();
    }

    public void login(String username, String password, final Response.Listener<WUtente> onSuccess, final Response.Listener<List<Eccezione>> onException) throws UnsupportedEncodingException {
        Richiesta richiesta = new Richiesta(Comando.COMANDO_UTENTE_LOGIN);
        NetWLogin loginData = new NetWLogin(username, password);
        richiesta.aggiungiArgomento(new Argomento(LOGIN_ARG_LOGIN, loginData.getRemoteClassPath(), loginData));

        ResponseListener listener = new ResponseListener(Comando.COMANDO_UTENTE_LOGIN, new Predicate<Integer>() {
            @Override
            public boolean predict(Integer element) {
                return element.intValue() == 1;
            }
        }, new Consumer<List<Risultato>>() {
            @Override
            public void supply(List<Risultato> element) {
                onSuccess.onResponse(element.get(0).castRisultato(WUtente.class));
            }
        }, onException, errorListener);


        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        CodaRichiesteSingleton.getInstance(context).addToRequestQueue(richiestaVolley);
    }

    public void logout(final Response.Listener<Void> onSuccess, final Response.Listener<List<Eccezione>> onException) throws UnsupportedEncodingException {
        Richiesta richiesta = new Richiesta(Comando.COMANDO_UTENTE_LOGOUT);

        ResponseListener listener = new ResponseListener(Comando.COMANDO_UTENTE_LOGOUT, new Predicate<Integer>() {
            @Override
            public boolean predict(Integer element) {
                return element.intValue() == 0;
            }
        }, new Consumer<List<Risultato>>() {
            @Override
            public void supply(List<Risultato> element) {
                onSuccess.onResponse(null);
            }
        }, onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        CodaRichiesteSingleton.getInstance(context).addToRequestQueue(richiestaVolley);
    }

    public void restituisciListaStaff(final Response.Listener<List<WStaff>> onSuccess, final Response.Listener<List<Eccezione>> onException) throws UnsupportedEncodingException {
        final Richiesta richiesta = new Richiesta(Comando.COMANDO_UTENTE_RESTITUISCI_LISTA_STAFF);

        ResponseListener listener = new ResponseListener(Comando.COMANDO_UTENTE_RESTITUISCI_LISTA_STAFF, new Predicate<Integer>() {
            @Override
            public boolean predict(Integer element) {
                return true;
            }
        }, new Consumer<List<Risultato>>() {
            @Override
            public void supply(List<Risultato> element) {
                List<WStaff> myList = new ArrayList<>();

                for(Risultato risultato : element)
                {
                    myList.add(risultato.castRisultato(WStaff.class));
                }

                onSuccess.onResponse(myList);
            }
        }, onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        CodaRichiesteSingleton.getInstance(context).addToRequestQueue(richiestaVolley);
    }

    public void restituisciListaStaffMembri(final Response.Listener<List<WStaff>> onSuccess, final Response.Listener<List<Eccezione>> onException) throws UnsupportedEncodingException {
        final Richiesta richiesta = new Richiesta(Comando.COMANDO_UTENTE_RESTITUISCI_LISTA_STAFF_MEMBRI);

        ResponseListener listener = new ResponseListener(Comando.COMANDO_UTENTE_RESTITUISCI_LISTA_STAFF_MEMBRI, new Predicate<Integer>() {
            @Override
            public boolean predict(Integer element) {
                return true;
            }
        }, new Consumer<List<Risultato>>() {
            @Override
            public void supply(List<Risultato> element) {
                List<WStaff> myList = new ArrayList<>();

                for(Risultato risultato : element)
                {
                    myList.add(risultato.castRisultato(WStaff.class));
                }

                onSuccess.onResponse(myList);
            }
        }, onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        CodaRichiesteSingleton.getInstance(context).addToRequestQueue(richiestaVolley);
    }

    public void renewToken(final Response.Listener<WToken> onSuccess, final Response.Listener<List<Eccezione>> onException) throws UnsupportedEncodingException {
        Comando comando = Comando.COMANDO_UTENTE_RENEW_TOKEN;
        Richiesta richiesta = new Richiesta(comando);

        ResponseListener listener = new ResponseListener(comando, new Predicate<Integer>() {
            @Override
            public boolean predict(Integer element) {
                return element.intValue() == 1;
            }
        }, new Consumer<List<Risultato>>() {
            @Override
            public void supply(List<Risultato> element) {
                onSuccess.onResponse(element.get(0).castRisultato(WToken.class));
            }
        }, onException, errorListener);


        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        CodaRichiesteSingleton.getInstance(context).addToRequestQueue(richiestaVolley);
    }

    public void getToken(final Response.Listener<WToken> onSuccess, final Response.Listener<List<Eccezione>> onException) throws UnsupportedEncodingException {
        Comando comando = Comando.COMANDO_UTENTE_GET_TOKEN;
        Richiesta richiesta = new Richiesta(comando);

        ResponseListener listener = new ResponseListener(comando, new Predicate<Integer>() {
            @Override
            public boolean predict(Integer element) {
                return element.intValue() == 1;
            }
        }, new Consumer<List<Risultato>>() {
            @Override
            public void supply(List<Risultato> element) {
                onSuccess.onResponse(element.get(0).castRisultato(WToken.class));
            }
        }, onException, errorListener);


        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        CodaRichiesteSingleton.getInstance(context).addToRequestQueue(richiestaVolley);
    }

    public void loginWithToken(String token, final Response.Listener<WUtente> onSuccess, final Response.Listener<List<Eccezione>> onException) throws UnsupportedEncodingException {
        Comando comando = Comando.COMANDO_UTENTE_LOGIN_TOKEN;
        Richiesta richiesta = new Richiesta(comando);
        NetWToken loginData = new NetWToken(token);
        richiesta.aggiungiArgomento(new Argomento(LOGIN_TOKEN_ARG_TOKEN, loginData.getRemoteClassPath(), loginData));

        ResponseListener listener = new ResponseListener(comando, new Predicate<Integer>() {
            @Override
            public boolean predict(Integer element) {
                return element.intValue() == 1;
            }
        }, new Consumer<List<Risultato>>() {
            @Override
            public void supply(List<Risultato> element) {
                onSuccess.onResponse(element.get(0).castRisultato(WUtente.class));
            }
        }, onException, errorListener);


        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        CodaRichiesteSingleton.getInstance(context).addToRequestQueue(richiestaVolley);
    }

}