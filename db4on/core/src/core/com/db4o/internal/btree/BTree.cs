namespace com.db4o.@internal.btree
{
	/// <exclude></exclude>
	public class BTree : com.db4o.@internal.PersistentBase, com.db4o.@internal.TransactionParticipant
	{
		private const byte BTREE_VERSION = (byte)1;

		private const int DEFRAGMENT_INCREMENT_OFFSET = 1 + com.db4o.@internal.Const4.INT_LENGTH
			 * 2;

		private readonly com.db4o.@internal.ix.Indexable4 _keyHandler;

		internal readonly com.db4o.@internal.ix.Indexable4 _valueHandler;

		private com.db4o.@internal.btree.BTreeNode _root;

		/// <summary>All instantiated nodes are held in this tree.</summary>
		/// <remarks>All instantiated nodes are held in this tree.</remarks>
		private com.db4o.@internal.TreeIntObject _nodes;

		private int _size;

		private com.db4o.foundation.Visitor4 _removeListener;

		private com.db4o.foundation.Hashtable4 _sizesByTransaction;

		protected com.db4o.foundation.Queue4 _processing;

		private int _nodeSize;

		internal int _halfNodeSize;

		private readonly int _cacheHeight;

		public BTree(com.db4o.@internal.Transaction trans, int id, com.db4o.@internal.ix.Indexable4
			 keyHandler) : this(trans, id, keyHandler, null)
		{
		}

		public BTree(com.db4o.@internal.Transaction trans, int id, com.db4o.@internal.ix.Indexable4
			 keyHandler, com.db4o.@internal.ix.Indexable4 valueHandler) : this(trans, id, keyHandler
			, valueHandler, Config(trans).BTreeNodeSize(), Config(trans).BTreeCacheHeight())
		{
		}

		public BTree(com.db4o.@internal.Transaction trans, int id, com.db4o.@internal.ix.Indexable4
			 keyHandler, com.db4o.@internal.ix.Indexable4 valueHandler, int treeNodeSize, int
			 treeCacheHeight)
		{
			if (null == keyHandler)
			{
				throw new System.ArgumentNullException();
			}
			_nodeSize = treeNodeSize;
			_halfNodeSize = _nodeSize / 2;
			_nodeSize = _halfNodeSize * 2;
			_cacheHeight = treeCacheHeight;
			_keyHandler = keyHandler;
			_valueHandler = (valueHandler == null) ? com.db4o.@internal.Null.INSTANCE : valueHandler;
			_sizesByTransaction = new com.db4o.foundation.Hashtable4();
			if (id == 0)
			{
				SetStateDirty();
				_root = new com.db4o.@internal.btree.BTreeNode(this, 0, true, 0, 0, 0);
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

		public virtual com.db4o.@internal.btree.BTreeNode Root()
		{
			return _root;
		}

		public virtual int NodeSize()
		{
			return _nodeSize;
		}

		public virtual void Add(com.db4o.@internal.Transaction trans, object key)
		{
			Add(trans, key, null);
		}

		public virtual void Add(com.db4o.@internal.Transaction trans, object key, object 
			value)
		{
			KeyCantBeNull(key);
			_keyHandler.PrepareComparison(key);
			_valueHandler.PrepareComparison(value);
			EnsureDirty(trans);
			com.db4o.@internal.btree.BTreeNode rootOrSplit = _root.Add(trans);
			if (rootOrSplit != null && rootOrSplit != _root)
			{
				_root = new com.db4o.@internal.btree.BTreeNode(trans, _root, rootOrSplit);
				_root.Write(trans.SystemTransaction());
				AddNode(_root);
			}
		}

		public virtual void Remove(com.db4o.@internal.Transaction trans, object key)
		{
			KeyCantBeNull(key);
			System.Collections.IEnumerator pointers = Search(trans, key).Pointers();
			if (!pointers.MoveNext())
			{
				return;
			}
			com.db4o.@internal.btree.BTreePointer first = (com.db4o.@internal.btree.BTreePointer
				)pointers.Current;
			EnsureDirty(trans);
			com.db4o.@internal.btree.BTreeNode node = first.Node();
			node.Remove(trans, first.Index());
		}

		public virtual com.db4o.@internal.btree.BTreeRange Search(com.db4o.@internal.Transaction
			 trans, object key)
		{
			KeyCantBeNull(key);
			com.db4o.@internal.btree.BTreeNodeSearchResult start = SearchLeaf(trans, key, com.db4o.@internal.btree.SearchTarget
				.LOWEST);
			com.db4o.@internal.btree.BTreeNodeSearchResult end = SearchLeaf(trans, key, com.db4o.@internal.btree.SearchTarget
				.HIGHEST);
			return start.CreateIncludingRange(end);
		}

		private void KeyCantBeNull(object key)
		{
			if (null == key)
			{
				throw new System.ArgumentNullException();
			}
		}

		public virtual com.db4o.@internal.ix.Indexable4 KeyHandler()
		{
			return _keyHandler;
		}

		public virtual com.db4o.@internal.btree.BTreeNodeSearchResult SearchLeaf(com.db4o.@internal.Transaction
			 trans, object key, com.db4o.@internal.btree.SearchTarget target)
		{
			EnsureActive(trans);
			_keyHandler.PrepareComparison(key);
			return _root.SearchLeaf(trans, target);
		}

		public virtual void Commit(com.db4o.@internal.Transaction trans)
		{
			com.db4o.@internal.Transaction systemTransaction = trans.SystemTransaction();
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
					((com.db4o.@internal.btree.BTreeNode)_processing.Next()).Commit(trans);
				}
				_processing = null;
				WriteAllNodes(systemTransaction, true);
			}
			SetStateDirty();
			Write(systemTransaction);
			Purge();
		}

		public virtual void Rollback(com.db4o.@internal.Transaction trans)
		{
			com.db4o.@internal.Transaction systemTransaction = trans.SystemTransaction();
			_sizesByTransaction.Remove(trans);
			if (_nodes == null)
			{
				return;
			}
			ProcessAllNodes();
			while (_processing.HasNext())
			{
				((com.db4o.@internal.btree.BTreeNode)_processing.Next()).Rollback(trans);
			}
			_processing = null;
			WriteAllNodes(systemTransaction, false);
			SetStateDirty();
			Write(systemTransaction);
			Purge();
		}

		private void WriteAllNodes(com.db4o.@internal.Transaction systemTransaction, bool
			 setDirty)
		{
			if (_nodes == null)
			{
				return;
			}
			_nodes.Traverse(new _AnonymousInnerClass202(this, setDirty, systemTransaction));
		}

		private sealed class _AnonymousInnerClass202 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass202(BTree _enclosing, bool setDirty, com.db4o.@internal.Transaction
				 systemTransaction)
			{
				this._enclosing = _enclosing;
				this.setDirty = setDirty;
				this.systemTransaction = systemTransaction;
			}

			public void Visit(object obj)
			{
				com.db4o.@internal.btree.BTreeNode node = (com.db4o.@internal.btree.BTreeNode)((com.db4o.@internal.TreeIntObject
					)obj).GetObject();
				if (setDirty)
				{
					node.SetStateDirty();
				}
				node.Write(systemTransaction);
			}

			private readonly BTree _enclosing;

			private readonly bool setDirty;

			private readonly com.db4o.@internal.Transaction systemTransaction;
		}

		private void Purge()
		{
			if (_nodes == null)
			{
				return;
			}
			com.db4o.foundation.Tree temp = _nodes;
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
			temp.Traverse(new _AnonymousInnerClass229(this));
		}

		private sealed class _AnonymousInnerClass229 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass229(BTree _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object obj)
			{
				com.db4o.@internal.btree.BTreeNode node = (com.db4o.@internal.btree.BTreeNode)((com.db4o.@internal.TreeIntObject
					)obj).GetObject();
				node.Purge();
			}

			private readonly BTree _enclosing;
		}

		private void ProcessAllNodes()
		{
			_processing = new com.db4o.foundation.Queue4();
			_nodes.Traverse(new _AnonymousInnerClass239(this));
		}

		private sealed class _AnonymousInnerClass239 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass239(BTree _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object obj)
			{
				this._enclosing._processing.Add(((com.db4o.@internal.TreeIntObject)obj).GetObject
					());
			}

			private readonly BTree _enclosing;
		}

		private void EnsureActive(com.db4o.@internal.Transaction trans)
		{
			if (!IsActive())
			{
				Read(trans.SystemTransaction());
			}
		}

		private void EnsureDirty(com.db4o.@internal.Transaction trans)
		{
			EnsureActive(trans);
			trans.Enlist(this);
			SetStateDirty();
		}

		public override byte GetIdentifier()
		{
			return com.db4o.@internal.Const4.BTREE;
		}

		public virtual void SetRemoveListener(com.db4o.foundation.Visitor4 vis)
		{
			_removeListener = vis;
		}

		public override int OwnLength()
		{
			return 1 + com.db4o.@internal.Const4.OBJECT_LENGTH + (com.db4o.@internal.Const4.INT_LENGTH
				 * 2) + com.db4o.@internal.Const4.ID_LENGTH;
		}

		internal virtual com.db4o.@internal.btree.BTreeNode ProduceNode(int id)
		{
			com.db4o.@internal.TreeIntObject addtio = new com.db4o.@internal.TreeIntObject(id
				);
			_nodes = (com.db4o.@internal.TreeIntObject)com.db4o.foundation.Tree.Add(_nodes, addtio
				);
			com.db4o.@internal.TreeIntObject tio = (com.db4o.@internal.TreeIntObject)addtio.AddedOrExisting
				();
			com.db4o.@internal.btree.BTreeNode node = (com.db4o.@internal.btree.BTreeNode)tio
				.GetObject();
			if (node == null)
			{
				node = new com.db4o.@internal.btree.BTreeNode(id, this);
				tio.SetObject(node);
				AddToProcessing(node);
			}
			return node;
		}

		internal virtual void AddNode(com.db4o.@internal.btree.BTreeNode node)
		{
			_nodes = (com.db4o.@internal.TreeIntObject)com.db4o.foundation.Tree.Add(_nodes, new 
				com.db4o.@internal.TreeIntObject(node.GetID(), node));
			AddToProcessing(node);
		}

		internal virtual void AddToProcessing(com.db4o.@internal.btree.BTreeNode node)
		{
			if (_processing != null)
			{
				_processing.Add(node);
			}
		}

		internal virtual void RemoveNode(com.db4o.@internal.btree.BTreeNode node)
		{
			_nodes = (com.db4o.@internal.TreeIntObject)_nodes.RemoveLike(new com.db4o.@internal.TreeInt
				(node.GetID()));
		}

		internal virtual void NotifyRemoveListener(object obj)
		{
			if (_removeListener != null)
			{
				_removeListener.Visit(obj);
			}
		}

		public override void ReadThis(com.db4o.@internal.Transaction a_trans, com.db4o.@internal.Buffer
			 a_reader)
		{
			a_reader.IncrementOffset(1);
			_size = a_reader.ReadInt();
			_nodeSize = a_reader.ReadInt();
			_halfNodeSize = NodeSize() / 2;
			_root = ProduceNode(a_reader.ReadInt());
		}

		public override void WriteThis(com.db4o.@internal.Transaction trans, com.db4o.@internal.Buffer
			 a_writer)
		{
			a_writer.Append(BTREE_VERSION);
			a_writer.WriteInt(_size);
			a_writer.WriteInt(NodeSize());
			a_writer.WriteIDOf(trans, _root);
		}

		public virtual int Size(com.db4o.@internal.Transaction trans)
		{
			EnsureActive(trans);
			object sizeDiff = _sizesByTransaction.Get(trans);
			if (sizeDiff != null)
			{
				return _size + ((int)sizeDiff);
			}
			return _size;
		}

		public virtual void TraverseKeys(com.db4o.@internal.Transaction trans, com.db4o.foundation.Visitor4
			 visitor)
		{
			EnsureActive(trans);
			if (_root == null)
			{
				return;
			}
			_root.TraverseKeys(trans, visitor);
		}

		public virtual void TraverseValues(com.db4o.@internal.Transaction trans, com.db4o.foundation.Visitor4
			 visitor)
		{
			EnsureActive(trans);
			if (_root == null)
			{
				return;
			}
			_root.TraverseValues(trans, visitor);
		}

		public virtual void SizeChanged(com.db4o.@internal.Transaction trans, int changeBy
			)
		{
			object sizeDiff = _sizesByTransaction.Get(trans);
			if (sizeDiff == null)
			{
				_sizesByTransaction.Put(trans, changeBy);
				return;
			}
			_sizesByTransaction.Put(trans, ((int)sizeDiff) + changeBy);
		}

		public virtual void Dispose(com.db4o.@internal.Transaction transaction)
		{
		}

		public virtual com.db4o.@internal.btree.BTreePointer FirstPointer(com.db4o.@internal.Transaction
			 trans)
		{
			EnsureActive(trans);
			if (null == _root)
			{
				return null;
			}
			return _root.FirstPointer(trans);
		}

		public virtual com.db4o.@internal.btree.BTreePointer LastPointer(com.db4o.@internal.Transaction
			 trans)
		{
			EnsureActive(trans);
			if (null == _root)
			{
				return null;
			}
			return _root.LastPointer(trans);
		}

		public virtual com.db4o.@internal.btree.BTree DebugLoadFully(com.db4o.@internal.Transaction
			 trans)
		{
			EnsureActive(trans);
			_root.DebugLoadFully(trans);
			return this;
		}

		private void TraverseAllNodes(com.db4o.@internal.Transaction trans, com.db4o.foundation.Visitor4
			 command)
		{
			EnsureActive(trans);
			_root.TraverseAllNodes(trans, command);
		}

		public virtual void DefragIndex(com.db4o.@internal.ReaderPair readers)
		{
			readers.IncrementOffset(DEFRAGMENT_INCREMENT_OFFSET);
			readers.CopyID();
		}

		public virtual void DefragIndexNode(com.db4o.@internal.ReaderPair readers)
		{
			com.db4o.@internal.btree.BTreeNode.DefragIndex(readers, _keyHandler, _valueHandler
				);
		}

		public virtual void DefragBTree(com.db4o.@internal.mapping.DefragContext context)
		{
			com.db4o.@internal.ReaderPair.ProcessCopy(context, GetID(), new _AnonymousInnerClass400
				(this));
			com.db4o.CorruptionException[] exc = { null };
			try
			{
				context.TraverseAllIndexSlots(this, new _AnonymousInnerClass407(this, context, exc
					));
			}
			catch (System.Exception e)
			{
				if (exc[0] != null)
				{
					throw exc[0];
				}
				throw;
			}
		}

		private sealed class _AnonymousInnerClass400 : com.db4o.@internal.SlotCopyHandler
		{
			public _AnonymousInnerClass400(BTree _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void ProcessCopy(com.db4o.@internal.ReaderPair readers)
			{
				this._enclosing.DefragIndex(readers);
			}

			private readonly BTree _enclosing;
		}

		private sealed class _AnonymousInnerClass407 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass407(BTree _enclosing, com.db4o.@internal.mapping.DefragContext
				 context, com.db4o.CorruptionException[] exc)
			{
				this._enclosing = _enclosing;
				this.context = context;
				this.exc = exc;
			}

			public void Visit(object obj)
			{
				int id = ((int)obj);
				try
				{
					com.db4o.@internal.ReaderPair.ProcessCopy(context, id, new _AnonymousInnerClass411
						(this));
				}
				catch (com.db4o.CorruptionException e)
				{
					exc[0] = e;
					throw new System.Exception();
				}
			}

			private sealed class _AnonymousInnerClass411 : com.db4o.@internal.SlotCopyHandler
			{
				public _AnonymousInnerClass411(_AnonymousInnerClass407 _enclosing)
				{
					this._enclosing = _enclosing;
				}

				public void ProcessCopy(com.db4o.@internal.ReaderPair readers)
				{
					this._enclosing._enclosing.DefragIndexNode(readers);
				}

				private readonly _AnonymousInnerClass407 _enclosing;
			}

			private readonly BTree _enclosing;

			private readonly com.db4o.@internal.mapping.DefragContext context;

			private readonly com.db4o.CorruptionException[] exc;
		}

		public virtual int CompareKeys(object key1, object key2)
		{
			_keyHandler.PrepareComparison(key2);
			return _keyHandler.CompareTo(key1);
		}

		private static com.db4o.@internal.Config4Impl Config(com.db4o.@internal.Transaction
			 trans)
		{
			if (null == trans)
			{
				throw new System.ArgumentNullException();
			}
			return trans.Stream().ConfigImpl();
		}

		public virtual void Free(com.db4o.@internal.Transaction systemTrans)
		{
			FreeAllNodeIds(systemTrans, AllNodeIds(systemTrans));
		}

		private void FreeAllNodeIds(com.db4o.@internal.Transaction systemTrans, System.Collections.IEnumerator
			 allNodeIDs)
		{
			while (allNodeIDs.MoveNext())
			{
				int id = ((int)allNodeIDs.Current);
				systemTrans.SlotFreePointerOnCommit(id);
			}
		}

		public virtual System.Collections.IEnumerator AllNodeIds(com.db4o.@internal.Transaction
			 systemTrans)
		{
			com.db4o.foundation.Collection4 allNodeIDs = new com.db4o.foundation.Collection4(
				);
			TraverseAllNodes(systemTrans, new _AnonymousInnerClass455(this, allNodeIDs));
			return allNodeIDs.GetEnumerator();
		}

		private sealed class _AnonymousInnerClass455 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass455(BTree _enclosing, com.db4o.foundation.Collection4 
				allNodeIDs)
			{
				this._enclosing = _enclosing;
				this.allNodeIDs = allNodeIDs;
			}

			public void Visit(object node)
			{
				allNodeIDs.Add(((com.db4o.@internal.btree.BTreeNode)node).GetID());
			}

			private readonly BTree _enclosing;

			private readonly com.db4o.foundation.Collection4 allNodeIDs;
		}

		public virtual com.db4o.@internal.btree.BTreeRange AsRange(com.db4o.@internal.Transaction
			 trans)
		{
			return new com.db4o.@internal.btree.BTreeRangeSingle(trans, this, FirstPointer(trans
				), null);
		}
	}
}
