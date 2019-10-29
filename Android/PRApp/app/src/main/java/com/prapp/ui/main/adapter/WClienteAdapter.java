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

package com.prapp.ui.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prapp.R;
import com.prapp.model.db.wrapper.WCliente;
import com.prapp.ui.utils.ItemClickListener;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

//https://www.androidhive.info/2017/11/android-recyclerview-with-search-filter-functionality/

public class WClienteAdapter extends RecyclerView.Adapter<WClienteAdapter.WClienteViewHolder> implements Filterable {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.mediumDate();

    public class WClienteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public WCliente wrapper;

        @BindView(R.id.wcliente_list_item_nome)
        public TextView textViewNome;

        @BindView(R.id.wcliente_list_item_cognome)
        public TextView textViewCognome;

        @BindView(R.id.wcliente_list_item_dataDiNascita)
        public TextView textViewDataDiNascita;

        public WClienteViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onItemClick(wrapper.getId(), wrapper);
        }
    }

    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private final ItemClickListener<WCliente> mOnClickListener;
    private Context parentContex;
    private List<WCliente> dataset;
    private List<WCliente> datasetFiltered;


    public WClienteAdapter(WCliente[] dataset) {
        this(dataset, (clickedItemId,obj) -> {
        });
    }

    public WClienteAdapter(List<WCliente> dataset)
    {
        this(dataset, (clickedItemId,obj) -> {
        });
    }

    public WClienteAdapter(List<WCliente> dataset, ItemClickListener<WCliente> mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
        this.dataset = dataset;
        this.datasetFiltered = dataset;
    }

    public WClienteAdapter(WCliente[] dataset, ItemClickListener<WCliente> mOnClickListener) {
        this(Arrays.asList(dataset), mOnClickListener);
    }

    public WClienteAdapter(ItemClickListener<WCliente> mOnClickListener){
        this(new ArrayList<>(), mOnClickListener);
    }

    public void replace(WCliente obj){
        dataset.clear();
        dataset.add(obj);
        datasetFiltered = dataset;
        notifyDataSetChanged();
    }

    public void replace(List<WCliente> list){
        dataset = list;
        //Annulla qualsiasi filtro in corso.
        datasetFiltered = list;
        notifyDataSetChanged();
    }

    public void addAll(List<WCliente> list){
        if(dataset.addAll(list)){
            //Annulla qualsiasi filtro in corso.
            datasetFiltered = dataset;
            notifyDataSetChanged();
        }
    }

    public void add(WCliente obj){
        if(dataset.add(obj)){
            //Annulla qualsiasi filtro in corso.
            datasetFiltered = dataset;
            notifyDataSetChanged();
        }
    }

    public void remove(WCliente obj){
        if(dataset.remove(obj)){
            //Annulla qualsiasi filtro in corso.
            datasetFiltered = dataset;
            notifyDataSetChanged();
        }
    }

    public void remove(int position){
        int size = dataset.size();
        if(position >= 0 && position < size){
            //Annulla qualsiasi filtro in corso.
            dataset.remove(position);
            datasetFiltered = dataset;
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public WClienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        parentContex = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(parentContex);
        View view = inflater.inflate(R.layout.wcliente_list_item, parent, false);

        return new WClienteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WClienteViewHolder holder, int position) {
        WCliente cliente = datasetFiltered.get(position);

        holder.wrapper = cliente;

        holder.textViewNome.setText(cliente.getNome());
        holder.textViewCognome.setText(cliente.getCognome());

        if(cliente.isDataDiNascitaPresent()){
            holder.textViewDataDiNascita.setText(cliente.getDataDiNascita().toString(DATE_FORMATTER));
        }else{
            holder.textViewDataDiNascita.setText(R.string.wcliente_list_item_dataDiNascita);
        }
    }

    @Override
    public int getItemCount() {
        return datasetFiltered.size();
    }

    /**
     * Usato per filtrare i clienti in base al nome e al cognome.
     *
     * @return un nuovo filtro con le specifiche nome cognome.
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String filterText = charSequence.toString().toLowerCase();

                if (filterText.isEmpty()) {
                    datasetFiltered = dataset;
                } else {
                    //Qui metto la lista che soddisfa il filtro.
                    List<WCliente> filteredDataset = new ArrayList<>();

                    for (WCliente cliente : dataset) {

                        String nome = cliente.getNome().toLowerCase();
                        String cognome = cliente.getCognome().toLowerCase();

                        //Ricerca per nome o cognome.
                        if(nome.startsWith(filterText) || cognome.startsWith(filterText)){
                            filteredDataset.add(cliente);
                        }

                    }

                    datasetFiltered = filteredDataset;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = datasetFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if(filterResults.values instanceof List){
                    datasetFiltered = (List<WCliente>) filterResults.values;

                    // refresh the list with filtered data
                    notifyDataSetChanged();
                }
            }
        };
    }

}
