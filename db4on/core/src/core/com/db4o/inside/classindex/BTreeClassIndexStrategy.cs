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

		public override void Read(com.db4o.YapReader reader, com.db4o.YapStream stream)
		{
			ReadBTreeIndex(reader, stream);
		}

		public override void WriteId(com.db4o.YapReader writer, com.db4o.Transaction trans
			)
		{
			if (_btreeIndex == null)
			{
				writer.WriteInt(0);
			}
			else
			{
				_btreeIndex.Write(trans);
				writer.WriteInt(-_btreeIndex.GetID());
			}
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
			_btreeIndex.SetRemoveListener(new _AnonymousInnerClass63(this, stream));
		}

		private sealed class _AnonymousInnerClass63 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass63(BTreeClassIndexStrategy _enclosing, com.db4o.YapStream
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
					stream.YapObjectGCd(yo);
				}
			}

			private readonly BTreeClassIndexStrategy _enclosing;

			private readonly com.db4o.YapStream stream;
		}

		private void ReadBTreeIndex(com.db4o.YapReader reader, com.db4o.YapStream stream)
		{
			int indexId = reader.ReadInt();
			if (!stream.IsClient() && _btreeIndex == null)
			{
				com.db4o.YapFile yf = (com.db4o.YapFile)stream;
				if (indexId < 0)
				{
					CreateBTreeIndex(stream, -indexId);
				}
				else
				{
					CreateBTreeIndex(stream, 0);
					new com.db4o.inside.convert.conversions.ClassIndexesToBTrees().Convert(yf, indexId
						, _btreeIndex);
					yf.SetDirtyInSystemTransaction(_yapClass);
				}
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

		public override void TraverseAllSlotIDs(com.db4o.Transaction trans, com.db4o.foundation.Visitor4
			 command)
		{
			_btreeIndex.TraverseAllSlotIDs(trans, command);
		}

		public override void DefragIndex(com.db4o.YapReader source, com.db4o.YapReader target
			, com.db4o.IDMapping mapping)
		{
			_btreeIndex.DefragIndex(source, target, mapping);
		}
	}
}
