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

package com.prapp.ui.main.fragment.amministratore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.prapp.R;
import com.prapp.model.MyContext;
import com.prapp.model.db.wrapper.WStaff;
import com.prapp.model.db.wrapper.WStatisticheCassiereEvento;
import com.prapp.model.db.wrapper.WStatisticheEvento;
import com.prapp.model.db.wrapper.WStatistichePREvento;
import com.prapp.model.db.wrapper.WUtente;
import com.prapp.model.net.manager.ManagerAmministratore;
import com.prapp.model.net.manager.ManagerMembro;
import com.prapp.ui.AbstractViewModel;
import com.prapp.ui.Result;

import java.util.List;

public class AmministratoreViewModel extends AbstractViewModel {

    public static final String TAG = AmministratoreViewModel.class.getSimpleName();

    private MutableLiveData<Result<List<WUtente>, Void>> membriStaffResult = new MutableLiveData<>();

    private MutableLiveData<Result<List<WStatistichePREvento>, Integer>> statistichePREventoResult = new MutableLiveData<>();
    private MutableLiveData<Result<WStatisticheCassiereEvento, Integer>> statisticheCassiereEventoResult = new MutableLiveData<>();
    private MutableLiveData<Result<List<WStatisticheEvento>, Void>> statisticheAmministratoreEventoResult = new MutableLiveData<>();

    public LiveData<Result<List<WUtente>, Void>> getMembriStaffResult() {
        return membriStaffResult;
    }

    public LiveData<Result<List<WStatistichePREvento>, Integer>> getStatistichePREventoResult() {
        return statistichePREventoResult;
    }

    public LiveData<Result<WStatisticheCassiereEvento, Integer>> getStatisticheCassiereEventoResult() {
        return statisticheCassiereEventoResult;
    }

    public LiveData<Result<List<WStatisticheEvento>, Void>> getStatisticheAmministratoreEventoResult() {
        return statisticheAmministratoreEventoResult;
    }

    public AmministratoreViewModel() {
        super();
    }

    public void getMembriStaff() {
        MyContext myContext = getMyContext();

        if (myContext.isStaffScelto() && myContext.isLoggato()) {
            ManagerMembro managerMembro = getManagerMembro();
            WStaff staff = getStaff();

            managerMembro.restituisciListaUtentiStaff(staff.getId(), new DefaultSuccessListener<>(membriStaffResult), new DefaultExceptionListener<>(membriStaffResult));
        } else {
            membriStaffResult.setValue(new Result<>(R.string.no_staff));
        }
    }

    public void getStatisticheAmministratoreEvento() {
        MyContext myContext = getMyContext();

        if (myContext.isLoggato() && myContext.isStaffScelto() && myContext.isEventoScelto()) {
            ManagerAmministratore managerAmministratore = getManagerAmministratore();
            Integer idEvento = myContext.getEvento().getId();

            managerAmministratore.resitituisciStatisticheEvento(idEvento, new DefaultSuccessListener<>(statisticheAmministratoreEventoResult), new DefaultExceptionListener<>(statisticheAmministratoreEventoResult));
        } else {
            statisticheAmministratoreEventoResult.setValue(new Result<>(R.string.no_login));
        }
    }

    public void getStatistichePREvento(Integer idPR) {
        MyContext myContext = getMyContext();

        if (myContext.isLoggato() && myContext.isStaffScelto() && myContext.isEventoScelto()) {
            ManagerAmministratore managerAmministratore = getManagerAmministratore();
            Integer idEvento = myContext.getEvento().getId();

            managerAmministratore.resitituisciStatistichePREvento(idPR, idEvento, new DefaultSuccessListener<>(statistichePREventoResult, idPR), new DefaultExceptionListener<>(statistichePREventoResult, idPR));
        } else {
            statistichePREventoResult.setValue(new Result<>(R.string.no_evento));
        }
    }

    public void getStatisticheCassiereEvento(Integer idCassiere) {
        MyContext myContext = getMyContext();

        if (myContext.isLoggato() && myContext.isStaffScelto() && myContext.isEventoScelto()) {
            ManagerAmministratore managerAmministratore = getManagerAmministratore();
            Integer idEvento = myContext.getEvento().getId();

            managerAmministratore.resitituisciStatisticheCassiereEvento(idCassiere, idEvento, new DefaultSuccessListener<>(statisticheCassiereEventoResult, idCassiere), new DefaultExceptionListener<>(statisticheCassiereEventoResult, idCassiere));
        } else {
            statisticheCassiereEventoResult.setValue(new Result<>(R.string.no_evento));
        }
    }

}
