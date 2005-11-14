namespace com.db4o
{
	/// <summary>Base class for balanced trees.</summary>
	/// <remarks>Base class for balanced trees.</remarks>
	/// <exclude></exclude>
	public class TreeInt : com.db4o.Tree, com.db4o.ReadWriteable
	{
		public int i_key;

		public TreeInt(int a_key)
		{
			this.i_key = a_key;
		}

		public override int compare(com.db4o.Tree a_to)
		{
			return i_key - ((com.db4o.TreeInt)a_to).i_key;
		}

		internal virtual com.db4o.Tree deepClone()
		{
			return new com.db4o.TreeInt(i_key);
		}

		public override bool duplicates()
		{
			return false;
		}

		internal static com.db4o.TreeInt find(com.db4o.Tree a_in, int a_key)
		{
			if (a_in == null)
			{
				return null;
			}
			return ((com.db4o.TreeInt)a_in).find(a_key);
		}

		internal com.db4o.TreeInt find(int a_key)
		{
			int cmp = i_key - a_key;
			if (cmp < 0)
			{
				if (i_subsequent != null)
				{
					return ((com.db4o.TreeInt)i_subsequent).find(a_key);
				}
			}
			else
			{
				if (cmp > 0)
				{
					if (i_preceding != null)
					{
						return ((com.db4o.TreeInt)i_preceding).find(a_key);
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

		public override void write(com.db4o.YapWriter a_writer)
		{
			a_writer.writeInt(i_key);
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
			com.db4o.QCandidate qc = new com.db4o.QCandidate(candidates, null, i_key, true);
			qc.i_preceding = toQCandidate((com.db4o.TreeInt)i_preceding, candidates);
			qc.i_subsequent = toQCandidate((com.db4o.TreeInt)i_subsequent, candidates);
			qc.i_size = i_size;
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
			return "" + i_key;
		}
	}
}
