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

import android.webkit.CookieManager;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyCookieStore implements CookieStore {

    private Map<URI, List<HttpCookie>> map;

    public MyCookieStore() {
        this.map = new HashMap<>();
    }

    @Override
    public void add(URI uri, HttpCookie httpCookie) {
        CookieManager instance = CookieManager.getInstance();
        instance.setCookie(httpCookie.getName(), httpCookie.getValue());

        List<HttpCookie> list = map.get(uri);

        if(list == null){
            list = new ArrayList<>();
            map.put(uri, list);
        }

        list.remove(httpCookie);
        list.add(httpCookie);
    }

    @Override
    public List<HttpCookie> get(URI uri) {
        List<HttpCookie> httpCookies = map.get(uri);

        if(httpCookies == null){
            httpCookies = new ArrayList<>();
        }

        return httpCookies;
    }

    @Override
    public List<HttpCookie> getCookies() {
        List<HttpCookie> all = new ArrayList<>();

        for(List<HttpCookie> list : map.values()){
            all.addAll(list);
        }

        return all;
    }

    @Override
    public List<URI> getURIs() {
        return new ArrayList<>(map.keySet());
    }

    @Override
    public boolean remove(URI uri, HttpCookie httpCookie) {
        List<HttpCookie> httpCookies = map.get(uri);

        if(httpCookies != null){
            return httpCookies.remove(httpCookie);
        }

        return false;
    }

    @Override
    public boolean removeAll() {
        map.clear();
        return true;
    }
}
