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

    popolaLista(listaEventi) {
        var $lista = $("#listaEventi");

        for (let index = 0; index < listaEventi.length; index++) {
            const evento = listaEventi[index];
            var $elemento = $("<li class=\"list-group-item\">" + evento.nome + "</li>");

            $elemento.click(function () {
                ajax.setEvento(evento);
                uiUtils.impostaScritta("Hai scelto: " + evento.nome);
                console.log("Selected evento: " + JSON.stringify(evento));
            });

            $lista.append($elemento);

        }
    }
}

var uiUtils = new UiUtils();
var ajax = new AjaxRequest();

var funzionePrincipale = function () {
    uiUtils.impostaLogout();
    uiUtils.attivaMenu();

    if (ajax.isStaffSelected()) {
        if (ajax.isEventoSelected()) {
            uiUtils.impostaScritta("Hai scelto: " + ajax.getEvento().nome);
        } else {
            uiUtils.impostaScritta("Scegli un'evento");
        }

        ajax.getListaEventiStaff(function (response) {
            //Popolo.
            uiUtils.popolaLista(response.results);
        }, function (response) {
            uiUtils.impostaErrore("Errore: " + response.exceptions[0].msg);
            console.log("Get list staff failed: " + response.exceptions[0].msg);
        });
    } else {
        //Devi scegliere prima lo staff:
        uiUtils.impostaErrore("Devi scegliere lo staff per continuare.");
    }
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
        } else {
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
} 
else {
    $(document).ready(function () {
        //Il browser non supporta il local storage:
        uiUtils.disattivaTuttiMenu();
        //Invio un messaggio.
        uiUtils.impostaErrore("Il tuo browser non supporta l'applicazione.");
    });
}