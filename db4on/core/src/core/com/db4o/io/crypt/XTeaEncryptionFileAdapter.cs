namespace com.db4o.io.crypt
{
	/// <summary>
	/// XTeaEncryptionFileAdapter is an encryption IoAdapter plugin for db4o file IO
	/// <br />
	/// that realized XTEA encryption algorithm.
	/// </summary>
	/// <remarks>
	/// XTeaEncryptionFileAdapter is an encryption IoAdapter plugin for db4o file IO
	/// <br />
	/// that realized XTEA encryption algorithm. <br />
	/// <br />
	/// Configure db4o to add this encryption mechanism:<br />
	/// <code>Db4o.configure().io(new XTeaEncryptionFileAdapter("password"));
	/// </code><br />
	/// Any changes must be taken with the same password.<br />
	/// <br />
	/// Remember that any configuration settings must be set before opening
	/// ObjectContainer.
	/// </remarks>
	public class XTeaEncryptionFileAdapter : com.db4o.io.IoAdapter
	{
		private com.db4o.io.IoAdapter _adapter;

		private string _key;

		private com.db4o.io.crypt.XTEA _xtea;

		private long _pos;

		private com.db4o.io.crypt.XTEA.IterationSpec _iterat;

		public XTeaEncryptionFileAdapter(string password) : this(new com.db4o.io.RandomAccessFileAdapter
			(), password, com.db4o.io.crypt.XTEA.ITERATIONS32)
		{
		}

		public XTeaEncryptionFileAdapter(string password, com.db4o.io.crypt.XTEA.IterationSpec
			 iterat) : this(new com.db4o.io.RandomAccessFileAdapter(), password, iterat)
		{
		}

		public XTeaEncryptionFileAdapter(com.db4o.io.IoAdapter adapter, string password, 
			com.db4o.io.crypt.XTEA.IterationSpec iterat)
		{
			_adapter = adapter;
			_key = password;
			_iterat = iterat;
		}

		public XTeaEncryptionFileAdapter(com.db4o.io.IoAdapter adapter, string password)
		{
			_adapter = adapter;
			_key = password;
			_iterat = com.db4o.io.crypt.XTEA.ITERATIONS32;
		}

		private XTeaEncryptionFileAdapter(com.db4o.io.IoAdapter adapter, com.db4o.io.crypt.XTEA
			 xtea)
		{
			_adapter = adapter;
			_xtea = xtea;
		}

		/// <summary>implement to close the adapter</summary>
		public override void close()
		{
			_adapter.close();
		}

		/// <summary>implement to return the absolute length of the file</summary>
		public override long getLength()
		{
			return _adapter.getLength();
		}

		/// <summary>implement to open the file</summary>
		public override com.db4o.io.IoAdapter open(string path, bool lockFile, long initialLength
			)
		{
			return new com.db4o.io.crypt.XTeaEncryptionFileAdapter(_adapter.open(path, lockFile
				, initialLength), new com.db4o.io.crypt.XTEA(_key, _iterat));
		}

		/// <summary>implement to read and decrypt a buffer</summary>
		public override int read(byte[] bytes, int length)
		{
			long origPos = _pos;
			int fullLength = length;
			int prePad = (int)(_pos % 8);
			fullLength += prePad;
			int overhang = fullLength % 8;
			int postPad = (overhang == 0 ? 0 : 8 - (overhang));
			fullLength += postPad;
			byte[] pb = new byte[fullLength];
			if (prePad != 0)
			{
				seek(_pos - prePad);
			}
			int readResult = _adapter.read(pb);
			_xtea.decrypt(pb);
			j4o.lang.JavaSystem.arraycopy(pb, prePad, bytes, 0, length);
			seek(origPos + length);
			return readResult;
		}

		/// <summary>implement to set the read/write pointer in the file</summary>
		public override void seek(long pos)
		{
			_pos = pos;
			_adapter.seek(pos);
		}

		/// <summary>implement to flush the file contents to storage</summary>
		public override void sync()
		{
			_adapter.sync();
		}

		/// <summary>implement to write and encrypt a buffer</summary>
		public override void write(byte[] buffer, int length)
		{
			long origPos = _pos;
			int fullLength = length;
			int prePad = (int)(_pos % 8);
			fullLength += prePad;
			int overhang = fullLength % 8;
			int postPad = (overhang == 0 ? 0 : 8 - (overhang));
			fullLength += postPad;
			byte[] pb = new byte[fullLength];
			if (prePad != 0)
			{
				seek(origPos - prePad);
			}
			if (blockSize() % 8 != 0 || prePad != 0)
			{
				read(pb);
				seek(origPos - prePad);
			}
			j4o.lang.JavaSystem.arraycopy(buffer, 0, pb, prePad, length);
			if (prePad == 0)
			{
			}
			_xtea.encrypt(pb);
			_adapter.write(pb, pb.Length);
			seek(origPos + length);
		}

		private void log(string msg, byte[] buf)
		{
			j4o.lang.JavaSystem._out.println("\n " + msg);
			for (int idx = 0; idx < buf.Length; idx++)
			{
				j4o.lang.JavaSystem._out.print(buf[idx] + " ");
			}
		}
	}
}
