/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.config.ObjectTranslator;
import com.db4o.ext.StoredField;
import com.db4o.foundation.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.ix.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.query.processor.*;
import com.db4o.internal.slots.*;
import com.db4o.reflect.*;
import com.db4o.reflect.generic.*;


/**
 * @exclude
 */
public class FieldMetadata implements StoredField {

    private ClassMetadata         i_yapClass;

    //  position in YapClass i_fields
    private int              i_arrayPosition;

    protected String         i_name;

    private boolean          i_isArray;

    private boolean          i_isNArray;

    private boolean          i_isPrimitive;

    private ReflectField     i_javaField;

    TypeHandler4              i_handler;

    private int              i_handlerID;

    private int              i_state;

    private static final int NOT_LOADED  = 0;

    private static final int UNAVAILABLE = -1;

    private static final int AVAILABLE   = 1;

    private Config4Field     i_config;

    private Db4oTypeImpl     i_db4oType;
    
    private BTree _index;

    static final FieldMetadata[]  EMPTY_ARRAY = new FieldMetadata[0];

    public FieldMetadata(ClassMetadata a_yapClass) {
        i_yapClass = a_yapClass;
    }

    FieldMetadata(ClassMetadata a_yapClass, ObjectTranslator a_translator) {
        // for YapFieldTranslator only
    	this(a_yapClass);
        init(a_yapClass, a_translator.getClass().getName());
        i_state = AVAILABLE;
        ObjectContainerBase stream =getStream(); 
        i_handler = stream.i_handlers.handlerForClass(
            stream, stream.reflector().forClass(a_translator.storedClass()));
    }

    FieldMetadata(ClassMetadata a_yapClass, ReflectField a_field, TypeHandler4 a_handler) {
    	this(a_yapClass);
        init(a_yapClass, a_field.getName());
        i_javaField = a_field;
        i_javaField.setAccessible();
        i_handler = a_handler;
        
        // TODO: beautify !!!  possibly pull up isPrimitive to ReflectField
        boolean isPrimitive = false;
        if(a_field instanceof GenericField){
            isPrimitive  = ((GenericField)a_field).isPrimitive();
        }
        configure( a_field.getFieldType(), isPrimitive);
        checkDb4oType();
        i_state = AVAILABLE;
    }

    public void addFieldIndex(MarshallerFamily mf, ClassMetadata yapClass, StatefulBuffer writer, Slot oldSlot) {
        if (! hasIndex()) {
            writer.incrementOffset(linkLength());
            return;
        }
        
        addIndexEntry(writer, readIndexEntry(mf, writer));
    }

    protected void addIndexEntry(StatefulBuffer a_bytes, Object indexEntry) {
        addIndexEntry(a_bytes.getTransaction(), a_bytes.getID(), indexEntry);
    }

    public void addIndexEntry(Transaction trans, int parentID, Object indexEntry) {
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
		return i_javaField.indexEntry(indexEntry);
	}
    
    public boolean canUseNullBitmap(){
        return true;
    }
    
    // alive() checked
    public Object readIndexEntry(MarshallerFamily mf, StatefulBuffer writer){
        try {
            return i_handler.readIndexEntry(mf, writer);
        } catch (CorruptionException e) {
        }
        return null;
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
        if (i_state == AVAILABLE) {
            return true;
        }
        if (i_state == NOT_LOADED) {

            if (i_handler == null) {

                // this may happen if the local YapClassCollection has not
                // been updated from the server and presumably in some
                // refactoring cases. The origin is not verified but we
                // saw a database that had 0 in some wrapper IDs.

                // We try to heal the problem by re-reading the class.

                // This could be inherently dangerous, if the class type of
                // a field was modified.

                // TODO: add class refactoring features

                i_handler = loadJavaField1();
                if (i_handler != null) {
                    if (i_handlerID == 0) {
                        i_handlerID = i_handler.getID();
                    } else {
                        if (i_handler.getID() != i_handlerID) {
                            i_handler = null;
                        }
                    }
                }
            }

            loadJavaField();
            
            if(i_handler == null || i_javaField == null){
                i_state = UNAVAILABLE;
                i_javaField = null;
            }else{

                // TODO: This part is not quite correct.
                // We are using the old array information read from file to wrap.

                // If a schema evolution changes an array to a different variable,
                // we are in trouble here.

                i_handler = wrapHandlerToArrays(getStream(), i_handler);

                i_state = AVAILABLE;
                checkDb4oType();
            }
        }
        return i_state == AVAILABLE;

    }

    boolean canAddToQuery(String fieldName){
        if(! alive()){
            return false;
        }
        return fieldName.equals(getName())  && getParentYapClass() != null && !getParentYapClass().isInternal(); 
    }
    
    public boolean canHold(ReflectClass claxx) {
        // alive() is checked in QField caller
        if (claxx == null) {
            return !i_isPrimitive;
        }
        return i_handler.canHold(claxx);
    }

    public Object coerce(ReflectClass claxx, Object obj) {
        // alive() is checked in QField caller
        
        if (claxx == null || obj == null) {
            return i_isPrimitive ? No4.INSTANCE : obj;
        }
        return i_handler.coerce(claxx, obj);
    }

    public final boolean canLoadByIndex() {
        if (i_handler instanceof ClassMetadata) {
            ClassMetadata yc = (ClassMetadata) i_handler;
            if(yc.isArray()){
                return false;
            }
        }
        return true;
    }

    void cascadeActivation(Transaction a_trans, Object a_object, int a_depth,
        boolean a_activate) {
        if (alive()) {
            try {
                Object cascadeTo = getOrCreate(a_trans, a_object);
                if (cascadeTo != null && i_handler != null) {
                    i_handler.cascadeActivation(a_trans, cascadeTo, a_depth,
                        a_activate);
                }
            } catch (Exception e) {
            }
        }
    }

    private void checkDb4oType() {
        if (i_javaField != null) {
            if (getStream().i_handlers.ICLASS_DB4OTYPE.isAssignableFrom(i_javaField.getFieldType())) {
                i_db4oType = HandlerRegistry.getDb4oType(i_javaField.getFieldType());
            }
        }
    }

    void collectConstraints(Transaction a_trans, QConObject a_parent,
        Object a_template, Visitor4 a_visitor) {
        Object obj = getOn(a_trans, a_template);
        if (obj != null) {
            Collection4 objs = Platform4.flattenCollection(a_trans.stream(), obj);
            Iterator4 j = objs.iterator();
            while (j.moveNext()) {
                obj = j.current();
                if (obj != null) {
                    
                    if (i_isPrimitive) {
                        if (i_handler instanceof PrimitiveHandler) {
                            if (obj.equals(((PrimitiveHandler) i_handler)
                                .primitiveNull())) {
                                return;
                            }
                        }
                    }
                    
                    if(Deploy.csharp){
                        if(Platform4.ignoreAsConstraint(obj)){
                            return;
                        }
                    }
                    if (!a_parent.hasObjectInParentPath(obj)) {
                        a_visitor.visit(new QConObject(a_trans, a_parent,
                            qField(a_trans), obj));
                    }
                }
            }
        }
    }

    public final TreeInt collectIDs(MarshallerFamily mf, TreeInt tree, StatefulBuffer a_bytes) {
        if (alive()) {
            if (i_handler instanceof ClassMetadata) {
                tree = (TreeInt) Tree.add(tree, new TreeInt(a_bytes.readInt()));
            } else if (i_handler instanceof ArrayHandler) {
                tree = ((ArrayHandler) i_handler).collectIDs(mf, tree, a_bytes);
            }
        }
        return tree;

    }

    void configure(ReflectClass a_class, boolean isPrimitive) {
        i_isPrimitive = isPrimitive | a_class.isPrimitive();
        i_isArray = a_class.isArray();
        if (i_isArray) {
            ReflectArray reflectArray = getStream().reflector().array();
            i_isNArray = reflectArray.isNDimensional(a_class);
            a_class = reflectArray.getComponentType(a_class);
            if (Deploy.csharp) {
            } else {
                i_isPrimitive = a_class.isPrimitive();
            }
            if (i_isNArray) {
                i_handler = new MultidimensionalArrayHandler(getStream(), i_handler, i_isPrimitive);
            } else {
                i_handler = new ArrayHandler(getStream(), i_handler, i_isPrimitive);
            }
        }
    }

    void deactivate(Transaction a_trans, Object a_onObject, int a_depth) {
        if (!alive()) {
            return;
        }
        try {
            boolean isEnumClass = i_yapClass.isEnum();
            if (i_isPrimitive && !i_isArray) {
                if(!isEnumClass) {
                    i_javaField.set(a_onObject, ((PrimitiveHandler) i_handler)
                        .primitiveNull());
                }
                return;
            }
            if (a_depth > 0) {
                cascadeActivation(a_trans, a_onObject, a_depth, false);
            }
            if(!isEnumClass) {
                i_javaField.set(a_onObject, null);
            }
        } catch (Throwable t) {
        }
    }

    public void delete(MarshallerFamily mf, StatefulBuffer a_bytes, boolean isUpdate) {
        if (! alive()) {
            incrementOffset(a_bytes);
            return;
        }
        
        removeIndexEntry(mf, a_bytes);
        
        boolean dotnetValueType = false;
        if(Deploy.csharp){
            dotnetValueType = Platform4.isValueType(i_handler.classReflector());    
        }
        
        if ((i_config != null && i_config.cascadeOnDelete() == Const4.YES)
            || dotnetValueType) {
            int preserveCascade = a_bytes.cascadeDeletes();
            a_bytes.setCascadeDeletes(1);
            i_handler.deleteEmbedded(mf, a_bytes);
            a_bytes.setCascadeDeletes(preserveCascade);
        }else if(i_config != null && i_config.cascadeOnDelete() == Const4.NO){
            int preserveCascade = a_bytes.cascadeDeletes();
            a_bytes.setCascadeDeletes(0);
            i_handler.deleteEmbedded(mf, a_bytes);
            a_bytes.setCascadeDeletes(preserveCascade);
        } else {
            i_handler.deleteEmbedded(mf, a_bytes);
        }
    }

    private final void removeIndexEntry(MarshallerFamily mf, StatefulBuffer a_bytes) {
        if(! hasIndex()){
            return;
        }
        int offset = a_bytes._offset;
        Object obj = null;
        try {
            obj = i_handler.readIndexEntry(mf, a_bytes);
        } catch (CorruptionException e) {
            if(Debug.atHome){
                e.printStackTrace();
            }
        }
        removeIndexEntry(a_bytes.getTransaction(), a_bytes.getID(), obj);
        a_bytes._offset = offset;
    }

    public boolean equals(Object obj) {
        if (obj instanceof FieldMetadata) {
            FieldMetadata yapField = (FieldMetadata) obj;
            yapField.alive();
            alive();
            return yapField.i_isPrimitive == i_isPrimitive
                && yapField.i_handler.equals(i_handler)
                && yapField.i_name.equals(i_name);
        }
        return false;
    }

    public int hashCode() {
    	return i_name.hashCode();
    }
    
    public Object get(Object a_onObject) {
        if (i_yapClass != null) {
            ObjectContainerBase stream = i_yapClass.getStream();
            if (stream != null) {
                synchronized (stream.i_lock) {
                    stream.checkClosed();
                    ObjectReference yo = stream.getYapObject(a_onObject);
                    if (yo != null) {
                        int id = yo.getID();
                        if (id > 0) {
                            StatefulBuffer writer = stream.readWriterByID(stream
                                .getTransaction(), id);
                            if (writer != null) {
                                
                                writer._offset = 0;
                                ObjectHeader oh = new ObjectHeader(stream, i_yapClass, writer);
                                if(oh.objectMarshaller().findOffset(i_yapClass,oh._headerAttributes, writer, this)){
                                    try {
                                        return read(oh._marshallerFamily, writer);
                                    } catch (CorruptionException e) {
                                        if (Debug.atHome) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public String getName() {
        return i_name;
    }

    public ClassMetadata getFieldYapClass(ObjectContainerBase a_stream) {
        // alive needs to be checked by all callers: Done
        return i_handler.getYapClass(a_stream);
    }
    
    public TypeHandler4 getHandler() {
        // alive needs to be checked by all callers: Done
        return i_handler;
    }
    
    public int getHandlerID(){
        return i_handlerID;
    }

    public Object getOn(Transaction a_trans, Object a_OnObject) {
        if (alive()) {
            try {
                return i_javaField.get(a_OnObject);
            } catch (Throwable t) {
                // this is typically the case, if a field is removed from an
                // object.
            }
        }
        return null;
    }

    /**
     * dirty hack for com.db4o.types some of them need to be set automatically
     * TODO: Derive from YapField for Db4oTypes
     */
    public Object getOrCreate(Transaction a_trans, Object a_OnObject) {
        if (alive()) {
            try {
                Object obj = i_javaField.get(a_OnObject);
                if (i_db4oType != null) {
                    if (obj == null) {
                        obj = i_db4oType.createDefault(a_trans);
                        i_javaField.set(a_OnObject, obj);
                    }
                }
                return obj;
            } catch (Throwable t) {
                if(Debug.atHome){
                    t.printStackTrace();
                }
            }
            // this is typically the case, if a field is removed from an
            // object.
        }
        return null;
    }

    public ClassMetadata getParentYapClass() {
        // alive needs to be checked by all callers: Done
        return i_yapClass;
    }

    public ReflectClass getStoredType() {
        if (!Deploy.csharp) {
            if (i_isPrimitive) {
                return i_handler.primitiveClassReflector();
            }
        }
        if(i_handler==null) {
        	return null;
        }
        return i_handler.classReflector();
    }
    
    public ObjectContainerBase getStream(){
        if(i_yapClass == null){
            return null;
        }
        return i_yapClass.getStream();
    }
    
    public boolean hasConfig() {
    	return i_config!=null;
    }
    
    public boolean hasIndex() {
        // alive needs to be checked by all callers: Done
        return _index != null;
    }

    public final void incrementOffset(Buffer a_bytes) {
        a_bytes.incrementOffset(linkLength());
    }

    public final void init(ClassMetadata a_yapClass, String a_name) {
        i_yapClass = a_yapClass;
        i_name = a_name;
        initIndex(a_yapClass, a_name);
    }

	final void initIndex(ClassMetadata a_yapClass, String a_name) {
		if (a_yapClass.i_config != null) {
            i_config = a_yapClass.i_config.configField(a_name);
            if (Debug.configureAllFields) {
                if (i_config == null) {
                    i_config = (Config4Field) a_yapClass.i_config
                        .objectField(i_name);
                }
            }
        }
	}
    
    public void init(int handlerID, boolean isPrimitive, boolean isArray, boolean isNArray) {
        i_handlerID = handlerID;
        i_isPrimitive = isPrimitive;
        i_isArray = isArray;
        i_isNArray = isNArray;
    }

    private boolean _initialized=false;

    final void initConfigOnUp(Transaction trans) {
        if (i_config != null&&!_initialized) {
        	_initialized=true;
            i_config.initOnUp(trans, this);
        }
    }

    public void instantiate(MarshallerFamily mf, ObjectReference a_yapObject, Object a_onObject, StatefulBuffer a_bytes)
        throws CorruptionException {
        
        if (! alive()) {
            incrementOffset(a_bytes);
            return;
        }
            
        Object toSet = null;
        try {
            toSet = read(mf, a_bytes);
        } catch (Exception e) {
            throw new CorruptionException();
        }
        if (i_db4oType != null) {
            if (toSet != null) {
                ((Db4oTypeImpl) toSet).setTrans(a_bytes.getTransaction());
            }
        }
        
        set(a_onObject, toSet);
        
    }

    public boolean isArray() {
        return i_isArray;
    }

    
    public int linkLength() {
        alive();
        if (i_handler == null) {
            // must be a YapClass
            return Const4.ID_LENGTH;
        }
        return i_handler.linkLength();
    }
    
    public void calculateLengths(Transaction trans, ObjectHeaderAttributes header, Object obj){
        alive();
        if (i_handler == null) {
            
            // must be a YapClass
            
            header.addBaseLength(Const4.ID_LENGTH);
            return ;
        }
        
        i_handler.calculateLengths(trans, header, true, obj, true);
    }
    

    public void loadHandler(ObjectContainerBase a_stream) {
    	i_handler=a_stream.handlerByID(i_handlerID);
    }

    private void loadJavaField() {
        TypeHandler4 handler = loadJavaField1();
        if (handler == null || (!handler.equals(i_handler))) {
            i_javaField = null;
            i_state = UNAVAILABLE;
        }
    }

    private TypeHandler4 loadJavaField1() {
        try {
            ObjectContainerBase stream = i_yapClass.getStream();
            ReflectClass claxx = i_yapClass.classReflector();
            if(claxx == null){
                return null;
            }
            i_javaField = claxx.getDeclaredField(i_name);
            if (i_javaField == null) {
                return null;
            }
            i_javaField.setAccessible();
            stream.showInternalClasses(true);
            TypeHandler4 handler = stream.i_handlers.handlerForClass(stream,
                i_javaField.getFieldType());
            stream.showInternalClasses(false);
            return handler;
        } catch (Exception e) {
            if (Debug.atHome) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void marshall(
            ObjectReference yo, 
            Object obj, 
            MarshallerFamily mf,
            StatefulBuffer writer,
            Config4Class config, 
            boolean isNew) {
        
        // alive needs to be checked by all callers: Done
        
        Object indexEntry = null;
        
        if (obj != null
            && ((config != null && (config.cascadeOnUpdate() == Const4.YES)) || (i_config != null && (i_config.cascadeOnUpdate() == Const4.YES)))) {
            int min = 1;
            if (i_yapClass.isCollection(obj)) {
                GenericReflector reflector = i_yapClass.reflector();
                min = reflector.collectionUpdateDepth(reflector.forObject(obj));
            }
            int updateDepth = writer.getUpdateDepth();
            if (updateDepth < min) {
                writer.setUpdateDepth(min);
            }
            indexEntry = i_handler.writeNew(mf, obj, true, writer, true, true);
            writer.setUpdateDepth(updateDepth);
        } else {
            indexEntry = i_handler.writeNew(mf, obj, true, writer, true, true);
        }
        addIndexEntry(writer, indexEntry);
    }

    public boolean needsArrayAndPrimitiveInfo(){
        return true;
    }

    public boolean needsHandlerId(){
        return true;
    }
    
    public Comparable4 prepareComparison(Object obj) {
        if (alive()) {
            i_handler.prepareComparison(obj);
            return i_handler;
        }
        return null;
    }
    
    public QField qField(Transaction a_trans) {
        int yapClassID = 0;
        if(i_yapClass != null){
            yapClassID = i_yapClass.getID();
        }
        return new QField(a_trans, i_name, this, yapClassID, i_arrayPosition);
    }

    Object read(MarshallerFamily mf, StatefulBuffer a_bytes) throws CorruptionException {
        if (!alive()) {
            incrementOffset(a_bytes);
            return null;
        }
        return i_handler.read(mf, a_bytes, true);
    }

    public Object readQuery(Transaction a_trans, MarshallerFamily mf, Buffer a_reader)
        throws CorruptionException {
        return i_handler.readQuery(a_trans, mf, true, a_reader, false);
    }
    
    public void readVirtualAttribute(Transaction a_trans, Buffer a_reader, ObjectReference a_yapObject) {
        a_reader.incrementOffset(i_handler.linkLength());
    }

    void refresh() {
        TypeHandler4 handler = loadJavaField1();
        if (handler != null) {
            handler = wrapHandlerToArrays(getStream(), handler);
            if (handler.equals(i_handler)) {
                return;
            }
        }
        i_javaField = null;
        i_state = UNAVAILABLE;
    }

    public void rename(String newName) {
        ObjectContainerBase stream = i_yapClass.getStream();
        if (! stream.isClient()) {
            i_name = newName;
            i_yapClass.setStateDirty();
            i_yapClass.write(stream.getSystemTransaction());
        } else {
            Exceptions4.throwRuntimeException(58);
        }
    }

    public void setArrayPosition(int a_index) {
        i_arrayPosition = a_index;
    }
    
    public final void set(Object onObject, Object obj){
        
        try {
            i_javaField.set(onObject, obj);
        } catch (Throwable t) {
            if(Debug.atHome){
                t.printStackTrace();
            }
        }
    }

    void setName(String a_name) {
        i_name = a_name;
    }

    boolean supportsIndex() {
        return alive() && i_handler.supportsIndex();
    }
    
    public final void traverseValues(final Visitor4 userVisitor) {
        if(! alive()){
            return;
        }
        
        assertHasIndex();
        
        ObjectContainerBase stream = i_yapClass.getStream();
        if(stream.isClient()){
            Exceptions4.throwRuntimeException(Messages.CLIENT_SERVER_UNSUPPORTED);
        }
        
        synchronized(stream.lock()){
            final Transaction trans = stream.getTransaction();
            _index.traverseKeys(trans, new Visitor4() {
                public void visit(Object obj) {
                    FieldIndexKey key = (FieldIndexKey) obj;
                    userVisitor.visit(i_handler.indexEntryToObject(trans, key.value()));
                }
            });
        }
    }

	private void assertHasIndex() {
		if(! hasIndex()){
            Exceptions4.throwRuntimeException(Messages.ONLY_FOR_INDEXED_FIELDS);
        }
	}


    private final TypeHandler4 wrapHandlerToArrays(ObjectContainerBase a_stream, TypeHandler4 a_handler) {
        if (i_isNArray) {
            a_handler = new MultidimensionalArrayHandler(a_stream, a_handler, i_isPrimitive);
        } else {
            if (i_isArray) {
                a_handler = new ArrayHandler(a_stream, a_handler, i_isPrimitive);
            }
        }
        return a_handler;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (i_yapClass != null) {
            sb.append(i_yapClass.getName());
            sb.append(".");
            sb.append(getName());
        }
        return sb.toString();
    }

    public final String toString(MarshallerFamily mf, StatefulBuffer writer) {
        String str = "\n Field " + i_name;
        if (! alive()) {
            incrementOffset(writer);
        }else{
            Object obj = null;
            try{
                obj = read(mf, writer);
            }catch(Exception e){
                // can happen
            }
            if(obj == null){
                str += "\n [null]";
            }else{
                str+="\n  " + obj.toString();
            }
        }
        return str;
    }

    public void initIndex(Transaction systemTrans) {        
        initIndex(systemTrans, 0);
    }

    public void initIndex(Transaction systemTrans, final int id) {
    	if(_index != null){
    		throw new IllegalStateException();
        }
        if(systemTrans.stream().isClient()){
            return;
        }
        _index = newBTree(systemTrans, id);
    }

	protected final BTree newBTree(Transaction systemTrans, final int id) {
		ObjectContainerBase stream = systemTrans.stream();
		Indexable4 indexHandler = indexHandler(stream);
		if(indexHandler==null) {
			if(Debug.atHome) {
				System.err.println("Could not create index for "+this+": No index handler found");
			}
			return null;
		}
		return new BTree(systemTrans, id, new FieldIndexKeyHandler(stream, indexHandler));
	}

	protected Indexable4 indexHandler(ObjectContainerBase stream) {
		ReflectClass indexType =null;
		if(i_javaField!=null) {
			indexType=i_javaField.indexType();
		}
		Indexable4 indexHandler = stream.i_handlers.handlerForClass(stream,indexType);
		if(Debug.indexAllFields) {
			// check for legacy case with uninitialized MetaIndex on old headers
			if(indexHandler==null) {
				indexHandler=i_handler;
				System.err.println("No index handler found for "+this);
			}
		}
		return indexHandler;
	}
    
	public BTree getIndex(Transaction transaction){
        return _index;
    }

    public boolean isVirtual() {
        return false;
    }

    public boolean isPrimitive() {
        return i_isPrimitive;
    }
	
	public BTreeRange search(Transaction transaction, Object value) {
		assertHasIndex();
		BTreeNodeSearchResult lowerBound = searchLowerBound(transaction, value);
	    BTreeNodeSearchResult upperBound = searchUpperBound(transaction, value);	    
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

	protected void rebuildIndexForObject(LocalObjectContainer stream, final ClassMetadata yapClass, final int objectId) {
		StatefulBuffer writer = stream.readWriterByID(stream.getSystemTransaction(), objectId);
		if (writer != null) {
		    rebuildIndexForWriter(stream, writer, objectId);
		} else {
		    if(Deploy.debug){
		        throw new RuntimeException("Unexpected null object for ID");
		    }
		}
	}

	protected void rebuildIndexForWriter(LocalObjectContainer stream, StatefulBuffer writer, final int objectId) {
		ObjectHeader oh = new ObjectHeader(stream, writer);
		Object obj = readIndexEntryForRebuild(writer, oh);
		addIndexEntry(stream.getSystemTransaction(), objectId, obj);
	}

	private Object readIndexEntryForRebuild(StatefulBuffer writer, ObjectHeader oh) {
		return oh.objectMarshaller().readIndexEntry(oh.yapClass(), oh._headerAttributes, this, writer);
	}

    public void dropIndex(Transaction systemTrans) {
        if(_index == null){
            return;
        }
        ObjectContainerBase stream = systemTrans.stream(); 
        if (stream.configImpl().messageLevel() > Const4.NONE) {
            stream.message("dropping index " + toString());
        }
        _index.free(systemTrans);
        stream.setDirtyInSystemTransaction(getParentYapClass());
        _index = null;
    }    
    
    public void defragField(MarshallerFamily mf,ReaderPair readers) {
    	getHandler().defrag(mf, readers, true);
    }
}