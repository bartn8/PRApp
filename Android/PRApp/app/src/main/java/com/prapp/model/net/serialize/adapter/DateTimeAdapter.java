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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.lang.reflect.Type;

public class DateTimeAdapter implements JsonSerializer<DateTime>, JsonDeserializer<DateTime> {
    @Override
    public JsonElement serialize(DateTime src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());//ISO8061
    }

    @Override
    public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        DateTime dateTime = new DateTime(json.getAsString());//ISO8061
        return dateTime.withZone(DateTimeZone.getDefault());
    }
}
