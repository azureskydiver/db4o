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

		public override int compare(com.db4o.Tree a_to)
		{
			return _key - ((com.db4o.TreeInt)a_to)._key;
		}

		internal virtual com.db4o.Tree deepClone()
		{
			return new com.db4o.TreeInt(_key);
		}

		public override bool duplicates()
		{
			return false;
		}

		public static com.db4o.TreeInt find(com.db4o.Tree a_in, int a_key)
		{
			if (a_in == null)
			{
				return null;
			}
			return ((com.db4o.TreeInt)a_in).find(a_key);
		}

		internal com.db4o.TreeInt find(int a_key)
		{
			int cmp = _key - a_key;
			if (cmp < 0)
			{
				if (_subsequent != null)
				{
					return ((com.db4o.TreeInt)_subsequent).find(a_key);
				}
			}
			else
			{
				if (cmp > 0)
				{
					if (_preceding != null)
					{
						return ((com.db4o.TreeInt)_preceding).find(a_key);
					}
				}
				else
				{
					return this;
				}
			}
			return null;
		}

		public override object read(com.db4o.YapReader a_bytes)
		{
			return new com.db4o.TreeInt(a_bytes.readInt());
		}

		public override void write(com.db4o.YapReader a_writer)
		{
			a_writer.writeInt(_key);
		}

		public override int ownLength()
		{
			return com.db4o.YapConst.YAPINT_LENGTH;
		}

		internal override bool variableLength()
		{
			return false;
		}

		internal virtual com.db4o.QCandidate toQCandidate(com.db4o.QCandidates candidates
			)
		{
			com.db4o.QCandidate qc = new com.db4o.QCandidate(candidates, null, _key, true);
			qc._preceding = toQCandidate((com.db4o.TreeInt)_preceding, candidates);
			qc._subsequent = toQCandidate((com.db4o.TreeInt)_subsequent, candidates);
			qc._size = _size;
			return qc;
		}

		public static com.db4o.QCandidate toQCandidate(com.db4o.TreeInt tree, com.db4o.QCandidates
			 candidates)
		{
			if (tree == null)
			{
				return null;
			}
			return tree.toQCandidate(candidates);
		}

		public override string ToString()
		{
			return "" + _key;
		}

		protected override com.db4o.Tree shallowCloneInternal(com.db4o.Tree tree)
		{
			com.db4o.TreeInt treeint = (com.db4o.TreeInt)base.shallowCloneInternal(tree);
			treeint._key = _key;
			return treeint;
		}

		public override object shallowClone()
		{
			com.db4o.TreeInt treeint = new com.db4o.TreeInt(_key);
			return shallowCloneInternal(treeint);
		}
	}
}
