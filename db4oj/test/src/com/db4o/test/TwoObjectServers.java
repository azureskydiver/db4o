/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.*;
import com.db4o.tools.*;


public class TwoObjectServers extends AllTests {
    
    String name;
    
    public void storeOne(){
        name = "foo";
    }
    
    
    public void testOne(){
        
        if(Test.clientServer){
        
            ObjectServer server = Db4o.openServer(FILE_SERVER, 0);
            
            ObjectContainer oc = server.openClient();
            
            ObjectSet os = oc.get(null);
            while(os.hasNext()){
                Logger.log(os.next());
            }
            
            oc.close();
            
            server.close();
        }
        
    }
    
    

}
