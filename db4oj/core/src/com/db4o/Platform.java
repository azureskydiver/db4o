/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.io.*;
import java.lang.reflect.*;
import java.text.*;
import java.util.*;

import com.db4o.config.*;
import com.db4o.query.*;
import com.db4o.reflect.*;
import com.db4o.types.*;

public final class Platform {
    

    static private int collectionCheck;

    static private JDK jdkWrapper;
    static private int nioCheck;

    static private int setAccessibleCheck;
    static private int shutDownHookCheck;
    static int callConstructorCheck;
    static ShutDownRunnable shutDownRunnable;

    static Thread shutDownThread;
    
    static final String ACCESSIBLEOBJECT = "java.lang.reflect.AccessibleObject";
    static final String REFLECTIONFACTORY = "sun.reflect.ReflectionFactory";
    static final String GETCONSTRUCTOR = "newConstructorForSerialization";
    static final String UTIL = "java.util.";
    static final String DB4O_CONFIG = "com.db4o.config.";
    static final String ENUM = "java.lang.Enum"; 
    
    
    // static private int cCreateNewFile;
    static private int weakReferenceCheck;

    static final void addShutDownHook(Object a_stream, Object a_lock) {
        synchronized (a_lock) {
            if (hasShutDownHook()) {
                if (shutDownThread == null) {
                    shutDownRunnable = new ShutDownRunnable();
                    shutDownThread = jdk().addShutdownHook(shutDownRunnable);
                }
                shutDownRunnable.ensure(a_stream);
            }
        }
    }

    public static final boolean canSetAccessible() {
        if (Deploy.csharp) {
            return true;
        }
        if (setAccessibleCheck == YapConst.UNCHECKED) {
            if (Deploy.csharp) {
                setAccessibleCheck = YapConst.NO;
            } else {
                if (jdk().ver() >= 2) {
                    setAccessibleCheck = YapConst.YES;
                } else {
                    setAccessibleCheck = YapConst.NO;
                    if (Db4o.i_config.i_messageLevel >= 0) {
                        Db4o.logErr(Db4o.i_config, 47, null, null);
                    }
                }
            }
        }
        return setAccessibleCheck == YapConst.YES;
    }
    
    static final boolean classIsAvailable(String className) {
        try {
            return Db4o.reflector().forName(className) != null;
        } catch (Throwable t) {
            return false;
        }
    }
    
    static Db4oCollections collections(Object a_object){
        return jdk().collections((YapStream)a_object);
    }

    static final int collectionUpdateDepth(Class a_class) {
        return JavaOnly.collectionUpdateDepth(a_class);
    }
    
    static final Object createReferenceQueue() {
        return jdk().createReferenceQueue();
    }

    static final Object createYapRef(Object a_queue, Object a_yapObject, Object a_object) {
        return jdk().createYapRef(a_queue, (YapObject) a_yapObject, a_object);
    }
    
    static Object deserialize(byte[] bytes) {
        try {
            return new ObjectInputStream(new ByteArrayInputStream(bytes)).readObject();
        } catch (Exception e) {
        }
        return null;
    }
    
    static final long doubleToLong(double a_double) {
        return Double.doubleToLongBits(a_double);
    }

    static final QEvaluation evaluationCreate(Transaction a_trans, Object example){
        if(example instanceof Evaluation){
            return new QEvaluation(a_trans, example, false);
        }
        return null;
    }
    
    static final void evaluationEvaluate(Object a_evaluation, Candidate a_candidate){
        ((Evaluation)a_evaluation).evaluate(a_candidate);
    }

    /** may be needed for YapConfig processID() at a later date */
    /*
    static boolean createNewFile(File file) throws IOException{
    	return file.createNewFile();
    }
    */

    static final Collection4 flattenCollection(Object obj) {
        Collection4 col = new Collection4();
        flattenCollection1(obj, col);
        return col;
    }

    static final void flattenCollection1(Object obj, Collection4 col) {
        if (obj == null) {
            col.add(null);
        } else {
            Class clazz = obj.getClass();
            if (clazz.isArray()) {
                Object[] objects;
                if (clazz.getComponentType().isArray()) {
                    objects = new YapArrayN(null, false).allElements(obj);
                } else {
                    objects = new YapArray(null, false).allElements(obj);
                }
                for (int i = 0; i < objects.length; i++) {
                    flattenCollection1(objects[i], col);
                }
            } else {
                if (hasCollections()) {
                    Platform.flattenCollection2(obj, col);
                } else {
                    // TODO: You are missing Vector and Hashtable on JDK 1.1.x here
                    col.add(obj);
                }
            }
        }
    }

    static final void flattenCollection2(Object a_object, final com.db4o.Collection4 col) {
        jdk().flattenCollection2(a_object, col);
    }

    static final void forEachCollectionElement(Object a_object, Visitor4 a_visitor) {
        if (hasCollections()) {
            jdk().forEachCollectionElement(a_object, a_visitor);
        }
    }

    static final String format(Date date, boolean showTime) {
        String fmt = "yyyy-MM-dd";
        if (showTime) {
            fmt += " HH:mm:ss";
        }
        return new SimpleDateFormat(fmt).format(date);
    }

    public static Object getClassForType(Object obj) {
        return obj;
    }

    static final void getDefaultConfiguration(Config4Impl config) {
    	
        if (!Deploy.csharp) {
        	
        	// Initialize all JDK stuff first, before doing ClassLoader stuff
        	jdk();
        	hasWeakReferences();
        	hasNio();
        	hasCollections();
        	hasShutDownHook();

            config.objectClass("java.lang.StringBuffer").compare(new ObjectAttribute() {
                public Object attribute(Object original) {
                    if (original instanceof StringBuffer) {
                        return ((StringBuffer) original).toString();
                    }
                    return original;
                }
            });
            translate(config, config.objectClass("java.lang.Class"), "TClass");
            translateCollection(config, "Hashtable", "THashtable", true);
            if (jdk().ver() >= 2) {
				try {
					translateCollection(config, "AbstractCollection", "TCollection", false);
					translateUtilNull(config, "AbstractList");
					translateUtilNull(config, "AbstractSequentialList");
					translateUtilNull(config, "LinkedList");
					translateUtilNull(config, "ArrayList");
					translateUtilNull(config, "Vector");
					translateUtilNull(config, "Stack");
					translateUtilNull(config, "AbstractSet");
					translateUtilNull(config, "HashSet");
					translate(config, UTIL + "TreeSet", "TTreeSet");
					translateCollection(config, "AbstractMap", "TMap", true);
					translateUtilNull(config, "HashMap");
					translateUtilNull(config, "WeakHashMap");
					translate(config, UTIL + "TreeMap", "TTreeMap");
				} catch (Exception e) {
				}
				if(config.i_classLoader == null){
				    config.setClassLoader(jdk().getContextClassLoader());
				}
            } else {
				translateCollection(config, "Vector", "TVector", false);
            }
        }
    }
    
    public static Object getTypeForClass(Object obj){
        return obj;
    }

    static final Object getYapRefObject(Object a_object) {
        return jdk().getYapRefObject(a_object);
    }

    static final synchronized boolean hasCollections() {
        if (collectionCheck == YapConst.UNCHECKED) {
            if (!Deploy.csharp) {
                if (classIsAvailable(UTIL + "Collection")) {
                    collectionCheck = YapConst.YES;
                    return true;
                }
            }
            collectionCheck = YapConst.NO;
        }
        return collectionCheck == YapConst.YES;
    }

    static final boolean hasLockFileThread() {
        return !Deploy.csharp;
    }

    static final boolean hasNio() {
        if (!Debug.nio) {
            return false;
        }
        if (nioCheck == YapConst.UNCHECKED) {
            if ((jdk().ver() >= 4)
                && (!noNIO())) {
                nioCheck = YapConst.YES;
                return true;
            }
            nioCheck = YapConst.NO;
        }
        return nioCheck == YapConst.YES;

    }

    static final boolean hasShutDownHook() {
        if (shutDownHookCheck == YapConst.UNCHECKED) {
            if (!Deploy.csharp) {
                if (jdk().ver() >= 3){
                    shutDownHookCheck = YapConst.YES;
                    return true;
                } else {
                    JavaOnly.runFinalizersOnExit();
                }
            }
            shutDownHookCheck = YapConst.NO;
        }
        return shutDownHookCheck == YapConst.YES;
    }

    static final boolean hasWeakReferences() {
        if (!Debug.weakReferences) {
            return false;
        }
        if (weakReferenceCheck == YapConst.UNCHECKED) {
            if (!Deploy.csharp) {
                if (classIsAvailable(ACCESSIBLEOBJECT)
                    && classIsAvailable("java.lang.ref.ReferenceQueue")
                    && jdk().ver() >= 2) {
                    weakReferenceCheck = YapConst.YES;
                    return true;
                }
            }
            weakReferenceCheck = YapConst.NO;
        }
        return weakReferenceCheck == YapConst.YES;
    }
    
    static final boolean ignoreAsConstraint(Object obj){
        return false;
    }

    static final boolean isCollection(Class a_class) {
        return JavaOnly.isCollection(a_class);
    }

    static final boolean isCollectionTranslator(Config4Class a_config) {
        return JavaOnly.isCollectionTranslator(a_config); 
    }
    
    static final boolean isSecondClass(Class a_class){
    	return false;
    }
    
    static final boolean isValueType(Class a_class){
    	return false;
    }
    
    public static JDK jdk() {
        if (jdkWrapper == null) {
            jdkWrapper = JavaOnly.jdk();
        }
        return jdkWrapper;
    }

    static final Object createInstance(String name) {
        try {
            Class clazz = Class.forName(name);
            if(clazz != null){
                return clazz.newInstance();
            }
        } catch (Exception e) {
        }
        return null;
    }
    
	public static boolean isSimple(Class a_class){
		for (int i = 0; i < SIMPLE_CLASSES.length; i++) {
			if(a_class == SIMPLE_CLASSES[i]){
				return true;
			}
		}
		return false;
	}
    
    static final void killYapRef(Object a_object){
    	jdk().killYapRef(a_object);
    }
    
    public static void link(){
        if (!Deploy.csharp) {
            
            // link standard translators, so they won't get deleted
            // by deployment
            
            JavaOnly.link();
        }
    }

    public static final void lock(RandomAccessFile file) {
        if (hasNio()) {
            jdk().lock(file);
        }
    }

    static final double longToDouble(long a_long) {
        return Double.longBitsToDouble(a_long);
    }

    static void markTransient(String a_marker) {
        // do nothing
    }

    static final boolean methodIsAvailable(
        String className,
        String methodName,
        Class[] params) {
        try {
            IClass classReflector = Db4o.reflector().forName(className);
            if (classReflector.getMethod(methodName, params) != null) {
                return true;
            }
            return false;
        } catch (Throwable t) {
            return false;
        }
    }
    
    static boolean callConstructor() {
        if (callConstructorCheck == YapConst.UNCHECKED) {
            
            if(methodIsAvailable(
                REFLECTIONFACTORY,
                GETCONSTRUCTOR,
                new Class[]{Class.class, Constructor.class}
                )){
                
                callConstructorCheck = YapConst.NO;
                return false;
            }
            callConstructorCheck = YapConst.YES;
        }
        return callConstructorCheck == YapConst.YES;
    }

    private static final boolean noNIO() {
        try {
            if (propertyIs("java.vendor", "Sun")
                && propertyIs("java.version", "1.4.0")
                && (propertyIs("os.name", "Linux")
                    || propertyIs("os.name", "Windows 95")
                    || propertyIs("os.name", "Windows 98"))) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    static final void pollReferenceQueue(Object a_stream, Object a_referenceQueue) {
        jdk().pollReferenceQueue((YapStream) a_stream, a_referenceQueue);
    }

    static void postOpen(ObjectContainer a_oc) {
        // do nothing 
     }
    
    static void preClose(ObjectContainer a_oc) {
        // do nothing 
    }

    private static final boolean propertyIs(String propertyName, String propertyValue) {
        String property = System.getProperty(propertyName);
        return (property != null) && (property.indexOf(propertyValue) == 0);
    }

    static final void removeShutDownHook(Object a_stream, Object a_lock) {
        synchronized (a_lock) {
            if (hasShutDownHook()) {
                if (shutDownRunnable != null) {
                    shutDownRunnable.remove(a_stream);
                }
                if (shutDownRunnable.size() == 0) {
                    if (!shutDownRunnable.dontRemove) {
                        try {
                            jdk().removeShutdownHook(shutDownThread);
                        } catch (Exception e) {
                            // this is safer than attempting perfect
                            // synchronisation
                        }
                    }
                    shutDownThread = null;
                    shutDownRunnable = null;
                }
            }
        }
    }
    
    static final byte[] serialize(Object obj) throws Exception{
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        new ObjectOutputStream(byteStream).writeObject(obj);
        return byteStream.toByteArray();
    }

    public static final void setAccessible(Object a_accessible) {
        if (!Deploy.csharp) {
            if (setAccessibleCheck == YapConst.UNCHECKED) {
                canSetAccessible();
            }
            if (setAccessibleCheck == YapConst.YES) {
                jdk().setAccessible(a_accessible);
            }
        }
    }
    
    public static boolean storeStaticFieldValues(Class clazz) {
        return jdk().storeStaticFieldValues(clazz);
    }

    private static final void translate(Config4Impl config, ObjectClass oc, String to) {
        ((Config4Class)oc).translateOnDemand(DB4O_CONFIG + to);
    }

    private static final void translate(Config4Impl config, String from, String to) {
        translate(config, config.objectClass(from), to);
    }

    private static final void translateCollection(
        Config4Impl config,
        String from,
        String to,
        boolean cascadeOnDelete) {
        ObjectClass oc = config.objectClass(UTIL + from);
        oc.updateDepth(3);
        if (cascadeOnDelete) {
            oc.cascadeOnDelete(true);
        }
        translate(config, oc, to);
    }

    private static final void translateUtilNull(Config4Impl config, String className) {
        translate(config, UTIL + className, "TNull");
    }

    static final YapTypeAbstract[] types() {
        return new YapTypeAbstract[0];
    }

    public static final void unlock(RandomAccessFile file) {
        if (hasNio()) {
            jdk().unlock(file);
        }
    }
    
    static byte[] updateClassName(byte[] bytes) {
        // needed for .NET only: update assembly names if necessary
        return bytes;
    }

	private static final Class[] SIMPLE_CLASSES = JavaOnly.SIMPLE_CLASSES;


	
}