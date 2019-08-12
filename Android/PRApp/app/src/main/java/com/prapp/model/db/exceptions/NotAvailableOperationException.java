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

package com.prapp.model.db.exceptions;

import com.prapp.model.net.Eccezione;
import com.prapp.model.net.Twinned;

/**
 * Si verifica quando una condizione sopraggiunge e interrompe l'avvenimento di un'operazione.
 */

public class NotAvailableOperationException extends Exception implements Twinned {

    public static final String CLASS_PATH = "com\\model\\db\\exception\\NotAvailableOperationException";
    public static final String ARG_NAME = "exception";


    static {
        Eccezione.register(CLASS_PATH, NotAvailableOperationException.class);
    }

    public NotAvailableOperationException() {
    }

    public NotAvailableOperationException(String message) {
        super(message);
    }

    public NotAvailableOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotAvailableOperationException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getRemoteClassPath() {
        return CLASS_PATH;
    }

//    @Override
//    public String getRemoteArgName() {
//        return ARG_NAME;
//    }

}
