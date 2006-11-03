namespace com.db4o.foundation
{
	/// <exclude></exclude>
	public class TreeKeyIterator : System.Collections.IEnumerator
	{
		private readonly com.db4o.foundation.Tree _tree;

		private com.db4o.foundation.Stack4 _stack;

		public TreeKeyIterator(com.db4o.foundation.Tree tree)
		{
			_tree = tree;
		}

		public virtual object Current
		{
			get
			{
				if (_stack == null)
				{
					throw new System.InvalidOperationException();
				}
				com.db4o.foundation.Tree tree = Peek();
				if (tree == null)
				{
					return null;
				}
				return tree.Key();
			}
		}

		private com.db4o.foundation.Tree Peek()
		{
			return (com.db4o.foundation.Tree)_stack.Peek();
		}

		public virtual void Reset()
		{
			_stack = null;
		}

		public virtual bool MoveNext()
		{
			if (_stack == null)
			{
				InitStack();
				return _stack != null;
			}
			com.db4o.foundation.Tree current = Peek();
			if (current == null)
			{
				return false;
			}
			if (PushPreceding(current._subsequent))
			{
				return true;
			}
			while (true)
			{
				_stack.Pop();
				com.db4o.foundation.Tree parent = Peek();
				if (parent == null)
				{
					return false;
				}
				if (current == parent._preceding)
				{
					return true;
				}
				current = parent;
			}
		}

		private void InitStack()
		{
			if (_tree == null)
			{
				return;
			}
			_stack = new com.db4o.foundation.Stack4();
			PushPreceding(_tree);
		}

		private bool PushPreceding(com.db4o.foundation.Tree node)
		{
			if (node == null)
			{
				return false;
			}
			while (node != null)
			{
				_stack.Push(node);
				node = node._preceding;
			}
			return true;
		}
	}
}
