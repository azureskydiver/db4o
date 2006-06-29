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

		public override com.db4o.Tree Add(com.db4o.Tree a_new, int a_cmp)
		{
			if (a_cmp < 0)
			{
				if (_subsequent == null)
				{
					_subsequent = a_new;
				}
				else
				{
					_subsequent = _subsequent.Add(a_new);
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
					_preceding = _preceding.Add(a_new);
				}
			}
			return BalanceCheckNulls();
		}

		internal virtual void BeginMerge()
		{
			_preceding = null;
			_subsequent = null;
			SetSizeOwn();
		}

		public override com.db4o.Tree DeepClone(object a_param)
		{
			com.db4o.inside.ix.IxTree tree = (com.db4o.inside.ix.IxTree)this.ShallowClone();
			tree._fieldTransaction = (com.db4o.inside.ix.IndexTransaction)a_param;
			return tree;
		}

		internal com.db4o.inside.ix.Indexable4 Handler()
		{
			return _fieldTransaction.i_index._handler;
		}

		internal com.db4o.inside.ix.Index4 Index()
		{
			return _fieldTransaction.i_index;
		}

		/// <summary>
		/// Overridden in IxFileRange
		/// Only call directly after compare()
		/// </summary>
		internal virtual int[] LowerAndUpperMatch()
		{
			return null;
		}

		public sealed override int Nodes()
		{
			return _nodes;
		}

		public sealed override void Nodes(int count)
		{
			_nodes = count;
		}

		public override void SetSizeOwn()
		{
			base.SetSizeOwn();
			_nodes = 1;
		}

		public override void SetSizeOwnPrecedingSubsequent()
		{
			base.SetSizeOwnPrecedingSubsequent();
			_nodes = 1 + _preceding.Nodes() + _subsequent.Nodes();
		}

		public override void SetSizeOwnPreceding()
		{
			base.SetSizeOwnPreceding();
			_nodes = 1 + _preceding.Nodes();
		}

		public override void SetSizeOwnSubsequent()
		{
			base.SetSizeOwnSubsequent();
			_nodes = 1 + _subsequent.Nodes();
		}

		public sealed override void SetSizeOwnPlus(com.db4o.Tree tree)
		{
			base.SetSizeOwnPlus(tree);
			_nodes = 1 + tree.Nodes();
		}

		public sealed override void SetSizeOwnPlus(com.db4o.Tree tree1, com.db4o.Tree tree2
			)
		{
			base.SetSizeOwnPlus(tree1, tree2);
			_nodes = 1 + tree1.Nodes() + tree2.Nodes();
		}

		internal virtual int SlotLength()
		{
			return Handler().LinkLength() + com.db4o.YapConst.YAPINT_LENGTH;
		}

		internal com.db4o.YapFile Stream()
		{
			return Trans().i_file;
		}

		internal com.db4o.Transaction Trans()
		{
			return _fieldTransaction.i_trans;
		}

		public abstract void Visit(object obj);

		public abstract void Visit(com.db4o.foundation.Visitor4 visitor, int[] a_lowerAndUpperMatch
			);

		public abstract void VisitAll(com.db4o.foundation.IntObjectVisitor visitor);

		public abstract void FreespaceVisit(com.db4o.inside.freespace.FreespaceVisitor visitor
			, int index);

		public abstract int Write(com.db4o.inside.ix.Indexable4 a_handler, com.db4o.YapWriter
			 a_writer);

		public virtual void VisitFirst(com.db4o.inside.freespace.FreespaceVisitor visitor
			)
		{
			if (_preceding != null)
			{
				((com.db4o.inside.ix.IxTree)_preceding).VisitFirst(visitor);
				if (visitor.Visited())
				{
					return;
				}
			}
			FreespaceVisit(visitor, 0);
			if (visitor.Visited())
			{
				return;
			}
			if (_subsequent != null)
			{
				((com.db4o.inside.ix.IxTree)_subsequent).VisitFirst(visitor);
				if (visitor.Visited())
				{
					return;
				}
			}
		}

		public virtual void VisitLast(com.db4o.inside.freespace.FreespaceVisitor visitor)
		{
			if (_subsequent != null)
			{
				((com.db4o.inside.ix.IxTree)_subsequent).VisitLast(visitor);
				if (visitor.Visited())
				{
					return;
				}
			}
			FreespaceVisit(visitor, 0);
			if (visitor.Visited())
			{
				return;
			}
			if (_preceding != null)
			{
				((com.db4o.inside.ix.IxTree)_preceding).VisitLast(visitor);
				if (visitor.Visited())
				{
					return;
				}
			}
		}

		protected override com.db4o.Tree ShallowCloneInternal(com.db4o.Tree tree)
		{
			com.db4o.inside.ix.IxTree ixTree = (com.db4o.inside.ix.IxTree)base.ShallowCloneInternal
				(tree);
			ixTree._fieldTransaction = _fieldTransaction;
			ixTree._version = _version;
			ixTree._nodes = _nodes;
			return ixTree;
		}
	}
}
