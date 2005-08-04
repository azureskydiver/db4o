namespace com.db4o.foundation.network
{
	public class YapClientSocket : com.db4o.foundation.network.YapSocketReal
	{
		protected string _hostName;

		protected int _port;

		public YapClientSocket(string hostName, int port) : base(new j4o.net.Socket(hostName
			, port))
		{
			_hostName = hostName;
			_port = port;
		}

		public override com.db4o.foundation.network.YapSocket openParalellSocket()
		{
			return new com.db4o.foundation.network.YapClientSocket(_hostName, _port);
		}
	}
}
