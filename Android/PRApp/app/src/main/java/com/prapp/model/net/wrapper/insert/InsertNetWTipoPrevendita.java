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

package com.prapp.model.net.wrapper.insert;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.prapp.model.net.Twinned;
import com.prapp.model.net.serialize.JSONSerializable;
import com.prapp.model.net.serialize.adapter.DateTimeAdapter;
import com.prapp.model.net.wrapper.NetWrapper;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;

public class InsertNetWTipoPrevendita implements Twinned, NetWrapper, JSONSerializable {

    public static final String CLASS_PATH = "com\\model\\net\\wrapper\\insert\\InsertNetWTipoPrevendita";


    @NotNull
    @org.jetbrains.annotations.Contract(" -> new")
    public static InsertNetWTipoPrevendita getEmpty()
    {
        return new InsertNetWTipoPrevendita("", "", 0.00f, new DateTime("1970-01-01T00:00:00.000Z"), new DateTime("1970-01-01T00:00:00.000Z"));
    }

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

    public InsertNetWTipoPrevendita(String nome, String descrizione, Float prezzo, DateTime aperturaPrevendite, DateTime chiusuraPrevendite) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.prezzo = prezzo;
        this.aperturaPrevendite = aperturaPrevendite;
        this.chiusuraPrevendite = chiusuraPrevendite;
    }

    public InsertNetWTipoPrevendita(String nome, String descrizione, Float prezzo, String aperturaPrevendite, String chiusuraPrevendite) {
        this(nome, descrizione, prezzo, new DateTime(aperturaPrevendite), new DateTime(chiusuraPrevendite));
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

}
