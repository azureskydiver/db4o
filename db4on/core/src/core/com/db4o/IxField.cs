
namespace com.db4o
{
	/// <exclude></exclude>
	public class IxField
	{
		internal const int MAX_LEAVES = 3;

		private static int i_version;

		internal readonly com.db4o.YapField i_field;

		internal readonly com.db4o.MetaIndex i_metaIndex;

		internal com.db4o.IxFieldTransaction i_globalIndex;

		internal com.db4o.foundation.Collection4 i_transactionIndices;

		internal com.db4o.IxFileRangeReader i_fileRangeReader;

		internal IxField(com.db4o.Transaction a_systemTrans, com.db4o.YapField a_field, com.db4o.MetaIndex
			 a_metaIndex)
		{
			i_metaIndex = a_metaIndex;
			i_field = a_field;
			i_globalIndex = new com.db4o.IxFieldTransaction(a_systemTrans, this);
			createGlobalFileRange();
		}

		internal virtual com.db4o.IxFieldTransaction dirtyFieldTransaction(com.db4o.Transaction
			 a_trans)
		{
			com.db4o.IxFieldTransaction ift = new com.db4o.IxFieldTransaction(a_trans, this);
			if (i_transactionIndices == null)
			{
				i_transactionIndices = new com.db4o.foundation.Collection4();
			}
			else
			{
				com.db4o.IxFieldTransaction iftExisting = (com.db4o.IxFieldTransaction)i_transactionIndices
					.get(ift);
				if (iftExisting != null)
				{
					return iftExisting;
				}
			}
			a_trans.addDirtyFieldIndex(ift);
			ift.setRoot(com.db4o.Tree.deepClone(i_globalIndex.getRoot(), ift));
			ift.i_version = ++i_version;
			i_transactionIndices.add(ift);
			return ift;
		}

		internal virtual com.db4o.IxFieldTransaction getFieldTransaction(com.db4o.Transaction
			 a_trans)
		{
			if (i_transactionIndices != null)
			{
				com.db4o.IxFieldTransaction ift = new com.db4o.IxFieldTransaction(a_trans, this);
				ift = (com.db4o.IxFieldTransaction)i_transactionIndices.get(ift);
				if (ift != null)
				{
					return ift;
				}
			}
			return i_globalIndex;
		}

		internal virtual void commit(com.db4o.IxFieldTransaction a_ft)
		{
			i_transactionIndices.remove(a_ft);
			i_globalIndex.merge(a_ft);
			int leaves = i_globalIndex.countLeaves();
			bool createNewFileRange = true;
			if (createNewFileRange)
			{
				com.db4o.Transaction trans = i_globalIndex.i_trans;
				int[] free = new int[] { i_metaIndex.indexAddress, i_metaIndex.indexLength, i_metaIndex
					.patchAddress, i_metaIndex.patchLength };
				com.db4o.Tree root = i_globalIndex.getRoot();
				com.db4o.YapDataType handler = i_field.getHandler();
				int lengthPerEntry = handler.linkLength() + com.db4o.YapConst.YAPINT_LENGTH;
				i_metaIndex.indexEntries = root == null ? 0 : root.size();
				i_metaIndex.indexLength = i_metaIndex.indexEntries * lengthPerEntry;
				i_metaIndex.indexAddress = ((com.db4o.YapFile)trans.i_stream).getSlot(i_metaIndex
					.indexLength);
				i_metaIndex.patchEntries = 0;
				i_metaIndex.patchAddress = 0;
				i_metaIndex.patchLength = 0;
				trans.i_stream.setInternal(trans, i_metaIndex, 1, false);
				com.db4o.YapWriter writer = new com.db4o.YapWriter(trans, i_metaIndex.indexAddress
					, lengthPerEntry);
				if (root != null)
				{
					root.traverse(new _AnonymousInnerClass119(this, handler, writer));
				}
				com.db4o.IxFileRange newFileRange = createGlobalFileRange();
				com.db4o.foundation.Iterator4 i = i_transactionIndices.iterator();
				while (i.hasNext())
				{
					com.db4o.IxFieldTransaction ft = (com.db4o.IxFieldTransaction)i.next();
					com.db4o.Tree clonedTree = newFileRange;
					if (clonedTree != null)
					{
						clonedTree = clonedTree.deepClone(ft);
					}
					com.db4o.Tree[] tree = { clonedTree };
					ft.getRoot().traverseFromLeaves((new _AnonymousInnerClass136(this, ft, tree)));
					ft.setRoot(tree[0]);
				}
				if (free[0] > 0)
				{
					trans.i_file.free(free[0], free[1]);
				}
				if (free[2] > 0)
				{
					trans.i_file.free(free[2], free[3]);
				}
			}
			else
			{
				com.db4o.foundation.Iterator4 i = i_transactionIndices.iterator();
				while (i.hasNext())
				{
					((com.db4o.IxFieldTransaction)i.next()).merge(a_ft);
				}
			}
		}

		private sealed class _AnonymousInnerClass119 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass119(IxField _enclosing, com.db4o.YapDataType handler, 
				com.db4o.YapWriter writer)
			{
				this._enclosing = _enclosing;
				this.handler = handler;
				this.writer = writer;
			}

			public void visit(object a_object)
			{
				((com.db4o.IxTree)a_object).write(handler, writer);
			}

			private readonly IxField _enclosing;

			private readonly com.db4o.YapDataType handler;

			private readonly com.db4o.YapWriter writer;
		}

		private sealed class _AnonymousInnerClass136 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass136(IxField _enclosing, com.db4o.IxFieldTransaction ft
				, com.db4o.Tree[] tree)
			{
				this._enclosing = _enclosing;
				this.ft = ft;
				this.tree = tree;
			}

			public void visit(object a_object)
			{
				com.db4o.IxTree ixTree = (com.db4o.IxTree)a_object;
				if (ixTree.i_version == ft.i_version)
				{
					if (!(ixTree is com.db4o.IxFileRange))
					{
						ixTree.beginMerge();
						tree[0] = tree[0].add(ixTree);
					}
				}
			}

			private readonly IxField _enclosing;

			private readonly com.db4o.IxFieldTransaction ft;

			private readonly com.db4o.Tree[] tree;
		}

		private com.db4o.IxFileRange createGlobalFileRange()
		{
			com.db4o.IxFileRange fr = null;
			if (i_metaIndex.indexEntries > 0)
			{
				fr = new com.db4o.IxFileRange(i_globalIndex, i_metaIndex.indexAddress, 0, i_metaIndex
					.indexEntries);
			}
			i_globalIndex.setRoot(fr);
			return fr;
		}

		internal virtual void rollback(com.db4o.IxFieldTransaction a_ft)
		{
			i_transactionIndices.remove(a_ft);
		}

		internal virtual com.db4o.IxFileRangeReader fileRangeReader()
		{
			if (i_fileRangeReader == null)
			{
				i_fileRangeReader = new com.db4o.IxFileRangeReader(i_field.getHandler());
			}
			return i_fileRangeReader;
		}

		public override string ToString()
		{
			j4o.lang.StringBuffer sb = new j4o.lang.StringBuffer();
			sb.append("IxField  " + j4o.lang.JavaSystem.identityHashCode(this));
			if (i_globalIndex != null)
			{
				sb.append("\n  Global \n   ");
				sb.append(i_globalIndex.ToString());
			}
			else
			{
				sb.append("\n  no global index \n   ");
			}
			if (i_transactionIndices != null)
			{
				com.db4o.foundation.Iterator4 i = i_transactionIndices.iterator();
				while (i.hasNext())
				{
					sb.append("\n");
					sb.append(i.next().ToString());
				}
			}
			return sb.ToString();
		}
	}
}
