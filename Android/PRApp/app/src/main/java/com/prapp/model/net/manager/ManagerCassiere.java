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
import com.prapp.model.db.wrapper.WCliente;
import com.prapp.model.db.wrapper.WEntrata;
import com.prapp.model.db.wrapper.WPrevendita;
import com.prapp.model.db.wrapper.WPrevenditaPlus;
import com.prapp.model.db.wrapper.WStatisticheCassiereEvento;
import com.prapp.model.db.wrapper.WStatisticheCassiereStaff;
import com.prapp.model.db.wrapper.WStatisticheCassiereTotali;
import com.prapp.model.net.Argomento;
import com.prapp.model.net.CodaRichiesteSingleton;
import com.prapp.model.net.Eccezione;
import com.prapp.model.net.Richiesta;
import com.prapp.model.net.RichiestaVolley;
import com.prapp.model.net.Risultato;
import com.prapp.model.net.enums.Comando;
import com.prapp.model.net.wrapper.NetWEntrata;
import com.prapp.model.net.wrapper.NetWId;
import com.prapp.model.util.functional.Consumer;
import com.prapp.model.util.functional.Predicate;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ManagerCassiere extends Manager {

    public static final String TIMBRA_ENTRATA_ARG_ENTRATA = "entrata";
    public static final String RESTITUISCI_DATI_CLIENTE_ARG_RPEVENDITA = "prevendita";
    public static final String RESTITUISCI_STATISTICHE_STAFF_ARG_STAFF = "staff";
    public static final String RESTITUISCI_STATISTICHE_EVENTO_ARG_EVENTO = "evento";
    public static final String ENTRATE_SVOLTE_ARG_EVENTO = "evento";
    public static final String RESTITUISCI_PREVENDTITE_ARG_EVENTO = "evento";
    public static final String RESTITUISCI_INFORMAZIONI_PREVENDITA_ARG_RPEVENDITA = "prevendita";
    public static final String RESTITUISCI_LISTA_PREVENDITE_TIMBRATE_ARG_EVENTO = "evento";
    public static final String RESTITUISCI_LISTA_PREVENDITE_NON_TIMBRATE_ARG_EVENTO = "evento";


    //Istanza singleton produce memory leak
//    private static ManagerCassiere singleton;

    public static synchronized ManagerCassiere newInstance(Context context)
    {
        ManagerCassiere tmp = null;

        if(tmp == null)
            tmp = new ManagerCassiere(context);

        return tmp;
    }

    public ManagerCassiere(Context context) {
        super(context);
    }

    public ManagerCassiere(URL indirizzo, Context context) {
        super(indirizzo, context);
    }

    public void timbraEntrata(NetWEntrata entrata, final Response.Listener<WEntrata> onSuccess, final Response.Listener<List<Eccezione>> onException) throws UnsupportedEncodingException {
        Comando comando = Comando.COMANDO_CASSIERE_TIMBRA_ENTRATA;
        final Richiesta richiesta = new Richiesta(comando);
        richiesta.aggiungiArgomento(new Argomento(TIMBRA_ENTRATA_ARG_ENTRATA, entrata.getRemoteClassPath(), entrata));


        ResponseListener listener = new ResponseListener(comando, new Predicate<Integer>() {
            @Override
            public boolean predict(Integer element) {
                return element.intValue() == 1;
            }
        }, new Consumer<List<Risultato>>() {
            @Override
            public void supply(List<Risultato> element) {
                onSuccess.onResponse(element.get(0).castRisultato(WEntrata.class));
            }
        }, onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        CodaRichiesteSingleton.addToRequestQueue(richiestaVolley, context);
    }

    public void restituisciDatiCliente(int idPrevendita, final Response.Listener<WCliente> onSuccess, final Response.Listener<List<Eccezione>> onException) throws UnsupportedEncodingException {
        Comando comando = Comando.COMANDO_CASSIERE_RESTITUISCI_DATI_CLIENTE;
        final Richiesta richiesta = new Richiesta(comando);
        NetWId netWId = new NetWId(idPrevendita);
        richiesta.aggiungiArgomento(new Argomento(RESTITUISCI_DATI_CLIENTE_ARG_RPEVENDITA, netWId.getRemoteClassPath(), netWId));


        ResponseListener listener = new ResponseListener(comando, new Predicate<Integer>() {
            @Override
            public boolean predict(Integer element) {
                return element.intValue() == 1;
            }
        }, new Consumer<List<Risultato>>() {
            @Override
            public void supply(List<Risultato> element) {
                onSuccess.onResponse(element.get(0).castRisultato(WCliente.class));
            }
        }, onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        CodaRichiesteSingleton.addToRequestQueue(richiestaVolley, context);
    }

    public void resitituisciStatisticheTotali(final Response.Listener<WStatisticheCassiereTotali> onSuccess, final Response.Listener<List<Eccezione>> onException) throws UnsupportedEncodingException {
        Comando comando = Comando.COMANDO_CASSIERE_RESTITUISCI_STATISTICHE_CASSIERE_TOTALI;
        final Richiesta richiesta = new Richiesta(comando);

        ResponseListener listener = new ResponseListener(comando, new Predicate<Integer>() {
            @Override
            public boolean predict(Integer element) {
                return element.intValue() == 1;
            }
        }, new Consumer<List<Risultato>>() {
            @Override
            public void supply(List<Risultato> element) {
                onSuccess.onResponse(element.get(0).castRisultato(WStatisticheCassiereTotali.class));
            }
        }, onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        CodaRichiesteSingleton.addToRequestQueue(richiestaVolley, context);
    }

    public void resitituisciStatisticheStaff(int idStaff, final Response.Listener<WStatisticheCassiereStaff> onSuccess, final Response.Listener<List<Eccezione>> onException) throws UnsupportedEncodingException {
        Comando comando = Comando.COMANDO_CASSIERE_RESTITUISCI_STATISTICHE_CASSIERE_STAFF;
        final Richiesta richiesta = new Richiesta(comando);
        NetWId netWId = new NetWId(idStaff);
        richiesta.aggiungiArgomento(new Argomento(RESTITUISCI_STATISTICHE_STAFF_ARG_STAFF, netWId.getRemoteClassPath(), netWId));

        ResponseListener listener = new ResponseListener(comando, new Predicate<Integer>() {
            @Override
            public boolean predict(Integer element) {
                return element.intValue() == 1;
            }
        }, new Consumer<List<Risultato>>() {
            @Override
            public void supply(List<Risultato> element) {
                onSuccess.onResponse(element.get(0).castRisultato(WStatisticheCassiereStaff.class));
            }
        }, onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        CodaRichiesteSingleton.addToRequestQueue(richiestaVolley, context);
    }

    public void resitituisciStatisticheEvento(int idEvento, final Response.Listener<WStatisticheCassiereEvento> onSuccess, final Response.Listener<List<Eccezione>> onException) throws UnsupportedEncodingException {
        Comando comando = Comando.COMANDO_CASSIERE_RESTITUISCI_STATISTICHE_CASSIERE_EVENTO;
        final Richiesta richiesta = new Richiesta(comando);
        NetWId netWId = new NetWId(idEvento);
        richiesta.aggiungiArgomento(new Argomento(RESTITUISCI_STATISTICHE_EVENTO_ARG_EVENTO, netWId.getRemoteClassPath(), netWId));

        ResponseListener listener = new ResponseListener(comando, new Predicate<Integer>() {
            @Override
            public boolean predict(Integer element) {
                return element.intValue() == 1;
            }
        }, new Consumer<List<Risultato>>() {
            @Override
            public void supply(List<Risultato> element) {
                onSuccess.onResponse(element.get(0).castRisultato(WStatisticheCassiereEvento.class));
            }
        }, onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        CodaRichiesteSingleton.addToRequestQueue(richiestaVolley, context);
    }

    public void restituisciEntrateEvento(int idEvento, final Response.Listener<List<WEntrata>> onSuccess, final Response.Listener<List<Eccezione>> onException) throws UnsupportedEncodingException {
        Comando comando = Comando.COMANDO_CASSIERE_RESTITUISCI_ENTRATE_SVOLTE;
        final Richiesta richiesta = new Richiesta(comando);
        NetWId netWId = new NetWId(idEvento);
        richiesta.aggiungiArgomento(new Argomento(ENTRATE_SVOLTE_ARG_EVENTO, netWId.getRemoteClassPath(), netWId));


        ResponseListener listener = new ResponseListener(comando, new Predicate<Integer>() {
            @Override
            public boolean predict(Integer element) {
                return true;
            }
        }, new Consumer<List<Risultato>>() {
            @Override
            public void supply(List<Risultato> element) {
                List<WEntrata> myList = new ArrayList<>();

                for (Risultato risultato:element)
                {
                    myList.add(risultato.castRisultato(WEntrata.class));
                }

                onSuccess.onResponse(myList);
            }
        }, onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        CodaRichiesteSingleton.addToRequestQueue(richiestaVolley, context);
    }

    public void restituisciListaPrevendite(int idEvento, final Response.Listener<List<WPrevendita>> onSuccess, final Response.Listener<List<Eccezione>> onException) throws UnsupportedEncodingException {
        Comando comando = Comando.COMANDO_CASSIERE_RESTITUISCI_PREVENDITE_EVENTO;
        final Richiesta richiesta = new Richiesta(comando);
        NetWId netWId = new NetWId(idEvento);
        richiesta.aggiungiArgomento(new Argomento(RESTITUISCI_PREVENDTITE_ARG_EVENTO, netWId.getRemoteClassPath(), netWId));


        ResponseListener listener = new ResponseListener(comando, new Predicate<Integer>() {
            @Override
            public boolean predict(Integer element) {
                return true;
            }
        }, new Consumer<List<Risultato>>() {
            @Override
            public void supply(List<Risultato> element) {
                List<WPrevendita> myList = new ArrayList<>();

                for (Risultato risultato:element)
                {
                    myList.add(risultato.castRisultato(WPrevendita.class));
                }

                onSuccess.onResponse(myList);
            }
        }, onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        CodaRichiesteSingleton.addToRequestQueue(richiestaVolley, context);
    }

    public void restituisciInformazioniPrevendita(int idPrevendita, final Response.Listener<WPrevenditaPlus> onSuccess, final Response.Listener<List<Eccezione>> onException) throws UnsupportedEncodingException {
        Comando comando = Comando.COMANDO_CASSIERE_RESTITUISCI_INFORMAZIONI_PREVENDITA;
        final Richiesta richiesta = new Richiesta(comando);
        NetWId netWId = new NetWId(idPrevendita);
        richiesta.aggiungiArgomento(new Argomento(RESTITUISCI_INFORMAZIONI_PREVENDITA_ARG_RPEVENDITA, netWId.getRemoteClassPath(), netWId));


        ResponseListener listener = new ResponseListener(comando, new Predicate<Integer>() {
            @Override
            public boolean predict(Integer element) {
                return element.intValue() == 1;
            }
        }, new Consumer<List<Risultato>>() {
            @Override
            public void supply(List<Risultato> element) {
                onSuccess.onResponse(element.get(0).castRisultato(WPrevenditaPlus.class));
            }
        }, onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        CodaRichiesteSingleton.addToRequestQueue(richiestaVolley, context);
    }

    public void restituisciListaPrevenditeTimbrate(int idEvento, final Response.Listener<List<WPrevenditaPlus>> onSuccess, final Response.Listener<List<Eccezione>> onException) throws UnsupportedEncodingException {
        Comando comando = Comando.COMANDO_CASSIERE_RESTITUISCI_LISTA_PREVENDITE_TIMBRATE;
        final Richiesta richiesta = new Richiesta(comando);
        NetWId netWId = new NetWId(idEvento);
        richiesta.aggiungiArgomento(new Argomento(RESTITUISCI_LISTA_PREVENDITE_TIMBRATE_ARG_EVENTO, netWId.getRemoteClassPath(), netWId));


        ResponseListener listener = new ResponseListener(comando, new Predicate<Integer>() {
            @Override
            public boolean predict(Integer element) {
                return true;
            }
        }, new Consumer<List<Risultato>>() {
            @Override
            public void supply(List<Risultato> element) {
                List<WPrevenditaPlus> myList = new ArrayList<>();

                for (Risultato risultato:element)
                {
                    myList.add(risultato.castRisultato(WPrevenditaPlus.class));
                }

                onSuccess.onResponse(myList);
            }
        }, onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        CodaRichiesteSingleton.addToRequestQueue(richiestaVolley, context);
    }

    public void restituisciListaPrevenditeNonTimbrate(int idEvento, final Response.Listener<List<WPrevenditaPlus>> onSuccess, final Response.Listener<List<Eccezione>> onException) throws UnsupportedEncodingException {
        Comando comando = Comando.COMANDO_CASSIERE_RESTITUISCI_LISTA_PREVENDITE_NON_TIMBRATE;
        final Richiesta richiesta = new Richiesta(comando);
        NetWId netWId = new NetWId(idEvento);
        richiesta.aggiungiArgomento(new Argomento(RESTITUISCI_LISTA_PREVENDITE_NON_TIMBRATE_ARG_EVENTO, netWId.getRemoteClassPath(), netWId));


        ResponseListener listener = new ResponseListener(comando, new Predicate<Integer>() {
            @Override
            public boolean predict(Integer element) {
                return true;
            }
        }, new Consumer<List<Risultato>>() {
            @Override
            public void supply(List<Risultato> element) {
                List<WPrevenditaPlus> myList = new ArrayList<>();

                for (Risultato risultato:element)
                {
                    myList.add(risultato.castRisultato(WPrevenditaPlus.class));
                }

                onSuccess.onResponse(myList);
            }
        }, onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        CodaRichiesteSingleton.addToRequestQueue(richiestaVolley, context);
    }

}
