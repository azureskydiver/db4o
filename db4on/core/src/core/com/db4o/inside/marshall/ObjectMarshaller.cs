namespace com.db4o.inside.marshall
{
	public abstract class ObjectMarshaller
	{
		public com.db4o.inside.marshall.MarshallerFamily _family;

		public abstract void AddFieldIndices(com.db4o.YapClass yc, com.db4o.inside.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.YapWriter writer, bool isNew);

		public abstract com.db4o.TreeInt CollectFieldIDs(com.db4o.TreeInt tree, com.db4o.YapClass
			 yc, com.db4o.inside.marshall.ObjectHeaderAttributes attributes, com.db4o.YapWriter
			 reader, string name);

		protected virtual com.db4o.YapWriter CreateWriterForNew(com.db4o.Transaction trans
			, com.db4o.YapObject yo, int updateDepth, int length)
		{
			int id = yo.GetID();
			int address = -1;
			if (!trans.i_stream.IsClient())
			{
				address = trans.i_file.GetSlot(length);
			}
			trans.SetPointer(id, address, length);
			return CreateWriterForUpdate(trans, updateDepth, id, address, length);
		}

		protected virtual com.db4o.YapWriter CreateWriterForUpdate(com.db4o.Transaction a_trans
			, int updateDepth, int id, int address, int length)
		{
			com.db4o.YapWriter writer = new com.db4o.YapWriter(a_trans, length);
			writer.UseSlot(id, address, length);
			writer.SetUpdateDepth(updateDepth);
			return writer;
		}

		public abstract void DeleteMembers(com.db4o.YapClass yc, com.db4o.inside.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.YapWriter writer, int a_type, bool isUpdate);

		public abstract bool FindOffset(com.db4o.YapClass yc, com.db4o.inside.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.YapReader reader, com.db4o.YapField field);

		public abstract void InstantiateFields(com.db4o.YapClass yc, com.db4o.inside.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.YapObject yo, object obj, com.db4o.YapWriter reader);

		public abstract com.db4o.YapWriter MarshallNew(com.db4o.Transaction a_trans, com.db4o.YapObject
			 yo, int a_updateDepth);

		public abstract void MarshallUpdate(com.db4o.Transaction a_trans, int a_updateDepth
			, com.db4o.YapObject a_yapObject, object a_object);

		protected virtual void MarshallUpdateWrite(com.db4o.Transaction trans, com.db4o.YapObject
			 yo, object obj, com.db4o.YapWriter writer)
		{
			com.db4o.YapClass yc = yo.GetYapClass();
			com.db4o.YapStream stream = trans.i_stream;
			stream.WriteUpdate(yc, writer);
			if (yo.IsActive())
			{
				yo.SetStateClean();
			}
			yo.EndProcessing();
			yc.DispatchEvent(stream, obj, com.db4o.EventDispatcher.UPDATE);
		}

		public abstract object ReadIndexEntry(com.db4o.YapClass yc, com.db4o.inside.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.YapField yf, com.db4o.YapWriter reader);

		public abstract com.db4o.inside.marshall.ObjectHeaderAttributes ReadHeaderAttributes
			(com.db4o.YapReader reader);

		public abstract void ReadVirtualAttributes(com.db4o.Transaction trans, com.db4o.YapClass
			 yc, com.db4o.YapObject yo, com.db4o.inside.marshall.ObjectHeaderAttributes attributes
			, com.db4o.YapReader reader);
	}
}
