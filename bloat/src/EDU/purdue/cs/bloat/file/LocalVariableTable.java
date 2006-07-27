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

import EDU.purdue.cs.bloat.reflect.*;

/**
 * LocalVariableTable represents debugging information that may be used by a
 * debugger to determine the value of a given local variable during program
 * execution. It is essentially an array of <tt>reflect.LocalDebugInfo</tt>.
 * 
 * @see EDU.purdue.cs.bloat.reflect.LocalDebugInfo
 * 
 * @author Nate Nystrom (<a
 *         href="mailto:nystrom@cs.purdue.edu">nystrom@cs.purdue.edu</a>)
 */
public class LocalVariableTable extends Attribute {
	private LocalDebugInfo[] locals;

	/**
	 * Constructor. Create an attribute from a data stream.
	 * 
	 * @param in
	 *            The data stream of the class file.
	 * @param index
	 *            The index into the constant pool of the name of the attribute.
	 * @param len
	 *            The length of the attribute, excluding the header.
	 * @exception IOException
	 *                If an error occurs while reading.
	 */
	public LocalVariableTable(final DataInputStream in, final int index,
			final int len) throws IOException {
		super(index, len);

		final int numLocals = in.readUnsignedShort();

		locals = new LocalDebugInfo[numLocals];

		for (int i = 0; i < locals.length; i++) {
			final int startPC = in.readUnsignedShort();
			final int length = in.readUnsignedShort();
			final int nameIndex = in.readUnsignedShort();
			final int typeIndex = in.readUnsignedShort();
			final int varIndex = in.readUnsignedShort();
			locals[i] = new LocalDebugInfo(startPC, length, nameIndex,
					typeIndex, varIndex);
		}
	}

	/**
	 * Get the local variable debug info for the code.
	 * 
	 * @return The local variable debug info for the code.
	 */
	public LocalDebugInfo[] locals() {
		return locals;
	}

	/**
	 * Set the local variable debug info for the code.
	 * 
	 * @param locals
	 *            The local variable debug info for the code.
	 */
	public void setLocals(final LocalDebugInfo[] locals) {
		this.locals = locals;
	}

	/**
	 * Get the length of the attribute.
	 * 
	 * @return The length of the attribute.
	 */
	public int length() {
		return 2 + locals.length * 10;
	}

	public String toString() {
		String x = "(locals";

		for (int i = 0; i < locals.length; i++) {
			x += "\n          (local @" + locals[i].index() + " name="
					+ locals[i].nameIndex() + " type=" + locals[i].typeIndex()
					+ " pc=" + locals[i].startPC() + ".."
					+ (locals[i].startPC() + locals[i].length()) + ")";
		}

		return x + ")";
	}

	/**
	 * Write the attribute to a data stream.
	 * 
	 * @param out
	 *            The data stream of the class file.
	 * @exception IOException
	 *                If an error occurs while writing.
	 */
	public void writeData(final DataOutputStream out) throws IOException {
		out.writeShort(locals.length);

		for (int i = 0; i < locals.length; i++) {
			out.writeShort(locals[i].startPC());
			out.writeShort(locals[i].length());
			out.writeShort(locals[i].nameIndex());
			out.writeShort(locals[i].typeIndex());
			out.writeShort(locals[i].index());
		}
	}

	/**
	 * Private constructor used in cloning.
	 */
	private LocalVariableTable(final LocalVariableTable other) {
		super(other.nameIndex, other.length);

		this.locals = new LocalDebugInfo[other.locals.length];
		for (int i = 0; i < other.locals.length; i++) {
			this.locals[i] = (LocalDebugInfo) other.locals[i].clone();
		}
	}

	public Object clone() {
		return (new LocalVariableTable(this));
	}
}
