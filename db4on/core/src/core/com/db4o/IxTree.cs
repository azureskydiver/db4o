namespace com.db4o
{
	/// <exclude></exclude>
	public abstract class IxTree : com.db4o.Tree
	{
		internal com.db4o.IxFieldTransaction i_fieldTransaction;

		internal int i_version;

		internal int _nodes = 1;

		internal IxTree(com.db4o.IxFieldTransaction a_ft)
		{
			i_fieldTransaction = a_ft;
			i_version = a_ft.i_version;
		}

		internal override com.db4o.Tree add(com.db4o.Tree a_new, int a_cmp)
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

		internal override com.db4o.Tree deepClone(object a_param)
		{
			try
			{
				com.db4o.IxTree tree = (com.db4o.IxTree)j4o.lang.JavaSystem.clone(this);
				tree.i_fieldTransaction = (com.db4o.IxFieldTransaction)a_param;
				return tree;
			}
			catch (j4o.lang.CloneNotSupportedException e)
			{
				j4o.lang.JavaSystem.printStackTrace(e);
			}
			return null;
		}

		internal com.db4o.YapDataType handler()
		{
			return i_fieldTransaction.i_index.i_field.getHandler();
		}

		/// <summary>
		/// Overridden in IxFileRange
		/// Only call directly after compare()
		/// </summary>
		internal virtual int[] lowerAndUpperMatch()
		{
			return null;
		}

		internal sealed override int nodes()
		{
			return _nodes;
		}

		internal sealed override void nodes(int count)
		{
			_nodes = count;
		}

		internal override void setSizeOwn()
		{
			base.setSizeOwn();
			_nodes = 1;
		}

		internal override void setSizeOwnPrecedingSubsequent()
		{
			base.setSizeOwnPrecedingSubsequent();
			_nodes = 1 + i_preceding.nodes() + i_subsequent.nodes();
		}

		internal override void setSizeOwnPreceding()
		{
			base.setSizeOwnPreceding();
			_nodes = 1 + i_preceding.nodes();
		}

		internal override void setSizeOwnSubsequent()
		{
			base.setSizeOwnSubsequent();
			_nodes = 1 + i_subsequent.nodes();
		}

		internal sealed override void setSizeOwnPlus(com.db4o.Tree tree)
		{
			base.setSizeOwnPlus(tree);
			_nodes = 1 + tree.nodes();
		}

		internal sealed override void setSizeOwnPlus(com.db4o.Tree tree1, com.db4o.Tree tree2
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

		internal abstract void write(com.db4o.YapDataType a_handler, com.db4o.YapWriter a_writer
			);
	}
}
