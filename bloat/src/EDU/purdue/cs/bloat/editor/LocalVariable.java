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

package EDU.purdue.cs.bloat.editor;

/**
 * LocalVariable represents a local variable index operand to various
 * instructions.
 * 
 * @author Nate Nystrom (<a
 *         href="mailto:nystrom@cs.purdue.edu">nystrom@cs.purdue.edu</a>)
 */
public class LocalVariable {
	private String name;

	private Type type;

	private int index;

	/**
	 * Constructor.
	 * 
	 * @param index
	 *            The index of the local variable in the method's local variable
	 *            array.
	 */
	public LocalVariable(final int index) {
		this.name = null;
		this.type = null;
		this.index = index;
	}

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            The name of the local variable.
	 * @param type
	 *            The descriptor (or index into the constant pool) representing
	 *            the variable type.
	 * @param index
	 *            The index of the local variable in the method's local variable
	 *            array.
	 */
	public LocalVariable(final String name, final Type type, final int index) {
		this.name = name;
		this.type = type;
		this.index = index;
	}

	/**
	 * Hash the local variable.
	 * 
	 * A stricter hashing than using the index will break Hashtable lookups
	 * since a variable could have a name assigned to it after its first use.
	 * 
	 * @return The hash code.
	 */
	public int hashCode() {
		return index;
	}

	/**
	 * Check if an object is equal to this variable.
	 * 
	 * A stricter comparison than comparing indices will break Hashtable lookups
	 * since a variable could have a name assigned to it after its first use.
	 * 
	 * @param obj
	 *            The object to compare against.
	 * @return true if equal, false if not.
	 */
	public boolean equals(final Object obj) {
		return (obj != null) && (obj instanceof LocalVariable)
				&& (((LocalVariable) obj).index == index);
	}

	/**
	 * Get the name of the local variable.
	 * 
	 * @return The name of the local variable.
	 */
	public String name() {
		return name;
	}

	/**
	 * Get the type of the local variable.
	 * 
	 * @return The type of the local variable.
	 */
	public Type type() {
		return type;
	}

	/**
	 * Get the index into the local variable array.
	 * 
	 * @return The index into the local variable array.
	 */
	public int index() {
		return index;
	}

	/**
	 * Convert the variable to a string.
	 * 
	 * @return A string representation of the variable.
	 */
	public String toString() {
		if (name == null) {
			return "Local$" + index;
		}

		return name + "$" + index;
	}
}
