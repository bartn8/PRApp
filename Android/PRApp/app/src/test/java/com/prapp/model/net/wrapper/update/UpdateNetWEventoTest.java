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

package com.prapp.model.net.wrapper.update;

import com.google.gson.Gson;

import org.junit.Test;

import static org.junit.Assert.*;

public class UpdateNetWEventoTest {

    @Test
    public void jsonSerializeTest()
    {
        UpdateNetWEvento obj = UpdateNetWEvento.getEmpty();
        Gson gson = new Gson();
        assertEquals("{\"idEvento\":0,\"nome\":\"\",\"inizio\":\"1970-01-01T00:00:00.000Z\",\"fine\":\"1970-01-01T00:00:00.000Z\",\"indirizzo\":\"\",\"città\":\"\",\"provincia\":\"\",\"stato\":\"\",\"statoEvento\":0}", gson.toJson(obj));
    }

}