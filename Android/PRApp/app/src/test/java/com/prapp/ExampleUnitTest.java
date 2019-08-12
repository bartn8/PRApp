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

package com.prapp;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.prapp.model.net.wrapper.NetWFiltriStatoPrevendita;
import com.prapp.model.net.wrapper.insert.InsertNetWCliente;
import com.prapp.model.net.wrapper.insert.InsertNetWEvento;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void howListToJSONWork() throws JSONException {
        List<Integer> testList = new ArrayList<>();
        testList.add(1);
        testList.add(2);
        testList.add(3);

        Gson gson = new Gson();

        assertEquals("{\"filtri\":[1,2,3]}", gson.toJson(testList));
    }

    @Test
    public void testISO8061()
    {
        DateTime dateTime = new DateTime("2019-07-08T12:08:46+02:00");
        assertEquals("2019-07-08T10:08:46.000Z", dateTime.toString());
    }

    @Test
    public void testIntegerJSON()
    {
        Gson gson = new Gson();
        Integer myInteger = 1;
        assertEquals("1", gson.toJson(myInteger));
    }

}