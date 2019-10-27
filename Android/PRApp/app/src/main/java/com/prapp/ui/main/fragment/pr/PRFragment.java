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

package com.prapp.ui.main.fragment.pr;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.prapp.R;
import com.prapp.ui.main.InterfaceHolder;
import com.prapp.ui.main.MainActivityInterface;
import com.prapp.ui.main.MainViewModel;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PRFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PRFragment extends Fragment implements InterfaceHolder<MainActivityInterface> {

    private MainViewModel mainViewModel;
    private Unbinder unbinder;

    @BindView(R.id.fragment_pr_toolbar_cliente)
    public Toolbar toolbarClienti;

    @BindView(R.id.fragment_pr_aggiungiCliente_layout)
    public LinearLayout aggiungiClienteLayout;

    @BindView(R.id.fragment_pr_aggiungiCliente_nome_editText)
    public EditText aggiungiClienteNomeEditText;

    @BindView(R.id.fragment_pr_aggiungiCliente_cognome_editText)
    public EditText aggiungiClienteCognomeEditText;

    @BindView(R.id.fragment_pr_recyclerViewClienti)
    public RecyclerView recyclerViewClienti;

    /**
     * Interfaccia usata per comunicare con l'activity madre.
     */
    private MainActivityInterface mainActivityInterface;

    @Override
    public void holdInterface(@NotNull MainActivityInterface mainActivityInterface) {
        this.mainActivityInterface = mainActivityInterface;
    }

    @Override
    public boolean isInterfaceSet() {
        return this.mainActivityInterface != null;
    }


    public PRFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PRFragment.
     */
    public static PRFragment newInstance() {
        PRFragment fragment = new PRFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);    //Opzione menu
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    //ROBA MENU------------------------------------------------------------------------------

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.pr_menu, menu);

        //Inizialmente sono in modalità aggiungi: Rimuovo pulsante.
        menu.removeItem(R.id.fragment_pr_menu_aggiungiPrevenditaItem);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.fragment_pr_menu_aggiungiPrevenditaItem:

                return true;

            case R.id.fragment_pr_menu_listaPrevenditeItem:

                return true;

            case R.id.fragment_pr_menu_statisticheEventoItem:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //--------------------------------------------------------------------------------------

    private boolean onClientiMenuItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()){

            case R.id.fragment_pr_cliente_menu_aggiungiClienteItem:

                return true;

            case R.id.fragment_pr_cliente_menu_searchClienteItem:

                return true;

            default: return false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pr, container, false);

        unbinder = ButterKnife.bind(this, view);

        mainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        //Imposto la toolbar clienti.
        toolbarClienti.inflateMenu(R.menu.pr_cliente_menu);
        Menu clientiMenu = toolbarClienti.getMenu();

        //Rimuovo il pulsante aggiungi cliente: parto da esso.
        clientiMenu.removeItem(R.id.fragment_pr_cliente_menu_aggiungiClienteItem);

        //Imposto il callback.
        toolbarClienti.setOnMenuItemClickListener(this::onClientiMenuItemSelected);

        toolbarClienti.setTitle(R.string.fragment_pr_cliente_toolbar_label);


        return view;
    }

}
