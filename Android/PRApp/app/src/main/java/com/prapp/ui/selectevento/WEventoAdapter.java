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

package com.prapp.ui.selectevento;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prapp.R;
import com.prapp.model.db.wrapper.WEvento;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WEventoAdapter extends RecyclerView.Adapter<WEventoAdapter.WEventoViewHolder> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.shortDateTime();

    /**
     * The interface that receives onClick messages.
     */
    public interface ItemClickListener {
        void onListItemClick(int clickedItemId);
    }

    public class WEventoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public Integer eventoId;

        @BindView(R.id.wevento_list_item_nome)
        public TextView textViewNome;

        @BindView(R.id.wevento_list_item_descrizione)
        public TextView textViewDescrizione;

        @BindView(R.id.wevento_list_item_periodo)
        public TextView textViewPeriodo;

        public WEventoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onListItemClick(eventoId);
        }
    }

    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private final WEventoAdapter.ItemClickListener mOnClickListener;
    private Context parentContex;
    private WEvento[] dataset;

    public WEventoAdapter(WEvento[] dataset) {
        this(dataset, new WEventoAdapter.ItemClickListener() {
            @Override
            public void onListItemClick(int clickedItemId) {

            }
        });
    }

    public WEventoAdapter(List<WEvento> dataset)
    {
        this(dataset, new WEventoAdapter.ItemClickListener() {
            @Override
            public void onListItemClick(int clickedItemId) {

            }
        });
    }

    public WEventoAdapter(List<WEvento> dataset, WEventoAdapter.ItemClickListener mOnClickListener) {
        this(dataset.toArray(new WEvento[0]), mOnClickListener);
    }

    public WEventoAdapter(WEvento[] dataset, WEventoAdapter.ItemClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
        this.dataset = dataset;
    }

    @NonNull
    @Override
    public WEventoAdapter.WEventoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        parentContex = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(parentContex);
        View view = inflater.inflate(R.layout.wevento_list_item, parent, false);

        return new WEventoAdapter.WEventoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WEventoAdapter.WEventoViewHolder holder, int position) {
        WEvento evento = dataset[position];

        holder.textViewNome.setText(evento.getNome());
        holder.textViewDescrizione.setText(evento.getDescrizione());
        holder.textViewPeriodo.setText(evento.getInizio().toString(DATE_FORMATTER) + " - " + evento.getFine().toString(DATE_FORMATTER));

        holder.eventoId = evento.getId();
    }

    @Override
    public int getItemCount() {
        return dataset.length;
    }

}


