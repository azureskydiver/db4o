namespace com.db4o.header
{
	/// <exclude></exclude>
	public class FileHeader0 : com.db4o.header.FileHeader
	{
		internal const int LENGTH = 2 + (com.db4o.YapConst.INT_LENGTH * 4);

		private com.db4o.YapConfigBlock _configBlock;

		private com.db4o.PBootRecord _bootRecord;

		public override void Close()
		{
			_configBlock.Close();
		}

		protected override com.db4o.header.FileHeader NewOnSignatureMatch(com.db4o.YapFile
			 file, com.db4o.YapReader reader)
		{
			byte firstFileByte = reader.ReadByte();
			if (firstFileByte != com.db4o.YapConst.YAPBEGIN)
			{
				if (firstFileByte != com.db4o.YapConst.YAPFILEVERSION)
				{
					return null;
				}
				file.BlockSizeReadFromFile(reader.ReadByte());
			}
			else
			{
				if (reader.ReadByte() != com.db4o.YapConst.YAPFILE)
				{
					return null;
				}
			}
			return new com.db4o.header.FileHeader0();
		}

		protected override void ReadFixedPart(com.db4o.YapFile file, com.db4o.YapReader reader
			)
		{
			_configBlock = com.db4o.YapConfigBlock.ForExistingFile(file, reader.ReadInt());
			SkipConfigurationLockTime(reader);
			ReadClassCollectionAndFreeSpace(file, reader);
		}

		private void SkipConfigurationLockTime(com.db4o.YapReader reader)
		{
			reader.IncrementOffset(com.db4o.YapConst.ID_LENGTH);
		}

		public override void ReadVariablePart(com.db4o.YapFile file)
		{
			if (_configBlock._bootRecordID <= 0)
			{
				return;
			}
			file.ShowInternalClasses(true);
			object bootRecord = file.GetByID1(file.GetSystemTransaction(), _configBlock._bootRecordID
				);
			file.ShowInternalClasses(false);
			if (!(bootRecord is com.db4o.PBootRecord))
			{
				InitBootRecord(file);
				file.GenerateNewIdentity();
				return;
			}
			_bootRecord = (com.db4o.PBootRecord)bootRecord;
			file.Activate(bootRecord, int.MaxValue);
			file.SetNextTimeStampId(_bootRecord.i_versionGenerator);
			file.SystemData().Identity(_bootRecord.i_db);
		}

		public override void InitNew(com.db4o.YapFile file)
		{
			_configBlock = com.db4o.YapConfigBlock.ForNewFile(file);
			InitBootRecord(file);
		}

		private void InitBootRecord(com.db4o.YapFile file)
		{
			file.ShowInternalClasses(true);
			_bootRecord = new com.db4o.PBootRecord();
			file.SetInternal(file.GetSystemTransaction(), _bootRecord, false);
			_configBlock._bootRecordID = file.GetID1(file.GetSystemTransaction(), _bootRecord
				);
			WriteVariablePart(file, 1);
			file.ShowInternalClasses(false);
		}

		public override com.db4o.Transaction InterruptedTransaction()
		{
			return _configBlock.GetTransactionToCommit();
		}

		public override void WriteTransactionPointer(com.db4o.Transaction systemTransaction
			, int transactionAddress)
		{
			WriteTransactionPointer(systemTransaction, transactionAddress, _configBlock.Address
				(), com.db4o.YapConfigBlock.TRANSACTION_OFFSET);
		}

		public virtual com.db4o.MetaIndex GetUUIDMetaIndex()
		{
			return _bootRecord.GetUUIDMetaIndex();
		}

		public override int Length()
		{
			return LENGTH;
		}

		public override void WriteFixedPart(com.db4o.YapFile file, bool shuttingDown, com.db4o.YapWriter
			 writer, int blockSize_, int freespaceID)
		{
			writer.Append(com.db4o.YapConst.YAPFILEVERSION);
			writer.Append((byte)blockSize_);
			writer.WriteInt(_configBlock.Address());
			writer.WriteInt((int)TimeToWrite(_configBlock.OpenTime(), shuttingDown));
			writer.WriteInt(file.SystemData().ClassCollectionID());
			writer.WriteInt(freespaceID);
			if (com.db4o.Debug.xbytes && com.db4o.Deploy.overwrite)
			{
				writer.SetID(com.db4o.YapConst.IGNORE_ID);
			}
			writer.Write();
		}

		public override void WriteVariablePart(com.db4o.YapFile file, int part)
		{
			if (part == 1)
			{
				_configBlock.Write();
			}
			else
			{
				if (part == 2)
				{
					_bootRecord.Write(file);
				}
			}
		}
	}
}
