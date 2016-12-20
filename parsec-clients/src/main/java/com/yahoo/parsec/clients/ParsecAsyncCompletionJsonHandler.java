// Copyright 2016 Yahoo Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.parsec.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.Response;

import javax.xml.ws.http.HTTPException;
import java.util.Set;

/**
 * Async Json Handler that returns T.
 *
 * @author yamlin
 */
public class ParsecAsyncCompletionJsonHandler<T> extends AsyncCompletionHandler {
    private final ObjectMapper objectMapper;
    private final Class<T> tClass;
    private final Set<Integer> expectedStatusCodes;

    public ParsecAsyncCompletionJsonHandler(Class<T> tClass, Set<Integer> expectedStatusCodes, ObjectMapper o) {
        this.objectMapper = o;
        this.tClass = tClass;
        this.expectedStatusCodes = expectedStatusCodes;
    }

    public ParsecAsyncCompletionJsonHandler(Class<T> tClass, Set<Integer> expectedStatusCodes) {
        this(tClass, expectedStatusCodes, new ObjectMapper());
    }

    @Override
    public T onCompleted(Response response) throws Exception {
        if (expectedStatusCodes != null && expectedStatusCodes.contains(response.getStatusCode())) {
            if (response.hasResponseBody()) {
                return objectMapper.readValue(response.getResponseBody(), tClass);
            }
            return null;
        } else {
            throw new HTTPException(response.getStatusCode());
        }
    }
}
