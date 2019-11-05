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

package com.prapp.ui.activity.login;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.prapp.R;
import com.prapp.model.db.wrapper.WUtente;
import com.prapp.ui.Result;
import com.prapp.ui.utils.PopupUtil;
import com.prapp.ui.utils.UiUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnTextChanged;

public class LoginActivity extends AppCompatActivity {

    private PopupUtil popupUtil;
    private UiUtil uiUtil;

    private LoginViewModel loginViewModel;

    @BindView(R.id.username)
    public EditText usernameEditText;

    @BindView(R.id.password)
    public EditText passwordEditText;

    @BindView(R.id.login)
    public Button loginButton;

    private Observer<LoginFormState> loginFormStateObserver = new Observer<LoginFormState>() {
        @Override
        public void onChanged(@Nullable LoginFormState loginFormState) {
            if (loginFormState == null) {
                return;
            }

            loginButton.setEnabled(loginFormState.isDataValid());

            if (loginFormState.getUsernameError() != null) {
                usernameEditText.setError(getString(loginFormState.getUsernameError()));
            }

            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
        }
    };

    private Observer<Result<WUtente, Void>> loginResultObserver = loginResult -> {
        if (loginResult == null) {
            return;
        }

        if (loginResult.getError() != null) {
            showLoginFailed(loginResult.getError());
        }

        if (loginResult.getSuccess() != null) {
            showLoginSuccess(loginResult.getSuccess());
        }

        popupUtil.hideLoadingPopup();

        //Ritorna allo splash
        setResult(Activity.RESULT_OK);
        finish();
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        popupUtil = new PopupUtil(this);
        uiUtil = new UiUtil(this);

        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory()).get(LoginViewModel.class);

        //Quando si aggiorna lo stato del form si esegue sta roba
        loginViewModel.getLoginFormState().observe(this, loginFormStateObserver);

        //Quando viene aggiornato il risultato del login si esegue sta roba che fa vedere delle cose.
        loginViewModel.getLoginResult().observe(this, loginResultObserver);

    }

    private void showLoginSuccess(@NotNull WUtente model) {
        String welcome = getString(R.string.welcome, model.getNome());
        uiUtil.makeToast(welcome);
    }

    private void showLoginFailed(@NotNull List<Exception> exceptionList) {
        uiUtil.showError(exceptionList);
    }

    @OnTextChanged(value = {R.id.username, R.id.password}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onTextChanged(Editable s) {
        loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                passwordEditText.getText().toString());
    }

    @OnClick(R.id.login)
    public void buttonClick(Button view) {
        popupUtil.showLoadingPopup();

        loginViewModel.login(usernameEditText.getText().toString(),
                passwordEditText.getText().toString());
    }

    @OnEditorAction(R.id.password)
    public boolean onEditorActionPassword(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            popupUtil.showLoadingPopup();

            loginViewModel.login(usernameEditText.getText().toString(),
                    passwordEditText.getText().toString());
        }
        return true;
    }
}
