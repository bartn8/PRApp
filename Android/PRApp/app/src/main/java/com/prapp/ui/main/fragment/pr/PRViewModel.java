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

package com.prapp.ui.main.fragment.pr;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.prapp.R;
import com.prapp.model.MyContext;
import com.prapp.model.db.wrapper.WCliente;
import com.prapp.model.db.wrapper.WTipoPrevendita;
import com.prapp.model.net.manager.ManagerMembro;
import com.prapp.model.net.manager.ManagerPR;
import com.prapp.model.net.wrapper.insert.InsertNetWCliente;
import com.prapp.ui.AbstractViewModel;
import com.prapp.ui.Result;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

public class PRViewModel extends AbstractViewModel {

    public static final String TAG = PRViewModel.class.getSimpleName();

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.fullDate();

    public static final int CLIENTE_SEARCH_MODE = 0;
    public static final int CLIENTE_ADD_MODE = 1;
    public static final int CLIENTE_SELECT_MODE = 2;

    public static final int PREVENDITA_SELECT_MODE = 0;
    public static final int PREVENDITA_NOT_SELECT_MODE = 1;
    public static final int PREVENDITA_SEARCH_MODE = 1;

    //Roba di controlli
    private MutableLiveData<AggiungiClienteState> aggiungiClienteState = new MutableLiveData<>();

    public LiveData<AggiungiClienteState> getAggiungiClienteState() {
        return aggiungiClienteState;
    }

    private MutableLiveData<Integer> clienteMode = new MutableLiveData<>(CLIENTE_ADD_MODE);

    public int getClienteMode() {
        Integer value = clienteMode.getValue();
        return value == null ? -1 : value;
    }

    public LiveData<Integer> getClienteModeLiveData() {
        return clienteMode;
    }

    public void setClienteMode(int clienteMode) {
        this.clienteMode.setValue(clienteMode);
    }

    private MutableLiveData<Integer> prevenditaMode = new MutableLiveData<>(PREVENDITA_NOT_SELECT_MODE);

    public int getPrevenditaMode() {
        Integer value = prevenditaMode.getValue();
        return value == null ? -1 : value;
    }

    public LiveData<Integer> getPrevenditaModeLiveData() {
        return prevenditaMode;
    }

    public void setPrevenditaMode(int prevenditaMode) {
        this.prevenditaMode.setValue(prevenditaMode);
    }

    //Roba lato network
    private MutableLiveData<Result<List<WCliente>, Void>> listaClientiResult = new MutableLiveData<>();
    private MutableLiveData<Result<WCliente, Void>> aggiungiClienteResult = new MutableLiveData<>();
    private MutableLiveData<Result<List<WTipoPrevendita>, Void>> listaTipoPrevenditaResult = new MutableLiveData<>();

    public LiveData<Result<List<WCliente>, Void>> getListaClientiResult() {
        return listaClientiResult;
    }

    public LiveData<Result<WCliente, Void>> getAggiungiClienteResult() {
        return aggiungiClienteResult;
    }

    public LiveData<Result<List<WTipoPrevendita>, Void>> getListaTipoPrevenditaResult() {
        return listaTipoPrevenditaResult;
    }


    public PRViewModel() {
        super();
    }

    public void getListaClienti() {
        MyContext myContext = getMyContext();

        if (myContext.isLoggato() && myContext.isStaffScelto()) {

            ManagerMembro managerMembro = getManagerMembro();
            Integer idStaff = myContext.getStaff().getId();

            managerMembro.restituisciListaClientiStaff(idStaff, new DefaultSuccessListener<>(listaClientiResult), new DefaultExceptionListener<>(listaClientiResult));

        } else {
            listaClientiResult.setValue(new Result<>(R.string.no_login));
        }
    }

    public void aggiungiCliente(String nome, String cognome, String dataDiNascita) {
        MyContext myContext = getMyContext();
        if (myContext.isLoggato() && myContext.isStaffScelto()) {
            Integer idStaff = getStaff().getId();
            ManagerPR managerPR = getManagerPR();

            LocalDate convDataNascita = null;

            if (isStringNotBlank(dataDiNascita)) {
                convDataNascita = DATE_FORMAT.parseLocalDate(dataDiNascita);
            }

            InsertNetWCliente insertNetWCliente = new InsertNetWCliente(idStaff, nome, cognome, null, convDataNascita, null);

            managerPR.aggiungiCliente(insertNetWCliente, new DefaultSuccessListener<>(aggiungiClienteResult), new DefaultExceptionListener<>(aggiungiClienteResult));
        } else {
            aggiungiClienteResult.setValue(new Result<>(R.string.no_login));
        }
    }

    public void getListaTipoPrevendita() {
        MyContext myContext = getMyContext();

        if (myContext.isLoggato() && myContext.isStaffScelto() && myContext.isEventoScelto()) {
            ManagerMembro managerMembro = getManagerMembro();
            Integer idEvento = getEvento().getId();

            managerMembro.restituisciListaTipiPrevenditaEvento(idEvento, new DefaultSuccessListener<>(listaTipoPrevenditaResult), new DefaultExceptionListener<>(listaTipoPrevenditaResult));

        } else {
            listaTipoPrevenditaResult.setValue(new Result<>(R.string.no_login));
        }
    }

    private boolean isStringNotBlank(String myString) {
        if (myString != null) {
            if (!myString.isEmpty()) {
                return !myString.trim().isEmpty();
            }
        }
        return false;
    }

    public void aggiungiClienteStateChanged(String nome, String cognome, String dataDiNascita) {
        if (!isNomeClienteValid(nome)) {
            aggiungiClienteState.setValue(new AggiungiClienteState(R.string.fragment_pr_add_cliente_invalid_nome, null, null));
        } else if (!isCognomeClienteValid(cognome)) {
            aggiungiClienteState.setValue(new AggiungiClienteState(null, R.string.fragment_pr_add_cliente_invalid_cognome, null));
        } else if (!isDataDiNascitaClienteValid(dataDiNascita)) {
            aggiungiClienteState.setValue(new AggiungiClienteState(null, null, R.string.fragment_pr_add_cliente_invalid_dataDiNascita));
        } else {
            aggiungiClienteState.setValue(new AggiungiClienteState(true));
        }
    }

    private boolean isNomeClienteValid(String nome) {
        return isStringNotBlank(nome);
    }

    private boolean isCognomeClienteValid(String cognome) {
        return isStringNotBlank(cognome);
    }

    private boolean isDataDiNascitaClienteValid(String dataDiNascita) {
        if (isStringNotBlank(dataDiNascita)) {
            try {
                LocalDate date = DATE_FORMAT.parseLocalDate(dataDiNascita);
                //Conversione riuscita ritorno vero
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        //Accetto anche senza data di nascita.
        return true;
    }


}
