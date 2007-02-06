namespace com.db4o.@internal.ix
{
	/// <exclude></exclude>
	public class Index4
	{
		public readonly com.db4o.@internal.ix.Indexable4 _handler;

		private static int _version;

		public readonly com.db4o.MetaIndex _metaIndex;

		private com.db4o.@internal.ix.IndexTransaction _globalIndexTransaction;

		private com.db4o.foundation.Collection4 _indexTransactions;

		private com.db4o.@internal.ix.IxFileRangeReader _fileRangeReader;

		internal readonly bool _nullHandling;

		public Index4(com.db4o.@internal.Transaction systemTrans, com.db4o.@internal.ix.Indexable4
			 handler, com.db4o.MetaIndex metaIndex, bool nullHandling)
		{
			_metaIndex = metaIndex;
			_handler = handler;
			_globalIndexTransaction = new com.db4o.@internal.ix.IndexTransaction(systemTrans, 
				this);
			_nullHandling = nullHandling;
			CreateGlobalFileRange();
		}

		public virtual com.db4o.@internal.ix.IndexTransaction DirtyIndexTransaction(com.db4o.@internal.Transaction
			 a_trans)
		{
			com.db4o.@internal.ix.IndexTransaction ift = new com.db4o.@internal.ix.IndexTransaction
				(a_trans, this);
			if (_indexTransactions == null)
			{
				_indexTransactions = new com.db4o.foundation.Collection4();
			}
			else
			{
				com.db4o.@internal.ix.IndexTransaction iftExisting = (com.db4o.@internal.ix.IndexTransaction
					)_indexTransactions.Get(ift);
				if (iftExisting != null)
				{
					return iftExisting;
				}
			}
			a_trans.AddDirtyFieldIndex(ift);
			ift.SetRoot(com.db4o.foundation.Tree.DeepClone(_globalIndexTransaction.GetRoot(), 
				ift));
			ift.i_version = ++_version;
			_indexTransactions.Add(ift);
			return ift;
		}

		public virtual com.db4o.@internal.ix.IndexTransaction GlobalIndexTransaction()
		{
			return _globalIndexTransaction;
		}

		public virtual com.db4o.@internal.ix.IndexTransaction IndexTransactionFor(com.db4o.@internal.Transaction
			 a_trans)
		{
			if (_indexTransactions != null)
			{
				com.db4o.@internal.ix.IndexTransaction ift = new com.db4o.@internal.ix.IndexTransaction
					(a_trans, this);
				ift = (com.db4o.@internal.ix.IndexTransaction)_indexTransactions.Get(ift);
				if (ift != null)
				{
					return ift;
				}
			}
			return _globalIndexTransaction;
		}

		private int[] FreeForMetaIndex()
		{
			return new int[] { _metaIndex.indexAddress, _metaIndex.indexLength };
		}

		private void DoFree(int[] addressLength)
		{
			com.db4o.@internal.LocalObjectContainer yf = File();
			for (int i = 0; i < addressLength.Length; i += 2)
			{
				yf.Free(addressLength[i], addressLength[i + 1]);
			}
		}

		/// <summary>
		/// solving a hen-egg problem: commit itself works with freespace
		/// so we have to do this all sequentially in the right way, working
		/// with with both indexes at the same time.
		/// </summary>
		/// <remarks>
		/// solving a hen-egg problem: commit itself works with freespace
		/// so we have to do this all sequentially in the right way, working
		/// with with both indexes at the same time.
		/// </remarks>
		public virtual void CommitFreeSpace(com.db4o.@internal.ix.Index4 other)
		{
			int entries = CountEntries();
			int length = (entries + 4) * LengthPerEntry();
			int mySlot = GetSlot(length);
			int otherSlot = GetSlot(length);
			DoFree(FreeForMetaIndex());
			DoFree(other.FreeForMetaIndex());
			entries = WriteToNewSlot(mySlot);
			MetaIndexSetMembers(entries, length, mySlot);
			CreateGlobalFileRange();
			int otherEntries = other.WriteToNewSlot(otherSlot);
			other.MetaIndexSetMembers(entries, length, otherSlot);
			other.CreateGlobalFileRange();
		}

		private int LengthPerEntry()
		{
			return _handler.LinkLength() + com.db4o.@internal.Const4.INT_LENGTH;
		}

		private void MetaIndexStore(int entries, int length, int address)
		{
			com.db4o.@internal.Transaction transact = Trans();
			MetaIndexSetMembers(entries, length, address);
			transact.Stream().SetInternal(transact, _metaIndex, 1, false);
		}

		private void MetaIndexSetMembers(int entries, int length, int address)
		{
			_metaIndex.indexEntries = entries;
			_metaIndex.indexLength = length;
			_metaIndex.indexAddress = address;
		}

		private int WriteToNewSlot(int slot)
		{
			com.db4o.foundation.Tree root = GetRoot();
			com.db4o.@internal.StatefulBuffer writer = new com.db4o.@internal.StatefulBuffer(
				Trans(), slot, LengthPerEntry());
			int[] entries = new int[] { 0 };
			if (root != null)
			{
				root.Traverse(new _AnonymousInnerClass149(this, entries, writer));
			}
			return entries[0];
		}

		private sealed class _AnonymousInnerClass149 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass149(Index4 _enclosing, int[] entries, com.db4o.@internal.StatefulBuffer
				 writer)
			{
				this._enclosing = _enclosing;
				this.entries = entries;
				this.writer = writer;
			}

			public void Visit(object a_object)
			{
				entries[0] += ((com.db4o.@internal.ix.IxTree)a_object).Write(this._enclosing._handler
					, writer);
			}

			private readonly Index4 _enclosing;

			private readonly int[] entries;

			private readonly com.db4o.@internal.StatefulBuffer writer;
		}

		internal virtual void Commit(com.db4o.@internal.ix.IndexTransaction ixTrans)
		{
			_indexTransactions.Remove(ixTrans);
			_globalIndexTransaction.Merge(ixTrans);
			bool createNewFileRange = true;
			if (createNewFileRange)
			{
				int entries = CountEntries();
				int length = CountEntries() * LengthPerEntry();
				int slot = GetSlot(length);
				int[] free = FreeForMetaIndex();
				MetaIndexStore(entries, length, slot);
				WriteToNewSlot(slot);
				com.db4o.@internal.ix.IxFileRange newFileRange = CreateGlobalFileRange();
				if (_indexTransactions != null)
				{
					System.Collections.IEnumerator i = _indexTransactions.GetEnumerator();
					while (i.MoveNext())
					{
						com.db4o.@internal.ix.IndexTransaction ft = (com.db4o.@internal.ix.IndexTransaction
							)i.Current;
						com.db4o.foundation.Tree clonedTree = newFileRange;
						if (clonedTree != null)
						{
							clonedTree = (com.db4o.foundation.Tree)clonedTree.DeepClone(ft);
						}
						com.db4o.foundation.Tree.ByRef tree = new com.db4o.foundation.Tree.ByRef(clonedTree
							);
						ft.GetRoot().TraverseFromLeaves((new _AnonymousInnerClass197(this, ft, tree)));
						ft.SetRoot(tree.value);
					}
				}
				DoFree(free);
			}
			else
			{
				System.Collections.IEnumerator i = _indexTransactions.GetEnumerator();
				while (i.MoveNext())
				{
					((com.db4o.@internal.ix.IndexTransaction)i.Current).Merge(ixTrans);
				}
			}
		}

		private sealed class _AnonymousInnerClass197 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass197(Index4 _enclosing, com.db4o.@internal.ix.IndexTransaction
				 ft, com.db4o.foundation.Tree.ByRef tree)
			{
				this._enclosing = _enclosing;
				this.ft = ft;
				this.tree = tree;
			}

			public void Visit(object a_object)
			{
				com.db4o.@internal.ix.IxTree ixTree = (com.db4o.@internal.ix.IxTree)a_object;
				if (ixTree._version == ft.i_version)
				{
					if (!(ixTree is com.db4o.@internal.ix.IxFileRange))
					{
						ixTree.BeginMerge();
						tree.value = com.db4o.foundation.Tree.Add(tree.value, ixTree);
					}
				}
			}

			private readonly Index4 _enclosing;

			private readonly com.db4o.@internal.ix.IndexTransaction ft;

			private readonly com.db4o.foundation.Tree.ByRef tree;
		}

		private com.db4o.@internal.ix.IxFileRange CreateGlobalFileRange()
		{
			com.db4o.@internal.ix.IxFileRange fr = null;
			if (_metaIndex.indexEntries > 0)
			{
				fr = new com.db4o.@internal.ix.IxFileRange(_globalIndexTransaction, _metaIndex.indexAddress
					, 0, _metaIndex.indexEntries);
			}
			_globalIndexTransaction.SetRoot(fr);
			return fr;
		}

		internal virtual void Rollback(com.db4o.@internal.ix.IndexTransaction a_ft)
		{
			_indexTransactions.Remove(a_ft);
		}

		internal virtual com.db4o.@internal.ix.IxFileRangeReader FileRangeReader()
		{
			if (_fileRangeReader == null)
			{
				_fileRangeReader = new com.db4o.@internal.ix.IxFileRangeReader(_handler);
			}
			return _fileRangeReader;
		}

		public override string ToString()
		{
			return base.ToString();
			System.Text.StringBuilder sb = new System.Text.StringBuilder();
			sb.Append("IxField  " + j4o.lang.JavaSystem.IdentityHashCode(this));
			if (_globalIndexTransaction != null)
			{
				sb.Append("\n  Global \n   ");
				sb.Append(_globalIndexTransaction.ToString());
			}
			else
			{
				sb.Append("\n  no global index \n   ");
			}
			if (_indexTransactions != null)
			{
				System.Collections.IEnumerator i = _indexTransactions.GetEnumerator();
				while (i.MoveNext())
				{
					sb.Append("\n");
					sb.Append(i.Current.ToString());
				}
			}
			return sb.ToString();
		}

		private com.db4o.@internal.Transaction Trans()
		{
			return _globalIndexTransaction.i_trans;
		}

		private com.db4o.@internal.LocalObjectContainer File()
		{
			return Trans().i_file;
		}

		private int GetSlot(int length)
		{
			return File().GetSlot(length);
		}

		private com.db4o.foundation.Tree GetRoot()
		{
			return _globalIndexTransaction.GetRoot();
		}

		private int CountEntries()
		{
			com.db4o.foundation.Tree root = GetRoot();
			return root == null ? 0 : root.Size();
		}
	}
}
