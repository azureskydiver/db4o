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
 * LineNumberTable is an attribute of a code attribute. A LineNumberTable stores
 * information that relates indices into the code array (instructions) to the
 * lines of code in the source file from which they were compiled. This optional
 * attribute is used with debuggers (<i>duh</i>) and consists of an array of
 * <tt>reflect.LineNumberDebugInfo</tt>.
 * 
 * @see Code
 * @see EDU.purdue.cs.bloat.reflect.LineNumberDebugInfo
 * 
 * @author Nate Nystrom (<a
 *         href="mailto:nystrom@cs.purdue.edu">nystrom@cs.purdue.edu</a>)
 */
public class LineNumberTable extends Attribute {
	private LineNumberDebugInfo[] lineNumbers;

	/**
	 * Constructor. Create an attribute from a data stream.
	 * 
	 * @param in
	 *            The data stream of the class file.
	 * @param nameIndex
	 *            The index into the constant pool of the name of the attribute.
	 * @param length
	 *            The length of the attribute, excluding the header.
	 * @exception IOException
	 *                If an error occurs while reading.
	 */
	public LineNumberTable(final DataInputStream in, final int nameIndex,
			final int length) throws IOException {
		super(nameIndex, length);

		final int numLines = in.readUnsignedShort();

		lineNumbers = new LineNumberDebugInfo[numLines];

		for (int i = 0; i < lineNumbers.length; i++) {
			final int startPC = in.readUnsignedShort();
			final int lineNumber = in.readUnsignedShort();
			lineNumbers[i] = new LineNumberDebugInfo(startPC, lineNumber);
		}
	}

	/**
	 * Get the line number debug info for the code.
	 * 
	 * @return The line number debug info for the code.
	 */
	public LineNumberDebugInfo[] lineNumbers() {
		return lineNumbers;
	}

	/**
	 * Set the line number debug info for the code.
	 * 
	 * @param lineNumbers
	 *            The line number debug info for the code.
	 */
	public void setLineNumbers(final LineNumberDebugInfo[] lineNumbers) {
		this.lineNumbers = lineNumbers;
	}

	/**
	 * Get the length of the attribute.
	 * 
	 * @return The length of the attribute.
	 */
	public int length() {
		return 2 + lineNumbers.length * 4;
	}

	public String toString() {
		String x = "(lines";

		for (int i = 0; i < lineNumbers.length; i++) {
			x += "\n          (line #" + lineNumbers[i].lineNumber() + " pc="
					+ lineNumbers[i].startPC() + ")";
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
		out.writeShort(lineNumbers.length);

		for (int i = 0; i < lineNumbers.length; i++) {
			out.writeShort(lineNumbers[i].startPC());
			out.writeShort(lineNumbers[i].lineNumber());
		}
	}

	/**
	 * Private constructor used in cloning.
	 */
	private LineNumberTable(final LineNumberTable other) {
		super(other.nameIndex, other.length);

		this.lineNumbers = new LineNumberDebugInfo[other.lineNumbers.length];
		for (int i = 0; i < other.lineNumbers.length; i++) {
			this.lineNumbers[i] = (LineNumberDebugInfo) other.lineNumbers[i]
					.clone();
		}
	}

	public Object clone() {
		return (new LineNumberTable(this));
	}
}
