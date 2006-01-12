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

package EDU.purdue.cs.bloat.optimize;

import EDU.purdue.cs.bloat.reflect.*;
import EDU.purdue.cs.bloat.file.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.tree.*;
import EDU.purdue.cs.bloat.ssa.*;
import EDU.purdue.cs.bloat.tbaa.*;
import EDU.purdue.cs.bloat.trans.*;
import EDU.purdue.cs.bloat.codegen.*;
import EDU.purdue.cs.bloat.diva.*;
import EDU.purdue.cs.bloat.util.*;
import EDU.purdue.cs.bloat.context.*;
import EDU.purdue.cs.bloat.inline.*;

import java.io.*;
import java.text.*;
import java.util.*;

/**
 * Usage: java EDU.purdue.cs.bloat.optimize.Main [-options] classes dir
 *
 * where options include:
 *    -help             print out this message
 *    -v -verbose       turn on verbose mode
 *    -debug            display a hideous amount of debug info
 *    -classpath <directories separated by colons>
 *                      list directories in which to look for classes
 *    -f                optimize files even if up-to-date
 *    -closure          recursively optimize referenced classes
 *    -relax-loading    don't report errors if a class is not found
 *    -skip <class|package.*>
 *                      skip the given class or package
 *    -only <class|package.*>
 *                      skip all but the given class or package
 *    -preserve-debug   try to preserve debug information
 *    -[no]anno         insert an annotation in the contant pool
 *    -[no]stack-alloc  try to push locals onto the operand stack
 *    -peel-loops <n|all>
 *                      peel innermost loops to enable code hoisting
 *                      (n >= 0 is the maximum loop level to peel)
 *    -[no]pre          perform partial redundency elimination
 *    -[no]appre        perform partial redundency elimination on access paths
 *    -[no]dce          perform dead code elimination
 *    -diva             perform demand-driven induction variable analysis
 *    -[no]prop         perform copy and constant propagation
 *
 */
public class Main
{
  // Flags that can be set/unset from the command line
  static boolean DEBUG = false;        // Display debugging information
  static boolean VERBOSE = false;      // Display status information as program runs
  public static boolean TRACE = false;        // Track our progress
  static boolean FORCE = false;        // Optimize file even if they are up-to-date
  static boolean CLOSURE = false;      // Opimtize over entire heirarchy (i.e. start
  // at the specified classes and recurse up the
  // class heirarchy)

  static DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG);

  static boolean DIVA = false;
  public static boolean PRE = true;
  public static boolean DCE = true;
  public static boolean PROP = true;
  public static boolean FOLD = true;      // Value folding
  public static boolean INFER = true;     // Type inference
  public static boolean NUMBER = true;    // Value numbering
  public static boolean PERSIST = false;  // Persistent check elimination
  public static boolean STACK_ALLOC = false;
  public static boolean COMPACT_ARRAY_INIT = true;
  static boolean ANNO = true;         // Note that class file was optimized
  public static boolean VERIFY = true;
  public static boolean OPT_STACK_1 = false;  // Perform stack optimizations
  public static boolean OPT_STACK_2 = false;

  static String[] ARGS = null;        // Arguments from the command line
  public static List SKIP = new ArrayList(); // Classes that are specifically not optimized
  static List ONLY = new ArrayList(); // Classes that are specifically optimized
  static String METHOD = null;        // The name of one method to edit
  
  static BloatContext context = null;
  static ClassFileLoader loader = null;  // Used to load classes from class files

  /**
   * Parses the command line.  The user must specify at least one class to
   * optimize and the directory in which to place the optimized class files.
   * The methods of the specified classes are then optimized according to
   * the command line options.
   *
   * @see ClassEditor
   * @see ClassFileLoader
   * @see ClassFile
   * @see MethodEditor
   * @see MethodInfo
   *
   * @see CompactArrayInitializer
   * @see FlowGraph
   *
   */  
  public static void main(String[] args) {
    try {
      loader = new ClassFileLoader();
      
      List classes = new ArrayList(args.length);   // The classes to optimize
      boolean gotdir = false;            // Has an output directory been specified?
      
      ARGS = args;
      
      for (int i = 0; i < args.length; i++) {
	if (args[i].equals("-v") || args[i].equals("-verbose")) {
	  VERBOSE = true;
	  loader.setVerbose(true);

	} else if (args[i].equals("-debug")) {
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
	  CodeGenerator.DB_OPT_STACK = true;

	} else if (args[i].equals("-trace")) {
	  TRACE = true;

	} else if(args[i].equals("-db")) {

	  if(++i >= args.length) {
	    System.err.println("** No debugging option specified");
	    usage();
	  }

	  if(args[i].equals("bc")) {
	    CodeArray.DEBUG = true;

	  } else if(args[i].equals("cfg")) {
	    FlowGraph.DEBUG = true;

	  } else if(args[i].equals("ssa")) {
	    SSA.DEBUG = true;
	    SSAGraph.DEBUG = true;

	  } else if(args[i].equals("graphs")) {
	    FlowGraph.DB_GRAPHS = true;

	  } else if(args[i].startsWith("-")) {
	    i--;

	  } else {
	    System.err.println("** Unknown debugging option: " + args[i]);
	    usage();
	  }

	} else if (args[i].equals("-debugvf")) {
	  ValueFolding.DUMP = true;

	} else if(args[i].equals("-debugbc")) {
	  BloatContext.DEBUG = true;

	} else if (args[i].equals("-help")) {
	  usage();

	} else if (args[i].equals("-noanno")) {
	  ANNO = false;

	} else if (args[i].equals("-anno")) {
	  ANNO = true;

	} else if (args[i].equals("-print-flow-graph")) {
	  FlowGraph.PRINT_GRAPH = true;

	} else if (args[i].equals("-preserve-debug")) {
	  MethodEditor.PRESERVE_DEBUG = true;

	} else if (args[i].equals("-nouse-stack-vars")) {
	  Tree.USE_STACK = false;

	} else if (args[i].equals("-use-stack-vars")) {
	  Tree.USE_STACK = true;

	} else if (args[i].equals("-unique-handlers")) {
	  MethodEditor.UNIQUE_HANDLERS = true;

	} else if (args[i].equals("-nocompact-array-init")) {
	  COMPACT_ARRAY_INIT = false;

	} else if (args[i].equals("-compact-array-init")) {
	  COMPACT_ARRAY_INIT = true;

	} else if (args[i].equals("-nostack-alloc")) {
	  STACK_ALLOC = false;

	} else if (args[i].equals("-stack-alloc")) {
	  STACK_ALLOC = true;

	} else if(args[i].equals("-no-verify")) {
	  VERIFY = false;

	} else if (args[i].equals("-peel-loops")) {
	  if (++i >= args.length) {
	    usage();
	  }
	  
	  String n = args[i];
	  
	  if (n.equals("all")) {
	    FlowGraph.PEEL_LOOPS_LEVEL = FlowGraph.PEEL_ALL_LOOPS;
	  
	  } else {
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

	} else if (args[i].equals("-color")) {
	  Liveness.UNIQUE = false;

	} else if (args[i].equals("-nocolor")) {
	  Liveness.UNIQUE = true;

	} else if (args[i].equals("-only-method")) {
	  if (++i >= args.length) {
	    usage();
	  }
	  
	  METHOD = args[i];

	} else if (args[i].equals("-classpath")) {
	  if (++i >= args.length) {
	    usage();
	  }
	  
	  String classpath = args[i];
	  loader.setClassPath(classpath);

	} else if (args[i].equals("-classpath/p")) {
	  if (++i >= args.length) {
	    usage();
	  }
	  
	  String classpath = args[i];
	  loader.prependClassPath(classpath);

	} else if (args[i].equals("-skip")) {
	  if (++i >= args.length) {
	    usage();
	  }
	  
	  String pkg = args[i];

	  // Account for class file name on command line
	  if(pkg.endsWith(".class"))
	    pkg = pkg.substring(0, pkg.lastIndexOf('.'));

	  SKIP.add(pkg.replace('.', '/'));

	} else if (args[i].equals("-only")) {
	  if (++i >= args.length) {
	    usage();
	  }

	  String pkg = args[i];

	  // Account for class file name on command line
	  if(pkg.endsWith(".class"))
	    pkg = pkg.substring(0, pkg.lastIndexOf('.'));

	  ONLY.add(pkg.replace('.', '/'));

	} else if (args[i].equals("-nodce")) {
	  DCE = false;

	} else if (args[i].equals("-noprop")) {
	  PROP = false;

	} else if (args[i].equals("-noappre")) {
	  SSAPRE.NO_ACCESS_PATHS = true;

	} else if (args[i].equals("-nopre")) {
	  PRE = false;

	} else if (args[i].equals("-dce")) {
	  DCE = true;

	} else if (args[i].equals("-prop")) {
	  PROP = true;

	} else if (args[i].equals("-appre")) {
	  SSAPRE.NO_ACCESS_PATHS = false;

	} else if (args[i].equals("-pre")) {
	  PRE = true;

	} else if (args[i].equals("-closure")) {
	  CLOSURE = true;

	} else if (args[i].equals("-opt-stack-1")) {
	  OPT_STACK_1 = true;
	  CodeGenerator.OPT_STACK = true;

	} else if (args[i].equals("-opt-stack-2")) {
	  OPT_STACK_2 = true;
	  MethodEditor.OPT_STACK_2 = true;

	} else if (args[i].equals("-diva")) {
	  DIVA = true;

	} else if (args[i].equals("-no-thread")) {
	  SSAPRE.NO_THREAD = true;

	} else if (args[i].equals("-no-precise")) {
	  SSAPRE.NO_PRECISE = true;

	} else if (args[i].equals("-relax-loading")) {
	  ClassHierarchy.RELAX = true;

	} else if (args[i].equals("-f") || args[i].equals("-force")) {
	  FORCE = true;

	} else if (args[i].startsWith("-")) {
	  System.err.println("No such option: " + args[i]);
	  usage();

	} else if (i == args.length-1) {
	  // Last argument is the name of the output directory
	  
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

	  // Tell class loader to put optimized classes in f directory
	  loader.setOutputDir(f);
	  gotdir = true;
	}
	else {
	  // The argument must be a class name...
	  classes.add(args[i]);
	}
      }
      
      if (! gotdir) {
	System.err.println("No output directory specified");
	usage();
      }
      
      if (classes.size() == 0) {
	System.err.println("** No classes specified");
	usage();
      }
      
      // Use the CachingBloatingContext 
      context = new CachingBloatContext(loader, classes, CLOSURE);
      
      boolean errors = false;
      
      Iterator iter = classes.iterator();

      // Now that we've parsed the command line, load the classes into the 
      // class loader
      while (iter.hasNext()) {
	String name = (String) iter.next();

	try {
	  context.loadClass(name);
	  
	} catch (ClassNotFoundException ex) {
	  System.err.println("Couldn't find class: " +
			     ex.getMessage());
	  
	  errors = true;
	}
      }
      
      if (errors) {
	System.exit(1);
      }
      
      if (! CLOSURE) {
	Iterator e = classes.iterator();
	
	// Edit only the classes that were specified on the command line

	while (e.hasNext()) {
	  String name = (String) e.next();
	  editClass(name);
	}
      }
      else {
	// Edit all the classes in the class file editor and their superclasses

	classes = null;
	
	if(TRACE) {
	  System.out.println("Computing closure " + 
			     dateFormat.format(new Date()));
	}

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
		       "\nUsage: java EDU.purdue.cs.bloat.optimize.Main" +
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
		       "\n    -diva             perform demand-driven induction variable analysis" +
		       "\n    -[no]prop         perform copy and constant propagation" +
		       "");
    System.exit(0);
  }

  /**
   * Performs the actual editing of a class.  Does a whole mess of stuff
   * including reading in the classfile, building data structures to
   * represent the class file, converting the CFG for each method in the
   * class into SSA form, perform some anlayses and optimizations on the
   * method, and finally committing it back to the class file.  Phew.
   */  
  private static void editClass(String className)
  {
    ClassFile classFile;  // Holds info about a class (implements ClassInfo)
    
    // Get information about the class className
    try {
      classFile = (ClassFile) context.loadClass(className);
    }
    catch (ClassNotFoundException ex) {
      System.err.println("** Couldn't find class: " + ex.getMessage());
      return;
    }
    
    if (! FORCE) {
      // Check to see if the file is up-to-date (i.e. has been 
      // recompiled since it was last optimized).  If so, do nothing
      //  because the FORCE flag is false.

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
      // Print the contents of the class file to System.out
      classFile.print(System.out);
    }

    ClassEditor c = context.editClass(classFile);
    
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

      // We're done with this class file, decrement its reference count
      context.release(classFile);
      return;
    }
    
    // Touch the output file first.  That is, create the file, but make
    // it empty, just to make sure we can create it. 
    File outputDir = loader.outputDir();
    
    try {
      File f = classFile.outputFile();
      
      if (f.exists()) {
	f.delete();
      }
      
      File dir = new File(f.getParent());
      dir.mkdirs();
      
      if (! dir.exists()) {
	throw new RuntimeException("Couldn't create directory: " + dir);
      }
      
      DataOutputStream out = new DataOutputStream(new FileOutputStream(f));
      new PrintStream(out).println();
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
    
    if (VERBOSE) {
      System.out.println("Optimizing " + c.type().className());
    }

    // Finally, we can start playing with the methods...
    MethodInfo[] methods = c.methods();
    
    int numMethods = methods.length + 1;;
    int whichMethod = 0;

    for (int j = 0; j < methods.length; j++) {
      final MethodEditor m;
      
      try {
	m = context.editMethod(methods[j]);
      }
      catch (ClassFormatException ex) {
	System.err.println(ex.getMessage());
	continue;
      }
      
      if(TRACE) {
	whichMethod++;
	System.out.println("Optimizing " + name + "." + m.name() + 
			   " (method " + whichMethod + " of " + 
			   numMethods + ")");
      }

      if (METHOD != null) {
	// A method name has been specified on the command line using
	// -only-method.
	boolean pass = true;
	
	String t = m.name() + m.type();
	
	if (t.equals(METHOD)) {
	  pass = false;
	}
	
	t = m.name();
	
	if (t.equals(METHOD)) {
	  pass = false;
	}
	
	if (pass) {
	  // This isn't the method we're looking for.  
	  // Decrement its reference count.
	  context.release(methods[j]);
	  continue;
	}
      }
      
      if (DEBUG) {
	m.print(System.out);
      }
      
      if (m.isNative() || m.isAbstract()) {
	// We can't edit native or abstract methods
	context.release(methods[j]);
	continue;
      }

      bloatMethod(m, context);
    }

    if (ANNO) {
      String s = "Optimized with: EDU.purdue.cs.bloat.optimize.Main";
      
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
    context.release(classFile);
    
    if(TRACE)
      System.out.println(context.toString());
  }



  /**
   * Runs BLOAT on a method.
   */
  public static void bloatMethod(MethodEditor m, BloatContext context) {
    try {
      if (COMPACT_ARRAY_INIT) {
	// Compact the initialization of arrays of the basic types by 
	// putting the values of the array into a string in the constant
	//  pool.  The initialization code is replaced with a loop that
	// loads the array from the string in the constant pool.

	if(TRACE) {
	  System.out.println("  Compacting Arrays: " + 
			     dateFormat.format(new Date()));
	}

	CompactArrayInitializer.transform(m);
	
	if (DEBUG) {
	  System.out.println("---------- After compaction:");
	  m.print(System.out);
	  System.out.println("---------- end print");
	}
      }
      
      FlowGraph cfg;            // The control flow graph for a method
      
      if(TRACE) {
	System.out.println("  Constructing CFG: " + 
			   dateFormat.format(new Date()));
      }

      try {
	// Construct the control flow graph for method m
	cfg = new FlowGraph(m);
      }
      catch (ClassFormatException ex) {
	System.err.println(ex.getMessage());
	context.release(m.methodInfo());
	return;
      }
      
      // We separate out initialization since before this the FlowGraph
      // more exactly represents the input program.
      cfg.initialize();
      
      if(TRACE) {
	System.out.println("  Transforming to SSA: " + 
			   dateFormat.format(new Date()));
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
      
      if (! Tree.USE_STACK) {
	// Do copy propagation and value numbering first to get rid of
	// all the extra copies inserted for dups.  If they're left in,
	// it really slows down value numbering.
	if (PROP) {
	  if (DEBUG) {
	    System.out.println("-----Before Copy Propagation-----");
	  }
	  
	  if(TRACE) {
	    System.out.println("  Copy propagation: " + 
			       dateFormat.format(new Date()));
	  }

	  ExprPropagation copy = new ExprPropagation(cfg);
	  copy.transform();
	  
	  if (DEBUG) {
	    cfg.visit(new VerifyCFG(false));
	  }
	  
	  if (DEBUG) {
	    System.out.println("------After Copy Propagation-----");
	    cfg.print(System.out);
	  }
	}
      }

      DeadCodeElimination dce = null;

      if (DCE) {
      
	  if(TRACE) {
	      System.out.println("  Dead Code Elimination: " + 
				 dateFormat.format(new Date()));
	  }
	  
	  if (DEBUG) {
	      System.out.println("---Before Dead Code Elimination--");
	  }
	  
	  dce = new DeadCodeElimination(cfg);
	  dce.transform();
	  
	  if (DEBUG) {
	      cfg.visit(new VerifyCFG(false));
	  }
      
	  if (DEBUG) {
	      System.out.println("---After Dead Code Elimination---");
	      cfg.print(System.out);
	  }
      }      

      if(INFER) {

	if (DEBUG) {
	  System.out.println("---------Doing type inference--------");
	}
      
	if(TRACE) {
	  System.out.println("  Type Inferencing: " + 
			     dateFormat.format(new Date()));
	}

	(new TypeInference()).transform(cfg, context.getHierarchy());
      }

      if(NUMBER) {

	if(TRACE) {
	  System.out.println("  Value Numbering: " + 
			     dateFormat.format(new Date()));
	}

	if (DEBUG) {
	  System.out.println("--------Doing value numbering--------");
	}
      
	(new ValueNumbering()).transform(cfg);
      }
      
      if (FOLD) {
	if (DEBUG) {
	  System.out.println("--------Before Value Folding---------");
	}
	
	if(TRACE) {
	  System.out.println("  Value Folding: " + 
			     dateFormat.format(new Date()));
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
	
	if(TRACE) {
	  System.out.println("  SSAPRE: " + 
			     dateFormat.format(new Date()));
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
	
	if(TRACE) {
	  System.out.println("  Value Folding: " + 
			     dateFormat.format(new Date()));
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
	
	if(TRACE) {
	  System.out.println("  Copy Propagation " + 
			     dateFormat.format(new Date()));
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
       
           // make sure we've done at least one thing since the last DCE
      if (DCE && (INFER || NUMBER || FOLD || PRE || PROP) ) {
	if (DEBUG) {
	  System.out.println("-----Before Dead Code Elimination----");
	}
	
	if(TRACE) {
	  System.out.println("  Dead Code Elimination: " + 
			     dateFormat.format(new Date()));
	}

	dce = new DeadCodeElimination(cfg);
	dce.transform();
	
	if (DEBUG) {
	  cfg.visit(new VerifyCFG());
	}
	
	if (DEBUG) {
	  System.out.println("-----After Dead Code Elimination-----");
	  cfg.print(System.out);
	}
      }
      
      if(PERSIST) {
	(new PersistentCheckElimination()).transform(cfg);
      }
      
      if(DIVA){
	if (DEBUG) {
	  System.out.println("-----Before DIVA------");
	}
	
	if(TRACE) {
	  System.out.println("  DIVA: " + 
			     dateFormat.format(new Date()));
	}

	(new InductionVarAnalyzer()).transform(cfg);
	
	if (DEBUG) {
	  System.out.println("-----After DIVA-----");
	  cfg.print(System.out);
	}

      }
      
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

      // Do the new stack optimization
      if (OPT_STACK_2) {

	  if(TRACE) {
	      System.out.println("  New stack optimization: " +
				 dateFormat.format(new Date()));
	  }

	  // generate code without doing liveness or register allocation
	  CodeGenerator codegen = new CodeGenerator(m);
	  codegen.replacePhis(cfg);
	  m.clearCode2();
	  cfg.visit(codegen);
	  // do stack optimization on the bytecode

	  
	  StackOpt so = new StackOpt(); 
	  so.transform(m); 
	  

	  // convert it back to a cfg
	  cfg = new FlowGraph(m); 
	  cfg.initialize();
	      
	      
	  // convert it back to SSA
	  SSA.transform(cfg);

	  // do more dead code elimination (eliminate stores)
	  dce = new DeadCodeElimination(cfg);
	  dce.transform();
      }


      if(TRACE) {
	System.out.println("  Register allocation: " + 
			   dateFormat.format(new Date()));
      }

      if(VERIFY) {
	  try {
	      cfg.visit(new VerifyCFG());
	  }
	  catch (IllegalArgumentException ee) {
	      System.out.println(" NOTE: CFG did not verify while " +
				 "bloating " + m.name() + 
				 " after all optimizations. Exception: " +
				 ee);
	  }
      }

      // We're all done performing optimizations.  Let's generate some code
      // and go home.

      // Perform liveness analysis of variables in the method.
      // Assign local variables ("registers") to expression values.
      Liveness liveness = new Liveness(cfg);
      RegisterAllocator alloc = new RegisterAllocator(cfg, liveness);

      // Gather information which can be used to optimize use of the stack
      if (CodeGenerator.OPT_STACK) {
	if(TRACE) {
	  System.out.println("  Old stack optimization: " +
			     dateFormat.format(new Date()));
	}
	StackOptimizer.optimizeCFG(cfg);
      }

      if(TRACE) {
	System.out.println("  Code Generation: " + 
			   dateFormat.format(new Date()));
      }

      // Start the code generation process.
      CodeGenerator codegen = new CodeGenerator(m);
      codegen.replacePhis(cfg);

      if (DEBUG) {
	System.out.println("After fixing Phis------------------------");
	cfg.print(System.out);
	System.out.println("End print--------------------------------");
      }
      
      codegen.simplifyControlFlow(cfg);
      codegen.allocReturnAddresses(cfg, alloc);
      
      if (DEBUG) {
	System.out.println("After removing empty blocks--------------");
	cfg.print(System.out);
	System.out.println("End print--------------------------------");
      }
      
      // Clear the old contents of the bytecode store and generate new code.
      // Code is generated using a visitor pattern on the CFG.
      m.clearCode();
      cfg.visit(codegen);

      Peephole.transform(m);

      // Commit any changes that have been made to the method      
      context.commit(m.methodInfo());

    } catch(Exception ex99) {
      String msg = "** Exception while optimizing " + m.name() + m.type()
	+ " of class " + m.declaringClass().name();
      System.err.println(msg);
      System.err.println(ex99.getMessage());
      ex99.printStackTrace(System.err);
      System.exit(1);
    }
  }    

    public static void dumpcode(MethodEditor m) {

	PrintWriter out = new PrintWriter(System.out, true);
	StackHeightCounter shc = new StackHeightCounter(m);
	

	out.println("Code for method " + m.name() + m.type());
	List instructions = m.code();
	ListIterator iter = instructions.listIterator();
	while (iter.hasNext()) {
	    Object obj = iter.next();
	    if (obj instanceof Label) 
		shc.handle((Label) obj);
	    else if (obj instanceof Instruction) 
		shc.handle((Instruction) obj);

	    System.out.println("        " + obj + " (sh: " + shc.height()
			       + ")");
	}
    }


}
