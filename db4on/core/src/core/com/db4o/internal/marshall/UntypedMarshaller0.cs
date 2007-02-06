namespace com.db4o.@internal.marshall
{
	/// <exclude></exclude>
	public class UntypedMarshaller0 : com.db4o.@internal.marshall.UntypedMarshaller
	{
		public override void DeleteEmbedded(com.db4o.@internal.StatefulBuffer parentBytes
			)
		{
			int objectID = parentBytes.ReadInt();
			if (objectID > 0)
			{
				com.db4o.@internal.StatefulBuffer reader = parentBytes.GetStream().ReadWriterByID
					(parentBytes.GetTransaction(), objectID);
				if (reader != null)
				{
					reader.SetCascadeDeletes(parentBytes.CascadeDeletes());
					com.db4o.@internal.marshall.ObjectHeader oh = new com.db4o.@internal.marshall.ObjectHeader
						(reader);
					if (oh.YapClass() != null)
					{
						oh.YapClass().DeleteEmbedded1(_family, reader, objectID);
					}
				}
			}
		}

		public override bool UseNormalClassRead()
		{
			return true;
		}

		public override object Read(com.db4o.@internal.StatefulBuffer reader)
		{
			throw com.db4o.@internal.Exceptions4.ShouldNeverBeCalled();
		}

		public override object ReadQuery(com.db4o.@internal.Transaction trans, com.db4o.@internal.Buffer
			 reader, bool toArray)
		{
			throw com.db4o.@internal.Exceptions4.ShouldNeverBeCalled();
		}

		public override com.db4o.@internal.TypeHandler4 ReadArrayHandler(com.db4o.@internal.Transaction
			 a_trans, com.db4o.@internal.Buffer[] a_bytes)
		{
			int id = 0;
			int offset = a_bytes[0]._offset;
			try
			{
				id = a_bytes[0].ReadInt();
			}
			catch
			{
			}
			a_bytes[0]._offset = offset;
			if (id != 0)
			{
				com.db4o.@internal.StatefulBuffer reader = a_trans.Stream().ReadWriterByID(a_trans
					, id);
				if (reader != null)
				{
					com.db4o.@internal.marshall.ObjectHeader oh = new com.db4o.@internal.marshall.ObjectHeader
						(reader);
					try
					{
						if (oh.YapClass() != null)
						{
							a_bytes[0] = reader;
							return oh.YapClass().ReadArrayHandler1(a_bytes);
						}
					}
					catch (System.Exception e)
					{
					}
				}
			}
			return null;
		}

		public override com.db4o.@internal.query.processor.QCandidate ReadSubCandidate(com.db4o.@internal.Buffer
			 reader, com.db4o.@internal.query.processor.QCandidates candidates, bool withIndirection
			)
		{
			return null;
		}

		public override object WriteNew(object a_object, bool restoreLinkOffset, com.db4o.@internal.StatefulBuffer
			 a_bytes)
		{
			if (a_object == null)
			{
				a_bytes.WriteInt(0);
				return 0;
			}
			int id = a_bytes.GetStream().SetInternal(a_bytes.GetTransaction(), a_object, a_bytes
				.GetUpdateDepth(), true);
			a_bytes.WriteInt(id);
			return id;
		}

		public override void Defrag(com.db4o.@internal.ReaderPair readers)
		{
		}
	}
}
