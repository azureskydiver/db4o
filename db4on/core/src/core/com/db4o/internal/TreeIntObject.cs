namespace com.db4o.@internal
{
	/// <exclude></exclude>
	public class TreeIntObject : com.db4o.@internal.TreeInt
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
			return ShallowCloneInternal(new com.db4o.@internal.TreeIntObject(_key));
		}

		protected override com.db4o.foundation.Tree ShallowCloneInternal(com.db4o.foundation.Tree
			 tree)
		{
			com.db4o.@internal.TreeIntObject tio = (com.db4o.@internal.TreeIntObject)base.ShallowCloneInternal
				(tree);
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

		public override object Read(com.db4o.@internal.Buffer a_bytes)
		{
			int key = a_bytes.ReadInt();
			object obj = null;
			if (_object is com.db4o.@internal.TreeInt)
			{
				obj = new com.db4o.@internal.TreeReader(a_bytes, (com.db4o.@internal.Readable)_object
					).Read();
			}
			else
			{
				obj = ((com.db4o.@internal.Readable)_object).Read(a_bytes);
			}
			return new com.db4o.@internal.TreeIntObject(key, obj);
		}

		public override void Write(com.db4o.@internal.Buffer a_writer)
		{
			a_writer.WriteInt(_key);
			if (_object == null)
			{
				a_writer.WriteInt(0);
			}
			else
			{
				if (_object is com.db4o.@internal.TreeInt)
				{
					com.db4o.@internal.TreeInt.Write(a_writer, (com.db4o.@internal.TreeInt)_object);
				}
				else
				{
					((com.db4o.@internal.ReadWriteable)_object).Write(a_writer);
				}
			}
		}

		public override int OwnLength()
		{
			if (_object == null)
			{
				return com.db4o.@internal.Const4.INT_LENGTH * 2;
			}
			return com.db4o.@internal.Const4.INT_LENGTH + ((com.db4o.@internal.Readable)_object
				).ByteCount();
		}

		internal override bool VariableLength()
		{
			return true;
		}
	}
}
