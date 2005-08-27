
namespace com.db4o
{
	internal class YapFieldNull : com.db4o.YapField
	{
		public YapFieldNull() : base(null)
		{
		}

		internal override com.db4o.YapComparable prepareComparison(object obj)
		{
			return com.db4o.Null.INSTANCE;
		}

		internal override object read(com.db4o.YapWriter a_bytes)
		{
			return null;
		}

		internal override object readQuery(com.db4o.Transaction a_trans, com.db4o.YapReader
			 a_reader)
		{
			return null;
		}
	}
}
