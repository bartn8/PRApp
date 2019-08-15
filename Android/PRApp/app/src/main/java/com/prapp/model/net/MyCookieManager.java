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

package com.prapp.model.net;

import android.content.Context;
import android.webkit.WebView;

import com.prapp.model.preferences.ApplicationPreferences;

import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;

public class MyCookieManager {

    public static final String COOKIE_TOKEN = "token";

    private static MyCookieManager singleton;

    public static MyCookieManager getSingleton(Context context)
    {
        if(singleton == null)
            singleton = new MyCookieManager(context);

        return singleton;
    }

    private CookieManager manager;
    private Context context;

    private MyCookieManager(Context context) {
        this.context = context;
    }


    public void initCookieManager(){
        //Devo eliminare i cookie: Rimane PHPSESSID.
        if(manager == null)
            manager = new CookieManager();

        CookieHandler.setDefault(manager);

        android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeAllCookies(null);
    }

    public boolean acceptThirdPartyCookies(WebView view)
    {
        android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
        return cookieManager.acceptThirdPartyCookies(view);
    }


    //Ho risolto in altro modo: parametri get nella webapp.

//    public void copyCookiesFromPreferences(){
//        //TODO: copia anche dell'evento e dello staff scelto
//
//        String token = getToken();
//
//        //https://stackoverflow.com/questions/1652850/android-webview-cookie-problem
//        android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
//        String cookieString = COOKIE_TOKEN + "=" + token + ";";
//        cookieManager.setCookie(MyContext.DEFAULT_HOST, cookieString);
//    }
//
//    public void copyCookiesFromCookieHandler(){
//        CookieStore cookieStore = manager.getCookieStore();
//
//        try {
//            List<HttpCookie> httpCookies = cookieStore.get(new URI(MyContext.DEFAULT_ADDRESS));
//            android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
//
//            for(HttpCookie cookie : httpCookies)
//            {
//                cookieManager.setCookie(cookie.getName(), cookie.getValue());
//            }
//
//        } catch (URISyntaxException e) {
//
//        }
//    }


    private String getToken(){
        ApplicationPreferences preferences = ApplicationPreferences.getInstance(context);
        String token = "";
        try {
            token = preferences.getLastStoredToken().getToken();
        } catch (UnsupportedEncodingException e) {

        }
        return token;
    }

}
