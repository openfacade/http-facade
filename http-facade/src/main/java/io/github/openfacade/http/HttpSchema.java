package io.github.openfacade.http;

public enum HttpSchema {
    HTTP("http"),
    HTTPS("https");

    private final String name;

    HttpSchema(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
