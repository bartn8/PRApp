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

package com.prapp.ui.activity.main;

import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.prapp.R;
import com.prapp.model.db.enums.Ruolo;
import com.prapp.model.db.wrapper.WRuoliMembro;
import com.prapp.ui.activity.main.fragment.amministratore.AmministratoreFragment;
import com.prapp.ui.activity.main.fragment.cassiere.CassiereFragment;
import com.prapp.ui.activity.main.fragment.membro.MembroFragment;
import com.prapp.ui.activity.main.fragment.pr.PRFragment;
import com.prapp.ui.activity.main.fragment.utente.UtenteFragment;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainActivityInterface {


    private static final String CURRENT_FRAGMENT_KEY = "CURFRAG";

    public static final int ID_FRAGMENT_UTENTE = 0;
    public static final int ID_FRAGMENT_MEMBRO = 1;
    public static final int ID_FRAGMENT_PR = 2;
    public static final int ID_FRAGMENT_CASSIERE = 3;
    public static final int ID_FRAGMENT_AMMINISTRATORE = 4;


    private MainViewModel mainViewModel;

    private Set<Ruolo> diritti;

    private int currentFragment = 0;

    private UtenteFragment utenteFragment = UtenteFragment.newInstance();
    private MembroFragment membroFragment = MembroFragment.newInstance();
    private PRFragment prFragment = PRFragment.newInstance();
    private CassiereFragment cassiereFragment = CassiereFragment.newInstance();
    private AmministratoreFragment amminisratoreFragment = AmministratoreFragment.newInstance();

    @BindView(R.id.nav_view)
    public BottomNavigationView navView;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_utente:
                        currentFragment = ID_FRAGMENT_UTENTE;
                        cambiaTab(false);
                        return true;

                    case R.id.navigation_membro:
                        currentFragment = ID_FRAGMENT_MEMBRO;
                        cambiaTab(false);
                        return true;

                    case R.id.navigation_pr:
                        currentFragment = ID_FRAGMENT_PR;
                        cambiaTab(false);
                        return true;

                    case R.id.navigation_cassiere:
                        currentFragment = ID_FRAGMENT_CASSIERE;
                        cambiaTab(false);
                        return true;

//                    case R.id.navigation_amministratore:
//                        currentFragment = ID_FRAGMENT_AMMINISTRATORE;
//                        cambiaTab(false);
//                        return true;
                }

                return false;
            };

    /**
     * Cambia il fragment solamente in campo del navigatore di sotto.
     * Per esempio se CassiereFragment attiva un sotto fragment, non si usa questo metodo.
     *
     * @param setBottomNavigationBar se impostato imposta l'elemento selezionato nel bottom nav
     */
    private void cambiaTab(boolean setBottomNavigationBar) {
        switch (currentFragment) {
            case ID_FRAGMENT_UTENTE:
                if (!utenteFragment.isInLayout())
                    cambiaFragment(utenteFragment);

                if(setBottomNavigationBar)
                    navView.setSelectedItemId(R.id.navigation_utente);
                break;
            case ID_FRAGMENT_MEMBRO:
                if (!membroFragment.isInLayout())
                    cambiaFragment(membroFragment);

                if(setBottomNavigationBar)
                    navView.setSelectedItemId(R.id.navigation_membro);
                break;
            case ID_FRAGMENT_PR:
                if (!prFragment.isInLayout())
                    cambiaFragment(prFragment);

                if(setBottomNavigationBar)
                    navView.setSelectedItemId(R.id.navigation_pr);
                break;
            case ID_FRAGMENT_CASSIERE:
                if (!cassiereFragment.isInLayout())
                    cambiaFragment(cassiereFragment);

                if(setBottomNavigationBar)
                    navView.setSelectedItemId(R.id.navigation_cassiere);
                break;
//            case ID_FRAGMENT_AMMINISTRATORE:
//                if (!amminisratoreFragment.isInLayout())
//                    cambiaFragment(amminisratoreFragment);
//
//                if(setBottomNavigationBar)
//                    navView.setSelectedItemId(R.id.navigation_amministratore);
//                break;

            default:
                if (!utenteFragment.isInLayout())
                    cambiaFragment(utenteFragment);

                if(setBottomNavigationBar)
                    navView.setSelectedItemId(R.id.navigation_utente);
                break;
        }
    }

    //https://stackoverflow.com/questions/5658675/replacing-a-fragment-with-another-fragment-inside-activity-group

    @Override
    public void cambiaFragment(Fragment nuovoFragment) {
        // Create new transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //Animazione Up-Down
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down);

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack if needed
        transaction.replace(R.id.nav_container, nuovoFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    @Override
    public Fragment getNavFragment(int id) {
        switch (id){
            case ID_FRAGMENT_UTENTE: return utenteFragment;
            case ID_FRAGMENT_MEMBRO: return membroFragment;
            case ID_FRAGMENT_PR: return prFragment;
            case ID_FRAGMENT_CASSIERE: return cassiereFragment;
            case ID_FRAGMENT_AMMINISTRATORE: return amminisratoreFragment;
            default: return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        //Inizializzo i fragment
        amminisratoreFragment.holdInterface(this);
        utenteFragment.holdInterface(this);
        membroFragment.holdInterface(this);
        prFragment.holdInterface(this);
        cassiereFragment.holdInterface(this);

        //Inizializzo il view model e applico gli observer.
        mainViewModel = ViewModelProviders.of(this, new MainViewModelFactory()).get(MainViewModel.class);

        //Recupero i diritti dell'utente.
        WRuoliMembro dirittiUtente = mainViewModel.getDirittiUtente();

        diritti = dirittiUtente.getRuoli();

        //Controllo se era presente un fragment precedente.
        //https://medium.com/hootsuite-engineering/handling-orientation-changes-on-android-41a6b62cb43f

        if (savedInstanceState != null) {
            currentFragment = savedInstanceState.getInt(CURRENT_FRAGMENT_KEY);
        }

        //Imposto la schermata selezionata
        cambiaTab(true);

        //Imposto le schermate in base ai diritti posseduti.

        Menu menu = navView.getMenu();

        menu.findItem(R.id.navigation_pr).setEnabled(diritti.contains(Ruolo.PR));
        menu.findItem(R.id.navigation_cassiere).setEnabled(diritti.contains(Ruolo.CASSIERE));
//        menu.findItem(R.id.navigation_amministratore).setEnabled(diritti.contains(Diritto.AMMINISTRATORE));

        //Listener del menù sottostante
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());

    }

    //https://medium.com/hootsuite-engineering/handling-orientation-changes-on-android-41a6b62cb43f

    @Override
    public void onSaveInstanceState(@NotNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        //Devo salvare il fragment selezionato.
        savedInstanceState.putInt(CURRENT_FRAGMENT_KEY, currentFragment);
    }


    //Back button method.
    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        //Per i search???

        //Chiudo direttamente l'applicazione.
        setResult(RESULT_OK);
        finish();
    }

}
