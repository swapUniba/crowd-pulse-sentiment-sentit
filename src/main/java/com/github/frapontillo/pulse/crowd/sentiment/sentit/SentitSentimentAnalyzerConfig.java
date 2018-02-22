package com.github.frapontillo.pulse.crowd.sentiment.sentit;

import com.github.frapontillo.pulse.spi.IPluginConfig;
import com.github.frapontillo.pulse.spi.PluginConfigHelper;
import com.google.gson.JsonElement;

/**
 * Plugin configuration class.
 */
public class SentitSentimentAnalyzerConfig implements IPluginConfig<SentitSentimentAnalyzerConfig> {

    /**
     * Calculate the sentiment of all messages coming from the stream.
     */
    public static final String ALL = "all";

    /**
     * Calculate the sentiment of the messages with no sentiment (property is null).
     */
    public static final String NEW = "new";


    /**
     * Accepted values: NEW, ALL
     */
    private String calculate;

    @Override
    public SentitSentimentAnalyzerConfig buildFromJsonElement(JsonElement jsonElement) {
        return PluginConfigHelper.buildFromJson(jsonElement, SentitSentimentAnalyzerConfig.class);
    }

    public String getCalculate() {
        return calculate;
    }

    public void setCalculate(String calculate) {
        this.calculate = calculate;
    }
}
