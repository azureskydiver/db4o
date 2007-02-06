namespace com.db4o.@internal
{
	/// <exclude></exclude>
	public sealed class ReaderPair : com.db4o.@internal.SlotReader
	{
		private com.db4o.@internal.Buffer _source;

		private com.db4o.@internal.Buffer _target;

		private com.db4o.@internal.mapping.DefragContext _mapping;

		private com.db4o.@internal.Transaction _systemTrans;

		public ReaderPair(com.db4o.@internal.Buffer source, com.db4o.@internal.mapping.DefragContext
			 mapping, com.db4o.@internal.Transaction systemTrans)
		{
			_source = source;
			_mapping = mapping;
			_target = new com.db4o.@internal.Buffer(source.GetLength());
			_source.CopyTo(_target, 0, 0, _source.GetLength());
			_systemTrans = systemTrans;
		}

		public int Offset()
		{
			return _source.Offset();
		}

		public void Offset(int offset)
		{
			_source.Offset(offset);
			_target.Offset(offset);
		}

		public void IncrementOffset(int numBytes)
		{
			_source.IncrementOffset(numBytes);
			_target.IncrementOffset(numBytes);
		}

		public void IncrementIntSize()
		{
			IncrementOffset(com.db4o.@internal.Const4.INT_LENGTH);
		}

		public int CopyUnindexedID()
		{
			int orig = _source.ReadInt();
			int mapped = -1;
			try
			{
				mapped = _mapping.MappedID(orig);
			}
			catch (com.db4o.@internal.mapping.MappingNotFoundException)
			{
				mapped = _mapping.AllocateTargetSlot(com.db4o.@internal.Const4.POINTER_LENGTH);
				_mapping.MapIDs(orig, mapped, false);
				_mapping.RegisterUnindexed(orig);
			}
			_target.WriteInt(mapped);
			return mapped;
		}

		public int CopyID()
		{
			int mapped = _mapping.MappedID(_source.ReadInt(), false);
			_target.WriteInt(mapped);
			return mapped;
		}

		public int CopyID(bool flipNegative, bool lenient)
		{
			int id = _source.ReadInt();
			return InternalCopyID(flipNegative, lenient, id);
		}

		public com.db4o.@internal.mapping.MappedIDPair CopyIDAndRetrieveMapping()
		{
			int id = _source.ReadInt();
			return new com.db4o.@internal.mapping.MappedIDPair(id, InternalCopyID(false, false
				, id));
		}

		private int InternalCopyID(bool flipNegative, bool lenient, int id)
		{
			if (flipNegative && id < 0)
			{
				id = -id;
			}
			int mapped = _mapping.MappedID(id, lenient);
			if (flipNegative && id < 0)
			{
				mapped = -mapped;
			}
			_target.WriteInt(mapped);
			return mapped;
		}

		public void ReadBegin(byte identifier)
		{
			_source.ReadBegin(identifier);
			_target.ReadBegin(identifier);
		}

		public byte ReadByte()
		{
			byte value = _source.ReadByte();
			_target.IncrementOffset(1);
			return value;
		}

		public int ReadInt()
		{
			int value = _source.ReadInt();
			_target.IncrementOffset(com.db4o.@internal.Const4.INT_LENGTH);
			return value;
		}

		public void WriteInt(int value)
		{
			_source.IncrementOffset(com.db4o.@internal.Const4.INT_LENGTH);
			_target.WriteInt(value);
		}

		public void Write(com.db4o.@internal.LocalObjectContainer file, int address)
		{
			file.WriteBytes(_target, address, 0);
		}

		public string ReadShortString(com.db4o.@internal.LatinStringIO sio)
		{
			string value = com.db4o.@internal.marshall.StringMarshaller.ReadShort(sio, false, 
				_source);
			com.db4o.@internal.marshall.StringMarshaller.ReadShort(sio, false, _target);
			return value;
		}

		public com.db4o.@internal.Buffer Source()
		{
			return _source;
		}

		public com.db4o.@internal.Buffer Target()
		{
			return _target;
		}

		public com.db4o.@internal.mapping.IDMapping Mapping()
		{
			return _mapping;
		}

		public com.db4o.@internal.Transaction SystemTrans()
		{
			return _systemTrans;
		}

		public com.db4o.@internal.mapping.DefragContext Context()
		{
			return _mapping;
		}

		public static void ProcessCopy(com.db4o.@internal.mapping.DefragContext context, 
			int sourceID, com.db4o.@internal.SlotCopyHandler command)
		{
			ProcessCopy(context, sourceID, command, false);
		}

		public static void ProcessCopy(com.db4o.@internal.mapping.DefragContext context, 
			int sourceID, com.db4o.@internal.SlotCopyHandler command, bool registerAddressMapping
			)
		{
			com.db4o.@internal.Buffer sourceReader = (registerAddressMapping ? context.SourceWriterByID
				(sourceID) : context.SourceReaderByID(sourceID));
			int targetID = context.MappedID(sourceID);
			int targetLength = sourceReader.GetLength();
			int targetAddress = context.AllocateTargetSlot(targetLength);
			if (registerAddressMapping)
			{
				int sourceAddress = ((com.db4o.@internal.StatefulBuffer)sourceReader).GetAddress(
					);
				context.MapIDs(sourceAddress, targetAddress, false);
			}
			com.db4o.@internal.Buffer targetPointerReader = new com.db4o.@internal.Buffer(com.db4o.@internal.Const4
				.POINTER_LENGTH);
			targetPointerReader.WriteInt(targetAddress);
			targetPointerReader.WriteInt(targetLength);
			context.TargetWriteBytes(targetPointerReader, targetID);
			com.db4o.@internal.ReaderPair readers = new com.db4o.@internal.ReaderPair(sourceReader
				, context, context.SystemTrans());
			command.ProcessCopy(readers);
			context.TargetWriteBytes(readers, targetAddress);
		}

		public void Append(byte value)
		{
			_source.IncrementOffset(1);
			_target.Append(value);
		}

		public long ReadLong()
		{
			long value = _source.ReadLong();
			_target.IncrementOffset(com.db4o.@internal.Const4.LONG_LENGTH);
			return value;
		}

		public void WriteLong(long value)
		{
			_source.IncrementOffset(com.db4o.@internal.Const4.LONG_LENGTH);
			_target.WriteLong(value);
		}

		public com.db4o.foundation.BitMap4 ReadBitMap(int bitCount)
		{
			com.db4o.foundation.BitMap4 value = _source.ReadBitMap(bitCount);
			_target.IncrementOffset(value.MarshalledLength());
			return value;
		}

		public void CopyBytes(byte[] target, int sourceOffset, int targetOffset, int length
			)
		{
			_source.CopyBytes(target, sourceOffset, targetOffset, length);
		}

		public void ReadEnd()
		{
			_source.ReadEnd();
			_target.ReadEnd();
		}

		public int PreparePayloadRead()
		{
			int newPayLoadOffset = ReadInt();
			ReadInt();
			int linkOffSet = Offset();
			Offset(newPayLoadOffset);
			return linkOffSet;
		}
	}
}
