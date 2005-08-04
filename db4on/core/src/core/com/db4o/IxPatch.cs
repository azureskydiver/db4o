namespace com.db4o
{
	/// <summary>Node for index tree, can be addition or removal node</summary>
	internal abstract class IxPatch : com.db4o.IxTree
	{
		internal int i_parentID;

		internal object i_value;

		internal com.db4o.foundation.Queue4 i_queue;

		internal IxPatch(com.db4o.IxFieldTransaction a_ft, int a_parentID, object a_value
			) : base(a_ft)
		{
			i_parentID = a_parentID;
			i_value = a_value;
		}

		public override com.db4o.Tree add(com.db4o.Tree a_new)
		{
			int cmp = compare(a_new);
			if (cmp == 0)
			{
				com.db4o.IxPatch patch = (com.db4o.IxPatch)a_new;
				cmp = i_parentID - patch.i_parentID;
				if (cmp == 0)
				{
					com.db4o.foundation.Queue4 queue = i_queue;
					if (queue == null)
					{
						queue = new com.db4o.foundation.Queue4();
						queue.add(this);
					}
					queue.add(patch);
					patch.i_queue = queue;
					patch.i_subsequent = i_subsequent;
					patch.i_preceding = i_preceding;
					patch.calculateSize();
					return patch;
				}
			}
			return add(a_new, cmp);
		}

		internal override int compare(com.db4o.Tree a_to)
		{
			com.db4o.YapDataType handler = i_fieldTransaction.i_index.i_field.getHandler();
			return handler.compareTo(handler.comparableObject(trans(), i_value));
		}
	}
}
