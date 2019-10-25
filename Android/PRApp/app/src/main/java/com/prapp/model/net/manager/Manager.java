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
import com.android.volley.VolleyError;
import com.prapp.model.MyContext;
import com.prapp.model.net.Eccezione;
import com.prapp.model.net.Risposta;
import com.prapp.model.net.Risultato;
import com.prapp.model.net.enums.Comando;
import com.prapp.model.net.exceptions.CheckException;
import com.prapp.model.util.functional.Consumer;
import com.prapp.model.util.functional.Predicate;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//TODO: trasformare le classi manager in statiche se possibile.

public abstract class Manager {

    protected class ResponseErrorDefaultListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            error.printStackTrace();
        }
    }

    protected class ResponseErrorMultipleListener implements Response.ErrorListener {
        private List<Response.ErrorListener> set = new ArrayList<>();
        private ResponseErrorDefaultListener defaultListener = new ResponseErrorDefaultListener();

        public void addDefaultErrorListener() {
            set.add(defaultListener);
        }

        public void removeDefaultErrorListener() {
            set.remove(defaultListener);
        }

        public void addListener(Response.ErrorListener listener) {
            set.add(listener);
        }

        public void removeListener(Response.ErrorListener listener) {
            set.remove(listener);
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            for (Response.ErrorListener listener : set) {
                listener.onErrorResponse(error);
            }
        }
    }

    protected class ResponseListener implements Response.Listener<Risposta> {

        private Comando comando;
        private Predicate<Integer> isSizeValid;
        private Consumer<List<Risultato>> onSuccess;
        private Response.Listener<List<Eccezione>> onException;
        private Response.ErrorListener errorListener;

        public ResponseListener(Comando comando, Predicate<Integer> isSizeValid, Consumer<List<Risultato>> onSuccess, Response.Listener<List<Eccezione>> onException, Response.ErrorListener errorListener) {
            this.comando = comando;
            this.isSizeValid = isSizeValid;
            this.onSuccess = onSuccess;
            this.onException = onException;
            this.errorListener = errorListener;
        }

        @Override
        public void onResponse(Risposta response) {
            if (!comando.equals(response.getComando())) {
                errorListener.onErrorResponse(new VolleyError((new CheckException("Risulta un comando diverso: " + response.getComando()))));
                return;
            }

            switch (response.getStatoRisposta()) {
                case STATORISPOSTA_OK:
                    if (!isSizeValid.predict(response.getRisultati().size())) {
                        errorListener.onErrorResponse(new VolleyError((new CheckException("Il numero di risultati non corrisponde: " + response.getRisultati().size()))));
                        return;
                    }

                    onSuccess.supply(response.getRisultati());
                    break;

                case STATORISPOSTA_ECCEZIONE:
                    onException.onResponse(response.getEccezioni());
                    break;

                default:
                    errorListener.onErrorResponse(new VolleyError((new CheckException("Risulta uno stato diverso: " + response.getStatoRisposta()))));
            }
        }
    }

    protected URL indirizzo;
    protected android.content.Context context;
    protected ResponseErrorMultipleListener errorListener;

    public Manager(android.content.Context context) {
        this(MyContext.getSingleton().getIndirizzo(), context);
    }

    public Manager(URL indirizzo, android.content.Context context) {
        this.indirizzo = indirizzo;
        this.context = context;
        this.errorListener = new ResponseErrorMultipleListener();
    }

    public URL getIndirizzo() {
        return indirizzo;
    }

    public android.content.Context getContext() {
        return context;
    }

    public void setIndirizzo(URL indirizzo) {
        this.indirizzo = indirizzo;
    }

    public void setContext(android.content.Context context) {
        this.context = context;
    }

    public void addDefaultErrorListener() {
        errorListener.addDefaultErrorListener();
    }

    public void removeDefaultErrorListener() {
        errorListener.removeDefaultErrorListener();
    }

    public void addListener(Response.ErrorListener listener) {
        errorListener.addListener(listener);
    }

    public void removeListener(Response.ErrorListener listener) {
        errorListener.removeListener(listener);
    }

}
