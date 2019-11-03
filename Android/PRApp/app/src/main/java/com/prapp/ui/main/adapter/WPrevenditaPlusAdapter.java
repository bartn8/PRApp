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
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.prapp.R;
import com.prapp.model.db.wrapper.WPrevenditaPlus;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WPrevenditaPlusAdapter extends AbstractAdapter<WPrevenditaPlusAdapter.WPrevenditaPlusWrapper, WPrevenditaPlusAdapter.WPrevenditaPlusViewHolder> {

    private static final String TAG = WPrevenditaPlusAdapter.class.getSimpleName();
    private static final NumberFormat LOCAL_CURRENCY_FORMAT = NumberFormat.getCurrencyInstance();

    public class WPrevenditaPlusWrapper {

        @NotNull
        private WPrevenditaPlus data;

        @Nullable
        private String errore;

        WPrevenditaPlusWrapper(@NotNull WPrevenditaPlus data) {
            this.data = data;
        }

        @NotNull
        public WPrevenditaPlus getData() {
            return data;
        }

        @Nullable
        String getErrore() {
            return errore;
        }

        void setErrore(@Nullable String errore) {
            this.errore = errore;
        }

        boolean isErroreSet() {
            return this.errore != null;
        }

        @Override
        public int hashCode() {
            return data.hashCode();
        }
    }

    /**
     * The interface that receives button clicks.
     */
    public interface ButtonListener {
        void onApprovaClick(WPrevenditaPlusWrapper prevendita);

        void onAnnullaClick(WPrevenditaPlusWrapper prevendita);
    }

    public class WPrevenditaPlusViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private WPrevenditaPlusWrapper reference;
        private int position;

        @BindView(R.id.wprevendita_plus_list_item_nomeEvento)
        public TextView textViewNomeEvento;

        @BindView(R.id.wprevendita_plus_list_item_nomeEvento_label)
        public TextView textViewNomeEventoLabel;

        @BindView(R.id.wprevendita_plus_list_item_nomePR)
        public TextView textViewNomePR;

        @BindView(R.id.wprevendita_plus_list_item_nomePR_label)
        public TextView textViewNomePRLabel;

        @BindView(R.id.wprevendita_plus_list_item_nomeCliente)
        public TextView textViewNomeCliente;

        @BindView(R.id.wprevendita_plus_list_item_nomeCliente_label)
        public TextView textViewNomeClienteLabel;

        @BindView(R.id.wprevendita_plus_list_item_nomeTipoPrevendita)
        public TextView textViewNomeTipoPrevendita;

        @BindView(R.id.wprevendita_plus_list_item_nomeTipoPrevendita_label)
        public TextView textViewNomeTipoPrevenditaLabel;

        @BindView(R.id.wprevendita_plus_list_item_prezzoTipoPrevendita)
        public TextView textViewPrezzoTipoPrevendita;

        @BindView(R.id.wprevendita_plus_list_item_prezzoTipoPrevendita_label)
        public TextView textViewPrezzoTipoPrevenditaLabel;

        @BindView(R.id.wprevendita_plus_list_item_statoPrevendita)
        public TextView textViewStatoPrevendita;

        @BindView(R.id.wprevendita_plus_list_item_statoPrevendita_label)
        public TextView textViewStatoPrevenditaLabel;

        @BindView(R.id.wprevendita_plus_list_item_errore)
        public TextView textViewErrore;

        @BindView(R.id.wprevendita_plus_list_item_errore_label)
        public TextView textViewErroreLabel;

        @BindView(R.id.wprevendita_plus_list_item_approva)
        public Button buttonApprova;

        @BindView(R.id.wprevendita_plus_list_item_annulla)
        public Button buttonAnnulla;

        public WPrevenditaPlusViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.wprevendita_plus_list_item_approva)
        public void onApprovaClick(View view) {
            if (buttonListener != null)
                buttonListener.onApprovaClick(reference);
        }

        @OnClick(R.id.wprevendita_plus_list_item_annulla)
        public void onAnnullaClick(View view) {
            if (buttonListener != null)
                buttonListener.onAnnullaClick(reference);
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

    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private ButtonListener buttonListener;

    //Utilizzato per indicare quanti elementi devo mostare contemporaneamente.
    private int maxShownItems;

    //Indica se mostrare i pulsanti.
    private boolean showButtons;
    //Indica se mostrare l'errore.
    private boolean showError;

    public WPrevenditaPlusAdapter() {
        this(-1, false, false);
    }

    public WPrevenditaPlusAdapter(int maxShownItems, boolean showButtons, boolean showError) {
        super();
        this.maxShownItems = maxShownItems;
        this.showButtons = showButtons;
        this.showError = showError;
    }

    public void setButtonListener(@NotNull ButtonListener buttonListener) {
        this.buttonListener = buttonListener;
    }

    public void add(WPrevenditaPlus wPrevenditaPlus) {
        add(wPrevenditaPlus, null);
    }

    public void add(@NotNull List<WPrevenditaPlus> list) {
        for (WPrevenditaPlus wPrevenditaPlus : list) {
            WPrevenditaPlusWrapper wPrevenditaPlusWrapper = new WPrevenditaPlusWrapper(wPrevenditaPlus);
            wPrevenditaPlusWrapper.setErrore(null);
            add(wPrevenditaPlusWrapper);
        }

        notifyDataSetChanged();
    }

    public void add(WPrevenditaPlus wPrevenditaPlus, String errore) {
        WPrevenditaPlusWrapper wPrevenditaPlusWrapper = new WPrevenditaPlusWrapper(wPrevenditaPlus);
        wPrevenditaPlusWrapper.setErrore(errore);
        add(wPrevenditaPlusWrapper, true);
    }

    @NonNull
    @Override
    public WPrevenditaPlusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        setParentContex(parent.getContext());

        LayoutInflater inflater = LayoutInflater.from(getParentContex());
        View view = inflater.inflate(R.layout.wprevenditaplus_list_item, parent, false);

        return new WPrevenditaPlusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WPrevenditaPlusViewHolder holder, int position) {
        WPrevenditaPlusWrapper prevenditaPlusWrapper = getElement(position);
        WPrevenditaPlus prevenditaPlus = prevenditaPlusWrapper.getData();

        holder.reference = prevenditaPlusWrapper;
        holder.position = position;

        holder.textViewNomeEventoLabel.setText(R.string.wprevendita_plus_list_item_nomeEvento_label);
        holder.textViewNomeEvento.setText(prevenditaPlus.getNomeEvento());

        holder.textViewNomePRLabel.setText(R.string.wprevendita_plus_list_item_nomePR_label);
        String nomePR = getParentContex().getString(R.string.wprevendita_plus_list_item_nomePR_formatted, prevenditaPlus.getNomePR(), prevenditaPlus.getCognomePR());
        holder.textViewNomePR.setText(nomePR);

        if (prevenditaPlus.getNomeCliente() == null || prevenditaPlus.getCognomeCliente() == null) {
            holder.textViewNomeClienteLabel.setText(R.string.wprevendita_plus_list_item_nomeCliente_label);
            holder.textViewNomeCliente.setText(R.string.wprevendita_plus_list_item_nomeCliente);
        } else {
            holder.textViewNomeClienteLabel.setText(R.string.wprevendita_plus_list_item_nomeCliente_label);
            String nomeCliente = getParentContex().getString(R.string.wprevendita_plus_list_item_nomeCliente_formatted, prevenditaPlus.getNomeCliente(), prevenditaPlus.getCognomeCliente());
            holder.textViewNomeCliente.setText(nomeCliente);
        }


        holder.textViewNomeTipoPrevenditaLabel.setText(R.string.wprevendita_plus_list_item_nomeTipoPrevendita_label);
        holder.textViewNomeTipoPrevendita.setText(prevenditaPlus.getNomeTipoPrevendita());

        holder.textViewPrezzoTipoPrevenditaLabel.setText(R.string.wprevendita_plus_list_item_prezzoTipoPrevendita_label);
        holder.textViewPrezzoTipoPrevendita.setText(LOCAL_CURRENCY_FORMAT.format(prevenditaPlus.getPrezzoTipoPrevendita()));

        holder.textViewStatoPrevenditaLabel.setText(R.string.wprevendita_plus_list_item_statoPrevendita_label);
        holder.textViewStatoPrevendita.setText(prevenditaPlus.getStato().getNome());

        if (showError) {
            holder.textViewErroreLabel.setVisibility(View.VISIBLE);
            holder.textViewErrore.setVisibility(View.VISIBLE);

            if (prevenditaPlusWrapper.isErroreSet()) {
                holder.textViewErroreLabel.setText(R.string.wprevendita_plus_list_item_errore_label);
                holder.textViewErrore.setText(prevenditaPlusWrapper.getErrore());
            } else {
                holder.textViewErroreLabel.setText(R.string.wprevendita_plus_list_item_errore_label);
                holder.textViewErrore.setText(R.string.wprevendita_plus_list_item_errore);
            }
        } else {
            holder.textViewErroreLabel.setVisibility(View.GONE);
            holder.textViewErrore.setVisibility(View.GONE);
        }

        //Mostro i pulsanti solo se showButton abilitato
        if (showButtons) {
            holder.buttonApprova.setEnabled(true);
            holder.buttonApprova.setVisibility(View.VISIBLE);
            holder.buttonAnnulla.setEnabled(true);
            holder.buttonAnnulla.setVisibility(View.VISIBLE);
        } else {
            holder.buttonApprova.setEnabled(false);
            holder.buttonApprova.setVisibility(View.GONE);
            holder.buttonAnnulla.setEnabled(false);
            holder.buttonAnnulla.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        int size = getDataset().size();

        if (maxShownItems <= 0)
            return size;
        else
            return size < maxShownItems ? size : maxShownItems;
    }

}


