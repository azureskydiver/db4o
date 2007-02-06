namespace com.db4o.db4ounit.common.io
{
	public class IoAdapterTest : Db4oUnit.TestCase, Db4oUnit.TestLifeCycle
	{
		private string _cachedIoAdapterFile = "CachedIoAdapter.dat";

		private string _randomAccessFileAdapterFile = "_randomAccessFileAdapter.dat";

		private com.db4o.io.IoAdapter[] _adapters;

		public virtual void SetUp()
		{
			DeleteAllTestFiles();
			_adapters = new com.db4o.io.IoAdapter[] { InitCachedRandomAccessAdapter(), InitRandomAccessAdapter
				() };
		}

		public virtual void TearDown()
		{
			CloseAllAdapters();
			DeleteAllTestFiles();
		}

		public virtual void TestReadWrite()
		{
			for (int i = 0; i < _adapters.Length; ++i)
			{
				AssertReadWrite(_adapters[i]);
			}
		}

		private void AssertReadWrite(com.db4o.io.IoAdapter adapter)
		{
			adapter.Seek(0);
			int count = 1024 * 8 + 10;
			byte[] data = new byte[count];
			for (int i = 0; i < count; ++i)
			{
				data[i] = (byte)(i % 256);
			}
			adapter.Write(data);
			adapter.Seek(0);
			byte[] readBytes = new byte[count];
			adapter.Read(readBytes);
			for (int i = 0; i < count; i++)
			{
				Db4oUnit.Assert.AreEqual(data[i], readBytes[i]);
			}
		}

		public virtual void TestSeek()
		{
			for (int i = 0; i < _adapters.Length; ++i)
			{
				AssertSeek(_adapters[i]);
			}
		}

		private void AssertSeek(com.db4o.io.IoAdapter adapter)
		{
			int count = 1024 * 2 + 10;
			byte[] data = new byte[count];
			for (int i = 0; i < data.Length; ++i)
			{
				data[i] = (byte)(i % 256);
			}
			adapter.Write(data);
			byte[] readBytes = new byte[count];
			adapter.Seek(0);
			adapter.Read(readBytes);
			for (int i = 0; i < count; i++)
			{
				Db4oUnit.Assert.AreEqual(data[i], readBytes[i]);
			}
			adapter.Seek(20);
			adapter.Read(readBytes);
			for (int i = 0; i < count - 20; i++)
			{
				Db4oUnit.Assert.AreEqual(data[i + 20], readBytes[i]);
			}
			byte[] writtenData = new byte[10];
			for (int i = 0; i < writtenData.Length; ++i)
			{
				writtenData[i] = (byte)i;
			}
			adapter.Seek(1000);
			adapter.Write(writtenData);
			adapter.Seek(1000);
			int readCount = adapter.Read(readBytes, 10);
			Db4oUnit.Assert.AreEqual(10, readCount);
			for (int i = 0; i < readCount; ++i)
			{
				Db4oUnit.Assert.AreEqual(i, readBytes[i]);
			}
		}

		private com.db4o.io.IoAdapter InitCachedRandomAccessAdapter()
		{
			com.db4o.io.IoAdapter adapter = new com.db4o.io.CachedIoAdapter(new com.db4o.io.RandomAccessFileAdapter
				());
			adapter = adapter.Open(_cachedIoAdapterFile, false, 0);
			return adapter;
		}

		private com.db4o.io.IoAdapter InitRandomAccessAdapter()
		{
			com.db4o.io.IoAdapter adapter = new com.db4o.io.RandomAccessFileAdapter();
			adapter = adapter.Open(_randomAccessFileAdapterFile, false, 0);
			return adapter;
		}

		private void DeleteAllTestFiles()
		{
			new j4o.io.File(_cachedIoAdapterFile).Delete();
			new j4o.io.File(_randomAccessFileAdapterFile).Delete();
		}

		private void CloseAllAdapters()
		{
			for (int i = 0; i < _adapters.Length; ++i)
			{
				try
				{
					_adapters[i].Close();
				}
				catch (System.IO.IOException)
				{
				}
			}
		}
	}
}
