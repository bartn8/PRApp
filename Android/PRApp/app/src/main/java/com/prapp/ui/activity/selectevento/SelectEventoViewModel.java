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

package com.prapp.ui.activity.selectevento;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.prapp.R;
import com.prapp.model.MyContext;
import com.prapp.model.db.wrapper.WEvento;
import com.prapp.model.net.manager.ManagerMembro;
import com.prapp.model.preferences.ApplicationPreferences;
import com.prapp.ui.AbstractViewModel;
import com.prapp.ui.Result;

import java.util.List;

public class SelectEventoViewModel extends AbstractViewModel {

    private static final String TAG = SelectEventoViewModel.class.getSimpleName();

    private MutableLiveData<Result<List<WEvento>, Void>> listEventiResult = new MutableLiveData<>();
    private MutableLiveData<Result<WEvento, Void>> scegliEventoResult = new MutableLiveData<>();

    SelectEventoViewModel() {
        super();
    }

    public LiveData<Result<List<WEvento>, Void>> getListEventiResult() {
        return listEventiResult;
    }
    public LiveData<Result<WEvento, Void>> getScegliEventoResult() {
        return scegliEventoResult;
    }

    public void selectEvento(WEvento evento) {
        MyContext myContext = getMyContext();

        if (myContext.isLoggato() && myContext.isStaffScelto()) {
            ManagerMembro managerMembro = getManagerMembro();

            managerMembro.scegliEvento(evento, response -> {
                myContext.setEvento(evento);

                scegliEventoResult.setValue(new Result<>(evento, null));
            }, new DefaultExceptionListener<>(scegliEventoResult));
        } else {
            scegliEventoResult.setValue(new Result<>(R.string.no_staff));
        }

    }

    public void getEventiStaff() {
        MyContext myContext = getMyContext();
        ApplicationPreferences preferences = getPreferences();

        if (myContext.isLoggato() && myContext.isStaffScelto()) {
            ManagerMembro managerMembro = getManagerMembro();

            managerMembro.restituisciListaEventiStaff(response -> {
                listEventiResult.setValue(new Result<>(response, null));
            }, new DefaultExceptionListener<>(listEventiResult));
        } else {
            listEventiResult.setValue(new Result<>(R.string.no_staff));
        }
    }


}