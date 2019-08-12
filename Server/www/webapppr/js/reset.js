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
}

var uiUtils = new UiUtils();
var ajax = new AjaxRequest();

if (typeof(Storage) !== "undefined") {
    // Code for localStorage/sessionStorage.

    //Ricavo l'oggetto AjaxRequest.    
    ajax.initFromSessionStorage();

    //Quando la pagina è pronta:
    $(document).ready(function(){

        //Disattivo temporaneamente i menu.
        uiUtils.disattivaMenu();

        //Imposto un messaggio.
        uiUtils.impostaScritta("Sto resettando l'applicazione...");

        //Forzo il logout e faccio il reset dell'AjaxRequest.
        ajax.getUtente().id = -1;
        ajax.logout(function(response){
            console.log("Logout ok");
        }, function(response){
            console.log("Error: "+ response.exceptions[0].msg);
        });

        //Rimuovo i cookies.
        Cookies.remove("PHPSESSID");
        Cookies.remove("token");
        
        //Ripristino l'oggetto AjaxRequest.
        ajax.restoreDefaultUtente();
        ajax.restoreDefaultStaff();
        ajax.restoreDefaultEvento();

        //Ora vado in modalità login.
        uiUtils.impostaScritta("Applicazione resettata. Torna al menu principale.");
        uiUtils.impostaLogin();
    });
  } else {
    $(document).ready(function(){
        //Il browser non supporta il local storage:
        uiUtils.disattivaTuttiMenu();
        //Invio un messaggio.
        uiUtils.impostaErrore("Il tuo browser non supporta l'applicazione.");
    });
}