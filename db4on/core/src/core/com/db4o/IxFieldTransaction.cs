
namespace com.db4o
{
	/// <summary>Index root holder for a field and a transaction.</summary>
	/// <remarks>Index root holder for a field and a transaction.</remarks>
	internal class IxFieldTransaction : com.db4o.foundation.Visitor4
	{
		internal readonly com.db4o.IxField i_index;

		internal readonly com.db4o.Transaction i_trans;

		internal int i_version;

		private com.db4o.Tree i_root;

		internal IxFieldTransaction(com.db4o.Transaction a_trans, com.db4o.IxField a_index
			)
		{
			i_trans = a_trans;
			i_index = a_index;
		}

		public override bool Equals(object obj)
		{
			return i_trans == ((com.db4o.IxFieldTransaction)obj).i_trans;
		}

		internal virtual void add(com.db4o.IxPatch a_patch)
		{
			i_root = com.db4o.Tree.add(i_root, a_patch);
		}

		internal virtual com.db4o.Tree getRoot()
		{
			return i_root;
		}

		internal virtual void commit()
		{
			i_index.commit(this);
		}

		internal virtual void rollback()
		{
			i_index.rollback(this);
		}

		internal virtual void merge(com.db4o.IxFieldTransaction a_ft)
		{
			com.db4o.Tree otherRoot = a_ft.getRoot();
			if (otherRoot != null)
			{
				otherRoot.traverseFromLeaves(this);
			}
		}

		/// <summary>
		/// Visitor functionality for merge:<br />
		/// Add
		/// </summary>
		public virtual void visit(object obj)
		{
			if (obj is com.db4o.IxPatch)
			{
				com.db4o.IxPatch tree = (com.db4o.IxPatch)obj;
				if (tree.i_queue != null)
				{
					com.db4o.foundation.Queue4 queue = tree.i_queue;
					tree.i_queue = null;
					while ((tree = (com.db4o.IxPatch)queue.next()) != null)
					{
						tree.i_queue = null;
						addPatchToRoot(tree);
					}
				}
				else
				{
					addPatchToRoot(tree);
				}
			}
		}

		private void addPatchToRoot(com.db4o.IxPatch tree)
		{
			if (tree.i_version != i_version)
			{
				tree.beginMerge();
				tree.handler().prepareComparison(tree.handler().comparableObject(i_trans, tree.i_value
					));
				if (i_root == null)
				{
					i_root = tree;
				}
				else
				{
					i_root = i_root.add(tree);
				}
			}
		}

		internal virtual int countLeaves()
		{
			if (i_root == null)
			{
				return 0;
			}
			int[] leaves = { 0 };
			i_root.traverse(new _AnonymousInnerClass86(this, leaves));
			return leaves[0];
		}

		private sealed class _AnonymousInnerClass86 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass86(IxFieldTransaction _enclosing, int[] leaves)
			{
				this._enclosing = _enclosing;
				this.leaves = leaves;
			}

			public void visit(object a_object)
			{
				leaves[0]++;
			}

			private readonly IxFieldTransaction _enclosing;

			private readonly int[] leaves;
		}

		public virtual void setRoot(com.db4o.Tree a_tree)
		{
			i_root = a_tree;
		}

		public override string ToString()
		{
			j4o.lang.StringBuffer sb = new j4o.lang.StringBuffer();
			sb.append("IxFieldTransaction ");
			sb.append(j4o.lang.JavaSystem.identityHashCode(this));
			if (i_root == null)
			{
				sb.append("\n    Empty");
			}
			else
			{
				i_root.traverse(new _AnonymousInnerClass105(this, sb));
			}
			return sb.ToString();
		}

		private sealed class _AnonymousInnerClass105 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass105(IxFieldTransaction _enclosing, j4o.lang.StringBuffer
				 sb)
			{
				this._enclosing = _enclosing;
				this.sb = sb;
			}

			public void visit(object a_object)
			{
				sb.append("\n");
				sb.append(a_object.ToString());
			}

			private readonly IxFieldTransaction _enclosing;

			private readonly j4o.lang.StringBuffer sb;
		}
	}
}
