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

import org.jetbrains.annotations.NotNull;

public interface InterfaceHolder<T> {

    /**
     * Aggancia una interfaccia
     * @param callback interfaccia da agganciare
     */
    public void holdInterface(@NotNull T callback);

    /**
     * Indica se l'interaccia è collegata
     * @return vero se collegata
     */
    public boolean isInterfaceSet();
}
