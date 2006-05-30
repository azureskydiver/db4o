namespace com.db4o.inside.marshall
{
	/// <exclude></exclude>
	internal class ObjectMarshaller0 : com.db4o.inside.marshall.ObjectMarshaller
	{
		public override void AddFieldIndices(com.db4o.YapClass yc, com.db4o.inside.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.YapWriter writer, bool isNew)
		{
			int fieldCount = writer.ReadInt();
			for (int i = 0; i < fieldCount; i++)
			{
				yc.i_fields[i].AddFieldIndex(_family, writer, isNew);
			}
			if (yc.i_ancestor != null)
			{
				AddFieldIndices(yc.i_ancestor, attributes, writer, isNew);
			}
		}

		public override com.db4o.TreeInt CollectFieldIDs(com.db4o.TreeInt tree, com.db4o.YapClass
			 yc, com.db4o.inside.marshall.ObjectHeaderAttributes attributes, com.db4o.YapWriter
			 reader, string name)
		{
			int length = yc.ReadFieldCount(reader);
			for (int i = 0; i < length; i++)
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
			if (yc.i_ancestor != null)
			{
				return CollectFieldIDs(tree, yc.i_ancestor, attributes, reader, name);
			}
			return tree;
		}

		public override void DeleteMembers(com.db4o.YapClass yc, com.db4o.inside.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.YapWriter a_bytes, int a_type, bool isUpdate)
		{
			int length = yc.ReadFieldCount(a_bytes);
			for (int i = 0; i < length; i++)
			{
				yc.i_fields[i].Delete(_family, a_bytes, isUpdate);
			}
			if (yc.i_ancestor != null)
			{
				DeleteMembers(yc.i_ancestor, attributes, a_bytes, a_type, isUpdate);
			}
		}

		public override bool FindOffset(com.db4o.YapClass yc, com.db4o.inside.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.YapReader a_bytes, com.db4o.YapField a_field)
		{
			int length = com.db4o.Debug.atHome ? yc.ReadFieldCountSodaAtHome(a_bytes) : yc.ReadFieldCount
				(a_bytes);
			for (int i = 0; i < length; i++)
			{
				if (yc.i_fields[i] == a_field)
				{
					return true;
				}
				a_bytes.IncrementOffset(yc.i_fields[i].LinkLength());
			}
			if (yc.i_ancestor == null)
			{
				return false;
			}
			return FindOffset(yc.i_ancestor, attributes, a_bytes, a_field);
		}

		protected int HeaderLength()
		{
			return com.db4o.YapConst.OBJECT_LENGTH + com.db4o.YapConst.YAPID_LENGTH;
		}

		public override void InstantiateFields(com.db4o.YapClass yc, com.db4o.inside.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.YapObject a_yapObject, object a_onObject, com.db4o.YapWriter
			 a_bytes)
		{
			int length = yc.ReadFieldCount(a_bytes);
			try
			{
				for (int i = 0; i < length; i++)
				{
					yc.i_fields[i].Instantiate(_family, a_yapObject, a_onObject, a_bytes);
				}
				if (yc.i_ancestor != null)
				{
					InstantiateFields(yc.i_ancestor, attributes, a_yapObject, a_onObject, a_bytes);
				}
			}
			catch (com.db4o.CorruptionException ce)
			{
			}
		}

		private int LinkLength(com.db4o.YapClass yc, com.db4o.YapObject yo)
		{
			int length = com.db4o.YapConst.YAPINT_LENGTH;
			if (yc.i_fields != null)
			{
				for (int i = 0; i < yc.i_fields.Length; i++)
				{
					length += LinkLength(yc.i_fields[i], yo);
				}
			}
			if (yc.i_ancestor != null)
			{
				length += LinkLength(yc.i_ancestor, yo);
			}
			return length;
		}

		protected virtual int LinkLength(com.db4o.YapField yf, com.db4o.YapObject yo)
		{
			return yf.LinkLength();
		}

		private void Marshall(com.db4o.YapClass yapClass, com.db4o.YapObject a_yapObject, 
			object a_object, com.db4o.YapWriter writer, bool a_new)
		{
			MarshallDeclaredFields(yapClass, a_yapObject, a_object, writer, a_new);
		}

		private void MarshallDeclaredFields(com.db4o.YapClass yapClass, com.db4o.YapObject
			 a_yapObject, object a_object, com.db4o.YapWriter a_bytes, bool a_new)
		{
			com.db4o.Config4Class config = yapClass.ConfigOrAncestorConfig();
			a_bytes.WriteInt(yapClass.i_fields.Length);
			for (int i = 0; i < yapClass.i_fields.Length; i++)
			{
				object obj = yapClass.i_fields[i].GetOrCreate(a_bytes.GetTransaction(), a_object);
				if (obj is com.db4o.Db4oTypeImpl)
				{
					obj = ((com.db4o.Db4oTypeImpl)obj).StoredTo(a_bytes.GetTransaction());
				}
				yapClass.i_fields[i].Marshall(a_yapObject, obj, _family, a_bytes, config, a_new);
			}
			if (yapClass.i_ancestor != null)
			{
				MarshallDeclaredFields(yapClass.i_ancestor, a_yapObject, a_object, a_bytes, a_new
					);
			}
		}

		protected virtual int MarshalledLength(com.db4o.YapField yf, com.db4o.YapObject yo
			)
		{
			return 0;
		}

		public override com.db4o.YapWriter MarshallNew(com.db4o.Transaction a_trans, com.db4o.YapObject
			 yo, int a_updateDepth)
		{
			com.db4o.YapWriter writer = CreateWriterForNew(a_trans, yo, a_updateDepth, ObjectLength
				(yo));
			com.db4o.YapClass yc = yo.GetYapClass();
			object obj = yo.GetObject();
			if (yc.IsPrimitive())
			{
				((com.db4o.YapClassPrimitive)yc).i_handler.WriteNew(com.db4o.inside.marshall.MarshallerFamily
					.Current(), obj, false, writer, true);
			}
			else
			{
				writer.WriteInt(yc.GetID());
				yc.CheckUpdateDepth(writer);
				Marshall(yc, yo, obj, writer, true);
			}
			return writer;
		}

		public override void MarshallUpdate(com.db4o.Transaction trans, int updateDepth, 
			com.db4o.YapObject yapObject, object obj)
		{
			com.db4o.YapWriter writer = CreateWriterForUpdate(trans, updateDepth, yapObject.GetID
				(), 0, ObjectLength(yapObject));
			com.db4o.YapClass yapClass = yapObject.GetYapClass();
			yapClass.CheckUpdateDepth(writer);
			writer.WriteInt(yapClass.GetID());
			Marshall(yapClass, yapObject, obj, writer, false);
			MarshallUpdateWrite(trans, yapObject, obj, writer);
		}

		private int ObjectLength(com.db4o.YapObject yo)
		{
			return HeaderLength() + LinkLength(yo.GetYapClass(), yo);
		}

		public override com.db4o.inside.marshall.ObjectHeaderAttributes ReadHeaderAttributes
			(com.db4o.YapReader reader)
		{
			return null;
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
			int length = yc.ReadFieldCount(reader);
			for (int i = 0; i < length; i++)
			{
				yc.i_fields[i].ReadVirtualAttribute(trans, reader, yo);
			}
			if (yc.i_ancestor != null)
			{
				ReadVirtualAttributes(trans, yc.i_ancestor, yo, attributes, reader);
			}
		}
	}
}
