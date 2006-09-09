/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.performance;

import java.io.*;

import com.db4o.*;
import com.db4o.io.*;
// import com.sitraka.jprobe.api.*;
// import com.sitraka.jprobe.profiler.api.*;


public class SimpleInsertPerformance {
    
    private static boolean CLIENT_SERVER = false;
    
    private static int COUNT = 1000;
    
    private static int DEPTH = 3;
    
    private static final String FILE = "sip.yap";
    
    public static void main(String[] arguments) {
        
        // JPPerformanceAPI jpAPI = JPPerformanceAPI.getInstance();
        
        // jpAPI.pauseRecording();
        
        
        new File(FILE).delete();
        
        Db4o.configure().lockDatabaseFile(false);
        Db4o.configure().weakReferences(false);
        Db4o.configure().io(new MemoryIoAdapter());
        Db4o.configure().flushFileBuffers(false);
        
        Db4o.configure().singleThreadedClient(true);


        ObjectServer server = null;
        ObjectContainer oc = null;
        
        
        if(CLIENT_SERVER){
            server = Db4o.openServer(FILE, 0);
            oc = server.openClient();
        } else{
            oc = Db4o.openFile(FILE);
        }
         
        // jpAPI.resumeRecording();
        
        
        long start = System.currentTimeMillis();
        for (int i = 0; i < COUNT ;i++) {
            SIPLoad root = new SIPLoad("load", null);
            for (int j = 1; j < DEPTH; j++) {
                root = new SIPLoad("load", root);
            }
            oc.set(root);
        }
        oc.commit();
        
        // jpAPI.pauseRecording();

        long stop = System.currentTimeMillis();
        
        long duration = stop - start;
        
        int totalObjects = COUNT * DEPTH;
        
        System.out.println("Time to store " + totalObjects + " objects: " + duration + "ms");
        oc.close();
        
        if(CLIENT_SERVER){
            server.close();
        }

    }
    
    

}
