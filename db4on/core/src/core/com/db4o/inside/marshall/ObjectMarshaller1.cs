namespace com.db4o.inside.marshall
{
	/// <exclude></exclude>
	public class ObjectMarshaller1 : com.db4o.inside.marshall.ObjectMarshaller
	{
		public override void AddFieldIndices(com.db4o.YapClass yc, com.db4o.inside.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.YapWriter writer, bool isNew)
		{
			AddDeclaredFieldIndices(yc, (com.db4o.inside.marshall.ObjectHeaderAttributes1)attributes
				, writer, 0, isNew);
		}

		private void AddDeclaredFieldIndices(com.db4o.YapClass yc, com.db4o.inside.marshall.ObjectHeaderAttributes1
			 attributes, com.db4o.YapWriter writer, int fieldIndex, bool isNew)
		{
			int fieldCount = writer.ReadInt();
			for (int i = 0; i < fieldCount; i++)
			{
				if (attributes.IsNull(fieldIndex))
				{
					yc.i_fields[i].AddIndexEntry(writer.GetTransaction(), writer.GetID(), null);
				}
				else
				{
					yc.i_fields[i].AddFieldIndex(_family, writer, isNew);
				}
				fieldIndex++;
			}
			if (yc.i_ancestor != null)
			{
				AddDeclaredFieldIndices(yc.i_ancestor, attributes, writer, fieldIndex, isNew);
			}
		}

		public override com.db4o.TreeInt CollectFieldIDs(com.db4o.TreeInt tree, com.db4o.YapClass
			 yc, com.db4o.inside.marshall.ObjectHeaderAttributes attributes, com.db4o.YapWriter
			 reader, string name)
		{
			return CollectDeclaredFieldIDs(tree, yc, (com.db4o.inside.marshall.ObjectHeaderAttributes1
				)attributes, reader, name, 0);
		}

		public virtual com.db4o.TreeInt CollectDeclaredFieldIDs(com.db4o.TreeInt tree, com.db4o.YapClass
			 yc, com.db4o.inside.marshall.ObjectHeaderAttributes1 attributes, com.db4o.YapWriter
			 reader, string name, int fieldIndex)
		{
			int length = yc.ReadFieldCount(reader);
			for (int i = 0; i < length; i++)
			{
				if (!attributes.IsNull(fieldIndex))
				{
					if (name.Equals(yc.i_fields[i].GetName()))
					{
						tree = yc.i_fields[i].CollectIDs(_family, tree, reader);
					}
					else
					{
						yc.i_fields[i].IncrementOffset(reader);
					}
				}
				fieldIndex++;
			}
			if (yc.i_ancestor != null)
			{
				return CollectDeclaredFieldIDs(tree, yc.i_ancestor, attributes, reader, name, fieldIndex
					);
			}
			return tree;
		}

		public override void DeleteMembers(com.db4o.YapClass yc, com.db4o.inside.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.YapWriter a_bytes, int a_type, bool isUpdate)
		{
			DeleteDeclaredMembers(yc, (com.db4o.inside.marshall.ObjectHeaderAttributes1)attributes
				, a_bytes, a_type, 0, isUpdate);
		}

		public virtual void DeleteDeclaredMembers(com.db4o.YapClass yc, com.db4o.inside.marshall.ObjectHeaderAttributes1
			 attributes, com.db4o.YapWriter a_bytes, int a_type, int fieldIndex, bool isUpdate
			)
		{
			int length = yc.ReadFieldCount(a_bytes);
			for (int i = 0; i < length; i++)
			{
				if (attributes.IsNull(fieldIndex))
				{
					yc.i_fields[i].RemoveIndexEntry(a_bytes.GetTransaction(), a_bytes.GetID(), null);
				}
				else
				{
					yc.i_fields[i].Delete(_family, a_bytes, isUpdate);
				}
				fieldIndex++;
			}
			if (yc.i_ancestor != null)
			{
				DeleteDeclaredMembers(yc.i_ancestor, attributes, a_bytes, a_type, fieldIndex, isUpdate
					);
			}
		}

		public override bool FindOffset(com.db4o.YapClass yc, com.db4o.inside.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.YapReader a_bytes, com.db4o.YapField a_field)
		{
			return FindDeclaredOffset(yc, (com.db4o.inside.marshall.ObjectHeaderAttributes1)attributes
				, a_bytes, a_field, 0);
		}

		public virtual bool FindDeclaredOffset(com.db4o.YapClass yc, com.db4o.inside.marshall.ObjectHeaderAttributes1
			 attributes, com.db4o.YapReader a_bytes, com.db4o.YapField a_field, int fieldIndex
			)
		{
			int fieldCount = com.db4o.Debug.atHome ? yc.ReadFieldCountSodaAtHome(a_bytes) : yc
				.ReadFieldCount(a_bytes);
			for (int i = 0; i < fieldCount; i++)
			{
				if (yc.i_fields[i] == a_field)
				{
					return !attributes.IsNull(fieldIndex);
				}
				if (!attributes.IsNull(fieldIndex))
				{
					a_bytes.IncrementOffset(yc.i_fields[i].LinkLength());
				}
				fieldIndex++;
			}
			if (yc.i_ancestor == null)
			{
				return false;
			}
			return FindDeclaredOffset(yc.i_ancestor, attributes, a_bytes, a_field, fieldIndex
				);
		}

		public override void InstantiateFields(com.db4o.YapClass yc, com.db4o.inside.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.YapObject a_yapObject, object a_onObject, com.db4o.YapWriter
			 a_bytes)
		{
			InstantiateDeclaredFields(yc, (com.db4o.inside.marshall.ObjectHeaderAttributes1)attributes
				, a_yapObject, a_onObject, a_bytes, 0);
		}

		public virtual void InstantiateDeclaredFields(com.db4o.YapClass yc, com.db4o.inside.marshall.ObjectHeaderAttributes1
			 attributes, com.db4o.YapObject a_yapObject, object a_onObject, com.db4o.YapWriter
			 a_bytes, int fieldIndex)
		{
			int fieldCount = yc.ReadFieldCount(a_bytes);
			try
			{
				for (int i = 0; i < fieldCount; i++)
				{
					if (attributes.IsNull(fieldIndex))
					{
						yc.i_fields[i].Set(a_onObject, null);
					}
					else
					{
						yc.i_fields[i].Instantiate(_family, a_yapObject, a_onObject, a_bytes);
					}
					fieldIndex++;
				}
				if (yc.i_ancestor != null)
				{
					InstantiateDeclaredFields(yc.i_ancestor, attributes, a_yapObject, a_onObject, a_bytes
						, fieldIndex);
				}
			}
			catch (com.db4o.CorruptionException ce)
			{
			}
		}

		private void Marshall(com.db4o.YapObject yo, object obj, com.db4o.inside.marshall.ObjectHeaderAttributes1
			 attributes, com.db4o.YapWriter writer, int fieldIndex, bool isNew)
		{
			com.db4o.YapClass yc = yo.GetYapClass();
			writer.WriteInt(-yc.GetID());
			attributes.Write(writer);
			yc.CheckUpdateDepth(writer);
			MarshallDeclaredFields(yc, yo, obj, attributes, writer, fieldIndex, isNew);
		}

		private void MarshallDeclaredFields(com.db4o.YapClass yc, com.db4o.YapObject yo, 
			object obj, com.db4o.inside.marshall.ObjectHeaderAttributes1 attributes, com.db4o.YapWriter
			 writer, int fieldIndex, bool isNew)
		{
			com.db4o.Config4Class config = yc.ConfigOrAncestorConfig();
			com.db4o.Transaction trans = writer.GetTransaction();
			int fieldCount = yc.i_fields.Length;
			writer.WriteInt(fieldCount);
			for (int i = 0; i < fieldCount; i++)
			{
				com.db4o.YapField yf = yc.i_fields[i];
				if (!attributes.IsNull(fieldIndex))
				{
					object child = yf.GetOrCreate(trans, obj);
					if (child is com.db4o.Db4oTypeImpl)
					{
						child = ((com.db4o.Db4oTypeImpl)child).StoredTo(trans);
					}
					yf.Marshall(yo, child, _family, writer, config, isNew);
				}
				else
				{
					yf.AddIndexEntry(trans, writer.GetID(), null);
				}
				fieldIndex++;
			}
			if (yc.i_ancestor != null)
			{
				MarshallDeclaredFields(yc.i_ancestor, yo, obj, attributes, writer, fieldIndex, isNew
					);
			}
		}

		public override com.db4o.YapWriter MarshallNew(com.db4o.Transaction a_trans, com.db4o.YapObject
			 yo, int a_updateDepth)
		{
			com.db4o.inside.marshall.ObjectHeaderAttributes1 attributes = new com.db4o.inside.marshall.ObjectHeaderAttributes1
				(yo);
			com.db4o.YapWriter writer = CreateWriterForNew(a_trans, yo, a_updateDepth, attributes
				.ObjectLength());
			Marshall(yo, yo.GetObject(), attributes, writer, 0, true);
			return writer;
		}

		public override void MarshallUpdate(com.db4o.Transaction trans, int updateDepth, 
			com.db4o.YapObject yo, object obj)
		{
			com.db4o.inside.marshall.ObjectHeaderAttributes1 attributes = new com.db4o.inside.marshall.ObjectHeaderAttributes1
				(yo);
			com.db4o.YapWriter writer = CreateWriterForUpdate(trans, updateDepth, yo.GetID(), 
				0, attributes.ObjectLength());
			if (trans.i_file != null)
			{
				trans.i_file.GetSlotForUpdate(writer);
			}
			Marshall(yo, obj, attributes, writer, 0, false);
			MarshallUpdateWrite(trans, yo, obj, writer);
		}

		public override com.db4o.inside.marshall.ObjectHeaderAttributes ReadHeaderAttributes
			(com.db4o.YapReader reader)
		{
			return new com.db4o.inside.marshall.ObjectHeaderAttributes1(reader);
		}

		public override object ReadIndexEntry(com.db4o.YapClass yc, com.db4o.inside.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.YapField yf, com.db4o.YapWriter reader)
		{
			if (yc == null)
			{
				return null;
			}
			if (!FindOffset(yc, attributes, reader, yf))
			{
				return null;
			}
			return yf.ReadIndexEntry(_family, reader);
		}

		public override void ReadVirtualAttributes(com.db4o.Transaction trans, com.db4o.YapClass
			 yc, com.db4o.YapObject yo, com.db4o.inside.marshall.ObjectHeaderAttributes attributes
			, com.db4o.YapReader reader)
		{
			ReadVirtualAttributesDeclared(trans, yc, yo, (com.db4o.inside.marshall.ObjectHeaderAttributes1
				)attributes, reader, 0);
		}

		private void ReadVirtualAttributesDeclared(com.db4o.Transaction trans, com.db4o.YapClass
			 yc, com.db4o.YapObject yo, com.db4o.inside.marshall.ObjectHeaderAttributes1 attributes
			, com.db4o.YapReader reader, int fieldIndex)
		{
			int fieldCount = yc.ReadFieldCount(reader);
			for (int i = 0; i < fieldCount; i++)
			{
				if (!attributes.IsNull(fieldIndex))
				{
					yc.i_fields[i].ReadVirtualAttribute(trans, reader, yo);
				}
				fieldIndex++;
			}
			if (yc.i_ancestor != null)
			{
				ReadVirtualAttributesDeclared(trans, yc.i_ancestor, yo, attributes, reader, fieldIndex
					);
			}
		}
	}
}
