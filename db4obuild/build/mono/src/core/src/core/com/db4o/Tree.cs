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
	/// <exclude></exclude>
	public abstract class Tree : j4o.lang.Cloneable, com.db4o.Readable
	{
		internal com.db4o.Tree i_preceding;

		internal int i_size = 1;

		internal com.db4o.Tree i_subsequent;

		internal static com.db4o.Tree add(com.db4o.Tree a_old, com.db4o.Tree a_new)
		{
			if (a_old == null)
			{
				return a_new;
			}
			return a_old.add(a_new);
		}

		public virtual com.db4o.Tree add(com.db4o.Tree a_new)
		{
			return add(a_new, compare(a_new));
		}

		internal virtual com.db4o.Tree add(com.db4o.Tree a_new, int a_cmp)
		{
			if (a_cmp < 0)
			{
				if (i_subsequent == null)
				{
					i_subsequent = a_new;
					i_size++;
				}
				else
				{
					i_subsequent = i_subsequent.add(a_new);
					if (i_preceding == null)
					{
						return rotateLeft();
					}
					else
					{
						return balance();
					}
				}
			}
			else
			{
				if (a_cmp > 0 || a_new.duplicates())
				{
					if (i_preceding == null)
					{
						i_preceding = a_new;
						i_size++;
					}
					else
					{
						i_preceding = i_preceding.add(a_new);
						if (i_subsequent == null)
						{
							return rotateRight();
						}
						else
						{
							return balance();
						}
					}
				}
				else
				{
					a_new.isDuplicateOf(this);
				}
			}
			return this;
		}

		internal com.db4o.Tree balance()
		{
			int cmp = i_subsequent.i_size - i_preceding.i_size;
			if (cmp < -2)
			{
				return rotateRight();
			}
			else
			{
				if (cmp > 2)
				{
					return rotateLeft();
				}
				else
				{
					i_size = i_preceding.i_size + i_subsequent.i_size + ownSize();
					return this;
				}
			}
		}

		internal com.db4o.Tree balanceCheckNulls()
		{
			if (i_subsequent == null)
			{
				if (i_preceding == null)
				{
					i_size = ownSize();
					return this;
				}
				return rotateRight();
			}
			else
			{
				if (i_preceding == null)
				{
					return rotateLeft();
				}
			}
			return balance();
		}

		public static int byteCount(com.db4o.Tree a_tree)
		{
			if (a_tree == null)
			{
				return com.db4o.YapConst.YAPINT_LENGTH;
			}
			return a_tree.byteCount();
		}

		public int byteCount()
		{
			if (variableLength())
			{
				int[] length = new int[] { com.db4o.YapConst.YAPINT_LENGTH };
				traverse(new _AnonymousInnerClass91(this, length));
				return length[0];
			}
			else
			{
				return com.db4o.YapConst.YAPINT_LENGTH + (size() * ownLength());
			}
		}

		private sealed class _AnonymousInnerClass91 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass91(Tree _enclosing, int[] length)
			{
				this._enclosing = _enclosing;
				this.length = length;
			}

			public void visit(object obj)
			{
				length[0] += ((com.db4o.Tree)obj).ownLength();
			}

			private readonly Tree _enclosing;

			private readonly int[] length;
		}

		internal virtual void calculateSize()
		{
			if (i_preceding == null)
			{
				if (i_subsequent == null)
				{
					i_size = ownSize();
				}
				else
				{
					i_size = i_subsequent.i_size + ownSize();
				}
			}
			else
			{
				if (i_subsequent == null)
				{
					i_size = i_preceding.i_size + ownSize();
				}
				else
				{
					i_size = i_preceding.i_size + i_subsequent.i_size + ownSize();
				}
			}
		}

		/// <summary>
		/// returns 0, if keys are equal
		/// returns negative if compared key (a_to) is smaller
		/// returns positive if compared key (a_to) is greater
		/// </summary>
		internal abstract int compare(com.db4o.Tree a_to);

		internal static com.db4o.Tree deepClone(com.db4o.Tree a_tree, object a_param)
		{
			if (a_tree == null)
			{
				return null;
			}
			com.db4o.Tree newNode = a_tree.deepClone(a_param);
			newNode.i_size = a_tree.i_size;
			newNode.i_preceding = com.db4o.Tree.deepClone(a_tree.i_preceding, a_param);
			newNode.i_subsequent = com.db4o.Tree.deepClone(a_tree.i_subsequent, a_param);
			return newNode;
		}

		internal virtual com.db4o.Tree deepClone(object a_param)
		{
			try
			{
				return (com.db4o.Tree)j4o.lang.JavaSystem.clone(this);
			}
			catch (j4o.lang.CloneNotSupportedException e)
			{
			}
			return null;
		}

		internal virtual bool duplicates()
		{
			return true;
		}

		internal com.db4o.Tree filter(com.db4o.VisitorBoolean a_filter)
		{
			if (i_preceding != null)
			{
				i_preceding = i_preceding.filter(a_filter);
			}
			if (i_subsequent != null)
			{
				i_subsequent = i_subsequent.filter(a_filter);
			}
			if (!a_filter.isVisit(this))
			{
				return remove();
			}
			return this;
		}

		internal static com.db4o.Tree find(com.db4o.Tree a_in, com.db4o.Tree a_tree)
		{
			if (a_in == null)
			{
				return null;
			}
			return a_in.find(a_tree);
		}

		public com.db4o.Tree find(com.db4o.Tree a_tree)
		{
			int cmp = compare(a_tree);
			if (cmp < 0)
			{
				if (i_subsequent != null)
				{
					return i_subsequent.find(a_tree);
				}
			}
			else
			{
				if (cmp > 0)
				{
					if (i_preceding != null)
					{
						return i_preceding.find(a_tree);
					}
				}
				else
				{
					return this;
				}
			}
			return null;
		}

		internal static com.db4o.Tree findGreaterOrEqual(com.db4o.Tree a_in, com.db4o.Tree
			 a_finder)
		{
			if (a_in == null)
			{
				return null;
			}
			int cmp = a_in.compare(a_finder);
			if (cmp == 0)
			{
				return a_in;
			}
			else
			{
				if (cmp > 0)
				{
					com.db4o.Tree node = findGreaterOrEqual(a_in.i_preceding, a_finder);
					if (node != null)
					{
						return node;
					}
					return a_in;
				}
				else
				{
					return findGreaterOrEqual(a_in.i_subsequent, a_finder);
				}
			}
		}

		internal static com.db4o.Tree findSmaller(com.db4o.Tree a_in, com.db4o.Tree a_node
			)
		{
			if (a_in == null)
			{
				return null;
			}
			int cmp = a_in.compare(a_node);
			if (cmp < 0)
			{
				com.db4o.Tree node = findSmaller(a_in.i_subsequent, a_node);
				if (node != null)
				{
					return node;
				}
				return a_in;
			}
			else
			{
				return findSmaller(a_in.i_preceding, a_node);
			}
		}

		internal virtual void isDuplicateOf(com.db4o.Tree a_tree)
		{
			i_size = 0;
		}

		internal virtual int ownLength()
		{
			throw com.db4o.YapConst.virtualException();
		}

		internal virtual int ownSize()
		{
			return 1;
		}

		internal static com.db4o.Tree read(com.db4o.Tree a_tree, com.db4o.YapReader a_bytes
			)
		{
			throw com.db4o.YapConst.virtualException();
		}

		public virtual object read(com.db4o.YapReader a_bytes)
		{
			throw com.db4o.YapConst.virtualException();
		}

		internal virtual com.db4o.Tree remove()
		{
			if (i_subsequent != null && i_preceding != null)
			{
				i_subsequent = i_subsequent.rotateSmallestUp();
				i_subsequent.i_preceding = i_preceding;
				i_subsequent.calculateSize();
				return i_subsequent;
			}
			if (i_subsequent != null)
			{
				return i_subsequent;
			}
			return i_preceding;
		}

		internal virtual void removeChildren()
		{
			i_preceding = null;
			i_subsequent = null;
			i_size = ownSize();
		}

		internal static com.db4o.Tree removeLike(com.db4o.Tree from, com.db4o.Tree a_find
			)
		{
			if (from == null)
			{
				return null;
			}
			return from.removeLike(a_find);
		}

		public com.db4o.Tree removeLike(com.db4o.Tree a_find)
		{
			int cmp = compare(a_find);
			if (cmp == 0)
			{
				return remove();
			}
			if (cmp > 0)
			{
				if (i_preceding != null)
				{
					i_preceding = i_preceding.removeLike(a_find);
				}
			}
			else
			{
				if (i_subsequent != null)
				{
					i_subsequent = i_subsequent.removeLike(a_find);
				}
			}
			calculateSize();
			return this;
		}

		internal com.db4o.Tree removeNode(com.db4o.Tree a_tree)
		{
			if (this == a_tree)
			{
				return remove();
			}
			int cmp = compare(a_tree);
			if (cmp >= 0)
			{
				if (i_preceding != null)
				{
					i_preceding = i_preceding.removeNode(a_tree);
				}
			}
			if (cmp <= 0)
			{
				if (i_subsequent != null)
				{
					i_subsequent = i_subsequent.removeNode(a_tree);
				}
			}
			calculateSize();
			return this;
		}

		internal com.db4o.Tree rotateLeft()
		{
			com.db4o.Tree tree = i_subsequent;
			i_subsequent = tree.i_preceding;
			calculateSize();
			tree.i_preceding = this;
			if (tree.i_subsequent == null)
			{
				tree.i_size = i_size + tree.ownSize();
			}
			else
			{
				tree.i_size = i_size + tree.i_subsequent.i_size + tree.ownSize();
			}
			return tree;
		}

		internal com.db4o.Tree rotateRight()
		{
			com.db4o.Tree tree = i_preceding;
			i_preceding = tree.i_subsequent;
			calculateSize();
			tree.i_subsequent = this;
			if (tree.i_preceding == null)
			{
				tree.i_size = i_size + tree.ownSize();
			}
			else
			{
				tree.i_size = i_size + tree.i_preceding.i_size + tree.ownSize();
			}
			return tree;
		}

		private com.db4o.Tree rotateSmallestUp()
		{
			if (i_preceding != null)
			{
				i_preceding = i_preceding.rotateSmallestUp();
				return rotateRight();
			}
			return this;
		}

		internal static int size(com.db4o.Tree a_tree)
		{
			if (a_tree == null)
			{
				return 0;
			}
			return a_tree.size();
		}

		public virtual int size()
		{
			return i_size;
		}

		public void traverse(com.db4o.Visitor4 a_visitor)
		{
			if (i_preceding != null)
			{
				i_preceding.traverse(a_visitor);
			}
			a_visitor.visit(this);
			if (i_subsequent != null)
			{
				i_subsequent.traverse(a_visitor);
			}
		}

		internal void traverseFromLeaves(com.db4o.Visitor4 a_visitor)
		{
			if (i_preceding != null)
			{
				i_preceding.traverseFromLeaves(a_visitor);
			}
			if (i_subsequent != null)
			{
				i_subsequent.traverseFromLeaves(a_visitor);
			}
			a_visitor.visit(this);
		}

		internal virtual bool variableLength()
		{
			throw com.db4o.YapConst.virtualException();
		}

		internal static void write(com.db4o.YapWriter a_writer, com.db4o.Tree a_tree)
		{
			if (a_tree == null)
			{
				a_writer.writeInt(0);
			}
			else
			{
				a_writer.writeInt(a_tree.size());
				a_tree.traverse(new _AnonymousInnerClass383(a_writer));
			}
		}

		private sealed class _AnonymousInnerClass383 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass383(com.db4o.YapWriter a_writer)
			{
				this.a_writer = a_writer;
			}

			public void visit(object a_object)
			{
				((com.db4o.Tree)a_object).write(a_writer);
			}

			private readonly com.db4o.YapWriter a_writer;
		}

		public virtual void write(com.db4o.YapWriter a_writer)
		{
			throw com.db4o.YapConst.virtualException();
		}
	}
}
