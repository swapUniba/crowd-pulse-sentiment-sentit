crowd-pulse-sentiment-sentit
============================

SentIt based Crowd Pulse message sentiment analysis plugin.

----------------------------

The `sentiment-sentit` plugin needs a `sentit.properties` file in the class loader accessible 
resources directory, with the `sentit.key` value holding the SentIt API key.

You can specify the configuration option "calculate" with one of the following values:
- all: to tokenize all messages coming from the stream;
- new: to tokenize the sentiment of the messages with no tokens (property is null or array is empty);

Example of usage:

```json
{
  "process": {
    "name": "sentit-tester",
    "logs": "/opt/crowd-pulse/logs"
  },
  "nodes": {
    "fetch": {
      "plugin": "message-fetch",
      "config": {
        "db": "test-sentit"
      }
    },
    "sentiment": {
      "plugin": "sentiment-sentit",
      "config": {
        "calculate": "new"
      }
    },
    "persistance": {
      "plugin": "message-persist",
      "config": {
        "db": "test-sentit"
      }
    }
  },
  "edges": {
    "fetch": [
      "sentiment"
    ],
    "sentiment": [
      "persistance"
    ]
  }
}
```