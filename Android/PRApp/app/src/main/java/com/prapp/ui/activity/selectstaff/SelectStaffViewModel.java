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

package com.prapp.ui.activity.selectstaff;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.prapp.R;
import com.prapp.model.MyContext;
import com.prapp.model.db.wrapper.WStaff;
import com.prapp.model.net.manager.ManagerUtente;
import com.prapp.model.preferences.ApplicationPreferences;
import com.prapp.ui.AbstractViewModel;
import com.prapp.ui.Result;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SelectStaffViewModel extends AbstractViewModel {

    private MutableLiveData<Result<List<WStaff>, Void>> listStaffResult = new MutableLiveData<>();

    private List<WStaff> staffList = new ArrayList<>();

    public SelectStaffViewModel() {
        super();
    }

    public LiveData<Result<List<WStaff>, Void>> getListStaffResult() {
        return listStaffResult;
    }


    //Ricerca lo staff giusto le lo restituisce
    @Nullable
    private WStaff getStaff(Integer idStaff) {
        for (WStaff staff : staffList) {
            if (staff.getId().intValue() == idStaff.intValue()) {
                return staff;
            }
        }

        return null;
    }

    public void selectStaff(int idStaff) {
        MyContext myContext = getMyContext();
        ApplicationPreferences preferences = getPreferences();

        //Seleziono lo staff come default:
        //1) lo salvo nelle preferenze.
        //2) lo sparo nel contesto.

        //Staff fortunato.
        WStaff staff = getStaff(idStaff);
        //1)
        preferences.saveStaff(staff);
        //2)
        myContext.setStaff(staff);

        //Devo resettare l'evento scelto
        preferences.clearEvento();
        myContext.clearEvento();
    }

    public boolean caricaStaffSalvato() {
        MyContext myContext = getMyContext();
        ApplicationPreferences preferences = getPreferences();

        if (preferences.isStaffSaved()) {
            WStaff staff = preferences.getStaff();

            staffList = new ArrayList<>();
            staffList.add(staff);
            listStaffResult.setValue(new Result<>(staffList, null));

            myContext.setStaff(staff);

            return true;
        }

        return false;
    }


    public void getStaffMembri() {
        MyContext myContext = getMyContext();
        ApplicationPreferences preferences = getPreferences();

        if (myContext.isLoggato()) {
            ManagerUtente managerUtente = getManagerUtente();

            managerUtente.restituisciListaStaffMembri(response -> {
                staffList = response;
                listStaffResult.setValue(new Result<>(response, null));
            }, new DefaultExceptionListener<>(listStaffResult));
        } else {
            listStaffResult.setValue(new Result<>(R.string.no_login));
        }
    }


}
