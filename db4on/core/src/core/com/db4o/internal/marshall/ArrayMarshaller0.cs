namespace com.db4o.@internal.marshall
{
	internal class ArrayMarshaller0 : com.db4o.@internal.marshall.ArrayMarshaller
	{
		public override void DeleteEmbedded(com.db4o.@internal.handlers.ArrayHandler arrayHandler
			, com.db4o.@internal.StatefulBuffer reader)
		{
			int address = reader.ReadInt();
			int length = reader.ReadInt();
			if (address <= 0)
			{
				return;
			}
			com.db4o.@internal.Transaction trans = reader.GetTransaction();
			if (reader.CascadeDeletes() > 0 && arrayHandler.i_handler is com.db4o.@internal.ClassMetadata
				)
			{
				com.db4o.@internal.StatefulBuffer bytes = reader.GetStream().ReadWriterByAddress(
					trans, address, length);
				if (bytes != null)
				{
					bytes.SetCascadeDeletes(reader.CascadeDeletes());
					for (int i = arrayHandler.ElementCount(trans, bytes); i > 0; i--)
					{
						arrayHandler.i_handler.DeleteEmbedded(_family, bytes);
					}
				}
			}
			trans.SlotFreeOnCommit(address, address, length);
		}

		public override void CalculateLengths(com.db4o.@internal.Transaction trans, com.db4o.@internal.marshall.ObjectHeaderAttributes
			 header, com.db4o.@internal.handlers.ArrayHandler handler, object obj, bool topLevel
			)
		{
		}

		public override object WriteNew(com.db4o.@internal.handlers.ArrayHandler arrayHandler
			, object a_object, bool topLevel, com.db4o.@internal.StatefulBuffer a_bytes)
		{
			if (a_object == null)
			{
				a_bytes.WriteEmbeddedNull();
				return null;
			}
			int length = arrayHandler.ObjectLength(a_object);
			com.db4o.@internal.StatefulBuffer bytes = new com.db4o.@internal.StatefulBuffer(a_bytes
				.GetTransaction(), length);
			bytes.SetUpdateDepth(a_bytes.GetUpdateDepth());
			arrayHandler.WriteNew1(a_object, bytes);
			bytes.SetID(a_bytes._offset);
			a_bytes.GetStream().WriteEmbedded(a_bytes, bytes);
			a_bytes.IncrementOffset(com.db4o.@internal.Const4.ID_LENGTH);
			a_bytes.WriteInt(length);
			return a_object;
		}

		public override object Read(com.db4o.@internal.handlers.ArrayHandler arrayHandler
			, com.db4o.@internal.StatefulBuffer a_bytes)
		{
			com.db4o.@internal.StatefulBuffer bytes = a_bytes.ReadEmbeddedObject();
			if (bytes == null)
			{
				return null;
			}
			return arrayHandler.Read1(_family, bytes);
		}

		public override void ReadCandidates(com.db4o.@internal.handlers.ArrayHandler arrayHandler
			, com.db4o.@internal.Buffer reader, com.db4o.@internal.query.processor.QCandidates
			 candidates)
		{
			com.db4o.@internal.Buffer bytes = reader.ReadEmbeddedObject(candidates.i_trans);
			if (bytes == null)
			{
				return;
			}
			int count = arrayHandler.ElementCount(candidates.i_trans, bytes);
			for (int i = 0; i < count; i++)
			{
				candidates.AddByIdentity(new com.db4o.@internal.query.processor.QCandidate(candidates
					, null, bytes.ReadInt(), true));
			}
		}

		public sealed override object ReadQuery(com.db4o.@internal.handlers.ArrayHandler 
			arrayHandler, com.db4o.@internal.Transaction trans, com.db4o.@internal.Buffer reader
			)
		{
			com.db4o.@internal.Buffer bytes = reader.ReadEmbeddedObject(trans);
			if (bytes == null)
			{
				return null;
			}
			object array = arrayHandler.Read1Query(trans, _family, bytes);
			return array;
		}

		protected override com.db4o.@internal.Buffer PrepareIDReader(com.db4o.@internal.Transaction
			 trans, com.db4o.@internal.Buffer reader)
		{
			return reader.ReadEmbeddedObject(trans);
		}

		public override void DefragIDs(com.db4o.@internal.handlers.ArrayHandler arrayHandler
			, com.db4o.@internal.ReaderPair readers)
		{
		}
	}
}
