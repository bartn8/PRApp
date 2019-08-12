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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;

public class DateTimeAdapterTest {

    private static final String DEFAULT_DATETIME = "1970-01-01T00:00:00.000Z";

    private Gson gson;

    @Before
    public void initGson()
    {

        gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeAdapter()).create();
    }

    @Test
    public void localZoneTest()
    {
        DateTime dateTime = gson.fromJson(DEFAULT_DATETIME, DateTime.class);

        System.out.println(DEFAULT_DATETIME);
        System.out.println(dateTime.withZone(DateTimeZone.forID("Europe/Rome")));

    }


}