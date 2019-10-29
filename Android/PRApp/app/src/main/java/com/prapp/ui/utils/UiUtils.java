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

package com.prapp.ui.utils;

import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UiUtils {

    private static UiUtils singleton;

    public static UiUtils getInstance(@NotNull Context context) {
        if (singleton == null)
            singleton = new UiUtils(context);

        return singleton;
    }

    private Context context;

    private UiUtils(Context context) {
        this.context = context;
    }

    public void showError(@NotNull List<Exception> exceptionList) {
        for (Exception exception : exceptionList)
            makeToast(exception.getMessage());
    }

    public void showError(@NotNull Integer integerError) {
        Resources resources = context.getResources();
        String text = resources.getString(integerError);
        makeToast(text);
    }

    public void makeToast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public void makeToast(int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    public void makeToast(int resId, Object... args) {
        String string = context.getString(resId, args);
        makeToast(string);
    }

}
