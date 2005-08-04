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
	internal sealed class YapConfigBlock : j4o.lang.Runnable
	{
		private readonly object _timeWriterLock = new object();

		private readonly com.db4o.YapFile _stream;

		internal int _address;

		private com.db4o.Transaction _transactionToCommit;

		internal int _bootRecordID;

		private const int POINTER_ADDRESS = 2;

		private const int MINIMUM_LENGTH = com.db4o.YapConst.YAPINT_LENGTH + (com.db4o.YapConst
			.YAPLONG_LENGTH * 2) + 1;

		internal const int OPEN_TIME_OFFSET = com.db4o.YapConst.YAPINT_LENGTH;

		internal const int ACCESS_TIME_OFFSET = OPEN_TIME_OFFSET + com.db4o.YapConst.YAPLONG_LENGTH;

		internal const int TRANSACTION_OFFSET = MINIMUM_LENGTH;

		private const int BOOTRECORD_OFFSET = TRANSACTION_OFFSET + com.db4o.YapConst.YAPINT_LENGTH
			 * 2;

		private const int INT_FORMERLY_KNOWN_AS_BLOCK_OFFSET = BOOTRECORD_OFFSET + com.db4o.YapConst
			.YAPINT_LENGTH;

		private const int ENCRYPTION_PASSWORD_LENGTH = 5;

		private const int PASSWORD_OFFSET = INT_FORMERLY_KNOWN_AS_BLOCK_OFFSET + ENCRYPTION_PASSWORD_LENGTH;

		private const int LENGTH = MINIMUM_LENGTH + (com.db4o.YapConst.YAPINT_LENGTH * 4)
			 + ENCRYPTION_PASSWORD_LENGTH;

		private readonly long _opentime;

		internal byte _encoding;

		internal YapConfigBlock(com.db4o.YapFile stream)
		{
			_stream = stream;
			_opentime = processID();
			if (lockFile())
			{
				writeHeaderLock();
			}
		}

		internal YapConfigBlock(com.db4o.YapFile file, byte encoding) : this(file)
		{
			_encoding = encoding;
		}

		private com.db4o.YapWriter openTimeIO()
		{
			com.db4o.YapWriter writer = _stream.getWriter(_stream.getTransaction(), _address, 
				com.db4o.YapConst.YAPLONG_LENGTH);
			writer.moveForward(OPEN_TIME_OFFSET);
			return writer;
		}

		private void openTimeOverWritten()
		{
			if (lockFile())
			{
				com.db4o.YapWriter bytes = openTimeIO();
				bytes.read();
				if (com.db4o.YLong.readLong(bytes) != _opentime)
				{
					com.db4o.inside.Exceptions.throwRuntimeException(22);
				}
				writeOpenTime();
			}
		}

		internal com.db4o.Transaction getTransactionToCommit()
		{
			return _transactionToCommit;
		}

		internal void go()
		{
			_stream.createStringIO(_encoding);
			if (lockFile())
			{
				try
				{
					writeAccessTime();
				}
				catch (System.Exception e)
				{
				}
				syncFiles();
				openTimeOverWritten();
				new j4o.lang.Thread(this).start();
			}
		}

		private com.db4o.YapWriter headerLockIO()
		{
			com.db4o.YapWriter writer = _stream.getWriter(_stream.getTransaction(), 0, com.db4o.YapConst
				.YAPINT_LENGTH);
			writer.moveForward(2 + com.db4o.YapConst.YAPINT_LENGTH);
			return writer;
		}

		private void headerLockOverwritten()
		{
			if (lockFile())
			{
				com.db4o.YapWriter bytes = headerLockIO();
				bytes.read();
				if (com.db4o.YInt.readInt(bytes) != ((int)_opentime))
				{
					throw new com.db4o.ext.DatabaseFileLockedException();
				}
				writeHeaderLock();
			}
		}

		private bool lockFile()
		{
			return _stream.needsLockFileThread();
		}

		internal static long processID()
		{
			long id = j4o.lang.JavaSystem.currentTimeMillis();
			return id;
		}

		/// <summary>returns true if Unicode check is necessary</summary>
		internal bool read(com.db4o.YapWriter reader)
		{
			_address = reader.readInt();
			if (_address == 2)
			{
				return true;
			}
			read();
			return false;
		}

		internal void read()
		{
			writeOpenTime();
			com.db4o.YapWriter reader = _stream.getWriter(_stream.getSystemTransaction(), _address
				, LENGTH);
			try
			{
				_stream.readBytes(reader._buffer, _address, LENGTH);
			}
			catch (System.Exception e)
			{
			}
			int oldLength = reader.readInt();
			if (oldLength > LENGTH || oldLength < MINIMUM_LENGTH)
			{
				com.db4o.inside.Exceptions.throwRuntimeException(17);
			}
			long lastOpenTime = com.db4o.YLong.readLong(reader);
			long lastAccessTime = com.db4o.YLong.readLong(reader);
			_encoding = reader.readByte();
			if (oldLength > TRANSACTION_OFFSET)
			{
				int transactionID1 = com.db4o.YInt.readInt(reader);
				int transactionID2 = com.db4o.YInt.readInt(reader);
				if ((transactionID1 > 0) && (transactionID1 == transactionID2))
				{
					_transactionToCommit = new com.db4o.Transaction(_stream, null);
					_transactionToCommit.setAddress(transactionID1);
				}
			}
			if (oldLength > BOOTRECORD_OFFSET)
			{
				_bootRecordID = com.db4o.YInt.readInt(reader);
			}
			if (oldLength > INT_FORMERLY_KNOWN_AS_BLOCK_OFFSET)
			{
				com.db4o.YInt.readInt(reader);
			}
			if (oldLength > PASSWORD_OFFSET)
			{
				byte[] encpassword = reader.readBytes(ENCRYPTION_PASSWORD_LENGTH);
				byte[] storedpwd = passwordToken();
				for (int idx = 0; idx < storedpwd.Length; idx++)
				{
					if (storedpwd[idx] != encpassword[idx])
					{
						_stream.fatalException(54);
					}
				}
			}
			if (lockFile() && (lastAccessTime != 0))
			{
				_stream.logMsg(28, null);
				long waitTime = com.db4o.YapConst.LOCK_TIME_INTERVAL * 10;
				long currentTime = j4o.lang.JavaSystem.currentTimeMillis();
				while (j4o.lang.JavaSystem.currentTimeMillis() < currentTime + waitTime)
				{
					com.db4o.foundation.Cool.sleepIgnoringInterruption(waitTime);
				}
				reader = _stream.getWriter(_stream.getSystemTransaction(), _address, com.db4o.YapConst
					.YAPLONG_LENGTH * 2);
				reader.moveForward(OPEN_TIME_OFFSET);
				reader.read();
				long currentOpenTime = com.db4o.YLong.readLong(reader);
				long currentAccessTime = com.db4o.YLong.readLong(reader);
				if ((currentAccessTime > lastAccessTime))
				{
					throw new com.db4o.ext.DatabaseFileLockedException();
				}
			}
			if (lockFile())
			{
				com.db4o.foundation.Cool.sleepIgnoringInterruption(100);
				syncFiles();
				openTimeOverWritten();
			}
			if (oldLength < LENGTH)
			{
				write();
			}
			go();
		}

		public void run()
		{
		}

		internal void syncFiles()
		{
			_stream.syncFiles();
		}

		internal void write()
		{
			headerLockOverwritten();
			_address = _stream.getSlot(LENGTH);
			com.db4o.YapWriter writer = _stream.getWriter(_stream.i_trans, _address, LENGTH);
			com.db4o.YInt.writeInt(LENGTH, writer);
			com.db4o.YLong.writeLong(_opentime, writer);
			com.db4o.YLong.writeLong(_opentime, writer);
			writer.append(_encoding);
			com.db4o.YInt.writeInt(0, writer);
			com.db4o.YInt.writeInt(0, writer);
			com.db4o.YInt.writeInt(_bootRecordID, writer);
			com.db4o.YInt.writeInt(0, writer);
			writer.append(passwordToken());
			writer.write();
			writePointer();
		}

		private byte[] passwordToken()
		{
			byte[] pwdtoken = new byte[ENCRYPTION_PASSWORD_LENGTH];
			string fullpwd = _stream.i_config.i_password;
			if (_stream.i_config.i_encrypt && fullpwd != null)
			{
				try
				{
					byte[] pwdbytes = new com.db4o.YapStringIO().write(fullpwd);
					com.db4o.YapWriter encwriter = new com.db4o.YapWriter(_stream.i_trans, pwdbytes.Length
						 + ENCRYPTION_PASSWORD_LENGTH);
					encwriter.append(pwdbytes);
					encwriter.append(new byte[ENCRYPTION_PASSWORD_LENGTH]);
					_stream.i_handlers.decrypt(encwriter);
					j4o.lang.JavaSystem.arraycopy(encwriter._buffer, 0, pwdtoken, 0, ENCRYPTION_PASSWORD_LENGTH
						);
				}
				catch (System.Exception exc)
				{
					j4o.lang.JavaSystem.printStackTrace(exc);
				}
			}
			return pwdtoken;
		}

		internal bool writeAccessTime()
		{
			return _stream.writeAccessTime();
		}

		private void writeOpenTime()
		{
			if (lockFile())
			{
				com.db4o.YapWriter writer = openTimeIO();
				com.db4o.YLong.writeLong(_opentime, writer);
				writer.write();
			}
		}

		private void writeHeaderLock()
		{
			if (lockFile())
			{
				com.db4o.YapWriter writer = headerLockIO();
				com.db4o.YInt.writeInt(((int)_opentime), writer);
				writer.write();
			}
		}

		private void writePointer()
		{
			headerLockOverwritten();
			com.db4o.YapWriter writer = _stream.getWriter(_stream.i_trans, 0, com.db4o.YapConst
				.YAPID_LENGTH);
			writer.moveForward(2);
			com.db4o.YInt.writeInt(_address, writer);
			if (com.db4o.Deploy.debug && com.db4o.Deploy.overwrite)
			{
				writer.setID(com.db4o.YapConst.IGNORE_ID);
			}
			writer.write();
			writeHeaderLock();
		}
	}
}
