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

package EDU.purdue.cs.bloat.benchmark;

import EDU.purdue.cs.bloat.reflect.*;
import EDU.purdue.cs.bloat.file.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.tbaa.*;
import EDU.purdue.cs.bloat.util.*;
import EDU.purdue.cs.bloat.context.*;

import java.io.*;
import java.util.*;
import java.util.zip.*;

public class CounterDecorate implements Opcode
{
    private static final String COUNTER_TYPE = "I";
    private static final String COUNTER_RCNAME = "rcCount";
    private static final String COUNTER_AUNAME = "auCount";
    private static final String COUNTER_SUNAME = "suCount";
    private static final String COUNTER_MAIN =
	"LEDU/purdue/cs/bloat/benchmark/Counter;";

    private static int VERBOSE = 0;
    private static boolean FORCE = false;
    private static boolean CLOSURE = false;
    private static List SKIP = new ArrayList();
    private static List ONLY = new ArrayList();

    public static void main(String[] args)
    {
	ClassFileLoader loader = new ClassFileLoader();
	List classes = new ArrayList();
	boolean gotdir = false;

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
	    else if (args[i].startsWith("-")) {
		usage();
	    }
	    else if (i == args.length-1) {
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

	BloatContext context = new CachingBloatContext(loader, classes, 
						       CLOSURE);

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

    private static void usage()
    {
	System.err.println(
	"Usage: java EDU.purdue.cs.bloat.count.Main " +
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
	"\n                      (this option can be given more than once)");
	System.exit(0);
    }

    private static void decorateClass(EditorContext editor, ClassInfo info)
    {
	ClassFile classFile = (ClassFile) info;

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

	ClassEditor c = editor.editClass(info);

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

	    editor.release(info);
	    return;
	}

	if (VERBOSE > 0) {
	    System.out.println("Decorating class " + c.type().className());
	}

	if (VERBOSE > 2) {
	    ((ClassFile) info).print(System.out);
	}

	MethodInfo[] methods = c.methods();

	for (int j = 0; j < methods.length; j++) {
	    MethodEditor m;

	    try {
		m = editor.editMethod(methods[j]);
	    }
	    catch (ClassFormatException ex) {
		System.err.println(ex.getMessage());
		continue;
	    }

	    transform(m);
	    editor.commit(methods[j]);
	}

	editor.commit(info);
    }

    private static void transform(MethodEditor method)
    {
	if (VERBOSE > 1) {
	    System.out.println("Decorating method " + method);
	}

	MemberRef rcfield = new MemberRef(Type.getType(COUNTER_MAIN),
	    new NameAndType(COUNTER_RCNAME, Type.getType(COUNTER_TYPE)));
	MemberRef aufield = new MemberRef(Type.getType(COUNTER_MAIN),
	    new NameAndType(COUNTER_AUNAME, Type.getType(COUNTER_TYPE)));
	MemberRef sufield = new MemberRef(Type.getType(COUNTER_MAIN),
	    new NameAndType(COUNTER_SUNAME, Type.getType(COUNTER_TYPE)));

	ListIterator iter = method.code().listIterator(); 

	INST:
	while (iter.hasNext()) {
	    Object ce = iter.next();

	    if (VERBOSE > 2) {
		System.out.println("Examining " + ce);
	    }

	    if (ce instanceof Instruction) {
		Instruction inst = (Instruction) ce;

		if (inst.opcodeClass() == opcx_aupdate) {
		    iter.remove();
		    iter.add(new Instruction(opcx_getstatic, aufield));
		    iter.next();
		    iter.add(new Instruction(opcx_ldc, new Integer(1)));
		    iter.next();
		    iter.add(new Instruction(opcx_iadd));
		    iter.next();
		    iter.add(new Instruction(opcx_putstatic, aufield));
		    iter.next();
		}
		if (inst.opcodeClass() == opcx_supdate) {
		    iter.remove();
		    iter.add(new Instruction(opcx_getstatic, sufield));
		    iter.next();
		    iter.add(new Instruction(opcx_ldc, new Integer(1)));
		    iter.next();
		    iter.add(new Instruction(opcx_iadd));
		    iter.next();
		    iter.add(new Instruction(opcx_putstatic, sufield));
		    iter.next();
		}
		else if (inst.opcodeClass() == opcx_rc) {
		    iter.remove();
		    iter.add(new Instruction(opcx_getstatic, rcfield));
		    iter.next();
		    iter.add(new Instruction(opcx_ldc, new Integer(1)));
		    iter.next();
		    iter.add(new Instruction(opcx_iadd));
		    iter.next();
		    iter.add(new Instruction(opcx_putstatic, rcfield));
		    iter.next();
		}
	    }
	}
    }
}
