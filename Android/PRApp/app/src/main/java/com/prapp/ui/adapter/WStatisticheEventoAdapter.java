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

package com.prapp.ui.adapter;

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

public class WStatisticheEventoAdapter extends AbstractAdapter<WStatisticheEvento, WStatisticheEventoAdapter.WStatisticheEventoViewHolder> {

    private static final String TAG = WStatisticheEventoAdapter.class.getSimpleName();
    private static final NumberFormat LOCAL_CURRENCY_FORMAT = NumberFormat.getCurrencyInstance();
    private static final NumberFormat LOCAL_NUMBER_FORMAT = NumberFormat.getInstance();

    public class WStatisticheEventoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public WStatisticheEvento reference;
        public int position;

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

        @BindView(R.id.wstatisticheevento_list_item_prevenditeEntrate_label)
        public TextView textViewPrevenditeEntrateLabel;

        @BindView(R.id.wstatisticheevento_list_item_prevenditeEntrate)
        public TextView textViewPrevenditeEntrate;

        @BindView(R.id.wstatisticheevento_list_item_prevenditeNonEntrate_label)
        public TextView textViewPrevenditeNonEntrateLabel;

        @BindView(R.id.wstatisticheevento_list_item_prevenditeNonEntrate)
        public TextView textViewPrevenditeNonEntrate;

        public WStatisticheEventoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
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

    public WStatisticheEventoAdapter() {
    }

    public WStatisticheEventoAdapter(List<WStatisticheEvento> dataset) {
        super(dataset);
    }

    @NonNull
    @Override
    public WStatisticheEventoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        setParentContex(parent.getContext());

        LayoutInflater inflater = LayoutInflater.from(getParentContex());
        View view = inflater.inflate(R.layout.wstatisticheevento_list_item, parent, false);

        return new WStatisticheEventoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WStatisticheEventoViewHolder holder, int position) {
        WStatisticheEvento statisticheEvento = getElement(position);

        holder.textViewNomeTipoPrevenditaLabel.setText(R.string.wstatisticheevento_list_item_nomeTipoPrevendita_label);
        holder.textViewNomeTipoPrevendita.setText(statisticheEvento.getNomeTipoPrevendita());

        holder.textViewPrevenditeVenduteLabel.setText(R.string.wstatisticheevento_list_item_prevenditeVendute_label);
        holder.textViewPrevenditeVendute.setText(LOCAL_NUMBER_FORMAT.format(statisticheEvento.getPrevenditeVendute()));

        holder.textViewRicavoLabel.setText(R.string.wstatisticheevento_list_item_ricavo_label);
        holder.textViewRicavo.setText(LOCAL_CURRENCY_FORMAT.format(statisticheEvento.getRicavo()));

        holder.textViewPrevenditeEntrateLabel.setText(R.string.wstatisticheevento_list_item_prevenditeEntrate_label);
        holder.textViewPrevenditeEntrate.setText(LOCAL_NUMBER_FORMAT.format(statisticheEvento.getPrevenditeEntrate()));

        holder.textViewPrevenditeNonEntrateLabel.setText(R.string.wstatisticheevento_list_item_prevenditeNonEntrate_label);
        holder.textViewPrevenditeNonEntrate.setText(LOCAL_NUMBER_FORMAT.format(statisticheEvento.getPrevenditeNonEntrate()));

        holder.position = position;
        holder.reference = statisticheEvento;
    }

}
