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
	internal abstract class IxTree : com.db4o.Tree
	{
		internal com.db4o.IxFieldTransaction i_fieldTransaction;

		internal int i_version;

		internal IxTree(com.db4o.IxFieldTransaction a_ft)
		{
			i_fieldTransaction = a_ft;
			i_version = a_ft.i_version;
		}

		internal override com.db4o.Tree add(com.db4o.Tree a_new, int a_cmp)
		{
			if (a_cmp < 0)
			{
				if (i_subsequent == null)
				{
					i_subsequent = a_new;
				}
				else
				{
					i_subsequent = i_subsequent.add(a_new);
				}
			}
			else
			{
				if (i_preceding == null)
				{
					i_preceding = a_new;
				}
				else
				{
					i_preceding = i_preceding.add(a_new);
				}
			}
			return balanceCheckNulls();
		}

		internal abstract com.db4o.Tree addToCandidatesTree(com.db4o.Tree a_tree, com.db4o.QCandidates
			 a_candidates, int[] a_lowerAndUpperMatch);

		internal override com.db4o.Tree deepClone(object a_param)
		{
			try
			{
				com.db4o.IxTree tree = (com.db4o.IxTree)j4o.lang.JavaSystem.clone(this);
				tree.i_fieldTransaction = (com.db4o.IxFieldTransaction)a_param;
				return tree;
			}
			catch (j4o.lang.CloneNotSupportedException e)
			{
				j4o.lang.JavaSystem.printStackTrace(e);
			}
			return null;
		}

		internal com.db4o.YapDataType handler()
		{
			return i_fieldTransaction.i_index.i_field.getHandler();
		}

		internal virtual int slotLength()
		{
			return handler().linkLength() + com.db4o.YapConst.YAPINT_LENGTH;
		}

		internal com.db4o.Transaction trans()
		{
			return i_fieldTransaction.i_trans;
		}

		internal abstract void write(com.db4o.YapDataType a_handler, com.db4o.YapWriter a_writer
			);

		internal com.db4o.YapFile stream()
		{
			return trans().i_file;
		}

		internal virtual void beginMerge()
		{
			i_preceding = null;
			i_subsequent = null;
			i_size = ownSize();
		}
	}
}
