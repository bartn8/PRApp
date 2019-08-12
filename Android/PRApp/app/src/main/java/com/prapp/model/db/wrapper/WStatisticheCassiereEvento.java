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

import com.google.gson.annotations.SerializedName;
import com.prapp.model.net.Twinned;
import com.prapp.model.net.serialize.JSONDeserializable;
import com.prapp.model.net.serialize.JSONSerializable;

public class WStatisticheCassiereEvento implements  DatabaseWrapper, Twinned, JSONSerializable, JSONDeserializable {

    public static final String CLASS_PATH = "com\\model\\db\\wrapper\\WStatisticheCassiereEvento";

    public WStatisticheCassiereEvento getEmpty()
    {
        return new WStatisticheCassiereEvento(0,0,0,0);
    }

    @SerializedName("idUtente")
    private Integer idUtente;

    @SerializedName("idStaff")
    private Integer idStaff;

    @SerializedName("idEvento")
    private Integer idEvento;

    @SerializedName("entrate")
    private Integer entrate;

    public WStatisticheCassiereEvento(Integer idUtente, Integer idStaff, Integer idEvento, Integer entrate) {
        this.idUtente = idUtente;
        this.idStaff = idStaff;
        this.idEvento = idEvento;
        this.entrate = entrate;
    }

    public Integer getIdUtente() {
        return idUtente;
    }

    public Integer getIdStaff() {
        return idStaff;
    }

    public Integer getIdEvento() {
        return idEvento;
    }

    public Integer getEntrate() {
        return entrate;
    }

    @Override
    public String getRemoteClassPath() {
        return CLASS_PATH;
    }
}
