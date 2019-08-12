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
import com.prapp.model.db.enums.Diritto;
import com.prapp.model.net.Twinned;
import com.prapp.model.net.serialize.JSONSerializable;
import com.prapp.model.net.serialize.adapter.DirittoSetAdapter;
import com.prapp.model.net.wrapper.NetWrapper;

import java.util.Set;
import java.util.TreeSet;

public class UpdateNetWDirittiUtente implements Twinned, NetWrapper, JSONSerializable {

    public static final String CLASS_PATH = "com\\model\\net\\wrapper\\update\\UpdateNetWDirittiUtente";

    public UpdateNetWDirittiUtente getEmpty()
    {
        return new UpdateNetWDirittiUtente(0,0, new TreeSet<Diritto>());
    }

    @SerializedName("idUtente")
    private Integer idUtente;

    @SerializedName("idStaff")
    private Integer idStaff;

    @SerializedName("diritti")
    @JsonAdapter(DirittoSetAdapter.class)
    private Set<Diritto> diritti;

    public UpdateNetWDirittiUtente(Integer idUtente, Integer idStaff, Set<Diritto> diritti) {
        this.idUtente = idUtente;
        this.idStaff = idStaff;
        this.diritti = diritti;
    }

    public Integer getIdUtente() {
        return idUtente;
    }

    public Integer getIdStaff() {
        return idStaff;
    }

    public Set<Diritto> getDiritti() {
        return diritti;
    }

    @Override
    public String getRemoteClassPath() {
        return CLASS_PATH;
    }

}
