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
import com.prapp.model.db.wrapper.WEntrata;
import com.prapp.model.db.wrapper.WPrevendita;
import com.prapp.model.db.wrapper.WPrevenditaPlus;
import com.prapp.model.db.wrapper.WStatisticheCassiereEvento;
import com.prapp.model.db.wrapper.WStatisticheCassiereStaff;
import com.prapp.model.db.wrapper.WStatisticheCassiereTotali;
import com.prapp.model.net.Argomento;
import com.prapp.model.net.Eccezione;
import com.prapp.model.net.Richiesta;
import com.prapp.model.net.RichiestaVolley;
import com.prapp.model.net.Risultato;
import com.prapp.model.net.enums.Comando;
import com.prapp.model.net.wrapper.NetWEntrata;
import com.prapp.model.net.wrapper.NetWId;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ManagerCassiere extends Manager {

    private static final String TIMBRA_ENTRATA_ARG_ENTRATA = "entrata";
    private static final String RESTITUISCI_DATI_CLIENTE_ARG_RPEVENDITA = "prevendita";
    private static final String RESTITUISCI_STATISTICHE_STAFF_ARG_STAFF = "staff";
    private static final String RESTITUISCI_STATISTICHE_EVENTO_ARG_EVENTO = "evento";
    private static final String ENTRATE_SVOLTE_ARG_EVENTO = "evento";
    private static final String RESTITUISCI_PREVENDTITE_ARG_EVENTO = "evento";
    private static final String RESTITUISCI_INFORMAZIONI_PREVENDITA_ARG_RPEVENDITA = "prevendita";
    private static final String RESTITUISCI_LISTA_PREVENDITE_TIMBRATE_ARG_EVENTO = "evento";
    private static final String RESTITUISCI_LISTA_PREVENDITE_NON_TIMBRATE_ARG_EVENTO = "evento";

    private static ManagerCassiere singleton;

    public static synchronized ManagerCassiere getInstance() {
        if (singleton == null)
            singleton = new ManagerCassiere();

        return singleton;
    }

    private ManagerCassiere() {
        super();
    }

    public ManagerCassiere(URL indirizzo) {
        super(indirizzo);
    }

    public void timbraEntrata(NetWEntrata entrata, final Response.Listener<WEntrata> onSuccess, final Response.Listener<List<Eccezione>> onException) {
        Comando comando = Comando.COMANDO_CASSIERE_TIMBRA_ENTRATA;
        final Richiesta richiesta = new Richiesta(comando);
        richiesta.aggiungiArgomento(new Argomento(TIMBRA_ENTRATA_ARG_ENTRATA, entrata.getRemoteClassPath(), entrata));


        ResponseListener listener = new ResponseListener(comando, element -> element.intValue() == 1, element -> onSuccess.onResponse(element.get(0).castRisultato(WEntrata.class)), onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

    public void resitituisciStatisticheTotali(final Response.Listener<WStatisticheCassiereTotali> onSuccess, final Response.Listener<List<Eccezione>> onException) {
        Comando comando = Comando.COMANDO_CASSIERE_RESTITUISCI_STATISTICHE_CASSIERE_TOTALI;
        final Richiesta richiesta = new Richiesta(comando);

        ResponseListener listener = new ResponseListener(comando, element -> element.intValue() == 1, element -> onSuccess.onResponse(element.get(0).castRisultato(WStatisticheCassiereTotali.class)), onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

    public void resitituisciStatisticheStaff(int idStaff, final Response.Listener<WStatisticheCassiereStaff> onSuccess, final Response.Listener<List<Eccezione>> onException) {
        Comando comando = Comando.COMANDO_CASSIERE_RESTITUISCI_STATISTICHE_CASSIERE_STAFF;
        final Richiesta richiesta = new Richiesta(comando);
        NetWId netWId = new NetWId(idStaff);
        richiesta.aggiungiArgomento(new Argomento(RESTITUISCI_STATISTICHE_STAFF_ARG_STAFF, netWId.getRemoteClassPath(), netWId));

        ResponseListener listener = new ResponseListener(comando, element -> element.intValue() == 1, element -> onSuccess.onResponse(element.get(0).castRisultato(WStatisticheCassiereStaff.class)), onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

    public void resitituisciStatisticheEvento(int idEvento, final Response.Listener<WStatisticheCassiereEvento> onSuccess, final Response.Listener<List<Eccezione>> onException) {
        Comando comando = Comando.COMANDO_CASSIERE_RESTITUISCI_STATISTICHE_CASSIERE_EVENTO;
        final Richiesta richiesta = new Richiesta(comando);
        NetWId netWId = new NetWId(idEvento);
        richiesta.aggiungiArgomento(new Argomento(RESTITUISCI_STATISTICHE_EVENTO_ARG_EVENTO, netWId.getRemoteClassPath(), netWId));

        ResponseListener listener = new ResponseListener(comando, element -> element.intValue() == 1, element -> onSuccess.onResponse(element.get(0).castRisultato(WStatisticheCassiereEvento.class)), onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

    public void restituisciEntrateEvento(int idEvento, final Response.Listener<List<WEntrata>> onSuccess, final Response.Listener<List<Eccezione>> onException) {
        Comando comando = Comando.COMANDO_CASSIERE_RESTITUISCI_ENTRATE_SVOLTE;
        final Richiesta richiesta = new Richiesta(comando);
        NetWId netWId = new NetWId(idEvento);
        richiesta.aggiungiArgomento(new Argomento(ENTRATE_SVOLTE_ARG_EVENTO, netWId.getRemoteClassPath(), netWId));


        ResponseListener listener = new ResponseListener(comando, element -> true, element -> {
            List<WEntrata> myList = new ArrayList<>();

            for (Risultato risultato : element) {
                myList.add(risultato.castRisultato(WEntrata.class));
            }

            onSuccess.onResponse(myList);
        }, onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

    public void restituisciListaPrevendite(int idEvento, final Response.Listener<List<WPrevendita>> onSuccess, final Response.Listener<List<Eccezione>> onException) {
        Comando comando = Comando.COMANDO_CASSIERE_RESTITUISCI_PREVENDITE_EVENTO;
        final Richiesta richiesta = new Richiesta(comando);
        NetWId netWId = new NetWId(idEvento);
        richiesta.aggiungiArgomento(new Argomento(RESTITUISCI_PREVENDTITE_ARG_EVENTO, netWId.getRemoteClassPath(), netWId));


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

    public void restituisciInformazioniPrevendita(int idPrevendita, final Response.Listener<WPrevenditaPlus> onSuccess, final Response.Listener<List<Eccezione>> onException) {
        Comando comando = Comando.COMANDO_CASSIERE_RESTITUISCI_INFORMAZIONI_PREVENDITA;
        final Richiesta richiesta = new Richiesta(comando);
        NetWId netWId = new NetWId(idPrevendita);
        richiesta.aggiungiArgomento(new Argomento(RESTITUISCI_INFORMAZIONI_PREVENDITA_ARG_RPEVENDITA, netWId.getRemoteClassPath(), netWId));


        ResponseListener listener = new ResponseListener(comando, element -> element.intValue() == 1, element -> onSuccess.onResponse(element.get(0).castRisultato(WPrevenditaPlus.class)), onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

    public void restituisciListaPrevenditeTimbrate(int idEvento, final Response.Listener<List<WPrevenditaPlus>> onSuccess, final Response.Listener<List<Eccezione>> onException) {
        Comando comando = Comando.COMANDO_CASSIERE_RESTITUISCI_LISTA_PREVENDITE_TIMBRATE;
        final Richiesta richiesta = new Richiesta(comando);
        NetWId netWId = new NetWId(idEvento);
        richiesta.aggiungiArgomento(new Argomento(RESTITUISCI_LISTA_PREVENDITE_TIMBRATE_ARG_EVENTO, netWId.getRemoteClassPath(), netWId));


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

    public void restituisciListaPrevenditeNonTimbrate(int idEvento, final Response.Listener<List<WPrevenditaPlus>> onSuccess, final Response.Listener<List<Eccezione>> onException)  {
        Comando comando = Comando.COMANDO_CASSIERE_RESTITUISCI_LISTA_PREVENDITE_NON_TIMBRATE;
        final Richiesta richiesta = new Richiesta(comando);
        NetWId netWId = new NetWId(idEvento);
        richiesta.aggiungiArgomento(new Argomento(RESTITUISCI_LISTA_PREVENDITE_NON_TIMBRATE_ARG_EVENTO, netWId.getRemoteClassPath(), netWId));


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
