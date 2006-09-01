namespace com.db4o
{
	/// <summary>Base class for balanced trees.</summary>
	/// <remarks>Base class for balanced trees.</remarks>
	/// <exclude></exclude>
	public class TreeInt : com.db4o.Tree, com.db4o.ReadWriteable
	{
		public int _key;

		public TreeInt(int a_key)
		{
			this._key = a_key;
		}

		public override int Compare(com.db4o.Tree a_to)
		{
			return _key - ((com.db4o.TreeInt)a_to)._key;
		}

		internal virtual com.db4o.Tree DeepClone()
		{
			return new com.db4o.TreeInt(_key);
		}

		public override bool Duplicates()
		{
			return false;
		}

		public static com.db4o.TreeInt Find(com.db4o.Tree a_in, int a_key)
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

		public override object Read(com.db4o.YapReader a_bytes)
		{
			return new com.db4o.TreeInt(a_bytes.ReadInt());
		}

		public override void Write(com.db4o.YapReader a_writer)
		{
			a_writer.WriteInt(_key);
		}

		public override int OwnLength()
		{
			return com.db4o.YapConst.INT_LENGTH;
		}

		internal override bool VariableLength()
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

		protected override com.db4o.Tree ShallowCloneInternal(com.db4o.Tree tree)
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
	}
}
