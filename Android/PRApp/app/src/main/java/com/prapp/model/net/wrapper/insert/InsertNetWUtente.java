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

import com.google.gson.annotations.SerializedName;
import com.prapp.model.net.Twinned;
import com.prapp.model.net.serialize.JSONSerializable;
import com.prapp.model.net.wrapper.NetWrapper;

public class InsertNetWUtente implements Twinned, NetWrapper, JSONSerializable {

    public static final String CLASS_PATH = "com\\model\\net\\wrapper\\insert\\InsertNetWUtente";
    public static final String ARG_NAME = "utente";

    public static InsertNetWUtente getEmpty()
    {
        return new InsertNetWUtente("","","","","");
    }

    @SerializedName("nome")
    private String nome;

    @SerializedName("cognome")
    private String cognome;

    @SerializedName("telefono")
    private String telefono;

    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;


    public InsertNetWUtente(String nome, String cognome, String telefono, String username, String password) {
        this.nome = nome;
        this.cognome = cognome;
        this.telefono = telefono;
        this.username = username;
        this.password = password;
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

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void clear()
    {
        this.username = "";
        this.password = "";
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
