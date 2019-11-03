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

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prapp.R;
import com.prapp.model.db.wrapper.WPrevenditaPlus;
import com.prapp.ui.Result;
import com.prapp.ui.main.MainActivity;
import com.prapp.ui.main.MainActivityInterface;
import com.prapp.ui.main.adapter.WPrevenditaPlusAdapter;
import com.prapp.ui.utils.InterfaceHolder;
import com.prapp.ui.utils.ItemClickListener;
import com.prapp.ui.utils.UiUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import be.digitalia.common.widgets.MultiChoiceHelper;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PRSubFragmentLista extends Fragment implements InterfaceHolder<MainActivityInterface>, ItemClickListener<WPrevenditaPlusAdapter.WPrevenditaPlusWrapper> {

    private static final String MULTI_CHOICE_KEY = "multiChoiceKey";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment PRSubFragmentLista.
     */
    public static PRSubFragmentLista newInstance() {
        PRSubFragmentLista fragment = new PRSubFragmentLista();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public PRSubFragmentLista() {
        // Required empty public constructor
    }

    /**
     * Modalità con cui sono stato avviato.
     */
    private int mode;

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
     * Interfaccia usata per comunicare con l'activity madre.
     */
    private MainActivityInterface mainActivityInterface;

    /**
     * Adattatore per far vedere nella recycler view le prevendite che si vogliono approvare.
     */
    private WPrevenditaPlusAdapter recyclerAdapter = new WPrevenditaPlusAdapter();//Imposto al massimo un elemento per volta.

    /**
     * Usato per la selezione multipla.
     */
    private MultiChoiceHelper multiChoiceHelper;

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

    private Observer<Result<List<WPrevenditaPlus>, Void>> listaPrevenditeResultObserver = new Observer<Result<List<WPrevenditaPlus>, Void>>() {
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
                    recyclerAdapter.add(success);
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);    //Opzione menu
    }

    //ROBA MENU------------------------------------------------------------------------------

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.pr_menu, menu);

        //Rimuovo elemento menu
        menu.removeItem(R.id.fragment_pr_menu_listaPrevenditeItem);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.fragment_pr_menu_aggiungiPrevenditaItem:
                //Ritorno al fragment pr.
                mainActivityInterface.cambiaFragment(mainActivityInterface.getNavFragment(MainActivity.ID_FRAGMENT_PR));
                return true;

            case R.id.fragment_pr_menu_statisticheEventoItem:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //----------------------------------------------------------------------------------


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        uiUtil = new UiUtil(context);
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.subfragment_lista, container, false);

        //Mitico butterknife per fare il collegamento tra XML e oggetti.
        unbinder = ButterKnife.bind(this, view);

        recyclerAdapter.setClickListener(this);

        //Imposto l'helper
        multiChoiceHelper = new MultiChoiceHelper((AppCompatActivity) getActivity(), recyclerAdapter);
        multiChoiceHelper.setMultiChoiceModeListener(new MultiChoiceHelper.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });

        //Imposto il recyler view. Quello che fa vedere le prevendite.
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(recyclerAdapter);

        //View model per richiamare il server.
        viewModel = ViewModelProviders.of(getActivity()).get(PRViewModel.class);
        viewModel.getListaPrevenditeResult().observe(this, this.listaPrevenditeResultObserver);

        label.setText(R.string.subfragment_lista_pr_listaPrevendite_label);
        viewModel.getListaPrevenditeEvento();

        return view;
    }


    @Override
    public void onDestroyView() {
        //Rimuvo il binding di ButterKnife
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Parcelable parcelable = multiChoiceHelper.onSaveInstanceState();
        outState.putParcelable(MULTI_CHOICE_KEY, parcelable);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null){
            Parcelable parcelable = savedInstanceState.getParcelable(MULTI_CHOICE_KEY);
            multiChoiceHelper.onRestoreInstanceState(parcelable);
        }
    }

    @Override
    public void onItemClick(int id, WPrevenditaPlusAdapter.WPrevenditaPlusWrapper obj) {

    }

    @Override
    public void onItemLongClick(int pos, WPrevenditaPlusAdapter.WPrevenditaPlusWrapper obj) {
        multiChoiceHelper.toggleItemChecked(pos, true);
    }
}