namespace com.db4o.foundation.network
{
	/// <summary>
	/// Transport buffer for C/S mode to simulate a
	/// socket connection in memory.
	/// </summary>
	/// <remarks>
	/// Transport buffer for C/S mode to simulate a
	/// socket connection in memory.
	/// </remarks>
	internal class ByteBuffer4
	{
		private const int DISCARD_BUFFER_SIZE = 500;

		private byte[] i_cache;

		private bool i_closed = false;

		private int i_readOffset;

		protected int i_timeout;

		private int i_writeOffset;

		private readonly com.db4o.foundation.Lock4 i_lock = new com.db4o.foundation.Lock4
			();

		public ByteBuffer4(int timeout)
		{
			i_timeout = timeout;
		}

		private int Available()
		{
			return i_writeOffset - i_readOffset;
		}

		private void CheckDiscardCache()
		{
			if (i_readOffset == i_writeOffset && i_cache.Length > DISCARD_BUFFER_SIZE)
			{
				i_cache = null;
				i_readOffset = 0;
				i_writeOffset = 0;
			}
		}

		internal virtual void Close()
		{
			i_closed = true;
		}

		private void Makefit(int length)
		{
			if (i_cache == null)
			{
				i_cache = new byte[length];
			}
			else
			{
				if (i_writeOffset + length > i_cache.Length)
				{
					if (i_writeOffset + length - i_readOffset <= i_cache.Length)
					{
						byte[] temp = new byte[i_cache.Length];
						System.Array.Copy(i_cache, i_readOffset, temp, 0, i_cache.Length - i_readOffset);
						i_cache = temp;
						i_writeOffset -= i_readOffset;
						i_readOffset = 0;
					}
					else
					{
						byte[] temp = new byte[i_writeOffset + length];
						System.Array.Copy(i_cache, 0, temp, 0, i_cache.Length);
						i_cache = temp;
					}
				}
			}
		}

		public virtual int Read()
		{
			try
			{
				int ret = (int)i_lock.Run(new _AnonymousInnerClass70(this));
				return ret;
			}
			catch (System.IO.IOException iex)
			{
				throw iex;
			}
			catch (System.Exception bex)
			{
			}
			return -1;
		}

		private sealed class _AnonymousInnerClass70 : com.db4o.foundation.Closure4
		{
			public _AnonymousInnerClass70(ByteBuffer4 _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public object Run()
			{
				this._enclosing.WaitForAvailable();
				int retVal = this._enclosing.i_cache[this._enclosing.i_readOffset++];
				this._enclosing.CheckDiscardCache();
				return retVal;
			}

			private readonly ByteBuffer4 _enclosing;
		}

		public virtual int Read(byte[] a_bytes, int a_offset, int a_length)
		{
			try
			{
				int ret = (int)i_lock.Run(new _AnonymousInnerClass90(this, a_length, a_bytes, a_offset
					));
				return ret;
			}
			catch (System.IO.IOException iex)
			{
				throw iex;
			}
			catch (System.Exception bex)
			{
			}
			return -1;
		}

		private sealed class _AnonymousInnerClass90 : com.db4o.foundation.Closure4
		{
			public _AnonymousInnerClass90(ByteBuffer4 _enclosing, int a_length, byte[] a_bytes
				, int a_offset)
			{
				this._enclosing = _enclosing;
				this.a_length = a_length;
				this.a_bytes = a_bytes;
				this.a_offset = a_offset;
			}

			public object Run()
			{
				this._enclosing.WaitForAvailable();
				int avail = this._enclosing.Available();
				int length = a_length;
				if (avail < a_length)
				{
					length = avail;
				}
				System.Array.Copy(this._enclosing.i_cache, this._enclosing.i_readOffset, a_bytes, 
					a_offset, length);
				this._enclosing.i_readOffset += length;
				this._enclosing.CheckDiscardCache();
				return avail;
			}

			private readonly ByteBuffer4 _enclosing;

			private readonly int a_length;

			private readonly byte[] a_bytes;

			private readonly int a_offset;
		}

		public virtual void SetTimeout(int timeout)
		{
			i_timeout = timeout;
		}

		private void WaitForAvailable()
		{
			while (Available() == 0)
			{
				try
				{
					i_lock.Snooze(i_timeout);
				}
				catch (System.Exception e)
				{
					throw new System.IO.IOException(com.db4o.Messages.Get(55));
				}
			}
			if (i_closed)
			{
				throw new System.IO.IOException(com.db4o.Messages.Get(35));
			}
		}

		public virtual void Write(byte[] bytes)
		{
			Write(bytes, 0, bytes.Length);
		}

		public virtual void Write(byte[] bytes, int off, int len)
		{
			try
			{
				i_lock.Run(new _AnonymousInnerClass138(this, len, bytes, off));
			}
			catch (System.Exception e)
			{
			}
		}

		private sealed class _AnonymousInnerClass138 : com.db4o.foundation.Closure4
		{
			public _AnonymousInnerClass138(ByteBuffer4 _enclosing, int len, byte[] bytes, int
				 off)
			{
				this._enclosing = _enclosing;
				this.len = len;
				this.bytes = bytes;
				this.off = off;
			}

			public object Run()
			{
				this._enclosing.Makefit(len);
				System.Array.Copy(bytes, off, this._enclosing.i_cache, this._enclosing.i_writeOffset
					, len);
				this._enclosing.i_writeOffset += len;
				this._enclosing.i_lock.Awake();
				return null;
			}

			private readonly ByteBuffer4 _enclosing;

			private readonly int len;

			private readonly byte[] bytes;

			private readonly int off;
		}

		public virtual void Write(int i)
		{
			try
			{
				i_lock.Run(new _AnonymousInnerClass156(this, i));
			}
			catch (System.Exception e)
			{
			}
		}

		private sealed class _AnonymousInnerClass156 : com.db4o.foundation.Closure4
		{
			public _AnonymousInnerClass156(ByteBuffer4 _enclosing, int i)
			{
				this._enclosing = _enclosing;
				this.i = i;
			}

			public object Run()
			{
				this._enclosing.Makefit(1);
				this._enclosing.i_cache[this._enclosing.i_writeOffset++] = (byte)i;
				this._enclosing.i_lock.Awake();
				return null;
			}

			private readonly ByteBuffer4 _enclosing;

			private readonly int i;
		}
	}
}
