namespace com.db4o.@internal.fileheader
{
	/// <exclude></exclude>
	public class FileHeaderVariablePart1 : com.db4o.@internal.PersistentBase
	{
		private const int LENGTH = 1 + (com.db4o.@internal.Const4.INT_LENGTH * 4) + com.db4o.@internal.Const4
			.LONG_LENGTH + com.db4o.@internal.Const4.ADDED_LENGTH;

		private readonly com.db4o.@internal.SystemData _systemData;

		public FileHeaderVariablePart1(int id, com.db4o.@internal.SystemData systemData)
		{
			SetID(id);
			_systemData = systemData;
		}

		internal virtual com.db4o.@internal.SystemData SystemData()
		{
			return _systemData;
		}

		public override byte GetIdentifier()
		{
			return com.db4o.@internal.Const4.HEADER;
		}

		public override int OwnLength()
		{
			return LENGTH;
		}

		public override void ReadThis(com.db4o.@internal.Transaction trans, com.db4o.@internal.Buffer
			 reader)
		{
			_systemData.ConverterVersion(reader.ReadInt());
			_systemData.FreespaceSystem(reader.ReadByte());
			_systemData.FreespaceAddress(reader.ReadInt());
			ReadIdentity(trans, reader.ReadInt());
			_systemData.LastTimeStampID(reader.ReadLong());
			_systemData.UuidIndexId(reader.ReadInt());
		}

		public override void WriteThis(com.db4o.@internal.Transaction trans, com.db4o.@internal.Buffer
			 writer)
		{
			writer.WriteInt(_systemData.ConverterVersion());
			writer.Append(_systemData.FreespaceSystem());
			writer.WriteInt(_systemData.FreespaceAddress());
			writer.WriteInt(_systemData.Identity().GetID(trans));
			writer.WriteLong(_systemData.LastTimeStampID());
			writer.WriteInt(_systemData.UuidIndexId());
		}

		private void ReadIdentity(com.db4o.@internal.Transaction trans, int identityID)
		{
			com.db4o.@internal.LocalObjectContainer file = trans.i_file;
			com.db4o.ext.Db4oDatabase identity = (com.db4o.ext.Db4oDatabase)file.GetByID1(trans
				, identityID);
			file.Activate1(trans, identity, 2);
			_systemData.Identity(identity);
		}
	}
}
