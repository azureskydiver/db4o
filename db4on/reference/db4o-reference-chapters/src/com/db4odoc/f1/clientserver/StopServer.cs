using System;
using com.db4o;
using com.db4o.messaging;

namespace com.db4odoc.f1.clientserver
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
            ObjectContainer objectContainer = null;
            try
            {
                // connect to the server
                objectContainer = Db4o.OpenClient(HOST, PORT, USER, PASS);
            }
            catch (Exception e)
            {
                Console.WriteLine(e.ToString());
            }
            
            if (objectContainer != null)
            {
                // get the messageSender for the ObjectContainer 
                MessageSender messageSender = objectContainer.Ext()
                    .Configure().GetMessageSender();

                // send an instance of a StopServer object
                messageSender.Send(new StopServer());
                
                // close the ObjectContainer 
                objectContainer.Close();
            }
        }
    }
}
