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
	/// <summary>An addition to a field index.</summary>
	/// <remarks>An addition to a field index.</remarks>
	internal class IxAdd : com.db4o.IxPatch
	{
		internal bool i_keepRemoved;

		internal IxAdd(com.db4o.IxFieldTransaction a_ft, int a_parentID, object a_value) : 
			base(a_ft, a_parentID, a_value)
		{
		}

		internal override com.db4o.Tree addToCandidatesTree(com.db4o.Tree a_tree, com.db4o.QCandidates
			 a_candidates, int[] a_lowerAndUpperMatch)
		{
			com.db4o.QCandidate candidate = new com.db4o.QCandidate(a_candidates, i_parentID, 
				true);
			if (a_tree == null)
			{
				return candidate;
			}
			a_tree = a_tree.add(candidate);
			return a_tree;
		}

		internal override void write(com.db4o.YapDataType a_handler, com.db4o.YapWriter a_writer
			)
		{
			a_handler.writeIndexEntry(a_writer, i_value);
			a_writer.writeInt(i_parentID);
			a_writer.writeForward();
		}

		internal override void beginMerge()
		{
			base.beginMerge();
			handler().prepareComparison(handler().indexObject(trans(), i_value));
		}

		public override string ToString()
		{
			string str = "IxAdd " + i_parentID + "\n " + handler().indexObject(trans(), i_value
				);
			return str;
		}
	}
}
