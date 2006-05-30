namespace com.db4o.inside.marshall
{
	/// <exclude></exclude>
	public class ObjectHeader
	{
		public readonly com.db4o.YapClass _yapClass;

		public readonly com.db4o.inside.marshall.MarshallerFamily _marshallerFamily;

		public readonly com.db4o.inside.marshall.ObjectHeaderAttributes _headerAttributes;

		public ObjectHeader(com.db4o.YapStream stream, com.db4o.YapReader reader)
		{
			int id = reader.ReadInt();
			byte marshallerVersion = 0;
			if (id == 0)
			{
				_yapClass = null;
			}
			else
			{
				if (id > 0)
				{
					_yapClass = stream.GetYapClass(id);
				}
				else
				{
					_yapClass = stream.GetYapClass(-id);
					marshallerVersion = reader.ReadByte();
				}
			}
			_marshallerFamily = com.db4o.inside.marshall.MarshallerFamily.ForVersion(marshallerVersion
				);
			_headerAttributes = _marshallerFamily._object.ReadHeaderAttributes(reader);
		}

		public ObjectHeader(com.db4o.YapWriter writer) : this(writer.GetStream(), writer)
		{
		}

		public ObjectHeader(com.db4o.YapStream stream, com.db4o.YapClass yc, com.db4o.YapReader
			 reader)
		{
			_yapClass = yc;
			int id = reader.ReadInt();
			byte marshallerVersion = 0;
			if (id < 0)
			{
				marshallerVersion = reader.ReadByte();
			}
			_marshallerFamily = com.db4o.inside.marshall.MarshallerFamily.ForVersion(marshallerVersion
				);
			_headerAttributes = _marshallerFamily._object.ReadHeaderAttributes(reader);
		}

		public virtual com.db4o.inside.marshall.ObjectMarshaller ObjectMarshaller()
		{
			return _marshallerFamily._object;
		}
	}
}
