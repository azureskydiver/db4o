/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Net;
using System.Net.Sockets;

namespace com.db4o {

    internal class YapServerSocket : TcpListener {

        public YapServerSocket(int port) : base(port) {
            Start();
        }
      
        public YapSocket accept() {
            return new YapSocket(AcceptSocket());
        }
      
        public void close() {
            this.Stop();
        }

        public String getHostName() {
            return ((IPEndPoint)LocalEndpoint).Address.ToString();
        }
      
        public int getLocalPort() {
            return ((IPEndPoint)LocalEndpoint).Port;
        }
      
        public void setSoTimeout(int timeout) {
            Socket socket = Dynamic.GetProperty(this, "Server") as Socket;
            if(socket != null){
                socket.SetSocketOption(SocketOptionLevel.Socket, SocketOptionName.SendTimeout, timeout);
                socket.SetSocketOption(SocketOptionLevel.Socket, SocketOptionName.ReceiveTimeout, timeout);
            }
        }
      
    }
}