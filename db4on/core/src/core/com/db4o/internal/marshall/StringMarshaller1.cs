namespace com.db4o.@internal.marshall
{
	public class StringMarshaller1 : com.db4o.@internal.marshall.StringMarshaller
	{
		private const int DEFRAGMENT_INCREMENT_OFFSET = com.db4o.@internal.Const4.INT_LENGTH
			 * 2;

		public override bool InlinedStrings()
		{
			return true;
		}

		public override void CalculateLengths(com.db4o.@internal.Transaction trans, com.db4o.@internal.marshall.ObjectHeaderAttributes
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

		public override object WriteNew(object obj, bool topLevel, com.db4o.@internal.StatefulBuffer
			 writer, bool redirect)
		{
			com.db4o.@internal.ObjectContainerBase stream = writer.GetStream();
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
			com.db4o.@internal.StatefulBuffer bytes = new com.db4o.@internal.StatefulBuffer(writer
				.GetTransaction(), length);
			WriteShort(stream, str, bytes);
			writer.WritePayload(bytes, topLevel);
			return bytes;
		}

		public override com.db4o.@internal.Buffer ReadIndexEntry(com.db4o.@internal.StatefulBuffer
			 parentSlot)
		{
			int payLoadOffSet = parentSlot.ReadInt();
			int length = parentSlot.ReadInt();
			if (payLoadOffSet == 0)
			{
				return null;
			}
			return parentSlot.ReadPayloadWriter(payLoadOffSet, length);
		}

		public override com.db4o.@internal.Buffer ReadSlotFromParentSlot(com.db4o.@internal.ObjectContainerBase
			 stream, com.db4o.@internal.Buffer reader)
		{
			int payLoadOffSet = reader.ReadInt();
			int length = reader.ReadInt();
			if (payLoadOffSet == 0)
			{
				return null;
			}
			return reader.ReadPayloadReader(payLoadOffSet, length);
		}

		public override void Defrag(com.db4o.@internal.SlotReader reader)
		{
			reader.IncrementOffset(DEFRAGMENT_INCREMENT_OFFSET);
		}
	}
}
