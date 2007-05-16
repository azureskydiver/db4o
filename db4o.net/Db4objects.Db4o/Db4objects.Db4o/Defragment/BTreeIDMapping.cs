/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

using System.Collections;
using Db4objects.Db4o.Defragment;
using Db4objects.Db4o.Internal;
using Db4objects.Db4o.Internal.Btree;
using Db4objects.Db4o.Internal.IX;
using Db4objects.Db4o.Internal.Mapping;

namespace Db4objects.Db4o.Defragment
{
	/// <summary>BTree mapping for IDs during a defragmentation run.</summary>
	/// <remarks>BTree mapping for IDs during a defragmentation run.</remarks>
	/// <seealso cref="Db4objects.Db4o.Defragment.Defragment">Db4objects.Db4o.Defragment.Defragment
	/// 	</seealso>
	public class BTreeIDMapping : AbstractContextIDMapping
	{
		private string _fileName;

		private LocalObjectContainer _mappingDb;

		private BTree _idTree;

		private MappedIDPair _cache = new MappedIDPair(0, 0);

		private BTreeIDMapping.BTreeSpec _treeSpec = null;

		private int _commitFrequency = 0;

		private int _insertCount = 0;

		public BTreeIDMapping(string fileName) : this(fileName, null, 0)
		{
		}

		public BTreeIDMapping(string fileName, int nodeSize, int cacheHeight, int commitFrequency
			) : this(fileName, new BTreeIDMapping.BTreeSpec(nodeSize, cacheHeight), commitFrequency
			)
		{
		}

		private BTreeIDMapping(string fileName, BTreeIDMapping.BTreeSpec treeSpec, int commitFrequency
			)
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
			IBTreeRange range = _idTree.Search(Trans(), new MappedIDPair(oldID, 0));
			IEnumerator pointers = range.Pointers();
			if (pointers.MoveNext())
			{
				BTreePointer pointer = (BTreePointer)pointers.Current;
				_cache = (MappedIDPair)pointer.Key();
				return _cache.Mapped();
			}
			if (lenient)
			{
				return MapLenient(oldID, range);
			}
			return 0;
		}

		private int MapLenient(int oldID, IBTreeRange range)
		{
			range = range.Smaller();
			BTreePointer pointer = range.LastPointer();
			if (pointer == null)
			{
				return 0;
			}
			MappedIDPair mappedIDs = (MappedIDPair)pointer.Key();
			return mappedIDs.Mapped() + (oldID - mappedIDs.Orig());
		}

		protected override void MapNonClassIDs(int origID, int mappedID)
		{
			_cache = new MappedIDPair(origID, mappedID);
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
			_mappingDb = DefragContextImpl.FreshYapFile(_fileName, 1);
			IIndexable4 handler = new MappedIDPairHandler(_mappingDb);
			_idTree = (_treeSpec == null ? new BTree(Trans(), 0, handler) : new BTree(Trans()
				, 0, handler, _treeSpec.NodeSize(), _treeSpec.CacheHeight()));
		}

		public override void Close()
		{
			_mappingDb.Close();
		}

		private Transaction Trans()
		{
			return _mappingDb.SystemTransaction();
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
