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

package com.prapp.model.net.wrapper.insert;

import com.google.gson.Gson;

import org.joda.time.LocalDate;
import org.junit.Test;

import static org.junit.Assert.*;

public class InsertNetWClienteTest {

    @Test
    public void jsonSerializeTest()
    {
        InsertNetWCliente obj = new InsertNetWCliente(0, "", "", "", new LocalDate("1970-01-01"), "");
        Gson gson = new Gson();
        assertEquals("{\"idStaff\":0,\"nome\":\"\",\"cognome\":\"\",\"telefono\":\"\",\"dataDiNascita\":\"1970-01-01\",\"codiceFiscale\":\"\"}", gson.toJson(obj));
    }

}