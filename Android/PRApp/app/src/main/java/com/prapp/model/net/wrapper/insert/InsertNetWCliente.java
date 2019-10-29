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
import com.prapp.model.net.serialize.adapter.LocalDateAdapter;
import com.prapp.model.net.wrapper.NetWrapper;

import org.joda.time.LocalDate;


public class InsertNetWCliente implements Twinned, NetWrapper, JSONSerializable {

    public static final String CLASS_PATH = "com\\model\\net\\wrapper\\insert\\InsertNetWCliente";
    public static final String ARG_NAME = "cliente";

    public static InsertNetWCliente getEmpty()
    {
        return new InsertNetWCliente(0, "", "", "", new LocalDate("1970-01-01"), "");
    }

    @SerializedName("idStaff")
    private Integer idStaff;

    @SerializedName("nome")
    private String nome;

    @SerializedName("cognome")
    private String cognome;

    //Optional
    @SerializedName("telefono")
    private String telefono;

    //Optional
    @SerializedName("dataDiNascita")
    @JsonAdapter(LocalDateAdapter.class)
    private LocalDate dataDiNascita;

    //Optional
    @SerializedName("codiceFiscale")
    private String codiceFiscale;
    //private Optional<String> codiceFiscale;


    public InsertNetWCliente(Integer idStaff, String nome, String cognome, String telefono, LocalDate dataDiNascita, String codiceFiscale) {
        this.idStaff = idStaff;
        this.nome = nome;
        this.cognome = cognome;
        this.telefono = telefono;
        this.dataDiNascita = dataDiNascita;
        //this.codiceFiscale = Optional.ofNullable(codiceFiscale);
        this.codiceFiscale = codiceFiscale;
    }

    //Usare ISO8601 per dataDiNascita
    public InsertNetWCliente(Integer idStaff, String nome, String cognome, String telefono, String dataDiNascita, String codiceFiscale) {
        this(idStaff, nome, cognome, telefono, dataDiNascita != null ? new LocalDate(dataDiNascita) : null, codiceFiscale);
    }

    public Integer getIdStaff() {
        return idStaff;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getTelefono() {
        return telefono;
    }

    public LocalDate getDataDiNascita() {
        return dataDiNascita;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public boolean isTelefonoPresent()
    {
        return telefono != null;
    }

    public boolean isCodiceFiscalePresent()
    {
        return codiceFiscale != null;
    }

    @Override
    public String getRemoteClassPath() {
        return CLASS_PATH;
    }


}
