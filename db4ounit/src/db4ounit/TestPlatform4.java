package db4ounit;

import java.lang.reflect.*;

import com.db4o.foundation.*;

public class TestPlatform4 {
	
	public static String[] testMethodNames(Object clazz) {
		Collection4 names=new Collection4();
		Method[] methods=((Class)clazz).getMethods();
		for (int i = 0; i < methods.length; i++) {
			if(isTestMethod(methods[i])) {
				names.add(methods[i].getName());
			}
		}
		String[] ret=new String[names.size()];
		names.toArray(ret);
		return ret;
	}
	
	private static boolean isTestMethod(Method method) {
		return method.getName().startsWith("test")
			&& (method.getModifiers() & Modifier.PUBLIC)!=0
			&& method.getParameterTypes().length==0;
	}

	public static void runMethod(Object onObject,String methodName,Class[] params,Object[] args) throws Exception {
		Method method=onObject.getClass().getMethod(methodName,params);
		try {
			method.invoke(onObject,args);
		}
		catch(InvocationTargetException exc) {
			Throwable target=exc.getTargetException();
			if(target instanceof Exception) {
				throw (Exception)target;
			}
			if(target instanceof Error) {
				throw (Error)target;
			}
			throw exc;
		}
	}

	public static Object create(Object clazz, Object[] paramTypes,Object[] args) {
		try {
			Constructor constr=((Class)clazz).getConstructor((Class[])paramTypes);
			return constr.newInstance(args);
		} catch (Exception exc) {
		}
		return null;
	}
}
