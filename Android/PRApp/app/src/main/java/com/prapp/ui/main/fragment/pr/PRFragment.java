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


import android.app.DatePickerDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prapp.R;
import com.prapp.model.db.wrapper.WCliente;
import com.prapp.ui.Result;
import com.prapp.ui.main.MainActivityInterface;
import com.prapp.ui.main.adapter.WClienteAdapter;
import com.prapp.ui.utils.DatePickerFragment;
import com.prapp.ui.utils.InterfaceHolder;
import com.prapp.ui.utils.UiUtils;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PRFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PRFragment extends Fragment implements InterfaceHolder<MainActivityInterface>, DatePickerDialog.OnDateSetListener {

    private static final String TAG = PRFragment.class.getSimpleName();

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.mediumDate();


    private static final String CLIENTE_MODE_KEY = "CLIENTI_MODE";
    private static final int CLIENTE_SEARCH_MODE = 0;
    private static final int CLIENTE_ADD_MODE = 1;
    private static final int CLIENTE_SELECT_MODE = 2;

    /**
     * View-Model per interfacciarsi con il server.
     */
    private PRViewModel viewModel;

    /**
     * Robo per discollegarsi dalle view.
     */
    private Unbinder unbinder;

    /**
     * Utilty per la grafica
     */
    private UiUtils uiUtils;

    private int clienteMode = CLIENTE_ADD_MODE;

    @BindView(R.id.fragment_pr_toolbar_cliente)
    public Toolbar toolbarClienti;

    @BindView(R.id.fragment_pr_aggiungiCliente_layout)
    public LinearLayout aggiungiClienteLayout;

    @BindView(R.id.fragment_pr_aggiungiCliente_nome_editText)
    public EditText aggiungiClienteNomeEditText;

    @BindView(R.id.fragment_pr_aggiungiCliente_cognome_editText)
    public EditText aggiungiClienteCognomeEditText;

    @BindView(R.id.fragment_pr_aggiungiCliente_dataDiNascita_editText)
    public EditText aggiungiClienteDataDiNascitaEditText;

    @BindView(R.id.fragment_pr_aggiungiCliente_button)
    public Button aggiungiClienteButton;

    private WClienteAdapter adapterClienti;

    @BindView(R.id.fragment_pr_recyclerViewClienti)
    public RecyclerView recyclerViewClienti;

    private SearchView searchClienteView;

    private Observer<Result<List<WCliente>, Void>> getListaClientiResultObserver = new Observer<Result<List<WCliente>, Void>>() {

        @Override
        public void onChanged(Result<List<WCliente>, Void> listVoidResult) {
            if (listVoidResult == null) {
                return;
            }

            Integer integerError = listVoidResult.getIntegerError();
            List<Exception> error = listVoidResult.getError();
            List<WCliente> success = listVoidResult.getSuccess();

            if (integerError != null)
                uiUtils.showError(integerError);

            else if (error != null)
                uiUtils.showError(error);


            else if (success != null) {
                adapterClienti.replace(success);
            }
        }
    };

    private Observer<AggiungiClienteState> aggiungiClienteStateObserver = new Observer<AggiungiClienteState>() {
        @Override
        public void onChanged(AggiungiClienteState aggiungiClienteState) {
            if(aggiungiClienteState == null)
                return;

            if(aggiungiClienteState.isDataValid()){
                aggiungiClienteButton.setEnabled(true);
            }else{
                if(aggiungiClienteState.getNomeClienteError() != null){
                    aggiungiClienteNomeEditText.setError(getString(aggiungiClienteState.getNomeClienteError()));
                }
                if(aggiungiClienteState.getCognomeClienteError() != null){
                    aggiungiClienteCognomeEditText.setError(getString(aggiungiClienteState.getCognomeClienteError()));
                }
                if(aggiungiClienteState.getDataDiNascitaClienteError() != null){
                    aggiungiClienteDataDiNascitaEditText.setError(getString(aggiungiClienteState.getDataDiNascitaClienteError()));
                }
            }
        }
    };

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

        if (savedInstanceState != null) {
            clienteMode = savedInstanceState.getInt(CLIENTE_MODE_KEY);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        uiUtils = UiUtils.getInstance(context);
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

    private boolean onClientiMenuItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.fragment_pr_cliente_menu_aggiungiClienteItem:
                //Nascondo il recycler view.
                recyclerViewClienti.setVisibility(View.GONE);

                //Mostro la roba per aggiungere il cliente.
                aggiungiClienteLayout.setVisibility(View.VISIBLE);

                return true;

            default:
                return false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pr, container, false);

        unbinder = ButterKnife.bind(this, view);

        viewModel = ViewModelProviders.of(getActivity()).get(PRViewModel.class);
        viewModel.getListaClientiResult().observe(this, getListaClientiResultObserver);
        viewModel.getAggiungiClienteState().observe(this, aggiungiClienteStateObserver);

        //Impostazione recycler view clienti.
        adapterClienti = new WClienteAdapter(this::onSearchClienteItemClick);
        recyclerViewClienti.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewClienti.setHasFixedSize(false);
        recyclerViewClienti.setNestedScrollingEnabled(false);
        recyclerViewClienti.setAdapter(adapterClienti);
        recyclerViewClienti.setVisibility(View.GONE);


        //Imposto la toolbar clienti.
        toolbarClienti.inflateMenu(R.menu.pr_cliente_menu);
        //Imposto il callback e il titolo della toolbar.
        toolbarClienti.setOnMenuItemClickListener(this::onClientiMenuItemSelected);
        toolbarClienti.setTitle(R.string.fragment_pr_cliente_toolbar_label);

        //Nascondo il pulsante di aggiunta
        Menu clientiMenu = toolbarClienti.getMenu();

        //Item della ricerca
        MenuItem searchItem = clientiMenu.findItem(R.id.fragment_pr_cliente_menu_searchClienteItem);
        //Imposto l'item di ricerca:
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        //https://stackoverflow.com/questions/27378981/how-to-use-searchview-in-toolbar-android
        if (searchItem != null) {
            searchClienteView = (SearchView) searchItem.getActionView();

            if (searchClienteView != null) {
                // Assumes current activity is the searchable activity
                searchClienteView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

                searchClienteView.setIconifiedByDefault(true); // Do iconify the widget
                searchClienteView.setSubmitButtonEnabled(false); //Non voglio il pulsante di submit.

                //Imposto i listener:
                //Questo serve quando clicco sulla ricerca.
                searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem menuItem) {
                        return onSearchClienteExpand(menuItem);
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                        return onSearchClienteCollapse(menuItem);
                    }
                });

                //Questo serve per aggiornare i filtri
                searchClienteView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false; //Non ci interessa quando si preme il pulsante perchè non c'è pulsante.
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return onSearchClienteQueryTextChange(newText);
                    }
                });
            }
        }

        //FINE IMPOSTAZIONI TOOLBAR

        //Impostazioni aggiunta cliente
        //Inizialmente pulsante disabilitato: si abiliterà solo se i dati vanno bene
        aggiungiClienteButton.setEnabled(false);

        aggiungiClienteDataDiNascitaEditText.setOnClickListener(view1 -> {
            DatePickerFragment datePicker = new DatePickerFragment();
            datePicker.holdInterface(this);
            datePicker.show(getFragmentManager(), TAG);
        });

        //FINE IMPOSTAZIONI AGGIUNTA CLIENTE

        //Popolo l'adapter.
        viewModel.getListaClienti();

        return view;
    }

    private boolean onSearchClienteCollapse(MenuItem menuItem) {
        //Lo stato viene aggiornato precedentemente.
        switchClienteModeView();
        return true;
    }

    private boolean onSearchClienteExpand(MenuItem menuItem) {
        clienteMode = CLIENTE_SEARCH_MODE;
        switchClienteModeView();
        return true;
    }

    public void onSearchClienteItemClick(int id, WCliente obj) {
        //Quando ho premuto su un cliente vuol dire che ho selezionato il cliente:
        //Aggiorno lo stato
        clienteMode = CLIENTE_SELECT_MODE;

        //Chiudo la ricerca:
        Menu clientiMenu = toolbarClienti.getMenu();
        MenuItem item = clientiMenu.findItem(R.id.fragment_pr_cliente_menu_searchClienteItem);
        item.collapseActionView();

        //Pulisco il recycler view e lascio solo quello selezionato:
        adapterClienti.replace(obj);

        switchClienteModeView();
    }

    private boolean onSearchClienteQueryTextChange(String newText) {
        adapterClienti.getFilter().filter(newText);
        return false;
    }

    private void switchClienteModeView() {
        if (clienteMode == CLIENTE_ADD_MODE) {
            //Mostro aggiunta cliente:
            aggiungiClienteLayout.setVisibility(View.VISIBLE);

            //Nascondo il recycler view con i clienti.
            recyclerViewClienti.setVisibility(View.GONE);
        } else if (clienteMode == CLIENTE_SEARCH_MODE || clienteMode == CLIENTE_SELECT_MODE) {
            //Chiusura aggiunta cliente:
            aggiungiClienteLayout.setVisibility(View.GONE);

            //Mostro il recycler view con i clienti.
            recyclerViewClienti.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.fragment_pr_aggiungiCliente_button)
    public void onAggiungiButtonClick(View v) {
        //Pulizia dei componenti
        aggiungiClienteNomeEditText.getText().clear();
        aggiungiClienteCognomeEditText.getText().clear();
        aggiungiClienteDataDiNascitaEditText.getText().clear();

        //Provo ad inserire un nuovo cliente:


    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        LocalDate date = new LocalDate(year, month, dayOfMonth);
        aggiungiClienteDataDiNascitaEditText.setText(date.toString(DATE_FORMAT));
    }

    @OnTextChanged(value = {R.id.fragment_pr_aggiungiCliente_nome_editText,
            R.id.fragment_pr_aggiungiCliente_cognome_editText,
            R.id.fragment_pr_aggiungiCliente_dataDiNascita_editText},
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onTextChanged(Editable s)
    {
        viewModel.aggiungiClienteStateChanged(aggiungiClienteNomeEditText.getText().toString(),
                aggiungiClienteCognomeEditText.getText().toString(),
                aggiungiClienteDataDiNascitaEditText.getText().toString());
    }


}
