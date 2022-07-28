/*
 * PRApp  Copyright (C) 2022  Luca Bartolomei
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

    attivaFormLogin(onClick) {
        $("#loginButton").removeClass("disabled");
        $("#loginButton").click(onClick);
    }

    disattivaFormLogin() {
        $("#loginButton").addClass("disabled");
        $("#loginButton").click(function () {
            return false;
        });
    }
}

var uiUtils = new UiUtils();
var ajax = new AjaxRequest();

var loginButtonClick = function () {
    var username = $("#username").val();
    var password = $("#password").val();

    ajax.login(username, password, function (response) {
        console.log("Login ok");

        if (ajax.isLogged()) {
            uiUtils.impostaScritta("Complimenti! sei loggato: Scegli un'opzione");
            uiUtils.impostaLogout();
            uiUtils.attivaMenu(ajax.isLogged(), ajax.isStaffSelected(), ajax.isEventoSelected(), ajax.getDirittiMembro());
            uiUtils.disattivaFormLogin();

            //Passo a scegli staff.
            passRedirect("utente_scegli_staff.html", getRedirectURL());
        }
    }, function (response) {
        uiUtils.impostaErrore("Errore:" + response.exceptions[0].msg);
    });
};

if (ajax.isStorageEnabled()) {

    //Ricavo l'oggetto AjaxRequest.
    //Ora funziona con mixed.
    ajax.initFromSessionStorage();

    //Quando la pagina è pronta:
    $(document).ready(function () {

        //Disattivo temporaneamente i menu.
        uiUtils.disattivaMenu();

        //Check intermedio con get utente.
        ajax.restituisciUtente((responseUtente)=>{
            console.log("Login già effettuato: "+responseUtente.results[0]);
            //Posso passare direttamente alla scelta staff/evento: 
            //1) Se restitusce staff scelto allora passo a scegli evento
            //2) Se restituisce anche evento scelto allora passo a index.
            //Se volesse cambiare staff/evento basta che va su scegli bla bla bla

            ajax.restituisciStaff((responseStaff) => {
                console.log("Staff già scelto: "+ responseStaff.results[0]);

                ajax.restituisciEvento((responseEvento) => {
                    console.log("Evento già scelto: "+ responseEvento.results[0]);
                    redirect(getRedirectURL());
                    
                },(responseEvento) => {
                    console.log("Evento non scelto: "+responseEvento.exceptions[0].msg);
                    passRedirect("utente_scegli_evento.html", getRedirectURL());
                });
            },(responseStaff) => {
                console.log("Staff non scelto: "+responseStaff.exceptions[0].msg);
                passRedirect("utente_scegli_staff.html", getRedirectURL());
            });

        },(response)=>{
            console.log("Login precedente non trovato: "+response.exceptions[0].msg);

            //Avvio la schermata di login.
            uiUtils.attivaMenu(ajax.isLogged(), ajax.isStaffSelected(), ajax.isEventoSelected(), ajax.getDirittiMembro());
            uiUtils.impostaLoginConMessaggio(ajax.isLogged(), "Complimenti! sei loggato: Scegli un'opzione", "Effettua il login prima di continuare.");

            //Se sono loggato allora disattivo il login e attivo le altre pagine.
            if (ajax.isLogged()) {
                uiUtils.disattivaFormLogin();
            }
            else {
                uiUtils.attivaFormLogin(loginButtonClick);
            }
        });
    });
} else {
    $(document).ready(function () {
        //Il browser non supporta i cookies:
        uiUtils.disattivaMenu();
        uiUtils.disattivaFormLogin();

        //Invio un messaggio.
        uiUtils.impostaErrore("Attiva i cookies per usare l'applicazione.");
    });
}