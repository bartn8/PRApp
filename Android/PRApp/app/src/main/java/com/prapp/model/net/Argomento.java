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

package com.prapp.model.net;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.prapp.model.net.serialize.JSONSerializable;

public class Argomento {

    private static final Gson GSON_OBJECT = new Gson();

    @SerializedName("name")
    private String nome;

    @SerializedName("type")
    private String remoteClassPath;

    @SerializedName("value")
    private JsonElement oggetto;

    public Argomento(String nome, String remoteClassPath, JSONSerializable oggetto) {
        this.nome = nome;
        this.remoteClassPath = remoteClassPath;
        this.oggetto = GSON_OBJECT.toJsonTree(oggetto, oggetto.getClass());
    }

    public String getNome() {
        return nome;
    }

    public String getRemoteClassPath() {
        return remoteClassPath;
    }

    public JsonElement getOggetto() {
        return oggetto;
    }


}
