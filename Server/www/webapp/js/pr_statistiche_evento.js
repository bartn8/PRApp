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
        var $container = $("#containerStatistiche");
        var ricaviTot = 0;
        var ventuteTot = 0;

        for (let index = 0; index < statisticheEvento.length; index++) {
            const statisticaTipoPrevendita = statisticheEvento[index];
            ventuteTot += statisticaTipoPrevendita.prevenditeVendute;
            ricaviTot += statisticaTipoPrevendita.ricavo;

            let $row = $("<div class=\"row\"></div>");
            let $colNome = $("<div class=\"col\">"+statisticaTipoPrevendita.nomeTipoPrevendita+"</div>");
            let $colQuantita = $("<div class=\"col\">"+statisticaTipoPrevendita.prevenditeVendute+"</div>");
            let $colRicavo = $("<div class=\"col\">"+statisticaTipoPrevendita.ricavo+"</div>");
            
            $row.append($colNome);
            $row.append($colQuantita);
            $row.append($colRicavo);

            $container.append($row);
        }

        $container.append("<hr/>");

        let $row = $("<div class=\"row\"></div>");
        $row.append("<div class=\"col\"></div>");
        $row.append("<div class=\"col\">"+ventuteTot+"</div>");
        $row.append("<div class=\"col\">"+ricaviTot+"</div>");

        $container.append($row);
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
        uiUtils.attivaMenu(ajax.isLogged(), ajax.isStaffSelected(), ajax.isEventoSelected());
        uiUtils.impostaLoginConMessaggio(ajax.isLogged(), "Statistiche evento del PR", "Effettua il login prima di continuare.");

        if (ajax.isLogged()) {
            if (ajax.isStaffSelected()) {
                ajax.restituisciStatistichePREvento(function(response){
                    uiUtils.popolaStatistiche(response.results);
                }, function(response){
                    uiUtils.impostaErrore("Impossibile recuperare le statistiche: "+ response.exceptions[0].msg);
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