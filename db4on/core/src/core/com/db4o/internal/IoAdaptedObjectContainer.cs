namespace com.db4o.@internal
{
	/// <exclude></exclude>
	public class IoAdaptedObjectContainer : com.db4o.@internal.LocalObjectContainer
	{
		private readonly string _fileName;

		private com.db4o.io.IoAdapter _file;

		private com.db4o.io.IoAdapter _timerFile;

		private volatile com.db4o.io.IoAdapter _backupFile;

		private object _fileLock;

		private readonly com.db4o.config.FreespaceFiller _freespaceFiller;

		internal IoAdaptedObjectContainer(com.db4o.config.Configuration config, string fileName
			) : base(config, null)
		{
			lock (i_lock)
			{
				_fileLock = new object();
				_fileName = fileName;
				_freespaceFiller = CreateFreespaceFiller();
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
				if (_backupFile != null)
				{
					com.db4o.@internal.Exceptions4.ThrowRuntimeException(61);
				}
				try
				{
					_backupFile = ConfigImpl().IoAdapter().Open(path, true, _file.GetLength());
					_backupFile.BlockSize(BlockSize());
				}
				catch
				{
					_backupFile = null;
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
					_file.Seek(pos);
					int read = _file.Read(buffer);
					if (read <= 0)
					{
						break;
					}
					_backupFile.Seek(pos);
					_backupFile.Write(buffer, read);
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
				_backupFile.Close();
				_backupFile = null;
			}
		}

		public override void BlockSize(int size)
		{
			_file.BlockSize(size);
			if (_timerFile != null)
			{
				_timerFile.BlockSize(size);
			}
		}

		public override byte BlockSize()
		{
			return (byte)_file.BlockSize();
		}

		protected override void Close2()
		{
			FreePrefetchedPointers();
			Write(true);
			base.Close2();
			lock (_fileLock)
			{
				try
				{
					_file.Close();
					_file = null;
					_fileHeader.Close();
					CloseTimerFile();
				}
				catch (System.Exception e)
				{
					_file = null;
					com.db4o.@internal.Exceptions4.ThrowRuntimeException(11, e);
				}
				_file = null;
			}
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
				if (_backupFile == null)
				{
					_file.BlockCopy(oldAddress, oldAddressOffset, newAddress, newAddressOffset, length
						);
					return;
				}
				byte[] copyBytes = new byte[length];
				_file.BlockSeek(oldAddress, oldAddressOffset);
				_file.Read(copyBytes);
				_file.BlockSeek(newAddress, newAddressOffset);
				_file.Write(copyBytes);
				if (_backupFile != null)
				{
					_backupFile.BlockSeek(newAddress, newAddressOffset);
					_backupFile.Write(copyBytes);
				}
			}
			catch (System.Exception e)
			{
				com.db4o.@internal.Exceptions4.ThrowRuntimeException(16, e);
			}
		}

		private void CheckXBytes(int newAddress, int newAddressOffset, int length)
		{
			if (com.db4o.Debug.xbytes && com.db4o.Deploy.overwrite)
			{
				try
				{
					byte[] checkXBytes = new byte[length];
					_file.BlockSeek(newAddress, newAddressOffset);
					_file.Read(checkXBytes);
					for (int i = 0; i < checkXBytes.Length; i++)
					{
						if (checkXBytes[i] != com.db4o.@internal.Const4.XBYTE)
						{
							string msg = "XByte corruption adress:" + newAddress + " length:" + length;
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
				_file.Close();
			}
			catch
			{
			}
			_file = null;
		}

		public override long FileLength()
		{
			try
			{
				return _file.GetLength();
			}
			catch
			{
				throw new System.Exception();
			}
		}

		public override string FileName()
		{
			return _fileName;
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
						_file = ioAdapter.Open(FileName(), lockFile, 0);
						if (NeedsTimerFile())
						{
							_timerFile = ioAdapter.Open(FileName(), false, 0);
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
						WriteHeader(true, false);
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
				_file.BlockSeek(address, addressOffset);
				int bytesRead = _file.Read(bytes, length);
				AssertRead(bytesRead, length);
			}
			catch (System.IO.IOException ioex)
			{
				throw new com.db4o.io.UncheckedIOException(ioex);
			}
		}

		private void AssertRead(int bytesRead, int expected)
		{
			if (bytesRead != expected)
			{
				throw new com.db4o.io.UncheckedIOException("expected = " + expected + ", read = "
					 + bytesRead);
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
				ZeroFile(_file, address, length);
				ZeroFile(_backupFile, address, length);
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
				_file.Sync();
				if (_timerFile != null)
				{
					_timerFile.Sync();
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

		private void CloseTimerFile()
		{
			if (_timerFile == null)
			{
				return;
			}
			_timerFile.Close();
			_timerFile = null;
		}

		public override void WriteBytes(com.db4o.@internal.Buffer bytes, int address, int
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
					if (bytes is com.db4o.@internal.StatefulBuffer)
					{
						com.db4o.@internal.StatefulBuffer writer = (com.db4o.@internal.StatefulBuffer)bytes;
						if (writer.GetID() == com.db4o.@internal.Const4.IGNORE_ID)
						{
							doCheck = false;
						}
					}
					if (doCheck)
					{
						CheckXBytes(address, addressOffset, bytes.GetLength());
					}
				}
				_file.BlockSeek(address, addressOffset);
				_file.Write(bytes._buffer, bytes.GetLength());
				if (_backupFile != null)
				{
					_backupFile.BlockSeek(address, addressOffset);
					_backupFile.Write(bytes._buffer, bytes.GetLength());
				}
			}
			catch (System.Exception e)
			{
				com.db4o.@internal.Exceptions4.ThrowRuntimeException(16, e);
			}
		}

		public override void OverwriteDeletedBytes(int address, int length)
		{
			if (!ConfigImpl().IsReadOnly() && _freespaceFiller != null)
			{
				if (address > 0 && length > 0)
				{
					com.db4o.io.IoAdapterWindow window = new com.db4o.io.IoAdapterWindow(_file, address
						, length);
					try
					{
						CreateFreespaceFiller().Fill(window);
					}
					catch (System.Exception e)
					{
						j4o.lang.JavaSystem.PrintStackTrace(e);
					}
					finally
					{
						window.Disable();
					}
				}
			}
		}

		public virtual com.db4o.io.IoAdapter TimerFile()
		{
			return _timerFile;
		}

		private com.db4o.config.FreespaceFiller CreateFreespaceFiller()
		{
			com.db4o.config.FreespaceFiller freespaceFiller = Config().FreespaceFiller();
			return freespaceFiller;
		}

		private class XByteFreespaceFiller : com.db4o.config.FreespaceFiller
		{
			public virtual void Fill(com.db4o.io.IoAdapterWindow io)
			{
				io.Write(0, XBytes(io.Length()));
			}

			private byte[] XBytes(int len)
			{
				byte[] bytes = new byte[len];
				for (int i = 0; i < len; i++)
				{
					bytes[i] = com.db4o.@internal.Const4.XBYTE;
				}
				return bytes;
			}
		}
	}
}
