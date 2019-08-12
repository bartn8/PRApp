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

public class WStaff implements  DatabaseWrapper, Twinned, JSONSerializable, JSONDeserializable {

    public static final String CLASS_PATH = "com\\model\\db\\wrapper\\WStaff";

    public static WStaff getEmpty()
    {
        return new WStaff(0, "", "1970-01-01T00:00:00.000Z");
    }

    @SerializedName("id")
    private Integer id;

    @SerializedName("nome")
    private String nome;

    @SerializedName("timestampCreazione")
    @JsonAdapter(DateTimeAdapter.class)
    private DateTime timestampCreazione;

    public WStaff(Integer id, String nome, DateTime timestampCreazione) {
        this.id = id;
        this.nome = nome;
        this.timestampCreazione = timestampCreazione;
    }

    public WStaff(Integer id, String nome, String timestampCreazione) {
        this(id, nome, new DateTime(timestampCreazione));
    }

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public DateTime getTimestampCreazione() {
        return timestampCreazione;
    }

    @Override
    public String getRemoteClassPath() {
        return CLASS_PATH;
    }

}
