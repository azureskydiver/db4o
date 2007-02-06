namespace com.db4o.@internal.fileheader
{
	/// <exclude></exclude>
	public class TimerFileLockEnabled : com.db4o.@internal.fileheader.TimerFileLock
	{
		private readonly com.db4o.@internal.LocalObjectContainer _file;

		private int _headerLockOffset = 2 + com.db4o.@internal.Const4.INT_LENGTH;

		private readonly long _opentime;

		private int _baseAddress;

		private int _openTimeOffset;

		private int _accessTimeOffset;

		private bool _closed = false;

		public TimerFileLockEnabled(com.db4o.@internal.LocalObjectContainer file)
		{
			_file = file;
			_opentime = UniqueOpenTime();
		}

		public override void CheckHeaderLock()
		{
			com.db4o.@internal.StatefulBuffer reader = HeaderLockIO();
			reader.Read();
			if (reader.ReadInt() != (int)_opentime)
			{
				throw new com.db4o.ext.DatabaseFileLockedException();
			}
			WriteHeaderLock();
		}

		public override void CheckOpenTime()
		{
			com.db4o.@internal.StatefulBuffer reader = OpenTimeIO();
			if (reader == null)
			{
				return;
			}
			reader.Read();
			if (reader.ReadLong() != _opentime)
			{
				com.db4o.@internal.Exceptions4.ThrowRuntimeException(22);
			}
			WriteOpenTime();
		}

		public override void Close()
		{
			WriteAccessTime(true);
			_closed = true;
		}

		private com.db4o.@internal.StatefulBuffer GetWriter(int address, int offset, int 
			length)
		{
			com.db4o.@internal.StatefulBuffer writer = _file.GetWriter(_file.GetTransaction()
				, address, length);
			writer.MoveForward(offset);
			return writer;
		}

		private com.db4o.@internal.StatefulBuffer HeaderLockIO()
		{
			com.db4o.@internal.StatefulBuffer writer = GetWriter(0, _headerLockOffset, com.db4o.@internal.Const4
				.INT_LENGTH);
			return writer;
		}

		public override bool LockFile()
		{
			return true;
		}

		public override long OpenTime()
		{
			return _opentime;
		}

		private com.db4o.@internal.StatefulBuffer OpenTimeIO()
		{
			if (_baseAddress == 0)
			{
				return null;
			}
			com.db4o.@internal.StatefulBuffer writer = GetWriter(_baseAddress, _openTimeOffset
				, com.db4o.@internal.Const4.LONG_LENGTH);
			return writer;
		}

		public override void Run()
		{
			j4o.lang.Thread t = j4o.lang.Thread.CurrentThread();
			t.SetName("db4o file lock");
			try
			{
				while (WriteAccessTime(false))
				{
					com.db4o.foundation.Cool.SleepIgnoringInterruption(com.db4o.@internal.Const4.LOCK_TIME_INTERVAL
						);
					if (_closed)
					{
						break;
					}
				}
			}
			catch (System.IO.IOException)
			{
			}
		}

		public override void SetAddresses(int baseAddress, int openTimeOffset, int accessTimeOffset
			)
		{
			_baseAddress = baseAddress;
			_openTimeOffset = openTimeOffset;
			_accessTimeOffset = accessTimeOffset;
		}

		public override void Start()
		{
			WriteAccessTime(false);
			_file.SyncFiles();
			CheckOpenTime();
			new j4o.lang.Thread(this).Start();
		}

		private long UniqueOpenTime()
		{
			return j4o.lang.JavaSystem.CurrentTimeMillis();
		}

		private bool WriteAccessTime(bool closing)
		{
			if (_baseAddress < 1)
			{
				return true;
			}
			long time = closing ? 0 : j4o.lang.JavaSystem.CurrentTimeMillis();
			return _file.WriteAccessTime(_baseAddress, _accessTimeOffset, time);
		}

		public override void WriteHeaderLock()
		{
			com.db4o.@internal.StatefulBuffer writer = HeaderLockIO();
			writer.WriteInt((int)_opentime);
			writer.Write();
		}

		public override void WriteOpenTime()
		{
			com.db4o.@internal.StatefulBuffer writer = OpenTimeIO();
			if (writer == null)
			{
				return;
			}
			writer.WriteLong(_opentime);
			writer.Write();
		}
	}
}
