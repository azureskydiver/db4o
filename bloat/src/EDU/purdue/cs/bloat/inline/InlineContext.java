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

package EDU.purdue.cs.bloat.inline;

import java.util.*;

import EDU.purdue.cs.bloat.editor.*;

/**
 * An <Tt>InlineContext</tt> gives access to the <Tt>CallGraph</tt> for the
 * program whose classes are being operated on by BLOAT.
 */
public interface InlineContext extends EditorContext {

	/**
	 * Returns the call graph for the program.
	 */
	public CallGraph getCallGraph();

	/**
	 * Sets the root methods for this <tt>InlineContext</tt>.
	 * 
	 * @param roots
	 *            The root methods (<tt>MemberRef</tt>s) of the program
	 * @throws IllegalStateException
	 *             Call graph has already been created with different roots.
	 */
	public void setRootMethods(Set roots);

	/**
	 * Returns an <tt>InlineStats</tt> object for getting statistics about
	 * inlining.
	 */
	public InlineStats getInlineStats();

	/**
	 * Notes that all classes, methods, and fields in a package should be
	 * "ignored" by inlining. That is, methods won't be inlined, classes won't
	 * be involved in specialization, etc. Note that it is exceptable to just
	 * add a prefix of a package name. For instance, adding "java" will ignore
	 * java.lang.Object, java.io.File, etc.
	 */
	public void addIgnorePackage(String name);

	/**
	 * Notes that a class should be ignored by inlining. That is, none of its
	 * methods will be inlined and it won't be involved in specialization.
	 */
	public void addIgnoreClass(Type type);

	/**
	 * Notes that a method should be ignored by inlining. That is, it will not
	 * be inlined.
	 */
	public void addIgnoreMethod(MemberRef method);

	/**
	 * Notes that a field should be ignored by inlining.
	 */
	public void addIgnoreField(MemberRef field);

	/**
	 * Returns <tt>true</tt> if a class should be ignored by inlining.
	 */
	public boolean ignoreClass(Type type);

	/**
	 * Returns <tt>true</tt> if a method should be ignored by inlining.
	 */
	public boolean ignoreMethod(MemberRef method);

	/**
	 * Returns <tt>true</tt> if a field should be ignored by inlining.
	 */
	public boolean ignoreField(MemberRef field);

	/**
	 * Sets whether or not we ignore all system classes.
	 */
	public void setIgnoreSystem(boolean ignoreSystem);
}
