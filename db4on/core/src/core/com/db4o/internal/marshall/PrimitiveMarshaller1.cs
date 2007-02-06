namespace com.db4o.@internal.marshall
{
	public class PrimitiveMarshaller1 : com.db4o.@internal.marshall.PrimitiveMarshaller
	{
		public override bool UseNormalClassRead()
		{
			return false;
		}

		public override int WriteNew(com.db4o.@internal.Transaction trans, com.db4o.@internal.PrimitiveFieldHandler
			 yapClassPrimitive, object obj, bool topLevel, com.db4o.@internal.StatefulBuffer
			 writer, bool withIndirection, bool restoreLinkOffset)
		{
			if (obj != null)
			{
				com.db4o.@internal.TypeHandler4 handler = yapClassPrimitive.i_handler;
				handler.WriteNew(_family, obj, topLevel, writer, withIndirection, restoreLinkOffset
					);
			}
			return 0;
		}

		public override j4o.util.Date ReadDate(com.db4o.@internal.Buffer bytes)
		{
			return new j4o.util.Date(com.db4o.@internal.handlers.LongHandler.ReadLong(bytes));
		}
	}
}
