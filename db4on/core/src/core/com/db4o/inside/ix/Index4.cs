namespace com.db4o.inside.ix
{
	/// <exclude></exclude>
	public class Index4
	{
		public readonly com.db4o.inside.ix.Indexable4 _handler;

		private static int _version;

		public readonly com.db4o.MetaIndex _metaIndex;

		private com.db4o.inside.ix.IndexTransaction _globalIndexTransaction;

		private com.db4o.foundation.Collection4 _indexTransactions;

		private com.db4o.inside.ix.IxFileRangeReader _fileRangeReader;

		internal readonly bool _nullHandling;

		public Index4(com.db4o.Transaction systemTrans, com.db4o.inside.ix.Indexable4 handler
			, com.db4o.MetaIndex metaIndex, bool nullHandling)
		{
			_metaIndex = metaIndex;
			_handler = handler;
			_globalIndexTransaction = new com.db4o.inside.ix.IndexTransaction(systemTrans, this
				);
			_nullHandling = nullHandling;
			createGlobalFileRange();
		}

		public virtual com.db4o.inside.ix.IndexTransaction dirtyIndexTransaction(com.db4o.Transaction
			 a_trans)
		{
			com.db4o.inside.ix.IndexTransaction ift = new com.db4o.inside.ix.IndexTransaction
				(a_trans, this);
			if (_indexTransactions == null)
			{
				_indexTransactions = new com.db4o.foundation.Collection4();
			}
			else
			{
				com.db4o.inside.ix.IndexTransaction iftExisting = (com.db4o.inside.ix.IndexTransaction
					)_indexTransactions.get(ift);
				if (iftExisting != null)
				{
					return iftExisting;
				}
			}
			a_trans.addDirtyFieldIndex(ift);
			ift.setRoot(com.db4o.Tree.deepClone(_globalIndexTransaction.getRoot(), ift));
			ift.i_version = ++_version;
			_indexTransactions.add(ift);
			return ift;
		}

		public virtual com.db4o.inside.ix.IndexTransaction globalIndexTransaction()
		{
			return _globalIndexTransaction;
		}

		public virtual com.db4o.inside.ix.IndexTransaction indexTransactionFor(com.db4o.Transaction
			 a_trans)
		{
			if (_indexTransactions != null)
			{
				com.db4o.inside.ix.IndexTransaction ift = new com.db4o.inside.ix.IndexTransaction
					(a_trans, this);
				ift = (com.db4o.inside.ix.IndexTransaction)_indexTransactions.get(ift);
				if (ift != null)
				{
					return ift;
				}
			}
			return _globalIndexTransaction;
		}

		private int[] freeForMetaIndex()
		{
			return new int[] { _metaIndex.indexAddress, _metaIndex.indexLength, _metaIndex.patchAddress
				, _metaIndex.patchLength };
		}

		private void doFree(int[] addressLength)
		{
			com.db4o.YapFile yf = file();
			for (int i = 0; i < addressLength.Length; i += 2)
			{
				yf.free(addressLength[i], addressLength[i + 1]);
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
		public virtual void commitFreeSpace(com.db4o.inside.ix.Index4 other)
		{
			int entries = countEntries();
			int length = (entries + 4) * lengthPerEntry();
			int mySlot = getSlot(length);
			int otherSlot = getSlot(length);
			doFree(freeForMetaIndex());
			doFree(other.freeForMetaIndex());
			entries = writeToNewSlot(mySlot, length);
			metaIndexSetMembers(entries, length, mySlot);
			createGlobalFileRange();
			int otherEntries = other.writeToNewSlot(otherSlot, length);
			other.metaIndexSetMembers(entries, length, otherSlot);
			other.createGlobalFileRange();
		}

		private int lengthPerEntry()
		{
			return _handler.linkLength() + com.db4o.YapConst.YAPINT_LENGTH;
		}

		private void free()
		{
			file().free(_metaIndex.indexAddress, _metaIndex.indexLength);
			file().free(_metaIndex.patchAddress, _metaIndex.indexLength);
		}

		private void metaIndexStore(int entries, int length, int address)
		{
			com.db4o.Transaction transact = trans();
			metaIndexSetMembers(entries, length, address);
			transact.i_stream.setInternal(transact, _metaIndex, 1, false);
		}

		private void metaIndexSetMembers(int entries, int length, int address)
		{
			_metaIndex.indexEntries = entries;
			_metaIndex.indexLength = length;
			_metaIndex.indexAddress = address;
			_metaIndex.patchEntries = 0;
			_metaIndex.patchAddress = 0;
			_metaIndex.patchLength = 0;
		}

		private int writeToNewSlot(int slot, int length)
		{
			com.db4o.Tree root = getRoot();
			com.db4o.YapWriter writer = new com.db4o.YapWriter(trans(), slot, lengthPerEntry(
				));
			int[] entries = new int[] { 0 };
			if (root != null)
			{
				root.traverse(new _AnonymousInnerClass175(this, entries, writer));
			}
			return entries[0];
		}

		private sealed class _AnonymousInnerClass175 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass175(Index4 _enclosing, int[] entries, com.db4o.YapWriter
				 writer)
			{
				this._enclosing = _enclosing;
				this.entries = entries;
				this.writer = writer;
			}

			public void visit(object a_object)
			{
				entries[0] += ((com.db4o.inside.ix.IxTree)a_object).write(this._enclosing._handler
					, writer);
			}

			private readonly Index4 _enclosing;

			private readonly int[] entries;

			private readonly com.db4o.YapWriter writer;
		}

		internal virtual void commit(com.db4o.inside.ix.IndexTransaction ixTrans)
		{
			_indexTransactions.remove(ixTrans);
			_globalIndexTransaction.merge(ixTrans);
			bool createNewFileRange = true;
			if (createNewFileRange)
			{
				int entries = countEntries();
				int length = countEntries() * lengthPerEntry();
				int slot = getSlot(length);
				int[] free = freeForMetaIndex();
				metaIndexStore(entries, length, slot);
				writeToNewSlot(slot, length);
				com.db4o.inside.ix.IxFileRange newFileRange = createGlobalFileRange();
				if (_indexTransactions != null)
				{
					com.db4o.foundation.Iterator4 i = _indexTransactions.iterator();
					while (i.hasNext())
					{
						com.db4o.inside.ix.IndexTransaction ft = (com.db4o.inside.ix.IndexTransaction)i.next
							();
						com.db4o.Tree clonedTree = newFileRange;
						if (clonedTree != null)
						{
							clonedTree = clonedTree.deepClone(ft);
						}
						com.db4o.Tree[] tree = { clonedTree };
						ft.getRoot().traverseFromLeaves((new _AnonymousInnerClass223(this, ft, tree)));
						ft.setRoot(tree[0]);
					}
				}
				doFree(free);
			}
			else
			{
				com.db4o.foundation.Iterator4 i = _indexTransactions.iterator();
				while (i.hasNext())
				{
					((com.db4o.inside.ix.IndexTransaction)i.next()).merge(ixTrans);
				}
			}
		}

		private sealed class _AnonymousInnerClass223 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass223(Index4 _enclosing, com.db4o.inside.ix.IndexTransaction
				 ft, com.db4o.Tree[] tree)
			{
				this._enclosing = _enclosing;
				this.ft = ft;
				this.tree = tree;
			}

			public void visit(object a_object)
			{
				com.db4o.inside.ix.IxTree ixTree = (com.db4o.inside.ix.IxTree)a_object;
				if (ixTree._version == ft.i_version)
				{
					if (!(ixTree is com.db4o.inside.ix.IxFileRange))
					{
						ixTree.beginMerge();
						tree[0] = com.db4o.Tree.add(tree[0], ixTree);
					}
				}
			}

			private readonly Index4 _enclosing;

			private readonly com.db4o.inside.ix.IndexTransaction ft;

			private readonly com.db4o.Tree[] tree;
		}

		private com.db4o.inside.ix.IxFileRange createGlobalFileRange()
		{
			com.db4o.inside.ix.IxFileRange fr = null;
			if (_metaIndex.indexEntries > 0)
			{
				fr = new com.db4o.inside.ix.IxFileRange(_globalIndexTransaction, _metaIndex.indexAddress
					, 0, _metaIndex.indexEntries);
			}
			_globalIndexTransaction.setRoot(fr);
			return fr;
		}

		internal virtual void rollback(com.db4o.inside.ix.IndexTransaction a_ft)
		{
			_indexTransactions.remove(a_ft);
		}

		internal virtual com.db4o.inside.ix.IxFileRangeReader fileRangeReader()
		{
			if (_fileRangeReader == null)
			{
				_fileRangeReader = new com.db4o.inside.ix.IxFileRangeReader(_handler);
			}
			return _fileRangeReader;
		}

		public override string ToString()
		{
			return base.ToString();
			j4o.lang.StringBuffer sb = new j4o.lang.StringBuffer();
			sb.append("IxField  " + j4o.lang.JavaSystem.identityHashCode(this));
			if (_globalIndexTransaction != null)
			{
				sb.append("\n  Global \n   ");
				sb.append(_globalIndexTransaction.ToString());
			}
			else
			{
				sb.append("\n  no global index \n   ");
			}
			if (_indexTransactions != null)
			{
				com.db4o.foundation.Iterator4 i = _indexTransactions.iterator();
				while (i.hasNext())
				{
					sb.append("\n");
					sb.append(i.next().ToString());
				}
			}
			return sb.ToString();
		}

		private com.db4o.Transaction trans()
		{
			return _globalIndexTransaction.i_trans;
		}

		private com.db4o.YapFile file()
		{
			return trans().i_file;
		}

		private int getSlot(int length)
		{
			return file().getSlot(length);
		}

		private com.db4o.Tree getRoot()
		{
			return _globalIndexTransaction.getRoot();
		}

		private int countEntries()
		{
			com.db4o.Tree root = getRoot();
			return root == null ? 0 : root.size();
		}
	}
}
