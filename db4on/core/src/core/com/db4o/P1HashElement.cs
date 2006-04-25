namespace com.db4o
{
	/// <exclude></exclude>
	/// <persistent></persistent>
	public class P1HashElement : com.db4o.P1ListElement
	{
		public object i_key;

		public int i_hashCode;

		public int i_position;

		public P1HashElement()
		{
		}

		public P1HashElement(com.db4o.Transaction a_trans, com.db4o.P1ListElement a_next, 
			object a_key, int a_hashCode, object a_object) : base(a_trans, a_next, a_object)
		{
			i_hashCode = a_hashCode;
			i_key = a_key;
		}

		public override int adjustReadDepth(int a_depth)
		{
			return 1;
		}

		internal virtual object activatedKey(int a_depth)
		{
			checkActive();
			if (a_depth < 0)
			{
				com.db4o.Transaction trans = getTrans();
				if (trans != null)
				{
					if (trans.i_stream.i_config.activationDepth() < 1)
					{
						a_depth = 1;
					}
				}
			}
			activate(i_key, a_depth);
			return i_key;
		}

		internal override void delete(bool a_deleteRemoved)
		{
			if (a_deleteRemoved)
			{
				delete(i_key);
			}
			base.delete(a_deleteRemoved);
		}
	}
}
