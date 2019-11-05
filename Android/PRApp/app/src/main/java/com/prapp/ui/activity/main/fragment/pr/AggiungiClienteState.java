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

import androidx.annotation.Nullable;

public class AggiungiClienteState {

    @Nullable
    private Integer nomeClienteError;

    @Nullable
    private Integer cognomeClienteError;

    @Nullable
    private Integer dataDiNascitaClienteError;

    private boolean isDataValid;

    public AggiungiClienteState(@Nullable Integer nomeClienteError, @Nullable Integer cognomeClienteError, @Nullable Integer dataDiNascitaClienteError) {
        this.nomeClienteError = nomeClienteError;
        this.cognomeClienteError = cognomeClienteError;
        this.dataDiNascitaClienteError = dataDiNascitaClienteError;
        this.isDataValid = false;
    }

    public AggiungiClienteState(boolean isDataValid) {
        nomeClienteError = null;
        cognomeClienteError = null;
        dataDiNascitaClienteError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getNomeClienteError() {
        return nomeClienteError;
    }

    @Nullable
    public Integer getCognomeClienteError() {
        return cognomeClienteError;
    }

    @Nullable
    public Integer getDataDiNascitaClienteError() {
        return dataDiNascitaClienteError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}
