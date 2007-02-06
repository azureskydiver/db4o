namespace com.db4o.@internal.fileheader
{
	/// <exclude></exclude>
	public abstract class FileHeader
	{
		private static readonly com.db4o.@internal.fileheader.FileHeader[] AVAILABLE_FILE_HEADERS
			 = new com.db4o.@internal.fileheader.FileHeader[] { new com.db4o.@internal.fileheader.FileHeader0
			(), new com.db4o.@internal.fileheader.FileHeader1() };

		private static int ReaderLength()
		{
			int length = AVAILABLE_FILE_HEADERS[0].Length();
			for (int i = 1; i < AVAILABLE_FILE_HEADERS.Length; i++)
			{
				length = System.Math.Max(length, AVAILABLE_FILE_HEADERS[i].Length());
			}
			return length;
		}

		public static com.db4o.@internal.fileheader.FileHeader ReadFixedPart(com.db4o.@internal.LocalObjectContainer
			 file)
		{
			com.db4o.@internal.Buffer reader = PrepareFileHeaderReader(file);
			com.db4o.@internal.fileheader.FileHeader header = DetectFileHeader(file, reader);
			if (header == null)
			{
				com.db4o.@internal.Exceptions4.ThrowRuntimeException(com.db4o.@internal.Messages.
					INCOMPATIBLE_FORMAT);
			}
			else
			{
				header.ReadFixedPart(file, reader);
			}
			return header;
		}

		private static com.db4o.@internal.Buffer PrepareFileHeaderReader(com.db4o.@internal.LocalObjectContainer
			 file)
		{
			com.db4o.@internal.Buffer reader = new com.db4o.@internal.Buffer(ReaderLength());
			reader.Read(file, 0, 0);
			return reader;
		}

		private static com.db4o.@internal.fileheader.FileHeader DetectFileHeader(com.db4o.@internal.LocalObjectContainer
			 file, com.db4o.@internal.Buffer reader)
		{
			for (int i = 0; i < AVAILABLE_FILE_HEADERS.Length; i++)
			{
				reader.Seek(0);
				com.db4o.@internal.fileheader.FileHeader result = AVAILABLE_FILE_HEADERS[i].NewOnSignatureMatch
					(file, reader);
				if (result != null)
				{
					return result;
				}
			}
			return null;
		}

		public abstract void Close();

		public abstract void InitNew(com.db4o.@internal.LocalObjectContainer file);

		public abstract com.db4o.@internal.Transaction InterruptedTransaction();

		public abstract int Length();

		protected abstract com.db4o.@internal.fileheader.FileHeader NewOnSignatureMatch(com.db4o.@internal.LocalObjectContainer
			 file, com.db4o.@internal.Buffer reader);

		protected virtual long TimeToWrite(long time, bool shuttingDown)
		{
			return shuttingDown ? 0 : time;
		}

		protected abstract void ReadFixedPart(com.db4o.@internal.LocalObjectContainer file
			, com.db4o.@internal.Buffer reader);

		public abstract void ReadVariablePart(com.db4o.@internal.LocalObjectContainer file
			);

		protected virtual bool SignatureMatches(com.db4o.@internal.Buffer reader, byte[] 
			signature, byte version)
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

		public abstract void WriteFixedPart(com.db4o.@internal.LocalObjectContainer file, 
			bool shuttingDown, com.db4o.@internal.StatefulBuffer writer, int blockSize, int 
			freespaceID);

		public abstract void WriteTransactionPointer(com.db4o.@internal.Transaction systemTransaction
			, int transactionAddress);

		protected virtual void WriteTransactionPointer(com.db4o.@internal.Transaction systemTransaction
			, int transactionAddress, int address, int offset)
		{
			com.db4o.@internal.StatefulBuffer bytes = new com.db4o.@internal.StatefulBuffer(systemTransaction
				, address, com.db4o.@internal.Const4.INT_LENGTH * 2);
			bytes.MoveForward(offset);
			bytes.WriteInt(transactionAddress);
			bytes.WriteInt(transactionAddress);
			if (com.db4o.Debug.xbytes && com.db4o.Deploy.overwrite)
			{
				bytes.SetID(com.db4o.@internal.Const4.IGNORE_ID);
			}
			bytes.Write();
		}

		public abstract void WriteVariablePart(com.db4o.@internal.LocalObjectContainer file
			, int part);

		protected virtual void ReadClassCollectionAndFreeSpace(com.db4o.@internal.LocalObjectContainer
			 file, com.db4o.@internal.Buffer reader)
		{
			com.db4o.@internal.SystemData systemData = file.SystemData();
			systemData.ClassCollectionID(reader.ReadInt());
			systemData.FreespaceID(reader.ReadInt());
		}
	}
}
