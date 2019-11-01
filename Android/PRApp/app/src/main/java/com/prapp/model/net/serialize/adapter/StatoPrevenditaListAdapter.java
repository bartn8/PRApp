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

package com.prapp.model.net.serialize.adapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.prapp.model.db.enums.StatoPrevendita;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StatoPrevenditaListAdapter implements JsonSerializer<List<StatoPrevendita>>, JsonDeserializer<List<StatoPrevendita>> {
    @Override
    public JsonElement serialize(List<StatoPrevendita> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray myArray = new JsonArray();

        Iterator<StatoPrevendita> iterator = src.iterator();

        while (iterator.hasNext()) {
            StatoPrevendita next = iterator.next();
            myArray.add(next.getId());
        }

        return myArray;
    }

    @Override
    public List<StatoPrevendita> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        ArrayList<StatoPrevendita> list = new ArrayList<>();
        JsonArray array = json.getAsJsonArray();
        Iterator<JsonElement> iterator = array.iterator();

        while (iterator.hasNext()) {
            JsonElement next = iterator.next();
            StatoPrevendita statoPrevendita = StatoPrevendita.parseId(next.getAsInt());

            if(statoPrevendita == null) throw new JsonParseException("Stato prevendita non valido (parse exception)");

            list.add(statoPrevendita);
        }

        return list;
    }
}
