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

public class NetWStaffAccess implements Twinned, NetWrapper, JSONSerializable {

    public static final String CLASS_PATH = "com\\model\\net\\wrapper\\NetWStaffAcess";

    public static NetWStaffAccess getEmpty()
    {
        return new NetWStaffAccess(0, "");
    }

    @SerializedName("idStaff")
    private Integer idStaff;
    @SerializedName("codiceAccesso")
    private String codiceAccesso;

    public NetWStaffAccess(Integer idStaff, String codiceAccesso) {
        this.idStaff = idStaff;
        this.codiceAccesso = codiceAccesso;
    }

    public Integer getIdStaff() {
        return idStaff;
    }

    public String getCodiceAccesso() {
        return codiceAccesso;
    }

    @Override
    public String getRemoteClassPath() {
        return CLASS_PATH;
    }

    public void clear()
    {
        this.codiceAccesso = "";
    }

/*    @Override
    public JSONObject jsonSerialize() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("idStaff", getIdStaff());
        obj.put("codiceAccesso", getCodiceAccesso());
        return obj;
    }*/

}
