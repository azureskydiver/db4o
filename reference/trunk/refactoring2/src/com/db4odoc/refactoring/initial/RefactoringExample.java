/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */
package com.db4odoc.refactoring.initial;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

public class RefactoringExample {

	private final static String DB4O_FILE_NAME="reference.db4o";
	
	public static void main(String[] args) {
		storeData();
		readData();
	}
	
	public static void storeData(){
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			A a = new A();
			a.name = "A class";
			container.set(a);
			
			B b = new B();
			b.name = "B class";
			b.number = 1;
			container.set(b);
			
			C c = new C();
			c.name = "C class";
			c.number = 2;
			container.set(c);
		} finally {
			container.close();
		}
	}
	// end storeData

	public static void readData(){
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			ObjectSet result = container.get(new A());
			System.out.println("A class: ");
			listResult(result);
			
			result = container.get(new B());
			System.out.println();
			System.out.println("B class: ");
			listResult(result);
			
			result = container.get(new C());
			System.out.println();
			System.out.println("C class: ");
			listResult(result);
		} finally {
			container.close();
		}
	}
	// end readData
	
    private static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult

}
