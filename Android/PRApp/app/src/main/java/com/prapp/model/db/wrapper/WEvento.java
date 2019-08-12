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

package com.prapp.model.db.wrapper;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.prapp.model.db.enums.StatoEvento;
import com.prapp.model.net.Twinned;
import com.prapp.model.net.serialize.JSONDeserializable;
import com.prapp.model.net.serialize.JSONSerializable;
import com.prapp.model.net.serialize.adapter.DateTimeAdapter;
import com.prapp.model.net.serialize.adapter.StatoEventoAdapter;

import org.joda.time.DateTime;

public class WEvento implements  DatabaseWrapper, Twinned, JSONSerializable, JSONDeserializable {

    public static final String CLASS_PATH = "com\\model\\db\\wrapper\\WEvento";

    public static WEvento getEmpty()
    {
        return new WEvento(0,0,0,"","","1970-01-01T00:00:00.000Z","1970-01-01T00:00:00.000Z","",StatoEvento.VALIDO, 0,"1970-01-01T00:00:00.000Z");
    }

    @SerializedName("id")
    private Integer id;

    @SerializedName("idStaff")
    private Integer idStaff;

    @SerializedName("idCreatore")
    private Integer idCreatore;

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

    /*private String città;
    private String provincia;
    private String stato;*/

    @SerializedName("statoEvento")
    @JsonAdapter(StatoEventoAdapter.class)
    private StatoEvento statoEvento;

    @SerializedName("idModificatore")
    private Integer idModificatore;

    @SerializedName("timestampUltimaModifica")
    @JsonAdapter(DateTimeAdapter.class)
    private DateTime timestampUltimaModifica;


    public WEvento(Integer id, Integer idStaff, Integer idCreatore, String nome, String descrizione, DateTime inizio, DateTime fine, String indirizzo, StatoEvento statoEvento, Integer idModificatore, DateTime timestampUltimaModifica) {
        this.id = id;
        this.idStaff = idStaff;
        this.idCreatore = idCreatore;
        this.nome = nome;
        this.descrizione = descrizione;
        this.inizio = inizio;
        this.fine = fine;
        this.indirizzo = indirizzo;
        this.statoEvento = statoEvento;
        this.idModificatore = idModificatore;
        this.timestampUltimaModifica = timestampUltimaModifica;
    }

    public WEvento(Integer id, Integer idStaff, Integer idCreatore, String nome, String descrizione, String inizio, String fine, String indirizzo, StatoEvento statoEvento, Integer idModificatore, String timestampUltimaModifica) {
        this(id, idStaff, idCreatore, nome, descrizione, new DateTime(timestampUltimaModifica), new DateTime(timestampUltimaModifica), indirizzo, statoEvento, idModificatore, new DateTime(timestampUltimaModifica));
    }

    public Integer getId() {
        return id;
    }

    public Integer getIdStaff() {
        return idStaff;
    }

    public Integer getIdCreatore() {
        return idCreatore;
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

    public Integer getIdModificatore() {
        return idModificatore;
    }

    public DateTime getTimestampUltimaModifica() {
        return timestampUltimaModifica;
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
