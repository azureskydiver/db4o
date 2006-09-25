namespace com.db4o.header
{
	/// <exclude></exclude>
	public class FileHeader0 : com.db4o.header.FileHeader
	{
		private const int LENGTH = 2 + (com.db4o.YapConst.INT_LENGTH * 4);

		protected com.db4o.YapConfigBlock _configBlock;

		private byte blockSize = 1;

		internal com.db4o.PBootRecord _bootRecord;

		private com.db4o.inside.SystemData _systemData;

		public FileHeader0()
		{
			_systemData = new com.db4o.inside.SystemData(this);
		}

		public virtual void Read(com.db4o.YapFile file)
		{
			com.db4o.YapReader reader = new com.db4o.YapReader(Length());
			reader.Read(file, 0, 0);
			byte firstFileByte = reader.ReadByte();
			if (firstFileByte != com.db4o.YapConst.YAPBEGIN)
			{
				if (firstFileByte != com.db4o.YapConst.YAPFILEVERSION)
				{
					com.db4o.inside.Exceptions4.ThrowRuntimeException(17);
				}
				blockSize = reader.ReadByte();
			}
			else
			{
				if (reader.ReadByte() != com.db4o.YapConst.YAPFILE)
				{
					com.db4o.inside.Exceptions4.ThrowRuntimeException(17);
				}
			}
			file.BlockSize(blockSize);
			file.SetRegularEndAddress(file.FileLength());
			_configBlock = new com.db4o.YapConfigBlock(file);
			_configBlock.Read(reader.ReadInt());
			SkipConfigurationLockTime(reader);
			_systemData.ClassCollectionID(reader.ReadInt());
			_systemData.FreeSpaceID(reader.ReadInt());
			_systemData.UuidIndexId(_configBlock._uuidIndexId);
		}

		private void SkipConfigurationLockTime(com.db4o.YapReader reader)
		{
			reader.IncrementOffset(com.db4o.YapConst.ID_LENGTH);
		}

		public virtual void ReadBootRecord(com.db4o.YapFile yapFile)
		{
			if (_configBlock._bootRecordID <= 0)
			{
				return;
			}
			yapFile.ShowInternalClasses(true);
			object bootRecord = yapFile.GetByID1(yapFile.GetSystemTransaction(), _configBlock
				._bootRecordID);
			yapFile.ShowInternalClasses(false);
			if (!(bootRecord is com.db4o.PBootRecord))
			{
				InitBootRecord(yapFile);
				yapFile.GenerateNewIdentity();
				return;
			}
			_bootRecord = (com.db4o.PBootRecord)bootRecord;
			_bootRecord.i_stream = yapFile;
			yapFile.Activate(bootRecord, int.MaxValue);
			yapFile.SetNextTimeStampId(_bootRecord.i_versionGenerator);
			yapFile.SetIdentity(_bootRecord.i_db);
		}

		public virtual byte FreespaceSystem()
		{
			return _configBlock._freespaceSystem;
		}

		public virtual void InitNew(com.db4o.YapFile yf)
		{
			_configBlock = new com.db4o.YapConfigBlock(yf);
			_configBlock.ConverterVersion(com.db4o.inside.convert.Converter.VERSION);
			_configBlock.Write();
			_configBlock.Go();
			InitBootRecord(yf);
		}

		private void InitBootRecord(com.db4o.YapFile yf)
		{
			yf.ShowInternalClasses(true);
			_bootRecord = new com.db4o.PBootRecord();
			_bootRecord.i_stream = yf;
			_bootRecord.Init();
			yf.SetInternal(yf.GetSystemTransaction(), _bootRecord, false);
			_configBlock._bootRecordID = yf.GetID1(yf.GetSystemTransaction(), _bootRecord);
			_configBlock.Write();
			yf.ShowInternalClasses(false);
		}

		public virtual int FreespaceAddress()
		{
			return _configBlock._freespaceAddress;
		}

		public virtual int NewFreespaceSlot(byte freeSpaceSystem)
		{
			return _configBlock.NewFreespaceSlot(freeSpaceSystem);
		}

		public virtual void WriteVariablePart2()
		{
			_bootRecord.SetDirty();
			_bootRecord.Store(2);
		}

		public virtual com.db4o.Transaction InterruptedTransaction()
		{
			return _configBlock.GetTransactionToCommit();
		}

		public virtual int ConverterVersion()
		{
			return _configBlock.ConverterVersion();
		}

		public virtual void ConverterVersion(int version)
		{
			_configBlock.ConverterVersion(version);
		}

		public virtual int TransactionPointerAddress()
		{
			return _configBlock._address;
		}

		public virtual void WriteTransactionPointer(com.db4o.Transaction trans, int address
			)
		{
			com.db4o.YapWriter bytes = new com.db4o.YapWriter(trans, _configBlock._address, com.db4o.YapConst
				.INT_LENGTH * 2);
			bytes.MoveForward(com.db4o.YapConfigBlock.TRANSACTION_OFFSET);
			bytes.WriteInt(address);
			bytes.WriteInt(address);
			if (com.db4o.Debug.xbytes && com.db4o.Deploy.overwrite)
			{
				bytes.SetID(com.db4o.YapConst.IGNORE_ID);
			}
			bytes.Write();
		}

		public virtual void SeekForTimeLock(com.db4o.io.IoAdapter file)
		{
			file.BlockSeek(_configBlock._address, com.db4o.YapConfigBlock.ACCESS_TIME_OFFSET);
		}

		public virtual void WriteFixedPart(bool shuttingDown, com.db4o.YapWriter writer, 
			byte blockSize_, int classCollectionID, int freespaceID)
		{
			writer.Append(com.db4o.YapConst.YAPFILEVERSION);
			writer.Append(blockSize_);
			writer.WriteInt(_configBlock._address);
			int headerLockOpenTime = shuttingDown ? 0 : (int)_configBlock._opentime;
			writer.WriteInt(headerLockOpenTime);
			writer.WriteInt(classCollectionID);
			writer.WriteInt(freespaceID);
			if (com.db4o.Debug.xbytes && com.db4o.Deploy.overwrite)
			{
				writer.SetID(com.db4o.YapConst.IGNORE_ID);
			}
			writer.Write();
		}

		public virtual com.db4o.MetaIndex GetUUIDMetaIndex()
		{
			return _bootRecord.GetUUIDMetaIndex();
		}

		public virtual void SetLastTimeStampID(long val)
		{
			_bootRecord.i_versionGenerator = val;
		}

		public virtual void SetIdentity(com.db4o.ext.Db4oDatabase database)
		{
			_bootRecord.i_db = database;
		}

		public virtual int Length()
		{
			return LENGTH;
		}

		public override com.db4o.inside.SystemData SystemData()
		{
			return _systemData;
		}

		public override void VariablePartChanged()
		{
			_configBlock._uuidIndexId = _systemData.UuidIndexId();
			_configBlock.Write();
		}
	}
}
