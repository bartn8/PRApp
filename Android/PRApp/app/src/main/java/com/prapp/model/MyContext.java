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

package com.prapp.model;

import com.prapp.model.db.wrapper.WEvento;
import com.prapp.model.db.wrapper.WRuoliMembro;
import com.prapp.model.db.wrapper.WStaff;
import com.prapp.model.db.wrapper.WUtente;
import com.prapp.model.util.RandomString;

import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.TreeSet;

public class MyContext {

    private static final String LOCAL_HOST = "192.168.1.51";//10.0.0.2
    private static final String LOCAL_PROXY_HOST = "192.168.1.51";//10.0.0.2
    private static final String ALTERVISTA_HOST = "prapp.altervista.org";
    private static final String DEFAULT_HOST = ALTERVISTA_HOST;

    private static final String LOCAL_ADDRESS = "http://192.168.1.51";//10.0.0.2
    private static final String LOCAL_PROXY_ADDRESS = "http://192.168.1.51:8080";//10.0.0.2
    private static final String ALTERVISTA_ADDRESS = "https://prapp.altervista.org";
    private static final String DEFAULT_ADDRESS = ALTERVISTA_ADDRESS;

    private static final String LOCAL_FRAMEWORK_ADDRESS = "http://192.168.1.51/framework/ajax.php";//10.0.0.2
    private static final String LOCAL_PROXY_FRAMEWORK_ADDRESS = "http://192.168.1.51:8080/framework/ajax.php";//10.0.0.2
    private static final String ALTERVISTA_FRAMEWORK_ADDRESS = "https://prapp.altervista.org/framework/ajax.php";
    private static final String DEFAULT_FRAMEWORK_ADDRESS = ALTERVISTA_FRAMEWORK_ADDRESS;

    private static final String LOCAL_WEBAPP_ADDRESS = "http://192.168.1.51/webapppr/index.html";
    private static final String LOCAL_PROXY_WEBAPP_ADDRESS = "http://192.168.1.51:8080/webapppr/index.html";
    private static final String ALTERVISTA_WEBAPP_ADDRESS = "https://prapp.altervista.org/webapppr/login.html";
    private static final String DEFAULT_WEBAPP_ADDRESS = ALTERVISTA_WEBAPP_ADDRESS;

    private static final String LOCAL_WEBAPP_LOGIN_ADDRESS = "http://192.168.1.51/webapppr/login.html";
    private static final String LOCAL_PROXY_WEBAPP_LOGIN_ADDRESS = "http://192.168.1.51:8080/webapppr/login.html";
    private static final String ALTERVISTA_WEBAPP_LOGIN_ADDRESS = "https://prapp.altervista.org/webapppr/login.html";
    private static final String DEFAULT_WEBAPP_LOGIN_ADDRESS = ALTERVISTA_WEBAPP_LOGIN_ADDRESS;

    private static final String LOCAL_WEBAPP_LOGOUT_ADDRESS = "http://192.168.1.51/webapppr/logout.html";
    private static final String LOCAL_PROXY_WEBAPP_LOGOUT_ADDRESS = "http://192.168.1.51:8080/webapppr/logout.html";
    private static final String ALTERVISTA_WEBAPP_LOGOUT_ADDRESS = "https://prapp.altervista.org/webapppr/logout.html";
    private static final String DEFAULT_WEBAPP_LOGOUT_ADDRESS = ALTERVISTA_WEBAPP_LOGOUT_ADDRESS;

    public static final String WEBAPP_NAME = "webapppr";

    public static final int PREVENDITA_CODE_LENGTH = 3;

    private static MyContext context = null;

    public synchronized static MyContext getInstance()
    {
        if(context == null) {
            try {
                context = new MyContext();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        return context;
    }

    private URL indirizzo;

    private WUtente utente;
    private boolean loggato;

    private WStaff staff;
    private boolean staffScelto;

    private WEvento evento;
    private boolean eventoScelto;

    private WRuoliMembro ruoliMembro;

    private RandomString random = new RandomString();

    private MyContext() throws MalformedURLException {
        this.loggato = false;
        this.staffScelto = false;
        this.indirizzo = new URL(DEFAULT_FRAMEWORK_ADDRESS);
    }

    public Random getRandom() {
        return random.getRandom();
    }

    public String generatePrevenditaCode(){
        return random.nextString().substring(0, PREVENDITA_CODE_LENGTH);
    }

    public void login(@NotNull WUtente utente)
    {
        this.utente = utente;
        this.loggato = true;
    }

    public void clearUtente()
    {
        this.loggato = false;
        this.utente = null;
    }

    public void logout()
    {
        clearUtente();
        clearStaff();
        clearEvento();
    }

    public void clearSelected(){
        clearStaff();
        clearEvento();
    }

    public WRuoliMembro getRuoliMembro() {
        return ruoliMembro;
    }

    public void setRuoliMembro(WRuoliMembro ruoliMembro) {
        this.ruoliMembro = ruoliMembro;
    }

    public WStaff getStaff() {
        return staff;
    }

    public void setNoRuoliMembro()
    {
        this.ruoliMembro = new WRuoliMembro(utente.getId(), staff.getId(), new TreeSet<>());
    }

    public synchronized void setStaff(WStaff staff) {
        this.staff = staff;
        this.staffScelto = true;
    }

    public void clearStaff()
    {
        this.staffScelto = false;
        this.staff = null;
    }

    public WEvento getEvento() {
        return evento;
    }

    public void setEvento(WEvento evento) {
        this.evento = evento;
        this.eventoScelto = true;
    }

    public void clearEvento()
    {
        this.eventoScelto = false;
        this.evento = null;
    }

    public boolean isEventoScelto(){
        return eventoScelto;
    }

    public boolean isStaffScelto(){
        return staffScelto;
    }

    public boolean isLoggato() {
        return loggato;
    }

    public WUtente getUtente() {
        return utente;
    }

    public URL getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(URL indirizzo) {
        this.indirizzo = indirizzo;
    }
}
