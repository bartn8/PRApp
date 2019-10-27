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

package com.prapp.ui.selectstaff;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.prapp.R;
import com.prapp.model.db.wrapper.WStaff;

import org.jetbrains.annotations.NotNull;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

//https://github.com/antedesk/RecyclerViewHTMLit
public class WStaffAdapter extends RecyclerView.Adapter<WStaffAdapter.WStaffViewHolder> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.shortDate();

    /**
     * The interface that receives onClick messages.
     */
    public interface ItemClickListener {
        void onListItemClick(int clickedItemId);
    }

    public class WStaffViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public Integer staffId;

        @BindView(R.id.wstaff_list_item_nome)
        public TextView textViewNome;

        @BindView(R.id.wstaff_list_item_data)
        public TextView textViewData;

        public WStaffViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onListItemClick(staffId);
        }
    }

    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private final ItemClickListener mOnClickListener;
    private Context parentContex;
    private WStaff[] dataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public WStaffAdapter(List<WStaff> listDataset, ItemClickListener listener) {
        dataset = listDataset.toArray(new WStaff[0]);
        mOnClickListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @NotNull
    @Override
    public WStaffViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        parentContex = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(parentContex);
        View view = inflater.inflate(R.layout.wstaff_list_item, parent, false);

        return new WStaffViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(WStaffViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        WStaff wStaff = dataset[position];

        holder.textViewNome.setText(wStaff.getNome());
        holder.textViewData.setText(wStaff.getTimestampCreazione().toString(DATE_FORMATTER));
        holder.staffId = wStaff.getId();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataset.length;
    }


}
