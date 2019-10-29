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
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

import com.google.android.gms.samples.vision.barcodereader.ui.camera.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.prapp.R;
import com.prapp.model.db.wrapper.WEntrata;
import com.prapp.model.db.wrapper.WPrevenditaPlus;
import com.prapp.model.net.wrapper.NetWEntrata;
import com.prapp.ui.Result;
import com.prapp.ui.utils.UiUtils;
import com.prapp.ui.utils.InterfaceHolder;
import com.prapp.ui.main.MainActivityInterface;
import com.prapp.ui.main.MainViewModel;
import com.prapp.ui.main.adapter.WPrevenditaPlusAdapter;

import org.jetbrains.annotations.NotNull;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
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
public class CassiereFragment extends Fragment implements WPrevenditaPlusAdapter.ButtonListener, InterfaceHolder<MainActivityInterface> {

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
    private MainViewModel mainViewModel;

    /**
     * Robo per discollegarsi dalle view.
     */
    private Unbinder unbinder;

    /**
     * Utilty per la grafica
     */
    private UiUtils uiUtils;

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
    private WPrevenditaPlusAdapter recyclerAdapter = new WPrevenditaPlusAdapter(this, 1, true, true);//Imposto al massimo un elemento per volta.

    /**
     * Oggetto per il popup animato che dice l'esito dell'entrata.
     */
    private Dialog popupEsito;

    private boolean isAutoApprovaOn = false;

    //ROBA PER SCANNER--------------------------------------------------

    private boolean isScanOn = false;
    private boolean isFlashOn = false;

    private BarcodeDetector detector;
    private CameraSource cameraSource;

    @BindView(R.id.scanner_surfaceview)
    public SurfaceView scannerSurfaceView;

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
     * Callback usato per rilasciare la camera quando si distrugge la view che mostra la camera.
     */
    private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            //Attivazione camera manuale.
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            cameraSource.stop();
        }
    };

    /**
     * Callback usata quando il processore di QR ne trova uno:
     * Cerco di decodificare il codice QR tramite il formato JSON.
     * Poi lo passo al View-Model che ricava altre informazioni tramite server.
     */
    private Detector.Processor<Barcode> detectorProcessor = new Detector.Processor<Barcode>() {
        @Override
        public void release() {

        }

        @Override
        public void receiveDetections(Detector.Detections<Barcode> detections) {
            final SparseArray<Barcode> items = detections.getDetectedItems();

            if (items.size() != 0) {
                //Decodifico il codice e lo mando
                Gson gson = new Gson();

                Barcode barcode = items.valueAt(0);

                try {
                    NetWEntrata netWEntrata = gson.fromJson(barcode.displayValue, NetWEntrata.class);
                    mainViewModel.getInformazioniPrevendita(netWEntrata);
                } catch (JsonParseException e) {
                    //Non fare nulla.
                    //Non verrà aggiunta la lettura.
                }

            }

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
                uiUtils.showError(integerError);

            else if (error != null)
                uiUtils.showError(error);

            else if (success != null) {
                //Devo controllare se attivo Auto-Approva: in caso positivo approvo direttamente
                if(isAutoApprovaOn){
                    mainViewModel.timbraEntrata(success);
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
                    uiUtils.makeToast(R.string.fragment_cassiere_toast_errore_formatted, localizedMessage);
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
                        uiUtils.makeToast(R.string.fragment_cassiere_toast_errore_formatted, localizedMessage);
                    }
                }
            } else if (wEntrataResult.isSuccessPresent()) {
                //uiUtils.makeToast(R.string.fragment_cassiere_toast_approvata_formatted, success.getTimestampEntrata().toString(TIME_FORMAT));

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
        uiUtils = UiUtils.getInstance(context);
        this.popupEsito = new Dialog(context);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cassiere, container, false);

        //Mitico butterknife per fare il collegamento tra XML e oggetti.
        unbinder = ButterKnife.bind(this, view);

        //View model per richiamare il server.
        mainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        mainViewModel.getInfoPrevenditaResult().observe(this, getInfoPrevenditaObserver);
        mainViewModel.getEntrataResult().observe(this, timbraEntrataObserver);

        //Imposto il recyler view. Quello che fa vedere le entrate.
        entrateRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        entrateRecyclerView.setHasFixedSize(false);
        entrateRecyclerView.setNestedScrollingEnabled(false);
        entrateRecyclerView.setAdapter(recyclerAdapter);

        //ROBA PER LO SCANNER--------------------

        //Abilito il pulsante se disponibile il flash.
        flashlightSwitch.setEnabled(hasFlash());

        //https://www.html.it/pag/63571/leggere-codici-a-barre-e-qr-code/

        detector = new BarcodeDetector.Builder(getContext())
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        if (!detector.isOperational()) {
            //Lo scanner non funziona:
            //Disabilito i pulsanti flash e attiva scansione.
            scansioneQRSwitch.setEnabled(false);
            flashlightSwitch.setEnabled(false);
            //Invio anche un messaggio di errore.
            uiUtils.makeToast(R.string.fragment_cassiere_impossibile_avviare_scanner);
        }

        // istanziamo un oggetto CameraSource collegata al detector
        cameraSource = new CameraSource
                .Builder(getContext(), detector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                //.setRequestedPreviewSize(scannerSurfaceView.getWidth(), scannerSurfaceView.getHeight())
                .setRequestedFps(15.0f)
                .setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO)
                .build();

        scannerSurfaceView.getHolder().addCallback(surfaceCallback);

        detector.setProcessor(detectorProcessor);

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
        mainViewModel.timbraEntrata(prevendita.getData());

        //Si può ri-scannerizzare perchè la cosa non è stata gradita dallo staff.
        mainViewModel.remove(prevendita.getData());
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
        uiUtils.makeToast(R.string.fragment_cassiere_toast_annullata_label);

        //Levo semplicemente dagli archivi.
        mainViewModel.remove(prevendita.getData());
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
            //Integer idEvento = Integer.parseInt(editTextIdEvento.getText().toString());
            Integer idEvento = mainViewModel.getEvento().getId();
            String codice = editTextCodice.getText().toString();

            NetWEntrata netWEntrata = new NetWEntrata(idPrevendita, idEvento, codice);

            mainViewModel.getInformazioniPrevendita(netWEntrata);

            //Pulisco i campi
            editTextIdPrevendita.getText().clear();
            editTextCodice.getText().clear();
        }catch (NumberFormatException ex){
            uiUtils.makeToast(R.string.fragment_cassiere_entrata_manuale_dati_non_validi);
        }
    }

    //https://www.awsrh.com/2017/10/custom-pop-up-window-with-android-studio.html

    /**
     * Mostra un popup personalizzato.
     *
     * @param imageResId ID immagine da visualizzare
     * @param textResId ID testo da visualizzare
     * @param text Testo custom da visualizzare (come arg di textResId)
     */
    private void showPopup(int imageResId, int textResId, String text) {
        ImageView esitoImage;
        TextView esitoText;

        popupEsito.setContentView(R.layout.esito_entrata_popup);

        esitoImage = popupEsito.findViewById(R.id.esitoEntrataImage);
        esitoText = popupEsito.findViewById(R.id.esitoEntrataText);

        //Imposto la schermata di successo
        esitoImage.setImageResource(imageResId);

        //Costruisco il testo se bisogno:
        if (text != null) {
            String esitoTextString = getContext().getString(textResId, text);
            esitoText.setText(esitoTextString);
        } else {
            esitoText.setText(textResId);
        }

        //Faccio l'animazione bella.
        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(500);

        esitoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupEsito.dismiss();
            }
        });

        popupEsito.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupEsito.show();

        esitoImage.setAnimation(rotate);
        esitoImage.animate();
    }

    /**
     * Mostra un popup di successo.
     */
    private void showSuccessPopup() {
        showPopup(R.drawable.ic_iconfinder_success, R.string.fragment_cassiere_popup_success, null);
    }

    /**
     * Mostra un popup di errore.
     * @param error stringa con l'errore da mostrare.
     */
    private void showErrorPopup(String error) {
        showPopup(R.drawable.ic_iconfinder_error, R.string.fragment_cassiere_popup_errore_formatted, error);
    }


    /**
     * Abilita/Disabilita la scansione dei QR.
     * Prima controlla se ho i permessi.
     * @param how vero se si vuole attivare la scansione.
     */
    private void turnScan(boolean how) {
        if(how){
            if (ActivityCompat.checkSelfPermission(getContext(), NEEDED_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
                try {
                    cameraSource.start(scannerSurfaceView.getHolder());
                    scansioneQRSwitch.setChecked(true);
                    isScanOn = true;
                } catch (IOException e) {
                    uiUtils.makeToast(R.string.fragment_cassiere_impossibile_avviare_scanner);
                }
            }else{
                //uiUtils.makeToast(R.string.fragment_cassiere_impossibile_avviare_scanner);
                checkCameraPermission();
                scansioneQRSwitch.setChecked(false);
                isScanOn = false;
            }
        }else{
            cameraSource.stop();
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
            flashlightSwitch.setChecked(false);
            isFlashOn = false;
            return;
        }

        cameraSource.setFlashMode(isOn ? Camera.Parameters.FLASH_MODE_TORCH : Camera.Parameters.FLASH_MODE_OFF);
        isFlashOn = isOn;
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
                uiUtils.makeToast(R.string.show_camera_permission_granted);
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
