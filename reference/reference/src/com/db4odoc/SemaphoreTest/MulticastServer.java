package com.db4odoc.SemaphoreTest;
public class MulticastServer {
    public static void main(String[] args) throws java.io.IOException {
        new MulticastServerThread().start();
        MulticastClient.main(new String[] {""});
    }
}