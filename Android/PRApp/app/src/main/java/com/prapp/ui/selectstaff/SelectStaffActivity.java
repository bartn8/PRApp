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

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prapp.R;
import com.prapp.model.db.wrapper.WStaff;
import com.prapp.ui.Result;
import com.prapp.ui.UiUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectStaffActivity extends AppCompatActivity implements WStaffAdapter.ItemClickListener {

    public static final String SEARCH_PREFERENCES_MESSAGE = "searchPreferences";

    private boolean searchPreferences = false;

    private SelectStaffViewModel selectStaffViewModel;

    private WStaffAdapter.ItemClickListener itemClickListener = this;

    private UiUtils uiUtils;

    @BindView(R.id.selectStaffRecyclerView)
    public RecyclerView selectStaffRecyclerView;

    private Observer<Result<List<WStaff>,Void>> listStaffResultObserver = new Observer<Result<List<WStaff>,Void>>() {
        @Override
        public void onChanged(Result<List<WStaff>,Void> listStaffResult) {
            if (listStaffResult == null) {
                return;
            }

            Integer integerError = listStaffResult.getIntegerError();
            List<Exception> error = listStaffResult.getError();
            List<WStaff> success = listStaffResult.getSuccess();

            if (integerError != null)
                uiUtils.showError(integerError);

            if (error != null)
                uiUtils.showError(error);

            if (success != null) {
                //Popolo la recycler view.
                WStaffAdapter adapter = new WStaffAdapter(success, itemClickListener);
                selectStaffRecyclerView.setAdapter(adapter);
            }

        }
    };



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_staff);

        ButterKnife.bind(this);

        uiUtils = UiUtils.getInstance(getApplicationContext());

        Intent intent = getIntent();
        searchPreferences = intent.getBooleanExtra(SEARCH_PREFERENCES_MESSAGE, false);

        selectStaffRecyclerView.setHasFixedSize(true);
        selectStaffRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        selectStaffViewModel = ViewModelProviders.of(this, new SelectStaffViewModelFactory(getApplicationContext())).get(SelectStaffViewModel.class);
        selectStaffViewModel.getListStaffResult().observe(this, listStaffResultObserver);
//        selectStaffViewModel.getGetInfoUtenteResult().observe(this, getInfoUtenteResultObserver);

        //Non verifico login: lo fa il viewModel.
        if(searchPreferences)
        {
            //Carico lo staff già aperto in precedenza.
            if(selectStaffViewModel.caricaStaffSalvato())
            {
                //Posso chiudere l'activity
                //Si ritorna allo splash...
                setResult(RESULT_OK);
                finish();
            }
            else
            {
                //Carico gli staff e procedo normalmente
                selectStaffViewModel.getStaffMembri();
            }
        }
        else
        {
            //Carico gli staff e procedo normalmente
            selectStaffViewModel.getStaffMembri();
        }
    }

    @Override
    public void onListItemClick(int idStaff) {
        selectStaffViewModel.selectStaff(idStaff);

        //Posso chiudere l'activity
        //Si ritorna allo splash...
        setResult(RESULT_OK);
        finish();
    }
}