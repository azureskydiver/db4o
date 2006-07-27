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

/**
 * LocalDebugInfo is used to map a local variable index in a range of
 * instructions to a local in the original Java source file. In addition,
 * LocalDebugInfo keeps track of a local variable's name and type (as indices
 * into the constant pool) and which number local variable is being represented.
 * Instances of <tt>file.LocalVariableTable</tt> consist of an array of
 * LocalDebugInfo.
 * 
 * @see EDU.purdue.cs.bloat.file.LocalVariableTable
 * 
 * @author Nate Nystrom (<a
 *         href="mailto:nystrom@cs.purdue.edu">nystrom@cs.purdue.edu</a>)
 */
public class LocalDebugInfo {
	private int startPC;

	private int length;

	private int nameIndex;

	private int typeIndex;

	private int index;

	/**
	 * Constructor.
	 * 
	 * @param startPC
	 *            The start PC of the live range of the variable.
	 * @param length
	 *            The length of the live range of the variable.
	 * @param nameIndex
	 *            The index into the constant pool of the name of the variable.
	 * @param typeIndex
	 *            The index into the constant pool of the type descriptor of the
	 *            variable.
	 * @param index
	 *            The index of this variable into the local variable array for
	 *            the method.
	 */
	public LocalDebugInfo(final int startPC, final int length,
			final int nameIndex, final int typeIndex, final int index) {
		this.startPC = startPC;
		this.length = length;
		this.nameIndex = nameIndex;
		this.typeIndex = typeIndex;
		this.index = index;
	}

	/**
	 * Get the start PC of the live range of the variable.
	 * 
	 * @return The start PC of the live range of the variable.
	 */
	public int startPC() {
		return startPC;
	}

	/**
	 * Get the length of the live range of the variable.
	 * 
	 * @return The length of the live range of the variable.
	 */
	public int length() {
		return length;
	}

	/**
	 * Get the index into the constant pool of the name of the variable.
	 * 
	 * @return The index into the constant pool of the name of the variable.
	 */
	public int nameIndex() {
		return nameIndex;
	}

	/**
	 * Get the index into the constant pool of the type descriptor of the
	 * variable.
	 * 
	 * @return The index into the constant pool of the type descriptor of the
	 *         variable.
	 */
	public int typeIndex() {
		return typeIndex;
	}

	/**
	 * Get the index of this variable into the local variable array for the
	 * method.
	 * 
	 * @return The index of this variable into the local variable array for the
	 *         method.
	 */
	public int index() {
		return index;
	}

	public Object clone() {
		return (new LocalDebugInfo(this.startPC, this.length, this.nameIndex,
				this.typeIndex, this.index));
	}

	/**
	 * Returns a string representation of the attribute.
	 */
	public String toString() {
		return "(local #" + index + " pc=" + startPC + ".."
				+ (startPC + length) + " name=" + nameIndex + " desc="
				+ typeIndex + ")";
	}
}
