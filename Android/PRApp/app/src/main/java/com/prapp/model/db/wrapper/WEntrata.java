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

public class WEntrata implements  DatabaseWrapper, Twinned, JSONSerializable, JSONDeserializable {

    public static final String CLASS_PATH = "com\\model\\db\\wrapper\\WEntrata";

    public static WEntrata getEmpty()
    {
        return new WEntrata(0, 0, "1970-01-01T00:00:00.000Z");
    }

    @SerializedName("idCassiere")
    private Integer idCassiere;

    @SerializedName("idPrevendita")
    private Integer idPrevendita;

    @SerializedName("timestampEntrata")
    @JsonAdapter(DateTimeAdapter.class)
    private DateTime timestampEntrata;


    public WEntrata(Integer idCassiere, Integer idPrevendita, DateTime timestampEntrata) {
        this.idCassiere = idCassiere;
        this.idPrevendita = idPrevendita;
        this.timestampEntrata = timestampEntrata;
    }

    public WEntrata(Integer idCassiere, Integer idPrevendita, String timestampEntrata) {
        this(idCassiere, idPrevendita, new DateTime(timestampEntrata));
    }

    public Integer getIdCassiere() {
        return idCassiere;
    }

    public Integer getIdPrevendita() {
        return idPrevendita;
    }

    public DateTime getTimestampEntrata() {
        return timestampEntrata;
    }

    @Override
    public String getRemoteClassPath() {
        return CLASS_PATH;
    }
}
