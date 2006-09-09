/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.convert;

import com.db4o.*;


public abstract class Conversion {
    
    protected YapFile _yapFile;
    
    public void setFile(YapFile yapFile){
        _yapFile = yapFile;
    }

    public void convertWhenSystemIsUp() {
        
    }
    
    public void convertWhenClassCollectionAvailable(){
        
    }
    
    
    
    

}
