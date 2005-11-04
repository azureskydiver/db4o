namespace com.db4o
{
	internal class Null : com.db4o.YapComparable
	{
		public virtual int compareTo(object a_obj)
		{
			if (a_obj == null)
			{
				return 0;
			}
			return -1;
		}

		public override bool Equals(object obj)
		{
			return obj == null;
		}

		public virtual bool isEqual(object obj)
		{
			return obj == null;
		}

		public virtual bool isGreater(object obj)
		{
			return false;
		}

		public virtual bool isSmaller(object obj)
		{
			return false;
		}

		public virtual com.db4o.YapComparable prepareComparison(object obj)
		{
			return this;
		}

		internal static readonly com.db4o.YapComparable INSTANCE = new com.db4o.Null();
	}
}
