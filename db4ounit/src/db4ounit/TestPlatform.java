package db4ounit;

import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class TestPlatform {
	public static void printStackTrace(Writer writer, Throwable t) {
		t.printStackTrace(new java.io.PrintWriter(writer));
	}

	public static Writer getStdOut() {
		return new PrintWriter(System.out);
	}

	public static boolean isTestMethod(Method method) {
		return method.getName().startsWith("test")			
			&& Modifier.isPublic(method.getModifiers())
			&& !Modifier.isStatic(method.getModifiers())
			&& method.getParameterTypes().length == 0;
	}
}
