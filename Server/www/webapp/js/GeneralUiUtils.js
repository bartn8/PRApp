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

class GeneralUiUtils {

    constructor(){

    }

    impostaLoginConMessaggio(loggato, loggedWelcome, notLoggedWelcome){
        if(!loggato){
            this.impostaLogin();
            this.impostaScritta(notLoggedWelcome);
        }else{
            this.impostaLogout();
            this.impostaScritta(loggedWelcome);
        }
    }

    impostaLogin(){
        $("#login_logout").text("Login");
        $("#login_logout").attr("href", "login.html");    
    }

    impostaLogout(){
        $("#login_logout").text("Logout");
        $("#login_logout").attr("href", "logout.html");
    }

    impostaScritta(scritta){
        $("#text").text(scritta);
        $("#text").css('color', 'black');
    }

    appendScritta(scritta){
        var tmp = $("#text").text();
        tmp += "<br>";
        tmp += scritta;
        $("#text").html(tmp);
        $("#text").css('color', 'black');
    }

    impostaErrore(errore){
        $("#text").html(errore + " Prova a <a href=\"reset.html\">resettare</a>");
        $("#text").css('color', 'red');
    }

    attivaMenu(isLoggato, isStaffScelto, isEventoScelto){
        if(isLoggato){
            $("#scegliStaff").removeClass("disabled");
            $("#scegliStaff").attr("href", "utente_scegli_staff.html");

            $("#navbardropUtente").removeClass("disabled");
            
            if(isStaffScelto){
                $("#scegliEvento").removeClass("disabled");
                $("#scegliEvento").attr("href", "utente_scegli_evento.html");
            
                if(isEventoScelto){
                    $("#navbardropMembro").removeClass("disabled");
                    $("#navbardropPR").removeClass("disabled");
                    $("#navbardropCassiere").removeClass("disabled");
                    $("#navbardropAmministratore").removeClass("disabled");
                }
            }
        }
    }

    disattivaMenu(){
        $("#scegliStaff").addClass("disabled");
        $("#scegliStaff").attr("href", "#");

        $("#scegliEvento").addClass("disabled");
        $("#scegliEvento").attr("href", "#");

        $("#navbardropUtente").addClass("disabled");
        $("#navbardropMembro").addClass("disabled");
        $("#navbardropPR").addClass("disabled");
        $("#navbardropCassiere").addClass("disabled");
        $("#navbardropAmministratore").addClass("disabled");
    }
    
}