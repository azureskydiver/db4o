namespace com.db4o.inside.marshall
{
	/// <exclude></exclude>
	public abstract class ClassMarshaller
	{
		public com.db4o.inside.marshall.MarshallerFamily _family;

		public virtual com.db4o.inside.marshall.RawClassSpec ReadSpec(com.db4o.Transaction
			 trans, com.db4o.YapReader reader)
		{
			byte[] nameBytes = ReadName(trans, reader);
			string className = trans.Stream().StringIO().Read(nameBytes);
			ReadMetaClassID(reader);
			int ancestorID = reader.ReadInt();
			reader.IncrementOffset(com.db4o.YapConst.INT_LENGTH);
			int numFields = reader.ReadInt();
			return new com.db4o.inside.marshall.RawClassSpec(className, ancestorID, numFields
				);
		}

		public virtual void Write(com.db4o.Transaction trans, com.db4o.YapClass clazz, com.db4o.YapReader
			 writer)
		{
			writer.WriteShortString(trans, clazz.NameToWrite());
			int intFormerlyKnownAsMetaClassID = 0;
			writer.WriteInt(intFormerlyKnownAsMetaClassID);
			writer.WriteIDOf(trans, clazz.i_ancestor);
			WriteIndex(trans, clazz, writer);
			com.db4o.YapField[] fields = clazz.i_fields;
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

		protected virtual void WriteIndex(com.db4o.Transaction trans, com.db4o.YapClass clazz
			, com.db4o.YapReader writer)
		{
			int indexID = clazz.Index().Write(trans);
			writer.WriteInt(IndexIDForWriting(indexID));
		}

		protected abstract int IndexIDForWriting(int indexID);

		public virtual byte[] ReadName(com.db4o.Transaction trans, com.db4o.YapReader reader
			)
		{
			byte[] name = ReadName(trans.Stream().StringIO(), reader);
			return name;
		}

		public virtual int ReadMetaClassID(com.db4o.YapReader reader)
		{
			return reader.ReadInt();
		}

		private byte[] ReadName(com.db4o.YapStringIO sio, com.db4o.YapReader reader)
		{
			int len = reader.ReadInt();
			len = len * sio.BytesPerChar();
			byte[] nameBytes = new byte[len];
			System.Array.Copy(reader._buffer, reader._offset, nameBytes, 0, len);
			nameBytes = com.db4o.Platform4.UpdateClassName(nameBytes);
			reader.IncrementOffset(len);
			return nameBytes;
		}

		public virtual void Read(com.db4o.YapStream stream, com.db4o.YapClass clazz, com.db4o.YapReader
			 reader)
		{
			clazz.i_ancestor = stream.GetYapClass(reader.ReadInt());
			if (clazz.i_dontCallConstructors)
			{
				clazz.CreateConstructor(stream, clazz.ClassReflector(), clazz.GetName(), true);
			}
			clazz.CheckDb4oType();
			ReadIndex(stream, clazz, reader);
			clazz.i_fields = CreateFields(clazz, reader.ReadInt());
			ReadFields(stream, reader, clazz.i_fields);
		}

		protected abstract void ReadIndex(com.db4o.YapStream stream, com.db4o.YapClass clazz
			, com.db4o.YapReader reader);

		private com.db4o.YapField[] CreateFields(com.db4o.YapClass clazz, int fieldCount)
		{
			com.db4o.YapField[] fields = new com.db4o.YapField[fieldCount];
			for (int i = 0; i < fields.Length; i++)
			{
				fields[i] = new com.db4o.YapField(clazz);
				fields[i].SetArrayPosition(i);
			}
			return fields;
		}

		private void ReadFields(com.db4o.YapStream stream, com.db4o.YapReader reader, com.db4o.YapField[]
			 fields)
		{
			for (int i = 0; i < fields.Length; i++)
			{
				fields[i] = _family._field.Read(stream, fields[i], reader);
			}
		}

		public virtual int MarshalledLength(com.db4o.YapStream stream, com.db4o.YapClass 
			clazz)
		{
			int len = stream.StringIO().ShortLength(clazz.NameToWrite()) + com.db4o.YapConst.
				OBJECT_LENGTH + (com.db4o.YapConst.INT_LENGTH * 2) + (com.db4o.YapConst.ID_LENGTH
				);
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

		public virtual void Defrag(com.db4o.YapClass yapClass, com.db4o.YapStringIO sio, 
			com.db4o.YapReader source, com.db4o.YapReader target, com.db4o.IDMapping mapping
			, int classIndexID)
		{
			ReadName(sio, source);
			ReadName(sio, target);
			int metaClassOldID = source.ReadInt();
			int metaClassNewId = 0;
			target.WriteInt(metaClassNewId);
			int ancestorOldID = source.ReadInt();
			int ancestorNewId = mapping.MappedID(ancestorOldID);
			target.WriteInt(ancestorNewId);
			yapClass.Index().DefragReference(yapClass, source, target, mapping, classIndexID);
			source.IncrementOffset(com.db4o.YapConst.INT_LENGTH);
			target.IncrementOffset(com.db4o.YapConst.INT_LENGTH);
			com.db4o.YapField[] fields = yapClass.i_fields;
			for (int fieldIdx = 0; fieldIdx < fields.Length; fieldIdx++)
			{
				_family._field.Defrag(yapClass, fields[fieldIdx], sio, source, target, mapping);
			}
		}
	}
}
