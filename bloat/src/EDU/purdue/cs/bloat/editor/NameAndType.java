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
 * Methods and fields are described by their name and type descriptor.
 * NameAndType represents exactly that.
 * 
 * @author Nate Nystrom (<a
 *         href="mailto:nystrom@cs.purdue.edu">nystrom@cs.purdue.edu</a>)
 */
public class NameAndType {
	private String name;

	private Type type;

	/**
	 * Constructor.
	 */
	public NameAndType(final String name, final Type type) {
		this.name = name;
		this.type = type;
	}

	/**
	 * Returns the name.
	 */
	public String name() {
		return name;
	}

	/**
	 * Returns the type.
	 */
	public Type type() {
		return type;
	}

	/**
	 * Returns a string representation of the name and type.
	 */
	public String toString() {
		return "<NameandType " + name + " " + type + ">";
	}

	/**
	 * Check if an object is equal to this name and type.
	 * 
	 * @param obj
	 *            The object to compare against.
	 * @return <tt>true</tt> if equal
	 */
	public boolean equals(final Object obj) {
		return (obj instanceof NameAndType)
				&& ((NameAndType) obj).name.equals(name)
				&& ((NameAndType) obj).type.equals(type);
	}

	/**
	 * Returns a hash of the name and type.
	 */
	public int hashCode() {
		return name.hashCode() ^ type.hashCode();
	}
}
