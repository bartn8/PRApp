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
import com.prapp.model.net.Twinned;
import com.prapp.model.net.serialize.JSONDeserializable;
import com.prapp.model.net.serialize.JSONSerializable;
import com.prapp.model.net.serialize.adapter.DateTimeAdapter;

import org.joda.time.DateTime;

public class WTipoPrevendita  implements DatabaseWrapper, Twinned, JSONSerializable, JSONDeserializable {

    public static final String CLASS_PATH = "com\\model\\db\\wrapper\\WTipoPrevendita";
    public static final String ARG_NAME = "prevendita";

    public static WTipoPrevendita getEmpty()
    {
        return new WTipoPrevendita(0,0,"","",0.00f, "1970-01-01T00:00:00.000Z", "1970-01-01T00:00:00.000Z");
    }

    @SerializedName("id")
    private Integer id;

    @SerializedName("idEvento")
    private Integer idEvento;

    @SerializedName("nome")
    private String nome;

    @SerializedName("descrizione")
    private String descrizione;

    @SerializedName("prezzo")
    private Float prezzo;

    @SerializedName("aperturaPrevendite")
    @JsonAdapter(DateTimeAdapter.class)
    private DateTime aperturaPrevendite;

    @SerializedName("chiusuraPrevendite")
    @JsonAdapter(DateTimeAdapter.class)
    private DateTime chiusuraPrevendite;

    public WTipoPrevendita(Integer id, Integer idEvento, String nome, String descrizione, Float prezzo, DateTime aperturaPrevendite, DateTime chiusuraPrevendite) {
        this.id = id;
        this.idEvento = idEvento;
        this.nome = nome;
        this.descrizione = descrizione;
        this.prezzo = prezzo;
        this.aperturaPrevendite = aperturaPrevendite;
        this.chiusuraPrevendite = chiusuraPrevendite;
    }

    public WTipoPrevendita(Integer id, Integer idEvento, String nome, String descrizione, Float prezzo, String aperturaPrevendite, String chiusuraPrevendite) {
        this(id, idEvento, nome, descrizione, prezzo, new DateTime(aperturaPrevendite), new DateTime(chiusuraPrevendite));
    }

    public Integer getId() {
        return id;
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

    public Float getPrezzo() {
        return prezzo;
    }

    public DateTime getAperturaPrevendite() {
        return aperturaPrevendite;
    }

    public DateTime getChiusuraPrevendite() {
        return chiusuraPrevendite;
    }

    @Override
    public String getRemoteClassPath() {
        return CLASS_PATH;
    }

//    @Override
//    public String getRemoteArgName() {
//        return ARG_NAME;
//    }
}
