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

public class WStatisticheCassiereTotali implements  DatabaseWrapper, Twinned, JSONSerializable, JSONDeserializable {

    public static final String CLASS_PATH = "com\\model\\db\\wrapper\\WStatisticheCassiereTotali";

    public WStatisticheCassiereTotali getEmpty()
    {
        return new WStatisticheCassiereTotali(0,0);
    }

    @SerializedName("idUtente")
    private Integer idUtente;

    @SerializedName("entrate")
    private Integer entrate;

    public WStatisticheCassiereTotali(Integer idUtente, Integer entrate) {
        this.idUtente = idUtente;
        this.entrate = entrate;
    }

    public Integer getIdUtente() {
        return idUtente;
    }

    public Integer getEntrate() {
        return entrate;
    }

    @Override
    public String getRemoteClassPath() {
        return CLASS_PATH;
    }
}
