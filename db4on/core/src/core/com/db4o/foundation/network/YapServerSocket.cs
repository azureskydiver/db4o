
namespace com.db4o.foundation.network
{
	public class YapServerSocket
	{
		private j4o.net.ServerSocket _serverSocket;

		public YapServerSocket(int port)
		{
			_serverSocket = new j4o.net.ServerSocket(port);
		}

		public virtual void setSoTimeout(int timeout)
		{
			try
			{
				_serverSocket.setSoTimeout(timeout);
			}
			catch (System.Net.Sockets.SocketException e)
			{
				j4o.lang.JavaSystem.printStackTrace(e);
			}
		}

		public virtual int getLocalPort()
		{
			return _serverSocket.getLocalPort();
		}

		public virtual com.db4o.foundation.network.YapSocket accept()
		{
			j4o.net.Socket sock = _serverSocket.accept();
			return new com.db4o.foundation.network.YapSocketReal(sock);
		}

		public virtual void close()
		{
			_serverSocket.close();
		}
	}
}
