/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.drs;

public class SimpleArrayHolder {
    
    private String name;
    
    private SimpleArrayContent[] arr;
    
    public SimpleArrayHolder() {
        
    }
    
    public SimpleArrayHolder(String name) {
        this.name = name;
    }

    
    public SimpleArrayContent[] getArr() {
        return arr;
    }
    
    public void setArr(SimpleArrayContent[] arr) {
        this.arr = arr;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void add(SimpleArrayContent sac){
        if(arr == null){
            arr = new SimpleArrayContent[]{sac};
            return;
        }
        SimpleArrayContent[] temp = arr;
        arr = new SimpleArrayContent[temp.length + 1];
        System.arraycopy(temp, 0, arr, 0, temp.length);
        arr[temp.length] = sac;
    }
    
    

}
