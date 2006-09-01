namespace com.db4o.inside.marshall
{
	/// <exclude></exclude>
	public class UntypedMarshaller1 : com.db4o.inside.marshall.UntypedMarshaller
	{
		public override bool UseNormalClassRead()
		{
			return false;
		}

		public override void DeleteEmbedded(com.db4o.YapWriter reader)
		{
			int payLoadOffset = reader.ReadInt();
			if (payLoadOffset > 0)
			{
				int linkOffset = reader._offset;
				reader._offset = payLoadOffset;
				int yapClassID = reader.ReadInt();
				com.db4o.YapClass yc = reader.GetStream().GetYapClass(yapClassID);
				if (yc != null)
				{
					yc.DeleteEmbedded(_family, reader);
				}
				reader._offset = linkOffset;
			}
		}

		public override object Read(com.db4o.YapWriter reader)
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
			com.db4o.YapClass yc = reader.GetStream().GetYapClass(yapClassID);
			if (yc != null)
			{
				ret = yc.Read(_family, reader, true);
			}
			reader._offset = linkOffSet;
			return ret;
		}

		public override object ReadQuery(com.db4o.Transaction trans, com.db4o.YapReader reader
			, bool toArray)
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
			com.db4o.YapClass yc = trans.Stream().GetYapClass(yapClassID);
			if (yc != null)
			{
				ret = yc.ReadQuery(trans, _family, false, reader, toArray);
			}
			reader._offset = linkOffSet;
			return ret;
		}

		public override com.db4o.TypeHandler4 ReadArrayHandler(com.db4o.Transaction trans
			, com.db4o.YapReader[] reader)
		{
			int payLoadOffSet = reader[0].ReadInt();
			if (payLoadOffSet == 0)
			{
				return null;
			}
			com.db4o.TypeHandler4 ret = null;
			reader[0]._offset = payLoadOffSet;
			int yapClassID = reader[0].ReadInt();
			com.db4o.YapClass yc = trans.Stream().GetYapClass(yapClassID);
			if (yc != null)
			{
				ret = yc.ReadArrayHandler(trans, _family, reader);
			}
			return ret;
		}

		public override com.db4o.QCandidate ReadSubCandidate(com.db4o.YapReader reader, com.db4o.QCandidates
			 candidates, bool withIndirection)
		{
			int payLoadOffSet = reader.ReadInt();
			if (payLoadOffSet == 0)
			{
				return null;
			}
			com.db4o.QCandidate ret = null;
			int linkOffSet = reader._offset;
			reader._offset = payLoadOffSet;
			int yapClassID = reader.ReadInt();
			com.db4o.YapClass yc = candidates.i_trans.Stream().GetYapClass(yapClassID);
			if (yc != null)
			{
				ret = yc.ReadSubCandidate(_family, reader, candidates, false);
			}
			reader._offset = linkOffSet;
			return ret;
		}

		public override object WriteNew(object obj, bool restoreLinkOffset, com.db4o.YapWriter
			 writer)
		{
			if (obj == null)
			{
				writer.WriteInt(0);
				return 0;
			}
			com.db4o.YapClass yc = com.db4o.YapClass.ForObject(writer.GetTransaction(), obj, 
				false);
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
	}
}
