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

package com.prapp.ui.selectevento;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.prapp.R;
import com.prapp.model.MyContext;
import com.prapp.model.db.wrapper.WEvento;
import com.prapp.model.net.manager.ManagerMembro;
import com.prapp.model.preferences.ApplicationPreferences;
import com.prapp.ui.AbstractViewModel;
import com.prapp.ui.Result;

import org.jetbrains.annotations.Nullable;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class SelectEventoViewModel extends AbstractViewModel {

    private static final String TAG = SelectEventoViewModel.class.getSimpleName();

    private MutableLiveData<Result<List<WEvento>, Void>> listEventiResult = new MutableLiveData<>();

    private List<WEvento> eventiList = new ArrayList<>();

    SelectEventoViewModel() {
        super();
    }

    public LiveData<Result<List<WEvento>, Void>> getListEventiResult() {
        return listEventiResult;
    }


    //Ricerca lo staff giusto le lo restituisce
    @Nullable
    private WEvento getEvento(Integer idEvento) {
        for (WEvento evento : eventiList) {
            if (evento.getId().intValue() == idEvento.intValue()) {
                return evento;
            }
        }

        return null;
    }

    public void selectEvento(int idEvento) {
        MyContext myContext = getMyContext();
        ApplicationPreferences preferences = getPreferences();

        //Seleziono lo staff come default:
        //1) lo salvo nelle preferenze.
        //2) lo sparo nel contesto.

        //Staff fortunato.
        WEvento evento = getEvento(idEvento);
        //1)
        preferences.saveEvento(evento);
        //2)
        myContext.setEvento(evento);
    }

    public boolean caricaEventoSalvato() {
        MyContext myContext = getMyContext();
        ApplicationPreferences preferences = getPreferences();

        if (preferences.isEventoSaved()) {
            WEvento evento = preferences.getEvento();

            eventiList = new ArrayList<>();
            eventiList.add(evento);
            listEventiResult.setValue(new Result<>(eventiList, null));

            myContext.setEvento(evento);

            return true;
        }

        return false;
    }


    public void getEventiStaff() {
        MyContext myContext = getMyContext();
        ApplicationPreferences preferences = getPreferences();

        if (myContext.isLoggato() && myContext.isStaffScelto()) {
            ManagerMembro managerMembro = getManagerMembro();
            Integer idStaff = myContext.getStaff().getId();

            try {
                managerMembro.restituisciListaEventiStaff(idStaff, response -> {
                    eventiList = response;
                    listEventiResult.setValue(new Result<>(response, null));
                }, new DefaultExceptionListener<>(listEventiResult));
            } catch (UnsupportedEncodingException e) {
                //Non dovrebbe succedere.

                listEventiResult.setValue(new Result<>(e));
                Log.d(TAG, e.getLocalizedMessage());
            }
        } else {
            listEventiResult.setValue(new Result<>(R.string.no_staff));
        }
    }


}