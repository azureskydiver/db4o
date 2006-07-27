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
 * Field models a field (member variable) in a class. The Field class grants
 * access to information such as the field's modifiers, its name and type
 * descriptor (represented as indices into the constant pool), and any
 * attributes of the field. Static fields have a ConstantValue attribute.
 * 
 * @see ConstantValue
 * 
 * @author Nate Nystrom (<a
 *         href="mailto:nystrom@cs.purdue.edu">nystrom@cs.purdue.edu</a>)
 */
public class Field implements FieldInfo {
	private ClassInfo classInfo;

	private int modifiers;

	private int name;

	private int type;

	private Attribute[] attrs;

	private ConstantValue constantValue;

	/**
	 * Constructor for creating a new field from scratch
	 */
	Field(final ClassInfo classInfo, final int modifiers, final int typeIndex,
			final int nameIndex) {
		this.classInfo = classInfo;
		this.modifiers = modifiers;
		this.name = nameIndex;
		this.type = typeIndex;
		this.attrs = new Attribute[0];
		this.constantValue = null;
	}

	/**
	 * Constructor for creating a new field that has a constant value from
	 * scratch
	 */
	Field(final ClassInfo classInfo, final int modifiers, final int typeIndex,
			final int nameIndex, final int cvNameIndex,
			final int constantValueIndex) {
		this.classInfo = classInfo;
		this.modifiers = modifiers;
		this.name = nameIndex;
		this.type = typeIndex;
		this.constantValue = new ConstantValue(cvNameIndex, 2,
				constantValueIndex);

		// The constant value is an attribute
		this.attrs = new Attribute[1];
		this.attrs[0] = constantValue;
	}

	/**
	 * Constructor. Read a field from a class file.
	 * 
	 * @param in
	 *            The data stream of the class file.
	 * @param classInfo
	 *            The class file containing the field.
	 * @exception IOException
	 *                If an error occurs while reading.
	 */
	public Field(final DataInputStream in, final ClassInfo classInfo)
			throws IOException {
		this.classInfo = classInfo;

		modifiers = in.readUnsignedShort();

		name = in.readUnsignedShort();
		type = in.readUnsignedShort();

		final int numAttributes = in.readUnsignedShort();

		attrs = new Attribute[numAttributes];

		for (int i = 0; i < numAttributes; i++) {
			final int nameIndex = in.readUnsignedShort();
			final int length = in.readInt();

			final Constant name = classInfo.constants()[nameIndex];

			if (name != null) {
				if ("ConstantValue".equals(name.value())) {
					constantValue = new ConstantValue(in, nameIndex, length);
					attrs[i] = constantValue;
				}
			}

			if (attrs[i] == null) {
				attrs[i] = new GenericAttribute(in, nameIndex, length);
			}
		}
	}

	/**
	 * Get the class which declared the field.
	 * 
	 * @return The ClassInfo of the class which declared the field.
	 */
	public ClassInfo declaringClass() {
		return classInfo;
	}

	/**
	 * Set the index into the constant pool of the name of the field.
	 * 
	 * @param name
	 *            The name of the field.
	 */
	public void setNameIndex(final int name) {
		this.name = name;
	}

	/**
	 * Set the index into the constant pool of the type of the field.
	 * 
	 * @param type
	 *            The type of the field.
	 */
	public void setTypeIndex(final int type) {
		this.type = type;
	}

	/**
	 * Get the index into the constant pool of the name of the field.
	 * 
	 * @return The index into the constant pool of the name of the field.
	 */
	public int nameIndex() {
		return name;
	}

	/**
	 * Get the index into the constant pool of the type of the field.
	 * 
	 * @return The index into the constant pool of the type of the field.
	 */
	public int typeIndex() {
		return type;
	}

	/**
	 * Set the modifiers of the field. The values correspond to the constants in
	 * the Modifiers class.
	 * 
	 * @param modifiers
	 *            A bit vector of modifier flags for the field.
	 * @see Modifiers
	 */
	public void setModifiers(final int modifiers) {
		this.modifiers = modifiers;
	}

	/**
	 * Get the modifiers of the field. The values correspond to the constants in
	 * the Modifiers class.
	 * 
	 * @return A bit vector of modifier flags for the field.
	 * @see Modifiers
	 */
	public int modifiers() {
		return modifiers;
	}

	/**
	 * Get the index into the constant pool of the field's constant value, if
	 * any. Returns 0 if the field does not have a constant value.
	 * 
	 * @see ClassInfo#constants
	 */
	public int constantValue() {
		if (constantValue != null) {
			return constantValue.constantValueIndex();
		}
		return 0;
	}

	/**
	 * Set the index into the constant pool of the field's constant value.
	 * 
	 * @see ClassInfo#constants
	 */
	public void setConstantValue(final int index) {
		if (constantValue != null) {
			constantValue.setConstantValueIndex(index);
		}
	}

	/**
	 * Write the field to a class file.
	 * 
	 * @param out
	 *            The data stream of the class file.
	 * @exception IOException
	 *                If an error occurs while writing.
	 */
	public void write(final DataOutputStream out) throws IOException {
		out.writeShort(modifiers);

		out.writeShort(name);
		out.writeShort(type);

		out.writeShort(attrs.length);

		for (int i = 0; i < attrs.length; i++) {
			out.writeShort(attrs[i].nameIndex());
			out.writeInt(attrs[i].length());
			attrs[i].writeData(out);
		}
	}

	/**
	 * Convert the field to a string.
	 * 
	 * @return A string representation of the field.
	 */
	public String toString() {
		String x = "";

		x += " (modifiers";

		if ((modifiers & Modifiers.PUBLIC) != 0) {
			x += " PUBLIC";
		}
		if ((modifiers & Modifiers.PRIVATE) != 0) {
			x += " PRIVATE";
		}
		if ((modifiers & Modifiers.PROTECTED) != 0) {
			x += " PROTECTED";
		}
		if ((modifiers & Modifiers.STATIC) != 0) {
			x += " STATIC";
		}
		if ((modifiers & Modifiers.FINAL) != 0) {
			x += " FINAL";
		}
		if ((modifiers & Modifiers.SYNCHRONIZED) != 0) {
			x += " SYNCHRONIZED";
		}
		if ((modifiers & Modifiers.VOLATILE) != 0) {
			x += " VOLATILE";
		}
		if ((modifiers & Modifiers.TRANSIENT) != 0) {
			x += " TRANSIENT";
		}
		if ((modifiers & Modifiers.NATIVE) != 0) {
			x += " NATIVE";
		}
		if ((modifiers & Modifiers.INTERFACE) != 0) {
			x += " INTERFACE";
		}
		if ((modifiers & Modifiers.ABSTRACT) != 0) {
			x += " ABSTRACT";
		}
		x += ")";

		if (constantValue != null) {
			x += " " + constantValue;
		}

		return "(field " + name + " " + type + x + ")";
	}
}
