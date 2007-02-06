namespace com.db4o.@internal.marshall
{
	/// <exclude></exclude>
	public abstract class ClassMarshaller
	{
		public com.db4o.@internal.marshall.MarshallerFamily _family;

		public virtual com.db4o.@internal.marshall.RawClassSpec ReadSpec(com.db4o.@internal.Transaction
			 trans, com.db4o.@internal.Buffer reader)
		{
			byte[] nameBytes = ReadName(trans, reader);
			string className = trans.Stream().StringIO().Read(nameBytes);
			ReadMetaClassID(reader);
			int ancestorID = reader.ReadInt();
			reader.IncrementOffset(com.db4o.@internal.Const4.INT_LENGTH);
			int numFields = reader.ReadInt();
			return new com.db4o.@internal.marshall.RawClassSpec(className, ancestorID, numFields
				);
		}

		public virtual void Write(com.db4o.@internal.Transaction trans, com.db4o.@internal.ClassMetadata
			 clazz, com.db4o.@internal.Buffer writer)
		{
			writer.WriteShortString(trans, clazz.NameToWrite());
			int intFormerlyKnownAsMetaClassID = 0;
			writer.WriteInt(intFormerlyKnownAsMetaClassID);
			writer.WriteIDOf(trans, clazz.i_ancestor);
			WriteIndex(trans, clazz, writer);
			com.db4o.@internal.FieldMetadata[] fields = clazz.i_fields;
			if (fields == null)
			{
				writer.WriteInt(0);
				return;
			}
			writer.WriteInt(fields.Length);
			for (int i = 0; i < fields.Length; i++)
			{
				_family._field.Write(trans, clazz, fields[i], writer);
			}
		}

		protected virtual void WriteIndex(com.db4o.@internal.Transaction trans, com.db4o.@internal.ClassMetadata
			 clazz, com.db4o.@internal.Buffer writer)
		{
			int indexID = clazz.Index().Write(trans);
			writer.WriteInt(IndexIDForWriting(indexID));
		}

		protected abstract int IndexIDForWriting(int indexID);

		public virtual byte[] ReadName(com.db4o.@internal.Transaction trans, com.db4o.@internal.Buffer
			 reader)
		{
			byte[] name = ReadName(trans.Stream().StringIO(), reader);
			return name;
		}

		public virtual int ReadMetaClassID(com.db4o.@internal.Buffer reader)
		{
			return reader.ReadInt();
		}

		private byte[] ReadName(com.db4o.@internal.LatinStringIO sio, com.db4o.@internal.Buffer
			 reader)
		{
			int len = reader.ReadInt();
			len = len * sio.BytesPerChar();
			byte[] nameBytes = new byte[len];
			System.Array.Copy(reader._buffer, reader._offset, nameBytes, 0, len);
			nameBytes = com.db4o.@internal.Platform4.UpdateClassName(nameBytes);
			reader.IncrementOffset(len);
			return nameBytes;
		}

		public virtual void Read(com.db4o.@internal.ObjectContainerBase stream, com.db4o.@internal.ClassMetadata
			 clazz, com.db4o.@internal.Buffer reader)
		{
			clazz.i_ancestor = stream.GetYapClass(reader.ReadInt());
			if (clazz.i_dontCallConstructors)
			{
				clazz.CreateConstructor(stream, clazz.ClassReflector(), clazz.GetName(), true);
			}
			clazz.CheckType();
			ReadIndex(stream, clazz, reader);
			clazz.i_fields = CreateFields(clazz, reader.ReadInt());
			ReadFields(stream, reader, clazz.i_fields);
		}

		protected abstract void ReadIndex(com.db4o.@internal.ObjectContainerBase stream, 
			com.db4o.@internal.ClassMetadata clazz, com.db4o.@internal.Buffer reader);

		private com.db4o.@internal.FieldMetadata[] CreateFields(com.db4o.@internal.ClassMetadata
			 clazz, int fieldCount)
		{
			com.db4o.@internal.FieldMetadata[] fields = new com.db4o.@internal.FieldMetadata[
				fieldCount];
			for (int i = 0; i < fields.Length; i++)
			{
				fields[i] = new com.db4o.@internal.FieldMetadata(clazz);
				fields[i].SetArrayPosition(i);
			}
			return fields;
		}

		private void ReadFields(com.db4o.@internal.ObjectContainerBase stream, com.db4o.@internal.Buffer
			 reader, com.db4o.@internal.FieldMetadata[] fields)
		{
			for (int i = 0; i < fields.Length; i++)
			{
				fields[i] = _family._field.Read(stream, fields[i], reader);
			}
		}

		public virtual int MarshalledLength(com.db4o.@internal.ObjectContainerBase stream
			, com.db4o.@internal.ClassMetadata clazz)
		{
			int len = stream.StringIO().ShortLength(clazz.NameToWrite()) + com.db4o.@internal.Const4
				.OBJECT_LENGTH + (com.db4o.@internal.Const4.INT_LENGTH * 2) + (com.db4o.@internal.Const4
				.ID_LENGTH);
			len += clazz.Index().OwnLength();
			if (clazz.i_fields != null)
			{
				for (int i = 0; i < clazz.i_fields.Length; i++)
				{
					len += _family._field.MarshalledLength(stream, clazz.i_fields[i]);
				}
			}
			return len;
		}

		public virtual void Defrag(com.db4o.@internal.ClassMetadata yapClass, com.db4o.@internal.LatinStringIO
			 sio, com.db4o.@internal.ReaderPair readers, int classIndexID)
		{
			ReadName(sio, readers.Source());
			ReadName(sio, readers.Target());
			int metaClassID = 0;
			readers.WriteInt(metaClassID);
			readers.CopyID();
			readers.WriteInt(IndexIDForWriting(classIndexID));
			readers.IncrementIntSize();
			com.db4o.@internal.FieldMetadata[] fields = yapClass.i_fields;
			for (int fieldIdx = 0; fieldIdx < fields.Length; fieldIdx++)
			{
				_family._field.Defrag(yapClass, fields[fieldIdx], sio, readers);
			}
		}
	}
}
