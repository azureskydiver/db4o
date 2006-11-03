namespace com.db4o.inside.marshall
{
	public class StringMarshaller1 : com.db4o.inside.marshall.StringMarshaller
	{
		public override bool InlinedStrings()
		{
			return true;
		}

		public override void CalculateLengths(com.db4o.Transaction trans, com.db4o.inside.marshall.ObjectHeaderAttributes
			 header, bool topLevel, object obj, bool withIndirection)
		{
			if (topLevel)
			{
				header.AddBaseLength(LinkLength());
				header.PrepareIndexedPayLoadEntry(trans);
			}
			else
			{
				if (withIndirection)
				{
					header.AddPayLoadLength(LinkLength());
				}
			}
			if (obj == null)
			{
				return;
			}
			header.AddPayLoadLength(trans.Stream().StringIO().Length((string)obj));
		}

		public override object WriteNew(object obj, bool topLevel, com.db4o.YapWriter writer
			, bool redirect)
		{
			com.db4o.YapStream stream = writer.GetStream();
			string str = (string)obj;
			if (!redirect)
			{
				if (str != null)
				{
					WriteShort(stream, str, writer);
				}
				return str;
			}
			if (str == null)
			{
				writer.WriteEmbeddedNull();
				return null;
			}
			int length = stream.StringIO().Length(str);
			com.db4o.YapWriter bytes = new com.db4o.YapWriter(writer.GetTransaction(), length
				);
			WriteShort(stream, str, bytes);
			writer.WritePayload(bytes, topLevel);
			return bytes;
		}

		public override com.db4o.YapReader ReadIndexEntry(com.db4o.YapWriter parentSlot)
		{
			int payLoadOffSet = parentSlot.ReadInt();
			int length = parentSlot.ReadInt();
			if (payLoadOffSet == 0)
			{
				return null;
			}
			return parentSlot.ReadPayloadWriter(payLoadOffSet, length);
		}

		public override com.db4o.YapReader ReadSlotFromParentSlot(com.db4o.YapStream stream
			, com.db4o.YapReader reader)
		{
			int payLoadOffSet = reader.ReadInt();
			int length = reader.ReadInt();
			if (payLoadOffSet == 0)
			{
				return null;
			}
			return reader.ReadPayloadReader(payLoadOffSet, length);
		}

		public override void Defrag(com.db4o.SlotReader reader)
		{
			reader.IncrementIntSize();
			reader.IncrementIntSize();
		}
	}
}
