/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;


public class BackupStressItem {
    
    public String _name;
    
    public int _iteration;
    
    
    public BackupStressItem() {
        
    }

    public BackupStressItem(String name, int iteration) {
        _name = name;
        _iteration = iteration;
    }
    

}
