namespace com.db4o.@internal.cs.messages
{
	public class MWriteBatchedMessages : com.db4o.@internal.cs.messages.MsgD
	{
		public sealed override bool ProcessAtServer(com.db4o.@internal.cs.ServerMessageDispatcher
			 serverThread)
		{
			int count = ReadInt();
			com.db4o.@internal.Transaction ta = Transaction();
			for (int i = 0; i < count; i++)
			{
				com.db4o.@internal.StatefulBuffer writer = _payLoad.ReadYapBytes();
				int messageId = writer.ReadInt();
				com.db4o.@internal.cs.messages.Msg message = com.db4o.@internal.cs.messages.Msg.GetMessage
					(messageId);
				com.db4o.@internal.cs.messages.Msg clonedMessage = message.Clone(ta);
				if (clonedMessage is com.db4o.@internal.cs.messages.MsgD)
				{
					com.db4o.@internal.cs.messages.MsgD mso = (com.db4o.@internal.cs.messages.MsgD)clonedMessage;
					mso.PayLoad(writer);
					if (mso.PayLoad() != null)
					{
						mso.PayLoad().IncrementOffset(com.db4o.@internal.Const4.MESSAGE_LENGTH - com.db4o.@internal.Const4
							.INT_LENGTH);
						mso.PayLoad().SetTransaction(ta);
						mso.ProcessAtServer(serverThread);
					}
				}
				else
				{
					if (!clonedMessage.ProcessAtServer(serverThread))
					{
						serverThread.ProcessSpecialMsg(clonedMessage);
					}
				}
			}
			return true;
		}
	}
}
