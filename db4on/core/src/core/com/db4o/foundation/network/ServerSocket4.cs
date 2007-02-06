namespace com.db4o.foundation.network
{
	public class ServerSocket4
	{
		private j4o.net.ServerSocket _serverSocket;

		public ServerSocket4(int port)
		{
			_serverSocket = new j4o.net.ServerSocket(port);
		}

		public virtual void SetSoTimeout(int timeout)
		{
			try
			{
				_serverSocket.SetSoTimeout(timeout);
			}
			catch (System.Net.Sockets.SocketException e)
			{
				j4o.lang.JavaSystem.PrintStackTrace(e);
			}
		}

		public virtual int GetLocalPort()
		{
			return _serverSocket.GetLocalPort();
		}

		public virtual com.db4o.foundation.network.Socket4 Accept()
		{
			j4o.net.Socket sock = _serverSocket.Accept();
			return new com.db4o.foundation.network.NetworkSocket(sock);
		}

		public virtual void Close()
		{
			_serverSocket.Close();
		}
	}
}
