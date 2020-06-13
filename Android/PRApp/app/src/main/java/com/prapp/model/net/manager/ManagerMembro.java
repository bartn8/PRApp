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
import com.prapp.PRAppApplication;
import com.prapp.model.db.wrapper.WRuoliMembro;
import com.prapp.model.db.wrapper.WEvento;
import com.prapp.model.db.wrapper.WTipoPrevendita;
import com.prapp.model.db.wrapper.WUtente;
import com.prapp.model.net.Argomento;
import com.prapp.model.net.Eccezione;
import com.prapp.model.net.Richiesta;
import com.prapp.model.net.RichiestaVolley;
import com.prapp.model.net.Risultato;
import com.prapp.model.net.enums.Comando;
import com.prapp.model.net.wrapper.NetWId;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ManagerMembro extends Manager {

    private static final String RESTITUISCI_LISTA_UTENTI_ARG_STAFF = "staff";
    private static final String RESTITUISCI_DIRITTI_PERSONALI_ARG_STAFF = "staff";
    private static final String RESTITUISCI_DIRITTI_UTENTE_ARG_STAFF = "staff";
    private static final String RESTITUISCI_DIRITTI_UTENTE_ARG_UTENTE = "utente";
    private static final String RESTITUISCI_LISTA_EVENTI_ARG_STAFF = "staff";
    private static final String RESTITUISCI_TIPI_PREVENDITA_ARG_EVENTO = "evento";
    private static final String RESTITUISCI_LISTA_CLIENTI_ARG_STAFF = "staff";

    private static ManagerMembro singleton;

    public static synchronized ManagerMembro getInstance() {
        if (singleton == null)
            singleton = new ManagerMembro();

        return singleton;
    }

    private ManagerMembro() {
        super();
    }

    public ManagerMembro(URL indirizzo) {
        super(indirizzo);
    }

    public void restituisciListaUtentiStaff(int idStaff, final Response.Listener<List<WUtente>> onSuccess, final Response.Listener<List<Eccezione>> onException)  {
        Comando comando = Comando.COMANDO_MEMBRO_RESTITUISCI_LISTA_UTENTI;
        final Richiesta richiesta = new Richiesta(comando);
        NetWId netWId = new NetWId(idStaff);
        richiesta.aggiungiArgomento(new Argomento(RESTITUISCI_LISTA_UTENTI_ARG_STAFF, netWId.getRemoteClassPath(), netWId));


        ResponseListener listener = new ResponseListener(comando, element -> true, element -> {
            List<WUtente> myList = new ArrayList<>();

            for (Risultato risultato : element) {
                myList.add(risultato.castRisultato(WUtente.class));
            }

            onSuccess.onResponse(myList);
        }, onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

    public void restituisciDirittiPersonaliStaff(int idStaff, final Response.Listener<WRuoliMembro> onSuccess, final Response.Listener<List<Eccezione>> onException)  {
        Comando comando = Comando.COMANDO_MEMBRO_RESTITUISCI_DIRITTI_PERSONALI;
        final Richiesta richiesta = new Richiesta(comando);
        NetWId netWId = new NetWId(idStaff);
        richiesta.aggiungiArgomento(new Argomento(RESTITUISCI_DIRITTI_PERSONALI_ARG_STAFF, netWId.getRemoteClassPath(), netWId));


        ResponseListener listener = new ResponseListener(comando, element -> element.intValue() == 1, element -> onSuccess.onResponse(element.get(0).castRisultato(WRuoliMembro.class)), onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

    public void restituisciDirittiUtenteStaff(int idUtente, int idStaff, final Response.Listener<WRuoliMembro> onSuccess, final Response.Listener<List<Eccezione>> onException)  {
        Comando comando = Comando.COMANDO_MEMBRO_RESTITUISCI_DIRITTI_UTENTE;
        final Richiesta richiesta = new Richiesta(comando);
        NetWId netWIdUtente = new NetWId(idUtente);
        richiesta.aggiungiArgomento(new Argomento(RESTITUISCI_DIRITTI_UTENTE_ARG_UTENTE, netWIdUtente.getRemoteClassPath(), netWIdUtente));
        NetWId netWIdStaff = new NetWId(idStaff);
        richiesta.aggiungiArgomento(new Argomento(RESTITUISCI_DIRITTI_UTENTE_ARG_STAFF, netWIdStaff.getRemoteClassPath(), netWIdStaff));


        ResponseListener listener = new ResponseListener(comando, element -> element.intValue() == 1, element -> onSuccess.onResponse(element.get(0).castRisultato(WRuoliMembro.class)), onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

    public void restituisciListaEventiStaff(int idStaff, final Response.Listener<List<WEvento>> onSuccess, final Response.Listener<List<Eccezione>> onException)  {
        Comando comando = Comando.COMANDO_MEMBRO_RESTITUISCI_LISTA_EVENTI;
        final Richiesta richiesta = new Richiesta(comando);
        NetWId netWId = new NetWId(idStaff);
        richiesta.aggiungiArgomento(new Argomento(RESTITUISCI_LISTA_EVENTI_ARG_STAFF, netWId.getRemoteClassPath(), netWId));


        ResponseListener listener = new ResponseListener(comando, element -> true, element -> {
            List<WEvento> myList = new ArrayList<>();

            for (Risultato risultato : element) {
                myList.add(risultato.castRisultato(WEvento.class));
            }

            onSuccess.onResponse(myList);
        }, onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

    public void restituisciListaTipiPrevenditaEvento(int idEvento, final Response.Listener<List<WTipoPrevendita>> onSuccess, final Response.Listener<List<Eccezione>> onException)  {
        Comando comando = Comando.COMANDO_MEMBRO_RESTITUISCI_TIPI_PREVENDITA;
        final Richiesta richiesta = new Richiesta(comando);
        NetWId netWId = new NetWId(idEvento);
        richiesta.aggiungiArgomento(new Argomento(RESTITUISCI_TIPI_PREVENDITA_ARG_EVENTO, netWId.getRemoteClassPath(), netWId));


        ResponseListener listener = new ResponseListener(comando, element -> true, element -> {
            List<WTipoPrevendita> myList = new ArrayList<>();

            for (Risultato risultato : element) {
                myList.add(risultato.castRisultato(WTipoPrevendita.class));
            }

            onSuccess.onResponse(myList);
        }, onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

}
