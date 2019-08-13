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

class AjaxRequest {

    constructor() {
        this.url = "../framework/ajax.php";

        this.defaultUtente = {id:0, nome:"ND", cognome:"ND", telefono:"ND"};
        this.defaultStaff = {id: 0, nome:"ND", timestampCreazione: "1970-01-01T00:00:00.000Z"};
        this.defaultEvento = {id: 0, idStaff: 0, idCreatore: 0, nome: "ND", descrizione: null, inizio: "1970-01-01T00:00:00.000Z", fine: "1970-01-01T00:00:00.000Z", indirizzo: "ND", stato: 0, idModificatore: null, timestampUltimaModifica: "1970-01-01T00:00:00.000Z"};;

        this.utente = Object.assign({}, this.defaultUtente);
        this.staff = Object.assign({}, this.defaultStaff);
        this.evento = Object.assign({}, this.defaultEvento);
    }

    initFromSessionStorage(){
        if(sessionStorage.ajaxRequestEvento !== undefined && sessionStorage.ajaxRequestUtente !== undefined && sessionStorage.ajaxRequestStaff !== undefined)
        {
            this.initFromJson(sessionStorage.ajaxRequestUtente, sessionStorage.ajaxRequestStaff, sessionStorage.ajaxRequestEvento);
        }
    }

    initFromJson(myUtente, myStaff, myEvento){
        this.utente = JSON.parse(myUtente);
        this.staff = JSON.parse(myStaff);
        this.evento = JSON.parse(myEvento);
    }

    saveToSessionStorage()
    {
        sessionStorage.ajaxRequestUtente = JSON.stringify(this.utente);
        sessionStorage.ajaxRequestStaff = JSON.stringify(this.staff);
        sessionStorage.ajaxRequestEvento = JSON.stringify(this.evento);
    }

    restoreDefaultUtente()
    {
        this.utente = Object.assign({}, this.defaultUtente);
        this.saveToSessionStorage();
    }

    restoreDefaultStaff()
    {
        this.staff = Object.assign({}, this.defaultStaff);
        this.saveToSessionStorage();
    }

    restoreDefaultEvento()
    {
        this.evento = Object.assign({}, this.defaultEvento);
        this.saveToSessionStorage();
    }

    setStaff(myStaff){
        this.staff = myStaff;
        this.saveToSessionStorage();
    }

    setEvento(myEvento){
        this.evento = myEvento;
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

    isLogged(){
        return this.utente.id !== 0;
    }

    isStaffSelected(){
        return this.staff.id !== 0;
    }

    isEventoSelected(){
        return this.evento.id !== 0;
    }

    login(myUsername, myPassword, onSuccess, onError) {
        //Devo essere non loggato.
        if(this.utente.id !== 0)
            return;

        var data = {
            command: 2,
            args: JSON.stringify([{
                name: "login",
                type: "com\\model\\net\\wrapper\\NetWLogin",
                value: {username: myUsername, password: myPassword}
            }])
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
                        //Imposto l'utente corrente.
                        this.context.utente = response.results[0];
                        this.context.saveToSessionStorage();
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

    logout(onSuccess, onError) {
        //Devo essere loggato.
        if(this.utente.id === 0)
            return;

        var data = {
            command: 3,
            args: JSON.stringify([])
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
                        this.context.restoreDefaultUtente();
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

    getListaStaffMembri(onSuccess, onError) {
        //Devo essere loggato.
        if(this.utente.id === 0)
            return;
        
        var data = {
            command: 7,
            args: JSON.stringify([])
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

    renewToken(onSuccess, onError) {
        //Devo essere loggato.
        if(this.utente.id === 0)
            return;

        var data = {
            command: 8,
            args: JSON.stringify([])
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

    loginToken(myToken, onSuccess, onError) {
        //Devo essere non loggato.
        if(this.utente.id !== 0)
            return;

        var data = {
            command: 10,
            args: JSON.stringify([{
                name: "token",
                type: "com\\model\\net\\wrapper\\NetWToken",
                value: {token: myToken}
            }])
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
                        //Imposto l'utente corrente.
                        this.context.utente = response.results[0];
                        this.context.saveToSessionStorage();
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

    restituisciUtente(onSuccess, onError) {
        var data = {
            command: 11,
            args: JSON.stringify([])
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
                        //Imposto l'utente corrente.
                        this.context.utente = response.results[0];
                        this.context.saveToSessionStorage();
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

    getListaEventiStaff(onSuccess, onError) {
        //Devo essere loggato.
        if(this.utente.id === 0)
            return;

        //Devo aver scelto lo staff.
        if(this.staff.id === 0)
            return;

        var data = {
            command: 105,
            args: JSON.stringify([
                {name:"staff", type:"com\\model\\net\\wrapper\\NetWId", value:{"id": this.staff.id}}
            ])
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

    getListaTipoPrevenditaEvento(onSuccess, onError) {
        //Devo essere loggato.
        if(this.utente.id === 0)
            return;
        
        //Devo aver scelto l'evento.
        if(this.evento.id === 0)
            return;

        var data = {
            command: 106,
            args: JSON.stringify([
                {name:"evento", type:"com\\model\\net\\wrapper\\NetWId", value:{"id": this.evento.id}}
            ])
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

    aggiungiCliente(myNome, myCognome, myDataDiNascita, onSuccess, onError) {
        //Devo essere loggato.
        if(this.utente.id === 0)
            return;
        
        var data = {
            command: 202,
            args: JSON.stringify([{
                name: "cliente",
                type: "com\\model\\net\\wrapper\\insert\\InsertNetWCliente",
                value: {idStaff: this.staff.id, nome: myNome, cognome: myCognome, dataDiNascita: myDataDiNascita}
            }])
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

    aggiungiPrevendita(myClienteId, myTipoPrevenditaId, myCodice, myStato, onSuccess, onError) {
        //Devo essere loggato.
        if(this.utente.id === 0)
            return;
        
        //Devo aver scelto l'evento.
        if(this.evento.id === 0)
            return;


        var data = {
            command: 203,
            args: JSON.stringify([{
                name: "prevendita",
                type: "com\\model\\net\\wrapper\\insert\\InsertNetWPrevendita",
                value: {idEvento: this.evento.id, idCliente: parseInt(myClienteId), idTipoPrevendita: parseInt(myTipoPrevenditaId), codice: myCodice, stato: parseInt(myStato)}
            }])
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

    restituisciPrevendite(onSuccess, onError) {
        //Devo essere loggato.
        if(this.utente.id === 0)
            return;
        
        //Devo aver scelto l'evento.
        if(this.evento.id === 0)
            return;

        var data = {
            command: 209,
            args: JSON.stringify([
                {name:"evento", type:"com\\model\\net\\wrapper\\NetWId", value:{"id": this.evento.id}}
            ])
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

}