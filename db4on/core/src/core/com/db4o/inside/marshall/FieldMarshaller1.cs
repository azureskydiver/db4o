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
			writer.WriteIDOf(trans, field.GetIndex());
		}

		public override com.db4o.YapField Read(com.db4o.YapStream stream, com.db4o.YapField
			 originalField, com.db4o.YapReader reader)
		{
			com.db4o.YapField actualField = base.Read(stream, originalField, reader);
			if (!HasBTreeIndex(actualField))
			{
				return actualField;
			}
			int id = reader.ReadInt();
			if (id == 0)
			{
				return actualField;
			}
			actualField.InitIndex(stream.GetSystemTransaction(), id);
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
	}
}
