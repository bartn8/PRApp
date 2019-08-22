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

package com.prapp.ui.main.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.prapp.ui.UiUtils;
import com.prapp.ui.main.MainViewModel;
import com.prapp.ui.main.adapter.StatisticheMembroAdapter;
import com.prapp.ui.main.adapter.WStatisticheEventoAdapter;

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
public class AmministratoreFragment extends Fragment {

    private static final String TAG = AmministratoreFragment.class.getSimpleName();
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormat.shortTime();
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormat.shortDateTime();

    private MainViewModel mainViewModel;
    private UiUtils uiUtils;
    private Unbinder unbinder;

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
                uiUtils.showError(integerError);

            else if (error != null)
                uiUtils.showError(error);

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
                uiUtils.showError(integerError);

            else if (error != null)
                uiUtils.showError(error);

            else if(success != null)
            {
                //Ora che ho la lista dei membri posso fare l'adapter.
                adapter = new StatisticheMembroAdapter(success);
                statisticheMembriRecyclerView.setAdapter(adapter);

                //Faccio partire la ricerca delle statistiche.
                for(WUtente membro: success)
                {
                    //Log.d(TAG, "Utente: " + membro.getCognome() + " " + membro.getId());
                    mainViewModel.getStatisticheCassiereEvento(membro.getId());
                    mainViewModel.getStatistichePREvento(membro.getId());
                }
            }
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
                uiUtils.showError(integerError);

            else if (error != null)
                uiUtils.showError(error);

            else if(success != null && listResult.isExtraPresent())
            {
                if(!success.isEmpty()){
//                    Log.d(TAG, "Statistiche PR: " + " " + success.size() + " " + listResult.getExtra());
//
//                    for(WStatistichePREvento statistica : success)
//                        Log.d(TAG, statistica.getNomeTipoPrevendita());

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
                uiUtils.showError(integerError);

            else if (error != null)
                uiUtils.showError(error);

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
    public void onAttach(Context context) {
        super.onAttach(context);
        uiUtils = UiUtils.getInstance(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_amministratore, container, false);

        unbinder = ButterKnife.bind(this, view);

        //Imposto il recyler view. Quello che fa vedere le entrate.
        statisticheMembriRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        statisticheMembriRecyclerView.setHasFixedSize(true);
        statisticheMembriRecyclerView.setNestedScrollingEnabled(false);

        statisticheEventoRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        statisticheEventoRecyclerView.setHasFixedSize(true);
        statisticheEventoRecyclerView.setNestedScrollingEnabled(false);

        //View model per richiamare il server.
        mainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        mainViewModel.getStatisticheEventoResult().observe(this, statisticheEventoResultObserver);
        mainViewModel.getMembriStaffResult().observe(this, membriStaffResultObserver);
        mainViewModel.getStatistichePREventoResult().observe(this, statistichePREventoResultObserver);
        mainViewModel.getStatisticheCassiereEventoResult().observe(this, statisticheCassiereEventoResultObserver);

        mainViewModel.getStatisticheEvento();
        mainViewModel.getMembriStaff();

        return view;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

}
