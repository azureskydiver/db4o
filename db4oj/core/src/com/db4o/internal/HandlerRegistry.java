/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.diagnostic.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.replication.*;
import com.db4o.reflect.*;
import com.db4o.reflect.generic.*;


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
	
	private final ObjectContainerBase _container;  // this is the master container and not valid
	                                   // for TransportObjectContainer

    private static final Db4oTypeImpl[]   _db4oTypes     = { new BlobImpl()};


    // Array Indices in i_YapContainers
    private static final int        CLASSCOUNT      = 11;

    private ClassMetadata                i_anyArray;
    private ClassMetadata                i_anyArrayN;

    public StringHandler          _stringHandler;

    private Hashtable4           _handlers = new Hashtable4(16);
    
    private Hashtable4           _classes = new Hashtable4(16);
    
    private Hashtable4          _classMetadata = new Hashtable4(16);

    

    private int                     i_maxTypeID     = Handlers4.ANY_ARRAY_N_ID + 1;

    private NetTypeHandler[]       _platformTypes;
    static private final int        PRIMITIVECOUNT  = 8;

    public static final int         ANY_ID        = 11;
    
    private final VirtualFieldMetadata[]         _virtualFields = new VirtualFieldMetadata[2]; 

    private final Hashtable4        _mapReflectorToClassMetadata  = new Hashtable4(32);
    
    private SharedIndexedFields              		_indexes;
    
    /**
	 * @deprecated
	 */
    ReplicationImpl				    i_replication;
    
    MigrationConnection             i_migration;
    
    Db4oReplicationReferenceProvider _replicationReferenceProvider;
    
    public final DiagnosticProcessor      _diagnosticProcessor;
    
    public boolean                 i_encrypt;
    byte[]                  i_encryptor;
    int                     i_lastEncryptorByte;
    
    final GenericReflector                _reflector;
    
    private final Hashtable4 _handlerVersions = new Hashtable4();
    
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

    HandlerRegistry(final ObjectContainerBase container, byte stringEncoding, GenericReflector reflector) {
    	
    	_container = container;
    	container._handlers = this;
        
        _reflector = reflector;
        _diagnosticProcessor = container.configImpl().diagnosticProcessor();
    	
    	initClassReflectors(reflector);
        
        _indexes = new SharedIndexedFields(container);
        
        _virtualFields[0] = _indexes._version;
        _virtualFields[1] = _indexes._uUID;

        registerHandlers(stringEncoding);

        for (int id = 1; id <= CLASSCOUNT; id++) {
            TypeHandler4 handler = handlerForID(id);
            PrimitiveFieldHandler primitiveFieldHandler = new PrimitiveFieldHandler(container, handler, id);
            _classMetadata.put(id, primitiveFieldHandler);
            _mapReflectorToClassMetadata.put(handler.classReflector(), primitiveFieldHandler);
            if(id < ANY_ID){
            	reflector.registerPrimitiveClass(id, handler.classReflector().getName(), null);
            }
            if (!Deploy.csharp) {
               if(handler instanceof PrimitiveHandler){
                   PrimitiveHandler primitiveHandler = (PrimitiveHandler) handler;
                   ReflectClass primitiveClassReflector = primitiveHandler.primitiveClassReflector();
                   if(primitiveClassReflector != null){
                       _mapReflectorToClassMetadata.put(primitiveClassReflector, primitiveFieldHandler);
                   }
               }
            }
        }
        
        _platformTypes = Platform4.types(container);

        for (int i = 0; i < _platformTypes.length; i++) {
            NetTypeHandler handler = _platformTypes[i]; 
            handler.initialize();
        	int id = handler.getID();
        	
            registerBuiltinHandler(id, handler);

            GenericConverter converter = (handler instanceof GenericConverter) ? (GenericConverter)handler : null;  
            reflector.registerPrimitiveClass(id, handler.getName(), converter);
            
            
            PrimitiveFieldHandler primitiveFieldHandler = new PrimitiveFieldHandler(container, handler, id);
            _classMetadata.put(id, primitiveFieldHandler);
            
            if (id > i_maxTypeID) {
                i_maxTypeID = id;
            }
            _mapReflectorToClassMetadata.put(handler.classReflector(), primitiveFieldHandler);
            if (!Deploy.csharp) {
                if (handler.primitiveClassReflector() != null) {
                	_mapReflectorToClassMetadata.put(handler.primitiveClassReflector(), primitiveFieldHandler);
                }
            }
        }

        i_anyArray = new PrimitiveFieldHandler(container, new ArrayHandler(_container,
            untypedHandler(), false), Handlers4.ANY_ARRAY_ID);
        _classMetadata.put(Handlers4.ANY_ARRAY_ID, i_anyArray);

        i_anyArrayN = new PrimitiveFieldHandler(container, new MultidimensionalArrayHandler(_container,
            untypedHandler(), false), Handlers4.ANY_ARRAY_N_ID);
        _classMetadata.put(Handlers4.ANY_ARRAY_N_ID, i_anyArrayN);

        
    }
    
    private void registerHandlers(byte stringEncoding){
        
        IntHandler intHandler = new IntHandler(_container);
        registerBuiltinHandler(Handlers4.INT_ID, intHandler);
        registerHandlerVersion(intHandler, 0, new IntHandler0(_container));
        
        LongHandler longHandler = new LongHandler(_container);
        registerBuiltinHandler(Handlers4.LONG_ID, longHandler);
        registerHandlerVersion(longHandler, 0, new LongHandler0(_container));
        
        FloatHandler floatHandler = new FloatHandler(_container);
        registerBuiltinHandler(Handlers4.FLOAT_ID, floatHandler);
        registerHandlerVersion(floatHandler, 0, new FloatHandler0(_container));
        
        BooleanHandler booleanHandler = new BooleanHandler(_container);
        registerBuiltinHandler(Handlers4.BOOLEAN_ID, booleanHandler);
        // TODO: Are we missing a boolean handler version?
        
        DoubleHandler doubleHandler = new DoubleHandler(_container);
        registerBuiltinHandler(Handlers4.DOUBLE_ID, doubleHandler);
        registerHandlerVersion(doubleHandler, 0, new DoubleHandler0(_container));
        
        ByteHandler byteHandler = new ByteHandler(_container);
        registerBuiltinHandler(Handlers4.BYTE_ID, byteHandler);
        // TODO: Are we missing a byte handler version?

        CharHandler charHandler = new CharHandler(_container);
        registerBuiltinHandler(Handlers4.CHAR_ID, charHandler);
        // TODO: Are we missing a char handler version?
        
        ShortHandler shortHandler = new ShortHandler(_container);
        registerBuiltinHandler(Handlers4.SHORT_ID, shortHandler);
        registerHandlerVersion(shortHandler, 0, new ShortHandler0(_container));
        
        _stringHandler = new StringHandler2(_container, LatinStringIO.forEncoding(stringEncoding));
        registerBuiltinHandler(Handlers4.STRING_ID, _stringHandler);
        registerHandlerVersion(_stringHandler, 0, new StringHandler0(_stringHandler));

        DateHandler dateHandler = new DateHandler(_container);
        registerBuiltinHandler(Handlers4.DATE_ID, dateHandler);
        registerHandlerVersion(dateHandler, 0, new DateHandler0(_container));
        
        UntypedFieldHandler untypedFieldHandler = new UntypedFieldHandler(_container);
        registerBuiltinHandler(Handlers4.UNTYPED_ID, untypedFieldHandler);
        registerHandlerVersion(untypedFieldHandler, 0, new UntypedFieldHandler0(_container));
    }

    private void registerBuiltinHandler(int id, BuiltinTypeHandler handler) {
        _handlers.put(id, handler);
        _classes.put(id, handler.classReflector());
    }

	private void registerHandlerVersion(TypeHandler4 handler, int version, TypeHandler4 replacement) {
	    _handlerVersions.put(new HandlerVersionKey(handler, version), replacement);
    }

    public TypeHandler4 correctHandlerVersion(TypeHandler4 handler, int version){
        TypeHandler4 replacement = (TypeHandler4) _handlerVersions.get(new HandlerVersionKey(handler, version));
        if(replacement != null){
            return replacement;
        }
        if(handler instanceof MultidimensionalArrayHandler && (version == 0)){
            return new MultidimensionalArrayHandler0(handler);
        }
        if(handler instanceof ArrayHandler  && (version == 0)){
            return new ArrayHandler0(handler);
        }
        return handler;
    }

    int arrayType(Object a_object) {
    	ReflectClass claxx = _container.reflector().forObject(a_object);
        if (! claxx.isArray()) {
            return 0;
        }
        if (_container.reflector().array().isNDimensional(claxx)) {
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
            if(claxx.skipConstructor(skipConstructor, _container.config().testConstructors())){
                return true;
            }
        }
        
        if (! _container.configImpl().testConstructors()) {
            return true;
        }
        
        if (claxx.newInstance() != null) {
            return true;
        }
        
        if (_container.reflector().constructorCallsSupported()) {
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
		
		Iterator4 iter = new TreeNodeIterator(sortedConstructors);
		while (iter.moveNext()) {
			Object obj = iter.current();
			ReflectConstructor constructor = (ReflectConstructor) ((TreeIntObject) obj)._object;
			ReflectClass[] paramTypes = constructor.getParameterTypes();
			Object[] params = new Object[paramTypes.length];
			for (int j = 0; j < params.length; j++) {
				params[j] = nullValue(paramTypes[j]);
			}
			Object res = constructor.newInstance(params);
			if (res != null) {
				claxx.useConstructor(constructor, params);
				return true;
			}
		}
		return false;
	}

	private Object nullValue(ReflectClass clazz) {
		for (int k = 1; k <= PRIMITIVECOUNT; k++) {
		    PrimitiveHandler handler = (PrimitiveHandler) handlerForID(k); 
			if (clazz.equals(handler.primitiveClassReflector())) {
				return handler.primitiveNull();
			}
		}
		return null;
	}
	
	private Tree sortConstructorsByParamsCount(final ReflectClass claxx) {
		ReflectConstructor[] constructors = claxx.getDeclaredConstructors();

		Tree sortedConstructors = null;

		// sort constructors by parameter count
		for (int i = 0; i < constructors.length; i++) {
			constructors[i].setAccessible();
			int parameterCount = constructors[i].getParameterTypes().length;
			sortedConstructors = Tree.add(sortedConstructors,
					new TreeIntObject(i + constructors.length * parameterCount,
							constructors[i]));
		}
		return sortedConstructors;
	}
    
	public final void decrypt(Buffer reader) {
	    if(i_encrypt){
			int encryptorOffSet = i_lastEncryptorByte;
			byte[] bytes = reader._buffer;
			for (int i = reader.length() - 1; i >= 0; i--) {
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
	        for (int i = reader.length() - 1; i >= 0; i--) {
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
        _container.configImpl().oldEncryptionOff();
    }
    
    public final ReflectClass classForID(int id) {
        return (ReflectClass) _classes.get(id);
    }

    public final TypeHandler4 handlerForID(int id) {
        return (TypeHandler4) _handlers.get(id);
    }
    

    // TODO: Interfaces should be handled by the ANY handler but we
    // need to write the code to migrate from the old field handler to the new
    public final TypeHandler4 handlerForClass(ObjectContainerBase container, ReflectClass clazz) {
        return classMetadataForClass(container, clazz).typeHandler();
    }
    
    public final ClassMetadata classMetadataForClass(ObjectContainerBase container, ReflectClass clazz) {
        if(clazz == null){
            return null;
        }
        ReflectClass baseType = Handlers4.baseType(clazz);
        ClassMetadata classMetadata = classMetadataForClass(baseType);
        if (classMetadata != null) {
            return classMetadata;
        }
        return container.produceClassMetadata(baseType);
    }

	public TypeHandler4 untypedHandler() {
		return handlerForID(Handlers4.UNTYPED_ID);
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
        for (int i = 0; i < _db4oTypes.length; i++) {
            if (clazz.isInstance(_db4oTypes[i])) {
                return _db4oTypes[i];
            }
        }
        return null;
    }

    public ClassMetadata classMetadataForId(int id) {
        return primitiveClassById(id);
    }
    
    public ClassMetadata primitiveClassById(int id) {
        return (ClassMetadata) _classMetadata.get(id);
    }


    ClassMetadata classMetadataForClass(ReflectClass clazz) {
        if (clazz == null) {
            return null;
        }
        if (clazz.isArray()) {
            if (_container.reflector().array().isNDimensional(clazz)) {
                return i_anyArrayN;
            }
            return i_anyArray;
        }
        return (ClassMetadata) _mapReflectorToClassMetadata.get(clazz);
    }
    
    public boolean isSecondClass(Object a_object){
    	if(a_object != null){
    		ReflectClass claxx = _container.reflector().forObject(a_object);
    		if(_mapReflectorToClassMetadata.get(claxx) != null){
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

	/**
	 * @deprecated
	 */
	public void replication(ReplicationImpl impl) {
		i_replication = impl;
	}
	
	/**
	 * @deprecated
	 */
	public ReplicationImpl replication(){
		return i_replication;
	}

	public VirtualFieldMetadata virtualFieldByName(String name) {
        for (int i = 0; i < _virtualFields.length; i++) {
            if (name.equals(_virtualFields[i].getName())) {
                return _virtualFields[i];
            }
        }
        return null;
	}

    public boolean isVariableLength(TypeHandler4 handler) {
        return handler instanceof StringHandler || handler instanceof ArrayHandler;
    }
    
    public SharedIndexedFields indexes(){
        return _indexes;
    }
}