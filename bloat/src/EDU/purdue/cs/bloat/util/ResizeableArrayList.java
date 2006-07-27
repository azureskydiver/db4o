/*
 * All files in the distribution of BLOAT (Bytecode Level Optimization and
 * Analysis tool for Java(tm)) are Copyright 1997-2001 by the Purdue
 * Research Foundation of Purdue University.  All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms are permitted
 * provided that this entire copyright notice is duplicated in all
 * such copies, and that any documentation, announcements, and other
 * materials related to such distribution and use acknowledge that the
 * software was developed at Purdue University, West Lafayette, IN by
 * Antony Hosking, David Whitlock, and Nathaniel Nystrom.  No charge
 * may be made for copies, derivations, or distributions of this
 * material without the express written consent of the copyright
 * holder.  Neither the name of the University nor the name of the
 * author may be used to endorse or promote products derived from this
 * material without specific prior written permission.  THIS SOFTWARE
 * IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR ANY PARTICULAR PURPOSE.
 *
 * <p>
 * Java is a trademark of Sun Microsystems, Inc.
 */

package EDU.purdue.cs.bloat.util;

import java.util.*;

/**
 * ResizableArrayList is the same as ArrayList except that ensureSize not only
 * increases the size of the array (super.ensureCapacity), but it also fills the
 * empty space with null. This way, the size method will return the length of
 * the array and not just the number of elements in it. I guess.
 */
public class ResizeableArrayList extends ArrayList implements List, Cloneable,
		java.io.Serializable {
	/**
	 * This constructor is no longer supported in JDK1.2 public
	 * ResizeableArrayList(int initialCapacity, int capacityIncrement) {
	 * super(initialCapacity, capacityIncrement); }
	 */
	public ResizeableArrayList(final int initialCapacity) {
		super(initialCapacity);
	}

	public ResizeableArrayList() {
		super();
	}

	public ResizeableArrayList(final Collection c) {
		super(c);
	}

	public void ensureSize(final int size) {
		ensureCapacity(size);

		while (size() < size) {
			add(null);
		}
	}

	public Object clone() {
		return super.clone();
	}
}
