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
import com.prapp.model.db.enums.Diritto;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class DirittoSetAdapter implements JsonSerializer<Set<Diritto>>, JsonDeserializer<Set<Diritto>> {
    @Override
    public JsonElement serialize(Set<Diritto> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray array = new JsonArray();

        Iterator<Diritto> iterator = src.iterator();

        while (iterator.hasNext()) {
            Diritto next = iterator.next();
            array.add(next.getId());
        }

        return array;
    }


    @Override
    public Set<Diritto> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        TreeSet<Diritto> set = new TreeSet<>();
        JsonArray array = json.getAsJsonArray();
        Iterator<JsonElement> iterator = array.iterator();

        while (iterator.hasNext()) {
            JsonElement next = iterator.next();
            Diritto diritto = Diritto.parseId(next.getAsInt());

            if(diritto == null) throw new JsonParseException("Diritto non valido (parse exception)");

            set.add(diritto);
        }

        return set;
    }
}
