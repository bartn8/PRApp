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

package com.prapp;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.prapp.model.db.enums.Ruolo;
import com.prapp.model.db.enums.StatoEvento;
import com.prapp.model.db.enums.StatoPrevendita;

import net.danlew.android.joda.JodaTimeAndroid;

import org.jetbrains.annotations.Contract;

import java.net.CookieHandler;
import java.net.CookieManager;

//https://www.androidhive.info/2017/11/android-recyclerview-with-search-filter-functionality/
public class PRAppApplication extends Application {

    public static final String TAG = PRAppApplication.class.getSimpleName();
    public static final boolean NETWORK_DEBUG = true;

    public static final String FILE_PROVIDER = "com.prapp.fileprovider";

    private static PRAppApplication myInstance;

    @Contract(pure = true)
    public static synchronized PRAppApplication getInstance(){
        return myInstance;
    }

    private RequestQueue requestQueue;
    private CookieManager manager;

    @Override
    public void onCreate() {
        super.onCreate();
        myInstance = this;
        initCookieManager();
        initEnumsResources();
		initJodaTime();
    }
	
	private void initJodaTime(){
		//Inizializzo JodaTime
        //https://stackoverflow.com/questions/31775276/joda-time-resource-not-found-error
        JodaTimeAndroid.init(getApplicationContext());
	}

    private void initCookieManager(){
        //Devo eliminare i cookie: Rimane PHPSESSID.
        if(manager == null){
            //https://stackoverflow.com/questions/16680701/using-cookies-with-android-volley-library
//            CookieStore cookieStore = new MySimpleCookieStore();
//            manager = new CookieManager( cookieStore, CookiePolicy.ACCEPT_ALL );
            manager = new CookieManager();
        }

        CookieHandler.setDefault(manager);
    }

    private void initEnumsResources(){
        //StatoPrevendita
        StatoPrevendita.VALIDA.setResId(R.string.statoPrevendita_valida);
        StatoPrevendita.VALIDA.setResValue(getString(StatoPrevendita.VALIDA.getResId()));

        StatoPrevendita.ANNULLATA.setResId(R.string.statoPrevendita_rimborsata);
        StatoPrevendita.ANNULLATA.setResValue(getString(StatoPrevendita.ANNULLATA.getResId()));

        StatoPrevendita.ANNULLATA_NON_RIMBORSATA.setResId(R.string.statoPrevendita_annullata);
        StatoPrevendita.ANNULLATA_NON_RIMBORSATA.setResValue(getString(StatoPrevendita.ANNULLATA_NON_RIMBORSATA.getResId()));

        //Ruolo
        Ruolo.PR.setResId(R.string.diritto_pr);
        Ruolo.PR.setResValue(getString(Ruolo.PR.getResId()));

        Ruolo.CASSIERE.setResId(R.string.diritto_cassiere);
        Ruolo.CASSIERE.setResValue(getString(Ruolo.CASSIERE.getResId()));

        Ruolo.AMMINISTRATORE.setResId(R.string.diritto_amministratore);
        Ruolo.AMMINISTRATORE.setResValue(getString(Ruolo.AMMINISTRATORE.getResId()));

        //StatoEvento
        StatoEvento.VALIDO.setResId(R.string.statoEvento_valido);
        StatoEvento.VALIDO.setResValue(getString(StatoEvento.VALIDO.getResId()));

        StatoEvento.ANNULLATO.setResId(R.string.statoEvento_annullato);
        StatoEvento.ANNULLATO.setResValue(getString(StatoEvento.ANNULLATO.getResId()));
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests() {
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }

    public void cancelPendingRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }

}
