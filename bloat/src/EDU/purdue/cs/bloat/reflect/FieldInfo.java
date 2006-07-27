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
 * FieldInfo grants access to a field's name and type (represented as indices
 * into the constant pool), as well as its modifiers. FieldInfo is implemented
 * in <tt>file.Field</tt>.
 * 
 * @see EDU.purdue.cs.bloat.file.Field
 * 
 * @author Nate Nystrom (<a
 *         href="mailto:nystrom@cs.purdue.edu">nystrom@cs.purdue.edu</a>)
 */
public interface FieldInfo {
	/**
	 * Get the class which declared the field.
	 * 
	 * @return The ClassInfo of the class which declared the field.
	 */
	public ClassInfo declaringClass();

	/**
	 * Get the index into the constant pool of the name of the field.
	 * 
	 * @return The index into the constant pool of the name of the field.
	 */
	public int nameIndex();

	/**
	 * Get the index into the constant pool of the type of the field.
	 * 
	 * @return The index into the constant pool of the type of the field.
	 */
	public int typeIndex();

	/**
	 * Set the index into the constant pool of the name of the field.
	 * 
	 * @param index
	 *            The index into the constant pool of the name of the field.
	 */
	public void setNameIndex(int index);

	/**
	 * Set the index into the constant pool of the type of the field.
	 * 
	 * @param index
	 *            The index into the constant pool of the type of the field.
	 */
	public void setTypeIndex(int index);

	/**
	 * Set the modifiers of the field. The values correspond to the constants in
	 * the Modifiers class.
	 * 
	 * @param modifiers
	 *            A bit vector of modifier flags for the field.
	 * @see Modifiers
	 */
	public void setModifiers(int modifiers);

	/**
	 * Get the modifiers of the field. The values correspond to the constants in
	 * the Modifiers class.
	 * 
	 * @return A bit vector of modifier flags for the field.
	 * @see Modifiers
	 */
	public int modifiers();

	/**
	 * Get the index into the constant pool of the field's constant value, if
	 * any. Returns 0 if the field does not have a constant value.
	 * 
	 * @see ClassInfo#constants
	 */
	public int constantValue();

	/**
	 * Set the index into the constant pool of the field's constant value.
	 * 
	 * @see ClassInfo#constants
	 */
	public void setConstantValue(int index);
}
