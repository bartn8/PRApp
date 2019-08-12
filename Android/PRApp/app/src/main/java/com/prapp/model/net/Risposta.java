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

package com.prapp.model.net;

import com.google.gson.Gson;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.prapp.model.net.enums.Comando;
import com.prapp.model.net.enums.StatoRisposta;
import com.prapp.model.net.serialize.adapter.ComandoAdapter;
import com.prapp.model.net.serialize.adapter.RisultatoListAdapter;
import com.prapp.model.net.serialize.adapter.StatoRispostaAdapter;

import java.util.List;

public class Risposta {

    private static final Gson GSON_OBJECT = new Gson();

    @SerializedName("command")
    @JsonAdapter(ComandoAdapter.class)
    private Comando comando;

    @SerializedName("status")
    @JsonAdapter(StatoRispostaAdapter.class)
    private StatoRisposta statoRisposta;

    @SerializedName("results")
    @JsonAdapter(RisultatoListAdapter.class)
    private List<Risultato> risultati;

    @SerializedName("exceptions")
    //Non ho bisogno di un adapter particolare.
    private List<Eccezione> eccezioni;

    public Risposta(Comando comando, StatoRisposta statoRisposta, List<Risultato> risultati, List<Eccezione> eccezioni) {
        this.comando = comando;
        this.statoRisposta = statoRisposta;
        this.risultati = risultati;
        this.eccezioni = eccezioni;
    }

    public Comando getComando() {
        return comando;
    }

    public int getComandoAsInt(){
        return comando.getComando();
    }

    public StatoRisposta getStatoRisposta() {
        return statoRisposta;
    }

    public int getStatoRispostaAsInt(){
        return statoRisposta.getStato();
    }

    public List<Risultato> getRisultati() {
        return risultati;
    }

    public  List<Eccezione> getEccezioni() {
        return eccezioni;
    }

    public int risultatiSize() {
        return risultati.size();
    }

    public int eccezioniSize() {
        return eccezioni.size();
    }

    public Risultato getRisultato(int i) {
        return risultati.get(i);
    }

    public Eccezione getEccezione(int i) {
        return eccezioni.get(i);
    }
}

