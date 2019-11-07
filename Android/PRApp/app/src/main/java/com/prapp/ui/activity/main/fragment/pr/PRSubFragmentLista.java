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

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.Selection;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StableIdKeyProvider;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prapp.R;
import com.prapp.model.db.enums.StatoPrevendita;
import com.prapp.model.db.wrapper.WPrevendita;
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

public class PRSubFragmentLista extends Fragment implements InterfaceHolder<MainActivityInterface>, ItemClickListener<WPrevenditaPlus> {

    private static final String MULTI_CHOICE_KEY = "multiChoiceKey";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
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

    private PopupUtil popupUtil;

    /**
     * Usato per tracciare la multi-selezione
     */
    private SelectionTracker<Long> tracker;

    /**
     * Interfaccia usata per comunicare con l'activity madre.
     */
    private MainActivityInterface mainActivityInterface;

    /**
     * Adattatore per far vedere nella recycler view le prevendite che si vogliono approvare.
     */
    private WPrevenditaPlusAdapter recyclerAdapter;


    @Override
    public void holdInterface(MainActivityInterface mainActivityInterface) {
        this.mainActivityInterface = mainActivityInterface;
    }

    @Override
    public boolean isInterfaceSet() {
        return this.mainActivityInterface != null;
    }

    @BindView(R.id.subfragment_lista_label)
    public TextView label;

    @BindView(R.id.subfragment_lista_recyclerView)
    public RecyclerView recyclerView;

    private Observer<Result<List<WPrevenditaPlus>, Void>> listaPrevenditeResultObserver = result -> {
            if (result == null) {
                return;
            }

            Integer integerError = result.getIntegerError();
            List<Exception> error = result.getError();
            List<WPrevenditaPlus> success = result.getSuccess();

            if (integerError != null)
                uiUtil.showError(integerError);

            else if (error != null)
                uiUtil.showError(error);

            else if (success != null) {
                if (success.isEmpty()) {
                    uiUtil.makeToast(R.string.subfragment_lista_cassiere_lista_vuota_toast);
                } else {
                    //Applico al recycler view i dati.
                    recyclerAdapter.replaceDataset(success);
                }
            }

            popupUtil.hideLoadingPopup();
    };

    private Observer<Result<WPrevendita, WPrevenditaPlus>> modificaPrevenditaResultObserver = result -> {
        if (result == null) {
            return;
        }

        Integer integerError = result.getIntegerError();
        List<Exception> error = result.getError();
        WPrevendita success = result.getSuccess();

        if (integerError != null)
            uiUtil.showError(integerError);

        else if (error != null)
            uiUtil.showError(error);

        else if (success != null){
            String message = getString(R.string.subfragment_lista_pr_modifica_toast, success.getId());
            uiUtil.makeToast(message);

            //Aggiorno la lista
            recyclerAdapter.replaceElement(result.getExtra());
        }

        popupUtil.hideLoadingPopup();
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

        recyclerAdapter = new WPrevenditaPlusAdapter();//Imposto al massimo un elemento per volta.
        recyclerAdapter.setClickListener(this);

        //Imposto il recyler view. Quello che fa vedere le prevendite.
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(recyclerAdapter);

        //Roba per multi selezione
        SelectionTracker.Builder<Long> builder = new SelectionTracker.Builder<Long>("mySelection", recyclerView, new StableIdKeyProvider(recyclerView), new ItemDetailsLookup<Long>() {
            @Nullable
            @Override
            public ItemDetails<Long> getItemDetails(@NonNull MotionEvent e) {
                View childViewUnder = recyclerView.findChildViewUnder(e.getX(), e.getY());

                if (childViewUnder != null) {
                    return ((WPrevenditaPlusAdapter.WPrevenditaPlusViewHolder) recyclerView.getChildViewHolder(childViewUnder)).getItemDetails();
                }
                return null;
            }
        }, StorageStrategy.createLongStorage());

        tracker = builder.build();

        tracker.addObserver(new SelectionTracker.SelectionObserver() {

            private ActionMode actionMode;

            @Override
            public void onSelectionRestored() {
                super.onSelectionRestored();
                actionMode.finish();
                actionMode = null;
            }

            @Override
            public void onSelectionChanged() {
                super.onSelectionChanged();
                Selection<Long> selection = tracker.getSelection();

                if(!selection.isEmpty()){
                    if(actionMode == null){
                        actionMode = getActivity().startActionMode(new ActionMode.Callback() {
                            @Override
                            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                                MenuInflater inflater = actionMode.getMenuInflater();
                                inflater.inflate(R.menu.pr_selection_prevendita_menu, menu);

                                return true;
                            }

                            @Override
                            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                                return false;
                            }

                            @Override
                            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                                if(menuItem.getItemId() == R.id.fragment_pr_selection_prevendita_menu_editItem){
                                    //Faccio aprire il contesto di modifica

                                    AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                                    b.setTitle(R.string.subfragment_lista_pr_dialog_title);
                                    String[] stringValues = StatoPrevendita.stringResValues();

                                    b.setItems(stringValues, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();

                                            StatoPrevendita stato = StatoPrevendita.parseId(i);

                                            //Faccio partire un loading popup
                                            popupUtil.showLoadingPopup();

                                            for(Long selectedId : selection){
                                                WPrevenditaPlus itemById = recyclerAdapter.getItemById(selectedId);
                                                if(itemById != null){
                                                    viewModel.modificaPrevendita(itemById, stato);
                                                }
                                            }

                                            tracker.clearSelection();
                                            actionMode.finish();
                                        }
                                    });

                                    AlertDialog alertDialog = b.create();
                                    alertDialog.show();
                                    return true;
                                }
                                return false;
                            }

                            @Override
                            public void onDestroyActionMode(ActionMode mode) {
                                //Devo annullare la selezione
                                tracker.clearSelection();
                                actionMode = null;
                            }
                        });
                    }
                }else{
                    //Se selezione vuota faccio il finish dell'action mode
                    if(actionMode != null)
                        actionMode.finish();
                }
            }
        });


        recyclerAdapter.setTracker(tracker);

        //View model per richiamare il server.
        viewModel = ViewModelProviders.of(getActivity()).get(PRViewModel.class);
        viewModel.getListaPrevenditeResult().observe(this, this.listaPrevenditeResultObserver);
        viewModel.getModificaPrevenditaResult().observe(this, this.modificaPrevenditaResultObserver);

        label.setText(R.string.subfragment_lista_pr_listaPrevendite_label);
        viewModel.getListaPrevenditeEvento();

        popupUtil.showLoadingPopup();

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