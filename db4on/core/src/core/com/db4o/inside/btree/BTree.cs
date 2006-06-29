namespace com.db4o.inside.btree
{
	/// <exclude></exclude>
	public class BTree : com.db4o.YapMeta
	{
		private const byte BTREE_VERSION = (byte)1;

		internal readonly com.db4o.inside.ix.Indexable4 _keyHandler;

		internal readonly com.db4o.inside.ix.Indexable4 _valueHandler;

		private com.db4o.inside.btree.BTreeNode _root;

		/// <summary>All instantiated nodes are held in this tree.</summary>
		/// <remarks>All instantiated nodes are held in this tree.</remarks>
		private com.db4o.TreeIntObject _nodes;

		private int _size;

		private com.db4o.foundation.Visitor4 _removeListener;

		private com.db4o.foundation.Hashtable4 _sizesByTransaction;

		private com.db4o.foundation.Queue4 _processing;

		internal int _nodeSize;

		internal int _halfNodeSize;

		private readonly int _cacheHeight;

		public BTree(int nodeSize, int cacheHeight, com.db4o.Transaction trans, int id, com.db4o.inside.ix.Indexable4
			 keyHandler, com.db4o.inside.ix.Indexable4 valueHandler)
		{
			_halfNodeSize = nodeSize / 2;
			_nodeSize = _halfNodeSize * 2;
			_cacheHeight = cacheHeight;
			_keyHandler = keyHandler;
			_valueHandler = (valueHandler == null) ? com.db4o.Null.INSTANCE : valueHandler;
			_sizesByTransaction = new com.db4o.foundation.Hashtable4(1);
			if (id == 0)
			{
				SetStateDirty();
				_root = new com.db4o.inside.btree.BTreeNode(this, 0, true, 0, 0, 0);
				_root.Write(trans.SystemTransaction());
				AddNode(_root);
				Write(trans.SystemTransaction());
			}
			else
			{
				SetID(id);
				SetStateDeactivated();
			}
		}

		public virtual void Add(com.db4o.Transaction trans, object value)
		{
			EnsureDirty(trans);
			_keyHandler.PrepareComparison(value);
			com.db4o.inside.btree.BTreeNode rootOrSplit = _root.Add(trans);
			if (rootOrSplit != null && rootOrSplit != _root)
			{
				_root = new com.db4o.inside.btree.BTreeNode(trans, _root, rootOrSplit);
				_root.Write(trans.SystemTransaction());
				AddNode(_root);
			}
			SetStateDirty();
			SizeChanged(trans, 1);
		}

		public virtual void Remove(com.db4o.Transaction trans, object value)
		{
			EnsureDirty(trans);
			_keyHandler.PrepareComparison(value);
			_root.Remove(trans);
			SizeChanged(trans, -1);
		}

		public virtual void Commit(com.db4o.Transaction trans)
		{
			com.db4o.Transaction systemTransAction = trans.SystemTransaction();
			object sizeDiff = _sizesByTransaction.Get(trans);
			if (sizeDiff != null)
			{
				_size += ((int)sizeDiff);
			}
			_sizesByTransaction.Remove(trans);
			if (_nodes != null)
			{
				ProcessAllNodes();
				while (_processing.HasNext())
				{
					((com.db4o.inside.btree.BTreeNode)_processing.Next()).Commit(trans);
				}
				_processing = null;
				if (_nodes != null)
				{
					_nodes.Traverse(new _AnonymousInnerClass102(this, systemTransAction));
				}
			}
			SetStateDirty();
			Write(systemTransAction);
			Purge();
		}

		private sealed class _AnonymousInnerClass102 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass102(BTree _enclosing, com.db4o.Transaction systemTransAction
				)
			{
				this._enclosing = _enclosing;
				this.systemTransAction = systemTransAction;
			}

			public void Visit(object obj)
			{
				com.db4o.inside.btree.BTreeNode node = (com.db4o.inside.btree.BTreeNode)((com.db4o.TreeIntObject
					)obj).GetObject();
				node.SetStateDirty();
				node.Write(systemTransAction);
			}

			private readonly BTree _enclosing;

			private readonly com.db4o.Transaction systemTransAction;
		}

		public virtual void Rollback(com.db4o.Transaction trans)
		{
			_sizesByTransaction.Remove(trans);
			if (_nodes == null)
			{
				return;
			}
			ProcessAllNodes();
			while (_processing.HasNext())
			{
				((com.db4o.inside.btree.BTreeNode)_processing.Next()).Rollback(trans);
			}
			_processing = null;
			Purge();
		}

		private void Purge()
		{
			if (_nodes == null)
			{
				return;
			}
			com.db4o.Tree temp = _nodes;
			_nodes = null;
			if (_cacheHeight > 0)
			{
				_root.MarkAsCached(_cacheHeight);
			}
			else
			{
				_root.HoldChildrenAsIDs();
				AddNode(_root);
			}
			temp.Traverse(new _AnonymousInnerClass152(this));
		}

		private sealed class _AnonymousInnerClass152 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass152(BTree _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object obj)
			{
				com.db4o.inside.btree.BTreeNode node = (com.db4o.inside.btree.BTreeNode)((com.db4o.TreeIntObject
					)obj).GetObject();
				node.Purge();
			}

			private readonly BTree _enclosing;
		}

		private void ProcessAllNodes()
		{
			_processing = new com.db4o.foundation.Queue4();
			_nodes.Traverse(new _AnonymousInnerClass162(this));
		}

		private sealed class _AnonymousInnerClass162 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass162(BTree _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object obj)
			{
				this._enclosing._processing.Add(((com.db4o.TreeIntObject)obj).GetObject());
			}

			private readonly BTree _enclosing;
		}

		private void EnsureActive(com.db4o.Transaction trans)
		{
			if (!IsActive())
			{
				Read(trans.SystemTransaction());
			}
		}

		private void EnsureDirty(com.db4o.Transaction trans)
		{
			EnsureActive(trans);
			trans.DirtyBTree(this);
			SetStateDirty();
		}

		public override byte GetIdentifier()
		{
			return com.db4o.YapConst.BTREE;
		}

		public virtual void SetRemoveListener(com.db4o.foundation.Visitor4 vis)
		{
			_removeListener = vis;
		}

		public override int OwnLength()
		{
			return 1 + com.db4o.YapConst.OBJECT_LENGTH + (com.db4o.YapConst.YAPINT_LENGTH * 2
				) + com.db4o.YapConst.YAPID_LENGTH;
		}

		internal virtual com.db4o.inside.btree.BTreeNode ProduceNode(int id)
		{
			com.db4o.TreeIntObject addtio = new com.db4o.TreeIntObject(id);
			_nodes = (com.db4o.TreeIntObject)com.db4o.Tree.Add(_nodes, addtio);
			com.db4o.TreeIntObject tio = (com.db4o.TreeIntObject)addtio.DuplicateOrThis();
			com.db4o.inside.btree.BTreeNode node = (com.db4o.inside.btree.BTreeNode)tio.GetObject
				();
			if (node == null)
			{
				node = new com.db4o.inside.btree.BTreeNode(id, this);
				tio.SetObject(node);
				AddToProcessing(node);
			}
			return node;
		}

		internal virtual void AddNode(com.db4o.inside.btree.BTreeNode node)
		{
			_nodes = (com.db4o.TreeIntObject)com.db4o.Tree.Add(_nodes, new com.db4o.TreeIntObject
				(node.GetID(), node));
			AddToProcessing(node);
		}

		internal virtual void AddToProcessing(com.db4o.inside.btree.BTreeNode node)
		{
			if (_processing != null)
			{
				_processing.Add(node);
			}
		}

		internal virtual void RemoveNode(com.db4o.inside.btree.BTreeNode node)
		{
			_nodes = (com.db4o.TreeIntObject)_nodes.RemoveLike(new com.db4o.TreeInt(node.GetID
				()));
		}

		internal virtual void NotifyRemoveListener(object obj)
		{
			if (_removeListener != null)
			{
				_removeListener.Visit(obj);
			}
		}

		public override void ReadThis(com.db4o.Transaction a_trans, com.db4o.YapReader a_reader
			)
		{
			a_reader.IncrementOffset(1);
			_size = a_reader.ReadInt();
			_nodeSize = a_reader.ReadInt();
			_halfNodeSize = _nodeSize / 2;
			_root = ProduceNode(a_reader.ReadInt());
		}

		public override void WriteThis(com.db4o.Transaction trans, com.db4o.YapReader a_writer
			)
		{
			a_writer.Append(BTREE_VERSION);
			a_writer.WriteInt(_size);
			a_writer.WriteInt(_nodeSize);
			a_writer.WriteIDOf(trans, _root);
		}

		public virtual int Size(com.db4o.Transaction trans)
		{
			EnsureActive(trans);
			object sizeDiff = _sizesByTransaction.Get(trans);
			if (sizeDiff != null)
			{
				return _size + ((int)sizeDiff);
			}
			return _size;
		}

		public virtual void TraverseKeys(com.db4o.Transaction trans, com.db4o.foundation.Visitor4
			 visitor)
		{
			EnsureActive(trans);
			if (_root == null)
			{
				return;
			}
			_root.TraverseKeys(trans, visitor);
		}

		private void SizeChanged(com.db4o.Transaction trans, int changeBy)
		{
			object sizeDiff = _sizesByTransaction.Get(trans);
			if (sizeDiff == null)
			{
				_sizesByTransaction.Put(trans, changeBy);
				return;
			}
			_sizesByTransaction.Put(trans, ((int)sizeDiff) + changeBy);
		}
	}
}
