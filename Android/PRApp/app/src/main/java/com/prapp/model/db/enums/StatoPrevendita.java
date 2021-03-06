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

public enum StatoPrevendita {

    VALIDA(0, "VALIDA"),
    ANNULLATA(1, "ANNULLATA"),
    ANNULLATA_NON_RIMBORSATA(2, "ANNULLATA_NON_RIMBORSATA");

    private static final int LENGTH = 3;

    private static final SparseArray<StatoPrevendita> mappaId = new SparseArray<>(LENGTH);

    static {
        //Inizializzo le mappe
        mappaId.put(StatoPrevendita.VALIDA.getId(), StatoPrevendita.VALIDA);
        mappaId.put(StatoPrevendita.ANNULLATA.getId(), StatoPrevendita.ANNULLATA);
        mappaId.put(StatoPrevendita.ANNULLATA_NON_RIMBORSATA.getId(), StatoPrevendita.ANNULLATA_NON_RIMBORSATA);
    }

    @Nullable
    public static StatoPrevendita parseId(int id) {
        return mappaId.get(id);
    }

    public static String[] stringResValues(){
        String[] stringValues = new String[LENGTH];

        for(int i = 0; i < LENGTH; i++){
            stringValues[i] = parseId(i).getResValue();
        }

        return stringValues;
    }

    private int id;
    private String nome;
    private int resId;
    private String resValue;

    StatoPrevendita(int id, String nome) {
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
