namespace com.db4o
{
	internal sealed class MWriteUpdate : com.db4o.MsgObject
	{
		internal sealed override bool processMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			int yapClassId = _payLoad.readInt();
			com.db4o.YapFile stream = (com.db4o.YapFile)getStream();
			unmarshall(com.db4o.YapConst.YAPINT_LENGTH);
			lock (stream.i_lock)
			{
				com.db4o.YapClass yc = stream.getYapClass(yapClassId);
				_payLoad.writeEmbedded();
				yc.addFieldIndices(_payLoad, false);
				stream.writeUpdate(yc, _payLoad);
			}
			return true;
		}
	}
}
