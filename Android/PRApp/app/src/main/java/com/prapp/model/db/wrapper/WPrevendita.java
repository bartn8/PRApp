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
import com.prapp.model.db.enums.StatoPrevendita;
import com.prapp.model.net.Twinned;
import com.prapp.model.net.serialize.JSONDeserializable;
import com.prapp.model.net.serialize.JSONSerializable;
import com.prapp.model.net.serialize.adapter.DateTimeAdapter;
import com.prapp.model.net.serialize.adapter.StatoPrevenditaAdapter;

import org.joda.time.DateTime;

import java.text.ParseException;

public class WPrevendita implements DatabaseWrapper, Twinned, JSONSerializable, JSONDeserializable {

    public static final String CLASS_PATH = "com\\model\\db\\wrapper\\WPrevendita";

    public static WPrevendita getEmpty() {
        return new WPrevendita(0, 0, 0, "", "", 0, "", StatoPrevendita.VALIDA, "1970-01-01T00:00:00.000Z");
    }

    @SerializedName("id")
    private Integer id;

    @SerializedName("idEvento")
    private Integer idEvento;

    @SerializedName("idPR")
    private Integer idPR;

    @SerializedName("nomeCliente")
    private String nomeCliente;
    @SerializedName("cognomeCliente")
    private String cognomeCliente;

    @SerializedName("idTipoPrevendita")
    private Integer idTipoPrevendita;

    @SerializedName("codice")
    private String codice;

    @SerializedName("stato")
    @JsonAdapter(StatoPrevenditaAdapter.class)
    private StatoPrevendita stato;

    @SerializedName("timestampUltimaModifica")
    @JsonAdapter(DateTimeAdapter.class)
    private DateTime timestampUltimaModifica;

    public WPrevendita(Integer id, Integer idEvento, Integer idPR, String nomeCliente, String cognomeCliente, Integer idTipoPrevendita, String codice, StatoPrevendita stato, DateTime timestampUltimaModifica) {
        this.id = id;
        this.idEvento = idEvento;
        this.idPR = idPR;
        this.nomeCliente = nomeCliente;
        this.cognomeCliente = cognomeCliente;
        this.idTipoPrevendita = idTipoPrevendita;
        this.codice = codice;
        this.stato = stato;
        this.timestampUltimaModifica = timestampUltimaModifica;
    }

    public WPrevendita(Integer id, Integer idEvento, Integer idPR, String nomeCliente, String cognomeCliente, Integer idTipoPrevendita, String codice, StatoPrevendita stato, String timestampUltimaModifica) {
        this(id, idEvento, idPR, nomeCliente, cognomeCliente, idTipoPrevendita, codice, stato, new DateTime(timestampUltimaModifica));
    }

    public WPrevendita(Integer id, Integer idEvento, Integer idPR, String nomeCliente, String cognomeCliente, Integer idTipoPrevendita, String codice, int stato, String timestampUltimaModifica) throws ParseException {
        this(id, idEvento, idPR, nomeCliente, cognomeCliente, idTipoPrevendita, codice, StatoPrevendita.parseId(stato), timestampUltimaModifica);
    }

    public Integer getId() {
        return id;
    }

    public Integer getIdEvento() {
        return idEvento;
    }

    public Integer getIdPR() {
        return idPR;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public String getCognomeCliente() {
        return cognomeCliente;
    }

    public Integer getIdTipoPrevendita() {
        return idTipoPrevendita;
    }

    public String getCodice() {
        return codice;
    }

    public StatoPrevendita getStato() {
        return stato;
    }

    public DateTime getTimestampUltimaModifica() {
        return timestampUltimaModifica;
    }

    @Override
    public String getRemoteClassPath() {
        return CLASS_PATH;
    }

}
