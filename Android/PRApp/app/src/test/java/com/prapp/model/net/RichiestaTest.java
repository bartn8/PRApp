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
import com.prapp.model.db.wrapper.WUtente;
import com.prapp.model.net.enums.Comando;
import com.prapp.model.net.wrapper.insert.InsertNetWCliente;

import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RichiestaTest {

    private static final Gson GSON_OBJECT = new Gson();

    private Richiesta req;
    private Richiesta reqNoArgs;

    @Before
    public void init() throws MalformedURLException
    {
        List<Argomento> myList = new ArrayList<>();

        InsertNetWCliente arg1 = InsertNetWCliente.getEmpty();
        WUtente arg2 = WUtente.getEmpty();

        myList.add(new Argomento("cliente", arg1.getRemoteClassPath(), arg1));
        myList.add(new Argomento("utente", arg2.getRemoteClassPath(), arg2));

        req = new Richiesta(Comando.COMANDO_MANUTENZIONE_ECHO, myList);
        reqNoArgs = new Richiesta(Comando.COMANDO_MANUTENZIONE_ECHO);
    }

    @Test
    public void getArgomentiAsJsonString() {
        String myString = req.getArgomentiAsJsonString();
        assertEquals("[{\"name\":\"cliente\",\"type\":\"com\\\\\\\\model\\\\\\\\net\\\\\\\\wrapper\\\\\\\\insert\\\\\\\\InsertNetWCliente\",\"value\":{\"idStaff\":0,\"nome\":\"\",\"cognome\":\"\",\"telefono\":\"\",\"dataDiNascita\":\"1970-01-01\",\"codiceFiscale\":\"\"}},{\"name\":\"utente\",\"type\":\"com\\\\\\\\model\\\\\\\\db\\\\\\\\wrapper\\\\\\\\WUtente\",\"value\":{\"id\":0,\"nome\":\"\",\"cognome\":\"\",\"telefono\":\"\"}}]", myString);

        String myStringNoArgs = reqNoArgs.getArgomentiAsJsonString();
        assertEquals("[]", myStringNoArgs);
    }

    @Test
    public void generatePOSTQueryTest() throws UnsupportedEncodingException {
        String postQuery = req.generatePOSTQuery();
        assertEquals("command=951&args=%5B%7B%22name%22%3A%22cliente%22%2C%22type%22%3A%22com%5C%5C%5C%5Cmodel%5C%5C%5C%5Cnet%5C%5C%5C%5Cwrapper%5C%5C%5C%5Cinsert%5C%5C%5C%5CInsertNetWCliente%22%2C%22value%22%3A%7B%22idStaff%22%3A0%2C%22nome%22%3A%22%22%2C%22cognome%22%3A%22%22%2C%22telefono%22%3A%22%22%2C%22dataDiNascita%22%3A%221970-01-01%22%2C%22codiceFiscale%22%3A%22%22%7D%7D%2C%7B%22name%22%3A%22utente%22%2C%22type%22%3A%22com%5C%5C%5C%5Cmodel%5C%5C%5C%5Cdb%5C%5C%5C%5Cwrapper%5C%5C%5C%5CWUtente%22%2C%22value%22%3A%7B%22id%22%3A0%2C%22nome%22%3A%22%22%2C%22cognome%22%3A%22%22%2C%22telefono%22%3A%22%22%7D%7D%5D", postQuery);

        String postQueryNoArgs = reqNoArgs.generatePOSTQuery();
        assertEquals("command=951&args=%5B%5D", postQueryNoArgs);
    }



}