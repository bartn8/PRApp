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

package com.prapp.ui;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Result<T, K> {

    @Nullable
    private T success;

    @Nullable
    private List<Exception> error;

    @Nullable
    private Integer integerError;

    @Nullable
    private K extra;

    public Result(@Nullable Exception error)
    {
        this.error = new ArrayList<>();
        this.error.add(error);
    }

    public Result(@Nullable String error)
    {
        this(new Exception(error));
    }

    public Result(@Nullable Integer integerError)
    {
        this.integerError = integerError;
    }

    public Result(@Nullable T success, @Nullable List<Exception> error) {
        this.success = success;
        this.error = error;
    }

    @Nullable
    public T getSuccess() {
        return success;
    }

    @Nullable
    public List<Exception> getError() {
        return error;
    }

    @Nullable
    public Integer getIntegerError() {
        return integerError;
    }

    @Nullable
    public K getExtra() {
        return extra;
    }

    public void setExtra(@Nullable K extra) {
        this.extra = extra;
    }

    public boolean isSuccessPresent()
    {
        return success != null;
    }

    public boolean isIntegerErrorPresent()
    {
        return integerError != null;
    }

    public boolean isErrorPresent()
    {
        return error != null;
    }

    public boolean isExtraPresent()
    {
        return extra != null;
    }

}
