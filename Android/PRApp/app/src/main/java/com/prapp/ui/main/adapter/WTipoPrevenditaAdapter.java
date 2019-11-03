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

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WTipoPrevenditaAdapter extends AbstractAdapter<WTipoPrevendita, WTipoPrevenditaAdapter.WTipoPrevenditaViewHolder> implements Filterable {

    private static final String TAG = WTipoPrevenditaAdapter.class.getSimpleName();

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormat.shortDateTime();
    private static final NumberFormat LOCAL_CURRENCY_FORMAT = NumberFormat.getCurrencyInstance();

    public class WTipoPrevenditaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public WTipoPrevendita reference;
        public int position;

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
            onClickImpl(view, position, reference);
        }

        @Override
        public boolean onLongClick(View view) {
            return onLongClickImpl(view, position, reference);
        }
    }


    @NonNull
    @Override
    public WTipoPrevenditaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        setParentContex(parent.getContext());

        LayoutInflater inflater = LayoutInflater.from(getParentContex());
        View view = inflater.inflate(R.layout.wtipoprevendita_list_item, parent, false);

        return new WTipoPrevenditaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WTipoPrevenditaViewHolder holder, int position) {
        WTipoPrevendita tipoPrevendita = getElement(position);

        holder.reference = tipoPrevendita;
        holder.position = position;

        holder.nomeTextView.setText(tipoPrevendita.getNome());

        holder.prezzoLabelTextView.setText(R.string.wtipoprevendita_list_item_prezzo_label);
        holder.prezzoTextView.setText(LOCAL_CURRENCY_FORMAT.format(tipoPrevendita.getPrezzo()));

        String periodo = getParentContex().getString(R.string.wtipoprevendita_list_item_periodoVendita, tipoPrevendita.getAperturaPrevendite().toString(DATETIME_FORMATTER), tipoPrevendita.getChiusuraPrevendite().toString(DATETIME_FORMATTER));

        holder.periodoVenditaLabelTextView.setText(R.string.wtipoprevendita_list_item_periodoVendita_label);
        holder.periodoVenditaTextView.setText(periodo);
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
                    restoreFilteredDataset();
                } else {
                    //Qui metto la lista che soddisfa il filtro.
                    List<WTipoPrevendita> filteredDataset = new ArrayList<>();

                    for (WTipoPrevendita tipoPrevendita : getOriginalDataset()) {

                        String nome = tipoPrevendita.getNome().toLowerCase();

                        //Ricerca per nome o cognome.
                        if (nome.startsWith(filterText)) {
                            filteredDataset.add(tipoPrevendita);
                        }

                    }

                    setDatasetFiltered(filteredDataset);
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = getDataset();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (filterResults.values instanceof List) {
                    setDatasetFiltered((List<WTipoPrevendita>) filterResults.values);

                    // refresh the list with filtered data
                    notifyDataSetChanged();
                }
            }
        };
    }

}
