namespace com.db4o.inside.marshall
{
	/// <exclude></exclude>
	public class FieldMarshaller1 : com.db4o.inside.marshall.FieldMarshaller0
	{
		private bool HasBTreeIndex(com.db4o.YapField field)
		{
			return !field.IsVirtual();
		}

		public override void Write(com.db4o.Transaction trans, com.db4o.YapClass clazz, com.db4o.YapField
			 field, com.db4o.YapReader writer)
		{
			base.Write(trans, clazz, field, writer);
			if (!HasBTreeIndex(field))
			{
				return;
			}
			writer.WriteIDOf(trans, field.GetIndex(trans));
		}

		public override com.db4o.inside.marshall.RawFieldSpec ReadSpec(com.db4o.YapStream
			 stream, com.db4o.YapReader reader)
		{
			com.db4o.inside.marshall.RawFieldSpec spec = base.ReadSpec(stream, reader);
			if (spec == null)
			{
				return null;
			}
			if (spec.IsVirtual())
			{
				return spec;
			}
			int indexID = reader.ReadInt();
			spec.IndexID(indexID);
			return spec;
		}

		protected override com.db4o.YapField FromSpec(com.db4o.inside.marshall.RawFieldSpec
			 spec, com.db4o.YapStream stream, com.db4o.YapField field)
		{
			com.db4o.YapField actualField = base.FromSpec(spec, stream, field);
			if (spec == null)
			{
				return field;
			}
			if (spec.IndexID() != 0)
			{
				actualField.InitIndex(stream.GetSystemTransaction(), spec.IndexID());
			}
			return actualField;
		}

		public override int MarshalledLength(com.db4o.YapStream stream, com.db4o.YapField
			 field)
		{
			int len = base.MarshalledLength(stream, field);
			if (!HasBTreeIndex(field))
			{
				return len;
			}
			int BTREE_ID = com.db4o.YapConst.ID_LENGTH;
			return len + BTREE_ID;
		}

		public override void Defrag(com.db4o.YapClass yapClass, com.db4o.YapField yapField
			, com.db4o.YapStringIO sio, com.db4o.ReaderPair readers)
		{
			base.Defrag(yapClass, yapField, sio, readers);
			if (yapField.IsVirtual())
			{
				return;
			}
			if (yapField.HasIndex())
			{
				com.db4o.inside.btree.BTree index = yapField.GetIndex(readers.SystemTrans());
				int targetIndexID = readers.CopyID();
				if (targetIndexID != 0)
				{
					index.DefragBTree(readers.Context());
				}
			}
			else
			{
				readers.WriteInt(0);
			}
		}
	}
}
