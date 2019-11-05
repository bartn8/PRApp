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

package com.prapp.ui.main.fragment.cassiere;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.prapp.R;
import com.prapp.model.db.wrapper.WEntrata;
import com.prapp.model.db.wrapper.WPrevenditaPlus;
import com.prapp.model.net.wrapper.NetWEntrata;
import com.prapp.ui.Result;
import com.prapp.ui.main.MainActivityInterface;
import com.prapp.ui.main.adapter.WPrevenditaPlusAdapter;
import com.prapp.ui.utils.InterfaceHolder;
import com.prapp.ui.utils.PopupUtil;
import com.prapp.ui.utils.UiUtil;

import org.jetbrains.annotations.NotNull;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CassiereFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
//https://github.com/journeyapps/zxing-android-embedded/blob/master/sample/src/main/java/example/zxing/CustomScannerActivity.java
public class CassiereFragment extends Fragment implements DecoratedBarcodeView.TorchListener, WPrevenditaPlusAdapter.ButtonListener, InterfaceHolder<MainActivityInterface> {

    private static final String TAG = CassiereFragment.class.getSimpleName();
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormat.shortTime();

    private static final String NEEDED_PERMISSION = Manifest.permission.CAMERA;
    private static final int PERMISSION_REQUEST_CAMERA = 0;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CassiereFragment.
     */
    public static CassiereFragment newInstance() {
        CassiereFragment fragment = new CassiereFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * View-Model per interfacciarsi con il server.
     */
    private CassiereViewModel viewModel;

    /**
     * Robo per discollegarsi dalle view.
     */
    private Unbinder unbinder;

    /**
     * Utilty per la grafica
     */
    private UiUtil uiUtil;

    /**
     * Utily per generare popup.
     */
    private PopupUtil popupUtil;

    /**
     * Interfaccia usata per comunicare con l'activity madre.
     */
    private MainActivityInterface mainActivityInterface;

    @Override
    public void holdInterface(@NotNull MainActivityInterface mainActivityInterface){
        this.mainActivityInterface = mainActivityInterface;
    }

    @Override
    public boolean isInterfaceSet(){
        return this.mainActivityInterface != null;
    }

    /**
     * Adattatore per far vedere nella recycler view le prevendite che si vogliono approvare.
     */
    private WPrevenditaPlusAdapter recyclerAdapter;

    private boolean isAutoApprovaOn = false;

    //ROBA PER SCANNER--------------------------------------------------

    private Collection<BarcodeFormat> formats = Collections.singletonList(BarcodeFormat.QR_CODE);

    private boolean isScanOn = false;
    private boolean isFlashOn = false;

    @BindView(R.id.scanner_barcodeview)
    public DecoratedBarcodeView decoratedBarcodeView;

    @BindView(R.id.scansioneQRSwitch)
    public ToggleButton scansioneQRSwitch;

    @BindView(R.id.flashlightSwitch)
    public ToggleButton flashlightSwitch;

    //-----------------------------------------------------------

    @BindView(R.id.fragment_cassiere_entrata_manuale_idPrevendita)
    public EditText editTextIdPrevendita;

    @BindView(R.id.fragment_cassiere_entrata_manuale_codice)
    public EditText editTextCodice;

    @BindView(R.id.buttonEntrataManuale)
    public Button entrataManualeButton;

    @BindView(R.id.fragment_cassiere_recyclerView)
    public RecyclerView entrateRecyclerView;

    @BindView(R.id.autoApprovaWarning)
    public CardView autoApprovaWarning;

    /**
     * Callback usata quando il processore di QR ne trova uno:
     * Cerco di decodificare il codice QR tramite il formato JSON.
     * Poi lo passo al View-Model che ricava altre informazioni tramite server.
     */
    private BarcodeCallback callback = new BarcodeCallback() {

        @Override
        public void barcodeResult(BarcodeResult result) {
            String text = result.getText();

            if (text == null || text.isEmpty()) {
                // Prevent duplicate scans
                return;
            }

            //Decodifico il codice e lo mando
            Gson gson = new Gson();
            try {
                NetWEntrata netWEntrata = gson.fromJson(text, NetWEntrata.class);
                viewModel.getInformazioniPrevendita(netWEntrata);
            } catch (JsonParseException e) {
                //Non fare nulla.
                //Non verrà aggiunta la lettura.
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };

    /**
     * Quando il server restituisce le informazioni della prevendita scannerizzata
     * lo mando all'adapter e vibro singolo.
     * In caso di errore mostro un toast. Il popup viene usato solo in caso di approvazione.
     *
     * NOTA: Se è attivo l'auto approva, passo direttamente ad approvazione.
     */
    private Observer<Result<WPrevenditaPlus, Void>> getInfoPrevenditaObserver = new Observer<Result<WPrevenditaPlus, Void>>() {
        @Override
        public void onChanged(Result<WPrevenditaPlus, Void> wPrevenditaPlusResult) {
            if (wPrevenditaPlusResult == null) {
                return;
            }

            Integer integerError = wPrevenditaPlusResult.getIntegerError();
            List<Exception> error = wPrevenditaPlusResult.getError();
            WPrevenditaPlus success = wPrevenditaPlusResult.getSuccess();

            if (integerError != null)
                uiUtil.showError(integerError);

            else if (error != null)
                uiUtil.showError(error);

            else if (success != null) {
                //Devo controllare se attivo Auto-Approva: in caso positivo approvo direttamente
                if(isAutoApprovaOn){
                    viewModel.timbraEntrata(success);
                }else{
                    recyclerAdapter.add(success);
                    vibraSingolo();
                }

            }
        }
    };

    /**
     * Observer del risultato di un tentativo di entrata:
     *   In caso di errore: mostro un popup negativo con errore.
     *   In caso di successo: mostro un popup positivo + doppia vibrazione.
     */
    private Observer<Result<WEntrata, WPrevenditaPlus>> timbraEntrataObserver = new Observer<Result<WEntrata, WPrevenditaPlus>>() {
        @Override
        public void onChanged(Result<WEntrata, WPrevenditaPlus> wEntrataResult) {
            if (wEntrataResult == null) {
                return;
            }

            Integer integerError = wEntrataResult.getIntegerError();
            List<Exception> error = wEntrataResult.getError();
            WEntrata success = wEntrataResult.getSuccess();
            WPrevenditaPlus extra = wEntrataResult.getExtra();

            if (wEntrataResult.isIntegerErrorPresent()) {
                String localizedMessage = getString(integerError);

                if (wEntrataResult.isExtraPresent()) {
                    //Sistema diverso, mostro il popup senza reinserire nel recycler view.
                    //recyclerAdapter.add(extra, localizedMessage);
                    showErrorPopup(localizedMessage);
                } else {
                    //Errore strano.
                    uiUtil.makeToast(R.string.fragment_cassiere_toast_errore_formatted, localizedMessage);
                }
            } else if (wEntrataResult.isErrorPresent()) {
                if (error.size() > 0) {
                    Exception exception = error.get(0);
                    String localizedMessage = exception.getLocalizedMessage();

                    if (wEntrataResult.isExtraPresent()) {
                        //Sistema diverso, mostro il popup senza reinserire nel recycler view.
                        //recyclerAdapter.add(extra, localizedMessage);
                        showErrorPopup(localizedMessage);
                    } else {
                        //Errore strano.
                        uiUtil.makeToast(R.string.fragment_cassiere_toast_errore_formatted, localizedMessage);
                    }
                }
            } else if (wEntrataResult.isSuccessPresent()) {
                //uiUtil.makeToast(R.string.fragment_cassiere_toast_approvata_formatted, success.getTimestampEntrata().toString(TIME_FORMAT));

                //Mostro il popup in modo da essere più incisivo
                showSuccessPopup();

                vibraDoppio();
            }
        }
    };

    public CassiereFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);    //Opzione menu
    }

    //ROBA MENU------------------------------------------------------------------------------

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.cassiere_menu, menu);

        //Disattivo l'opzione scansiona
        menu.removeItem(R.id.cassiere_scansionaItem);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.cassiere_autoApprovaItem:
                //Toggle dello stato
                isAutoApprovaOn = !isAutoApprovaOn;

                //Devo aggiornare la ui.
                toggleAutoApprovaWarning();

                return true;
            case R.id.cassiere_genteEntrataItem:
                //Istanzio un nuovo fragment lista e lo inizializzo per le prevendite approvate.
                if(isInterfaceSet()){
                    CassiereSubFragmentLista cassiereSubFragment = CassiereSubFragmentLista.newInstance(CassiereSubFragmentLista.MODE_LIST_TIMBRATE);
                    cassiereSubFragment.holdInterface(mainActivityInterface);
                    mainActivityInterface.cambiaFragment(cassiereSubFragment);
                }

                return true;
            case R.id.cassiere_genteNonEntrataItem:
                //Istanzio un nuovo fragment lista e lo inizializzo per le prevendite non approvate.
                if(isInterfaceSet()){
                    CassiereSubFragmentLista cassiereSubFragment = CassiereSubFragmentLista.newInstance(CassiereSubFragmentLista.MODE_LIST_NON_TIMBRATE);
                    cassiereSubFragment.holdInterface(mainActivityInterface);
                    mainActivityInterface.cambiaFragment(cassiereSubFragment);
                }

                return true;
            case R.id.cassiere_statisticheEventoItem:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void toggleAutoApprovaWarning(){
        autoApprovaWarning.setVisibility(isAutoApprovaOn ? View.VISIBLE : View.GONE);
    }

    //--------------------------------------------------------------------------------------

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        uiUtil = new UiUtil(context);
        popupUtil = new PopupUtil(context);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cassiere, container, false);

        //Mitico butterknife per fare il collegamento tra XML e oggetti.
        unbinder = ButterKnife.bind(this, view);

        recyclerAdapter = new WPrevenditaPlusAdapter(1, true, false);//Imposto al massimo un elemento per volta.
        recyclerAdapter.setButtonListener(this);

        //View model per richiamare il server.
        viewModel = ViewModelProviders.of(getActivity()).get(CassiereViewModel.class);
        viewModel.getInfoPrevenditaResult().observe(this, getInfoPrevenditaObserver);
        viewModel.getEntrataResult().observe(this, timbraEntrataObserver);

        //Imposto il recyler view. Quello che fa vedere le entrate.
        entrateRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        entrateRecyclerView.setHasFixedSize(false);
        entrateRecyclerView.setNestedScrollingEnabled(false);
        entrateRecyclerView.setAdapter(recyclerAdapter);

        //ROBA PER LO SCANNER--------------------

        //Abilito il pulsante se disponibile il flash.
        flashlightSwitch.setEnabled(hasFlash());

        //Imposto la view responabile dello scanner qr.
        decoratedBarcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        decoratedBarcodeView.initializeFromIntent(getActivity().getIntent());
        decoratedBarcodeView.setTorchListener(this);

        //Decodifica continuamente.
        decoratedBarcodeView.decodeContinuous(callback);


        //Devo capire come rimetterlo
//        if (!detector.isOperational()) {
//            //Lo scanner non funziona:
//            //Disabilito i pulsanti flash e attiva scansione.
//            scansioneQRSwitch.setEnabled(false);
//            flashlightSwitch.setEnabled(false);
//            //Invio anche un messaggio di errore.
//            uiUtil.makeToast(R.string.fragment_cassiere_impossibile_avviare_scanner);
//        }

        //---------------------------------------------------------------------------------------


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        //Quando il fragment viene ripreso devo disattivare la scansione.
        if (isScanOn) {
            turnScan(false);
            turnFlash(false);
        }
    }

    @Override
    public void onPause() {
        //Quando il fragment viene sospeso devo disattivare la scansione.
        if (isScanOn) {
            turnScan(false);
            turnFlash(false);
        }

        super.onPause();
    }

    @Override
    public void onDestroyView() {
        //Rimuvo il binding di ButterKnife
        unbinder.unbind();
        super.onDestroyView();
    }

    /**
     * Quando faccio il click sul pulsante flash faccio il toggle del flash.
     *
     * @param view non usato
     */
    @OnClick(R.id.flashlightSwitch)
    public void onClickFlash(View view) {
        turnFlash(!isFlashOn);
    }

    /**
     * Quando faccio il click sul pulsante scansione faccio il toggle della scansione QR.
     *
     * @param view non usata
     */
    @OnClick(R.id.scansioneQRSwitch)
    public void onClickScansioneQR(View view) {
        if (isScanOn) {
            turnScan(false);
            turnFlash(false);
        } else {
            turnScan(true);
        }
    }

    /**
     * Quando clicco su approva dico al View-Model di provare ad accettare la prevendita.
     *
     * @param prevendita Wrapper con la prevendita
     */
    @Override
    public void onApprovaClick(WPrevenditaPlusAdapter.WPrevenditaPlusWrapper prevendita) {
        //Faccio partire l'entrata. Levo temporaneamente dal recycler la prevendita:
        //Verrà riaggiunta in caso di errore
        viewModel.timbraEntrata(prevendita.getData());

        //Si può ri-scannerizzare perchè la cosa non è stata gradita dallo staff.
        viewModel.remove(prevendita.getData());
        recyclerAdapter.remove(prevendita);
    }

    /**
     * Quando clicco su annulla faccio finta di non aver scannerizzato la prevendita.
     * Devo comunque dirlo al View-Model il quale ha una struttra interna per salvarsi le prevendite.
     * Al livello server non viene visto nulla.
     *
     * @param prevendita prevendita da rimuovere.
     */
    @Override
    public void onAnnullaClick(WPrevenditaPlusAdapter.WPrevenditaPlusWrapper prevendita) {
        uiUtil.makeToast(R.string.fragment_cassiere_toast_annullata_label);

        //Levo semplicemente dagli archivi.
        viewModel.remove(prevendita.getData());
        recyclerAdapter.remove(prevendita);
    }

    /**
     * Handler del pulsante di entrata manuale:
     * Cerca di fare il parsing degli argomenti inseriti e
     * prova a popolare la lista delle prevendite da approvare con quella inserita.
     */
    @OnClick(R.id.buttonEntrataManuale)
    public void onEntrataManualeClick() {
        try{
            Integer idPrevendita = Integer.parseInt(editTextIdPrevendita.getText().toString());
            Integer idEvento = viewModel.getEvento().getId();
            String codice = editTextCodice.getText().toString();

            NetWEntrata netWEntrata = new NetWEntrata(idPrevendita, idEvento, codice);

            viewModel.getInformazioniPrevendita(netWEntrata);

            //Pulisco i campi
            editTextIdPrevendita.getText().clear();
            editTextCodice.getText().clear();
        }catch (NumberFormatException ex){
            uiUtil.makeToast(R.string.fragment_cassiere_entrata_manuale_dati_non_validi);
        }
    }

    /**
     * Mostra un popup di successo.
     */
    private void showSuccessPopup() {
        popupUtil.showEsitoPopup(getActivity(), R.drawable.ic_iconfinder_success, R.string.fragment_cassiere_popup_success, null);
    }

    /**
     * Mostra un popup di errore.
     * @param error stringa con l'errore da mostrare.
     */
    private void showErrorPopup(String error) {
        popupUtil.showEsitoPopup(getActivity(), R.drawable.ic_iconfinder_error, R.string.popup_esito_errore_formatted, error);
    }


    /**
     * Abilita/Disabilita la scansione dei QR.
     * Prima controlla se ho i permessi.
     * @param how vero se si vuole attivare la scansione.
     */
    private void turnScan(boolean how) {
        if(how){
            if (ActivityCompat.checkSelfPermission(getContext(), NEEDED_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
                decoratedBarcodeView.resume();
                scansioneQRSwitch.setChecked(true);
                isScanOn = true;
            }else{
                //uiUtil.makeToast(R.string.fragment_cassiere_impossibile_avviare_scanner);
                checkCameraPermission();
                scansioneQRSwitch.setChecked(false);
                isScanOn = false;
            }
        }else{
            decoratedBarcodeView.pauseAndWait();
            scansioneQRSwitch.setChecked(false);
            isScanOn = false;
        }
    }

    /**
     * Check if the device's camera has a Flashlight.
     *
     * @return true if there is Flashlight, otherwise false.
     */
    private boolean hasFlash() {
        return getContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    /**
     * Imposta il flash.
     * @param isOn true se si vuole accendere il flash
     */
    private void turnFlash(boolean isOn) {
        //Verifico che la scansione sia attiva, altrimenti disattivo il flash.
        if(!isScanOn && isOn){
            isFlashOn = false;
            return;
        }

        if(isOn){
            decoratedBarcodeView.setTorchOn();
        }else{
            decoratedBarcodeView.setTorchOff();
        }

        isFlashOn = isOn;
    }

    @Override
    public void onTorchOn() {
        flashlightSwitch.setChecked(true);
    }

    @Override
    public void onTorchOff() {
        flashlightSwitch.setChecked(false);
    }

    //https://stackoverflow.com/questions/13950338/how-to-make-an-android-device-vibrate
    //https://gist.github.com/nieldeokar/e05fffe4d639dfabf0d57e96cb8055e2

    /**
     * Fa vibrare il telefono una volta.
     */
    private void vibraSingolo() {
        Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibrator.vibrate(200);
        }
    }

    /**
     * Fa vibrare il telefono due volte.
     */
    private void vibraDoppio() {
        // Get instance of Vibrator from current Context
        Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);

        // Start without a delay
        // Each element then alternates between vibrate, sleep, vibrate, sleep...
        long[] pattern = {0, 200, 200, 200, 0};


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            VibrationEffect waveform = VibrationEffect.createWaveform(pattern, -1);
            vibrator.vibrate(waveform);
        } else {
            //deprecated in API 26
            // The '-1' here means to vibrate once, as '-1' is out of bounds in the pattern array
            vibrator.vibrate(pattern, -1);
        }
    }

    //Camera permission methods.

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                uiUtil.makeToast(R.string.show_camera_permission_granted);
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
        if (ActivityCompat.checkSelfPermission(getContext(), NEEDED_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), NEEDED_PERMISSION)) {
                new AlertDialog.Builder(getContext())
                        .setMessage(R.string.show_camera_permission_request)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{NEEDED_PERMISSION},
                                        PERMISSION_REQUEST_CAMERA);
                            }
                        })
                        .show();
            }else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            }
        }
    }

}
