namespace com.db4o.@internal.classindex
{
	/// <summary>representation to collect and hold all IDs of one class</summary>
	public class ClassIndex : com.db4o.@internal.PersistentBase, com.db4o.@internal.ReadWriteable
	{
		private readonly com.db4o.@internal.ClassMetadata _yapClass;

		/// <summary>contains TreeInt with object IDs</summary>
		private com.db4o.@internal.TreeInt i_root;

		internal ClassIndex(com.db4o.@internal.ClassMetadata yapClass)
		{
			_yapClass = yapClass;
		}

		public virtual void Add(int a_id)
		{
			i_root = com.db4o.@internal.TreeInt.Add(i_root, a_id);
		}

		public int ByteCount()
		{
			return com.db4o.@internal.Const4.INT_LENGTH * (com.db4o.foundation.Tree.Size(i_root
				) + 1);
		}

		public void Clear()
		{
			i_root = null;
		}

		internal virtual void EnsureActive(com.db4o.@internal.Transaction trans)
		{
			if (!IsActive())
			{
				SetStateDirty();
				Read(trans);
			}
		}

		internal virtual int EntryCount(com.db4o.@internal.Transaction ta)
		{
			if (IsActive() || IsNew())
			{
				return com.db4o.foundation.Tree.Size(i_root);
			}
			com.db4o.@internal.slots.Slot slot = ((com.db4o.@internal.LocalTransaction)ta).GetCurrentSlotOfID
				(GetID());
			int length = com.db4o.@internal.Const4.INT_LENGTH;
			com.db4o.@internal.Buffer reader = new com.db4o.@internal.Buffer(length);
			reader.ReadEncrypt(ta.Stream(), slot._address);
			return reader.ReadInt();
		}

		public sealed override byte GetIdentifier()
		{
			return com.db4o.@internal.Const4.YAPINDEX;
		}

		internal virtual com.db4o.@internal.TreeInt GetRoot()
		{
			return i_root;
		}

		public sealed override int OwnLength()
		{
			return com.db4o.@internal.Const4.OBJECT_LENGTH + ByteCount();
		}

		public object Read(com.db4o.@internal.Buffer a_reader)
		{
			throw com.db4o.@internal.Exceptions4.VirtualException();
		}

		public sealed override void ReadThis(com.db4o.@internal.Transaction a_trans, com.db4o.@internal.Buffer
			 a_reader)
		{
			i_root = (com.db4o.@internal.TreeInt)new com.db4o.@internal.TreeReader(a_reader, 
				new com.db4o.@internal.TreeInt(0)).Read();
		}

		public virtual void Remove(int a_id)
		{
			i_root = com.db4o.@internal.TreeInt.RemoveLike(i_root, a_id);
		}

		internal virtual void SetDirty(com.db4o.@internal.ObjectContainerBase a_stream)
		{
			a_stream.SetDirtyInSystemTransaction(this);
		}

		public virtual void Write(com.db4o.@internal.Buffer a_writer)
		{
			WriteThis(null, a_writer);
		}

		public sealed override void WriteThis(com.db4o.@internal.Transaction trans, com.db4o.@internal.Buffer
			 a_writer)
		{
			com.db4o.@internal.TreeInt.Write(a_writer, i_root);
		}

		public override string ToString()
		{
			return base.ToString();
			return _yapClass + " index";
		}
	}
}
