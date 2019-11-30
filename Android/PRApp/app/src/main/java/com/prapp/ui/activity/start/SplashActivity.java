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

package com.prapp.ui.activity.start;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.prapp.R;
import com.prapp.model.db.wrapper.WDirittiUtente;
import com.prapp.model.db.wrapper.WToken;
import com.prapp.model.db.wrapper.WUtente;
import com.prapp.ui.Result;
import com.prapp.ui.activity.login.LoginActivity;
import com.prapp.ui.activity.main.MainActivity;
import com.prapp.ui.activity.selectevento.SelectEventoActivity;
import com.prapp.ui.activity.selectstaff.SelectStaffActivity;
import com.prapp.ui.utils.UiUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SplashActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_LOGIN = 1;
    public static final int REQUEST_CODE_SELECT_STAFF = 2;
    public static final int REQUEST_CODE_SELECT_EVENTO = 3;

    private SplashViewModel splashViewModel;
    private UiUtil uiUtil;

    private Observer<Result<WUtente, Void>> loginTokenResultObserver = new Observer<Result<WUtente, Void>>() {

        @Override
        public void onChanged(Result<WUtente, Void> loginTokenResult) {
            if (loginTokenResult == null) {
                return;
            }

            Integer integerError = loginTokenResult.getIntegerError();
            List<Exception> error = loginTokenResult.getError();
            WUtente success = loginTokenResult.getSuccess();

            if (integerError != null) {
                uiUtil.showError(integerError);
                passaggioAllaPaginaDiLogin();
            }

            if (error != null) {
                uiUtil.showError(error);
                passaggioAllaPaginaDiLogin();
            }

            if (success != null) {
                showLoginSuccess(success);

                if (!splashViewModel.isStaffScelto())
                    passaggioAllaPaginaDiSceltaStaff();
                else if (!splashViewModel.isEventoScelto())
                    passaggioAllaPaginaDiSceltaEvento();
                else
                    recuperoInformazioniUtente();
            }
        }
    };

    private Observer<Result<WDirittiUtente, Void>> getInfoUtenteResultObserver = new Observer<Result<WDirittiUtente, Void>>() {
        @Override
        public void onChanged(Result<WDirittiUtente, Void> getInfoUtenteResult) {
            if (getInfoUtenteResult == null) {
                return;
            }

            Integer integerError = getInfoUtenteResult.getIntegerError();
            List<Exception> error = getInfoUtenteResult.getError();
            WDirittiUtente success = getInfoUtenteResult.getSuccess();

            if (integerError != null)
                uiUtil.showError(integerError);

            if (error != null)
                uiUtil.showError(error);

            if (success != null) {
                //Do i diritti alla pagina successiva.
                passaggioAllaPaginaPrincipale();
            }
        }
    };

    private Observer<Result<WToken, Void>> renewTokenResultObserver = new Observer<Result<WToken, Void>>() {
        @Override
        public void onChanged(Result<WToken, Void> renewTokenResult) {
            if (renewTokenResult == null) {
                return;
            }

            Integer integerError = renewTokenResult.getIntegerError();
            List<Exception> error = renewTokenResult.getError();
            WToken success = renewTokenResult.getSuccess();

            if (integerError != null)
                uiUtil.showError(integerError);

            if (error != null)
                uiUtil.showError(error);

            if (success != null) {
                //non fare nulla.
            }
        }
    };

    private void showLoginSuccess(@NotNull WUtente model) {
        String welcome = getString(R.string.welcome, model.getNome());
        uiUtil.makeToast(welcome);
    }

    private void creaToken() {
        splashViewModel.renewToken();
    }

    private void verificaToken() {
        splashViewModel.loginToken();
    }

    private void recuperoInformazioniUtente() {
        splashViewModel.getInfoUtente();
    }

    private void passaggioAllaPaginaDiLogin() {
        startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_CODE_LOGIN);
    }

    private void passaggioAllaPaginaDiSceltaStaff() {
        Intent intent = new Intent(this, SelectStaffActivity.class);
        intent.putExtra(SelectStaffActivity.SEARCH_PREFERENCES_MESSAGE, true);
        startActivityForResult(intent, REQUEST_CODE_SELECT_STAFF);
    }

    private void passaggioAllaPaginaDiSceltaEvento() {
        Intent intent = new Intent(this, SelectEventoActivity.class);
        intent.putExtra(SelectStaffActivity.SEARCH_PREFERENCES_MESSAGE, true);
        startActivityForResult(intent, REQUEST_CODE_SELECT_EVENTO);
    }

    private void passaggioAllaPaginaPrincipale() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        //Fine splash.
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiUtil = new UiUtil(getApplicationContext());

        //Inizializzo il view model e applico gli observer.
        splashViewModel = ViewModelProviders.of(this, new SplashViewModelFactory(getApplicationContext())).get(SplashViewModel.class);
        splashViewModel.getLoginResult().observe(this, loginTokenResultObserver);
        splashViewModel.getGetInfoUtenteResult().observe(this, getInfoUtenteResultObserver);
        splashViewModel.getRenewTokenResult().observe(this, renewTokenResultObserver);

        //Non serve setContentLayout: c'è il tema.
        verificaToken();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case REQUEST_CODE_LOGIN: {
                if (splashViewModel.isLoggato()) {
                    //Login effettuato.

                    //Creo il token.
                    creaToken();

                    //Devo riselezionare staff ed evento: Se ho cambiato username?
                    splashViewModel.clearSelected();

                    if (!splashViewModel.isStaffScelto()) {
                        passaggioAllaPaginaDiSceltaStaff();
                        break;
                    }

                    recuperoInformazioniUtente();
                } else {
                    //Login fallito ritorno al login.
                    passaggioAllaPaginaDiLogin();
                }
                break;
            }

            case REQUEST_CODE_SELECT_STAFF: {
                //Ocio: devo rifare la sccelta dell'evento: se staff diverso?
                passaggioAllaPaginaDiSceltaEvento();
                break;
            }

            case REQUEST_CODE_SELECT_EVENTO: {
                recuperoInformazioniUtente();
                break;
            }

            default:
                break;
        }

    }
}
