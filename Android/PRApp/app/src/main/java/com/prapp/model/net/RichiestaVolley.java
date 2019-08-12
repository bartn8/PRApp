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

package com.prapp.model.net;

import androidx.annotation.Nullable;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;

public class RichiestaVolley extends JsonRequest<Risposta> {

    private static final String PROTOCOL_CONTENT_TYPE = "application/x-www-form-urlencoded; charset=utf-8";

    private Gson gson = new Gson();//TODO: leva
    private Richiesta request;

    public RichiestaVolley(String url, @NotNull Richiesta request, Response.Listener<Risposta> listener, @Nullable Response.ErrorListener errorListener) throws UnsupportedEncodingException {
        super(Method.POST, url, request.generatePOSTQuery(), listener,  errorListener);
        this.request = request;
    }

    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    @Override
    protected Response<Risposta> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonResponse = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));

            return Response.success(
                    gson.fromJson(jsonResponse, Risposta.class),
                    HttpHeaderParser.parseCacheHeaders(response));

        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

}
