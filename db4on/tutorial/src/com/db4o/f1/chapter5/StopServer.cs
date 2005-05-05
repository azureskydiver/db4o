using System;
using com.db4o;
using com.db4o.messaging;

namespace com.db4o.f1.chapter5 {
  /**
   * stops the db4o Server started with StartServer.
   * This is done by opening a client connection
   * to the server and by sending a StopServer object as
   * a message. StartServer will react in it's
   * processMessage method.
   */
   public class StopServer : ServerConfiguration {
  
    /**
     * stops a db4o Server started with StartServer.
     * @throws Exception
     */
    public static void Main(string[] args){
      
      ObjectContainer objectContainer = null;
      try {
        
        // connect to the server
        objectContainer = Db4o.openClient(HOST, PORT, USER, PASS);
        
      } catch (Exception e) {
        Console.WriteLine(e.ToString());
      }
      
      if(objectContainer != null){
      
        // get the messageSender for the ObjectContainer 
        MessageSender messageSender = objectContainer.ext()
            .configure().getMessageSender();
        
        // send an instance of a StopServer object
        messageSender.send(new StopServer());
        
        // close the ObjectContainer 
        objectContainer.close();
      }
    }
  }
}
