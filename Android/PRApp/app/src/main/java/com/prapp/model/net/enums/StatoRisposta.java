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

package com.prapp.model.net.enums;

public enum StatoRisposta {

    STATORISPOSTA_INDEFINITO(-1),
    STATORISPOSTA_OK(0),
    STATORISPOSTA_NON_TROVATO(1),
    STATORISPOSTA_ECCEZIONE(2);

    public static StatoRisposta parseStatoRisposta(int stato) {
        StatoRisposta[] values = StatoRisposta.values();

        for (StatoRisposta value: values) {
            if(value.getStato() == stato)
            {
                return value;
            }
        }

        return null;
    }

    private int stato;

    private StatoRisposta(int stato) {this.stato = stato;}

    public int getStato() { return stato; }

}
