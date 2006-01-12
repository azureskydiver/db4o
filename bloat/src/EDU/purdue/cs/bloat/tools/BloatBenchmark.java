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

package EDU.purdue.cs.bloat.tools;

import EDU.purdue.cs.bloat.reflect.*;
import EDU.purdue.cs.bloat.file.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.inline.*;
import EDU.purdue.cs.bloat.context.*;
import EDU.purdue.cs.bloat.util.*;
import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.tree.*;
import EDU.purdue.cs.bloat.ssa.*;
import EDU.purdue.cs.bloat.tbaa.*;
import EDU.purdue.cs.bloat.trans.*;
import EDU.purdue.cs.bloat.codegen.*;
import EDU.purdue.cs.bloat.optimize.*;
import EDU.purdue.cs.bloat.benchmark.*;

import java.io.*;
import java.util.*;

/**
 * This program is used to BLOAT Java programs.  In particular, we use
 * it to BLOAT the programs used to benchmark BLOAT.  This program is
 * intended to be run multiple times to generate multiple BLOATed
 * programs.  
 */
public class BloatBenchmark {
  public static boolean TRACE = false;

  private static boolean INLINE = false;
  private static boolean INTRA = false;
  private static boolean PEEPHOLE = false;
  private static boolean VERIFY = true;
  private static boolean SPECIALIZE = false;
  private static boolean STATS = false;
  private static boolean SUN = false;
  private static boolean USE1_1 = false;
  private static boolean USE1_2 = false;
  private static boolean OPT_STACK = true;
  private static boolean CHECK = true;
  private static boolean TIMES = false;
 
  private static PrintWriter out = new PrintWriter(System.out, true);
  private static PrintWriter err = new PrintWriter(System.err, true);

  private static Set CLASSES = new HashSet();
  private static String statsFile = null;
  private static String timesFile = null;
  private static int DEPTH = 2;    // No. calls deep
  private static int SIZE = 1000;  // Max size of methods
  private static int MORPH = -1;    // Max "morphosity" of virtual calls
  private static int CALLEE_SIZE = -1;
  private static List SKIP = new ArrayList(); // Classes that are specifically not optimized

  private static void tr(String s) {
    if(TRACE)
      System.out.println(s);
  }

  private static void usage() {
    err.println("java TestSpecialize [options] classNames outputDir");
    err.println("where [options] are:");
    err.println("  -calleeSize size   Max method size to inline");
    err.println("  -classpath path    Classpath is always prepended");
    err.println("  -depth depth       Max inline depth");
    err.println("  -inline            Inline calls to static methods");
    err.println("  -intra             Intraprocedural BLOAT");
    err.println("  -lookIn dir        Look for classes here");
    err.println("  -morph morph       Max morphosity of call sites");
    err.println("  -no-verify         Don't verify CFG");
    err.println("  -no-opt-stack      Don't optimize stack usage");
    err.println("  -no-stack-vars     Don't use stack vars in CFG");
    err.println("  -no-stack-alloc    Don't try to push locals onto the operand stack");
    err.println("  -peel-loops <n|all>" +
		"\n                   Peel innermost loops to enable code hoisting" +
		"\n                   (n >= 0 is the maximum loop level to peel)");
    err.println("  -no-pre            Don't perform partial redundency elimination");
    err.println("  -no-dce            Don't perform dead code elimination");
    err.println("  -no-prop           Don't perform copy and constant propagation");
    err.println("  -no-color          Don't do graph coloring");
    err.println("  -peephole          Perform peephole after inter");
    err.println("  -size size         Max method size");
    err.println("  -specialize        Specialize virtual method calls");
    err.println("  -stats statsFile   Generate stats");
    err.println("  -sun               Include sun packages");
    err.println("  -times timesFile   Print timings");
    err.println("  -trace             Print trace information");
    err.println("  -no-check          Don't check that my options 'make sense'");
    err.println("  -skip <class|package.*>" +
		"\n                   Skip the given class or package");
    err.println("  -1.1               BLOAT for JDK1.1");
    err.println("  -1.2               BLOAT for JDK1.2");
    err.println("");
    System.exit(1);
  }

  public static void main(String[] args) {
    String CLASSPATH = null;
    String outputDirName = null;
    String lookIn = null;

    // Parse the command line
    for(int i = 0; i < args.length; i++) {
      if(args[i].equals("-trace")) {
	TRACE = true;
	PersistentBloatContext.DB_COMMIT = true;

      } else if(args[i].equals("-calleeSize")) {
	if(++i >= args.length) {
	  err.println("** No callee size specified");
	  usage();
	}

	try {
	  CALLEE_SIZE = Integer.parseInt(args[i]);

	} catch(NumberFormatException ex33) {
	  err.println("** Bad number: " + args[i]);
	  usage();
	}


      } else if(args[i].startsWith("-classpath")) {
	if(++i >= args.length) {
	  err.println("** No classpath specified");
	  usage();
	}

	// If there is more than one -classpath append it to the
	// current one.  That way the CLASSPATH reflects the order in
	// which the options came on the command line.
	if(CLASSPATH == null) {
	  CLASSPATH = args[i];

	} else {
	  CLASSPATH += File.pathSeparator + args[i];
	}

	} else if (args[i].equals("-no-stack-alloc")) {
	  Main.STACK_ALLOC = false;
      
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
	} else if (args[i].equals("-no-color")) {
	  Liveness.UNIQUE = true;

	} else if (args[i].equals("-no-dce")) {
	  Main.DCE = false;

	} else if (args[i].equals("-no-prop")) {
	  Main.PROP = false;

	} else if (args[i].equals("-no-pre")) {
	  Main.PRE = false;

	} else if (args[i].equals("-no-check")){
	   CHECK = false;

      } else if(args[i].equals("-depth")) {
	if(++i >= args.length) {
	  err.println("** No depth specified");
	  usage();
	}

	try {
	  DEPTH = Integer.parseInt(args[i]);

	} catch(NumberFormatException ex33) {
	  err.println("** Bad number: " + args[i]);
	  usage();
	}

      } else if(args[i].equals("-inline")) {
	// Inline calls to static methods
	INLINE = true;

      } else if(args[i].equals("-intra")) {
	INTRA = true;

      } else if(args[i].equals("-lookIn")) {
	if(++i >= args.length) {
	  err.println("** No directory specified");
	  usage();
	}

	if(lookIn != null) {
	  lookIn += File.pathSeparator + args[i];

	} else {
	  lookIn = args[i];
	}

      } else if(args[i].equals("-morph")) {
	if(++i >= args.length) {
	  err.println("** No morphosity specified");
	  usage();
	}

	try {
	  MORPH = Integer.parseInt(args[i]);

	} catch(NumberFormatException ex33) {
	  err.println("** Bad number: " + args[i]);
	  usage();
	}

      } else if(args[i].equals("-noinline")) {
	// Don't perform inlining, just specialize
	INLINE = false;

      } else if(args[i].equals("-peephole")) {
	// Perform peephole optimizations when doing interprocedural
	// stuff
	PEEPHOLE = true;

      } else if(args[i].equals("-size")) {
	if(++i >= args.length) {
	  err.println("** No size specified");
	  usage();
	}

	try {
	  SIZE = Integer.parseInt(args[i]);

	} catch(NumberFormatException ex33) {
	  err.println("** Bad number: " + args[i]);
	  usage();
	}

      } else if(args[i].equals("-specialize")) {
	// Specialize virtual method call sites
	SPECIALIZE = true;

      } else if(args[i].equals("-stats")) {
	if(++i >= args.length) {
	  err.println("** No stats file specified");
	  usage();
	}

	statsFile = args[i];

      } else if(args[i].equals("-sun")) {
	// Optimize sun packages
	SUN = true;

      } else if(args[i].equals("-times")) {
	TIMES = true;

	if(++i >= args.length) {
	  err.println("** No times file specified");
	  usage();
	}

	timesFile = args[i];

      } else if(args[i].equals("-no-verify")) {
	VERIFY = false;

      } else if(args[i].equals("-no-opt-stack")) {
	OPT_STACK = false;
	CodeGenerator.OPT_STACK = false;

      } else if(args[i].equals("-no-stack-vars")) {
	Tree.USE_STACK = false;

	} else if (args[i].equals("-skip")) {
	  if (++i >= args.length) {
	    usage();
	  }
	  
	  String pkg = args[i];

	  // Account for class file name on command line
	  if(pkg.endsWith(".class"))
	    pkg = pkg.substring(0, pkg.lastIndexOf('.'));

	  SKIP.add(pkg.replace('.', '/'));

      } else if(args[i].equals("-1.1")) {
	// There are some classes that we don't want to be pre-live.
	// They don't exist in JDK1.1.
	USE1_1 = true;
	CallGraph.USE1_2 = false;

      } else if(args[i].equals("-1.2")) {
	USE1_2 = true;
	CallGraph.USE1_2 = true;

	if(lookIn != null) {
	  lookIn += File.separator + "1.2";
	}

      } else if(args[i].startsWith("-")) {
	err.println("** Unrecognized option: " + args[i]);
	usage();

      } else if(i == args.length - 1) {
	outputDirName = args[i];

      } else {
	CLASSES.add(args[i]);
      }
    }

    if(CLASSES.isEmpty()) {
      err.println("** No classes specified");
      usage();	
    }

    if(outputDirName == null) {
      err.println("** No output directory specified");
      usage();
    }

    // Make sure the options the user entered make sense
    if (CHECK)
	checkOptions();

    if(USE1_1) {
      // Don't generate stats for 1.1
      statsFile = null;
    }      

    if(lookIn != null) {
      CLASSPATH = lookIn + File.pathSeparator + CLASSPATH;
    }

    StringBuffer sb = new StringBuffer();
    for(int i = 0; i < args.length; i++) {
      sb.append(args[i] + " ");
    }
    tr("BLOATing with command line: " + sb);

    BloatContext context = null;

    float systemStart = 0.0F;
    float systemDelta = 0.0F;
    float systemEnd = 0.0F;
    float systemTotal = 0.0F;
    
    float userStart = 0.0F;
    float userDelta = 0.0F;
    float userEnd = 0.0F;
    float userTotal = 0.0F;
    
    PrintWriter times = null;

    if(TIMES) {
      try {
	times = new PrintWriter(new FileWriter(timesFile), true);
	
      } catch(IOException ex) {
	times = new PrintWriter(System.out, true);
      }
    }

    if(INTRA) {
      tr("Intraprocedural BLOAT");

      // First compute the roots of the call graph.  Figure out which
      // methods are live.
      context = makeContext(CLASSPATH, null);
      Collection liveMethods = liveMethods(CLASSES, context);

      // Run intraprocedural BLOAT on the live methods. 
      tr(liveMethods.size() + " live methods");
      context = makeContext(CLASSPATH, outputDirName);
      intraBloat(liveMethods, context);

    } else {
      tr("Interprocedural BLOAT");

      if(TIMES) {
	Times.snapshot();
	systemStart = Times.systemTime();
	userStart = Times.userTime();
      }

      // Do the interprocedural BLOATing
      context = makeContext(CLASSPATH, outputDirName);
      liveMethods(CLASSES, context);

      if(TIMES) {
	// Take a measurement
	Times.snapshot();
	
	systemEnd = Times.systemTime();
	userEnd = Times.userTime();

	systemDelta = systemEnd - systemStart;
	userDelta = userEnd - userStart;

	systemStart = systemEnd;
	userStart = userEnd;

	systemTotal += systemDelta;
	userTotal += userDelta;

	times.println("Call graph construction");
	times.println("  User: " + userDelta);
	times.println("  System: " + systemDelta);
      }

      if(SPECIALIZE) {
	specialize(context);
      }

      if(TIMES) {
	// Take a measurement
	Times.snapshot();
	
	systemEnd = Times.systemTime();
	userEnd = Times.userTime();

	systemDelta = systemEnd - systemStart;
	userDelta = userEnd - userStart;

	systemStart = systemEnd;
	userStart = userEnd;

	systemTotal += systemDelta;
	userTotal += userDelta;

	times.println("Call site specialization");
	times.println("  User: " + userDelta);
	times.println("  System: " + systemDelta);
      }

      if(INLINE) {
	inline(context);
      }

      if(TIMES) {
	// Take a measurement
	Times.snapshot();
	
	systemEnd = Times.systemTime();
	userEnd = Times.userTime();

	systemDelta = systemEnd - systemStart;
	userDelta = userEnd - userStart;

	systemStart = systemEnd;
	userStart = userEnd;

	systemTotal += systemDelta;
	userTotal += userDelta;

	times.println("Method inlining");
	times.println("  User: " + userDelta);
	times.println("  System: " + systemDelta);
      }

      if(PEEPHOLE) {
	peephole(context);
      }
    }

    // Commit dirty data
    tr("Committing dirty methods");
    context.commitDirty();

    if(TIMES) {
      // Take a measurement
      Times.snapshot();
      
      systemEnd = Times.systemTime();
      userEnd = Times.userTime();
      
      systemDelta = systemEnd - systemStart;
      userDelta = userEnd - userStart;
      
      systemStart = systemEnd;
      userStart = userEnd;
      
      systemTotal += systemDelta;
      userTotal += userDelta;
      
      times.println("Committal");
      times.println("  User: " + userDelta);
      times.println("  System: " + systemDelta);
    }

    if(TIMES) {
      times.println("Total");
      times.println("  User: " + userTotal);
      times.println("  System: " + systemTotal);
    }

    if(statsFile != null) {
      InlineStats stats = context.getInlineStats();
      PrintWriter statsOut = null;
      try {
	statsOut = new PrintWriter(new FileWriter(statsFile), true);
	
      } catch(IOException ex) {
	statsOut = new PrintWriter(System.out, true);
      }
      
      stats.printSummary(statsOut);
    }

    tr("Finished");
  }

  /**
   * Creates a <tt>BloatContext</tt> that loads classes from a given
   * CLASSPATH. 
   */
  static BloatContext makeContext(String classpath, 
				  String outputDirName) {
    ClassFileLoader loader = new ClassFileLoader();
    if(classpath != null) {
      loader.prependClassPath(classpath);
    }

    //      if(TRACE) {
    //        loader.setVerbose(true);
    //      }

    tr("  Creating a BloatContext for CLASSPATH: " +
       loader.getClassPath()); 

    if(outputDirName != null) {
      loader.setOutputDir(new File(outputDirName));
    }
    BloatContext context = 
      new CachingBloatContext(loader, CLASSES, true);

    // Always ignore the sun packages and the opj stuff for
    // interprocedural stuff
    if(!SUN) {
      context.addIgnorePackage("sun");
    }

    context.addIgnorePackage("java.lang.ref");
    context.addIgnorePackage("org.opj.system");

    if(USE1_1) {
      // Toba can't deal with java.lang.Character
      context.addIgnoreClass(Type.getType("Ljava/lang/Character;"));
    }

    return(context);
  }

  /**
   * Returns the live methods of a program whose root methods are the
   * <tt>main</tt> method of a set of classes.
   *
   * @param classes
   *        Names of classes containing root methods
   * @param outputDirName
   *        Name of directory in which BLOATed classes are placed
   * @param context
   *        Repository for accessing BLOAT stuff
   * @return The <tt>MemberRef</tt>s of the live methods
   */
  private static Collection liveMethods(Collection classes, 
					BloatContext context) {

    // Determine the roots of the call graph
    Set roots = new HashSet();
    Iterator iter = classes.iterator();
    while(iter.hasNext()) {
      String className = (String) iter.next();
      try {
	ClassEditor ce = context.editClass(className);
	MethodInfo[] methods = ce.methods();

	for(int i = 0; i < methods.length; i++) {
	    MethodEditor me = context.editMethod(methods[i]);
	    
	    if(!me.name().equals("main")) {
		continue;
	    }
	    
	    tr("  Root " + ce.name() + "." + me.name() + me.type());
	    roots.add(me.memberRef());
	}
      
      } catch(ClassNotFoundException ex1) {
	  err.println("** Could not find class: " + ex1.getMessage());
	  System.exit(1);
      }
    }

    if(roots.isEmpty()) {
      err.print("** No main method found in classes: ");
      iter = classes.iterator();
      while(iter.hasNext()) {
	String name = (String) iter.next();
	err.print(name);
	if(iter.hasNext()) {
	  err.print(", ");
	}
      }
      err.println("");
    }

    context.setRootMethods(roots);
    CallGraph cg = context.getCallGraph();

    Set liveMethods = new TreeSet(new MemberRefComparator());
    liveMethods.addAll(cg.liveMethods());

    return(liveMethods);
  }

  /**
   * Specializes the live methods in a program.
   */
  private static void specialize(BloatContext context) {

    CallGraph cg = context.getCallGraph();

    Set liveMethods = new TreeSet(new MemberRefComparator());
    liveMethods.addAll(cg.liveMethods());

    // Specialize all possible methods
    InlineStats stats = context.getInlineStats();

    if(statsFile != null) {
      Specialize.STATS = true;
      stats.setConfigName("BloatBenchmark");
    }

    if(MORPH != -1) {
      Specialize.MAX_MORPH = MORPH;
    }
    Specialize spec = new Specialize(context);

    if(Specialize.STATS) {
      stats.noteLiveMethods(liveMethods.size());
      stats.noteLiveClasses(cg.liveClasses().size());
    }

    tr("Specializing live methods");
    Iterator iter = liveMethods.iterator();

    for(int count = 0; iter.hasNext(); count++) {
      try {
        MethodEditor live = context.editMethod((MemberRef) iter.next());

	if(context.ignoreMethod(live.memberRef())) {
	  // Don't display ignored methods, it's misleading.
	  continue;
	}

        tr("  " + count + ") "+ live.declaringClass().name() + "." +
	   live.name() + live.type());

        spec.specialize(live);

      } catch(NoSuchMethodException ex2) {
        err.println("** Could not find method " + ex2.getMessage());
        System.exit(1);
      }
    }
  }

  /**
   * Inlines calls to static methods in the live methods of a given
   * program.
   */
  private static void inline(BloatContext context) {

    Set liveMethods = new TreeSet(new MemberRefComparator());
    CallGraph cg = context.getCallGraph();
    liveMethods.addAll(cg.liveMethods());

    tr("Inlining " + liveMethods.size() + " live methods");

    if(CALLEE_SIZE != -1) {
      Inline.CALLEE_SIZE = CALLEE_SIZE;
    }

    Iterator iter = liveMethods.iterator();
    for(int count = 0; INLINE && iter.hasNext(); count++) {
      try {
        MethodEditor live = context.editMethod((MemberRef) iter.next());

	if(context.ignoreMethod(live.memberRef())) {
	  // Don't display ignored methods, it's misleading.
	  continue;
	}

	tr("  " + count + ") "+ live.declaringClass().name() + "." +
	   live.name() + live.type());

	Inline inline = new Inline(context, SIZE);
	inline.setMaxCallDepth(DEPTH);
	inline.inline(live);

	// Commit here in an attempt to conserve memory
	context.commit(live.methodInfo());
	context.release(live.methodInfo());

      } catch(NoSuchMethodException ex3) {
        err.println("** Could not find method " + ex3.getMessage());
        System.exit(1);
      }
    }
  }

  /**
   * Performs peephole optimizations on a program's live methods.
   */
  private static void peephole(BloatContext context) {
   
    Set liveMethods = new TreeSet(new MemberRefComparator());
    CallGraph cg = context.getCallGraph();
    liveMethods.addAll(cg.liveMethods());

    // Perform peephole optimizations.  We do this separately because
    // some peephole optimizations do things to the stack that
    // inlining doesn't like.  For instance, a peephole optimizations
    // might make it so that a method has a non-empty stack upon
    // return.  Inlining will barf at the sight of this.
    tr("Performing peephole optimizations");

    Iterator iter = liveMethods.iterator();
    while(PEEPHOLE && iter.hasNext()) {
      try {
	MethodEditor live = context.editMethod((MemberRef) iter.next());
	Peephole.transform(live);
	context.commit(live.methodInfo());
	context.release(live.methodInfo());

      } catch(NoSuchMethodException ex314) {
	err.println("** Could not find method " + ex314.getMessage());
	ex314.printStackTrace(System.err);
	System.exit(1);
      }
    }
  }

  /**
   * Performs intraprocedural BLOAT on a program's live methods.
   *
   * @param liveMethods
   *        Should be alphabetized.  This way we can commit a class
   *        once we've BLOATed all of its methods.
   */
  private static void intraBloat(Collection liveMethods, 
				 BloatContext context) {

    ClassEditor prevClass = null;
    Iterator iter = liveMethods.iterator();
    for(int count = 0; iter.hasNext(); count++) {
      MethodEditor live = null;
      ClassEditor ce = null;  // Hack to make sure commit happens
      try {
	live = context.editMethod((MemberRef) iter.next());
	ce = context.editClass(live.declaringClass().classInfo());

      } catch(NoSuchMethodException ex3) {
	err.println("** Could not find method " + ex3.getMessage());
	System.exit(1);
      }

      /* So we can skip classes or packages */
      String name = ce.type().className();
      String qual = ce.type().qualifier() + "/*";
      boolean skip = false;
      for (int i = 0; i < SKIP.size(); i++) {
	  String pkg = (String) SKIP.get(i);
	  
	  if (name.equals(pkg) || qual.equals(pkg)) {
	      skip = true;
	      break;
	  }
      }


      if(context.ignoreMethod(live.memberRef()) || skip) {
	// Don't display ignored methods, it's misleading.
	context.release(live.methodInfo());
	continue;
      }
	
      Runtime runtime = Runtime.getRuntime();
      runtime.gc();

      Date start = new Date();
      tr("  " + count + ") "+ live.declaringClass().name() + "." +
	 live.name() + live.type());
      tr("    Start: " + start);

      try {
	EDU.purdue.cs.bloat.optimize.Main.TRACE = TRACE;
	if(!VERIFY) {
	  EDU.purdue.cs.bloat.optimize.Main.VERIFY = false;
	}
	EDU.purdue.cs.bloat.optimize.Main.bloatMethod(live, context);
	
      } catch(Exception oops) {
	err.println("******************************************");
	err.println("Exception while BLOATing " +
		    live.declaringClass().name() + "." +
		    live.name() + live.type());
	err.println(oops.getMessage());
	oops.printStackTrace(System.err);
	err.println("******************************************");
      }
	
      // Commit here in an attempt to conserve memory
      context.commit(live.methodInfo());
      context.release(live.methodInfo());

      if(prevClass == null) {
	prevClass = ce;

      } else if(!prevClass.equals(ce)) {
	// We've finished BLOATed the methods for prevClass, commit
	// prevClass and move on
	tr(prevClass.type() + " != " + ce.type());
	context.commit(prevClass.classInfo());
	context.release(prevClass.classInfo());
	//	context.commitDirty();
	//  	tr(context.toString());
	prevClass = ce;

      } else {
	context.release(ce.classInfo());
      }

      Date end = new Date();
      tr("    Ellapsed time: " + (end.getTime() - start.getTime()) + " ms");
    }

    context.commitDirty();
  }

  /**
   * Performs "old school" BLOAT on a method.
   */
  private static void bloatMethod(BloatContext context, 
				  MethodEditor method) {

    tr("    Array initialization compaction");
    CompactArrayInitializer.transform(method);

    tr("    Constructing CFG");

    FlowGraph cfg = null;

    try {
      cfg = new FlowGraph(method);
      cfg.initialize();

    } catch(ClassFormatException ex) {
      System.err.println(ex.getMessage());
      return;
    }

    tr("    Converting into SSA form");
    SSA.transform(cfg);

    tr("    Constant/Copy Propagation 1");
    ExprPropagation copy = new ExprPropagation(cfg);
    copy.transform();

    tr("    Dead code elimination 1");
    DeadCodeElimination dce = new DeadCodeElimination(cfg);
    dce.transform();

    tr("    Type inference");
    (new TypeInference()).transform(cfg, context.getHierarchy());

    tr("    Value numbering");
    (new ValueNumbering()).transform(cfg);

    tr("    Value folding 1");
    (new ValueFolding()).transform(cfg);

    tr("    SSAPRE");
    SSAPRE pre = new SSAPRE(cfg, context);
    pre.transform();

    tr("    Value folding 2");
    (new ValueFolding()).transform(cfg);

    tr("    Constant/Copy Propagation 2");
    ExprPropagation copy2 = new ExprPropagation(cfg);
    copy2.transform();

    tr("    Dead code elimination 2");
    DeadCodeElimination dce2 = new DeadCodeElimination(cfg);
    dce2.transform();

    tr("    Register allocation");
    Liveness liveness = new Liveness(cfg);
    RegisterAllocator alloc = new RegisterAllocator(cfg, liveness);

    if(OPT_STACK) {
      tr("    Stack optimizations");
      StackOptimizer.optimizeCFG(cfg);
    }
    
 
    tr("    Code generation");
    CodeGenerator codegen = new CodeGenerator(cfg.method());
    codegen.replacePhis(cfg);
    codegen.simplifyControlFlow(cfg);
    codegen.allocReturnAddresses(cfg, alloc);
    cfg.method().clearCode();
    cfg.visit(codegen);
    Peephole.transform(cfg.method());
  }

  /**
   * Checks to make sure that the chosen options make sense.
   */
  private static void checkOptions() {
    if(!INTRA && !SPECIALIZE && !INLINE) {
      err.println("** There is nothing to do!");
      usage();

    } else if(MORPH != -1 && !SPECIALIZE) {
      err.println("** Must specialize when setting morphosity");
      usage();
    }
  }

  private static class MemberRefComparator implements Comparator {
    public int compare(Object o1, Object o2) {
      Assert.isTrue(o1 instanceof MemberRef, o1 + 
		    " is not a MemberRef!");
      Assert.isTrue(o2 instanceof MemberRef, o2 + 
		    " is not a MemberRef!");
    
      MemberRef me1 = (MemberRef) o1;
      MemberRef me2 = (MemberRef) o2;
    
      String s1 = me1.declaringClass() + "." + me1.name() +
	me1.type();
      String s2 = me2.declaringClass() + "." + me2.name() +
	me2.type();

      return(s1.compareTo(s2));
    }

    public boolean equals(Object other) {
      return(true);
    }
  }
}
