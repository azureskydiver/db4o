namespace com.db4o.@internal.marshall
{
	/// <exclude></exclude>
	public class FieldMarshaller1 : com.db4o.@internal.marshall.FieldMarshaller0
	{
		private bool HasBTreeIndex(com.db4o.@internal.FieldMetadata field)
		{
			return !field.IsVirtual();
		}

		public override void Write(com.db4o.@internal.Transaction trans, com.db4o.@internal.ClassMetadata
			 clazz, com.db4o.@internal.FieldMetadata field, com.db4o.@internal.Buffer writer
			)
		{
			base.Write(trans, clazz, field, writer);
			if (!HasBTreeIndex(field))
			{
				return;
			}
			writer.WriteIDOf(trans, field.GetIndex(trans));
		}

		public override com.db4o.@internal.marshall.RawFieldSpec ReadSpec(com.db4o.@internal.ObjectContainerBase
			 stream, com.db4o.@internal.Buffer reader)
		{
			com.db4o.@internal.marshall.RawFieldSpec spec = base.ReadSpec(stream, reader);
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

		protected override com.db4o.@internal.FieldMetadata FromSpec(com.db4o.@internal.marshall.RawFieldSpec
			 spec, com.db4o.@internal.ObjectContainerBase stream, com.db4o.@internal.FieldMetadata
			 field)
		{
			com.db4o.@internal.FieldMetadata actualField = base.FromSpec(spec, stream, field);
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

		public override int MarshalledLength(com.db4o.@internal.ObjectContainerBase stream
			, com.db4o.@internal.FieldMetadata field)
		{
			int len = base.MarshalledLength(stream, field);
			if (!HasBTreeIndex(field))
			{
				return len;
			}
			int BTREE_ID = com.db4o.@internal.Const4.ID_LENGTH;
			return len + BTREE_ID;
		}

		public override void Defrag(com.db4o.@internal.ClassMetadata yapClass, com.db4o.@internal.FieldMetadata
			 yapField, com.db4o.@internal.LatinStringIO sio, com.db4o.@internal.ReaderPair readers
			)
		{
			base.Defrag(yapClass, yapField, sio, readers);
			if (yapField.IsVirtual())
			{
				return;
			}
			if (yapField.HasIndex())
			{
				com.db4o.@internal.btree.BTree index = yapField.GetIndex(readers.SystemTrans());
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
