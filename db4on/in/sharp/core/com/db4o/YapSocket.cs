/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

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