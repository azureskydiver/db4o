namespace com.db4o.inside.marshall
{
	/// <exclude></exclude>
	public class ObjectHeader
	{
		private readonly com.db4o.YapClass _yapClass;

		public readonly com.db4o.inside.marshall.MarshallerFamily _marshallerFamily;

		public readonly com.db4o.inside.marshall.ObjectHeaderAttributes _headerAttributes;

		public ObjectHeader(com.db4o.YapStream stream, com.db4o.YapReader reader) : this(
			stream, null, reader)
		{
		}

		public ObjectHeader(com.db4o.YapClass yapClass, com.db4o.YapReader reader) : this
			(null, yapClass, reader)
		{
		}

		public ObjectHeader(com.db4o.YapWriter writer) : this(writer.GetStream(), writer)
		{
		}

		public ObjectHeader(com.db4o.YapStream stream, com.db4o.YapClass yc, com.db4o.YapReader
			 reader)
		{
			int classID = reader.ReadInt();
			_marshallerFamily = ReadMarshallerFamily(reader, classID);
			classID = NormalizeID(classID);
			_yapClass = (yc != null ? yc : stream.GetYapClass(classID));
			_headerAttributes = ReadAttributes(_marshallerFamily, reader);
		}

		public static com.db4o.inside.marshall.ObjectHeader Defrag(com.db4o.ReaderPair readers
			)
		{
			com.db4o.YapReader source = readers.Source();
			com.db4o.YapReader target = readers.Target();
			com.db4o.inside.marshall.ObjectHeader header = new com.db4o.inside.marshall.ObjectHeader
				(readers.Context().SystemTrans().Stream(), null, source);
			int newID = readers.Mapping().MappedID(header.YapClass().GetID());
			header._marshallerFamily._object.WriteObjectClassID(target, newID);
			header._marshallerFamily._object.SkipMarshallerInfo(target);
			ReadAttributes(header._marshallerFamily, target);
			return header;
		}

		public virtual com.db4o.inside.marshall.ObjectMarshaller ObjectMarshaller()
		{
			return _marshallerFamily._object;
		}

		private com.db4o.inside.marshall.MarshallerFamily ReadMarshallerFamily(com.db4o.YapReader
			 reader, int classID)
		{
			bool marshallerAware = MarshallerAware(classID);
			byte marshallerVersion = 0;
			if (marshallerAware)
			{
				marshallerVersion = reader.ReadByte();
			}
			com.db4o.inside.marshall.MarshallerFamily marshallerFamily = com.db4o.inside.marshall.MarshallerFamily
				.Version(marshallerVersion);
			return marshallerFamily;
		}

		private static com.db4o.inside.marshall.ObjectHeaderAttributes ReadAttributes(com.db4o.inside.marshall.MarshallerFamily
			 marshallerFamily, com.db4o.YapReader reader)
		{
			return marshallerFamily._object.ReadHeaderAttributes(reader);
		}

		private bool MarshallerAware(int id)
		{
			return id < 0;
		}

		private int NormalizeID(int id)
		{
			return (id < 0 ? -id : id);
		}

		public virtual com.db4o.YapClass YapClass()
		{
			return _yapClass;
		}
	}
}
