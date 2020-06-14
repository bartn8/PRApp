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

package com.prapp.ui.activity.main.fragment.pr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import androidx.core.content.FileProvider;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.prapp.PRAppApplication;
import com.prapp.R;
import com.prapp.model.MyContext;
import com.prapp.model.db.enums.StatoPrevendita;
import com.prapp.model.db.wrapper.WPrevendita;
import com.prapp.model.db.wrapper.WPrevenditaPlus;
import com.prapp.model.db.wrapper.WTipoPrevendita;
import com.prapp.model.net.manager.ManagerMembro;
import com.prapp.model.net.manager.ManagerPR;
import com.prapp.model.net.wrapper.insert.InsertNetWPrevendita;
import com.prapp.model.net.wrapper.update.UpdateNetWPrevendita;
import com.prapp.ui.AbstractViewModel;
import com.prapp.ui.Result;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PRViewModel extends AbstractViewModel {

    public static final String TAG = PRViewModel.class.getSimpleName();

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.shortDate();

    public static final int CLIENTE_SEARCH_MODE = 0;
    public static final int CLIENTE_ADD_MODE = 1;
    public static final int CLIENTE_SELECT_MODE = 2;

    public static final int PREVENDITA_SELECT_MODE = 0;
    public static final int PREVENDITA_NOT_SELECT_MODE = 1;
    public static final int PREVENDITA_SEARCH_MODE = 1;

    /**
     * Classe usata per condividere l'immagine.
     */
    private final class ShareTask extends AsyncTask<Bitmap, Void, Uri>{

        private Activity activity;
        private String shareText;

        public ShareTask(Activity activity, String shareText) {
            this.activity = activity;
            this.shareText = shareText;
        }

        //https://stackoverflow.com/questions/33222918/sharing-bitmap-via-android-intent

        /**
         * Saves the image as PNG to the app's private external storage folder.
         * @param image Bitmap to save.
         * @return Uri of the saved file or null
         */
        private Uri saveImageExternal(Bitmap image) {

            Uri uri = null;
            try {
                File file = new File(PRAppApplication.getInstance().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "to-share.png");
                FileOutputStream stream = new FileOutputStream(file);
                image.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                stream.close();
                //https://stackoverflow.com/questions/38200282/android-os-fileuriexposedexception-file-storage-emulated-0-test-txt-exposed
                uri = FileProvider.getUriForFile(PRAppApplication.getInstance(), PRAppApplication.FILE_PROVIDER, file);
            } catch (IOException e) {
                Log.d(TAG, "IOException while trying to write file for sharing: " + e.getMessage());
            }
            return uri;
        }

        /**
         * Checks if the external storage is writable.
         * @return true if storage is writable, false otherwise
         */
        public boolean isExternalStorageWritable() {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                return true;
            }
            return false;
        }

        /**
         * Shares the PNG image from Uri.
         * @param uri Uri of image to share.
         */
        private void shareImageUri(Uri uri){
            //https://stackoverflow.com/questions/20333186/how-to-share-image-text-together-using-action-send-in-android
            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, shareText);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/png");
            activity.startActivity(intent);
        }

        @Override
        protected Uri doInBackground(Bitmap... bitmaps) {
            Bitmap bitmap = bitmaps[0];
            return saveImageExternal(bitmap);
        }

        @Override
        protected void onPostExecute(Uri uri) {
            super.onPostExecute(uri);

            if(isExternalStorageWritable()){
                shareImageUri(uri);
            }
        }
    }

    //Roba di controlli
    private MutableLiveData<AggiungiClienteState> aggiungiClienteState = new MutableLiveData<>();

    public LiveData<AggiungiClienteState> getAggiungiClienteState() {
        return aggiungiClienteState;
    }

    //Roba di stati
    private MutableLiveData<Integer> prevenditaMode = new MutableLiveData<>(PREVENDITA_NOT_SELECT_MODE);

    public int getPrevenditaMode() {
        Integer value = prevenditaMode.getValue();
        return value == null ? -1 : value;
    }

    public LiveData<Integer> getPrevenditaModeLiveData() {
        return prevenditaMode;
    }

    public void setPrevenditaMode(int prevenditaMode) {
        this.prevenditaMode.setValue(prevenditaMode);
    }

    //Roba generale

    //Roba lato network
    private MutableLiveData<Result<List<WTipoPrevendita>, Void>> listaTipoPrevenditaResult = new MutableLiveData<>();
    private MutableLiveData<Result<WPrevendita, Void>> aggiungiPrevenditaResult = new MutableLiveData<>();
    private MutableLiveData<Result<List<WPrevenditaPlus>, Void>> listaPrevenditeResult = new MutableLiveData<>();
    private MutableLiveData<Result<WPrevendita, WPrevenditaPlus>> modificaPrevenditaResult = new MutableLiveData<>();

    public LiveData<Result<WPrevendita, WPrevenditaPlus>> getModificaPrevenditaResult() {
        return modificaPrevenditaResult;
    }


    public LiveData<Result<List<WTipoPrevendita>, Void>> getListaTipoPrevenditaResult() {
        return listaTipoPrevenditaResult;
    }

    public LiveData<Result<WPrevendita, Void>> getAggiungiPrevenditaResult() {
        return aggiungiPrevenditaResult;
    }

    public LiveData<Result<List<WPrevenditaPlus>, Void>> getListaPrevenditeResult() {
        return listaPrevenditeResult;
    }


    public PRViewModel() {
        super();
    }

    public void getListaTipoPrevendita() {
        MyContext myContext = getMyContext();

        if (myContext.isLoggato() && myContext.isStaffScelto() && myContext.isEventoScelto()) {
            ManagerMembro managerMembro = getManagerMembro();

            managerMembro.restituisciListaTipiPrevenditaEvento(new DefaultSuccessListener<>(listaTipoPrevenditaResult), new DefaultExceptionListener<>(listaTipoPrevenditaResult));
        } else {
            listaTipoPrevenditaResult.setValue(new Result<>(R.string.no_login));
        }
    }

    public void aggiungiPrevendita(String nomeCliente, String cognomeCliente, WTipoPrevendita tipoPrevendita, StatoPrevendita statoPrevendita) {
        MyContext myContext = getMyContext();

        if (myContext.isLoggato() && myContext.isStaffScelto() && myContext.isEventoScelto()) {
            ManagerPR managerPR = getManagerPR();


            Integer idEvento = getEvento().getId();
            Integer idTipoPrevendita = tipoPrevendita.getId();
            String codice = myContext.generatePrevenditaCode();

            //Creo la struttura interna
            InsertNetWPrevendita insertNetWPrevendita = new InsertNetWPrevendita(nomeCliente, cognomeCliente, idEvento, idTipoPrevendita, codice, statoPrevendita);

            managerPR.aggiungiPrevendita(insertNetWPrevendita, new DefaultSuccessListener<>(aggiungiPrevenditaResult), new DefaultExceptionListener<>(aggiungiPrevenditaResult));
        } else {
            aggiungiPrevenditaResult.setValue(new Result<>(R.string.no_login));
        }
    }

    public void modificaPrevendita(WPrevenditaPlus prevenditaPlus, StatoPrevendita nuovoStato){
        MyContext myContext = getMyContext();

        if (myContext.isLoggato() && myContext.isStaffScelto() && myContext.isEventoScelto()) {
            ManagerPR managerPR = getManagerPR();

            UpdateNetWPrevendita updateNetWPrevendita = new UpdateNetWPrevendita(prevenditaPlus.getId(), nuovoStato);

            managerPR.modificaPrevendita(updateNetWPrevendita, new DefaultSuccessListener<>(modificaPrevenditaResult, prevenditaPlus), new DefaultExceptionListener<>(modificaPrevenditaResult, prevenditaPlus));

        } else {
            modificaPrevenditaResult.setValue(new Result<>(R.string.no_login));
        }
    }

    public void getListaPrevenditeEvento(){
        MyContext myContext = getMyContext();

        if (myContext.isLoggato() && myContext.isStaffScelto() && myContext.isEventoScelto()) {
            ManagerPR managerPR = getManagerPR();

            managerPR.resitituisciPrevenditeEvento(new DefaultSuccessListener<>(listaPrevenditeResult), new DefaultExceptionListener<>(listaPrevenditeResult));
        } else {
            listaPrevenditeResult.setValue(new Result<>(R.string.no_login));
        }
    }

    private boolean isStringNotBlank(String myString) {
        if (myString != null) {
            if (!myString.isEmpty()) {
                return !myString.trim().isEmpty();
            }
        }
        return false;
    }

    public void aggiungiClienteStateChanged(String nome, String cognome) {
        if (!isNomeClienteValid(nome)) {
            aggiungiClienteState.setValue(new AggiungiClienteState(R.string.fragment_pr_add_cliente_invalid_nome, null, null));
        } else if (!isCognomeClienteValid(cognome)) {
            aggiungiClienteState.setValue(new AggiungiClienteState(null, R.string.fragment_pr_add_cliente_invalid_cognome, null));
        } else {
            aggiungiClienteState.setValue(new AggiungiClienteState(true));
        }
    }

    private boolean isNomeClienteValid(String nome) {
        return isStringNotBlank(nome);
    }

    private boolean isCognomeClienteValid(String cognome) {
        return isStringNotBlank(cognome);
    }


    public void shareImage(Activity activty, Bitmap image, String shareText){
        ShareTask shareTask = new ShareTask(activty, shareText);
        shareTask.execute(image);
    }


}
