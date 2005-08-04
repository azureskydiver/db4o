namespace com.db4o
{
	/// <exclude></exclude>
	internal sealed class TreeReader
	{
		private readonly com.db4o.Readable i_template;

		private readonly com.db4o.YapReader i_bytes;

		private int i_current = 0;

		private int i_levels = 0;

		private int i_size;

		private bool i_orderOnRead = false;

		internal TreeReader(com.db4o.YapReader a_bytes, com.db4o.Readable a_template)
		{
			i_template = a_template;
			i_bytes = a_bytes;
		}

		internal TreeReader(com.db4o.YapReader a_bytes, com.db4o.Readable a_template, bool
			 a_orderOnRead) : this(a_bytes, a_template)
		{
			i_orderOnRead = a_orderOnRead;
		}

		public com.db4o.Tree read()
		{
			return read(i_bytes.readInt());
		}

		public com.db4o.Tree read(int a_size)
		{
			i_size = a_size;
			if (i_size > 0)
			{
				if (i_orderOnRead)
				{
					com.db4o.Tree tree = null;
					for (int i = 0; i < i_size; i++)
					{
						tree = com.db4o.Tree.add(tree, (com.db4o.Tree)i_template.read(i_bytes));
					}
					return tree;
				}
				else
				{
					while ((1 << i_levels) < (i_size + 1))
					{
						i_levels++;
					}
					return linkUp(null, i_levels);
				}
			}
			return null;
		}

		private com.db4o.Tree linkUp(com.db4o.Tree a_preceding, int a_level)
		{
			com.db4o.Tree node = (com.db4o.Tree)i_template.read(i_bytes);
			i_current++;
			node.i_preceding = a_preceding;
			node.i_subsequent = linkDown(a_level + 1);
			node.calculateSize();
			if (i_current < i_size)
			{
				return linkUp(node, a_level - 1);
			}
			return node;
		}

		private com.db4o.Tree linkDown(int a_level)
		{
			if (i_current < i_size)
			{
				i_current++;
				if (a_level < i_levels)
				{
					com.db4o.Tree preceding = linkDown(a_level + 1);
					com.db4o.Tree node = (com.db4o.Tree)i_template.read(i_bytes);
					node.i_preceding = preceding;
					node.i_subsequent = linkDown(a_level + 1);
					node.calculateSize();
					return node;
				}
				else
				{
					return (com.db4o.Tree)i_template.read(i_bytes);
				}
			}
			return null;
		}
	}
}
