package com.db4o.f1.chapter5;

/**
 * Configuration used for {@link StartServer} and {@link StopServer}.
 */
public interface ServerConfiguration {
  
  /**
   * the host to be used.
   * <br>If you want to run the client server examples on two computers,
   * enter the computer name of the one that you want to use as server. 
   */
  public String   HOST = "localhost";  
   
  /**
   * the database file to be used by the server.
   */
  public String   FILE = "formula1.yap";
  
  /**
   * the port to be used by the server.
   */
  public int    PORT = 4488;
  
  /**
   * the user name for access control.
   */
  public String   USER = "db4o";
  
  /**
   * the pasword for access control.
   */
  public String   PASS = "db4o";
}
