/**
 * All files in the distribution of BLOAT (Bytecode Level Optimization and
 * Analysis tool for Java(tm)) are Copyright 1997-2001 by the Purdue
 * Research Foundation of Purdue University.  All rights reserved.
 *
 * <p>
 *
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

package EDU.purdue.cs.bloat.context;

import EDU.purdue.cs.bloat.util.*;
import EDU.purdue.cs.bloat.reflect.*;
import EDU.purdue.cs.bloat.file.*;
import EDU.purdue.cs.bloat.editor.*;

import java.io.*;
import java.util.*;

/**
 * Does a lot of the same stuff as <tt>PersistentBloatContext</tt>
 * except that it manages the chaches of BLOAT objects.  For example,
 * when a <tt>MethodEditor</tt> is no longer needed, it is removed
 * from the cache if it is not dirty.  This context is meant to used
 * in volatile memory.
 */
public class CachingBloatContext extends PersistentBloatContext {

  // Keep track of reference counts in a manner reminiscent of the old
  // Editor class.
  protected Map classRC;
  protected Map methodRC;
  protected Map fieldRC;

  /**
   * Constructor.
   *
   * @param loader
   *        Used to load classes
   * @param classes
   *        Some initial classes in the context
   * @param closure
   *        Do we look for the maximum number of classes?
   */
  public CachingBloatContext(ClassInfoLoader loader, 
			     Collection classes, boolean closure) {
    super(loader, closure);

    classRC = new HashMap();
    methodRC = new HashMap();
    fieldRC = new HashMap();

    addClasses(classes);
  }

  public ClassEditor newClass(int modifiers, String className, 
                              Type superType, Type[] interfaces) {

    ClassEditor ce = 
      super.newClass(modifiers, className, superType, interfaces);
    ClassInfo info = ce.classInfo();
    classRC.put(info, new Integer(1));

    return ce;
  }

  public ClassEditor editClass(ClassInfo info) {
    // Check the cache
    ClassEditor ce = (ClassEditor) classEditors.get(info);

    if(ce == null) {
      ce = new ClassEditor(this, info);
      classEditors.put(info, ce);
      classRC.put(info, new Integer(1));

      if(!classInfos.containsValue(info)) {
	String className = ce.name().intern();
	db("editClass(ClassInfo): " + className + " -> " + info);
	classInfos.put(className, info);
      }

    } else {
      Integer rc = (Integer) classRC.get(info);
      classRC.put(info, new Integer(rc.intValue()+1));
    }

    return(ce);
  }

  public MethodEditor editMethod(MemberRef method) 
    throws NoSuchMethodException {

    // Check the MethodInfo cache
    MethodInfo info = (MethodInfo) methodInfos.get(method);

    if(info == null) {
      // Groan, we have to do this the HARD way.
      db("Creating a new MethodEditor for " + method);
      NameAndType nat = method.nameAndType();
      String name = nat.name();
      Type type = nat.type();

      try {
	ClassEditor ce = editClass(method.declaringClass());
	MethodInfo[] methods = ce.methods();

	for(int i = 0; i < methods.length; i++) {
	  MethodEditor me = editMethod(methods[i]);

	  if(me.name().equals(name) && me.type().equals(type)) {
	    // The call to editMethod should have already handled the
	    // methodEditors mapping, but we still need to do
	    // methodInfos.
	    methodInfos.put(method, methods[i]);
	    release(ce.classInfo());
	    return(me);
	  }
	}

	release(ce.classInfo());

      } catch(ClassNotFoundException ex1) {
	throw new NoSuchMethodException(method.toString() + "(" +
					ex1.getMessage() + ")");

      } catch(ClassFormatException ex2) {
	throw new NoSuchMethodException(method.toString() + "(" +
					ex2.getMessage() + ")");

      }

      throw new NoSuchMethodException(method.toString());
    }

    return(editMethod(info));
  }

  public MethodEditor editMethod(MethodInfo info) {
    // Check methodEditors cache
    MethodEditor me = (MethodEditor) methodEditors.get(info);

    if(me == null) {
      ClassInfo classInfo = info.declaringClass();
      me = new MethodEditor(editClass(classInfo), info);
      release(classInfo);

      methodEditors.put(info, me);
      methodRC.put(info, new Integer(1));
      db("Creating a new MethodEditor for " + me.memberRef());

    } else {
      Integer rc = (Integer) methodRC.get(info);
      methodRC.put(info, new Integer(rc.intValue() + 1));
    }

    return(me);
  }

  public FieldEditor editField(MemberRef field) 
    throws NoSuchFieldException {

    // Just like we had to do with methods
    FieldInfo info = (FieldInfo) fieldInfos.get(field);

    if(info == null) {
      NameAndType nat = field.nameAndType();
      String name = nat.name();
      Type type = nat.type();

      try {
	ClassEditor ce = editClass(field.declaringClass());
	FieldInfo[] fields = ce.fields();

	for(int i = 0; i < fields.length; i++) {
	  FieldEditor fe = editField(fields[i]);

	  if(fe.name().equals(name) && fe.type().equals(type)) {
	    fieldInfos.put(field, fields[i]);
	    release(ce.classInfo());
	    return(fe);
	  }

	  release(fields[i]);
	}

	release(ce.classInfo());
      } catch(ClassNotFoundException ex1) {
      } catch(ClassFormatException ex2) {
      }

      throw new NoSuchFieldException(field.toString());
    }

    return(editField(info));
  }

  public FieldEditor editField(FieldInfo info) {
    // Check the cache
    FieldEditor fe = (FieldEditor) fieldEditors.get(info);

    db("Editing " + info);

    if(fe == null) {
      ClassInfo classInfo = info.declaringClass();
      fe = new FieldEditor(editClass(classInfo), info);
      release(classInfo);

      fieldEditors.put(info, fe);
      fieldRC.put(info, new Integer(0));
      db("Creating a new FieldEditor for " + fe.nameAndType());

    } else {
      Integer rc = (Integer) fieldRC.get(info);
      fieldRC.put(info, new Integer(rc.intValue() + 1));

    }

    return(fe);
  }

  public void release(ClassInfo info) {
    Integer rc = (Integer) classRC.get(info);

    if(rc != null && rc.intValue() > 1) {
      // Not done yet;
      classRC.put(info, new Integer(rc.intValue() - 1));
      return;

    }

    ClassEditor ce = (ClassEditor) classEditors.get(info);
    if(ce != null && ce.isDirty()) {
      return;
    }

    // We're done with this class, remove all traces of it
    ce = (ClassEditor) classEditors.remove(info);
    classRC.remove(info);
    classEditors.remove(info);

    Iterator iter = classInfos.keySet().iterator();
    while(iter.hasNext()) {
      String name = (String) iter.next();
      ClassInfo info2 = (ClassInfo) classInfos.get(name);
      if(info2 == info) {
	db("Removing ClassInfo: " + name + " -> " + info2);
	classInfos.remove(name);
	break;
      }
    }

    if(ce != null) {
      // Remove all of the class's fields and methods also
      MethodInfo[] methods = ce.methods();
      for(int i = 0; i < methods.length; i++) {
	release(methods[i]);
      }

      FieldInfo[] fields = ce.fields();
      for(int i = 0; i < fields.length; i++) {
	release(fields[i]);
      }
    }

  }

  public void release(MethodInfo info) {
    Integer rc = (Integer) classRC.get(info);

    if(rc != null && rc.intValue() > 1) {
      methodRC.put(info, new Integer(rc.intValue() - 1));
      return;
    }

    MethodEditor me = (MethodEditor) methodEditors.get(info);

    // We should keep dirty methods around.  My original thought was
    // that if we committed dirty methods when they were released, we
    // risk having MethodEditors editing different versions of the
    // same method.  So, if we don't release dirty methods, we'll only
    // have ONE MethodEditor.
    if(me != null && me.isDirty()) {
      return;
    }

    // We're done with this method, remove all traces of it
    methodRC.remove(info);
    methodEditors.remove(info);

    Iterator iter = methodInfos.keySet().iterator();
    while(iter.hasNext()) {
      MemberRef ref = (MemberRef) iter.next();
      MethodInfo info2 = (MethodInfo) methodInfos.get(ref);
      if(info2 == info) {
	methodInfos.remove(ref);
	break;
      }
    }
  }

  public void release(FieldInfo info) {
    Integer rc = (Integer) fieldRC.get(info);

    db("Releasing " + info);

    if(rc != null && rc.intValue() > 1) {
      fieldRC.put(info, new Integer(rc.intValue() - 1));
      return;
    }

    FieldEditor fe = (FieldEditor) fieldEditors.get(info);
    if(fe != null && fe.isDirty()) {
      return;
    }

    // We're done with this field, remove all traces of it
    fieldRC.remove(info);
    fieldEditors.remove(info);

    Iterator iter = fieldInfos.keySet().iterator();
    while(iter.hasNext()) {
      MemberRef ref = (MemberRef) iter.next();
      FieldInfo info2 = (FieldInfo) fieldInfos.get(ref);
      if(info2 == info) {
	fieldInfos.remove(ref);
	break;
      }
    }
  }

  public void commit(ClassInfo info) {
    super.commit(info);

    classEditors.remove(info);
    classRC.remove(info);
  }

  public void commit(MethodInfo info) {
    super.commit(info);

    methodEditors.remove(info);
    methodRC.remove(info);
  }

  public void commit(FieldInfo info) {
    super.commit(info);

    fieldEditors.remove(info);
    fieldRC.remove(info);
  }

  public void commit() {
    Iterator iter = fieldEditors.values().iterator();
    while(iter.hasNext()) {
      FieldEditor fe = (FieldEditor) iter.next();
      commit(fe.fieldInfo());
    }

    iter = methodEditors.values().iterator();
    while(iter.hasNext()) {
      MethodEditor me = (MethodEditor) iter.next();
      commit(me.methodInfo());
    }

    iter = classEditors.values().iterator();
    while(iter.hasNext()) {
      ClassEditor ce = (ClassEditor) iter.next();
      commit(ce.classInfo());
    }
  }

  /**
   * Return a textual description of all of the caches.  Useful if we
   * run out of memory.
   */
  public String toString() {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw, true);

    pw.println("Context of caches in CachingBloatContext...");

    pw.println("  Class Infos");
    Iterator iter = classInfos.keySet().iterator();
    while(iter.hasNext()) {
      Object key = iter.next();
      pw.println("    " + key + " -> " + classInfos.get(key));
    }

   pw.println("  Class Editors");
    iter = classEditors.keySet().iterator();
    while(iter.hasNext()) {
      Object key = iter.next();
      pw.println("    " + key + " -> " + classEditors.get(key));
    }

    pw.println("  Class RC");
    iter = classRC.keySet().iterator();
    while(iter.hasNext()) {
      Object key = iter.next();
      pw.println("    " + key + " -> " + classRC.get(key));
    }

    pw.println("  Method Infos");
    iter = methodInfos.keySet().iterator();
    while(iter.hasNext()) {
      Object key = iter.next();
      pw.println("    " + key + " -> " + methodInfos.get(key));
    }

    pw.println("  Method Editors");
    iter = methodEditors.keySet().iterator();
    while(iter.hasNext()) {
      Object key = iter.next();
      pw.println("    " + key + " -> " + methodEditors.get(key));
    }

    pw.println("  Method RC");
    iter = methodRC.keySet().iterator();
    while(iter.hasNext()) {
      Object key = iter.next();
      pw.println("    " + key + " -> " + methodRC.get(key));
    }

    pw.println("  Field Infos");
    iter = fieldInfos.keySet().iterator();
    while(iter.hasNext()) {
      Object key = iter.next();
      pw.println("    " + key + " -> " + fieldInfos.get(key));
    }

     pw.println("  Field Editors");
    iter = fieldEditors.keySet().iterator();
    while(iter.hasNext()) {
      Object key = iter.next();
      pw.println("    " + key + " -> " + fieldEditors.get(key));
    }

    pw.println("  Field RC");
    iter = fieldRC.keySet().iterator();
    while(iter.hasNext()) {
      Object key = iter.next();
      pw.println("    " + key + " -> " + fieldRC.get(key));
    }

    return(sw.toString());
  }
}
