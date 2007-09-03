/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import java.io.IOException;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.config.ObjectMarshaller;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.classindex.*;
import com.db4o.internal.diagnostic.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.query.processor.*;
import com.db4o.internal.slots.*;
import com.db4o.marshall.*;
import com.db4o.query.*;
import com.db4o.reflect.*;
import com.db4o.reflect.generic.*;


/**
 * @exclude
 */
public class ClassMetadata extends PersistentBase implements IndexableTypeHandler, FirstClassHandler, StoredClass {
    
    public ClassMetadata i_ancestor;

    private Config4Class i_config;
    public int _metaClassID;
    
    public FieldMetadata[] i_fields;
    
    private final ClassIndexStrategy _index;
    
    protected String i_name;

    private final ObjectContainerBase _container;

    byte[] i_nameBytes;
    private Buffer i_reader;

    private boolean _classIndexed;
    
    private ReflectClass _reflector;
    
    private boolean _isEnum;
    
    private EventDispatcher _eventDispatcher;
    
    private boolean _internal;
    
    private boolean _unversioned;
    
	// for indexing purposes.
    // TODO: check race conditions, upon multiple calls against the same class
    private int i_lastID;
    
    private TernaryBool _canUpdateFast=TernaryBool.UNSPECIFIED;
    
    public final ObjectContainerBase stream() {
    	return _container;
    }
    
    public final boolean canUpdateFast(){
    	return _canUpdateFast.booleanValue(checkCanUpdateFast());
    }
    
    private final boolean checkCanUpdateFast() {
    	if(i_ancestor != null && ! i_ancestor.canUpdateFast()){
    		return false;
    	}
		if(i_config != null && i_config.cascadeOnDelete() == TernaryBool.YES) {
			return false;
		}
		for(int i = 0; i < i_fields.length; ++i) {
			if(i_fields[i].hasIndex()) {
				return false;
			}
		}
		return true;
	}

	boolean isInternal() {
    	return _internal;
    }

    private ClassIndexStrategy createIndexStrategy() {
		return new BTreeClassIndexStrategy(this);
	}

    ClassMetadata(ObjectContainerBase container, ReflectClass reflector){
    	_container = container;
        _reflector = reflector;
        _index = createIndexStrategy();
        _classIndexed = true;
    }
    
    void activateFields(Transaction trans, Object obj, int depth) {
        if(objectCanActivate(trans, obj)){
            activateFieldsLoop(trans, obj, depth);
        }
    }

    private final void activateFieldsLoop(Transaction trans, Object obj, int depth) {
        for (int i = 0; i < i_fields.length; i++) {
            i_fields[i].cascadeActivation(trans, obj, depth, true);
        }
        if (i_ancestor != null) {
            i_ancestor.activateFieldsLoop(trans, obj, depth);
        }
    }

    public final void addFieldIndices(StatefulBuffer a_writer, Slot oldSlot) {
        if(hasClassIndex() || hasVirtualAttributes()){
            ObjectHeader oh = new ObjectHeader(_container, this, a_writer);
            oh._marshallerFamily._object.addFieldIndices(this, oh._headerAttributes, a_writer, oldSlot);
        }
    }
    
    void addMembers(ObjectContainerBase ocb) {
        bitTrue(Const4.CHECKED_CHANGES);
        if (installTranslator(ocb) || installMarshaller(ocb)) {
        	return;
        }

        if (ocb.detectSchemaChanges()) {
            boolean dirty = isDirty();

            Collection4 members = new Collection4();

            if (null != i_fields) {
            	members.addAll(i_fields);
            	if(i_fields.length==1&&i_fields[0] instanceof TranslatedFieldMetadata) {
            		setStateOK();
            		return;
            	}
            }
            if(generateVersionNumbers()) {
                if(! hasVersionField()) {
                    members.add(ocb.getVersionIndex());
                    dirty = true;
                }
            }
            if(generateUUIDs()) {
                if(! hasUUIDField()) {
                    members.add(ocb.getUUIDIndex());
                    dirty = true;
                }
            }
            dirty = collectReflectFields(ocb, members) | dirty;
            if (dirty) {
                _container.setDirtyInSystemTransaction(this);
                i_fields = new FieldMetadata[members.size()];
                members.toArray(i_fields);
                for (int i = 0; i < i_fields.length; i++) {
                    i_fields[i].setArrayPosition(i);
                }
            } else {
                if (members.size() == 0) {
                    i_fields = new FieldMetadata[0];
                }
            }
            
            DiagnosticProcessor dp = _container._handlers._diagnosticProcessor;
            if(dp.enabled()){
                dp.checkClassHasFields(this);
            }
            
        } else {
            if (i_fields == null) {
                i_fields = new FieldMetadata[0];
            }
        }
        _container.callbacks().classOnRegistered(this);
        setStateOK();
    }

	private boolean collectReflectFields(ObjectContainerBase stream, Collection4 collectedFields) {
		boolean dirty=false;
		ReflectField[] fields = reflectFields();
		for (int i = 0; i < fields.length; i++) {
		    if (storeField(fields[i])) {
		        TypeHandler4 wrapper = stream._handlers.handlerForClass(stream, fields[i].getFieldType());
		        if (wrapper == null) {
		            continue;
		        }
		        FieldMetadata field = new FieldMetadata(this, fields[i], wrapper);

		        boolean found = false;
		        Iterator4 m = collectedFields.iterator();
		        while (m.moveNext()) {
		            if (((FieldMetadata)m.current()).equals(field)) {
		                found = true;
		                break;
		            }
		        }
		        if (found) {
		            continue;
		        }

		        // this has no effect on YapClients
		        dirty = true;
		        // we need a local dirty flag to tell us to reconstruct
		        // i_fields

		        collectedFields.add(field);
		    }
		}
		return dirty;
	}
	
	private boolean installMarshaller(ObjectContainerBase ocb) {
		ObjectMarshaller om = getMarshaller();
    	if (om == null) {
    		return false;
    	}
    	installCustomFieldMetadata(ocb, new CustomMarshallerFieldMetadata(this, om));
    	return true;
	}
	

    private boolean installTranslator(ObjectContainerBase ocb) {
    	ObjectTranslator ot = getTranslator();
    	if (ot == null) {
    		return false;
    	}
    	if (isNewTranslator(ot)) {
    		_container.setDirtyInSystemTransaction(this);
    	}
        installCustomFieldMetadata(ocb, new TranslatedFieldMetadata(this, ot));
    	return true;
    }

	private void installCustomFieldMetadata(ObjectContainerBase ocb, FieldMetadata customFieldMetadata) {
		int fieldCount = 1;
        
        boolean versions = generateVersionNumbers() && ! ancestorHasVersionField();
        boolean uuids = generateUUIDs()  && ! ancestorHasUUIDField();
        
        if(versions){
            fieldCount = 2;
        }
        
        if(uuids){
            fieldCount = 3;
        }
    	
    	i_fields = new FieldMetadata[fieldCount];
    	
        i_fields[0] = customFieldMetadata;
        
        
        // Some explanation on the thoughts here:
        
        // Since i_fields for the translator are generated every time,
        // we want to make sure that the order of fields is consistent.
        
        // Therefore it's easier to implement with fixed index places in
        // the i_fields array:
        
        // [0] is the translator
        // [1] is the version
        // [2] is the UUID
        
        if(versions || uuids) {
            
            // We don't want to have a null field, so let's add the version
            // number, if we have a UUID, even if it's not needed.
            
            i_fields[1] = ocb.getVersionIndex();
        }
        
        if(uuids){
            i_fields[2] = ocb.getUUIDIndex();
        }
        
    	setStateOK();
	}
    
    private ObjectTranslator getTranslator() {
    	return i_config == null
    		? null
    		: i_config.getTranslator();
    }
    
    private ObjectMarshaller getMarshaller() {
    	return i_config == null
		? null
		: i_config.getMarshaller();
    }

	private boolean isNewTranslator(ObjectTranslator ot) {
		return !hasFields()
		    || !ot.getClass().getName().equals(i_fields[0].getName());
	}

	private boolean hasFields() {
		return i_fields != null
		    && i_fields.length > 0;
	}

    void addToIndex(LocalObjectContainer a_stream, Transaction a_trans, int a_id) {
        if (a_stream.maintainsIndices()) {
            addToIndex1(a_stream, a_trans, a_id);
        }
    }

    void addToIndex1(LocalObjectContainer a_stream, Transaction a_trans, int a_id) {
        if (i_ancestor != null) {
            i_ancestor.addToIndex1(a_stream, a_trans, a_id);
        }
        if (hasClassIndex()) {
            _index.add(a_trans, a_id);
        }
    }

    boolean allowsQueries() {
        return hasClassIndex();
    }

    public void cascadeActivation(
        Transaction a_trans,
        Object a_object,
        int a_depth,
        boolean a_activate) {
        Config4Class config = configOrAncestorConfig();
        if (config != null) {
            if (a_activate) {
                a_depth = config.adjustActivationDepth(a_depth);
            }
        }
        if (a_depth > 0) {
            ObjectContainerBase stream = a_trans.container();
            if (a_activate) {
                if(isValueType()){
                    activateFields(a_trans, a_object, a_depth - 1);
                }else{
                    stream.stillToActivate(a_trans, a_object, a_depth - 1);
                }
            } else {
                stream.stillToDeactivate(a_trans, a_object, a_depth - 1, false);
            }
        }
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
                if (_reflector != null) {
                    addMembers(_container);
                    if (!_container.isClient()) {
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
        if (_container._handlers.ICLASS_DB4OTYPEIMPL.isAssignableFrom(claxx)) {
        	Db4oTypeImpl db4oTypeImpl = (Db4oTypeImpl) claxx.newInstance();
        	_classIndexed = (db4oTypeImpl == null || db4oTypeImpl.hasClassIndex());
		} else if(i_config != null){
			_classIndexed = i_config.indexed();
		}
    }
    
    public final int adjustUpdateDepth(Transaction trans, int depth) {
        Config4Class config = configOrAncestorConfig();
        if (depth == Const4.UNSPECIFIED) {
            depth = checkUpdateDepthUnspecified(trans.container().configImpl());
            if (classReflector().isCollection()) {
                depth = adjustDepthToBorders(depth);
            }
        }
        if ((config != null && (config.cascadeOnDelete() == TernaryBool.YES || config.cascadeOnUpdate() == TernaryBool.YES))) {
            depth = adjustDepthToBorders(depth);
        }
        return depth - 1;
    }

	private int adjustDepthToBorders(int depth) {
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
        Transaction a_trans,
        QConObject a_parent,
        Object a_object,
        Visitor4 a_visitor) {
        if (i_fields != null) {
            for (int i = 0; i < i_fields.length; i++) {
                i_fields[i].collectConstraints(a_trans, a_parent, a_object, a_visitor);
            }
        }
        if (i_ancestor != null) {
            i_ancestor.collectConstraints(a_trans, a_parent, a_object, a_visitor);
        }
    }
    
    public final TreeInt collectFieldIDs(MarshallerFamily mf, ObjectHeaderAttributes attributes, TreeInt tree, StatefulBuffer a_bytes, String name) {
        return mf._object.collectFieldIDs(tree, this, attributes, a_bytes, name);
    }

    public boolean customizedNewInstance(){
        return configInstantiates();
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

    private boolean createConstructor(ObjectContainerBase container, String className) {
        ReflectClass claxx = container.reflector().forName(className);
        return createConstructor(container, claxx , className, true);
    }

    public boolean createConstructor(ObjectContainerBase a_stream, ReflectClass a_class, String a_name, boolean errMessages) {
        
        _reflector = a_class;
        
        _eventDispatcher = EventDispatcher.forClass(a_stream, a_class);
        
        if(! Deploy.csharp){
            if(a_class != null){
                _isEnum = Platform4.jdk().isEnum(reflector(), a_class);
            }
        }
        
        if(customizedNewInstance()){
            return true;
        }
        
        if(a_class != null){
            if(a_stream._handlers.ICLASS_TRANSIENTCLASS.isAssignableFrom(a_class)
            	|| Platform4.isTransient(a_class)) {
                a_class = null;
            }
        }
        if (a_class == null) {
            if(a_name == null || !Platform4.isDb4oClass(a_name)){
                if(errMessages){
                    a_stream.logMsg(23, a_name);
                }
            }
            setStateDead();
            return false;
        }
        
        if(a_stream._handlers.createConstructor(a_class, ! callConstructor())){
            return true;
        }
        
        setStateDead();
        if(errMessages){
            a_stream.logMsg(7, a_name);
        }
        
        if (a_stream.configImpl().exceptionsOnNotStorable()) {
            throw new ObjectNotStorableException(a_class);
        }

        return false;
        
    }

	public void deactivate(Transaction trans, Object obj, int depth) {
        if(objectCanDeactivate(trans, obj)){
            deactivate1(trans, obj, depth);
            objectOnDeactivate(trans, obj);
        }
    }

	private void objectOnDeactivate(Transaction transaction, Object obj) {
		ObjectContainerBase container = transaction.container();
		container.callbacks().objectOnDeactivate(transaction, obj);
		dispatchEvent(container, obj, EventDispatcher.DEACTIVATE);
	}

	private boolean objectCanDeactivate(Transaction transaction, Object obj) {
		ObjectContainerBase container = transaction.container();
		return container.callbacks().objectCanDeactivate(transaction, obj)
			&& dispatchEvent(container, obj, EventDispatcher.CAN_DEACTIVATE);
	}

    void deactivate1(Transaction a_trans, Object a_object, int a_depth) {
        
        for (int i = 0; i < i_fields.length; i++) {
            i_fields[i].deactivate(a_trans, a_object, a_depth);
        }
        if (i_ancestor != null) {
            i_ancestor.deactivate1(a_trans, a_object, a_depth);
        }
    }

    final void delete(StatefulBuffer a_bytes, Object a_object) {
        ObjectHeader oh = new ObjectHeader(_container, this, a_bytes);
        delete1(oh._marshallerFamily, oh._headerAttributes, a_bytes, a_object);
    }

    private final void delete1(MarshallerFamily mf, ObjectHeaderAttributes attributes, StatefulBuffer a_bytes, Object a_object) {
        removeFromIndex(a_bytes.getTransaction(), a_bytes.getID());
        deleteMembers(mf, attributes, a_bytes, a_bytes.getTransaction().container()._handlers.arrayType(a_object), false);
    }

    public void deleteEmbedded(MarshallerFamily mf, StatefulBuffer a_bytes) throws Db4oIOException {
        if (a_bytes.cascadeDeletes() > 0) {
            int id = a_bytes.readInt();
            if (id > 0) {
                deleteEmbedded1(mf, a_bytes, id);
            }
        } else {
            a_bytes.incrementOffset(linkLength());
        }
    }
    
    /** @param mf */
    public void deleteEmbedded1(MarshallerFamily mf, StatefulBuffer a_bytes, int a_id) throws Db4oIOException {
        if (a_bytes.cascadeDeletes() > 0) {
        	
        	ObjectContainerBase stream = a_bytes.getStream();
            
            // short-term reference to prevent WeakReference-gc to hit
            Transaction transaction = a_bytes.getTransaction();
            Object obj = stream.getByID2(transaction, a_id);

            int cascade = a_bytes.cascadeDeletes() - 1;
            if (obj != null) {
                if (isCollection(obj)) {
                    cascade += reflector().collectionUpdateDepth(reflector().forObject(obj)) - 1;
                }
            }

            ObjectReference yo = transaction.referenceForId(a_id);
            if (yo != null) {
                a_bytes.getStream().delete2(transaction, yo, obj,cascade, false);
            }
        }
    }

    void deleteMembers(MarshallerFamily mf, ObjectHeaderAttributes attributes, StatefulBuffer a_bytes, int a_type, boolean isUpdate) {
        try{
	        Config4Class config = configOrAncestorConfig();
	        if (config != null && (config.cascadeOnDelete() == TernaryBool.YES)) {
	            int preserveCascade = a_bytes.cascadeDeletes();
	            if (classReflector().isCollection()) {
	                int newCascade =
	                    preserveCascade + reflector().collectionUpdateDepth(classReflector()) - 3;
	                if (newCascade < 1) {
	                    newCascade = 1;
	                }
	                a_bytes.setCascadeDeletes(newCascade);
	            } else {
	                a_bytes.setCascadeDeletes(1);
	            }
                mf._object.deleteMembers(this, attributes, a_bytes, a_type, isUpdate);
	            a_bytes.setCascadeDeletes(preserveCascade);
	        } else {
                mf._object.deleteMembers(this, attributes, a_bytes, a_type, isUpdate);
	        }
        }catch(Exception e){
            
            // This a catch for changed class hierarchies.
            // It's quite ugly to catch all here but it does
            // help to heal migration from earlier db4o
            // versions.
            
            if(Debug.atHome){
                e.printStackTrace();
            }
        }
    }

    public final boolean dispatchEvent(ObjectContainerBase stream, Object obj, int message) {
    	if(!dispatchingEvents(stream)){
    		return true;
    	}
        return _eventDispatcher.dispatch(stream, obj, message);
    }

	private boolean dispatchingEvents(ObjectContainerBase stream) {
		return _eventDispatcher != null && stream.dispatchsEvents();
	}
    
    public boolean hasEventRegistered(ObjectContainerBase stream, int eventID) {
    	if(!dispatchingEvents(stream)){
    		return true;
    	}
    	return _eventDispatcher.hasEventRegistered(eventID);
    }
    
    public final int fieldCount(){
        int count = i_fields.length;
        
        if(i_ancestor != null){
            count += i_ancestor.fieldCount();
        }
        
        return count;
    }
    
    private static class FieldMetadataIterator implements Iterator4 {
    	private final ClassMetadata _initialClazz;
    	private ClassMetadata _curClazz;
    	private int _curIdx;
    	
    	public FieldMetadataIterator(ClassMetadata clazz) {
    		_initialClazz=clazz;
    		reset();
    	}
    	
		public Object current() {
			return _curClazz.i_fields[_curIdx];
		}

		public boolean moveNext() {
			if(_curClazz==null) {
				_curClazz=_initialClazz;
				_curIdx=0;
			} else {
				_curIdx++;
			}
			while(_curClazz!=null&&!indexInRange()) {
				_curClazz=_curClazz.i_ancestor;
				_curIdx=0;
			}
			return _curClazz!=null&&indexInRange();
		}

		public void reset() {
    		_curClazz=null;
    		_curIdx=-1;
		}

		private boolean indexInRange() {
			return _curIdx<_curClazz.i_fields.length;
		}
	}
    
	public Iterator4 fields() {
		return new FieldMetadataIterator(this);
	}

    // Scrolls offset in passed reader to the offset the passed field should
    // be read at.
    //
	// returns null if not successful or if the field value at this offset is null
    // returns MarshallerFamily from the object header if it is successful and
    //         if the value at this offset is not null
    public final MarshallerFamily findOffset(Buffer a_bytes, FieldMetadata a_field) {
        if (a_bytes == null) {
            return null;
        }
        a_bytes._offset = 0;
        ObjectHeader oh = new ObjectHeader(_container, this, a_bytes);
        boolean res = oh.objectMarshaller().findOffset(this, oh._headerAttributes, a_bytes, a_field);
        if(! res){
            return null;
        }
        return oh._marshallerFamily;
    }

    void forEachFieldMetadata(Visitor4 visitor) {
        if (i_fields != null) {
            for (int i = 0; i < i_fields.length; i++) {
                visitor.visit(i_fields[i]);
            }
        }
        if (i_ancestor != null) {
            i_ancestor.forEachFieldMetadata(visitor);
        }
    }
    
    public static ClassMetadata forObject(Transaction trans, Object obj, boolean allowCreation){
        ReflectClass reflectClass = trans.reflector().forObject(obj);
        if (reflectClass != null && reflectClass.getSuperclass() == null && obj != null) {
        	throw new ObjectNotStorableException(obj.toString());
        }
        if(allowCreation){
        	return trans.container().produceClassMetadata(reflectClass);
        }
        return trans.container().classMetadataForReflectClass(reflectClass);
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


    ClassMetadata getAncestor() {
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
        synchronized(_container._lock){
	        if (! stateOK()) {
                return new long[0];
            }
	        return getIDs(_container.transaction());
        }
    }

    public long[] getIDs(Transaction trans) {
        synchronized(_container._lock){
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
        return _classIndexed;
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
        return Arrays4.containsInstanceOf(i_fields, UUIDFieldMetadata.class);
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
        return Arrays4.containsInstanceOf(i_fields, VersionFieldMetadata.class);
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
    
    public Object indexEntryToObject(Transaction trans, Object indexEntry){
        if(indexEntry == null){
            return null;
        }
        int id = ((Integer)indexEntry).intValue();
        return container().getByID2(trans, id);
    }    

    public ReflectClass classReflector(){
        return _reflector;
    }

    public String getName() {
        if(i_name == null){
            if(_reflector != null){
                i_name = _reflector.getName();
            }
        }
        return i_name;
    }
    
    public StoredClass getParentStoredClass(){
        return getAncestor();
    }

    public StoredField[] getStoredFields(){
        synchronized(_container._lock){
	        if(i_fields == null){
	            return new StoredField[0];
	        }
	        StoredField[] fields = new StoredField[i_fields.length];
	        System.arraycopy(i_fields, 0, fields, 0, i_fields.length);
	        return fields;
        }
    }

    final ObjectContainerBase container() {
        return _container;
    }

    public FieldMetadata fieldMetadataForName(final String name) {
        final FieldMetadata[] yf = new FieldMetadata[1];
        forEachFieldMetadata(new Visitor4() {
            public void visit(Object obj) {
                if (name.equals(((FieldMetadata)obj).getName())) {
                    yf[0] = (FieldMetadata)obj;
                }
            }
        });
        return yf[0];

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

    void incrementFieldsOffset1(Buffer a_bytes) {
        int length = readFieldCount(a_bytes);
        for (int i = 0; i < length; i++) {
            i_fields[i].incrementOffset(a_bytes);
        }
    }

    final boolean init( ObjectContainerBase a_stream, ClassMetadata a_ancestor,ReflectClass claxx) {
        
        if(DTrace.enabled){
            DTrace.YAPCLASS_INIT.log(getID());
        }
        
        setAncestor(a_ancestor);
        
        Config4Impl config = a_stream.configImpl();
        String className = claxx.getName();		
		setConfig(config.configClass(className));
        
        if(! createConstructor(a_stream, claxx, className, false)){
            return false;
        }
        
        checkType();
        if (allowsQueries()) {
            _index.initialize(a_stream);
        }
        i_name = className;
        i_ancestor = a_ancestor;
        bitTrue(Const4.CHECKED_CHANGES);
        
        return true;
    }
    
    final void initConfigOnUp(Transaction systemTrans) {
        Config4Class extendedConfig=Platform4.extendConfiguration(_reflector, _container.configure(), i_config);
    	if(extendedConfig!=null) {
    		i_config=extendedConfig;
    	}
        if (i_config == null) {
            return;
        }
        if (! stateOK()) {
            return;
        }
        
        if (i_fields == null) {
            return;
        }
        
        for (int i = 0; i < i_fields.length; i++) {
            FieldMetadata curField = i_fields[i];
            String fieldName = curField.getName();
			if(!curField.hasConfig()&&extendedConfig!=null&&extendedConfig.configField(fieldName)!=null) {
            	curField.initIndex(this,fieldName);
            }
			curField.initConfigOnUp(systemTrans);
        }
    }

    void initOnUp(Transaction systemTrans) {
        if (! stateOK()) {
            return;
        }
        initConfigOnUp(systemTrans);
        storeStaticFieldValues(systemTrans, false);
    }

	Object instantiate(ObjectReference ref, Object obj, MarshallerFamily mf, ObjectHeaderAttributes attributes, StatefulBuffer buffer, boolean addToIDTree) {
        
        // overridden in YapClassPrimitive
        // never called for primitive YapAny
		
		adjustInstantiationDepth(buffer);

		final ObjectContainerBase stream = buffer.getStream();
		
        Transaction transaction = buffer.getTransaction();

		final boolean instantiating = (obj == null);
		if (instantiating) {
			obj = instantiateObject(buffer, mf);
			if (obj == null) {
				return null;
			}
			
			shareTransaction(obj, transaction);
			shareObjectReference(obj, ref);
            
			ref.setObjectWeak(stream, obj);
			transaction.referenceSystem().addExistingReferenceToObjectTree(ref);
			
			objectOnInstantiate(buffer.getTransaction(), obj);
		}
        
		if(addToIDTree){
			ref.addExistingReferenceToIdTree(transaction);
		}
        
		// when there's a ObjectConstructor configured for a type
		// the type is marshalled through a lone virtual field
		// of type YapFieldTranslator which should take care of everything		
		//final boolean instantiatedByTranslator = instantiating && configInstantiates();	
		
		if (instantiating) {
			if (buffer.getInstantiationDepth() == 0) {
				ref.setStateDeactivated();
			} else {
				activate(buffer, mf, attributes, ref, obj);
			}
		} else {
			if (activatingActiveObject(stream, ref)) {
				if (buffer.getInstantiationDepth() > 1) {
                    activateFields(buffer.getTransaction(), obj, buffer.getInstantiationDepth() - 1);
                }
			} else {
				activate(buffer, mf, attributes, ref, obj);
			}
		}
        return obj;
    }
	
	   /** @param obj */
    Object instantiateTransient(ObjectReference ref, Object obj, MarshallerFamily mf, ObjectHeaderAttributes attributes, StatefulBuffer buffer) {

        // overridden in YapClassPrimitive
        // never called for primitive YapAny

        Object instantiated = instantiateObject(buffer, mf);
        if (instantiated == null) {
            return null;
        }
        buffer.getStream().peeked(ref.getID(), instantiated);
        instantiateFields(ref, instantiated, mf, attributes, buffer);
        return instantiated;
    }
    
    
    public Object instantiate(UnmarshallingContext context) {
        
        // overridden in YapClassPrimitive
        // never called for primitive YapAny
        
        context.adjustInstantiationDepth();
        
        Object obj = context.persistentObject();
        
        final boolean instantiating = (obj == null);
        if (instantiating) {
            obj = instantiateObject(context);
            if (obj == null) {
                return null;
            }
            
            shareTransaction(obj, context.transaction());
            shareObjectReference(obj, context.reference());
            
            context.setObjectWeak(obj);
            
            context.transaction().referenceSystem().addExistingReferenceToObjectTree(context.reference());
            
            objectOnInstantiate(context.transaction(), obj);
        }
        
        context.addToIDTree();
        
        if (instantiating) {
            if (context.activationDepth() == 0) {
                context.reference().setStateDeactivated();
            } else {
                activate(context);
            }
        } else {
            if (activatingActiveObject(context.container(), context.reference())) {
                if (context.activationDepth() > 1) {
                    activateFields(context.transaction(), obj, context.activationDepth() - 1);
                }
            } else {
                activate(context);
            }
        }
        return obj;
    }
    
    public Object instantiateTransient(UnmarshallingContext context) {

        // overridden in YapClassPrimitive
        // never called for primitive YapAny

        Object obj = instantiateObject(context);
        if (obj == null) {
            return null;
        }
        context.container().peeked(context.objectID(), obj);
        instantiateFields(context);
        return obj;
        
    }

	private boolean activatingActiveObject(final ObjectContainerBase container, ObjectReference ref) {
		return !container._refreshInsteadOfActivate && ref.isActive();
	}

	private void activate(StatefulBuffer buffer, MarshallerFamily mf, ObjectHeaderAttributes attributes, ObjectReference ref, Object obj) {
		if(objectCanActivate(buffer.getTransaction(), obj)){
			ref.setStateClean();
			if (buffer.getInstantiationDepth() > 0 || cascadeOnActivate()) {
				instantiateFields(ref, obj, mf, attributes, buffer);
			}
			objectOnActivate(buffer.getTransaction(), obj);
		} else {
			ref.setStateDeactivated();
		}
	}
	
   private void activate(UnmarshallingContext context) {
        if(! objectCanActivate(context.transaction(), context.persistentObject())){
            context.reference().setStateDeactivated();
            return;
        }
        context.reference().setStateClean();
        if (context.activationDepth() > 0 || cascadeOnActivate()) {
            instantiateFields(context);
        }
        objectOnActivate(context.transaction(), context.persistentObject());
    }
	
    private boolean configInstantiates(){
        return config() != null && config().instantiates();
    }
	
	private Object instantiateObject(StatefulBuffer buffer, MarshallerFamily mf) {
        if (configInstantiates()) {
            return instantiateFromConfig(buffer.getStream(), buffer, mf);
        }
        return  instantiateFromReflector(buffer.getStream());
	}
	
	private Object instantiateObject(UnmarshallingContext context) {
	    Object obj = configInstantiates() ? instantiateFromConfig(context) : instantiateFromReflector(context.container());
	    context.persistentObject(obj);
        return obj;
	}

	private void objectOnInstantiate(Transaction transaction, Object instance) {
		transaction.container().callbacks().objectOnInstantiate(transaction, instance);
	}

	Object instantiateFromReflector(ObjectContainerBase stream) {
		if (_reflector == null) {
		    return null;
		}

		stream.instantiating(true);
		try {
		    return _reflector.newInstance();
		} catch (NoSuchMethodError e) {
		    stream.logMsg(7, classReflector().getName());
		    return null;
		} catch (Exception e) {
		    // TODO: be more helpful here
		    return null;
		} finally {
			stream.instantiating(false);
		}
	}

	private Object instantiateFromConfig(ObjectContainerBase stream, StatefulBuffer a_bytes, MarshallerFamily mf) {
		int bytesOffset = a_bytes._offset;
		a_bytes.incrementOffset(Const4.INT_LENGTH);
		// Field length is always 1
		try {
		    return i_config.instantiate(stream, i_fields[0].read(mf, a_bytes));                      
		} catch (CorruptionException e) {
			Messages.logErr(stream.configImpl(), 6, classReflector().getName(), e);
		    return null;
		} 
		finally {
			a_bytes._offset = bytesOffset;
		}
	}
	
	private Object instantiateFromConfig(UnmarshallingContext context) {
       
       int offset = context.offset();
       
    // Field length is always 1
       context.seek(offset + Const4.INT_LENGTH);
       
        try {
            return i_config.instantiate(context.container(), i_fields[0].read(context));                      
        } finally {
            context.seek(offset);
        }
    }


	private void adjustInstantiationDepth(StatefulBuffer a_bytes) {
		if (i_config != null) {
            a_bytes.setInstantiationDepth(
                i_config.adjustActivationDepth(a_bytes.getInstantiationDepth()));
        }
	}

	private boolean cascadeOnActivate() {
		return i_config != null && (i_config.cascadeOnActivate() == TernaryBool.YES);
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
		dispatchEvent(container, obj, EventDispatcher.ACTIVATE);
	}

	private boolean objectCanActivate(Transaction transaction, Object obj) {
		ObjectContainerBase container = transaction.container();
		return container.callbacks().objectCanActivate(transaction, obj)
			&& dispatchEvent(container, obj, EventDispatcher.CAN_ACTIVATE);
	}

    void instantiateFields(ObjectReference a_yapObject, Object a_onObject, MarshallerFamily mf,ObjectHeaderAttributes attributes, StatefulBuffer a_bytes) {
        mf._object.instantiateFields(this, attributes, a_yapObject, a_onObject, a_bytes);
    }
    
    void instantiateFields(UnmarshallingContext context) {
        context.marshallerFamily()._object.instantiateFields(context);
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
    
    boolean isEnum(){
        return _isEnum;
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
        return Platform4.isValueType(classReflector());
    }
    
    public String nameToWrite() {
        if(i_config != null && i_config.writeAs() != null){
            return i_config.writeAs();
        }
        if(i_name == null){
            return "";
        }
        return _container.configImpl().resolveAliasRuntimeName(i_name);
    }
    
    public final boolean callConstructor() {
        TernaryBool specialized = callConstructorSpecialized();
		// FIXME: If specified, return yes?!?
		if(!specialized.unspecified()){
		    return specialized.definiteYes();
		}
		return _container.configImpl().callConstructors().definiteYes();
    }
    
    private final TernaryBool callConstructorSpecialized(){
        if(i_config!= null){
            TernaryBool res = i_config.callConstructor();
            if(!res.unspecified()){
                return res;
            }
        }
        if(_isEnum){
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
    
    void purge() {
        _index.purge();
        
        // TODO: may want to add manual purge to Btree
        //       indexes here
    }

    public Object read(MarshallerFamily mf, StatefulBuffer a_bytes, boolean redirect) throws CorruptionException, Db4oIOException {
//        try {
            int id = a_bytes.readInt();
            int depth = a_bytes.getInstantiationDepth() - 1;

            Transaction trans = a_bytes.getTransaction();
            ObjectContainerBase stream = trans.container();

            if (a_bytes.getUpdateDepth() == Const4.TRANSIENT) {
                return stream.peekPersisted(trans, id, depth);
            }
            
            if (isValueType()) {
                return readValueType(trans, id, depth);
            } 

            Object ret = stream.getByID2(trans, id);

            if (ret instanceof Db4oTypeImpl) {
                depth = ((Db4oTypeImpl)ret).adjustReadDepth(depth);
            }

            // this is OK for primitive YapAnys. They will not be added
            // to the list, since they will not be found in the ID tree.
            stream.stillToActivate(trans, ret, depth);

            return ret;

//        } catch (Exception e) {
//        }
//        return null;
    }

	public Object readValueType(Transaction trans, int id, int depth) {
		// for C# value types only:
		// they need to be instantiated fully before setting them
		// on the parent object because the set call modifies identity.
		
		// We also have to instantiate structs completely every time.
    	int newDepth = Math.max(1, depth);
		
		// TODO: Do we want value types in the ID tree?
		// Shouldn't we treat them like strings and update
		// them every time ???		
		ObjectReference ref = trans.referenceForId(id);
		if (ref != null) {
		    Object obj = ref.getObject();
		    if(obj == null){
		        trans.removeReference(ref);
		    }else{
		        ref.activate(trans, obj, newDepth, false);
		        return ref.getObject();
		    }
		}
		return new ObjectReference(id).read(trans, newDepth,Const4.ADD_TO_ID_TREE, false);
	}
    
    public Object readQuery(Transaction a_trans, MarshallerFamily mf, boolean withRedirection, Buffer a_reader, boolean a_toArray) throws CorruptionException, Db4oIOException {
        try {
            return a_trans.container().getByID2(a_trans, a_reader.readInt());
        } catch (Exception e) {
        	// FIXME: DO WE NEED TO CATCH IT HERE?
            if (Debug.atHome) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public TypeHandler4 readArrayHandler(Transaction a_trans, MarshallerFamily mf, Buffer[] a_bytes) {
        if (isArray()) {
            return this;
        }
        return null;
    }

    public TypeHandler4 readArrayHandler1(Buffer[] a_bytes) {
        if(DTrace.enabled){
            if(a_bytes[0] instanceof StatefulBuffer){
                DTrace.READ_ARRAY_WRAPPER.log(((StatefulBuffer)a_bytes[0]).getID());
            }
        }
        if (isArray()) {
            if (Platform4.isCollectionTranslator(this.i_config)) {
                a_bytes[0].incrementOffset(Const4.INT_LENGTH);
                return new ArrayHandler(_container, null, false);
            }
            incrementFieldsOffset1(a_bytes[0]);
            if (i_ancestor != null) {
                return i_ancestor.readArrayHandler1(a_bytes);
            }
        }
        return null;
    }
    
    public QCandidate readSubCandidate(MarshallerFamily mf, Buffer reader, QCandidates candidates, boolean withIndirection) {
        int id = reader.readInt();
        if(id == 0){
            return null;
        }
        return new QCandidate(candidates, null, id, true);
    } 

    public void readCandidates(MarshallerFamily mf, final Buffer a_bytes, final QCandidates a_candidates) {
        int id = 0;

        int offset = a_bytes._offset;
        try {
            id = a_bytes.readInt();
        } catch (Exception e) {
        }
        a_bytes._offset = offset;

        if (id != 0) {
            final Transaction trans = a_candidates.i_trans;
            Object obj = trans.container().getByID(trans, id);
            if (obj != null) {

                a_candidates.i_trans.container().activate(trans, obj, 2);
                Platform4.forEachCollectionElement(obj, new Visitor4() {
                    public void visit(Object elem) {
                        a_candidates.addByIdentity(new QCandidate(a_candidates, elem, trans.container().getID(trans, elem), true));
                    }
                });
            }

        }
    }

    public final int readFieldCount(Buffer buffer) {
        int count = buffer.readInt();
        if (count > i_fields.length) {
            if (Debug.atHome) {
                System.out.println(
                    "ClassMetadata.readFieldCount "
                        + getName()
                        + " count to high:"
                        + count
                        + " i_fields:"
                        + i_fields.length);
                new Exception().printStackTrace();
            }
            return i_fields.length;
        }		
        return count;
    }

    public Object readIndexEntry(Buffer a_reader) {
        return new Integer(a_reader.readInt());
    }
    
    public Object readIndexEntry(MarshallerFamily mf, StatefulBuffer a_writer) throws CorruptionException{
        return readIndexEntry(a_writer);
    }

    byte[] readName(Transaction a_trans) {
        i_reader = a_trans.container().readReaderByID(a_trans, getID());
        return readName1(a_trans, i_reader);
    }

    public final byte[] readName1(Transaction trans, Buffer reader) {
		if (reader == null)
			return null;

		i_reader = reader;
		boolean ok = false;
		try {
			ClassMarshaller marshaller = MarshallerFamily.current()._class;
			i_nameBytes = marshaller.readName(trans, reader);
			_metaClassID = marshaller.readMetaClassID(reader);

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
    
    void readVirtualAttributes(Transaction a_trans, ObjectReference a_yapObject) {
        int id = a_yapObject.getID();
        ObjectContainerBase stream = a_trans.container();
        Buffer reader = stream.readReaderByID(a_trans, id);
        ObjectHeader oh = new ObjectHeader(stream, this, reader);
        oh.objectMarshaller().readVirtualAttributes(a_trans, this, a_yapObject, oh._headerAttributes, reader);
    }
    
	GenericReflector reflector() {
		return _container.reflector();
	}
    
    public void rename(String newName){
        if (!_container.isClient()) {
            int tempState = _state;
            setStateOK();
            i_name = newName;
            setStateDirty();
            write(_container.systemTransaction());
            _state = tempState;
        }else{
            Exceptions4.throwRuntimeException(58);
        }
    }

    final void createConfigAndConstructor(
        Hashtable4 a_byteHashTable,
        ReflectClass claxx,
        String name) {
        i_name = name;
        setConfig(_container.configImpl().configClass(i_name));
        if (claxx == null) {
            createConstructor(_container, i_name);
        } else {
            createConstructor(_container, claxx, i_name, true);
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
        	return _container.configImpl().resolveAliasStoredName(name);
        }
        throw new IllegalStateException();
    }

    boolean readThis() {
        if (stateUnread()) {
            setStateOK();
            setStateClean();
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
        
        MarshallerFamily.forConverterVersion(_container.converterVersion())._class.read(_container, this, i_reader);
       
        i_nameBytes = null;
        i_reader = null;
        bitFalse(Const4.READING);
    }	

    public void readThis(Transaction a_trans, Buffer a_reader) {
        throw Exceptions4.virtualException();
    }

    public void refresh() {
        if (!stateUnread()) {
            createConstructor(_container, i_name);
            bitFalse(Const4.CHECKED_CHANGES);
            checkChanges();
            if (i_fields != null) {
                for (int i = 0; i < i_fields.length; i++) {
                    i_fields[i].refresh();
                }
            }
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

    boolean renameField(String a_from, String a_to) {
        boolean renamed = false;
        for (int i = 0; i < i_fields.length; i++) {
            if (i_fields[i].getName().equals(a_to)) {
                _container.logMsg(9, "class:" + getName() + " field:" + a_to);
                return false;
            }
        }
        for (int i = 0; i < i_fields.length; i++) {
            if (i_fields[i].getName().equals(a_from)) {
                i_fields[i].setName(a_to);
                renamed = true;
            }
        }
        return renamed;
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

    private final void setStateOK() {
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
        if(! stateOK()  || i_fields == null){
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

    boolean storeField(ReflectField a_field) {
        if (a_field.isStatic()) {
            return false;
        }
        if (a_field.isTransient()) {
            Config4Class config = configOrAncestorConfig();
            if (config == null) {
                return false;
            }
            if (!config.storeTransientFields()) {
                return false;
            }
        }
        return Platform4.canSetAccessible() || a_field.isPublic();
    }
    
    public StoredField storedField(String a_name, Object a_type) {
        synchronized(_container._lock){
        	
            ClassMetadata yc = _container.classMetadataForReflectClass(ReflectorUtils.reflectClassFor(reflector(), a_type)); 
    		
	        if(i_fields != null){
	            for (int i = 0; i < i_fields.length; i++) {
	                if(i_fields[i].getName().equals(a_name)){
	                    if(yc == null || yc == i_fields[i].getFieldYapClass(_container)){
	                        return (i_fields[i]);
	                    }
	                }
                }
	        }
    		
    		//TODO: implement field creation
    		
	        return null;
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
		return !trans.container().config().isReadOnly() 
					&&  (staticFieldValuesArePersisted()
        			|| Platform4.storeStaticFieldValues(trans.reflector(), classReflector()));
	}

	private void updateStaticClass(final Transaction trans, final StaticClass sc) {
		final ObjectContainerBase stream = trans.container();
		stream.activate(trans, sc, 4);
		
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
		StaticClass sc = new StaticClass(i_name, toStaticFieldArray(staticReflectFieldsToStaticFields()));
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
		reflectField.setAccessible();
		return reflectField.get(null);
	}

	private void setStaticClass(Transaction trans, StaticClass sc) {
		// TODO: we should probably use a specific update depth here, 4?
		trans.container().setInternal(trans, sc, true);
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
                reflectField.set(null, existingField.value);
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
		q.descend("name").constrain(i_name);
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

    public void writeIndexEntry(Buffer a_writer, Object a_object) {
        
        if(a_object == null){
            a_writer.writeInt(0);
            return;
        }
        
        a_writer.writeInt(((Integer)a_object).intValue());
    }
    
    public final void writeThis(Transaction trans, Buffer writer) {
        MarshallerFamily.current()._class.write(trans, this, writer);
    }

    // Comparison_______________________

    private ReflectClass i_compareTo;
    
    public Comparable4 prepareComparison(Object obj) {
        if(obj == null){
            i_lastID = 0;
            i_compareTo = null;
            return this;
        }
        if(obj instanceof Integer){
            i_lastID = ((Integer)obj).intValue();
        }else if (obj instanceof TransactionContext){
            TransactionContext tc = (TransactionContext)obj;
            obj = tc._object;
            i_lastID = _container.getID(tc._transaction, obj);
        }else{
            throw new IllegalComparisonException();
        }
        i_compareTo = reflector().forObject(obj);
        return this;
    }
    
    public int compareTo(Object obj) {
        if(obj instanceof TransactionContext){
            obj = ((TransactionContext)obj)._object;
        }
        if(obj instanceof Integer){
            return ((Integer)obj).intValue() - i_lastID;
        }
        if( obj == null ){
            if (i_compareTo == null){
                return 0;
            }
            return -1;
        }
        if(i_compareTo != null){
            if (i_compareTo.isAssignableFrom(reflector().forObject(obj))){
                return 0;
            }
        }
        throw new IllegalComparisonException(); 
    }
    
    public String toString(MarshallerFamily mf, StatefulBuffer writer, ObjectReference yapObject, int depth, int maxDepth)  {
        int length = readFieldCount(writer);
        String str = "";
        for (int i = 0; i < length; i++) {
            str += i_fields[i].toString(mf, writer);
        }
        if (i_ancestor != null) {
            str+= i_ancestor.toString(mf, writer, yapObject, depth, maxDepth);
        }
        return str;

    }

    public static void defragObject(BufferPair readers) {
    	ObjectHeader header=ObjectHeader.defrag(readers);
    	header._marshallerFamily._object.defragFields(header.classMetadata(),header,readers);
        if (Deploy.debug) {
            readers.readEnd();
        }
    }	

	public void defrag(MarshallerFamily mf, BufferPair readers, boolean redirect) {
		if(hasClassIndex()) {
			readers.copyID();
		}
		else {
			readers.copyUnindexedID();
		}
		int restLength = (linkLength()-Const4.INT_LENGTH);
		readers.incrementOffset(restLength);
	}
	
	public void defragClass(BufferPair readers, int classIndexID) throws CorruptionException, IOException {
		MarshallerFamily mf = MarshallerFamily.current();
		mf._class.defrag(this,_container.stringIO(), readers, classIndexID);
	}

    public static ClassMetadata readClass(ObjectContainerBase stream, Buffer reader) {
        ObjectHeader oh = new ObjectHeader(stream, reader);
        return oh.classMetadata();
    }

	public boolean isAssignableFrom(ClassMetadata other) {
		return classReflector().isAssignableFrom(other.classReflector());
	}

	public final void defragIndexEntry(BufferPair readers) {
		readers.copyID();
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
    
    public Object read(ReadContext context) {
        return context.readObject();
    }

    public void write(WriteContext context, Object obj) {
        context.writeObject(obj);
    }
    
}
