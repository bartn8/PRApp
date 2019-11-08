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

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.prapp.ui.utils.ItemClickListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAdapter<T, K extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<K> {

    private ItemClickListener<T> clickListener;
    private List<T> dataset;
    private List<T> datasetFiltered;
    private Context parentContex;

    public AbstractAdapter(){
        this(new ArrayList<>());
    }

    public AbstractAdapter(List<T> dataset) {
        this.dataset = dataset;
        this.datasetFiltered = dataset;
    }

    protected Context getParentContex() {
        return parentContex;
    }

    protected void setParentContex(Context parentContex) {
        this.parentContex = parentContex;
    }

    protected List<T> getOriginalDataset(){
        return dataset;
    }

    protected List<T> getDataset(){
        return datasetFiltered;
    }

    protected void setDatasetFiltered(List<T> datasetFiltered) {
        this.datasetFiltered = datasetFiltered;
    }

    protected T getElement(int position){
        return datasetFiltered.get(position);
    }

    protected void restoreFilteredDataset(){
        this.datasetFiltered = dataset;
    }

    protected ItemClickListener<T> getClickListener(){
        return clickListener;
    }

    protected void onClickImpl(View view, int position, T reference){
        if(clickListener != null) {
            clickListener.onItemClick(position, reference);
        }
    }

    protected boolean onLongClickImpl(View view, int position, T reference) {
        if(clickListener != null){
            clickListener.onItemLongClick(position, reference);
            return true;
        }
        return false;
    }

    public void setClickListener(@NotNull ItemClickListener<T> clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public long getItemId(int position) {
        return (long)position;
    }

    public T getItemById(long id){
        return getElement((int)id);
    }

    public void replaceElement(T obj){
        if(dataset.remove(obj)){
            dataset.add(obj);
            datasetFiltered = dataset;
            notifyDataSetChanged();
        }
    }

    public void replaceDataset(T obj){
        dataset.clear();
        dataset.add(obj);
        datasetFiltered = dataset;
        notifyDataSetChanged();
    }

    public void replaceDataset(List<T> list){
        dataset = list;
        //Annulla qualsiasi filtro in corso.
        datasetFiltered = list;
        notifyDataSetChanged();
    }

    public void addAll(List<T> list){
        if(dataset.addAll(list)){
            //Annulla qualsiasi filtro in corso.
            datasetFiltered = dataset;
            notifyDataSetChanged();
        }
    }

    public void add(T obj){
        add(obj, true);
    }

    public void add(T obj, boolean notify){
        if(dataset.add(obj)){
            //Annulla qualsiasi filtro in corso.
            datasetFiltered = dataset;
            if(notify) notifyDataSetChanged();
        }
    }

    public void remove(T obj){
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

    @Override
    public int getItemCount() {
        return datasetFiltered.size();
    }

}
