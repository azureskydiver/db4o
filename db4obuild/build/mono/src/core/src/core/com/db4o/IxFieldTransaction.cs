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
	/// <summary>Index root holder for a field and a transaction.</summary>
	/// <remarks>Index root holder for a field and a transaction.</remarks>
	internal class IxFieldTransaction : com.db4o.Visitor4
	{
		internal readonly com.db4o.IxField i_index;

		internal readonly com.db4o.Transaction i_trans;

		internal int i_version;

		private com.db4o.Tree i_root;

		internal IxFieldTransaction(com.db4o.Transaction a_trans, com.db4o.IxField a_index
			)
		{
			i_trans = a_trans;
			i_index = a_index;
		}

		public override bool Equals(object obj)
		{
			return i_trans == ((com.db4o.IxFieldTransaction)obj).i_trans;
		}

		internal virtual void add(com.db4o.IxPatch a_patch)
		{
			i_root = com.db4o.Tree.add(i_root, a_patch);
		}

		internal virtual com.db4o.Tree getRoot()
		{
			return i_root;
		}

		internal virtual void commit()
		{
			i_index.commit(this);
		}

		internal virtual void rollback()
		{
			i_index.rollback(this);
		}

		internal virtual void merge(com.db4o.IxFieldTransaction a_ft)
		{
			com.db4o.Tree otherRoot = a_ft.getRoot();
			if (otherRoot != null)
			{
				otherRoot.traverseFromLeaves(this);
			}
		}

		/// <summary>
		/// Visitor functionality for merge:<br />
		/// Add
		/// </summary>
		public virtual void visit(object obj)
		{
			if (obj is com.db4o.IxPatch)
			{
				com.db4o.IxPatch tree = (com.db4o.IxPatch)obj;
				if (tree.i_queue != null)
				{
					com.db4o.Queue4 queue = tree.i_queue;
					tree.i_queue = null;
					while ((tree = (com.db4o.IxPatch)queue.next()) != null)
					{
						tree.i_queue = null;
						addPatchToRoot(tree);
					}
				}
				else
				{
					addPatchToRoot(tree);
				}
			}
		}

		private void addPatchToRoot(com.db4o.IxPatch tree)
		{
			if (tree.i_version != i_version)
			{
				tree.beginMerge();
				tree.handler().prepareComparison(tree.handler().indexObject(i_trans, tree.i_value
					));
				if (i_root == null)
				{
					i_root = tree;
				}
				else
				{
					i_root = i_root.add(tree);
				}
			}
		}

		internal virtual int countLeaves()
		{
			if (i_root == null)
			{
				return 0;
			}
			int[] leaves = { 0 };
			i_root.traverse(new _AnonymousInnerClass84(this, leaves));
			return leaves[0];
		}

		private sealed class _AnonymousInnerClass84 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass84(IxFieldTransaction _enclosing, int[] leaves)
			{
				this._enclosing = _enclosing;
				this.leaves = leaves;
			}

			public void visit(object a_object)
			{
				leaves[0]++;
			}

			private readonly IxFieldTransaction _enclosing;

			private readonly int[] leaves;
		}

		public virtual void setRoot(com.db4o.Tree a_tree)
		{
			i_root = a_tree;
		}

		public override string ToString()
		{
			j4o.lang.StringBuffer sb = new j4o.lang.StringBuffer();
			sb.append("IxFieldTransaction ");
			sb.append(j4o.lang.JavaSystem.identityHashCode(this));
			if (i_root == null)
			{
				sb.append("\n    Empty");
			}
			else
			{
				i_root.traverse(new _AnonymousInnerClass103(this, sb));
			}
			return sb.ToString();
		}

		private sealed class _AnonymousInnerClass103 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass103(IxFieldTransaction _enclosing, j4o.lang.StringBuffer
				 sb)
			{
				this._enclosing = _enclosing;
				this.sb = sb;
			}

			public void visit(object a_object)
			{
				sb.append("\n");
				sb.append(a_object.ToString());
			}

			private readonly IxFieldTransaction _enclosing;

			private readonly j4o.lang.StringBuffer sb;
		}
	}
}
