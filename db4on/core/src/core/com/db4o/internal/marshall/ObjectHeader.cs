namespace com.db4o.@internal.marshall
{
	/// <exclude></exclude>
	public sealed class ObjectHeader
	{
		private readonly com.db4o.@internal.ClassMetadata _yapClass;

		public readonly com.db4o.@internal.marshall.MarshallerFamily _marshallerFamily;

		public readonly com.db4o.@internal.marshall.ObjectHeaderAttributes _headerAttributes;

		public ObjectHeader(com.db4o.@internal.ObjectContainerBase stream, com.db4o.@internal.Buffer
			 reader) : this(stream, null, reader)
		{
		}

		public ObjectHeader(com.db4o.@internal.ClassMetadata yapClass, com.db4o.@internal.Buffer
			 reader) : this(null, yapClass, reader)
		{
		}

		public ObjectHeader(com.db4o.@internal.StatefulBuffer writer) : this(writer.GetStream
			(), writer)
		{
		}

		public ObjectHeader(com.db4o.@internal.ObjectContainerBase stream, com.db4o.@internal.ClassMetadata
			 yc, com.db4o.@internal.Buffer reader)
		{
			int classID = reader.ReadInt();
			_marshallerFamily = ReadMarshallerFamily(reader, classID);
			classID = NormalizeID(classID);
			_yapClass = (yc != null ? yc : stream.GetYapClass(classID));
			_headerAttributes = ReadAttributes(_marshallerFamily, reader);
		}

		public static com.db4o.@internal.marshall.ObjectHeader Defrag(com.db4o.@internal.ReaderPair
			 readers)
		{
			com.db4o.@internal.Buffer source = readers.Source();
			com.db4o.@internal.Buffer target = readers.Target();
			com.db4o.@internal.marshall.ObjectHeader header = new com.db4o.@internal.marshall.ObjectHeader
				(readers.Context().SystemTrans().Stream(), null, source);
			int newID = readers.Mapping().MappedID(header.YapClass().GetID());
			header._marshallerFamily._object.WriteObjectClassID(target, newID);
			header._marshallerFamily._object.SkipMarshallerInfo(target);
			ReadAttributes(header._marshallerFamily, target);
			return header;
		}

		public com.db4o.@internal.marshall.ObjectMarshaller ObjectMarshaller()
		{
			return _marshallerFamily._object;
		}

		private com.db4o.@internal.marshall.MarshallerFamily ReadMarshallerFamily(com.db4o.@internal.Buffer
			 reader, int classID)
		{
			bool marshallerAware = MarshallerAware(classID);
			byte marshallerVersion = 0;
			if (marshallerAware)
			{
				marshallerVersion = reader.ReadByte();
			}
			com.db4o.@internal.marshall.MarshallerFamily marshallerFamily = com.db4o.@internal.marshall.MarshallerFamily
				.Version(marshallerVersion);
			return marshallerFamily;
		}

		private static com.db4o.@internal.marshall.ObjectHeaderAttributes ReadAttributes(
			com.db4o.@internal.marshall.MarshallerFamily marshallerFamily, com.db4o.@internal.Buffer
			 reader)
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

		public com.db4o.@internal.ClassMetadata YapClass()
		{
			return _yapClass;
		}
	}
}
