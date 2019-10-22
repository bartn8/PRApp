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

    effettuaSubmit() {
        $('#loginForm').submit();
    }
}

var uiUtils = new UiUtils();
var ajax = new AjaxRequest();

var loginButtonClick = function () {
    var username = $("#username").val();
    var password = $("#password").val();

    ajax.login(username, password, function (response) {
        console.log("Login ok");

        //Renew del token.
        ajax.renewToken(function (response2) {
            Cookies.set("token", response2.results[0].token, { expires: 7 });
            console.log("Renew token ok");
        }, function (response2) {
            console.log("Renew token failed: " + response2.exceptions[0].msg);
        });

        if (ajax.isLogged()) {
            uiUtils.impostaScritta("Complimenti! sei loggato: Scegli un'opzione");
            uiUtils.impostaLogout();
            uiUtils.attivaMenu();
            uiUtils.disattivaFormLogin();
            uiUtils.effettuaSubmit();
        }
        else {
            console.log("BUG LOGIN");
        }
    }, function (response) {
        uiUtils.impostaErrore("Errore:" + response.exceptions[0].msg);
    });
};

var getParameters = function () {
    var myReturn = false;

    var url_string = window.location.href;

    //Recupero dati GET.
    var url = new URL(url_string);
    var token = decodeURIComponent(url.searchParams.get("token"));
    var idEvento = decodeURIComponent(url.searchParams.get("idEvento"));
    var nomeEvento = decodeURIComponent(url.searchParams.get("nomeEvento"));
    var idStaff = decodeURIComponent(url.searchParams.get("idStaff"));
    var nomeStaff = decodeURIComponent(url.searchParams.get("nomeStaff"));
    


    if (token !== 'null') {
        Cookies.set("token", token, { expires: 7 });
        myReturn = true;
    }

    if (idEvento !== 'null' && nomeEvento !== 'null') {
        var idEventoInteger = parseInt(idEvento);

        if(idEventoInteger != NaN) {
            var myEvento = ajax.getDefaultEvento();
            myEvento.id =idEventoInteger;
            myEvento.nome = nomeEvento;

            ajax.setEvento(myEvento);
        }
        //myReturn = true;
    }

    if (idStaff !== 'null' && nomeStaff !== 'null') {
        var idStaffInteger = parseInt(idStaff);

        if(idStaffInteger != NaN) {
            var myStaff = ajax.getDefaultStaff();
            myStaff.id =idStaffInteger;
            myStaff.nome = nomeStaff;

            ajax.setStaff(myStaff);
        }
        //myReturn = true;
    }

    return myReturn;
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
            uiUtils.impostaScritta("Complimenti! sei loggato: Scegli un'opzione");
            uiUtils.impostaLogout();
            uiUtils.attivaMenu();
            uiUtils.disattivaFormLogin();


        }, function (response) {
            console.log("Login token failed: " + response.exceptions[0].msg);
            //Devo effettuare il login normale.
            uiUtils.impostaErrore("login con token fallito: " + response.exceptions[0].msg);
            uiUtils.impostaLogin();
            uiUtils.attivaFormLogin(loginButtonClick);
        });
    }
    else {
        //Devo effettuare il login normale.
        uiUtils.impostaErrore("Devi effettuare l'accesso per continuare.");
        uiUtils.impostaLogin();
        uiUtils.attivaFormLogin(loginButtonClick);
    }
};

//Ora funziona con cookies.
if (navigator.cookieEnabled) {

    //Ricavo l'oggetto AjaxRequest.
    //Ora funziona con cookies.
    ajax.initFromCookies();

    //Quando la pagina è pronta:
    $(document).ready(function () {

        //Disattivo temporaneamente i menu.
        uiUtils.disattivaMenu();

        //Se sono loggato allora disattivo il login e attivo le altre pagine.
        if (ajax.isLogged()) {
            uiUtils.impostaScritta("Complimenti! sei loggato: Scegli un'opzione");
            uiUtils.impostaLogout();
            uiUtils.attivaMenu();
            uiUtils.disattivaFormLogin();
        }
        else {
            //Prima controllo se c'è un problema di sessione:
            ajax.restituisciUtente(function(response){
                //Siamo loggati:
                uiUtils.impostaScritta("Complimenti! sei loggato: Scegli un'opzione");
                uiUtils.impostaLogout();
                uiUtils.attivaMenu();
                uiUtils.disattivaFormLogin();
            }, function(response){
                //Probabilmente non siamo veramente loggati:
                getParameters();
                loginToken(false);
            });
        }
    });
} else {
    $(document).ready(function () {
        //Il browser non supporta i cookies:
        uiUtils.disattivaTuttiMenu();
        uiUtils.disattivaFormLogin();

        //Invio un messaggio.
        uiUtils.impostaErrore("Attiva i cookies per usare l'applicazione.");
    });
}