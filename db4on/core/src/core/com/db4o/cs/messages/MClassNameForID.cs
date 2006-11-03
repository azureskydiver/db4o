namespace com.db4o.cs.messages
{
	/// <summary>get the classname for an internal ID</summary>
	internal sealed class MClassNameForID : com.db4o.cs.messages.MsgD
	{
		public sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 sock)
		{
			int id = _payLoad.ReadInt();
			string name = string.Empty;
			com.db4o.YapStream stream = GetStream();
			lock (stream.i_lock)
			{
				try
				{
					com.db4o.YapClass yapClass = stream.GetYapClass(id);
					if (yapClass != null)
					{
						name = yapClass.GetName();
					}
				}
				catch
				{
				}
			}
			com.db4o.cs.messages.Msg.CLASS_NAME_FOR_ID.GetWriterForString(GetTransaction(), name
				).Write(stream, sock);
			return true;
		}
	}
}
