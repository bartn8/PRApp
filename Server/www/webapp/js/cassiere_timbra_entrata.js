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
        this.scansioniAppend = [];
    }

    disabilitaFlash(){
        $("#toggleFlash").addClass("disabled");
        $("#toggleFlash").click(function(){return false;});
    }

    scrittaPulsanteScansione(scritta){
        $("#toggleScan").text(scritta);
    }

    impostaClickBtnScansione(funzione){
        $("#toggleScan").click(funzione);
    }

    impostaClickBtnFlash(funzione){
        $("#toggleFlash").click(funzione);
    }

    getElementoVideo(){
        return $("#scanVideo")[0];
    }

    compilaModulo(nome, cognome, nomePR, cognomePR, nomeTipoPrevendita, codice, stato, confirmFunction){
        var $modulo = $("#moduloConferma");

        var $header = $("<div class=\"row\"><div class=\"col\">Nome</div>"+
                        "<div class=\"col\">Nome PR</div>"+
                        "<div class=\"col\">Tipo Prevendita</div>"+
                        "<div class=\"col\">Codice</div>"+
                        "<div class=\"col\">Stato</div></div>");

        var $body =   $("<div class=\"row\"><div class=\"col\">"+nome+" "+cognome+"</div>"+
                        "<div class=\"col\">"+nomePR+" "+cognomePR+"</div>"+
                        "<div class=\"col\">"+nomeTipoPrevendita+"</div>"+
                        "<div class=\"col\">"+codice+"</div>"+
                        "<div class=\"col\">"+(stato == 0 ? "VALIDA" : "ANNULLATA")+"</div></div>");

        var $footer = $('<div class="row"></row>');
        var $footerCol = $('<div class="col"></row>');

        var $confirmButton = $('<button type="button" class="btn btn-primary btn-block">Conferma</button>');
        $confirmButton.click(confirmFunction);

        $footerCol.append($confirmButton);
        $footer.append($footerCol);

        $modulo.append($header);
        $modulo.append($body);
        $modulo.append($footer);
    }

    cleanModulo(){
        var $modulo = $("#moduloConferma");
        $modulo.empty();
    }

    appendListaTimbri(qrData, timbro, valido, motivo){
        if(this.scansioniAppend.includes(qrData.idPrevendita) && !valido){
            return;
        }
        this.scansioniAppend.push(qrData.idPrevendita);

        var timestamp = new Date();
        
        if(timbro !== undefined){
            timestamp = new Date(timbro.timestampEntrata);
        }

        var $lista = $("#listaTimbri");
        var $elementoLi = $("<li class=\"list-group-item "+(valido ? "list-group-item-success" : "list-group-item-danger")+"\"></li>");
        var $elementoSpan = $("<span>ID: "+ qrData.idPrevendita + ", Codice: " + qrData.codiceAccesso + " (" + timestamp.toLocaleString() + ") ("+(motivo !== undefined ? motivo : "")+")</span>");
                    
        $elementoLi.append($elementoSpan);
        $lista.append($elementoLi);
    }
}

var uiUtils = new UiUtils();
var ajax = new AjaxRequest();
var scanActive = false;
var camHasFlash = false;
var scansioniValide = [];

if (ajax.isStorageEnabled()) {

    //Ricavo l'oggetto AjaxRequest.
    //Ora funziona con mixed.
    ajax.initFromSessionStorage();

    //Quando la pagina è pronta:
    $(document).ready(function () {

        //UI
        uiUtils.disattivaMenu();
        uiUtils.attivaMenu(ajax.isLogged(), ajax.isStaffSelected(), ajax.isEventoSelected(), ajax.getDirittiMembro());
        uiUtils.impostaLoginConMessaggio(ajax.isLogged(), "Effettua una scansione", "Effettua il login prima di continuare.");

        var scanner = new QrScanner(uiUtils.getElementoVideo(), result => {
            scanner.stop();
            const obj = JSON.parse(result.data);
            
            if(!scansioniValide.includes(obj.idPrevendita)){
                ajax.restituisciInfoPrevendita(obj.idPrevendita, (response) => {
                    const prevendita = response.results[0];
                    uiUtils.compilaModulo(prevendita.nomeCliente, prevendita.cognomeCliente, prevendita.nomePR, prevendita.cognomePR, prevendita.nomeTipoPrevendita, prevendita.codice, prevendita.stato, () =>{
                        ajax.timbraEntrata(obj.idPrevendita, obj.idEvento, obj.codiceAccesso, (response2) => {
                            let scritta = "(VALIDA) Timbrata: "+ obj.idPrevendita+ " "+prevendita.nomeCliente+" "+prevendita.cognomeCliente;
                            uiUtils.impostaScritta(scritta);
                            uiUtils.cleanModulo();
                            uiUtils.appendListaTimbri(obj, response2.results[0], true);
                            scansioniValide.push(obj.idPrevendita);
                            scanner.start();
                        }, (response2) => {
                            let scritta = "(ERRORE) Impossibile timbrare ("+obj.idPrevendita+ " "+prevendita.nomeCliente+" "+prevendita.cognomeCliente+"): "+ response2.exceptions[0].msg;
                            uiUtils.impostaErrore(scritta);
                            uiUtils.cleanModulo();
                            uiUtils.appendListaTimbri(obj, undefined, false, response2.exceptions[0].msg);
                            scanner.start();
                        });
                    });
                }, (response) => {
                    let scritta = "(ERRORE) Impossibile leggere info: "+ response.exceptions[0].msg;
                    alert(scritta);
                    uiUtils.impostaErrore(scritta);
                    uiUtils.appendListaTimbri(obj, undefined, false, response.exceptions[0].msg);
                    scanner.start();
                });
            }
        },
        {highlightScanRegion: true, returnDetailedScanResult: true});
        
        const updateFlashAvailability = () => {
            scanner.hasFlash().then(hasFlash => {
                camHasFlash = hasFlash;
                if(!camHasFlash){
                    uiUtils.disabilitaFlash();
                }
            });
        };

        const startScan = () => {
            if(!scanActive){
                scanner.start().then(() => {
                    updateFlashAvailability();
                    scanActive = true;
                    uiUtils.scrittaPulsanteScansione("Ferma scansione");
                });
            }
        };

        const stopScan = () => {
            if(scanActive){
                scanner.stop();
                scanActive = false;
                uiUtils.scrittaPulsanteScansione("Avvia scansione");
            }
        };

        uiUtils.impostaClickBtnScansione(function(){
            if(!scanActive){
                  startScan();
            }
            else{
                stopScan();
            } 
        });

        uiUtils.impostaClickBtnFlash(function(){
             if(scanActive){
                if(camHasFlash){
                    if(scanner.isFlashOn()){
                        scanner.turnFlashOff();
                    }else{
                        scanner.turnFlashOn();
                    }
                }else{
                    disabilitaFlash();
                }
             }
        });

    });
} else {
    $(document).ready(function () {
        //Il browser non supporta i cookies:
        uiUtils.disattivaMenu();
        //Invio un messaggio.
        uiUtils.impostaErrore("Attiva i cookies per usare l'applicazione.");
    });
}