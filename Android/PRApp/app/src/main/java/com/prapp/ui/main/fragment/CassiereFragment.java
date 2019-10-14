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

package com.prapp.ui.main.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.SparseArray;
import android.view.LayoutInflater;
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

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.vision.CameraSource;
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
import com.prapp.ui.UiUtils;
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
public class CassiereFragment extends Fragment implements WPrevenditaPlusAdapter.ButtonListener {

    private static final String TAG = CassiereFragment.class.getSimpleName();
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormat.shortTime();


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

    private MainViewModel mainViewModel;
    private Unbinder unbinder;
    private UiUtils uiUtils;
    private WPrevenditaPlusAdapter recyclerAdapter = new WPrevenditaPlusAdapter(this, 1);//Imposto al massimo un elemento per volta.


    //private Collection<BarcodeFormat> formats = Collections.singletonList(BarcodeFormat.QR_CODE);

    //Oggetto per il popup animato che dice l'esito dell'entrata.
    private Dialog popupEsito;

    //ROBA PER SCANNER--------------------------------------------------

    //Vecchia API deprecata
//    private Camera camera = Camera.open();

    //Nuova API
//    private CameraManager cameraManager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);

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

//    @BindView(R.id.fragment_cassiere_entrata_manuale_idEvento)
//    public EditText editTextIdEvento;

    @BindView(R.id.fragment_cassiere_entrata_manuale_codice)
    public EditText editTextCodice;

    @BindView(R.id.buttonEntrataManuale)
    public Button entrataManualeButton;

    @BindView(R.id.entrateRecyclerView)
    public RecyclerView entrateRecyclerView;

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

                //Se succede qualcosa di strano mando il codice in esecuzione sul thread principale.
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                });
            }

        }
    };

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
                recyclerAdapter.add(success);
                vibraSingolo();
            }
        }
    };

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

    //https://stackoverflow.com/questions/13950338/how-to-make-an-android-device-vibrate
    //https://gist.github.com/nieldeokar/e05fffe4d639dfabf0d57e96cb8055e2

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

    public CassiereFragment() {
        // Required empty public constructor
    }

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
                .setAutoFocusEnabled(true)
                .build();

        scannerSurfaceView.getHolder().addCallback(surfaceCallback);

        detector.setProcessor(detectorProcessor);

        //---------------------------------------------------------------------------------------


        return view;
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

    private void turnFlash(boolean isOn) {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            // only for 25 and newer versions
            CameraManager cameraManager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
            String cameraId = null;
            try {
                cameraId = cameraManager.getCameraIdList()[0];
                cameraManager.setTorchMode(cameraId, isOn);
            } catch (CameraAccessException e) {
                //Faccio un toast di errore
                uiUtils.makeToast(R.string.fragment_cassiere_impossibile_fare_flash);
            }
        } else {
            //Evito di usare la vecchia API.
            //Mi lamento dicendo di aggironare android.
            uiUtils.makeToast(R.string.fragment_cassiere_impossibile_fare_flash);
        }

        isFlashOn = isOn;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isScanOn) {
            turnScanOff();
            turnFlash(false);
        }
    }

    @Override
    public void onPause() {
        if (isScanOn) {
            turnScanOff();
            turnFlash(false);
        }

        super.onPause();
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }


    @OnClick(R.id.flashlightSwitch)
    public void onClickFlash(View view) {
        if (isScanOn) {
            if (isFlashOn) {
                turnFlash(false);
            } else {
                turnFlash(true);
            }
        } else {
            turnFlash(false);
        }
    }

    @OnClick(R.id.scansioneQRSwitch)
    public void onClickScansioneQR(View view) {
        if (isScanOn) {
            turnScanOff();
            turnFlash(false);
        } else {
            turnScanOn();
        }
    }

    private void turnScanOff() {
        cameraSource.stop();
        scansioneQRSwitch.setChecked(false);
        isScanOn = false;
    }

    private void turnScanOn() {
        try {
            cameraSource.start(scannerSurfaceView.getHolder());
            scansioneQRSwitch.setChecked(true);
            isScanOn = true;
        } catch (IOException e) {
            uiUtils.makeToast(R.string.fragment_cassiere_impossibile_avviare_scanner);
        }
    }

    @Override
    public void onApprovaClick(WPrevenditaPlusAdapter.WPrevenditaPlusWrapper prevendita) {
        //Faccio partire l'entrata. Levo temporaneamente dal recycler la prevendita:
        //Verrà riaggiunta in caso di errore
        mainViewModel.timbraEntrata(prevendita.getData());

        //Si può ri-scannerizzare perchè la cosa non è stata gradita dallo staff.
        mainViewModel.remove(prevendita.getData());
        recyclerAdapter.remove(prevendita);
    }

    @Override
    public void onAnnullaClick(WPrevenditaPlusAdapter.WPrevenditaPlusWrapper prevendita) {
        uiUtils.makeToast(R.string.fragment_cassiere_toast_annullata_label);

        //Levo semplicemente dagli archivi.
        mainViewModel.remove(prevendita.getData());
        recyclerAdapter.remove(prevendita);
    }

    @OnClick(R.id.buttonEntrataManuale)
    public void onEntrataManualeClick() {
        Integer idPrevendita = Integer.parseInt(editTextIdPrevendita.getText().toString());
        //Integer idEvento = Integer.parseInt(editTextIdEvento.getText().toString());
        Integer idEvento = mainViewModel.getEvento().getId();
        String codice = editTextCodice.getText().toString();

        NetWEntrata netWEntrata = new NetWEntrata(idPrevendita, idEvento, codice);

        mainViewModel.getInformazioniPrevendita(netWEntrata);

        //Pulisco i campi
        editTextIdPrevendita.getText().clear();
        editTextCodice.getText().clear();
    }

    //https://www.awsrh.com/2017/10/custom-pop-up-window-with-android-studio.html
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

    public void showSuccessPopup() {
        showPopup(R.drawable.ic_iconfinder_success, R.string.fragment_cassiere_popup_success, null);
    }

    public void showErrorPopup(String error) {
        showPopup(R.drawable.ic_iconfinder_error, R.string.fragment_cassiere_popup_errore_formatted, error);
    }

}
