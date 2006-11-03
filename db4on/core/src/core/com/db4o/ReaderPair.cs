namespace com.db4o
{
	/// <exclude></exclude>
	public class ReaderPair : com.db4o.SlotReader
	{
		private com.db4o.YapReader _source;

		private com.db4o.YapReader _target;

		private com.db4o.inside.mapping.DefragContext _mapping;

		private com.db4o.Transaction _systemTrans;

		public ReaderPair(com.db4o.YapReader source, com.db4o.inside.mapping.DefragContext
			 mapping, com.db4o.Transaction systemTrans)
		{
			_source = source;
			_mapping = mapping;
			_target = new com.db4o.YapReader(source.GetLength());
			_source.CopyTo(_target, 0, 0, _source.GetLength());
			_systemTrans = systemTrans;
		}

		public virtual int Offset()
		{
			return _source.Offset();
		}

		public virtual void Offset(int offset)
		{
			_source.Offset(offset);
			_target.Offset(offset);
		}

		public virtual void IncrementOffset(int numBytes)
		{
			_source.IncrementOffset(numBytes);
			_target.IncrementOffset(numBytes);
		}

		public virtual void IncrementIntSize()
		{
			IncrementIntSize(1);
		}

		public virtual void IncrementIntSize(int times)
		{
			IncrementOffset(times * com.db4o.YapConst.INT_LENGTH);
		}

		public virtual int CopyID()
		{
			return CopyID(false, false);
		}

		public virtual int CopyID(bool flipNegative, bool lenient)
		{
			int id = _source.ReadInt();
			return InternalCopyID(flipNegative, lenient, id);
		}

		public virtual com.db4o.inside.mapping.MappedIDPair CopyIDAndRetrieveMapping()
		{
			int id = _source.ReadInt();
			return new com.db4o.inside.mapping.MappedIDPair(id, InternalCopyID(false, false, 
				id));
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

		public virtual void ReadBegin(byte identifier)
		{
			_source.ReadBegin(identifier);
			_target.ReadBegin(identifier);
		}

		public virtual byte ReadByte()
		{
			byte value = _source.ReadByte();
			_target.IncrementOffset(1);
			return value;
		}

		public virtual int ReadInt()
		{
			int value = _source.ReadInt();
			_target.IncrementOffset(com.db4o.YapConst.INT_LENGTH);
			return value;
		}

		public virtual void WriteInt(int value)
		{
			_source.IncrementOffset(com.db4o.YapConst.INT_LENGTH);
			_target.WriteInt(value);
		}

		public virtual void Write(com.db4o.YapFile file, int address)
		{
			file.WriteBytes(_target, address, 0);
		}

		public virtual string ReadShortString(com.db4o.YapStringIO sio)
		{
			string value = com.db4o.inside.marshall.StringMarshaller.ReadShort(sio, false, _source
				);
			com.db4o.inside.marshall.StringMarshaller.ReadShort(sio, false, _target);
			return value;
		}

		public virtual com.db4o.YapReader Source()
		{
			return _source;
		}

		public virtual com.db4o.YapReader Target()
		{
			return _target;
		}

		public virtual com.db4o.inside.mapping.IDMapping Mapping()
		{
			return _mapping;
		}

		public virtual com.db4o.Transaction SystemTrans()
		{
			return _systemTrans;
		}

		public virtual com.db4o.inside.mapping.DefragContext Context()
		{
			return _mapping;
		}

		public static void ProcessCopy(com.db4o.inside.mapping.DefragContext context, int
			 sourceID, com.db4o.SlotCopyHandler command)
		{
			ProcessCopy(context, sourceID, command, false, false);
		}

		public static void ProcessCopy(com.db4o.inside.mapping.DefragContext context, int
			 sourceID, com.db4o.SlotCopyHandler command, bool registerAddressMapping, bool registerSeen
			)
		{
			com.db4o.YapReader sourceReader = (registerAddressMapping ? context.SourceWriterByID
				(sourceID) : context.SourceReaderByID(sourceID));
			int targetID = context.MappedID(sourceID);
			int targetLength = sourceReader.GetLength();
			int targetAddress = context.AllocateTargetSlot(targetLength);
			if (registerAddressMapping)
			{
				int sourceAddress = ((com.db4o.YapWriter)sourceReader).GetAddress();
				context.MapIDs(sourceAddress, targetAddress, false, false);
			}
			com.db4o.YapReader targetPointerReader = new com.db4o.YapReader(com.db4o.YapConst
				.POINTER_LENGTH);
			targetPointerReader.WriteInt(targetAddress);
			targetPointerReader.WriteInt(targetLength);
			context.TargetWriteBytes(targetPointerReader, targetID);
			com.db4o.ReaderPair readers = new com.db4o.ReaderPair(sourceReader, context, context
				.SystemTrans());
			command.ProcessCopy(readers);
			context.TargetWriteBytes(readers, targetAddress);
			if (registerSeen)
			{
				context.RegisterSeen(sourceID);
			}
		}

		public virtual void Append(byte value)
		{
			_source.IncrementOffset(1);
			_target.Append(value);
		}

		public virtual long ReadLong()
		{
			long value = _source.ReadLong();
			_target.IncrementOffset(com.db4o.YapConst.LONG_LENGTH);
			return value;
		}

		public virtual void WriteLong(long value)
		{
			_source.IncrementOffset(com.db4o.YapConst.LONG_LENGTH);
			_target.WriteLong(value);
		}

		public virtual com.db4o.foundation.BitMap4 ReadBitMap(int bitCount)
		{
			com.db4o.foundation.BitMap4 value = _source.ReadBitMap(bitCount);
			_target.IncrementOffset(value.MarshalledLength());
			return value;
		}

		public virtual void CopyBytes(byte[] target, int sourceOffset, int targetOffset, 
			int length)
		{
			_source.CopyBytes(target, sourceOffset, targetOffset, length);
		}

		public virtual void ReadEnd()
		{
			_source.ReadEnd();
			_target.ReadEnd();
		}

		public virtual int PreparePayloadRead()
		{
			int newPayLoadOffset = ReadInt();
			ReadInt();
			int linkOffSet = Offset();
			Offset(newPayLoadOffset);
			return linkOffSet;
		}
	}
}
