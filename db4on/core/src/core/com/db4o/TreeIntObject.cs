namespace com.db4o
{
	/// <exclude></exclude>
	internal class TreeIntObject : com.db4o.TreeInt
	{
		internal object i_object;

		internal TreeIntObject(int a_key) : base(a_key)
		{
		}

		internal TreeIntObject(int a_key, object a_object) : base(a_key)
		{
			i_object = a_object;
		}

		public override object read(com.db4o.YapReader a_bytes)
		{
			int key = a_bytes.readInt();
			object obj = null;
			if (i_object is com.db4o.Tree)
			{
				obj = new com.db4o.TreeReader(a_bytes, (com.db4o.Tree)i_object).read();
			}
			else
			{
				obj = ((com.db4o.Readable)i_object).read(a_bytes);
			}
			return new com.db4o.TreeIntObject(key, obj);
		}

		public override void write(com.db4o.YapWriter a_writer)
		{
			a_writer.writeInt(i_key);
			if (i_object == null)
			{
				a_writer.writeInt(0);
			}
			else
			{
				if (i_object is com.db4o.Tree)
				{
					com.db4o.Tree.write(a_writer, (com.db4o.Tree)i_object);
				}
				else
				{
					((com.db4o.ReadWriteable)i_object).write(a_writer);
				}
			}
		}

		internal override int ownLength()
		{
			if (i_object == null)
			{
				return com.db4o.YapConst.YAPINT_LENGTH * 2;
			}
			else
			{
				return com.db4o.YapConst.YAPINT_LENGTH + ((com.db4o.Readable)i_object).byteCount(
					);
			}
		}

		internal override bool variableLength()
		{
			return true;
		}
	}
}
