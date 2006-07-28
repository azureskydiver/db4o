/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.j2me.bloat;

import java.io.*;

import com.db4o.*;
import com.db4o.j2me.bloat.testdata.*;
import com.db4o.reflect.self.*;

public class EnhanceTestMain {
	private static final String FILENAME = "enhanceddog.yap";

	public static void main(String[] args) throws Exception {
        Class registryClazz=Class.forName("com.db4o.j2me.bloat.testdata.GeneratedDogSelfReflectionRegistry");
        SelfReflectionRegistry registry=(SelfReflectionRegistry)registryClazz.newInstance();
        Db4o.configure().reflectWith(new SelfReflector(registry));
        new File(FILENAME).delete();
        ObjectContainer db=Db4o.openFile(FILENAME);
        db.set(new Dog("Laika",111,new Dog[]{},new int[]{1,2,3}));
        db.close();
        db=Db4o.openFile(FILENAME);
        ObjectSet result=db.get(Dog.class);
        while(result.hasNext()) {
        	System.out.println(result.next());
        }
        db.close();
	}
}
