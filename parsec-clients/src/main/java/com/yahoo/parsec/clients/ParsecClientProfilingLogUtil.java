// Copyright 2016 Yahoo Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.

package com.yahoo.parsec.clients;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ning.http.client.Request;
import com.ning.http.client.Response;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;


/**
 * @author hankting, waynewu
 */
public final class ParsecClientProfilingLogUtil {

    /**
     * profiling logger.
     */
    private static final Logger PROF_LOGGER = LoggerFactory.getLogger("parsec.clients.profiling_log");

    /**
     * Object mapper.
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
        .configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    /**
     * Unused private constructor.
     */
    private ParsecClientProfilingLogUtil() {
        // no use
    }

    /**
     * log remote profiling log.
     *
     * @param request ning http request
     * @param response ning http response
     * @param requestStatus request status
     * @param progress parsec async progress do
     * @param msgMap additional log msg in key=String, value=String format
     */
    public static void logRemoteRequest(
            final Request request,
            final Response response,
            final String requestStatus,
            final ParsecAsyncProgress progress,
            final Map<String, String> msgMap
            ) {
        if (!PROF_LOGGER.isTraceEnabled()) {
            return;
        }

        //
        // prepare log data
        //
        long now = System.currentTimeMillis();
        BigDecimal timeInSecond = new BigDecimal(now).divide(BigDecimal.valueOf(DateUtils.MILLIS_PER_SECOND));
        String contentLength = "";
        String origin = "";
        int respCode = -1;

        String reqUrl = request.getUri().toUrl();
        String reqMethod = request.getMethod();
        String reqHostHeader = request.getHeaders().getFirstValue(ParsecClientDefine.HEADER_HOST);

        if (response != null) {
            contentLength = response.getHeader(ParsecClientDefine.HEADER_CONTENT_LENGTH);
            respCode = response.getStatusCode();
        }

        try {
            String executeInfo = OBJECT_MAPPER.writeValueAsString(progress);

            //
            // FIXME: should implement a servlet filter to set $_SERVER['REQUEST_URI']
            //
            String srcUrl = "";

            StringBuilder stringBuilder = new StringBuilder()
                .append("time=").append(timeInSecond).append(", ")
                .append("req_url=").append(reqUrl).append(", ")
                .append("req_host_header=").append(reqHostHeader).append(", ")
                .append("req_method=").append(reqMethod).append(", ")
                .append("exec_info=").append(executeInfo).append(", ")
                .append("resp_code=").append(respCode).append(", ")
                .append("src_url=").append(srcUrl).append(", ")
                .append("req_status=").append(requestStatus).append(", ")
                .append("content_length=").append(contentLength).append(", ")
                .append("origin=").append(origin).append(", ");

            if(msgMap != null){
                for (Map.Entry<String, String> entry : msgMap.entrySet()){
                    stringBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append(", ");
                }
            }

            String logMsg = stringBuilder.toString();

            //logging
            PROF_LOGGER.trace(logMsg);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *log remote profiling log.
     *
     * @param request ning http request
     * @param response ning http response
     * @param requestStatus request status
     * @param progress parsec async progress do
     */
    public static void logRemoteRequest(
            final Request request,
            final Response response,
            final String requestStatus,
            final ParsecAsyncProgress progress
        ){
        logRemoteRequest(request, response, requestStatus, progress, null);
    }
}
