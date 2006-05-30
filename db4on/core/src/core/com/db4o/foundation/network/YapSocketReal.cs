namespace com.db4o.foundation.network
{
	public class YapSocketReal : com.db4o.foundation.network.YapSocket
	{
		private j4o.net.Socket _socket;

		private j4o.io.OutputStream _out;

		private j4o.io.InputStream _in;

		private string _hostName;

		public YapSocketReal(string hostName, int port) : this(new j4o.net.Socket(hostName
			, port))
		{
			_hostName = hostName;
		}

		public YapSocketReal(j4o.net.Socket socket)
		{
			_socket = socket;
			_out = _socket.GetOutputStream();
			_in = _socket.GetInputStream();
		}

		public virtual void Close()
		{
			_socket.Close();
		}

		public virtual void Flush()
		{
			_out.Flush();
		}

		public virtual int Read()
		{
			return _in.Read();
		}

		public virtual int Read(byte[] a_bytes, int a_offset, int a_length)
		{
			return _in.Read(a_bytes, a_offset, a_length);
		}

		public virtual void SetSoTimeout(int timeout)
		{
			try
			{
				_socket.SetSoTimeout(timeout);
			}
			catch (System.Net.Sockets.SocketException e)
			{
				j4o.lang.JavaSystem.PrintStackTrace(e);
			}
		}

		public virtual void Write(byte[] bytes)
		{
			_out.Write(bytes);
		}

		public virtual void Write(byte[] bytes, int off, int len)
		{
			_out.Write(bytes, off, len);
		}

		public virtual void Write(int i)
		{
			_out.Write(i);
		}

		public virtual com.db4o.foundation.network.YapSocket OpenParalellSocket()
		{
			if (_hostName == null)
			{
				throw new System.IO.IOException("Cannot open parallel socket - invalid state.");
			}
			return new com.db4o.foundation.network.YapSocketReal(_hostName, _socket.GetPort()
				);
		}
	}
}
