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

package com.prapp.model.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prapp.model.net.serialize.adapter.DateTimeAdapter;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RispostaTest {

    private static final Gson GSON_OBJECT = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeAdapter()).create();

    @Test
    public void jsonDeserializeTest()
    {
        String jsonObj = "{\"command\":952,\"status\":0,\"results\":[\"2019-07-09T22:47:02+02:00\"],\"exceptions\":[]}";
        Risposta obj = GSON_OBJECT.fromJson(jsonObj, Risposta.class);

        Risultato risultato = obj.getRisultati().get(0);
        DateTime myDate = risultato.castRisultato(DateTime.class);

        assertEquals(952, obj.getComando());
        assertEquals(0, obj.getStatoRisposta());
        assertEquals(new DateTime("2019-07-09T22:47:02+02:00"), myDate);
        assertEquals(0, obj.getEccezioni().size());
    }

}