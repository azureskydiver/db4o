package com.db4o.f1;

import com.db4o.*;


public class Util {
    public final static String YAPFILENAME="formula1.yap";

    public static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    
    public static void listRefreshedResult(ObjectContainer container,ObjectSet result,int depth) {
        System.out.println(result.size());
        while(result.hasNext()) {
            Object obj = result.next();
            container.ext().refresh(obj, depth);
            System.out.println(obj);
        }
    }
    
    public static void deleteAll(ObjectContainer db) {
        db.delete(Object.class);
    }
}
