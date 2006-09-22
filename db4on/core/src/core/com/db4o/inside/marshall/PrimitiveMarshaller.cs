namespace com.db4o.inside.marshall
{
	public abstract class PrimitiveMarshaller
	{
		public com.db4o.inside.marshall.MarshallerFamily _family;

		public abstract bool UseNormalClassRead();

		public abstract int WriteNew(com.db4o.Transaction trans, com.db4o.YapClassPrimitive
			 yapClassPrimitive, object obj, bool topLevel, com.db4o.YapWriter parentWriter, 
			bool withIndirection, bool restoreLinkOffset);

		protected int ObjectLength(com.db4o.TypeHandler4 handler)
		{
			return handler.LinkLength() + com.db4o.YapConst.OBJECT_LENGTH + com.db4o.YapConst
				.ID_LENGTH;
		}
	}
}
