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

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.prapp.model.db.enums.StatoPrevendita;
import com.prapp.model.net.Twinned;
import com.prapp.model.net.serialize.JSONDeserializable;
import com.prapp.model.net.serialize.adapter.StatoPrevenditaAdapter;

import org.joda.time.DateTime;

import java.text.ParseException;

public class WPrevenditaPlus implements DatabaseWrapper, Twinned, JSONDeserializable {

    public static final String CLASS_PATH = "com\\model\\db\\wrapper\\WPrevenditaPlus";

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("idEvento")
    @Expose
    private int idEvento;

    @SerializedName("nomeEvento")
    @Expose
    private String nomeEvento;

    @SerializedName("idPR")
    @Expose
    private Integer idPR;

    @SerializedName("nomePR")
    @Expose
    private String nomePR;

    @SerializedName("cognomePR")
    @Expose
    private String cognomePR;

    //Optional perchè il cliente può cancellarsi.
    @SerializedName("nomeCliente")
    @Expose
    private String nomeCliente;

    //Optional perchè il cliente può cancellarsi.
    @SerializedName("cognomeCliente")
    @Expose
    private String cognomeCliente;

    @SerializedName("idTipoPrevendita")
    @Expose
    private Integer idTipoPrevendita;

    @SerializedName("nomeTipoPrevendita")
    @Expose
    private String nomeTipoPrevendita;

    @SerializedName("prezzoTipoPrevendita")
    @Expose
    private Float prezzoTipoPrevendita;

    @SerializedName("codice")
    @Expose
    private String codice;

    @SerializedName("stato")
    @Expose
    @JsonAdapter(StatoPrevenditaAdapter.class)
    private StatoPrevendita stato;

    public WPrevenditaPlus(Integer id, int idEvento, String nomeEvento, Integer idPR, String nomePR, String nomeCliente, String cognomeCliente, Integer idTipoPrevendita, String nomeTipoPrevendita, Float prezzoTipoPrevendita, String codice, StatoPrevendita stato) {
        this.id = id;
        this.idEvento = idEvento;
        this.nomeEvento = nomeEvento;
        this.idPR = idPR;
        this.nomePR = nomePR;
        this.nomeCliente = nomeCliente;
        this.cognomeCliente = cognomeCliente;
        this.idTipoPrevendita = idTipoPrevendita;
        this.nomeTipoPrevendita = nomeTipoPrevendita;
        this.prezzoTipoPrevendita = prezzoTipoPrevendita;
        this.codice = codice;
        this.stato = stato;
    }

    public WPrevenditaPlus(Integer id, int idEvento, String nomeEvento, Integer idPR, String nomePR, String nomeCliente, String cognomeCliente, Integer idTipoPrevendita, String nomeTipoPrevendita, Float prezzoTipoPrevendita, String codice, int stato) throws ParseException {
        this(id, idEvento, nomeEvento, idPR, nomePR, nomeCliente, cognomeCliente, idTipoPrevendita, nomeTipoPrevendita, prezzoTipoPrevendita, codice, StatoPrevendita.parseId(stato));
    }

    public Integer getId() {
        return id;
    }

    public int getIdEvento() {
        return idEvento;
    }

    public String getNomeEvento() {
        return nomeEvento;
    }

    public Integer getIdPR() {
        return idPR;
    }

    public String getNomePR() {
        return nomePR;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public Integer getIdTipoPrevendita() {
        return idTipoPrevendita;
    }

    public String getNomeTipoPrevendita() {
        return nomeTipoPrevendita;
    }

    public Float getPrezzoTipoPrevendita() {
        return prezzoTipoPrevendita;
    }

    public String getCodice() {
        return codice;
    }

    public StatoPrevendita getStato() {
        return stato;
    }

    public String getCognomePR() {
        return cognomePR;
    }

    public String getCognomeCliente() {
        return cognomeCliente;
    }

    @Override
    public String getRemoteClassPath() {
        return CLASS_PATH;
    }


    public WPrevendita getWPrevendita(){
        return new WPrevendita(id, idEvento, idPR, nomeCliente, cognomeCliente, idTipoPrevendita, codice, stato, new DateTime());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof WPrevenditaPlus){
            WPrevenditaPlus other = (WPrevenditaPlus)obj;
            return getId().intValue() == other.getId().intValue();
        }

        return false;
    }
}
