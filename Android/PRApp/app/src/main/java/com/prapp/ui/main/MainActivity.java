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

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.prapp.R;
import com.prapp.model.db.enums.Diritto;
import com.prapp.model.db.wrapper.WDirittiUtente;
import com.prapp.ui.UiUtils;
import com.prapp.ui.main.fragment.AmministratoreFragment;
import com.prapp.ui.main.fragment.CassiereFragment;
import com.prapp.ui.main.fragment.MembroFragment;
import com.prapp.ui.main.fragment.PRFragment;
import com.prapp.ui.main.fragment.UtenteFragment;

import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String NEEDED_PERMISSION = Manifest.permission.CAMERA;
    private static final int PERMISSION_REQUEST_CAMERA = 0;
    private static final String CURRENT_FRAGMENT_KEY = "CURFRAG";


    private MainViewModel mainViewModel;

    private UiUtils uiUtils;
    private Set<Diritto> diritti;

    private int currentFragment = 0;

    private UtenteFragment utenteFragment = UtenteFragment.newInstance();
    private MembroFragment membroFragment = MembroFragment.newInstance();
    private PRFragment prFragment = PRFragment.newInstance();
    private CassiereFragment cassiereFragment = CassiereFragment.newInstance();
    private AmministratoreFragment amminisratoreFragment = AmministratoreFragment.newInstance();

    @BindView(R.id.nav_view)
    public BottomNavigationView navView;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_utente:
                    currentFragment = 0;
                    cambiaTab(false);
                    return true;

                case R.id.navigation_membro:
                    currentFragment = 1;
                    cambiaTab(false);
                    return true;

                case R.id.navigation_pr:
                    currentFragment = 2;
                    cambiaTab(false);
                    return true;

                case R.id.navigation_cassiere:
                    currentFragment = 3;
                    cambiaTab(false);
                    return true;

                case R.id.navigation_amministratore:
                    currentFragment = 4;
                    cambiaTab(false);
                    return true;
            }

            return false;
        }
    };

    private void cambiaTab(boolean setBottomNavigationBar) {
        switch (currentFragment) {
            case 0:
                if (!utenteFragment.isInLayout())
                    cambiaFragment(utenteFragment);

                if(setBottomNavigationBar)
                    navView.setSelectedItemId(R.id.navigation_utente);
                break;
            case 1:
                if (!membroFragment.isInLayout())
                    cambiaFragment(membroFragment);

                if(setBottomNavigationBar)
                    navView.setSelectedItemId(R.id.navigation_membro);
                break;
            case 2:
                if (!prFragment.isInLayout())
                    cambiaFragment(prFragment);

                if(setBottomNavigationBar)
                    navView.setSelectedItemId(R.id.navigation_pr);
                break;
            case 3:
                if (!cassiereFragment.isInLayout())
                    cambiaFragment(cassiereFragment);

                if(setBottomNavigationBar)
                    navView.setSelectedItemId(R.id.navigation_cassiere);
                break;
            case 4:
                if (!amminisratoreFragment.isInLayout())
                    cambiaFragment(amminisratoreFragment);

                if(setBottomNavigationBar)
                    navView.setSelectedItemId(R.id.navigation_amministratore);
                break;

            default:
                if (!utenteFragment.isInLayout())
                    cambiaFragment(utenteFragment);

                if(setBottomNavigationBar)
                    navView.setSelectedItemId(R.id.navigation_utente);
                break;
        }
    }

    //https://stackoverflow.com/questions/5658675/replacing-a-fragment-with-another-fragment-inside-activity-group
    private void cambiaFragment(Fragment nuovoFragment) {
        // Create new transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down);

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack if needed
        transaction.replace(R.id.nav_container, nuovoFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        //Inizializzo il view model e applico gli observer.
        mainViewModel = ViewModelProviders.of(this, new MainViewModelFactory(getApplicationContext())).get(MainViewModel.class);


        uiUtils = UiUtils.getInstance(getApplicationContext());

        //Recupero i diritti dell'utente.
        WDirittiUtente dirittiUtente = mainViewModel.getDirittiUtente();

        diritti = dirittiUtente.getDiritti();

        //Controllo se era presente un fragment precedente.
        //https://medium.com/hootsuite-engineering/handling-orientation-changes-on-android-41a6b62cb43f

        if (savedInstanceState != null) {
            currentFragment = savedInstanceState.getInt(CURRENT_FRAGMENT_KEY);
        }

        //Imposto la schermata selezionata
        cambiaTab(true);

        //Imposto le schermate in base ai diritti posseduti.

        Menu menu = navView.getMenu();

        menu.findItem(R.id.navigation_pr).setEnabled(diritti.contains(Diritto.PR));
        menu.findItem(R.id.navigation_cassiere).setEnabled(diritti.contains(Diritto.CASSIERE));
        menu.findItem(R.id.navigation_amministratore).setEnabled(diritti.contains(Diritto.AMMINISTRATORE));

        //Listener del menù sottostante
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());

        //Controllo permessi camera
        checkCameraPermission();

    }

    //https://medium.com/hootsuite-engineering/handling-orientation-changes-on-android-41a6b62cb43f

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        //Devo salvare il fragment selezionato.
        savedInstanceState.putInt(CURRENT_FRAGMENT_KEY, currentFragment);
    }



    //Back button method.

    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        //Chiudo direttamente l'applicazione.
        setResult(RESULT_OK);
        finish();
    }

    //Camera permission methods.

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                uiUtils.makeToast(R.string.show_camera_permission_granted);

                //Riattivo nel caso cassiere.
                Menu menu = navView.getMenu();
                menu.findItem(R.id.navigation_cassiere).setEnabled(diritti.contains(Diritto.CASSIERE));

            }
        }
        // END_INCLUDE(onRequestPermissionsResult)
    }

    /**
     * Requests the {@link android.Manifest.permission#CAMERA} permission.
     * If an additional rationale should be displayed, the user has to launch the request from
     * a SnackBar that includes additional information.
     */
    private void checkCameraPermission() {
        if (ActivityCompat.checkSelfPermission(this, NEEDED_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, NEEDED_PERMISSION)) {
                new AlertDialog.Builder(this)
                        .setMessage(R.string.show_camera_permission_request)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{NEEDED_PERMISSION},
                                        PERMISSION_REQUEST_CAMERA);
                            }
                        })
                        .show();
            }else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            }
        }
    }
}
