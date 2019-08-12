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

public class WToken implements DatabaseWrapper, Twinned, JSONSerializable, JSONDeserializable {

    public static final String CLASS_PATH = "com\\model\\db\\wrapper\\WToken";


    public static WToken getEmpty()
    {
        return new WToken(0, "", "1970-01-01T00:00:00.000Z");
    }

    @SerializedName("id")
    private Integer id;

    @SerializedName("token")
    private String token;

    @SerializedName("scadenzaToken")
    @JsonAdapter(DateTimeAdapter.class)
    private DateTime scadenzaToken;

    public WToken(Integer id, String token, DateTime scadenzaToken) {
        this.id = id;
        this.token = token;
        this.scadenzaToken = scadenzaToken;
    }

    public WToken(Integer id, String token, String scadenzaToken) {
        this(id, token, new DateTime(scadenzaToken));//ISO8061
    }

    public Integer getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public DateTime getScadenzaToken() {
        return scadenzaToken;
    }

    public boolean isTokenValid()
    {
        if(getScadenzaToken() == null)
            return false;

        DateTime now = new DateTime();
        return now.compareTo(getScadenzaToken()) < 0;
    }

    @Override
    public String getRemoteClassPath() {
        return CLASS_PATH;
    }
}
