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

package com.prapp.ui.activity.main.fragment.amministratore;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prapp.R;
import com.prapp.model.db.wrapper.WStatisticheCassiereEvento;
import com.prapp.model.db.wrapper.WStatisticheEvento;
import com.prapp.model.db.wrapper.WStatistichePREvento;
import com.prapp.model.db.wrapper.WUtente;
import com.prapp.ui.Result;
import com.prapp.ui.activity.main.MainActivityInterface;
import com.prapp.ui.adapter.StatisticheMembroAdapter;
import com.prapp.ui.adapter.WStatisticheEventoAdapter;
import com.prapp.ui.utils.InterfaceHolder;
import com.prapp.ui.utils.PopupUtil;
import com.prapp.ui.utils.UiUtil;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AmministratoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AmministratoreFragment extends Fragment implements InterfaceHolder<MainActivityInterface> {

    private static final String TAG = AmministratoreFragment.class.getSimpleName();
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormat.shortTime();
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormat.shortDateTime();

    private AmministratoreViewModel viewModel;
    private UiUtil uiUtil;
    private PopupUtil popupUtil;
    private Unbinder unbinder;

    /**
     * Interfaccia usata per comunicare con l'activity madre.
     */
    private MainActivityInterface mainActivityInterface;

    @Override
    public void holdInterface(MainActivityInterface mainActivityInterface){
        this.mainActivityInterface = mainActivityInterface;
    }

    @Override
    public boolean isInterfaceSet(){
        return this.mainActivityInterface != null;
    }

    /**
     * Adattatore per dare impasto le statistiche al recycler view.
     */
    private StatisticheMembroAdapter adapter;

    @BindView(R.id.statisticheEventoRecyclerView)
    public RecyclerView statisticheEventoRecyclerView;

    @BindView(R.id.statisticheMembriRecyclerView)
    public RecyclerView statisticheMembriRecyclerView;

    private Observer<Result<List<WStatisticheEvento>, Void>> statisticheEventoResultObserver = new Observer<Result<List<WStatisticheEvento>, Void>>() {
        @Override
        public void onChanged(Result<List<WStatisticheEvento>, Void> listResult) {
            if (listResult == null) {
                return;
            }

            Integer integerError = listResult.getIntegerError();
            List<Exception> error = listResult.getError();
            List<WStatisticheEvento> success = listResult.getSuccess();

            if (integerError != null)
                uiUtil.showError(integerError);

            else if (error != null)
                uiUtil.showError(error);

            else if(success != null)
                statisticheEventoRecyclerView.setAdapter(new WStatisticheEventoAdapter(success));
        }
    };

    private Observer<Result<List<WUtente>, Void>> membriStaffResultObserver = new Observer<Result<List<WUtente>, Void>>() {
        @Override
        public void onChanged(Result<List<WUtente>, Void> listResult) {
            if (listResult == null) {
                return;
            }

            Integer integerError = listResult.getIntegerError();
            List<Exception> error = listResult.getError();
            List<WUtente> success = listResult.getSuccess();

            if (integerError != null)
                uiUtil.showError(integerError);

            else if (error != null)
                uiUtil.showError(error);

            else if(success != null)
            {
                //Ora che ho la lista dei membri posso fare l'adapter.
                adapter = new StatisticheMembroAdapter();
                adapter.addMembri(success);
                statisticheMembriRecyclerView.setAdapter(adapter);

                //Faccio partire la ricerca delle statistiche.
                for(WUtente membro: success)
                {
                    //Log.d(TAG, "Utente: " + membro.getCognome() + " " + membro.getId());
                    viewModel.getStatisticheCassiereEvento(membro.getId());
                    viewModel.getStatistichePREvento(membro.getId());
                }
            }

            popupUtil.hideLoadingPopup();
        }
    };

    private Observer<Result<List<WStatistichePREvento>, Integer>> statistichePREventoResultObserver = new Observer<Result<List<WStatistichePREvento>, Integer>>() {
        @Override
        public void onChanged(Result<List<WStatistichePREvento>, Integer> listResult) {
            if (listResult == null) {
                return;
            }

            Integer integerError = listResult.getIntegerError();
            List<Exception> error = listResult.getError();
            List<WStatistichePREvento> success = listResult.getSuccess();

            if (integerError != null)
                uiUtil.showError(integerError);

            else if (error != null)
                uiUtil.showError(error);

            else if(success != null && listResult.isExtraPresent())
            {
                if(!success.isEmpty()){
                    adapter.addStatistichePR(listResult.getExtra(), success);
                }
            }
        }
    };

    private Observer<Result<WStatisticheCassiereEvento, Integer>> statisticheCassiereEventoResultObserver = new Observer<Result<WStatisticheCassiereEvento, Integer>>() {
        @Override
        public void onChanged(Result<WStatisticheCassiereEvento, Integer> listResult) {
            if (listResult == null) {
                return;
            }

            Integer integerError = listResult.getIntegerError();
            List<Exception> error = listResult.getError();
            WStatisticheCassiereEvento success = listResult.getSuccess();

            if (integerError != null)
                uiUtil.showError(integerError);

            else if (error != null)
                uiUtil.showError(error);

            else if(success != null && listResult.isExtraPresent())
            {
                //Log.d(TAG, "Statistiche Cassiere: " + listResult.getExtra());
                adapter.addStatisticheCassiere(listResult.getExtra(), success);
            }
        }
    };

    public AmministratoreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AmministratoreFragment.
     */
    public static AmministratoreFragment newInstance() {
        AmministratoreFragment fragment = new AmministratoreFragment();
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
        inflater.inflate(R.menu.amministratore_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            default:
                uiUtil.makeToast(R.string.not_implemented_yet);
                return super.onOptionsItemSelected(item);
        }
    }

    //--------------------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_amministratore, container, false);

        unbinder = ButterKnife.bind(this, view);

        uiUtil = new UiUtil(getActivity());
        popupUtil = new PopupUtil(getActivity());

        //Imposto il recyler view. Quello che fa vedere le entrate.
        statisticheMembriRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        statisticheMembriRecyclerView.setHasFixedSize(true);
        statisticheMembriRecyclerView.setNestedScrollingEnabled(false);

        statisticheEventoRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        statisticheEventoRecyclerView.setHasFixedSize(true);
        statisticheEventoRecyclerView.setNestedScrollingEnabled(false);

        //View model per richiamare il server.
        viewModel = ViewModelProviders.of(getActivity()).get(AmministratoreViewModel.class);
        viewModel.getStatisticheAmministratoreEventoResult().observe(this, statisticheEventoResultObserver);
        viewModel.getMembriStaffResult().observe(this, membriStaffResultObserver);
        viewModel.getStatistichePREventoResult().observe(this, statistichePREventoResultObserver);
        viewModel.getStatisticheCassiereEventoResult().observe(this, statisticheCassiereEventoResultObserver);

        viewModel.getStatisticheAmministratoreEvento();
        viewModel.getMembriStaff();

        popupUtil.showLoadingPopup();

        return view;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

}
