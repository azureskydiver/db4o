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

import EDU.purdue.cs.bloat.reflect.*;

/**
 * An <tt>EditorContext</tt> supplies a means of loading and editing classes.
 * Note that a number of these methods are identical to methods in
 * <tt>Editor</tt>. It is expected that an <tt>EditorContext</tt> will have
 * a different caching (of <tt>ClassEditor</tt>s, etc.) policy than
 * <tt>Editor</tt> does. Hence, the methods in <tt>EditorContext</tt> should
 * be used to edit classes, etc.
 */
public interface EditorContext {

	/**
	 * Loads a class into BLOAT
	 */
	public ClassInfo loadClass(String className) throws ClassNotFoundException;

	/**
	 * Creates a new <code>ClassInfo</code>
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
	public ClassInfo newClassInfo(int modifiers, int classIndex,
			int superClassIndex, int[] interfaceIndexes,
			java.util.List constants);

	/**
	 * Returns the <tt>ClassHierarchy</tt> of all classes and interfaces known
	 * to BLOAT.
	 */
	public ClassHierarchy getHierarchy();

	/**
	 * Returns a <code>ClassEditor</code> for editing a new class with the
	 * given name. It will override any class with the given name that is
	 * already being edited.
	 */
	public ClassEditor newClass(int modifiers, String className,
			Type superType, Type[] interfaces);

	/**
	 * Returns a <tt>ClassEditor</tt> used to edit a class of a given name.
	 */
	public ClassEditor editClass(String className)
			throws ClassNotFoundException, ClassFormatException;

	/**
	 * Returns a <tt>ClassEditor</tt> used to edit a class described by a
	 * given <tt>Type</tt>.
	 */
	public ClassEditor editClass(Type classType) throws ClassNotFoundException,
			ClassFormatException;

	/**
	 * Returns a <tt>ClassEditor</tt> used to edit a class described by a
	 * given <tt>ClassInfo</tt>.
	 */
	public ClassEditor editClass(ClassInfo info);

	/**
	 * Returns a <tt>FieldEditor</tt> for editing a <tt>FieldInfo</tt>.
	 */
	public FieldEditor editField(FieldInfo info);

	/**
	 * Returns a <tt>FieldEditor</tt> for editing a field.
	 */
	public FieldEditor editField(MemberRef field) throws NoSuchFieldException;

	/**
	 * Returns a <tt>MethodEditor</tt> for editing a method.
	 */
	public MethodEditor editMethod(MethodInfo info);

	/**
	 * Returns a <tt>MethodEditor</tt> for editing a method.
	 */
	public MethodEditor editMethod(MemberRef method)
			throws NoSuchMethodException;

	/**
	 * Signals that we are done editing a method. The object used to model it
	 * may be reclaimed.
	 */
	public void release(MethodInfo info);

	/**
	 * Signals that we are done editing a field. The object used to model it may
	 * be reclaimed.
	 */
	public void release(FieldInfo info);

	/**
	 * Signals that we are done editing a class. The object used to model it may
	 * be reclaimed.
	 */
	public void release(ClassInfo info);

	/**
	 * Commits the changes made to a class.
	 */
	public void commit(ClassInfo info);

	/**
	 * Commits the changes made to a method.
	 */
	public void commit(MethodInfo info);

	/**
	 * Commits the changes made to a field.
	 */
	public void commit(FieldInfo info);

	/**
	 * Commits all changes made to classes, methods, and fields.
	 */
	public void commit();
}
