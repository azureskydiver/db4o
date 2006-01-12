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

package EDU.purdue.cs.bloat.decorate;

import EDU.purdue.cs.bloat.reflect.*;
import EDU.purdue.cs.bloat.file.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.tbaa.*;
import EDU.purdue.cs.bloat.trans.*;
import EDU.purdue.cs.bloat.util.*;
import EDU.purdue.cs.bloat.context.*;

import java.io.*;
import java.util.*;
import java.util.zip.*;

/**
 * Inserts residency, update, or swizzle checks into the methods of the
 * classes specified on the command line.
 *
 * Usage: java EDU.purdue.cs.bloat.decorate.Main
 *            [-options] classes output_dir
 *
 * where options include:
 *     -help             print out this message
 *     -v -verbose       turn on verbose mode 
 *                       (can be given multiple times)
 *     -classpath <directories separated by colons
 *                       list directories in which to look for classes
 *     -f                decorate files even if up-to-date
 *     -closure          recursively decorate referenced classes
 *     -relax-loading    don't report errors if a class is not found
 *     -skip <class|package.*>
 *                       skip the given class or package
 *                       (this option can be given more than once)
 *     -only <class|package.*>
 *                       skip all but the given class or package
 *                       (this option can be given more than once)
 *     -rc               insert residency checks (default)
 *     -norc             don't insert residency checks
 *     -uc               insert update checks (default)
 *     -sc               insert array swizzle checks (default)
 *     -nosc             don't insert array swizzle checkso
 *
 */
public class Main implements Opcode
{
  private static int VERBOSE = 0;          // The level of verbosity
  private static boolean FORCE = false;
  private static boolean CLOSURE = false;
  private static boolean RC = true;        // Insert residency checks?
  private static boolean UC = true;        // Insert update checks?
  private static boolean SC = true;        // Insert swizzle checks?
  private static List SKIP = new ArrayList();
  private static List ONLY = new ArrayList();
  private static final int NONE    = 0;
  private static final int POINTER = 1;
  private static final int SCALAR  = 2;

  /**
   * Verbose debugging
   */
  private static void vb(int level, String s) {
    if(VERBOSE > level) {
      System.out.println(s);
    }
  }

  /**
   * Parse the command line.  Inserts residency, update, and swizzle checks
   * into the bytecode of the methods of the specified classes.
   */
  public static void main(String[] args) {
    ClassFileLoader loader = new ClassFileLoader();
    List classes = new ArrayList();     // Names of classes from command line
    boolean gotdir = false;             // Did user specify an output dir?

    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-v") || args[i].equals("-verbose")) {
	VERBOSE++;
      }
      else if (args[i].equals("-help")) {
	usage();
      }
      else if (args[i].equals("-classpath")) {
	if (++i >= args.length) {
	  usage();
	}

	String classpath = args[i];
	loader.setClassPath(classpath);
      }
      else if (args[i].equals("-skip")) {
	if (++i >= args.length) {
	  usage();
	}

	String pkg = args[i].replace('.', '/');
	SKIP.add(pkg);
      }
      else if (args[i].equals("-only")) {
	if (++i >= args.length) {
	  usage();
	}

	String pkg = args[i].replace('.', '/');
	ONLY.add(pkg);
      }
      else if (args[i].equals("-closure")) {
	CLOSURE = true;
      }
      else if (args[i].equals("-relax-loading")) {
	ClassHierarchy.RELAX = true;
      }
      else if (args[i].equals("-f")) {
	FORCE = true;
      }
      else if (args[i].equals("-norc")) {
	RC = false;
      }
      else if (args[i].equals("-rc")) {
	RC = true;
      }
      else if (args[i].equals("-nouc")) {
	UC = false;
      }
      else if (args[i].equals("-uc")) {
	UC = true;
      }
      else if (args[i].equals("-nosc")) {
	SC = false;
      }
      else if (args[i].equals("-sc")) {
	SC = true;
      }
      else if (args[i].startsWith("-")) {
	usage();
      }
      else if (i == args.length-1) {
	// Last argument is the name of the outpu directory
	File f = new File(args[i]);

	if (f.exists() && ! f.isDirectory()) {
	  System.err.println("No such directory: " + f.getPath());
	  System.exit(2);
	}

	loader.setOutputDir(f);
	gotdir = true;
      }
      else {
	classes.add(args[i]);
      }
    }

    if (! gotdir) {
      usage();
    }

    if (classes.size() == 0) {
      usage();
    }

    if (VERBOSE > 3) {
      ClassFileLoader.DEBUG = true;
      ClassEditor.DEBUG = true;
    }

    boolean errors = false;

    Iterator iter = classes.iterator();

    // Load each class specified on the command line
    while (iter.hasNext()) {
      String name = (String) iter.next();

      try {
	loader.loadClass(name);
      }
      catch (ClassNotFoundException ex) {
	System.err.println("Couldn't find class: " + ex.getMessage());
	errors = true;
      }
    }

    if (errors) {
      System.exit(1);
    }

    BloatContext context = new CachingBloatContext(loader, classes, CLOSURE);

    if (! CLOSURE) {
      Iterator e = classes.iterator();

      while (e.hasNext()) {
	String name = (String) e.next();
	try {
	  ClassInfo info = loader.loadClass(name);
	  decorateClass(context, info);
	}
	catch (ClassNotFoundException ex) {
	  System.err.println("Couldn't find class: " +
			     ex.getMessage());
	  System.exit(1);
	}
      }
    }
    else {
      classes = null;

      ClassHierarchy hier = context.getHierarchy();

      Iterator e = hier.classes().iterator();

      while (e.hasNext()) {
	Type t = (Type) e.next();

	if (t.isObject()) {
	  try {
	    ClassInfo info = loader.loadClass(t.className());
	    decorateClass(context, info);
	  }
	  catch (ClassNotFoundException ex) {
	    System.err.println("Couldn't find class: " +
			       ex.getMessage());
	    System.exit(1);
	  }
	}
      }
    }
  }

  private static void usage() {
    System.err.println(
		       "Usage: java EDU.purdue.cs.bloat.decorate.Main " +
		       "\n            [-options] classes output_dir" +
		       "\n" +
		       "\nwhere options include:" +
		       "\n    -help             print out this message" +
		       "\n    -v -verbose       turn on verbose mode " +
		       "(can be given multiple times)" +
		       "\n    -classpath <directories separated by colons>" +
		       "\n                      list directories in which to look for classes"+
		       "\n    -f                decorate files even if up-to-date" +
		       "\n    -closure          recursively decorate referenced classes" +
		       "\n    -relax-loading    don't report errors if a class is not found" +
		       "\n    -skip <class|package.*>" +
		       "\n                      skip the given class or package" +
		       "\n                      (this option can be given more than once)" +
		       "\n    -only <class|package.*>" +
		       "\n                      skip all but the given class or package" +
		       "\n                      (this option can be given more than once)" +
		       "\n    -rc               insert residency checks (default)" +
		       "\n    -norc             don't insert residency checks" +
		       "\n    -uc               insert update checks (default)" +
		       "\n    -sc               insert array swizzle checks (default)" +
		       "\n    -nosc             don't insert array swizzle checks");
    System.exit(0);
  }

  /**
   * Adds residency/update/swizzle checks to all of the methods in a
   * given class.
   *
   * @param context
   *        Information about all the classes we're dealing with
   * @param info
   *        Information about the class we're decorating
   */
  private static void decorateClass(EditorContext context, ClassInfo info) {
    ClassFile classFile = (ClassFile) info;

    // Check to see if the class file is up-to-date
    if (! FORCE) {
      File source = classFile.file();
      File target = classFile.outputFile();

      if (source != null && target != null &&
	  source.exists() && target.exists() &&
	  source.lastModified() < target.lastModified()) {

	if (VERBOSE > 1) {
	  System.out.println(classFile.name() + " is up to date");
	}

	return;
      }
    }

    if (VERBOSE > 2) {
      classFile.print(System.out);
    }

    ClassEditor c = context.editClass(info);

    boolean skip = false;

    String name = c.type().className();
    String qual = c.type().qualifier() + "/*";

    // Edit only classes explicitly mentioned.
    if (ONLY.size() > 0) {
      skip = true;

      // Only edit classes we explicitly don't name.
      for (int i = 0; i < ONLY.size(); i++) {
	String pkg = (String) ONLY.get(i);

	if (name.equals(pkg) || qual.equals(pkg)) {
	  skip = false;
	  break;
	}
      }
    }

    // Don't edit classes we explicitly skip.
    if (! skip) {
      for (int i = 0; i < SKIP.size(); i++) {
	String pkg = (String) SKIP.get(i);

	if (name.equals(pkg) || qual.equals(pkg)) {
	  skip = true;
	  break;
	}
      }
    }

    if (skip) {
      if (VERBOSE > 0) {
	System.out.println("Skipping " + c.type().className());
      }

      context.release(info);
      return;
    }

    if (VERBOSE > 0) {
      System.out.println("Decorating class " + c.type().className());
    }

    if (VERBOSE > 2) {
      ((ClassFile) info).print(System.out);
    }

    MethodInfo[] methods = c.methods();

    // Add residency checks (via transform()) to each method in the class
    for (int j = 0; j < methods.length; j++) {
      MethodEditor m;

      try {
	m = context.editMethod(methods[j]);
      }
      catch (ClassFormatException ex) {
	System.err.println(ex.getMessage());
	continue;
      }

      transform(m);
      context.commit(methods[j]);
    }

    context.commit(info);
  }

  /**
   * Inserts residency/update/swizzle checks into a method.  Iterates
   * over the bytecodes in the method and inserts the appropriate
   * residency opcode.
   *
   * @param method
   *        The method to which to add checks.
   *
   * @see MethodEditor#code
   */
  private static void transform(MethodEditor method) {
    if (VERBOSE > 1) {
      System.out.println("Decorating method " + method);
    }

    // Optimize initialization of arrays to speed things up.
    CompactArrayInitializer.transform(method);

    ListIterator iter = method.code().listIterator(); 

    // Go through the code (Instructions and Labels) in the method
  INST:
    while (iter.hasNext()) {
      Object ce = iter.next();

      if (VERBOSE > 2) {
	System.out.println("Examining " + ce);
      }

      if (ce instanceof Instruction) {
	Instruction inst = (Instruction) ce;

	int uctype = NONE;     // Type of update check (POINTER or SCALAR)
	boolean insert_sc = false;  // Insert swizzle check (aaload only)?

	int opc = inst.opcodeClass();
	int depth;

	switch (opc) {
	case opcx_arraylength:
	case opcx_athrow:
	case opcx_getfield:
	case opcx_instanceof: {
	  depth = 0;
	  break;
	}
	case opcx_iaload:
	case opcx_laload:
	case opcx_faload:
	case opcx_daload:
	case opcx_baload:
	case opcx_caload:
	case opcx_saload: {
	  depth = 1;
	  break;
	}
	case opcx_aaload: {
	  depth = 1;
	  insert_sc = true;
	  break;
	}
	case opcx_iastore:
	case opcx_fastore:
	case opcx_aastore:
	case opcx_bastore:
	case opcx_castore:
	case opcx_sastore: {
	  depth = 2;
	  break;
	}
	case opcx_lastore:
	case opcx_dastore: {
	  depth = 3;
	  break;
	}
	case opcx_putfield: {
	  MemberRef ref = (MemberRef) inst.operand();
	  depth = ref.type().stackHeight();
	  if (ref.type().isReference()) {
	    uctype = POINTER;
	  }
	  else {
	    uctype = SCALAR;
	  }
	  break;
	}
	case opcx_invokevirtual:
	case opcx_invokespecial:
	case opcx_invokeinterface: {
	  MemberRef ref = (MemberRef) inst.operand();
	  depth = ref.type().stackHeight();
	  break;
	}
	case opcx_rc: {
	  // Skip any existing residency checks.
	  iter.remove();
	  continue INST;
	}
	case opcx_aupdate: {
	  // Skip any existing update checks.
	  iter.remove();
	  continue INST;
	}
	case opcx_supdate: {
	  // Skip any existing update checks.
	  iter.remove();
	  continue INST;
	}
	default: {
	  continue INST;
	}
	}

	Instruction addInst;

	// Insert a residency check...
	if (RC) {
	  Object t;

	  ////////////////////////////////////
	  // Before...
	  // +-----+------+-----------+
	  // | ... | inst | afterInst |
	  // +-----+------+-----------+
	  //         ^prev  ^next
	  //
	  // After...
	  // +-----+----+------+-----------+
	  // | ... | RC | inst | afterInst |
	  // +-----+----+------+-----------+
	  //              ^prev   ^next
	  ////////////////////////////////////

	  // +-----+------+-----------+
	  // | ... | inst | afterInst |
	  // +-----+------+-----------+
	  //         ^prev  ^next

	  t = iter.previous();
	  Assert.isTrue(t == inst, t + " != " + inst);

	  // +-----+------+-----------+
	  // | ... | inst | afterInst |
	  // +-----+------+-----------+
	  //  ^prev  ^next

	  addInst = new Instruction(opcx_rc, new Integer(depth));
	  iter.add(addInst);

	  // +-----+----+------+-----------+
	  // | ... | RC | inst | afterInst |
	  // +-----+----+------+-----------+
	  //        ^prev  ^next

	  t = iter.previous();
	  Assert.isTrue(t == addInst, t + " != " + addInst);

	  // +-----+----+------+-----------+
	  // | ... | RC | inst | afterInst |
	  // +-----+----+------+-----------+
	  //  ^prev  ^next

	  t = iter.next();
	  Assert.isTrue(t == addInst, t + " != " + addInst);
	  
	  // +-----+----+------+-----------+
	  // | ... | RC | inst | afterInst |
	  // +-----+----+------+-----------+
	  //        ^prev  ^next

	  t = iter.next();
	  Assert.isTrue(t == inst, t + " != " + inst);

	  // +-----+----+------+-----------+
	  // | ... | RC | inst | afterInst |
	  // +-----+----+------+-----------+
	  //             ^prev  ^next

	  if (VERBOSE > 2) {
	    System.out.println("Inserting " + addInst +
			       " before " + inst);
	  }
	}
	else {
	  if (VERBOSE > 2) {
	    System.out.println("Not inserting rc before " + inst);
	  }
	}

	// Insert a swizzle check...
	if (insert_sc) {
	  if (SC) {		  
	    Object t;

	    //////////////////////////////////////////////
	    // Before...
	    // +-----+------+-----------+
	    // | ... | inst | afterInst |
	    // +-----+------+-----------+
	    //         ^prev  ^next
	    //
	    // After...
	    // +-----+------+----------+------+-----------+
	    // | ... | dup2 | aswizzle | inst | afterInst |
	    // +-----+------+----------+------+-----------+
	    //                          ^prev   ^next
	    ///////////////////////////////////////////////

	    // +-----+------+-----------+
	    // | ... | inst | afterInst |
	    // +-----+------+-----------+
	    //         ^prev  ^next
		
	    t = iter.previous();
	    Assert.isTrue(t == inst, t + " != " + inst);

	    // +-----+------+-----------+
	    // | ... | inst | afterInst |
	    // +-----+------+-----------+
	    //  ^prev  ^next

	    addInst = new Instruction(opcx_dup2);
	    iter.add(addInst);
			
	    // +-----+------+------+-----------+
	    // | ... | dup2 | inst | afterInst |
	    // +-----+------+------+-----------+
	    //         ^prev  ^next

	    t = iter.previous();
	    Assert.isTrue(t == addInst, t + " != " + addInst);

	    // +-----+------+------+-----------+
	    // | ... | dup2 | inst | afterInst |
	    // +-----+------+------+-----------+
	    //  ^prev  ^next

	    t = iter.next();
	    Assert.isTrue(t == addInst, t + " != " + addInst);

	    // +-----+------+------+-----------+
	    // | ... | dup2 | inst | afterInst |
	    // +-----+------+------+-----------+
	    //         ^prev  ^next

	    addInst = new Instruction(opcx_aswizzle);
	    iter.add(addInst);

	    // +-----+------+----------+------+-----------+
	    // | ... | dup2 | aswizzle | inst | afterInst |
	    // +-----+------+----------+------+-----------+
	    //                    ^prev  ^next

	    t = iter.previous();
	    Assert.isTrue(t == addInst, t + " != " + addInst);

	    // +-----+------+----------+------+-----------+
	    // | ... | dup2 | aswizzle | inst | afterInst |
	    // +-----+------+----------+------+-----------+
	    //        ^prev    ^next
		
	    t = iter.next();
	    Assert.isTrue(t == addInst, t + " != " + addInst);

	    // +-----+------+----------+------+-----------+
	    // | ... | dup2 | aswizzle | inst | afterInst |
	    // +-----+------+----------+------+-----------+
	    //                  ^prev    ^next
		
	    t = iter.next();
	    Assert.isTrue(t == inst, t + " != " + inst);

	    // +-----+------+----------+------+-----------+
	    // | ... | dup2 | aswizzle | inst | afterInst |
	    // +-----+------+----------+------+-----------+
	    //                           ^prev    ^next


	    if (VERBOSE > 2) {
	      System.out.println("Inserting dup2,aswizzle before " + inst);
	    }
	  }
		  
	  else {
	    if (VERBOSE > 2) {
	      System.out.println("Not inserting aswizzle before " + inst);
	    }
	  }
	}

	// Insert an update check...
	if (uctype != NONE) {
	  if (UC) {
	    Object t;

	    //////////////////////////////////////////////
	    // Before...
	    // +-----+------+-----------+
	    // | ... | inst | afterInst |
	    // +-----+------+-----------+
	    //         ^prev  ^next
	    //
	    // After...
	    // +-----+---------+------+-----------+
	    // | ... | aupdate | inst | afterInst |
	    // +-----+---------+------+-----------+
	    //                   ^prev   ^next
	    ///////////////////////////////////////////////

	    // +-----+------+-----------+
	    // | ... | inst | afterInst |
	    // +-----+------+-----------+
	    //         ^prev  ^next

	    t = iter.previous();
	    Assert.isTrue(t == inst, t + " != " + inst);

	    // +-----+------+-----------+
	    // | ... | inst | afterInst |
	    // +-----+------+-----------+
	    //   ^prev  ^next

	    addInst = new Instruction(opcx_aupdate,
				      new Integer(depth));
	    /*
	      if (uctype == POINTER) {
	      addInst = new Instruction(opcx_aupdate,
	      new Integer(depth));
	      }
	      else {
	      addInst = new Instruction(opcx_supdate,
	      new Integer(depth));
	      }
	      */

	    iter.add(addInst);

	    // +-----+---------+------+-----------+
	    // | ... | aupdate | inst | afterInst |
	    // +-----+---------+------+-----------+
	    //            ^prev  ^next

	    t = iter.previous();
	    Assert.isTrue(t == addInst, t + " != " + addInst);

	    // +-----+---------+------+-----------+
	    // | ... | aupdate | inst | afterInst |
	    // +-----+---------+------+-----------+
	    //   ^prev  ^next

	    t = iter.next();
	    Assert.isTrue(t == addInst, t + " != " + addInst);

	    // +-----+---------+------+-----------+
	    // | ... | aupdate | inst | afterInst |
	    // +-----+---------+------+-----------+
	    //            ^prev  ^next

	    t = iter.next();
	    Assert.isTrue(t == inst, t + " != " + inst);

	    // +-----+---------+------+-----------+
	    // | ... | aupdate | inst | afterInst |
	    // +-----+---------+------+-----------+
	    //                   ^prev  ^next

	    if (VERBOSE > 2) {
	      System.out.println("Inserting " + addInst +
				 " before " + inst);
	    }
	  }
	  else if (VERBOSE > 2) {
	    System.out.println("Not inserting uc before " + inst);
	  }
	}
      }
    }
  }
}
