package com.yetac.doctor.applet;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

public class ExampleRunner {
    private final static int SERVERPORT=0xdb40;
    private final static String SERVERUSER="user";
    private final static String SERVERPASSWORD="password";
    
    private final ClassLoader classLoader;
    private final File yapFile;
    private final Class db4oClass;
    private final Class objectContainerClass;
    private final Class objectServerClass;
    
    private Map sig2exec;
    
    public ExampleRunner(ClassLoader classLoader, File yapFile) throws Exception {
        this.classLoader = classLoader;
        this.yapFile=yapFile;
        this.db4oClass = classLoader.loadClass("com.db4o.Db4o");
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
            sig2exec.put(executors[idx].getSignature(classLoader),executors[idx]);
        }
    }

    public void reset() {
        boolean result=yapFile.delete();
        System.err.println("Deleted "+yapFile.getAbsolutePath()+" : "+result);
    }
    
    public void runExample(String exampleClassName,String methodname,OutputStream out) throws Exception {
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

    private void execute(Class exampleClass, String methodname)
            throws Exception {
        Iterator sigiter=sig2exec.keySet().iterator();
        boolean found=false;
        while(sigiter.hasNext()&&!found) {
            Class[] sig=(Class[])sigiter.next();
        	try {
                Method exampleMethod=exampleClass.getMethod(methodname,sig);
                found=true;
                Executor exec=(Executor)sig2exec.get(sig);
                exec.execute(exampleMethod);
                break;
            }
            catch(NoSuchMethodException nsmexc) {
            }
            catch(Exception exc) {
                exc.printStackTrace();
            }
        }
        if(!found) {
            System.err.println("Method "+methodname+" not found in "+exampleClass);
        }
    }

    private abstract class Executor {
        public void execute(Method exampleMethod) throws Exception{
            callSetClassloader();
            executeInternal(exampleMethod);
        }
        
        private void callSetClassloader() throws ClassNotFoundException,NoSuchMethodException,IllegalAccessException,InvocationTargetException {
            Method configureMethod = db4oClass.getMethod("configure",new Class[] {});
            Object config = configureMethod.invoke(db4oClass, new Class[] {});
            Class configClass = classLoader.loadClass("com.db4o.config.Configuration");
            Method setClassLoaderMethod = configClass.getMethod("setClassLoader",new Class[] { ClassLoader.class });
            setClassLoaderMethod.invoke(config, new Object[] { classLoader });
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
                db=applyMethod(db4oClass,"openFile",null,new Class[]{String.class},new Object[]{ExampleRunner.this.yapFile.getAbsolutePath()});
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
                server=applyMethod(db4oClass,"openServer",null,new Class[] { String.class,Integer.TYPE },new Object[] { ExampleRunner.this.yapFile.getAbsolutePath(),new Integer(port) });
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
