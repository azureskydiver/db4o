using System;
using System.Threading;
using com.db4o;
using com.db4o.messaging;

namespace com.db4odoc.f1.clientserver
{
    /// <summary>
    /// starts a db4o server with the settings from ServerConfiguration. 
    /// This is a typical setup for a long running server.
    /// The Server may be stopped from a remote location by running
    /// StopServer. The StartServer instance is used as a MessageRecipient 
    /// and reacts to receiving an instance of a StopServer object.
    /// Note that all user classes need to be present on the server side
    /// and that all possible Db4o.Configure() calls to alter the db4o
    /// configuration need to be executed on the client and on the server.
    /// </summary>
    public class StartServer : ServerConfiguration, MessageRecipient
    {
        /// <summary>
        /// setting the value to true denotes that the server should be closed
        /// </summary>
        private bool stop = false;

        /// <summary>
        /// starts a db4o server using the configuration from
        /// ServerConfiguration.
        /// </summary>
        public static void Main(string[] arguments)
        {
            new StartServer().RunServer();
        } 
		// end Main

        /// <summary>
        /// opens the ObjectServer, and waits forever until Close() is called
        /// or a StopServer message is being received.
        /// </summary>
        public void RunServer()
        {
            lock(this)
            {
                ObjectServer db4oServer = Db4o.OpenServer(FILE, PORT);
                db4oServer.GrantAccess(USER, PASS);
                
                // Using the messaging functionality to redirect all
                // messages to this.processMessage
                db4oServer.Ext().Configure().SetMessageRecipient(this);
                try
                {
                    if (! stop)
                    {
                        // wait forever for Notify() from Close()
                        Monitor.Wait(this);   
                    }
                }
                catch (Exception e)
                {
                    Console.WriteLine(e.ToString());
                }
                db4oServer.Close();
            }
        }
		// end RunServer

        /// <summary>
        /// messaging callback
        /// see com.db4o.messaging.MessageRecipient#ProcessMessage()
        /// </summary>
        public void ProcessMessage(ObjectContainer con, object message)
        {
            if (message is StopServer)
            {
                Close();
            }
        }
		// end ProcessMessage

        /// <summary>
        /// closes this server.
        /// </summary>
        public void Close()
        {
            lock(this)
            {
                stop = true;
                Monitor.PulseAll(this);
            }
        }
		// end Close
    }
}