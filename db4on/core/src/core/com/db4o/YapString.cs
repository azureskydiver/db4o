namespace com.db4o
{
	/// <summary>
	/// YapString
	/// Legacy rename for C# obfuscator production trouble
	/// </summary>
	/// <exclude></exclude>
	public sealed class YapString : com.db4o.YapIndependantType
	{
		public com.db4o.YapStringIO i_stringIo;

		public YapString(com.db4o.YapStream stream, com.db4o.YapStringIO stringIO) : base
			(stream)
		{
			i_stringIo = stringIO;
		}

		public override bool CanHold(com.db4o.reflect.ReflectClass claxx)
		{
			return claxx.Equals(ClassReflector());
		}

		public override void CascadeActivation(com.db4o.Transaction a_trans, object a_object
			, int a_depth, bool a_activate)
		{
		}

		public override com.db4o.reflect.ReflectClass ClassReflector()
		{
			return _stream.i_handlers.ICLASS_STRING;
		}

		public override object ComparableObject(com.db4o.Transaction a_trans, object a_object
			)
		{
			if (a_object == null)
			{
				return null;
			}
			if (a_object is com.db4o.YapReader)
			{
				return a_object;
			}
			com.db4o.inside.slots.Slot s = (com.db4o.inside.slots.Slot)a_object;
			return a_trans.Stream().ReadReaderByAddress(s._address, s._length);
		}

		public override void DeleteEmbedded(com.db4o.inside.marshall.MarshallerFamily mf, 
			com.db4o.YapWriter a_bytes)
		{
			int address = a_bytes.ReadInt();
			int length = a_bytes.ReadInt();
			if (address > 0 && !mf._string.InlinedStrings())
			{
				a_bytes.GetTransaction().SlotFreeOnCommit(address, address, length);
			}
		}

		public override bool Equals(com.db4o.TypeHandler4 a_dataType)
		{
			return (this == a_dataType);
		}

		public override int GetID()
		{
			return 9;
		}

		internal byte GetIdentifier()
		{
			return com.db4o.YapConst.YAPSTRING;
		}

		public override com.db4o.YapClass GetYapClass(com.db4o.YapStream a_stream)
		{
			return a_stream.i_handlers.i_yapClasses[GetID() - 1];
		}

		public override object IndexEntryToObject(com.db4o.Transaction trans, object indexEntry
			)
		{
			try
			{
				return com.db4o.inside.marshall.StringMarshaller.ReadShort(_stream, (com.db4o.YapReader
					)indexEntry);
			}
			catch (com.db4o.CorruptionException)
			{
			}
			return null;
		}

		public override bool IndexNullHandling()
		{
			return true;
		}

		public override int IsSecondClass()
		{
			return com.db4o.YapConst.YES;
		}

		public override void CalculateLengths(com.db4o.Transaction trans, com.db4o.inside.marshall.ObjectHeaderAttributes
			 header, bool topLevel, object obj, bool withIndirection)
		{
			com.db4o.inside.marshall.MarshallerFamily.Current()._string.CalculateLengths(trans
				, header, topLevel, obj, withIndirection);
		}

		public override object Read(com.db4o.inside.marshall.MarshallerFamily mf, com.db4o.YapWriter
			 a_bytes, bool redirect)
		{
			return mf._string.ReadFromParentSlot(a_bytes.GetStream(), a_bytes, redirect);
		}

		public override com.db4o.TypeHandler4 ReadArrayHandler(com.db4o.Transaction a_trans
			, com.db4o.inside.marshall.MarshallerFamily mf, com.db4o.YapReader[] a_bytes)
		{
			return null;
		}

		public override void ReadCandidates(com.db4o.inside.marshall.MarshallerFamily mf, 
			com.db4o.YapReader a_bytes, com.db4o.QCandidates a_candidates)
		{
		}

		public override com.db4o.QCandidate ReadSubCandidate(com.db4o.inside.marshall.MarshallerFamily
			 mf, com.db4o.YapReader reader, com.db4o.QCandidates candidates, bool withIndirection
			)
		{
			try
			{
				object obj = null;
				if (withIndirection)
				{
					obj = ReadQuery(candidates.i_trans, mf, withIndirection, reader, true);
				}
				else
				{
					obj = mf._string.Read(_stream, reader);
				}
				if (obj != null)
				{
					return new com.db4o.QCandidate(candidates, obj, 0, true);
				}
			}
			catch (com.db4o.CorruptionException)
			{
			}
			return null;
		}

		/// <summary>This readIndexEntry method reads from the parent slot.</summary>
		/// <remarks>
		/// This readIndexEntry method reads from the parent slot.
		/// TODO: Consider renaming methods in Indexable4 and Typhandler4 to make direction clear.
		/// </remarks>
		public override object ReadIndexEntry(com.db4o.inside.marshall.MarshallerFamily mf
			, com.db4o.YapWriter a_writer)
		{
			return mf._string.ReadIndexEntry(a_writer);
		}

		/// <summary>This readIndexEntry method reads from the actual index in the file.</summary>
		/// <remarks>
		/// This readIndexEntry method reads from the actual index in the file.
		/// TODO: Consider renaming methods in Indexable4 and Typhandler4 to make direction clear.
		/// </remarks>
		public override object ReadIndexEntry(com.db4o.YapReader reader)
		{
			com.db4o.inside.slots.Slot s = new com.db4o.inside.slots.Slot(reader.ReadInt(), reader
				.ReadInt());
			if (IsInvalidSlot(s))
			{
				return null;
			}
			return s;
		}

		private bool IsInvalidSlot(com.db4o.inside.slots.Slot slot)
		{
			return (slot._address == 0) && (slot._length == 0);
		}

		public override object ReadQuery(com.db4o.Transaction a_trans, com.db4o.inside.marshall.MarshallerFamily
			 mf, bool withRedirection, com.db4o.YapReader a_reader, bool a_toArray)
		{
			if (!withRedirection)
			{
				return mf._string.Read(a_trans.Stream(), a_reader);
			}
			com.db4o.YapReader reader = mf._string.ReadSlotFromParentSlot(a_trans.Stream(), a_reader
				);
			if (a_toArray)
			{
				if (reader != null)
				{
					return mf._string.ReadFromOwnSlot(a_trans.Stream(), reader);
				}
			}
			return reader;
		}

		internal void SetStringIo(com.db4o.YapStringIO a_io)
		{
			i_stringIo = a_io;
		}

		public override bool SupportsIndex()
		{
			return true;
		}

		public override void WriteIndexEntry(com.db4o.YapReader writer, object entry)
		{
			if (entry == null)
			{
				writer.WriteInt(0);
				writer.WriteInt(0);
				return;
			}
			if (entry is com.db4o.YapWriter)
			{
				com.db4o.YapWriter entryAsWriter = (com.db4o.YapWriter)entry;
				writer.WriteInt(entryAsWriter.GetAddress());
				writer.WriteInt(entryAsWriter.GetLength());
				return;
			}
			if (entry is com.db4o.inside.slots.Slot)
			{
				com.db4o.inside.slots.Slot s = (com.db4o.inside.slots.Slot)entry;
				writer.WriteInt(s._address);
				writer.WriteInt(s._length);
				return;
			}
			throw new System.ArgumentException();
		}

		public override object WriteNew(com.db4o.inside.marshall.MarshallerFamily mf, object
			 a_object, bool topLevel, com.db4o.YapWriter a_bytes, bool withIndirection, bool
			 restoreLinkeOffset)
		{
			return mf._string.WriteNew(a_object, topLevel, a_bytes, withIndirection);
		}

		internal void WriteShort(string a_string, com.db4o.YapReader a_bytes)
		{
			if (a_string == null)
			{
				a_bytes.WriteInt(0);
			}
			else
			{
				a_bytes.WriteInt(a_string.Length);
				i_stringIo.Write(a_bytes, a_string);
			}
		}

		public override int GetTypeID()
		{
			return com.db4o.YapConst.TYPE_SIMPLE;
		}

		private com.db4o.YapReader i_compareTo;

		private com.db4o.YapReader Val(object obj)
		{
			if (obj is com.db4o.YapReader)
			{
				return (com.db4o.YapReader)obj;
			}
			if (obj is string)
			{
				return com.db4o.inside.marshall.StringMarshaller.WriteShort(_stream, (string)obj);
			}
			if (obj is com.db4o.inside.slots.Slot)
			{
				com.db4o.inside.slots.Slot s = (com.db4o.inside.slots.Slot)obj;
				return _stream.ReadReaderByAddress(s._address, s._length);
			}
			return null;
		}

		public override void PrepareComparison(com.db4o.Transaction a_trans, object obj)
		{
			i_compareTo = (com.db4o.YapReader)obj;
		}

		public override com.db4o.YapComparable PrepareComparison(object obj)
		{
			if (obj == null)
			{
				i_compareTo = null;
				return com.db4o.Null.INSTANCE;
			}
			i_compareTo = Val(obj);
			return this;
		}

		public override object Current()
		{
			return i_compareTo;
		}

		public override int CompareTo(object obj)
		{
			if (i_compareTo == null)
			{
				if (obj == null)
				{
					return 0;
				}
				return 1;
			}
			return Compare(i_compareTo, Val(obj));
		}

		public override bool IsEqual(object obj)
		{
			if (i_compareTo == null)
			{
				return obj == null;
			}
			return i_compareTo.ContainsTheSame(Val(obj));
		}

		public override bool IsGreater(object obj)
		{
			if (i_compareTo == null)
			{
				return obj != null;
			}
			return Compare(i_compareTo, Val(obj)) > 0;
		}

		public override bool IsSmaller(object obj)
		{
			if (i_compareTo == null)
			{
				return false;
			}
			return Compare(i_compareTo, Val(obj)) < 0;
		}

		/// <summary>
		/// returns: -x for left is greater and +x for right is greater
		/// TODO: You will need collators here for different languages.
		/// </summary>
		/// <remarks>
		/// returns: -x for left is greater and +x for right is greater
		/// TODO: You will need collators here for different languages.
		/// </remarks>
		internal int Compare(com.db4o.YapReader a_compare, com.db4o.YapReader a_with)
		{
			if (a_compare == null)
			{
				if (a_with == null)
				{
					return 0;
				}
				return 1;
			}
			if (a_with == null)
			{
				return -1;
			}
			return Compare(a_compare._buffer, a_with._buffer);
		}

		internal static int Compare(byte[] compare, byte[] with)
		{
			int min = compare.Length < with.Length ? compare.Length : with.Length;
			int start = com.db4o.YapConst.INT_LENGTH;
			for (int i = start; i < min; i++)
			{
				if (compare[i] != with[i])
				{
					return with[i] - compare[i];
				}
			}
			return with.Length - compare.Length;
		}

		public override void DefragIndexEntry(com.db4o.ReaderPair readers)
		{
			readers.CopyID(false, true);
			readers.IncrementIntSize();
		}

		public override void Defrag(com.db4o.inside.marshall.MarshallerFamily mf, com.db4o.ReaderPair
			 readers, bool redirect)
		{
			if (!redirect)
			{
				readers.IncrementOffset(LinkLength());
			}
			else
			{
				mf._string.Defrag(readers);
			}
		}
	}
}
