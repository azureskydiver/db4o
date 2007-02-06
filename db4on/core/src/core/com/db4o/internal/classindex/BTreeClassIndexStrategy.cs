namespace com.db4o.@internal.classindex
{
	/// <exclude></exclude>
	public class BTreeClassIndexStrategy : com.db4o.@internal.classindex.AbstractClassIndexStrategy
	{
		private com.db4o.@internal.btree.BTree _btreeIndex;

		public BTreeClassIndexStrategy(com.db4o.@internal.ClassMetadata yapClass) : base(
			yapClass)
		{
		}

		public virtual com.db4o.@internal.btree.BTree Btree()
		{
			return _btreeIndex;
		}

		public override int EntryCount(com.db4o.@internal.Transaction ta)
		{
			return _btreeIndex != null ? _btreeIndex.Size(ta) : 0;
		}

		public override void Initialize(com.db4o.@internal.ObjectContainerBase stream)
		{
			CreateBTreeIndex(stream, 0);
		}

		public override void Purge()
		{
		}

		public override void Read(com.db4o.@internal.ObjectContainerBase stream, int indexID
			)
		{
			ReadBTreeIndex(stream, indexID);
		}

		public override int Write(com.db4o.@internal.Transaction trans)
		{
			if (_btreeIndex == null)
			{
				return 0;
			}
			_btreeIndex.Write(trans);
			return _btreeIndex.GetID();
		}

		public override void TraverseAll(com.db4o.@internal.Transaction ta, com.db4o.foundation.Visitor4
			 command)
		{
			if (_btreeIndex != null)
			{
				_btreeIndex.TraverseKeys(ta, command);
			}
		}

		private void CreateBTreeIndex(com.db4o.@internal.ObjectContainerBase stream, int 
			btreeID)
		{
			if (stream.IsClient())
			{
				return;
			}
			_btreeIndex = ((com.db4o.@internal.LocalObjectContainer)stream).CreateBTreeClassIndex
				(btreeID);
			_btreeIndex.SetRemoveListener(new _AnonymousInnerClass61(this, stream));
		}

		private sealed class _AnonymousInnerClass61 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass61(BTreeClassIndexStrategy _enclosing, com.db4o.@internal.ObjectContainerBase
				 stream)
			{
				this._enclosing = _enclosing;
				this.stream = stream;
			}

			public void Visit(object obj)
			{
				int id = ((int)obj);
				com.db4o.@internal.ObjectReference yo = stream.GetYapObject(id);
				if (yo != null)
				{
					stream.RemoveReference(yo);
				}
			}

			private readonly BTreeClassIndexStrategy _enclosing;

			private readonly com.db4o.@internal.ObjectContainerBase stream;
		}

		private void ReadBTreeIndex(com.db4o.@internal.ObjectContainerBase stream, int indexId
			)
		{
			if (!stream.IsClient() && _btreeIndex == null)
			{
				CreateBTreeIndex(stream, indexId);
			}
		}

		protected override void InternalAdd(com.db4o.@internal.Transaction trans, int id)
		{
			_btreeIndex.Add(trans, id);
		}

		protected override void InternalRemove(com.db4o.@internal.Transaction ta, int id)
		{
			_btreeIndex.Remove(ta, id);
		}

		public override void DontDelete(com.db4o.@internal.Transaction transaction, int id
			)
		{
		}

		public override void DefragReference(com.db4o.@internal.ClassMetadata yapClass, com.db4o.@internal.ReaderPair
			 readers, int classIndexID)
		{
			int newID = -classIndexID;
			readers.WriteInt(newID);
		}

		public override int Id()
		{
			return _btreeIndex.GetID();
		}

		public override System.Collections.IEnumerator AllSlotIDs(com.db4o.@internal.Transaction
			 trans)
		{
			return _btreeIndex.AllNodeIds(trans);
		}

		public override void DefragIndex(com.db4o.@internal.ReaderPair readers)
		{
			_btreeIndex.DefragIndex(readers);
		}

		public static com.db4o.@internal.btree.BTree Btree(com.db4o.@internal.ClassMetadata
			 clazz)
		{
			com.db4o.@internal.classindex.ClassIndexStrategy index = clazz.Index();
			if (!(index is com.db4o.@internal.classindex.BTreeClassIndexStrategy))
			{
				throw new System.InvalidOperationException();
			}
			return ((com.db4o.@internal.classindex.BTreeClassIndexStrategy)index).Btree();
		}

		public static System.Collections.IEnumerator Iterate(com.db4o.@internal.ClassMetadata
			 clazz, com.db4o.@internal.Transaction trans)
		{
			return Btree(clazz).AsRange(trans).Keys();
		}
	}
}
