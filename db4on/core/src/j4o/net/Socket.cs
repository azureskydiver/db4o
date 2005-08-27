
using System;
using System.Net;
using j4o.io;
using NativeSocket=System.Net.Sockets.Socket;
using System.Net.Sockets;

namespace j4o.net
{
	public class Socket : SocketWrapper
	{	
		InputStream _in;
		OutputStream _out;

		public Socket(string hostName, int port)
		{
			NativeSocket socket = new NativeSocket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
			socket.Connect(new IPEndPoint(Dns.Resolve(hostName).AddressList[0], port));
			Initialize(socket);
		}

		public Socket(NativeSocket socket)
		{
			Initialize(socket);
		}

		public InputStream getInputStream()
		{
			return _in;
		}

		public OutputStream getOutputStream()
		{
			return _out;
		}

		override protected void Initialize(NativeSocket socket)
		{
			base.Initialize(socket);
			NetworkStream stream = new NetworkStream(_delegate);
			_in = new InputStream(stream);
			_out = new OutputStream(stream);
		}
	}
}
