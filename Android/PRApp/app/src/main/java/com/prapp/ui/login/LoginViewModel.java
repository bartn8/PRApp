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

package com.prapp.ui.login;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Response;
import com.prapp.R;
import com.prapp.model.MyContext;
import com.prapp.model.db.wrapper.WUtente;
import com.prapp.model.net.Eccezione;
import com.prapp.ui.AbstractViewModel;
import com.prapp.ui.Result;

import org.jetbrains.annotations.Contract;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class LoginViewModel extends AbstractViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<Result<WUtente,Void>> loginResult = new MutableLiveData<>();


    //Costruttore
    public LoginViewModel(Context context) {
        super(context);
    }

    //Getters

    public LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    public LiveData<Result<WUtente,Void>> getLoginResult() {
        return loginResult;
    }


    public void login(String username, String password) {
        MyContext myContext = getMyContext();

        try {
            getManagerUtente().login(username, password, new Response.Listener<WUtente>() {

                /**
                 * Passo i dati dell'utente e aggiorno il contesto server.
                 *
                 * @param response
                 */
                @Override
                public void onResponse(WUtente response) {
                    myContext.login(response);
                    loginResult.setValue(new Result<>(response, null));
                }
            }, new Response.Listener<List<Eccezione>>() {

                /**
                 * Converto la lista in eccezioni vere e la passo fuori come risultato.
                 * @param response
                 */
                @Override
                public void onResponse(List<Eccezione> response) {
                    loginResult.setValue(new Result<>(null, Eccezione.convertiInExceptions(response)));
                }
            });
        } catch (UnsupportedEncodingException e) {
            loginResult.setValue(new Result<>(e));
        }
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    @Contract("null -> false")
    private boolean isUserNameValid(String username) {
        return username != null /*&& !username.trim().isEmpty()*/;
    }

    // A placeholder password validation check
    @Contract("null -> false")
    private boolean isPasswordValid(String password) {
        return password != null /*&& !password.trim().isEmpty()*/;
    }
}
