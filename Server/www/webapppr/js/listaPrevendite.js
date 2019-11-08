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

    popolaLista(listaPrevendite) {
        var $listaConsegnate = $("#listaPrevenditeConsegnate");
        var $listaPagate = $("#listaPrevenditePagate");
        var $listaAnnullate = $("#listaPrevenditeAnnullate");
        var $listaRimborsate = $("#listaPrevenditeRimborsate");

        var counterConsegnate = 0;
        var counterPagate = 0;
        var counterAnnullate = 0;
        var counterRimborsate = 0;

        for (let index = 0; index < listaPrevendite.length; index++) {
            const prevendita = listaPrevendite[index];

            var scritta;

            if (typeof (prevendita.nomeCliente) !== "undefined") {
                if (typeof (prevendita.cognomeCliente) !== "undefined") {
                    scritta = prevendita.nomeCliente + " " + prevendita.cognomeCliente + " - " + prevendita.codice;
                }
                else {
                    scritta = prevendita.nomeCliente + " - " + prevendita.codice;
                }
            }
            else {
                scritta = prevendita.codice;
            }

            var link = generaLink(prevendita.id, prevendita.idEvento, prevendita.nomeCliente, prevendita.cognomeCliente, "RL", prevendita.codice, "RL");

            var $elemento = $("<a class=\"list-group-item d-flex justify-content-between\" href=\"#\"><p class=\"p-0 m-0 flex-grow-1\">" + scritta + "</p><button type=\"button\" class=\"btn btn-primary\"><span class=\"fas fa-eye\" aria-hidden=\"true\"></span></button></a>");

            $elemento.find("button").click(function (event) {
                var win = window.open(link, '_blank');
                if (win) {
                    //Browser has allowed it to be opened
                    win.focus();
                }

                event.stopPropagation();
            });


            //https://stackoverflow.com/questions/16091823/get-clicked-element-using-jquery-on-event
            //https://stackoverflow.com/questions/38373842/bootstrap-buttons-inside-list-group-item?rq=1
            //https://getbootstrap.com/docs/4.0/components/buttons/

            var $buttonPagata = $("<button type=\"button\" class=\"btn btn-success\"><span class=\"fas fa-euro-sign\" aria-hidden=\"true\"></span></button>");
            var $buttonAnnullata = $("<button type=\"button\" class=\"btn btn-danger\"><span class=\"fas fa-trash\" aria-hidden=\"true\"></span></button>");
            var $buttonRimborsata = $("<button type=\"button\" class=\"btn btn-danger\"><span class=\"fas fa-trash\" aria-hidden=\"true\"></span></button>");


            $buttonPagata.click(function (event) {
                //Ha cliccato su PAGATA: modifico la prevendita.

                var $myButton = $(this);
                var conferma = confirm("Premi OK per confermare il pagamento di: "+prevendita.nomeCliente+" "+prevendita.cognomeCliente+" ("+prevendita.id+")");

                if(!conferma){
                    event.stopPropagation();
                    return;
                }

                ajax.modificaPrevendita(prevendita.id, 1, function (response) {
                    //Devo spostare la lista sulle pagate e modificare i pulsanti.
                    var $modElement = $myButton.parent();   //Ricavo l'elemento padre.
                    $modElement.children().remove();        //Rimuvo i pulsanti.
                    $modElement.append($buttonRimborsata);  //Aggiungo il pulsante rimborsa
                    $modElement.remove();                   //Rimuovo dalla lista precedente.
                    uiUtils.impostaScritta("Prevendita modificata, nuovo stato: PAGATA, aggiorna la pagina.");
                    //$listaPagate.append($modElement);       //Aggiungo alla lista delle pagate.

                }, function (responseError) {
                    uiUtils.impostaErrore("Impossibile modificare prevendita " + prevendita.id + ": " + responseError.exceptions[0].msg);
                });

                event.stopPropagation();
            });


            $buttonAnnullata.click(function (event) {
                //Ha cliccato su ANNULLATA: modifico la prevendita.

                var $myButton = $(this);
                var conferma = confirm("Premi OK per confermare l'annullamento di: "+prevendita.nomeCliente+" "+prevendita.cognomeCliente+" ("+prevendita.id+")");

                if(!conferma){
                    event.stopPropagation();
                    return;
                }

                ajax.modificaPrevendita(prevendita.id, 2, function (response) {
                    //Devo spostare la lista sulle pagate e modificare i pulsanti.
                    var modElement = $myButton.parent();    //Ricavo l'elemento padre.
                    $modElement.children().remove();        //Rimuvo i pulsanti.
                    $modElement.remove();                   //Rimuovo dalla lista precedente.
                    uiUtils.impostaScritta("Prevendita modificata, nuovo stato: ANNULLATA, aggiorna la pagina.");
                    //$listaAnnullate.append($modElement);    //Aggiungo alla lista delle annullate.
                }, function (responseError) {
                    uiUtils.impostaErrore("Impossibile modificare prevendita " + prevendita.id + ": " + responseError.exceptions[0].msg);
                });

                event.stopPropagation();
            });


            $buttonRimborsata.click(function (event) {
                //Ha cliccato su PAGATA: modifico la prevendita.

                var $myButton = $(this);
                var conferma = confirm("Premi OK per confermare il rimborso di: "+prevendita.nomeCliente+" "+prevendita.cognomeCliente+" ("+prevendita.id+")");

                if(!conferma){
                    event.stopPropagation();
                    return;
                }

                ajax.modificaPrevendita(prevendita.id, 3, function (response) {
                    //Devo spostare la lista sulle pagate e modificare i pulsanti.
                    var $modElement = $myButton.parent();   //Ricavo l'elemento padre.
                    $modElement.children().remove();        //Rimuvo i pulsanti.
                    $modElement.remove();                   //Rimuovo dalla lista precedente.
                    uiUtils.impostaScritta("Prevendita modificata, nuovo stato: RIMBORSATA, aggiorna la pagina.");
                    //$listaRimborsate.append($modElement);   //Aggiungo alla lista delle rimborsate.
                }, function (responseError) {
                    uiUtils.impostaErrore("Impossibile modificare prevendita " + prevendita.id + ": " + responseError.exceptions[0].msg);
                });

                event.stopPropagation();
            });

            //Smisto le prevendite in base allo stato della prevendita.
            switch (prevendita.stato) {
                //CONSEGNATA
                case 0:
                    //Aggiungo i pulsanti di PAGATA e ANNULLATA
                    $elemento.append($buttonPagata);
                    $elemento.append($buttonAnnullata);

                    counterConsegnate++;
                    $listaConsegnate.append($elemento);
                    break;

                //PAGATA
                case 1:
                    //Aggiungo il pulsante di RIMBORSATA
                    $elemento.append($buttonRimborsata);

                    counterPagate++;
                    $listaPagate.append($elemento);
                    break;

                //ANNULLATA
                case 2:
                    counterAnnullate++;
                    $listaAnnullate.append($elemento);
                    break;

                //RIMBORSATA
                case 3:
                    counterRimborsate++;
                    $listaRimborsate.append($elemento);
                    break;
                default: break;
            }
        }

        //Scrivo un messaggio di riassunto per ogni stato.
        this.aggiornaContatoriPrevendite(counterConsegnate, counterPagate, counterAnnullate, counterRimborsate);
    }

    aggiornaContatoriPrevendite(counterConsegnate, counterPagate, counterAnnullate, counterRimborsate) {
        var $textConsegnate = $("#textConsegnate");
        var $textPagate = $("#textPagate");
        var $textAnnullate = $("#textAnnullate");
        var $textRimborsate = $("#textRimborsate");

        //Scrivo un messaggio di riassunto per ogni stato.
        $textConsegnate.text("Ci sono " + counterConsegnate + " prevendite consegnate.");
        $textPagate.text("Ci sono " + counterPagate + " prevendite pagate.");
        $textAnnullate.text("Ci sono " + counterAnnullate + " prevendite annullate.");
        $textRimborsate.text("Ci sono " + counterRimborsate + " prevendite rimborsate.");
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

var funzionePrincipale = function () {
    uiUtils.impostaLogout();
    uiUtils.attivaMenu();

    ajax.restituisciPrevendite(function (response) {
        //Popolo.
        uiUtils.popolaLista(response.results);

        //Imposto un messaggio.
        uiUtils.impostaScritta("Totale: " + response.results.length + " prevendite");

    }, function (response) {
        uiUtils.impostaErrore("Errore: " + response.exceptions[0].msg);
        console.log("Get list prevendite failed: " + response.exceptions[0].msg);
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
            uiUtils.impostaScritta("Complimenti! sei loggato: Scegli un'opzione");
            uiUtils.impostaLogout();
            uiUtils.attivaMenu();

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

if (ajax.isStorageEnabled()) {

    //Ricavo l'oggetto AjaxRequest.
    //Ora funziona con mixed.
    ajax.initFromMixedStorage();

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
} else {
    $(document).ready(function () {
        //Il browser non supporta i cookies:
        uiUtils.disattivaTuttiMenu();
        //Invio un messaggio.
        uiUtils.impostaErrore("Attiva i cookies per usare l'applicazione.");
    });
}