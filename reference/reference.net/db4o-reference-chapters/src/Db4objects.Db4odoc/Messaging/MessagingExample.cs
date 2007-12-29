using System;
using Db4objects.Db4o;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Messaging;

namespace Db4objects.Db4odoc.Messaging
{

	public class MessagingExample 
	{
		private const string Db4oFileName = "reference.db4o";

        public static void Main(string[] Args)
        {
            ConfigureServer();
        }
        //end Main

		public static void ConfigureServer() 
		{
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.ClientServer().SetMessageRecipient(new SimpleMessageRecipient());
			IObjectServer objectServer = Db4oFactory.OpenServer(configuration, Db4oFileName, 0);
            try 
			{
                IConfiguration clientConfiguration = Db4oFactory.NewConfiguration();
                // Here is what we would do on the client to send the message
                IMessageSender sender = clientConfiguration.ClientServer().GetMessageSender();
				IObjectContainer clientObjectContainer = objectServer.OpenClient(clientConfiguration);
				
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
		public void ProcessMessage(IMessageContext context,Object message) 
		{
			// message objects will arrive in this code block
			System.Console.WriteLine(message);
		}
	}
}
