// Copyright 2016 Yahoo Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.

package com.yahoo.parsec.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.ws.rs.core.Response;
import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * A util maps the response returned by {@link ParsecAsyncHttpRequest} to a object T.
 *
 * @param <T> T
 * @author yamlin
 */
public class ParsecResponseFuture<T> implements Future {
    /**
     * Future.
     */
    private final Future<Response> future;

    /**
     * The expected class type we will get from the response body.
     */
    private final Class<T> tClass;

    /**
     * Object Mapper.
     */
    private final ObjectMapper objectMapper;

    /**
     * The expected status codes returned by response.
     */
    private final Set<Integer> expectedStatusCodes;

    /**
     * Constuructor. The object mapper is provided.
     * @param future future.
     * @param tClass class type.
     * @param expectedStatusCodes expected status codes.
     * @param objectMapper object mapper.
     */
    public ParsecResponseFuture(Future<Response> future, Class<T> tClass,
                                Set<Integer> expectedStatusCodes, ObjectMapper objectMapper) {
        this.future = future;
        this.tClass = tClass;
        this.objectMapper = objectMapper;
        this.expectedStatusCodes = expectedStatusCodes;
    }

    /**
     * Constructor.
     * @param future future.
     * @param tClass class type.
     * @param expectedStatusCodes expected status codes.
     */
    public ParsecResponseFuture(Future<Response> future, Class<T> tClass,
                                Set<Integer> expectedStatusCodes) {
        this(future, tClass, expectedStatusCodes, new ObjectMapper());
    }

    /**
     * Get the response.
     * @return Response response
     * @throws ExecutionException Execution exception
     * @throws InterruptedException Interrupted exception
     */
    public Response getResponse() throws ExecutionException, InterruptedException {
        return future.get();
    }

    /**
     * Cancel.
     *
     * @param mayInterruptIfRunning mayInterruptIfRunning
     * @return cancel was success or not
     */
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
    }

    /**
     * Is cancelled.
     *
     * @return boolean
     */
    @Override
    public boolean isCancelled() {
        return future.isCancelled();
    }

    /**
     * Is done.
     *
     * @return boolean
     */
    @Override
    public boolean isDone() {
        return future.isDone();
    }

    /**
     * Get the response object.
     * @return T
     * @throws ExecutionException Execution exception.
     * @throws InterruptedException Interrupted exception.
     * @throws HTTPException Http exception.
     */
    @Override
    public T get() throws ExecutionException, InterruptedException, HTTPException {
        Response response = getResponse();
        int statusCode = response.getStatus();

        if (expectedStatusCodes != null && expectedStatusCodes.contains(statusCode)) {
            try {
                if (response.hasEntity()) {
                    String body = response.getEntity().toString();
                    return objectMapper.readValue(body, tClass);
                }
                return null;
            } catch (IOException e) {
                throw new RuntimeException("Invalid Response Body");
            }
        } else {
            throw new HTTPException(statusCode);
        }
    }

    /**
     * Get the response object with timeout.
     * @param timeout timeout.
     * @param unit time unit.
     * @return T
     * @throws ExecutionException Execution exception.
     * @throws InterruptedException Interrupted exception.
     * @throws TimeoutException Timeout exception.
     * @throws HTTPException Http exception.
     */
    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException,
            TimeoutException, HTTPException {
        // make sure that we can get the response before timeout
        future.get(timeout, unit);
        return get();
    }
}
