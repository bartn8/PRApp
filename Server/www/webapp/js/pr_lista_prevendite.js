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
        var $lista = $("#listaPrevendite");

        for (let index = 0; index < listaPrevendite.length; index++) {
            const prevendita = listaPrevendite[index];
            var $elementoLi = $("<li class=\"list-group-item "+(prevendita.stato == 0 ? "list-group-item-success" : "list-group-item-danger")+"\"></li>");
            var $elementoSpan = $("<a href=\""+generaLink(prevendita)+"\">"+ prevendita.id + " " + prevendita.nomeCliente + " " + prevendita.cognomeCliente + (prevendita.stato == 1 ? " (ANNULLATA) " : "") + "</span>");
            
            $elementoLi.append($elementoSpan);
            $lista.append($elementoLi);
        }
    }

}

var uiUtils = new UiUtils();
var ajax = new AjaxRequest();

var generaLink = function (prevendita) {
    //Vecchio formato
    //var url_string = "https://prapp.altervista.org/qrCode.html?idPrevendita=1&idEvento=1&nome=Nome&cognome=Cognome&data=02%2F05%2F1990&codice=ifg453";
    //Nuovo formato
    //var url_string = "https://prapp.altervista.org/qrCode.html?idPrev=1&idEv=1&nome=Nome&cognome=Cognome&data=02%2F05%2F1990&cod=ifg453&nTipoP=OMAGGIO";

    var defaultUrl = "https://prapp.altervista.org/qrCode.html";

    var idPrevendita = prevendita.id;
    var idEvento = prevendita.idEvento;
    var nome = prevendita.nomeCliente;
    var cognome = prevendita.cognomeCliente;
    var codice = prevendita.codice;
    var nomeTipoPrevendita = "Duplicato";

    var idPrevenditaEncoded = encodeURIComponent(idPrevendita);
    var idEventoEncoded = encodeURIComponent(idEvento);
    var nomeEncoded = encodeURIComponent(nome);
    var cognomeEncoded = encodeURIComponent(cognome);
    var codiceEncoded = encodeURIComponent(codice);
    var nomeTipoPrevenditaEncoded = encodeURIComponent(nomeTipoPrevendita);

    return defaultUrl + "?idPrev=" + idPrevenditaEncoded + "&idEv=" + idEventoEncoded + "&nome=" + nomeEncoded + "&cognome=" + cognomeEncoded + "&cod=" + codiceEncoded + "&nTipoP=" + nomeTipoPrevenditaEncoded;
}

if (ajax.isStorageEnabled()) {

    //Ricavo l'oggetto AjaxRequest.
    //Ora funziona con mixed.
    ajax.initFromSessionStorage();

    //Quando la pagina è pronta:
    $(document).ready(function () {

        //UI
        uiUtils.disattivaMenu();
        uiUtils.attivaMenu(ajax.isLogged(), ajax.isStaffSelected(), ajax.isEventoSelected(), ajax.getDirittiMembro());
        uiUtils.impostaLoginConMessaggio(ajax.isLogged(), "Prevendite del PR", "Effettua il login prima di continuare.");

        if (ajax.isLogged()) {
            if (ajax.isStaffSelected()) {
                ajax.restituisciPrevendite(function(response){
                    uiUtils.popolaLista(response.results);
                }, function(response){
                    uiUtils.impostaErrore("Impossibile recuperare le prevendite: "+ response.exceptions[0].msg);
                });
            }
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