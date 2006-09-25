namespace com.db4o.inside.marshall
{
	internal class ArrayMarshaller0 : com.db4o.inside.marshall.ArrayMarshaller
	{
		public override com.db4o.TreeInt CollectIDs(com.db4o.YapArray arrayHandler, com.db4o.TreeInt
			 tree, com.db4o.YapWriter reader)
		{
			com.db4o.Transaction trans = reader.GetTransaction();
			return arrayHandler.CollectIDs1(trans, tree, reader.ReadEmbeddedObject(trans));
		}

		public override void DeleteEmbedded(com.db4o.YapArray arrayHandler, com.db4o.YapWriter
			 reader)
		{
			int address = reader.ReadInt();
			int length = reader.ReadInt();
			if (address <= 0)
			{
				return;
			}
			com.db4o.Transaction trans = reader.GetTransaction();
			if (reader.CascadeDeletes() > 0 && arrayHandler.i_handler is com.db4o.YapClass)
			{
				com.db4o.YapWriter bytes = reader.GetStream().ReadWriterByAddress(trans, address, 
					length);
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

		public override void CalculateLengths(com.db4o.Transaction trans, com.db4o.inside.marshall.ObjectHeaderAttributes
			 header, com.db4o.YapArray handler, object obj, bool topLevel)
		{
		}

		public override object WriteNew(com.db4o.YapArray arrayHandler, object a_object, 
			bool topLevel, com.db4o.YapWriter a_bytes)
		{
			if (a_object == null)
			{
				a_bytes.WriteEmbeddedNull();
				return null;
			}
			int length = arrayHandler.ObjectLength(a_object);
			com.db4o.YapWriter bytes = new com.db4o.YapWriter(a_bytes.GetTransaction(), length
				);
			bytes.SetUpdateDepth(a_bytes.GetUpdateDepth());
			arrayHandler.WriteNew1(a_object, bytes);
			bytes.SetID(a_bytes._offset);
			a_bytes.GetStream().WriteEmbedded(a_bytes, bytes);
			a_bytes.IncrementOffset(com.db4o.YapConst.ID_LENGTH);
			a_bytes.WriteInt(length);
			return a_object;
		}

		public override object Read(com.db4o.YapArray arrayHandler, com.db4o.YapWriter a_bytes
			)
		{
			com.db4o.YapWriter bytes = a_bytes.ReadEmbeddedObject();
			if (bytes == null)
			{
				return null;
			}
			return arrayHandler.Read1(_family, bytes);
		}

		public override void ReadCandidates(com.db4o.YapArray arrayHandler, com.db4o.YapReader
			 reader, com.db4o.QCandidates candidates)
		{
			com.db4o.YapReader bytes = reader.ReadEmbeddedObject(candidates.i_trans);
			if (bytes == null)
			{
				return;
			}
			int count = arrayHandler.ElementCount(candidates.i_trans, bytes);
			for (int i = 0; i < count; i++)
			{
				candidates.AddByIdentity(new com.db4o.QCandidate(candidates, null, bytes.ReadInt(
					), true));
			}
		}

		public sealed override object ReadQuery(com.db4o.YapArray arrayHandler, com.db4o.Transaction
			 trans, com.db4o.YapReader reader)
		{
			com.db4o.YapReader bytes = reader.ReadEmbeddedObject(trans);
			if (bytes == null)
			{
				return null;
			}
			object array = arrayHandler.Read1Query(trans, _family, bytes);
			return array;
		}
	}
}
