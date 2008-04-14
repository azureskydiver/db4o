/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4odoc.clientserver;

/**
 * Configuration used for {@link StartServer} and {@link StopServer}.
 * 
 * @sharpen.ignore
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
  public String   FILE = "reference.db4o";
  
  /**
   * the port to be used by the server.
   */
  public int    PORT = 0xdb40;
  
  /**
   * the user name for access control.
   */
  public String   USER = "db4o";
  
  /**
   * the pasword for access control.
   */
  public String   PASS = "db4o";
}
