package com.db4odoc.crossplatform;

import com.db4o.ObjectServer;
import com.db4o.config.DotnetSupport;
import com.db4o.cs.Db4oClientServer;
import com.db4o.cs.config.ServerConfiguration;

import java.io.File;

/**
 * A Java server which also accepts .NET db4o clients
 */
public class JavaServer {
    private static final String DATABASE_FILE = "database.db4o";

    public static void main(String[] args) throws Exception {
        cleanUp();

        ServerConfiguration configuration = Db4oClientServer.newServerConfiguration();

        // #example: Add dot NET support.
        // Add .NET support. In client-server mode set the CS-support flag to true
        // In embedded-mode to false
        configuration.common().add(new DotnetSupport(true));
        // #end example
        
        ObjectServer server = Db4oClientServer.openServer(configuration, DATABASE_FILE,1337);
        server.grantAccess("sa","sa");

        System.out.println("Server is running. Press any key to close it...");
        while(System.in.available()==0){
            Thread.sleep(1000);
        }
        System.out.println("Closing..");
        server.close();

    }

    private static void cleanUp()
    {
        new File(DATABASE_FILE).delete();
    }
}
