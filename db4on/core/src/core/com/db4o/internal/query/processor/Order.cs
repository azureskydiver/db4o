namespace com.db4o.@internal.query.processor
{
	internal class Order : com.db4o.@internal.query.processor.Orderable
	{
		private int i_major;

		private int i_minor;

		public virtual int CompareTo(object obj)
		{
			if (obj is com.db4o.@internal.query.processor.Order)
			{
				com.db4o.@internal.query.processor.Order other = (com.db4o.@internal.query.processor.Order
					)obj;
				int res = other.i_major - i_major;
				if (res != 0)
				{
					return res;
				}
				return other.i_minor - i_minor;
			}
			return 1;
		}

		public virtual void HintOrder(int a_order, bool a_major)
		{
			if (a_major)
			{
				i_major = a_order;
			}
			else
			{
				i_minor = a_order;
			}
		}

		public virtual bool HasDuplicates()
		{
			return true;
		}
	}
}
