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


import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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
import com.prapp.ui.UiUtils;
import com.prapp.ui.main.MainViewModel;
import com.prapp.ui.main.adapter.WPrevenditaPlusAdapter;

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
public class CassiereFragment extends Fragment implements DecoratedBarcodeView.TorchListener, WPrevenditaPlusAdapter.ButtonListener {

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
    private WPrevenditaPlusAdapter recyclerAdapter = new WPrevenditaPlusAdapter(this);

    private boolean isScanOn = false;
    private boolean isFlashOn = false;
    private Collection<BarcodeFormat> formats = Collections.singletonList(BarcodeFormat.QR_CODE);

    @BindView(R.id.zxing_barcode_scanner)
    public DecoratedBarcodeView barcodeScannerView;

    @BindView(R.id.buttonScansioneQR)
    public Button scansioneQRButton;

    @BindView(R.id.buttonFlash)
    public Button switchFlashlightButton;

    @BindView(R.id.fragment_cassiere_entrata_manuale_idPrevendita)
    public EditText editTextIdPrevendita;

    @BindView(R.id.fragment_cassiere_entrata_manuale_idEvento)
    public EditText editTextIdEvento;

    @BindView(R.id.fragment_cassiere_entrata_manuale_codice)
    public EditText editTextCodice;

    @BindView(R.id.buttonEntrataManuale)
    public Button entrataManualeButton;

    @BindView(R.id.entrateRecyclerView)
    public RecyclerView entrateRecyclerView;

    private BarcodeCallback callback = new BarcodeCallback() {

        private Gson gson = new Gson();

        @Override
        public void barcodeResult(BarcodeResult result) {
            String text = result.getText();
            if (text == null || text.isEmpty()) {
                // Prevent duplicate scans
                return;
            }

            try {
                NetWEntrata netWEntrata = gson.fromJson(text, NetWEntrata.class);
                mainViewModel.getInformazioniPrevendita(netWEntrata);
            } catch (JsonParseException e) {
                //Non fare nulla.
                //Non verrà aggiunta la lettura.
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

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
                    recyclerAdapter.add(extra, localizedMessage);
                } else {
                    uiUtils.makeToast(R.string.fragment_cassiere_toast_errore_formatted, localizedMessage);
                }
            } else if (wEntrataResult.isErrorPresent()) {
                if (error.size() > 0) {
                    Exception exception = error.get(0);
                    String localizedMessage = exception.getLocalizedMessage();

                    if (wEntrataResult.isExtraPresent()) {
                        recyclerAdapter.add(extra, localizedMessage);
                    } else {
                        uiUtils.makeToast(R.string.fragment_cassiere_toast_errore_formatted, localizedMessage);
                    }
                }
            } else if (wEntrataResult.isSuccessPresent()) {
                uiUtils.makeToast(R.string.fragment_cassiere_toast_approvata_formatted, success.getTimestampEntrata().toString(TIME_FORMAT));
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

        //Abilito il pulsante se disponibile il flash.
        switchFlashlightButton.setEnabled(hasFlash());


        //Imposto la view responabile dello scanner qr.
        barcodeScannerView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeScannerView.initializeFromIntent(getActivity().getIntent());//?????
        barcodeScannerView.setTorchListener(this);

        //Decodifica continuamente.
        barcodeScannerView.decodeContinuous(callback);


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

    private void switchFlashlight(View view) {
        if (isScanOn) {
            if (isFlashOn) {
                turnFlashOff();
            } else {
                turnFlashOn();
            }
        } else {
            turnFlashOff();
        }
    }

    private void turnFlashOff() {
        barcodeScannerView.setTorchOff();
        isFlashOn = false;
    }

    private void turnFlashOn() {
        barcodeScannerView.setTorchOn();
        isFlashOn = true;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isScanOn)
            barcodeScannerView.resume();
    }

    @Override
    public void onPause() {
        if (isScanOn)
            turnScanOff();

        super.onPause();
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }


    @Override
    public void onTorchOn() {
        switchFlashlightButton.setBackgroundResource(R.color.primaryColorRed);
    }

    @Override
    public void onTorchOff() {
        switchFlashlightButton.setBackgroundResource(R.color.primaryButtonColor);
    }

    @OnClick(R.id.buttonFlash)
    public void onClickFlash(View view) {
        switchFlashlight(view);
    }

    @OnClick(R.id.buttonScansioneQR)
    public void onClickScansioneQR(View view) {
        if (isScanOn) {
            turnScanOff();
            turnFlashOff();
        } else {
            turnScanOn();
        }
    }

    private void turnScanOff() {
        scansioneQRButton.setBackgroundResource(R.color.primaryColorRed);
        barcodeScannerView.pauseAndWait();
        isScanOn = false;
    }

    private void turnScanOn() {
        scansioneQRButton.setBackgroundResource(R.color.primaryButtonColor);
        barcodeScannerView.resume();
        isScanOn = true;
    }

    @Override
    public void onApprovaClick(WPrevenditaPlusAdapter.WPrevenditaPlusWrapper prevendita) {
        //Faccio partire l'entrata. Levo temporaneamente dal recycler la prevendita:
        //Verrà riaggiunta in caso di errore
        mainViewModel.timbraEntrata(prevendita.getData());
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
        Integer idEvento = Integer.parseInt(editTextIdEvento.getText().toString());
        String codice = editTextCodice.getText().toString();

        NetWEntrata netWEntrata = new NetWEntrata(idPrevendita, idEvento, codice);

        mainViewModel.getInformazioniPrevendita(netWEntrata);
    }
}
