namespace com.db4o.inside.marshall
{
	/// <exclude></exclude>
	public class FieldMarshaller0 : com.db4o.inside.marshall.FieldMarshaller
	{
		public virtual int MarshalledLength(com.db4o.YapStream stream, com.db4o.YapField 
			field)
		{
			int len = stream.StringIO().ShortLength(field.GetName());
			if (field.NeedsArrayAndPrimitiveInfo())
			{
				len += 1;
			}
			if (field.NeedsHandlerId())
			{
				len += com.db4o.YapConst.ID_LENGTH;
			}
			return len;
		}

		public virtual com.db4o.YapField Read(com.db4o.YapStream stream, com.db4o.YapField
			 field, com.db4o.YapReader reader)
		{
			string name = null;
			try
			{
				name = com.db4o.inside.marshall.StringMarshaller.ReadShort(stream, reader);
			}
			catch (com.db4o.CorruptionException ce)
			{
				return field;
			}
			if (name.IndexOf(com.db4o.YapConst.VIRTUAL_FIELD_PREFIX) == 0)
			{
				com.db4o.YapFieldVirtual[] virtuals = stream.i_handlers.i_virtualFields;
				for (int i = 0; i < virtuals.Length; i++)
				{
					if (name.Equals(virtuals[i].GetName()))
					{
						return virtuals[i];
					}
				}
			}
			field.Init(field.GetParentYapClass(), name);
			int handlerID = reader.ReadInt();
			com.db4o.YapBit yb = new com.db4o.YapBit(reader.ReadByte());
			bool isPrimitive = yb.Get();
			bool isArray = yb.Get();
			bool isNArray = yb.Get();
			field.Init(handlerID, isPrimitive, isArray, isNArray);
			field.LoadHandler(stream);
			return field;
		}

		public virtual void Write(com.db4o.Transaction trans, com.db4o.YapClass clazz, com.db4o.YapField
			 field, com.db4o.YapReader writer)
		{
			field.Alive();
			writer.WriteShortString(trans, field.GetName());
			if (field.IsVirtual())
			{
				return;
			}
			com.db4o.TypeHandler4 handler = field.GetHandler();
			if (handler is com.db4o.YapClass)
			{
				if (handler.GetID() == 0)
				{
					trans.Stream().NeedsUpdate(clazz);
				}
			}
			int handlerID = 0;
			try
			{
				handlerID = handler.GetID();
			}
			catch (System.Exception e)
			{
			}
			if (handlerID == 0)
			{
				handlerID = field.GetHandlerID();
			}
			writer.WriteInt(handlerID);
			com.db4o.YapBit yb = new com.db4o.YapBit(0);
			yb.Set(handler is com.db4o.YapArrayN);
			yb.Set(handler is com.db4o.YapArray);
			yb.Set(field.IsPrimitive());
			writer.Append(yb.GetByte());
		}

		public virtual void Defrag(com.db4o.YapClass yapClass, com.db4o.YapField yapField
			, com.db4o.YapStringIO sio, com.db4o.YapReader source, com.db4o.YapReader target
			, com.db4o.IDMapping mapping)
		{
			com.db4o.inside.marshall.StringMarshaller.ReadShort(sio, false, source);
			com.db4o.inside.marshall.StringMarshaller.ReadShort(sio, false, target);
			if (yapField.IsVirtual())
			{
				return;
			}
			int oldHandlerID = source.ReadInt();
			int newHandlerID = mapping.MappedID(oldHandlerID);
			if (oldHandlerID != newHandlerID)
			{
				target.WriteInt(newHandlerID);
			}
			else
			{
				target.IncrementOffset(com.db4o.YapConst.INT_LENGTH);
			}
			source.IncrementOffset(1);
			target.IncrementOffset(1);
		}
	}
}
