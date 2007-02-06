namespace com.db4o.@internal.cs.messages
{
	/// <summary>get the classname for an internal ID</summary>
	internal sealed class MClassNameForID : com.db4o.@internal.cs.messages.MsgD
	{
		public sealed override bool ProcessAtServer(com.db4o.@internal.cs.ServerMessageDispatcher
			 serverThread)
		{
			int id = _payLoad.ReadInt();
			string name = string.Empty;
			lock (StreamLock())
			{
				try
				{
					com.db4o.@internal.ClassMetadata yapClass = Stream().GetYapClass(id);
					if (yapClass != null)
					{
						name = yapClass.GetName();
					}
				}
				catch
				{
				}
			}
			serverThread.Write(com.db4o.@internal.cs.messages.Msg.CLASS_NAME_FOR_ID.GetWriterForString
				(Transaction(), name));
			return true;
		}
	}
}
