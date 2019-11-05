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

package com.prapp.ui.activity.main.fragment.utente;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.prapp.R;
import com.prapp.model.MyContext;
import com.prapp.model.net.manager.ManagerUtente;
import com.prapp.model.preferences.ApplicationPreferences;
import com.prapp.ui.AbstractViewModel;
import com.prapp.ui.Result;

public class UtenteViewModel extends AbstractViewModel {

    public static final String TAG = UtenteViewModel.class.getSimpleName();

    public UtenteViewModel() {
        super();
    }

    private MutableLiveData<Result<Void, Void>> logoutResult = new MutableLiveData<>();

    public LiveData<Result<Void, Void>> getLogoutResult() {
        return logoutResult;
    }

    public void logout() {
        MyContext myContext = getMyContext();
        ApplicationPreferences preferences = getPreferences();

        if (myContext.isLoggato())  
        {
            ManagerUtente managerUtente = getManagerUtente();

            managerUtente.logout(response -> {
                //Pulisco il contesto e le preferenze.
                myContext.logout();
                preferences.logout();
                logoutResult.setValue(new Result<>(null, null));
            }, new DefaultExceptionListener<>(logoutResult));
        } else {
            logoutResult.setValue(new Result<>(R.string.no_login));
        }
    }

}
