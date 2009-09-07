package com.yetac.doctor.runner;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

public class ExampleRunner {

	private final static int SERVERPORT=0xdb40;
    private final static String SERVERUSER="user";
    private final static String SERVERPASSWORD="password";
    
    private final ClassLoader classLoader;
    private final File databaseFile;
    private final Class db4oEmbeddedClass;
    private final Class db4oClientServerClass;
    private final Class objectContainerClass;
    private final Class objectServerClass;
    
    private Map sig2exec;
    
    public ExampleRunner(ClassLoader classLoader, File databaseFile) throws Exception {
        this.classLoader = classLoader;
        this.databaseFile = databaseFile;
        this.db4oEmbeddedClass = classLoader.loadClass("com.db4o.Db4o");
        this.db4oClientServerClass = classLoader.loadClass("com.db4o.cs.Db4oClientServer");
        this.objectContainerClass = classLoader.loadClass("com.db4o.ObjectContainer");
        this.objectServerClass = classLoader.loadClass("com.db4o.ObjectServer");
        setupExecutors();
    }

    private void setupExecutors() {
        Executor[] executors={
            new PlainExecutor(),
            new ObjectContainerExecutor(),
            new ObjectServerExecutor(0),
            new ObjectServerExecutor(SERVERPORT)
            
        };
        sig2exec=new HashMap();
        for (int idx=0;idx<executors.length;idx++) {
            sig2exec.put(new Signature(executors[idx].getSignature(classLoader)),executors[idx]);
        }
    }
    
    public final class Signature {
    	
		public final Class[] types;
		
		private Integer hashCode;

		public Signature(Class[] types) {
			this.types = types;
        }
		
		public int hashCode() {
			if (null == hashCode) {
				hashCode = new Integer(hashCode(types));
			}
			return hashCode.intValue();
        }

		public boolean equals(Object obj) {
			Signature other = (Signature)obj;
			if (types.length != other.types.length)
				return false;
			for (int i=0; i<types.length; ++i)
				if (types[i] != other.types[i])
					return false;
			return true;
		}
		
		private int hashCode(Object[] array) {
	        int prime = 31;
	        if (array == null)
		        return 0;
	        int result = 1;
	        for (int index = 0; index < array.length; index++) {
		        result = prime * result + (array[index] == null ? 0 : array[index].hashCode());
	        }
	        return result;
        }
    }

    public void reset() {
        boolean result=databaseFile.delete();
        System.err.println("Deleted "+databaseFile.getAbsolutePath()+" : "+result);
    }
    
    public void runExample(String exampleClassName, String methodname, OutputStream out) throws Exception {
        Class exampleClass= classLoader.loadClass(exampleClassName);
        PrintStream newOut=new PrintStream(out);
        PrintStream origOut=System.out;
        System.setOut(newOut);
        try {
            execute(exampleClass,methodname);
            System.out.flush();
        } 
        finally {
            System.setOut(origOut);
        }
    }

    private void execute(Class exampleClass, String methodname) throws Exception {
    	Method exampleMethod = findMethod(exampleClass, methodname);
        Executor exec=(Executor)sig2exec.get(new Signature(exampleMethod.getParameterTypes()));
        exec.execute(exampleMethod);
    }

	private Method findMethod(Class exampleClass, String methodName) {
        Iterator sigiter=sig2exec.keySet().iterator();
		while (sigiter.hasNext()) {
            Signature sig=(Signature)sigiter.next();
            try {
	            return exampleClass.getMethod(methodName, sig.types);
            } catch (NoSuchMethodException e) {
            }
        }
		throw new IllegalArgumentException("Method '" + methodName + "' not found on " + exampleClass);
    }

    private abstract class Executor {
        public void execute(Method exampleMethod) throws Exception{
            configure();
            executeInternal(exampleMethod);
        }
        
        // TODO: setCL() is deprecated, replace with reflectWith() call
        private void configure() throws Exception {
            try {
				Object configuration = applyMethod(db4oEmbeddedClass, "configure", db4oEmbeddedClass, new Class[] {}, new Object[]{});
				Class configurationClass = classLoader.loadClass("com.db4o.config.Configuration");
                applyMethod(configurationClass, "setClassLoader", configuration, new Class[] { Object.class }, new Object[] { classLoader });
                applyMethod(configurationClass, "allowVersionUpdates", configuration, new Class[] { boolean.class }, new Object[] { new Boolean(true)});
			} catch (Exception exc) {
				exc.printStackTrace();
				throw exc;
			}
        }

        protected Object applyMethod(Class clazz,String methodName,Object target,Class[] argtypes,Object[] params) throws Exception {
            Method method = clazz.getMethod(methodName,argtypes);
            return method.invoke(target,params);
        }
        
        protected abstract Class[] getSignature(ClassLoader classLoader);
        protected abstract void executeInternal(Method exampleMethod) throws Exception;
    }

    private final class PlainExecutor extends Executor {
        protected Class[] getSignature(ClassLoader classLoader) {
            return new Class[]{};
        }

        protected void executeInternal(Method exampleMethod) throws Exception {
            exampleMethod.invoke(null, new Object[] {});
        }
    }    

    private final class ObjectContainerExecutor extends Executor {
        protected Class[] getSignature(ClassLoader classLoader) {
            return new Class[]{objectContainerClass};
        }

        protected void executeInternal(Method exampleMethod) throws Exception {
            Object db=null;
            try {
                db=applyMethod(db4oEmbeddedClass,"openFile",null,new Class[]{String.class},new Object[]{ExampleRunner.this.databaseFile.getAbsolutePath()});
                exampleMethod.invoke(null, new Object[] { db });
            }
            finally {
                if(db!=null) {
                    applyMethod(objectContainerClass,"close",db,null,null);
                }
            }
        }
    }

    private final class ObjectServerExecutor extends Executor {
        private int port;
        
        public ObjectServerExecutor(int port) {
            this.port=port;
        }
        
        protected Class[] getSignature(ClassLoader classLoader) {
            return (isWithNetworking() ? new Class[]{Integer.TYPE,String.class,String.class} : new Class[]{objectServerClass});
        }

        protected void executeInternal(Method exampleMethod) throws Exception {
            Object server=null;
            try {
                server=applyMethod(db4oClientServerClass,"openServer",null,new Class[] { String.class,Integer.TYPE },new Object[] { ExampleRunner.this.databaseFile.getAbsolutePath(),new Integer(port) });
                if(!isWithNetworking()) {
                    exampleMethod.invoke(null, new Object[] { server });
                }
                else {
                    applyMethod(objectServerClass,"grantAccess",server,new Class[] { String.class,String.class },new Object[]{SERVERUSER,SERVERPASSWORD});
                    exampleMethod.invoke(null, new Object[] { new Integer(port),SERVERUSER,SERVERPASSWORD });
                }
            }
            finally {
                if(server!=null) {
                    applyMethod(objectServerClass,"close",server,null,null);
                }
            }
        }
        
        private boolean isWithNetworking() {
            return port>0;
        }
    }
}
