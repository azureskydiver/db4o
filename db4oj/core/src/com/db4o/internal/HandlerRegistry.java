/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.diagnostic.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.replication.*;
import com.db4o.reflect.*;
import com.db4o.reflect.generic.*;
import com.db4o.types.*;


/**
 * @exclude
 * 
 * TODO: This class was written to make ObjectContainerBase 
 * leaner, so TransportObjectContainer has less members.
 * 
 * All funcionality of this class should become part of 
 * ObjectContainerBase and the functionality in 
 * ObjectContainerBase should delegate to independant
 * modules without circular references.
 * 
 */
public final class HandlerRegistry {
	
	private final ObjectContainerBase _masterStream;  // this is master YapStream and not valid
	                                   // for YapObjectCarrier

    private static final Db4oTypeImpl[]   i_db4oTypes     = { new BlobImpl()};

    public static final int         ANY_ARRAY_ID      = 12;
    public static final int         ANY_ARRAY_N_ID     = 13;

    // Array Indices in i_YapContainers
    private static final int        CLASSCOUNT      = 11;

    private ClassMetadata                i_anyArray;
    private ClassMetadata                i_anyArrayN;

    public final StringHandler          i_stringHandler;

    private TypeHandler4[]           i_handlers;

    private int                     i_maxTypeID     = ANY_ARRAY_N_ID + 1;

    private NetTypeHandler[]       i_platformTypes;
    static private final int        PRIMITIVECOUNT  = 8;

    ClassMetadata[]                      i_yapClasses;

    // need to keep getID Functions in Sync with ArrayIndex
    private static final int        ANY_INDEX          = 10;
    public static final int         ANY_ID        = 11;
    
    private final VirtualFieldMetadata[]         _virtualFields = new VirtualFieldMetadata[2]; 

    private final Hashtable4        i_classByClass  = new Hashtable4(32);
    
    Db4oCollections 				i_collections;
    
    SharedIndexedFields              		i_indexes;
    
    ReplicationImpl				    i_replication;
    
    MigrationConnection             i_migration;
    
    Db4oReplicationReferenceProvider _replicationReferenceProvider;
    
    public final DiagnosticProcessor      _diagnosticProcessor;
    
    
    public boolean                 i_encrypt;
    byte[]                  i_encryptor;
    int                     i_lastEncryptorByte;
    
    final GenericReflector                _reflector;
    
    public ReflectClass ICLASS_COMPARE;
    ReflectClass ICLASS_DB4OTYPE;
    ReflectClass ICLASS_DB4OTYPEIMPL;
	public ReflectClass ICLASS_INTERNAL;
    ReflectClass ICLASS_UNVERSIONED;
    public ReflectClass ICLASS_OBJECT;
    ReflectClass ICLASS_OBJECTCONTAINER;
	public ReflectClass ICLASS_STATICCLASS;
	public ReflectClass ICLASS_STRING;
    ReflectClass ICLASS_TRANSIENTCLASS;

    HandlerRegistry(final ObjectContainerBase a_stream, byte stringEncoding, GenericReflector reflector) {
    	
    	_masterStream = a_stream;
    	a_stream.i_handlers = this;
        
        _reflector = reflector;
        _diagnosticProcessor = a_stream.configImpl().diagnosticProcessor();
    	
    	initClassReflectors(reflector);
        
        i_indexes = new SharedIndexedFields(a_stream);
        
        _virtualFields[0] = i_indexes.i_fieldVersion;
        _virtualFields[1] = i_indexes.i_fieldUUID;

        i_stringHandler = new StringHandler(a_stream, LatinStringIO.forEncoding(stringEncoding));

        i_handlers = new TypeHandler4[] { new IntHandler(a_stream), new LongHandler(a_stream), new FloatHandler(a_stream),
            new BooleanHandler(a_stream), new DoubleHandler(a_stream), new ByteHandler(a_stream), new CharHandler(a_stream),
            new ShortHandler(a_stream),

            // primitives first
            i_stringHandler, new DateHandler(a_stream), new UntypedFieldHandler(a_stream) // Index = 10, ID = 11
        };
        
        i_platformTypes = Platform4.types(a_stream);

        if (i_platformTypes.length > 0) {
            for (int i = 0; i < i_platformTypes.length; i++) {
                i_platformTypes[i].initialize();
                if (i_platformTypes[i].getID() > i_maxTypeID) {
                    i_maxTypeID = i_platformTypes[i].getID();
                }
            }

            TypeHandler4[] temp = i_handlers;
            i_handlers = new TypeHandler4[i_maxTypeID];
            System.arraycopy(temp, 0, i_handlers, 0, temp.length);
            for (int i = 0; i < i_platformTypes.length; i++) {
                int idx = i_platformTypes[i].getID() - 1;
                i_handlers[idx] = i_platformTypes[i];
            }
        }

        i_yapClasses = new ClassMetadata[i_maxTypeID + 1];

        for (int i = 0; i < CLASSCOUNT; i++) {
            int id = i + 1; // note that we avoid 0 here
            i_yapClasses[i] = new PrimitiveFieldHandler(a_stream, i_handlers[i]);
            i_yapClasses[i].setID(id); 
            i_classByClass.put(i_handlers[i].classReflector(), i_yapClasses[i]);
            if(i < ANY_INDEX){
            	reflector.registerPrimitiveClass(id, i_handlers[i].classReflector().getName(), null);
            }
            if (!Deploy.csharp) {
                if (i_handlers[i].primitiveClassReflector() != null) {
                	i_classByClass.put(i_handlers[i].primitiveClassReflector(), i_yapClasses[i]);
                }
            }
        }
        for (int i = 0; i < i_platformTypes.length; i++) {
        	int id = i_platformTypes[i].getID();
            int idx = id - 1;
            GenericConverter converter = (i_platformTypes[i] instanceof GenericConverter) ? (GenericConverter)i_platformTypes[i] : null;  
            reflector.registerPrimitiveClass(id, i_platformTypes[i].getName(), converter);
            i_handlers[idx] = i_platformTypes[i];
            i_yapClasses[idx] = new PrimitiveFieldHandler(a_stream, i_platformTypes[i]);
            i_yapClasses[idx].setID(id);
            if (id > i_maxTypeID) {
                i_maxTypeID = idx;
            }
            i_classByClass.put(i_platformTypes[i].classReflector(), i_yapClasses[idx]);
            if (!Deploy.csharp) {
                if (i_platformTypes[i].primitiveClassReflector() != null) {
                	i_classByClass.put(i_platformTypes[i].primitiveClassReflector(), i_yapClasses[idx]);
                }
            }
        }

        i_anyArray = new PrimitiveFieldHandler(a_stream, new ArrayHandler(_masterStream,
            untypedHandler(), false));
        i_anyArray.setID(ANY_ARRAY_ID);
        i_yapClasses[ANY_ARRAY_ID - 1] = i_anyArray;

        i_anyArrayN = new PrimitiveFieldHandler(a_stream, new MultidimensionalArrayHandler(_masterStream,
            untypedHandler(), false));
        i_anyArrayN.setID(ANY_ARRAY_N_ID);
        i_yapClasses[ANY_ARRAY_N_ID - 1] = i_anyArrayN;
    }

	int arrayType(Object a_object) {
    	ReflectClass claxx = _masterStream.reflector().forObject(a_object);
        if (! claxx.isArray()) {
            return 0;
        }
        if (_masterStream.reflector().array().isNDimensional(claxx)) {
            return Const4.TYPE_NARRAY;
        } 
        return Const4.TYPE_ARRAY;
    }
	
    boolean createConstructor(final ReflectClass claxx, boolean skipConstructor){
        
        if (claxx == null) {
            return false;
        }
        
        if (claxx.isAbstract() || claxx.isInterface()) {
            return true;
        }
        
        if(! Platform4.callConstructor()){
            if(claxx.skipConstructor(skipConstructor)){
                return true;
            }
        }
        
        if (! _masterStream.configImpl().testConstructors()) {
            return true;
        }
        
        if (claxx.newInstance() != null) {
            return true;
        }
        
        if (_masterStream.reflector().constructorCallsSupported()) {
			Tree sortedConstructors = sortConstructorsByParamsCount(claxx);
			return findConstructor(claxx, sortedConstructors);
		}
		return false;
	}

	private boolean findConstructor(final ReflectClass claxx,
			Tree sortedConstructors) {
		if (sortedConstructors == null) {
			return false;
		}
		final boolean[] foundConstructor = { false };
		final TypeHandler4[] handlers = i_handlers;
		//TODO: use Iterator4 instead of traverse.
		sortedConstructors.traverse(new Visitor4() {
			public void visit(Object a_object) {
				if (!foundConstructor[0]) {
					ReflectConstructor constructor = (ReflectConstructor) ((TreeIntObject) a_object)._object;
					ReflectClass[] pTypes = constructor.getParameterTypes();
					Object[] params = new Object[pTypes.length];
					for (int j = 0; j < params.length; j++) {
						for (int k = 0; k < PRIMITIVECOUNT; k++) {
							if (pTypes[j].equals(handlers[k]
									.primitiveClassReflector())) {
								params[j] = ((PrimitiveHandler) handlers[k])
										.primitiveNull();
								break;
							}
						}
					}
					Object res = constructor.newInstance(params);
					if (res != null) {
						foundConstructor[0] = true;
						claxx.useConstructor(constructor, params);
					}
				}
			}
		});
		return foundConstructor[0];
	}

	private Tree sortConstructorsByParamsCount(final ReflectClass claxx) {
		ReflectConstructor[] constructors = claxx.getDeclaredConstructors();

		Tree sortedConstructors = null;

		// sort constructors by parameter count
		for (int i = 0; i < constructors.length; i++) {
			try {
				constructors[i].setAccessible();
				int parameterCount = constructors[i].getParameterTypes().length;
				sortedConstructors = Tree.add(sortedConstructors,
						new TreeIntObject(i + constructors.length
								* parameterCount, constructors[i]));
			} catch (SecurityException e) {
				if (Debug.atHome) {
					e.printStackTrace();
				}
			}
		}
		return sortedConstructors;
	}
    
	public final void decrypt(Buffer reader) {
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
	
    public final void encrypt(Buffer reader) {
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
    
    public void oldEncryptionOff() {
        i_encrypt = false;
        i_encryptor = null;
        i_lastEncryptorByte = 0;
        _masterStream.configImpl().oldEncryptionOff();
    }
    
    final TypeHandler4 getHandler(int a_index) {
        return i_handlers[a_index - 1];
    }

    final TypeHandler4 handlerForClass(ReflectClass a_class, ReflectClass[] a_Supported) {
        for (int i = 0; i < a_Supported.length; i++) {
            if (a_Supported[i].equals(a_class)) {
                return i_handlers[i];
            }
        }
        return null;
    }

    // TODO: Interfaces should be handled by the ANY handler but we
    // need to write the code to migrate from the old field handler to the new
    public final TypeHandler4 handlerForClass(ObjectContainerBase a_stream, ReflectClass a_class) {
        if(a_class == null){
            return null;
        }
//        if (a_class.isInterface()) {
//        	return anyObject();
//        }
        if (a_class.isArray()) {
            return handlerForClass(a_stream, a_class.getComponentType());
        }
        ClassMetadata yc = getYapClassStatic(a_class);
        if (yc != null) {
            return ((PrimitiveFieldHandler) yc).i_handler;
        }
        return a_stream.produceYapClass(a_class);
    }

	public TypeHandler4 untypedHandler() {
		return i_handlers[ANY_INDEX];
	}
    
    private void initClassReflectors(GenericReflector reflector){
		ICLASS_COMPARE = reflector.forClass(Const4.CLASS_COMPARE);
		ICLASS_DB4OTYPE = reflector.forClass(Const4.CLASS_DB4OTYPE);
		ICLASS_DB4OTYPEIMPL = reflector.forClass(Const4.CLASS_DB4OTYPEIMPL);
        ICLASS_INTERNAL = reflector.forClass(Const4.CLASS_INTERNAL);
        ICLASS_UNVERSIONED = reflector.forClass(Const4.CLASS_UNVERSIONED);
		ICLASS_OBJECT = reflector.forClass(Const4.CLASS_OBJECT);
		ICLASS_OBJECTCONTAINER = reflector
				.forClass(Const4.CLASS_OBJECTCONTAINER);
		ICLASS_STATICCLASS = reflector.forClass(Const4.CLASS_STATICCLASS);
		ICLASS_STRING = reflector.forClass(String.class);
		ICLASS_TRANSIENTCLASS = reflector
				.forClass(Const4.CLASS_TRANSIENTCLASS);
		
		Platform4.registerCollections(reflector);
    }
    
    void initEncryption(Config4Impl a_config){
        if (a_config.encrypt() && a_config.password() != null
            && a_config.password().length() > 0) {
            i_encrypt = true;
            i_encryptor = new byte[a_config.password().length()];
            for (int i = 0; i < i_encryptor.length; i++) {
                i_encryptor[i] = (byte) (a_config.password().charAt(i) & 0xff);
            }
            i_lastEncryptorByte = a_config.password().length() - 1;
            return;
        }
        
        oldEncryptionOff();
    }
    
    static Db4oTypeImpl getDb4oType(ReflectClass clazz) {
        for (int i = 0; i < i_db4oTypes.length; i++) {
            if (clazz.isInstance(i_db4oTypes[i])) {
                return i_db4oTypes[i];
            }
        }
        return null;
    }

    public ClassMetadata getYapClassStatic(int a_id) {
        if (a_id > 0 && a_id <= i_maxTypeID) {
            return i_yapClasses[a_id - 1];
        }
        return null;
    }

    ClassMetadata getYapClassStatic(ReflectClass a_class) {
        if (a_class == null) {
            return null;
        }
        if (a_class.isArray()) {
            if (_masterStream.reflector().array().isNDimensional(a_class)) {
                return i_anyArrayN;
            }
            return i_anyArray;
        }
        return (ClassMetadata) i_classByClass.get(a_class);
    }
    
    public boolean isSecondClass(Object a_object){
    	if(a_object != null){
    		ReflectClass claxx = _masterStream.reflector().forObject(a_object);
    		if(i_classByClass.get(claxx) != null){
    			return true;
    		}
            if(Deploy.csharp){
                return Platform4.isValueType(claxx);
            }
    	}
    	return false;
    }

    public boolean isSystemHandler(int id) {
    	return id<=i_maxTypeID;
    }

	public void migrationConnection(MigrationConnection mgc) {
		i_migration = mgc;
	}
	
	public MigrationConnection  migrationConnection() {
		return i_migration;
	}

	public void replication(ReplicationImpl impl) {
		i_replication = impl;
	}
	
	public ReplicationImpl replication(){
		return i_replication;
	}

	public ClassMetadata primitiveClassById(int id) {
        return i_yapClasses[id - 1];
	}
	
	public VirtualFieldMetadata virtualFieldByName(String name) {
        for (int i = 0; i < _virtualFields.length; i++) {
            if (name.equals(_virtualFields[i].getName())) {
                return _virtualFields[i];
            }
        }
        return null;
	}
}