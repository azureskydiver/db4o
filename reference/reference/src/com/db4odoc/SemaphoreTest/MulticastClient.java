package com.db4odoc.SemaphoreTest;
import java.io.*;
import java.net.*;

/**
 * @sharpen.ignore 
 */
public class MulticastClient {

    public static void main(String[] args) throws IOException {

        MulticastSocket socket = new MulticastSocket(4445);
        InetAddress address = InetAddress.getByName("225.4.5.6");
	socket.joinGroup(InetAddress.getByName("225.4.5.6"));

        DatagramPacket packet;
    
            // get a few quotes
	for (int i = 0; i < 5; i++) {

	    byte[] buf = new byte[256];
            packet = new DatagramPacket(buf, buf.length);
            System.out.println("Before");
            socket.receive(packet);
            System.out.println("After");

            String received = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Quote of the Moment: " + received);
	}

	socket.leaveGroup(address);
	socket.close();
    }

}
