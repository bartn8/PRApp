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
import com.prapp.model.db.wrapper.WCliente;
import com.prapp.model.db.wrapper.WPrevendita;
import com.prapp.model.db.wrapper.WPrevenditaPlus;
import com.prapp.model.db.wrapper.WStatistichePREvento;
import com.prapp.model.db.wrapper.WStatistichePRStaff;
import com.prapp.model.db.wrapper.WStatistichePRTotali;
import com.prapp.model.net.Argomento;
import com.prapp.model.net.Eccezione;
import com.prapp.model.net.Richiesta;
import com.prapp.model.net.RichiestaVolley;
import com.prapp.model.net.Risultato;
import com.prapp.model.net.enums.Comando;
import com.prapp.model.net.wrapper.NetWFiltriStatoPrevendita;
import com.prapp.model.net.wrapper.NetWId;
import com.prapp.model.net.wrapper.insert.InsertNetWCliente;
import com.prapp.model.net.wrapper.insert.InsertNetWPrevendita;
import com.prapp.model.net.wrapper.update.UpdateNetWPrevendita;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ManagerPR extends Manager {

    private static final String AGGIUNGI_CLIENTE_ARG_CLIENTE = "cliente";
    private static final String AGGIUNGI_PREVENDITA_ARG_PREVENDITA = "prevendita";
    private static final String MODIFICA_PREVENDITA_ARG_PREVENDITA = "prevendita";
    private static final String RESTITUISCI_LISTA_PREVENDITE_ARG_FILTRI = "filtri";
    private static final String RESTITUISCI_STATISTICHE_STAFF_ARG_STAFF = "staff";
    private static final String RESTITUISCI_STATISTICHE_EVENTO_ARG_EVENTO = "evento";
    private static final String RESTITUISCI_PREVENDITE_EVENTO_ARG_EVENTO = "evento";

    private static ManagerPR singleton;

    public static synchronized ManagerPR getInstance() {
        if (singleton == null)
            singleton = new ManagerPR();

        return singleton;
    }

    private ManagerPR() {
        super();
    }

    public ManagerPR(URL indirizzo, Context context) {
        super(indirizzo);
    }

    public void aggiungiCliente(InsertNetWCliente newCliente, final Response.Listener<WCliente> onSuccess, final Response.Listener<List<Eccezione>> onException)  {
        Comando comando = Comando.COMANDO_PR_AGGIUNGI_CLIENTE;
        final Richiesta richiesta = new Richiesta(comando);
        richiesta.aggiungiArgomento(new Argomento(AGGIUNGI_CLIENTE_ARG_CLIENTE, newCliente.getRemoteClassPath(), newCliente));


        ResponseListener listener = new ResponseListener(comando, element -> element.intValue() == 1, element -> onSuccess.onResponse(element.get(0).castRisultato(WCliente.class)), onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

    public void aggiungiPrevendita(InsertNetWPrevendita newPrevendita, final Response.Listener<WPrevendita> onSuccess, final Response.Listener<List<Eccezione>> onException)  {
        Comando comando = Comando.COMANDO_PR_AGGIUNGI_PREVENDITA;
        final Richiesta richiesta = new Richiesta(comando);
        richiesta.aggiungiArgomento(new Argomento(AGGIUNGI_PREVENDITA_ARG_PREVENDITA, newPrevendita.getRemoteClassPath(), newPrevendita));


        ResponseListener listener = new ResponseListener(comando, element -> element.intValue() == 1, element -> onSuccess.onResponse(element.get(0).castRisultato(WPrevendita.class)), onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

    public void modificaPrevendita(UpdateNetWPrevendita updatePrevendita, final Response.Listener<WPrevendita> onSuccess, final Response.Listener<List<Eccezione>> onException)  {
        Comando comando = Comando.COMANDO_PR_MODIFICA_PREVENDITA;
        final Richiesta richiesta = new Richiesta(comando);
        richiesta.aggiungiArgomento(new Argomento(MODIFICA_PREVENDITA_ARG_PREVENDITA, updatePrevendita.getRemoteClassPath(), updatePrevendita));


        ResponseListener listener = new ResponseListener(comando, element -> element.intValue() == 1, element -> onSuccess.onResponse(element.get(0).castRisultato(WPrevendita.class)), onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

    public void restituisciListaPrevendite(NetWFiltriStatoPrevendita filtri, final Response.Listener<List<WPrevendita>> onSuccess, final Response.Listener<List<Eccezione>> onException)  {
        Comando comando = Comando.COMANDO_PR_RESTITUISCI_PREVENDITE;
        final Richiesta richiesta = new Richiesta(comando);
        richiesta.aggiungiArgomento(new Argomento(RESTITUISCI_LISTA_PREVENDITE_ARG_FILTRI, filtri.getRemoteClassPath(), filtri));


        ResponseListener listener = new ResponseListener(comando, element -> true, element -> {
            List<WPrevendita> myList = new ArrayList<>();

            for (Risultato risultato : element) {
                myList.add(risultato.castRisultato(WPrevendita.class));
            }

            onSuccess.onResponse(myList);
        }, onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

    public void resitituisciStatisticheTotali(final Response.Listener<WStatistichePRTotali> onSuccess, final Response.Listener<List<Eccezione>> onException)  {
        Comando comando = Comando.COMANDO_PR_RESTITUISCI_STATISTICHE_PR_TOTALI;
        final Richiesta richiesta = new Richiesta(comando);

        ResponseListener listener = new ResponseListener(comando, element -> element.intValue() == 1, element -> onSuccess.onResponse(element.get(0).castRisultato(WStatistichePRTotali.class)), onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

    public void resitituisciStatisticheStaff(int idStaff, final Response.Listener<WStatistichePRStaff> onSuccess, final Response.Listener<List<Eccezione>> onException)  {
        Comando comando = Comando.COMANDO_PR_RESTITUISCI_STATISTICHE_PR_STAFF;
        final Richiesta richiesta = new Richiesta(comando);
        NetWId netWId = new NetWId(idStaff);
        richiesta.aggiungiArgomento(new Argomento(RESTITUISCI_STATISTICHE_STAFF_ARG_STAFF, netWId.getRemoteClassPath(), netWId));

        ResponseListener listener = new ResponseListener(comando, element -> element.intValue() == 1, element -> onSuccess.onResponse(element.get(0).castRisultato(WStatistichePRStaff.class)), onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

    public void resitituisciStatisticheEvento(int idEvento, final Response.Listener<WStatistichePREvento> onSuccess, final Response.Listener<List<Eccezione>> onException)  {
        Comando comando = Comando.COMANDO_PR_RESTITUISCI_STATISTICHE_PR_EVENTO;
        final Richiesta richiesta = new Richiesta(comando);
        NetWId netWId = new NetWId(idEvento);
        richiesta.aggiungiArgomento(new Argomento(RESTITUISCI_STATISTICHE_EVENTO_ARG_EVENTO, netWId.getRemoteClassPath(), netWId));

        ResponseListener listener = new ResponseListener(comando, element -> element.intValue() == 1, element -> onSuccess.onResponse(element.get(0).castRisultato(WStatistichePREvento.class)), onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

    public void resitituisciPrevenditeEvento(int idEvento, final Response.Listener<List<WPrevenditaPlus>> onSuccess, final Response.Listener<List<Eccezione>> onException)  {
        Comando comando = Comando.COMANDO_PR_RESTITUISCI_PREVENDITE_EVENTO;
        final Richiesta richiesta = new Richiesta(comando);
        NetWId netWId = new NetWId(idEvento);
        richiesta.aggiungiArgomento(new Argomento(RESTITUISCI_PREVENDITE_EVENTO_ARG_EVENTO, netWId.getRemoteClassPath(), netWId));

        ResponseListener listener = new ResponseListener(comando, element -> true, element -> {
            List<WPrevenditaPlus> myList = new ArrayList<>();

            for (Risultato risultato : element) {
                myList.add(risultato.castRisultato(WPrevenditaPlus.class));
            }

            onSuccess.onResponse(myList);
        }, onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

}
