namespace com.db4o.header
{
	/// <exclude></exclude>
	public class FileHeaderVariablePart1 : com.db4o.YapMeta
	{
		private const int LENGTH = 1 + (com.db4o.YapConst.INT_LENGTH * 4) + com.db4o.YapConst
			.LONG_LENGTH + com.db4o.YapConst.ADDED_LENGTH;

		private readonly com.db4o.inside.SystemData _systemData;

		public FileHeaderVariablePart1(int id, com.db4o.inside.SystemData systemData)
		{
			SetID(id);
			_systemData = systemData;
		}

		internal virtual com.db4o.inside.SystemData SystemData()
		{
			return _systemData;
		}

		public override byte GetIdentifier()
		{
			return com.db4o.YapConst.HEADER;
		}

		public override int OwnLength()
		{
			return LENGTH;
		}

		public override void ReadThis(com.db4o.Transaction trans, com.db4o.YapReader reader
			)
		{
			_systemData.ConverterVersion(reader.ReadInt());
			_systemData.FreespaceSystem(reader.ReadByte());
			_systemData.FreespaceAddress(reader.ReadInt());
			ReadIdentity(trans, reader.ReadInt());
			_systemData.LastTimeStampID(reader.ReadLong());
			_systemData.UuidIndexId(reader.ReadInt());
		}

		public override void WriteThis(com.db4o.Transaction trans, com.db4o.YapReader writer
			)
		{
			writer.WriteInt(_systemData.ConverterVersion());
			writer.Append(_systemData.FreespaceSystem());
			writer.WriteInt(_systemData.FreespaceAddress());
			writer.WriteInt(_systemData.Identity().GetID(trans));
			writer.WriteLong(_systemData.LastTimeStampID());
			writer.WriteInt(_systemData.UuidIndexId());
		}

		private void ReadIdentity(com.db4o.Transaction trans, int identityID)
		{
			com.db4o.YapFile file = trans.i_file;
			com.db4o.ext.Db4oDatabase identity = (com.db4o.ext.Db4oDatabase)file.GetByID1(trans
				, identityID);
			file.Activate1(trans, identity, 2);
			_systemData.Identity(identity);
		}
	}
}
