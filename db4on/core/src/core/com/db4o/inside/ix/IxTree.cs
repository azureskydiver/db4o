namespace com.db4o.inside.ix
{
	/// <exclude></exclude>
	public abstract class IxTree : com.db4o.Tree
	{
		internal com.db4o.inside.ix.IndexTransaction i_fieldTransaction;

		internal int i_version;

		internal int _nodes = 1;

		internal IxTree(com.db4o.inside.ix.IndexTransaction a_ft)
		{
			i_fieldTransaction = a_ft;
			i_version = a_ft.i_version;
		}

		public override com.db4o.Tree add(com.db4o.Tree a_new, int a_cmp)
		{
			if (a_cmp < 0)
			{
				if (i_subsequent == null)
				{
					i_subsequent = a_new;
				}
				else
				{
					i_subsequent = i_subsequent.add(a_new);
				}
			}
			else
			{
				if (i_preceding == null)
				{
					i_preceding = a_new;
				}
				else
				{
					i_preceding = i_preceding.add(a_new);
				}
			}
			return balanceCheckNulls();
		}

		internal virtual void beginMerge()
		{
			i_preceding = null;
			i_subsequent = null;
			setSizeOwn();
		}

		public override com.db4o.Tree deepClone(object a_param)
		{
			try
			{
				com.db4o.inside.ix.IxTree tree = (com.db4o.inside.ix.IxTree)j4o.lang.JavaSystem.clone
					(this);
				tree.i_fieldTransaction = (com.db4o.inside.ix.IndexTransaction)a_param;
				return tree;
			}
			catch (j4o.lang.CloneNotSupportedException e)
			{
				j4o.lang.JavaSystem.printStackTrace(e);
			}
			return null;
		}

		internal com.db4o.inside.ix.Indexable4 handler()
		{
			return i_fieldTransaction.i_index._handler;
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
			_nodes = 1 + i_preceding.nodes() + i_subsequent.nodes();
		}

		public override void setSizeOwnPreceding()
		{
			base.setSizeOwnPreceding();
			_nodes = 1 + i_preceding.nodes();
		}

		public override void setSizeOwnSubsequent()
		{
			base.setSizeOwnSubsequent();
			_nodes = 1 + i_subsequent.nodes();
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
			return i_fieldTransaction.i_trans;
		}

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
			if (i_preceding != null)
			{
				((com.db4o.inside.ix.IxTree)i_preceding).visitFirst(visitor);
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
			if (i_subsequent != null)
			{
				((com.db4o.inside.ix.IxTree)i_subsequent).visitFirst(visitor);
				if (visitor.visited())
				{
					return;
				}
			}
		}

		public virtual void visitLast(com.db4o.inside.freespace.FreespaceVisitor visitor)
		{
			if (i_subsequent != null)
			{
				((com.db4o.inside.ix.IxTree)i_subsequent).visitLast(visitor);
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
			if (i_preceding != null)
			{
				((com.db4o.inside.ix.IxTree)i_preceding).visitLast(visitor);
				if (visitor.visited())
				{
					return;
				}
			}
		}
	}
}
