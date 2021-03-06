package com.github.frapontillo.pulse.crowd.sentiment.sentit;

import com.github.frapontillo.pulse.crowd.data.entity.Message;
import com.github.frapontillo.pulse.crowd.sentiment.sentit.rest.*;
import com.github.frapontillo.pulse.rx.RxUtil;
import com.github.frapontillo.pulse.spi.IPlugin;
import com.github.frapontillo.pulse.spi.VoidConfig;
import com.github.frapontillo.pulse.util.PulseLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.Logger;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.GsonConverter;
import rx.Observable;
import rx.Subscriber;
import rx.observers.SafeSubscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Francesco Pontillo
 */
public class SentitSentimentAnalyzer extends IPlugin<Message, Message, SentitSentimentAnalyzerConfig> {
    public final static String PLUGIN_NAME = "sentiment-sentit";
    private final static String SENTIT_ENDPOINT = "http://193.204.187.210:9009/sentipolc/v1";
    private final static int MAX_MESSAGES_PER_REQ = 10;
    private final static Logger logger = PulseLogger.getLogger(SentitSentimentAnalyzer.class);

    private SentitService service;

    @Override public String getName() {
        return PLUGIN_NAME;
    }

    @Override public SentitSentimentAnalyzerConfig getNewParameter() {
        return new SentitSentimentAnalyzerConfig();
    }

    /**
     * This plugin doesn't give any {@link rx.Observable.Operator} as output, as it will only
     * expose a custom {@link rx.Observable.Transformer} that has to be applied to a stream of
     * {@link Message}s.
     *
     * @return Always {@code null}.
     */
    @Override public Observable.Operator<Message, Message> getOperator(SentitSentimentAnalyzerConfig parameters) {
        return null;
    }

    @Override public Observable.Transformer<Message, Message> transform(SentitSentimentAnalyzerConfig params) {
        return messages -> messages.buffer(MAX_MESSAGES_PER_REQ)
                .delay(500, TimeUnit.MILLISECONDS)
                .lift(new SentitOperator(params))
                // flatten the sequence of Observables back into one single Observable
                .compose(RxUtil.flatten());
    }


    private class SentitOperator implements Observable.Operator<List<Message>, List<Message>> {

        private SentitSentimentAnalyzerConfig params;

        SentitOperator(SentitSentimentAnalyzerConfig params) {
            this.params = params;
        }

        @Override public Subscriber<? super List<Message>> call(
                Subscriber<? super List<Message>> subscriber) {
            return new SafeSubscriber<>(new Subscriber<List<Message>>() {
                @Override public void onCompleted() {
                    reportPluginAsCompleted();
                    subscriber.onCompleted();
                }

                @Override public void onError(Throwable e) {
                    reportPluginAsErrored();
                    subscriber.onError(e);
                }

                @Override public void onNext(List<Message> messages) {
                    messages.forEach(m -> reportElementAsStarted(m.getId()));
                    List<Message> toProcessMessages = new ArrayList<>();

                    if (params != null && params.getCalculate() != null
                            && params.getCalculate().equals(SentitSentimentAnalyzerConfig.NEW)) {

                        for (Message message: messages) {

                            // if the sentiment score has not calculated yet
                            if (message.getSentiment() == null) {
                                toProcessMessages.add(message);
                            } else {
                                logger.info("Message skipped (sentiment score calculated)");
                            }
                        }

                    }  else {
                        toProcessMessages = messages;
                    }

                    if (toProcessMessages.size() > 0) {

                        // make the request
                        SentitRequest request = new SentitRequest(toProcessMessages);
                        SentitResponse response;
                        long remainingAttempts = 3;
                        do {
                            try {
                                response = getService().classify(request);
                                // for each message, set the result
                                for (Message message : toProcessMessages) {
                                    message.setSentiment(response.getSentimentForMessage(message));
                                }
                                remainingAttempts = 0;
                            } catch (RetrofitError error) {
                                remainingAttempts -= 1;
                                if (error.getResponse() != null &&
                                        error.getResponse().getStatus() == 401) {
                                    logger.error("Got error 401", error);
                                }
                                error.printStackTrace();
                            }
                        } while (remainingAttempts > 0);
                    }
                    messages.forEach(m -> reportElementAsEnded(m.getId()));
                    subscriber.onNext(messages);
                }
            });
        }
    }

    private SentitService getService() {
        if (service == null) {
            // build the Gson deserializers collection
            Gson gson = new GsonBuilder().registerTypeAdapter(SentitResponse.SentitResultMap.class,
                    new SentitResultMapDeserializer()).create();
            // build the REST client
            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(SENTIT_ENDPOINT)
                    .setConverter(new GsonConverter(gson))
                    .setRequestInterceptor(new SentitInterceptor()).build();
            service = restAdapter.create(SentitService.class);
        }
        return service;
    }
}
