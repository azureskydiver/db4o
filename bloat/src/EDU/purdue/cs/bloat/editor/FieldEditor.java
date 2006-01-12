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
import EDU.purdue.cs.bloat.util.*;
import java.util.*;
import java.io.*;

/**
 * <tt>FieldEditor</tt> provides a means to edit a field of a class.  A
 * <tt>FieldEditor</tt> is created from a <tt>ClassEditor</tt> and a
 * <tt>reflect.FieldInfo</tt>.  A <tt>FieldEditor</tt> knows its name,
 * type (descriptor), and its constant value (if it has one).
 *
 * @see EDU.purdue.cs.bloat.reflect.FieldInfo
 *
 * @author Nate Nystrom
 *         (<a href="mailto:nystrom@cs.purdue.edu">nystrom@cs.purdue.edu</a>)
 */
public class FieldEditor {
  private ClassEditor editor;
  private FieldInfo fieldInfo;
  private String name;
  private Type type;
  private Object constantValue;
  private boolean isDirty;
  private boolean isDeleted = false;

  /**
   * Creates a new <code>FieldEditor</code> for editing a field in a
   * given class with the given modifiers, type and name
   *
   * @throws IllegalArgumentException
   *         If a field with the desired name already exists in the
   *         class 
   */
  public FieldEditor(ClassEditor editor, int modifiers, Type type,
                     String name) {

    this(editor, modifiers, type, name, null);
  }

  public FieldEditor(ClassEditor editor, int modifiers, Class type,
                     String name, Object constantValue) {
    this(editor, modifiers, Type.getType(type), name, constantValue);
  }

  public FieldEditor(ClassEditor editor, int modifiers, Class type,
                     String name) {
    this(editor, modifiers, Type.getType(type), name, null);
  }

  /**
   * Creates a new <code>FieldEditor</code> for editing a field in a
   * given class with the given modifiers, type, name, and constant
   * value. 
   *
   * @param modifiers
   *        Fields that have a constant value must be
   *        <code>static</code> and <code>final</code>
   *
   * @throws IllegalArgumentException
   *         If a field with the desired name already exists in the
   *         class or if <code>constantValue</code> is non-null and
   *         neither a <code>String</code>, <code>Integer</code>,
   *         <code>Long</code>, <code>Float</code>, nor
   *         <code>Double</code>.
   */
  public FieldEditor(ClassEditor editor, int modifiers, Type type,
                     String name, Object constantValue) {

    // Does the class already have a field with this name?
    FieldInfo[] fields = editor.fields();
    for(int i = 0; i < fields.length; i++) {
      FieldEditor fe = new FieldEditor(editor, fields[i]);
      if(fe.name().equals(name)) {
        String s = "A field named " + name + " already exists in " +
          editor.name(); 
        throw new IllegalArgumentException(s);
      }
    }

    this.editor = editor;

    ConstantPool cp = editor.constants();
    this.name = name;
    this.type = type;

    int typeIndex = cp.getUTF8Index(this.type.descriptor());
    int nameIndex = cp.getUTF8Index(name);

    ClassInfo classInfo = editor.classInfo();

    if(constantValue != null) {
      // Only static final field may have constant values
      if(((modifiers & Modifiers.STATIC) == 0) || 
         ((modifiers & Modifiers.FINAL) == 0)) {
        String s = "Field " + name + 
          " with a constant value must be static and final";
        throw new IllegalArgumentException(s);
      }

      // Create an entry in the constant pool for the constant value
      // of this field
      int valueIndex;
      if(constantValue instanceof String) {
        if(!type.equals(Type.STRING)) {
          String s = "Can't have field type of " + type.className() +
            " with a constant value of \"" + constantValue + "\"";
          throw new IllegalArgumentException(s);
        }
        valueIndex = cp.getStringIndex((String) constantValue);

      } else if(constantValue instanceof Integer) {
        if(!type.equals(Type.INTEGER)) {
          String s = "Can't have field type of " + type.className() +
            " with a constant value of \"" + constantValue + "\"";
          throw new IllegalArgumentException(s);
        }
        valueIndex = cp.getIntegerIndex((Integer) constantValue);

      } else if(constantValue instanceof Long) {
        if(!type.equals(Type.LONG)) {
          String s = "Can't have field type of " + type.className() +
            " with a constant value of \"" + constantValue + "\"";
          throw new IllegalArgumentException(s);
        }
        valueIndex = cp.getLongIndex((Long) constantValue);

      } else if(constantValue instanceof Float) {
        if(!type.equals(Type.FLOAT)) {
          String s = "Can't have field type of " + type.className() +
            " with a constant value of \"" + constantValue + "\"";
          throw new IllegalArgumentException(s);
        }
        valueIndex = cp.getFloatIndex((Float) constantValue);

      } else if(constantValue instanceof Double) {
        if(!type.equals(Type.DOUBLE)) {
          String s = "Can't have field type of " + type.className() +
            " with a constant value of \"" + constantValue + "\"";
          throw new IllegalArgumentException(s);
        }
        valueIndex = cp.getDoubleIndex((Double) constantValue);

      } else {
        String s = "Cannot have a constant value of type " +
          constantValue.getClass().getName();
        throw new IllegalArgumentException(s);
      }

      this.constantValue = constantValue;

      int cvNameIndex = cp.getUTF8Index("ConstantValue");
      this.fieldInfo = 
        classInfo.addNewField(modifiers, typeIndex, nameIndex,
                              cvNameIndex, valueIndex);

    } else {
      this.fieldInfo = 
        classInfo.addNewField(modifiers, typeIndex, nameIndex);
    }

    this.isDirty = true;
  }

  /**
   * Constructor.
   *
   * @param editor
   *        The class containing the field.
   * @param fieldInfo
   *        The field to edit.
   *
   * @see ClassEditor
   * @see FieldInfo
   */
  public FieldEditor(ClassEditor editor, FieldInfo fieldInfo) {
    ConstantPool cp = editor.constants();

    this.fieldInfo = fieldInfo;
    this.editor = editor;

    int index;

    index = fieldInfo.nameIndex();
    name = (String) cp.constantAt(index);

    index = fieldInfo.typeIndex();
    String typeName = (String) cp.constantAt(index);
    type = Type.getType(typeName);

    index = fieldInfo.constantValue();
    constantValue = cp.constantAt(index);
    this.isDirty = false;
  }

  /**
   * Returns the <tt>ClassEditor</tt> used to edit the class in
   * which this field resides.
   */
  public ClassEditor declaringClass() {
    return editor;
  } 

  /**
   * Returns <tt>true</tt> if this field has been modified.
   */
  public boolean isDirty() {
    return(this.isDirty);
  }

  /**
   * Sets the dirty flag of this method.  The dirty flag is
   * <tt>true</tt> if the method has been modified.
   *
   * @throw IllegalStateException
   *        This field has been marked for deletion
   */
  public void setDirty(boolean isDirty) {
    if (this.isDeleted) {
      String s = "Cannot change a field once it has been marked " +
        "for deletion";
      throw new IllegalStateException(s);
    }

    this.isDirty = isDirty;
    if(isDirty == true) {
      this.editor.setDirty(true);
    }
  }

  /**
   * Marks this field for deletion.  Once a field has been marked for
   * deletion all attempts to change it will throw an
   * <code>IllegalStateException</code>. 
   */
  public void delete() {
    this.setDirty(true);
    this.isDeleted = true;
  }

  /**
   * Returns the raw FieldInfo of the field being edited.
   */
  public FieldInfo fieldInfo() {
    return fieldInfo;
  }

  public Object constantValue() {
    return constantValue;
  }

  public boolean isPublic()
  {
    return (fieldInfo.modifiers() & Modifiers.PUBLIC) != 0;
  }

  public boolean isPrivate()
  {
    return (fieldInfo.modifiers() & Modifiers.PRIVATE) != 0;
  }

  public boolean isProtected()
  {
    return (fieldInfo.modifiers() & Modifiers.PROTECTED) != 0;
  }

  /**
   * Returns true, if the field has package level visibility.
   */
  public boolean isPackage() {
    return(!isPublic() && !isPrivate() && !isProtected());
  }

    public boolean isStatic()
  {
    return (fieldInfo.modifiers() & Modifiers.STATIC) != 0;
  }

  public boolean isFinal()
  {
    return (fieldInfo.modifiers() & Modifiers.FINAL) != 0;
  }

  public boolean isVolatile()
  {
    return (fieldInfo.modifiers() & Modifiers.VOLATILE) != 0;
  }

  public boolean isTransient()
  {
    return (fieldInfo.modifiers() & Modifiers.TRANSIENT) != 0;
  }

  /**
   * @throw IllegalStateException
   *        This field has been marked for deletion
   */
  public void setPublic(boolean flag) {
    if (this.isDeleted) {
      String s = "Cannot change a field once it has been marked " +
        "for deletion";
      throw new IllegalStateException(s);
    }

    int modifiers = fieldInfo.modifiers();

    if (flag) {
      modifiers |= Modifiers.PUBLIC;
    }
    else {
      modifiers &= ~Modifiers.PUBLIC;
    }

    fieldInfo.setModifiers(modifiers);
    this.setDirty(true);
  }

  /**
   * @throw IllegalStateException
   *        This field has been marked for deletion
   */
  public void setPrivate(boolean flag) {
    if (this.isDeleted) {
      String s = "Cannot change a field once it has been marked " +
        "for deletion";
      throw new IllegalStateException(s);
    }

    int modifiers = fieldInfo.modifiers();

    if (flag) {
      modifiers |= Modifiers.PRIVATE;
    }
    else {
      modifiers &= ~Modifiers.PRIVATE;
    }

    fieldInfo.setModifiers(modifiers);
    this.setDirty(true);
  }

  /**
   * @throw IllegalStateException
   *        This field has been marked for deletion
   */
  public void setProtected(boolean flag) {
    if (this.isDeleted) {
      String s = "Cannot change a field once it has been marked " +
        "for deletion";
      throw new IllegalStateException(s);
    }

    int modifiers = fieldInfo.modifiers();

    if (flag) {
      modifiers |= Modifiers.PROTECTED;
    }
    else {
      modifiers &= ~Modifiers.PROTECTED;
    }

    fieldInfo.setModifiers(modifiers);
    this.setDirty(true);
  }

  /**
   * @throw IllegalStateException
   *        This field has been marked for deletion
   */
  public void setStatic(boolean flag) {
    if (this.isDeleted) {
      String s = "Cannot change a field once it has been marked " +
        "for deletion";
      throw new IllegalStateException(s);
    }

    int modifiers = fieldInfo.modifiers();

    if (flag) {
      modifiers |= Modifiers.STATIC;
    }
    else {
      modifiers &= ~Modifiers.STATIC;
    }

    fieldInfo.setModifiers(modifiers);
    this.setDirty(true);
  }

  /**
   * @throw IllegalStateException
   *        This field has been marked for deletion 
   */
  public void setFinal(boolean flag) {
    if (this.isDeleted) {
      String s = "Cannot change a field once it has been marked " +
        "for deletion";
      throw new IllegalStateException(s);
    }

    int modifiers = fieldInfo.modifiers();

    if (flag) {
      modifiers |= Modifiers.FINAL;
    }
    else {
      modifiers &= ~Modifiers.FINAL;
    }

    fieldInfo.setModifiers(modifiers);
    this.setDirty(true);
  }

  /**
   * @throw IllegalStateException
   *        This field has been marked for deletion
   */
  public void setTransient(boolean flag) {
    if (this.isDeleted) {
      String s = "Cannot change a field once it has been marked " +
        "for deletion";
      throw new IllegalStateException(s);
    }

    int modifiers = fieldInfo.modifiers();

    if (flag) {
      modifiers |= Modifiers.TRANSIENT;
    }
    else {
      modifiers &= ~Modifiers.TRANSIENT;
    }

    fieldInfo.setModifiers(modifiers);
    this.setDirty(true);
  }

  /**
   * @throw IllegalStateException
   *        This field has been marked for deletion
   */
  public void setVolatile(boolean flag) {
    if (this.isDeleted) {
      String s = "Cannot change a field once it has been marked " +
        "for deletion";
      throw new IllegalStateException(s);
    }

    int modifiers = fieldInfo.modifiers();

    if (flag) {
      modifiers |= Modifiers.VOLATILE;
    }
    else {
      modifiers &= ~Modifiers.VOLATILE;
    }

    fieldInfo.setModifiers(modifiers);
    this.setDirty(true);
  }

  /**
   * Returns the name of the field.
   */
  public String name() {
    return name;
  }

  /**
   * Returns the type of the field.
   */
  public Type type() {
    return type;
  }

  /**
   * Returns a <tt>NameAndType</tt> of the field.
   */
  public NameAndType nameAndType() {
    return(new NameAndType(this.name(), this.type()));
  }

  /**
   * Returns a <code>MemberRef</code> for the field
   */
  public MemberRef memberRef() {
    return(new MemberRef(this.declaringClass().type(),
                         this.nameAndType()));
  }

  /**
   * Commit changes to the field back to the ClassEditor.  Note
   * that the field is committed regardless of whether or not
   * it is dirty.
   */
  public void commit() {
    if (this.isDeleted) {
      // Even if the field is newly-added, we can still delete it
      // without problems because its FieldInfo was already noted with
      // the ClassInfo.

      ConstantPool cp = editor.constants();
      int nameIndex = cp.getUTF8Index(name);
      this.editor.classInfo().deleteField(nameIndex);

    } else {
      ConstantPool cp = editor.constants();

      fieldInfo.setNameIndex(cp.addConstant(Constant.UTF8, name));
      fieldInfo.setTypeIndex(cp.addConstant(Constant.UTF8,
                                          type.descriptor()));

      if (constantValue != null) {
        if (constantValue instanceof Long) {
          fieldInfo.setConstantValue(cp.addConstant(Constant.LONG,
                                                    constantValue));
        } else if (constantValue instanceof Float) {
          fieldInfo.setConstantValue(cp.addConstant(Constant.FLOAT,
                                                    constantValue));
        } else if (constantValue instanceof Double) {
	fieldInfo.setConstantValue(cp.addConstant(Constant.DOUBLE,
                                                  constantValue));
        } else if (constantValue instanceof Integer) {
          fieldInfo.setConstantValue(cp.addConstant(Constant.INTEGER,
                                                    constantValue));
        } else if (constantValue instanceof String) {
          fieldInfo.setConstantValue(cp.addConstant(Constant.STRING,
                                                    constantValue));
        }
      }
    }

    // This field is no longer dirty
    this.isDirty = false;
  }

  /**
   * Print the field.
   *
   * @param out
   *        Stream to which to print.
   */
  public void print(PrintStream out)
  {
    out.println("field " + name + " " + type);
  }

  /**
   * Returns a String that contains the declaring class name and the
   * name of the field
   */
  public String fullName() {
    return declaringClass().name() + "." + this.name();
  }

  public String toString() {
    return("[FieldEditor for " + this.name + this.type + "]");
  }
}
