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

package com.prapp.ui.main.fragment.utente;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.prapp.R;
import com.prapp.model.db.wrapper.WDirittiUtente;
import com.prapp.model.db.wrapper.WEvento;
import com.prapp.model.db.wrapper.WStaff;
import com.prapp.model.db.wrapper.WUtente;
import com.prapp.ui.Result;
import com.prapp.ui.main.MainActivityInterface;
import com.prapp.ui.start.SplashActivity;
import com.prapp.ui.utils.InterfaceHolder;
import com.prapp.ui.utils.PopupUtil;
import com.prapp.ui.utils.UiUtil;

import org.jetbrains.annotations.NotNull;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UtenteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UtenteFragment extends Fragment implements InterfaceHolder<MainActivityInterface> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.shortDateTime();

    private UtenteViewModel viewModel;
    private Unbinder unbinder;

    private UiUtil uiUtil;
    private PopupUtil popupUtil;

    /**
     * Interfaccia usata per comunicare con l'activity madre.
     */
    private MainActivityInterface mainActivityInterface;

    @Override
    public void holdInterface(MainActivityInterface mainActivityInterface) {
        this.mainActivityInterface = mainActivityInterface;
    }

    @Override
    public boolean isInterfaceSet() {
        return this.mainActivityInterface != null;
    }

    @BindView(R.id.fragment_utente_nome)
    TextView textViewNome;

    @BindView(R.id.fragment_utente_cognome)
    TextView textViewCognome;

    @BindView(R.id.fragment_utente_telefono)
    TextView textViewTelefono;

    @BindView(R.id.fragment_utente_nome_staff)
    TextView textViewNomeStaff;

    @BindView(R.id.fragment_utente_diritti)
    TextView textViewDiritti;

    @BindView(R.id.fragment_utente_nomeEvento)
    TextView textViewNomeEvento;

    @BindView(R.id.fragment_utente_descrizioneEvento)
    TextView textViewDescrizioneEvento;

    @BindView(R.id.fragment_utente_periodoEvento)
    TextView textViewPeriodoEvento;

    @BindView(R.id.fragment_utente_logout)
    Button buttonLogout;

    private Observer<Result<Void, Void>> logoutResultObserver = logoutResult -> {
        if (logoutResult == null) {
            return;
        }

        Integer integerError = logoutResult.getIntegerError();
        List<Exception> error = logoutResult.getError();

        if (integerError != null)
            showError(integerError);

        else if (error != null)
            showError(error);
        else {
            //Logout effettuato: devo ritornare allo splash.
            startActivity(new Intent(getActivity(), SplashActivity.class));

            getActivity().setResult(RESULT_OK);
            getActivity().finish();
        }
    };

    private void showError(@NotNull List<Exception> exceptionList) {
        uiUtil.showError(exceptionList);
    }

    private void showError(@NotNull Integer integerError) {
        uiUtil.showError(integerError);
    }

    public UtenteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UtenteFragment.
     */
    public static UtenteFragment newInstance() {
        UtenteFragment fragment = new UtenteFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);    //Opzione menu
    }

    //ROBA MENU------------------------------------------------------------------------------

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.utente_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //--------------------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_utente, container, false);
        unbinder = ButterKnife.bind(this, view);

        uiUtil = new UiUtil(getActivity());
        popupUtil = new PopupUtil(getActivity());

        viewModel = ViewModelProviders.of(getActivity()).get(UtenteViewModel.class);
        viewModel.getLogoutResult().observe(this, logoutResultObserver);

        if (viewModel.isLoggato()) {
            WUtente utente = viewModel.getUtente();

            textViewNome.setText(utente.getNome());
            textViewCognome.setText(utente.getCognome());
            textViewTelefono.setText(utente.getTelefono());

            if (viewModel.isStaffScelto()) {
                WStaff staff = viewModel.getStaff();
                WDirittiUtente dirittiUtente = viewModel.getDirittiUtente();

                textViewNomeStaff.setText(staff.getNome());
                textViewDiritti.setText(dirittiUtente.getDiritti().toString());
            }

            if (viewModel.isEventoScelto()) {
                WEvento evento = viewModel.getEvento();
                textViewNomeEvento.setText(evento.getNome());
                textViewDescrizioneEvento.setText(evento.getDescrizione());
                textViewPeriodoEvento.setText(getString(R.string.fragment_utente_periodoEvento_placeholder, evento.getInizio().toString(DATE_FORMATTER), evento.getFine().toString(DATE_FORMATTER)));
            }
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.fragment_utente_logout)
    public void logout() {
        viewModel.logout();
    }

}
