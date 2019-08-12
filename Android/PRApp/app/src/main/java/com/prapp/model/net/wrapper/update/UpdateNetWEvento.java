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

package com.prapp.model.net.wrapper.update;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.prapp.model.db.enums.StatoEvento;
import com.prapp.model.net.Twinned;
import com.prapp.model.net.serialize.JSONSerializable;
import com.prapp.model.net.serialize.adapter.DateTimeAdapter;
import com.prapp.model.net.serialize.adapter.StatoEventoAdapter;
import com.prapp.model.net.wrapper.NetWrapper;

import org.joda.time.DateTime;

public class UpdateNetWEvento implements Twinned, NetWrapper, JSONSerializable {

    public static final String CLASS_PATH = "com\\model\\net\\wrapper\\update\\UpdateNetWEvento";

    public static UpdateNetWEvento getEmpty()
    {
        return new UpdateNetWEvento(0, "", null, new DateTime("1970-01-01T00:00:00.000Z"), new DateTime("1970-01-01T00:00:00.000Z"), "", StatoEvento.VALIDO);
    }

    @SerializedName("idEvento")
    private Integer idEvento;

    @SerializedName("nome")
    private String nome;

    //Optional
    @SerializedName("descrizione")
    private String descrizione;

    @SerializedName("inizio")
    @JsonAdapter(DateTimeAdapter.class)
    private DateTime inizio;

    @SerializedName("fine")
    @JsonAdapter(DateTimeAdapter.class)
    private DateTime fine;

    @SerializedName("indirizzo")
    private String indirizzo;

    @SerializedName("statoEvento")
    @JsonAdapter(StatoEventoAdapter.class)
    private StatoEvento statoEvento;

    public UpdateNetWEvento(Integer idEvento, String nome, String descrizione, DateTime inizio, DateTime fine, String indirizzo, StatoEvento statoEvento) {
        this.idEvento = idEvento;
        this.nome = nome;
        this.descrizione = descrizione;
        this.inizio = inizio;
        this.fine = fine;
        this.indirizzo = indirizzo;
        this.statoEvento = statoEvento;
    }

    public UpdateNetWEvento(Integer idEvento, String nome, String descrizione, String inizio, String fine, String indirizzo, StatoEvento statoEvento) {
        this(idEvento, nome, descrizione, new DateTime(inizio), new DateTime(fine), indirizzo, statoEvento);
    }

    public Integer getIdEvento() {
        return idEvento;
    }

    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public DateTime getInizio() {
        return inizio;
    }

    public DateTime getFine() {
        return fine;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public StatoEvento getStatoEvento() {
        return statoEvento;
    }

    public boolean isDescrizionePresent()
    {
        return descrizione != null;
    }

    @Override
    public String getRemoteClassPath() {
        return CLASS_PATH;
    }

}
