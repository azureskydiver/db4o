namespace com.db4o
{
	/// <exclude></exclude>
	public class TreeIntObject : com.db4o.TreeInt
	{
		public object _object;

		public TreeIntObject(int a_key) : base(a_key)
		{
		}

		public TreeIntObject(int a_key, object a_object) : base(a_key)
		{
			_object = a_object;
		}

		public override object shallowClone()
		{
			return shallowCloneInternal(new com.db4o.TreeIntObject(_key));
		}

		protected override com.db4o.Tree shallowCloneInternal(com.db4o.Tree tree)
		{
			com.db4o.TreeIntObject tio = (com.db4o.TreeIntObject)base.shallowCloneInternal(tree
				);
			tio._object = _object;
			return tio;
		}

		public override object read(com.db4o.YapReader a_bytes)
		{
			int key = a_bytes.readInt();
			object obj = null;
			if (_object is com.db4o.Tree)
			{
				obj = new com.db4o.TreeReader(a_bytes, (com.db4o.Tree)_object).read();
			}
			else
			{
				obj = ((com.db4o.Readable)_object).read(a_bytes);
			}
			return new com.db4o.TreeIntObject(key, obj);
		}

		public override void write(com.db4o.YapReader a_writer)
		{
			a_writer.writeInt(_key);
			if (_object == null)
			{
				a_writer.writeInt(0);
			}
			else
			{
				if (_object is com.db4o.Tree)
				{
					com.db4o.Tree.write(a_writer, (com.db4o.Tree)_object);
				}
				else
				{
					((com.db4o.ReadWriteable)_object).write(a_writer);
				}
			}
		}

		public override int ownLength()
		{
			if (_object == null)
			{
				return com.db4o.YapConst.YAPINT_LENGTH * 2;
			}
			else
			{
				return com.db4o.YapConst.YAPINT_LENGTH + ((com.db4o.Readable)_object).byteCount();
			}
		}

		internal override bool variableLength()
		{
			return true;
		}
	}
}
