namespace com.db4o
{
	internal class YapFieldNull : com.db4o.YapField
	{
		public YapFieldNull() : base(null)
		{
		}

		internal override com.db4o.YapComparable PrepareComparison(object obj)
		{
			return com.db4o.Null.INSTANCE;
		}

		internal override object Read(com.db4o.inside.marshall.MarshallerFamily mf, com.db4o.YapWriter
			 a_bytes)
		{
			return null;
		}

		internal override object ReadQuery(com.db4o.Transaction a_trans, com.db4o.inside.marshall.MarshallerFamily
			 mf, com.db4o.YapReader a_reader)
		{
			return null;
		}
	}
}
