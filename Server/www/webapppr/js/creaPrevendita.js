/*
 * PRApp  Copyright (C) 2019  Luca Bartolomei
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

    impostaDatePickerOnClick() {
        $("#dataDiNascita").click(function () {
            this.apriDatePicker();
        });
    }

    apriDatePicker() {
        $("#dataDiNascita").datepicker();
    }

    pulisciCampi() {
        $("#nomeCliente").val("");
        $("#cognomeCliente").val("");
    }

    popolaTipoPrevendita(listaTipoPrevendita) {
        var $select = $("#tipoPrevendita");

        for (let index = 0; index < listaTipoPrevendita.length; index++) {
            const tipoPrevendita = listaTipoPrevendita[index];

            $select.append($('<option>', {
                value: tipoPrevendita.id,
                text: tipoPrevendita.nome
            }));

        }
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


    disegnaCanvas(idPrevendita, idEvento, nome, cognome, data, codice, nomeTipoPrevendita, image) {
        var myPerson = nome + " " + cognome + " - " + data;
        var myPrevendita = idPrevendita + " - " + idEvento + " - " + codice;

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

    erroreNomeCliente() {
        var $nomeClienteText = $("#nomeCliente").parent("div");
        var $errorIcon = $("<span class=\"glyphicon glyphicon-remove form-control-feedback\"></span>");

        $nomeClienteText.addClass("has-error");
        $nomeClienteText.addClass("has-feedback");
        $nomeClienteText.append($errorIcon);
    }

    erroreCognomeCliente() {
        var $cognomeClienteText = $("#cognomeCliente").parent("div");
        var $errorIcon = $("<span class=\"glyphicon glyphicon-remove form-control-feedback\"></span>");

        $cognomeClienteText.addClass("has-error");
        $cognomeClienteText.addClass("has-feedback");
        $cognomeClienteText.append($errorIcon);
    }

    erroreDataDiNascitaCliente() {
        var $dataDiNascitaClienteText = $("#dataDiNascita").parent("div");
        var $errorIcon = $("<span class=\"glyphicon glyphicon-remove form-control-feedback\"></span>");

        $dataDiNascitaClienteText.addClass("has-error");
        $dataDiNascitaClienteText.addClass("has-feedback");
        $dataDiNascitaClienteText.append($errorIcon);


    }

    resetFieldsCliente() {
        var $nomeClienteText = $("#nomeCliente").parent("div");
        var $cognomeClienteText = $("#cognomeCliente").parent("div");
        var $dataDiNascitaClienteText = $("#dataDiNascita").parent("div");

        $nomeClienteText.removeClass("has-error");
        $nomeClienteText.removeClass("has-feedback");
        $nomeClienteText.remove("span");

        $cognomeClienteText.removeClass("has-error");
        $cognomeClienteText.removeClass("has-feedback");
        $cognomeClienteText.remove("span");

        $dataDiNascitaClienteText.removeClass("has-error");
        $dataDiNascitaClienteText.removeClass("has-feedback");
        $dataDiNascitaClienteText.remove("span");
    }

}

var generaLink = function (idPrevendita, idEvento, nome, cognome, data, codice, nomeTipoPrevendita) {
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

    if(data != null){
        var dataEncoded = encodeURIComponent(data);
        return defaultUrl + "?idPrev=" + idPrevenditaEncoded + "&idEv=" + idEventoEncoded + "&nome=" + nomeEncoded + "&cognome=" + cognomeEncoded + "&data=" + dataEncoded + "&cod=" + codiceEncoded + "&nTipoP=" + nomeTipoPrevenditaEncoded;
    }
    else{
        return defaultUrl + "?idPrev=" + idPrevenditaEncoded + "&idEv=" + idEventoEncoded + "&nome=" + nomeEncoded + "&cognome=" + cognomeEncoded + "&cod=" + codiceEncoded + "&nTipoP=" + nomeTipoPrevenditaEncoded;
    }

}

var onCopiaLinkClick = function () {
    /*
    var $myHiddenInput = $("#myHiddenInput");
    $myHiddenInput.attr("value", link);
    $myHiddenInput.select();
    document.execCommand("copy");
    */

    /* Get the text field */
    var copyText = document.getElementById("myLink");

    /* Select the text field */
    copyText.select();

    /* Copy the text inside the text field */
    document.execCommand("copy");

    /* Alert the copied text */
    alert("Link copiato: " + copyText.value);
};

//https://stackoverflow.com/questions/13459866/javascript-change-date-into-format-of-dd-mm-yyyy
var convertiData = function (inputFormat) {
    if (inputFormat == null) {
        return "XX-XX-XXXX";
    }

    function pad(s) { return (s < 10) ? '0' + s : s; }
    var d = new Date(inputFormat);
    return [pad(d.getDate()), pad(d.getMonth() + 1), d.getFullYear()].join('/');
};

var uiUtils = new UiUtils();
var ajax = new AjaxRequest();

//Roba per ricordare il cliente precedente.
var nomeClientePrecedente = "";
var cognomeClientePrecedente = "";

var loginToken = function (needRenew) {
    //Devo verificare lo stato di login:
    var token = Cookies.get('token');

    if (token !== undefined && token !== null) {
        //Esiste il token: vedo se è scaduto oppure no.
        ajax.loginToken(token, function (response) {
            console.log("Login token ok");

            if (needRenew) {
                //Renew del token.
                ajax.renewToken(function (response2) {
                    Cookies.set("token", response2.results[0].token, { expires: 7 });
                    console.log("Renew token ok");
                }, function (response2) {
                    console.log("Renew token failed: " + response2.exceptions[0].msg);
                    Cookies.remove("token");
                });
            }

            funzionePrincipale();

        }, function (response) {
            console.log("Login token failed: " + response.exceptions[0].msg);
            //Devo effettuare il login normale.
            uiUtils.impostaErrore("Devi effettuare l'accesso per continuare.");
            uiUtils.impostaLogin();
        });
    }
    else {
        //Devo effettuare il login normale.
        uiUtils.impostaErrore("Devi effettuare l'accesso per continuare.");
        uiUtils.impostaLogin();
    }
};

var funzionePrincipale = function () {
    uiUtils.impostaLogout();
    uiUtils.attivaMenu();

    if (ajax.isEventoSelected()) {
        uiUtils.impostaScritta("Aggiungi una prevendita");


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
    } else {
        uiUtils.impostaErrore("Devi scegliere un evento per continuare.");
    }
};

var creaPrevenditaButtonClick = function () {
    var nomeCliente = $("#nomeCliente").val();

    //Check 
    if (nomeCliente == "") {
        //nomeCliente = null;
        //invio un messaggio sull'edit text.
        //uiUtils.erroreNomeCliente();

        alert("Nome cliente non valido");

        return;
    }

    var cognomeCliente = $("#cognomeCliente").val();

    //Check 
    if (cognomeCliente == "") {
        //cognomeCliente = null;
        //invio un messaggio sull'edit text.
        //uiUtils.erroreCognomeCliente();

        alert("Cognome cliente non valido");

        return;
    }

    var dataDiNascita = $("#dataDiNascita").val();

    //Check 
    if (dataDiNascita == "") {
        dataDiNascita = null;
    }

    var idTipoPrevendita = $("#tipoPrevendita").val();
    var nomeTipoPrevendita = $("#tipoPrevendita").children("option:selected").text();
    //Riduco il codice a soli 3 caratteri
    var codice = Math.random().toString(36).substring(2, 5);
    var stato = $("#statoPrevendita").val();


    //Prima controllo che si siano inseriti nome e cognome diversi:
    //Se uguali invio un messaggio di conferma:
    if (nomeCliente == nomeClientePrecedente && cognomeCliente == cognomeClientePrecedente) {
        var conferma = confirm("La prevendita in creazione ha gli stessi dati di quella precedente (" + nomeCliente + " " + cognomeCliente + ") Continuo?");

        //Se l'utente preme annulla non faccio la prevendita.
        if (!conferma) {
            alert("PREVENDITA ANNULLATA");
            return;
        }
    }

    ajax.aggiungiCliente(nomeCliente, cognomeCliente, dataDiNascita, function (response) {
        console.log("add cliente ok: " + JSON.stringify(response.results[0]));

        //Posso ricavare il cliente e aggiungere la prevendita:
        var idCliente = response.results[0].id;

        //Aggiorno i clienti precedenti.
        nomeClientePrecedente = nomeCliente;
        cognomeClientePrecedente = cognomeCliente;

        ajax.aggiungiPrevendita(idCliente, idTipoPrevendita, codice, stato, function (response2) {
            //ho aggiunto la prevendita, pulisco i campi.
            uiUtils.pulisciCampi();

            //Imposto un messaggio:
            uiUtils.impostaScritta("Prevendita aggiunta: " + nomeCliente + " " + cognomeCliente);

            //Creo le info per il qr.
            var prevendita = response2.results[0];
            var myIdPrevendita = parseInt(prevendita.id);
            var myIdEvento = parseInt(prevendita.idEvento);
            var netWEntrata = { idPrevendita: myIdPrevendita, idEvento: myIdEvento, codiceAccesso: codice };

            //Creo il qr code.
            var typeNumber = 4;
            var errorCorrectionLevel = 'L';
            var qr = qrcode(typeNumber, errorCorrectionLevel);
            qr.addData(JSON.stringify(netWEntrata));
            qr.make();

            var myDataDiNascita = convertiData(dataDiNascita);
            var qrCodeImage = qr.createDataURL(8, 36);

            uiUtils.disegnaCanvas(myIdPrevendita, myIdEvento, nomeCliente, cognomeCliente, myDataDiNascita, codice, nomeTipoPrevendita, qrCodeImage);

            //Attivo i pulsanti di qr code.
            var link = generaLink(myIdPrevendita, myIdEvento, nomeCliente, cognomeCliente, dataDiNascita != null ? myDataDiNascita : null, codice, nomeTipoPrevendita);

            uiUtils.attivaButtonCondividiQrCode(myIdPrevendita, nomeCliente, cognomeCliente, codice, link);
            uiUtils.impostaLink(link);

            //Non posso inserire qua il link: prima devo generare il canvas
            //Aggiorno il pulsante dopo timeout (2sec).
            setTimeout(function () {
                uiUtils.attivaButtonScaricaQrCode(myIdPrevendita, nomeCliente, cognomeCliente, uiUtils.scaricaCanvas());
            }, 2000);

            //Messaggio di log.
            console.log("add prevendita ok: " + JSON.stringify(prevendita));

        }, function (response2) {
            console.log("error: " + JSON.stringify(response2.exceptions));
            uiUtils.impostaErrore("Errore:" + response2.exceptions[0].msg);
        });

    }, function (response) {
        console.log("error: " + JSON.stringify(response.exceptions));
        uiUtils.impostaErrore("Errore:" + response.exceptions[0].msg);
    });
};

if (ajax.isStorageEnabled()) {

    //Ricavo l'oggetto AjaxRequest.
    //Ora funziona con mixed.
    ajax.initFromMixedStorage();

    //Quando la pagina è pronta:
    $(document).ready(function () {

        //Disattivo i pulsanti di qr code.
        uiUtils.disattivaButtonCondividiQrCode();
        uiUtils.disattivaButtonScaricaQrCode();

        //Disattivo temporaneamente i menu e il form.
        uiUtils.disattivaMenu();
        uiUtils.disattivaFormCreaPrevendita();

        //Se sono loggato allora disattivo il login e attivo le altre pagine.
        if (ajax.isLogged()) {
            //uiUtils.impostaLogout();
            //uiUtils.attivaMenu();
            funzionePrincipale();

        } else {
            //Prima controllo se c'è un problema di sessione:
            ajax.restituisciUtente(function (response) {
                //Siamo loggati:
                funzionePrincipale();
            }, function (response) {
                //Probabilmente non siamo veramente loggati:
                loginToken(false);
            });
        }
    });
} else {
    $(document).ready(function () {
        //Il browser non supporta i cookies:
        uiUtils.disattivaTuttiMenu();
        uiUtils.disattivaFormCreaPrevendita();

        uiUtils.disattivaButtonCondividiQrCode();

        //Invio un messaggio.
        uiUtils.impostaErrore("Attiva i cookies per usare l'applicazione.");
    });
}