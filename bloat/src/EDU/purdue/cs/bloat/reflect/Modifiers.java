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
 * Modifiers is an interface containing constants used as modifiers of classes,
 * fields, and methods.
 * 
 * @author Nate Nystrom (<a
 *         href="mailto:nystrom@cs.purdue.edu">nystrom@cs.purdue.edu</a>)
 */
public interface Modifiers {
	/**
	 * The class, field, or method is declared public.
	 */
	public static final short PUBLIC = 0x0001;

	/**
	 * The class, field, or method is declared private.
	 */
	public static final short PRIVATE = 0x0002;

	/**
	 * The class, field, or method is declared protected.
	 */
	public static final short PROTECTED = 0x0004;

	/**
	 * The field or method is declared static.
	 */
	public static final short STATIC = 0x0008;

	/**
	 * The class, field, or method is declared final.
	 */
	public static final short FINAL = 0x0010;

	/**
	 * The class calls methods in the superclass.
	 */
	public static final short SUPER = 0x0020;

	/**
	 * The method is declared synchronized.
	 */
	public static final short SYNCHRONIZED = 0x0020;

	/**
	 * The field is declared volatile.
	 */
	public static final short VOLATILE = 0x0040;

	/**
	 * The field is declared transient.
	 */
	public static final short TRANSIENT = 0x0080;

	/**
	 * The method is declared native.
	 */
	public static final short NATIVE = 0x0100;

	/**
	 * The class is an interface.
	 */
	public static final short INTERFACE = 0x0200;

	/**
	 * The class or method is declared abstract.
	 */
	public static final short ABSTRACT = 0x0400;
}
