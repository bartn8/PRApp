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

import android.webkit.WebView;

import java.net.CookieHandler;
import java.net.CookieManager;

public class MyCookieManager {

    //Rimosso singleton per problemi di memory leak

    private static CookieManager manager;

    public static void initCookieManager(){
        //Devo eliminare i cookie: Rimane PHPSESSID.
        if(manager == null)
            manager = new CookieManager();

        CookieHandler.setDefault(manager);

        android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeAllCookies(null);
    }

    public static boolean acceptThirdPartyCookies(WebView view)
    {
        android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
        return cookieManager.acceptThirdPartyCookies(view);
    }
    
}
