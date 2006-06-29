/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.inside.*;
import com.db4o.inside.btree.*;
import com.db4o.inside.convert.conversions.*;
import com.db4o.inside.diagnostic.*;
import com.db4o.inside.marshall.*;
import com.db4o.query.*;
import com.db4o.reflect.*;
import com.db4o.reflect.generic.*;

/**
 * @exclude
 */
public class YapClass extends YapMeta implements TypeHandler4, StoredClass, UseSystemTransaction {

    public YapClass i_ancestor;

    Config4Class i_config;
    int _metaClassID;
    
    public YapField[] i_fields;
    
    private ClassIndex i_index;
    
    private BTree _index;
    
    
    protected String i_name;

    protected final YapStream i_stream;

    byte[] i_nameBytes;
    private YapReader i_reader;

    private Db4oTypeImpl i_db4oType;
    
    private ReflectClass _reflector;
    private boolean _isEnum;
    boolean i_dontCallConstructors;
    
    private EventDispatcher _eventDispatcher;
    
    private boolean _internal;
    private boolean _unversioned;
    
    boolean isInternal() {
    	return _internal;
    }

    // for indexing purposes.
    // TODO: check race conditions, upon multiple calls against the same class
    int i_lastID;
    
    YapClass(YapStream stream, ReflectClass reflector){
    	i_stream = stream;
        _reflector = reflector;
    }
    
    void activateFields(Transaction a_trans, Object a_object, int a_depth) {
        if(dispatchEvent(a_trans.i_stream, a_object, EventDispatcher.CAN_ACTIVATE)){
            activateFields1(a_trans, a_object, a_depth);
        }
    }

    void activateFields1(Transaction a_trans, Object a_object, int a_depth) {
        for (int i = 0; i < i_fields.length; i++) {
            i_fields[i].cascadeActivation(a_trans, a_object, a_depth, true);
        }
        if (i_ancestor != null) {
            i_ancestor.activateFields1(a_trans, a_object, a_depth);
        }
    }

    final void addFieldIndices(YapWriter a_writer, boolean a_new) {
        if(hasIndex() || hasVirtualAttributes()){
            ObjectHeader oh = new ObjectHeader(i_stream, this, a_writer);
            oh._marshallerFamily._object.addFieldIndices(this, oh._headerAttributes, a_writer, a_new);
        }
    }
    
    void addMembers(YapStream a_stream) {
        bitTrue(YapConst.CHECKED_CHANGES);
        if (addTranslatorFields(a_stream)) {
        	return;
        }

        if (a_stream.detectSchemaChanges()) {
            Iterator4 m;
            boolean found;
            boolean dirty = isDirty();
            YapField field;
            TypeHandler4 wrapper;

            Collection4 members = new Collection4();

            members.addAll(i_fields);
            if(generateVersionNumbers()) {
                if(! hasVersionField()) {
                    members.add(a_stream.i_handlers.i_indexes.i_fieldVersion);
                    dirty = true;
                }
            }
            if(generateUUIDs()) {
                if(! hasUUIDField()) {
                    members.add(a_stream.i_handlers.i_indexes.i_fieldUUID);
                    dirty = true;
                }
            }
            ReflectField[] fields = classReflector().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                if (storeField(fields[i])) {
                    wrapper = a_stream.i_handlers.handlerForClass(a_stream, fields[i].getFieldType());
                    if (wrapper == null) {
                        continue;
                    }
                    field = new YapField(this, fields[i], wrapper);

                    found = false;
                    m = members.iterator();
                    while (m.hasNext()) {
                        if (((YapField)m.next()).equals(field)) {
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

                    members.add(field);
                }
            }
            if (dirty) {
                i_stream.setDirty(this);
                i_fields = new YapField[members.size()];
                members.toArray(i_fields);
                for (int i = 0; i < i_fields.length; i++) {
                    i_fields[i].setArrayPosition(i);
                }
            } else {
                if (members.size() == 0) {
                    i_fields = new YapField[0];
                }
            }
            if(i_stream.i_handlers._diagnosticProcessor.enabled()){
                if(i_fields != null && i_fields.length == 0){
                    String name = getName();
                    if (name.indexOf("com.db4o.") != 0){
                        i_stream.i_handlers._diagnosticProcessor.onDiagnostic(
                            new DiagnosticMessage(name 
                                + " : This class does not contain any persistent fields.\n"
                                + "  Every class in the class hierarchy requires some overhead for the maintenance of a class index." 
                                + " Consider removing this class from the hierarchy, if it is not needed.")
                        );
                    }
                }
            }
            
        } else {
            if (i_fields == null) {
                i_fields = new YapField[0];
            }
        }
        setStateOK();
    }
    
    private boolean addTranslatorFields(YapStream a_stream) {
        
    	ObjectTranslator ot = getTranslator();
    	if (ot == null) {
    		return false;
    	}
    	
    	if (isNewTranslator(ot)) {
    		i_stream.setDirty(this);
    	}
        
        int fieldCount = 1;
        
        boolean versions = generateVersionNumbers() && ! ancestorHasVersionField();
        boolean uuids = generateUUIDs()  && ! ancestorHasUUIDField();
        
        if(versions){
            fieldCount = 2;
        }
        
        if(uuids){
            fieldCount = 3;
        }
    	
    	i_fields = new YapField[fieldCount];
        
        i_fields[0] = new YapFieldTranslator(this, ot);
        
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
            
            i_fields[1] = a_stream.i_handlers.i_indexes.i_fieldVersion;
        }
        
        if(uuids){
            i_fields[2] = a_stream.i_handlers.i_indexes.i_fieldUUID;
        }
        
    	setStateOK();
    	return true;
    }
    
    private ObjectTranslator getTranslator() {
    	return i_config == null
    		? null
    		: i_config.getTranslator();
    }

	private boolean isNewTranslator(ObjectTranslator ot) {
		return !hasFields()
		    || !ot.getClass().getName().equals(i_fields[0].getName());
	}

	private boolean hasFields() {
		return i_fields != null
		    && i_fields.length > 0;
	}

    void addToIndex(YapFile a_stream, Transaction a_trans, int a_id) {
        if (a_stream.maintainsIndices()) {
            addToIndex1(a_stream, a_trans, a_id);
        }
    }

    void addToIndex1(YapFile a_stream, Transaction a_trans, int a_id) {
        if (i_ancestor != null) {
            i_ancestor.addToIndex1(a_stream, a_trans, a_id);
        }
        if (hasIndex()) {
            if(Debug.useOldClassIndex){
                a_trans.addToClassIndex(getID(), a_id);
            }
            if(Debug.useBTrees){
                _index.add(a_trans, new Integer(a_id));
            }
        }
    }

    boolean allowsQueries() {
        return hasIndex();
    }

    public boolean canHold(ReflectClass claxx) {
        if (claxx == null) {
            return true;
        }
        if (_reflector != null) {
        	if(classReflector().isCollection()){
                return true;
            }
            return classReflector().isAssignableFrom(claxx);
        }
        return false;
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
            YapStream stream = a_trans.i_stream;
            if (a_activate) {
                if(isValueType()){
                    activateFields(a_trans, a_object, a_depth - 1);
                }else{
                    stream.stillToActivate(a_object, a_depth - 1);
                }
            } else {
                stream.stillToDeactivate(a_object, a_depth - 1, false);
            }
        }
    }

    void checkChanges() {
        if (stateOK()) {
            if (!bitIsTrue(YapConst.CHECKED_CHANGES)) {
                bitTrue(YapConst.CHECKED_CHANGES);
                if (i_ancestor != null) {
                    i_ancestor.checkChanges();
                    // Ancestor first, so the object length calculates
                    // correctly
                }
                if (_reflector != null) {
                    addMembers(i_stream);
                    if (!i_stream.isClient()) {
                        write(i_stream.getSystemTransaction());
                    }
                }
            }
        }
    }
    
    private void checkDb4oType() {
        ReflectClass claxx = classReflector();
        if (claxx == null){
            return;
        }
        if (i_stream.i_handlers.ICLASS_INTERNAL.isAssignableFrom(claxx)) {
            _internal = true;
        }
        if (i_stream.i_handlers.ICLASS_UNVERSIONED.isAssignableFrom(claxx)) {
            _unversioned = true;
        }
        if (i_stream.i_handlers.ICLASS_DB4OTYPEIMPL.isAssignableFrom(claxx)) {
            try {
                i_db4oType = (Db4oTypeImpl)claxx.newInstance();
            } catch (Exception e) {
            }
        }
    }

    public void checkUpdateDepth(YapWriter a_bytes) {
        int depth = a_bytes.getUpdateDepth();
        Config4Class config = configOrAncestorConfig();
        if (depth == YapConst.UNSPECIFIED) {
            depth = checkUpdateDepthUnspecified(a_bytes.getStream());
        }
        if (classReflector().isCollection() || (config != null && (config.cascadeOnDelete() == YapConst.YES || config.cascadeOnUpdate() == YapConst.YES))) {
            int depthBorder = reflector().collectionUpdateDepth(classReflector());
            if (depth>Integer.MIN_VALUE && depth < depthBorder) {
                depth = depthBorder;
            }
        }
        a_bytes.setUpdateDepth(depth - 1);
    }

    int checkUpdateDepthUnspecified(YapStream a_stream) {
        int depth = a_stream.i_config.updateDepth() + 1;
        if (i_config != null && i_config.updateDepth() != 0) {
            depth = i_config.updateDepth() + 1;
        }
        if (i_ancestor != null) {
            int ancestordepth = i_ancestor.checkUpdateDepthUnspecified(a_stream);
            if (ancestordepth > depth) {
                return ancestordepth;
            }
        }
        return depth;
    }

    public Object coerce(ReflectClass claxx, Object obj) {
        return canHold(claxx) ? obj : No4.INSTANCE;
    }

    void collectConstraints(
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
    
    final TreeInt collectFieldIDs(MarshallerFamily mf, ObjectHeaderAttributes attributes, TreeInt tree, YapWriter a_bytes, String name) {
        return mf._object.collectFieldIDs(tree, this, attributes, a_bytes, name);
    }

    final boolean configInstantiates(){
        return i_config != null && i_config.instantiates();
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

    public void copyValue(Object a_from, Object a_to) {
        // do nothing
    }
    
    private void createBTreeIndex(int btreeID){
        if(hasIndex()  && ! i_stream.isClient()){
            _index = ((YapFile)i_stream).createBTreeClassIndex(this, btreeID);
            _index.setRemoveListener(new Visitor4() {
                public void visit(Object obj) {
                    int id = ((Integer)obj).intValue();
                    YapObject yo = i_stream.getYapObject(id);
                    if (yo != null) {
                        i_stream.yapObjectGCd(yo);
                    }
                }
            });
        }
    }

    private boolean createConstructor(YapStream a_stream, String a_name) {
        
        ReflectClass claxx;
        try {
        	claxx = a_stream.reflector().forName(a_name);
        } catch (Throwable t) {
            claxx = null;
        }
        
        return createConstructor(a_stream,claxx , a_name, true);
    }

    private boolean createConstructor(YapStream a_stream, ReflectClass a_class, String a_name, boolean errMessages) {
        
        _reflector = a_class;
        
        _eventDispatcher = EventDispatcher.forClass(a_stream, a_class);
        
        if(! Deploy.csharp){
            if(a_class != null){
                _isEnum = Platform4.jdk().isEnum(reflector(), a_class);
            }
        }
        
        if(configInstantiates()){
            return true;
        }
        
        if(a_class != null){
            if(a_stream.i_handlers.ICLASS_TRANSIENTCLASS.isAssignableFrom(a_class)
            	|| Platform4.isTransient(a_class)) {
                a_class = null;
            }
        }
        if (a_class == null) {
            if(a_name == null || a_name.indexOf("com.db4o") != 0){
                if(errMessages){
                    a_stream.logMsg(23, a_name);
                }
            }
            setStateDead();
            return false;
        }
        
        if(a_stream.i_handlers.createConstructor(a_class, ! callConstructor())){
            return true;
        }
        
        setStateDead();
        if(errMessages){
            a_stream.logMsg(7, a_name);
        }
        
        if (a_stream.i_config.exceptionsOnNotStorable()) {
            throw new ObjectNotStorableException(a_class);
        }

        return false;
        
    }
    
    public void deactivate(Transaction a_trans, Object a_object, int a_depth) {
        if(dispatchEvent(a_trans.i_stream, a_object, EventDispatcher.CAN_DEACTIVATE)){
            deactivate1(a_trans, a_object, a_depth);
            dispatchEvent(a_trans.i_stream, a_object, EventDispatcher.DEACTIVATE);
        }
    }

    void deactivate1(Transaction a_trans, Object a_object, int a_depth) {
        
        for (int i = 0; i < i_fields.length; i++) {
            i_fields[i].deactivate(a_trans, a_object, a_depth);
        }
        if (i_ancestor != null) {
            i_ancestor.deactivate1(a_trans, a_object, a_depth);
        }
    }

    final void delete(YapWriter a_bytes, Object a_object) {
        ObjectHeader oh = new ObjectHeader(i_stream, this, a_bytes);
        delete1(oh._marshallerFamily, oh._headerAttributes, a_bytes, a_object);
    }

    private final void delete1(MarshallerFamily mf, ObjectHeaderAttributes attributes, YapWriter a_bytes, Object a_object) {
        removeFromIndex(a_bytes.getTransaction(), a_bytes.getID());
        deleteMembers(mf, attributes, a_bytes, a_bytes.getTransaction().i_stream.i_handlers.arrayType(a_object), false);
    }

    public void deleteEmbedded(MarshallerFamily mf, YapWriter a_bytes) {
        if (a_bytes.cascadeDeletes() > 0) {
            int id = a_bytes.readInt();
            if (id > 0) {
                deleteEmbedded1(mf, a_bytes, id);
            }
        } else {
            a_bytes.incrementOffset(linkLength());
        }
    }

    public void deleteEmbedded1(MarshallerFamily mf, YapWriter a_bytes, int a_id) {
        if (a_bytes.cascadeDeletes() > 0) {
        	
        	YapStream stream = a_bytes.getStream();
            
            // short-term reference to prevent WeakReference-gc to hit
            Object obj = stream.getByID2(a_bytes.getTransaction(), a_id);

            int cascade = a_bytes.cascadeDeletes() - 1;
            if (obj != null) {
                if (isCollection(obj)) {
                    cascade += reflector().collectionUpdateDepth(reflector().forObject(obj)) - 1;
                }
            }

            YapObject yo = stream.getYapObject(a_id);
            if (yo != null) {
                a_bytes.getStream().delete3(a_bytes.getTransaction(), yo, obj,cascade, false);
            }
        }
    }

    void deleteMembers(MarshallerFamily mf, ObjectHeaderAttributes attributes, YapWriter a_bytes, int a_type, boolean isUpdate) {
        try{
	        Config4Class config = configOrAncestorConfig();
	        if (config != null && (config.cascadeOnDelete() == YapConst.YES)) {
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

    public final boolean dispatchEvent(YapStream stream, Object obj, int message) {
        if (_eventDispatcher != null) {
            if(stream.dispatchsEvents()){
                return _eventDispatcher.dispatch(stream, obj, message);
            }
        }
        return true;
    }
    
    public final boolean equals(TypeHandler4 a_dataType) {
        return (this == a_dataType);
    }
    
    public final int fieldCount(){
        int count = i_fields.length;
        
        if(i_ancestor != null){
            count += i_ancestor.fieldCount();
        }
        
        return count;
    }

    // Scrolls offset in passed reader to the offset the passed field should
    // be read at.
    //
	// returns null if not successful or if the field value at this offset is null
    // returns MarshallerFamily from the object header if it is successful and
    //         if the value at this offset is not null
    final MarshallerFamily findOffset(YapReader a_bytes, YapField a_field) {
        if (a_bytes == null) {
            return null;
        }
        a_bytes._offset = 0;
        ObjectHeader oh = new ObjectHeader(i_stream, this, a_bytes);
        boolean res = oh.objectMarshaller().findOffset(this, oh._headerAttributes, a_bytes, a_field);
        if(! res){
            return null;
        }
        return oh._marshallerFamily;
    }

    void forEachYapField(Visitor4 visitor) {
        if (i_fields != null) {
            for (int i = 0; i < i_fields.length; i++) {
                visitor.visit(i_fields[i]);
            }
        }
        if (i_ancestor != null) {
            i_ancestor.forEachYapField(visitor);
        }
    }
    
    public static YapClass forObject(Transaction trans, Object obj, boolean allowCreation){
        return trans.i_stream.getYapClass(trans.reflector().forObject(obj), allowCreation);
    }
    
    private boolean generateUUIDs() {
        if(! generateVirtual()){
            return false;
        }
        int configValue = (i_config == null) ? 0 : i_config.generateUUIDs();
        return generate1(i_stream.bootRecord().i_generateUUIDs, configValue); 
    }

    private boolean generateVersionNumbers() {
        if(! generateVirtual()){
            return false;
        }
        int configValue = (i_config == null) ? 0 : i_config.generateVersionNumbers();
        return generate1(i_stream.bootRecord().i_generateVersionNumbers, configValue); 
    }
    
    private boolean generateVirtual(){
        if(_unversioned){
            return false;
        }
        if(_internal){
            return false;
        }
        if( ! (i_stream instanceof YapFile) ){
            return false;
        }
        return i_stream.bootRecord() != null; 
    }
    
    private boolean generate1(int bootRecordValue, int configValue) {
        if(bootRecordValue < 0) {
            return false;
        }
        if(configValue < 0) {
            return false;
        }
        if(bootRecordValue > 1) {
            return true;
        }
        return configValue > 0;
    }


    YapClass getAncestor() {
        return i_ancestor;
    }

    Object getComparableObject(Object forObject) {
        if (i_config != null) {
            if (i_config.queryAttributeProvider() != null) {
                return i_config.queryAttributeProvider().attribute(forObject);
            }
        }
        return forObject;
    }

    YapClass getHigherHierarchy(YapClass a_yapClass) {
        YapClass yc = getHigherHierarchy1(a_yapClass);
        if (yc != null) {
            return yc;
        }
        return a_yapClass.getHigherHierarchy1(this);
    }

    private YapClass getHigherHierarchy1(YapClass a_yapClass) {
        if (a_yapClass == this) {
            return this;
        }
        if (i_ancestor != null) {
            return i_ancestor.getHigherHierarchy1(a_yapClass);
        }
        return null;
    }

    YapClass getHigherOrCommonHierarchy(YapClass a_yapClass) {
        YapClass yc = getHigherHierarchy1(a_yapClass);
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
        return YapConst.YAPCLASS;
    }

    public long[] getIDs() {
        synchronized(i_stream.i_lock){
	        if (! stateOK()) {
                return new long[0];
            }
	        return getIDs(i_stream.getTransaction());
        }
    }

    public long[] getIDs(Transaction trans) {
        
        if (! stateOK()){
            return new long[0];
        }
        
        if( ! hasIndex() ){
            return new long[0];
        }
        
        final long[] ids;
        
        if(trans.i_stream.isClient()){
            YapClient stream = (YapClient)trans.i_stream;
            stream.writeMsg(Msg.GET_INTERNAL_IDS.getWriterForInt(trans, getID()));
            YapWriter reader = stream.expectedByteResponse(Msg.ID_LIST);
            int size = reader.readInt();
            ids = new long[size];
            for (int i = 0; i < size; i++) {
                ids[i] = reader.readInt();
            }
            return ids;
        }
        
        if(Debug.useOldClassIndex){
            Tree tree = getIndex().cloneForYapClass(trans, getID());
            if(tree == null){
                return new long[0];
            }
            ids = new long[tree.size()];
            final int[] inc = new int[] { 0 };
            tree.traverse(new Visitor4() {
                public void visit(Object obj) {
                    ids[inc[0]++] = ((TreeInt) obj)._key;
                }
            });
            return ids;
        }

        if(Debug.useBTrees){
            ids = new long[_index.size(trans)];
            final int[] count = new int[]{0};
            _index.traverseKeys(trans, new Visitor4() {
                public void visit(Object obj) {
                    int id = ((Integer)obj).intValue();
                    if(id > 0){
                        ids[count[0]] = id;
                        count[0] ++;
                    }
                }
            });
            return ids;
        }
        
        return new long[0];
    }

    boolean hasIndex() {
        return i_db4oType == null || i_db4oType.hasClassIndex();
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
        if(i_fields != null) {
            for (int i = 0; i < i_fields.length; i++) {
                if(i_fields[i] instanceof YapFieldUUID) {
                    return true;
                }
            }
        }
        return false;
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
        if(i_fields != null) {
            for (int i = 0; i < i_fields.length; i++) {
                if(i_fields[i] instanceof YapFieldVersion) {
                    return true;
                }
            }
        }
        return false;
    }
    
    BTree index(){
        if(! stateOK()){
            return null;
        }
        return _index;
    }

    ClassIndex getIndex() {
        if (stateOK() && i_index != null) {
            i_index.ensureActive();
            return i_index;
        }
        return null;
    }
    
    int indexEntryCount(Transaction ta){
        if(!stateOK()){
            return 0;
        }
        
        if(Debug.useBTrees){
            if(_index != null){
                return _index.size(ta);
            }
        }
        
        if(Debug.useOldClassIndex){
            if (i_index != null) {
                return i_index.entryCount(ta);
            }
        }
        
        return 0;
    }
    
    public Object indexEntryToObject(Transaction trans, Object indexEntry){
        if(indexEntry == null){
            return null;
        }
        int id = ((Integer)indexEntry).intValue();
        return getStream().getByID2(trans, id);
    }

    final Tree getIndex(Transaction a_trans) {
        
        if(!hasIndex()){
            return null;
        }
        
        if(Debug.useBTrees){
            
            // TODO: Index should work with BTrees only, no more conversion
            // to TreeInt should be necessary.
            
            TreeInt zero = new TreeInt(0);
            final Tree[] tree = new Tree[]{zero};
            _index.traverseKeys(a_trans, new Visitor4() {
                public void visit(Object obj) {
                    tree[0] = tree[0].add(new TreeInt(((Integer)obj).intValue()));
                }
            });
            tree[0] = tree[0].removeNode(zero);
            return tree[0];
        }
        
        if(Debug.useOldClassIndex){
            ClassIndex ci = getIndex();
            if (ci != null) {
                return ci.cloneForYapClass(a_trans, getID());
            }
        }
        return null;
    }

    final TreeInt getIndexRoot() {
        if (! hasIndex()) {
            return null;
        }
        ClassIndex ci = getIndex();
        if(ci == null){
            return null;
        }
        return (TreeInt)ci.getRoot();
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
        synchronized(i_stream.i_lock){
	        if(i_fields == null){
	            return null;
	        }
	        StoredField[] fields = new StoredField[i_fields.length];
	        System.arraycopy(i_fields, 0, fields, 0, i_fields.length);
	        return fields;
        }
    }

    YapStream getStream() {
        return i_stream;
    }

    public int getTypeID() {
        return YapConst.TYPE_CLASS;
    }

    public YapClass getYapClass(YapStream a_stream) {
        return this;
    }

    public YapField getYapField(final String name) {
        final YapField[] yf = new YapField[1];
        forEachYapField(new Visitor4() {
            public void visit(Object obj) {
                if (name.equals(((YapField)obj).getName())) {
                    yf[0] = (YapField)obj;
                }
            }
        });
        return yf[0];

    }
    
    public boolean hasFixedLength(){
        return true;
    }

    public boolean hasField(YapStream a_stream, String a_field) {
    	if(classReflector().isCollection()){
            return true;
        }
        return getYapField(a_field) != null;
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

    void incrementFieldsOffset1(YapReader a_bytes) {
        int length = Debug.atHome ? readFieldCountSodaAtHome(a_bytes) : readFieldCount(a_bytes);
        for (int i = 0; i < length; i++) {
            i_fields[i].incrementOffset(a_bytes);
        }
    }

    public Object comparableObject(Transaction a_trans, Object a_object) {
        return a_object;
    }
    
    boolean init(
        YapStream a_stream,
        YapClass a_ancestor,
        ReflectClass claxx,
        boolean errMessages
        ) {
        
        if(DTrace.enabled){
            DTrace.YAPCLASS_INIT.log(getID());
        }
        
        i_ancestor = a_ancestor;
        
        Config4Impl config = a_stream.i_config;
        String className = claxx.getName();		
		setConfig(config.configClass(className));
        
        if(! createConstructor(a_stream, claxx, className, false)){
            return false;
        }
        
        checkDb4oType();
        if (allowsQueries()) {
            if(Debug.useOldClassIndex){
                i_index = a_stream.createClassIndex(this);
            }
            if(Debug.useBTrees){
                createBTreeIndex(0);
            }
        }
        i_name = className;
        i_ancestor = a_ancestor;
        bitTrue(YapConst.CHECKED_CHANGES);
        
        return true;
    }
    
    void initConfigOnUp(Transaction systemTrans) {
        Config4Class extendedConfig=Platform4.extendConfiguration(_reflector, i_stream.configure(), i_config);
    	if(extendedConfig!=null) {
    		i_config=extendedConfig;
    	}
        if (i_config == null) {
            return;
        }
        if (! stateOK()) {
            return;
        }
        YapStream stream = systemTrans.i_stream; 
        stream.showInternalClasses(true);
        int[] metaClassID = new int[]{_metaClassID};
        if(i_config.initOnUp(systemTrans, metaClassID)){
            if(_metaClassID != metaClassID[0]){
                _metaClassID = metaClassID[0];
                setStateDirty();
                write(systemTrans);
            }
            if (i_fields != null) {
                for (int i = 0; i < i_fields.length; i++) {
                    i_fields[i].initConfigOnUp(systemTrans);
                }
            }
        }
        stream.showInternalClasses(false);
    }

    void initOnUp(Transaction systemTrans) {
        if (! stateOK()) {
            return;
        }
        initConfigOnUp(systemTrans);
        storeStaticFieldValues(systemTrans, false);
    }

    Object instantiate(YapObject a_yapObject, Object a_object, MarshallerFamily mf, ObjectHeaderAttributes attributes, YapWriter a_bytes, boolean a_addToIDTree) {
        
        // overridden in YapClassPrimitive
        // never called for primitive YapAny

        YapStream stream = a_bytes.getStream();
        Transaction trans = a_bytes.getTransaction();
        boolean create = (a_object == null);

        if (i_config != null) {
            a_bytes.setInstantiationDepth(
                i_config.adjustActivationDepth(a_bytes.getInstantiationDepth()));
        }

        boolean doFields =
            (a_bytes.getInstantiationDepth() > 0)
                || (i_config != null && (i_config.cascadeOnActivate() == YapConst.YES));

        if (create) {
            if (configInstantiates()) {
                int bytesOffset = a_bytes._offset;
                a_bytes.incrementOffset(YapConst.YAPINT_LENGTH);
                // Field length is always 1
                try {
                    a_object = i_config.instantiate(stream, i_fields[0].read(mf, a_bytes));
                } catch (Exception e) {
                    Messages.logErr(stream.i_config, 6, classReflector().getName(), e);
                    return null;
                }
                a_bytes._offset = bytesOffset;
            } else {
                if (_reflector == null) {
                    return null;
                }

                stream.instantiating(true);
                try {
                    a_object = _reflector.newInstance();
                } catch (NoSuchMethodError e) {
                    stream.logMsg(7, classReflector().getName());
                    stream.instantiating(false);
                    return null;
                } catch (Exception e) {
                    // TODO: be more helpful here
                    stream.instantiating(false);
                    return null;
                }
                stream.instantiating(false);

            }
            if (a_object instanceof TransactionAware) {
                ((TransactionAware)a_object).setTrans(a_bytes.getTransaction());
            }
            if (a_object instanceof Db4oTypeImpl) {
                ((Db4oTypeImpl)a_object).setYapObject(a_yapObject);
            }
            a_yapObject.setObjectWeak(stream, a_object);
            stream.hcTreeAdd(a_yapObject);
        } else {
            if(! stream.i_refreshInsteadOfActivate){
	            if (a_yapObject.isActive()) {
	                doFields = false;
	            }
            }
        }
        
        if(a_addToIDTree){
            a_yapObject.addToIDTree(stream);
        }
        
        if (doFields) {
            if(dispatchEvent(stream, a_object, EventDispatcher.CAN_ACTIVATE)){
	            a_yapObject.setStateClean();
	            instantiateFields(a_yapObject, a_object, mf, attributes, a_bytes);
	            dispatchEvent(stream, a_object, EventDispatcher.ACTIVATE);
            }else{
                if (create) {
                    a_yapObject.setStateDeactivated();
                }
            }
        } else {
            if (create) {
                a_yapObject.setStateDeactivated();
            } else {
                if (a_bytes.getInstantiationDepth() > 1) {
                    activateFields(trans, a_object, a_bytes.getInstantiationDepth() - 1);
                }
            }
        }
        return a_object;
    }

    Object instantiateTransient(YapObject a_yapObject, Object a_object, MarshallerFamily mf, ObjectHeaderAttributes attributes, YapWriter a_bytes) {

        // overridden in YapClassPrimitive
        // never called for primitive YapAny

        YapStream stream = a_bytes.getStream();

        if (configInstantiates()) {
            int bytesOffset = a_bytes._offset;
            a_bytes.incrementOffset(YapConst.YAPINT_LENGTH);
            // Field length is always 1
            try {
                a_object = i_config.instantiate(stream, i_fields[0].read(mf, a_bytes));
            } catch (Exception e) {
                Messages.logErr(stream.i_config, 6, classReflector().getName(), e);
                return null;
            }
            a_bytes._offset = bytesOffset;
        } else {
            if (_reflector == null) {
                return null;
            }
            stream.instantiating(true);
            try {
                a_object = _reflector.newInstance();
            } catch (NoSuchMethodError e) {
                stream.logMsg(7, classReflector().getName());
                stream.instantiating(false);
                return null;
            } catch (Exception e) {
                // TODO: be more helpful here
                stream.instantiating(false);
                return null;
            }
            stream.instantiating(false);
        }
        stream.peeked(a_yapObject.getID(), a_object);
        instantiateFields(a_yapObject, a_object, mf, attributes, a_bytes);
        return a_object;
    }

    void instantiateFields(YapObject a_yapObject, Object a_onObject, MarshallerFamily mf,ObjectHeaderAttributes attributes, YapWriter a_bytes) {
        mf._object.instantiateFields(this, attributes, a_yapObject, a_onObject, a_bytes);
    }

    public boolean indexNullHandling() {
        return true;
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
    
    public int isSecondClass(){
        return YapConst.NO;
    }

    /**
	 * no any, primitive, array or other tricks. overriden in YapClassAny and
	 * YapClassPrimitive
	 */
    boolean isStrongTyped() {
        return true;
    }
    
    boolean isValueType(){
        return Platform4.isValueType(classReflector());
    }
    
    public void calculateLengths(Transaction trans, ObjectHeaderAttributes header, boolean topLevel, Object obj, boolean withIndirection) {
        if(topLevel){
            header.addBaseLength(linkLength());
        }else{
            header.addPayLoadLength(linkLength());
        }
    }

    private String nameToWrite(){
        String name = i_name;
        if(i_config != null && i_config.writeAs() != null){
            return i_config.writeAs();
        }
        if(i_name == null){
            return "";
        }
        return i_name;
    }
    
    final boolean callConstructor() {
        i_dontCallConstructors = ! callConstructor1();
        return ! i_dontCallConstructors;
    }
    
    private final boolean callConstructor1() {
        int res = callConstructorSpecialized();
        if(res != YapConst.DEFAULT){
            return res == YapConst.YES;
        }
        return (i_stream.i_config.callConstructors() == YapConst.YES);
    }
    
    private final int callConstructorSpecialized(){
        if(i_config!= null){
            int res = i_config.callConstructor();
            if(res != YapConst.DEFAULT){
                return res;
            }
        }
        if(_isEnum){
            return YapConst.NO;
        }
        if(i_ancestor != null){
            return i_ancestor.callConstructorSpecialized();
        }
        return YapConst.DEFAULT;
    }

    public int ownLength() {
        int len =
            i_stream.stringIO().shortLength(nameToWrite())
                + YapConst.OBJECT_LENGTH
                + (YapConst.YAPINT_LENGTH * 2)
                + (YapConst.YAPID_LENGTH);
        
        if(Debug.useOldClassIndex){
            len += YapConst.YAPID_LENGTH;
        }
        
        if(Debug.useBTrees){
            len += YapConst.YAPID_LENGTH;
        }
        
        if (i_fields != null) {
            for (int i = 0; i < i_fields.length; i++) {
                len += i_fields[i].ownLength(i_stream);
            }
        }
        
        return len;
    }
    
	public ReflectClass primitiveClassReflector(){
		return null;
	}

    void purge() {
        if (i_index != null) {
            if (!i_index.isDirty()) {
                i_index.clear();
                i_index.setStateDeactivated();
            }
        }
        
        // TODO: may want to add manual purge to Btree
        //       indexes here
        
        
    }

    public Object read(MarshallerFamily mf, YapWriter a_bytes, boolean redirect) throws CorruptionException{
        try {
            int id = a_bytes.readInt();
            int depth = a_bytes.getInstantiationDepth() - 1;

            Transaction trans = a_bytes.getTransaction();
            YapStream stream = trans.i_stream;

            if (a_bytes.getUpdateDepth() == YapConst.TRANSIENT) {
                return stream.peekPersisted1(trans, id, depth);
            }
            
            if (isValueType()) {

                // for C# value types only:
                // they need to be instantiated fully before setting them
                // on the parent object because the set call modifies identity.
                
                // We also have to instantiate structs completely every time. 
                if(depth < 1){
                    depth = 1;
                }
                
                // TODO: Do we want value types in the ID tree?
                // Shouldn't we treat them like strings and update
                // them every time ???

                
                YapObject yo = stream.getYapObject(id);
                if (yo != null) {
                    Object obj = yo.getObject();
                    if(obj == null){
                        stream.yapObjectGCd(yo);
                    }else{
                        yo.activate(trans, obj, depth, false);
                        return yo.getObject();
                    }
                }
                    return new YapObject(id).read(
                        trans,
                        null,
                        null,
                        depth,
                        YapConst.ADD_TO_ID_TREE, false);
                
            } else {

                Object ret = stream.getByID2(trans, id);

                if (ret instanceof Db4oTypeImpl) {
                    depth = ((Db4oTypeImpl)ret).adjustReadDepth(depth);
                }

                // this is OK for primitive YapAnys. They will not be added
                // to the list, since they will not be found in the ID tree.
                stream.stillToActivate(ret, depth);

                return ret;
            }

        } catch (Exception e) {
        }
        return null;
    }
    
    public Object readQuery(Transaction a_trans, MarshallerFamily mf, boolean withRedirection, YapReader a_reader, boolean a_toArray) throws CorruptionException{
        try {
            return a_trans.i_stream.getByID2(a_trans, a_reader.readInt());
        } catch (Exception e) {
            if (Debug.atHome) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public TypeHandler4 readArrayHandler(Transaction a_trans, MarshallerFamily mf, YapReader[] a_bytes) {
        if (isArray()) {
            return this;
        }
        return null;
    }

    public TypeHandler4 readArrayHandler1(YapReader[] a_bytes) {
        if(DTrace.enabled){
            if(a_bytes[0] instanceof YapWriter){
                DTrace.READ_ARRAY_WRAPPER.log(((YapWriter)a_bytes[0]).getID());
            }
        }
        if (isArray()) {
            if (Platform4.isCollectionTranslator(this.i_config)) {
                a_bytes[0].incrementOffset(YapConst.YAPINT_LENGTH);
                return new YapArray(i_stream, null, false);
            }
            incrementFieldsOffset1(a_bytes[0]);
            if (i_ancestor != null) {
                return i_ancestor.readArrayHandler1(a_bytes);
            }
        }
        return null;
    }
    
    public QCandidate readSubCandidate(MarshallerFamily mf, YapReader reader, QCandidates candidates, boolean withIndirection) {
        int id = reader.readInt();
        if(id == 0){
            return null;
        }
        return new QCandidate(candidates, null, id, true);
    } 

    public void readCandidates(MarshallerFamily mf, final YapReader a_bytes, final QCandidates a_candidates) {
        int id = 0;

        int offset = a_bytes._offset;
        try {
            id = a_bytes.readInt();
        } catch (Exception e) {
        }
        a_bytes._offset = offset;

        if (id != 0) {
            final Transaction trans = a_candidates.i_trans;
            Object obj = trans.i_stream.getByID1(trans, id);
            if (obj != null) {

                a_candidates.i_trans.i_stream.activate1(trans, obj, 2);
                Platform4.forEachCollectionElement(obj, new Visitor4() {
                    public void visit(Object elem) {
                        a_candidates.addByIdentity(new QCandidate(a_candidates, elem, (int)trans.i_stream.getID(elem), true));
                    }
                });
            }

        }
    }

    public final int readFieldCount(YapReader a_bytes) {
        int count = a_bytes.readInt();
        if (count > i_fields.length) {
            if (Debug.atHome) {
                System.out.println(
                    "YapClass.readFieldCount "
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

    public int readFieldCountSodaAtHome(YapReader a_bytes) {
        if (Debug.atHome) {
            int count = a_bytes.readInt();
            if (count > i_fields.length) {
                return i_fields.length;
            }
            return count;
        }
        return 0;
    }

    public Object readIndexEntry(YapReader a_reader) {
        return new Integer(a_reader.readInt());
    }
    
    public Object readIndexEntry(MarshallerFamily mf, YapWriter a_writer) throws CorruptionException{
        return readIndexEntry(a_writer);
    }

    byte[] readName(Transaction a_trans) {
        i_reader = a_trans.i_stream.readReaderByID(a_trans, getID());
        if (i_reader != null) {
            return readName1(a_trans, i_reader);
        }
        return null;
    }

    byte[] readName1(Transaction a_trans, YapReader a_reader) {
        i_reader = a_reader;
        try {
            if (Deploy.debug) {
                a_reader.readBegin(getIdentifier());
            }
            
            int len = a_reader.readInt();

            len = len * a_trans.i_stream.stringIO().bytesPerChar();

            i_nameBytes = new byte[len];
            System.arraycopy(a_reader._buffer, a_reader._offset, i_nameBytes, 0, len);
            
            if(Deploy.csharp){
                i_nameBytes  = Platform4.updateClassName(i_nameBytes);
            }

            a_reader.incrementOffset(len);
            _metaClassID = a_reader.readInt();

            setStateUnread();

            bitFalse(YapConst.CHECKED_CHANGES);
            bitFalse(YapConst.STATIC_FIELDS_STORED);

            return i_nameBytes;

        } catch (Throwable t) {
            setStateDead();
            if (Debug.atHome) {
                t.printStackTrace();
            }
        }
        return null;
    }
    
    void readVirtualAttributes(Transaction a_trans, YapObject a_yapObject) {
        int id = a_yapObject.getID();
        YapStream stream = a_trans.i_stream;
        YapReader reader = stream.readReaderByID(a_trans, id);
        ObjectHeader oh = new ObjectHeader(stream, this, reader);
        oh.objectMarshaller().readVirtualAttributes(a_trans, this, a_yapObject, oh._headerAttributes, reader);
    }
    
	GenericReflector reflector() {
		return i_stream.reflector();
	}
    
    public void rename(String newName){
        if (!i_stream.isClient()) {
            int tempState = i_state;
            setStateOK();
            i_name = newName;
            setStateDirty();
            write(i_stream.getSystemTransaction());
            i_state = tempState;
        }else{
            Exceptions4.throwRuntimeException(58);
        }
    }

    void createConfigAndConstructor(
        Hashtable4 a_byteHashTable,
        YapStream a_stream,
        ReflectClass a_class) {
        if (a_class == null) {
            if (i_nameBytes != null) {
                i_name = a_stream.stringIO().read(i_nameBytes);
            }
        } else {
            i_name = a_class.getName();
        }
        setConfig(i_stream.i_config.configClass(i_name));
        if (a_class == null) {
            createConstructor(a_stream, i_name);
        } else {
            createConstructor(a_stream, a_class, i_name, true);
        }
        if (i_nameBytes != null) {
            a_byteHashTable.remove(i_nameBytes);
            i_nameBytes = null;
        }
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
    
    void forceRead(){
        if(i_reader != null && bitIsFalse(YapConst.READING)){
	        bitTrue(YapConst.READING);
	        i_ancestor = i_stream.getYapClass(i_reader.readInt());
	        
	        if(i_dontCallConstructors){
		        // The logic further down checks the ancestor YapClass, whether
	            // or not it is allowed, not to call constructors. The ancestor
	            // YapClass may possibly have not been loaded yet.
		        createConstructor(i_stream, classReflector(), i_name, true);
	        }
	        
	        checkDb4oType();
            
            if(Debug.useOldClassIndex){
                int classIndexId = i_reader.readInt();
                if (hasIndex()) {
                    i_index = i_stream.createClassIndex(this);
                    if (classIndexId > 0) {
                        i_index.setID(classIndexId);
                    }
                    i_index.setStateDeactivated();
                }
            }
            
            if(Debug.useBTrees){
                int indexId = i_reader.readInt();
                if(! i_stream.isClient()  && hasIndex()  && _index == null){
                    YapFile yf = (YapFile)i_stream;
                    if(indexId < 0){
                        createBTreeIndex(- indexId);
                    }else{
                        createBTreeIndex(0);
                        new ClassIndexesToBTrees().convert(yf, indexId, _index);
                        yf.setDirty(this);
                    }
                }
            }
            
	        i_fields = new YapField[i_reader.readInt()];
	        for (int i = 0; i < i_fields.length; i++) {
	            i_fields[i] = new YapField(this);
	            i_fields[i].setArrayPosition(i);
	        }
	        for (int i = 0; i < i_fields.length; i++) {
	            i_fields[i] = i_fields[i].readThis(i_stream, i_reader);
	        }
	        for (int i = 0; i < i_fields.length; i++) {
	            i_fields[i].loadHandler(i_stream);
	        }
	        i_nameBytes = null;
	        i_reader = null;
	        bitFalse(YapConst.READING);
        }
    }

    public boolean readArray(Object array, YapWriter reader) {
        return false;
    }

    public void readThis(Transaction a_trans, YapReader a_reader) {
        throw Exceptions4.virtualException();
    }

    void refresh() {
        if (!stateUnread()) {
            createConstructor(i_stream, i_name);
            bitFalse(YapConst.CHECKED_CHANGES);
            checkChanges();
            if (i_fields != null) {
                for (int i = 0; i < i_fields.length; i++) {
                    i_fields[i].refresh();
                }
            }
        }
    }

    void removeFromIndex(Transaction ta, int id) {
        if (hasIndex()) {
            if(Debug.useOldClassIndex){
                ta.removeFromClassIndex(getID(), id);
            }
            if(Debug.useBTrees){
                _index.remove(ta, new Integer(id));
            }
        }
        if (i_ancestor != null) {
            i_ancestor.removeFromIndex(ta, id);
        }
    }

    boolean renameField(String a_from, String a_to) {
        boolean renamed = false;
        for (int i = 0; i < i_fields.length; i++) {
            if (i_fields[i].getName().equals(a_to)) {
                i_stream.logMsg(9, "class:" + getName() + " field:" + a_to);
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

    private final void setStateDead() {
        bitTrue(YapConst.DEAD);
        bitFalse(YapConst.CONTINUE);
    }

    private final void setStateUnread() {
        bitFalse(YapConst.DEAD);
        bitTrue(YapConst.CONTINUE);
    }

    private final void setStateOK() {
        bitFalse(YapConst.DEAD);
        bitFalse(YapConst.CONTINUE);
    }
    
    boolean stateDead(){
        return bitIsTrue(YapConst.DEAD);
    }

    private final boolean stateOK() {
        return bitIsFalse(YapConst.CONTINUE)
            && bitIsFalse(YapConst.DEAD)
            && bitIsFalse(YapConst.READING);
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
        return bitIsTrue(YapConst.CONTINUE)
            && bitIsFalse(YapConst.DEAD)
            && bitIsFalse(YapConst.READING);
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
        synchronized(i_stream.i_lock){
            
            YapClass yc = i_stream.getYapClass(i_stream.i_config.reflectorFor(a_type), false); 
    		
	        if(i_fields != null){
	            for (int i = 0; i < i_fields.length; i++) {
	                if(i_fields[i].getName().equals(a_name)){
	                    if(yc == null || yc == i_fields[i].getFieldYapClass(i_stream)){
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
        if (!bitIsTrue(YapConst.STATIC_FIELDS_STORED) || force) {
            bitTrue(YapConst.STATIC_FIELDS_STORED);
            boolean store = 
                (i_config != null && i_config.staticFieldValuesArePersisted())
            || Platform4.storeStaticFieldValues(trans.reflector(), classReflector()); 
            
            if (store) {
                YapStream stream = trans.i_stream;
                stream.showInternalClasses(true);
                Query q = stream.querySharpenBug(trans);
                q.constrain(YapConst.CLASS_STATICCLASS);
                q.descend("name").constrain(i_name);
                StaticClass sc = new StaticClass();
                sc.name = i_name;
                ObjectSet os = q.execute();
                StaticField[] oldFields = null;
                if (os.size() > 0) {
                    sc = (StaticClass)os.next();
                    stream.activate1(trans, sc, 4);
                    oldFields = sc.fields;
                }
                ReflectField[] fields = classReflector().getDeclaredFields();

                Collection4 newFields = new Collection4();

                for (int i = 0; i < fields.length; i++) {
                    if (fields[i].isStatic()) {
                        fields[i].setAccessible();
                        String fieldName = fields[i].getName();
                        Object value = fields[i].get(null);
                        boolean handled = false;
                        if (oldFields != null) {
                            for (int j = 0; j < oldFields.length; j++) {
                                if (fieldName.equals(oldFields[j].name)) {
                                    if (oldFields[j].value != null
                                        && value != null
                                        && oldFields[j].value.getClass() == value.getClass()) {
                                        long id = stream.getID1(trans, oldFields[j].value);
                                        if (id > 0) {
                                            if (oldFields[j].value != value) {
                                                
                                                // This is the clue:
                                                // Bind the current static member to it's old database identity,
                                                // so constants and enums will work with '=='
                                                stream.bind1(trans, value, id);
                                                
                                                // This may produce unwanted side effects if the static field object
                                                // was modified in the current session. TODO:Add documentation case.
                                                
                                                stream.refresh(value, Integer.MAX_VALUE);
                                                
                                                oldFields[j].value = value;
                                            }
                                            handled = true;
                                        }
                                    }
                                    if (!handled) {
                                        if(value == null){
                                            try{
                                                fields[i].set(null, oldFields[j].value);
                                            }catch(Exception ex){
                                                // fail silently
                                            }
                                            
                                        }else{
                                            oldFields[j].value = value;
                                            if (!stream.isClient()) {
                                                stream.setInternal(trans, oldFields[j], true);
                                            }
                                        }
                                    }
                                    newFields.add(oldFields[j]);
                                    handled = true;
                                    break;
                                }
                            }
                        }
                        if (!handled) {
                            newFields.add(new StaticField(fieldName, value));
                        }
                    }
                }
                if (newFields.size() > 0) {
                    sc.fields = new StaticField[newFields.size()];
                    newFields.toArray(sc.fields);
                    if (!stream.isClient()) {
                        stream.setInternal(trans, sc, true);
                    }
                }
                stream.showInternalClasses(false);
            }
        }
    }

    public boolean supportsIndex() {
        return true;
    }

    public String toString() {
        return i_name;
    }
    
    public boolean writeArray(Object array, YapWriter reader) {
        return false;
    }

    public boolean writeObjectBegin() {
        if (!stateOK()) {
            return false;
        }
        return super.writeObjectBegin();
    }

    public void writeIndexEntry(YapReader a_writer, Object a_object) {
        
        if(a_object == null){
            a_writer.writeInt(0);
            return;
        }
        
        a_writer.writeInt(((Integer)a_object).intValue());
    }
    
    public Object writeNew(MarshallerFamily mf, Object a_object, boolean topLevel, YapWriter a_bytes, boolean withIndirection, boolean restoreLinkOffset) {
        if (a_object == null) {
            a_bytes.writeInt(0);
            return new Integer(0);
        }

		int id = a_bytes.getStream().setInternal(
                    a_bytes.getTransaction(),
                    a_object,
                    a_bytes.getUpdateDepth(), true);
        
        a_bytes.writeInt(id);
		return new Integer(id);
    }

    public final void writeThis(Transaction trans, YapReader a_writer) {
        
        a_writer.writeShortString(trans, nameToWrite());
        a_writer.writeInt(_metaClassID);
        
        a_writer.writeIDOf(trans, i_ancestor);
        
        if(Debug.useOldClassIndex){
            a_writer.writeIDOf(trans, i_index);
        }
        
        if(Debug.useBTrees){
            if(_index == null){
                a_writer.writeInt(0);
            }else{
                _index.write(trans);
                a_writer.writeInt(- _index.getID());
            }
        }
        
        if (i_fields == null) {
            a_writer.writeInt(0);
        } else {
            a_writer.writeInt(i_fields.length);
            for (int i = 0; i < i_fields.length; i++) {
                i_fields[i].writeThis(trans, a_writer, this);
            }
        }
        
    }

    // Comparison_______________________

    private ReflectClass i_compareTo;
    
	public void prepareComparison(Transaction a_trans, Object obj) {
	    prepareComparison(obj);
	}

    public YapComparable prepareComparison(Object obj) {
        if (obj != null) {
            if(obj instanceof Integer){
                i_lastID = ((Integer)obj).intValue();
            }else{
                i_lastID = (int)i_stream.getID(obj);
            }
            i_compareTo = reflector().forObject(obj);
        } else {
            i_lastID = 0;
            i_compareTo = null;
        }
        return this;
    }
    
    public Object current(){
        if(i_compareTo == null){
            return null;
        }
        return new Integer(i_lastID);
    }

    public int compareTo(Object a_obj) {
        if(a_obj instanceof Integer){
            return ((Integer)a_obj).intValue() - i_lastID;
        }
        if( (a_obj == null) && (i_compareTo == null)){
            return 0;
        }
        return -1;
    }
    
    public boolean isEqual(Object obj) {
        if (obj == null) {
            return i_compareTo == null;
        }
        return i_compareTo.isAssignableFrom(reflector().forObject(obj));
    }

    public boolean isGreater(Object obj) {
        return false;
    }

    public boolean isSmaller(Object obj) {
        return false;
    }

    public String toString(MarshallerFamily mf, YapWriter writer, YapObject yapObject, int depth, int maxDepth) throws CorruptionException {
        int length = readFieldCount(writer);
        String str = "";
        for (int i = 0; i < length; i++) {
            str += i_fields[i].toString(mf, writer, yapObject, depth + 1, maxDepth);
        }
        if (i_ancestor != null) {
            str+= i_ancestor.toString(mf, writer, yapObject, depth, maxDepth);
        }
        return str;

    }





}
