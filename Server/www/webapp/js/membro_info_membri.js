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

    popolaLista(listaMembri) {
        var $lista = $("#listaMembri");

        for (let index = 0; index < listaMembri.length; index++) {
            const membro = listaMembri[index];
            var $elemento = $("<a href=\"#\" class=\"list-group-item list-group-item-action\">" + membro.nome + " " + membro.cognome + " " + membro.telefono + "</>");
            $lista.append($elemento);
        }
    }

    stampaInfo(listaMembri, dirittiUtente){
        if(listaMembri !== undefined){
            var lenMembri = listaMembri.length;
            var myStr = "Ci sono "+ lenMembri + " membri";
            uiUtils.impostaScritta(myStr);
        }

        if(dirittiUtente !== undefined){
            var myStr = "L'utente è: ";
            for(let index = 0; index < dirittiUtente.length; index++){
                if(dirittiUtente[index] == 0){
                    myStr += "PR ";
                }
                if(dirittiUtente[index] == 1){
                    myStr += "CASSIERE ";
                }
                if(dirittiUtente[index] == 2){
                    myStr += "AMMINISTRATORE ";
                }
            }
            uiUtils.appendScritta(myStr);
        }
    }

}

var uiUtils = new UiUtils();
var ajax = new AjaxRequest();

var listaMembri = undefined;
var dirittiUtente = undefined;

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
            ajax.getMembriStaff(function(response){
                uiUtils.popolaLista(response.results);
                listaMembri = response.results;
                uiUtils.stampaInfo(listaMembri, dirittiUtente);
            }, function(response){
                uiUtils.impostaErrore("Impossibile recuperare i membri dello staff: "+ response.exceptions[0].msg);
            });
            ajax.getDirittiUtenteStaff(function(response){
                dirittiUtente = response.results[0].ruoli;
                uiUtils.stampaInfo(listaMembri, dirittiUtente);
            }, function(response){
                uiUtils.impostaErrore("Impossibile recuperare i diritti dell'utente: "+ response.exceptions[0].msg);
            });
        }else{
            //Redirect automatico alla pagina di login
            passRedirect("login.html", "membro_info_membri.html");
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