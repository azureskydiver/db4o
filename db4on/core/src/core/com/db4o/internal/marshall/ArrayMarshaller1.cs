namespace com.db4o.@internal.marshall
{
	internal class ArrayMarshaller1 : com.db4o.@internal.marshall.ArrayMarshaller
	{
		public override void CalculateLengths(com.db4o.@internal.Transaction trans, com.db4o.@internal.marshall.ObjectHeaderAttributes
			 header, com.db4o.@internal.handlers.ArrayHandler arrayHandler, object obj, bool
			 topLevel)
		{
			com.db4o.@internal.TypeHandler4 typeHandler = arrayHandler.i_handler;
			if (topLevel)
			{
				header.AddBaseLength(arrayHandler.LinkLength());
			}
			else
			{
				header.AddPayLoadLength(arrayHandler.LinkLength());
			}
			if (typeHandler.HasFixedLength())
			{
				header.AddPayLoadLength(arrayHandler.ObjectLength(obj));
			}
			else
			{
				header.AddPayLoadLength(arrayHandler.OwnLength(obj));
				object[] all = arrayHandler.AllElements(obj);
				for (int i = 0; i < all.Length; i++)
				{
					typeHandler.CalculateLengths(trans, header, false, all[i], true);
				}
			}
		}

		public override void DeleteEmbedded(com.db4o.@internal.handlers.ArrayHandler arrayHandler
			, com.db4o.@internal.StatefulBuffer reader)
		{
			int address = reader.ReadInt();
			reader.ReadInt();
			if (address <= 0)
			{
				return;
			}
			int linkOffSet = reader._offset;
			com.db4o.@internal.Transaction trans = reader.GetTransaction();
			com.db4o.@internal.TypeHandler4 typeHandler = arrayHandler.i_handler;
			if (reader.CascadeDeletes() > 0 && typeHandler is com.db4o.@internal.ClassMetadata
				)
			{
				reader._offset = address;
				reader.SetCascadeDeletes(reader.CascadeDeletes());
				for (int i = arrayHandler.ElementCount(trans, reader); i > 0; i--)
				{
					arrayHandler.i_handler.DeleteEmbedded(_family, reader);
				}
			}
			if (linkOffSet > 0)
			{
				reader._offset = linkOffSet;
			}
		}

		public override object Read(com.db4o.@internal.handlers.ArrayHandler arrayHandler
			, com.db4o.@internal.StatefulBuffer reader)
		{
			int linkOffSet = reader.PreparePayloadRead();
			object array = arrayHandler.Read1(_family, reader);
			reader._offset = linkOffSet;
			return array;
		}

		public override void ReadCandidates(com.db4o.@internal.handlers.ArrayHandler arrayHandler
			, com.db4o.@internal.Buffer reader, com.db4o.@internal.query.processor.QCandidates
			 candidates)
		{
			reader._offset = reader.ReadInt();
			arrayHandler.Read1Candidates(_family, reader, candidates);
		}

		public sealed override object ReadQuery(com.db4o.@internal.handlers.ArrayHandler 
			arrayHandler, com.db4o.@internal.Transaction trans, com.db4o.@internal.Buffer reader
			)
		{
			reader._offset = reader.ReadInt();
			return arrayHandler.Read1Query(trans, _family, reader);
		}

		public override object WriteNew(com.db4o.@internal.handlers.ArrayHandler arrayHandler
			, object obj, bool restoreLinkOffset, com.db4o.@internal.StatefulBuffer writer)
		{
			if (obj == null)
			{
				writer.WriteEmbeddedNull();
				return null;
			}
			int length = arrayHandler.ObjectLength(obj);
			int linkOffset = writer.ReserveAndPointToPayLoadSlot(length);
			arrayHandler.WriteNew1(obj, writer);
			if (restoreLinkOffset)
			{
				writer._offset = linkOffset;
			}
			return obj;
		}

		protected override com.db4o.@internal.Buffer PrepareIDReader(com.db4o.@internal.Transaction
			 trans, com.db4o.@internal.Buffer reader)
		{
			reader._offset = reader.ReadInt();
			return reader;
		}

		public override void DefragIDs(com.db4o.@internal.handlers.ArrayHandler arrayHandler
			, com.db4o.@internal.ReaderPair readers)
		{
			int offset = readers.PreparePayloadRead();
			arrayHandler.Defrag1(_family, readers);
			readers.Offset(offset);
		}
	}
}
