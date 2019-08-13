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

class UiUtils extends GeneralUiUtils {
    constructor() {
        super();
    }
}

var uiUtils = new UiUtils();
var ajax = new AjaxRequest();

var funzionePrincipale = function () {
    uiUtils.impostaScritta("Logout in corso...");
    uiUtils.impostaLogout();
    uiUtils.attivaMenu();

    ajax.logout(function (response) {
        console.log("Logout ok");

        ajax.restoreDefaultUtente();
        ajax.restoreDefaultStaff();
        ajax.restoreDefaultEvento();

        uiUtils.impostaScritta("Logout completato: effettua il login per continuare.");
        uiUtils.impostaLogin();
        uiUtils.disattivaMenu();

        //Elimino il token.
        Cookies.remove("token");

    }, function (response) {
        console.log("Logout failed");
        uiUtils.impostaErrore("Logout fallito.");
    });
};

var loginToken = function (needRenew) {
    //Devo verificare lo stato di login:
    var token = Cookies.get('token');

    if (token !== undefined && token !== null) {
        //Esiste il token: vedo se è scaduto oppure no.
        ajax.loginToken(token, function (response) {
            console.log("Login token ok");

            if (needRenew) {
                //Renew del token.
                ajax.renewToken(function (response2) {
                    Cookies.set("token", response2.results[0].token, { expires: 7 });
                    console.log("Renew token ok");
                }, function (response2) {
                    console.log("Renew token failed: " + response2.exceptions[0].msg);
                    Cookies.remove("token");
                });
            }

            //Sono loggato.
            funzionePrincipale();

        }, function (response) {
            console.log("Login token failed: " + response.exceptions[0].msg);
            //Devo effettuare il login normale.
            uiUtils.impostaErrore("Devi effettuare l'accesso per continuare.");
            uiUtils.impostaLogin();
        });
    }
    else {
        //Devo effettuare il login normale.
        uiUtils.impostaErrore("Devi effettuare l'accesso per continuare.");
        uiUtils.impostaLogin();
    }
};



if (typeof (Storage) !== "undefined") {
    // Code for localStorage/sessionStorage.

    //Ricavo l'oggetto AjaxRequest.
    ajax.initFromSessionStorage();

    //Quando la pagina è pronta:
    $(document).ready(function () {

        //Disattivo temporaneamente i menu.
        uiUtils.disattivaMenu();

        //Se sono loggato allora disattivo il login e attivo le altre pagine.
        if (ajax.isLogged()) {
            funzionePrincipale();
        }
        else {
            //Prima controllo se c'è un problema di sessione:
            ajax.restituisciUtente(function (response) {
                //Siamo loggati:
                funzionePrincipale();
            }, function (response) {
                //Probabilmente non siamo veramente loggati:
                loginToken(false);
            });
        }
    });
} else {
    $(document).ready(function () {
        //Il browser non supporta il local storage:
        uiUtils.disattivaTuttiMenu();

        //Invio un messaggio.
        uiUtils.impostaErrore("Il tuo browser non supporta l'applicazione.");
    });
}