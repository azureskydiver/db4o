using System.Net.Sockets;
using NativeSocket=System.Net.Sockets.Socket;

namespace j4o.net
{
	public class SocketWrapper
	{
		protected NativeSocket _delegate;

		protected virtual void Initialize(NativeSocket socket)
		{
			_delegate = socket;
		}

		public void setSoTimeout(int timeout)
		{
			_delegate.SetSocketOption(SocketOptionLevel.Socket, SocketOptionName.ReceiveTimeout, timeout);
			_delegate.SetSocketOption(SocketOptionLevel.Socket, SocketOptionName.SendTimeout, timeout);
		}

		public void close()
		{
			_delegate.Close();
		}
	}
}
