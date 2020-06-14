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

package com.prapp.model.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.prapp.PRAppApplication;
import com.prapp.model.db.wrapper.WToken;

import java.nio.charset.StandardCharsets;

//https://www.apriorit.com/dev-blog/432-using-androidkeystore
public class ApplicationPreferences {
    public static final String PREFERENCES_FILE = "settings";

    private static final String IS_TOKEN_SAVED_KEY = "is_token_saved";
    private static final String TOKEN_KEY = "token";

    //Non ha più senso salvare nelle preferenze evento e staff: vengono chiesti ogni volta dal server.

    private static final Gson GSON_OBJECT = new Gson();

    private static ApplicationPreferences instance;

    private SharedPreferences preferences;
    private PasswordStorageHelper passwordStorage;

    private ApplicationPreferences(Context context) {
        preferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        passwordStorage = new PasswordStorageHelper(context);
    }

    public static ApplicationPreferences getInstance() {
        if (instance == null) {
            instance = new ApplicationPreferences(PRAppApplication.getInstance());
        }

        return instance;
    }

    public boolean isTokenSaved() {
        return preferences.getBoolean(IS_TOKEN_SAVED_KEY, false);
    }

    public void saveToken(WToken token) {
        String tokenJson = GSON_OBJECT.toJson(token, WToken.class);
        passwordStorage.setData(TOKEN_KEY, tokenJson.getBytes(StandardCharsets.UTF_8));

        setTokenSaved(true);
    }

    public WToken getLastStoredToken() {
        if(isTokenSaved())
        {
            byte[] storageData = passwordStorage.getData(TOKEN_KEY);

            if(storageData != null){
                String tokenJson = new String(storageData, StandardCharsets.UTF_8);
                return GSON_OBJECT.fromJson(tokenJson, WToken.class);
            }else{
                return WToken.getEmpty();
            }

        }

        //return WToken.getEmpty();
        throw new RuntimeException("No data");
    }

    public void logout()
    {
        clearToken();
    }

    public void clearSelected(){
    }

    public void clearToken() {
        setTokenSaved(false);
        passwordStorage.remove(TOKEN_KEY);
    }

    public void setTokenSaved(boolean isEnabled) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(IS_TOKEN_SAVED_KEY, isEnabled);
        editor.apply();
    }


}
