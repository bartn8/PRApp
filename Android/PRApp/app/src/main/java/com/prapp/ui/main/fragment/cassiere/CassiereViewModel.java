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

package com.prapp.ui.main.fragment.cassiere;

import android.util.SparseArray;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.prapp.R;
import com.prapp.model.MyContext;
import com.prapp.model.db.wrapper.WEntrata;
import com.prapp.model.db.wrapper.WPrevenditaPlus;
import com.prapp.model.net.manager.ManagerCassiere;
import com.prapp.model.net.wrapper.NetWEntrata;
import com.prapp.ui.AbstractViewModel;
import com.prapp.ui.Result;

import java.util.List;

public class CassiereViewModel extends AbstractViewModel {

    public static final String TAG = CassiereViewModel.class.getSimpleName();

    private MutableLiveData<Result<WPrevenditaPlus, Void>> infoPrevenditaResult = new MutableLiveData<>();
    private MutableLiveData<Result<WEntrata, WPrevenditaPlus>> entrataResult = new MutableLiveData<>();
    private MutableLiveData<Result<List<WPrevenditaPlus>, Void>> prevenditeResult = new MutableLiveData<>();

    public LiveData<Result<WEntrata, WPrevenditaPlus>> getEntrataResult() {
        return entrataResult;
    }

    public LiveData<Result<WPrevenditaPlus, Void>> getInfoPrevenditaResult() {
        return infoPrevenditaResult;
    }

    public LiveData<Result<List<WPrevenditaPlus>, Void>> getPrevenditeResult() {
        return prevenditeResult;
    }


    private SparseArray<NetWEntrata> mapEntrata = new SparseArray<NetWEntrata>();

    public NetWEntrata get(Integer idPrevendita) {
        return mapEntrata.get(idPrevendita);
    }

    public void remove(WPrevenditaPlus prevendita) {
        mapEntrata.remove(prevendita.getId());
    }


    public CassiereViewModel() {
        super();
    }

    public void getInformazioniPrevendita(NetWEntrata entrata) {
        MyContext myContext = getMyContext();
        Integer idPrevendita = entrata.getIdPrevendita();

        //La prevendita non deve essere già stata controllata.
        if (mapEntrata.indexOfKey(idPrevendita) < 0) {
            mapEntrata.put(idPrevendita, entrata);

            if (myContext.isLoggato()) {
                ManagerCassiere managerCassiere = getManagerCassiere();

                managerCassiere.restituisciInformazioniPrevendita(idPrevendita, new DefaultSuccessListener<>(infoPrevenditaResult), new DefaultExceptionListener<>(infoPrevenditaResult));
            } else {
                infoPrevenditaResult.setValue(new Result<>(R.string.no_login));
            }
        }
    }

    public void timbraEntrata(WPrevenditaPlus prevendita) {
        MyContext myContext = getMyContext();
        Integer idPrevendita = prevendita.getId();

        //Deve essere presente il codice originale per la verifica.
        if (mapEntrata.indexOfKey(idPrevendita) >= 0) {
            if (myContext.isLoggato()) {
                ManagerCassiere managerCassiere = getManagerCassiere();

                managerCassiere.timbraEntrata(mapEntrata.get(idPrevendita), new DefaultSuccessListener<>(entrataResult, prevendita), new DefaultExceptionListener<>(entrataResult, prevendita));
            } else {
                entrataResult.setValue(new Result<>(R.string.no_login));
            }
        }
    }

    public void getListaPrevenditeTimbrateEvento() {
        MyContext myContext = getMyContext();

        if (myContext.isLoggato() && myContext.isStaffScelto() && myContext.isEventoScelto()) {
            ManagerCassiere managerCassiere = getManagerCassiere();
            Integer idEvento = myContext.getEvento().getId();

            managerCassiere.restituisciListaPrevenditeTimbrate(idEvento, new DefaultSuccessListener<>(prevenditeResult), new DefaultExceptionListener<>(prevenditeResult));
        } else {
            prevenditeResult.setValue(new Result<>(R.string.no_login));
        }
    }

    public void getListaPrevenditeNonTimbrateEvento() {
        MyContext myContext = getMyContext();

        if (myContext.isLoggato() && myContext.isStaffScelto() && myContext.isEventoScelto()) {
            ManagerCassiere managerCassiere = getManagerCassiere();
            Integer idEvento = myContext.getEvento().getId();

            managerCassiere.restituisciListaPrevenditeNonTimbrate(idEvento, new DefaultSuccessListener<>(prevenditeResult), new DefaultExceptionListener<>(prevenditeResult));
        } else {
            prevenditeResult.setValue(new Result<>(R.string.no_login));
        }
    }


}
