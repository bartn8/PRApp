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

package com.prapp.ui.activity.main.fragment.cassiere;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prapp.R;
import com.prapp.model.db.wrapper.WPrevenditaPlus;
import com.prapp.ui.Result;
import com.prapp.ui.activity.main.MainActivity;
import com.prapp.ui.activity.main.MainActivityInterface;
import com.prapp.ui.adapter.WPrevenditaPlusAdapter;
import com.prapp.ui.utils.InterfaceHolder;
import com.prapp.ui.utils.ItemClickListener;
import com.prapp.ui.utils.PopupUtil;
import com.prapp.ui.utils.UiUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CassiereSubFragmentLista extends Fragment implements InterfaceHolder<MainActivityInterface>, ItemClickListener<WPrevenditaPlus> {

    public static final String MODE_KEY = "MODE";

    public static final int MODE_LIST_TIMBRATE = 0;
    public static final int MODE_LIST_NON_TIMBRATE = 1;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mode use CassiereSubFragmentLista.MODE_LIST_TIMBRATE or CassiereSubFragmentLista.MODE_LIST_NON_TIMBRATE
     *
     * @return A new instance of fragment CassiereSubFragmentLista.
     */
    public static CassiereSubFragmentLista newInstance(int mode) {
        CassiereSubFragmentLista fragment = new CassiereSubFragmentLista();
        Bundle args = new Bundle();
        args.putInt(MODE_KEY, mode);
        fragment.setArguments(args);
        return fragment;
    }

    public CassiereSubFragmentLista() {
        // Required empty public constructor
    }

    /**
     * Modalità con cui sono stato avviato.
     */
    private int mode;

    /**
     * View-Model per interfacciarsi con il server.
     */
    private CassiereViewModel viewModel;

    /**
     * Robo per discollegarsi dalle view.
     */
    private Unbinder unbinder;

    /**
     * Utilty per la grafica
     */
    private UiUtil uiUtil;

    private PopupUtil popupUtil;

    /**
     * Interfaccia usata per comunicare con l'activity madre.
     */
    private MainActivityInterface mainActivityInterface;

    /**
     * Adattatore per far vedere nella recycler view le prevendite che si vogliono approvare.
     */
    private WPrevenditaPlusAdapter recyclerAdapter;

    @Override
    public void holdInterface(MainActivityInterface mainActivityInterface){
        this.mainActivityInterface = mainActivityInterface;
    }

    @Override
    public boolean isInterfaceSet(){
        return this.mainActivityInterface != null;
    }

    @BindView(R.id.subfragment_lista_label)
    public TextView label;

    @BindView(R.id.subfragment_lista_recyclerView)
    public RecyclerView recyclerView;

    private Observer<Result<List<WPrevenditaPlus>, Void>> getPrevenditeResultObserver = new Observer<Result<List<WPrevenditaPlus>, Void>>() {
        @Override
        public void onChanged(Result<List<WPrevenditaPlus>, Void> wPrevenditaPlusResult) {
            if (wPrevenditaPlusResult == null) {
                return;
            }

            Integer integerError = wPrevenditaPlusResult.getIntegerError();
            List<Exception> error = wPrevenditaPlusResult.getError();
            List<WPrevenditaPlus> success = wPrevenditaPlusResult.getSuccess();

            if (integerError != null)
                uiUtil.showError(integerError);

            else if (error != null)
                uiUtil.showError(error);

            else if (success != null) {
                if(success.isEmpty()){
                    uiUtil.makeToast(R.string.subfragment_lista_cassiere_lista_vuota_toast);
                }else{
                    //Applico al recycler view i dati.
                    recyclerAdapter.replaceDataset(success);
                }
            }

            popupUtil.hideLoadingPopup();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);    //Opzione menu

        //Recupero argomenti.
        Bundle args = getArguments();

        if(args != null){
            mode = args.getInt(MODE_KEY);
        }
    }

    //ROBA MENU------------------------------------------------------------------------------

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.cassiere_menu, menu);

        //Disattivo parti del menu:
        menu.removeItem(R.id.cassiere_autoApprovaItem);

        if(mode == MODE_LIST_TIMBRATE) menu.removeItem(R.id.cassiere_genteEntrataItem);
        else if(mode == MODE_LIST_NON_TIMBRATE) menu.removeItem(R.id.cassiere_genteNonEntrataItem);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.cassiere_scansionaItem:
                //Ritorno al fragment cassiere.
                if(isInterfaceSet())
                    mainActivityInterface.cambiaFragment(mainActivityInterface.getNavFragment(MainActivity.ID_FRAGMENT_CASSIERE));
                return true;
            case R.id.cassiere_genteEntrataItem:
                //Non è il mio campo
                if(mode == MODE_LIST_TIMBRATE) return super.onOptionsItemSelected(item);

                //Istanzio un nuovo fragment lista e lo inizializzo per le prevendite approvate.
                if(isInterfaceSet()){
                    CassiereSubFragmentLista cassiereSubFragmentLista = CassiereSubFragmentLista.newInstance(MODE_LIST_TIMBRATE);
                    cassiereSubFragmentLista.holdInterface(mainActivityInterface);
                    mainActivityInterface.cambiaFragment(cassiereSubFragmentLista);
                }

                return true;
            case R.id.cassiere_genteNonEntrataItem:
                //Non è il mio campo
                if(mode == MODE_LIST_NON_TIMBRATE) return super.onOptionsItemSelected(item);

                //Istanzio un nuovo fragment lista e lo inizializzo per le prevendite non approvate.
                if(isInterfaceSet()){
                    CassiereSubFragmentLista cassiereSubFragmentLista = CassiereSubFragmentLista.newInstance(MODE_LIST_NON_TIMBRATE);
                    cassiereSubFragmentLista.holdInterface(mainActivityInterface);
                    mainActivityInterface.cambiaFragment(cassiereSubFragmentLista);
                }

                return true;
            default:
                uiUtil.makeToast(R.string.not_implemented_yet);
                return super.onOptionsItemSelected(item);
        }
    }

    //----------------------------------------------------------------------------------

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.subfragment_lista, container, false);

        //Mitico butterknife per fare il collegamento tra XML e oggetti.
        unbinder = ButterKnife.bind(this, view);

        uiUtil = new UiUtil(getActivity());
        popupUtil = new PopupUtil(getActivity());

        recyclerAdapter = new WPrevenditaPlusAdapter();
        recyclerAdapter.setClickListener(this);

        //Imposto il recyler view. Quello che fa vedere le prevendite.
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(recyclerAdapter);

        //View model per richiamare il server.
        viewModel = ViewModelProviders.of(getActivity()).get(CassiereViewModel.class);
        viewModel.getPrevenditeResult().observe(getViewLifecycleOwner(), this.getPrevenditeResultObserver);

        //Richiedo al server i dati.
        if(mode == MODE_LIST_TIMBRATE){
            label.setText(R.string.subfragment_lista_cassiere_listaTimbrate_label);
            viewModel.getListaPrevenditeTimbrateEvento();
            popupUtil.showLoadingPopup();
        }
        else if (mode == MODE_LIST_NON_TIMBRATE){
            label.setText(R.string.subfragment_lista_cassiere_listaNonTimbrate_label);
            viewModel.getListaPrevenditeNonTimbrateEvento();
            popupUtil.showLoadingPopup();
        }


        return view;
    }


    @Override
    public void onDestroyView() {
        //Rimuvo il binding di ButterKnife
        unbinder.unbind();
        super.onDestroyView();
    }


    @Override
    public void onItemClick(int id, WPrevenditaPlus obj) {

    }

    @Override
    public void onItemLongClick(int pos, WPrevenditaPlus obj) {

    }
}
