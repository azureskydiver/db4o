/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA  02111-1307, USA. */

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