/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.util.*;

import com.db4o.types.*;

/**
 * 
 */
/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class DeleteRemovedMapElements {
    
    Map i_map;
    
    public void storeOne(){
        i_map = Test.objectContainer().collections().newHashMap(1);
        i_map.put(new DRME_Key(), new DRME_Value());
        i_map.put(new DRME_Key(), new DRME_Value());
    }
    
    public void testOne(){
        
        Test.ensureOccurrences(DRME_Key.class, 2);
        Test.ensureOccurrences(DRME_Value.class, 2);
        
        ((Db4oMap)i_map).deleteRemoved(true);
                
        i_map.clear();
        
        Test.ensureOccurrences(DRME_Key.class, 0);
        Test.ensureOccurrences(DRME_Value.class, 0);
        
    }
    
    public static class DRME_Key{
        
    }
    
    public static class DRME_Value{
        
        
    }
}
