/* Copyright (C) 2004 - 2009  Versant Inc.  http://www.db4o.com */

using System;
using Db4objects.Db4o;
using Db4objects.Db4o.Internal;
using Db4objects.Db4o.Internal.Fileheader;
using Sharpen.Lang;

namespace Db4objects.Db4o.Internal.Fileheader
{
	/// <exclude></exclude>
	public class FileHeader0 : FileHeader
	{
		internal const int HeaderLength = 2 + (Const4.IntLength * 4);

		private ConfigBlock _configBlock;

		private PBootRecord _bootRecord;

		public FileHeader0(LocalObjectContainer container)
		{
		}

		// The header format is:
		// Old format
		// -------------------------
		// {
		// Y
		// [Rest]
		// New format
		// -------------------------
		// (byte)4
		// block size in bytes 1 to 127
		// [Rest]
		// Rest (only ints)
		// -------------------
		// address of the extended configuration block, see YapConfigBlock
		// headerLock
		// YapClassCollection ID
		// FreeBySize ID
		/// <exception cref="Db4objects.Db4o.Ext.Db4oIOException"></exception>
		public override void Close()
		{
			_configBlock.Close();
		}

		protected override FileHeader NewOnSignatureMatch(LocalObjectContainer file, ByteArrayBuffer
			 reader)
		{
			byte firstFileByte = reader.ReadByte();
			if (firstFileByte != Const4.Yapbegin)
			{
				if (firstFileByte != Const4.Yapfileversion)
				{
					return null;
				}
				file.BlockSizeReadFromFile(reader.ReadByte());
			}
			else
			{
				if (reader.ReadByte() != Const4.Yapfile)
				{
					return null;
				}
			}
			return new Db4objects.Db4o.Internal.Fileheader.FileHeader0(file);
		}

		/// <exception cref="Db4objects.Db4o.Ext.OldFormatException"></exception>
		protected override void Read(LocalObjectContainer file, ByteArrayBuffer reader)
		{
			_configBlock = ConfigBlock.ForExistingFile(file, reader.ReadInt());
			reader.IncrementOffset(Const4.IdLength);
			SystemData systemData = file.SystemData();
			systemData.ClassCollectionID(reader.ReadInt());
			reader.ReadInt();
		}

		// was freespace ID, can no longer be read
		private object GetBootRecord(LocalObjectContainer file)
		{
			file.ShowInternalClasses(true);
			try
			{
				return file.GetByID(file.SystemTransaction(), _configBlock._bootRecordID);
			}
			finally
			{
				file.ShowInternalClasses(false);
			}
		}

		/// <exception cref="Db4objects.Db4o.Ext.Db4oIOException"></exception>
		public override void InitNew(LocalObjectContainer file)
		{
			throw new InvalidOperationException();
		}

		public override void CompleteInterruptedTransaction(LocalObjectContainer container
			)
		{
			_configBlock.CompleteInterruptedTransaction();
		}

		public override void WriteTransactionPointer(Transaction systemTransaction, int transactionPointer
			)
		{
			WriteTransactionPointer(systemTransaction, transactionPointer, _configBlock.Address
				(), ConfigBlock.TransactionOffset);
		}

		public virtual MetaIndex GetUUIDMetaIndex()
		{
			return _bootRecord.GetUUIDMetaIndex();
		}

		public override int Length()
		{
			return HeaderLength;
		}

		public override void WriteFixedPart(LocalObjectContainer file, bool startFileLockingThread
			, bool shuttingDown, StatefulBuffer writer, int blockSize_)
		{
			throw new InvalidOperationException();
		}

		public override void WriteVariablePart(LocalObjectContainer file, bool shuttingDown
			)
		{
			throw new InvalidOperationException();
		}

		public override void ReadIdentity(LocalObjectContainer container)
		{
			if (_configBlock._bootRecordID <= 0)
			{
				return;
			}
			object bootRecord = GetBootRecord(container);
			if (!(bootRecord is PBootRecord))
			{
				return;
			}
			_bootRecord = (PBootRecord)bootRecord;
			container.Activate(bootRecord, int.MaxValue);
			container.SetNextTimeStampId(_bootRecord.i_versionGenerator);
			container.SystemData().Identity(_bootRecord.i_db);
		}

		public override IRunnable Commit(bool shuttingDown)
		{
			throw new InvalidOperationException();
		}
	}
}
