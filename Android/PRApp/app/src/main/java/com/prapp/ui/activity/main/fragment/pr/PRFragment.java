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

package com.prapp.ui.activity.main.fragment.pr;


import android.app.SearchManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.prapp.R;
import com.prapp.barcodescanner.CustomBarcodeEncoder;
import com.prapp.model.db.enums.StatoPrevendita;
import com.prapp.model.db.wrapper.WPrevendita;
import com.prapp.model.db.wrapper.WTipoPrevendita;
import com.prapp.model.net.wrapper.NetWEntrata;
import com.prapp.ui.Result;
import com.prapp.ui.activity.main.MainActivityInterface;
import com.prapp.ui.adapter.WTipoPrevenditaAdapter;
import com.prapp.ui.utils.InterfaceHolder;
import com.prapp.ui.utils.ItemClickListener;
import com.prapp.ui.utils.PopupUtil;
import com.prapp.ui.utils.UiUtil;

import org.jetbrains.annotations.NotNull;
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
public class PRFragment extends Fragment implements InterfaceHolder<MainActivityInterface> {

    private static final String TAG = PRFragment.class.getSimpleName();

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.shortDate();

    private static final StatoPrevendita DEFAULT_STATO_PREVENDITA = StatoPrevendita.VALIDA;

    private static final Gson GSON = new Gson();

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
    private UiUtil uiUtil;

    /**
     * Utilty per popup.
     */
    private PopupUtil popupUtil;

    private Bitmap qrCode;

    private WTipoPrevendita selectTipoPrevendita;
    private StatoPrevendita selectStatoPrevendita = DEFAULT_STATO_PREVENDITA;    //Imposto a default
    private WPrevendita nuovaPrevendita;

    @BindView(R.id.fragment_pr_selezionaPrevendita_layout)
    public LinearLayout aggiungiPrevenditaLayout;

    @BindView(R.id.fragment_pr_prevendita_toolbar)
    public Toolbar prevenditaToolbar;

    @BindView(R.id.fragment_pr_aggiungiCliente_nome_editText)
    public EditText aggiungiClienteNomeEditText;

    @BindView(R.id.fragment_pr_aggiungiCliente_cognome_editText)
    public EditText aggiungiClienteCognomeEditText;

    @BindView(R.id.fragment_pr_tipoPrevendita_editText)
    public EditText tipoPrevenditaEditText;

    private ArrayAdapter<StatoPrevendita> statoPrevenditaAdapter;

    @BindView(R.id.fragment_pr_statoPrevendita_spinner)
    public Spinner statoPrevenditaSpinner;

    private WTipoPrevenditaAdapter tipoPrevenditaAdapter;

    @BindView(R.id.fragment_pr_tipiPrevendita_recyclerView)
    public RecyclerView tipiPrevenditaRecyclerView;

    @BindView(R.id.fragment_pr_aggiungiPrevendita_button)
    public Button aggiungiPrevenditaButton;

    private Observer<Result<List<WTipoPrevendita>, Void>> getListaTipoPrevenditaResultObserver = new Observer<Result<List<WTipoPrevendita>, Void>>() {

        @Override
        public void onChanged(Result<List<WTipoPrevendita>, Void> listVoidResult) {
            if (listVoidResult == null) {
                return;
            }

            Integer integerError = listVoidResult.getIntegerError();
            List<Exception> error = listVoidResult.getError();
            List<WTipoPrevendita> success = listVoidResult.getSuccess();

            if (integerError != null)
                uiUtil.showError(integerError);

            else if (error != null)
                uiUtil.showError(error);


            else if (success != null) {
                tipoPrevenditaAdapter.replaceDataset(success);
            }
        }
    };

    private Observer<Result<WPrevendita, Void>> aggiungiPrevenditaResultObserver = new Observer<Result<WPrevendita, Void>>() {
        @Override
        public void onChanged(Result<WPrevendita, Void> result) {
            if (result == null)
                return;

            Integer integerError = result.getIntegerError();
            List<Exception> error = result.getError();
            WPrevendita success = result.getSuccess();

            if (integerError != null)
                uiUtil.showError(integerError);

            else if (error != null)
                uiUtil.showError(error);

            else if (success != null) {
                nuovaPrevendita = success;  //Salvo la nuova prevendita in modo da utilizzarla dopo

                //Devo serializzare una NetWEntrata
                NetWEntrata entrata = new NetWEntrata(nuovaPrevendita.getId(), nuovaPrevendita.getIdEvento(), nuovaPrevendita.getCodice());

                String serialObj = GSON.toJson(entrata);

                String rigaPersona = getString(R.string.fragment_pr_qr_text_persona, nuovaPrevendita.getNomeCliente(), nuovaPrevendita.getCognomeCliente());
                String rigaWarning = getString(R.string.fragment_pr_qr_text_warning);
                String rigaInfoPrev = getString(R.string.fragment_pr_qr_text_infoPrevendita, success.getId(), success.getIdEvento(), success.getCodice());
                String rigaNomeTipoPrev = selectTipoPrevendita.getNome();

                CustomBarcodeEncoder barcodeEncoder = new CustomBarcodeEncoder();

                barcodeEncoder.add(rigaPersona, false);
                barcodeEncoder.add(rigaWarning, true);
                barcodeEncoder.add(rigaInfoPrev, false);
                barcodeEncoder.add(rigaNomeTipoPrev, false);

                try {
                    qrCode = barcodeEncoder.encodeBitmap(serialObj, BarcodeFormat.QR_CODE, 400, 400);
                    showSuccessPopup(qrCode);

                } catch (WriterException e) {
                    Log.v(TAG, e.toString());
                }
            }
        }
    };

    private Observer<Integer> prevenditaModeObserver = mode -> {
        if (mode != null) {
            aggiungiPrevenditaButton.setEnabled(mode == PRViewModel.PREVENDITA_SELECT_MODE);
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
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        uiUtil = new UiUtil(context);
        popupUtil = new PopupUtil(context);
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
            case R.id.fragment_pr_menu_listaPrevenditeItem:
                //Istanzio un nuovo fragment lista e lo inizializzo per le prevendite.
                if(isInterfaceSet()){
                    PRSubFragmentLista fragmentLista = PRSubFragmentLista.newInstance();
                    fragmentLista.holdInterface(mainActivityInterface);
                    mainActivityInterface.cambiaFragment(fragmentLista);
                }
                return true;
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
        View view = inflater.inflate(R.layout.fragment_pr, container, false);

        unbinder = ButterKnife.bind(this, view);

        viewModel = ViewModelProviders.of(getActivity()).get(PRViewModel.class);
        viewModel.getPrevenditaModeLiveData().observe(getViewLifecycleOwner(), prevenditaModeObserver);
        viewModel.getListaTipoPrevenditaResult().observe(getViewLifecycleOwner(), getListaTipoPrevenditaResultObserver);
        viewModel.getAggiungiPrevenditaResult().observe(getViewLifecycleOwner(), aggiungiPrevenditaResultObserver);

        //Imposto l'item di ricerca:
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        //Impostazioni prevendita.

        //Impostazione recycler view tipi prevendita.
        tipoPrevenditaAdapter = new WTipoPrevenditaAdapter();
        tipoPrevenditaAdapter.setClickListener(new ItemClickListener<WTipoPrevendita>() {
            @Override
            public void onItemClick(int pos, WTipoPrevendita obj) {
                onSearchTipoPrevenditaItemClick(pos, obj);
            }

            @Override
            public void onItemLongClick(int pos, WTipoPrevendita obj) {

            }
        });
        tipiPrevenditaRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tipiPrevenditaRecyclerView.setHasFixedSize(false);
        tipiPrevenditaRecyclerView.setNestedScrollingEnabled(false);
        tipiPrevenditaRecyclerView.setAdapter(tipoPrevenditaAdapter);
        tipiPrevenditaRecyclerView.setVisibility(View.GONE);

        //Imposto la toolbar clienti.
        prevenditaToolbar.inflateMenu(R.menu.pr_prevendita_menu);
        prevenditaToolbar.setTitle(R.string.fragment_pr_prevendita_toolbar_label);

        //Nascondo il pulsante di aggiunta
        Menu prevenditaMenu = prevenditaToolbar.getMenu();

        //Item della ricerca
        MenuItem searchTipoPrevenditaItem = prevenditaMenu.findItem(R.id.fragment_pr_prevendita_menu_searchItem);

        //https://stackoverflow.com/questions/27378981/how-to-use-searchview-in-toolbar-android
        if (searchTipoPrevenditaItem != null) {
            SearchView searchTipoPrevenditaView = (SearchView) searchTipoPrevenditaItem.getActionView();

            if (searchTipoPrevenditaView != null) {
                // Assumes current activity is the searchable activity
                searchTipoPrevenditaView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

                searchTipoPrevenditaView.setIconifiedByDefault(true); // Do iconify the widget
                searchTipoPrevenditaView.setSubmitButtonEnabled(false); //Non voglio il pulsante di submit.

                //Imposto i listener:
                //Questo serve quando clicco sulla ricerca.
                searchTipoPrevenditaItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem menuItem) {
                        return onSearchTipoPrevenditaExpand(menuItem);
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                        return onSearchTipoPrevenditaCollapse(menuItem);
                    }
                });

                //Questo serve per aggiornare i filtri
                searchTipoPrevenditaView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false; //Non ci interessa quando si preme il pulsante perchè non c'è pulsante.
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return onSearchTipoPrevenditaQueryTextChange(newText);
                    }
                });
            }
        }

        //Spinner dello stato prevendita

        //https://developer.android.com/guide/topics/ui/controls/spinner
        //https://stackoverflow.com/questions/24712540/set-key-and-value-in-spinner
        statoPrevenditaAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, StatoPrevendita.values());
        statoPrevenditaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  // Specify the layout to use when the list of choices appears
        statoPrevenditaSpinner.setAdapter(statoPrevenditaAdapter);  // Apply the adapter to the spinner
        statoPrevenditaSpinner.setSelection(statoPrevenditaAdapter.getPosition(selectStatoPrevendita));
        statoPrevenditaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                onStatoPrevenditaItemSelected(parent, view, pos, id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Inizialmente pulsante disabilitato: si abiliterà solo se i dati vanno bene
        aggiungiPrevenditaButton.setEnabled(false);

        return view;
    }

    @OnTextChanged(value = {R.id.fragment_pr_aggiungiCliente_nome_editText,
            R.id.fragment_pr_aggiungiCliente_cognome_editText},
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onTextChanged(Editable s) {
        viewModel.aggiungiClienteStateChanged(aggiungiClienteNomeEditText.getText().toString(),
                aggiungiClienteCognomeEditText.getText().toString());
    }

    public void onSearchTipoPrevenditaItemClick(int pos, WTipoPrevendita obj) {
        //Quando ho premuto su un cliente vuol dire che ho selezionato il cliente:
        //Aggiorno lo stato
        viewModel.setPrevenditaMode(PRViewModel.PREVENDITA_SELECT_MODE);

        //Chiudo la ricerca:
        Menu prevenditaMenu = prevenditaToolbar.getMenu();
        MenuItem item = prevenditaMenu.findItem(R.id.fragment_pr_prevendita_menu_searchItem);
        item.collapseActionView();

        //Pulisco il recycler view e lascio solo quello selezionato:
        tipoPrevenditaAdapter.replaceDataset(obj);
        //Imposto il text view.
        tipoPrevenditaEditText.setText(obj.getNome());

        selectTipoPrevendita = obj;
    }

    private boolean onSearchTipoPrevenditaCollapse(MenuItem menuItem) {
        //Quando collasso devo verificare che non abbia premuto il tasto indietro senza fare una select.
        if (viewModel.getPrevenditaMode() != PRViewModel.PREVENDITA_SELECT_MODE) {
            viewModel.setPrevenditaMode(PRViewModel.PREVENDITA_SELECT_MODE);
        }
        switchPrevenditaModeView();
        return true;
    }

    private boolean onSearchTipoPrevenditaExpand(MenuItem item){
        viewModel.setPrevenditaMode(PRViewModel.PREVENDITA_SEARCH_MODE);
        switchPrevenditaModeView();
        //Popolo l'adapter.
        viewModel.getListaTipoPrevendita();
        return true;
    }

    private boolean onSearchTipoPrevenditaQueryTextChange(String newText) {
        tipoPrevenditaAdapter.getFilter().filter(newText);
        return false;
    }

    private void switchPrevenditaModeView() {
        if (viewModel.getPrevenditaMode() == PRViewModel.PREVENDITA_SEARCH_MODE) {
            //Chiusura aggiunta cliente:
            aggiungiPrevenditaLayout.setVisibility(View.GONE);

            //Mostro il recycler view con i clienti.
            tipiPrevenditaRecyclerView.setVisibility(View.VISIBLE);
        } else {
            //Mostro aggiunta cliente:
            aggiungiPrevenditaLayout.setVisibility(View.VISIBLE);

            //Nascondo il recycler view con i clienti.
            tipiPrevenditaRecyclerView.setVisibility(View.GONE);
        }
    }

    public void onStatoPrevenditaItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        //Imposto il selezionato.
        selectStatoPrevendita = (StatoPrevendita)parent.getSelectedItem();
    }

    @OnClick(R.id.fragment_pr_aggiungiPrevendita_button)
    public void onAggiungiPrevenditaClick(View v){
        //Dato che il tasto è abilitato solo se i dati sono selezionati posso passare direttamente
        //All'inserimento della prevendita.

        String nomeCliente = aggiungiClienteNomeEditText.getText().toString();
        String cognomeCliente = aggiungiClienteCognomeEditText.getText().toString();

        viewModel.aggiungiPrevendita(nomeCliente, cognomeCliente, selectTipoPrevendita, selectStatoPrevendita);
    }

    /**
     * Mostra un popup di successo.
     */
    private void showSuccessPopup(Bitmap image) {
        popupUtil.showQRPopup(getActivity(), image, this::onCondividiButtonClick, R.string.fragment_pr_popup_condividi);
    }

    private void onCondividiButtonClick(View view){
        //Creo un testo di condivisone.
        String shareText = getString(R.string.fragment_pr_qr_share_text, nuovaPrevendita.getNomeCliente(), nuovaPrevendita.getCognomeCliente(), nuovaPrevendita.getId(), nuovaPrevendita.getIdEvento(), nuovaPrevendita.getCodice(), viewModel.getEvento().getNome(), nuovaPrevendita.getStato());
        viewModel.shareImage(getActivity(), qrCode, shareText);
    }

}
