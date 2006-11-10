using System;
using Db4objects.Db4o;
using Db4objects.Db4o.Messaging;

namespace Db4objects.Db4odoc.Messaging
{

	public class MessagingExample 
	{
		public readonly static string YapFileName = "formula1.yap";
	
		public static void ConfigureServer() 
		{
			IObjectServer objectServer = Db4oFactory.OpenServer(YapFileName, 0);
			objectServer.Ext().Configure().SetMessageRecipient(new SimpleMessageRecipient());
			try 
			{
				IObjectContainer clientObjectContainer = objectServer.OpenClient();
				// Here is what we would do on the client to send the message
				IMessageSender sender = clientObjectContainer.Ext().Configure()
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

	public class SimpleMessageRecipient: IMessageRecipient
	{
		public void ProcessMessage(IObjectContainer objectContainer,Object message) 
		{
			// message objects will arrive in this code block
			System.Console.WriteLine(message);
		}
	}
}
