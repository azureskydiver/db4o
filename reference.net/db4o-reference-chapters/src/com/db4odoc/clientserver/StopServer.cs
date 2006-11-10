using System;
using Db4objects.Db4o;
using Db4objects.Db4o.Messaging;

namespace Db4objects.Db4odoc.Clientserver
{
    /// <summary>
    /// stops the db4o Server started with StartServer.
    /// This is done by opening a client connection
    /// to the server and by sending a StopServer object as
    /// a message. StartServer will react in it's
    /// processMessage method.
    /// </summary>
    public class StopServer : ServerConfiguration
    {
        /// <summary>
        /// stops a db4o Server started with StartServer.
        /// </summary>
        /// <exception cref="Exception" />
        public static void Main(string[] args)
        {
            IObjectContainer objectContainer = null;
            try
            {
                // connect to the server
                objectContainer = Db4oFactory.OpenClient(HOST, PORT, USER, PASS);
            }
            catch (Exception e)
            {
                Console.WriteLine(e.ToString());
            }
            
            if (objectContainer != null)
            {
                // get the messageSender for the IObjectContainer 
                IMessageSender messageSender = objectContainer.Ext()
                    .Configure().GetMessageSender();

                // send an instance of a StopServer object
                messageSender.Send(new StopServer());
                
                // close the IObjectContainer 
                objectContainer.Close();
            }
        }
		// end Main
    }
}
