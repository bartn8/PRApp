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

class GeneralUiUtils {

    constructor(){

    }

    impostaLogin(){
        $("#login_logout > a").text("Login");
        $("#login_logout > a").attr("href", "login.html");
    }

    impostaLogout(){
        $("#login_logout > a").text("Logout");
        $("#login_logout > a").attr("href", "logout.html");
    }

    impostaScritta(scritta){
        $("#text").text(scritta);
        $("#text").css('color', 'black');
    }

    impostaErrore(errore){
        $("#text").html(errore + " Prova a <a href=\"reset.html\">resettare</a>");
        $("#text").css('color', 'red');
    }

    attivaMenu(){
        $("#scegliStaff").removeClass("disabled");
        $("#scegliStaff > a").attr("href", "scegliStaff.html");


        $("#scegliEvento").removeClass("disabled");
        $("#scegliEvento > a").attr("href", "scegliEvento.html");


        $("#creaPrevendita").removeClass("disabled");
        $("#creaPrevendita > a").attr("href", "creaPrevendita.html");

        $("#listaPrevendite").removeClass("disabled");
        $("#listaPrevendite > a").attr("href", "listaPrevendite.html");

    }

    disattivaMenu(){
        $("#scegliStaff").addClass("disabled");
        $("#scegliStaff > a").attr("href", "#");

        $("#scegliEvento").addClass("disabled");
        $("#scegliEvento > a").attr("href", "#");

        $("#creaPrevendita").addClass("disabled");
        $("#creaPrevendita > a").attr("href", "#");

        $("#listaPrevendite").addClass("disabled");
        $("#listaPrevendite > a").attr("href", "#");
    }

    disattivaTuttiMenu(){
        //Disattivo il menu
        $("#login_logout").addClass("disabled");
        $("#login_logout > a").attr("href", "#");
        $("#login_logout").click(function() {
            return false;
        });

        $("#scegliStaff").addClass("disabled");
        $("#scegliStaff > a").attr("href", "#");
        $("#scegliStaff").click(function() {
            return false;
        });

        $("#scegliEvento").addClass("disabled");
        $("#scegliEvento > a").attr("href", "#");
        $("#scegliEvento").click(function() {
            return false;
        });

        $("#creaPrevendita").addClass("disabled");
        $("#creaPrevendita > a").attr("href", "#");
        $("#creaPrevendita").click(function() {
            return false;
        });

        $("#listaPrevendite").addClass("disabled");
        $("#listaPrevendite > a").attr("href", "#");
        $("#listaPrevendite").click(function() {
            return false;
        });
    }
    
}