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

public class NetWLogin implements NetWrapper, Twinned, JSONSerializable {

    public static final String CLASS_PATH = "com\\model\\net\\wrapper\\NetWLogin";

    public static NetWLogin getEmpty()
    {
        return new NetWLogin("", "");
    }

    @SerializedName("username")
    private String username;
    @SerializedName("password")
    private String password;

    public NetWLogin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void clear()
    {
        this.username = "";
        this.password = "";
    }

    @Override
    public String getRemoteClassPath() {
        return CLASS_PATH;
    }

/*    @Override
    public JSONObject jsonSerialize() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("username", getUsername());
        obj.put("password", getPassword());
        return obj;
    }*/



}
