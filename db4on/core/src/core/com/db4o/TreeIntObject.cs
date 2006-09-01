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

		public override object ShallowClone()
		{
			return ShallowCloneInternal(new com.db4o.TreeIntObject(_key));
		}

		protected override com.db4o.Tree ShallowCloneInternal(com.db4o.Tree tree)
		{
			com.db4o.TreeIntObject tio = (com.db4o.TreeIntObject)base.ShallowCloneInternal(tree
				);
			tio._object = _object;
			return tio;
		}

		public virtual object GetObject()
		{
			return _object;
		}

		public virtual void SetObject(object obj)
		{
			_object = obj;
		}

		public override object Read(com.db4o.YapReader a_bytes)
		{
			int key = a_bytes.ReadInt();
			object obj = null;
			if (_object is com.db4o.Tree)
			{
				obj = new com.db4o.TreeReader(a_bytes, (com.db4o.Tree)_object).Read();
			}
			else
			{
				obj = ((com.db4o.Readable)_object).Read(a_bytes);
			}
			return new com.db4o.TreeIntObject(key, obj);
		}

		public override void Write(com.db4o.YapReader a_writer)
		{
			a_writer.WriteInt(_key);
			if (_object == null)
			{
				a_writer.WriteInt(0);
			}
			else
			{
				if (_object is com.db4o.Tree)
				{
					com.db4o.Tree.Write(a_writer, (com.db4o.Tree)_object);
				}
				else
				{
					((com.db4o.ReadWriteable)_object).Write(a_writer);
				}
			}
		}

		public override int OwnLength()
		{
			if (_object == null)
			{
				return com.db4o.YapConst.INT_LENGTH * 2;
			}
			return com.db4o.YapConst.INT_LENGTH + ((com.db4o.Readable)_object).ByteCount();
		}

		internal override bool VariableLength()
		{
			return true;
		}
	}
}
