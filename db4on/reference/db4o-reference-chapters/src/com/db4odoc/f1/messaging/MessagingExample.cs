using System;
using com.db4o;
using com.db4o.messaging;


namespace com.db4odoc.f1.messaging
{

	public class MessagingExample 
	{
		public readonly static string YapFileName = "formula1.yap";
	
		public static void ConfigureServer() 
		{
			ObjectServer objectServer = Db4o.OpenServer(YapFileName, 0);
			objectServer.Ext().Configure().SetMessageRecipient(new SimpleMessageRecipient());
			try 
			{
				ObjectContainer clientObjectContainer = objectServer.OpenClient();
				// Here is what we would do on the client to send the message
				MessageSender sender = clientObjectContainer.Ext().Configure()
					.GetMessageSender();

				sender.Send(new MyClientServerMessage("Hello from client."));
				clientObjectContainer.Close();
			} 
			finally 
			{
				objectServer.Close();
			}
		}
		// end ConfigureServer 
	}

	public class SimpleMessageRecipient: MessageRecipient
	{
		public void ProcessMessage(ObjectContainer objectContainer,Object message) 
		{
			// message objects will arrive in this code block
			System.Console.WriteLine(message);
		}
	}
}
