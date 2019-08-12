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

    popolaLista(listaPrevendite){
        var $lista = $("#listaPrevenditeVendute");

        for (let index = 0; index < listaPrevendite.length; index++) {
            const prevendita = listaPrevendite[index];

            var scritta;

            if(typeof(prevendita.nomeCliente) !== "undefined"){
                if(typeof(prevendita.cognomeCliente) !== "undefined"){
                    scritta = prevendita.nomeCliente + " " + prevendita.cognomeCliente + " - " + prevendita.codice;
                }
                else{
                    scritta = prevendita.nomeCliente + " - " + prevendita.codice;
                }
            }
            else{
                scritta = prevendita.codice;
            }

            var link = generaLink(prevendita.id, prevendita.idEvento, prevendita.nomeCliente, prevendita.cognomeCliente, "RL", prevendita.codice, "RL");

            var $elemento = $("<li class=\"list-group-item\"> <a href=\""+link+"\" target=\"_blank\">"+ scritta +"</a></li>");

            $lista.append($elemento);
        }
    }
}

var uiUtils = new UiUtils();
var ajax = new AjaxRequest();

var generaLink = function (idPrevendita, idEvento, nome, cognome, data, codice, nomeTipoPrevendita) {
    //Vecchio formato
    //var url_string = "https://prapp.altervista.org/qrCode.html?idPrevendita=1&idEvento=1&nome=Nome&cognome=Cognome&data=02%2F05%2F1990&codice=ifg453";
    //Nuovo formato
    //var url_string = "https://prapp.altervista.org/qrCode.html?idPrev=1&idEv=1&nome=Nome&cognome=Cognome&data=02%2F05%2F1990&cod=ifg453&nTipoP=OMAGGIO";

    var defaultUrl = "https://prapp.altervista.org/qrCode.html";

    var idPrevenditaEncoded = encodeURIComponent(idPrevendita);
    var idEventoEncoded = encodeURIComponent(idEvento);
    var nomeEncoded = encodeURIComponent(nome);
    var cognomeEncoded = encodeURIComponent(cognome);
    var dataEncoded = encodeURIComponent(data);
    var codiceEncoded = encodeURIComponent(codice);
    var nomeTipoPrevenditaEncoded = encodeURIComponent(nomeTipoPrevendita);

    return defaultUrl + "?idPrev=" + idPrevenditaEncoded + "&idEv=" + idEventoEncoded + "&nome=" + nomeEncoded + "&cognome=" + cognomeEncoded + "&data=" + dataEncoded + "&cod=" + codiceEncoded + "&nTipoP=" + nomeTipoPrevenditaEncoded;
}

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

        }, function(response){
            console.log("Login token failed: " + response.exceptions[0].msg);
            //Devo effettuare il login normale.
            uiUtils.impostaErrore("Devi effettuare l'accesso per continuare.");
            uiUtils.impostaLogin(); 
        });
    }
    else
    {
        //Devo effettuare il login normale.
        uiUtils.impostaErrore("Devi effettuare l'accesso per continuare.");
        uiUtils.impostaLogin();   
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
            uiUtils.impostaLogout();
            uiUtils.attivaMenu();
                
            ajax.restituisciPrevendite(function(response){
                //Popolo.
                uiUtils.popolaLista(response.results);

                //Imposto un messaggio.
                uiUtils.impostaScritta("Hai venduto: "+response.results.length +" prevendite");

            }, function(response){
                uiUtils.impostaErrore("Errore: "+ response.exceptions[0].msg);
                console.log("Get list prevendite failed: "+ response.exceptions[0].msg);
            });
        }else{
            loginToken();
        }
    });
  } else {
    $(document).ready(function(){
        //Il browser non supporta il local storage:
        uiUtils.disattivaTuttiMenu();
        //Invio un messaggio.
        uiUtils.impostaErrore("Il tuo browser non supporta l'applicazione.");
    });
}