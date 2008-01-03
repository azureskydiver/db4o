/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */
package com.db4odoc.refactoring.refactored;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4odoc.refactoring.initial.C;

public class RefactoringUtil {
	
	private final static String DB4O_FILE_NAME="reference.db4o";

	public static void main(String[] args) {
		moveValues();
	}

	public static void moveValues(){
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			ObjectSet result = container.get(new C());
			while (result.hasNext()){
				C c = (C)result.next();
				E e = new E();
				e.name = c.name;
				e.number = c.number;
				container.delete(c);
				container.set(e);
			}
			
		} finally {
			container.close();
			System.out.println("Done");
		}
	}
	// end moveValues
	
	
}
