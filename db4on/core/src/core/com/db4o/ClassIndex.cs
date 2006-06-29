namespace com.db4o
{
	/// <summary>representation to collect and hold all IDs of one class</summary>
	internal class ClassIndex : com.db4o.YapMeta, com.db4o.ReadWriteable, com.db4o.UseSystemTransaction
	{
		private readonly com.db4o.YapClass _yapClass;

		/// <summary>contains TreeInt with object IDs</summary>
		private com.db4o.Tree i_root;

		internal ClassIndex(com.db4o.YapClass yapClass)
		{
			_yapClass = yapClass;
		}

		internal virtual void Add(int a_id)
		{
			i_root = com.db4o.Tree.Add(i_root, new com.db4o.TreeInt(a_id));
		}

		public int ByteCount()
		{
			return com.db4o.YapConst.YAPINT_LENGTH * (com.db4o.Tree.Size(i_root) + 1);
		}

		public void Clear()
		{
			i_root = null;
		}

		internal com.db4o.Tree CloneForYapClass(com.db4o.Transaction a_trans, int a_yapClassID
			)
		{
			com.db4o.Tree[] tree = new com.db4o.Tree[] { com.db4o.Tree.DeepClone(i_root, null
				) };
			a_trans.TraverseAddedClassIDs(a_yapClassID, new _AnonymousInnerClass40(this, tree
				));
			a_trans.TraverseRemovedClassIDs(a_yapClassID, new _AnonymousInnerClass45(this, tree
				));
			return tree[0];
		}

		private sealed class _AnonymousInnerClass40 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass40(ClassIndex _enclosing, com.db4o.Tree[] tree)
			{
				this._enclosing = _enclosing;
				this.tree = tree;
			}

			public void Visit(object obj)
			{
				tree[0] = com.db4o.Tree.Add(tree[0], new com.db4o.TreeInt(((com.db4o.TreeInt)obj)
					._key));
			}

			private readonly ClassIndex _enclosing;

			private readonly com.db4o.Tree[] tree;
		}

		private sealed class _AnonymousInnerClass45 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass45(ClassIndex _enclosing, com.db4o.Tree[] tree)
			{
				this._enclosing = _enclosing;
				this.tree = tree;
			}

			public void Visit(object obj)
			{
				tree[0] = com.db4o.Tree.RemoveLike(tree[0], (com.db4o.TreeInt)obj);
			}

			private readonly ClassIndex _enclosing;

			private readonly com.db4o.Tree[] tree;
		}

		internal virtual void EnsureActive()
		{
			if (!IsActive())
			{
				SetStateDirty();
				Read(GetStream().GetSystemTransaction());
			}
		}

		internal virtual int EntryCount(com.db4o.Transaction ta)
		{
			if (IsActive())
			{
				return com.db4o.Tree.Size(i_root);
			}
			com.db4o.inside.slots.Slot slot = ta.GetSlotInformation(i_id);
			int length = com.db4o.YapConst.YAPINT_LENGTH;
			com.db4o.YapReader reader = new com.db4o.YapReader(length);
			reader.ReadEncrypt(ta.i_stream, slot._address);
			if (reader == null)
			{
				return 0;
			}
			return reader.ReadInt();
		}

		public sealed override byte GetIdentifier()
		{
			return com.db4o.YapConst.YAPINDEX;
		}

		internal virtual com.db4o.TreeInt GetRoot()
		{
			EnsureActive();
			return (com.db4o.TreeInt)i_root;
		}

		internal virtual com.db4o.YapStream GetStream()
		{
			return _yapClass.GetStream();
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
			i_root = new com.db4o.TreeReader(a_reader, new com.db4o.TreeInt(0)).Read();
		}

		internal virtual void Remove(int a_id)
		{
			i_root = com.db4o.Tree.RemoveLike(i_root, new com.db4o.TreeInt(a_id));
		}

		internal virtual void SetDirty(com.db4o.YapStream a_stream)
		{
			a_stream.SetDirty(this);
		}

		public virtual void Write(com.db4o.YapReader a_writer)
		{
			WriteThis(null, a_writer);
		}

		public sealed override void WriteThis(com.db4o.Transaction trans, com.db4o.YapReader
			 a_writer)
		{
			com.db4o.Tree.Write(a_writer, i_root);
		}

		public override string ToString()
		{
			return base.ToString();
			return _yapClass + " index";
		}
	}
}
