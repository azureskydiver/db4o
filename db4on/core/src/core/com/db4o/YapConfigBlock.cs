namespace com.db4o
{
	/// <summary>
	/// configuration and agent to write the configuration block
	/// The configuration block also contains the timer lock and
	/// a pointer to the running transaction.
	/// </summary>
	/// <remarks>
	/// configuration and agent to write the configuration block
	/// The configuration block also contains the timer lock and
	/// a pointer to the running transaction.
	/// </remarks>
	/// <exclude></exclude>
	public sealed class YapConfigBlock : j4o.lang.Runnable
	{
		private readonly com.db4o.YapFile _stream;

		public int _address;

		private com.db4o.Transaction _transactionToCommit;

		public int _bootRecordID;

		private const int MINIMUM_LENGTH = com.db4o.YapConst.INT_LENGTH + (com.db4o.YapConst
			.LONG_LENGTH * 2) + 1;

		internal const int OPEN_TIME_OFFSET = com.db4o.YapConst.INT_LENGTH;

		public const int ACCESS_TIME_OFFSET = OPEN_TIME_OFFSET + com.db4o.YapConst.LONG_LENGTH;

		public const int TRANSACTION_OFFSET = MINIMUM_LENGTH;

		private const int BOOTRECORD_OFFSET = TRANSACTION_OFFSET + com.db4o.YapConst.INT_LENGTH
			 * 2;

		private const int INT_FORMERLY_KNOWN_AS_BLOCK_OFFSET = BOOTRECORD_OFFSET + com.db4o.YapConst
			.INT_LENGTH;

		private const int ENCRYPTION_PASSWORD_LENGTH = 5;

		private const int PASSWORD_OFFSET = INT_FORMERLY_KNOWN_AS_BLOCK_OFFSET + ENCRYPTION_PASSWORD_LENGTH;

		private const int FREESPACE_SYSTEM_OFFSET = PASSWORD_OFFSET + 1;

		private const int FREESPACE_ADDRESS_OFFSET = FREESPACE_SYSTEM_OFFSET + com.db4o.YapConst
			.INT_LENGTH;

		private const int CONVERTER_VERSION_OFFSET = FREESPACE_ADDRESS_OFFSET + com.db4o.YapConst
			.INT_LENGTH;

		private const int UUID_INDEX_ID_OFFSET = CONVERTER_VERSION_OFFSET + com.db4o.YapConst
			.INT_LENGTH;

		private const int LENGTH = MINIMUM_LENGTH + (com.db4o.YapConst.INT_LENGTH * 7) + 
			ENCRYPTION_PASSWORD_LENGTH + 1;

		public readonly long _opentime;

		internal byte _encoding;

		public byte _freespaceSystem;

		public int _freespaceAddress;

		private int _converterVersion;

		public int _uuidIndexId;

		public YapConfigBlock(com.db4o.YapFile stream)
		{
			_stream = stream;
			_encoding = stream.ConfigImpl().Encoding();
			_freespaceSystem = com.db4o.inside.freespace.FreespaceManager.CheckType(stream.ConfigImpl
				().FreespaceSystem());
			_opentime = ProcessID();
			if (LockFile())
			{
				WriteHeaderLock();
			}
		}

		public com.db4o.Transaction GetTransactionToCommit()
		{
			return _transactionToCommit;
		}

		private void EnsureFreespaceSlot()
		{
			if (_freespaceAddress == 0)
			{
				NewFreespaceSlot(_freespaceSystem);
			}
		}

		public int NewFreespaceSlot(byte freespaceSystem)
		{
			_freespaceAddress = com.db4o.inside.freespace.FreespaceManager.InitSlot(_stream);
			_freespaceSystem = freespaceSystem;
			return _freespaceAddress;
		}

		public void Go()
		{
			_stream.CreateStringIO(_encoding);
			if (LockFile())
			{
				try
				{
					WriteAccessTime();
				}
				catch (System.Exception e)
				{
				}
				SyncFiles();
				OpenTimeOverWritten();
				new j4o.lang.Thread(this).Start();
			}
		}

		private com.db4o.YapWriter HeaderLockIO()
		{
			com.db4o.YapWriter writer = _stream.GetWriter(_stream.GetTransaction(), 0, com.db4o.YapConst
				.INT_LENGTH);
			writer.MoveForward(2 + com.db4o.YapConst.INT_LENGTH);
			return writer;
		}

		private void HeaderLockOverwritten()
		{
			if (LockFile())
			{
				com.db4o.YapWriter bytes = HeaderLockIO();
				bytes.Read();
				int newOpenTime = com.db4o.YInt.ReadInt(bytes);
				if (newOpenTime != ((int)_opentime))
				{
					throw new com.db4o.ext.DatabaseFileLockedException();
				}
				WriteHeaderLock();
			}
		}

		private bool LockFile()
		{
			return _stream.NeedsLockFileThread();
		}

		private com.db4o.YapWriter OpenTimeIO()
		{
			com.db4o.YapWriter writer = _stream.GetWriter(_stream.GetTransaction(), _address, 
				com.db4o.YapConst.LONG_LENGTH);
			writer.MoveForward(OPEN_TIME_OFFSET);
			return writer;
		}

		private void OpenTimeOverWritten()
		{
			if (LockFile())
			{
				com.db4o.YapWriter bytes = OpenTimeIO();
				bytes.Read();
				if (com.db4o.YLong.ReadLong(bytes) != _opentime)
				{
					com.db4o.inside.Exceptions4.ThrowRuntimeException(22);
				}
				WriteOpenTime();
			}
		}

		private byte[] PasswordToken()
		{
			byte[] pwdtoken = new byte[ENCRYPTION_PASSWORD_LENGTH];
			string fullpwd = _stream.ConfigImpl().Password();
			if (_stream.ConfigImpl().Encrypt() && fullpwd != null)
			{
				try
				{
					byte[] pwdbytes = new com.db4o.YapStringIO().Write(fullpwd);
					com.db4o.YapWriter encwriter = new com.db4o.YapWriter(_stream.i_trans, pwdbytes.Length
						 + ENCRYPTION_PASSWORD_LENGTH);
					encwriter.Append(pwdbytes);
					encwriter.Append(new byte[ENCRYPTION_PASSWORD_LENGTH]);
					_stream.i_handlers.Decrypt(encwriter);
					System.Array.Copy(encwriter._buffer, 0, pwdtoken, 0, ENCRYPTION_PASSWORD_LENGTH);
				}
				catch (System.Exception exc)
				{
					j4o.lang.JavaSystem.PrintStackTrace(exc);
				}
			}
			return pwdtoken;
		}

		internal static long ProcessID()
		{
			long id = j4o.lang.JavaSystem.CurrentTimeMillis();
			return id;
		}

		public void Read(int address)
		{
			_address = address;
			WriteOpenTime();
			com.db4o.YapWriter reader = _stream.GetWriter(_stream.GetSystemTransaction(), _address
				, LENGTH);
			try
			{
				_stream.ReadBytes(reader._buffer, _address, LENGTH);
			}
			catch (System.Exception e)
			{
			}
			int oldLength = reader.ReadInt();
			if (oldLength > LENGTH || oldLength < MINIMUM_LENGTH)
			{
				com.db4o.inside.Exceptions4.ThrowRuntimeException(17);
			}
			if (oldLength != LENGTH)
			{
				if (!_stream.ConfigImpl().IsReadOnly() && !_stream.ConfigImpl().AllowVersionUpdates
					())
				{
					if (_stream.ConfigImpl().AutomaticShutDown())
					{
						com.db4o.Platform4.RemoveShutDownHook(_stream, _stream.i_lock);
					}
					com.db4o.inside.Exceptions4.ThrowRuntimeException(65);
				}
			}
			com.db4o.YLong.ReadLong(reader);
			long lastAccessTime = com.db4o.YLong.ReadLong(reader);
			_encoding = reader.ReadByte();
			if (oldLength > TRANSACTION_OFFSET)
			{
				int transactionID1 = com.db4o.YInt.ReadInt(reader);
				int transactionID2 = com.db4o.YInt.ReadInt(reader);
				if ((transactionID1 > 0) && (transactionID1 == transactionID2))
				{
					_transactionToCommit = _stream.NewTransaction(null);
					_transactionToCommit.SetAddress(transactionID1);
				}
			}
			if (oldLength > BOOTRECORD_OFFSET)
			{
				_bootRecordID = com.db4o.YInt.ReadInt(reader);
			}
			if (oldLength > INT_FORMERLY_KNOWN_AS_BLOCK_OFFSET)
			{
				com.db4o.YInt.ReadInt(reader);
			}
			if (oldLength > PASSWORD_OFFSET)
			{
				byte[] encpassword = reader.ReadBytes(ENCRYPTION_PASSWORD_LENGTH);
				bool nonZeroByte = false;
				for (int i = 0; i < encpassword.Length; i++)
				{
					if (encpassword[i] != 0)
					{
						nonZeroByte = true;
						break;
					}
				}
				if (!nonZeroByte)
				{
					_stream.i_handlers.OldEncryptionOff();
				}
				else
				{
					byte[] storedpwd = PasswordToken();
					for (int idx = 0; idx < storedpwd.Length; idx++)
					{
						if (storedpwd[idx] != encpassword[idx])
						{
							_stream.FatalException(54);
						}
					}
				}
			}
			_freespaceSystem = com.db4o.inside.freespace.FreespaceManager.FM_LEGACY_RAM;
			if (oldLength > FREESPACE_SYSTEM_OFFSET)
			{
				_freespaceSystem = reader.ReadByte();
			}
			if (oldLength > FREESPACE_ADDRESS_OFFSET)
			{
				_freespaceAddress = reader.ReadInt();
			}
			if (oldLength > CONVERTER_VERSION_OFFSET)
			{
				_converterVersion = reader.ReadInt();
			}
			if (oldLength > UUID_INDEX_ID_OFFSET)
			{
				_uuidIndexId = reader.ReadInt();
			}
			EnsureFreespaceSlot();
			if (LockFile() && (lastAccessTime != 0))
			{
				_stream.LogMsg(28, null);
				long waitTime = com.db4o.YapConst.LOCK_TIME_INTERVAL * 10;
				long currentTime = j4o.lang.JavaSystem.CurrentTimeMillis();
				while (j4o.lang.JavaSystem.CurrentTimeMillis() < currentTime + waitTime)
				{
					com.db4o.foundation.Cool.SleepIgnoringInterruption(waitTime);
				}
				reader = _stream.GetWriter(_stream.GetSystemTransaction(), _address, com.db4o.YapConst
					.LONG_LENGTH * 2);
				reader.MoveForward(OPEN_TIME_OFFSET);
				reader.Read();
				com.db4o.YLong.ReadLong(reader);
				long currentAccessTime = com.db4o.YLong.ReadLong(reader);
				if ((currentAccessTime > lastAccessTime))
				{
					throw new com.db4o.ext.DatabaseFileLockedException();
				}
			}
			if (LockFile())
			{
				com.db4o.foundation.Cool.SleepIgnoringInterruption(100);
				SyncFiles();
				OpenTimeOverWritten();
			}
			if (oldLength < LENGTH)
			{
				Write();
			}
			Go();
		}

		public void Run()
		{
		}

		public void ConverterVersion(int ver)
		{
			_converterVersion = ver;
		}

		public int ConverterVersion()
		{
			return _converterVersion;
		}

		internal void SyncFiles()
		{
			_stream.SyncFiles();
		}

		public void Write()
		{
			HeaderLockOverwritten();
			_address = _stream.GetSlot(LENGTH);
			com.db4o.YapWriter writer = _stream.GetWriter(_stream.i_trans, _address, LENGTH);
			com.db4o.YInt.WriteInt(LENGTH, writer);
			com.db4o.YLong.WriteLong(_opentime, writer);
			com.db4o.YLong.WriteLong(_opentime, writer);
			writer.Append(_encoding);
			com.db4o.YInt.WriteInt(0, writer);
			com.db4o.YInt.WriteInt(0, writer);
			com.db4o.YInt.WriteInt(_bootRecordID, writer);
			com.db4o.YInt.WriteInt(0, writer);
			writer.Append(PasswordToken());
			writer.Append(_freespaceSystem);
			EnsureFreespaceSlot();
			com.db4o.YInt.WriteInt(_freespaceAddress, writer);
			com.db4o.YInt.WriteInt(_converterVersion, writer);
			com.db4o.YInt.WriteInt(_uuidIndexId, writer);
			writer.Write();
			WritePointer();
		}

		internal bool WriteAccessTime()
		{
			return _stream.WriteAccessTime();
		}

		private void WriteOpenTime()
		{
			if (LockFile())
			{
				com.db4o.YapWriter writer = OpenTimeIO();
				com.db4o.YLong.WriteLong(_opentime, writer);
				writer.Write();
			}
		}

		private void WriteHeaderLock()
		{
			if (LockFile())
			{
				com.db4o.YapWriter writer = HeaderLockIO();
				com.db4o.YInt.WriteInt(((int)_opentime), writer);
				writer.Write();
			}
		}

		private void WritePointer()
		{
			HeaderLockOverwritten();
			com.db4o.YapWriter writer = _stream.GetWriter(_stream.i_trans, 0, com.db4o.YapConst
				.ID_LENGTH);
			writer.MoveForward(2);
			com.db4o.YInt.WriteInt(_address, writer);
			if (com.db4o.Debug.xbytes && com.db4o.Deploy.overwrite)
			{
				writer.SetID(com.db4o.YapConst.IGNORE_ID);
			}
			writer.Write();
			WriteHeaderLock();
		}
	}
}
