namespace com.db4o
{
	internal class YapFieldVersion : com.db4o.YapFieldVirtual
	{
		internal YapFieldVersion(com.db4o.YapStream stream) : base()
		{
			i_name = com.db4o.ext.VirtualField.VERSION;
			i_handler = new com.db4o.YLong(stream);
		}

		internal override void addFieldIndex(com.db4o.YapWriter a_writer, bool a_new)
		{
			com.db4o.YLong.writeLong(a_writer.getStream().bootRecord().version(), a_writer);
		}

		internal override void instantiate1(com.db4o.Transaction a_trans, com.db4o.YapObject
			 a_yapObject, com.db4o.YapReader a_bytes)
		{
			a_yapObject.i_virtualAttributes.i_version = com.db4o.YLong.readLong(a_bytes);
		}

		internal override void marshall1(com.db4o.YapObject a_yapObject, com.db4o.YapWriter
			 a_bytes, bool a_migrating, bool a_new)
		{
			if (!a_migrating)
			{
				com.db4o.YapStream stream = a_bytes.getStream().i_parent;
				com.db4o.PBootRecord br = stream.bootRecord();
				if (br != null)
				{
					a_yapObject.i_virtualAttributes.i_version = br.version();
				}
			}
			if (a_yapObject.i_virtualAttributes == null)
			{
				com.db4o.YLong.writeLong(0, a_bytes);
			}
			else
			{
				com.db4o.YLong.writeLong(a_yapObject.i_virtualAttributes.i_version, a_bytes);
			}
		}

		public override int linkLength()
		{
			return com.db4o.YapConst.YAPLONG_LENGTH;
		}
	}
}
