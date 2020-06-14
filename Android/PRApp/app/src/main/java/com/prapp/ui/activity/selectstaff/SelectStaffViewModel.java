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

import java.util.List;

public class SelectStaffViewModel extends AbstractViewModel {

    private MutableLiveData<Result<List<WStaff>, Void>> listStaffResult = new MutableLiveData<>();
    private MutableLiveData<Result<WStaff, Void>> scegliStaffResult = new MutableLiveData<>();

    public SelectStaffViewModel() {
        super();
    }

    public LiveData<Result<List<WStaff>, Void>> getListStaffResult() {
        return listStaffResult;
    }

    public LiveData<Result<WStaff, Void>> getScegliStaffResult() {
        return scegliStaffResult;
    }


    public void selectStaff(WStaff staff) {
        MyContext myContext = getMyContext();

        if (myContext.isLoggato()) {
            ManagerUtente managerUtente = getManagerUtente();

            managerUtente.scegliStaff(staff, (WStaff response) -> {
                myContext.setStaff(response);

                scegliStaffResult.setValue(new Result<>(response, null));
            }, new DefaultExceptionListener<>(scegliStaffResult));
        } else {
            scegliStaffResult.setValue(new Result<>(R.string.no_login));
        }

        //Devo resettare l'evento scelto
        myContext.clearEvento();
    }


    public void getStaffMembri() {
        MyContext myContext = getMyContext();
        ApplicationPreferences preferences = getPreferences();

        if (myContext.isLoggato()) {
            ManagerUtente managerUtente = getManagerUtente();

            managerUtente.restituisciListaStaffMembri(response -> {
                listStaffResult.setValue(new Result<>(response, null));
            }, new DefaultExceptionListener<>(listStaffResult));
        } else {
            listStaffResult.setValue(new Result<>(R.string.no_login));
        }
    }


}
