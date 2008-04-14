package com.db4odoc.SemaphoreTest;
import java.io.*;

/**
 * @sharpen.ignore 
 */
public class QuoteServer {
    public static void main(String[] args) throws IOException {
    	
        new QuoteServerThread().start();
        QuoteClient.main(new String[]{"s","s"});
    }
}