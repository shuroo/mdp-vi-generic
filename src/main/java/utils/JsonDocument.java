package utils;

import com.couchbase.client.java.json.JsonObject;

public class JsonDocument {
        private final String id;
        private final JsonObject content;

        public JsonDocument(String id, JsonObject content) {
            this.id = id;
            this.content = content;
        }

        public String getId() { return id;}

        public JsonObject getContent() { return content;}

        @Override
        public String toString() {
            return "JsonDocument{id='" + id + "', content=" + content + "}";
        }
    }
