namespace com.db4o.cs.messages
{
	public class MObjectByUuid : com.db4o.cs.messages.MsgD
	{
		public sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			long uuid = ReadLong();
			byte[] signature = ReadBytes();
			int id = 0;
			com.db4o.YapStream stream = GetStream();
			com.db4o.Transaction trans = GetTransaction();
			lock (stream.i_lock)
			{
				try
				{
					object[] arr = trans.ObjectAndYapObjectBySignature(uuid, signature);
					if (arr[1] != null)
					{
						com.db4o.YapObject yo = (com.db4o.YapObject)arr[1];
						id = yo.GetID();
					}
				}
				catch (System.Exception e)
				{
				}
			}
			com.db4o.cs.messages.Msg.OBJECT_BY_UUID.GetWriterForInt(trans, id).Write(stream, 
				sock);
			return true;
		}
	}
}
