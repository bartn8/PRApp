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

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.prapp.model.db.enums.StatoPrevendita;
import com.prapp.model.net.Twinned;
import com.prapp.model.net.serialize.JSONSerializable;
import com.prapp.model.net.serialize.adapter.StatoPrevenditaAdapter;
import com.prapp.model.net.wrapper.NetWrapper;

public class UpdateNetWPrevendita implements Twinned, NetWrapper, JSONSerializable {

    public static final String CLASS_PATH = "com\\model\\net\\wrapper\\update\\UpdateNetWPrevendita";

    public static UpdateNetWPrevendita getEmpty()
    {
        return new UpdateNetWPrevendita(0,StatoPrevendita.VALIDA);
    }

    @SerializedName("id")
    private Integer id;

    @SerializedName("stato")
    @JsonAdapter(StatoPrevenditaAdapter.class)
    private StatoPrevendita statoPrevendita;

    public UpdateNetWPrevendita(Integer id, StatoPrevendita statoPrevendita) {
        this.id = id;
        this.statoPrevendita = statoPrevendita;
    }

    public Integer getId() {
        return id;
    }

    public StatoPrevendita getStatoPrevendita() {
        return statoPrevendita;
    }

    @Override
    public String getRemoteClassPath() {
        return CLASS_PATH;
    }
}
