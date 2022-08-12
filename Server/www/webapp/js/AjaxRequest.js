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

class AjaxRequest {

    constructor() {
        this.url = "../framework/ajax.php";

        this.defaultUtente = {id:0, nome:"ND", cognome:"ND", telefono:"ND"};
        this.defaultStaff = {id: 0, nome:"ND", timestampCreazione: "1970-01-01T00:00:00.000Z"};
        this.defaultEvento = {id: 0, idStaff: 0, idCreatore: 0, nome: "ND", descrizione: null, inizio: "1970-01-01T00:00:00.000Z", fine: "1970-01-01T00:00:00.000Z", indirizzo: "ND", stato: 0, idModificatore: null, timestampUltimaModifica: "1970-01-01T00:00:00.000Z"};;

        this.utente = Object.assign({}, this.defaultUtente);
        this.staff = Object.assign({}, this.defaultStaff);
        this.evento = Object.assign({}, this.defaultEvento);

        this.dirittiMembro = [];
    }

    isStorageEnabled(){
        return typeof (Storage) !== "undefined";
    }

    isCookiesEnabled(){
        return navigator.cookieEnabled;
    }

    /**
     * Inizializza l'oggetto a partire dalla Session Storage del browser.
     */
    initFromSessionStorage(){
        var requestUtente = sessionStorage.ajaxRequestUtente;
        var requestStaff = sessionStorage.ajaxRequestStaff;
        var requestEvento = sessionStorage.ajaxRequestEvento;
        var requestDiritti = sessionStorage.ajaxRequestDiritti;

        if(requestUtente !== undefined && requestStaff !== undefined && requestEvento !== undefined && requestDiritti !== undefined)
        {
            if(requestUtente !== "" && requestStaff !== "" && requestEvento !== "" && requestDiritti !== undefined)
            {
                this.initFromJson(requestUtente, requestStaff, requestEvento, requestDiritti);
            }
        }
    }

    /**
     * Inizializza l'oggetto a partire dai cookies.
     */
    initFromCookies(){
        var requestUtente = Cookies.get("ajaxRequestUtente");
        var requestStaff = Cookies.get("ajaxRequestStaff");
        var requestEvento = Cookies.get("ajaxRequestEvento");
        var requestDiritti = Cookies.get("ajaxRequestDiritti");

        if(requestUtente !== undefined && requestStaff !== undefined && requestEvento !== undefined && requestDiritti !== undefined)
        {
            if(requestUtente !== "" && requestStaff !== "" && requestEvento !== "" && requestDiritti !== undefined)
            {
                this.initFromJson(requestUtente, requestStaff, requestEvento, requestDiritti);
            }
        }
    }

    /**
     * Inizializza da stringhe
     * 
     * @param {string} myUtente 
     * @param {string} myStaff 
     * @param {string} myEvento 
     */
    initFromJson(myUtente, myStaff, myEvento, myDiritti){
        this.utente = JSON.parse(myUtente);
        this.staff = JSON.parse(myStaff);
        this.evento = JSON.parse(myEvento);
        this.dirittiMembro = JSON.parse(myDiritti);
    }

    /**
     * Salva l'oggetto sul session storage.
     * Deprecato: usare i cookies.
     */
    saveToSessionStorage()
    {
        sessionStorage.ajaxRequestUtente = JSON.stringify(this.utente);
        sessionStorage.ajaxRequestStaff = JSON.stringify(this.staff);
        sessionStorage.ajaxRequestEvento = JSON.stringify(this.evento);
        sessionStorage.ajaxRequestDiritti = JSON.stringify(this.dirittiMembro);
    }

    /**
     * Salva l'oggetto sui cookies.
     */
    saveToCookies(){
        Cookies.set("ajaxRequestUtente", JSON.stringify(this.utente), { expires: 7 });
        Cookies.set("ajaxRequestStaff", JSON.stringify(this.staff), { expires: 7 });
        Cookies.set("ajaxRequestEvento", JSON.stringify(this.evento), { expires: 7 });
        Cookies.set("ajaxRequestDiritti", JSON.stringify(this.dirittiMembro), { expires: 7 });
    }

    restoreDefaultUtente()
    {
        this.utente = Object.assign({}, this.defaultUtente);
        this.saveToSessionStorage();
        //this.saveToCookies();
    }

    restoreDefaultStaff()
    {
        this.staff = Object.assign({}, this.defaultStaff);
        this.saveToSessionStorage();
        //this.saveToCookies();
    }

    restoreDefaultEvento()
    {
        this.evento = Object.assign({}, this.defaultEvento);
        this.saveToSessionStorage();
        //this.saveToCookies();
    }

    restoreDefaultDiritti(){
        this.dirittiMembro = [];
        this.saveToSessionStorage();
    }

    setUtente(myUtente){
        this.utente = myUtente;
        this.saveToSessionStorage();
    }

    setStaff(myStaff){
        this.staff = myStaff;
        this.saveToSessionStorage();
        //this.saveToCookies();
    }

    setEvento(myEvento){
        this.evento = myEvento;
        this.saveToSessionStorage();
        //this.saveToCookies();
    }

    setDiritti(myDiritti){
        this.dirittiMembro = myDiritti;
        this.saveToSessionStorage();
    }

    getUtente(){
        return this.utente;
    }

    getStaff(){
        return this.staff;
    }

    getEvento(){
        return this.evento;
    }

    getDirittiMembro(){
        return this.dirittiMembro;
    }

    getDefaultEvento(){
        return Object.assign({}, this.defaultEvento);
    }

    getDefaultStaff(){
        return Object.assign({}, this.defaultStaff);
    }

    getDefaultDirittiMembro(){
        return [];
    }

    isLogged(){
        return this.utente.id !== 0;
    }

    isStaffSelected(){
        return this.staff.id !== 0;
    }

    isEventoSelected(){
        return this.evento.id !== 0;
    }

    generalRequest(checkLoggato, checkStaff, checkEvento, cmd, args, onSuccess, onError){
        if(checkLoggato !== undefined){
            if(checkLoggato && this.utente.id === 0)
                return;
            if(!checkLoggato && this.utente.id !== 0)
                return;
        }

        if(checkStaff !== undefined){
            if(checkStaff && this.staff.id === 0)
                return;
            if(!checkStaff && this.staff.id !== 0)
                return;
        }
        
        if(checkEvento !== undefined){
            if(checkEvento && this.evento.id === 0)
                return;
            if(!checkEvento && this.evento.id !== 0)
                return;
        }

        var data = {
            command: cmd,
            args: JSON.stringify(args)
        };

        var context = {
            context: this,
            onSuccess: onSuccess,
            onError: onError
        };

        $.ajax({
            type: "POST",
            url: this.url,
            data: data,
            context: context,
            success: function (response) {
                switch (response.status) {
                    case 0:
                        this.onSuccess(response);
                        break;
                    case 2:
                        this.onError(response);
                        break;
                    default:
                        break;
                }
            },
            dataType: "json"
        });        
    }

    login(myUsername, myPassword, onSuccess, onError) {
        this.generalRequest(false, undefined, undefined, 2, [{
            name: "login",
            value: {username: myUsername, password: myPassword}
        }], (response) => {
            this.restoreDefaultUtente();
            this.restoreDefaultStaff();
            this.restoreDefaultEvento();
            this.restoreDefaultDiritti();
            this.setUtente(response.results[0]);
            onSuccess(response);
        }, onError);
    }

    logout(onSuccess, onError) {
        this.generalRequest(true, undefined, undefined, 3, [], (response) => {
            this.restoreDefaultUtente();
            this.restoreDefaultStaff();
            this.restoreDefaultEvento();
            this.restoreDefaultDiritti();
            onSuccess(response);
        }, onError);
    }

    getListaStaffMembri(onSuccess, onError) {
        this.generalRequest(true, undefined, undefined, 7, [], onSuccess, onError);
    }

    restituisciUtente(onSuccess, onError) {
        //login non controllato perché mi serve per aggiornare lo storage
        this.generalRequest(undefined, undefined, undefined, 11, [], (response) => {
            this.setUtente(response.results[0]);
            onSuccess(response);
        }, onError);
    }

    scegliStaff(idStaff, onSuccess, onError){
        this.generalRequest(true, undefined, undefined, 12, [
            {name:"staff", 
             value:{"id": idStaff}}
        ], (response) => {
            this.setStaff(response.results[0]);
            onSuccess(response);
        }, onError);        
    }

    restituisciStaff(onSuccess, onError){
        //staff non controllato perché mi serve per aggiornare lo storage
        this.generalRequest(true, undefined, undefined, 13, [], (response) => {
            this.setStaff(response.results[0]);
            onSuccess(response);
        }, onError);        
    }

    //getStaffScelto(onSuccess, onError){}

    getMembriStaff(onSuccess, onError) {
        this.generalRequest(true, true, undefined, 102, [], onSuccess, onError);
    }

    getDirittiUtenteStaff(onSuccess, onError) {
        this.generalRequest(true, true, undefined, 103, [], (response) => {
            this.setDiritti(response.results[0]["ruoli"]);
            onSuccess(response);
        }, onError);
    }

    getListaEventiStaff(onSuccess, onError) {
        this.generalRequest(true, true, undefined, 105, [], onSuccess, onError);
    }

    getListaTipoPrevenditaEvento(onSuccess, onError) {
        this.generalRequest(true, true, true, 106, [], onSuccess, onError);
    }

    scegliEvento(idEvento, onSuccess, onError){
        this.generalRequest(true, true, undefined, 108, [
            {name:"evento", 
             value:{"id": idEvento}}
        ], (response) => {
            this.setEvento(response.results[0]);
            onSuccess(response);
        }, onError);
    }

    restituisciEvento(onSuccess, onError){
        //evento non controllato perché mi serve per aggiornare lo storage
        this.generalRequest(true, true, undefined, 109, [], (response) => {
            this.setEvento(response.results[0]);
            onSuccess(response);
        }, onError);        
    }
    
    aggiungiPrevendita(myNomeCliente, myCognomeCliente, myTipoPrevenditaId, myCodice, onSuccess, onError) {
        this.generalRequest(true, true, true, 203, [{
            name: "prevendita",
            value: {nomeCliente: myNomeCliente, cognomeCliente: myCognomeCliente,
                 idTipoPrevendita: parseInt(myTipoPrevenditaId), codice: myCodice, stato: 0}
        }], onSuccess, onError);
    }

    modificaPrevendita(myIdPrevendita, myStato, onSuccess, onError){
        this.generalRequest(true, undefined, undefined, 204, [{
            name: "prevendita",
            value: {id: parseInt(myIdPrevendita), stato: parseInt(myStato)}
        }], onSuccess, onError);
    }

    restituisciStatistichePREvento(onSuccess, onError) {
        this.generalRequest(true, true, true, 208, [], onSuccess, onError);
    }

    restituisciPrevendite(onSuccess, onError) {
        this.generalRequest(true, true, true, 209, [], onSuccess, onError);
    }

    timbraEntrata(myIdPrevendita, myIdEvento, myCodice, onSuccess, onError) {
        this.generalRequest(true, true, true, 302, [{"name":"entrata", 
        "value":{"idPrevendita":parseInt(myIdPrevendita), "idEvento":parseInt(myIdEvento), "codiceAccesso":myCodice}}], onSuccess, onError);
    }

    restituisciStatisticheCassiereEvento(onSuccess, onError){
        this.generalRequest(true, true, true, 306, [], onSuccess, onError);
    }

    restituisciInfoPrevendita(myIdPrevendita, onSuccess, onError) {
        this.generalRequest(true, true, true, 309, [{"name":"prevendita", "value":{"id": parseInt(myIdPrevendita)}}], onSuccess, onError);
    }

    restituisciListaEntrate(onSuccess, onError) {
        this.generalRequest(true, true, true, 310, [], onSuccess, onError);
    }

    restituisciListaNonEntrate(onSuccess, onError) {
        this.generalRequest(true, true, true, 311, [], onSuccess, onError);
    }

    restituisciStatisticheEventoAmm(onSuccess, onError) {
        this.generalRequest(true, true, true, 411, [], onSuccess, onError);
    }

    restituisciPrevenditeEventoAmm(onSuccess, onError) {
        this.generalRequest(true, true, true, 412, [], onSuccess, onError);
    }

    restituisciStatistichePREventoAmm(myIdPR, onSuccess, onError) {
        this.generalRequest(true, true, true, 415, [{"name":"pr", "value":{"id": parseInt(myIdPR)}}], onSuccess, onError);
    }

    restituisciStatisticheCassiereEventoAmm(myIdCassiere,onSuccess, onError) {
        this.generalRequest(true, true, true, 416, [{"name":"cassiere", "value":{"id": parseInt(myIdCassiere)}}], onSuccess, onError);
    }

    modificaPrevenditaAmm(myIdPrevendita, myStato, onSuccess, onError){
        this.generalRequest(true, undefined, undefined, 418, [{
            name: "prevendita",
            value: {id: parseInt(myIdPrevendita), stato: parseInt(myStato)}
        }], onSuccess, onError);
    }

}