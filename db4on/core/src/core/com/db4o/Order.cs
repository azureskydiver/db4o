namespace com.db4o
{
	internal class Order : com.db4o.Orderable
	{
		private int i_major;

		private int i_minor;

		public virtual int compareTo(object obj)
		{
			if (obj is com.db4o.Order)
			{
				com.db4o.Order other = (com.db4o.Order)obj;
				int res = i_major - other.i_major;
				if (res != 0)
				{
					return res;
				}
				return i_minor - other.i_minor;
			}
			return 1;
		}

		public virtual void hintOrder(int a_order, bool a_major)
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

		public virtual bool hasDuplicates()
		{
			return true;
		}
	}
}
