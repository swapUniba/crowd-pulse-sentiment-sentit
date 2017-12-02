package com.github.frapontillo.pulse.crowd.sentiment.sentit.rest;

import retrofit.http.Body;
import retrofit.http.Headers;
import retrofit.http.POST;

/**
 * @author Francesco Pontillo
 */
public interface SentitService {
    @Headers({
            "Content-Type: text/plain"
    })
    @POST("/classify") SentitResponse classify(@Body SentitRequest request);
}
