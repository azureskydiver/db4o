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

package EDU.purdue.cs.bloat.file;

import java.io.*;

/**
 * Attribute is an abstract class for an attribute defined for a method, field,
 * or class. An attribute consists of its name (represented as an index into the
 * constant pool) and its length. Attribute is extended to represent a constant
 * value, code, exceptions, etc.
 * 
 * @see Code
 * @see ConstantValue
 * @see Exceptions
 * 
 * @author Nate Nystrom (<a
 *         href="mailto:nystrom@cs.purdue.edu">nystrom@cs.purdue.edu</a>)
 */
public abstract class Attribute {
	protected int nameIndex;

	protected int length;

	/**
	 * Constructor.
	 * 
	 * @param nameIndex
	 *            The index into the constant pool of the name of the attribute.
	 * @param length
	 *            The length of the attribute, excluding the header.
	 */
	public Attribute(final int nameIndex, final int length) {
		this.nameIndex = nameIndex;
		this.length = length;
	}

	/**
	 * Write the attribute to a data stream.
	 * 
	 * @param out
	 *            The data stream of the class file.
	 */
	public abstract void writeData(DataOutputStream out) throws IOException;

	/**
	 * Returns a string representation of the attribute.
	 */
	public String toString() {
		return "(attribute " + nameIndex + " " + length + ")";
	}

	/**
	 * Returns the index into the constant pool of the name of the attribute.
	 */
	public int nameIndex() {
		return nameIndex;
	}

	/**
	 * Returns the length of the attribute, excluding the header.
	 */
	public int length() {
		return length;
	}

	public Object clone() {
		throw new UnsupportedOperationException("Cannot clone Attribute! "
				+ " (subclass: " + this.getClass() + ")");
	}
}
