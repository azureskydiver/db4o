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
using System.Net.Sockets;

namespace com.db4o {

    internal class YapSocket : TcpClient {

        private String hostName;
		
        private int port;
      
        protected int timeout = 300000;
		
        internal YapSocket() : base() {
        }
      
        public YapSocket(String hostName, int port) : base(hostName, port) {
            this.hostName = hostName;
            this.port = port;
        }

        public YapSocket(Socket socket) : base(){
            Dynamic.SetProperty(this, "Client", socket);
        }
      
        public virtual void close() {  
            Close();
        }
      
        public virtual void flush() {
            GetStream().Flush();
        }
      
        public virtual String getHostName() {
            return hostName;
        }
      
        public virtual int getPort() {
            return port;
        }
      
        public virtual int read() {
            return GetStream().ReadByte();
        }
      
        public virtual int read(byte[] bytes, int offset, int length) {
            return GetStream().Read(bytes, offset, length);
        }
      
        public virtual void setSoTimeout(int timeout) {
            this.timeout = timeout;
            Dynamic.SetProperty(this, "SendTimeout", timeout);
            Dynamic.SetProperty(this, "ReceiveTimeout", timeout);
        }
      
        public virtual void write(byte[] bytes) {
            write(bytes,0, bytes.Length);
        }

        public virtual void write(byte[] bytes,int offset,int length) {
            GetStream().Write(bytes,offset,length);
        }
      
        public virtual void write(int i) {
            GetStream().WriteByte((byte)i);
        }
    }
}