namespace com.db4o.@internal
{
	/// <exclude></exclude>
	public class NullFieldMetadata : com.db4o.@internal.FieldMetadata
	{
		public NullFieldMetadata() : base(null)
		{
		}

		public override com.db4o.@internal.Comparable4 PrepareComparison(object obj)
		{
			return com.db4o.@internal.Null.INSTANCE;
		}

		internal override object Read(com.db4o.@internal.marshall.MarshallerFamily mf, com.db4o.@internal.StatefulBuffer
			 a_bytes)
		{
			return null;
		}

		public override object ReadQuery(com.db4o.@internal.Transaction a_trans, com.db4o.@internal.marshall.MarshallerFamily
			 mf, com.db4o.@internal.Buffer a_reader)
		{
			return null;
		}
	}
}
