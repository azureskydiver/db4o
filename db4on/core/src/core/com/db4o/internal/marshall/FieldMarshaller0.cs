namespace com.db4o.@internal.marshall
{
	/// <exclude></exclude>
	public class FieldMarshaller0 : com.db4o.@internal.marshall.FieldMarshaller
	{
		public virtual int MarshalledLength(com.db4o.@internal.ObjectContainerBase stream
			, com.db4o.@internal.FieldMetadata field)
		{
			int len = stream.StringIO().ShortLength(field.GetName());
			if (field.NeedsArrayAndPrimitiveInfo())
			{
				len += 1;
			}
			if (field.NeedsHandlerId())
			{
				len += com.db4o.@internal.Const4.ID_LENGTH;
			}
			return len;
		}

		public virtual com.db4o.@internal.marshall.RawFieldSpec ReadSpec(com.db4o.@internal.ObjectContainerBase
			 stream, com.db4o.@internal.Buffer reader)
		{
			string name = null;
			try
			{
				name = com.db4o.@internal.marshall.StringMarshaller.ReadShort(stream, reader);
			}
			catch (com.db4o.CorruptionException)
			{
				return null;
			}
			if (name.IndexOf(com.db4o.@internal.Const4.VIRTUAL_FIELD_PREFIX) == 0)
			{
				com.db4o.@internal.VirtualFieldMetadata[] virtuals = stream.i_handlers.i_virtualFields;
				for (int i = 0; i < virtuals.Length; i++)
				{
					if (name.Equals(virtuals[i].GetName()))
					{
						return new com.db4o.@internal.marshall.RawFieldSpec(name);
					}
				}
			}
			int handlerID = reader.ReadInt();
			byte attribs = reader.ReadByte();
			return new com.db4o.@internal.marshall.RawFieldSpec(name, handlerID, attribs);
		}

		public com.db4o.@internal.FieldMetadata Read(com.db4o.@internal.ObjectContainerBase
			 stream, com.db4o.@internal.FieldMetadata field, com.db4o.@internal.Buffer reader
			)
		{
			com.db4o.@internal.marshall.RawFieldSpec spec = ReadSpec(stream, reader);
			return FromSpec(spec, stream, field);
		}

		protected virtual com.db4o.@internal.FieldMetadata FromSpec(com.db4o.@internal.marshall.RawFieldSpec
			 spec, com.db4o.@internal.ObjectContainerBase stream, com.db4o.@internal.FieldMetadata
			 field)
		{
			if (spec == null)
			{
				return field;
			}
			string name = spec.Name();
			if (spec.IsVirtual())
			{
				com.db4o.@internal.VirtualFieldMetadata[] virtuals = stream.i_handlers.i_virtualFields;
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
			field.Alive();
			return field;
		}

		public virtual void Write(com.db4o.@internal.Transaction trans, com.db4o.@internal.ClassMetadata
			 clazz, com.db4o.@internal.FieldMetadata field, com.db4o.@internal.Buffer writer
			)
		{
			field.Alive();
			writer.WriteShortString(trans, field.GetName());
			if (field.IsVirtual())
			{
				return;
			}
			com.db4o.@internal.TypeHandler4 handler = field.GetHandler();
			if (handler is com.db4o.@internal.ClassMetadata)
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
			com.db4o.foundation.BitMap4 bitmap = new com.db4o.foundation.BitMap4(3);
			bitmap.Set(0, field.IsPrimitive());
			bitmap.Set(1, handler is com.db4o.@internal.handlers.ArrayHandler);
			bitmap.Set(2, handler is com.db4o.@internal.handlers.MultidimensionalArrayHandler
				);
			writer.Append(bitmap.GetByte(0));
		}

		public virtual void Defrag(com.db4o.@internal.ClassMetadata yapClass, com.db4o.@internal.FieldMetadata
			 yapField, com.db4o.@internal.LatinStringIO sio, com.db4o.@internal.ReaderPair readers
			)
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
