namespace com.db4o.inside.marshall
{
	/// <exclude></exclude>
	public class UntypedMarshaller0 : com.db4o.inside.marshall.UntypedMarshaller
	{
		public override void DeleteEmbedded(com.db4o.YapWriter parentBytes)
		{
			int objectID = parentBytes.ReadInt();
			if (objectID > 0)
			{
				com.db4o.YapWriter reader = parentBytes.GetStream().ReadWriterByID(parentBytes.GetTransaction
					(), objectID);
				if (reader != null)
				{
					reader.SetCascadeDeletes(parentBytes.CascadeDeletes());
					com.db4o.inside.marshall.ObjectHeader oh = new com.db4o.inside.marshall.ObjectHeader
						(reader);
					if (oh._yapClass != null)
					{
						oh._yapClass.DeleteEmbedded1(_family, reader, objectID);
					}
				}
			}
		}

		public override bool UseNormalClassRead()
		{
			return true;
		}

		public override object Read(com.db4o.YapWriter reader)
		{
			throw com.db4o.inside.Exceptions4.ShouldNeverBeCalled();
		}

		public override object ReadQuery(com.db4o.Transaction trans, com.db4o.YapReader reader
			, bool toArray)
		{
			throw com.db4o.inside.Exceptions4.ShouldNeverBeCalled();
		}

		public override com.db4o.TypeHandler4 ReadArrayHandler(com.db4o.Transaction a_trans
			, com.db4o.YapReader[] a_bytes)
		{
			int id = 0;
			int offset = a_bytes[0]._offset;
			try
			{
				id = a_bytes[0].ReadInt();
			}
			catch (System.Exception e)
			{
			}
			a_bytes[0]._offset = offset;
			if (id != 0)
			{
				com.db4o.YapWriter reader = a_trans.Stream().ReadWriterByID(a_trans, id);
				if (reader != null)
				{
					com.db4o.inside.marshall.ObjectHeader oh = new com.db4o.inside.marshall.ObjectHeader
						(reader);
					try
					{
						if (oh._yapClass != null)
						{
							a_bytes[0] = reader;
							return oh._yapClass.ReadArrayHandler1(a_bytes);
						}
					}
					catch (System.Exception e)
					{
					}
				}
			}
			return null;
		}

		public override com.db4o.QCandidate ReadSubCandidate(com.db4o.YapReader reader, com.db4o.QCandidates
			 candidates, bool withIndirection)
		{
			return null;
		}

		public override object WriteNew(object a_object, bool restoreLinkOffset, com.db4o.YapWriter
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
	}
}
