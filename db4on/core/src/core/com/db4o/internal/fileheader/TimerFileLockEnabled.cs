namespace com.db4o.@internal.fileheader
{
	/// <exclude></exclude>
	public class TimerFileLockEnabled : com.db4o.@internal.fileheader.TimerFileLock
	{
		private readonly com.db4o.io.IoAdapter _timerFile;

		private readonly object _timerLock;

		private byte[] _longBytes = new byte[com.db4o.@internal.Const4.LONG_LENGTH];

		private byte[] _intBytes = new byte[com.db4o.@internal.Const4.INT_LENGTH];

		private int _headerLockOffset = 2 + com.db4o.@internal.Const4.INT_LENGTH;

		private readonly long _opentime;

		private int _baseAddress = -1;

		private int _openTimeOffset;

		private int _accessTimeOffset;

		private bool _closed = false;

		public TimerFileLockEnabled(com.db4o.@internal.IoAdaptedObjectContainer file)
		{
			_timerLock = file.Lock();
			_timerFile = file.TimerFile();
			_opentime = UniqueOpenTime();
		}

		public override void CheckHeaderLock()
		{
			try
			{
				if (((int)_opentime) == ReadInt(0, _headerLockOffset))
				{
					WriteHeaderLock();
					return;
				}
			}
			catch (System.IO.IOException)
			{
			}
			throw new com.db4o.ext.DatabaseFileLockedException();
		}

		public override void CheckOpenTime()
		{
			try
			{
				if (_opentime == ReadLong(_baseAddress, _openTimeOffset))
				{
					WriteOpenTime();
					return;
				}
			}
			catch (System.IO.IOException)
			{
			}
			throw new com.db4o.ext.DatabaseFileLockedException();
		}

		public override void Close()
		{
			WriteAccessTime(true);
			_closed = true;
		}

		public override bool LockFile()
		{
			return true;
		}

		public override long OpenTime()
		{
			return _opentime;
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
			_timerFile.Sync();
			CheckOpenTime();
			new j4o.lang.Thread(this).Start();
		}

		private long UniqueOpenTime()
		{
			return j4o.lang.JavaSystem.CurrentTimeMillis();
		}

		private bool WriteAccessTime(bool closing)
		{
			if (NoAddressSet())
			{
				return true;
			}
			long time = closing ? 0 : j4o.lang.JavaSystem.CurrentTimeMillis();
			bool ret = WriteLong(_baseAddress, _accessTimeOffset, time);
			Sync();
			return ret;
		}

		private bool NoAddressSet()
		{
			return _baseAddress < 0;
		}

		public override void WriteHeaderLock()
		{
			try
			{
				WriteInt(0, _headerLockOffset, (int)_opentime);
				Sync();
			}
			catch (System.IO.IOException)
			{
			}
		}

		public override void WriteOpenTime()
		{
			try
			{
				WriteLong(_baseAddress, _openTimeOffset, _opentime);
				Sync();
			}
			catch (System.IO.IOException)
			{
			}
		}

		private bool WriteLong(int address, int offset, long time)
		{
			lock (_timerLock)
			{
				if (_timerFile == null)
				{
					return false;
				}
				_timerFile.BlockSeek(address, offset);
				com.db4o.foundation.PrimitiveCodec.WriteLong(_longBytes, time);
				_timerFile.Write(_longBytes);
				return true;
			}
		}

		private long ReadLong(int address, int offset)
		{
			lock (_timerLock)
			{
				if (_timerFile == null)
				{
					return 0;
				}
				_timerFile.BlockSeek(address, offset);
				_timerFile.Read(_longBytes);
				return com.db4o.foundation.PrimitiveCodec.ReadLong(_longBytes, 0);
			}
		}

		private bool WriteInt(int address, int offset, int time)
		{
			lock (_timerLock)
			{
				if (_timerFile == null)
				{
					return false;
				}
				_timerFile.BlockSeek(address, offset);
				com.db4o.foundation.PrimitiveCodec.WriteInt(_intBytes, 0, time);
				_timerFile.Write(_intBytes);
				return true;
			}
		}

		private long ReadInt(int address, int offset)
		{
			lock (_timerLock)
			{
				if (_timerFile == null)
				{
					return 0;
				}
				_timerFile.BlockSeek(address, offset);
				_timerFile.Read(_longBytes);
				return com.db4o.foundation.PrimitiveCodec.ReadInt(_longBytes, 0);
			}
		}

		private void Sync()
		{
			_timerFile.Sync();
		}
	}
}
