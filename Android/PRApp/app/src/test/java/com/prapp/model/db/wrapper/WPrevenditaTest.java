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

import com.google.gson.Gson;
import com.prapp.model.db.enums.StatoPrevendita;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WPrevenditaTest {



    @Test
    public void jsonSerializeTest()
    {
        WPrevendita obj = WPrevendita.getEmpty();
        Gson gson = new Gson();
        assertEquals("{\"id\":0,\"idEvento\":0,\"idPR\":0,\"idCliente\":0,\"idTipoPrevendita\":0,\"codice\":\"\",\"stato\":0,\"timestampUltimaModifica\":\"1970-01-01T00:00:00.000Z\"}", gson.toJson(obj));
    }


    @Test
    public void jsonDeserializeTest()
    {
        String jsonObj = "{\"id\":0,\"idEvento\":0,\"idPR\":0,\"idTipoPrevendita\":0,\"codice\":\"\",\"stato\":0,\"timestampUltimaModifica\":\"1970-01-01T00:00:00.000Z\"}";
        Gson gson = new Gson();
        WPrevendita obj = gson.fromJson(jsonObj, WPrevendita.class);

        assertEquals(0, obj.getId().intValue());
        assertEquals(0, obj.getIdEvento().intValue());
        assertEquals(0, obj.getIdPR().intValue());
        assertEquals(0, obj.getIdTipoPrevendita().intValue());
        assertEquals(new DateTime("1970-01-01T00:00:00.000Z"), obj.getTimestampUltimaModifica());
        assertEquals(StatoPrevendita.CONSEGNATA, obj.getStato());

        assertFalse(obj.isIdClientePresent());

        String jsonObj2 = "{\"id\":0,\"idEvento\":0,\"idPR\":0,\"idCliente\":0,\"idTipoPrevendita\":0,\"codice\":\"\",\"stato\":0,\"timestampUltimaModifica\":\"1970-01-01T00:00:00.000Z\"}";
        WPrevendita obj2 = gson.fromJson(jsonObj2, WPrevendita.class);

        assertEquals(0, obj2.getId().intValue());
        assertEquals(0, obj2.getIdEvento().intValue());
        assertEquals(0, obj2.getIdPR().intValue());
        assertEquals(0, obj2.getIdCliente().intValue());
        assertEquals(0, obj2.getIdTipoPrevendita().intValue());
        assertEquals(new DateTime("1970-01-01T00:00:00.000Z"), obj2.getTimestampUltimaModifica());
        assertEquals(StatoPrevendita.CONSEGNATA, obj2.getStato());

        assertTrue(obj2.isIdClientePresent());

    }

}