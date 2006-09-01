namespace com.db4o.inside.marshall
{
	public class PrimitiveMarshaller0 : com.db4o.inside.marshall.PrimitiveMarshaller
	{
		public override bool UseNormalClassRead()
		{
			return true;
		}

		public override int WriteNew(com.db4o.Transaction trans, com.db4o.YapClassPrimitive
			 yapClassPrimitive, object obj, bool topLevel, com.db4o.YapWriter parentWriter, 
			bool withIndirection, bool restoreLinkOffset)
		{
			int id = 0;
			if (obj != null)
			{
				com.db4o.TypeHandler4 handler = yapClassPrimitive.i_handler;
				com.db4o.YapStream stream = trans.Stream();
				id = stream.NewUserObject();
				int address = -1;
				int length = ObjectLength(handler, obj);
				if (!stream.IsClient())
				{
					address = trans.i_file.GetSlot(length);
				}
				trans.SetPointer(id, address, length);
				com.db4o.YapWriter writer = new com.db4o.YapWriter(trans, length);
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
	}
}
