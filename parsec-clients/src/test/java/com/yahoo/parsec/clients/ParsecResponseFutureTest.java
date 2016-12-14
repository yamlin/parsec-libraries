// Copyright 2016 Yahoo Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.

package com.yahoo.parsec.clients;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import javax.xml.ws.http.HTTPException;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;
import static org.testng.Assert.assertEquals;


public class ParsecResponseFutureTest {
    private Future<Response> mockFuture;
    private Response mockResponse;
    private Set<Integer> expectedStatusCodes;
    private ObjectMapper mockObjectMapper;

    @BeforeMethod
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        mockFuture = mock(Future.class);
        when(mockFuture.isCancelled()).thenReturn(false);
        when(mockFuture.isDone()).thenReturn(false);

        mockResponse = mock(Response.class);
        mockObjectMapper = mock(ObjectMapper.class);

        expectedStatusCodes = new HashSet<>();
        expectedStatusCodes.add(200);
    }

    @Test
    public void testGet() throws Exception {
        String body = "OK";
        // Test default value
        ParsecResponseFuture<String> parsecResponseFuture =
                new ParsecResponseFuture<String>(mockFuture, String.class, expectedStatusCodes, mockObjectMapper);
        when(mockFuture.get()).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(200);
        when(mockResponse.hasEntity()).thenReturn(true);
        when(mockResponse.getEntity()).thenReturn(body);
        when(mockObjectMapper.readValue(anyString(), any(Class.class))).thenReturn("OK");

        assertEquals(parsecResponseFuture.get(), body);
    }

    @Test
    public void testGetNull() throws Exception {
        String body = null;
        // Test default value
        ParsecResponseFuture<String> parsecResponseFuture =
                new ParsecResponseFuture<>(mockFuture, String.class, expectedStatusCodes, mockObjectMapper);
        when(mockFuture.get()).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(200);
        when(mockResponse.hasEntity()).thenReturn(false);
        when(mockResponse.getEntity()).thenReturn(body);
        when(mockObjectMapper.readValue(anyString(), any(Class.class))).thenReturn(null);

        assertEquals(parsecResponseFuture.get(), body);
    }

    @Test
    public void testGetTimeout() throws Exception {
        String body = "OK";
        // Test default value
        ParsecResponseFuture<String> parsecResponseFuture =
                new ParsecResponseFuture<>(mockFuture, String.class, expectedStatusCodes, mockObjectMapper);
        when(mockFuture.get()).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(200);
        when(mockResponse.hasEntity()).thenReturn(true);
        when(mockResponse.getEntity()).thenReturn(body);
        when(mockObjectMapper.readValue(anyString(), any(Class.class))).thenReturn(body);

        assertEquals(parsecResponseFuture.get(1, TimeUnit.SECONDS), body);
    }


    @Test(expectedExceptions = HTTPException.class)
    public void testGetStatusCodeException() throws Exception {
        String body = "TEST";
        // Test default value
        ParsecResponseFuture<String> parsecResponseFuture =
                new ParsecResponseFuture<>(mockFuture, String.class, null, mockObjectMapper);
        when(mockFuture.get()).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(200);
        when(mockObjectMapper.readValue(anyString(), any(Class.class))).thenReturn(body);

        parsecResponseFuture.get();

    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testMappingException() throws Exception {
        // Test default value
        ParsecResponseFuture<String> parsecResponseFuture =
                new ParsecResponseFuture<>(mockFuture, String.class, expectedStatusCodes, mockObjectMapper);
        when(mockFuture.get()).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(200);
        when(mockResponse.hasEntity()).thenReturn(true);
        when(mockResponse.getEntity()).thenReturn("OK");
        when(mockObjectMapper.readValue(anyString(), any(Class.class))).thenThrow(new IOException());
        parsecResponseFuture.get();
        try {
            parsecResponseFuture.get();
        } catch (RuntimeException e) {
            assertEquals(e.getMessage(), "Invalid Response Body");
            throw e;
        }
    }



    @Test
    public void testIsCancel() throws Exception {
        // Test default value
        ParsecResponseFuture<String> future =
                new ParsecResponseFuture<>(mockFuture, String.class, expectedStatusCodes);

        // Test cancel
        future.cancel(true);
        assertFalse(future.isCancelled());
    }

    @Test
    public void testIsCancelled() throws Exception {
        // Test default value
        ParsecResponseFuture<String> future =
                new ParsecResponseFuture<>(mockFuture, String.class, expectedStatusCodes);
        assertFalse(future.isCancelled());

        when(mockFuture.isCancelled()).thenReturn(true);
        assertTrue(future.isCancelled());
    }

    @Test
    public void testIsDone() throws Exception {
        // Test default value
        ParsecResponseFuture<String> future =
                new ParsecResponseFuture<>(mockFuture, String.class, expectedStatusCodes);
        assertFalse(future.isDone());

        when(mockFuture.isDone()).thenReturn(true);
        assertTrue(future.isDone());
    }


}
