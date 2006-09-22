namespace com.db4o.inside.classindex
{
	/// <exclude></exclude>
	public class BTreeClassIndexStrategy : com.db4o.inside.classindex.AbstractClassIndexStrategy
	{
		private com.db4o.inside.btree.BTree _btreeIndex;

		public BTreeClassIndexStrategy(com.db4o.YapClass yapClass) : base(yapClass)
		{
		}

		public virtual com.db4o.inside.btree.BTree Btree()
		{
			return _btreeIndex;
		}

		public override int EntryCount(com.db4o.Transaction ta)
		{
			return _btreeIndex != null ? _btreeIndex.Size(ta) : 0;
		}

		public override void Initialize(com.db4o.YapStream stream)
		{
			CreateBTreeIndex(stream, 0);
		}

		public override void Purge()
		{
		}

		public override void Read(com.db4o.YapStream stream, int indexID)
		{
			ReadBTreeIndex(stream, indexID);
		}

		public override int Write(com.db4o.Transaction trans)
		{
			if (_btreeIndex == null)
			{
				return 0;
			}
			_btreeIndex.Write(trans);
			return _btreeIndex.GetID();
		}

		public override void TraverseAll(com.db4o.Transaction ta, com.db4o.foundation.Visitor4
			 command)
		{
			if (_btreeIndex != null)
			{
				_btreeIndex.TraverseKeys(ta, command);
			}
		}

		private void CreateBTreeIndex(com.db4o.YapStream stream, int btreeID)
		{
			if (stream.IsClient())
			{
				return;
			}
			_btreeIndex = ((com.db4o.YapFile)stream).CreateBTreeClassIndex(btreeID);
			_btreeIndex.SetRemoveListener(new _AnonymousInnerClass61(this, stream));
		}

		private sealed class _AnonymousInnerClass61 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass61(BTreeClassIndexStrategy _enclosing, com.db4o.YapStream
				 stream)
			{
				this._enclosing = _enclosing;
				this.stream = stream;
			}

			public void Visit(object obj)
			{
				int id = ((int)obj);
				com.db4o.YapObject yo = stream.GetYapObject(id);
				if (yo != null)
				{
					stream.RemoveReference(yo);
				}
			}

			private readonly BTreeClassIndexStrategy _enclosing;

			private readonly com.db4o.YapStream stream;
		}

		private void ReadBTreeIndex(com.db4o.YapStream stream, int indexId)
		{
			if (!stream.IsClient() && _btreeIndex == null)
			{
				CreateBTreeIndex(stream, indexId);
			}
		}

		protected override void InternalAdd(com.db4o.Transaction trans, int id)
		{
			_btreeIndex.Add(trans, id);
		}

		protected override void InternalRemove(com.db4o.Transaction ta, int id)
		{
			_btreeIndex.Remove(ta, id);
		}

		public override void DontDelete(com.db4o.Transaction transaction, int id)
		{
		}

		public override void DefragReference(com.db4o.YapClass yapClass, com.db4o.YapReader
			 source, com.db4o.YapReader target, com.db4o.IDMapping mapping, int classIndexID
			)
		{
			source.ReadInt();
			int newID = -classIndexID;
			target.WriteInt(newID);
		}

		public override int Id()
		{
			return _btreeIndex.GetID();
		}

		public override com.db4o.foundation.Iterator4 AllSlotIDs(com.db4o.Transaction trans
			)
		{
			return _btreeIndex.AllNodeIds(trans);
		}

		public override void DefragIndex(com.db4o.YapReader source, com.db4o.YapReader target
			, com.db4o.IDMapping mapping)
		{
			_btreeIndex.DefragIndex(source, target, mapping);
		}
	}
}
