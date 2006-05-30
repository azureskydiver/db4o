namespace com.db4o
{
	/// <exclude></exclude>
	public class YapRandomAccessFile : com.db4o.YapFile
	{
		private com.db4o.Session i_session;

		private com.db4o.io.IoAdapter i_file;

		private com.db4o.io.IoAdapter i_timerFile;

		private volatile com.db4o.io.IoAdapter i_backupFile;

		private byte[] i_timerBytes = new byte[com.db4o.YapConst.LONG_BYTES];

		private object i_fileLock;

		internal YapRandomAccessFile(com.db4o.Session a_session) : base(null)
		{
			lock (i_lock)
			{
				i_fileLock = new object();
				i_session = a_session;
				try
				{
					Open();
				}
				catch (com.db4o.ext.DatabaseFileLockedException e)
				{
					StopSession();
					throw e;
				}
				Initialize3();
			}
		}

		public override void Backup(string path)
		{
			lock (i_lock)
			{
				CheckClosed();
				if (i_backupFile != null)
				{
					com.db4o.inside.Exceptions4.ThrowRuntimeException(61);
				}
				try
				{
					i_backupFile = i_config.IoAdapter().Open(path, true, i_file.GetLength());
				}
				catch (System.Exception e)
				{
					i_backupFile = null;
					com.db4o.inside.Exceptions4.ThrowRuntimeException(12, path);
				}
			}
			long pos = 0;
			int bufferlength = 8192;
			byte[] buffer = new byte[bufferlength];
			do
			{
				lock (i_lock)
				{
					i_file.Seek(pos);
					int read = i_file.Read(buffer);
					i_backupFile.Seek(pos);
					i_backupFile.Write(buffer, read);
					pos += read;
				}
			}
			while (pos < i_file.GetLength());
			lock (i_lock)
			{
				i_backupFile.Close();
				i_backupFile = null;
			}
		}

		internal override void BlockSize(int blockSize)
		{
			i_file.BlockSize(blockSize);
			if (i_timerFile != null)
			{
				i_timerFile.BlockSize(blockSize);
			}
		}

		public override byte BlockSize()
		{
			return (byte)i_file.BlockSize();
		}

		internal override bool Close2()
		{
			bool stopSession = true;
			lock (com.db4o.Db4o.Lock)
			{
				stopSession = i_session.CloseInstance();
				if (stopSession)
				{
					FreePrefetchedPointers();
					i_entryCounter++;
					try
					{
						Write(true);
					}
					catch (System.Exception t)
					{
						FatalException(t);
					}
					base.Close2();
					i_entryCounter--;
					com.db4o.Db4o.SessionStopped(i_session);
					lock (i_fileLock)
					{
						try
						{
							i_file.Close();
							i_file = null;
							if (NeedsLockFileThread() && com.db4o.Debug.lockFile)
							{
								com.db4o.YapWriter lockBytes = new com.db4o.YapWriter(i_systemTrans, com.db4o.YapConst
									.YAPLONG_LENGTH);
								com.db4o.YLong.WriteLong(0, lockBytes);
								i_timerFile.BlockSeek(_configBlock._address, com.db4o.YapConfigBlock.ACCESS_TIME_OFFSET
									);
								i_timerFile.Write(lockBytes._buffer);
								i_timerFile.Close();
							}
						}
						catch (System.Exception e)
						{
							i_file = null;
							com.db4o.inside.Exceptions4.ThrowRuntimeException(11, e);
						}
						i_file = null;
					}
				}
			}
			return stopSession;
		}

		internal override void Commit1()
		{
			EnsureLastSlotWritten();
			base.Commit1();
		}

		public override void Copy(int oldAddress, int oldAddressOffset, int newAddress, int
			 newAddressOffset, int length)
		{
			if (com.db4o.Debug.xbytes && com.db4o.Deploy.overwrite)
			{
				CheckXBytes(newAddress, newAddressOffset, length);
			}
			try
			{
				if (i_backupFile == null)
				{
					i_file.BlockCopy(oldAddress, oldAddressOffset, newAddress, newAddressOffset, length
						);
					return;
				}
				byte[] copyBytes = new byte[length];
				i_file.BlockSeek(oldAddress, oldAddressOffset);
				i_file.Read(copyBytes);
				i_file.BlockSeek(newAddress, newAddressOffset);
				i_file.Write(copyBytes);
				if (i_backupFile != null)
				{
					i_backupFile.BlockSeek(newAddress, newAddressOffset);
					i_backupFile.Write(copyBytes);
				}
			}
			catch (System.Exception e)
			{
				com.db4o.inside.Exceptions4.ThrowRuntimeException(16, e);
			}
		}

		private void CheckXBytes(int a_newAddress, int newAddressOffset, int a_length)
		{
			if (com.db4o.Debug.xbytes && com.db4o.Deploy.overwrite)
			{
				try
				{
					byte[] checkXBytes = new byte[a_length];
					i_file.BlockSeek(a_newAddress, newAddressOffset);
					i_file.Read(checkXBytes);
					for (int i = 0; i < checkXBytes.Length; i++)
					{
						if (checkXBytes[i] != com.db4o.YapConst.XBYTE)
						{
							string msg = "XByte corruption adress:" + a_newAddress + " length:" + a_length;
							throw new j4o.lang.RuntimeException(msg);
						}
					}
				}
				catch (System.Exception e)
				{
					j4o.lang.JavaSystem.PrintStackTrace(e);
				}
			}
		}

		internal override void EmergencyClose()
		{
			base.EmergencyClose();
			try
			{
				i_file.Close();
			}
			catch (System.Exception e)
			{
			}
			try
			{
				com.db4o.Db4o.SessionStopped(i_session);
			}
			catch (System.Exception e)
			{
			}
			i_file = null;
		}

		internal override long FileLength()
		{
			try
			{
				return i_file.GetLength();
			}
			catch (System.Exception e)
			{
				throw new j4o.lang.RuntimeException();
			}
		}

		internal override string FileName()
		{
			return i_session.FileName();
		}

		private void Open()
		{
			bool isNew = false;
			com.db4o.io.IoAdapter ioAdapter = i_config.IoAdapter();
			try
			{
				if (FileName().Length > 0)
				{
					if (!ioAdapter.Exists(FileName()))
					{
						isNew = true;
						LogMsg(14, FileName());
					}
					try
					{
						bool lockFile = com.db4o.Debug.lockFile && i_config.LockFile() && (!i_config.IsReadOnly
							());
						i_file = ioAdapter.Open(FileName(), lockFile, 0);
						if (NeedsLockFileThread() && com.db4o.Debug.lockFile)
						{
							i_timerFile = ioAdapter.Open(FileName(), false, 0);
						}
					}
					catch (com.db4o.ext.DatabaseFileLockedException de)
					{
						throw de;
					}
					catch (System.Exception e)
					{
						com.db4o.inside.Exceptions4.ThrowRuntimeException(12, FileName(), e);
					}
					if (isNew)
					{
						ConfigureNewFile();
						if (i_config.ReservedStorageSpace() > 0)
						{
							Reserve(i_config.ReservedStorageSpace());
						}
						Write(false);
						WriteHeader(false);
					}
					else
					{
						ReadThis();
					}
				}
				else
				{
					com.db4o.inside.Exceptions4.ThrowRuntimeException(21);
				}
			}
			catch (System.Exception exc)
			{
				if (i_references != null)
				{
					i_references.StopTimer();
				}
				throw exc;
			}
		}

		internal override void ReadBytes(byte[] bytes, int address, int length)
		{
			ReadBytes(bytes, address, 0, length);
		}

		internal override void ReadBytes(byte[] bytes, int address, int addressOffset, int
			 length)
		{
			try
			{
				i_file.BlockSeek(address, addressOffset);
				i_file.Read(bytes, length);
			}
			catch (System.IO.IOException ioex)
			{
				throw new j4o.lang.RuntimeException();
			}
		}

		internal override void Reserve(int byteCount)
		{
			lock (i_lock)
			{
				int address = GetSlot(byteCount);
				WriteBytes(new com.db4o.YapReader(byteCount), address, 0);
				Free(address, byteCount);
			}
		}

		public override void SyncFiles()
		{
			try
			{
				i_file.Sync();
				if (NeedsLockFileThread() && com.db4o.Debug.lockFile)
				{
					i_timerFile.Sync();
				}
			}
			catch (System.Exception e)
			{
			}
		}

		internal override bool WriteAccessTime()
		{
			if (!NeedsLockFileThread())
			{
				return true;
			}
			lock (i_fileLock)
			{
				if (i_file == null)
				{
					return false;
				}
				long lockTime = j4o.lang.JavaSystem.CurrentTimeMillis();
				com.db4o.YLong.WriteLong(lockTime, i_timerBytes);
				i_timerFile.BlockSeek(_configBlock._address, com.db4o.YapConfigBlock.ACCESS_TIME_OFFSET
					);
				i_timerFile.Write(i_timerBytes);
			}
			return true;
		}

		internal override void WriteBytes(com.db4o.YapReader a_bytes, int address, int addressOffset
			)
		{
			if (i_config.IsReadOnly())
			{
				return;
			}
			if (com.db4o.Deploy.debug && !com.db4o.Deploy.flush)
			{
				return;
			}
			try
			{
				if (com.db4o.Debug.xbytes && com.db4o.Deploy.overwrite)
				{
					bool doCheck = true;
					if (a_bytes is com.db4o.YapWriter)
					{
						com.db4o.YapWriter writer = (com.db4o.YapWriter)a_bytes;
						if (writer.GetID() == com.db4o.YapConst.IGNORE_ID)
						{
							doCheck = false;
						}
					}
					if (doCheck)
					{
						CheckXBytes(address, addressOffset, a_bytes.GetLength());
					}
				}
				i_file.BlockSeek(address, addressOffset);
				i_file.Write(a_bytes._buffer, a_bytes.GetLength());
				if (i_backupFile != null)
				{
					i_backupFile.BlockSeek(address, addressOffset);
					i_backupFile.Write(a_bytes._buffer, a_bytes.GetLength());
				}
			}
			catch (System.Exception e)
			{
				com.db4o.inside.Exceptions4.ThrowRuntimeException(16, e);
			}
		}

		public override void WriteXBytes(int a_address, int a_length)
		{
		}
	}
}
