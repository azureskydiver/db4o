/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.io.*;
import java.net.*;

class YapServerSocket extends YapSocket {

    private ServerSocket i_serverSocket;

    public YapServerSocket(int port) throws IOException {
        i_serverSocket = new ServerSocket(port);
    }

    public void setSoTimeout(int timeout) {
        try {
            i_serverSocket.setSoTimeout(timeout);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public int getLocalPort() {
        return i_serverSocket.getLocalPort();
    }

    public YapSocket accept() throws IOException {
        Socket sock = i_serverSocket.accept();
//        
//        SocketAddress sa = sock.getRemoteSocketAddress();
//        if(sa instanceof InetSocketAddress){
//            
//        }
        
        // TODO: check connection permissions here
        
        
        return new YapSocket(sock);
    }
    
	public String getHostName(){
		return i_serverSocket.getInetAddress().getHostName();
	}
	
	public void close() throws IOException {
		i_serverSocket.close();
	}

}
