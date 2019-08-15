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
import com.prapp.model.db.wrapper.WStatistichePREvento;

import java.text.NumberFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StatistichePRAdapter extends RecyclerView.Adapter<StatistichePRAdapter.StatistichePRViewHolder> {

    private static final String TAG = StatistichePRAdapter.class.getSimpleName();
    private static final NumberFormat LOCAL_CURRENCY_FORMAT = NumberFormat.getCurrencyInstance();
    private static final NumberFormat LOCAL_NUMBER_FORMAT = NumberFormat.getInstance();

    public class StatistichePRViewHolder extends RecyclerView.ViewHolder {

        //private StatisticheMembroWrapper reference;
        //private int position;

        @BindView(R.id.statistichemembro_sub_list_pr_item_label_tipoPrevendita)
        public TextView textViewLabelTipoPrevendita;

        @BindView(R.id.statistichemembro_sub_list_pr_item_tipoPrevendita)
        public TextView textViewTipoPrevendita;

        @BindView(R.id.statistichemembro_sub_list_pr_item_label_prevenditeVendute)
        public TextView textViewLabelPrevenditeVendute;

        @BindView(R.id.statistichemembro_sub_list_pr_item_prevenditeVendute)
        public TextView textViewPrevenditeVendute;

        @BindView(R.id.statistichemembro_sub_list_pr_item_label_ricavo)
        public TextView textViewLabelRicavo;

        @BindView(R.id.statistichemembro_sub_list_pr_item_ricavo)
        public TextView textViewRicavo;

        public StatistichePRViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    private List<WStatistichePREvento> dataset;
    private Context parentContex;


    public StatistichePRAdapter(List<WStatistichePREvento> dataset) {
        this.dataset = dataset;
    }

    @NonNull
    @Override
    public StatistichePRViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        parentContex = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(parentContex);
        View view = inflater.inflate(R.layout.statistichemembro_sub_list_pr_item, parent, false);

        return new StatistichePRViewHolder(view);
    }

    public void setDataset(List<WStatistichePREvento> dataset)
    {
        this.dataset = dataset;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull StatistichePRViewHolder holder, int position) {
        WStatistichePREvento statistichePREvento = dataset.get(position);

        holder.textViewLabelPrevenditeVendute.setText(R.string.statistichemembro_sub_list_pr_item_label_prevenditeVendute);
        holder.textViewLabelRicavo.setText(R.string.statistichemembro_sub_list_pr_item_label_ricavo);
        holder.textViewLabelTipoPrevendita.setText(R.string.statistichemembro_sub_list_pr_item_label_tipoPrevendita);

        holder.textViewRicavo.setText(LOCAL_CURRENCY_FORMAT.format(statistichePREvento.getRicavo()));
        holder.textViewTipoPrevendita.setText(statistichePREvento.getNomeTipoPrevendita());
        holder.textViewPrevenditeVendute.setText(LOCAL_NUMBER_FORMAT.format(statistichePREvento.getPrevenditeVendute()));
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

}
