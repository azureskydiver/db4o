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
			_out = _socket.getOutputStream();
			_in = _socket.getInputStream();
		}

		public virtual void close()
		{
			_socket.close();
		}

		public virtual void flush()
		{
			_out.flush();
		}

		public virtual int read()
		{
			return _in.read();
		}

		public virtual int read(byte[] a_bytes, int a_offset, int a_length)
		{
			return _in.read(a_bytes, a_offset, a_length);
		}

		public virtual void setSoTimeout(int timeout)
		{
			try
			{
				_socket.setSoTimeout(timeout);
			}
			catch (System.Net.Sockets.SocketException e)
			{
				j4o.lang.JavaSystem.printStackTrace(e);
			}
		}

		public virtual void write(byte[] bytes)
		{
			_out.write(bytes);
		}

		public virtual void write(byte[] bytes, int off, int len)
		{
			_out.write(bytes, off, len);
		}

		public virtual void write(int i)
		{
			_out.write(i);
		}

		public virtual com.db4o.foundation.network.YapSocket openParalellSocket()
		{
			if (_hostName == null)
			{
				throw new System.IO.IOException("Cannot open parallel socket - invalid state.");
			}
			return new com.db4o.foundation.network.YapSocketReal(_hostName, _socket.getPort()
				);
		}
	}
}
