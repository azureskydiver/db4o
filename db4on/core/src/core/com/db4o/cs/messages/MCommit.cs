namespace com.db4o.cs.messages
{
	internal sealed class MCommit : com.db4o.cs.messages.Msg
	{
		public sealed override bool ProcessMessageAtServer(com.db4o.foundation.network.YapSocket
			 @in)
		{
			GetTransaction().Commit();
			return true;
		}
	}
}
