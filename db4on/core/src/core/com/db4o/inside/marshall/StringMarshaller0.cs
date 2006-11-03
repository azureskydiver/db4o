namespace com.db4o.inside.marshall
{
	public class StringMarshaller0 : com.db4o.inside.marshall.StringMarshaller
	{
		public override bool InlinedStrings()
		{
			return false;
		}

		public override void CalculateLengths(com.db4o.Transaction trans, com.db4o.inside.marshall.ObjectHeaderAttributes
			 header, bool topLevel, object obj, bool withIndirection)
		{
		}

		public override object WriteNew(object a_object, bool topLevel, com.db4o.YapWriter
			 a_bytes, bool redirect)
		{
			if (a_object == null)
			{
				a_bytes.WriteEmbeddedNull();
				return null;
			}
			com.db4o.YapStream stream = a_bytes.GetStream();
			string str = (string)a_object;
			int length = stream.StringIO().Length(str);
			com.db4o.YapWriter bytes = new com.db4o.YapWriter(a_bytes.GetTransaction(), length
				);
			WriteShort(stream, str, bytes);
			bytes.SetID(a_bytes._offset);
			a_bytes.GetStream().WriteEmbedded(a_bytes, bytes);
			a_bytes.IncrementOffset(com.db4o.YapConst.ID_LENGTH);
			a_bytes.WriteInt(length);
			return bytes;
		}

		public override com.db4o.YapReader ReadIndexEntry(com.db4o.YapWriter parentSlot)
		{
			return parentSlot.GetStream().ReadWriterByAddress(parentSlot.GetTransaction(), parentSlot
				.ReadInt(), parentSlot.ReadInt());
		}

		public override com.db4o.YapReader ReadSlotFromParentSlot(com.db4o.YapStream stream
			, com.db4o.YapReader reader)
		{
			return reader.ReadEmbeddedObject(stream.GetTransaction());
		}

		public override void Defrag(com.db4o.SlotReader reader)
		{
		}
	}
}
