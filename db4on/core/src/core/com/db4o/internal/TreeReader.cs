namespace com.db4o.@internal
{
	/// <exclude></exclude>
	public sealed class TreeReader
	{
		private readonly com.db4o.@internal.Readable i_template;

		private readonly com.db4o.@internal.Buffer i_bytes;

		private int i_current = 0;

		private int i_levels = 0;

		private int i_size;

		private bool i_orderOnRead;

		public TreeReader(com.db4o.@internal.Buffer a_bytes, com.db4o.@internal.Readable 
			a_template) : this(a_bytes, a_template, false)
		{
		}

		public TreeReader(com.db4o.@internal.Buffer a_bytes, com.db4o.@internal.Readable 
			a_template, bool a_orderOnRead)
		{
			i_template = a_template;
			i_bytes = a_bytes;
			i_orderOnRead = a_orderOnRead;
		}

		public com.db4o.foundation.Tree Read()
		{
			return Read(i_bytes.ReadInt());
		}

		public com.db4o.foundation.Tree Read(int a_size)
		{
			i_size = a_size;
			if (i_size > 0)
			{
				if (i_orderOnRead)
				{
					com.db4o.foundation.Tree tree = null;
					for (int i = 0; i < i_size; i++)
					{
						tree = com.db4o.foundation.Tree.Add(tree, (com.db4o.foundation.Tree)i_template.Read
							(i_bytes));
					}
					return tree;
				}
				while ((1 << i_levels) < (i_size + 1))
				{
					i_levels++;
				}
				return LinkUp(null, i_levels);
			}
			return null;
		}

		private com.db4o.foundation.Tree LinkUp(com.db4o.foundation.Tree a_preceding, int
			 a_level)
		{
			com.db4o.foundation.Tree node = (com.db4o.foundation.Tree)i_template.Read(i_bytes
				);
			i_current++;
			node._preceding = a_preceding;
			node._subsequent = LinkDown(a_level + 1);
			node.CalculateSize();
			if (i_current < i_size)
			{
				return LinkUp(node, a_level - 1);
			}
			return node;
		}

		private com.db4o.foundation.Tree LinkDown(int a_level)
		{
			if (i_current < i_size)
			{
				i_current++;
				if (a_level < i_levels)
				{
					com.db4o.foundation.Tree preceding = LinkDown(a_level + 1);
					com.db4o.foundation.Tree node = (com.db4o.foundation.Tree)i_template.Read(i_bytes
						);
					node._preceding = preceding;
					node._subsequent = LinkDown(a_level + 1);
					node.CalculateSize();
					return node;
				}
				return (com.db4o.foundation.Tree)i_template.Read(i_bytes);
			}
			return null;
		}
	}
}
