namespace com.db4o.@internal.marshall
{
	public abstract class PrimitiveMarshaller
	{
		public com.db4o.@internal.marshall.MarshallerFamily _family;

		public abstract bool UseNormalClassRead();

		public abstract int WriteNew(com.db4o.@internal.Transaction trans, com.db4o.@internal.PrimitiveFieldHandler
			 yapClassPrimitive, object obj, bool topLevel, com.db4o.@internal.StatefulBuffer
			 parentWriter, bool withIndirection, bool restoreLinkOffset);

		public abstract j4o.util.Date ReadDate(com.db4o.@internal.Buffer bytes);

		protected int ObjectLength(com.db4o.@internal.TypeHandler4 handler)
		{
			return handler.LinkLength() + com.db4o.@internal.Const4.OBJECT_LENGTH + com.db4o.@internal.Const4
				.ID_LENGTH;
		}
	}
}
