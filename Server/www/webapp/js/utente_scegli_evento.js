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

    popolaLista(listaEventi) {
        var $lista = $("#listaEventi");

        var selectedEvento = ajax.getEvento();

        for (let index = 0; index < listaEventi.length; index++) {
            const evento = listaEventi[index];
            var $elemento = $("<a href=\"#\" class=\"list-group-item list-group-item-action\">" + evento.nome + "</>");

            if(evento.id === selectedEvento.id){
                $elemento.addClass("active");   //Ho già scelto l'evento.
            }

            $elemento.click(function () {
                //Seleziono l'evento nel server
                ajax.scegliEvento(evento.id, function(response){
                    uiUtils.attivaMenu(ajax.isLogged(), ajax.isStaffSelected(), ajax.isEventoSelected());
                    uiUtils.impostaScritta("Hai scelto: " + evento.nome);
                    $lista.children().removeClass("active");    //Devo rimuovere la classe active da tutti i figli di lista.
                    $elemento.addClass("active");               //Aggiungo la classe active solo a quello selezionato.
                }, function(response){
                    uiUtils.impostaErrore("Selezione evento fallita: "+ response.exceptions[0].msg);
                });                                 
                //console.log("Selected evento: " + JSON.stringify(evento));
            });

            $lista.append($elemento);

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

        //Disattivo temporaneamente i menu.
        uiUtils.disattivaMenu();
        uiUtils.attivaMenu(ajax.isLogged(), ajax.isStaffSelected(), ajax.isEventoSelected());
        uiUtils.impostaLoginConMessaggio(ajax.isLogged(), "Complimenti! sei loggato: Scegli un evento", "Effettua il login prima di continuare.");

        //Se sono loggato allora disattivo il login e attivo le altre pagine.
        if (ajax.isLogged()) {
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
                    console.log("Get list eventi staff failed: " + response.exceptions[0].msg);
                });
            } else {
                //Devi scegliere prima lo staff:
                uiUtils.impostaErrore("Devi scegliere lo staff per continuare.");
            }
        }
    });
} 
else {
    $(document).ready(function () {
        //Il browser non supporta i cookies:
        uiUtils.disattivaMenu();
        //Invio un messaggio.
        uiUtils.impostaErrore("Attiva i cookies per usare l'applicazione.");
    });
}