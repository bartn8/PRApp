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
import com.prapp.model.net.manager.ManagerMembro;
import com.prapp.ui.AbstractViewModel;
import com.prapp.ui.Result;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

public class PRViewModel extends AbstractViewModel {

    public static final String TAG = PRViewModel.class.getSimpleName();

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.mediumDate();

    //Roba di controlli
    private MutableLiveData<AggiungiClienteState> aggiungiClienteState = new MutableLiveData<>();

    //Roba lato network
    private MutableLiveData<Result<List<WCliente>, Void>> listaClientiResult = new MutableLiveData<>();

    public LiveData<Result<List<WCliente>, Void>> getListaClientiResult() {
        return listaClientiResult;
    }

    public LiveData<AggiungiClienteState> getAggiungiClienteState() {
        return aggiungiClienteState;
    }

    public PRViewModel() {
        super();
    }

    public void getListaClienti(){
        MyContext myContext = getMyContext();

        if (myContext.isLoggato() && myContext.isStaffScelto()) {

            ManagerMembro managerMembro = getManagerMembro();
            Integer idStaff = myContext.getStaff().getId();

            managerMembro.restituisciListaClientiStaff(idStaff, new DefaultSuccessListener<>(listaClientiResult), new DefaultExceptionListener<>(listaClientiResult));

        } else {
            listaClientiResult.setValue(new Result<>(R.string.no_login));
        }
    }

    public void aggiungiCliente(String nome, String cognome, String dataDiNascita){
        MyContext myContext = getMyContext();
        if (myContext.isLoggato() && myContext.isStaffScelto()) {

        } else {
            listaClientiResult.setValue(new Result<>(R.string.no_login));
        }
    }

    private boolean isStringNotBlank(String myString){
        if(myString != null){
            if(!myString.isEmpty()){
                return !myString.trim().isEmpty();
            }
        }
        return false;
    }

    public void aggiungiClienteStateChanged(String nome, String cognome, String dataDiNascita){
        if(!isNomeClienteValid(nome)){
            aggiungiClienteState.setValue(new AggiungiClienteState(R.string.fragment_pr_add_cliente_invalid_nome, null, null));
        }else if(!isCognomeClienteValid(cognome)){
            aggiungiClienteState.setValue(new AggiungiClienteState(null, R.string.fragment_pr_add_cliente_invalid_cognome, null));
        }else if(!isDataDiNascitaClienteValid(dataDiNascita)){
            aggiungiClienteState.setValue(new AggiungiClienteState(null, null, R.string.fragment_pr_add_cliente_invalid_dataDiNascita));
        }
    }

    private boolean isNomeClienteValid(String nome){
        return isStringNotBlank(nome);
    }

    private boolean isCognomeClienteValid(String cognome){
        return isStringNotBlank(cognome);
    }

    private boolean isDataDiNascitaClienteValid(String dataDiNascita){
        if(isStringNotBlank(dataDiNascita)){
            try{
                LocalDate date = DATE_FORMAT.parseLocalDate(dataDiNascita);
                //Conversione riuscita ritorno vero
                return true;
            }catch(IllegalArgumentException e){
                return false;
            }
        }

        //Accetto anche senza data di nascita.
        return true;
    }



}
