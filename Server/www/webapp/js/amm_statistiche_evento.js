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

    popolaStatistiche(statisticheEvento) {
        var $tab = $("#bodyTabella");
        var ricaviTot = 0;
        var ventuteTot = 0;
        var entrateTot = 0;
        var nonEntrateTot = 0;


        for (let index = 0; index < statisticheEvento.length; index++) {
            const statisticaTipoPrevendita = statisticheEvento[index];
            ventuteTot += statisticaTipoPrevendita.prevenditeVendute;
            ricaviTot += statisticaTipoPrevendita.ricavo;
            entrateTot += statisticaTipoPrevendita.prevenditeEntrate;
            nonEntrateTot += statisticaTipoPrevendita.prevenditeNonEntrate;

            let $row = $("<tr></tr>");
            let $colNome = $("<td>"+statisticaTipoPrevendita.nomeTipoPrevendita+"</td>");
            let $colQuantita = $("<td>"+statisticaTipoPrevendita.prevenditeVendute+"</td>");
            let $colRicavo = $("<td>"+statisticaTipoPrevendita.ricavo+"</td>");
            let $colEntrate = $("<td>"+statisticaTipoPrevendita.prevenditeEntrate+"</td>");
            let $colNonEntrate = $("<td>"+statisticaTipoPrevendita.prevenditeNonEntrate+"</td>");
            
            $row.append($colNome);
            $row.append($colQuantita);
            $row.append($colRicavo);
            $row.append($colEntrate);
            $row.append($colNonEntrate);

            $tab.append($row);
        }

        //$container.append("<hr/>");

        let $row = $("<tr></tr>");
        $row.append("<th scope=\"row\">Totale</th>");
        $row.append("<td>"+ventuteTot+"</td>");
        $row.append("<td>"+ricaviTot+"</td>");
        $row.append("<td>"+entrateTot+"</td>");
        $row.append("<td>"+nonEntrateTot+"</td>");

        $tab.append($row);
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
        uiUtils.impostaLoginConMessaggio(ajax.isLogged(), "Statistiche evento", "Effettua il login prima di continuare.");

        if (ajax.isLogged() && ajax.isStaffSelected() && ajax.isEventoSelected()) {
            ajax.restituisciStatisticheEventoAmm(function(response){
                uiUtils.popolaStatistiche(response.results);
            }, function(response){
                uiUtils.impostaErrore("Impossibile recuperare le statistiche: "+ response.exceptions[0].msg);
            });
        }else{
            //Redirect automatico alla pagina di login
            passRedirect("login.html", "amm_statistiche_evento.html");
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