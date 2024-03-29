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

    popolaLista(listaPrevendite) {
        let $lista = $("#listaPrevendite");

        for (let index = 0; index < listaPrevendite.length; index++) {
            const prevendita = listaPrevendite[index];
            let $elementoLi = $("<li class=\"list-group-item "+(prevendita.stato == 0 ? "list-group-item-success" : "list-group-item-danger")+"\"></li>");
            let $elementoSpan = $("<span>"+ prevendita.id + " " + prevendita.nomeCliente + " " + prevendita.cognomeCliente + (prevendita.stato == 1 ? " (ANNULLATA) " : " ") + "</span>");
            let $elementoButton = $("<button type=\"button\" class=\"btn btn-primary\">Annulla</button>");

            if(prevendita.stato == 0){
                $elementoButton.click(function(){
                    const $this = $(this);
                    if(confirm("Vuoi davvero eliminare la prevendita "+ prevendita.id + " " + prevendita.nomeCliente + " " + prevendita.cognomeCliente+"?")){
                        ajax.modificaPrevenditaAmm(prevendita.id, 1, function(response){
                            $this.addClass("disabled");
                            uiUtils.impostaScritta("Prevendita "+prevendita.id + " " + prevendita.nomeCliente + " " + prevendita.cognomeCliente+ " annullata.");
                        }, function(response){
                            uiUtils.impostaErrore("Impossibile annullare la prevendita: "+response.exceptions[0].msg);
                        });
                    }
                });
            }else{
                $elementoButton.addClass("disabled");
            }

            $elementoLi.append($elementoSpan);
            $elementoLi.append($elementoButton);

            $lista.append($elementoLi);
        }
    }

}

var uiUtils = new UiUtils();
var ajax = new AjaxRequest();

if (ajax.isStorageEnabled()) {

    //Ricavo l'oggetto AjaxRequest.
    //Ora funziona con mixed.
    ajax.initFromSessionStorage();

    //Quando la pagina è pronta:
    $(document).ready(function () {

        //UI
        uiUtils.disattivaMenu();
        uiUtils.attivaMenu(ajax.isLogged(), ajax.isStaffSelected(), ajax.isEventoSelected(), ajax.getDirittiMembro());
        uiUtils.impostaLoginConMessaggio(ajax.isLogged(), "Seleziona una prevendita da annullare", "Effettua il login prima di continuare.");

        if (ajax.isLogged() && ajax.isStaffSelected() && ajax.isEventoSelected()) {
            ajax.restituisciPrevenditeEventoAmm(function(response){
                uiUtils.popolaLista(response.results);
            }, function(response){
                uiUtils.impostaErrore("Impossibile recuperare le prevendite: "+ response.exceptions[0].msg);
            });
        }else{
            //Redirect automatico alla pagina di login
            passRedirect("login.html", "amm_modifica_prevendita.html");
        }

    });
} else {
    $(document).ready(function () {
        //Il browser non supporta i cookies:
        uiUtils.disattivaMenu();
        //Invio un messaggio.
        uiUtils.impostaErrore("Attiva i cookies per usare l'applicazione.");
    });
}