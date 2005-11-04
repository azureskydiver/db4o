namespace com.db4o.foundation.network
{
	/// <summary>Fakes a socket connection for an embedded client.</summary>
	/// <remarks>Fakes a socket connection for an embedded client.</remarks>
	public class YapSocketFake : com.db4o.foundation.network.YapSocket
	{
		private readonly com.db4o.foundation.network.YapSocketFakeServer _server;

		private com.db4o.foundation.network.YapSocketFake _affiliate;

		private com.db4o.foundation.network.ByteBuffer4 _uploadBuffer;

		private com.db4o.foundation.network.ByteBuffer4 _downloadBuffer;

		public YapSocketFake(com.db4o.foundation.network.YapSocketFakeServer a_server, int
			 timeout)
		{
			_server = a_server;
			_uploadBuffer = new com.db4o.foundation.network.ByteBuffer4(timeout);
			_downloadBuffer = new com.db4o.foundation.network.ByteBuffer4(timeout);
		}

		public YapSocketFake(com.db4o.foundation.network.YapSocketFakeServer a_server, int
			 timeout, com.db4o.foundation.network.YapSocketFake affiliate) : this(a_server, 
			timeout)
		{
			_affiliate = affiliate;
			affiliate._affiliate = this;
			_downloadBuffer = affiliate._uploadBuffer;
			_uploadBuffer = affiliate._downloadBuffer;
		}

		public virtual void close()
		{
			if (_affiliate != null)
			{
				com.db4o.foundation.network.YapSocketFake temp = _affiliate;
				_affiliate = null;
				temp.close();
			}
			_affiliate = null;
		}

		public virtual void flush()
		{
		}

		public virtual bool isClosed()
		{
			return _affiliate == null;
		}

		public virtual int read()
		{
			return _downloadBuffer.read();
		}

		public virtual int read(byte[] a_bytes, int a_offset, int a_length)
		{
			return _downloadBuffer.read(a_bytes, a_offset, a_length);
		}

		public virtual void setSoTimeout(int a_timeout)
		{
			_uploadBuffer.setTimeout(a_timeout);
			_downloadBuffer.setTimeout(a_timeout);
		}

		public virtual void write(byte[] bytes)
		{
			_uploadBuffer.write(bytes);
		}

		public virtual void write(byte[] bytes, int off, int len)
		{
			_uploadBuffer.write(bytes, off, len);
		}

		public virtual void write(int i)
		{
			_uploadBuffer.write(i);
		}

		public virtual com.db4o.foundation.network.YapSocket openParalellSocket()
		{
			return _server.openClientSocket();
		}
	}
}
