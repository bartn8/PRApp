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
import com.prapp.model.net.serialize.adapter.LocalDateAdapter;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class WCliente implements  DatabaseWrapper, Twinned, JSONSerializable, JSONDeserializable {

    public static final String CLASS_PATH = "com\\model\\db\\wrapper\\WCliente";

    public static WCliente getEmpty()
    {
        return new WCliente(0,0,"","","","1970-01-01", "", "1970-01-01T00:00:00.000Z");
    }

    @SerializedName("id")
    private Integer id;

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

    @SerializedName("timestampInserimento")
    @JsonAdapter(DateTimeAdapter.class)
    private DateTime timestampInserimento;


    public WCliente(Integer id, Integer idStaff, String nome, String cognome, String telefono, LocalDate dataDiNascita, String codiceFiscale, DateTime timestampInserimento) {
        this.id = id;
        this.idStaff = idStaff;
        this.nome = nome;
        this.cognome = cognome;
        this.telefono = telefono;
        this.dataDiNascita = dataDiNascita;
        this.codiceFiscale = codiceFiscale;
        this.timestampInserimento = timestampInserimento;
    }

    public WCliente(Integer id, Integer idStaff, String nome, String cognome, String telefono, String dataDiNascita, String codiceFiscale, String timestampInserimento) {
        this(id, idStaff, nome, cognome, telefono, dataDiNascita != null ? new LocalDate(dataDiNascita) : null, codiceFiscale, new DateTime(timestampInserimento));
    }

    public Integer getId() {
        return id;
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

    public DateTime getTimestampInserimento() {
        return timestampInserimento;
    }

    public boolean isTelefonoPresent()
    {
        return telefono != null;
    }

    public boolean isCodiceFiscalePresent()
    {
        return codiceFiscale != null;
    }

    public boolean isDataDiNascitaPresent(){
        return dataDiNascita != null;
    }

    @Override
    public String getRemoteClassPath() {
        return CLASS_PATH;
    }

}
