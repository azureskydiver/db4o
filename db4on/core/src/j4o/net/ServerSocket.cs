using System;
using System.Net;
using System.Net.Sockets;
using NativeSocket=System.Net.Sockets.Socket;

namespace j4o.net
{
	public class ServerSocket : SocketWrapper
	{
		public ServerSocket(int port)
		{
			NativeSocket socket = new NativeSocket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
			socket.Bind(new IPEndPoint(IPAddress.Any, port));

			int maxConnections = 42;
			socket.Listen(maxConnections);

			Initialize(socket);
		}

		public Socket accept()
		{
			return new Socket(_delegate.Accept());
		}

		public int getLocalPort()
		{
			return ((IPEndPoint)_delegate.LocalEndPoint).Port;
		}
	}
}
