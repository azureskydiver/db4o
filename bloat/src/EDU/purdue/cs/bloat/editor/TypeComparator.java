/**
 * All files in the distribution of BLOAT (Bytecode Level Optimization and
 * Analysis tool for Java(tm)) are Copyright 1997-2001 by the Purdue
 * Research Foundation of Purdue University.  All rights reserved.
 *
 * <p>
 *
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

package EDU.purdue.cs.bloat.editor;

import java.util.*;

import EDU.purdue.cs.bloat.util.*;

// // For testing only
// import EDU.purdue.cs.bloat.file.*;
// import EDU.purdue.cs.bloat.context.*;

/**
 * A <tt>TypeComparator</tt> orders <tt>Type</tt>s such that a subclass
 * preceededs its superclass. Note that this doesn't really work with
 * interfaces.
 */
public final class TypeComparator implements Comparator {

	public static boolean DEBUG = false;

	private EditorContext context;

	private static void db(final String s) {
		if (TypeComparator.DEBUG) {
			System.out.println(s);
		}
	}

	/**
	 * Constructor.
	 */
	public TypeComparator(final EditorContext context) {
		this.context = context;
	}

	/**
	 * Returns a negative value if o1 < o2 (t1 is a subclass of t2). Otherwise,
	 * it returns a positive value.
	 */
	public int compare(final Object o1, final Object o2) {
		Assert.isTrue(o1 instanceof Type, o1 + " is not a Type");
		Assert.isTrue(o2 instanceof Type, o2 + " is not a Type");

		final Type t1 = (Type) o1;
		final Type t2 = (Type) o2;

		TypeComparator.db("Comparing " + t1 + " to " + t2);

		final ClassHierarchy hier = context.getHierarchy();

		if (hier.subclassOf(t1, t2)) {
			TypeComparator.db("  " + t1 + " is a subclass of " + t2);
			return (-1);

		} else if (hier.subclassOf(t2, t1)) {
			TypeComparator.db("  " + t2 + " is a subclass of " + t1);
			return (1);

		} else {
			TypeComparator.db("  " + t1 + " and " + t2 + " are unrelated");

			// Don't return 0. If you do, the type will not get included in
			// the sorted set. Weird.
			return (1);
		}
	}

	/**
	 * Indicates whether some other object is "equal to" this Comparator.
	 */
	public boolean equals(final Object other) {
		return (other instanceof TypeComparator);
	}

	// /**
	// * Test program. Reads class names from the command line.
	// */
	// public static void main(String[] args) {
	// // Make a list of class names
	// List names = new ArrayList();
	// for(int i = 0; i < args.length; i++) {
	// names.add(args[i]);
	// }

	// // Do some BLOAT magic
	// EditorContext context =
	// new CachingBloatContext(new ClassFileLoader(), names, false);
	// Collection classes = context.getHierarchy().classes();

	// TypeComparator.DEBUG = true;
	// SortedSet sorted = new TreeSet(new TypeComparator(context));
	// sorted.addAll(classes);

	// System.out.println(classes.size() + " classes");
	// System.out.println(sorted.size() + " sorted types:");
	// Object[] array = sorted.toArray();
	// for(int i = 0; i < array.length; i++) {
	// System.out.println(" " + array[i]);
	// }
	// }
}
