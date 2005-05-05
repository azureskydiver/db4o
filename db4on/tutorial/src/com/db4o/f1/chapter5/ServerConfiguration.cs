namespace com.db4o.f1.chapter5 {

  /**
   * Configuration used for StartServer and StopServer.
   */
  public class ServerConfiguration {
    
    /**
     * the host to be used.
     * If you want to run the client server examples on two computers,
     * enter the computer name of the one that you want to use as server. 
     */
    public const string   HOST = "localhost";  
     
    /**
     * the database file to be used by the server.
     */
    public const string FILE = "formula1.yap";
    
    /**
     * the port to be used by the server.
     */
    public const int    PORT = 4488;
    
    /**
     * the user name for access control.
     */
    public const string   USER = "db4o";
    
    /**
     * the pasword for access control.
     */
    public const string   PASS = "db4o";
  
  }
}