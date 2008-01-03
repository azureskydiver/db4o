/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */
package com.db4odoc.refactoring.refactored;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

public class RefactoringExample {
	
	private final static String DB4O_FILE_NAME="reference.db4o";

	public static void main(String[] args) {
		readData();
	}

	
	public static void readData(){
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			ObjectSet result = container.get(new D());
			System.out.println();
			System.out.println("D class: ");
			listResult(result);
			
			result = container.get(new E());
			System.out.println();
			System.out.println("E class: ");
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
