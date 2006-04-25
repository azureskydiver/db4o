namespace com.db4o
{
	internal sealed class MWriteNew : com.db4o.MsgObject
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
				stream.prefetchedIDConsumed(_payLoad.getID());
				_payLoad.address(stream.getSlot(_payLoad.getLength()));
				yc.addFieldIndices(_payLoad, true);
				stream.writeNew(yc, _payLoad);
				getTransaction().writePointer(_payLoad.getID(), _payLoad.getAddress(), _payLoad.getLength
					());
			}
			return true;
		}
	}
}
