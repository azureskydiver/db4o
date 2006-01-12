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

package EDU.purdue.cs.bloat.diva;

import EDU.purdue.cs.bloat.reflect.*;
import EDU.purdue.cs.bloat.file.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.tree.*;
import EDU.purdue.cs.bloat.ssa.*;
import EDU.purdue.cs.bloat.tbaa.*;
import EDU.purdue.cs.bloat.trans.*;
import EDU.purdue.cs.bloat.codegen.*;
import EDU.purdue.cs.bloat.util.*;
import EDU.purdue.cs.bloat.context.*;

import java.io.*;
import java.util.*;

/**
 * Performs a number of analyses on the methods of some specified classes.
 * However, it does not perform some of the optimizations that optimize.Main
 * does.
 *
 * @see EDU.purdue.cs.bloat.optimize.Main
 */
public class Main
{
  static boolean DEBUG = false;
  static boolean VERBOSE = false;
  static boolean FORCE = false;
  static boolean CLOSURE = false;
  static boolean PRE = true;
  static boolean DCE = true;
  static boolean PROP = true;
  static boolean FOLD = true;
  static boolean STACK_ALLOC = false;
  static boolean COMPACT_ARRAY_INIT = true;
  static boolean ANNO = true;
  static String[] ARGS = null;
  static List SKIP = new ArrayList();
  static List ONLY = new ArrayList();
  static String METHOD = null;

  static BloatContext context = null;
  static ClassFileLoader loader = null;

  public static void main(String[] args)
  {
    try {
      loader = new ClassFileLoader();

      List classes = new ArrayList(args.length);
      boolean gotdir = false;

      ARGS = args;

      for (int i = 0; i < args.length; i++) {
	if (args[i].equals("-v") || args[i].equals("-verbose")) {
	  VERBOSE = true;
	  loader.setVerbose(true);
	}
	else if (args[i].equals("-debug")) {
	  DEBUG = true;
	  loader.setVerbose(true);
	  ClassFileLoader.DEBUG = true;
	  CompactArrayInitializer.DEBUG = true;
	  ClassEditor.DEBUG = true;
	  FlowGraph.DEBUG = true;
	  DominatorTree.DEBUG = true;
	  Tree.DEBUG = true;
	  CodeGenerator.DEBUG = true;
	  Liveness.DEBUG = true;
	  SSA.DEBUG = true;
	  SSAGraph.DEBUG = true;
	  PersistentCheckElimination.DEBUG = true;
	  ValueNumbering.DEBUG = true;
	  ValueFolding.DEBUG = true;
	  ClassHierarchy.DEBUG = true;
	  TypeInference.DEBUG = true;
	  SSAPRE.DEBUG = true;
	  StackPRE.DEBUG = true;
	  ExprPropagation.DEBUG = true;
	  DeadCodeElimination.DEBUG = true;
	}
	else if (args[i].equals("-help")) {
	  usage();
	}
	else if (args[i].equals("-noanno")) {
	  ANNO = false;
	}
	else if (args[i].equals("-anno")) {
	  ANNO = true;
	}
	else if (args[i].equals("-preserve-debug")) {
	  MethodEditor.PRESERVE_DEBUG = true;
	}
	else if (args[i].equals("-nouse-stack-vars")) {
	  Tree.USE_STACK = false;
	}
	else if (args[i].equals("-use-stack-vars")) {
	  Tree.USE_STACK = true;
	}
	else if (args[i].equals("-nocompact-array-init")) {
	  COMPACT_ARRAY_INIT = false;
	}
	else if (args[i].equals("-compact-array-init")) {
	  COMPACT_ARRAY_INIT = true;
	}
	else if (args[i].equals("-nostack-alloc")) {
	  STACK_ALLOC = false;
	}
	else if (args[i].equals("-stack-alloc")) {
	  STACK_ALLOC = true;
	}
	else if (args[i].equals("-peel-loops")) {
	  if (++i >= args.length) {
	    usage();
	  }

	  String n = args[i];

	  if (n.equals("all")) {
	    FlowGraph.PEEL_LOOPS_LEVEL = FlowGraph.PEEL_ALL_LOOPS;
	  }
	  else {
	    try {
	      FlowGraph.PEEL_LOOPS_LEVEL = Integer.parseInt(n);

	      if (FlowGraph.PEEL_LOOPS_LEVEL < 0) {
		usage();
	      }
	    }
	    catch (NumberFormatException ex) {
	      usage();
	    }
	  }
	}
	else if (args[i].equals("-color")) {
	  Liveness.UNIQUE = false;
	}
	else if (args[i].equals("-nocolor")) {
	  Liveness.UNIQUE = true;
	}
	else if (args[i].equals("-only-method")) {
	  if (++i >= args.length) {
	    usage();
	  }

	  METHOD = args[i];
	}
	else if (args[i].equals("-print-flow-graph")) {
	  FlowGraph.PRINT_GRAPH = true;
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
	else if (args[i].equals("-nodce")) {
	  DCE = false;
	}
	else if (args[i].equals("-noprop")) {
	  PROP = false;
	}
	else if (args[i].equals("-nopre")) {
	  PRE = false;
	}
	else if (args[i].equals("-dce")) {
	  DCE = true;
	}
	else if (args[i].equals("-prop")) {
	  PROP = true;
	}
	else if (args[i].equals("-pre")) {
	  PRE = true;
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
	else if (args[i].startsWith("-")) {
	  usage();
	}
	else if (i == args.length-1) {
	  File f = new File(args[i]);

	  if (f.exists() && ! f.isDirectory()) {
	    System.err.println("No such directory: " + f.getPath());
	    System.exit(2);
	  }

	  if (! f.exists()) {
	    f.mkdirs();
	  }

	  if (! f.exists()) {
	    System.err.println("Couldn't create directory: " +
			       f.getPath());
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

      boolean errors = false;

      Iterator iter = classes.iterator();

      while (iter.hasNext()) {
	String name = (String) iter.next();

	try {
	  loader.loadClass(name);
	}
	catch (ClassNotFoundException ex) {
	  System.err.println("Couldn't find class: " +
			     ex.getMessage());
	  errors = true;
	}
      }

      if (errors) {
	System.exit(1);
      }

      context = new CachingBloatContext(loader, classes, CLOSURE);

      if (! CLOSURE) {
	Iterator e = classes.iterator();

	while (e.hasNext()) {
	  String name = (String) e.next();
	  editClass(name);
	}
      }
      else {
	classes = null;

	Iterator e = context.getHierarchy().classes().iterator();

	while (e.hasNext()) {
	  Type t = (Type) e.next();

	  if (t.isObject()) {
	    editClass(t.className());
	  }
	}
      }
    }
    catch (ExceptionInInitializerError ex) {
      ex.printStackTrace();
      System.out.println(ex.getException());
    }
  }

  private static void usage()
  {
    System.err.println(
       "Usage: java EDU.purdue.cs.bloat.optimize.Main" +
       "\n            [-options] classes dir" +
       "\n" +
       "\nwhere options include:" +
       "\n    -help             print out this message" +
       "\n    -v -verbose       turn on verbose mode" +
       "\n    -debug            display a hideous amount of debug info" +
       "\n    -classpath <directories separated by colons>" +
       "\n                      list directories in which to look for classes"+
       "\n    -f                optimize files even if up-to-date" +
       "\n    -closure          recursively optimize referenced classes" +
       "\n    -relax-loading    don't report errors if a class is not found" + 
       "\n    -skip <class|package.*>" +
       "\n                      skip the given class or package" +
       "\n    -only <class|package.*>" +
       "\n                      skip all but the given class or package" +
       "\n    -preserve-debug   try to preserve debug information" +
       "\n    -[no]anno         insert an annotation in the contant pool" +
       "\n    -[no]stack-alloc  try to push locals onto the operand stack" +
       "\n    -peel-loops <n|all>" +
       "\n                      peel innermost loops to enable code hoisting" +
       "\n                      (n >= 0 is the maximum loop level to peel)" +
       "\n    -[no]pre          perform partial redundency elimination" +
       "\n    -[no]dce          perform dead code elimination" +
       "\n    -[no]prop         perform copy and constant propagation" +
       "");
    System.exit(0);
  }

  private static void editClass(String className)
  {
    ClassFile classFile;

    try {
      classFile = (ClassFile) loader.loadClass(className);
    }
    catch (ClassNotFoundException ex) {
      System.err.println("Couldn't find class: " + ex.getMessage());
      return;
    }

    if (! FORCE) {
      File source = classFile.file();
      File target = classFile.outputFile();

      if (source != null && target != null &&
	  source.exists() && target.exists() &&
	  source.lastModified() < target.lastModified()) {

	if (VERBOSE) {
	  System.out.println(classFile.name() + " is up to date");
	}

	return;
      }
    }

    if (DEBUG) {
      classFile.print(System.out);
    }

    final ClassEditor c = context.editClass(classFile);

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
      if (VERBOSE) {
	System.out.println("Skipping " + c.type().className());
      }

      context.release(classFile);
      return;
    }

    if (VERBOSE) {
      System.out.println("Optimizing " + c.type().className());
    }

    MethodInfo[] methods = c.methods();

    for (int j = 0; j < methods.length; j++) {
      final MethodEditor m;

      try {
	m = context.editMethod(methods[j]);
      }
      catch (ClassFormatException ex) {
	System.err.println(ex.getMessage());
	continue;
      }

      if (METHOD != null && ! m.name().equals(METHOD)) {
	context.release(methods[j]);
	continue;
      }

      if (DEBUG) {
	m.print(System.out);
      }

      if (m.isNative() || m.isAbstract()) {
	context.release(methods[j]);
	continue;
      }

      if (COMPACT_ARRAY_INIT) {
	CompactArrayInitializer.transform(m);

	if (DEBUG) {
	  System.out.println("---------- After compaction:");
	  m.print(System.out);
	  System.out.println("---------- end print");
	}
      }

      FlowGraph cfg;

      try {
	cfg = new FlowGraph(m);
      }
      catch (ClassFormatException ex) {
	System.err.println(ex.getMessage());
	context.release(methods[j]);
	continue;
      }

      SSA.transform(cfg);

      if (FlowGraph.DEBUG) {
	System.out.println("---------- After SSA:");
	cfg.print(System.out);
	System.out.println("---------- end print");
      }

      if (DEBUG) {
	cfg.visit(new VerifyCFG(false));
      }

      // Do copy propagation first to get rid of all the extra copies
      // inserted for dups.  If they're left it, it really slows down
      // value numbering.
      if (PROP) {
	if (DEBUG) {
	  System.out.println("-------Before Copy Propagation-------");
	}

	ExprPropagation copy = new ExprPropagation(cfg);
	copy.transform();

	if (DEBUG) {
	  cfg.visit(new VerifyCFG(false));
	}

	if (DEBUG) {
	  System.out.println("--------After Copy Propagation-------");
	  cfg.print(System.out);
	}
      }

      if (DCE) {
	if (DEBUG) {
	  System.out.println("-----Before Dead Code Elimination----");
	}

	DeadCodeElimination dce = new DeadCodeElimination(cfg);
	dce.transform();

	if (DEBUG) {
	  cfg.visit(new VerifyCFG(false));
	}

	if (DEBUG) {
	  System.out.println("-----After Dead Code Elimination-----");
	  cfg.print(System.out);
	}
      }

      if (DEBUG) {
	System.out.println("---------Doing type inference--------");
      }

      TypeInference.transform(cfg, context.getHierarchy());

      if (DEBUG) {
	System.out.println("--------Doing value numbering--------");
      }

      (new ValueNumbering()).transform(cfg);

      if (FOLD) {
	if (DEBUG) {
	  System.out.println("--------Before Value Folding---------");
	}

	(new ValueFolding()).transform(cfg);

	if (DEBUG) {
	  cfg.visit(new VerifyCFG());
	}

	if (DEBUG) {
	  System.out.println("---------After Value Folding---------");
	  cfg.print(System.out);
	}
      }

      if (PRE) {
	if (DEBUG) {
	  System.out.println("-------------Before SSAPRE-----------");
	}

	SSAPRE pre = new SSAPRE(cfg, context);
	pre.transform();

	if (DEBUG) {
	  cfg.visit(new VerifyCFG());
	}

	if (DEBUG) {
	  System.out.println("-------------After SSAPRE------------");
	  cfg.print(System.out);
	}
      }

      if (FOLD) {
	if (DEBUG) {
	  System.out.println("--------Before Value Folding---------");
	}

	(new ValueFolding()).transform(cfg);

	if (DEBUG) {
	  cfg.visit(new VerifyCFG());
	}

	if (DEBUG) {
	  System.out.println("---------After Value Folding---------");
	  cfg.print(System.out);
	}
      }


      if (PROP) {
	if (DEBUG) {
	  System.out.println("-------Before Copy Propagation-------");
	}

	ExprPropagation copy = new ExprPropagation(cfg);
	copy.transform();

	if (DEBUG) {
	  cfg.visit(new VerifyCFG());
	}

	if (DEBUG) {
	  System.out.println("--------After Copy Propagation-------");
	  cfg.print(System.out);
	}
      }

      if (DCE) {
	if (DEBUG) {
	  System.out.println("-----Before Dead Code Elimination----");
	}

	DeadCodeElimination dce = new DeadCodeElimination(cfg);
	dce.transform();

	if (DEBUG) {
	  cfg.visit(new VerifyCFG());
	}

	if (DEBUG) {
	  System.out.println("-----After Dead Code Elimination-----");
	  cfg.print(System.out);
	}
      }

      (new PersistentCheckElimination()).transform(cfg);
      (new InductionVarAnalyzer()).transform(cfg);


      /*
	if (STACK_ALLOC) {
	if (DEBUG) {
	System.out.println("------------Before StackPRE----------");
	}

	StackPRE pre = new StackPRE(cfg);
	pre.transform();

	if (DEBUG) {
	cfg.visit(new VerifyCFG());
	}

	if (DEBUG) {
	System.out.println("------------After StackPRE-----------");
	cfg.print(System.out);
	}
	}
	*/

      cfg.commit();

      Peephole.transform(m);

      context.commit(methods[j]);
    }

    if (ANNO) {
      String s = "Optimized with: EDU.purdue.cs.bloat.diva.Main";

      for (int i = 0; i < ARGS.length; i++) {
	if (ARGS[i].indexOf(' ') >= 0 || ARGS[i].indexOf('\t') >= 0 ||
	    ARGS[i].indexOf('\r') >= 0 || ARGS[i].indexOf('\n') >= 0) {
	  s += " '" + ARGS[i] + "'";
	}
	else {
	  s += " " + ARGS[i];
	}
      }

      System.out.println(s);
      // c.constants().addConstant(Constant.UTF8, s);
    }
    
    context.commit(classFile);
  }
}
