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
import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.ssa.*;
import EDU.purdue.cs.bloat.inline.*;

import java.io.*;
import java.util.*;

/**
 * Maintains all BLOAT data structures as if they were meant to reside
 * in a persistent store.  As a result, it keeps every piece of BLOAT
 * data around because it might be needed in the future.  No fancing
 * cache maintainence is performed.  Because we are going for maximum
 * information we take the closure of classes when working with the
 * class hierarchy.  
 */
public class PersistentBloatContext extends BloatContext {

  protected final ClassHierarchy hierarchy;

  protected Map classInfos;    // Maps Strings to ClassInfos
  protected Map methodInfos;   // Maps MemberRefs to MethodInfos
  protected Map fieldInfos;    // Maps MemberRefs to FieldInfos

  protected Map classEditors;  // Maps ClassInfos to ClassEditors
  protected Map methodEditors; // Maps MethodInfos to MethodEditors
  protected Map fieldEditors;  // Maps MethodInfos to FieldEditors

  public static boolean DB_COMMIT = false;

  protected static void comm(String s) {
    if(DB_COMMIT || DEBUG) {
      System.out.println(s);
    }
  }

  /**
   * Constructor.  Each <tt>BloatContext</tt> stems from a
   * <tt>ClassInfoLoader</tt>.  Using the loader it can create an
   * <tt>Editor</tt> and such. Initially, no classes are loaded.
   */
  public PersistentBloatContext(ClassInfoLoader loader) {
   this(loader, true);
  }

  /**
   * Constructor.  It is the responsibility of the subclasses to add
   * classes to the hierarchy by calling <tt>addClasses</tt>.
   *
   * @param loader
   *        Used to load classes
   * @param closure
   *        Do we look for the maximum number of classes?
   */
  protected PersistentBloatContext(ClassInfoLoader loader, 
				   boolean closure) {
    super(loader);
    db("Creating a new BloatContext");

    // Create a bunch of the mappings we maintain.  Make sure to do
    // this before anything else!
    classInfos = new HashMap();
    methodInfos = new HashMap();
    fieldInfos = new HashMap();

    classEditors = new HashMap();
    methodEditors = new HashMap();
    fieldEditors = new HashMap();

    // Have to create an empty class hierarchy then add the classes.
    // There is a strange circular dependence between the hierarchy
    // and the context.
    this.hierarchy = new ClassHierarchy(this, new ArrayList(),
					closure);

  }

  /**
   * Adds a bunch of (names of) classes to the hierarchy.
   */
  protected void addClasses(Collection classes) {
    Iterator iter = classes.iterator();
    while(iter.hasNext()) {
      String className = (String) iter.next();
      this.hierarchy.addClassNamed(className);
    }
  }

  public ClassInfo loadClass(String className) 
    throws ClassNotFoundException {
    // Lots of interesting stuff to do here.  For the moment, just
    // load the class from the ClassInfoLoader and add it to the
    // hierarchy.

    className = className.replace('.', '/').intern();

    // Check the cache of ClassInfos
    ClassInfo info = (ClassInfo) classInfos.get(className);

    if(info == null) {
      db("BloatContext: Loading class " + className);
      info = loader.loadClass(className);
      hierarchy.addClassNamed(className);
      db("loadClass: " + className + " -> " + info);
      classInfos.put(className, info);
    }

    return(info);
  }

  public ClassInfo newClassInfo(int modifiers, int classIndex, 
                                int superClassIndex, 
                                int[] interfaceIndexes, 
                                List constants) {

    return this.loader.newClass(modifiers, classIndex, superClassIndex,
                                interfaceIndexes, constants);
  }

  public ClassHierarchy getHierarchy() {
    return(this.hierarchy);
  }

  public ClassEditor newClass(int modifiers, String className, 
                              Type superType, Type[] interfaces) {

    ClassEditor ce = 
      new ClassEditor(this, modifiers, className, superType,
                      interfaces);
    ClassInfo info = ce.classInfo();

    className = ce.name().intern();

    db("editClass(ClassInfo): " + className + " -> " + info);

    classInfos.put(className, info);
    classEditors.put(info, ce);

    return ce;
  }


  public ClassEditor editClass(String className)
    throws ClassNotFoundException, ClassFormatException 
  {
    // Only make the name -> classInfo mapping if we edit the class,
    // this way the mapping will be deleted when the ClassEditor is
    // released.

    className = className.intern();

    ClassInfo info = (ClassInfo) classInfos.get(className);

    if(info == null) {
      info = loadClass(className);
      //      db("editClass(String): " + className + " -> " + info);
      //      classInfos.put(className, info);
    }

    return(editClass(info));
  }

  public ClassEditor editClass(Type classType) 
    throws ClassNotFoundException, ClassFormatException
  {
    return(editClass(classType.className()));
  }

  public ClassEditor editClass(ClassInfo info) {
    // Check the cache
    ClassEditor ce = (ClassEditor) classEditors.get(info);

    if(ce == null) {
      ce = new ClassEditor(this, info);
      classEditors.put(info, ce);

      if(!classInfos.containsValue(info)) {
	String className = ce.name().intern();
	db("editClass(ClassInfo): " + className + " -> " + info);
	classInfos.put(className, info);
      }
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

	  release(methods[i]);
	}

      } catch(ClassNotFoundException ex1) {
      } catch(ClassFormatException ex2) {
      }

      throw new NoSuchMethodException(method.toString());
    }

    return(editMethod(info));
  }

  public MethodEditor editMethod(MethodInfo info) {
    // Check methodEditors cache
    MethodEditor me = (MethodEditor) methodEditors.get(info);

    if(me == null) {
      me = new MethodEditor(editClass(info.declaringClass()), info);
      methodEditors.put(info, me);
      db("Creating a new MethodEditor for " + me.memberRef());
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

    if(fe == null) {
      fe = new FieldEditor(editClass(info.declaringClass()), info);
      fieldEditors.put(info, fe);
      db("Creating a new FieldEditor for " + fe.nameAndType());
    }

    return(fe);
  }

  public void release(ClassInfo info) {
    // Since we keep around all data, do nothing
  }

  public void release(ClassEditor ce) {
    // Since we keep around all data, do nothing
  }

  public void release(MethodInfo info) {
    // Since we keep around all data, do nothing
  }

  public void release(FieldInfo info) {
    // Since we keep around all data, do nothing
  }

  /**
   * Classes that are ignored are not committed.
   *
   * @see ignoreClass
   */
  public void commit(ClassInfo info) {
    Type type = Type.getType("L" + info.name() + ";");
    if(ignoreClass(type)) {
      return;
    }

    ClassEditor ce = editClass(info);

    // Commit all of the class's methods and fields
    MethodInfo[] methods = ce.methods();
    for(int i = 0; i < methods.length; i++) {
      commit(methods[i]);
    }

    FieldInfo[] fields = ce.fields();
    for(int i = 0; i < fields.length; i++) {
      commit(fields[i]);
    }

    ce.commit();

    ce.setDirty(false);
    release(info);
  }

  public void commit(MethodInfo info) {
    MethodEditor me = editMethod(info);
    me.commit();

    // We make the method's class dirty so it, too, will be committed
    me.declaringClass().setDirty(true);
    me.setDirty(false);
    release(info);
  }

  public void commit(FieldInfo info) {
    FieldEditor fe = editField(info);
    fe.commit();

    // We make the method's class dirty so it, too, will be committed
    fe.declaringClass().setDirty(true);
    fe.setDirty(false);
    release(info);
  }

  public void commit() {
    Object[] array = fieldEditors.values().toArray();
    for(int i = 0; i < array.length; i++) {
      FieldEditor fe = (FieldEditor) array[i];
      if(!ignoreField(fe.memberRef())) {
	commit(fe.fieldInfo());
      }
    }

    array = methodEditors.values().toArray();
    for(int i = 0; i < array.length; i++) {
      MethodEditor me = (MethodEditor) array[i];
      if(!ignoreMethod(me.memberRef())) {
	commit(me.methodInfo());
      }
    }

    array = classEditors.values().toArray();
    for(int i = 0; i < array.length; i++) {
      ClassEditor ce = (ClassEditor) array[i];
      if(!ignoreClass(ce.type())) {
	commit(ce.classInfo());
      }
    }
  }

  public void commitDirty() {
    comm("Committing dirty data");

    // Commit all dirty fields
    Object[] array = this.fieldEditors.values().toArray();
    for(int i = 0; i < array.length; i++) {
      FieldEditor fe = (FieldEditor) array[i];
      if(fe.isDirty() && !ignoreField(fe.memberRef())) {
        comm("  Committing field: " + fe.declaringClass().name() +
	     "." + fe.name());
        commit(fe.fieldInfo());
      }
    }

    // Commit all dirty methods
    array = this.methodEditors.values().toArray();
    for(int i = 0; i < array.length; i++) {
      MethodEditor me = (MethodEditor) array[i];
      if(me.isDirty() && !ignoreMethod(me.memberRef())) {
        comm("  Committing method: " + me.declaringClass().name() +
           "." + me.name() + me.type());
        commit(me.methodInfo());
      }
    }

    // Commit all dirty classes
    array = this.classEditors.values().toArray();
    for(int i = 0; i < array.length; i++) {
      ClassEditor ce = (ClassEditor) array[i];
      if(ce.isDirty() && !ignoreClass(ce.type())) {
        comm("  Committing class: " + ce.name());
        commit(ce.classInfo());
      }
    }
  }
}
