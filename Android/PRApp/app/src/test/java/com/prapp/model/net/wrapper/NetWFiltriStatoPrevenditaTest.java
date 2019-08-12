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

package com.prapp.model.net.wrapper;

import com.google.gson.Gson;
import com.prapp.model.db.enums.StatoPrevendita;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class NetWFiltriStatoPrevenditaTest {

    @Test
    public void jsonSerializeTest()
    {
        NetWFiltriStatoPrevendita obj = new NetWFiltriStatoPrevendita(Arrays.asList(new StatoPrevendita[]{StatoPrevendita.CONSEGNATA, StatoPrevendita.RIMBORSATA}));
        Gson gson = new Gson();

        assertEquals("{\"filtri\":[0,3]}", gson.toJson(obj));
    }

}
