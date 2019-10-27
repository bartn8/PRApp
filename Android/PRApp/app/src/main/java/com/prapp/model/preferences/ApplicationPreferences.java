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
import com.prapp.model.db.wrapper.WEvento;
import com.prapp.model.db.wrapper.WStaff;
import com.prapp.model.db.wrapper.WToken;

import java.nio.charset.StandardCharsets;

//https://www.apriorit.com/dev-blog/432-using-androidkeystore
public class ApplicationPreferences {
    private static final String PREFERENCES_FILE = "settings";

    private static final String IS_TOKEN_SAVED_KEY = "is_token_saved";
    private static final String TOKEN_KEY = "token";

    private static final String IS_STAFF_SAVED_KEY = "is_staff_saved";
    private static final String STAFF_KEY = "staff";

    private static final String IS_EVENTO_SAVED_KEY = "is_evento_saved";
    private static final String EVENTO_KEY = "evento";

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
            String tokenJson = new String(passwordStorage.getData(TOKEN_KEY), StandardCharsets.UTF_8);
            return GSON_OBJECT.fromJson(tokenJson, WToken.class);
        }

        //return WToken.getEmpty();
        throw new RuntimeException("No data");
    }

    public void logout()
    {
        clearToken();
        clearStaff();
        clearEvento();
    }

    public void clearSelected(){
        clearStaff();
        clearEvento();
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

    public boolean isStaffSaved()
    {
        return preferences.getBoolean(IS_STAFF_SAVED_KEY, false);
    }

    public void setStaffSaved(boolean isEnabled)
    {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(IS_STAFF_SAVED_KEY, isEnabled);
        editor.apply();
    }

    public void clearStaff()
    {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(IS_STAFF_SAVED_KEY, false);
        editor.remove(STAFF_KEY);
        editor.apply();
    }

    public void saveStaff(WStaff staff)
    {
        String staffJson = GSON_OBJECT.toJson(staff, WStaff.class);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(STAFF_KEY, staffJson);
        editor.putBoolean(IS_STAFF_SAVED_KEY, true);
        editor.apply();
    }

    public WStaff getStaff()
    {
        if(isStaffSaved())
        {
            String staffJson = preferences.getString(STAFF_KEY, "");
            return GSON_OBJECT.fromJson(staffJson, WStaff.class);
        }

        //return WStaff.getEmpty();
        throw new RuntimeException("No data");
    }

    //-----------------------

    public boolean isEventoSaved()
    {
        return preferences.getBoolean(IS_EVENTO_SAVED_KEY, false);
    }

    public void setEventoSaved(boolean isEnabled)
    {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(IS_EVENTO_SAVED_KEY, isEnabled);
        editor.apply();
    }

    public void clearEvento()
    {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(IS_EVENTO_SAVED_KEY, false);
        editor.remove(EVENTO_KEY);
        editor.apply();
    }

    public void saveEvento(WEvento staff)
    {
        String staffJson = GSON_OBJECT.toJson(staff, WEvento.class);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(EVENTO_KEY, staffJson);
        editor.putBoolean(IS_EVENTO_SAVED_KEY, true);
        editor.apply();
    }

    public WEvento getEvento()
    {
        if(isEventoSaved())
        {
            String staffJson = preferences.getString(EVENTO_KEY, "");
            return GSON_OBJECT.fromJson(staffJson, WEvento.class);
        }

        //return WStaff.getEmpty();
        throw new RuntimeException("No data");
    }


}
