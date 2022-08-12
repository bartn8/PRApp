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

    popolaTipoPrevendita(listaTipoPrevendita) {
        var $select = $("#tipoPrevendita");
        var ora = new Date();

        for (let index = 0; index < listaTipoPrevendita.length; index++) {
            const tipoPrevendita = listaTipoPrevendita[index];

            var aperturaPrevendite = new Date(tipoPrevendita.aperturaPrevendite);
            var chiusuraPrevendite = new Date(tipoPrevendita.chiusuraPrevendite);
            var qtVend = tipoPrevendita.quantitaMax == 0 ? "infinita" : (tipoPrevendita.quantitaMax - tipoPrevendita.quantita);
            
            if(aperturaPrevendite <= ora && ora <= chiusuraPrevendite &&  (qtVend === "infinita" || qtVend > 0)){
                $select.append($('<option>', {
                    value: tipoPrevendita.id,
                    text: tipoPrevendita.nome + " (Da " + aperturaPrevendite.toLocaleString() + " a " + chiusuraPrevendite.toLocaleString() + ") (Vendibili: " + qtVend + ")"
                }));
            }
        }
    }

    pulisciCampi() {
        $("#nomeCliente").val("");
        $("#cognomeCliente").val("");
    }

    attivaFormCreaPrevendita(onClick) {
        $("#creaPrevenditaButton").removeClass("disabled");
        $("#creaPrevenditaButton").click(onClick);
    }


    disattivaFormCreaPrevendita() {
        $("#creaPrevenditaButton").addClass("disabled");
        $("#creaPrevenditaButton").click(function () {
            return false;
        });
    }
    
    attivaButtonCondividiQrCode(idPrevendita, nome, cognome, codice, link) {
        var textMessage = "Ciao " + nome + " " + cognome + "\n" +
            "IdPrevendita: " + idPrevendita + "\n" +
            "Codice: " + codice + "\n" +
            "Ecco il tuo codice QR: " + link;
        var encodedTextMessage = encodeURIComponent(textMessage);

        $("#condividiWhatsAppButton").removeClass("disabled");
        $("#condividiWhatsAppButton").attr("href", "whatsapp://send?text=" + encodedTextMessage);
    }

    disattivaButtonCondividiQrCode() {
        $("#condividiWhatsAppButton").addClass("disabled");
        $("#condividiWhatsAppButton").attr("href", "#");
        $("#condividiWhatsAppButton").attr("data-href", "#");
    }

    attivaButtonScaricaQrCode(idPrevendita, nome, cognome, image) {
        $("#scaricaQRCodeButton").removeClass("disabled");
        $("#scaricaQRCodeButton").attr("download", idPrevendita + "_" + nome + "_" + cognome + ".png");
        $("#scaricaQRCodeButton").attr("href", image);
    }

    disattivaButtonScaricaQrCode() {
        $("#scaricaQRCodeButton").addClass("disabled");
        $("#scaricaQRCodeButton").attr("href", "#");
        $("#scaricaQRCodeButton").attr("data-href", "#");
    }

    impostaLink(link) {
        var copyText = document.getElementById("myLink");
        copyText.value = link;
    }

    disegnaCanvas(idPrevendita, idEvento, nome, cognome, codice, nomeTipoPrevendita, image) {
        var myPerson = nome + " " + cognome;
        var myPrevendita = idPrevendita + " - " + idEvento + " - " + codice;

        $("#myCanvas").removeLayers();
        $("#myCanvas").clearCanvas();
        
        $("#myCanvas").addLayer({
            type: 'rectangle',
            name: "background",
            index: 0,
            fillStyle: '#FFF',
            fromCenter: false,
            x: 0, y: 0,
            width: 320,
            height: 410
        }).addLayer({
            type: 'image',
            name: "image",
            index: 1,
            source: image,
            x: 160, y: 160,
            fromCenter: true
        }).addLayer({
            type: 'text',
            name: "person",
            groups: ["textGroup"],
            index: 2,
            fillStyle: '#000',
            strokeStyle: '#000',
            strokeWidth: 0,
            x: 150, y: 330,
            fontSize: 12,
            fontFamily: 'Verdana, sans-serif',
            text: myPerson
        }).addLayer({
            type: 'text',
            name: "warning",
            groups: ["textGroup"],
            index: 2,
            fillStyle: '#000',
            strokeStyle: '#000',
            strokeWidth: 1,
            x: 150, y: 350,
            fontSize: 12,
            fontFamily: 'Verdana, sans-serif',
            text: 'RICORDATI UN DOCUMENTO VALIDO'
        }).addLayer({
            type: 'text',
            name: "prevendita",
            groups: ["textGroup"],
            index: 2,
            fillStyle: '#000',
            strokeStyle: '#000',
            strokeWidth: 0,
            x: 150, y: 370,
            fontSize: 13,
            fontFamily: 'Verdana, sans-serif',
            text: myPrevendita
        }).addLayer({
            type: 'text',
            name: "tipoPrevendita",
            groups: ["textGroup"],
            index: 2,                
            fillStyle: '#000',
            strokeStyle: '#000',
            strokeWidth: 0,
            x: 150, y: 390,
            fontSize: 13,
            fontFamily: 'Verdana, sans-serif',
            text: nomeTipoPrevendita
        }).drawLayers();
    }

    scaricaCanvas() {
        var imageURL = $("#myCanvas").getCanvasImage();
        return imageURL;
    }

}

var uiUtils = new UiUtils();
var ajax = new AjaxRequest();

//Roba per ricordare il cliente precedente.
var nomeClientePrecedente = "";
var cognomeClientePrecedente = "";

var generaLink = function (idPrevendita, idEvento, nome, cognome, codice, nomeTipoPrevendita) {
    //Vecchio formato
    //var url_string = "https://prapp.altervista.org/qrCode.html?idPrevendita=1&idEvento=1&nome=Nome&cognome=Cognome&data=02%2F05%2F1990&codice=ifg453";
    //Nuovo formato
    //var url_string = "https://prapp.altervista.org/qrCode.html?idPrev=1&idEv=1&nome=Nome&cognome=Cognome&data=02%2F05%2F1990&cod=ifg453&nTipoP=OMAGGIO";

    var defaultUrl = "https://prapp.altervista.org/qrCode.html";

    var idPrevenditaEncoded = encodeURIComponent(idPrevendita);
    var idEventoEncoded = encodeURIComponent(idEvento);
    var nomeEncoded = encodeURIComponent(nome);
    var cognomeEncoded = encodeURIComponent(cognome);
    var codiceEncoded = encodeURIComponent(codice);
    var nomeTipoPrevenditaEncoded = encodeURIComponent(nomeTipoPrevendita);

    return defaultUrl + "?idPrev=" + idPrevenditaEncoded + "&idEv=" + idEventoEncoded + "&nome=" + nomeEncoded + "&cognome=" + cognomeEncoded + "&cod=" + codiceEncoded + "&nTipoP=" + nomeTipoPrevenditaEncoded;
}

var onCopiaLinkClick = function () {
    /* Get the text field */
    var copyText = document.getElementById("myLink");

    /* Select the text field */
    copyText.select();

    /* Copy the text inside the text field */
    document.execCommand("copy");

    /* Alert the copied text */
    alert("Link copiato: " + copyText.value);
};

var creaPrevenditaButtonClick = function () {

    var nomeCliente = "";
    var cognomeCliente = "";

    var idTipoPrevendita = $("#tipoPrevendita").val();
    var nomeTipoPrevendita = $("#tipoPrevendita").children("option:selected").text();
    nomeTipoPrevendita = nomeTipoPrevendita.substring(0, nomeTipoPrevendita.indexOf("("));
    //Riduco il codice a soli 3 caratteri
    var codice = Math.random().toString(36).substring(2, 5);

    nomeCliente = $("#nomeCliente").val();

    //Check 
    if (nomeCliente == "") {
        //nomeCliente = null;
        //invio un messaggio sull'edit text.
        //uiUtils.erroreNomeCliente();

        alert("Nome cliente non valido");

        return;
    }

    cognomeCliente = $("#cognomeCliente").val();

    //Check 
    if (cognomeCliente == "") {
        //cognomeCliente = null;
        //invio un messaggio sull'edit text.
        //uiUtils.erroreCognomeCliente();

        alert("Cognome cliente non valido");

        return;
    }

    //Prima controllo che si siano inseriti nome e cognome diversi:
    //Se uguali invio un messaggio di conferma:
    if (nomeCliente == nomeClientePrecedente && cognomeCliente == cognomeClientePrecedente) {
        let conferma = confirm("La prevendita in creazione ha gli stessi dati di quella precedente (" + nomeCliente + " " + cognomeCliente + ") Continuo?");

        //Se l'utente preme annulla non faccio la prevendita.
        if (!conferma) {
            alert("PREVENDITA ANNULLATA");
            return;
        }
    }

    let conferma = confirm("Confermare "+ nomeCliente + " " + cognomeCliente+" ?");

    if(!conferma){
        alert("PREVENDITA ANNULLATA");
        return;
    }

    ajax.aggiungiPrevendita(nomeCliente, cognomeCliente, idTipoPrevendita, codice, function (response) {
        //ho aggiunto la prevendita, pulisco i campi.
        uiUtils.pulisciCampi();

        //Aggiorno i clienti precedenti.
        nomeClientePrecedente = nomeCliente;
        cognomeClientePrecedente = cognomeCliente;

        //Imposto un messaggio:
        uiUtils.impostaScritta("Prevendita aggiunta: " + nomeCliente + " " + cognomeCliente);

        //Creo le info per il qr.
        var prevendita = response.results[0];
        var myIdPrevendita = parseInt(prevendita.id);
        var myIdEvento = parseInt(prevendita.idEvento);
        var netWEntrata = { idPrevendita: myIdPrevendita, idEvento: myIdEvento, codiceAccesso: codice };

        //Creo il qr code.
        var typeNumber = 4;
        var errorCorrectionLevel = 'L';
        var qr = qrcode(typeNumber, errorCorrectionLevel);
        qr.addData(JSON.stringify(netWEntrata));
        qr.make();

        var qrCodeImage = qr.createDataURL(8, 36);

        uiUtils.disegnaCanvas(myIdPrevendita, myIdEvento, nomeCliente, cognomeCliente, codice, nomeTipoPrevendita, qrCodeImage);

        //Attivo i pulsanti di qr code.
        var link = generaLink(myIdPrevendita, myIdEvento, nomeCliente, cognomeCliente, codice, nomeTipoPrevendita);

        uiUtils.attivaButtonCondividiQrCode(myIdPrevendita, nomeCliente, cognomeCliente, codice, link);
        uiUtils.impostaLink(link);

        //Non posso inserire qua il link: prima devo generare il canvas
        //Aggiorno il pulsante dopo timeout (2sec).
        setTimeout(function () {
            uiUtils.attivaButtonScaricaQrCode(myIdPrevendita, nomeCliente, cognomeCliente, uiUtils.scaricaCanvas());
        }, 2000);

        //Messaggio di log.
        console.log("add prevendita ok: " + JSON.stringify(prevendita));

    }, function (response) {
        console.log("error: " + JSON.stringify(response.exceptions));
        uiUtils.impostaErrore("Errore:" + response.exceptions[0].msg);
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
        uiUtils.disattivaButtonCondividiQrCode();
        uiUtils.disattivaButtonScaricaQrCode();
        uiUtils.disattivaFormCreaPrevendita();

        uiUtils.attivaMenu(ajax.isLogged(), ajax.isStaffSelected(), ajax.isEventoSelected(), ajax.getDirittiMembro());
        uiUtils.impostaLoginConMessaggio(ajax.isLogged(), "Aggiungi una prevendita", "Effettua il login prima di continuare.");
        
        if (ajax.isLogged() && ajax.isStaffSelected() && ajax.isEventoSelected()) { 
            //Carico le prevenidite.
            ajax.getListaTipoPrevenditaEvento(function (response) {

                //popolo il select
                uiUtils.popolaTipoPrevendita(response.results);

                //Posso attivare il form
                uiUtils.attivaFormCreaPrevendita(creaPrevenditaButtonClick);

            }, function (response) {
                console.log("Error: " + response.exceptions[0].msg);
                uiUtils.impostaErrore("Impossibile recuperare i tipi prevendita.");

            });
        }else{
            //Redirect automatico alla pagina di login
            passRedirect("login.html", "pr_aggiungi_prevendita.html");
        }

    });
} else {
    $(document).ready(function () {
        //Il browser non supporta i cookies:
        uiUtils.disattivaMenu();
        uiUtils.disattivaButtonCondividiQrCode();
        uiUtils.disattivaButtonScaricaQrCode();
        uiUtils.disattivaFormCreaPrevendita();
        //Invio un messaggio.
        uiUtils.impostaErrore("Attiva i cookies per usare l'applicazione.");
    });
}