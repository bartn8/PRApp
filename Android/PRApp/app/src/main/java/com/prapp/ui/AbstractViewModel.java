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

package com.prapp.ui;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.Response;
import com.prapp.model.MyContext;
import com.prapp.model.db.wrapper.WDirittiUtente;
import com.prapp.model.db.wrapper.WEvento;
import com.prapp.model.db.wrapper.WStaff;
import com.prapp.model.db.wrapper.WUtente;
import com.prapp.model.net.Eccezione;
import com.prapp.model.net.manager.ManagerAmministratore;
import com.prapp.model.net.manager.ManagerCassiere;
import com.prapp.model.net.manager.ManagerManutenzione;
import com.prapp.model.net.manager.ManagerMembro;
import com.prapp.model.net.manager.ManagerPR;
import com.prapp.model.net.manager.ManagerUtente;
import com.prapp.model.preferences.ApplicationPreferences;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractViewModel extends ViewModel {

    protected class DefaultSuccessListener<T, K> implements Response.Listener<T>
    {
        private MutableLiveData<Result<T, K>> result;

        @Nullable
        private K extra;

        public DefaultSuccessListener(MutableLiveData<Result<T, K>> result)
        {
            this(result, null);
        }

        public DefaultSuccessListener(MutableLiveData<Result<T, K>> result, @Nullable K extra) {
            this.result = result;
            this.extra = extra;
        }

        @Override
        public void onResponse(T response) {
            Result<T, K> result = new Result<>(response, null);
            result.setExtra(extra);
            this.result.setValue(result);
        }
    }

    protected class DefaultExceptionListener<T,K> implements Response.Listener<List<Eccezione>>
    {
        private MutableLiveData<Result<T,K>> result;

        @Nullable
        private K extra;

        public DefaultExceptionListener(MutableLiveData<Result<T,K>> result) {
            this(result, null);
        }

        public DefaultExceptionListener(MutableLiveData<Result<T,K>> result, @Nullable K extra) {
            this.result = result;
            this.extra = extra;
        }

        @Override
        public void onResponse(List<Eccezione> response) {
            Result<T, K> result = new Result<>(null, Eccezione.convertiInExceptions(response));
            result.setExtra(extra);
            this.result.setValue(result);
        }
    }

    @NotNull
    private Context context;
    private MyContext myContext;
    private ApplicationPreferences preferences;

    private ManagerUtente managerUtente;
    private ManagerMembro managerMembro;
    private ManagerPR managerPR;
    private ManagerCassiere managerCassiere;
    private ManagerAmministratore managerAmministratore;
    private ManagerManutenzione managerManutenzione;

    public AbstractViewModel(@NotNull Context context) {
        this.context = context;
        this.myContext = MyContext.getSingleton();
        this.preferences = ApplicationPreferences.getInstance(context);

        this.managerUtente = ManagerUtente.getInstance(context);
        this.managerMembro = ManagerMembro.getInstance(context);
        this.managerPR = ManagerPR.getInstance(context);
        this.managerCassiere = ManagerCassiere.getInstance(context);
        this.managerAmministratore = ManagerAmministratore.getInstance(context);
        this.managerManutenzione = ManagerManutenzione.getInstance(context);
    }

    public WDirittiUtente getDirittiUtente() {
        return myContext.getDirittiUtente();
    }

    public WStaff getStaff() {
        return myContext.getStaff();
    }

    public boolean isStaffScelto() {
        return myContext.isStaffScelto();
    }

    public boolean isLoggato() {
        return myContext.isLoggato();
    }

    public WUtente getUtente() {
        return myContext.getUtente();
    }

    public WEvento getEvento() {
        return myContext.getEvento();
    }

    public void setEvento(WEvento evento) {
        myContext.setEvento(evento);
    }

    public boolean isEventoScelto() {
        return myContext.isEventoScelto();
    }

    protected Context getContext() {
        return context;
    }

    protected MyContext getMyContext() {
        return myContext;
    }

    protected ApplicationPreferences getPreferences() {
        return preferences;
    }

    protected ManagerUtente getManagerUtente() {
        return managerUtente;
    }

    protected ManagerMembro getManagerMembro() {
        return managerMembro;
    }

    protected ManagerPR getManagerPR() {
        return managerPR;
    }

    protected ManagerCassiere getManagerCassiere() {
        return managerCassiere;
    }

    protected ManagerAmministratore getManagerAmministratore() {
        return managerAmministratore;
    }

    protected ManagerManutenzione getManagerManutenzione() {
        return managerManutenzione;
    }
}
