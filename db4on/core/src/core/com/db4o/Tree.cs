namespace com.db4o
{
	/// <exclude></exclude>
	public abstract class Tree : com.db4o.foundation.ShallowClone, com.db4o.Readable
	{
		public com.db4o.Tree _preceding;

		public int _size = 1;

		public com.db4o.Tree _subsequent;

		public static com.db4o.Tree Add(com.db4o.Tree a_old, com.db4o.Tree a_new)
		{
			if (a_old == null)
			{
				return a_new;
			}
			return a_old.Add(a_new);
		}

		public virtual com.db4o.Tree Add(com.db4o.Tree a_new)
		{
			return Add(a_new, Compare(a_new));
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
		public virtual com.db4o.Tree Add(com.db4o.Tree a_new, int a_cmp)
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
					_subsequent = _subsequent.Add(a_new);
					if (_preceding == null)
					{
						return RotateLeft();
					}
					return Balance();
				}
			}
			else
			{
				if (a_cmp > 0 || a_new.Duplicates())
				{
					if (_preceding == null)
					{
						_preceding = a_new;
						_size++;
					}
					else
					{
						_preceding = _preceding.Add(a_new);
						if (_subsequent == null)
						{
							return RotateRight();
						}
						return Balance();
					}
				}
				else
				{
					a_new.IsDuplicateOf(this);
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
		public virtual com.db4o.Tree DuplicateOrThis()
		{
			if (_size == 0)
			{
				return _preceding;
			}
			return this;
		}

		public com.db4o.Tree Balance()
		{
			int cmp = _subsequent.Nodes() - _preceding.Nodes();
			if (cmp < -2)
			{
				return RotateRight();
			}
			else
			{
				if (cmp > 2)
				{
					return RotateLeft();
				}
				else
				{
					SetSizeOwnPrecedingSubsequent();
					return this;
				}
			}
		}

		public virtual com.db4o.Tree BalanceCheckNulls()
		{
			if (_subsequent == null)
			{
				if (_preceding == null)
				{
					SetSizeOwn();
					return this;
				}
				return RotateRight();
			}
			else
			{
				if (_preceding == null)
				{
					return RotateLeft();
				}
			}
			return Balance();
		}

		public static int ByteCount(com.db4o.Tree a_tree)
		{
			if (a_tree == null)
			{
				return com.db4o.YapConst.INT_LENGTH;
			}
			return a_tree.ByteCount();
		}

		public int ByteCount()
		{
			if (VariableLength())
			{
				int[] length = new int[] { com.db4o.YapConst.INT_LENGTH };
				Traverse(new _AnonymousInnerClass114(this, length));
				return length[0];
			}
			return com.db4o.YapConst.INT_LENGTH + (Size() * OwnLength());
		}

		private sealed class _AnonymousInnerClass114 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass114(Tree _enclosing, int[] length)
			{
				this._enclosing = _enclosing;
				this.length = length;
			}

			public void Visit(object obj)
			{
				length[0] += ((com.db4o.Tree)obj).OwnLength();
			}

			private readonly Tree _enclosing;

			private readonly int[] length;
		}

		public virtual void CalculateSize()
		{
			if (_preceding == null)
			{
				if (_subsequent == null)
				{
					SetSizeOwn();
				}
				else
				{
					SetSizeOwnSubsequent();
				}
			}
			else
			{
				if (_subsequent == null)
				{
					SetSizeOwnPreceding();
				}
				else
				{
					SetSizeOwnPrecedingSubsequent();
				}
			}
		}

		/// <summary>
		/// returns 0, if keys are equal
		/// uses this - other
		/// returns positive if this is greater than a_to
		/// returns negative if this is smaller than a_to
		/// </summary>
		public abstract int Compare(com.db4o.Tree a_to);

		public static com.db4o.Tree DeepClone(com.db4o.Tree a_tree, object a_param)
		{
			if (a_tree == null)
			{
				return null;
			}
			com.db4o.Tree newNode = a_tree.DeepClone(a_param);
			newNode._size = a_tree._size;
			newNode.Nodes(a_tree.Nodes());
			newNode._preceding = com.db4o.Tree.DeepClone(a_tree._preceding, a_param);
			newNode._subsequent = com.db4o.Tree.DeepClone(a_tree._subsequent, a_param);
			return newNode;
		}

		public virtual com.db4o.Tree DeepClone(object a_param)
		{
			return (com.db4o.Tree)this.ShallowClone();
		}

		public virtual bool Duplicates()
		{
			return true;
		}

		internal com.db4o.Tree Filter(com.db4o.VisitorBoolean a_filter)
		{
			if (_preceding != null)
			{
				_preceding = _preceding.Filter(a_filter);
			}
			if (_subsequent != null)
			{
				_subsequent = _subsequent.Filter(a_filter);
			}
			if (!a_filter.IsVisit(this))
			{
				return Remove();
			}
			return this;
		}

		public static com.db4o.Tree Find(com.db4o.Tree a_in, com.db4o.Tree a_tree)
		{
			if (a_in == null)
			{
				return null;
			}
			return a_in.Find(a_tree);
		}

		public com.db4o.Tree Find(com.db4o.Tree a_tree)
		{
			int cmp = Compare(a_tree);
			if (cmp < 0)
			{
				if (_subsequent != null)
				{
					return _subsequent.Find(a_tree);
				}
			}
			else
			{
				if (cmp > 0)
				{
					if (_preceding != null)
					{
						return _preceding.Find(a_tree);
					}
				}
				else
				{
					return this;
				}
			}
			return null;
		}

		public static com.db4o.Tree FindGreaterOrEqual(com.db4o.Tree a_in, com.db4o.Tree 
			a_finder)
		{
			if (a_in == null)
			{
				return null;
			}
			int cmp = a_in.Compare(a_finder);
			if (cmp == 0)
			{
				return a_in;
			}
			if (cmp > 0)
			{
				com.db4o.Tree node = FindGreaterOrEqual(a_in._preceding, a_finder);
				if (node != null)
				{
					return node;
				}
				return a_in;
			}
			return FindGreaterOrEqual(a_in._subsequent, a_finder);
		}

		public static com.db4o.Tree FindSmaller(com.db4o.Tree a_in, com.db4o.Tree a_node)
		{
			if (a_in == null)
			{
				return null;
			}
			int cmp = a_in.Compare(a_node);
			if (cmp < 0)
			{
				com.db4o.Tree node = FindSmaller(a_in._subsequent, a_node);
				if (node != null)
				{
					return node;
				}
				return a_in;
			}
			return FindSmaller(a_in._preceding, a_node);
		}

		public com.db4o.Tree First()
		{
			if (_preceding == null)
			{
				return this;
			}
			return _preceding.First();
		}

		internal virtual void IsDuplicateOf(com.db4o.Tree a_tree)
		{
			_size = 0;
			_preceding = a_tree;
		}

		/// <returns>the number of nodes in this tree for balancing</returns>
		public virtual int Nodes()
		{
			return _size;
		}

		public virtual void Nodes(int count)
		{
		}

		public virtual int OwnLength()
		{
			throw com.db4o.inside.Exceptions4.VirtualException();
		}

		public virtual int OwnSize()
		{
			return 1;
		}

		internal static com.db4o.Tree Read(com.db4o.Tree a_tree, com.db4o.YapReader a_bytes
			)
		{
			throw com.db4o.inside.Exceptions4.VirtualException();
		}

		public virtual object Read(com.db4o.YapReader a_bytes)
		{
			throw com.db4o.inside.Exceptions4.VirtualException();
		}

		public virtual com.db4o.Tree Remove()
		{
			if (_subsequent != null && _preceding != null)
			{
				_subsequent = _subsequent.RotateSmallestUp();
				_subsequent._preceding = _preceding;
				_subsequent.CalculateSize();
				return _subsequent;
			}
			if (_subsequent != null)
			{
				return _subsequent;
			}
			return _preceding;
		}

		public virtual void RemoveChildren()
		{
			_preceding = null;
			_subsequent = null;
			SetSizeOwn();
		}

		public virtual com.db4o.Tree RemoveFirst()
		{
			if (_preceding == null)
			{
				return _subsequent;
			}
			_preceding = _preceding.RemoveFirst();
			CalculateSize();
			return this;
		}

		public static com.db4o.Tree RemoveLike(com.db4o.Tree from, com.db4o.Tree a_find)
		{
			if (from == null)
			{
				return null;
			}
			return from.RemoveLike(a_find);
		}

		public com.db4o.Tree RemoveLike(com.db4o.Tree a_find)
		{
			int cmp = Compare(a_find);
			if (cmp == 0)
			{
				return Remove();
			}
			if (cmp > 0)
			{
				if (_preceding != null)
				{
					_preceding = _preceding.RemoveLike(a_find);
				}
			}
			else
			{
				if (_subsequent != null)
				{
					_subsequent = _subsequent.RemoveLike(a_find);
				}
			}
			CalculateSize();
			return this;
		}

		public com.db4o.Tree RemoveNode(com.db4o.Tree a_tree)
		{
			if (this == a_tree)
			{
				return Remove();
			}
			int cmp = Compare(a_tree);
			if (cmp >= 0)
			{
				if (_preceding != null)
				{
					_preceding = _preceding.RemoveNode(a_tree);
				}
			}
			if (cmp <= 0)
			{
				if (_subsequent != null)
				{
					_subsequent = _subsequent.RemoveNode(a_tree);
				}
			}
			CalculateSize();
			return this;
		}

		public com.db4o.Tree RotateLeft()
		{
			com.db4o.Tree tree = _subsequent;
			_subsequent = tree._preceding;
			CalculateSize();
			tree._preceding = this;
			if (tree._subsequent == null)
			{
				tree.SetSizeOwnPlus(this);
			}
			else
			{
				tree.SetSizeOwnPlus(this, tree._subsequent);
			}
			return tree;
		}

		public com.db4o.Tree RotateRight()
		{
			com.db4o.Tree tree = _preceding;
			_preceding = tree._subsequent;
			CalculateSize();
			tree._subsequent = this;
			if (tree._preceding == null)
			{
				tree.SetSizeOwnPlus(this);
			}
			else
			{
				tree.SetSizeOwnPlus(this, tree._preceding);
			}
			return tree;
		}

		private com.db4o.Tree RotateSmallestUp()
		{
			if (_preceding != null)
			{
				_preceding = _preceding.RotateSmallestUp();
				return RotateRight();
			}
			return this;
		}

		public virtual void SetSizeOwn()
		{
			_size = OwnSize();
		}

		public virtual void SetSizeOwnPrecedingSubsequent()
		{
			_size = OwnSize() + _preceding._size + _subsequent._size;
		}

		public virtual void SetSizeOwnPreceding()
		{
			_size = OwnSize() + _preceding._size;
		}

		public virtual void SetSizeOwnSubsequent()
		{
			_size = OwnSize() + _subsequent._size;
		}

		public virtual void SetSizeOwnPlus(com.db4o.Tree tree)
		{
			_size = OwnSize() + tree._size;
		}

		public virtual void SetSizeOwnPlus(com.db4o.Tree tree1, com.db4o.Tree tree2)
		{
			_size = OwnSize() + tree1._size + tree2._size;
		}

		public static int Size(com.db4o.Tree a_tree)
		{
			if (a_tree == null)
			{
				return 0;
			}
			return a_tree.Size();
		}

		/// <returns>the number of objects represented.</returns>
		public virtual int Size()
		{
			return _size;
		}

		public static void Traverse(com.db4o.Tree tree, com.db4o.foundation.Visitor4 visitor
			)
		{
			if (tree == null)
			{
				return;
			}
			tree.Traverse(visitor);
		}

		public void Traverse(com.db4o.foundation.Visitor4 a_visitor)
		{
			if (_preceding != null)
			{
				_preceding.Traverse(a_visitor);
			}
			a_visitor.Visit(this);
			if (_subsequent != null)
			{
				_subsequent.Traverse(a_visitor);
			}
		}

		public void TraverseFromLeaves(com.db4o.foundation.Visitor4 a_visitor)
		{
			if (_preceding != null)
			{
				_preceding.TraverseFromLeaves(a_visitor);
			}
			if (_subsequent != null)
			{
				_subsequent.TraverseFromLeaves(a_visitor);
			}
			a_visitor.Visit(this);
		}

		internal virtual bool VariableLength()
		{
			throw com.db4o.inside.Exceptions4.VirtualException();
		}

		public static void Write(com.db4o.YapReader a_writer, com.db4o.Tree a_tree)
		{
			Write(a_writer, a_tree, a_tree == null ? 0 : a_tree.Size());
		}

		public static void Write(com.db4o.YapReader a_writer, com.db4o.Tree a_tree, int size
			)
		{
			if (a_tree == null)
			{
				a_writer.WriteInt(0);
				return;
			}
			a_writer.WriteInt(size);
			a_tree.Traverse(new _AnonymousInnerClass467(a_writer));
		}

		private sealed class _AnonymousInnerClass467 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass467(com.db4o.YapReader a_writer)
			{
				this.a_writer = a_writer;
			}

			public void Visit(object a_object)
			{
				((com.db4o.Tree)a_object).Write(a_writer);
			}

			private readonly com.db4o.YapReader a_writer;
		}

		public virtual void Write(com.db4o.YapReader a_writer)
		{
			throw com.db4o.inside.Exceptions4.VirtualException();
		}

		protected virtual com.db4o.Tree ShallowCloneInternal(com.db4o.Tree tree)
		{
			tree._preceding = _preceding;
			tree._size = _size;
			tree._subsequent = _subsequent;
			return tree;
		}

		public abstract object ShallowClone();
	}
}
