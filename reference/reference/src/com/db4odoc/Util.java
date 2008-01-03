package com.db4odoc;

import com.db4o.*;
///

public class Util {
    private final static String DB4O_FILE_NAME="reference.db4o";

    public static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    
    public static void listResult(java.util.List result){
    	System.out.println(result.size());
    	for(int x = 0; x < result.size(); x++)
    		System.out.println(result.get(x));
    }
    
    public static void listRefreshedResult(ObjectContainer container,ObjectSet result,int depth) {
        System.out.println(result.size());
        while(result.hasNext()) {
            Object obj = result.next();
            container.ext().refresh(obj, depth);
            System.out.println(obj);
        }
    }
    
    public static void retrieveAll(ObjectContainer container){
        ObjectSet result=container.queryByExample(new Object());
        listResult(result);
    }
    
    public static void deleteAll(ObjectContainer container) {
        ObjectSet result=container.queryByExample(new Object());
        while(result.hasNext()) {
            container.delete(result.next());
        }
    }
}
