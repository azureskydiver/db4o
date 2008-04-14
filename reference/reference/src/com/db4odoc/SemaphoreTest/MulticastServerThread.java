package com.db4odoc.SemaphoreTest;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * @sharpen.ignore 
 */

public class MulticastServerThread extends QuoteServerThread {

    private long FIVE_SECONDS = 5000;
    MulticastSocket socket;

    public MulticastServerThread() throws IOException {
    	socket = new MulticastSocket();
    }

    public void run() {
        while (moreQuotes) {
            try {
                byte[] buf = new byte[256];

                    // construct quote
                String dString = null;
                if (in == null)
                    dString = new Date().toString();
                else
                    dString = getNextQuote();
                System.out.println(dString);
                buf = dString.getBytes();

		    // send it
                InetAddress group = InetAddress.getByName("225.4.5.6");
                DatagramPacket packet = new DatagramPacket(buf, buf.length,
   					 InetAddress.getByName("225.4.5.6"), 4445);
                //DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 4445);
                socket.send(packet, (byte)1);

		     // sleep for a while
		try {
		    sleep((long)(Math.random() * FIVE_SECONDS));
		} catch (InterruptedException e) { }
            } catch (IOException e) {
                e.printStackTrace();
		moreQuotes = false;
            }
        }
	socket.close();
    }
}
