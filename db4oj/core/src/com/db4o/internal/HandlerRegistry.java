/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.diagnostic.*;
import com.db4o.internal.fieldhandlers.*;
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
    
    public static final byte HANDLER_VERSION = (byte)2;
    
    private final ObjectContainerBase _container;  // this is the master container and not valid
	                                   // for TransportObjectContainer

    private static final Db4oTypeImpl[]   _db4oTypes     = { new BlobImpl()};

    private ClassMetadata                _untypedArrayFieldHandler;
    
    private ClassMetadata                _untypedMultiDimensionalArrayFieldHandler;

    public StringHandler          _stringHandler;
    
    private Hashtable4           _mapIdToTypeInfo = newHashtable();
    
    private Hashtable4          _mapFieldHandlerToId = newHashtable();
    
    private Hashtable4          _mapTypeHandlerToId = newHashtable();
    

    private int                     _highestBuiltinTypeID     = Handlers4.ANY_ARRAY_N_ID + 1;

    static private final int        PRIMITIVECOUNT  = 8;

    public static final int         ANY_ID        = 11;
    
    private final VirtualFieldMetadata[]         _virtualFields = new VirtualFieldMetadata[2]; 

    private final Hashtable4        _mapReflectorToFieldHandler  = newHashtable();
    
    private final Hashtable4        _mapReflectorToTypeHandler  = newHashtable();
    
    private final Hashtable4        _mapFieldHandlerToReflector  = newHashtable();
    
    private SharedIndexedFields              		_indexes;
    
    MigrationConnection             i_migration;
    
    Db4oReplicationReferenceProvider _replicationReferenceProvider;
    
    public final DiagnosticProcessor      _diagnosticProcessor;
    
    public boolean                 i_encrypt;
    byte[]                  i_encryptor;
    int                     i_lastEncryptorByte;
    
    final GenericReflector                _reflector;
    
    private final Hashtable4 _handlerVersions = newHashtable();
    
    private LatinStringIO _stringIO;
    
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
        
        _stringIO = LatinStringIO.forEncoding(stringEncoding);
    	
    	_container = container;
    	container._handlers = this;
        
        _reflector = reflector;
        _diagnosticProcessor = container.configImpl().diagnosticProcessor();
    	
    	initClassReflectors(reflector);
        
        _indexes = new SharedIndexedFields(container);
        
        _virtualFields[0] = _indexes._version;
        _virtualFields[1] = _indexes._uUID;

        registerBuiltinHandlers();
        
        registerPlatformTypes();
        
        initArrayHandlers();
    }

    private void initArrayHandlers() {
        TypeHandler4 handler = typeHandlerForID(Handlers4.UNTYPED_ID);
        _untypedArrayFieldHandler = new PrimitiveFieldHandler(
            container(), 
            new ArrayHandler(container(),handler, false), 
            Handlers4.ANY_ARRAY_ID,
            ICLASS_OBJECT);
        mapTypeInfo(
            Handlers4.ANY_ARRAY_ID, 
            _untypedArrayFieldHandler, 
            new UntypedArrayFieldHandler(), 
            null,
            null );

        _untypedMultiDimensionalArrayFieldHandler = new PrimitiveFieldHandler(
            container(), 
            new MultidimensionalArrayHandler(container(), handler, false), 
            Handlers4.ANY_ARRAY_N_ID,
            ICLASS_OBJECT);
        mapTypeInfo(
            Handlers4.ANY_ARRAY_N_ID, 
            _untypedMultiDimensionalArrayFieldHandler, 
            new UntypedMultidimensionalArrayFieldHandler(), 
            null,
            null );

    }
    
    private void registerPlatformTypes() {
        NetTypeHandler[] handlers = Platform4.types(container());
        for (int i = 0; i < handlers.length; i++) {
            handlers[i].initialize();
        	GenericConverter converter = (handlers[i] instanceof GenericConverter) ? (GenericConverter)handlers[i] : null;
            registerBuiltinHandler(handlers[i].getID(), handlers[i], true, handlers[i].getName(), converter);
        }
    }
    
    private void registerBuiltinHandlers(){
        
        IntHandler intHandler = new IntHandler(container());
        registerBuiltinHandler(Handlers4.INT_ID, intHandler);
        registerHandlerVersion(intHandler, 0, new IntHandler0(container()));
        
        LongHandler longHandler = new LongHandler(container());
        registerBuiltinHandler(Handlers4.LONG_ID, longHandler);
        registerHandlerVersion(longHandler, 0, new LongHandler0(container()));
        
        FloatHandler floatHandler = new FloatHandler(container());
        registerBuiltinHandler(Handlers4.FLOAT_ID, floatHandler);
        registerHandlerVersion(floatHandler, 0, new FloatHandler0(container()));
        
        BooleanHandler booleanHandler = new BooleanHandler(container());
        registerBuiltinHandler(Handlers4.BOOLEAN_ID, booleanHandler);
        // TODO: Are we missing a boolean handler version?
        
        DoubleHandler doubleHandler = new DoubleHandler(container());
        registerBuiltinHandler(Handlers4.DOUBLE_ID, doubleHandler);
        registerHandlerVersion(doubleHandler, 0, new DoubleHandler0(container()));
        
        ByteHandler byteHandler = new ByteHandler(container());
        registerBuiltinHandler(Handlers4.BYTE_ID, byteHandler);
        // TODO: Are we missing a byte handler version?

        CharHandler charHandler = new CharHandler(container());
        registerBuiltinHandler(Handlers4.CHAR_ID, charHandler);
        // TODO: Are we missing a char handler version?
        
        ShortHandler shortHandler = new ShortHandler(container());
        registerBuiltinHandler(Handlers4.SHORT_ID, shortHandler);
        registerHandlerVersion(shortHandler, 0, new ShortHandler0(container()));
        
        _stringHandler = new StringHandler(container());
        registerBuiltinHandler(Handlers4.STRING_ID, _stringHandler);
        registerHandlerVersion(_stringHandler, 0, new StringHandler0(_stringHandler));

        DateHandler dateHandler = new DateHandler(container());
        registerBuiltinHandler(Handlers4.DATE_ID, dateHandler);
        registerHandlerVersion(dateHandler, 0, new DateHandler0(container()));
        
        registerUntypedHandlers();
    }

    private void registerUntypedHandlers() {
        int id = Handlers4.UNTYPED_ID;
        UntypedFieldHandler untypedFieldHandler = new UntypedFieldHandler(container());
        if(FieldHandlerRefactoring.COMPLETED){
            PrimitiveFieldHandler classMetadata = new PrimitiveFieldHandler(container(), untypedFieldHandler, id, ICLASS_OBJECT);
            map(id, classMetadata, untypedFieldHandler, new PlainObjectHandler(), ICLASS_OBJECT);
        } else {
            registerBuiltinHandler(Handlers4.UNTYPED_ID, untypedFieldHandler, false, null, null);
        }
        registerHandlerVersion(untypedFieldHandler, 0, new UntypedFieldHandler0(container()));
    }
    
    private void registerBuiltinHandler(int id, BuiltinTypeHandler handler) {
        registerBuiltinHandler(id, handler, true, handler.classReflector().getName(), null);
    }

    private void registerBuiltinHandler(int id, BuiltinTypeHandler typeHandler, boolean registerPrimitiveClass, String primitiveName, GenericConverter converter) {

        if(registerPrimitiveClass){
            _reflector.registerPrimitiveClass(id, primitiveName, converter);
        }
        
        ReflectClass classReflector = typeHandler.classReflector();
        PrimitiveFieldHandler fieldHandler = new PrimitiveFieldHandler(container(), typeHandler, id, classReflector);
        
        map(id, fieldHandler, fieldHandler, typeHandler, classReflector);

        if (!Deploy.csharp) {
            if(typeHandler instanceof PrimitiveHandler){
                ReflectClass primitiveClassReflector = 
                    ((PrimitiveHandler) typeHandler).primitiveClassReflector();
                if(primitiveClassReflector != null){
                    mapPrimitive(0, fieldHandler, typeHandler, primitiveClassReflector);
                }
            }
        }
    }
    
    private void map(
        int id,
        ClassMetadata classMetadata,  // TODO: remove when _mapIdToClassMetadata is gone 
        FieldHandler fieldHandler, 
        TypeHandler4 typeHandler, 
        ReflectClass classReflector) {
        
        mapTypeInfo(id, classMetadata, fieldHandler, typeHandler, classReflector);
        
        mapPrimitive(id, fieldHandler, typeHandler, classReflector);
        if (id > _highestBuiltinTypeID) {
            _highestBuiltinTypeID = id;
        }
    }
    
    private void mapTypeInfo(
        int id,
        ClassMetadata classMetadata, 
        FieldHandler fieldHandler,
        TypeHandler4 typeHandler, 
        ReflectClass classReflector) {
        _mapIdToTypeInfo.put(id, new TypeInfo(classMetadata,fieldHandler, typeHandler, classReflector));
    }
    
    private void mapPrimitive(int id, FieldHandler fieldHandler, TypeHandler4 typeHandler, ReflectClass classReflector) {
        _mapFieldHandlerToReflector.put(fieldHandler, classReflector);
        _mapReflectorToFieldHandler.put(classReflector, fieldHandler);
        _mapReflectorToTypeHandler.put(classReflector, typeHandler);
        if(id != 0){
            Integer wrappedID = new Integer(id);
            _mapFieldHandlerToId.put(fieldHandler, wrappedID);
            _mapTypeHandlerToId.put(typeHandler, wrappedID);
        }
    }

	private void registerHandlerVersion(TypeHandler4 handler, int version, TypeHandler4 replacement) {
	    _handlerVersions.put(new HandlerVersionKey(handler, version), replacement);
    }

    public TypeHandler4 correctHandlerVersion(TypeHandler4 handler, int version){
    	if(version == HandlerRegistry.HANDLER_VERSION){
    		return handler;
    	}
        TypeHandler4 replacement = (TypeHandler4) _handlerVersions.get(new HandlerVersionKey(handler, version));
        if(replacement != null){
            return replacement;
        }
        if(handler instanceof MultidimensionalArrayHandler && (version == 0)){
            return new MultidimensionalArrayHandler0((ArrayHandler)handler, this, version);
        }
        if(handler instanceof ArrayHandler  && (version == 0)){
            return new ArrayHandler0((ArrayHandler)handler, this, version);
        }
        if(handler instanceof PrimitiveFieldHandler  && (version == 0)){
            return new PrimitiveFieldHandler((PrimitiveFieldHandler) handler, this, version);
        }
        return handler;
    }

    int arrayType(Object a_object) {
    	ReflectClass claxx = reflector().forObject(a_object);
        if (! claxx.isArray()) {
            return 0;
        }
        if (reflector().array().isNDimensional(claxx)) {
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
            if(claxx.skipConstructor(skipConstructor, container().config().testConstructors())){
                return true;
            }
        }
        
        if (! container().configImpl().testConstructors()) {
            return true;
        }
        
        if (claxx.newInstance() != null) {
            return true;
        }
        
        if (reflector().constructorCallsSupported()) {
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
		    PrimitiveHandler handler = (PrimitiveHandler) typeHandlerForID(k); 
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
    
	public final void decrypt(BufferImpl reader) {
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
	
    public final void encrypt(BufferImpl reader) {
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
        container().configImpl().oldEncryptionOff();
    }
    
    public final ReflectClass classForID(int id) {
        TypeInfo typeInfo = typeInfoForID(id);
        if(typeInfo == null){
            return null;
        }
        return typeInfo.classReflector;
    }

    public final TypeHandler4 typeHandlerForID(int id) {
        TypeInfo typeInfo = typeInfoForID(id);
        if(typeInfo == null){
            return null;
        }
        return typeInfo.typeHandler;
    }
    
    public final FieldHandler fieldHandlerForID(int id) {
        TypeInfo typeInfo = typeInfoForID(id);
        if(typeInfo == null){
            return null;
        }
        return typeInfo.fieldHandler;
    }
    
    private TypeInfo typeInfoForID(int id){
        return (TypeInfo)_mapIdToTypeInfo.get(id);
    }
    
    public final int typeHandlerID(TypeHandler4 handler){
        if(handler instanceof ClassMetadata){
            return ((ClassMetadata)handler).getID();
        }
        Object idAsInt = _mapTypeHandlerToId.get(handler);
        if(idAsInt == null){
            return 0;
        }
        return ((Integer)idAsInt).intValue();
    }

    // TODO: Interfaces should be handled by the ANY handler but we
    // need to write the code to migrate from the old field handler to the new
    public final TypeHandler4 handlerForClass(ObjectContainerBase container, ReflectClass clazz) {
        ClassMetadata classMetadata = classMetadataForClass(container, clazz);
        if(classMetadata == null){
        	return null;
        }
		return classMetadata.typeHandler();
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
        TypeInfo typeInfo = typeInfoForID(id);
        if(typeInfo == null){
            return null;
        }
        return typeInfo.classMetadata;
    }
    
    public FieldHandler fieldHandlerForId(int id){
        TypeInfo typeInfo = typeInfoForID(id);
        if(typeInfo == null){
            return null;
        }
        return typeInfo.fieldHandler;
    }

    ClassMetadata classMetadataForClass(ReflectClass clazz) {
        if (clazz == null) {
            return null;
        }
        if (clazz.isArray()) {
            if (reflector().array().isNDimensional(clazz)) {
                return _untypedMultiDimensionalArrayFieldHandler;
            }
            return _untypedArrayFieldHandler;
        }
        return (ClassMetadata) _mapReflectorToFieldHandler.get(clazz);
    }
    
    public TypeHandler4 typeHandlerForClass(ReflectClass clazz){
        if(clazz == null){
            return null;
        }
        return (TypeHandler4) _mapReflectorToTypeHandler.get(clazz);
    }
    
    public ReflectClass classReflectorForHandler(TypeHandler4 handler){
        return (ReflectClass) _mapFieldHandlerToReflector.get(handler);
    }
    
    public boolean isSecondClass(Object a_object){
    	if(a_object != null){
    		ReflectClass claxx = reflector().forObject(a_object);
    		if(_mapReflectorToFieldHandler.get(claxx) != null){
    			return true;
    		}
            return Platform4.isValueType(claxx);
    	}
    	return false;
    }

    public boolean isSystemHandler(int id) {
    	return id<=_highestBuiltinTypeID;
    }

	public void migrationConnection(MigrationConnection mgc) {
		i_migration = mgc;
	}
	
	public MigrationConnection  migrationConnection() {
		return i_migration;
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
        return handler instanceof VariableLengthTypeHandler;
    }
    
    public SharedIndexedFields indexes(){
        return _indexes;
    }
    
    public LatinStringIO stringIO(){
        return _stringIO;
    }

    public void stringIO(LatinStringIO io) {
        _stringIO = io;
    }
    
    public ClassMetadata classMetadataForObject(Object obj){
        ReflectClass reflectClass = reflector().forObject(obj);
        return container().produceClassMetadata(reflectClass);
    }

    private GenericReflector reflector() {
        return container().reflector();
    }

    private ObjectContainerBase container() {
        return _container;
    }
    
    private static final Hashtable4 newHashtable(){
        return new Hashtable4(32);
    }

}