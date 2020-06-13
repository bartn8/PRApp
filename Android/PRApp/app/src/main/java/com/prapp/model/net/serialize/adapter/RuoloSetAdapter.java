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
import com.prapp.model.db.enums.Ruolo;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class RuoloSetAdapter implements JsonSerializer<Set<Ruolo>>, JsonDeserializer<Set<Ruolo>> {
    @Override
    public JsonElement serialize(Set<Ruolo> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray array = new JsonArray();

        Iterator<Ruolo> iterator = src.iterator();

        while (iterator.hasNext()) {
            Ruolo next = iterator.next();
            array.add(next.getId());
        }

        return array;
    }


    @Override
    public Set<Ruolo> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        TreeSet<Ruolo> set = new TreeSet<>();
        JsonArray array = json.getAsJsonArray();
        Iterator<JsonElement> iterator = array.iterator();

        while (iterator.hasNext()) {
            JsonElement next = iterator.next();
            Ruolo ruolo = Ruolo.parseId(next.getAsInt());

            if(ruolo == null) throw new JsonParseException("Ruolo non valido (parse exception)");

            set.add(ruolo);
        }

        return set;
    }
}
