namespace com.db4o.cs.messages
{
	public sealed class MCommitOK : com.db4o.cs.messages.Msg
	{
		public sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 @in)
		{
			GetTransaction().Commit();
			com.db4o.cs.messages.Msg.OK.Write(GetStream(), @in);
			return true;
		}
	}
}
