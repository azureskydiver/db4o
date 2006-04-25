namespace com.db4o
{
	/// <exclude></exclude>
	public abstract class Tree : com.db4o.foundation.ShallowClone, com.db4o.Readable
	{
		public com.db4o.Tree _preceding;

		public int _size = 1;

		public com.db4o.Tree _subsequent;

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

		/// <summary>
		/// On adding a node to a tree, if it already exists, and if
		/// Tree#duplicates() returns false, #isDuplicateOf() will be
		/// called.
		/// </summary>
		/// <remarks>
		/// On adding a node to a tree, if it already exists, and if
		/// Tree#duplicates() returns false, #isDuplicateOf() will be
		/// called. The added node can then be asked for the node that
		/// prevails in the tree using #duplicateOrThis(). This mechanism
		/// allows doing find() and add() in one run.
		/// </remarks>
		public virtual com.db4o.Tree add(com.db4o.Tree a_new, int a_cmp)
		{
			if (a_cmp < 0)
			{
				if (_subsequent == null)
				{
					_subsequent = a_new;
					_size++;
				}
				else
				{
					_subsequent = _subsequent.add(a_new);
					if (_preceding == null)
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
					if (_preceding == null)
					{
						_preceding = a_new;
						_size++;
					}
					else
					{
						_preceding = _preceding.add(a_new);
						if (_subsequent == null)
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

		/// <summary>
		/// On adding a node to a tree, if it already exists, and if
		/// Tree#duplicates() returns false, #isDuplicateOf() will be
		/// called.
		/// </summary>
		/// <remarks>
		/// On adding a node to a tree, if it already exists, and if
		/// Tree#duplicates() returns false, #isDuplicateOf() will be
		/// called. The added node can then be asked for the node that
		/// prevails in the tree using #duplicateOrThis(). This mechanism
		/// allows doing find() and add() in one run.
		/// </remarks>
		public virtual com.db4o.Tree duplicateOrThis()
		{
			if (_size == 0)
			{
				return _preceding;
			}
			return this;
		}

		public com.db4o.Tree balance()
		{
			int cmp = _subsequent.nodes() - _preceding.nodes();
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
			if (_subsequent == null)
			{
				if (_preceding == null)
				{
					setSizeOwn();
					return this;
				}
				return rotateRight();
			}
			else
			{
				if (_preceding == null)
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
				traverse(new _AnonymousInnerClass115(this, length));
				return length[0];
			}
			else
			{
				return com.db4o.YapConst.YAPINT_LENGTH + (size() * ownLength());
			}
		}

		private sealed class _AnonymousInnerClass115 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass115(Tree _enclosing, int[] length)
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
			if (_preceding == null)
			{
				if (_subsequent == null)
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
				if (_subsequent == null)
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
		/// uses this - other
		/// returns positive if this is greater than a_to
		/// returns negative if this is smaller than a_to
		/// </summary>
		public abstract int compare(com.db4o.Tree a_to);

		public static com.db4o.Tree deepClone(com.db4o.Tree a_tree, object a_param)
		{
			if (a_tree == null)
			{
				return null;
			}
			com.db4o.Tree newNode = a_tree.deepClone(a_param);
			newNode._size = a_tree._size;
			newNode.nodes(a_tree.nodes());
			newNode._preceding = com.db4o.Tree.deepClone(a_tree._preceding, a_param);
			newNode._subsequent = com.db4o.Tree.deepClone(a_tree._subsequent, a_param);
			return newNode;
		}

		public virtual com.db4o.Tree deepClone(object a_param)
		{
			return (com.db4o.Tree)this.shallowClone();
		}

		public virtual bool duplicates()
		{
			return true;
		}

		internal com.db4o.Tree filter(com.db4o.VisitorBoolean a_filter)
		{
			if (_preceding != null)
			{
				_preceding = _preceding.filter(a_filter);
			}
			if (_subsequent != null)
			{
				_subsequent = _subsequent.filter(a_filter);
			}
			if (!a_filter.isVisit(this))
			{
				return remove();
			}
			return this;
		}

		public static com.db4o.Tree find(com.db4o.Tree a_in, com.db4o.Tree a_tree)
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
				if (_subsequent != null)
				{
					return _subsequent.find(a_tree);
				}
			}
			else
			{
				if (cmp > 0)
				{
					if (_preceding != null)
					{
						return _preceding.find(a_tree);
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
					com.db4o.Tree node = findGreaterOrEqual(a_in._preceding, a_finder);
					if (node != null)
					{
						return node;
					}
					return a_in;
				}
				else
				{
					return findGreaterOrEqual(a_in._subsequent, a_finder);
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
				com.db4o.Tree node = findSmaller(a_in._subsequent, a_node);
				if (node != null)
				{
					return node;
				}
				return a_in;
			}
			else
			{
				return findSmaller(a_in._preceding, a_node);
			}
		}

		public com.db4o.Tree first()
		{
			if (_preceding == null)
			{
				return this;
			}
			return _preceding.first();
		}

		internal virtual void isDuplicateOf(com.db4o.Tree a_tree)
		{
			_size = 0;
			_preceding = a_tree;
		}

		/// <returns>the number of nodes in this tree for balancing</returns>
		public virtual int nodes()
		{
			return _size;
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
			if (_subsequent != null && _preceding != null)
			{
				_subsequent = _subsequent.rotateSmallestUp();
				_subsequent._preceding = _preceding;
				_subsequent.calculateSize();
				return _subsequent;
			}
			if (_subsequent != null)
			{
				return _subsequent;
			}
			return _preceding;
		}

		public virtual void removeChildren()
		{
			_preceding = null;
			_subsequent = null;
			setSizeOwn();
		}

		public virtual com.db4o.Tree removeFirst()
		{
			if (_preceding == null)
			{
				return _subsequent;
			}
			_preceding = _preceding.removeFirst();
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
				if (_preceding != null)
				{
					_preceding = _preceding.removeLike(a_find);
				}
			}
			else
			{
				if (_subsequent != null)
				{
					_subsequent = _subsequent.removeLike(a_find);
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
				if (_preceding != null)
				{
					_preceding = _preceding.removeNode(a_tree);
				}
			}
			if (cmp <= 0)
			{
				if (_subsequent != null)
				{
					_subsequent = _subsequent.removeNode(a_tree);
				}
			}
			calculateSize();
			return this;
		}

		public com.db4o.Tree rotateLeft()
		{
			com.db4o.Tree tree = _subsequent;
			_subsequent = tree._preceding;
			calculateSize();
			tree._preceding = this;
			if (tree._subsequent == null)
			{
				tree.setSizeOwnPlus(this);
			}
			else
			{
				tree.setSizeOwnPlus(this, tree._subsequent);
			}
			return tree;
		}

		public com.db4o.Tree rotateRight()
		{
			com.db4o.Tree tree = _preceding;
			_preceding = tree._subsequent;
			calculateSize();
			tree._subsequent = this;
			if (tree._preceding == null)
			{
				tree.setSizeOwnPlus(this);
			}
			else
			{
				tree.setSizeOwnPlus(this, tree._preceding);
			}
			return tree;
		}

		private com.db4o.Tree rotateSmallestUp()
		{
			if (_preceding != null)
			{
				_preceding = _preceding.rotateSmallestUp();
				return rotateRight();
			}
			return this;
		}

		public virtual void setSizeOwn()
		{
			_size = ownSize();
		}

		public virtual void setSizeOwnPrecedingSubsequent()
		{
			_size = ownSize() + _preceding._size + _subsequent._size;
		}

		public virtual void setSizeOwnPreceding()
		{
			_size = ownSize() + _preceding._size;
		}

		public virtual void setSizeOwnSubsequent()
		{
			_size = ownSize() + _subsequent._size;
		}

		public virtual void setSizeOwnPlus(com.db4o.Tree tree)
		{
			_size = ownSize() + tree._size;
		}

		public virtual void setSizeOwnPlus(com.db4o.Tree tree1, com.db4o.Tree tree2)
		{
			_size = ownSize() + tree1._size + tree2._size;
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
			return _size;
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
			if (_preceding != null)
			{
				_preceding.traverse(a_visitor);
			}
			a_visitor.visit(this);
			if (_subsequent != null)
			{
				_subsequent.traverse(a_visitor);
			}
		}

		public void traverseFromLeaves(com.db4o.foundation.Visitor4 a_visitor)
		{
			if (_preceding != null)
			{
				_preceding.traverseFromLeaves(a_visitor);
			}
			if (_subsequent != null)
			{
				_subsequent.traverseFromLeaves(a_visitor);
			}
			a_visitor.visit(this);
		}

		internal virtual bool variableLength()
		{
			throw com.db4o.YapConst.virtualException();
		}

		public static void write(com.db4o.YapReader a_writer, com.db4o.Tree a_tree)
		{
			write(a_writer, a_tree, a_tree == null ? 0 : a_tree.size());
		}

		public static void write(com.db4o.YapReader a_writer, com.db4o.Tree a_tree, int size
			)
		{
			if (a_tree == null)
			{
				a_writer.writeInt(0);
				return;
			}
			a_writer.writeInt(size);
			a_tree.traverse(new _AnonymousInnerClass472(a_writer));
		}

		private sealed class _AnonymousInnerClass472 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass472(com.db4o.YapReader a_writer)
			{
				this.a_writer = a_writer;
			}

			public void visit(object a_object)
			{
				((com.db4o.Tree)a_object).write(a_writer);
			}

			private readonly com.db4o.YapReader a_writer;
		}

		public virtual void write(com.db4o.YapReader a_writer)
		{
			throw com.db4o.YapConst.virtualException();
		}

		protected virtual com.db4o.Tree shallowCloneInternal(com.db4o.Tree tree)
		{
			tree._preceding = _preceding;
			tree._size = _size;
			tree._subsequent = _subsequent;
			return tree;
		}

		public abstract object shallowClone();
	}
}
