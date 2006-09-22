namespace com.db4o.inside.classindex
{
	/// <exclude></exclude>
	public class OldClassIndexStrategy : com.db4o.inside.classindex.AbstractClassIndexStrategy
		, com.db4o.TransactionParticipant
	{
		private com.db4o.inside.classindex.ClassIndex _index;

		private readonly com.db4o.foundation.Hashtable4 _perTransaction = new com.db4o.foundation.Hashtable4
			();

		public OldClassIndexStrategy(com.db4o.YapClass yapClass) : base(yapClass)
		{
		}

		public override void Read(com.db4o.YapStream stream, int indexID)
		{
			_index = CreateClassIndex(stream);
			if (indexID > 0)
			{
				_index.SetID(indexID);
			}
			_index.SetStateDeactivated();
		}

		private com.db4o.inside.classindex.ClassIndex GetActiveIndex(com.db4o.Transaction
			 transaction)
		{
			if (null != _index)
			{
				_index.EnsureActive(transaction);
			}
			return _index;
		}

		public override int EntryCount(com.db4o.Transaction transaction)
		{
			if (_index != null)
			{
				return _index.EntryCount(transaction);
			}
			return 0;
		}

		public override void Initialize(com.db4o.YapStream stream)
		{
			_index = CreateClassIndex(stream);
		}

		public override void Purge()
		{
			if (_index != null)
			{
				if (!_index.IsDirty())
				{
					_index.Clear();
					_index.SetStateDeactivated();
				}
			}
		}

		public override int Write(com.db4o.Transaction transaction)
		{
			if (_index == null)
			{
				return 0;
			}
			_index.Write(transaction);
			return _index.GetID();
		}

		private void FlushContext(com.db4o.Transaction transaction)
		{
			com.db4o.inside.classindex.OldClassIndexStrategy.TransactionState context = GetState
				(transaction);
			com.db4o.inside.classindex.ClassIndex index = GetActiveIndex(transaction);
			context.TraverseAdded(new _AnonymousInnerClass68(this, index));
			context.TraverseRemoved(new _AnonymousInnerClass74(this, transaction, index));
		}

		private sealed class _AnonymousInnerClass68 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass68(OldClassIndexStrategy _enclosing, com.db4o.inside.classindex.ClassIndex
				 index)
			{
				this._enclosing = _enclosing;
				this.index = index;
			}

			public void Visit(object a_object)
			{
				index.Add(this._enclosing.IdFromValue(a_object));
			}

			private readonly OldClassIndexStrategy _enclosing;

			private readonly com.db4o.inside.classindex.ClassIndex index;
		}

		private sealed class _AnonymousInnerClass74 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass74(OldClassIndexStrategy _enclosing, com.db4o.Transaction
				 transaction, com.db4o.inside.classindex.ClassIndex index)
			{
				this._enclosing = _enclosing;
				this.transaction = transaction;
				this.index = index;
			}

			public void Visit(object a_object)
			{
				int id = this._enclosing.IdFromValue(a_object);
				com.db4o.YapStream stream = transaction.Stream();
				com.db4o.YapObject yo = stream.GetYapObject(id);
				if (yo != null)
				{
					stream.RemoveReference(yo);
				}
				index.Remove(id);
			}

			private readonly OldClassIndexStrategy _enclosing;

			private readonly com.db4o.Transaction transaction;

			private readonly com.db4o.inside.classindex.ClassIndex index;
		}

		private void WriteIndex(com.db4o.Transaction transaction)
		{
			_index.SetStateDirty();
			_index.Write(transaction);
		}

		internal sealed class TransactionState
		{
			private com.db4o.foundation.Tree i_addToClassIndex;

			private com.db4o.foundation.Tree i_removeFromClassIndex;

			public void Add(int id)
			{
				i_removeFromClassIndex = com.db4o.foundation.Tree.RemoveLike(i_removeFromClassIndex
					, new com.db4o.TreeInt(id));
				i_addToClassIndex = com.db4o.foundation.Tree.Add(i_addToClassIndex, new com.db4o.TreeInt
					(id));
			}

			public void Remove(int id)
			{
				i_addToClassIndex = com.db4o.foundation.Tree.RemoveLike(i_addToClassIndex, new com.db4o.TreeInt
					(id));
				i_removeFromClassIndex = com.db4o.foundation.Tree.Add(i_removeFromClassIndex, new 
					com.db4o.TreeInt(id));
			}

			public void DontDelete(int id)
			{
				i_removeFromClassIndex = com.db4o.foundation.Tree.RemoveLike(i_removeFromClassIndex
					, new com.db4o.TreeInt(id));
			}

			internal void Traverse(com.db4o.foundation.Tree node, com.db4o.foundation.Visitor4
				 visitor)
			{
				if (node != null)
				{
					node.Traverse(visitor);
				}
			}

			public void TraverseAdded(com.db4o.foundation.Visitor4 visitor4)
			{
				Traverse(i_addToClassIndex, visitor4);
			}

			public void TraverseRemoved(com.db4o.foundation.Visitor4 visitor4)
			{
				Traverse(i_removeFromClassIndex, visitor4);
			}
		}

		protected override void InternalAdd(com.db4o.Transaction transaction, int id)
		{
			GetState(transaction).Add(id);
		}

		private com.db4o.inside.classindex.OldClassIndexStrategy.TransactionState GetState
			(com.db4o.Transaction transaction)
		{
			lock (_perTransaction)
			{
				com.db4o.inside.classindex.OldClassIndexStrategy.TransactionState context = (com.db4o.inside.classindex.OldClassIndexStrategy.TransactionState
					)_perTransaction.Get(transaction);
				if (null == context)
				{
					context = new com.db4o.inside.classindex.OldClassIndexStrategy.TransactionState();
					_perTransaction.Put(transaction, context);
					transaction.Enlist(this);
				}
				return context;
			}
		}

		private com.db4o.foundation.Tree GetAll(com.db4o.Transaction transaction)
		{
			com.db4o.inside.classindex.ClassIndex ci = GetActiveIndex(transaction);
			if (ci == null)
			{
				return null;
			}
			com.db4o.foundation.Tree[] tree = new com.db4o.foundation.Tree[] { com.db4o.foundation.Tree
				.DeepClone(ci.GetRoot(), null) };
			com.db4o.inside.classindex.OldClassIndexStrategy.TransactionState context = GetState
				(transaction);
			context.TraverseAdded(new _AnonymousInnerClass151(this, tree));
			context.TraverseRemoved(new _AnonymousInnerClass156(this, tree));
			return tree[0];
		}

		private sealed class _AnonymousInnerClass151 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass151(OldClassIndexStrategy _enclosing, com.db4o.foundation.Tree[]
				 tree)
			{
				this._enclosing = _enclosing;
				this.tree = tree;
			}

			public void Visit(object obj)
			{
				tree[0] = com.db4o.foundation.Tree.Add(tree[0], new com.db4o.TreeInt(this._enclosing
					.IdFromValue(obj)));
			}

			private readonly OldClassIndexStrategy _enclosing;

			private readonly com.db4o.foundation.Tree[] tree;
		}

		private sealed class _AnonymousInnerClass156 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass156(OldClassIndexStrategy _enclosing, com.db4o.foundation.Tree[]
				 tree)
			{
				this._enclosing = _enclosing;
				this.tree = tree;
			}

			public void Visit(object obj)
			{
				tree[0] = com.db4o.foundation.Tree.RemoveLike(tree[0], (com.db4o.TreeInt)obj);
			}

			private readonly OldClassIndexStrategy _enclosing;

			private readonly com.db4o.foundation.Tree[] tree;
		}

		protected override void InternalRemove(com.db4o.Transaction transaction, int id)
		{
			GetState(transaction).Remove(id);
		}

		public override void TraverseAll(com.db4o.Transaction transaction, com.db4o.foundation.Visitor4
			 command)
		{
			com.db4o.foundation.Tree tree = GetAll(transaction);
			if (tree != null)
			{
				tree.Traverse(new _AnonymousInnerClass171(this, command));
			}
		}

		private sealed class _AnonymousInnerClass171 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass171(OldClassIndexStrategy _enclosing, com.db4o.foundation.Visitor4
				 command)
			{
				this._enclosing = _enclosing;
				this.command = command;
			}

			public void Visit(object obj)
			{
				command.Visit(this._enclosing.IdFromValue(obj));
			}

			private readonly OldClassIndexStrategy _enclosing;

			private readonly com.db4o.foundation.Visitor4 command;
		}

		private int IdFromValue(object value)
		{
			return ((com.db4o.TreeInt)value)._key;
		}

		private com.db4o.inside.classindex.ClassIndex CreateClassIndex(com.db4o.YapStream
			 stream)
		{
			if (stream.IsClient())
			{
				return new com.db4o.inside.classindex.ClassIndexClient(_yapClass);
			}
			return new com.db4o.inside.classindex.ClassIndex(_yapClass);
		}

		public override void DontDelete(com.db4o.Transaction transaction, int id)
		{
			GetState(transaction).DontDelete(id);
		}

		public virtual void Commit(com.db4o.Transaction trans)
		{
			if (null != _index)
			{
				FlushContext(trans);
				WriteIndex(trans);
			}
		}

		public virtual void Dispose(com.db4o.Transaction transaction)
		{
			lock (_perTransaction)
			{
				_perTransaction.Remove(transaction);
			}
		}

		public virtual void Rollback(com.db4o.Transaction transaction)
		{
		}

		public override void DefragReference(com.db4o.YapClass yapClass, com.db4o.YapReader
			 source, com.db4o.YapReader target, com.db4o.IDMapping mapping, int classIndexID
			)
		{
		}

		public override int Id()
		{
			return _index.GetID();
		}

		public override com.db4o.foundation.Iterator4 AllSlotIDs(com.db4o.Transaction trans
			)
		{
			throw new com.db4o.foundation.NotImplementedException();
		}

		public override void DefragIndex(com.db4o.YapReader source, com.db4o.YapReader target
			, com.db4o.IDMapping mapping)
		{
		}
	}
}
