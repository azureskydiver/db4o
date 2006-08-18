package db4ounit;

import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class TestPlatform {
	public static void printStackTrace(Writer writer, Throwable t) {
		java.io.PrintWriter printWriter = new java.io.PrintWriter(writer);
		t.printStackTrace(printWriter);
		printWriter.flush();
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
	
	public static boolean isMalformedTestMethod(Method method) {
		final int modifiers = method.getModifiers();
		
		final boolean notPublic = !Modifier.isPublic(modifiers);
		final boolean staticc = Modifier.isStatic(modifiers);
		final boolean hasParams = method.getParameterTypes().length > 0;
		
		return method.getName().startsWith("test")			
			&& (notPublic
					|| staticc
					|| hasParams);
	}
}
