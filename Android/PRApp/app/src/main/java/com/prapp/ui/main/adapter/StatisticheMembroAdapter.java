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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prapp.R;
import com.prapp.model.db.wrapper.WStatisticheCassiereEvento;
import com.prapp.model.db.wrapper.WStatistichePREvento;
import com.prapp.model.db.wrapper.WUtente;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StatisticheMembroAdapter extends AbstractAdapter<StatisticheMembroAdapter.StatisticheMembroWrapper, StatisticheMembroAdapter.StatisticheMembroViewHolder> {

    private static final String TAG = StatisticheMembroAdapter.class.getSimpleName();
    private static final NumberFormat LOCAL_CURRENCY_FORMAT = NumberFormat.getCurrencyInstance();
    private static final NumberFormat LOCAL_NUMBER_FORMAT = NumberFormat.getInstance();

    public class StatisticheMembroWrapper {

        @NotNull
        private WUtente membro;

        @Nullable
        private List<WStatistichePREvento> statistichePR;

        @Nullable
        private WStatisticheCassiereEvento statisticheCassiere;

        public StatisticheMembroWrapper(@NotNull WUtente membro) {
            this(membro, null, null);
        }

        public StatisticheMembroWrapper(@NotNull WUtente membro, @Nullable List<WStatistichePREvento> statistichePR, @Nullable WStatisticheCassiereEvento statisticheCassiere) {
            this.membro = membro;
            this.statistichePR = statistichePR;
            this.statisticheCassiere = statisticheCassiere;
        }

        public void setStatistichePR(@Nullable List<WStatistichePREvento> statistichePR) {
            this.statistichePR = statistichePR;
        }

        public void setStatisticheCassiere(@Nullable WStatisticheCassiereEvento statisticheCassiere) {
            this.statisticheCassiere = statisticheCassiere;
        }

        @NotNull
        public WUtente getMembro() {
            return membro;
        }

        @Nullable
        public List<WStatistichePREvento> getStatistichePR() {
            return statistichePR;
        }

        @Nullable
        public WStatisticheCassiereEvento getStatisticheCassiere() {
            return statisticheCassiere;
        }

        @Override
        public int hashCode() {
            return membro.getId();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof StatisticheMembroWrapper))
                return false;

            StatisticheMembroWrapper wrapper = (StatisticheMembroWrapper) obj;
            return getMembro().getId().intValue() == wrapper.getMembro().getId().intValue();
        }
    }

    public class StatisticheMembroViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private StatisticheMembroWrapper reference;
        private int position;

        @BindView(R.id.statistichemembro_list_item_nomeMembro)
        public TextView textViewNomeMembro;

        @BindView(R.id.statistichemembro_list_item_cognomeMembro)
        public TextView textViewCognomeMembro;

        @BindView(R.id.statistichemembro_list_item_label_cassiere)
        public TextView textViewLabelCassiere;

        @BindView(R.id.statistichemembro_list_item_label_entrateCassiere)
        public TextView textViewLabelEntrateCassiere;

        @BindView(R.id.statistichemembro_list_item_entrateCassiere)
        public TextView textViewEntrateCassiere;

        @BindView(R.id.statistichemembro_list_item_label_pr)
        public TextView textViewLabelPR;

        @BindView(R.id.statistichePRRecyclerView)
        public RecyclerView recyclerViewStatistichePR;

        public StatisticheMembroViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
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

    public StatisticheMembroAdapter() {
        super();
    }

    public StatisticheMembroAdapter(List<StatisticheMembroWrapper> dataset) {
        super(dataset);
    }

    public void addMembri(List<WUtente> membri){
        for (WUtente membro: membri){
            StatisticheMembroWrapper wrapper = new StatisticheMembroWrapper(membro);
            add(wrapper, false);
        }

        restoreFilteredDataset();
        notifyDataSetChanged();
    }

    public void addStatistichePR(Integer idUtente, List<WStatistichePREvento> stat) {
        for (StatisticheMembroWrapper wrapper : getDataset()) {
            if (wrapper.getMembro().getId().intValue() == idUtente.intValue()) {
                wrapper.setStatistichePR(stat);
            }
        }

        //Annulla qualsiasi filtro in corso.
        restoreFilteredDataset();
        notifyDataSetChanged();
    }

    public void addStatisticheCassiere(Integer idUtente, WStatisticheCassiereEvento stat) {
        for (StatisticheMembroWrapper wrapper : getDataset()) {
            if (wrapper.getMembro().getId().intValue() == idUtente.intValue()) {
                wrapper.setStatisticheCassiere(stat);
            }
        }

        //Annulla qualsiasi filtro in corso.
        restoreFilteredDataset();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StatisticheMembroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        setParentContex(parent.getContext());

        LayoutInflater inflater = LayoutInflater.from(getParentContex());
        View view = inflater.inflate(R.layout.statistichemembro_list_item, parent, false);

        return new StatisticheMembroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatisticheMembroViewHolder holder, int position) {
        StatisticheMembroWrapper wrapper = getElement(position);
        WUtente membro = wrapper.getMembro();

        holder.position = position;
        holder.reference = wrapper;

        holder.textViewNomeMembro.setText(membro.getNome());
        holder.textViewCognomeMembro.setText(membro.getCognome());

        WStatisticheCassiereEvento statisticheCassiere = wrapper.getStatisticheCassiere();

        //Bug Risolto:
        //Non c'era l'else: la view veniva riciclata e veniva lascita ciò che c'era prima.
        //https://stackoverflow.com/questions/42688223/recycler-view-showing-wrong-data-when-scrolled-fast-have-added-images-for-the-sa
        if (statisticheCassiere != null) {
            holder.textViewLabelCassiere.setText(R.string.statistichemembro_list_item_label_cassiere);
            holder.textViewLabelEntrateCassiere.setText(R.string.statistichemembro_list_item_label_entrateCassiere);
            holder.textViewEntrateCassiere.setText(LOCAL_NUMBER_FORMAT.format(statisticheCassiere.getEntrate()));
        } else {
            holder.textViewLabelCassiere.setText(R.string.empty_string);
            holder.textViewLabelEntrateCassiere.setText(R.string.empty_string);
            holder.textViewEntrateCassiere.setText(R.string.empty_string);
        }

        List<WStatistichePREvento> statistichePR = wrapper.getStatistichePR();


        //Bug Risolto:
        //Non c'era l'else: la view veniva riciclata e veniva lascita ciò che c'era prima.
        //https://stackoverflow.com/questions/42688223/recycler-view-showing-wrong-data-when-scrolled-fast-have-added-images-for-the-sa

        holder.recyclerViewStatistichePR.setNestedScrollingEnabled(false);
        holder.recyclerViewStatistichePR.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getParentContex(), LinearLayoutManager.HORIZONTAL, false);
        holder.recyclerViewStatistichePR.setLayoutManager(linearLayoutManager);

        if (statistichePR != null) {
            if (!statistichePR.isEmpty()) {
                holder.textViewLabelPR.setText(R.string.statistichemembro_list_item_label_pr);
                holder.recyclerViewStatistichePR.setAdapter(new StatistichePRAdapter(statistichePR));
            } else {
                holder.textViewLabelPR.setText(R.string.empty_string);
                holder.recyclerViewStatistichePR.setAdapter(new StatistichePRAdapter(new ArrayList<>()));
            }
        } else {
            holder.textViewLabelPR.setText(R.string.empty_string);
            holder.recyclerViewStatistichePR.setAdapter(new StatistichePRAdapter(new ArrayList<>()));
        }

    }

}
