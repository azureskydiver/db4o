namespace com.db4o.inside.btree
{
	/// <exclude></exclude>
	public class BTree : com.db4o.YapMeta
	{
		internal readonly com.db4o.inside.ix.Indexable4 _keyHandler;

		internal readonly com.db4o.inside.ix.Indexable4 _valueHandler;

		internal com.db4o.inside.btree.BTreeNode _root;

		/// <summary>All instantiated nodes are held in this tree.</summary>
		/// <remarks>
		/// All instantiated nodes are held in this tree. From here the nodes
		/// are only referred to by weak references, so they can be garbage
		/// collected automatically, as soon as they are no longer referenced
		/// from the hard references in the BTreeNode#_children array.
		/// </remarks>
		private com.db4o.TreeIntWeakObject _nodes;

		private int _size;

		public BTree(int id, com.db4o.inside.ix.Indexable4 keyHandler, com.db4o.inside.ix.Indexable4
			 valueHandler)
		{
			_keyHandler = keyHandler;
			_valueHandler = (valueHandler == null) ? com.db4o.Null.INSTANCE : valueHandler;
			if (id > 0)
			{
				setID(id);
				setStateDeactivated();
			}
			else
			{
				_root = new com.db4o.inside.btree.BTreeNode(this);
				setStateDirty();
			}
		}

		public virtual void add(com.db4o.Transaction trans, object value)
		{
			trans.dirtyBTree(this);
			_keyHandler.prepareComparison(value);
			ensureActive(trans);
			object addResult = _root.add(trans);
			if (addResult is com.db4o.inside.btree.BTreeNode)
			{
				_root = _root.newRoot(trans, (com.db4o.inside.btree.BTreeNode)addResult);
				setStateDirty();
			}
			_size++;
		}

		public virtual void commit(com.db4o.Transaction trans)
		{
			if (_nodes != null)
			{
				_nodes = _nodes.traverseRemoveEmpty(new _AnonymousInnerClass56(this, trans));
			}
			write(trans);
		}

		private sealed class _AnonymousInnerClass56 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass56(BTree _enclosing, com.db4o.Transaction trans)
			{
				this._enclosing = _enclosing;
				this.trans = trans;
			}

			public void visit(object obj)
			{
				((com.db4o.inside.btree.BTreeNode)obj).commit(trans);
			}

			private readonly BTree _enclosing;

			private readonly com.db4o.Transaction trans;
		}

		public virtual void rollback(com.db4o.Transaction trans)
		{
			if (_nodes == null)
			{
				return;
			}
			_nodes = _nodes.traverseRemoveEmpty(new _AnonymousInnerClass69(this, trans));
		}

		private sealed class _AnonymousInnerClass69 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass69(BTree _enclosing, com.db4o.Transaction trans)
			{
				this._enclosing = _enclosing;
				this.trans = trans;
			}

			public void visit(object obj)
			{
				((com.db4o.inside.btree.BTreeNode)obj).rollback(trans);
			}

			private readonly BTree _enclosing;

			private readonly com.db4o.Transaction trans;
		}

		private void ensureActive(com.db4o.Transaction trans)
		{
			if (!isActive())
			{
				read(trans.systemTransaction());
			}
		}

		public override byte getIdentifier()
		{
			return com.db4o.YapConst.BTREE;
		}

		public override int ownLength()
		{
			return com.db4o.YapConst.OBJECT_LENGTH + com.db4o.YapConst.YAPINT_LENGTH + com.db4o.YapConst
				.YAPID_LENGTH;
		}

		internal virtual com.db4o.inside.btree.BTreeNode produceNode(int id)
		{
			com.db4o.TreeIntWeakObject tio = new com.db4o.TreeIntWeakObject(id);
			_nodes = (com.db4o.TreeIntWeakObject)com.db4o.Tree.add(_nodes, tio);
			tio = (com.db4o.TreeIntWeakObject)tio.duplicateOrThis();
			com.db4o.inside.btree.BTreeNode node = (com.db4o.inside.btree.BTreeNode)tio.getObject
				();
			if (node == null)
			{
				node = new com.db4o.inside.btree.BTreeNode(this, id);
				tio.setObject(node);
			}
			return node;
		}

		internal virtual void addNode(int id, com.db4o.inside.btree.BTreeNode node)
		{
			_nodes = (com.db4o.TreeIntWeakObject)com.db4o.Tree.add(_nodes, new com.db4o.TreeIntWeakObject
				(id, node));
		}

		public override void readThis(com.db4o.Transaction a_trans, com.db4o.YapReader a_reader
			)
		{
			_size = a_reader.readInt();
			_root = produceNode(a_reader.readInt());
		}

		public override void writeThis(com.db4o.Transaction trans, com.db4o.YapReader a_writer
			)
		{
			a_writer.writeInt(_size);
			a_writer.writeIDOf(trans, _root);
		}

		public virtual int size()
		{
			return _size;
		}

		public virtual void traverseKeys(com.db4o.Transaction trans, com.db4o.foundation.Visitor4
			 visitor)
		{
			if (_root == null)
			{
				return;
			}
			_root.traverseKeys(trans, visitor);
		}
	}
}
