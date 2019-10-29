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

package com.prapp.ui.main.fragment.membro;


import android.content.Context;
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
import com.prapp.model.db.wrapper.WUtente;
import com.prapp.ui.Result;
import com.prapp.ui.utils.UiUtils;
import com.prapp.ui.utils.InterfaceHolder;
import com.prapp.ui.main.MainActivityInterface;
import com.prapp.ui.main.MainViewModel;
import com.prapp.ui.main.adapter.WUtenteAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class MembroFragment extends Fragment implements InterfaceHolder<MainActivityInterface> {

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MembroFragment.
     */
    public static MembroFragment newInstance() {
        MembroFragment fragment = new MembroFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private MainViewModel mainViewModel;
    private UiUtils uiUtils;
    private Unbinder unbinder;

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

    @BindView(R.id.membriRecyclerView)
    RecyclerView membriRecyclerView;

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

            else if (success != null) {
                WUtenteAdapter myAdapter = new WUtenteAdapter(success);
                membriRecyclerView.setAdapter(myAdapter);
            }
        }
    };


    public MembroFragment() {
        // Required empty public constructor
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
        inflater.inflate(R.menu.membro_menu, menu);
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
    public void onAttach(Context context) {
        super.onAttach(context);
        uiUtils = UiUtils.getInstance(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_membro, container, false);

        unbinder = ButterKnife.bind(this, view);

        membriRecyclerView.setHasFixedSize(true);
        membriRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        mainViewModel.getMembriStaffResult().observe(this, membriStaffResultObserver);
        mainViewModel.getMembriStaff();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
