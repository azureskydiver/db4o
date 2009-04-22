/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.classindex.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.diagnostic.*;
import com.db4o.internal.encoding.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.handlers.array.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.query.processor.*;
import com.db4o.internal.reflect.*;
import com.db4o.internal.slots.*;
import com.db4o.marshall.*;
import com.db4o.query.*;
import com.db4o.reflect.*;
import com.db4o.reflect.core.*;
import com.db4o.reflect.generic.*;
import com.db4o.typehandlers.*;


/**
 * @exclude
 */
public class ClassMetadata extends PersistentBase implements StoredClass {
	
	
    /**
     * For reference types, _typeHandler always holds a StandardReferenceTypeHandler
     * that will use the _aspects of this class to take care of its business. A custom
     * type handler would appear as a TypeHandlerAspect in that case.
     * 
     * For value types, _typeHandler always holds the actual value type handler be it
     * a custom type handler or a builtin one.
     */
	protected TypeHandler4 _typeHandler;
    
	public ClassMetadata i_ancestor;

    private Config4Class i_config;

    public ClassAspect[] _aspects;
    
    private final ClassIndexStrategy _index;
    
    private String i_name;

    private final ObjectContainerBase _container;

    byte[] i_nameBytes;
    private ByteArrayBuffer i_reader;

    private boolean _classIndexed;
    
    private ReflectClass _classReflector;
    
    private EventDispatcher _eventDispatcher;
    
    private boolean _internal;
    
    private boolean _unversioned;
    
    private TernaryBool _canUpdateFast=TernaryBool.UNSPECIFIED;
    
    private TranslatedAspect _translator;
    
    private ModificationAware _modificationChecker = AlwaysModified.INSTANCE;
    
    private FieldAccessor _fieldAccessor;

	private Function4<UnmarshallingContext, Object> _constructor;

	private TypeHandlerAspect _customTypeHandlerAspect;
    
    final boolean canUpdateFast(){
        if(_canUpdateFast == TernaryBool.UNSPECIFIED){
            _canUpdateFast = TernaryBool.forBoolean(checkCanUpdateFast());
        }
    	return _canUpdateFast.booleanValue(false);
    }
    
    private final boolean checkCanUpdateFast() {
    	if(i_ancestor != null && ! i_ancestor.canUpdateFast()){
    		return false;
    	}
		if(i_config != null && i_config.cascadeOnDelete() == TernaryBool.YES) {
			return false;
		}
		final BooleanByRef hasIndex = new BooleanByRef(false); 
		forEachDeclaredField(new Procedure4() {
            public void apply(Object arg) {
                if(((FieldMetadata)arg).hasIndex()){
                    hasIndex.value = true;
                }
            }
        });
		return ! hasIndex.value;
	}

	public boolean isInternal() {
    	return _internal;
    }

    private ClassIndexStrategy createIndexStrategy() {
		return new BTreeClassIndexStrategy(this);
	}
    
    protected ClassMetadata(ObjectContainerBase container){
    	if (null == container) {
    		throw new ArgumentNullException();
    	}
    	_container = container;
    	_index = createIndexStrategy();
        _classIndexed = true;
        _fieldAccessor = new StrictFieldAccessor();
    }  

    public ClassMetadata(ObjectContainerBase container, ReflectClass classReflector){
    	if (null == container) {
    		throw new ArgumentNullException();
    	}
    	_container = container;
    	classReflector(classReflector);
    	_index = createIndexStrategy();
        _classIndexed = true;       
       
        if (_container.config().exceptionsOnNotStorable()) {
        	_fieldAccessor = new StrictFieldAccessor();	
        } else {
        	_fieldAccessor = new LenientFieldAccessor();
        }        
    }
    
    FieldAccessor fieldAccessor() {
    	return _fieldAccessor;
    }

    private TypeHandler4 createDefaultTypeHandler() {
    	// TODO: make sure initializeAspects has been executed
    	// before the actual type handler is required
    	// and remove this method
    	return new StandardReferenceTypeHandler(this);
    }
    
    public void cascadeActivation(final ActivationContext context) {
        if(objectCanActivate(context.transaction(), context.targetObject())){
            forEachAspect(new Procedure4() {
                public void apply(Object arg) {
                    ((ClassAspect)arg).cascadeActivation(context);
                }
            });
        }
    }

    public final void addFieldIndices(StatefulBuffer buffer, Slot slot) {
        if(! standardReferenceTypeHandlerIsUsed()){
            return;
        }
        if(hasClassIndex() || hasVirtualAttributes()){
            ObjectHeader oh = new ObjectHeader(_container, this, buffer);
            ObjectIdContextImpl context = new ObjectIdContextImpl(buffer.transaction(), buffer, oh, buffer.getID());
            Handlers4.fieldAwareTypeHandler(correctHandlerVersion(context)).addFieldIndices(context, slot);
        }
    }
    
    // FIXME: This method wants to be removed.
    private boolean standardReferenceTypeHandlerIsUsed(){
        return _typeHandler instanceof StandardReferenceTypeHandler;
    }
    
    void initializeAspects() {
    	bitTrue(Const4.CHECKED_CHANGES);

		Collection4 aspects = new Collection4();

		if (null != _aspects) {
			aspects.addAll(_aspects);
		}
		
		final TypeHandler4 customTypeHandler = container().handlers().configuredTypeHandler(classReflector());

		boolean dirty = isDirty();
		
		if(installTranslator(aspects, customTypeHandler)){
			dirty = true;
		}

		if (container().detectSchemaChanges()) {

			if (generateVersionNumbers()) {
				if (!hasVersionField()) {
					aspects.add(container().versionIndex());
					dirty = true;
				}
			}
			if (generateUUIDs()) {
				if (!hasUUIDField()) {
					aspects.add(container().uUIDIndex());
					dirty = true;
				}
			}
		}

		if(installCustomTypehandler(aspects, customTypeHandler)){
			dirty = true;
		}
		
		boolean defaultFieldBehaviour = _translator == null  &&  customTypeHandler == null; 

		if (container().detectSchemaChanges()) {

			if (defaultFieldBehaviour) {
				if (collectReflectFields(aspects)) {
					dirty = true;
				}
			}

			if (dirty) {
				_container.setDirtyInSystemTransaction(this);
			}

		}

		if (dirty || ! defaultFieldBehaviour) {
			_aspects = toClassAspectArray(aspects);
		}

		DiagnosticProcessor dp = _container._handlers._diagnosticProcessor;
		if (dp.enabled()) {
			dp.checkClassHasFields(this);
		}

		if (_aspects == null) {
			_aspects = new FieldMetadata[0];
		}

		initializeConstructor(customTypeHandler);
		if (stateDead()) {
			return;
		}
		_container.callbacks().classOnRegistered(this);
		setStateOK();
	}

	private ClassAspect[] toClassAspectArray(Collection4 aspects) {
		final ClassAspect[] array = new ClassAspect[aspects.size()];
		aspects.toArray(array);
		for (int i = 0; i < array.length; i++) {
			array[i].setHandle(i);
		}
		return array;
	}

	private boolean installCustomTypehandler(Collection4 aspects, TypeHandler4 customTypeHandler) {
		if (customTypeHandler == null) {
			return false;
		}
		if(customTypeHandler instanceof ModificationAware){
			_modificationChecker = (ModificationAware) customTypeHandler;
		}
		if(Handlers4.isStandaloneTypeHandler(customTypeHandler)){
			_typeHandler = customTypeHandler;
			return false;
		}
		boolean dirty = false;
		TypeHandlerAspect typeHandlerAspect = new TypeHandlerAspect(this, customTypeHandler);
		if (!replaceAspectByName(aspects, typeHandlerAspect)) {
			aspects.add(typeHandlerAspect);
			dirty = true;
		}
		disableAspectsBefore(aspects, typeHandlerAspect);
		
		_customTypeHandlerAspect = typeHandlerAspect;
		
		return dirty;
	}

	private void disableAspectsBefore(Collection4 aspects, TypeHandlerAspect typeHandlerAspect) {
		int disableFromVersion = aspects.indexOf(typeHandlerAspect) + 1;
		Iterator4 i = aspects.iterator();
		while(i.moveNext()){
			ClassAspect aspect = (ClassAspect) i.current();
			if(aspect == typeHandlerAspect){
				break;
			}
			aspect.disableFromAspectCountVersion(disableFromVersion);
		}
	}

	private boolean installTranslator(Collection4 aspects,
			TypeHandler4 customTypeHandler) {
    	if( i_config == null){
    		return false;
    	}
		ObjectTranslator translator = i_config.getTranslator();
		if (translator == null) {
			return false;
		}
		ClassAspect existingAspect = aspectByName(aspects, TranslatedAspect.fieldNameFor(translator));
		if (null != existingAspect) {
			return installTranslatorOnExistingAspect(translator, existingAspect, aspects);
		}
		
		if(customTypeHandler == null){
			return installTranslatorOnNewAspect(translator, aspects);
		}
		return false;
	}

	private boolean installTranslatorOnNewAspect(ObjectTranslator translator,
			Collection4 aspects) {
		TranslatedAspect translatedAspect = new TranslatedAspect(this, translator);
		aspects.add(translatedAspect);
		_translator = translatedAspect;
		return true;
	}

	private boolean installTranslatorOnExistingAspect(
			ObjectTranslator translator, ClassAspect existingAspect,
			Collection4 aspects) {
		if (existingAspect instanceof TranslatedAspect) {
			TranslatedAspect translatedAspect = (TranslatedAspect) existingAspect;
			translatedAspect.initializeTranslator(translator);
			_translator = translatedAspect;
			return false;
		}
		
		// older versions didn't store the aspect type properly
		_translator = new TranslatedAspect(this, translator);
		aspects.replaceByIdentity(existingAspect, _translator);
		
		return true;
	}

    private boolean replaceAspectByName(Collection4 aspects, ClassAspect aspect) {
    	ClassAspect existing = aspectByName(aspects, aspect.getName());
        if (existing == null) {
        	return false;
        }
        aspects.replaceByIdentity(existing, aspect);
        return true;
    }

	private ClassAspect aspectByName(Collection4 aspects,
			final String aspectName) {
        Iterator4 i = aspects.iterator();
        while (i.moveNext()) {
            ClassAspect current = (ClassAspect) i.current();
			if (current.getName().equals(aspectName)) {
            	return current;
            }
        }
		return null;
	}

    private boolean collectReflectFields(Collection4 collectedAspects) {
		boolean dirty=false;
		for (ReflectField reflectField : reflectFields()) {
			if (!storeField(reflectField)) {
				continue;
			}
            final ClassMetadata classMetadata = Handlers4.erasedFieldType(container(), reflectField.getFieldType());
            if (classMetadata == null) {
                continue;
            }
            FieldMetadata field = new FieldMetadata(this, reflectField, classMetadata);
	        if (contains(collectedAspects, field)) {
	            continue;
	        }
	        dirty = true;
	        collectedAspects.add(field);
		}
		return dirty;
	}

	private boolean contains(Collection4 collectedAspects, FieldMetadata field) {
        Iterator4 aspectIterator = collectedAspects.iterator();
        while (aspectIterator.moveNext()) {
            if (((ClassAspect)aspectIterator.current()).equals(field)) {
            	return true;
            }
        }
        return false;
	}

	void addToIndex(Transaction trans, int id) {
        if (! trans.container().maintainsIndices()) {
            return;
        }
        addToIndex1(trans, id);
    }

    final void addToIndex1(Transaction a_trans, int a_id) {
        if (i_ancestor != null) {
            i_ancestor.addToIndex1(a_trans, a_id);
        }
        if (hasClassIndex()) {
            _index.add(a_trans, a_id);
        }
    }

    boolean allowsQueries() {
        return hasClassIndex();
    }
    
    public boolean descendOnCascadingActivation() {
        return true;
    }

    void checkChanges() {
        if (stateOK()) {
            if (!bitIsTrue(Const4.CHECKED_CHANGES)) {
                bitTrue(Const4.CHECKED_CHANGES);
                if (i_ancestor != null) {
                    i_ancestor.checkChanges();
                    // Ancestor first, so the object length calculates
                    // correctly
                }
                if (_classReflector != null) {
                    initializeAspects();
                    if (!_container.isClient() && !isReadOnlyContainer()) {
						write(_container.systemTransaction());
                    }
                }
            }
        }
    }
    
    public void checkType() {
        ReflectClass claxx = classReflector();
        if (claxx == null){
            return;
        }
        if (_container._handlers.ICLASS_INTERNAL.isAssignableFrom(claxx)) {
            _internal = true;
        }
        if (_container._handlers.ICLASS_UNVERSIONED.isAssignableFrom(claxx)) {
            _unversioned = true;
        }        
        if (isDb4oTypeImpl()) {
        	Db4oTypeImpl db4oTypeImpl = (Db4oTypeImpl) claxx.newInstance();
        	_classIndexed = (db4oTypeImpl == null || db4oTypeImpl.hasClassIndex());
		} else if(i_config != null){
			_classIndexed = i_config.indexed();
		}
    }
    
    public boolean isDb4oTypeImpl() {
    	return _container._handlers.ICLASS_DB4OTYPEIMPL.isAssignableFrom(classReflector());
    }

	public final int adjustUpdateDepth(Transaction trans, int depth) {
        Config4Class config = configOrAncestorConfig();
        if (depth == Const4.UNSPECIFIED) {
            depth = checkUpdateDepthUnspecified(trans.container().configImpl());
            depth = adjustCollectionDepthToBorders(depth);
        }
        if(config == null){
            return depth - 1;
        }
        boolean cascadeOnDelete = config.cascadeOnDelete() == TernaryBool.YES;
        boolean cascadeOnUpdate = config.cascadeOnUpdate() == TernaryBool.YES;
        
        if ( cascadeOnDelete || cascadeOnUpdate) {
            depth = adjustDepthToBorders(depth);
        }
        return depth - 1;
    }
	
	public int adjustCollectionDepthToBorders(int depth) {
	    if (! classReflector().isCollection()) {
	        return depth;
	    }
	    return adjustDepthToBorders(depth);
	}

	public int adjustDepthToBorders(int depth) {
		int depthBorder = reflector().collectionUpdateDepth(classReflector());
		if (depth > Integer.MIN_VALUE && depth < depthBorder) {
		    depth = depthBorder;
		}
		return depth;
	}

    private final int checkUpdateDepthUnspecified(Config4Impl config) {
        int depth = config.updateDepth() + 1;
        if (i_config != null && i_config.updateDepth() != 0) {
            depth = i_config.updateDepth() + 1;
        }
        if (i_ancestor != null) {
            int ancestordepth = i_ancestor.checkUpdateDepthUnspecified(config);
            if (ancestordepth > depth) {
                return ancestordepth;
            }
        }
        return depth;
    }

    public void collectConstraints(
        final Transaction trans,
        final QConObject parentConstraint,
        final Object obj,
        final Visitor4 visitor) {
        forEachField(new Procedure4() {
            public void apply(Object arg) {
                FieldMetadata fieldMetadata = (FieldMetadata)arg;
                if(fieldMetadata.isEnabledOn(AspectVersionContextImpl.CHECK_ALWAYS_ENABLED)){
                	fieldMetadata.collectConstraints(trans, parentConstraint, obj, visitor);
                }
            }
        });
    }
    
    public final void collectIDs(CollectIdContext context, String fieldName) {
        if(! standardReferenceTypeHandlerIsUsed()){
            throw new IllegalStateException();
        }
        ((StandardReferenceTypeHandler)correctHandlerVersion(context)).collectIDs(context, fieldName);
        
    }
    
    public void collectIDs(final QueryingReadContext context) {
        if(! standardReferenceTypeHandlerIsUsed()){
            throw new IllegalStateException();
        }
        Handlers4.collectIDs(context, correctHandlerVersion(context));
    }

	public Config4Class config() {
    	return i_config;
    }

    public Config4Class configOrAncestorConfig() {
        if (i_config != null) {
            return i_config;
        }
        if (i_ancestor != null) {
            return i_ancestor.configOrAncestorConfig();
        }
        return null;
    }

    private void resolveClassReflector(String className) {
        final ReflectClass reflectClass = _container.reflector().forName(className);
        if (null == reflectClass) {
        	throw new IllegalStateException("Cannot initialize ClassMetadata for '" + className + "'.");
        }
		classReflector(reflectClass);
    }

    private void initializeConstructor(final TypeHandler4 customTypeHandler) {
    	
    	if (isTransient()) {
            _container.logMsg(23, getName());
            setStateDead();
            return;
        }
    	
    	if (isInterface() || isAbstract()) {
    		return;
        }
    	
    	Function4<UnmarshallingContext, Object> constructor = createConstructor(customTypeHandler);
	    if (constructor != null) {
	    	_constructor = constructor;
	    	return;
	    }
	    
	    notStorable();
    }

	private boolean isAbstract() {
		return classReflector().isAbstract();
	}

	private boolean isInterface() {
		return classReflector().isInterface();
	}

	private Function4<UnmarshallingContext, Object> createConstructor(
			final TypeHandler4 customTypeHandler) {
		
		if (customTypeHandler instanceof InstantiatingTypeHandler) {
    		return new Function4<UnmarshallingContext, Object>() {
        		public Object apply(UnmarshallingContext context) {
					return instantiateWithCustomTypeHandlerIfEnabled(context);
                }
        	};
    	}
        
        if (hasObjectConstructor()) {
        	return new Function4<UnmarshallingContext, Object>() {
        		public Object apply(UnmarshallingContext context) {
        			return _translator.construct(context);
                }
        	};
        }
        
	    if(classReflector().ensureCanBeInstantiated()) {
	    	return new Function4<UnmarshallingContext, Object>() {
        		public Object apply(UnmarshallingContext context) {
        			return instantiateFromReflector(context.container());
                }
        	};
        }
		return null;
	}

	private void notStorable() {
	    _container.logMsg(7, getName());
        setStateDead();
    }

	private boolean isTransient() {
	    return _container._handlers.isTransient(classReflector());
    }

	private void classReflector(ReflectClass claxx) {
	    _classReflector = claxx;
	    if(claxx == null){
	        _typeHandler = null;
	        return;
	    }
	    _typeHandler = createDefaultTypeHandler();
    }

    public void deactivate(Transaction trans, Object obj, ActivationDepth depth) {
        if(objectCanDeactivate(trans, obj)){
            deactivateFields(trans.container().activationContextFor(trans, obj, depth));
            objectOnDeactivate(trans, obj);
        }
    }

	private void objectOnDeactivate(Transaction transaction, Object obj) {
		ObjectContainerBase container = transaction.container();
		container.callbacks().objectOnDeactivate(transaction, obj);
		dispatchEvent(transaction, obj, EventDispatchers.DEACTIVATE);
	}

	private boolean objectCanDeactivate(Transaction transaction, Object obj) {
		ObjectContainerBase container = transaction.container();
		return container.callbacks().objectCanDeactivate(transaction, obj)
			&& dispatchEvent(transaction, obj, EventDispatchers.CAN_DEACTIVATE);
	}

    final void deactivateFields(final ActivationContext context) {
        forEachAspect(new Procedure4() {
            public void apply(Object arg) {
                ClassAspect classAspect = (ClassAspect)arg;
                if(classAspect.isEnabledOn(AspectVersionContextImpl.CHECK_ALWAYS_ENABLED)){
                	classAspect.deactivate(context);
                }
            }
        });
    }

    final void delete(StatefulBuffer buffer, Object obj) {
        ObjectHeader oh = new ObjectHeader(_container, this, buffer);
        
        Transaction trans = buffer.transaction();
        int id = buffer.getID();
        ArrayType arrayType = trans.container()._handlers.arrayType(obj);
        
        removeFromIndex(trans, id);
        
        DeleteContextImpl context = new DeleteContextImpl(buffer,oh,  classReflector(), null);
        deleteMembers(context, arrayType, false);
    }

    public void delete(DeleteContext context) throws Db4oIOException {
        correctHandlerVersion(context).delete(context);
    }
    
    void deleteMembers(DeleteContextImpl context, ArrayType arrayType, boolean isUpdate) {
        StatefulBuffer buffer = (StatefulBuffer) context.buffer();
        int preserveCascade = context.cascadeDeleteDepth();
        try{
            if (cascadeOnDelete()) {
                if (classReflector().isCollection()) {
                    buffer.setCascadeDeletes(collectionDeleteDepth(context));
                } else {
                    buffer.setCascadeDeletes(1);
                }
            }
            Handlers4.fieldAwareTypeHandler(correctHandlerVersion(context)).deleteMembers(context, isUpdate);

        }catch(Exception e){
            
            // This a catch for changed class hierarchies.
            // It's very ugly to catch all here but it does
            // help to heal migration from earlier db4o
            // versions.
            
            DiagnosticProcessor dp = container()._handlers._diagnosticProcessor;
            if(dp.enabled()){
                dp.deletionFailed();
            }
            
            if(Debug4.atHome){
                e.printStackTrace();
            }
        }
        buffer.setCascadeDeletes(preserveCascade);
    }


    private int collectionDeleteDepth(DeleteContextImpl context) {
        int depth = 
        	reflector().collectionUpdateDepth(classReflector())
        	- 2;  // Minus two ???  
        if (depth < 1) {
            depth = 1;
        }
        return depth;
    }

    /*
     * If we use KEY as the parameter, this method can be more generic.
     */
	public TernaryBool cascadeOnDeleteTernary() {
		Config4Class config = config();
		TernaryBool cascadeOnDelete = TernaryBool.UNSPECIFIED;
		if(config != null && (cascadeOnDelete = config.cascadeOnDelete())!= TernaryBool.UNSPECIFIED) {
			return cascadeOnDelete;
		}
		if(i_ancestor == null) {
			return cascadeOnDelete;
		}
		return i_ancestor.cascadeOnDeleteTernary();
	}
	
	public boolean cascadeOnDelete() {
		return cascadeOnDeleteTernary() == TernaryBool.YES;
	}

    public final boolean dispatchEvent(Transaction  trans, Object obj, int message) {
    	return eventDispatcher().dispatch(trans, obj, message);
    }
    
    public final boolean hasEventRegistered(Transaction trans, int eventID) {
    	return eventDispatcher().hasEventRegistered(eventID);
    }
    
    private EventDispatcher eventDispatcher() {
    	if (null != _eventDispatcher) {
    		return _eventDispatcher;
    	}
	    
	    _eventDispatcher = EventDispatchers.forClass(_container, classReflector());
		return _eventDispatcher;
    }

	public final int declaredAspectCount(){
    	if(_aspects == null){
    		return 0;
    	}
    	return _aspects.length;
    }
    
    public final int aspectCount(){
        int count = declaredAspectCount();
        if(i_ancestor != null){
            count += i_ancestor.aspectCount();
        }
        return count;
    }
    

    // Scrolls offset in passed reader to the offset the passed field should
    // be read at.
    public final HandlerVersion seekToField(Transaction trans, ByteArrayBuffer buffer, FieldMetadata field) {
        if (buffer == null) {
            return HandlerVersion.INVALID;
        }
        if(! standardReferenceTypeHandlerIsUsed()){
            return HandlerVersion.INVALID;
        }
        buffer.seek(0);
        ObjectHeader oh = new ObjectHeader(_container, this, buffer);
        boolean res = seekToField(new ObjectHeaderContext(trans, buffer, oh), field);
        if(! res){
            return HandlerVersion.INVALID;
        }
        return new HandlerVersion(oh.handlerVersion());
    }
    
    public final boolean seekToField(ObjectHeaderContext context, ClassAspect field){
        return Handlers4.fieldAwareTypeHandler(correctHandlerVersion(context)).seekToField(context, field);
    }

    public boolean generateUUIDs() {
        if(! generateVirtual()){
            return false;
        }
        boolean configValue = (i_config == null) ? false : i_config.generateUUIDs();
        return generate1(_container.config().generateUUIDs(), configValue); 
    }

    private boolean generateVersionNumbers() {
        if(! generateVirtual()){
            return false;
        }
        boolean configValue = (i_config == null) ? false : i_config.generateVersionNumbers();
        return generate1(_container.config().generateVersionNumbers(), configValue); 
    }
    
    private boolean generateVirtual(){
        if(_unversioned){
            return false;
        }
        if(_internal){
            return false;
        }
        return true; 
    }
    
    private boolean generate1(ConfigScope globalConfig, boolean individualConfig) {
    	return globalConfig.applyConfig(individualConfig);
    }


    public ClassMetadata getAncestor() {
        return i_ancestor;
    }

    public Object getComparableObject(Object forObject) {
        if (i_config != null) {
            if (i_config.queryAttributeProvider() != null) {
                return i_config.queryAttributeProvider().attribute(forObject);
            }
        }
        return forObject;
    }

    public ClassMetadata getHigherHierarchy(ClassMetadata a_yapClass) {
        ClassMetadata yc = getHigherHierarchy1(a_yapClass);
        if (yc != null) {
            return yc;
        }
        return a_yapClass.getHigherHierarchy1(this);
    }

    private ClassMetadata getHigherHierarchy1(ClassMetadata a_yapClass) {
        if (a_yapClass == this) {
            return this;
        }
        if (i_ancestor != null) {
            return i_ancestor.getHigherHierarchy1(a_yapClass);
        }
        return null;
    }

    public ClassMetadata getHigherOrCommonHierarchy(ClassMetadata a_yapClass) {
        ClassMetadata yc = getHigherHierarchy1(a_yapClass);
        if (yc != null) {
            return yc;
        }
        if (i_ancestor != null) {
            yc = i_ancestor.getHigherOrCommonHierarchy(a_yapClass);
            if (yc != null) {
                return yc;
            }
        }
        return a_yapClass.getHigherHierarchy1(this);
    }

    public byte getIdentifier() {
        return Const4.YAPCLASS;
    }

    public long[] getIDs() {
        synchronized(lock()){
	        if (! stateOK()) {
                return new long[0];
            }
	        return getIDs(_container.transaction());
        }
    }

    public long[] getIDs(Transaction trans) {
        synchronized(lock()){
            if (! stateOK()) {
                return new long[0];
            }        
            if (! hasClassIndex()) {
                return new long[0];
            }        
            return trans.container().getIDsForClass(trans, this);
        }
    }

    public boolean hasClassIndex() {
        if(! _classIndexed){
            return false;
        }
        return standardReferenceTypeHandlerIsUsed() || !  (Handlers4.isValueType(_typeHandler)); 
    }
    
    private boolean ancestorHasUUIDField(){
        if(i_ancestor == null) {
            return false;
        }
        return i_ancestor.hasUUIDField();
    }
    
    private boolean hasUUIDField() {
        if(ancestorHasUUIDField()){
            return true;
        }
        return Arrays4.containsInstanceOf(_aspects, UUIDFieldMetadata.class);
    }
    
    private boolean ancestorHasVersionField(){
        if(i_ancestor == null){
            return false;
        }
        return i_ancestor.hasVersionField();
    }
    
    private boolean hasVersionField() {
        if(ancestorHasVersionField()){
            return true;
        }
        return Arrays4.containsInstanceOf(_aspects, VersionFieldMetadata.class);
    }

    public ClassIndexStrategy index() {
    	return _index;
    }    
    
    public int indexEntryCount(Transaction ta){
        if(!stateOK()){
            return 0;
        }
        return _index.entryCount(ta);
    }
    
 
    public ReflectClass classReflector(){
        return _classReflector;
    }

    public String getName() {
        if(i_name == null){
            if(_classReflector != null){
                setName(_classReflector.getName());
            }
        }
        return i_name;
    }
    
    public StoredClass getParentStoredClass(){
        return getAncestor();
    }

    public StoredField[] getStoredFields(){
        synchronized(lock()){
	        if(_aspects == null){
	            return new StoredField[0];
	        }
	        final Collection4 storedFields = new Collection4();
	        forEachDeclaredField(new Procedure4() {
				public void apply(Object field) {
					storedFields.add(field);
				}
			});
	        StoredField[] fields = new StoredField[storedFields.size()];
	        storedFields.toArray(fields);
	        return fields;
        }
    }

    public final ObjectContainerBase container() {
        return _container;
    }

    public FieldMetadata fieldMetadataForName(final String name) {
        final ByRef byReference = new ByRef();
        forEachField(new Procedure4() {
			public void apply(Object arg) {
                if (name.equals(((FieldMetadata)arg).getName())) {
                    byReference.value = arg;
                }
			}
		});
        return (FieldMetadata) byReference.value;
    }
    
    /** @param container */
    public boolean hasField(ObjectContainerBase container, String fieldName) {
    	if(classReflector().isCollection()){
            return true;
        }
        return fieldMetadataForName(fieldName) != null;
    }
    
    boolean hasVirtualAttributes(){
        if(_internal){
            return false;
        }
        return hasVersionField() || hasUUIDField(); 
    }

    public boolean holdsAnyClass() {
      return classReflector().isCollection();
    }

    void incrementFieldsOffset1(ByteArrayBuffer a_bytes) {
        int length = readAspectCount(a_bytes);
        for (int i = 0; i < length; i++) {
            _aspects[i].incrementOffset(a_bytes);
        }
    }

    final boolean init(ClassMetadata ancestor) {
    	
    	if(DTrace.enabled){
            DTrace.CLASSMETADATA_INIT.log(getID());
        }
    	
    	setConfig(configImpl().configClass(getName()));
        
        setAncestor(ancestor);
        
        checkType();
        
        if (allowsQueries()) {
            _index.initialize(_container);
        }
        bitTrue(Const4.CHECKED_CHANGES);
        
        return true;
    }
    
    final void initConfigOnUp(Transaction systemTrans) {
        Config4Class extendedConfig=Platform4.extendConfiguration(_classReflector, _container.configure(), i_config);
    	if(extendedConfig!=null) {
    		i_config=extendedConfig;
    	}
        if (i_config == null) {
            return;
        }
        if (! stateOK()) {
            return;
        }
        
        initializeFieldsConfiguration(systemTrans, extendedConfig);
        
        checkAllConfiguredFieldsExist(extendedConfig);
    }

	private void initializeFieldsConfiguration(Transaction systemTrans, Config4Class extendedConfig) {
	    if (_aspects == null) {
        	return;
        }
        for (int i = 0; i < _aspects.length; i++) {
            if(_aspects[i] instanceof FieldMetadata){
                FieldMetadata field = (FieldMetadata) _aspects[i];
                String fieldName = field.getName();
    			if(!field.hasConfig()
    				&& extendedConfig !=null
    				&& extendedConfig.configField(fieldName) != null) {
                	field.initConfiguration(fieldName);
                }
    			field.initConfigOnUp(systemTrans);
            }
        }
    }

    private void checkAllConfiguredFieldsExist(Config4Class config) {
    	Hashtable4 exceptionalFields = config.exceptionalFieldsOrNull();
        if (exceptionalFields == null) {
            return;
        }
        Iterator4 i = exceptionalFields.valuesIterator();
        while(i.moveNext()){
        	Config4Field fieldConfig = (Config4Field) i.current();
        	if(! fieldConfig.used()){
        		configImpl().diagnosticProcessor().objectFieldDoesNotExist(getName(), fieldConfig.getName());
        	}
        }
	}

	void initOnUp(Transaction systemTrans) {
        if (! stateOK()) {
            return;
        }
        initConfigOnUp(systemTrans);
        storeStaticFieldValues(systemTrans, false);
    }
	
    public Object instantiate(UnmarshallingContext context) {
        
        // overridden in PrimitiveTypeMetadata
        // never called for primitive YapAny
        
    	// FIXME: [TA] no longer necessary?
//        context.adjustInstantiationDepth();
        
        Object obj = context.persistentObject();
        
        final boolean instantiating = (obj == null);
        if (instantiating) {
            obj = instantiateObject(context);
            if (obj == null) {
                return null;
            }
            
            shareTransaction(obj, context.transaction());
            shareObjectReference(obj, context.objectReference());
            
            onInstantiate(context, obj);

            if (!context.activationDepth().requiresActivation()) {
                context.objectReference().setStateDeactivated();
            } else {
                obj = activate(context);
            }
        } else {
            if (activatingActiveObject(context.activationDepth().mode(), context.objectReference())) {
            	ActivationDepth child = context.activationDepth().descend(this);
                if (child.requiresActivation()) {
                    cascadeActivation(new ActivationContext4(context.transaction(), obj, child));
                }
            } else {
                obj = activate(context);
            }
        }
        return obj;
    }

	protected final void onInstantiate(UnmarshallingContext context, Object obj) {
		
		context.setObjectWeak(obj);
		context.transaction().referenceSystem().addExistingReference(context.objectReference());
		objectOnInstantiate(context.transaction(), obj);
		
	}
    
    public Object instantiateTransient(UnmarshallingContext context) {

        // overridden in YapClassPrimitive
        // never called for primitive YapAny

        final Object obj = instantiateObject(context);
        if (obj == null) {
            return null;
        }
        context.container().peeked(context.objectID(), obj);
        
        if(context.activationDepth().requiresActivation()){
            instantiateFields(context);
        }
        return obj;
        
    }

	private boolean activatingActiveObject(final ActivationMode mode, ObjectReference ref) {
		return !mode.isRefresh() && ref.isActive();
	}

   private Object activate(UnmarshallingContext context) {
        final Object obj = context.persistentObject();
        if(! objectCanActivate(context.transaction(), obj)){
            context.objectReference().setStateDeactivated();
            return obj;
        }
        context.objectReference().setStateClean();
        if (context.activationDepth().requiresActivation()/* || cascadeOnActivate()*/) {
           instantiateFields(context);
        }
        objectOnActivate(context.transaction(), obj);
        return obj;
    }
	
    public boolean hasObjectConstructor(){
        return _translator != null && _translator.isObjectConstructor();
    }
	
	private Object instantiateObject(UnmarshallingContext context) {
		Object obj = _constructor.apply(context);
	    context.persistentObject(obj);
        return obj;
	}

	private void objectOnInstantiate(Transaction transaction, Object instance) {
		transaction.container().callbacks().objectOnInstantiate(transaction, instance);
	}

	private Object instantiateFromReflector(ObjectContainerBase stream) {
		if (_classReflector == null) {
		    throw new IllegalStateException();
		}
		
		try {
		    return _classReflector.newInstance();
		} catch (NoSuchMethodError e) {
		    container().logMsg(7, classReflector().getName());
		    return null;
		} catch (Exception e) {
		    // TODO: be more helpful here
		    return null;
		}
	}

	private void shareObjectReference(Object obj, ObjectReference ref) {
		if (obj instanceof Db4oTypeImpl) {
		    ((Db4oTypeImpl)obj).setObjectReference(ref);
		}
	}

	private void shareTransaction(Object obj, Transaction transaction) {
		if (obj instanceof TransactionAware) {
		    ((TransactionAware)obj).setTrans(transaction);
		}
	}

	private void objectOnActivate(Transaction transaction, Object obj) {
		ObjectContainerBase container = transaction.container();
		container.callbacks().objectOnActivate(transaction, obj);
		dispatchEvent(transaction, obj, EventDispatchers.ACTIVATE);
	}

	private boolean objectCanActivate(Transaction transaction, Object obj) {
		ObjectContainerBase container = transaction.container();
		return container.callbacks().objectCanActivate(transaction, obj)
			&& dispatchEvent(transaction, obj, EventDispatchers.CAN_ACTIVATE);
	}

    void instantiateFields(UnmarshallingContext context) {
        TypeHandler4 handler = correctHandlerVersion((HandlerVersionContext)context);
        Handlers4.activate(context, handler);
    }

	public boolean isArray() {
        return classReflector().isCollection(); 
    }
    
	boolean isCollection(Object obj) {
		return reflector().forObject(obj).isCollection();
	}

    public boolean isDirty() {
        if (!stateOK()) {
            return false;
        }
        return super.isDirty();
    }
    
    boolean isEnum() {
    	if (!Deploy.csharp) {
    		return Platform4.isEnum(reflector(), classReflector());
    	}
    	return false;
    }
    
    public boolean isPrimitive(){
        return false;
    }
    
    /**
	 * no any, primitive, array or other tricks. overriden in YapClassAny and
	 * YapClassPrimitive
	 */
    public boolean isStrongTyped() {
        return true;
    }
    
    public boolean isValueType(){
       return Handlers4.holdsValueType(_typeHandler);
    }
    
    private final Object lock(){
        return _container.lock();
    }
    
    public String nameToWrite() {
        if(i_config != null && i_config.writeAs() != null){
            return i_config.writeAs();
        }
        if(i_name == null){
            return "";
        }
        return configImpl().resolveAliasRuntimeName(i_name);
    }
    
    public final boolean callConstructor() {
        TernaryBool specialized = callConstructorSpecialized();
		// FIXME: If specified, return yes?!?
		if(!specialized.isUnspecified()){
		    return specialized.definiteYes();
		}
		return configImpl().callConstructors().definiteYes();
    }

	private Config4Impl configImpl() {
		return _container.configImpl();
	}
    
    private final TernaryBool callConstructorSpecialized(){
        if(i_config!= null){
            TernaryBool res = i_config.callConstructor();
            if(!res.isUnspecified()){
                return res;
            }
        }
        if(isEnum()){
            return TernaryBool.NO;
        }
        if(i_ancestor != null){
            return i_ancestor.callConstructorSpecialized();
        }
        return TernaryBool.UNSPECIFIED;
    }

    public int ownLength() {
        return MarshallerFamily.current()._class.marshalledLength(_container, this);
    }
    
    public int prefetchActivationDepth(){
    	return 1;
    }
    
    void purge() {
        _index.purge();
        
        // TODO: may want to add manual purge to Btree
        //       indexes here
    }

    // FIXME: [TA] ActivationDepth review
	public Object readAndActivate(Transaction trans, int id, ActivationDepth depth) {
		
		// Method for C# value types and for map members:
		// they need to be instantiated before setting them
		// on the parent object. 
		
		// For value types the set call modifies identity.

		// In maps, adding the object to the map calls #hashCode and #equals,
		// so the object needs to be activated.
		
		// TODO: Question: Do we want value types in the ID tree?
		// Shouldn't we treat them like strings and update
		// them every time ???		
		ObjectReference ref = trans.referenceForId(id);
		if (ref != null) {
		    Object obj = ref.getObject();
		    if(obj == null){
		        trans.removeReference(ref);
		    }else{
		        ref.activate(trans, obj, depth);
		        return ref.getObject();
		    }
		}
		return new ObjectReference(id).read(trans, depth, Const4.ADD_TO_ID_TREE, false);
	}
    
    public TypeHandler4 readCandidateHandler(QueryingReadContext context) {
        TypeHandler4 typeHandler = correctHandlerVersion(context);
        if(typeHandler instanceof CascadingTypeHandler){
        	return ((CascadingTypeHandler)typeHandler).readCandidateHandler(context);	
        }
        return null;
    }

    public TypeHandler4 seekCandidateHandler(QueryingReadContext context) {
        if (isArray()) {
            if (Platform4.isCollectionTranslator(this.i_config)) {
                context.seek(context.offset() + Const4.INT_LENGTH);
                return new ArrayHandler(null, false);
            }
            incrementFieldsOffset1((ByteArrayBuffer)context.buffer());
            if (i_ancestor != null) {
                return i_ancestor.seekCandidateHandler(context);
            }
        }
        return null;
    }

	public final int readAspectCount(ReadBuffer buffer) {
        int count = buffer.readInt();
        if (count > _aspects.length) {
            if (Debug4.atHome) {
                System.out.println(
                    "ClassMetadata.readFieldCount "
                        + getName()
                        + " count to high:"
                        + count
                        + " i_fields:"
                        + _aspects.length);
                new Exception().printStackTrace();
            }
            return _aspects.length;
        }		
        return count;
    }
	
    byte[] readName(Transaction a_trans) {
        i_reader = a_trans.container().readReaderByID(a_trans, getID());
        return readName1(a_trans, i_reader);
    }

    public final byte[] readName1(Transaction trans, ByteArrayBuffer reader) {
		if (reader == null)
			return null;

		i_reader = reader;
		boolean ok = false;
		try {
			ClassMarshaller marshaller = MarshallerFamily.current()._class;
			i_nameBytes = marshaller.readName(trans, reader);
			marshaller.readMetaClassID(reader);  // never used ???

			setStateUnread();

			bitFalse(Const4.CHECKED_CHANGES);
			bitFalse(Const4.STATIC_FIELDS_STORED);

			ok = true;
			return i_nameBytes;

		} finally {
			if (!ok) {
				setStateDead();
			}
		}
	}
    
	public void readVirtualAttributes(Transaction trans, ObjectReference ref, boolean lastCommitted) {
        int id = ref.getID();
        ObjectContainerBase stream = trans.container();
        ByteArrayBuffer buffer = stream.readReaderByID(trans, id, lastCommitted);
        ObjectHeader oh = new ObjectHeader(stream, this, buffer);
        ObjectReferenceContext context = new ObjectReferenceContext(trans,buffer, oh, ref);
        Handlers4.fieldAwareTypeHandler(correctHandlerVersion(context)).readVirtualAttributes(context);
	}

	public GenericReflector reflector() {
		return _container.reflector();
	}
    
    public void rename(String newName){
        if (_container.isClient()) {
        	Exceptions4.throwRuntimeException(58);
        }
        
        int tempState = _state;
        setStateOK();
        setName(newName);
        i_nameBytes = asBytes(i_name);
        setStateDirty();
        write(_container.systemTransaction());
        ReflectClass oldReflector = _classReflector;
        classReflector(container().reflector().forName(newName));
        container().classCollection().refreshClassCache(this, oldReflector);
        refresh();
        _state = tempState;
    }

    //TODO: duplicates ClassMetadataRepository#asBytes
	private byte[] asBytes(String str) {
		return container().stringIO().write(str);
	}

    final void createConfigAndConstructor(
        Hashtable4 a_byteHashTable,
        ReflectClass claxx) {
    	setName(resolveName(claxx));
        setConfig(configImpl().configClass(getName()));
        if (claxx == null) {
            resolveClassReflector(getName());
        } else {
        	classReflector(claxx);
//            createConstructor(true);
        }
        if (i_nameBytes != null) {
            a_byteHashTable.remove(i_nameBytes);
            i_nameBytes = null;
        }
    }

    String resolveName(ReflectClass claxx) {
        if (claxx != null) {
            return claxx.getName();
        }
        if (i_nameBytes != null) {
        	String name = _container.stringIO().read(i_nameBytes);
        	return configImpl().resolveAliasStoredName(name);
        }
        throw new IllegalStateException();
    }

    boolean readThis() {
    	boolean stateUnread = stateUnread();
        if (stateUnread) {
            setStateOK();
            setStateClean();
        }
        if (stateUnread || stateDead()) {
            forceRead();
            return true;
        }
        return false;
    }
    
    final void forceRead(){
        if(i_reader == null || bitIsTrue(Const4.READING)){
            return;
        }
        
        bitTrue(Const4.READING);
        try {
        	MarshallerFamily.forConverterVersion(_container.converterVersion())._class.read(_container, this, i_reader);
       
	        i_nameBytes = null;
	        i_reader = null;
        } finally {
        	bitFalse(Const4.READING);
        }
    }	

    public void readThis(Transaction a_trans, ByteArrayBuffer a_reader) {
        throw Exceptions4.virtualException();
    }

    public void refresh() {
        if (!stateUnread()) {
            resolveClassReflector(i_name);
            bitFalse(Const4.CHECKED_CHANGES);
            checkChanges();
            forEachDeclaredField(new Procedure4() {
                public void apply(Object arg) {
                    ((FieldMetadata)arg).refresh();
                }
            });
        }
    }

    void removeFromIndex(Transaction ta, int id) {
        if (hasClassIndex()) {
            _index.remove(ta, id);
        }
        if (i_ancestor != null) {
            i_ancestor.removeFromIndex(ta, id);
        }
    }

    boolean renameField(final String oldName, final String newName) {
        final BooleanByRef renamed = new BooleanByRef(false);
        for (int i = 0; i < _aspects.length; i++) {
            if (_aspects[i].getName().equals(newName)) {
                _container.logMsg(9, "class:" + getName() + " field:" + newName);
                return false;
            }
        }
        forEachDeclaredField(new Procedure4() {
            public void apply(Object arg) {
                FieldMetadata field = (FieldMetadata) arg;
                if (field.getName().equals(oldName)) {
                    field.setName(newName);
                    renamed.value = true;
                }
            }
        });
        return renamed.value;
    }
    
    void setConfig(Config4Class config){
        
        if(config == null){
            return;
        }
            
        // The configuration can be set by a ObjectClass#readAs setting
        // from YapClassCollection, right after reading the meta information
        // for the first time. In that case we never change the setting
        if(i_config == null){
            i_config = config;
        }
    }

    void setName(String a_name) {
        i_name = a_name;
    }

    final void setStateDead() {
        bitTrue(Const4.DEAD);
        bitFalse(Const4.CONTINUE);
    }

    private final void setStateUnread() {
        bitFalse(Const4.DEAD);
        bitTrue(Const4.CONTINUE);
    }

    final void setStateOK() {
        bitFalse(Const4.DEAD);
        bitFalse(Const4.CONTINUE);
    }
    
    boolean stateDead(){
        return bitIsTrue(Const4.DEAD);
    }

    private final boolean stateOK() {
        return bitIsFalse(Const4.CONTINUE)
            && bitIsFalse(Const4.DEAD)
            && bitIsFalse(Const4.READING);
    }
    
    final boolean stateOKAndAncestors(){
        if(! stateOK()  || _aspects == null){
            return false;
        }
        if(i_ancestor != null){
            return i_ancestor.stateOKAndAncestors();
        }
        return true;
    }

    boolean stateUnread() {
        return bitIsTrue(Const4.CONTINUE)
            && bitIsFalse(Const4.DEAD)
            && bitIsFalse(Const4.READING);
    }

    boolean storeField(ReflectField field) {
		if (field.isStatic()) {
            return false;
        }
        if (isTransient(field)) {
        	if (!shouldStoreTransientFields()) {
            	return false;
            }
        }
        return Platform4.canSetAccessible() || field.isPublic();
    }

	private boolean shouldStoreTransientFields() {
	    Config4Class config = configOrAncestorConfig();
	    if (config == null) {
	        return false;
	    }
	    return config.storeTransientFields();
    }

	private boolean isTransient(ReflectField field) {
	    return field.isTransient() || Platform4.isTransient(field.getFieldType());
    }
    
    public StoredField storedField(final String fieldName, final Object fieldType) {
        synchronized(lock()){
        	
            final ClassMetadata fieldTypeFilter = fieldType == null 
            	? null
            	: _container.classMetadataForReflectClass(ReflectorUtils.reflectClassFor(reflector(), fieldType));
            
            final ByRef foundField = new ByRef();
            forEachField(new Procedure4() {
                public void apply(Object arg) {
                    if(foundField.value != null){
                        return;
                    }
                    FieldMetadata field = (FieldMetadata)arg;
                    if(field.getName().equals(fieldName)){
                        if(fieldTypeFilter == null || fieldTypeFilter == field.fieldType()){
                            foundField.value = field;
                        }
                    }
                }
            });
    		
    		// TODO: implement field creation
	        return (StoredField) foundField.value;
        }
    }

    void storeStaticFieldValues(Transaction trans, boolean force) {
        if (bitIsTrue(Const4.STATIC_FIELDS_STORED) && !force) {
        	return;
        }
        bitTrue(Const4.STATIC_FIELDS_STORED);
        
        if (!shouldStoreStaticFields(trans)) {
        	return;
        }
        
        final ObjectContainerBase stream = trans.container();
        stream.showInternalClasses(true);
        try {
            StaticClass sc = queryStaticClass(trans);
            if (sc == null) {
            	createStaticClass(trans);
            } else {
            	updateStaticClass(trans, sc);
            }
        } finally {
            stream.showInternalClasses(false);
        }
    }

	private boolean shouldStoreStaticFields(Transaction trans) {
		return !isReadOnlyContainer() 
					&&  (staticFieldValuesArePersisted()
        			|| Platform4.storeStaticFieldValues(trans.reflector(), classReflector()));
	}

	private boolean isReadOnlyContainer() {
		return container().config().isReadOnly();
	}

	private void updateStaticClass(final Transaction trans, final StaticClass sc) {
		final ObjectContainerBase stream = trans.container();
		stream.activate(trans, sc, new FixedActivationDepth(4));
		
		final StaticField[] existingFields = sc.fields;
		final Iterator4 staticFields = Iterators.map(
				staticReflectFields(),
				new Function4() {
					public Object apply(Object arg) {
						final ReflectField reflectField = (ReflectField)arg;
					    StaticField existingField = fieldByName(existingFields, reflectField.getName());
					    if (existingField != null) {
					    	updateExistingStaticField(trans, existingField, reflectField);
					        return existingField;
					    }
					    return toStaticField(reflectField);
					}
				});
		sc.fields = toStaticFieldArray(staticFields);
		if (!stream.isClient()) {
			setStaticClass(trans, sc);
		}
	}

	private void createStaticClass(Transaction trans) {
		if (trans.container().isClient()) {
			return;
		}
		StaticClass sc = new StaticClass(getName(), toStaticFieldArray(staticReflectFieldsToStaticFields()));
		setStaticClass(trans, sc);
	}

	private Iterator4 staticReflectFieldsToStaticFields() {
		return Iterators.map(
			staticReflectFields(),
			new Function4() {
				public Object apply(Object arg) {
					return toStaticField((ReflectField) arg);
				}
			});
	}

	protected StaticField toStaticField(final ReflectField reflectField) {
		return new StaticField(reflectField.getName(), staticReflectFieldValue(reflectField));
	}

	private Object staticReflectFieldValue(final ReflectField reflectField) {
		return _fieldAccessor.get(reflectField, null);
	}

	private void setStaticClass(Transaction trans, StaticClass sc) {
		// TODO: we should probably use a specific update depth here, 4?
		trans.container().storeInternal(trans, sc, true);
	}

	private StaticField[] toStaticFieldArray(Iterator4 iterator4) {
		return toStaticFieldArray(new Collection4(iterator4));
	}

	private StaticField[] toStaticFieldArray(Collection4 fields) {
		return (StaticField[]) fields.toArray(new StaticField[fields.size()]);
	}

	private Iterator4 staticReflectFields() {
		return Iterators.filter(reflectFields(), new Predicate4() {
			public boolean match(Object candidate) {
				return ((ReflectField)candidate).isStatic();
			}
		});
	}

	private ReflectField[] reflectFields() {
		return classReflector().getDeclaredFields();
	}

	protected void updateExistingStaticField(Transaction trans, StaticField existingField, final ReflectField reflectField) {
		final ObjectContainerBase stream = trans.container();
		final Object newValue = staticReflectFieldValue(reflectField);
		
		if (existingField.value != null
	        && newValue != null
	        && existingField.value.getClass() == newValue.getClass()) {
	        int id = stream.getID(trans, existingField.value);
	        if (id > 0) {
	            if (existingField.value != newValue) {
	                
	                // This is the clue:
	                // Bind the current static member to it's old database identity,
	                // so constants and enums will work with '=='
	                stream.bind(trans, newValue, id);
	                
	                // This may produce unwanted side effects if the static field object
	                // was modified in the current session. TODO:Add documentation case.
	                
	                stream.refresh(trans, newValue, Integer.MAX_VALUE);
	                
	                existingField.value = newValue;
	            }
	            return;
	        }
	    }
		
		if(newValue == null){
            try{
            	_fieldAccessor.set(reflectField, null,  existingField.value);
            }catch(Exception ex){
                // fail silently
            	// TODO: why?
            }
	        return;   
	   }
		
		existingField.value = newValue;
	}

	private boolean staticFieldValuesArePersisted() {
		return (i_config != null && i_config.staticFieldValuesArePersisted());
	}

	protected StaticField fieldByName(StaticField[] fields, final String fieldName) {
		for (int i = 0; i < fields.length; i++) {
		    final StaticField field = fields[i];
			if (fieldName.equals(field.name)) {
				return field;
			}
		}
		return null;
	}

	private StaticClass queryStaticClass(Transaction trans) {
		Query q = trans.container().query(trans);
		q.constrain(Const4.CLASS_STATICCLASS);
		q.descend("name").constrain(getName());
		ObjectSet os = q.execute();
		return os.size() > 0
			? (StaticClass)os.next()
			: null;
	}

    public String toString() {
    	if(i_name!=null) {
    		return i_name;
    	}
        if(i_nameBytes==null){
            return "*CLASS NAME UNKNOWN*";
        }
	    LatinStringIO stringIO = 
	    	_container == null ? 
	    			Const4.stringIO 
	    			: _container.stringIO();
	    return stringIO.read(i_nameBytes);
    }
    
    public boolean writeObjectBegin() {
        if (!stateOK()) {
            return false;
        }
        return super.writeObjectBegin();
    }
    
    public final void writeThis(Transaction trans, ByteArrayBuffer writer) {
        MarshallerFamily.current()._class.write(trans, this, writer);
    }

	public PreparedComparison prepareComparison(Context context, Object source) {
		return Handlers4.prepareComparisonFor(_typeHandler, context, source);
	}
	
    public static void defragObject(DefragmentContextImpl context) {
    	ObjectHeader header = ObjectHeader.defrag(context);
    	DefragmentContextImpl childContext = new DefragmentContextImpl(context, header);
    	header.classMetadata().defragment(childContext);
        if (Deploy.debug) {
            context.readEnd();
        }
    }	

	public void defragment(DefragmentContext context) {
	    correctHandlerVersion(context).defragment(context);
	}
	
	public void defragClass(DefragmentContextImpl context, int classIndexID) {
		MarshallerFamily mf = MarshallerFamily.forConverterVersion(container().converterVersion());
		mf._class.defrag(this,_container.stringIO(), context, classIndexID);
	}

    public static ClassMetadata readClass(ObjectContainerBase stream, ByteArrayBuffer reader) {
        ObjectHeader oh = new ObjectHeader(stream, reader);
        return oh.classMetadata();
    }

	public boolean isAssignableFrom(ClassMetadata other) {
		return classReflector().isAssignableFrom(other.classReflector());
	}
	
	public void setAncestor(ClassMetadata ancestor){
		if(ancestor == this){
			throw new IllegalStateException();
		}
		i_ancestor = ancestor;
	}

    public Object wrapWithTransactionContext(Transaction transaction, Object value) {
        if(value instanceof Integer){
            return value;
        }
        return new TransactionContext(transaction, value);
    }

    public TypeHandler4 typeHandler(){
        return _typeHandler;
    }
    
    public TypeHandler4 delegateTypeHandler(Context context){
    	if(context instanceof HandlerVersionContext){
    		return correctHandlerVersion((HandlerVersionContext)context);
    	}
        return _typeHandler;
    }

    protected TypeHandler4 correctHandlerVersion(HandlerVersionContext context){
        TypeHandler4 typeHandler = HandlerRegistry.correctHandlerVersion(context, _typeHandler);
        if(typeHandler != _typeHandler){
            if(typeHandler instanceof StandardReferenceTypeHandler){
                ((StandardReferenceTypeHandler) typeHandler).classMetadata(this);
            }
        }
    	return typeHandler;
    }

    public void forEachField(Procedure4 procedure) {
        forEachAspect(new SubTypePredicate(FieldMetadata.class), procedure);
    }
    
    public void forEachDeclaredField(Procedure4 procedure) {
        forEachDeclaredAspect(new SubTypePredicate(FieldMetadata.class), procedure);
    }
    
    public void forEachAspect(Predicate4 predicate, Procedure4 procedure){
        ClassMetadata classMetadata = this;
        while(classMetadata != null){
            classMetadata.forEachDeclaredAspect(predicate, procedure);
            classMetadata = classMetadata.i_ancestor;
        }
    }
    
    public void forEachAspect(Procedure4 procedure){
        ClassMetadata classMetadata = this;
        while(classMetadata != null){
            classMetadata.forEachDeclaredAspect(procedure);
            classMetadata = classMetadata.i_ancestor;
        }
    }
    
    public void forEachDeclaredAspect(Predicate4 predicate, Procedure4 procedure){
        if(_aspects == null){
            return;
        }
        for (int i = 0; i < _aspects.length; i++) {
            if(predicate.match(_aspects[i])){
                procedure.apply(_aspects[i]);
            }
        }
    }
    
    public void forEachDeclaredAspect(Procedure4 procedure){
        if(_aspects == null){
            return;
        }
        for (int i = 0; i < _aspects.length; i++) {
            procedure.apply(_aspects[i]);
        }
    }
    
    public boolean aspectsAreNull(){
    	return _aspects == null;
    }
    
    private static final class AlwaysModified implements ModificationAware{

		static final AlwaysModified INSTANCE = new AlwaysModified();

		public boolean isModified(Object obj) {
			return true;
		}
    	
    }

	public boolean isModified(Object obj) {
		return _modificationChecker.isModified(obj);
	}

	public int instanceCount() {
		return instanceCount(_container.transaction());
	}

	public int instanceCount(Transaction trans) {
		return _container.instanceCount(this, trans);
	}

	public boolean canHold(ReflectClass type) {
		 ReflectClass classReflector = classReflector();
       if(classReflector.isCollection()){
           return true;
       }
       return classReflector.isAssignableFrom(type);
    }

	public boolean isStorable() {
		return !stateDead() || isTransient();
    }

	private Object instantiateWithCustomTypeHandlerIfEnabled(final UnmarshallingContext context) {
		if (!_customTypeHandlerAspect.isEnabledOn(context)) {
			return instantiateForVersionWithoutCustomTypeHandler(context);
    	}
		return instantiateWithCustomTypeHandler(context);
	}

	private Object instantiateForVersionWithoutCustomTypeHandler(final UnmarshallingContext context) {
		final Function4<UnmarshallingContext, Object> oldVersionConstructor = createConstructor(null);
		if (null == oldVersionConstructor) {
			throw new IllegalStateException();
		}
		return oldVersionConstructor.apply(context);
	}

	private Object instantiateWithCustomTypeHandler(final UnmarshallingContext context) {
		final ContextState contextState = context.saveState();
        try {
        	final boolean fieldHasValue = seekToField(context, _customTypeHandlerAspect);
        	if (!fieldHasValue) {
        		context.restoreState(contextState);
        		return instantiateForVersionWithoutCustomTypeHandler(context);
        	}
        	final InstantiatingTypeHandler customTypeHandler = (InstantiatingTypeHandler)_customTypeHandlerAspect._typeHandler;
        	return context.slotFormat().doWithSlotIndirection(context, new Closure4<Object>() { public Object run() {
        		return customTypeHandler.instantiate(context);
        	}});
        } finally {
            context.restoreState(contextState);
        }
	}
}
