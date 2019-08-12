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

import com.google.gson.annotations.SerializedName;
import com.prapp.model.net.Twinned;
import com.prapp.model.net.serialize.JSONDeserializable;
import com.prapp.model.net.serialize.JSONSerializable;

public class WUtente implements DatabaseWrapper, Twinned, JSONSerializable, JSONDeserializable {

    public static final String CLASS_PATH = "com\\model\\db\\wrapper\\WUtente";

/*    public static WUtente jsonDeserialize(JSONObject obj) throws JSONException {
        Integer id = obj.getInt("id");
        String nome = obj.getString("nome");
        String cognome = obj.getString("cognome");
        String telefono = obj.getString("telefono");

        return new WUtente(id, nome, cognome, telefono);
    }*/

    public static WUtente getEmpty()
    {
        return new WUtente(0, "", "", "");
    }

    @SerializedName("id")
    private  Integer id;

    @SerializedName("nome")
    private String nome;

    @SerializedName("cognome")
    private String cognome;

    @SerializedName("telefono")
    private String telefono;

    public WUtente(Integer id, String nome, String cognome, String telefono) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.telefono = telefono;
    }

    public Integer getId() {
        return id;
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

    @Override
    public String getRemoteClassPath() {
        return CLASS_PATH;
    }

/*    @Override
    public JSONObject jsonSerialize() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("id", getId());
        obj.put("nome", getNome());
        obj.put("cognome", getCognome());
        obj.put("telefono", getTelefono());
        return obj;
    }*/
}
