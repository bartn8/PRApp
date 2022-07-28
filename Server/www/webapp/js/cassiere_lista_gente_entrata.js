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
        var $lista = $("#listaGente");

        for (let index = 0; index < listaPrevendite.length; index++) {
            const prevendita = listaPrevendite[index];
            var $elementoLi = $("<li class=\"list-group-item\"></li>");
            var $elementoSpan = $("<span>"+ prevendita.id + " " + prevendita.nomeCliente + " " + prevendita.cognomeCliente + " Codice: " + prevendita.codice + " Tipo: " + prevendita.nomeTipoPrevendita + " PR: " + prevendita.nomePR + " " + prevendita.cognomePR + "</span>");
                        
            $elementoLi.append($elementoSpan);
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
        uiUtils.impostaLoginConMessaggio(ajax.isLogged(), "Entrate all'evento "+ajax.getEvento().nome, "Effettua il login prima di continuare.");

        if (ajax.isLogged() && ajax.isStaffSelected() && ajax.isEventoSelected()) {
            ajax.restituisciListaEntrate(function(response){
                uiUtils.popolaLista(response.results);
            }, function(response){
                uiUtils.impostaErrore("Impossibile recuperare le entrate: "+ response.exceptions[0].msg);
            });
        }else{
            //Redirect automatico alla pagina di login
            passRedirect("login.html", "cassiere_lista_gente_entrata.html");
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