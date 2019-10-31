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
import com.prapp.model.db.wrapper.WTipoPrevendita;
import com.prapp.ui.utils.ItemClickListener;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WTipoPrevenditaAdapter extends RecyclerView.Adapter<WTipoPrevenditaAdapter.WTipoPrevenditaViewHolder> implements Filterable {

    private static final String TAG = WTipoPrevenditaAdapter.class.getSimpleName();

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormat.shortDateTime();
    private static final NumberFormat LOCAL_CURRENCY_FORMAT = NumberFormat.getCurrencyInstance();

    public class WTipoPrevenditaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public WTipoPrevendita wrapper;

        @BindView(R.id.wtipoprevendita_list_item_nome)
        public TextView nomeTextView;

        @BindView(R.id.wtipoprevendita_list_item_prezzo)
        public TextView prezzoTextView;

        @BindView(R.id.wtipoprevendita_list_item_periodoVendita)
        public TextView periodoVenditaTextView;

        @BindView(R.id.wtipoprevendita_list_item_prezzo_label)
        public TextView prezzoLabelTextView;

        @BindView(R.id.wtipoprevendita_list_item_periodoVendita_label)
        public TextView periodoVenditaLabelTextView;

        public WTipoPrevenditaViewHolder(View itemView) {
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
    private final ItemClickListener<WTipoPrevendita> mOnClickListener;
    private Context parentContex;
    private List<WTipoPrevendita> dataset;
    private List<WTipoPrevendita> datasetFiltered;

    public WTipoPrevenditaAdapter(WTipoPrevendita[] dataset) {
        this(dataset, (clickedItemId,obj) -> {
        });
    }

    public WTipoPrevenditaAdapter(List<WTipoPrevendita> dataset)
    {
        this(dataset, (clickedItemId,obj) -> {
        });
    }

    public WTipoPrevenditaAdapter(List<WTipoPrevendita> dataset, ItemClickListener<WTipoPrevendita> mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
        this.dataset = dataset;
        this.datasetFiltered = dataset;
    }

    public WTipoPrevenditaAdapter(WTipoPrevendita[] dataset, ItemClickListener<WTipoPrevendita> mOnClickListener) {
        this(Arrays.asList(dataset), mOnClickListener);
    }

    public WTipoPrevenditaAdapter(ItemClickListener<WTipoPrevendita> mOnClickListener){
        this(new ArrayList<>(), mOnClickListener);
    }


    public void replace(WTipoPrevendita obj){
        dataset.clear();
        dataset.add(obj);
        datasetFiltered = dataset;
        notifyDataSetChanged();
    }

    public void replace(List<WTipoPrevendita> list){
        dataset = list;
        //Annulla qualsiasi filtro in corso.
        datasetFiltered = list;
        notifyDataSetChanged();
    }

    public void addAll(List<WTipoPrevendita> list){
        if(dataset.addAll(list)){
            //Annulla qualsiasi filtro in corso.
            datasetFiltered = dataset;
            notifyDataSetChanged();
        }
    }

    public void add(WTipoPrevendita obj){
        if(dataset.add(obj)){
            //Annulla qualsiasi filtro in corso.
            datasetFiltered = dataset;
            notifyDataSetChanged();
        }
    }

    public void remove(WTipoPrevendita obj){
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
    public WTipoPrevenditaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        parentContex = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(parentContex);
        View view = inflater.inflate(R.layout.wtipoprevendita_list_item, parent, false);

        return new WTipoPrevenditaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WTipoPrevenditaViewHolder holder, int position) {
        WTipoPrevendita tipoPrevendita = datasetFiltered.get(position);

        holder.wrapper = tipoPrevendita;

        holder.nomeTextView.setText(tipoPrevendita.getNome());

        holder.prezzoLabelTextView.setText(R.string.wtipoprevendita_list_item_prezzo_label);
        holder.prezzoTextView.setText(LOCAL_CURRENCY_FORMAT.format(tipoPrevendita.getPrezzo()));

        String periodo = parentContex.getString(R.string.wtipoprevendita_list_item_periodoVendita, tipoPrevendita.getAperturaPrevendite().toString(DATETIME_FORMATTER), tipoPrevendita.getChiusuraPrevendite().toString(DATETIME_FORMATTER));

        holder.periodoVenditaLabelTextView.setText(R.string.wtipoprevendita_list_item_periodoVendita_label);
        holder.periodoVenditaTextView.setText(periodo);
    }

    @Override
    public int getItemCount() {
        return datasetFiltered.size();
    }


    /**
     * Usato per filtrare i tipo prevendita in base al nome.
     *
     * @return un nuovo filtro con le specifiche nome.
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
                    List<WTipoPrevendita> filteredDataset = new ArrayList<>();

                    for (WTipoPrevendita tipoPrevendita : dataset) {

                        String nome = tipoPrevendita.getNome().toLowerCase();

                        //Ricerca per nome o cognome.
                        if(nome.startsWith(filterText)){
                            filteredDataset.add(tipoPrevendita);
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
                    datasetFiltered = (List<WTipoPrevendita>) filterResults.values;

                    // refresh the list with filtered data
                    notifyDataSetChanged();
                }
            }
        };
    }

}
