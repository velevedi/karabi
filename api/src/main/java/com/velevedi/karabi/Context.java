package com.velevedi.karabi;

import java.util.Map;

public record Context(Map<String, String> properties) {

    public static Context emptyContext = new Context(Map.of());

    public Context(Map<String, String> properties) {
        this.properties = Map.copyOf(properties);
    }

    public String property(String key) {
        return properties.get(key);
    }
}
