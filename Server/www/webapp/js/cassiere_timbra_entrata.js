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

    compilaModulo(nome, cognome, nomePR, cognomePR, nomeTipoPrevendita, codice, stato, confirmFunction, cancelFunction){
        let $tabBody = $("#bodyTabella");
        let $containerConferma = $("#containerConferma");

        let $body =   $("<td>"+nome+" "+cognome+"</td>"+
                        "<td>"+nomePR+" "+cognomePR+"</td>"+
                        "<td>"+nomeTipoPrevendita+"</td>"+
                        "<td>"+codice+"</td>"+
                        "<td>"+(stato == 0 ? "VALIDA" : "ANNULLATA")+"</td>");


        let $confirmButton = $('<button type="button" class="btn btn-primary btn-block">Conferma</button>');
        let $cancelButton = $('<button type="button" class="btn btn-danger btn-block">Annulla</button>');
        
        $confirmButton.click(confirmFunction);
        $cancelButton.click(cancelFunction);

        let $colConfirm = $("#colConfirm");
        let $colCancel = $("#colCancel");

        $tabBody.append($body);
        $colConfirm.append($confirmButton);
        $colCancel.append($cancelButton);
    }

    cleanModulo(){
        var $tabBody = $("#bodyTabella");
        var $confirmButton = $("#colConfirm > button");
        var $cancelButton = $("#colCancel > button");
        $tabBody.empty();
        $confirmButton.remove();
        $cancelButton.remove();
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
        var $elementoSpan = $("<span>ID: "+ qrData.idPrevendita + ", Codice: " + qrData.codiceAccesso + " (" + timestamp.toLocaleString() + ")"+(motivo !== undefined && motivo !== "" ? " (" + motivo + ")" : "")+"</span>");
                    
        $elementoLi.append($elementoSpan);
        $lista.append($elementoLi);
    }
}

var uiUtils = new UiUtils();
var ajax = new AjaxRequest();
var scanActive = false;
var camHasFlash = false;
var scansioniValide = [];
var listaScansioni = [];

var updateScansioniValide = (idPrevendita) => {
    scansioniValide.push(idPrevendita);
    sessionStorage.cassiere_scansioni_valide = JSON.stringify(scansioniValide);
}

var restoreScansioniValide = () => {
    let tmpScansioniValide = sessionStorage.cassiere_scansioni_valide;
    if(tmpScansioniValide !== undefined && tmpScansioniValide !== ""){
        scansioniValide = JSON.parse(tmpScansioniValide);
    }
};

var updateListaScansioni = (qrData, timbro, valido, motivo) =>{
    listaScansioni.push([qrData, timbro, valido, motivo]);
    sessionStorage.cassiere_lista_scansioni = JSON.stringify(listaScansioni);
};

var restoreListaScansioni = () => {
    let tmpListaScansioni = sessionStorage.cassiere_lista_scansioni;
    if(tmpListaScansioni !== undefined && tmpListaScansioni !== ""){
        listaScansioni = JSON.parse(tmpListaScansioni);
        for(let i = 0; i < listaScansioni.length; i++){
            let scansione = listaScansioni[i];
            uiUtils.appendListaTimbri(scansione[0], scansione[1], scansione[2], scansione[3]);
        }
    }
};

var main = () => {
    var scanner = new QrScanner(uiUtils.getElementoVideo(), result => {
        scanner.stop();
        const obj = JSON.parse(result.data);
        
        if(!scansioniValide.includes(obj.idPrevendita)){
            ajax.restituisciInfoPrevendita(obj.idPrevendita, (response) => {
                const prevendita = response.results[0];
                uiUtils.compilaModulo(prevendita.nomeCliente, prevendita.cognomeCliente, prevendita.nomePR, prevendita.cognomePR, prevendita.nomeTipoPrevendita, prevendita.codice, prevendita.stato, () =>{
                    ajax.timbraEntrata(obj.idPrevendita, obj.idEvento, obj.codiceAccesso, (response2) => {
                        let scritta = "(VALIDA) Timbrata: "+ obj.idPrevendita+ " "+prevendita.nomeCliente+" "+prevendita.cognomeCliente;
                        //Update grafica: pulizia modulo e aggiunta alla lista timbri
                        uiUtils.impostaScritta(scritta);
                        uiUtils.cleanModulo();
                        uiUtils.appendListaTimbri(obj, response2.results[0], true);
                        //Aggiunga ad array per logica
                        updateListaScansioni(obj, response2.results[0], true, "");
                        updateScansioniValide(obj.idPrevendita);
                        scanner.start();
                    }, (response2) => {
                        let scritta = "(ERRORE) Impossibile timbrare ("+obj.idPrevendita+ " "+prevendita.nomeCliente+" "+prevendita.cognomeCliente+"): "+ response2.exceptions[0].msg;
                        //Update grafica: pulizia modulo e aggiunta alla lista timbri
                        uiUtils.impostaErrore(scritta);
                        uiUtils.cleanModulo();
                        uiUtils.appendListaTimbri(obj, undefined, false, response2.exceptions[0].msg);
                        //Aggiunga ad array per logica
                        updateListaScansioni(obj, undefined, false, response2.exceptions[0].msg);
                        scanner.start();
                    });
                }, () => {
                    //Prevendita non confermata pulisco e riattivo
                    uiUtils.cleanModulo();
                    scanner.start();
                });
            }, (response) => {
                let scritta = "(ERRORE) Impossibile leggere info: "+ response.exceptions[0].msg;
                alert(scritta);
                uiUtils.impostaErrore(scritta);
                uiUtils.appendListaTimbri(obj, undefined, false, response.exceptions[0].msg);
                updateListaScansioni(obj, undefined, false, response.exceptions[0].msg);
                scanner.start();
            });
        }else{
            alert("Prevendita già scannerizzata: "+ obj.idPrevendita);
            scanner.start();
        }
    },
    {highlightScanRegion: true, maxScansPerSecond: 5, returnDetailedScanResult: true});
    
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
};

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

        if (ajax.isLogged() && ajax.isStaffSelected() && ajax.isEventoSelected()) {
            //restore della lista e funzione main
            restoreListaScansioni();
            restoreScansioniValide();
            main();
        }else{
            //Redirect automatico alla pagina di login
            passRedirect("login.html", "cassiere_timbra_entrata.html");
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