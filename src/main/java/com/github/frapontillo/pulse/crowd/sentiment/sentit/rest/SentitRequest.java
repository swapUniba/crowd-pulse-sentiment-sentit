package com.github.frapontillo.pulse.crowd.sentiment.sentit.rest;

import com.github.frapontillo.pulse.crowd.data.entity.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Models a Sentit Web Service request in the following example format:
 * <p/>
 * <pre>
 * {
 *      "texts": [
 *          {
 *              "id": "id001",
 *              "text": "Grillo Mi fa paura la gente che urla. Ne abbiamo già visti almeno un paio,
 *                      ed è finita com'è finita. Niente urla per me, grazie."
 *          },
 *          {
 *              "id": "id002",
 *              "text": "@Ale__Malik oddio quanto ti capisco.<3 *--*"
 *          }]
 * }
 * </pre>
 *
 * @author Francesco Pontillo
 */
public class SentitRequest {
    private List<SentitText> texts;

    public SentitRequest() {
        texts = new ArrayList<>();
    }

    public SentitRequest(List<Message> messages) {
        texts = new ArrayList<>(messages.size());
        messages.forEach(message -> texts.add(new SentitText(message)));
    }

    public List<SentitText> getTexts() {
        return texts;
    }

    public void setTexts(List<SentitText> texts) {
        this.texts = texts;
    }

    public class SentitText {
        private String id;
        private String text;

        public SentitText(Message message) {
            id = message.getId().toString();
            text = message.getText();
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
