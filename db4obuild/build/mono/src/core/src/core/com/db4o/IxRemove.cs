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
	/// <summary>A node to represent an entry removed from an Index</summary>
	internal class IxRemove : com.db4o.IxPatch
	{
		internal IxRemove(com.db4o.IxFieldTransaction a_ft, int a_parentID, object a_value
			) : base(a_ft, a_parentID, a_value)
		{
			i_size = 0;
		}

		internal override com.db4o.Tree addToCandidatesTree(com.db4o.Tree a_tree, com.db4o.QCandidates
			 a_candidates, int[] a_lowerAndUpperMatch)
		{
			return a_tree;
		}

		internal override void write(com.db4o.YapDataType a_handler, com.db4o.YapWriter a_writer
			)
		{
		}

		internal override int ownSize()
		{
			return 0;
		}

		public override string ToString()
		{
			string str = "IxRemove " + i_parentID + "\n " + handler().indexObject(trans(), i_value
				);
			return str;
		}
	}
}
