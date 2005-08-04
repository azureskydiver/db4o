namespace com.db4o
{
	internal sealed class MWriteNew : com.db4o.MsgObject
	{
		internal sealed override bool processMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			int yapClassId = payLoad.readInt();
			com.db4o.YapFile stream = (com.db4o.YapFile)getStream();
			unmarshall(com.db4o.YapConst.YAPINT_LENGTH);
			lock (stream.i_lock)
			{
				com.db4o.YapClass yc = stream.getYapClass(yapClassId);
				payLoad.writeEmbedded();
				stream.prefetchedIDConsumed(payLoad.getID());
				payLoad.address(stream.getSlot(payLoad.getLength()));
				yc.addFieldIndices(payLoad, true);
				stream.writeNew(yc, payLoad);
				getTransaction().writePointer(payLoad.getID(), payLoad.getAddress(), payLoad.getLength
					());
			}
			return true;
		}
	}
}
