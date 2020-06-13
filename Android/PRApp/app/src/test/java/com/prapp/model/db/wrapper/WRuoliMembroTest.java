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
import com.prapp.model.db.enums.Ruolo;

import org.junit.Test;

import static org.junit.Assert.*;

public class WRuoliMembroTest {

    @Test
    public void jsonSerializeTest()
    {
        WRuoliMembro obj = WRuoliMembro.getEmpty();
        Gson gson = new Gson();
        assertEquals("{\"idUtente\":0,\"idStaff\":0,\"diritti\":[]}", gson.toJson(obj));
    }


    @Test
    public void jsonDeserializeTest()
    {
        String jsonObj = "{\"idUtente\":0,\"idStaff\":1,\"diritti\":[0,1]}";
        Gson gson = new Gson();
        WRuoliMembro obj = gson.fromJson(jsonObj, WRuoliMembro.class);

        assertEquals(0, obj.getIdUtente().intValue());
        assertEquals(1, obj.getIdStaff().intValue());
        assertTrue(obj.getRuoli().contains(Ruolo.PR));
        assertTrue(obj.getRuoli().contains(Ruolo.CASSIERE));
        assertFalse(obj.getRuoli().contains(Ruolo.AMMINISTRATORE));
    }

}