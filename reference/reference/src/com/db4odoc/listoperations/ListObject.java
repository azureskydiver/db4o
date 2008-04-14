/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.listoperations;

import java.util.*;


public class ListObject {
    String _name;
    List<DataObject> _data;

    public ListObject() 
    {
        _data = new ArrayList<DataObject>(); 
    }
    
    public String getName() {
        return _name; 
    }
    
    public void setName(String name){
        _name = name;
    }

    public List<DataObject> getData() {
        return _data; 
    }
    
    public void setData(List<DataObject> data){
        _data = data;
    }
}
