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
import java.lang.reflect.Modifier;
import java.io.*;
import java.security.*;
import java.util.*;

/**
 * <P>This class computes the serial version UID of a class modeled by
 * a <code>ClassEditor</code>.  Otherwise, we would have to load the
 * class in order to compute its serial version UID.  That would
 * suck.</P>
 *
 * <P>The algorithm for computing the serial version UID can be found
 * in the <A
 * href="http://java.sun.com/j2se/1.3/docs/guide/serialization/spec">serialization spec</A></P>
 */
public class SerialVersionUID {

  /**
   * Returns <code>true</code> if the class modeled by the given
   * <code>ClassEditor</code> implements {@link java.io.Serializable
   * Serializable}.  It checks superclasses.
   */
  public static boolean implementsSerializable(ClassEditor ce) {
    if(ce.type().equals(Type.OBJECT)) {
      // Stop the recursion!
      return(false);
    }

    Type serializable = Type.getType("Ljava/io/Serializable;");
    Type[] interfaces = ce.interfaces();
    for(int i = 0; i < interfaces.length; i++) {
      if(interfaces[i].equals(serializable)) {
        return(true);
      }
    }

    // Does its superclass implement Serializable?

    Type superclass = ce.superclass();
    ClassInfoLoader loader = ce.classInfo().loader();
    try {
      ClassInfo ci = loader.loadClass(superclass.className());
      ClassEditor sce = new ClassEditor(ce.context(), ci);
      return(implementsSerializable(sce));

    } catch(ClassNotFoundException ex) {
      System.err.println("Could not load class: " + superclass +
                         ", superclass of " + ce.name());
      System.exit(1);
    }
    return(false);
  }

  /**
   * Returns the serial version UID of the class modeled by the given
   * <code>ClassEditor</code>. 
   *
   * @param ce
   *        The class must implement {@link java.io.Serializable
   *        Serializable} 
   */
  public static long serialVersionUID(ClassEditor ce) {
    // Make sure the class implements Serializable
    if(!implementsSerializable(ce)) {
      String s = "Class " + ce.name() + 
        " does not implement java.io.Serializable";
      throw new IllegalArgumentException(s);
    }

    // If the class already has a serialVersionUID, return that
    FieldInfo[] fields = ce.fields();
    for(int i = 0; i < fields.length; i++) {
      FieldEditor fe = new FieldEditor(ce, fields[i]);
      if(fe.name().equals("serialVersionUID")) {
        Object value = fe.constantValue();
        if(value != null) {
          if(value instanceof Long) {
            return(((Long) value).longValue());
          }
        }
      }
    }

    // Now, compute the digest of the bytes using SHA
    MessageDigest algorithm = null;
    try {
      algorithm = MessageDigest.getInstance("SHA");

    } catch(NoSuchAlgorithmException ex) {
      String s = "Can't use SHA-1 message digest algorith!";
      throw new IllegalArgumentException(s);
    }

    DataOutputStream dos = 
      new DataOutputStream(new DigestOutputStream(new
        ByteArrayOutputStream(), algorithm)) ;

    try {
      // Write a bunch of information about the class to the output
      // stream
      writeClassName(ce, dos);
      writeClassModifiers(ce, dos);
      writeInterfaceNames(ce, dos);
      writeFields(ce, dos);
      writeStaticInitializer(ce, dos);
      writeConstructors(ce, dos);
      writeMethods(ce, dos);

      dos.flush();
      dos.close();

    } catch(IOException ex) {
      String s = ("While computing serial version UID: " + ex);
      throw new IllegalArgumentException(s);
    }

    // Compute the hash value from the first 64 bites of the digest
    byte[] digest = algorithm.digest();
    long uid = 0;
    for (int i = 0; i < Math.min(8, digest.length); i++) {
      uid += (long)(digest[i] & 255) << (i * 8);
    }
    return(uid);

  }

  /**
   * Writes the name of the class to the data output stream
   */
  private static void writeClassName(ClassEditor ce, 
                                     DataOutputStream dos) 
    throws IOException {
    dos.writeUTF(ce.name().replace('/', '.'));
  }

  /**
   * Returns the Java reflection modifiers for a given class
   */
  static int getModifiers(ClassEditor ce) {
    // Translate BLOAT's class modifiers into Java's reflection
    // modifiers 
    int modifiers = 0;
    
    if(ce.isPublic()) {
      modifiers |= Modifier.PUBLIC;
    } 

    if(ce.isPrivate()) {
      modifiers |= Modifier.PRIVATE;
    } 

    if(ce.isProtected()) {
      modifiers |= Modifier.PROTECTED;
    }

    if(ce.isStatic()) {
      modifiers |= Modifier.STATIC;
    }

    if(ce.isFinal()) {
      modifiers |= Modifier.FINAL;
    }

    if(ce.isAbstract()) {
      modifiers |= Modifier.ABSTRACT;
    }

    if(ce.isInterface()) {
      modifiers |= Modifier.INTERFACE;
    }

    return(modifiers);
  }

  /**
   * Writes the class's modifiers to the output stream
   */
  private static void writeClassModifiers(ClassEditor ce,
                                          DataOutputStream dos) 
  throws IOException {
    dos.writeInt(getModifiers(ce));
  }

  /**
   * Writes the names of the interfaces implemented by the class to
   * the output stream
   */
  private static void writeInterfaceNames(ClassEditor ce,
                                          DataOutputStream dos) 
    throws IOException {

    // Sort interfaces by name
    SortedSet sorted = new TreeSet();

    Type[] interfaces = ce.interfaces();
    for(int i = 0; i < interfaces.length; i++) {
      sorted.add(interfaces[i].className().replace('/', '.'));
    }

    Iterator iter = sorted.iterator();
    while(iter.hasNext()) {
      String name = (String) iter.next();
      dos.writeUTF(name);
    }

  }

  /**
   * Returns the Java reflection modifiers for a field
   */
  static int getModifiers(FieldEditor fe) {
    int modifiers = 0;

    if(fe.isPublic()) {
      modifiers |= Modifier.PUBLIC;
    }

    if(fe.isPrivate()) {
      modifiers |= Modifier.PRIVATE;
    }

    if(fe.isProtected()) {
      modifiers |= Modifier.PROTECTED;
    }

    if(fe.isPackage()) {
      // Nothing
    }

    if(fe.isStatic()) {
      modifiers |= Modifier.STATIC;
    }

    if(fe.isFinal()) {
      modifiers |= Modifier.FINAL;
    }

    if(fe.isVolatile()) {
      modifiers |= Modifier.VOLATILE;
    }

    if(fe.isTransient()) {
      // Kind of a moot point
      modifiers |= Modifier.TRANSIENT;
    }

    return(modifiers);
  }

  /**
   * Writes information about the class's fields to the output stream
   */
  private static void writeFields(ClassEditor ce, 
                                  DataOutputStream dos) 
    throws IOException {

    // Sort the fields by their names
    SortedSet sorted = new TreeSet(new Comparator() {
        public int compare(Object o1, Object o2) {
          FieldEditor fe1 = (FieldEditor) o1;
          FieldEditor fe2 = (FieldEditor) o2;
          return(fe1.name().compareTo(fe2.name()));
        }
      });

    FieldInfo[] infos = ce.fields();
    for(int i = 0; i < infos.length; i++) {
      FieldEditor fe = new FieldEditor(ce, infos[i]);
      // Ignore private static and private transient fields
      if(fe.isPrivate() && fe.isStatic()) {
        break;

      } else if(fe.isPrivate() && fe.isTransient()) {
        break;

      } else {
        sorted.add(fe);
      }
    }

    Iterator iter = sorted.iterator();
    while(iter.hasNext()) {
      FieldEditor fe = (FieldEditor) iter.next();
      dos.writeUTF(fe.name());
      dos.writeInt(getModifiers(fe));
      dos.writeUTF(fe.type().descriptor());
    }
  }

  /**
   * Returns the Java reflection descriptors for a method
   */
  static int getModifiers(MethodEditor me) {
    int modifiers = 0;

    if(me.isPublic()) {
      modifiers |= Modifier.PUBLIC;
    }

    if(me.isPrivate()) {
      modifiers |= Modifier.PRIVATE;
    }

    if(me.isProtected()) {
      modifiers |= Modifier.PROTECTED;
    }

    if(me.isPackage()) {
      // Nothing
    }

    if(me.isStatic()) {
      modifiers |= Modifier.STATIC;
    }

    if(me.isFinal()) {
      modifiers |= Modifier.FINAL;
    }

    if(me.isSynchronized()) {
      modifiers |= Modifier.SYNCHRONIZED;
    }

    if(me.isNative()) {
      modifiers |= Modifier.NATIVE;
    }

    if(me.isAbstract()) {
      modifiers |= Modifier.ABSTRACT;
    }

    if(me.isInterface()) {
      modifiers |= Modifier.INTERFACE;
    }

    return(modifiers);
  }

  /**
   * Writes information about the classes static initializer if it has
   * one
   */
  private static void writeStaticInitializer(ClassEditor ce, 
                                              DataOutputStream dos) 
    throws IOException {
    
    MethodEditor clinit = null;

    MethodInfo[] methods = ce.methods();
    for(int i = 0; i < methods.length; i++) {
      MethodEditor me = new MethodEditor(ce, methods[i]);
      if(me.name().equals("<clinit>")) {
        clinit = me;
        break;
      }
    }

    if(clinit != null) {
      dos.writeUTF("<clinit>");
      dos.writeInt(Modifier.STATIC);
      dos.writeUTF("()V");
    }
  }

  /**
   * Writes information about the class's constructors 
   */
  private static void writeConstructors(ClassEditor ce,
                                        DataOutputStream dos) 
    throws IOException {
    
    // Sort constructors by their signatures
    SortedSet sorted = new TreeSet(new Comparator() {
        public int compare(Object o1, Object o2) {
          MethodEditor me1 = (MethodEditor) o1;
          MethodEditor me2 = (MethodEditor) o2;
          return(me1.type().descriptor().compareTo(me2.type().descriptor()));
        }
      });

    MethodInfo[] methods = ce.methods();
    for(int i = 0; i < methods.length; i++) {
      MethodEditor me = new MethodEditor(ce, methods[i]);
      if(me.name().equals("<init>")) {
        if(!me.isPrivate()) {
          // Ignore private constructors
          sorted.add(me);
        }
      }
    }

    Iterator iter = sorted.iterator();
    while(iter.hasNext()) {
      MethodEditor init = (MethodEditor) iter.next();
      dos.writeUTF("<init>");
      dos.writeInt(getModifiers(init));
      dos.writeUTF(init.type().descriptor());
    }
  }

  /**
   * Write information about the class's methods
   */ 
  private static void writeMethods(ClassEditor ce, 
                                   DataOutputStream dos)
    throws IOException {

    // Sort constructors by their names and signatures
    SortedSet sorted = new TreeSet(new Comparator() {
        public int compare(Object o1, Object o2) {
          MethodEditor me1 = (MethodEditor) o1;
          MethodEditor me2 = (MethodEditor) o2;

          String d1 = me1.name() + me1.type().descriptor();
          String d2 = me2.name() + me2.type().descriptor();
          return(d1.compareTo(d2));
        }
      });

    MethodInfo[] methods = ce.methods();
    for(int i = 0; i < methods.length; i++) {
      MethodEditor me = new MethodEditor(ce, methods[i]);
      if(!me.isPrivate() && !me.isConstructor() && 
         !me.name().equals("<clinit>")) {
        // Ignore private methods
        sorted.add(me);
      }
    }

    Iterator iter = sorted.iterator();
    while(iter.hasNext()) {
      MethodEditor me = (MethodEditor) iter.next();
      dos.writeUTF(me.name());
      dos.writeInt(getModifiers(me));
      dos.writeUTF(me.type().descriptor());
    }    

  }

}
