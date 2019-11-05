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

package com.prapp.ui.activity.main.fragment.membro;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.prapp.R;
import com.prapp.model.MyContext;
import com.prapp.model.db.wrapper.WStaff;
import com.prapp.model.db.wrapper.WUtente;
import com.prapp.model.net.manager.ManagerMembro;
import com.prapp.ui.AbstractViewModel;
import com.prapp.ui.Result;

import java.util.List;

public class MembroViewModel extends AbstractViewModel {

    public static final String TAG = MembroViewModel.class.getSimpleName();

    private MutableLiveData<Result<List<WUtente>, Void>> membriStaffResult = new MutableLiveData<>();

    public LiveData<Result<List<WUtente>, Void>> getMembriStaffResult() {
        return membriStaffResult;
    }

    public MembroViewModel() {
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



}
