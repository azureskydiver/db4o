/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */
package com.db4o.test;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;


public class DeleteDeep {
    
    public String name;
    
    public DeleteDeep child;
    
    public void storeOne(){
        addNodes(10);
        name = "root";
    }
    
    private void addNodes(int count){
        if(count > 0){
            child = new DeleteDeep();
            child.name = "" + count;
            child.addNodes(count -1);
        }
    }
    
    public void test(){
        ObjectContainer objectContainer = Test.objectContainer();
        Query q = objectContainer.query();
        q.constrain(DeleteDeep.class);
        q.descend("name").constrain("root");
        DeleteDeep root = (DeleteDeep)q.execute().next();
        objectContainer.activate(root, Integer.MAX_VALUE);
        
        deleteDeep(objectContainer, root);
        
        objectContainer.commit();
        Test.ensureOccurrences(DeleteDeep.class, 0);
    }
    
    private void deleteDeep(ObjectContainer objectContainer, Object obj){
        ObjectContainer allToDelete = ExtDb4o.openMemoryFile(null);
        allToDelete.set(obj);
        ObjectSet objectSet = allToDelete.get(null);
        while(objectSet.hasNext()){
            objectContainer.delete(objectSet.next());
        }
        allToDelete.close();
    }
    
}
