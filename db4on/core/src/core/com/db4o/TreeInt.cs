namespace com.db4o
{
	/// <summary>Base class for balanced trees.</summary>
	/// <remarks>Base class for balanced trees.</remarks>
	/// <exclude></exclude>
	public class TreeInt : com.db4o.foundation.Tree, com.db4o.ReadWriteable
	{
		public static com.db4o.TreeInt Add(com.db4o.TreeInt tree, int value)
		{
			return (com.db4o.TreeInt)com.db4o.foundation.Tree.Add(tree, new com.db4o.TreeInt(
				value));
		}

		public static com.db4o.TreeInt RemoveLike(com.db4o.TreeInt tree, int value)
		{
			return (com.db4o.TreeInt)com.db4o.foundation.Tree.RemoveLike(tree, new com.db4o.TreeInt
				(value));
		}

		public int _key;

		public TreeInt(int a_key)
		{
			this._key = a_key;
		}

		public override int Compare(com.db4o.foundation.Tree a_to)
		{
			return _key - ((com.db4o.TreeInt)a_to)._key;
		}

		internal virtual com.db4o.foundation.Tree DeepClone()
		{
			return new com.db4o.TreeInt(_key);
		}

		public override bool Duplicates()
		{
			return false;
		}

		public static com.db4o.TreeInt Find(com.db4o.foundation.Tree a_in, int a_key)
		{
			if (a_in == null)
			{
				return null;
			}
			return ((com.db4o.TreeInt)a_in).Find(a_key);
		}

		internal com.db4o.TreeInt Find(int a_key)
		{
			int cmp = _key - a_key;
			if (cmp < 0)
			{
				if (_subsequent != null)
				{
					return ((com.db4o.TreeInt)_subsequent).Find(a_key);
				}
			}
			else
			{
				if (cmp > 0)
				{
					if (_preceding != null)
					{
						return ((com.db4o.TreeInt)_preceding).Find(a_key);
					}
				}
				else
				{
					return this;
				}
			}
			return null;
		}

		public virtual object Read(com.db4o.YapReader a_bytes)
		{
			return new com.db4o.TreeInt(a_bytes.ReadInt());
		}

		public virtual void Write(com.db4o.YapReader a_writer)
		{
			a_writer.WriteInt(_key);
		}

		public static void Write(com.db4o.YapReader a_writer, com.db4o.TreeInt a_tree)
		{
			Write(a_writer, a_tree, a_tree == null ? 0 : a_tree.Size());
		}

		public static void Write(com.db4o.YapReader a_writer, com.db4o.TreeInt a_tree, int
			 size)
		{
			if (a_tree == null)
			{
				a_writer.WriteInt(0);
				return;
			}
			a_writer.WriteInt(size);
			a_tree.Traverse(new _AnonymousInnerClass83(a_writer));
		}

		private sealed class _AnonymousInnerClass83 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass83(com.db4o.YapReader a_writer)
			{
				this.a_writer = a_writer;
			}

			public void Visit(object a_object)
			{
				((com.db4o.TreeInt)a_object).Write(a_writer);
			}

			private readonly com.db4o.YapReader a_writer;
		}

		public virtual int OwnLength()
		{
			return com.db4o.YapConst.INT_LENGTH;
		}

		internal virtual bool VariableLength()
		{
			return false;
		}

		internal virtual com.db4o.QCandidate ToQCandidate(com.db4o.QCandidates candidates
			)
		{
			com.db4o.QCandidate qc = new com.db4o.QCandidate(candidates, null, _key, true);
			qc._preceding = ToQCandidate((com.db4o.TreeInt)_preceding, candidates);
			qc._subsequent = ToQCandidate((com.db4o.TreeInt)_subsequent, candidates);
			qc._size = _size;
			return qc;
		}

		public static com.db4o.QCandidate ToQCandidate(com.db4o.TreeInt tree, com.db4o.QCandidates
			 candidates)
		{
			if (tree == null)
			{
				return null;
			}
			return tree.ToQCandidate(candidates);
		}

		public override string ToString()
		{
			return "" + _key;
		}

		protected override com.db4o.foundation.Tree ShallowCloneInternal(com.db4o.foundation.Tree
			 tree)
		{
			com.db4o.TreeInt treeint = (com.db4o.TreeInt)base.ShallowCloneInternal(tree);
			treeint._key = _key;
			return treeint;
		}

		public override object ShallowClone()
		{
			com.db4o.TreeInt treeint = new com.db4o.TreeInt(_key);
			return ShallowCloneInternal(treeint);
		}

		public static int ByteCount(com.db4o.TreeInt a_tree)
		{
			if (a_tree == null)
			{
				return com.db4o.YapConst.INT_LENGTH;
			}
			return a_tree.ByteCount();
		}

		public int ByteCount()
		{
			if (VariableLength())
			{
				int[] length = new int[] { com.db4o.YapConst.INT_LENGTH };
				Traverse(new _AnonymousInnerClass138(this, length));
				return length[0];
			}
			return com.db4o.YapConst.INT_LENGTH + (Size() * OwnLength());
		}

		private sealed class _AnonymousInnerClass138 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass138(TreeInt _enclosing, int[] length)
			{
				this._enclosing = _enclosing;
				this.length = length;
			}

			public void Visit(object obj)
			{
				length[0] += ((com.db4o.TreeInt)obj).OwnLength();
			}

			private readonly TreeInt _enclosing;

			private readonly int[] length;
		}
	}
}
