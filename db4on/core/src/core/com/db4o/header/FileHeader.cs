namespace com.db4o.header
{
	/// <exclude></exclude>
	public abstract class FileHeader
	{
		private static readonly com.db4o.header.FileHeader[] AVAILABLE_FILE_HEADERS = new 
			com.db4o.header.FileHeader[] { new com.db4o.header.FileHeader0(), new com.db4o.header.FileHeader1
			() };

		private static int ReaderLength()
		{
			int length = AVAILABLE_FILE_HEADERS[0].Length();
			for (int i = 1; i < AVAILABLE_FILE_HEADERS.Length; i++)
			{
				length = com.db4o.YInt.Max(length, AVAILABLE_FILE_HEADERS[i].Length());
			}
			return length;
		}

		public static com.db4o.header.FileHeader ReadFixedPart(com.db4o.YapFile file)
		{
			com.db4o.YapReader reader = PrepareFileHeaderReader(file);
			com.db4o.header.FileHeader header = DetectFileHeader(file, reader);
			if (header == null)
			{
				com.db4o.inside.Exceptions4.ThrowRuntimeException(17);
			}
			else
			{
				header.ReadFixedPart(file, reader);
			}
			return header;
		}

		private static com.db4o.YapReader PrepareFileHeaderReader(com.db4o.YapFile file)
		{
			com.db4o.YapReader reader = new com.db4o.YapReader(ReaderLength());
			reader.Read(file, 0, 0);
			return reader;
		}

		private static com.db4o.header.FileHeader DetectFileHeader(com.db4o.YapFile file, 
			com.db4o.YapReader reader)
		{
			for (int i = 0; i < AVAILABLE_FILE_HEADERS.Length; i++)
			{
				reader.Seek(0);
				com.db4o.header.FileHeader result = AVAILABLE_FILE_HEADERS[i].NewOnSignatureMatch
					(file, reader);
				if (result != null)
				{
					return result;
				}
			}
			return null;
		}

		public abstract void Close();

		public abstract void InitNew(com.db4o.YapFile file);

		public abstract com.db4o.Transaction InterruptedTransaction();

		public abstract int Length();

		protected abstract com.db4o.header.FileHeader NewOnSignatureMatch(com.db4o.YapFile
			 file, com.db4o.YapReader reader);

		protected virtual long TimeToWrite(long time, bool shuttingDown)
		{
			return shuttingDown ? 0 : time;
		}

		protected abstract void ReadFixedPart(com.db4o.YapFile file, com.db4o.YapReader reader
			);

		public abstract void ReadVariablePart(com.db4o.YapFile file);

		protected virtual bool SignatureMatches(com.db4o.YapReader reader, byte[] signature
			, byte version)
		{
			for (int i = 0; i < signature.Length; i++)
			{
				if (reader.ReadByte() != signature[i])
				{
					return false;
				}
			}
			return reader.ReadByte() == version;
		}

		public abstract void WriteFixedPart(com.db4o.YapFile file, bool shuttingDown, com.db4o.YapWriter
			 writer, int blockSize, int freespaceID);

		public abstract void WriteTransactionPointer(com.db4o.Transaction systemTransaction
			, int transactionAddress);

		protected virtual void WriteTransactionPointer(com.db4o.Transaction systemTransaction
			, int transactionAddress, int address, int offset)
		{
			com.db4o.YapWriter bytes = new com.db4o.YapWriter(systemTransaction, address, com.db4o.YapConst
				.INT_LENGTH * 2);
			bytes.MoveForward(offset);
			bytes.WriteInt(transactionAddress);
			bytes.WriteInt(transactionAddress);
			if (com.db4o.Debug.xbytes && com.db4o.Deploy.overwrite)
			{
				bytes.SetID(com.db4o.YapConst.IGNORE_ID);
			}
			bytes.Write();
		}

		public abstract void WriteVariablePart(com.db4o.YapFile file, int part);

		protected virtual void ReadClassCollectionAndFreeSpace(com.db4o.YapFile file, com.db4o.YapReader
			 reader)
		{
			com.db4o.inside.SystemData systemData = file.SystemData();
			systemData.ClassCollectionID(reader.ReadInt());
			systemData.FreespaceID(reader.ReadInt());
		}
	}
}
