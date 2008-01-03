/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
/*
 * Singleton class used to keep auotincrement information 
 * and give the next available ID on request
 */
package com.db4odoc.autoinc;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

public class IncrementedId {
	private int no;
	private static IncrementedId ref;

	private IncrementedId() {
		this.no = 0;
	}

	// end IncrementedId

	public int getNextID(ObjectContainer container) {
		no++;
		container.store(this);
		return no;
	}

	// end increment

	public static IncrementedId getIdObject(ObjectContainer container) {
		// if ref is not assigned yet:
		if (ref == null) {
			// check if there is a stored instance from the previous 
			// session in the database
			ObjectSet os = container.queryByExample(IncrementedId.class);
			if (os.size() > 0)
				ref = (IncrementedId) os.next();
		}

		if (ref == null) {
			// create new instance and store it
			System.out.println("Id object is created");
			ref = new IncrementedId();
			container.store(ref);
		}
		return ref;
	}
	// end getIdObject
}
