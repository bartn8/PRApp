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
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
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

public class MainActivity extends AppCompatActivity  {

    private static final int PERMISSION_REQUEST_CAMERA = 0;

    private MainViewModel mainViewModel;

    private UiUtils uiUtils;
    private Set<Diritto> diritti;

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
                    if(!utenteFragment.isInLayout())
                        cambiaFragment(utenteFragment);
                    return true;
                case R.id.navigation_membro:
                    if(!membroFragment.isInLayout())
                        cambiaFragment(membroFragment);
                    return true;
                case R.id.navigation_pr:
                    if(!prFragment.isInLayout())
                        cambiaFragment(prFragment);
                    return true;

                case R.id.navigation_cassiere:
                    if(!cassiereFragment.isInLayout())
                        cambiaFragment(cassiereFragment);
                    return true;

                case R.id.navigation_amministratore:
                    if(!amminisratoreFragment.isInLayout())
                        cambiaFragment(amminisratoreFragment);
                    return true;
            }
            return false;
        }
    };

    private void aggiungiFragment(Fragment nuovoFragment)
    {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.nav_container, nuovoFragment).commit();
    }

    //https://stackoverflow.com/questions/5658675/replacing-a-fragment-with-another-fragment-inside-activity-group
    private void cambiaFragment(Fragment nuovoFragment)
    {
        // Create new transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

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

        //Imposto la schermata selezionata
        navView.setSelectedItemId(R.id.navigation_utente);

        cambiaFragment(utenteFragment);

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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
        }

    }

    //Serviva per fare il logout dalla webapp.
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        //super.onActivityResult(requestCode, resultCode, data);
//
//        if(resultCode != RESULT_OK)
//            return;
//
//
//        switch (requestCode)
//        {
//            case PRFragment.REQUEST_CODE_WEBAPP:
//                String stringUri = MyContext.DEFAULT_WEBAPP_LOGOUT_ADDRESS;
//                Uri uri = Uri.parse(stringUri);
//
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
//                startActivity(browserIntent);
//                break;
//
//            default:
//                break;
//        }
//    }

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

            } else {
                // Permission request was denied.

                //Disattivo l'entrata con QR. Inserimento manuale valido.

                //uiUtils.makeToast(R.string.show_camera_permission_denied);

                //Menu menu = navView.getMenu();
                //menu.findItem(R.id.navigation_cassiere).setEnabled(false);
            }
        }
        // END_INCLUDE(onRequestPermissionsResult)
    }

    /**
     * Requests the {@link android.Manifest.permission#CAMERA} permission.
     * If an additional rationale should be displayed, the user has to launch the request from
     * a SnackBar that includes additional information.
     */
    private void requestCameraPermission() {
        /*
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            Snackbar.make(mLayout, R.string.camera_access_required,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSION_REQUEST_CAMERA);
                }
            }).show();

        } else {
            Snackbar.make(mLayout, R.string.camera_unavailable, Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        }
        */

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
    }
}
