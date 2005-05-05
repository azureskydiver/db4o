using System;
using System.Threading;
using com.db4o;
using com.db4o.messaging;
using j4o.lang;

namespace com.db4o.f1.chapter5 {

  /**
   * starts a db4o server with the settings from ServerConfiguration. 
   * This is a typical setup for a long running server.
   * The Server may be stopped from a remote location by running
   * StopServer. The StartServer instance is used as a MessageRecipient 
   * and reacts to receiving an instance of a StopServer object.
   * Note that all user classes need to be present on the server side
   * and that all possible Db4o.configure() calls to alter the db4o
   * configuration need to be executed on the client and on the server.
   */
   public class StartServer : ServerConfiguration, MessageRecipient {
    
    /**
     * setting the value to true denotes that the server should be closed
     */
    private bool stop = false;
    
    /**
     * starts a db4o server using the configuration from
     * ServerConfiguration.
     */
    public static void Main(string[] arguments) {
      new StartServer().runServer();
    } 
    
    /**
     * opens the ObjectServer, and waits forever until close() is called
     * or a StopServer message is being received.
     */
    public void runServer(){
      lock(this){
        ObjectServer db4oServer = Db4o.openServer(FILE, PORT);
        db4oServer.grantAccess(USER, PASS);
        
        // Using the messaging functionality to redirect all
        // messages to this.processMessage
        db4oServer.ext().configure().setMessageRecipient(this);
        
        try {
            if(! stop){
              // wait forever for notify() from close()
              Monitor.Wait(this);   
            }
        } catch (Exception e) {
          Console.WriteLine(e.ToString());
        }
        db4oServer.close();
      }
    }
    
    /**
     * messaging callback
     * see com.db4o.messaging.MessageRecipient#processMessage()
     */
    public void processMessage(ObjectContainer con, object message) {
      if(message is StopServer){
        close();
      }
    }
    
    /**
     * closes this server.
     */
    public void close(){
      lock(this){
        stop = true;
        Monitor.PulseAll(this);
      }
    }
  }
}