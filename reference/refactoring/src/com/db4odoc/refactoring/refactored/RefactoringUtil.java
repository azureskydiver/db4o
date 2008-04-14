/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */
package com.db4odoc.refactoring.refactored;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4odoc.refactoring.initial.B;
 
public class RefactoringUtil {
	
	private final static String DB4O_FILE_NAME="reference.db4o";

	public static void main(String[] args) {
		moveValues();
	}

	public static void moveValues(){
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			// querying for B will bring back B and C values
			ObjectSet result = container.queryByExample(new B());
			while (result.hasNext()){
				B b = (B)result.next();
				D d = new D();
				d.name = b.name;
				d.number = b.number;
				container.delete(b);
				container.store(d);
			}
			
		} finally {
			container.close();
			System.out.println("Done");
		}
	}
	// end moveValues
	
	
}
