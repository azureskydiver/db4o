package db4ounit;

import java.io.*;
import java.lang.reflect.*;

/**
 * @sharpen.ignore
 */
public class TestPlatform {
	public static void printStackTrace(Writer writer, Throwable t) {
		java.io.PrintWriter printWriter = new java.io.PrintWriter(writer);
		t.printStackTrace(printWriter);
		printWriter.flush();
	}

	public static Writer getStdOut() {
		return new PrintWriter(System.out);
	}
	
	public static boolean isStatic(Method method) {
		return Modifier.isStatic(method.getModifiers());
	}

	public static boolean isPublic(Method method) {
		return Modifier.isPublic(method.getModifiers());
	}

	public static boolean hasParameters(Method method) {
		return method.getParameterTypes().length > 0;
	}

	public static void emitWarning(String warning) {
		System.err.println(warning);
	}
}
