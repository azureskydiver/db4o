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

/**
 * This class is used to run a benchmark Java program with Perfmon running in
 * the background. Perfmon is a software package developed at Michigan State
 * University that allows user-level programs to access the hardware counters on
 * Sparc processors.
 * 
 * <p>
 * 
 * The <tt>main</tt> method of this class takes several arguments (note that
 * the first four arguments are mutually exclusive):
 * 
 * <pre>
 *    -inst-load-stall       Count load interlock induced stalls
 *    -dcache                Count data cache hit rate
 *    -cycle-ic-miss-stall   Count I-cache miss induced stalls (and cycles)
 *    -inst-cycle            Count instructions (and cycles)
 * 
 *    -run n                 How many times is the program run
 * 
 *    class                  Java class to run (the benchmark)
 *    args                   Arguments to benchmark class
 * </pre>
 * 
 * The real work is done by the native <tt>run</tt> method that is implemented
 * in benchmark.c.
 * 
 * @see BenchmarkSecurityManager
 */
public class Benchmark {
	static {
		// Load native code from libbenchmark.so
		System.loadLibrary("benchmark");
	}

	public static native void init(Class main);

	public static native void run(Class main, String[] args);

	public static native void setMode(int mode);

	public static void main(final String[] args) throws Exception {
		int mode = 0;

		int runs = 1;
		int eat = 0;

		if (args.length <= 1) {
			Benchmark.usage();
		}

		for (eat = 0; eat < args.length; eat++) {
			if (args[eat].equals("-inst-cycle")) {
				mode = 3;
			} else if (args[eat].equals("-inst-load-stall")) {
				mode = 0;
			} else if (args[eat].equals("-dcache")) {
				mode = 1;
			} else if (args[eat].equals("-cycle-ic-miss-stall")) {
				mode = 2;
			} else if (args[eat].equals("-run")) {
				if (++eat >= args.length) {
					Benchmark.usage();
				}

				runs = Integer.parseInt(args[eat]);

				if (runs <= 0) {
					Benchmark.usage();
				}
			} else {
				// The main class
				eat++;
				break;
			}
		}

		/* Got all the args. */
		if (eat > args.length) {
			Benchmark.usage();
		}

		final BenchmarkSecurityManager sec = new BenchmarkSecurityManager();
		System.setSecurityManager(sec);

		final String mainClassName = args[eat - 1];
		final String[] a = new String[args.length - eat];

		System.err.println("Running " + mainClassName + " in mode " + mode);
		Benchmark.setMode(mode);

		final Class mainClass = Class.forName(mainClassName);
		Benchmark.init(mainClass);

		for (int i = 0; i < runs; i++) {
			try {
				System.arraycopy(args, eat, a, 0, a.length);
				Benchmark.run(mainClass, a);
			} catch (final SecurityException e) {
				continue;
			} catch (final Exception e) {
				e.printStackTrace(System.err);
				sec.allowExit = true;
				System.exit(1);
			}
		}

		sec.allowExit = true;
	}

	private static void usage() {
		System.err.print("usage: java EDU.purdue.cs.bloat.Benchmark ");
		System.err.println("options class args...");
		System.err.println("where options are one of:");
		System.err.println("    -inst-load-stall");
		System.err.println("    -inst-cycle");
		System.err.println("    -cycle-ic-miss-stall");
		System.err.println("    -dcache");
		System.err.println("    -run n");
		System.exit(1);
	}
}
