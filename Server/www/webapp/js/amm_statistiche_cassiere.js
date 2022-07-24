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

    impostaSelezione(funzione){
        $('#inputIdCassiere').on('change', ()=>{
            let $selected = $("#inputIdCassiere option:selected");
            funzione($selected.val(),$selected.text());
        });
    }

    popolaSelezione(listaMembri){
        var $lista = $("#inputIdCassiere");

        for (let index = 0; index < listaMembri.length; index++) {
            const membro = listaMembri[index];
            let $option = $("<option value=\""+membro.id+"\">"+membro.nome + " " + membro.cognome+"</option>");
            $lista.append($option);
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
        uiUtils.impostaLoginConMessaggio(ajax.isLogged(), "Statistiche evento del Cassiere", "Effettua il login prima di continuare.");

        if (ajax.isLogged()) {
            if (ajax.isStaffSelected()) {
                ajax.getMembriStaff(function(response){
                    uiUtils.popolaSelezione(response.results);
                    uiUtils.impostaSelezione((id, value)=>{
                        ajax.restituisciStatisticheCassiereEventoAmm(id,function(response){
                            if(response.results.length > 0)
                                uiUtils.impostaScritta("Per l'evento "+ ajax.getEvento().nome + ", " + value + " ha fatto entrare " + response.results[0].entrate+ "persone");
                            else
                                uiUtils.impostaScritta("Per l'evento "+ ajax.getEvento().nome + ", " + value + " ha fatto entrare 0 persone");
                        }, function(response){
                            uiUtils.impostaErrore("Impossibile recuperare le statistiche: "+ response.exceptions[0].msg);
                        });
                    })
                }, function(response){
                    uiUtils.impostaErrore("Impossibile recuperare i membri dello staff: "+ response.exceptions[0].msg);
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