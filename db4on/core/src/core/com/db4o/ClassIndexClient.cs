namespace com.db4o
{
	/// <summary>client class index.</summary>
	/// <remarks>
	/// client class index. Largly intended to do nothing or
	/// redirect functionality to the server.
	/// </remarks>
	internal sealed class ClassIndexClient : com.db4o.ClassIndex
	{
		internal ClassIndexClient(com.db4o.YapClass aYapClass) : base(aYapClass)
		{
		}

		internal override void add(int a_id)
		{
			throw com.db4o.YapConst.virtualException();
		}

		internal override void ensureActive()
		{
		}

		internal override long[] getInternalIDs(com.db4o.Transaction trans, int yapClassID
			)
		{
			com.db4o.YapClient stream = (com.db4o.YapClient)trans.i_stream;
			stream.writeMsg(com.db4o.Msg.GET_INTERNAL_IDS.getWriterForInt(trans, yapClassID));
			com.db4o.YapWriter reader = stream.expectedByteResponse(com.db4o.Msg.ID_LIST);
			int size = reader.readInt();
			long[] ids = new long[size];
			for (int i = 0; i < size; i++)
			{
				ids[i] = reader.readInt();
			}
			return ids;
		}

		public override void read(com.db4o.Transaction a_trans)
		{
		}

		internal override void setDirty(com.db4o.YapStream a_stream)
		{
		}

		internal void write(com.db4o.YapStream a_stream)
		{
		}

		internal sealed override void writeOwnID(com.db4o.Transaction trans, com.db4o.YapReader
			 a_writer)
		{
			a_writer.writeInt(0);
		}
	}
}
