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

    popolaLista(listaTipiPrevendite) {
        var $lista = $("#listaTipiPrevendite");

        for (let index = 0; index < listaTipiPrevendite.length; index++) {
            const tipoPrevendita = listaTipiPrevendite[index];
            let aperturaPrevendite = new Date(tipoPrevendita.aperturaPrevendite).toLocaleString();
            let chiusuraPrevendite = new Date(tipoPrevendita.chiusuraPrevendite).toLocaleString();
            let qtVend = tipoPrevendita.quantitaMax == 0 ? "infinita" : (tipoPrevendita.quantitaMax - tipoPrevendita.quantita);
            var $elemento = $("<a href=\"#\" class=\"list-group-item list-group-item-action\">" + tipoPrevendita.nome + ", " +
             tipoPrevendita.descrizione + ", Prezzo: " + tipoPrevendita.prezzo + "€, Apertura: " + aperturaPrevendite + ", Chiusura: " + chiusuraPrevendite + ", Quantità vendibile: " + qtVend + "</>");
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

        //UI
        uiUtils.disattivaMenu();
        uiUtils.attivaMenu(ajax.isLogged(), ajax.isStaffSelected(), ajax.isEventoSelected(), ajax.getDirittiMembro());
        uiUtils.impostaLoginConMessaggio(ajax.isLogged(), "Complimenti! sei loggato: Scegli un'opzione", "Effettua il login prima di continuare.");

        if (ajax.isLogged() && ajax.isStaffSelected() && ajax.isEventoSelected()) {
            ajax.getListaTipoPrevenditaEvento(function(response){
                uiUtils.popolaLista(response.results);
            }, function(response){
                uiUtils.impostaErrore("Impossibile recuperare i tipi di prevendite: "+ response.exceptions[0].msg);
            });
        }else{
            //Redirect automatico alla pagina di login
            passRedirect("login.html", "membro_lista_tipo_prevendite.html");
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