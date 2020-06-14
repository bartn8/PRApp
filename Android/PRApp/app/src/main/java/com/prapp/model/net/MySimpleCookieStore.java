/*
 * PRApp  Copyright (C) 2020  Luca Bartolomei
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

package com.prapp.model.net;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class MySimpleCookieStore implements CookieStore {

    private ArrayList<URI> URIs;
    private ArrayList<HttpCookie> cookies;

    public MySimpleCookieStore() {
        this.URIs = new ArrayList<>();
        this.cookies = new ArrayList<>();
    }

    @Override
    public void add(URI uri, HttpCookie httpCookie) {
        URIs.add(uri);
        cookies.add(httpCookie);
    }

    @Override
    public List<HttpCookie> get(URI uri) {
        return cookies;
    }

    @Override
    public List<HttpCookie> getCookies() {
        return cookies;
    }

    @Override
    public List<URI> getURIs() {
        return URIs;
    }

    @Override
    public boolean remove(URI uri, HttpCookie httpCookie) {
        URIs.remove(uri);
        cookies.remove(httpCookie);
        return true;
    }

    @Override
    public boolean removeAll() {
        URIs.clear();
        cookies.clear();
        return true;
    }
}
