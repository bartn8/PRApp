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

public class WStatistichePREvento implements  DatabaseWrapper, Twinned, JSONSerializable, JSONDeserializable {

    public static final String CLASS_PATH = "com\\model\\db\\wrapper\\WStatistichePREvento";

    public WStatistichePREvento getEmpty()
    {
        return new WStatistichePREvento(0,0,0,0, "",0,0.00f);
    }

    public WStatistichePREvento getEmpty(int idUtente, int idStaff, int idEvento, int idTipoPrevendita)
    {
        return new WStatistichePREvento(idUtente, idStaff, idEvento, idTipoPrevendita, "", 0, 0.0f);
    }

    @SerializedName("idUtente")
    private Integer idUtente;

    @SerializedName("idStaff")
    private Integer idStaff;

    @SerializedName("idEvento")
    private Integer idEvento;

    @SerializedName("idTipoPrevendita")
    private Integer idTipoPrevendita;

    @SerializedName("nomeTipoPrevendita")
    private String nomeTipoPrevendita;

    @SerializedName("prevenditeVendute")
    private Integer prevenditeVendute;

    @SerializedName("ricavo")
    private Float ricavo;

    public WStatistichePREvento(Integer idUtente, Integer idStaff, Integer idEvento, Integer idTipoPrevendita, String nomeTipoPrevendita, Integer prevenditeVendute, Float ricavo) {
        this.idUtente = idUtente;
        this.idStaff = idStaff;
        this.idEvento = idEvento;
        this.idTipoPrevendita = idTipoPrevendita;
        this.nomeTipoPrevendita = nomeTipoPrevendita;
        this.prevenditeVendute = prevenditeVendute;
        this.ricavo = ricavo;
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

    public Float getRicavo() {
        return ricavo;
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

    @Override
    public String getRemoteClassPath() {
        return CLASS_PATH;
    }
}
