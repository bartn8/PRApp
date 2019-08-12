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
import com.google.gson.JsonObject;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Risultato {

    private static Gson GSON_OBJECT = new Gson();

    private JsonObject risultato;

    public Risultato(JsonObject risultato) {
        this.risultato = risultato;
    }

    public <T> T castRisultato(Class<T> clazz)
    {
        return castRisultato(GSON_OBJECT, clazz);
    }

    public <T> T castRisultato(Gson gson, Class<T> clazz)
    {
        return gson.fromJson(risultato, clazz);
    }

    public boolean getAsBoolean() {
        return risultato.getAsBoolean();
    }

    public Number getAsNumber() {
        return risultato.getAsNumber();
    }

    public String getAsString() {
        return risultato.getAsString();
    }

    public double getAsDouble() {
        return risultato.getAsDouble();
    }

    public float getAsFloat() {
        return risultato.getAsFloat();
    }

    public long getAsLong() {
        return risultato.getAsLong();
    }

    public int getAsInt() {
        return risultato.getAsInt();
    }

    public byte getAsByte() {
        return risultato.getAsByte();
    }

    public char getAsCharacter() {
        return risultato.getAsCharacter();
    }

    public BigDecimal getAsBigDecimal() {
        return risultato.getAsBigDecimal();
    }

    public BigInteger getAsBigInteger() {
        return risultato.getAsBigInteger();
    }

    public short getAsShort() {
        return risultato.getAsShort();
    }
}
