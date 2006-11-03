namespace com.db4o.inside.marshall
{
	public abstract class StringMarshaller
	{
		public abstract bool InlinedStrings();

		public abstract void CalculateLengths(com.db4o.Transaction trans, com.db4o.inside.marshall.ObjectHeaderAttributes
			 header, bool topLevel, object obj, bool withIndirection);

		protected int LinkLength()
		{
			return com.db4o.YapConst.INT_LENGTH + com.db4o.YapConst.ID_LENGTH;
		}

		public abstract object WriteNew(object a_object, bool topLevel, com.db4o.YapWriter
			 a_bytes, bool redirect);

		public string Read(com.db4o.YapStream stream, com.db4o.YapReader reader)
		{
			if (reader == null)
			{
				return null;
			}
			string ret = ReadShort(stream, reader);
			return ret;
		}

		public virtual string ReadFromOwnSlot(com.db4o.YapStream stream, com.db4o.YapReader
			 reader)
		{
			try
			{
				return Read(stream, reader);
			}
			catch (System.Exception e)
			{
				if (com.db4o.Deploy.debug || com.db4o.Debug.atHome)
				{
					j4o.lang.JavaSystem.PrintStackTrace(e);
				}
			}
			return string.Empty;
		}

		public virtual string ReadFromParentSlot(com.db4o.YapStream stream, com.db4o.YapReader
			 reader, bool redirect)
		{
			if (!redirect)
			{
				return Read(stream, reader);
			}
			return Read(stream, ReadSlotFromParentSlot(stream, reader));
		}

		public abstract com.db4o.YapReader ReadIndexEntry(com.db4o.YapWriter parentSlot);

		public static string ReadShort(com.db4o.YapStream stream, com.db4o.YapReader bytes
			)
		{
			return ReadShort(stream.StringIO(), stream.ConfigImpl().InternStrings(), bytes);
		}

		public static string ReadShort(com.db4o.YapStringIO io, bool internStrings, com.db4o.YapReader
			 bytes)
		{
			int length = bytes.ReadInt();
			if (length > com.db4o.YapConst.MAXIMUM_BLOCK_SIZE)
			{
				throw new com.db4o.CorruptionException();
			}
			if (length > 0)
			{
				string str = io.Read(bytes, length);
				return str;
			}
			return string.Empty;
		}

		public abstract com.db4o.YapReader ReadSlotFromParentSlot(com.db4o.YapStream stream
			, com.db4o.YapReader reader);

		public static com.db4o.YapReader WriteShort(com.db4o.YapStream stream, string str
			)
		{
			com.db4o.YapReader reader = new com.db4o.YapReader(stream.StringIO().Length(str));
			WriteShort(stream, str, reader);
			return reader;
		}

		public static void WriteShort(com.db4o.YapStream stream, string str, com.db4o.YapReader
			 reader)
		{
			int length = str.Length;
			reader.WriteInt(length);
			stream.StringIO().Write(reader, str);
		}

		public abstract void Defrag(com.db4o.SlotReader reader);
	}
}
