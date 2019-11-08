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
import com.prapp.model.db.wrapper.WUtente;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WUtenteAdapter extends AbstractAdapter<WUtente, WUtenteAdapter.WUtenteViewHolder> {

    public class WUtenteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public WUtente reference;
        public int position;

        @BindView(R.id.wutente_list_item_nome)
        public TextView textViewNome;

        @BindView(R.id.wutente_list_item_cognome)
        public TextView textViewCognome;

        @BindView(R.id.wutente_list_item_telefono)
        public TextView textViewTelefono;

        public WUtenteViewHolder(View itemView) {
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

    public WUtenteAdapter() {
    }

    public WUtenteAdapter(List<WUtente> dataset) {
        super(dataset);
    }

    @NonNull
    @Override
    public WUtenteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        setParentContex(parent.getContext());

        LayoutInflater inflater = LayoutInflater.from(getParentContex());
        View view = inflater.inflate(R.layout.wutente_list_item, parent, false);

        return new WUtenteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WUtenteViewHolder holder, int position) {
        WUtente utente = getElement(position);

        holder.position = position;
        holder.reference = utente;

        holder.textViewNome.setText(utente.getNome());
        holder.textViewCognome.setText(utente.getCognome());
        holder.textViewTelefono.setText(utente.getTelefono());
    }

}


