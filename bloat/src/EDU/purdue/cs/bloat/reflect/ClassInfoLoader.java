/**
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

package EDU.purdue.cs.bloat.reflect;

import java.io.*;

/**
 * ClassInfoLoader provides an interface for loading classes. Implementing
 * classes can load classes from a file, from the JVM, or elsewhere.
 * 
 * @author Nate Nystrom (<a
 *         href="mailto:nystrom@cs.purdue.edu">nystrom@cs.purdue.edu</a>)
 */
public interface ClassInfoLoader {
	/**
	 * Load a class.
	 * 
	 * @param name
	 *            The name of the class to load, including the package name.
	 * @return A ClassInfo for the class.
	 * @exception ClassNotFoundException
	 *                The class cannot be found in the class path.
	 * @see ClassInfo
	 */
	public ClassInfo loadClass(String name) throws ClassNotFoundException;

	/**
	 * Creates a new class or interface.
	 * 
	 * @param modifiers
	 *            The modifiers describing the newly-created class
	 * @param classIndex
	 *            The index of the name of the newly-created class in its
	 *            constant pool
	 * @param superClassIndex
	 *            The index of the name of the newly-created class's superclass
	 *            in its constant pool
	 * @param interfaceIndexes
	 *            The indexes of the names of the interfaces that the
	 *            newly-created class implements
	 * @param constants
	 *            The constant pool for the newly created class (a list of
	 *            {@link Constant}s).
	 */
	public ClassInfo newClass(int modifiers, int classIndex,
			int superClassIndex, int[] interfaceIndexes,
			java.util.List constants);

	/**
	 * Returns an <code>OutputStream</code> to which a class should be
	 * written.
	 */
	OutputStream outputStreamFor(ClassInfo info) throws IOException;
}
