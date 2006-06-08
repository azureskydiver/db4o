package db4ounit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestPlatform4 {	
	
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
}
