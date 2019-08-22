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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prapp.R;
import com.prapp.model.db.wrapper.WStatisticheEvento;

import java.text.NumberFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WStatisticheEventoAdapter extends RecyclerView.Adapter<WStatisticheEventoAdapter.WStatisticheEventoViewHolder> {

    private static final String TAG = WStatisticheEventoAdapter.class.getSimpleName();
    private static final NumberFormat LOCAL_CURRENCY_FORMAT = NumberFormat.getCurrencyInstance();
    private static final NumberFormat LOCAL_NUMBER_FORMAT = NumberFormat.getInstance();

    public class WStatisticheEventoViewHolder extends RecyclerView.ViewHolder {

        public WStatisticheEvento statisticheEvento;

        @BindView(R.id.wstatisticheevento_list_item_nomeTipoPrevendita_label)
        public TextView textViewNomeTipoPrevenditaLabel;

        @BindView(R.id.wstatisticheevento_list_item_nomeTipoPrevendita)
        public TextView textViewNomeTipoPrevendita;

        @BindView(R.id.wstatisticheevento_list_item_prevenditeVendute_label)
        public TextView textViewPrevenditeVenduteLabel;

        @BindView(R.id.wstatisticheevento_list_item_prevenditeVendute)
        public TextView textViewPrevenditeVendute;

        @BindView(R.id.wstatisticheevento_list_item_ricavo_label)
        public TextView textViewRicavoLabel;

        @BindView(R.id.wstatisticheevento_list_item_ricavo)
        public TextView textViewRicavo;

        public WStatisticheEventoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    private Context parentContex;
    private WStatisticheEvento[] dataset;


    public WStatisticheEventoAdapter(List<WStatisticheEvento> dataset) {
        this(dataset.toArray(new WStatisticheEvento[0]));
    }

    public WStatisticheEventoAdapter(WStatisticheEvento[] dataset) {
        this.dataset = dataset;
    }

    @NonNull
    @Override
    public WStatisticheEventoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        parentContex = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(parentContex);
        View view = inflater.inflate(R.layout.wstatisticheevento_list_item, parent, false);

        return new WStatisticheEventoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WStatisticheEventoViewHolder holder, int position) {
        WStatisticheEvento statisticheEvento = dataset[position];

        holder.textViewNomeTipoPrevenditaLabel.setText(R.string.wstatisticheevento_list_item_nomeTipoPrevendita_label);
        holder.textViewNomeTipoPrevendita.setText(statisticheEvento.getNomeTipoPrevendita());

        holder.textViewPrevenditeVenduteLabel.setText(R.string.wstatisticheevento_list_item_prevenditeVendute_label);
        holder.textViewPrevenditeVendute.setText(LOCAL_NUMBER_FORMAT.format(statisticheEvento.getPrevenditeVendute()));

        holder.textViewRicavoLabel.setText(R.string.wstatisticheevento_list_item_ricavo_label);
        holder.textViewRicavo.setText(LOCAL_CURRENCY_FORMAT.format(statisticheEvento.getRicavo()));
    }

    @Override
    public int getItemCount() {
        return dataset.length;
    }

}
