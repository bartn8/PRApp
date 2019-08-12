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

class UiUtils extends GeneralUiUtils{
    constructor(){
        super();
    }

    attivaFormLogin(onClick){
        $("#loginButton").removeClass("disabled");
        $("#loginButton").click(onClick);
    }
    

    disattivaFormLogin(){
        $("#loginButton").addClass("disabled");
        $("#loginButton").click(function() {
            return false;
        });
    }

    effettuaSubmit(){
        $('#loginForm').submit();
    }
}

var uiUtils = new UiUtils();
var ajax = new AjaxRequest();

var loginButtonClick = function(){
    var username = $("#username").val();
    var password = $("#password").val();

    ajax.login(username, password, function(response){
        console.log("Login ok");

        //Renew del token.
        ajax.renewToken(function(response2){
            Cookies.set("token", response2.results[0].token, { expires: 7 });
            console.log("Renew token ok");
        }, function(response2){
            console.log("Renew token failed: " + response2.exceptions[0].msg);
        });

        if(ajax.isLogged())
        {
            uiUtils.impostaScritta("Complimenti! sei loggato: Scegli un'opzione");
            uiUtils.impostaLogout();
            uiUtils.attivaMenu();
            uiUtils.disattivaFormLogin();
            uiUtils.effettuaSubmit();
        }
        else
        {
            console.log("BUG LOGIN");
        }
    }, function(response){
        uiUtils.impostaErrore("Errore:" + response.exceptions[0].msg);
    });
};

var loginToken = function(){
    //Devo verificare lo stato di login:
    var token = Cookies.get('token');

    if(token !== undefined && token !== null)
    {
        //Esiste il token: vedo se è scaduto oppure no.
        ajax.loginToken(token, function(response){
            console.log("Login token ok");

            //Renew del token.
            ajax.renewToken(function(response2){
                Cookies.set("token", response2.results[0].token, { expires: 7 });
                console.log("Renew token ok");
            }, function(response2){
                console.log("Renew token failed: " + response2.exceptions[0].msg);
                Cookies.remove("token");
            });

            //Sono loggato.
            uiUtils.impostaScritta("Complimenti! sei loggato: Scegli un'opzione");
            uiUtils.impostaLogout();
            uiUtils.attivaMenu();
            uiUtils.disattivaFormLogin();
            

        }, function(response){
            console.log("Login token failed: " + response.exceptions[0].msg);
            //Devo effettuare il login normale.
            uiUtils.impostaErrore("Devi effettuare l'accesso per continuare.");
            uiUtils.impostaLogin(); 
            uiUtils.attivaFormLogin(loginButtonClick);
        });
    }
    else
    {
        //Devo effettuare il login normale.
        uiUtils.impostaErrore("Devi effettuare l'accesso per continuare.");
        uiUtils.impostaLogin();   
        uiUtils.attivaFormLogin(loginButtonClick);
    }
};


if (typeof(Storage) !== "undefined") {
    // Code for localStorage/sessionStorage.

    //Ricavo l'oggetto AjaxRequest.
    ajax.initFromSessionStorage();

    //Quando la pagina è pronta:
    $(document).ready(function(){

        //Disattivo temporaneamente i menu.
        uiUtils.disattivaMenu();

        //Se sono loggato allora disattivo il login e attivo le altre pagine.
        if(ajax.isLogged())
        {
            uiUtils.impostaScritta("Complimenti! sei loggato: Scegli un'opzione");
            uiUtils.impostaLogout();
            uiUtils.attivaMenu();
            uiUtils.disattivaFormLogin();
        }
        else
        {
            loginToken();

            //Devo verificare lo stato di login:
            //Evito il token: solo la pagina principale lo controlla.
            //Devo effettuare il login normale.
            //uiUtils.impostaScritta("Devi effettuare l'accesso per continuare.");
            //uiUtils.impostaLogin();   
            //uiUtils.attivaFormLogin(loginButtonClick);
        }
    });
  } else {
    $(document).ready(function(){
        //Il browser non supporta il local storage:
        uiUtils.disattivaTuttiMenu();
        uiUtils.disattivaFormLogin();

        //Invio un messaggio.
        uiUtils.impostaErrore("Il tuo browser non supporta l'applicazione.");
    });
}