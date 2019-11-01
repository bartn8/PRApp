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

import android.util.SparseArray;

import androidx.annotation.Nullable;

public enum StatoEvento {

    VALIDO(0, "VALIDO"),
    ANNULLATO(1, "ANNULLATO"),
    RIMBORSATO(2, "RIMBORSATO"),
    PAGATO(3, "PAGATO");

    private static final SparseArray<StatoEvento> mappaId = new SparseArray<>();

    static {
        //Inizializzo le mappe
        mappaId.put(StatoEvento.VALIDO.getId(), StatoEvento.VALIDO);
        mappaId.put(StatoEvento.ANNULLATO.getId(), StatoEvento.ANNULLATO);
        mappaId.put(StatoEvento.RIMBORSATO.getId(), StatoEvento.RIMBORSATO);
        mappaId.put(StatoEvento.PAGATO.getId(), StatoEvento.PAGATO);
    }

    @Nullable
    public static StatoEvento parseId(int id) {
        return mappaId.get(id);
    }

    private int id;
    private String nome;
    private int resId;
    private String resValue;

    private StatoEvento(int id, String nome)
    {
        this.id = id;
        this.nome = nome;
        this.resId = -1;
        this.resValue = nome;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getResValue() {
        return resValue;
    }

    public void setResValue(String resValue) {
        if(resValue != null)
            this.resValue = resValue;
    }

    @Override
    public String toString() {
        return resValue;
    }
}
