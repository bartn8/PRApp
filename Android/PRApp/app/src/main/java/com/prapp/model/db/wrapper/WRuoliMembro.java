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

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.prapp.model.db.enums.Ruolo;
import com.prapp.model.net.Twinned;
import com.prapp.model.net.serialize.JSONDeserializable;
import com.prapp.model.net.serialize.JSONSerializable;
import com.prapp.model.net.serialize.adapter.RuoloSetAdapter;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class WRuoliMembro implements Serializable, DatabaseWrapper, Twinned, JSONSerializable, JSONDeserializable {

    private static final long serialVersionUID = 1L;

    public static final String CLASS_PATH = "com\\model\\db\\wrapper\\WRuoliMembro";

    public static WRuoliMembro getEmpty()
    {
        return new WRuoliMembro(0,0,new TreeSet<Ruolo>());
    }

    @SerializedName("idUtente")
    private Integer idUtente;

    @SerializedName("idStaff")
    private Integer idStaff;

    @SerializedName("ruoli")
    @JsonAdapter(RuoloSetAdapter.class)
    private Set<Ruolo> ruoli;

    public WRuoliMembro(Integer idUtente, Integer idStaff, Set<Ruolo> ruoli) {
        this.idUtente = idUtente;
        this.idStaff = idStaff;
        this.ruoli = ruoli;
    }

    public WRuoliMembro(Integer idUtente, Integer idStaff, Ruolo[] ruoli) {
        this.idUtente = idUtente;
        this.idStaff = idStaff;
        this.ruoli = new TreeSet<>();

        Collections.addAll(this.ruoli, ruoli);
    }

    public WRuoliMembro(Integer idUtente, Integer idStaff, Integer[] intDiritti) throws ParseException {
        this.idUtente = idUtente;
        this.idStaff = idStaff;
        this.ruoli = new TreeSet<>();

        for (Integer intDiritto : intDiritti) {
            Ruolo tmp = Ruolo.parseId(intDiritto);
            ruoli.add(tmp);
        }

    }

    public Integer getIdUtente() {
        return idUtente;
    }

    public Integer getIdStaff() {
        return idStaff;
    }

    public Set<Ruolo> getRuoli() {
        return ruoli;
    }

    @Override
    public String getRemoteClassPath() {
        return CLASS_PATH;
    }
}
