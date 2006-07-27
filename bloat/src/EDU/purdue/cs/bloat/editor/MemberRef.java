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
 * MemberRef represents a method or field (as a <tt>NameAndType</tt>) and the
 * class (as a <tt>Type</tt>) in which it is declared.
 * 
 * @author Nate Nystrom (<a
 *         href="mailto:nystrom@cs.purdue.edu">nystrom@cs.purdue.edu</a>)
 */
public class MemberRef {
	private Type declaringClass;

	private NameAndType nameAndType;

	/**
	 * Constructor.
	 * 
	 * @param declaringClass
	 *            The type of the class which declared the member.
	 * @param nameAndType
	 *            The name and type of the member.
	 */
	public MemberRef(final Type declaringClass, final NameAndType nameAndType) {
		this.declaringClass = declaringClass;
		this.nameAndType = nameAndType;
	}

	/**
	 * Get the type of the class which declared the member.
	 * 
	 * @return The type of the class which declared the member.
	 */
	public Type declaringClass() {
		return declaringClass;
	}

	/**
	 * Get the name of the member.
	 * 
	 * @return The name of the member.
	 */
	public String name() {
		return nameAndType.name();
	}

	/**
	 * Get the type of the member.
	 * 
	 * @return The type of the member.
	 */
	public Type type() {
		return nameAndType.type();
	}

	/**
	 * Get the name and type of the member.
	 * 
	 * @return The name and type of the member.
	 */
	public NameAndType nameAndType() {
		return nameAndType;
	}

	/**
	 * Convert the reference to a string.
	 * 
	 * @return A string representation of the reference.
	 */
	public String toString() {
		// Take advantage of PRINT_TRUNCATED in Type
		final String className = declaringClass.toString();
		return "<" + (type().isMethod() ? "Method" : "Field") + " " + className
				+ "." + name() + " " + type() + ">";
	}

	/**
	 * Check if an object is equal to this reference.
	 * 
	 * @param obj
	 *            The object to compare against.
	 * @return true if equal, false if not.
	 */
	public boolean equals(final Object obj) {
		return (obj instanceof MemberRef)
				&& ((MemberRef) obj).declaringClass.equals(declaringClass)
				&& ((MemberRef) obj).nameAndType.equals(nameAndType);
	}

	/**
	 * Hash the member reference.
	 * 
	 * @return The hash code.
	 */
	public int hashCode() {
		return declaringClass.hashCode() ^ nameAndType.hashCode();
	}
}
