namespace com.db4o.@internal.marshall
{
	public abstract class ObjectMarshaller
	{
		public com.db4o.@internal.marshall.MarshallerFamily _family;

		protected abstract class TraverseFieldCommand
		{
			private bool _cancelled = false;

			public virtual int FieldCount(com.db4o.@internal.ClassMetadata yapClass, com.db4o.@internal.Buffer
				 reader)
			{
				return (com.db4o.Debug.atHome ? yapClass.ReadFieldCountSodaAtHome(reader) : yapClass
					.ReadFieldCount(reader));
			}

			public virtual bool Cancelled()
			{
				return _cancelled;
			}

			protected virtual void Cancel()
			{
				_cancelled = true;
			}

			public abstract void ProcessField(com.db4o.@internal.FieldMetadata field, bool isNull
				, com.db4o.@internal.ClassMetadata containingClass);
		}

		protected virtual void TraverseFields(com.db4o.@internal.ClassMetadata yc, com.db4o.@internal.Buffer
			 reader, com.db4o.@internal.marshall.ObjectHeaderAttributes attributes, com.db4o.@internal.marshall.ObjectMarshaller.TraverseFieldCommand
			 command)
		{
			int fieldIndex = 0;
			while (yc != null && !command.Cancelled())
			{
				int fieldCount = command.FieldCount(yc, reader);
				for (int i = 0; i < fieldCount && !command.Cancelled(); i++)
				{
					command.ProcessField(yc.i_fields[i], IsNull(attributes, fieldIndex), yc);
					fieldIndex++;
				}
				yc = yc.i_ancestor;
			}
		}

		protected abstract bool IsNull(com.db4o.@internal.marshall.ObjectHeaderAttributes
			 attributes, int fieldIndex);

		public abstract void AddFieldIndices(com.db4o.@internal.ClassMetadata yc, com.db4o.@internal.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.@internal.StatefulBuffer writer, com.db4o.@internal.slots.Slot
			 oldSlot);

		public abstract com.db4o.@internal.TreeInt CollectFieldIDs(com.db4o.@internal.TreeInt
			 tree, com.db4o.@internal.ClassMetadata yc, com.db4o.@internal.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.@internal.StatefulBuffer reader, string name);

		protected virtual com.db4o.@internal.StatefulBuffer CreateWriterForNew(com.db4o.@internal.Transaction
			 trans, com.db4o.@internal.ObjectReference yo, int updateDepth, int length)
		{
			int id = yo.GetID();
			int address = -1;
			if (!trans.Stream().IsClient())
			{
				address = trans.i_file.GetSlot(length);
			}
			trans.SetPointer(id, address, length);
			return CreateWriterForUpdate(trans, updateDepth, id, address, length);
		}

		protected virtual com.db4o.@internal.StatefulBuffer CreateWriterForUpdate(com.db4o.@internal.Transaction
			 a_trans, int updateDepth, int id, int address, int length)
		{
			com.db4o.@internal.StatefulBuffer writer = new com.db4o.@internal.StatefulBuffer(
				a_trans, length);
			writer.UseSlot(id, address, length);
			writer.SetUpdateDepth(updateDepth);
			return writer;
		}

		public abstract void DeleteMembers(com.db4o.@internal.ClassMetadata yc, com.db4o.@internal.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.@internal.StatefulBuffer writer, int a_type, bool isUpdate
			);

		public abstract bool FindOffset(com.db4o.@internal.ClassMetadata yc, com.db4o.@internal.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.@internal.Buffer reader, com.db4o.@internal.FieldMetadata 
			field);

		public abstract void InstantiateFields(com.db4o.@internal.ClassMetadata yc, com.db4o.@internal.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.@internal.ObjectReference yo, object obj, com.db4o.@internal.StatefulBuffer
			 reader);

		public abstract com.db4o.@internal.StatefulBuffer MarshallNew(com.db4o.@internal.Transaction
			 a_trans, com.db4o.@internal.ObjectReference yo, int a_updateDepth);

		public abstract void MarshallUpdate(com.db4o.@internal.Transaction a_trans, int a_updateDepth
			, com.db4o.@internal.ObjectReference a_yapObject, object a_object);

		protected virtual void MarshallUpdateWrite(com.db4o.@internal.Transaction trans, 
			com.db4o.@internal.ObjectReference yo, object obj, com.db4o.@internal.StatefulBuffer
			 writer)
		{
			com.db4o.@internal.ClassMetadata yc = yo.GetYapClass();
			com.db4o.@internal.ObjectContainerBase stream = trans.Stream();
			stream.WriteUpdate(yc, writer);
			if (yo.IsActive())
			{
				yo.SetStateClean();
			}
			yo.EndProcessing();
			ObjectOnUpdate(yc, stream, obj);
		}

		private void ObjectOnUpdate(com.db4o.@internal.ClassMetadata yc, com.db4o.@internal.ObjectContainerBase
			 stream, object obj)
		{
			stream.Callbacks().ObjectOnUpdate(obj);
			yc.DispatchEvent(stream, obj, com.db4o.@internal.EventDispatcher.UPDATE);
		}

		public abstract object ReadIndexEntry(com.db4o.@internal.ClassMetadata yc, com.db4o.@internal.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.@internal.FieldMetadata yf, com.db4o.@internal.StatefulBuffer
			 reader);

		public abstract com.db4o.@internal.marshall.ObjectHeaderAttributes ReadHeaderAttributes
			(com.db4o.@internal.Buffer reader);

		public abstract void ReadVirtualAttributes(com.db4o.@internal.Transaction trans, 
			com.db4o.@internal.ClassMetadata yc, com.db4o.@internal.ObjectReference yo, com.db4o.@internal.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.@internal.Buffer reader);

		public abstract void DefragFields(com.db4o.@internal.ClassMetadata yapClass, com.db4o.@internal.marshall.ObjectHeader
			 header, com.db4o.@internal.ReaderPair readers);

		public abstract void WriteObjectClassID(com.db4o.@internal.Buffer reader, int id);

		public abstract void SkipMarshallerInfo(com.db4o.@internal.Buffer reader);
	}
}
