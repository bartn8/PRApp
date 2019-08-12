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

package com.prapp.model.net.wrapper;

import com.google.gson.annotations.SerializedName;
import com.prapp.model.net.Twinned;
import com.prapp.model.net.serialize.JSONSerializable;

public class NetWEntrata implements Twinned, NetWrapper, JSONSerializable {

    public static final String CLASS_PATH = "com\\model\\net\\wrapper\\NetWEntrata";

    public static NetWEntrata getEmpty()
    {
        return new NetWEntrata(0,0, "");
    }

    @SerializedName("idPrevendita")
    private Integer idPrevendita;
    @SerializedName("idEvento")
    private Integer idEvento;
    @SerializedName("codiceAccesso")
    private String codice;

    public NetWEntrata(Integer idPrevendita, Integer idEvento, String codice) {
        this.idPrevendita = idPrevendita;
        this.idEvento = idEvento;
        this.codice = codice;
    }

    public Integer getIdPrevendita() {
        return idPrevendita;
    }

    public Integer getIdEvento() {
        return idEvento;
    }

    public String getCodice() {
        return codice;
    }

    @Override
    public String getRemoteClassPath() {
        return CLASS_PATH;
    }

    public void clear()
    {
        this.codice = "";
    }

}
