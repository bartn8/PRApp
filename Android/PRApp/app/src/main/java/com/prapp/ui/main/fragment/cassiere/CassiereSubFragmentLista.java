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

package com.prapp.ui.main.fragment.cassiere;

import android.content.Context;
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
import com.prapp.ui.UiUtils;
import com.prapp.ui.main.InterfaceHolder;
import com.prapp.ui.main.MainActivity;
import com.prapp.ui.main.MainActivityInterface;
import com.prapp.ui.main.MainViewModel;
import com.prapp.ui.main.adapter.WPrevenditaPlusAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CassiereSubFragmentLista extends Fragment implements InterfaceHolder<MainActivityInterface> {

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
    private MainViewModel mainViewModel;

    /**
     * Robo per discollegarsi dalle view.
     */
    private Unbinder unbinder;

    /**
     * Utilty per la grafica
     */
    private UiUtils uiUtils;

    /**
     * Interfaccia usata per comunicare con l'activity madre.
     */
    private MainActivityInterface mainActivityInterface;

    /**
     * Adattatore per far vedere nella recycler view le prevendite che si vogliono approvare.
     */
    private WPrevenditaPlusAdapter recyclerAdapter = new WPrevenditaPlusAdapter(null);//Imposto al massimo un elemento per volta.

    @Override
    public void holdInterface(MainActivityInterface mainActivityInterface){
        this.mainActivityInterface = mainActivityInterface;
    }

    @Override
    public boolean isInterfaceSet(){
        return this.mainActivityInterface != null;
    }

    @BindView(R.id.subfragment_lista_cassiere_label)
    public TextView label;

    @BindView(R.id.subfragment_lista_cassiere_recyclerView)
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
                uiUtils.showError(integerError);

            else if (error != null)
                uiUtils.showError(error);

            else if (success != null) {
                if(success.isEmpty()){
                    uiUtils.makeToast(R.string.subfragment_lista_cassiere_lista_vuota_toast);
                }else{
                    //Applico al recycler view i dati.
                    recyclerAdapter.add(success);
                }
            }
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
                mainActivityInterface.cambiaFragment(mainActivityInterface.getNavFragment(MainActivity.ID_FRAGMENT_CASSIERE));
                return true;
            case R.id.cassiere_genteEntrataItem:
                //Non è il mio campo
                if(mode == MODE_LIST_TIMBRATE) return super.onOptionsItemSelected(item);

                //Istanzio un nuovo fragment lista e lo inizializzo per le prevendite approvate.
                mainActivityInterface.cambiaFragment(CassiereSubFragmentLista.newInstance(MODE_LIST_TIMBRATE));


                return true;
            case R.id.cassiere_genteNonEntrataItem:
                //Non è il mio campo
                if(mode == MODE_LIST_NON_TIMBRATE) return super.onOptionsItemSelected(item);

                //Istanzio un nuovo fragment lista e lo inizializzo per le prevendite non approvate.
                mainActivityInterface.cambiaFragment(CassiereSubFragmentLista.newInstance(MODE_LIST_NON_TIMBRATE));

                return true;
            case R.id.cassiere_statisticheEventoItem:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //----------------------------------------------------------------------------------


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        uiUtils = UiUtils.getInstance(context);
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.subfragment_lista_cassiere, container, false);

        //Mitico butterknife per fare il collegamento tra XML e oggetti.
        unbinder = ButterKnife.bind(this, view);

        //Imposto il recyler view. Quello che fa vedere le prevendite.
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(recyclerAdapter);

        //View model per richiamare il server.
        mainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        mainViewModel.getPrevenditeResult().observe(this, this.getPrevenditeResultObserver);

        //Richiedo al server i dati.
        if(mode == MODE_LIST_TIMBRATE){
            label.setText(R.string.subfragment_lista_cassiere_listaTimbrate_label);
            mainViewModel.getListaPrevenditeTimbrateEvento();
        }
        else if (mode == MODE_LIST_NON_TIMBRATE){
            label.setText(R.string.subfragment_lista_cassiere_listaNonTimbrate_label);
            mainViewModel.getListaPrevenditeNonTimbrateEvento();
        }


        return view;
    }


    @Override
    public void onDestroyView() {
        //Rimuvo il binding di ButterKnife
        unbinder.unbind();
        super.onDestroyView();
    }


}
