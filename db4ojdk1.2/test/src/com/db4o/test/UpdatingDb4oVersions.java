/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.io.*;
import java.util.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;

/**
 * 
 */
public class UpdatingDb4oVersions {
    
    static final String PATH = "./test/db4oVersions/";

    static final String[] VERSIONS = {
        "db4o_3.0.3", 
        "db4o_4.0.004", 
        "db4o_4.1.001", 
        "db4o_4.6.003",
        "db4o_4.6.004", 
        "db4o_5.0.007", 
        "db4o_5.1.001",
        "db4o_5.2.001", 
        "db4o_5.2.003",
        "db4o_5.2.008",
        "db4o_5.3.001", 
        "db4o_5.4.004",
        "db4o_5.5.2",
        "db4o_5.6.2"};

    List list;
    Map map;
    String name;
    
    public void configure(){
        Db4o.configure().allowVersionUpdates(true);
        Db4o.configure().objectClass(UpdatingDb4oVersions.class).objectField("name").indexed(true);
    }

    public void store(){
        if(Test.isClientServer()){
            return;
        }
        String file = PATH + fileName();
        new File(file).mkdirs();
        new File(file).delete();
        ExtObjectContainer objectContainer = Db4o.openFile(file).ext();
        UpdatingDb4oVersions udv = new UpdatingDb4oVersions();
        udv.name = "check";
        udv.list = objectContainer.collections().newLinkedList();
        udv.map = objectContainer.collections().newHashMap(1);
        objectContainer.set(udv);
        udv.list.add("check");
        udv.map.put("check","check");
        objectContainer.close();
    }

    public void test(){
        if(Test.isClientServer()){
            return;
        }
        for(int i = 0; i < VERSIONS.length; i ++){
            File oldFile = new File(PATH + VERSIONS[i]);
            if(oldFile.exists()){
                String testFile = PATH + VERSIONS[i] + ".yap";
                new File(testFile).delete();
                try {
                    byte[] bytes = new byte[(int)oldFile.length()]; 
                    FileInputStream fin = new FileInputStream(oldFile);
                    fin.read(bytes);
                    fin.close();
                    FileOutputStream fout = new FileOutputStream(new File(testFile));
                    fout.write(bytes);
                    fout.flush();
                    fout.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
                ExtObjectContainer objectContainer = Db4o.openFile(testFile).ext();
                Query q = objectContainer.query();
                q.constrain(UpdatingDb4oVersions.class);
                ObjectSet objectSet = q.execute();
                Test.ensure(objectSet.size() == 1);
                UpdatingDb4oVersions udv = (UpdatingDb4oVersions)objectSet.next();
                Test.ensure(udv.name.equals("check"));
                Test.ensure(udv.list.size() == 1);
                Test.ensure(udv.list.get(0).equals("check"));
                Test.ensure(udv.map.get("check").equals("check"));
                objectContainer.close();
            }else{
                System.err.println("Version upgrade check failed. File not found:");
                System.err.println(oldFile);
            }
        }
    }

    private static String fileName(){
        return Db4o.version().replace(' ', '_') + ".yap";
    }
}

