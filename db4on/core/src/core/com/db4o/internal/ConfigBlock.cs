namespace com.db4o.@internal
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
	public sealed class ConfigBlock
	{
		private readonly com.db4o.@internal.LocalObjectContainer _stream;

		private readonly com.db4o.@internal.fileheader.TimerFileLock _timerFileLock;

		private int _address;

		private com.db4o.@internal.Transaction _transactionToCommit;

		public int _bootRecordID;

		private const int MINIMUM_LENGTH = com.db4o.@internal.Const4.INT_LENGTH + (com.db4o.@internal.Const4
			.LONG_LENGTH * 2) + 1;

		internal const int OPEN_TIME_OFFSET = com.db4o.@internal.Const4.INT_LENGTH;

		public const int ACCESS_TIME_OFFSET = OPEN_TIME_OFFSET + com.db4o.@internal.Const4
			.LONG_LENGTH;

		public const int TRANSACTION_OFFSET = MINIMUM_LENGTH;

		private const int BOOTRECORD_OFFSET = TRANSACTION_OFFSET + com.db4o.@internal.Const4
			.INT_LENGTH * 2;

		private const int INT_FORMERLY_KNOWN_AS_BLOCK_OFFSET = BOOTRECORD_OFFSET + com.db4o.@internal.Const4
			.INT_LENGTH;

		private const int ENCRYPTION_PASSWORD_LENGTH = 5;

		private const int PASSWORD_OFFSET = INT_FORMERLY_KNOWN_AS_BLOCK_OFFSET + ENCRYPTION_PASSWORD_LENGTH;

		private const int FREESPACE_SYSTEM_OFFSET = PASSWORD_OFFSET + 1;

		private const int FREESPACE_ADDRESS_OFFSET = FREESPACE_SYSTEM_OFFSET + com.db4o.@internal.Const4
			.INT_LENGTH;

		private const int CONVERTER_VERSION_OFFSET = FREESPACE_ADDRESS_OFFSET + com.db4o.@internal.Const4
			.INT_LENGTH;

		private const int UUID_INDEX_ID_OFFSET = CONVERTER_VERSION_OFFSET + com.db4o.@internal.Const4
			.INT_LENGTH;

		private const int LENGTH = MINIMUM_LENGTH + (com.db4o.@internal.Const4.INT_LENGTH
			 * 7) + ENCRYPTION_PASSWORD_LENGTH + 1;

		public static com.db4o.@internal.ConfigBlock ForNewFile(com.db4o.@internal.LocalObjectContainer
			 file)
		{
			return new com.db4o.@internal.ConfigBlock(file, true, 0);
		}

		public static com.db4o.@internal.ConfigBlock ForExistingFile(com.db4o.@internal.LocalObjectContainer
			 file, int address)
		{
			return new com.db4o.@internal.ConfigBlock(file, false, address);
		}

		private ConfigBlock(com.db4o.@internal.LocalObjectContainer stream, bool isNew, int
			 address)
		{
			_stream = stream;
			_timerFileLock = com.db4o.@internal.fileheader.TimerFileLock.ForFile(stream);
			TimerFileLock().WriteHeaderLock();
			if (!isNew)
			{
				Read(address);
			}
			TimerFileLock().Start();
		}

		private com.db4o.@internal.fileheader.TimerFileLock TimerFileLock()
		{
			return _timerFileLock;
		}

		public long OpenTime()
		{
			return TimerFileLock().OpenTime();
		}

		public com.db4o.@internal.Transaction GetTransactionToCommit()
		{
			return _transactionToCommit;
		}

		private bool LockFile()
		{
			return _stream.NeedsLockFileThread();
		}

		private byte[] PasswordToken()
		{
			byte[] pwdtoken = new byte[ENCRYPTION_PASSWORD_LENGTH];
			string fullpwd = _stream.ConfigImpl().Password();
			if (_stream.ConfigImpl().Encrypt() && fullpwd != null)
			{
				try
				{
					byte[] pwdbytes = new com.db4o.@internal.LatinStringIO().Write(fullpwd);
					com.db4o.@internal.Buffer encwriter = new com.db4o.@internal.StatefulBuffer(_stream
						.GetTransaction(), pwdbytes.Length + ENCRYPTION_PASSWORD_LENGTH);
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

		private com.db4o.@internal.SystemData SystemData()
		{
			return _stream.SystemData();
		}

		private void Read(int address)
		{
			AddressChanged(address);
			TimerFileLock().WriteOpenTime();
			com.db4o.@internal.StatefulBuffer reader = _stream.GetWriter(_stream.GetSystemTransaction
				(), _address, LENGTH);
			try
			{
				_stream.ReadBytes(reader._buffer, _address, LENGTH);
			}
			catch
			{
			}
			int oldLength = reader.ReadInt();
			if (oldLength > LENGTH || oldLength < MINIMUM_LENGTH)
			{
				com.db4o.@internal.Exceptions4.ThrowRuntimeException(com.db4o.@internal.Messages.
					INCOMPATIBLE_FORMAT);
			}
			if (oldLength != LENGTH)
			{
				if (!_stream.ConfigImpl().IsReadOnly() && !_stream.ConfigImpl().AllowVersionUpdates
					())
				{
					if (_stream.ConfigImpl().AutomaticShutDown())
					{
						com.db4o.@internal.Platform4.RemoveShutDownHook(_stream, _stream.i_lock);
					}
					throw new com.db4o.ext.OldFormatException();
				}
			}
			reader.ReadLong();
			long lastAccessTime = reader.ReadLong();
			SystemData().StringEncoding(reader.ReadByte());
			if (oldLength > TRANSACTION_OFFSET)
			{
				_transactionToCommit = com.db4o.@internal.Transaction.ReadInterruptedTransaction(
					_stream, reader);
			}
			if (oldLength > BOOTRECORD_OFFSET)
			{
				_bootRecordID = reader.ReadInt();
			}
			if (oldLength > INT_FORMERLY_KNOWN_AS_BLOCK_OFFSET)
			{
				reader.ReadInt();
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
			if (oldLength > FREESPACE_SYSTEM_OFFSET)
			{
				SystemData().FreespaceSystem(reader.ReadByte());
			}
			if (oldLength > FREESPACE_ADDRESS_OFFSET)
			{
				SystemData().FreespaceAddress(reader.ReadInt());
			}
			if (oldLength > CONVERTER_VERSION_OFFSET)
			{
				SystemData().ConverterVersion(reader.ReadInt());
			}
			if (oldLength > UUID_INDEX_ID_OFFSET)
			{
				int uuidIndexId = reader.ReadInt();
				if (0 != uuidIndexId)
				{
					SystemData().UuidIndexId(uuidIndexId);
				}
			}
			_stream.EnsureFreespaceSlot();
			if (LockFile() && (lastAccessTime != 0))
			{
				_stream.LogMsg(28, null);
				long waitTime = com.db4o.@internal.Const4.LOCK_TIME_INTERVAL * 5;
				long currentTime = j4o.lang.JavaSystem.CurrentTimeMillis();
				while (j4o.lang.JavaSystem.CurrentTimeMillis() < currentTime + waitTime)
				{
					com.db4o.foundation.Cool.SleepIgnoringInterruption(waitTime);
				}
				reader = _stream.GetWriter(_stream.GetSystemTransaction(), _address, com.db4o.@internal.Const4
					.LONG_LENGTH * 2);
				reader.MoveForward(OPEN_TIME_OFFSET);
				reader.Read();
				reader.ReadLong();
				long currentAccessTime = reader.ReadLong();
				if ((currentAccessTime > lastAccessTime))
				{
					throw new com.db4o.ext.DatabaseFileLockedException();
				}
			}
			if (LockFile())
			{
				com.db4o.foundation.Cool.SleepIgnoringInterruption(100);
				_stream.SyncFiles();
				TimerFileLock().CheckOpenTime();
			}
			if (oldLength < LENGTH)
			{
				Write();
			}
		}

		public void Write()
		{
			TimerFileLock().CheckHeaderLock();
			AddressChanged(_stream.GetSlot(LENGTH));
			com.db4o.@internal.StatefulBuffer writer = _stream.GetWriter(_stream.GetTransaction
				(), _address, LENGTH);
			com.db4o.@internal.handlers.IntHandler.WriteInt(LENGTH, writer);
			for (int i = 0; i < 2; i++)
			{
				writer.WriteLong(TimerFileLock().OpenTime());
			}
			writer.Append(SystemData().StringEncoding());
			com.db4o.@internal.handlers.IntHandler.WriteInt(0, writer);
			com.db4o.@internal.handlers.IntHandler.WriteInt(0, writer);
			com.db4o.@internal.handlers.IntHandler.WriteInt(_bootRecordID, writer);
			com.db4o.@internal.handlers.IntHandler.WriteInt(0, writer);
			writer.Append(PasswordToken());
			writer.Append(SystemData().FreespaceSystem());
			_stream.EnsureFreespaceSlot();
			com.db4o.@internal.handlers.IntHandler.WriteInt(SystemData().FreespaceAddress(), 
				writer);
			com.db4o.@internal.handlers.IntHandler.WriteInt(SystemData().ConverterVersion(), 
				writer);
			com.db4o.@internal.handlers.IntHandler.WriteInt(SystemData().UuidIndexId(), writer
				);
			writer.Write();
			WritePointer();
		}

		private void AddressChanged(int address)
		{
			_address = address;
			TimerFileLock().SetAddresses(_address, OPEN_TIME_OFFSET, ACCESS_TIME_OFFSET);
		}

		private void WritePointer()
		{
			TimerFileLock().CheckHeaderLock();
			com.db4o.@internal.StatefulBuffer writer = _stream.GetWriter(_stream.GetTransaction
				(), 0, com.db4o.@internal.Const4.ID_LENGTH);
			writer.MoveForward(2);
			com.db4o.@internal.handlers.IntHandler.WriteInt(_address, writer);
			writer.NoXByteCheck();
			writer.Write();
			TimerFileLock().WriteHeaderLock();
		}

		public int Address()
		{
			return _address;
		}

		public void Close()
		{
			TimerFileLock().Close();
		}
	}
}
