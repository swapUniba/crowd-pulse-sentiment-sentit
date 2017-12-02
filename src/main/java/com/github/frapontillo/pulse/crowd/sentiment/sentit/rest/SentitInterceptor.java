package com.github.frapontillo.pulse.crowd.sentiment.sentit.rest;

import com.github.frapontillo.pulse.util.PulseLogger;
import retrofit.RequestInterceptor;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author Francesco Pontillo
 */
public class SentitInterceptor implements RequestInterceptor {
    private static final String PROP_API_KEY = "sentit.key";
    private static String API_KEY;

    static {
        InputStream configInput =
                RequestInterceptor.class.getClassLoader().getResourceAsStream("sentit.properties");
        Properties prop = new Properties();

        try {
            prop.load(configInput);
            API_KEY = prop.getProperty(PROP_API_KEY);
        } catch (Exception exception) {
            PulseLogger.getLogger(SentitInterceptor.class)
                    .error("Error while loading Sentit configuration", exception);
            API_KEY = "";
        }
    }

    @Override public void intercept(RequestFacade request) {
        request.addHeader("applicationkey", API_KEY);
    }
}
