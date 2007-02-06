namespace com.db4o.@internal.marshall
{
	/// <exclude></exclude>
	public class UntypedMarshaller1 : com.db4o.@internal.marshall.UntypedMarshaller
	{
		public override bool UseNormalClassRead()
		{
			return false;
		}

		public override void DeleteEmbedded(com.db4o.@internal.StatefulBuffer reader)
		{
			int payLoadOffset = reader.ReadInt();
			if (payLoadOffset > 0)
			{
				int linkOffset = reader._offset;
				reader._offset = payLoadOffset;
				int yapClassID = reader.ReadInt();
				com.db4o.@internal.ClassMetadata yc = reader.GetStream().GetYapClass(yapClassID);
				if (yc != null)
				{
					yc.DeleteEmbedded(_family, reader);
				}
				reader._offset = linkOffset;
			}
		}

		public override object Read(com.db4o.@internal.StatefulBuffer reader)
		{
			object ret = null;
			int payLoadOffSet = reader.ReadInt();
			if (payLoadOffSet == 0)
			{
				return null;
			}
			int linkOffSet = reader._offset;
			reader._offset = payLoadOffSet;
			int yapClassID = reader.ReadInt();
			com.db4o.@internal.ClassMetadata yc = reader.GetStream().GetYapClass(yapClassID);
			if (yc != null)
			{
				ret = yc.Read(_family, reader, true);
			}
			reader._offset = linkOffSet;
			return ret;
		}

		public override object ReadQuery(com.db4o.@internal.Transaction trans, com.db4o.@internal.Buffer
			 reader, bool toArray)
		{
			object ret = null;
			int payLoadOffSet = reader.ReadInt();
			if (payLoadOffSet == 0)
			{
				return null;
			}
			int linkOffSet = reader._offset;
			reader._offset = payLoadOffSet;
			int yapClassID = reader.ReadInt();
			com.db4o.@internal.ClassMetadata yc = trans.Stream().GetYapClass(yapClassID);
			if (yc != null)
			{
				ret = yc.ReadQuery(trans, _family, false, reader, toArray);
			}
			reader._offset = linkOffSet;
			return ret;
		}

		public override com.db4o.@internal.TypeHandler4 ReadArrayHandler(com.db4o.@internal.Transaction
			 trans, com.db4o.@internal.Buffer[] reader)
		{
			int payLoadOffSet = reader[0].ReadInt();
			if (payLoadOffSet == 0)
			{
				return null;
			}
			com.db4o.@internal.TypeHandler4 ret = null;
			reader[0]._offset = payLoadOffSet;
			int yapClassID = reader[0].ReadInt();
			com.db4o.@internal.ClassMetadata yc = trans.Stream().GetYapClass(yapClassID);
			if (yc != null)
			{
				ret = yc.ReadArrayHandler(trans, _family, reader);
			}
			return ret;
		}

		public override com.db4o.@internal.query.processor.QCandidate ReadSubCandidate(com.db4o.@internal.Buffer
			 reader, com.db4o.@internal.query.processor.QCandidates candidates, bool withIndirection
			)
		{
			int payLoadOffSet = reader.ReadInt();
			if (payLoadOffSet == 0)
			{
				return null;
			}
			com.db4o.@internal.query.processor.QCandidate ret = null;
			int linkOffSet = reader._offset;
			reader._offset = payLoadOffSet;
			int yapClassID = reader.ReadInt();
			com.db4o.@internal.ClassMetadata yc = candidates.i_trans.Stream().GetYapClass(yapClassID
				);
			if (yc != null)
			{
				ret = yc.ReadSubCandidate(_family, reader, candidates, false);
			}
			reader._offset = linkOffSet;
			return ret;
		}

		public override object WriteNew(object obj, bool restoreLinkOffset, com.db4o.@internal.StatefulBuffer
			 writer)
		{
			if (obj == null)
			{
				writer.WriteInt(0);
				return 0;
			}
			com.db4o.@internal.ClassMetadata yc = com.db4o.@internal.ClassMetadata.ForObject(
				writer.GetTransaction(), obj, false);
			if (yc == null)
			{
				writer.WriteInt(0);
				return 0;
			}
			writer.WriteInt(writer._payloadOffset);
			int linkOffset = writer._offset;
			writer._offset = writer._payloadOffset;
			writer.WriteInt(yc.GetID());
			yc.WriteNew(_family, obj, false, writer, false, false);
			if (writer._payloadOffset < writer._offset)
			{
				writer._payloadOffset = writer._offset;
			}
			if (restoreLinkOffset)
			{
				writer._offset = linkOffset;
			}
			return obj;
		}

		public override void Defrag(com.db4o.@internal.ReaderPair readers)
		{
			int payLoadOffSet = readers.ReadInt();
			if (payLoadOffSet == 0)
			{
				return;
			}
			int linkOffSet = readers.Offset();
			readers.Offset(payLoadOffSet);
			int yapClassID = readers.CopyIDAndRetrieveMapping().Orig();
			com.db4o.@internal.ClassMetadata yc = readers.Context().YapClass(yapClassID);
			if (yc != null)
			{
				yc.Defrag(_family, readers, false);
			}
			readers.Offset(linkOffSet);
		}
	}
}
