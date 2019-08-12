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

package com.prapp.model.db.enums;

import java.text.ParseException;

public enum StatoPrevendita {

    CONSEGNATA(0, "CONSEGNATA"),
    PAGATA(1, "PAGATA"),
    ANNULLATA(2, "ANNULLATA"),
    RIMBORSATA(3, "RIMBORSATA");

    public static StatoPrevendita parseId(int id) throws ParseException {
        StatoPrevendita[] values = StatoPrevendita.values();

        for (StatoPrevendita value: values) {
            if(value.getId() == id)
            {
                return value;
            }
        }

        throw new ParseException("ID non valido", id);
    }

    private int id;
    private String nome;

    private StatoPrevendita(int id, String nome)
    {
        this.id = id;
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

}
