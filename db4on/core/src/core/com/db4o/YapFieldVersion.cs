namespace com.db4o
{
	internal class YapFieldVersion : com.db4o.YapFieldVirtual
	{
		internal YapFieldVersion(com.db4o.YapStream stream) : base()
		{
			i_name = com.db4o.ext.VirtualField.VERSION;
			i_handler = new com.db4o.YLong(stream);
		}

		public override void AddFieldIndex(com.db4o.inside.marshall.MarshallerFamily mf, 
			com.db4o.YapWriter writer, bool isnew)
		{
			com.db4o.YLong.WriteLong(writer.GetStream().GenerateTimeStampId(), writer);
		}

		public override void Delete(com.db4o.inside.marshall.MarshallerFamily mf, com.db4o.YapWriter
			 a_bytes, bool isUpdate)
		{
			a_bytes.IncrementOffset(LinkLength());
		}

		internal override void Instantiate1(com.db4o.Transaction a_trans, com.db4o.YapObject
			 a_yapObject, com.db4o.YapReader a_bytes)
		{
			a_yapObject.i_virtualAttributes.i_version = com.db4o.YLong.ReadLong(a_bytes);
		}

		internal override void Marshall1(com.db4o.YapObject a_yapObject, com.db4o.YapWriter
			 a_bytes, bool a_migrating, bool a_new)
		{
			com.db4o.YapStream stream = a_bytes.GetStream().i_parent;
			if (!a_migrating)
			{
				a_yapObject.i_virtualAttributes.i_version = stream.GenerateTimeStampId();
			}
			if (a_yapObject.i_virtualAttributes == null)
			{
				com.db4o.YLong.WriteLong(0, a_bytes);
			}
			else
			{
				com.db4o.YLong.WriteLong(a_yapObject.i_virtualAttributes.i_version, a_bytes);
			}
		}

		public override int LinkLength()
		{
			return com.db4o.YapConst.LONG_LENGTH;
		}

		internal override void MarshallIgnore(com.db4o.YapWriter writer)
		{
			com.db4o.YLong.WriteLong(0, writer);
		}
	}
}
