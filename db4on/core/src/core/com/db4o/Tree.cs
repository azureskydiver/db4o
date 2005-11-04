namespace com.db4o
{
	/// <exclude></exclude>
	public abstract class Tree : j4o.lang.Cloneable, com.db4o.Readable
	{
		public com.db4o.Tree i_preceding;

		public int i_size = 1;

		public com.db4o.Tree i_subsequent;

		public static com.db4o.Tree add(com.db4o.Tree a_old, com.db4o.Tree a_new)
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

		public virtual com.db4o.Tree add(com.db4o.Tree a_new, int a_cmp)
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

		public com.db4o.Tree balance()
		{
			int cmp = i_subsequent.nodes() - i_preceding.nodes();
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
					setSizeOwnPrecedingSubsequent();
					return this;
				}
			}
		}

		public virtual com.db4o.Tree balanceCheckNulls()
		{
			if (i_subsequent == null)
			{
				if (i_preceding == null)
				{
					setSizeOwn();
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
				traverse(new _AnonymousInnerClass93(this, length));
				return length[0];
			}
			else
			{
				return com.db4o.YapConst.YAPINT_LENGTH + (size() * ownLength());
			}
		}

		private sealed class _AnonymousInnerClass93 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass93(Tree _enclosing, int[] length)
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

		public virtual void calculateSize()
		{
			if (i_preceding == null)
			{
				if (i_subsequent == null)
				{
					setSizeOwn();
				}
				else
				{
					setSizeOwnSubsequent();
				}
			}
			else
			{
				if (i_subsequent == null)
				{
					setSizeOwnPreceding();
				}
				else
				{
					setSizeOwnPrecedingSubsequent();
				}
			}
		}

		/// <summary>
		/// returns 0, if keys are equal
		/// returns negative if compared key (a_to) is smaller
		/// returns positive if compared key (a_to) is greater
		/// </summary>
		public abstract int compare(com.db4o.Tree a_to);

		public static com.db4o.Tree deepClone(com.db4o.Tree a_tree, object a_param)
		{
			if (a_tree == null)
			{
				return null;
			}
			com.db4o.Tree newNode = a_tree.deepClone(a_param);
			newNode.i_size = a_tree.i_size;
			newNode.nodes(a_tree.nodes());
			newNode.i_preceding = com.db4o.Tree.deepClone(a_tree.i_preceding, a_param);
			newNode.i_subsequent = com.db4o.Tree.deepClone(a_tree.i_subsequent, a_param);
			return newNode;
		}

		public virtual com.db4o.Tree deepClone(object a_param)
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

		public virtual bool duplicates()
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

		public static com.db4o.Tree findGreaterOrEqual(com.db4o.Tree a_in, com.db4o.Tree 
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

		public static com.db4o.Tree findSmaller(com.db4o.Tree a_in, com.db4o.Tree a_node)
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

		public com.db4o.Tree first()
		{
			if (i_preceding == null)
			{
				return this;
			}
			return i_preceding.first();
		}

		internal virtual void isDuplicateOf(com.db4o.Tree a_tree)
		{
			i_size = 0;
		}

		/// <returns>the number of nodes in this tree for balancing</returns>
		public virtual int nodes()
		{
			return i_size;
		}

		public virtual void nodes(int count)
		{
		}

		public virtual int ownLength()
		{
			throw com.db4o.YapConst.virtualException();
		}

		public virtual int ownSize()
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

		public virtual com.db4o.Tree remove()
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

		public virtual void removeChildren()
		{
			i_preceding = null;
			i_subsequent = null;
			setSizeOwn();
		}

		public virtual com.db4o.Tree removeFirst()
		{
			if (i_preceding == null)
			{
				return i_subsequent;
			}
			i_preceding = i_preceding.removeFirst();
			calculateSize();
			return this;
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

		public com.db4o.Tree removeNode(com.db4o.Tree a_tree)
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

		public com.db4o.Tree rotateLeft()
		{
			com.db4o.Tree tree = i_subsequent;
			i_subsequent = tree.i_preceding;
			calculateSize();
			tree.i_preceding = this;
			if (tree.i_subsequent == null)
			{
				tree.setSizeOwnPlus(this);
			}
			else
			{
				tree.setSizeOwnPlus(this, tree.i_subsequent);
			}
			return tree;
		}

		public com.db4o.Tree rotateRight()
		{
			com.db4o.Tree tree = i_preceding;
			i_preceding = tree.i_subsequent;
			calculateSize();
			tree.i_subsequent = this;
			if (tree.i_preceding == null)
			{
				tree.setSizeOwnPlus(this);
			}
			else
			{
				tree.setSizeOwnPlus(this, tree.i_preceding);
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

		public virtual void setSizeOwn()
		{
			i_size = ownSize();
		}

		public virtual void setSizeOwnPrecedingSubsequent()
		{
			i_size = ownSize() + i_preceding.i_size + i_subsequent.i_size;
		}

		public virtual void setSizeOwnPreceding()
		{
			i_size = ownSize() + i_preceding.i_size;
		}

		public virtual void setSizeOwnSubsequent()
		{
			i_size = ownSize() + i_subsequent.i_size;
		}

		public virtual void setSizeOwnPlus(com.db4o.Tree tree)
		{
			i_size = ownSize() + tree.i_size;
		}

		public virtual void setSizeOwnPlus(com.db4o.Tree tree1, com.db4o.Tree tree2)
		{
			i_size = ownSize() + tree1.i_size + tree2.i_size;
		}

		public static int size(com.db4o.Tree a_tree)
		{
			if (a_tree == null)
			{
				return 0;
			}
			return a_tree.size();
		}

		/// <returns>the number of objects represented.</returns>
		public virtual int size()
		{
			return i_size;
		}

		public static void traverse(com.db4o.Tree tree, com.db4o.foundation.Visitor4 visitor
			)
		{
			if (tree == null)
			{
				return;
			}
			tree.traverse(visitor);
		}

		public void traverse(com.db4o.foundation.Visitor4 a_visitor)
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

		public void traverseFromLeaves(com.db4o.foundation.Visitor4 a_visitor)
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

		public static void write(com.db4o.YapWriter a_writer, com.db4o.Tree a_tree)
		{
			if (a_tree == null)
			{
				a_writer.writeInt(0);
			}
			else
			{
				a_writer.writeInt(a_tree.size());
				a_tree.traverse(new _AnonymousInnerClass447(a_writer));
			}
		}

		private sealed class _AnonymousInnerClass447 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass447(com.db4o.YapWriter a_writer)
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
