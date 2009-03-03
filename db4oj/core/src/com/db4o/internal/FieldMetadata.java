/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.handlers.array.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.query.processor.*;
import com.db4o.internal.reflect.FieldAccessor;
import com.db4o.internal.slots.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;
import com.db4o.reflect.generic.*;
import com.db4o.typehandlers.*;

/**
 * @exclude
 */
public class FieldMetadata extends ClassAspect implements StoredField {

    private ClassMetadata         _containingClass;

    private String         _name;
    
    private boolean          _isArray;

    private boolean          _isNArray;

    private boolean          _isPrimitive;
    
    private ReflectField     _reflectField;

    TypeHandler4              _handler;
    
    protected int              _handlerID;

    private FieldMetadataState              _state = FieldMetadataState.NOT_LOADED;

    private Config4Field     _config;

    private Db4oTypeImpl     _db4oType;
    
    private int _linkLength;
    
    private BTree _index;

    static final FieldMetadata[]  EMPTY_ARRAY = new FieldMetadata[0];

    public FieldMetadata(ClassMetadata classMetadata) {
        _containingClass = classMetadata;
    }
    
    FieldMetadata(ClassMetadata containingClass, ObjectTranslator translator) {
        // for TranslatedFieldMetadata only
    	this(containingClass);
        init(containingClass, translator.getClass().getName());
        _state = FieldMetadataState.AVAILABLE;
        ObjectContainerBase stream =container(); 
        ReflectClass claxx = stream.reflector().forClass(translatorStoredClass(translator));
        _handler = fieldHandlerForClass(stream, claxx);
    }

	protected final Class translatorStoredClass(ObjectTranslator translator) {
		try {
			return translator.storedClass();
		} catch (RuntimeException e) {
			throw new ReflectException(e);
		}
	}

    FieldMetadata(ClassMetadata containingClass, ReflectField field, TypeHandler4 handler, int handlerID) {
    	this(containingClass);
        init(containingClass, field.getName());
        _reflectField = field;
        _handler = handler;
        _handlerID = handlerID;
        
        // TODO: beautify !!!  possibly pull up isPrimitive to ReflectField
        boolean isPrimitive = false;
        if(field instanceof GenericField){
            isPrimitive  = ((GenericField)field).isPrimitive();
        }
        configure( field.getFieldType(), isPrimitive);
        checkDb4oType();
        _state = FieldMetadataState.AVAILABLE;
    }
    
    protected FieldMetadata(int handlerID, TypeHandler4 handler){
        _handlerID = handlerID;
        _handler = handler;
    }
    
    public void addFieldIndex(ObjectIdContextImpl context, Slot oldSlot)  throws FieldIndexException {
        if (! hasIndex()) {
            incrementOffset(context);
            return;
        }
        try {
            addIndexEntry(context.transaction(), context.id(), readIndexEntry(context));
        } catch (CorruptionException exc) {
            throw new FieldIndexException(exc,this);
        } 
    }
    
    protected final void addIndexEntry(StatefulBuffer a_bytes, Object indexEntry) {
        addIndexEntry(a_bytes.transaction(), a_bytes.getID(), indexEntry);
    }

    public final void addIndexEntry(Transaction trans, int parentID, Object indexEntry) {
        if (! hasIndex()) {
            return;
        }
            
        BTree index = getIndex(trans);
        
        // Although we checked hasIndex() already, we have to check
        // again here since index creation in YapFieldUUID can be
        // unsuccessful if it's called too early for PBootRecord.
        if(index == null){
            return;
        }
        index.add(trans, createFieldIndexKey(parentID, indexEntry));
    }

	private FieldIndexKey createFieldIndexKey(int parentID, Object indexEntry) {
		Object convertedIndexEntry = indexEntryFor(indexEntry);
		return new FieldIndexKey(parentID,  convertedIndexEntry);
	}

	protected Object indexEntryFor(Object indexEntry) {
		return _reflectField.indexEntry(indexEntry);
	}
    
    public boolean canUseNullBitmap(){
        return true;
    }
    
    public final Object readIndexEntry(ObjectIdContext context) throws CorruptionException, Db4oIOException {
        IndexableTypeHandler indexableTypeHandler = (IndexableTypeHandler) HandlerRegistry.correctHandlerVersion(context, _handler);
        return indexableTypeHandler.readIndexEntry(context);
    }
    
    public void removeIndexEntry(Transaction trans, int parentID, Object indexEntry){
        if (! hasIndex()) {
            return;
        }
        
        if(_index == null){
            return;
        }
        _index.remove(trans, createFieldIndexKey(parentID,  indexEntry));
    }

    public boolean alive() {
        if (_state == FieldMetadataState.AVAILABLE) {
            return true;
        }
        if (_state == FieldMetadataState.NOT_LOADED) {

            if (_handler == null) {

                // this may happen if the local ClassMetadataRepository
                // has not been updated from the server and presumably 
                // in some refactoring cases. 

                // We try to heal the problem by re-reading the class.

                // This could be dangerous, if the class type of a field
                // has been modified.

                // TODO: add class refactoring features

                _handler = detectHandlerForField();
                checkHandlerID();
            }

            checkCorrectHandlerForField();

            // TODO: This part is not quite correct.
            // We are using the old array information read from file to wrap.

            // If a schema evolution changes an array to a different variable,
            // we are in trouble here.
            _handler = wrapHandlerToArrays(_handler);
            
            if(_handler == null || _reflectField == null){
                _state = FieldMetadataState.UNAVAILABLE;
                _reflectField = null;
            } else {
                if(! updating()){
                    _state = FieldMetadataState.AVAILABLE;
                    checkDb4oType();
                }
            }
        }
        return _state == FieldMetadataState.AVAILABLE;
    }

    public boolean updating() {
        return _state == FieldMetadataState.UPDATING;
    }

    private void checkHandlerID() {
        int id = container().handlers().typeHandlerID(_handler);
        if (_handlerID == 0) {
            _handlerID = id;
            return;
        }
        if(id > 0 && id != _handlerID){
            // wrong type, refactoring, field should be turned off
        	// TODO: it would be cool to log something here
            _handler = null;
        }
    }

    boolean canAddToQuery(String fieldName){
        if(! alive()){
            return false;
        }
        return fieldName.equals(getName())  && containingClass() != null && !containingClass().isInternal(); 
    }
    
    public boolean canHold(ReflectClass claxx) {
        // alive() is checked in QField caller
        if (claxx == null) {
            return !_isPrimitive;
        }
        return Handlers4.handlerCanHold(_handler, reflector(), claxx);
    }

    public GenericReflector reflector() {
        ObjectContainerBase container = container();
        if (container == null) {
            return null;
        }
        return container.reflector();
    }

    public Object coerce(ReflectClass claxx, Object obj) {
        // alive() is checked in QField caller
        
        if (claxx == null || obj == null) {
            return _isPrimitive ? No4.INSTANCE : obj;
        }
        
        if(_handler instanceof PrimitiveHandler){
            return ((PrimitiveHandler)_handler).coerce(reflector(), claxx, obj);
        }

        if(! canHold(claxx)){
            return No4.INSTANCE;
        }
        
        return obj;
    }

    public final boolean canLoadByIndex() {
        return Handlers4.canLoadFieldByIndex(_handler);
    }

	public final void cascadeActivation(Transaction trans, Object onObject, ActivationDepth depth) {
        if (! alive()) {
            return;
        }
        
        if(! Handlers4.isFirstClass(_handler)){
            return;
        }
        
        Object cascadeTo = cascadingTarget(trans, depth, onObject);
        if (cascadeTo == null) {
        	return;
        }
        
        ensureObjectIsActive(trans, cascadeTo, depth);
        
        ActivationContext4 context = new ActivationContext4(trans, cascadeTo, depth);
        Handlers4.cascadeActivation(context, _handler);
    }

    private void ensureObjectIsActive(Transaction trans, Object cascadeTo, ActivationDepth depth) {
        if(!depth.mode().isActivate()){
            return;
        }
        if(Handlers4.isEmbedded(_handler)){
            return;
        }
        ObjectContainerBase container = trans.container();
        ClassMetadata classMetadata = container.classMetadataForObject(cascadeTo);
        if(classMetadata == null || classMetadata.isPrimitive()){
            return;
        }
        if(container.isActive(cascadeTo)){
            return;
        }
        container.stillToActivate(trans, cascadeTo, depth.descend(classMetadata));
    }

	protected Object cascadingTarget(Transaction trans, ActivationDepth depth, Object onObject) {
		if (depth.mode().isDeactivate()) {
			if (null == _reflectField) {
				return null;
			}
			return fieldAccessor().get( _reflectField, onObject);
		}
		return getOrCreate(trans, onObject);
	}

    private void checkDb4oType() {
        if (_reflectField != null) {
            if (container()._handlers.ICLASS_DB4OTYPE.isAssignableFrom(_reflectField.getFieldType())) {
                _db4oType = HandlerRegistry.getDb4oType(_reflectField.getFieldType());
            }
        }
    }

    void collectConstraints(Transaction trans, QConObject a_parent,
        Object a_template, Visitor4 a_visitor) {
        Object obj = getOn(trans, a_template);
        if (obj != null) {
            Collection4 objs = Platform4.flattenCollection(trans.container(), obj);
            Iterator4 j = objs.iterator();
            while (j.moveNext()) {
                obj = j.current();
                if (obj != null) {
                    
                    if (_isPrimitive) {
                        if (Handlers4.isPrimitive(_handler)) {
                            Object nullValue = _reflectField.getFieldType().nullValue();
							if (obj.equals(nullValue)) {
                                return;
                            }
                        }
                    }
                    
                    if(Platform4.ignoreAsConstraint(obj)){
                    	return;
                    }
                    if (!a_parent.hasObjectInParentPath(obj)) {
                        QConObject constraint = new QConObject(trans, a_parent,
                                qField(trans), obj);
                        constraint.byExample();
                        a_visitor.visit(constraint);
                    }
                }
            }
        }
    }
    
    public final void collectIDs(CollectIdContext context) throws FieldIndexException {
        if (! alive()) {
        	incrementOffset(context.buffer());
            return ;
        }
        
        final TypeHandler4 handler = HandlerRegistry.correctHandlerVersion(context, _handler);
        Handlers4.collectIdsInternal(context, handler, linkLength());
    }

    void configure(ReflectClass clazz, boolean isPrimitive) {
        _isArray = clazz.isArray();
        if (_isArray) {
            ReflectArray reflectArray = reflector().array();
            _isNArray = reflectArray.isNDimensional(clazz);
            _isPrimitive = reflectArray.getComponentType(clazz).isPrimitive();
            _handler = wrapHandlerToArrays(_handler);
        } else {
        	_isPrimitive = isPrimitive | clazz.isPrimitive();
        }
    }
    
    private final TypeHandler4 wrapHandlerToArrays(TypeHandler4 handler) {
        if(handler == null){
            return null;
        }
        if (_isNArray) {
            return new MultidimensionalArrayHandler(handler, arraysUsePrimitiveClassReflector());
        } 
        if (_isArray) {
            return new ArrayHandler(handler, arraysUsePrimitiveClassReflector());
        }
        return handler;
    }

    private boolean arraysUsePrimitiveClassReflector() {
    	return _isPrimitive;
    }

    public void deactivate(Transaction trans, Object onObject, ActivationDepth depth) {
        
    	if (!alive()) {
            return;
        }
    	
        boolean isEnumClass = _containingClass.isEnum();
		if (_isPrimitive && !_isArray) {
			if (!isEnumClass) {
				Object nullValue = _reflectField.getFieldType().nullValue();
				fieldAccessor().set(_reflectField, onObject, nullValue);
			}
			return;
		}
		if (depth.requiresActivation()) {
			cascadeActivation(trans, onObject, depth);
		}
		if (!isEnumClass) {
			fieldAccessor().set(_reflectField, onObject, null);
		}
    }

	private FieldAccessor fieldAccessor() {
		return _containingClass.fieldAccessor();
	}

    /** @param isUpdate */
    public void delete(DeleteContextImpl context, boolean isUpdate) throws FieldIndexException {
        if (! checkAlive(context)) {
            return;
        }
        try {
            removeIndexEntry(context);
            if(isUpdate){
            	incrementOffset(context);
            	return;
            }
            StatefulBuffer buffer = (StatefulBuffer) context.buffer();
            final DeleteContextImpl childContext = new DeleteContextImpl(context, getStoredType(), _config);
            context.slotFormat().doWithSlotIndirection(buffer, _handler, new Closure4() {
                public Object run() {
                    childContext.delete(_handler);
                    return null;
                }
            });
        } catch (CorruptionException exc) {
            throw new FieldIndexException(exc, this);
        }
    }

    private final void removeIndexEntry(DeleteContextImpl context) throws CorruptionException, Db4oIOException {
        if(! hasIndex()){
            return;
        }
        int offset = context.offset();
        Object obj = readIndexEntry(context);
        removeIndexEntry(context.transaction(), context.id(), obj);
        context.seek(offset);
    }


    public boolean equals(Object obj) {
        if (! (obj instanceof FieldMetadata)) {
            return false;
        }
        FieldMetadata other = (FieldMetadata) obj;
        other.alive();
        alive();
        return other._isPrimitive == _isPrimitive
            && ((_handler == null && other._handler == null) || other._handler.equals(_handler))
            && other._name.equals(_name);
    }

    public int hashCode() {
    	return _name.hashCode();
    }
    
    public final Object get(Object onObject) {
        return get(null, onObject);
    }
    
    public final Object get(Transaction trans, Object onObject) {
		if (_containingClass == null) {
			return null;
		}
		ObjectContainerBase container = container();
		if (container == null) {
			return null;
		}
		synchronized (container.lock()) {
		    
            // FIXME: The following is not really transactional.
            //        This will work OK for normal C/S and for
            //        single local mode but the transaction will
            //        be wrong for MTOC.
		    if(trans == null){
		        trans = container.transaction();
		    }
		    
			container.checkClosed();
			ObjectReference ref = trans.referenceForObject(onObject);
			if (ref == null) {
				return null;
			}
			int id = ref.getID();
			if (id <= 0) {
				return null;
			}
			UnmarshallingContext context = new UnmarshallingContext(trans, ref, Const4.ADD_TO_ID_TREE, false);
			context.activationDepth(new LegacyActivationDepth(1));
            return context.readFieldValue(this);
		}
	}

    public String getName() {
        return _name;
    }

    public final ClassMetadata handlerClassMetadata(ObjectContainerBase container) {
        // alive needs to be checked by all callers: Done
        TypeHandler4 handler = baseTypeHandler();
        if(Handlers4.handlesSimple(handler)){
            return container._handlers.classMetadataForId(handlerID());
        }
        if(handler instanceof ClassMetadata) {
        	return (ClassMetadata)handler;
        }
        return container.classMetadataForReflectClass(_reflectField.getFieldType());
    }

    private TypeHandler4 baseTypeHandler() {
        return Handlers4.baseTypeHandler(_handler);
    }
    
    public TypeHandler4 getHandler() {
        // alive needs to be checked by all callers: Done
        return _handler;
    }
    
    public int handlerID(){
        // alive needs to be checked by all callers: Done
        return _handlerID;
    }

    /** @param trans */
    public Object getOn(Transaction trans, Object onObject) {
		if (alive()) {
			return fieldAccessor().get( _reflectField, onObject);
		}
		return null;
	}

    /**
	 * dirty hack for com.db4o.types some of them need to be set automatically
	 * TODO: Derive from YapField for Db4oTypes
	 */
    public Object getOrCreate(Transaction trans, Object onObject) {
		if (!alive()) {
			return null;
		}
		Object obj = fieldAccessor().get( _reflectField, onObject);
		if (_db4oType != null && obj == null) {
			obj = _db4oType.createDefault(trans);
			fieldAccessor().set(_reflectField, onObject, obj);
		}
		return obj;
	}

    public final ClassMetadata containingClass() {
        // alive needs to be checked by all callers: Done
        return _containingClass;
    }

    public ReflectClass getStoredType() {
        if(_reflectField == null){
            return null;
        }
        return Handlers4.baseType(_reflectField.getFieldType());
    }
    
    public ObjectContainerBase container(){
        if(_containingClass == null){
            return null;
        }
        return _containingClass.container();
    }
    
    public boolean hasConfig() {
    	return _config!=null;
    }
    
    public boolean hasIndex() {
        return _index != null;
    }

    public final void init(ClassMetadata containingClass, String name) {
        _containingClass = containingClass;
        _name = name;
        initIndex(containingClass, name);
    }

	final void initIndex(ClassMetadata containingClass, String name) {
		if (containingClass.config() == null) {
		    return;
		}
        _config = containingClass.config().configField(name);
        if (Debug4.configureAllFields  && _config == null) {
            _config = (Config4Field) containingClass.config().objectField(_name);
        }
	}
    
    public void init(int handlerID, boolean isPrimitive, boolean isArray, boolean isNArray) {
        _handlerID = handlerID;
        _isPrimitive = isPrimitive;
        _isArray = isArray;
        _isNArray = isNArray;
    }

    private boolean _initialized=false;

    final void initConfigOnUp(Transaction trans) {
        if (_config != null&&!_initialized) {
        	_initialized=true;
            _config.initOnUp(trans, this);
        }
    }

    public void instantiate(UnmarshallingContext context) {
        if(! checkAlive(context)) {
            return;
        }
        Object toSet = read(context);
        informAboutTransaction(toSet, context.transaction());
        set(context.persistentObject(), toSet);
    }
    
    public void attemptUpdate(UnmarshallingContext context) {
        if(! updating()){
            incrementOffset(context);
            return;
        }
        int savedOffset = context.offset();
        try{
            Object toSet = context.read(_handler);
            if(toSet != null){
                set(context.persistentObject(), toSet);
            }
        }catch(Exception ex){
            
            // FIXME: COR-547 Diagnostics here please.
            
            context.buffer().seek(savedOffset);
            incrementOffset(context);
        }
    }
    
    private boolean checkAlive(AspectVersionContext context){
    	if(! checkEnabled(context)){
			return false;
		}		
		boolean alive = alive(); 
		if (!alive) {
		    incrementOffset((ReadBuffer)context);
		}
		return alive;
    }

    private void informAboutTransaction(Object obj, Transaction trans){
        if (_db4oType != null  && obj != null) {
            ((Db4oTypeImpl) obj).setTrans(trans);
        }
    }

    public boolean isArray() {
        return _isArray;
    }

    
    public int linkLength() {
        alive();
        
        if(_linkLength == 0){
            _linkLength = calculateLinkLength();
        }
        return _linkLength;
    }
    
    private int calculateLinkLength(){
    	return Handlers4.calculateLinkLength(_handler);
    }
    
    public void loadHandlerById(ObjectContainerBase container) {
        _handler=(TypeHandler4) container.fieldHandlerForId(_handlerID);
    }
    
    private TypeHandler4 detectHandlerForField() {
        ReflectClass claxx = _containingClass.classReflector();
        if (claxx == null) {
            return null;
        }
        _reflectField = claxx.getDeclaredField(_name);
        if (_reflectField == null) {
            return null;
        }
        return fieldHandlerForClass(container(), _reflectField.getFieldType());
    }

    private TypeHandler4 fieldHandlerForClass(ObjectContainerBase container, ReflectClass fieldType) {
        container.showInternalClasses(true);
        TypeHandler4 handlerForClass = 
            (TypeHandler4) container.fieldHandlerForClass(Handlers4.baseType(fieldType));
        container.showInternalClasses(false);
        return handlerForClass;
    }

    private void checkCorrectHandlerForField() {
        TypeHandler4 handler = detectHandlerForField();
        if (handler == null){
            _reflectField = null;
            _state = FieldMetadataState.UNAVAILABLE;
            return;
        }
        if(!handler.equals(_handler)) {
            
            // FIXME: COR-547 Diagnostics here please.
            
            _state = FieldMetadataState.UPDATING;
        }
    }

    private int adjustUpdateDepthForCascade(Object obj, int updateDepth) {
        int minimumUpdateDepth = 1;
        if (_containingClass.isCollection(obj)) {
            GenericReflector reflector = reflector();
            minimumUpdateDepth = reflector.collectionUpdateDepth(reflector.forObject(obj));
        }
        if (updateDepth < minimumUpdateDepth) {
            return minimumUpdateDepth;
        }
        return updateDepth;
    }

    private boolean cascadeOnUpdate(Config4Class parentClassConfiguration) {
        return ((parentClassConfiguration != null && (parentClassConfiguration.cascadeOnUpdate().definiteYes())) || (_config != null && (_config.cascadeOnUpdate().definiteYes())));
    }
    
    public void marshall(MarshallingContext context, Object obj){
    	
        // alive needs to be checked by all callers: Done
        int updateDepth = context.updateDepth();
        if (obj != null && cascadeOnUpdate(context.classConfiguration())) {
            context.updateDepth(adjustUpdateDepthForCascade(obj, updateDepth));
        }
        if(Handlers4.useDedicatedSlot(context, _handler)){
            context.writeObject(_handler, obj);
        }else {
            context.createIndirectionWithinSlot(_handler);
            _handler.write(context, obj);
        }
        
        context.updateDepth(updateDepth);
        
        if(hasIndex()){
            context.addIndexEntry(this, obj);
        }
    }
    
    public boolean needsArrayAndPrimitiveInfo(){
        return true;
    }

    public boolean needsHandlerId(){
        return true;
    }
    
    public PreparedComparison prepareComparison(Context context, Object obj) {
        if (!alive()) {
        	return null;
        }
        return _handler.prepareComparison(context, obj);
    }
    
    public QField qField(Transaction a_trans) {
        int yapClassID = 0;
        if(_containingClass != null){
            yapClassID = _containingClass.getID();
        }
        return new QField(a_trans, _name, this, yapClassID, _handle);
    }

    public Object read(InternalReadContext context) {
        if(!canReadFromSlot((AspectVersionContext) context)) {
			incrementOffset(context);
            return null;
        }
        return context.read(_handler);
    }

	private boolean canReadFromSlot(AspectVersionContext context) {
    	if(! enabled(context)){
    		return false;
    	}
    	if(alive()) {
    		return true;
    	}
		return _state != FieldMetadataState.NOT_LOADED;
	}

    /** never called but keep for Rickie */
    public void refreshActivated() {
    	_state = FieldMetadataState.AVAILABLE;
    	refresh();
    }
    
    void refresh() {
        TypeHandler4 handler = detectHandlerForField();
        if (handler != null) {
            handler = wrapHandlerToArrays(handler);
            if (handler.equals(_handler)) {
                return;
            }
        }
        _reflectField = null;
        _state = FieldMetadataState.UNAVAILABLE;
    }

    // FIXME: needs test case
    public void rename(String newName) {
        ObjectContainerBase container = container();
        if (! container.isClient()) {
            _name = newName;
            _containingClass.setStateDirty();
            _containingClass.write(container.systemTransaction());
        } else {
            Exceptions4.throwRuntimeException(58);
        }
    }

    public void set(Object onObject, Object obj){
    	// TODO: remove the following if and check callers
    	if (null == _reflectField) return;
    	fieldAccessor().set(_reflectField, onObject, obj);
    }

    void setName(String a_name) {
        _name = a_name;
    }

    boolean supportsIndex() {
        return alive() && 
            (_handler instanceof Indexable4)  && 
            (! Handlers4.isUntyped(_handler));
    }
    
    public final void traverseValues(final Visitor4 userVisitor) {
        if(! alive()){
            return;
        }
        traverseValues(container().transaction(), userVisitor);
    }
    
    public final void traverseValues(final Transaction transaction, final Visitor4 userVisitor) {
        if(! alive()){
            return;
        }
        assertHasIndex();
        ObjectContainerBase stream = transaction.container();
        if(stream.isClient()){
            Exceptions4.throwRuntimeException(Messages.CLIENT_SERVER_UNSUPPORTED);
        }
        synchronized(stream.lock()){
            final Context context = transaction.context();
            _index.traverseKeys(transaction, new Visitor4() {
                public void visit(Object obj) {
                    FieldIndexKey key = (FieldIndexKey) obj;
                    userVisitor.visit(((IndexableTypeHandler)_handler).indexEntryToObject(context, key.value()));
                }
            });
        }
    }
    
	private void assertHasIndex() {
		if(! hasIndex()){
            Exceptions4.throwRuntimeException(Messages.ONLY_FOR_INDEXED_FIELDS);
        }
	}

    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (_containingClass != null) {
            sb.append(_containingClass.getName());
            sb.append(".");
            sb.append(getName());
        }
        return sb.toString();
    }

    private void initIndex(Transaction systemTrans) {        
        initIndex(systemTrans, 0);
    }

    public void initIndex(Transaction systemTrans, final int id) {
    	if(_index != null){
    		throw new IllegalStateException();
        }
        if(systemTrans.container().isClient()){
            return;
        }
        _index = newBTree(systemTrans, id);
    }

	protected final BTree newBTree(Transaction systemTrans, final int id) {
		ObjectContainerBase stream = systemTrans.container();
		Indexable4 indexHandler = indexHandler(stream);
		if(indexHandler==null) {
			if(Debug4.atHome) {
				System.err.println("Could not create index for "+this+": No index handler found");
			}
			return null;
		}
		return new BTree(systemTrans, id, new FieldIndexKeyHandler(indexHandler));
	}

	protected Indexable4 indexHandler(ObjectContainerBase stream) {
		if(_reflectField ==null) {
		    return null;
		}
		ReflectClass indexType = _reflectField.indexType();
		TypeHandler4 classHandler = fieldHandlerForClass(stream,indexType);
		if(! (classHandler instanceof Indexable4)){
		    return null;
		}
		return (Indexable4) classHandler;
	}
    
	/** @param trans */
	public BTree getIndex(Transaction trans){
        return _index;
    }

    public boolean isVirtual() {
        return false;
    }

    public boolean isPrimitive() {
        return _isPrimitive;
    }
	
	public BTreeRange search(Transaction transaction, Object value) {
		assertHasIndex();
		Object transActionalValue = Handlers4.wrapWithTransactionContext(transaction, value, _handler);
		BTreeNodeSearchResult lowerBound = searchLowerBound(transaction, transActionalValue);
	    BTreeNodeSearchResult upperBound = searchUpperBound(transaction, transActionalValue);	    
		return lowerBound.createIncludingRange(upperBound);
	}

    private BTreeNodeSearchResult searchUpperBound(Transaction transaction, final Object value) {
		return searchBound(transaction, Integer.MAX_VALUE, value);
	}

	private BTreeNodeSearchResult searchLowerBound(Transaction transaction, final Object value) {
		return searchBound(transaction, 0, value);
	}

	private BTreeNodeSearchResult searchBound(Transaction transaction, int parentID, Object keyPart) {
	    return getIndex(transaction).searchLeaf(transaction, createFieldIndexKey(parentID, keyPart), SearchTarget.LOWEST);
	}

	public boolean rebuildIndexForClass(LocalObjectContainer stream, ClassMetadata yapClass) {
		// FIXME: BTree traversal over index here.
		long[] ids = yapClass.getIDs();		
		for (int i = 0; i < ids.length; i++) {
		    rebuildIndexForObject(stream, yapClass, (int)ids[i]);
		}
		return ids.length > 0;
	}

	/** @param classMetadata */
	protected void rebuildIndexForObject(LocalObjectContainer stream, final ClassMetadata classMetadata, final int objectId) throws FieldIndexException {
		StatefulBuffer writer = stream.readWriterByID(stream.systemTransaction(), objectId);
		if (writer != null) {
		    rebuildIndexForWriter(stream, writer, objectId);
		} else {
		    if(Deploy.debug){
		        throw new Db4oException("Unexpected null object for ID");
		    }
		}
	}

	protected void rebuildIndexForWriter(LocalObjectContainer stream, StatefulBuffer writer, final int objectId) {
		ObjectHeader oh = new ObjectHeader(stream, writer);
		Object obj = readIndexEntryForRebuild(writer, oh);
		addIndexEntry(stream.systemTransaction(), objectId, obj);
	}

	private final Object readIndexEntryForRebuild(StatefulBuffer writer, ObjectHeader oh) {
	    ClassMetadata classMetadata = oh.classMetadata();
        if(classMetadata == null){
            return null;
        }
        ObjectIdContextImpl context = new ObjectIdContextImpl(writer.transaction(), writer, oh, writer.getID());
        if(! classMetadata.seekToField(context, this)){
            return null;
        }
        try {
            return readIndexEntry(context);
        } catch (CorruptionException exc) {
            throw new FieldIndexException(exc,this);
        } 
	}

    public void dropIndex(Transaction systemTrans) {
        if(_index == null){
            return;
        }
        ObjectContainerBase stream = systemTrans.container(); 
        if (stream.configImpl().messageLevel() > Const4.NONE) {
            stream.message("dropping index " + toString());
        }
        _index.free(systemTrans);
        stream.setDirtyInSystemTransaction(containingClass());
        _index = null;
    }    
    
    public void defragAspect(final DefragmentContext context) {
    	final TypeHandler4 typeHandler = HandlerRegistry.correctHandlerVersion(context, _handler);
        context.slotFormat().doWithSlotIndirection(context, typeHandler, new Closure4() {
            public Object run() {
                context.defragment(typeHandler);
                return null;
            }
        });
    }
    
	public void createIndex() {
	    
		if(hasIndex()) {
			return;
		}
		LocalObjectContainer container= (LocalObjectContainer) container();
		
        if (container.configImpl().messageLevel() > Const4.NONE) {
            container.message("creating index " + toString());
        }
	    initIndex(container.systemTransaction());
	    container.setDirtyInSystemTransaction(containingClass());
        reindex(container);
	}

	private void reindex(LocalObjectContainer container) {
		ClassMetadata clazz = containingClass();		
		if (rebuildIndexForClass(container, clazz)) {
		    container.systemTransaction().commit();
		}
	}

    public AspectType aspectType() {
        return AspectType.FIELD;
    }

    // overriden in VirtualFieldMetadata
	public boolean canBeDisabled() {
		return true;
	}

	public void dropIndex() {
		dropIndex(container().systemTransaction());
	}

}