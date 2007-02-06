namespace com.db4o.@internal.marshall
{
	public class PrimitiveMarshaller0 : com.db4o.@internal.marshall.PrimitiveMarshaller
	{
		public override bool UseNormalClassRead()
		{
			return true;
		}

		public override int WriteNew(com.db4o.@internal.Transaction trans, com.db4o.@internal.PrimitiveFieldHandler
			 yapClassPrimitive, object obj, bool topLevel, com.db4o.@internal.StatefulBuffer
			 parentWriter, bool withIndirection, bool restoreLinkOffset)
		{
			int id = 0;
			if (obj != null)
			{
				com.db4o.@internal.TypeHandler4 handler = yapClassPrimitive.i_handler;
				com.db4o.@internal.ObjectContainerBase stream = trans.Stream();
				id = stream.NewUserObject();
				int address = -1;
				int length = ObjectLength(handler);
				if (!stream.IsClient())
				{
					address = trans.i_file.GetSlot(length);
				}
				trans.SetPointer(id, address, length);
				com.db4o.@internal.StatefulBuffer writer = new com.db4o.@internal.StatefulBuffer(
					trans, length);
				writer.UseSlot(id, address, length);
				writer.WriteInt(yapClassPrimitive.GetID());
				handler.WriteNew(_family, obj, false, writer, true, false);
				writer.WriteEnd();
				stream.WriteNew(yapClassPrimitive, writer);
			}
			if (parentWriter != null)
			{
				parentWriter.WriteInt(id);
			}
			return id;
		}

		public override j4o.util.Date ReadDate(com.db4o.@internal.Buffer a_bytes)
		{
			long longValue = com.db4o.@internal.handlers.LongHandler.ReadLong(a_bytes);
			if (longValue == long.MaxValue)
			{
				return null;
			}
			return new j4o.util.Date(longValue);
		}
	}
}
