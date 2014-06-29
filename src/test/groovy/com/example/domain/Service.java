package com.example.domain;

public class Service {
    Service delegate;
    Object valueFromDifferentClassloader;

    public void useValue(Value value) {
    }

    public Value createValue() {
        return new Value();
    }

    public <T extends Value> T castValue() {
        return (T) valueFromDifferentClassloader;
    }
}
