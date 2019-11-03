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

public class WStatisticheEvento implements  DatabaseWrapper, Twinned, JSONSerializable, JSONDeserializable {

    public static final String CLASS_PATH = "com\\model\\db\\wrapper\\WStatisticheEvento";

    public WStatisticheEvento getEmpty()
    {
        return new WStatisticheEvento(0,0, "", 0,0.00f,0,0);
    }

    @SerializedName("idEvento")
    private Integer idEvento;

    @SerializedName("idTipoPrevendita")
    private Integer idTipoPrevendita;

    @SerializedName("nomeTipoPrevendita")
    private String nomeTipoPrevendita;

    @SerializedName("prevenditeVendute")
    private Integer prevenditeVendute;

    @SerializedName("ricavo")
    private float ricavo;

    @SerializedName("prevenditeEntrate")
    private Integer prevenditeEntrate;

    @SerializedName("prevenditeNonEntrate")
    private Integer prevenditeNonEntrate;

    public WStatisticheEvento(Integer idEvento, Integer idTipoPrevendita, String nomeTipoPrevendita, Integer prevenditeVendute, float ricavo, Integer prevenditeEntrate, Integer prevenditeNonEntrate) {
        this.idEvento = idEvento;
        this.idTipoPrevendita = idTipoPrevendita;
        this.nomeTipoPrevendita = nomeTipoPrevendita;
        this.prevenditeVendute = prevenditeVendute;
        this.ricavo = ricavo;
        this.prevenditeEntrate = prevenditeEntrate;
        this.prevenditeNonEntrate = prevenditeNonEntrate;
    }

    public Integer getIdTipoPrevendita() {
        return idTipoPrevendita;
    }

    public String getNomeTipoPrevendita() {
        return nomeTipoPrevendita;
    }

    public Integer getPrevenditeVendute() {
        return prevenditeVendute;
    }

    public float getRicavo() {
        return ricavo;
    }

    public Integer getIdEvento() {
        return idEvento;
    }

    public Integer getPrevenditeEntrate() {
        return prevenditeEntrate;
    }

    public Integer getPrevenditeNonEntrate() {
        return prevenditeNonEntrate;
    }

    @Override
    public String getRemoteClassPath() {
        return CLASS_PATH;
    }
}
