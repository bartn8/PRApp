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
import com.prapp.PRAppApplication;
import com.prapp.model.db.wrapper.WStaff;
import com.prapp.model.db.wrapper.WUtente;
import com.prapp.model.net.Argomento;
import com.prapp.model.net.Eccezione;
import com.prapp.model.net.Richiesta;
import com.prapp.model.net.RichiestaVolley;
import com.prapp.model.net.Risultato;
import com.prapp.model.net.enums.Comando;
import com.prapp.model.net.wrapper.NetWId;
import com.prapp.model.net.wrapper.NetWLogin;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ManagerUtente extends Manager {

    private static final String LOGIN_ARG_LOGIN = "login";
    private static final String LOGIN_TOKEN_ARG_TOKEN = "token";
    private static final String SCEGLI_STAFF_ARG_STAFF = "staff";

    private static ManagerUtente singleton;

    public static synchronized ManagerUtente getInstance() {
        if (singleton == null)
            singleton = new ManagerUtente();

        return singleton;
    }

    private ManagerUtente() {
        super();
    }

    public ManagerUtente(URL indirizzo, Context context) {
        super(indirizzo);
    }

    public void registrazione() {
        //Non disponibile nella versione attuale di android
        throw new UnsupportedOperationException();
    }

    public void creaStaff() {
        //Non disponibile nella versione attuale di android
        throw new UnsupportedOperationException();
    }

    public void accediStaff() {
        //Non disponibile nella versione attuale di android
        throw new UnsupportedOperationException();
    }

    public void login(String username, String password, final Response.Listener<WUtente> onSuccess, final Response.Listener<List<Eccezione>> onException)  {
        Richiesta richiesta = new Richiesta(Comando.COMANDO_UTENTE_LOGIN);
        NetWLogin loginData = new NetWLogin(username, password);
        richiesta.aggiungiArgomento(new Argomento(LOGIN_ARG_LOGIN, loginData.getRemoteClassPath(), loginData));

        ResponseListener listener = new ResponseListener(Comando.COMANDO_UTENTE_LOGIN, element -> element.intValue() == 1, element -> onSuccess.onResponse(element.get(0).castRisultato(WUtente.class)), onException, errorListener);


        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

    public void logout(final Response.Listener<Void> onSuccess, final Response.Listener<List<Eccezione>> onException)  {
        Richiesta richiesta = new Richiesta(Comando.COMANDO_UTENTE_LOGOUT);

        ResponseListener listener = new ResponseListener(Comando.COMANDO_UTENTE_LOGOUT, element -> element.intValue() == 0, element -> onSuccess.onResponse(null), onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

    public void restituisciListaStaff(final Response.Listener<List<WStaff>> onSuccess, final Response.Listener<List<Eccezione>> onException)  {
        final Richiesta richiesta = new Richiesta(Comando.COMANDO_UTENTE_RESTITUISCI_LISTA_STAFF);

        ResponseListener listener = new ResponseListener(Comando.COMANDO_UTENTE_RESTITUISCI_LISTA_STAFF, element -> true, element -> {
            List<WStaff> myList = new ArrayList<>();

            for (Risultato risultato : element) {
                myList.add(risultato.castRisultato(WStaff.class));
            }

            onSuccess.onResponse(myList);
        }, onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

    public void restituisciListaStaffMembri(final Response.Listener<List<WStaff>> onSuccess, final Response.Listener<List<Eccezione>> onException)  {
        final Richiesta richiesta = new Richiesta(Comando.COMANDO_UTENTE_RESTITUISCI_LISTA_STAFF_MEMBRI);

        ResponseListener listener = new ResponseListener(Comando.COMANDO_UTENTE_RESTITUISCI_LISTA_STAFF_MEMBRI, element -> true, element -> {
            List<WStaff> myList = new ArrayList<>();

            for (Risultato risultato : element) {
                myList.add(risultato.castRisultato(WStaff.class));
            }

            onSuccess.onResponse(myList);
        }, onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

//    public void renewToken(final Response.Listener<WToken> onSuccess, final Response.Listener<List<Eccezione>> onException)  {
//        Comando comando = Comando.COMANDO_UTENTE_RENEW_TOKEN;
//        Richiesta richiesta = new Richiesta(comando);
//
//        ResponseListener listener = new ResponseListener(comando, element -> element.intValue() == 1, element -> onSuccess.onResponse(element.get(0).castRisultato(WToken.class)), onException, errorListener);
//
//
//        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);
//
//        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
//    }
//
//    public void getToken(final Response.Listener<WToken> onSuccess, final Response.Listener<List<Eccezione>> onException)  {
//        Comando comando = Comando.COMANDO_UTENTE_GET_TOKEN;
//        Richiesta richiesta = new Richiesta(comando);
//
//        ResponseListener listener = new ResponseListener(comando, element -> element.intValue() == 1, element -> onSuccess.onResponse(element.get(0).castRisultato(WToken.class)), onException, errorListener);
//
//
//        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);
//
//        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
//    }
//
//    public void loginWithToken(Integer idUtente, String token, final Response.Listener<WUtente> onSuccess, final Response.Listener<List<Eccezione>> onException)  {
//        Comando comando = Comando.COMANDO_UTENTE_LOGIN_TOKEN;
//        Richiesta richiesta = new Richiesta(comando);
//        NetWToken loginData = new NetWToken(idUtente, token);
//        richiesta.aggiungiArgomento(new Argomento(LOGIN_TOKEN_ARG_TOKEN, loginData.getRemoteClassPath(), loginData));
//
//        ResponseListener listener = new ResponseListener(comando, element -> element.intValue() == 1, element -> onSuccess.onResponse(element.get(0).castRisultato(WUtente.class)), onException, errorListener);
//
//
//        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);
//
//        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
//    }

    public void scegliStaff(WStaff staff, final Response.Listener<WStaff> onSuccess, final Response.Listener<List<Eccezione>> onException)  {
        Comando comando = Comando.COMANDO_UTENTE_SCEGLI_STAFF;
        Richiesta richiesta = new Richiesta(comando);

        NetWId idStaff = new NetWId(staff.getId());
        richiesta.aggiungiArgomento(new Argomento(SCEGLI_STAFF_ARG_STAFF, idStaff.getRemoteClassPath(), idStaff));

        ResponseListener listener = new ResponseListener(comando, element -> element.intValue() == 1, element -> onSuccess.onResponse(element.get(0).castRisultato(WStaff.class)), onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

}
