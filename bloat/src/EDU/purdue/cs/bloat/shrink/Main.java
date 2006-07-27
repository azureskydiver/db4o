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

package EDU.purdue.cs.bloat.shrink;

import java.io.*;
import java.util.*;

import EDU.purdue.cs.bloat.context.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.file.*;
import EDU.purdue.cs.bloat.reflect.*;
import EDU.purdue.cs.bloat.trans.*;

/**
 * This program just performs array initialization compaction on a class. It
 * really doesn't seem necessary anymore.
 */
public class Main implements Opcode {
	private static int VERBOSE = 0;

	private static boolean FORCE = false;

	private static boolean CLOSURE = false;

	private static final List SKIP = new ArrayList();

	private static final List ONLY = new ArrayList();

	public static void main(final String[] args) {
		final ClassFileLoader loader = new ClassFileLoader();
		List classes = new ArrayList();
		boolean gotdir = false;

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-v") || args[i].equals("-verbose")) {
				Main.VERBOSE++;
			} else if (args[i].equals("-help")) {
				Main.usage();
			} else if (args[i].equals("-classpath/p")) {
				if (++i >= args.length) {
					Main.usage();
				}

				final String classpath = args[i];
				loader.prependClassPath(classpath);
			} else if (args[i].equals("-classpath")) {
				if (++i >= args.length) {
					Main.usage();
				}

				final String classpath = args[i];
				loader.setClassPath(classpath);
			} else if (args[i].equals("-skip")) {
				if (++i >= args.length) {
					Main.usage();
				}

				final String pkg = args[i].replace('.', '/');
				Main.SKIP.add(pkg);
			} else if (args[i].equals("-only")) {
				if (++i >= args.length) {
					Main.usage();
				}

				final String pkg = args[i].replace('.', '/');
				Main.ONLY.add(pkg);
			} else if (args[i].equals("-closure")) {
				Main.CLOSURE = true;
			} else if (args[i].equals("-relax-loading")) {
				ClassHierarchy.RELAX = true;
			} else if (args[i].equals("-f")) {
				Main.FORCE = true;
			} else if (args[i].startsWith("-")) {
				Main.usage();
			} else if (i == args.length - 1) {
				final File f = new File(args[i]);

				if (f.exists() && !f.isDirectory()) {
					System.err.println("No such directory: " + f.getPath());
					System.exit(2);
				}

				loader.setOutputDir(f);
				gotdir = true;
			} else {
				classes.add(args[i]);
			}
		}

		if (!gotdir) {
			Main.usage();
		}

		if (classes.size() == 0) {
			Main.usage();
		}

		if (Main.VERBOSE > 3) {
			ClassFileLoader.DEBUG = true;
			CompactArrayInitializer.DEBUG = true;
			ClassEditor.DEBUG = true;
		}

		boolean errors = false;

		final Iterator iter = classes.iterator();

		while (iter.hasNext()) {
			final String name = (String) iter.next();

			try {
				loader.loadClass(name);
			} catch (final ClassNotFoundException ex) {
				System.err.println("Couldn't find class: " + ex.getMessage());
				errors = true;
			}
		}

		if (errors) {
			System.exit(1);
		}

		final BloatContext context = new CachingBloatContext(loader, classes,
				Main.CLOSURE);

		if (!Main.CLOSURE) {
			final Iterator e = classes.iterator();

			while (e.hasNext()) {
				final String name = (String) e.next();
				try {
					final ClassInfo info = loader.loadClass(name);
					Main.editClass(context, info);
				} catch (final ClassNotFoundException ex) {
					System.err.println("Couldn't find class: "
							+ ex.getMessage());
					System.exit(1);
				}
			}
		} else {
			classes = null;

			final ClassHierarchy hier = context.getHierarchy();

			final Iterator e = hier.classes().iterator();

			while (e.hasNext()) {
				final Type t = (Type) e.next();

				if (t.isObject()) {
					try {
						final ClassInfo info = loader.loadClass(t.className());
						Main.editClass(context, info);
					} catch (final ClassNotFoundException ex) {
						System.err.println("Couldn't find class: "
								+ ex.getMessage());
						System.exit(1);
					}
				}
			}
		}
	}

	private static void usage() {
		System.err
				.println("Usage: java EDU.purdue.cs.bloat.shrink.Main "
						+ "\n            [-options] classes output_dir"
						+ "\n"
						+ "\nwhere options include:"
						+ "\n    -help             print out this message"
						+ "\n    -v -verbose       turn on verbose mode "
						+ "(can be given multiple times)"
						+ "\n    -classpath <directories separated by colons>"
						+ "\n                      list directories in which to look for classes"
						+ "\n    -f                decorate files even if up-to-date"
						+ "\n    -closure          recursively decorate referenced classes"
						+ "\n    -relax-loading    don't report errors if a class is not found"
						+ "\n    -skip <class|package.*>"
						+ "\n                      skip the given class or package"
						+ "\n                      (this option can be given more than once)"
						+ "\n    -only <class|package.*>"
						+ "\n                      skip all but the given class or package"
						+ "\n                      (this option can be given more than once)");
		System.exit(0);
	}

	private static void editClass(final EditorContext editor,
			final ClassInfo info) {
		final ClassFile classFile = (ClassFile) info;

		if (!Main.FORCE) {
			final File source = classFile.file();
			final File target = classFile.outputFile();

			if ((source != null) && (target != null) && source.exists()
					&& target.exists()
					&& (source.lastModified() < target.lastModified())) {

				if (Main.VERBOSE > 1) {
					System.out.println(classFile.name() + " is up to date");
				}

				return;
			}
		}

		if (Main.VERBOSE > 2) {
			classFile.print(System.out);
		}

		final ClassEditor c = editor.editClass(info);

		boolean skip = false;

		final String name = c.type().className();
		final String qual = c.type().qualifier() + "/*";

		// Edit only classes explicitly mentioned.
		if (Main.ONLY.size() > 0) {
			skip = true;

			// Only edit classes we explicitly don't name.
			for (int i = 0; i < Main.ONLY.size(); i++) {
				final String pkg = (String) Main.ONLY.get(i);

				if (name.equals(pkg) || qual.equals(pkg)) {
					skip = false;
					break;
				}
			}
		}

		// Don't edit classes we explicitly skip.
		if (!skip) {
			for (int i = 0; i < Main.SKIP.size(); i++) {
				final String pkg = (String) Main.SKIP.get(i);

				if (name.equals(pkg) || qual.equals(pkg)) {
					skip = true;
					break;
				}
			}
		}

		if (skip) {
			if (Main.VERBOSE > 0) {
				System.out.println("Skipping " + c.type().className());
			}

			editor.release(info);
			return;
		}

		if (Main.VERBOSE > 0) {
			System.out.println("Decorating class " + c.type().className());
		}

		if (Main.VERBOSE > 2) {
			((ClassFile) info).print(System.out);
		}

		boolean changed = false;

		final MethodInfo[] methods = c.methods();

		for (int j = 0; j < methods.length; j++) {
			MethodEditor m;

			try {
				m = editor.editMethod(methods[j]);
			} catch (final ClassFormatException ex) {
				System.err.println(ex.getMessage());
				continue;
			}

			if (CompactArrayInitializer.transform(m)) {
				changed = true;

				if (Main.VERBOSE > 2) {
					System.out.println("commit " + m.name() + " " + m.type());
				}

				editor.commit(methods[j]);
			} else {
				if (Main.VERBOSE > 2) {
					System.out.println("release " + m.name() + " " + m.type());
				}

				editor.release(methods[j]);
			}
		}

		if (changed) {
			editor.commit(info);
		}
	}
}
