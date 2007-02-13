namespace com.db4o.@internal
{
	/// <exclude></exclude>
	public class IoAdaptedObjectContainer : com.db4o.@internal.LocalObjectContainer
	{
		private com.db4o.@internal.Session i_session;

		private com.db4o.io.IoAdapter i_file;

		private com.db4o.io.IoAdapter i_timerFile;

		private volatile com.db4o.io.IoAdapter i_backupFile;

		private byte[] i_timerBytes = new byte[com.db4o.@internal.Const4.LONG_BYTES];

		private object i_fileLock;

		internal IoAdaptedObjectContainer(com.db4o.config.Configuration config, com.db4o.@internal.Session
			 a_session) : base(config, null)
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
					throw;
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
					com.db4o.@internal.Exceptions4.ThrowRuntimeException(61);
				}
				try
				{
					i_backupFile = ConfigImpl().IoAdapter().Open(path, true, i_file.GetLength());
					i_backupFile.BlockSize(BlockSize());
				}
				catch
				{
					i_backupFile = null;
					com.db4o.@internal.Exceptions4.ThrowRuntimeException(12, path);
				}
			}
			long pos = 0;
			int bufferlength = 8192;
			byte[] buffer = new byte[bufferlength];
			while (true)
			{
				lock (i_lock)
				{
					i_file.Seek(pos);
					int read = i_file.Read(buffer);
					if (read <= 0)
					{
						break;
					}
					i_backupFile.Seek(pos);
					i_backupFile.Write(buffer, read);
					pos += read;
				}
				try
				{
					j4o.lang.Thread.Sleep(1);
				}
				catch
				{
				}
			}
			lock (i_lock)
			{
				i_backupFile.Close();
				i_backupFile = null;
			}
		}

		public override void BlockSize(int size)
		{
			i_file.BlockSize(size);
			if (i_timerFile != null)
			{
				i_timerFile.BlockSize(size);
			}
		}

		public override byte BlockSize()
		{
			return (byte)i_file.BlockSize();
		}

		protected override bool Close2()
		{
			bool stopSession = true;
			lock (com.db4o.@internal.Global4.Lock)
			{
				stopSession = i_session.CloseInstance();
				if (stopSession)
				{
					FreePrefetchedPointers();
					try
					{
						Write(true);
					}
					catch (System.Exception t)
					{
						FatalException(t);
					}
					base.Close2();
					com.db4o.@internal.Sessions.SessionStopped(i_session);
					lock (i_fileLock)
					{
						try
						{
							i_file.Close();
							i_file = null;
							_fileHeader.Close();
							CloseTimerFile();
						}
						catch (System.Exception e)
						{
							i_file = null;
							com.db4o.@internal.Exceptions4.ThrowRuntimeException(11, e);
						}
						i_file = null;
					}
				}
			}
			return stopSession;
		}

		public override void Commit1()
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
				com.db4o.@internal.Exceptions4.ThrowRuntimeException(16, e);
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
						if (checkXBytes[i] != com.db4o.@internal.Const4.XBYTE)
						{
							string msg = "XByte corruption adress:" + a_newAddress + " length:" + a_length;
							throw new System.Exception(msg);
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
			catch
			{
			}
			try
			{
				com.db4o.@internal.Sessions.SessionStopped(i_session);
			}
			catch
			{
			}
			i_file = null;
		}

		public override long FileLength()
		{
			try
			{
				return i_file.GetLength();
			}
			catch
			{
				throw new System.Exception();
			}
		}

		internal override string FileName()
		{
			return i_session.FileName();
		}

		private void Open()
		{
			bool isNew = false;
			com.db4o.io.IoAdapter ioAdapter = ConfigImpl().IoAdapter();
			try
			{
				if (FileName().Length > 0)
				{
					if (!ioAdapter.Exists(FileName()))
					{
						isNew = true;
						LogMsg(14, FileName());
						i_handlers.OldEncryptionOff();
					}
					try
					{
						bool lockFile = com.db4o.Debug.lockFile && ConfigImpl().LockFile() && (!ConfigImpl
							().IsReadOnly());
						i_file = ioAdapter.Open(FileName(), lockFile, 0);
						if (NeedsTimerFile())
						{
							i_timerFile = ioAdapter.Open(FileName(), false, 0);
						}
					}
					catch (com.db4o.ext.DatabaseFileLockedException de)
					{
						throw;
					}
					catch (System.Exception e)
					{
						com.db4o.@internal.Exceptions4.ThrowRuntimeException(12, FileName(), e);
					}
					if (isNew)
					{
						ConfigureNewFile();
						if (ConfigImpl().ReservedStorageSpace() > 0)
						{
							Reserve(ConfigImpl().ReservedStorageSpace());
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
					com.db4o.@internal.Exceptions4.ThrowRuntimeException(21);
				}
			}
			catch (System.Exception exc)
			{
				if (i_references != null)
				{
					i_references.StopTimer();
				}
				throw;
			}
		}

		public override void ReadBytes(byte[] bytes, int address, int length)
		{
			ReadBytes(bytes, address, 0, length);
		}

		public override void ReadBytes(byte[] bytes, int address, int addressOffset, int 
			length)
		{
			try
			{
				i_file.BlockSeek(address, addressOffset);
				int bytesRead = i_file.Read(bytes, length);
				if (bytesRead != length)
				{
					com.db4o.@internal.Exceptions4.ThrowRuntimeException(68, address + "/" + addressOffset
						, null, false);
				}
			}
			catch (System.IO.IOException ioex)
			{
				throw new System.Exception();
			}
		}

		internal override void Reserve(int byteCount)
		{
			lock (i_lock)
			{
				int address = GetSlot(byteCount);
				ZeroReservedStorage(address, byteCount);
				Free(address, byteCount);
			}
		}

		private void ZeroReservedStorage(int address, int length)
		{
			if (ConfigImpl().IsReadOnly())
			{
				return;
			}
			try
			{
				ZeroFile(i_file, address, length);
				ZeroFile(i_backupFile, address, length);
			}
			catch (System.IO.IOException e)
			{
				com.db4o.@internal.Exceptions4.ThrowRuntimeException(16, e);
			}
		}

		private void ZeroFile(com.db4o.io.IoAdapter io, int address, int length)
		{
			if (io == null)
			{
				return;
			}
			byte[] zeroBytes = new byte[1024];
			int left = length;
			io.BlockSeek(address, 0);
			while (left > zeroBytes.Length)
			{
				io.Write(zeroBytes, zeroBytes.Length);
				left -= zeroBytes.Length;
			}
			if (left > 0)
			{
				io.Write(zeroBytes, left);
			}
		}

		public override void SyncFiles()
		{
			try
			{
				i_file.Sync();
				if (i_timerFile != null)
				{
					i_timerFile.Sync();
				}
			}
			catch
			{
			}
		}

		private bool NeedsTimerFile()
		{
			return NeedsLockFileThread() && com.db4o.Debug.lockFile;
		}

		public override bool WriteAccessTime(int address, int offset, long time)
		{
			lock (i_fileLock)
			{
				if (i_timerFile == null)
				{
					return false;
				}
				i_timerFile.BlockSeek(address, offset);
				com.db4o.foundation.PrimitiveCodec.WriteLong(i_timerBytes, time);
				i_timerFile.Write(i_timerBytes);
				if (i_file == null)
				{
					CloseTimerFile();
					return false;
				}
				return true;
			}
		}

		private void CloseTimerFile()
		{
			if (i_timerFile == null)
			{
				return;
			}
			i_timerFile.Close();
			i_timerFile = null;
		}

		public override void WriteBytes(com.db4o.@internal.Buffer a_bytes, int address, int
			 addressOffset)
		{
			if (ConfigImpl().IsReadOnly())
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
					if (a_bytes is com.db4o.@internal.StatefulBuffer)
					{
						com.db4o.@internal.StatefulBuffer writer = (com.db4o.@internal.StatefulBuffer)a_bytes;
						if (writer.GetID() == com.db4o.@internal.Const4.IGNORE_ID)
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
				com.db4o.@internal.Exceptions4.ThrowRuntimeException(16, e);
			}
		}

		public override void DebugWriteXBytes(int a_address, int a_length)
		{
		}

		public virtual void WriteXBytes(int a_address, int a_length)
		{
			if (!ConfigImpl().IsReadOnly())
			{
				if (a_address > 0 && a_length > 0)
				{
					try
					{
						i_file.BlockSeek(a_address);
						i_file.Write(XBytes(a_address, a_length)._buffer, a_length);
					}
					catch (System.Exception e)
					{
						j4o.lang.JavaSystem.PrintStackTrace(e);
					}
				}
			}
		}
	}
}
