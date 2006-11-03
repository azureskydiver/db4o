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

		public virtual com.db4o.inside.marshall.RawFieldSpec ReadSpec(com.db4o.YapStream 
			stream, com.db4o.YapReader reader)
		{
			string name = null;
			try
			{
				name = com.db4o.inside.marshall.StringMarshaller.ReadShort(stream, reader);
			}
			catch (com.db4o.CorruptionException)
			{
				return null;
			}
			if (name.IndexOf(com.db4o.YapConst.VIRTUAL_FIELD_PREFIX) == 0)
			{
				com.db4o.YapFieldVirtual[] virtuals = stream.i_handlers.i_virtualFields;
				for (int i = 0; i < virtuals.Length; i++)
				{
					if (name.Equals(virtuals[i].GetName()))
					{
						return new com.db4o.inside.marshall.RawFieldSpec(name);
					}
				}
			}
			int handlerID = reader.ReadInt();
			byte attribs = reader.ReadByte();
			return new com.db4o.inside.marshall.RawFieldSpec(name, handlerID, attribs);
		}

		public com.db4o.YapField Read(com.db4o.YapStream stream, com.db4o.YapField field, 
			com.db4o.YapReader reader)
		{
			com.db4o.inside.marshall.RawFieldSpec spec = ReadSpec(stream, reader);
			return FromSpec(spec, stream, field);
		}

		protected virtual com.db4o.YapField FromSpec(com.db4o.inside.marshall.RawFieldSpec
			 spec, com.db4o.YapStream stream, com.db4o.YapField field)
		{
			if (spec == null)
			{
				return field;
			}
			string name = spec.Name();
			if (spec.IsVirtual())
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
			field.Init(spec.HandlerID(), spec.IsPrimitive(), spec.IsArray(), spec.IsNArray());
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
			, com.db4o.YapStringIO sio, com.db4o.ReaderPair readers)
		{
			readers.ReadShortString(sio);
			if (yapField.IsVirtual())
			{
				return;
			}
			readers.CopyID();
			readers.IncrementOffset(1);
		}
	}
}
