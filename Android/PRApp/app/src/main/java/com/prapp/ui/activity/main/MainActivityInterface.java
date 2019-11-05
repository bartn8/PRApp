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

package com.prapp.ui.activity.main;

import androidx.fragment.app.Fragment;

public interface MainActivityInterface {
    /**
     * Imposta un nuovo fragment nella schermata principale.
     * Il metodo può essere richiamato dai fragment figli per effetturare scambi di sotto fragment.
     *
     * @param nuovoFragment Fragment da impostare.
     */
    public void cambiaFragment(Fragment nuovoFragment);

    public Fragment getNavFragment(int id);
}
