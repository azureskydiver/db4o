namespace com.db4o.@internal.fileheader
{
	/// <exclude></exclude>
	public class FileHeader1 : com.db4o.@internal.fileheader.FileHeader
	{
		private static readonly byte[] SIGNATURE = { (byte)'d', (byte)'b', (byte)'4', (byte
			)'o' };

		private static byte VERSION = 1;

		private static readonly int HEADER_LOCK_OFFSET = SIGNATURE.Length + 1;

		private static readonly int OPEN_TIME_OFFSET = HEADER_LOCK_OFFSET + com.db4o.@internal.Const4
			.INT_LENGTH;

		private static readonly int ACCESS_TIME_OFFSET = OPEN_TIME_OFFSET + com.db4o.@internal.Const4
			.LONG_LENGTH;

		private static readonly int TRANSACTION_POINTER_OFFSET = ACCESS_TIME_OFFSET + com.db4o.@internal.Const4
			.LONG_LENGTH;

		internal static readonly int LENGTH = TRANSACTION_POINTER_OFFSET + (com.db4o.@internal.Const4
			.INT_LENGTH * 6);

		private com.db4o.@internal.fileheader.TimerFileLock _timerFileLock;

		private com.db4o.@internal.Transaction _interruptedTransaction;

		private com.db4o.@internal.fileheader.FileHeaderVariablePart1 _variablePart;

		public override void Close()
		{
			_timerFileLock.Close();
		}

		public override void InitNew(com.db4o.@internal.LocalObjectContainer file)
		{
			CommonTasksForNewAndRead(file);
			_variablePart = new com.db4o.@internal.fileheader.FileHeaderVariablePart1(0, file
				.SystemData());
			WriteVariablePart(file, 0);
		}

		protected override com.db4o.@internal.fileheader.FileHeader NewOnSignatureMatch(com.db4o.@internal.LocalObjectContainer
			 file, com.db4o.@internal.Buffer reader)
		{
			if (SignatureMatches(reader, SIGNATURE, VERSION))
			{
				return new com.db4o.@internal.fileheader.FileHeader1();
			}
			return null;
		}

		private void NewTimerFileLock(com.db4o.@internal.LocalObjectContainer file)
		{
			_timerFileLock = com.db4o.@internal.fileheader.TimerFileLock.ForFile(file);
		}

		public override com.db4o.@internal.Transaction InterruptedTransaction()
		{
			return _interruptedTransaction;
		}

		public override int Length()
		{
			return LENGTH;
		}

		protected override void ReadFixedPart(com.db4o.@internal.LocalObjectContainer file
			, com.db4o.@internal.Buffer reader)
		{
			CommonTasksForNewAndRead(file);
			reader.Seek(TRANSACTION_POINTER_OFFSET);
			_interruptedTransaction = com.db4o.@internal.Transaction.ReadInterruptedTransaction
				(file, reader);
			file.BlockSizeReadFromFile(reader.ReadInt());
			ReadClassCollectionAndFreeSpace(file, reader);
			_variablePart = new com.db4o.@internal.fileheader.FileHeaderVariablePart1(reader.
				ReadInt(), file.SystemData());
		}

		private void CommonTasksForNewAndRead(com.db4o.@internal.LocalObjectContainer file
			)
		{
			NewTimerFileLock(file);
			file.i_handlers.OldEncryptionOff();
		}

		public override void ReadVariablePart(com.db4o.@internal.LocalObjectContainer file
			)
		{
			_variablePart.Read(file.GetSystemTransaction());
		}

		public override void WriteFixedPart(com.db4o.@internal.LocalObjectContainer file, 
			bool shuttingDown, com.db4o.@internal.StatefulBuffer writer, int blockSize, int 
			freespaceID)
		{
			writer.Append(SIGNATURE);
			writer.Append(VERSION);
			writer.WriteInt((int)TimeToWrite(_timerFileLock.OpenTime(), shuttingDown));
			writer.WriteLong(TimeToWrite(_timerFileLock.OpenTime(), shuttingDown));
			writer.WriteLong(TimeToWrite(j4o.lang.JavaSystem.CurrentTimeMillis(), shuttingDown
				));
			writer.WriteInt(0);
			writer.WriteInt(0);
			writer.WriteInt(blockSize);
			writer.WriteInt(file.SystemData().ClassCollectionID());
			writer.WriteInt(freespaceID);
			writer.WriteInt(_variablePart.GetID());
			writer.NoXByteCheck();
			writer.Write();
		}

		public override void WriteTransactionPointer(com.db4o.@internal.Transaction systemTransaction
			, int transactionAddress)
		{
			WriteTransactionPointer(systemTransaction, transactionAddress, 0, TRANSACTION_POINTER_OFFSET
				);
		}

		public override void WriteVariablePart(com.db4o.@internal.LocalObjectContainer file
			, int part)
		{
			_variablePart.SetStateDirty();
			_variablePart.Write(file.GetSystemTransaction());
		}
	}
}
