/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
namespace com.db4o
{
	/// <summary>Base class for balanced trees.</summary>
	/// <remarks>Base class for balanced trees.</remarks>
	/// <exclude></exclude>
	public class TreeInt : com.db4o.Tree, com.db4o.ReadWriteable
	{
		internal int i_key;

		public TreeInt(int a_key)
		{
			this.i_key = a_key;
		}

		internal override int compare(com.db4o.Tree a_to)
		{
			return i_key - ((com.db4o.TreeInt)a_to).i_key;
		}

		internal virtual com.db4o.Tree deepClone()
		{
			return new com.db4o.TreeInt(i_key);
		}

		internal override bool duplicates()
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

		internal override int ownLength()
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
			com.db4o.QCandidate qc = new com.db4o.QCandidate(candidates, i_key, true);
			qc.i_preceding = toQCandidate((com.db4o.TreeInt)i_preceding, candidates);
			qc.i_subsequent = toQCandidate((com.db4o.TreeInt)i_subsequent, candidates);
			qc.i_size = i_size;
			return qc;
		}

		internal static com.db4o.QCandidate toQCandidate(com.db4o.TreeInt tree, com.db4o.QCandidates
			 candidates)
		{
			if (tree == null)
			{
				return null;
			}
			return tree.toQCandidate(candidates);
		}
	}
}
