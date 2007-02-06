namespace com.db4o.defragment
{
	/// <summary>BTree mapping for IDs during a defragmentation run.</summary>
	/// <remarks>BTree mapping for IDs during a defragmentation run.</remarks>
	/// <seealso cref="com.db4o.defragment.Defragment">com.db4o.defragment.Defragment</seealso>
	public class BTreeIDMapping : com.db4o.defragment.AbstractContextIDMapping
	{
		private string _fileName;

		private com.db4o.@internal.LocalObjectContainer _mappingDb;

		private com.db4o.@internal.btree.BTree _idTree;

		private com.db4o.@internal.mapping.MappedIDPair _cache = new com.db4o.@internal.mapping.MappedIDPair
			(0, 0);

		private com.db4o.defragment.BTreeIDMapping.BTreeSpec _treeSpec = null;

		private int _commitFrequency = 0;

		private int _insertCount = 0;

		public BTreeIDMapping(string fileName) : this(fileName, null, 0)
		{
		}

		public BTreeIDMapping(string fileName, int nodeSize, int cacheHeight, int commitFrequency
			) : this(fileName, new com.db4o.defragment.BTreeIDMapping.BTreeSpec(nodeSize, cacheHeight
			), commitFrequency)
		{
		}

		private BTreeIDMapping(string fileName, com.db4o.defragment.BTreeIDMapping.BTreeSpec
			 treeSpec, int commitFrequency)
		{
			_fileName = fileName;
			_treeSpec = treeSpec;
			_commitFrequency = commitFrequency;
		}

		public override int MappedID(int oldID, bool lenient)
		{
			if (_cache.Orig() == oldID)
			{
				return _cache.Mapped();
			}
			int classID = MappedClassID(oldID);
			if (classID != 0)
			{
				return classID;
			}
			com.db4o.@internal.btree.BTreeRange range = _idTree.Search(Trans(), new com.db4o.@internal.mapping.MappedIDPair
				(oldID, 0));
			System.Collections.IEnumerator pointers = range.Pointers();
			if (pointers.MoveNext())
			{
				com.db4o.@internal.btree.BTreePointer pointer = (com.db4o.@internal.btree.BTreePointer
					)pointers.Current;
				_cache = (com.db4o.@internal.mapping.MappedIDPair)pointer.Key();
				return _cache.Mapped();
			}
			if (lenient)
			{
				return MapLenient(oldID, range);
			}
			return 0;
		}

		private int MapLenient(int oldID, com.db4o.@internal.btree.BTreeRange range)
		{
			range = range.Smaller();
			com.db4o.@internal.btree.BTreePointer pointer = range.LastPointer();
			if (pointer == null)
			{
				return 0;
			}
			com.db4o.@internal.mapping.MappedIDPair mappedIDs = (com.db4o.@internal.mapping.MappedIDPair
				)pointer.Key();
			return mappedIDs.Mapped() + (oldID - mappedIDs.Orig());
		}

		protected override void MapNonClassIDs(int origID, int mappedID)
		{
			_cache = new com.db4o.@internal.mapping.MappedIDPair(origID, mappedID);
			_idTree.Add(Trans(), _cache);
			if (_commitFrequency > 0)
			{
				_insertCount++;
				if (_commitFrequency == _insertCount)
				{
					_idTree.Commit(Trans());
					_insertCount = 0;
				}
			}
		}

		public override void Open()
		{
			_mappingDb = com.db4o.defragment.DefragContextImpl.FreshYapFile(_fileName);
			com.db4o.@internal.ix.Indexable4 handler = new com.db4o.@internal.mapping.MappedIDPairHandler
				(_mappingDb);
			_idTree = (_treeSpec == null ? new com.db4o.@internal.btree.BTree(Trans(), 0, handler
				) : new com.db4o.@internal.btree.BTree(Trans(), 0, handler, null, _treeSpec.NodeSize
				(), _treeSpec.CacheHeight()));
		}

		public override void Close()
		{
			_mappingDb.Close();
		}

		private com.db4o.@internal.Transaction Trans()
		{
			return _mappingDb.GetSystemTransaction();
		}

		private class BTreeSpec
		{
			private int _nodeSize;

			private int _cacheHeight;

			public BTreeSpec(int nodeSize, int cacheHeight)
			{
				_nodeSize = nodeSize;
				_cacheHeight = cacheHeight;
			}

			public virtual int NodeSize()
			{
				return _nodeSize;
			}

			public virtual int CacheHeight()
			{
				return _cacheHeight;
			}
		}
	}
}
