namespace com.db4o.inside.ix
{
	/// <exclude></exclude>
	public abstract class IxTree : com.db4o.Tree, com.db4o.foundation.Visitor4
	{
		internal com.db4o.inside.ix.IndexTransaction _fieldTransaction;

		internal int _version;

		internal int _nodes = 1;

		internal IxTree(com.db4o.inside.ix.IndexTransaction a_ft)
		{
			_fieldTransaction = a_ft;
			_version = a_ft.i_version;
		}

		public override com.db4o.Tree add(com.db4o.Tree a_new, int a_cmp)
		{
			if (a_cmp < 0)
			{
				if (_subsequent == null)
				{
					_subsequent = a_new;
				}
				else
				{
					_subsequent = _subsequent.add(a_new);
				}
			}
			else
			{
				if (_preceding == null)
				{
					_preceding = a_new;
				}
				else
				{
					_preceding = _preceding.add(a_new);
				}
			}
			return balanceCheckNulls();
		}

		internal virtual void beginMerge()
		{
			_preceding = null;
			_subsequent = null;
			setSizeOwn();
		}

		public override com.db4o.Tree deepClone(object a_param)
		{
			com.db4o.inside.ix.IxTree tree = (com.db4o.inside.ix.IxTree)this.shallowClone();
			tree._fieldTransaction = (com.db4o.inside.ix.IndexTransaction)a_param;
			return tree;
		}

		internal com.db4o.inside.ix.Indexable4 handler()
		{
			return _fieldTransaction.i_index._handler;
		}

		internal com.db4o.inside.ix.Index4 index()
		{
			return _fieldTransaction.i_index;
		}

		/// <summary>
		/// Overridden in IxFileRange
		/// Only call directly after compare()
		/// </summary>
		internal virtual int[] lowerAndUpperMatch()
		{
			return null;
		}

		public sealed override int nodes()
		{
			return _nodes;
		}

		public sealed override void nodes(int count)
		{
			_nodes = count;
		}

		public override void setSizeOwn()
		{
			base.setSizeOwn();
			_nodes = 1;
		}

		public override void setSizeOwnPrecedingSubsequent()
		{
			base.setSizeOwnPrecedingSubsequent();
			_nodes = 1 + _preceding.nodes() + _subsequent.nodes();
		}

		public override void setSizeOwnPreceding()
		{
			base.setSizeOwnPreceding();
			_nodes = 1 + _preceding.nodes();
		}

		public override void setSizeOwnSubsequent()
		{
			base.setSizeOwnSubsequent();
			_nodes = 1 + _subsequent.nodes();
		}

		public sealed override void setSizeOwnPlus(com.db4o.Tree tree)
		{
			base.setSizeOwnPlus(tree);
			_nodes = 1 + tree.nodes();
		}

		public sealed override void setSizeOwnPlus(com.db4o.Tree tree1, com.db4o.Tree tree2
			)
		{
			base.setSizeOwnPlus(tree1, tree2);
			_nodes = 1 + tree1.nodes() + tree2.nodes();
		}

		internal virtual int slotLength()
		{
			return handler().linkLength() + com.db4o.YapConst.YAPINT_LENGTH;
		}

		internal com.db4o.YapFile stream()
		{
			return trans().i_file;
		}

		internal com.db4o.Transaction trans()
		{
			return _fieldTransaction.i_trans;
		}

		public abstract void visit(object obj);

		public abstract void visit(com.db4o.foundation.Visitor4 visitor, int[] a_lowerAndUpperMatch
			);

		public abstract void visitAll(com.db4o.foundation.IntObjectVisitor visitor);

		public abstract void freespaceVisit(com.db4o.inside.freespace.FreespaceVisitor visitor
			, int index);

		public abstract int write(com.db4o.inside.ix.Indexable4 a_handler, com.db4o.YapWriter
			 a_writer);

		public virtual void visitFirst(com.db4o.inside.freespace.FreespaceVisitor visitor
			)
		{
			if (_preceding != null)
			{
				((com.db4o.inside.ix.IxTree)_preceding).visitFirst(visitor);
				if (visitor.visited())
				{
					return;
				}
			}
			freespaceVisit(visitor, 0);
			if (visitor.visited())
			{
				return;
			}
			if (_subsequent != null)
			{
				((com.db4o.inside.ix.IxTree)_subsequent).visitFirst(visitor);
				if (visitor.visited())
				{
					return;
				}
			}
		}

		public virtual void visitLast(com.db4o.inside.freespace.FreespaceVisitor visitor)
		{
			if (_subsequent != null)
			{
				((com.db4o.inside.ix.IxTree)_subsequent).visitLast(visitor);
				if (visitor.visited())
				{
					return;
				}
			}
			freespaceVisit(visitor, 0);
			if (visitor.visited())
			{
				return;
			}
			if (_preceding != null)
			{
				((com.db4o.inside.ix.IxTree)_preceding).visitLast(visitor);
				if (visitor.visited())
				{
					return;
				}
			}
		}

		protected override com.db4o.Tree shallowCloneInternal(com.db4o.Tree tree)
		{
			com.db4o.inside.ix.IxTree ixTree = (com.db4o.inside.ix.IxTree)base.shallowCloneInternal
				(tree);
			ixTree._fieldTransaction = _fieldTransaction;
			ixTree._version = _version;
			ixTree._nodes = _nodes;
			return ixTree;
		}
	}
}
