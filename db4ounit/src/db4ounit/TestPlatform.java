package db4ounit;

import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Vector;

public class TestPlatform {
	public static void printStackTrace(Writer writer, Throwable t) {
		java.io.PrintWriter printWriter = new java.io.PrintWriter(writer);
		t.printStackTrace(printWriter);
		printWriter.flush();
	}

	public static Writer getStdOut() {
		return new PrintWriter(System.out);
	}

	public static Method[] getAllMethods(Class clazz) {
		Vector methods = new Vector();
		collectDeclaredMethods(methods, clazz);
		return toArray(methods);
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
	
	private static Method[] toArray(Vector methods) {
		Method[] array = new Method[methods.size()];
		methods.copyInto(array);
		return array;
	}

	private static void collectDeclaredMethods(Vector methods, Class clazz) {
		Method[] declaredMethods = clazz.getDeclaredMethods();
		for (int i=0; i<declaredMethods.length; ++i) {
			methods.addElement(declaredMethods[i]);
		}
		final Class superClass = clazz.getSuperclass();
		if (superClass != Object.class) {
			collectDeclaredMethods(methods, clazz.getSuperclass());
		}
	}
}
