namespace com.db4o.inside.classindex
{
	/// <summary>representation to collect and hold all IDs of one class</summary>
	public class ClassIndex : com.db4o.YapMeta, com.db4o.ReadWriteable
	{
		private readonly com.db4o.YapClass _yapClass;

		/// <summary>contains TreeInt with object IDs</summary>
		private com.db4o.TreeInt i_root;

		internal ClassIndex(com.db4o.YapClass yapClass)
		{
			_yapClass = yapClass;
		}

		public virtual void Add(int a_id)
		{
			i_root = com.db4o.TreeInt.Add(i_root, a_id);
		}

		public int ByteCount()
		{
			return com.db4o.YapConst.INT_LENGTH * (com.db4o.foundation.Tree.Size(i_root) + 1);
		}

		public void Clear()
		{
			i_root = null;
		}

		internal virtual void EnsureActive(com.db4o.Transaction trans)
		{
			if (!IsActive())
			{
				SetStateDirty();
				Read(trans);
			}
		}

		internal virtual int EntryCount(com.db4o.Transaction ta)
		{
			if (IsActive() || IsNew())
			{
				return com.db4o.foundation.Tree.Size(i_root);
			}
			com.db4o.inside.slots.Slot slot = ta.GetCurrentSlotOfID(GetID());
			int length = com.db4o.YapConst.INT_LENGTH;
			com.db4o.YapReader reader = new com.db4o.YapReader(length);
			reader.ReadEncrypt(ta.Stream(), slot._address);
			return reader.ReadInt();
		}

		public sealed override byte GetIdentifier()
		{
			return com.db4o.YapConst.YAPINDEX;
		}

		internal virtual com.db4o.TreeInt GetRoot()
		{
			return i_root;
		}

		public sealed override int OwnLength()
		{
			return com.db4o.YapConst.OBJECT_LENGTH + ByteCount();
		}

		public object Read(com.db4o.YapReader a_reader)
		{
			throw com.db4o.inside.Exceptions4.VirtualException();
		}

		public sealed override void ReadThis(com.db4o.Transaction a_trans, com.db4o.YapReader
			 a_reader)
		{
			i_root = (com.db4o.TreeInt)new com.db4o.TreeReader(a_reader, new com.db4o.TreeInt
				(0)).Read();
		}

		public virtual void Remove(int a_id)
		{
			i_root = com.db4o.TreeInt.RemoveLike(i_root, a_id);
		}

		internal virtual void SetDirty(com.db4o.YapStream a_stream)
		{
			a_stream.SetDirtyInSystemTransaction(this);
		}

		public virtual void Write(com.db4o.YapReader a_writer)
		{
			WriteThis(null, a_writer);
		}

		public sealed override void WriteThis(com.db4o.Transaction trans, com.db4o.YapReader
			 a_writer)
		{
			com.db4o.TreeInt.Write(a_writer, i_root);
		}

		public override string ToString()
		{
			return base.ToString();
			return _yapClass + " index";
		}
	}
}
