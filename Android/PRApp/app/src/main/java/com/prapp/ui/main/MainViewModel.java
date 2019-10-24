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

import android.content.Context;
import android.webkit.WebView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Response;
import com.prapp.R;
import com.prapp.model.MyContext;
import com.prapp.model.db.wrapper.WDirittiUtente;
import com.prapp.model.db.wrapper.WEntrata;
import com.prapp.model.db.wrapper.WEvento;
import com.prapp.model.db.wrapper.WPrevenditaPlus;
import com.prapp.model.db.wrapper.WStaff;
import com.prapp.model.db.wrapper.WStatisticheCassiereEvento;
import com.prapp.model.db.wrapper.WStatisticheEvento;
import com.prapp.model.db.wrapper.WStatistichePREvento;
import com.prapp.model.db.wrapper.WUtente;
import com.prapp.model.net.MyCookieManager;
import com.prapp.model.net.manager.ManagerAmministratore;
import com.prapp.model.net.manager.ManagerCassiere;
import com.prapp.model.net.manager.ManagerMembro;
import com.prapp.model.net.manager.ManagerUtente;
import com.prapp.model.net.wrapper.NetWEntrata;
import com.prapp.model.preferences.ApplicationPreferences;
import com.prapp.ui.AbstractViewModel;
import com.prapp.ui.Result;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainViewModel extends AbstractViewModel {

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



    private Map<Integer, NetWEntrata> mapEntrata = new HashMap<>();

    public MainViewModel(Context context) {
        super(context);
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

    public NetWEntrata get(Integer idPrevendita) {
        return mapEntrata.get(idPrevendita);
    }

    public NetWEntrata remove(WPrevenditaPlus prevendita) {
        return mapEntrata.remove(prevendita.getId());
    }

    public NetWEntrata remove(Integer idPrevendita) {
        return mapEntrata.remove(idPrevendita);
    }

    public void acceptThirdPartyCookies(WebView view) {
        MyCookieManager.getSingleton(getContext()).acceptThirdPartyCookies(view);
    }

    public String getToken() {
        ApplicationPreferences preferences = getPreferences();
        String token = "";

        try {
            token = preferences.getLastStoredToken().getToken();
        } catch (UnsupportedEncodingException e) {

        }

        return token;
    }

    public void logout() {
        MyContext myContext = getMyContext();
        ApplicationPreferences preferences = getPreferences();

        if (myContext.isLoggato())//TODO: da levare nelle activity i controlli???
        {
            ManagerUtente managerUtente = getManagerUtente();

            try {
                managerUtente.logout(new Response.Listener<Void>() {
                    @Override
                    public void onResponse(Void response) {
                        //Pulisco il contesto e le preferenze.
                        myContext.logout();
                        preferences.logout();
                        logoutResult.setValue(new Result<>(null, null));
                    }
                }, new DefaultExceptionListener<>(logoutResult));
            } catch (UnsupportedEncodingException e) {
                //Non dovrebbe succedere.
                logoutResult.setValue(new Result<>(e));
            }
        } else {
            logoutResult.setValue(new Result<>(R.string.no_login));
        }
    }

    public void getMembriStaff() {
        MyContext myContext = getMyContext();

        if (myContext.isStaffScelto() && myContext.isLoggato()) {
            ManagerMembro managerMembro = getManagerMembro();
            WStaff staff = getStaff();

            try {
                managerMembro.restituisciListaUtentiStaff(staff.getId(), new DefaultSuccessListener<>(membriStaffResult), new DefaultExceptionListener<>(membriStaffResult));
            } catch (UnsupportedEncodingException e) {
                //Non dovrebbe succedere.
                membriStaffResult.setValue(new Result<>(e));
            }
        } else {
            membriStaffResult.setValue(new Result<>(R.string.no_staff));
        }
    }

    public void getEventiStaff() {
        MyContext myContext = getMyContext();

        if (myContext.isStaffScelto() && myContext.isLoggato()) {
            ManagerMembro managerMembro = getManagerMembro();
            WStaff staff = getStaff();

            try {
                managerMembro.restituisciListaEventiStaff(staff.getId(), new DefaultSuccessListener<>(eventiStaffResult), new DefaultExceptionListener<>(eventiStaffResult));
            } catch (UnsupportedEncodingException e) {
                //Non dovrebbe succedere.
                eventiStaffResult.setValue(new Result<>(e));
            }
        } else {
            eventiStaffResult.setValue(new Result<>(R.string.no_staff));
        }
    }

    public void getInformazioniPrevendita(NetWEntrata entrata) {
        MyContext myContext = getMyContext();
        Integer idPrevendita = entrata.getIdPrevendita();

        //La prevendita non deve essere già stata controllata.
        if (!mapEntrata.containsKey(idPrevendita)) {
            mapEntrata.put(idPrevendita, entrata);

            if (myContext.isLoggato()) {
                ManagerCassiere managerCassiere = getManagerCassiere();

                try {
                    managerCassiere.restituisciInformazioniPrevendita(idPrevendita, new DefaultSuccessListener<>(infoPrevenditaResult), new DefaultExceptionListener<>(infoPrevenditaResult));
                } catch (UnsupportedEncodingException e) {
                    infoPrevenditaResult.setValue(new Result<>(e));
                }
            } else {
                infoPrevenditaResult.setValue(new Result<>(R.string.no_login));
            }
        }
    }

    public void timbraEntrata(WPrevenditaPlus prevendita) {
        MyContext myContext = getMyContext();
        Integer idPrevendita = prevendita.getId();

        //Deve essere presente il codice originale per la verifica.
        if (mapEntrata.containsKey(idPrevendita)) {
            if (myContext.isLoggato()) {
                ManagerCassiere managerCassiere = getManagerCassiere();

                try {
                    managerCassiere.timbraEntrata(mapEntrata.get(idPrevendita), new DefaultSuccessListener<>(entrataResult, prevendita), new DefaultExceptionListener<>(entrataResult, prevendita));
                } catch (UnsupportedEncodingException e) {
                    infoPrevenditaResult.setValue(new Result<>(e));
                }
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

            try {
                managerMembro.restituisciDirittiUtenteStaff(idUtente, idStaff, new DefaultSuccessListener<>(dirittiMembroResult, idUtente), new DefaultExceptionListener<>(dirittiMembroResult, idUtente));
            } catch (UnsupportedEncodingException e) {
                dirittiMembroResult.setValue(new Result<>(e));
            }
        } else {
            dirittiMembroResult.setValue(new Result<>(R.string.no_staff));
        }
    }

    public void getStatisticheAmministratoreEvento() {
        MyContext myContext = getMyContext();

        if (myContext.isLoggato() && myContext.isStaffScelto() && myContext.isEventoScelto()) {
            ManagerAmministratore managerAmministratore = getManagerAmministratore();
            Integer idEvento = myContext.getEvento().getId();

            try {
                managerAmministratore.resitituisciStatisticheEvento(idEvento, new DefaultSuccessListener<>(statisticheAmministratoreEventoResult), new DefaultExceptionListener<>(statisticheAmministratoreEventoResult));
            } catch (UnsupportedEncodingException e) {
                statisticheAmministratoreEventoResult.setValue(new Result<>(e));
            }
        } else {
            statisticheAmministratoreEventoResult.setValue(new Result<>(R.string.no_login));
        }
    }

    public void getStatistichePREvento(Integer idPR) {
        MyContext myContext = getMyContext();

        if (myContext.isLoggato() && myContext.isStaffScelto() && myContext.isEventoScelto()) {
            ManagerAmministratore managerAmministratore = getManagerAmministratore();
            Integer idEvento = myContext.getEvento().getId();

            try {
                managerAmministratore.resitituisciStatistichePREvento(idPR, idEvento, new DefaultSuccessListener<>(statistichePREventoResult, idPR), new DefaultExceptionListener<>(statistichePREventoResult, idPR));
            } catch (UnsupportedEncodingException e) {
                statistichePREventoResult.setValue(new Result<>(e));
            }
        } else {
            statistichePREventoResult.setValue(new Result<>(R.string.no_evento));
        }
    }

    public void getStatisticheCassiereEvento(Integer idCassiere) {
        MyContext myContext = getMyContext();

        if (myContext.isLoggato() && myContext.isStaffScelto() && myContext.isEventoScelto()) {
            ManagerAmministratore managerAmministratore = getManagerAmministratore();
            Integer idEvento = myContext.getEvento().getId();

            try {
                managerAmministratore.resitituisciStatisticheCassiereEvento(idCassiere, idEvento, new DefaultSuccessListener<>(statisticheCassiereEventoResult, idCassiere), new DefaultExceptionListener<>(statisticheCassiereEventoResult, idCassiere));
            } catch (UnsupportedEncodingException e) {
                statisticheCassiereEventoResult.setValue(new Result<>(e));
            }
        } else {
            statisticheCassiereEventoResult.setValue(new Result<>(R.string.no_evento));
        }
    }

    public void getListaPrevenditeTimbrateEvento() {
        MyContext myContext = getMyContext();

        if (myContext.isLoggato() && myContext.isStaffScelto() && myContext.isEventoScelto()) {
            ManagerCassiere managerCassiere = getManagerCassiere();
            Integer idEvento = myContext.getEvento().getId();

            try {
                managerCassiere.restituisciListaPrevenditeTimbrate(idEvento, new DefaultSuccessListener<>(prevenditeResult), new DefaultExceptionListener<>(prevenditeResult));
            } catch (UnsupportedEncodingException e) {
                prevenditeResult.setValue(new Result<>(e));
            }
        } else {
            prevenditeResult.setValue(new Result<>(R.string.no_login));
        }
    }

    public void getListaPrevenditeNonTimbrateEvento() {
        MyContext myContext = getMyContext();

        if (myContext.isLoggato() && myContext.isStaffScelto() && myContext.isEventoScelto()) {
            ManagerCassiere managerCassiere = getManagerCassiere();
            Integer idEvento = myContext.getEvento().getId();

            try {
                managerCassiere.restituisciListaPrevenditeNonTimbrate(idEvento, new DefaultSuccessListener<>(prevenditeResult), new DefaultExceptionListener<>(prevenditeResult));
            } catch (UnsupportedEncodingException e) {
                prevenditeResult.setValue(new Result<>(e));
            }
        } else {
            prevenditeResult.setValue(new Result<>(R.string.no_login));
        }
    }

}

