/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.lang.reflect.*;

import com.db4o.ext.*;
import com.db4o.reflect.*;
import com.db4o.reflect.jdk.CConstructor;
import com.db4o.types.*;

/**
 *  
 */
class YapHandlers {

    private static Db4oTypeImpl[]   i_db4oTypes     = { new BlobImpl()};

    static final int                ANYARRAYID      = 12;
    static final int                ANYARRAYNID     = 13;

    // Array Indices in i_YapContainers
    private static final int        CLASSCOUNT      = 11;

    private YapClass                i_anyArray;
    private YapClass                i_anyArrayN;

    final YapString                 i_stringHandler;

    private YapDataType[]           i_handlers;

    static private int              i_maxTypeID     = ANYARRAYNID + 1;

    private final YapTypeAbstract[] i_platformTypes = Platform.types();
    static private final int        PRIMITIVECOUNT  = 8;

    YapClass[]                      i_yapClasses;

    static final Class              licenseClass    = new License().getClass();

    // need to keep getID Functions in Sync with ArrayIndex
    static final int                YAPANY          = 10;
    static final int                YAPANYID        = 11;
    
    final YapFieldVirtual[]         i_virtualFields = new YapFieldVirtual[2]; 

    private final Hashtable4        i_classByClass  = new Hashtable4(32);
    
    Db4oCollections 				i_collections;
    
    YapIndexes              		i_indexes;
    
    ReplicationImpl				    i_replication;
    
    
    boolean                 i_encrypt;
    byte[]                  i_encryptor;
    int                     i_lastEncryptorByte;

    

    YapHandlers(YapStream a_stream) {
        
        i_indexes = new YapIndexes();
        
        i_virtualFields[0] = i_indexes.i_fieldVersion;
        i_virtualFields[1] = i_indexes.i_fieldUUID;

        i_stringHandler = new YapString();

        i_handlers = new YapDataType[] { new YInt(), new YLong(), new YFloat(),
            new YBoolean(), new YDouble(), new YByte(), new YChar(),
            new YShort(),

            // primitives first

            i_stringHandler, new YDate(), new YapClassAny() // Index = 10, ID = 11
        };

        if (i_platformTypes.length > 0) {
            for (int i = 0; i < i_platformTypes.length; i++) {
                i_platformTypes[i].initialize();
                if (i_platformTypes[i].getID() > i_maxTypeID) {
                    i_maxTypeID = i_platformTypes[i].getID();
                }
            }

            YapDataType[] temp = i_handlers;
            i_handlers = new YapDataType[i_maxTypeID];
            System.arraycopy(temp, 0, i_handlers, 0, temp.length);
            for (int i = 0; i < i_platformTypes.length; i++) {
                int idx = i_platformTypes[i].getID() - 1;
                i_handlers[idx] = i_platformTypes[i];
            }
        }

        i_yapClasses = new YapClass[i_maxTypeID + 1];

        for (int i = 0; i < CLASSCOUNT; i++) {
            i_yapClasses[i] = new YapClassPrimitive(null, i_handlers[i]);
            i_yapClasses[i].i_id = i + 1; // note that we avoid 0 here
            i_classByClass.put(i_handlers[i].getJavaClass(), i_yapClasses[i]);
            if (!Deploy.csharp) {
                if (i_handlers[i].getPrimitiveJavaClass() != null) {
                    i_classByClass.put(i_handlers[i].getPrimitiveJavaClass(),
                        i_yapClasses[i]);
                }
            }
        }
        for (int i = 0; i < i_platformTypes.length; i++) {
            int idx = i_platformTypes[i].getID() - 1;
            i_handlers[idx] = i_platformTypes[i];
            i_yapClasses[idx] = new YapClassPrimitive(null, i_platformTypes[i]);
            i_yapClasses[idx].i_id = idx + 1;
            if (i_yapClasses[idx].i_id > i_maxTypeID) {
                i_maxTypeID = idx;
            }
            i_classByClass.put(i_platformTypes[i].getJavaClass(),
                i_yapClasses[idx]);
            if (!Deploy.csharp) {
                if (i_platformTypes[i].getPrimitiveJavaClass() != null) {
                    i_classByClass.put(i_platformTypes[i]
                        .getPrimitiveJavaClass(), i_yapClasses[idx]);
                }
            }
        }

        i_anyArray = new YapClassPrimitive(null, new YapArray(
            i_handlers[YAPANY], false));
        i_anyArray.i_id = ANYARRAYID;
        i_yapClasses[ANYARRAYID - 1] = i_anyArray;

        i_anyArrayN = new YapClassPrimitive(null, new YapArrayN(
            i_handlers[YAPANY], false));
        i_anyArrayN.i_id = ANYARRAYNID;
        i_yapClasses[ANYARRAYNID - 1] = i_anyArrayN;
    }

    static int arrayType(Object a_object) {
        if (a_object.getClass().isArray()) {
            if (Array4.isNDimensional(a_object.getClass())) {
                return YapConst.TYPE_NARRAY;
            } else {
                return YapConst.TYPE_ARRAY;
            }
        }
        return 0;
    }

    final YapConstructor createConstructorStatic(final YapStream a_stream,
        final YapClass a_yapClass,
        final Class a_class
         ) {
        
        final IReflect reflector = a_stream.i_config.i_reflect;
        IClass classReflector;
        
        try {
            classReflector = reflector.forName(a_class.getName());
        } catch (ClassNotFoundException e) {
            return null;
        }
        if (classReflector == null) {
            if (a_stream.i_config.i_exceptionsOnNotStorable) {
                throw new ObjectNotStorableException(a_class);
            }
            return null;
        }
        if (classReflector.isAbstract() || classReflector.isInterface()) {
            return new YapConstructor(a_stream, a_class, null, null, false, false);
        }
        
        if(! Deploy.csharp){
	        if(! Platform.callConstructor()){
	            if(! a_yapClass.callConstructor(a_stream)){
		            Constructor constructor = Platform.jdk().serializableConstructor(a_class);
		            if(constructor != null){
		                try{
		                    Object o = constructor.newInstance(null);
		                    if(o != null){
		                        return new YapConstructor(a_stream, a_class, new CConstructor(reflector, constructor), null, true, true);
		                    }
		                }catch(Exception e){
		                    
		                }
		            }
	            }
	        }
        }
        
        if (a_stream.i_config.i_testConstructors) {
            Object o = classReflector.newInstance();
            if (o != null) {
                return new YapConstructor(a_stream, a_class, null, null, true, false);
            }
        } else {
            return new YapConstructor(a_stream, a_class, null, null, true, false);
        }
        
        if (reflector.constructorCallsSupported()) {
            try {
                
                IConstructor[] constructors = classReflector
                    .getDeclaredConstructors();
                
                Tree sortedConstructors = null;
                
                // sort constructors by parameter count  
                for (int i = 0; i < constructors.length; i++) {
                    try{
                        constructors[i].setAccessible();
                        int parameterCount =  constructors[i].getParameterTypes().length;
                        sortedConstructors = Tree.add(sortedConstructors, new TreeIntObject(parameterCount, constructors[i]));
                    } catch (Throwable t) {
                        if(Debug.atHome){
                            t.printStackTrace();
                        }
                    }
                }
                
                // call zero-arg constructors first
                final YapConstructor[] foundConstructor=new YapConstructor[1];
                if(sortedConstructors != null){
                    sortedConstructors.traverse(new Visitor4() {
                        public void visit(Object a_object) {
                            if(foundConstructor[0]==null) {
	                            IConstructor constructor = (IConstructor)((TreeIntObject)a_object).i_object;
	                            try {
	                                IClass[] pTypes = constructor.getParameterTypes();
	                                Object[] parms = new Object[pTypes.length];
	                                for (int j = 0; j < parms.length; j++) {
	                                    for (int k = 0; k < PRIMITIVECOUNT; k++) {
	                                        if (pTypes[j] == reflector.forClass(i_handlers[k]
	                                            .getPrimitiveJavaClass())) {
	                                            parms[j] = ((YapJavaClass) i_handlers[k])
	                                                .primitiveNull();
	                                            break;
	                                        }
	                                    }
	                                }
	                                Object res = constructor.newInstance(parms);
	                                if (res != null) {
	                                    foundConstructor[0] = new YapConstructor(a_stream, a_class,
	                                  constructor, parms, true, false);
	                                }
	                            } catch (Throwable t) {
	                                if(Debug.atHome){
	                                    t.printStackTrace();
	                                }
	                            }
                            }
                        }
                    });
                    
                }
                if(foundConstructor[0] != null){
                    return foundConstructor[0];
                }
                
            } catch (Throwable t1) {
                if(Debug.atHome){
                    t1.printStackTrace();
                }

            }
        }
        if (a_stream.i_config.i_exceptionsOnNotStorable) {
            throw new ObjectNotStorableException(a_class);
        }
        return null;
    }
    
	final void decrypt(YapReader reader) {
	    if(i_encrypt){
			int encryptorOffSet = i_lastEncryptorByte;
			byte[] bytes = reader._buffer;
			for (int i = reader.getLength() - 1; i >= 0; i--) {
				bytes[i] += i_encryptor[encryptorOffSet];
				if (encryptorOffSet == 0) {
					encryptorOffSet = i_lastEncryptorByte;
				} else {
					encryptorOffSet--;
				}
			}
	    }
	}
	
    final void encrypt(YapReader reader) {
        if(i_encrypt){
	        byte[] bytes = reader._buffer;
	        int encryptorOffSet = i_lastEncryptorByte;
	        for (int i = reader.getLength() - 1; i >= 0; i--) {
	            bytes[i] -= i_encryptor[encryptorOffSet];
	            if (encryptorOffSet == 0) {
	                encryptorOffSet = i_lastEncryptorByte;
	            } else {
	                encryptorOffSet--;
	            }
	        }
        }
    }
    
    Db4oDatabase ensureDb4oDatabase(Transaction a_trans, Db4oDatabase a_db){
        YapStream stream = a_trans.i_stream;
        Object obj = stream.db4oTypeStored(a_trans,a_db);
        if (obj != null) {
            return (Db4oDatabase)obj;
        } 
        stream.set3(a_trans,a_db, 2, false);
        return a_db;
    }
    
    final YapDataType getHandler(int a_index) {
        return i_handlers[a_index - 1];
    }

    final YapDataType handlerForClass(Class a_class, Class[] a_Supported) {
        for (int i = 0; i < a_Supported.length; i++) {
            if (a_Supported[i] == a_class) {
                return i_handlers[i];
            }
        }
        return null;
    }

    /**
     * Can't return ANY class for interfaces, since that would kill the
     * translators built into the architecture.
     */
    final YapDataType handlerForClass(YapStream a_stream, Class a_class) {
        if (a_class.isArray()) {
            return handlerForClass(a_stream, a_class.getComponentType());
        }
        YapClass yc = getYapClassStatic(a_class);
        if (yc != null) {
            return ((YapClassPrimitive) yc).i_handler;
        }
        return a_stream.getYapClass(a_class, true);
    }
    
    void initEncryption(Config4Impl a_config){
        if (a_config.i_encrypt && a_config.i_password != null
            && a_config.i_password.length() > 0) {
            i_encrypt = true;
            i_encryptor = new byte[a_config.i_password.length()];
            for (int i = 0; i < i_encryptor.length; i++) {
                i_encryptor[i] = (byte) (a_config.i_password.charAt(i) & 0xff);
            }
            i_lastEncryptorByte = a_config.i_password.length() - 1;
        } else {
            i_encrypt = false;
            i_encryptor = null;
            i_lastEncryptorByte = 0;
        }
    }
    
    static Db4oTypeImpl getDb4oType(Class clazz) {
        for (int i = 0; i < i_db4oTypes.length; i++) {
            if (clazz.isInstance(i_db4oTypes[i])) {
                return i_db4oTypes[i];
            }
        }
        return null;
    }

    YapClass getYapClassStatic(int a_id) {
        if (a_id > 0 && a_id <= i_maxTypeID) {
            return i_yapClasses[a_id - 1];
        }
        return null;
    }

    YapClass getYapClassStatic(Class a_class) {
        if (a_class == null) {
            return null;
        }
        if (a_class.isArray()) {
            if (Array4.isNDimensional(a_class)) {
                return i_anyArrayN;
            }
            return i_anyArray;
        }
        return (YapClass) i_classByClass.get(a_class);
    }
    
    public final boolean isSecondClass(Object a_object){
    	if(a_object != null){
    		Class clazz = a_object.getClass();
    		if(i_classByClass.get(clazz) != null){
    			return true;
    		}
    		return Platform.isSecondClass(clazz);
    	}
    	return false;
    }

    static int maxTypeID() {
        return i_maxTypeID;
    }
}