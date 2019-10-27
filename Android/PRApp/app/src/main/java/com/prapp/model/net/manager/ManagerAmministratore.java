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
import com.prapp.model.db.wrapper.WCliente;
import com.prapp.model.db.wrapper.WDirittiUtente;
import com.prapp.model.db.wrapper.WEvento;
import com.prapp.model.db.wrapper.WPrevendita;
import com.prapp.model.db.wrapper.WStatisticheCassiereEvento;
import com.prapp.model.db.wrapper.WStatisticheCassiereStaff;
import com.prapp.model.db.wrapper.WStatisticheEvento;
import com.prapp.model.db.wrapper.WStatistichePREvento;
import com.prapp.model.db.wrapper.WStatistichePRStaff;
import com.prapp.model.db.wrapper.WTipoPrevendita;
import com.prapp.model.db.wrapper.WUtente;
import com.prapp.model.net.Argomento;
import com.prapp.model.net.Eccezione;
import com.prapp.model.net.Richiesta;
import com.prapp.model.net.RichiestaVolley;
import com.prapp.model.net.Risultato;
import com.prapp.model.net.enums.Comando;
import com.prapp.model.net.wrapper.NetWId;
import com.prapp.model.net.wrapper.insert.InsertNetWEvento;
import com.prapp.model.net.wrapper.insert.InsertNetWTipoPrevendita;
import com.prapp.model.net.wrapper.update.UpdateNetWDirittiUtente;
import com.prapp.model.net.wrapper.update.UpdateNetWEvento;
import com.prapp.model.net.wrapper.update.UpdateNetWStaff;
import com.prapp.model.net.wrapper.update.UpdateNetWTipoPrevendita;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ManagerAmministratore extends Manager {

    private static final String RIMUOVI_CLIENTE_ARG_CLIENTE = "cliente";
    private static final String AGGIUNGI_EVENTO_ARG_EVENTO = "evento";
    private static final String MODIFICA_EVENTO_ARG_EVENTO = "evento";
    private static final String AGGIUNGI_TIPO_PREVENDITA_ARG_TIPO_PREVENDITA = "tipoPrevendita";
    private static final String MODIFICA_TIPO_PREVENDITA_ARG_TIPO_PREVENDITA = "tipoPrevendita";
    private static final String ELIMINA_TIPO_PREVENDITA_ARG_TIPO_PREVENDITA = "tipoPrevendita";
    private static final String MODIFICA_DIRITTI_UTENTE_ARG_DIRITTI = "dirittiUtente";
    private static final String RESTITUISCI_STATISTICHE_PR_STAFF_ARG_PR = "pr";
    private static final String RESTITUISCI_STATISTICHE_PR_STAFF_ARG_STAFF = "staff";
    private static final String RESTITUISCI_STATISTICHE_CASSIERE_STAFF_ARG_CASSIERE = "cassiere";
    private static final String RESTITUISCI_STATISTICHE_CASSIERE_STAFF_ARG_STAFF = "staff";
    private static final String RESTITUISCI_STATISTICHE_EVENTO_ARG_EVENTO = "evento";
    private static final String RESTITUISCI_PREVENDITE_EVENTO_ARG_EVENTO = "evento";
    private static final String RIMUOVI_MEMBRO_ARG_MEMBRO = "membro";
    private static final String RIMUOVI_MEMBRO_ARG_STAFF = "staff";
    private static final String MODIFICA_CODICE_ACCESSO_ARG_STAFF = "staff";
    private static final String RESTITUISCI_STATISTICHE_PR_EVENTO_ARG_PR = "pr";
    private static final String RESTITUISCI_STATISTICHE_PR_EVENTO_ARG_EVENTO = "evento";
    private static final String RESTITUISCI_STATISTICHE_CASSIERE_EVENTO_ARG_CASSIERE = "cassiere";
    private static final String RESTITUISCI_STATISTICHE_CASSIERE_EVENTO_ARG_EVENTO = "evento";

    private static ManagerAmministratore singleton;

    public static synchronized ManagerAmministratore getInstance() {
        if (singleton == null)
            singleton = new ManagerAmministratore();

        return singleton;
    }

    private ManagerAmministratore() {
        super();
    }

    public ManagerAmministratore(URL indirizzo) {
        super(indirizzo);
    }

    public void rimuoviCliente(int idCliente, final Response.Listener<WCliente> onSuccess, final Response.Listener<List<Eccezione>> onException) throws UnsupportedEncodingException {
        Comando comando = Comando.COMANDO_AMMINISTRATORE_RIMUOVI_CLIENTE;
        final Richiesta richiesta = new Richiesta(comando);
        NetWId netWId = new NetWId(idCliente);
        richiesta.aggiungiArgomento(new Argomento(RIMUOVI_CLIENTE_ARG_CLIENTE, netWId.getRemoteClassPath(), netWId));

        ResponseListener listener = new ResponseListener(comando, element -> element.intValue() == 1, element -> onSuccess.onResponse(element.get(0).castRisultato(WCliente.class)), onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

    public void aggiungiEvento(InsertNetWEvento evento, final Response.Listener<WEvento> onSuccess, final Response.Listener<List<Eccezione>> onException) throws UnsupportedEncodingException {
        Comando comando = Comando.COMANDO_AMMINISTRATORE_AGGIUNGI_EVENTO;
        final Richiesta richiesta = new Richiesta(comando);
        richiesta.aggiungiArgomento(new Argomento(AGGIUNGI_EVENTO_ARG_EVENTO, evento.getRemoteClassPath(), evento));

        ResponseListener listener = new ResponseListener(comando, element -> element.intValue() == 1, element -> onSuccess.onResponse(element.get(0).castRisultato(WEvento.class)), onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

    public void modificaEvento(UpdateNetWEvento evento, final Response.Listener<WEvento> onSuccess, final Response.Listener<List<Eccezione>> onException) throws UnsupportedEncodingException {
        Comando comando = Comando.COMANDO_AMMINISTRATORE_MODIFICA_EVENTO;
        final Richiesta richiesta = new Richiesta(comando);
        richiesta.aggiungiArgomento(new Argomento(MODIFICA_EVENTO_ARG_EVENTO, evento.getRemoteClassPath(), evento));

        ResponseListener listener = new ResponseListener(comando, element -> element.intValue() == 1, element -> onSuccess.onResponse(element.get(0).castRisultato(WEvento.class)), onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

    public void aggiungiTipoPrevendita(InsertNetWTipoPrevendita tipoPrevendita, final Response.Listener<WTipoPrevendita> onSuccess, final Response.Listener<List<Eccezione>> onException) throws UnsupportedEncodingException {
        Comando comando = Comando.COMANDO_AMMINISTRATORE_AGGIUNGI_TIPO_PREVENDITA;
        final Richiesta richiesta = new Richiesta(comando);
        richiesta.aggiungiArgomento(new Argomento(AGGIUNGI_TIPO_PREVENDITA_ARG_TIPO_PREVENDITA, tipoPrevendita.getRemoteClassPath(), tipoPrevendita));

        ResponseListener listener = new ResponseListener(comando, element -> element.intValue() == 1, element -> onSuccess.onResponse(element.get(0).castRisultato(WTipoPrevendita.class)), onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

    public void modificaTipoPrevendita(UpdateNetWTipoPrevendita tipoPrevendita, final Response.Listener<WTipoPrevendita> onSuccess, final Response.Listener<List<Eccezione>> onException) throws UnsupportedEncodingException {
        Comando comando = Comando.COMANDO_AMMINISTRATORE_MODIFICA_TIPO_PREVENDITA;
        final Richiesta richiesta = new Richiesta(comando);
        richiesta.aggiungiArgomento(new Argomento(MODIFICA_TIPO_PREVENDITA_ARG_TIPO_PREVENDITA, tipoPrevendita.getRemoteClassPath(), tipoPrevendita));

        ResponseListener listener = new ResponseListener(comando, element -> element.intValue() == 1, element -> onSuccess.onResponse(element.get(0).castRisultato(WTipoPrevendita.class)), onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

    public void eliminaTipoPrevendita(int idTipoPrevendita, final Response.Listener<WTipoPrevendita> onSuccess, final Response.Listener<List<Eccezione>> onException) throws UnsupportedEncodingException {
        Comando comando = Comando.COMANDO_AMMINISTRATORE_ELIMINA_TIPO_PREVENDITA;
        final Richiesta richiesta = new Richiesta(comando);
        NetWId netWId = new NetWId(idTipoPrevendita);
        richiesta.aggiungiArgomento(new Argomento(ELIMINA_TIPO_PREVENDITA_ARG_TIPO_PREVENDITA, netWId.getRemoteClassPath(), netWId));

        ResponseListener listener = new ResponseListener(comando, element -> element.intValue() == 1, element -> onSuccess.onResponse(element.get(0).castRisultato(WTipoPrevendita.class)), onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

    public void modificaDirittiUtente(UpdateNetWDirittiUtente tipoPrevendita, final Response.Listener<WDirittiUtente> onSuccess, final Response.Listener<List<Eccezione>> onException) throws UnsupportedEncodingException {
        Comando comando = Comando.COMANDO_AMMINISTRATORE_MODIFICA_DIRITTI_UTENTE;
        final Richiesta richiesta = new Richiesta(comando);
        richiesta.aggiungiArgomento(new Argomento(MODIFICA_DIRITTI_UTENTE_ARG_DIRITTI, tipoPrevendita.getRemoteClassPath(), tipoPrevendita));

        ResponseListener listener = new ResponseListener(comando, element -> element.intValue() == 1, element -> onSuccess.onResponse(element.get(0).castRisultato(WDirittiUtente.class)), onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

    public void resitituisciStatistichePRStaff(int idPR, int idStaff, final Response.Listener<WStatistichePRStaff> onSuccess, final Response.Listener<List<Eccezione>> onException) throws UnsupportedEncodingException {
        Comando comando = Comando.COMANDO_AMMINISTRATORE_RESTITUISCI_STATISTICHE_PR;
        final Richiesta richiesta = new Richiesta(comando);
        NetWId netWIdPR = new NetWId(idPR);
        richiesta.aggiungiArgomento(new Argomento(RESTITUISCI_STATISTICHE_PR_STAFF_ARG_PR, netWIdPR.getRemoteClassPath(), netWIdPR));
        NetWId netWIdStaff = new NetWId(idStaff);
        richiesta.aggiungiArgomento(new Argomento(RESTITUISCI_STATISTICHE_PR_STAFF_ARG_STAFF, netWIdStaff.getRemoteClassPath(), netWIdStaff));

        ResponseListener listener = new ResponseListener(comando, element -> element.intValue() == 1, element -> onSuccess.onResponse(element.get(0).castRisultato(WStatistichePRStaff.class)), onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

    public void resitituisciStatisticheCassiereStaff(int idCassiere, int idStaff, final Response.Listener<WStatisticheCassiereStaff> onSuccess, final Response.Listener<List<Eccezione>> onException) throws UnsupportedEncodingException {
        Comando comando = Comando.COMANDO_AMMINISTRATORE_RESTITUISCI_STATISTICHE_CASSIERE;
        final Richiesta richiesta = new Richiesta(comando);
        NetWId netWIdCassiere = new NetWId(idCassiere);
        richiesta.aggiungiArgomento(new Argomento(RESTITUISCI_STATISTICHE_CASSIERE_STAFF_ARG_CASSIERE, netWIdCassiere.getRemoteClassPath(), netWIdCassiere));
        NetWId netWIdStaff = new NetWId(idStaff);
        richiesta.aggiungiArgomento(new Argomento(RESTITUISCI_STATISTICHE_CASSIERE_STAFF_ARG_STAFF, netWIdStaff.getRemoteClassPath(), netWIdStaff));

        ResponseListener listener = new ResponseListener(comando, element -> element.intValue() == 1, element -> onSuccess.onResponse(element.get(0).castRisultato(WStatisticheCassiereStaff.class)), onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

    public void resitituisciStatisticheEvento(int idEvento, final Response.Listener<List<WStatisticheEvento>> onSuccess, final Response.Listener<List<Eccezione>> onException) throws UnsupportedEncodingException {
        Comando comando = Comando.COMANDO_AMMINISTRATORE_RESTITUISCI_STATISTICHE_EVENTO;
        final Richiesta richiesta = new Richiesta(comando);
        NetWId netWId = new NetWId(idEvento);
        richiesta.aggiungiArgomento(new Argomento(RESTITUISCI_STATISTICHE_EVENTO_ARG_EVENTO, netWId.getRemoteClassPath(), netWId));

        ResponseListener listener = new ResponseListener(comando, element -> true, element -> {
            List<WStatisticheEvento> list = new ArrayList<>();

            for (Risultato risultato : element) {
                list.add(risultato.castRisultato(WStatisticheEvento.class));
            }

            onSuccess.onResponse(list);
        }, onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

    public void resitituisciPrevenditeEvento(int idEvento, final Response.Listener<List<WPrevendita>> onSuccess, final Response.Listener<List<Eccezione>> onException) throws UnsupportedEncodingException {
        Comando comando = Comando.COMANDO_AMMINISTRATORE_RESTITUISCI_PREVENDITE;
        final Richiesta richiesta = new Richiesta(comando);
        NetWId netWId = new NetWId(idEvento);
        richiesta.aggiungiArgomento(new Argomento(RESTITUISCI_PREVENDITE_EVENTO_ARG_EVENTO, netWId.getRemoteClassPath(), netWId));

        ResponseListener listener = new ResponseListener(comando, element -> true, element -> {
            List<WPrevendita> list = new ArrayList<>();

            for (Risultato risultato : element) {
                list.add(risultato.castRisultato(WPrevendita.class));
            }

            onSuccess.onResponse(list);
        }, onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

    public void rimuoviMembroStaff(int idMembro, int idStaff, final Response.Listener<WUtente> onSuccess, final Response.Listener<List<Eccezione>> onException) throws UnsupportedEncodingException {
        Comando comando = Comando.COMANDO_AMMINISTRATORE_RIMUOVI_MEMBRO;
        final Richiesta richiesta = new Richiesta(comando);
        NetWId netWIdMembro = new NetWId(idMembro);
        richiesta.aggiungiArgomento(new Argomento(RIMUOVI_MEMBRO_ARG_MEMBRO, netWIdMembro.getRemoteClassPath(), netWIdMembro));
        NetWId netWIdStaff = new NetWId(idStaff);
        richiesta.aggiungiArgomento(new Argomento(RIMUOVI_MEMBRO_ARG_STAFF, netWIdStaff.getRemoteClassPath(), netWIdStaff));

        ResponseListener listener = new ResponseListener(comando, element -> element.intValue() == 1, element -> onSuccess.onResponse(element.get(0).castRisultato(WUtente.class)), onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

    public void modificaCodiceAccesso(UpdateNetWStaff update, final Response.Listener<Void> onSuccess, final Response.Listener<List<Eccezione>> onException) throws UnsupportedEncodingException {
        Comando comando = Comando.COMANDO_AMMINISTRATORE_MODIFICA_CODICE_ACCESSO;
        final Richiesta richiesta = new Richiesta(comando);
        richiesta.aggiungiArgomento(new Argomento(MODIFICA_CODICE_ACCESSO_ARG_STAFF, update.getRemoteClassPath(), update));

        ResponseListener listener = new ResponseListener(comando, element -> element.intValue() == 0, element -> {
            //Non fa nulla.
        }, onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

    public void resitituisciStatistichePREvento(int idPR, int idEvento, final Response.Listener<List<WStatistichePREvento>> onSuccess, final Response.Listener<List<Eccezione>> onException) throws UnsupportedEncodingException {
        Comando comando = Comando.COMANDO_AMMINISTRATORE_RESTITUISCI_STATISTICHE_PR_EVENTO;
        final Richiesta richiesta = new Richiesta(comando);
        NetWId netWIdPR = new NetWId(idPR);
        richiesta.aggiungiArgomento(new Argomento(RESTITUISCI_STATISTICHE_PR_EVENTO_ARG_PR, netWIdPR.getRemoteClassPath(), netWIdPR));
        NetWId netWIdEvento = new NetWId(idEvento);
        richiesta.aggiungiArgomento(new Argomento(RESTITUISCI_STATISTICHE_PR_EVENTO_ARG_EVENTO, netWIdEvento.getRemoteClassPath(), netWIdEvento));

        ResponseListener listener = new ResponseListener(comando, element -> true, element -> {
            List<WStatistichePREvento> list = new ArrayList<>();

            for (Risultato risultato : element) {
                list.add(risultato.castRisultato(WStatistichePREvento.class));
            }

            onSuccess.onResponse(list);
        }, onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }

    public void resitituisciStatisticheCassiereEvento(int idCassiere, int idEvento, final Response.Listener<WStatisticheCassiereEvento> onSuccess, final Response.Listener<List<Eccezione>> onException) throws UnsupportedEncodingException {
        Comando comando = Comando.COMANDO_AMMINISTRATORE_RESTITUISCI_STATISTICHE_CASSIERE_EVENTO;
        final Richiesta richiesta = new Richiesta(comando);
        NetWId netWIdCassiere = new NetWId(idCassiere);
        richiesta.aggiungiArgomento(new Argomento(RESTITUISCI_STATISTICHE_CASSIERE_EVENTO_ARG_CASSIERE, netWIdCassiere.getRemoteClassPath(), netWIdCassiere));
        NetWId netWIdEvento = new NetWId(idEvento);
        richiesta.aggiungiArgomento(new Argomento(RESTITUISCI_STATISTICHE_CASSIERE_EVENTO_ARG_EVENTO, netWIdEvento.getRemoteClassPath(), netWIdEvento));

        ResponseListener listener = new ResponseListener(comando, element -> element.intValue() == 0 || element.intValue() == 1, element -> {
            if (!element.isEmpty())
                onSuccess.onResponse(element.get(0).castRisultato(WStatisticheCassiereEvento.class));
        }, onException, errorListener);

        RichiestaVolley richiestaVolley = new RichiestaVolley(indirizzo.toString(), richiesta, listener, errorListener);

        PRAppApplication.getInstance().addToRequestQueue(richiestaVolley);
    }


}
