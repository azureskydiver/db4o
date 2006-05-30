namespace com.db4o.inside.marshall
{
	public class PrimitiveMarshaller1 : com.db4o.inside.marshall.PrimitiveMarshaller
	{
		public override bool UseNormalClassRead()
		{
			return false;
		}

		public override int WriteNew(com.db4o.Transaction trans, com.db4o.YapClassPrimitive
			 yapClassPrimitive, object obj, bool topLevel, com.db4o.YapWriter writer, bool withIndirection
			)
		{
			if (obj != null)
			{
				com.db4o.TypeHandler4 handler = yapClassPrimitive.i_handler;
				handler.WriteNew(_family, obj, topLevel, writer, withIndirection);
			}
			return 0;
		}
	}
}
