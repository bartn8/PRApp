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

package com.prapp.ui.start;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Response;
import com.prapp.R;
import com.prapp.model.MyContext;
import com.prapp.model.db.wrapper.WDirittiUtente;
import com.prapp.model.db.wrapper.WStaff;
import com.prapp.model.db.wrapper.WToken;
import com.prapp.model.db.wrapper.WUtente;
import com.prapp.model.net.manager.ManagerMembro;
import com.prapp.model.net.manager.ManagerUtente;
import com.prapp.model.preferences.ApplicationPreferences;
import com.prapp.ui.AbstractViewModel;
import com.prapp.ui.Result;

public class SplashViewModel extends AbstractViewModel {

    private MutableLiveData<Result<WUtente, Void>> loginResult = new MutableLiveData<>();
    private MutableLiveData<Result<WDirittiUtente, Void>> getInfoUtenteResult = new MutableLiveData<>();
    private MutableLiveData<Result<WToken, Void>> renewTokenResult = new MutableLiveData<>();

    public SplashViewModel() {
        super();
    }

    public LiveData<Result<WUtente, Void>> getLoginResult() {
        return loginResult;
    }

    public LiveData<Result<WDirittiUtente, Void>> getGetInfoUtenteResult() {
        return getInfoUtenteResult;
    }

    public LiveData<Result<WToken, Void>> getRenewTokenResult() {
        return renewTokenResult;
    }

    public void clearSelected() {
        //Pulizia di staff e evento scelto.
        MyContext myContext = getMyContext();
        ApplicationPreferences preferences = getPreferences();

        myContext.clearSelected();
        preferences.clearSelected();
    }

    public void loginToken() {
        MyContext myContext = getMyContext();
        ApplicationPreferences preferences = getPreferences();

        if (preferences.isTokenSaved()) {
            WToken token = preferences.getLastStoredToken();

            //Prima di procedere verifico che sia ancora valido.
            if (token.isTokenValid()) {
                ManagerUtente managerUtente = getManagerUtente();
                managerUtente.loginWithToken(token.getToken(), new Response.Listener<WUtente>() {
                    @Override
                    public void onResponse(WUtente response) {
                        myContext.login(response);
                        loginResult.setValue(new Result<>(response, null));
                    }
                }, new DefaultExceptionListener<>(loginResult));
            } else {
                //Token non valido lo elimino.
                preferences.clearToken();

                //Imposto un valore di errore
                loginResult.setValue(new Result<>(R.string.invalid_token));
            }
        } else {
            loginResult.setValue(new Result<>(R.string.no_token));
        }
    }

    public void getInfoUtente() {
        MyContext myContext = getMyContext();

        if (myContext.isLoggato() && myContext.isStaffScelto()) {
            WStaff staff = myContext.getStaff();

            ManagerMembro managerMembro = getManagerMembro();

            managerMembro.restituisciDirittiPersonaliStaff(staff.getId(), response -> {
                myContext.setDirittiUtente(response);
                getInfoUtenteResult.setValue(new Result<>(response, null));
            }, new DefaultExceptionListener<>(getInfoUtenteResult));

        } else {
            getInfoUtenteResult.setValue(new Result<>(R.string.no_staff));
        }
    }

    public void renewToken() {
        MyContext myContext = getMyContext();
        ApplicationPreferences preferences = getPreferences();

        if (myContext.isLoggato()) {
            ManagerUtente managerUtente = getManagerUtente();

            managerUtente.renewToken(response -> {
                preferences.saveToken(response);
                renewTokenResult.setValue(new Result<>(response, null));
            }, new DefaultExceptionListener<>(renewTokenResult));
        } else {
            renewTokenResult.setValue(new Result<>(R.string.no_login));
        }
    }

}
