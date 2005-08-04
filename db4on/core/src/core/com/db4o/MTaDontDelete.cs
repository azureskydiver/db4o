namespace com.db4o
{
	internal class MTaDontDelete : com.db4o.MsgD
	{
		internal sealed override bool processMessageAtServer(com.db4o.foundation.network.YapSocket
			 _in)
		{
			int classID = payLoad.readInt();
			int id = payLoad.readInt();
			com.db4o.Transaction trans = getTransaction();
			com.db4o.YapStream stream = trans.i_stream;
			lock (stream.i_lock)
			{
				trans.dontDelete(classID, id);
				return true;
			}
		}
	}
}
