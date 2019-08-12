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

package com.prapp.model.net.wrapper;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.prapp.model.db.enums.StatoPrevendita;
import com.prapp.model.net.Twinned;
import com.prapp.model.net.serialize.JSONSerializable;
import com.prapp.model.net.serialize.adapter.StatoPrevenditaAdapter;
import com.prapp.model.net.serialize.adapter.StatoPrevenditaListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class NetWFiltriStatoPrevendita implements Twinned, NetWrapper, JSONSerializable {

    public static final String CLASS_PATH = "com\\model\\net\\wrapper\\NetWFiltriStatoPrevendita";

    public static NetWFiltriStatoPrevendita getEmpty()
    {
        return new NetWFiltriStatoPrevendita(new ArrayList<StatoPrevendita>());
    }

    @SerializedName("filtri")
    @JsonAdapter(StatoPrevenditaListAdapter.class)
    private List<StatoPrevendita> filtri;

    public NetWFiltriStatoPrevendita(List<StatoPrevendita> filtri) {
        this.filtri = filtri;
    }

    public NetWFiltriStatoPrevendita(StatoPrevendita[] filtri) {
        this(Arrays.asList(filtri));
    }

    public List<StatoPrevendita> getFiltri() {
        return filtri;
    }

    @Override
    public String getRemoteClassPath() {
        return CLASS_PATH;
    }

/*    @Override
    public JSONObject jsonSerialize() throws JSONException {

        JSONObject obj = new JSONObject();
        JSONArray myArray = new JSONArray();

        Iterator<StatoPrevendita> iterator = filtri.iterator();

        while(iterator.hasNext())
        {
            StatoPrevendita next = iterator.next();
            myArray.put(next.getId());
        }

        obj.put("filtri", myArray);

        return obj;
    }*/

}
