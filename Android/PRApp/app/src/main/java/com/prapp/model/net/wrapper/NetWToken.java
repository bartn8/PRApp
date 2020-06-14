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

public class NetWToken implements Twinned, NetWrapper, JSONSerializable {


    public static final String CLASS_PATH = "com\\model\\net\\wrapper\\NetWToken";

    public static NetWToken getEmpty()
    {
        return new NetWToken(0, "");
    }

    @SerializedName("id")
    private Integer id;
    @SerializedName("token")
    private String token;

    public NetWToken(Integer id, String token) {
        this.id = id;
        this.token = token;
    }

    public Integer getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String getRemoteClassPath() {
        return CLASS_PATH;
    }

    public void clear()
    {
        this.token = "";
    }
    
}
