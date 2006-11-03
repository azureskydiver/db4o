namespace com.db4o.cs.messages
{
	public class MTaDontDelete : com.db4o.cs.messages.MsgD
	{
		public sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 @in)
		{
			int classID = _payLoad.ReadInt();
			int id = _payLoad.ReadInt();
			com.db4o.Transaction trans = GetTransaction();
			com.db4o.YapStream stream = trans.Stream();
			lock (stream.i_lock)
			{
				trans.DontDelete(classID, id);
				return true;
			}
		}
	}
}
