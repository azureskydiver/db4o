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
	/// <summary>Node for index tree, can be addition or removal node</summary>
	internal abstract class IxPatch : com.db4o.IxTree
	{
		internal int i_parentID;

		internal object i_value;

		internal com.db4o.Queue4 i_queue;

		internal IxPatch(com.db4o.IxFieldTransaction a_ft, int a_parentID, object a_value
			) : base(a_ft)
		{
			i_parentID = a_parentID;
			i_value = a_value;
		}

		public override com.db4o.Tree add(com.db4o.Tree a_new)
		{
			int cmp = compare(a_new);
			if (cmp == 0)
			{
				com.db4o.IxPatch patch = (com.db4o.IxPatch)a_new;
				cmp = i_parentID - patch.i_parentID;
				if (cmp == 0)
				{
					com.db4o.Queue4 queue = i_queue;
					if (queue == null)
					{
						queue = new com.db4o.Queue4();
						queue.add(this);
					}
					queue.add(patch);
					patch.i_queue = queue;
					patch.i_subsequent = i_subsequent;
					patch.i_preceding = i_preceding;
					patch.calculateSize();
					return patch;
				}
			}
			return add(a_new, cmp);
		}

		internal override int compare(com.db4o.Tree a_to)
		{
			com.db4o.YapDataType handler = i_fieldTransaction.i_index.i_field.getHandler();
			return handler.compareTo(handler.indexObject(trans(), i_value));
		}
	}
}
