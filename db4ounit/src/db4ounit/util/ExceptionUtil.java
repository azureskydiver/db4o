/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package db4ounit.util;

import java.lang.reflect.*;

/**
 * @sharpen.ignore
 */
public class ExceptionUtil {
	public static Throwable getExceptionCause(Throwable e) {
		try {
			Method method = e.getClass().getMethod("getCause", new Class[0]);
			return (Throwable) method.invoke(e, new Object[0]);
		} catch (Exception exc) {
			return null;
		}
	}
}
