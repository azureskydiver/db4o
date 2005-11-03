/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.ix;

import com.db4o.*;


public class NIxPaths {
    
    Tree _paths;
    
    void add(NIxPath path){
        _paths = Tree.add(_paths, path);
    }
    
    
    
    
    
    
    

}
