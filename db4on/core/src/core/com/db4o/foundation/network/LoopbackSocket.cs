namespace com.db4o.foundation.network
{
	/// <summary>Fakes a socket connection for an embedded client.</summary>
	/// <remarks>Fakes a socket connection for an embedded client.</remarks>
	public class LoopbackSocket : com.db4o.foundation.network.Socket4
	{
		private readonly com.db4o.foundation.network.LoopbackSocketServer _server;

		private com.db4o.foundation.network.LoopbackSocket _affiliate;

		private com.db4o.foundation.network.ByteBuffer4 _uploadBuffer;

		private com.db4o.foundation.network.ByteBuffer4 _downloadBuffer;

		public LoopbackSocket(com.db4o.foundation.network.LoopbackSocketServer a_server, 
			int timeout)
		{
			_server = a_server;
			_uploadBuffer = new com.db4o.foundation.network.ByteBuffer4(timeout);
			_downloadBuffer = new com.db4o.foundation.network.ByteBuffer4(timeout);
		}

		public LoopbackSocket(com.db4o.foundation.network.LoopbackSocketServer a_server, 
			int timeout, com.db4o.foundation.network.LoopbackSocket affiliate) : this(a_server
			, timeout)
		{
			_affiliate = affiliate;
			affiliate._affiliate = this;
			_downloadBuffer = affiliate._uploadBuffer;
			_uploadBuffer = affiliate._downloadBuffer;
		}

		public virtual void Close()
		{
			if (_affiliate != null)
			{
				com.db4o.foundation.network.LoopbackSocket temp = _affiliate;
				_affiliate = null;
				temp.Close();
			}
			CloseSocket();
		}

		private void CloseSocket()
		{
			_downloadBuffer.Close();
			_uploadBuffer.Close();
		}

		public virtual void Flush()
		{
		}

		public virtual bool IsConnected()
		{
			return _affiliate != null;
		}

		public virtual int Read()
		{
			return _downloadBuffer.Read();
		}

		public virtual int Read(byte[] a_bytes, int a_offset, int a_length)
		{
			return _downloadBuffer.Read(a_bytes, a_offset, a_length);
		}

		public virtual void SetSoTimeout(int a_timeout)
		{
			_uploadBuffer.SetTimeout(a_timeout);
			_downloadBuffer.SetTimeout(a_timeout);
		}

		public virtual void Write(byte[] bytes)
		{
			_uploadBuffer.Write(bytes);
		}

		public virtual void Write(byte[] bytes, int off, int len)
		{
			_uploadBuffer.Write(bytes, off, len);
		}

		public virtual void Write(int i)
		{
			_uploadBuffer.Write(i);
		}

		public virtual com.db4o.foundation.network.Socket4 OpenParalellSocket()
		{
			return _server.OpenClientSocket();
		}
	}
}
