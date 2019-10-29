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

package com.prapp.ui.main;

import android.util.SparseArray;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.prapp.R;
import com.prapp.model.MyContext;
import com.prapp.model.db.wrapper.WCliente;
import com.prapp.model.db.wrapper.WDirittiUtente;
import com.prapp.model.db.wrapper.WEntrata;
import com.prapp.model.db.wrapper.WEvento;
import com.prapp.model.db.wrapper.WPrevenditaPlus;
import com.prapp.model.db.wrapper.WStaff;
import com.prapp.model.db.wrapper.WStatisticheCassiereEvento;
import com.prapp.model.db.wrapper.WStatisticheEvento;
import com.prapp.model.db.wrapper.WStatistichePREvento;
import com.prapp.model.db.wrapper.WUtente;
import com.prapp.model.net.manager.ManagerAmministratore;
import com.prapp.model.net.manager.ManagerCassiere;
import com.prapp.model.net.manager.ManagerMembro;
import com.prapp.model.net.manager.ManagerUtente;
import com.prapp.model.net.wrapper.NetWEntrata;
import com.prapp.model.preferences.ApplicationPreferences;
import com.prapp.ui.AbstractViewModel;
import com.prapp.ui.Result;

import java.util.List;

public class MainViewModel extends AbstractViewModel {

    public static final String TAG = MainViewModel.class.getSimpleName();

    private MutableLiveData<Result<Void, Void>> logoutResult = new MutableLiveData<>();
    private MutableLiveData<Result<List<WUtente>, Void>> membriStaffResult = new MutableLiveData<>();
    private MutableLiveData<Result<List<WEvento>, Void>> eventiStaffResult = new MutableLiveData<>();
    private MutableLiveData<Result<WPrevenditaPlus, Void>> infoPrevenditaResult = new MutableLiveData<>();
    private MutableLiveData<Result<WEntrata, WPrevenditaPlus>> entrataResult = new MutableLiveData<>();
    private MutableLiveData<Result<WDirittiUtente, Integer>> dirittiMembroResult = new MutableLiveData<>();
    private MutableLiveData<Result<List<WStatistichePREvento>, Integer>> statistichePREventoResult = new MutableLiveData<>();
    private MutableLiveData<Result<WStatisticheCassiereEvento, Integer>> statisticheCassiereEventoResult = new MutableLiveData<>();
    private MutableLiveData<Result<List<WStatisticheEvento>, Void>> statisticheAmministratoreEventoResult = new MutableLiveData<>();
    private MutableLiveData<Result<List<WPrevenditaPlus>, Void>> prevenditeResult = new MutableLiveData<>();
    private MutableLiveData<Result<List<WCliente>, Void>> listaClientiResult = new MutableLiveData<>();

    private SparseArray<NetWEntrata> mapEntrata = new SparseArray<NetWEntrata>();

    MainViewModel() {
        super();
    }

    public LiveData<Result<Void, Void>> getLogoutResult() {
        return logoutResult;
    }

    public LiveData<Result<List<WUtente>, Void>> getMembriStaffResult() {
        return membriStaffResult;
    }

    public LiveData<Result<WEntrata, WPrevenditaPlus>> getEntrataResult() {
        return entrataResult;
    }

    public LiveData<Result<WPrevenditaPlus, Void>> getInfoPrevenditaResult() {
        return infoPrevenditaResult;
    }

    public LiveData<Result<List<WEvento>, Void>> getEventiStaffResult() {
        return eventiStaffResult;
    }

    public LiveData<Result<WDirittiUtente, Integer>> getDirittiMembroResult() {
        return dirittiMembroResult;
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

    public LiveData<Result<List<WPrevenditaPlus>, Void>> getPrevenditeResult() {
        return prevenditeResult;
    }

    public LiveData<Result<List<WCliente>, Void>> getListaClientiResult() {
        return listaClientiResult;
    }

    public NetWEntrata get(Integer idPrevendita) {
        return mapEntrata.get(idPrevendita);
    }

    public void remove(WPrevenditaPlus prevendita) {
        mapEntrata.remove(prevendita.getId());
    }

    public String getToken() {
        ApplicationPreferences preferences = getPreferences();
        return preferences.getLastStoredToken().getToken();
    }

    public void logout() {
        MyContext myContext = getMyContext();
        ApplicationPreferences preferences = getPreferences();

        if (myContext.isLoggato())//TODO: da levare nelle activity i controlli???
        {
            ManagerUtente managerUtente = getManagerUtente();

            managerUtente.logout(response -> {
                //Pulisco il contesto e le preferenze.
                myContext.logout();
                preferences.logout();
                logoutResult.setValue(new Result<>(null, null));
            }, new DefaultExceptionListener<>(logoutResult));
        } else {
            logoutResult.setValue(new Result<>(R.string.no_login));
        }
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

    public void getEventiStaff() {
        MyContext myContext = getMyContext();

        if (myContext.isStaffScelto() && myContext.isLoggato()) {
            ManagerMembro managerMembro = getManagerMembro();
            WStaff staff = getStaff();

            managerMembro.restituisciListaEventiStaff(staff.getId(), new DefaultSuccessListener<>(eventiStaffResult), new DefaultExceptionListener<>(eventiStaffResult));
        } else {
            eventiStaffResult.setValue(new Result<>(R.string.no_staff));
        }
    }

    public void getInformazioniPrevendita(NetWEntrata entrata) {
        MyContext myContext = getMyContext();
        Integer idPrevendita = entrata.getIdPrevendita();

        //La prevendita non deve essere già stata controllata.
        if (mapEntrata.indexOfKey(idPrevendita) < 0) {
            mapEntrata.put(idPrevendita, entrata);

            if (myContext.isLoggato()) {
                ManagerCassiere managerCassiere = getManagerCassiere();

                managerCassiere.restituisciInformazioniPrevendita(idPrevendita, new DefaultSuccessListener<>(infoPrevenditaResult), new DefaultExceptionListener<>(infoPrevenditaResult));
            } else {
                infoPrevenditaResult.setValue(new Result<>(R.string.no_login));
            }
        }
    }

    public void timbraEntrata(WPrevenditaPlus prevendita) {
        MyContext myContext = getMyContext();
        Integer idPrevendita = prevendita.getId();

        //Deve essere presente il codice originale per la verifica.
        if (mapEntrata.indexOfKey(idPrevendita) >= 0) {
            if (myContext.isLoggato()) {
                ManagerCassiere managerCassiere = getManagerCassiere();

                managerCassiere.timbraEntrata(mapEntrata.get(idPrevendita), new DefaultSuccessListener<>(entrataResult, prevendita), new DefaultExceptionListener<>(entrataResult, prevendita));
            } else {
                infoPrevenditaResult.setValue(new Result<>(R.string.no_login));
            }
        }
    }

    public void getDirittiMembro(Integer idUtente) {
        MyContext myContext = getMyContext();

        if (myContext.isLoggato() && myContext.isStaffScelto()) {
            Integer idStaff = myContext.getStaff().getId();
            ManagerMembro managerMembro = getManagerMembro();

            managerMembro.restituisciDirittiUtenteStaff(idUtente, idStaff, new DefaultSuccessListener<>(dirittiMembroResult, idUtente), new DefaultExceptionListener<>(dirittiMembroResult, idUtente));
        } else {
            dirittiMembroResult.setValue(new Result<>(R.string.no_staff));
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

    public void getListaPrevenditeTimbrateEvento() {
        MyContext myContext = getMyContext();

        if (myContext.isLoggato() && myContext.isStaffScelto() && myContext.isEventoScelto()) {
            ManagerCassiere managerCassiere = getManagerCassiere();
            Integer idEvento = myContext.getEvento().getId();

            managerCassiere.restituisciListaPrevenditeTimbrate(idEvento, new DefaultSuccessListener<>(prevenditeResult), new DefaultExceptionListener<>(prevenditeResult));
        } else {
            prevenditeResult.setValue(new Result<>(R.string.no_login));
        }
    }

    public void getListaPrevenditeNonTimbrateEvento() {
        MyContext myContext = getMyContext();

        if (myContext.isLoggato() && myContext.isStaffScelto() && myContext.isEventoScelto()) {
            ManagerCassiere managerCassiere = getManagerCassiere();
            Integer idEvento = myContext.getEvento().getId();

            managerCassiere.restituisciListaPrevenditeNonTimbrate(idEvento, new DefaultSuccessListener<>(prevenditeResult), new DefaultExceptionListener<>(prevenditeResult));
        } else {
            prevenditeResult.setValue(new Result<>(R.string.no_login));
        }
    }

    public void getListaClienti(){
        MyContext myContext = getMyContext();

        if (myContext.isLoggato() && myContext.isStaffScelto()) {

            ManagerMembro managerMembro = getManagerMembro();
            Integer idStaff = myContext.getStaff().getId();

            managerMembro.restituisciListaClientiStaff(idStaff, new DefaultSuccessListener<>(listaClientiResult), new DefaultExceptionListener<>(listaClientiResult));

        } else {
            prevenditeResult.setValue(new Result<>(R.string.no_login));
        }
    }

}

