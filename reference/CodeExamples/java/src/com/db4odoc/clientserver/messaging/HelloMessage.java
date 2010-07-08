package com.db4odoc.clientserver.messaging;


public class HelloMessage {
    private final String message;

    public HelloMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
