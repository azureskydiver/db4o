/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.config.Configuration;
import com.db4o.config.Entry;
import com.db4o.config.QueryEvaluationMode;
import com.db4o.ext.Db4oDatabase;
import com.db4o.ext.Db4oException;
import com.db4o.ext.Db4oUUID;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.ext.MemoryFile;
import com.db4o.ext.ObjectInfo;
import com.db4o.ext.ObjectNotStorableException;
import com.db4o.ext.StoredClass;
import com.db4o.ext.SystemInfo;
import com.db4o.foundation.IntIdGenerator;
import com.db4o.foundation.Iterator4;
import com.db4o.foundation.Iterator4Impl;
import com.db4o.foundation.List4;
import com.db4o.foundation.PersistentTimeStampIdGenerator;
import com.db4o.foundation.Tree;
import com.db4o.foundation.Visitor4;
import com.db4o.internal.callbacks.*;
import com.db4o.internal.cs.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.query.*;
import com.db4o.internal.query.processor.*;
import com.db4o.internal.query.result.*;
import com.db4o.internal.replication.*;
import com.db4o.query.Predicate;
import com.db4o.query.Query;
import com.db4o.query.QueryComparator;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.generic.GenericReflector;
import com.db4o.replication.ReplicationConflictHandler;
import com.db4o.replication.ReplicationProcess;
import com.db4o.types.Db4oCollections;
import com.db4o.types.Db4oType;
import com.db4o.types.SecondClass;
import com.db4o.types.TransientClass;


/**
 * NOTE: This is just a 'partial' base class to allow for variant implementations
 * in db4oj and db4ojdk1.2. It assumes that itself is an instance of YapStream
 * and should never be used explicitly.
 * 
 * @exclude
 * @sharpen.partial
 */
public abstract class PartialObjectContainer implements TransientClass, Internal4, ObjectContainerSpec {

    private boolean         i_amDuringFatalExit   = false;

    // Collection of all classes
    // if (i_classCollection == null) the engine is down.
    protected ClassMetadataRepository      _classCollection;
    
    protected ClassInfoHelper _classMetaHelper = new ClassInfoHelper();

    // the Configuration context for this ObjectContainer
    protected Config4Impl             i_config;

    // Counts the number of toplevel calls into YapStream
    private int           _stackDepth;

    // Tree of all YapObject references, sorted by IdentityHashCode
    private ObjectReference       i_hcTree;

    // Tree of all YapObject references, sorted by ID
    private ObjectReference       i_idTree;
    private Tree            i_justPeeked;

    public final Object            i_lock;

    // currently used to resolve self-linking concurrency problems
    // in cylic links, stores only YapClass objects
    private List4           i_needsUpdate;

    //  the parent ObjectContainer for YapObjectCarrier or this for all
    //  others. Allows identifying the responsible Objectcontainer for IDs
    final ObjectContainerBase         i_parent;

    //  allowed adding refresh with little code changes.
    boolean                 i_refreshInsteadOfActivate;

    // a value greater than 0 indicates class implementing the
    // "Internal" interface are visible in queries and can
    // be used.
    int                     i_showInternalClasses = 0;
    
    private List4           i_stillToActivate;
    private List4           i_stillToDeactivate;

    private List4           i_stillToSet;

    // used for YapClass and YapClassCollection
    // may be parent or equal to i_trans
    protected Transaction             i_systemTrans;

    // used for Objects
    protected Transaction             i_trans;

    private boolean         i_instantiating;

    // all the per-YapStream references that we don't
    // want created in YapobjectCarrier
    public HandlerRegistry             i_handlers;

    // One of three constants in ReplicationHandler: NONE, OLD, NEW
    // Detailed replication variables are stored in i_handlers.
    // Call state has to be maintained here, so YapObjectCarrier (who shares i_handlers) does
    // not accidentally think it operates in a replication call. 
    int                 _replicationCallState;  

    // weak reference management
    WeakReferenceCollector           i_references;

	private NativeQueryHandler _nativeQueryHandler;
    
	private final ObjectContainerBase _this;

	private Callbacks _callbacks = new com.db4o.internal.callbacks.NullCallbacks();
    
    protected final PersistentTimeStampIdGenerator _timeStampIdGenerator = new PersistentTimeStampIdGenerator();
    
    private int _topLevelCallId = 1;
    
    private IntIdGenerator _topLevelCallIdGenerator = new IntIdGenerator();

    protected PartialObjectContainer(Configuration config,ObjectContainerBase a_parent) {
    	_this = cast(this);
        i_parent = a_parent == null ? _this : a_parent;
        i_lock = a_parent == null ? new Object() : a_parent.i_lock;
        initializeTransactions();
        initialize1(config);
    }

    public void activate(Object a_activate, int a_depth) {
        synchronized (i_lock) {
        	activate1(null, a_activate, a_depth);
        }
    }

    public final void activate1(Transaction ta, Object a_activate) {
        activate1(ta, a_activate, configImpl().activationDepth());
    }

    public final void activate1(Transaction ta, Object a_activate, int a_depth) {
        activate2(checkTransaction(ta), a_activate, a_depth);
    }

    final void activate2(Transaction ta, Object a_activate, int a_depth) {
    	beginTopLevelCall();
        try {
            stillToActivate(a_activate, a_depth);
            activate3CheckStill(ta);
        } catch (Throwable t) {
            fatalException(t);
    	}finally{
    		endTopLevelCall();
    	}
    }
    
    final void activate3CheckStill(Transaction ta){
        while (i_stillToActivate != null) {

            // TODO: Optimize!  A lightweight int array would be faster.

            Iterator4 i = new Iterator4Impl(i_stillToActivate);
            i_stillToActivate = null;

            while (i.moveNext()) {
                ObjectReference yo = (ObjectReference) i.current();
                
                i.moveNext();
                int depth = ((Integer) i.current()).intValue();
                
                Object obj = yo.getObject();
                if (obj == null) {
                    removeReference(yo);
                } else {
                    yo.activate1(ta, obj, depth, i_refreshInsteadOfActivate);
                }
            }
        }
    }
    
    public int alignToBlockSize(int length){
        return blocksFor(length) * blockSize();
    }

    public void bind(Object obj, long id) {
        synchronized (i_lock) {
            bind1(null, obj, id);
        }
    }

    /** TODO: This is not transactional yet. */
    public final void bind1(Transaction ta, Object obj, long id) {
        
        if(DTrace.enabled){
            DTrace.BIND.log(id, " ihc " + System.identityHashCode(obj));
        }
        
        ta = checkTransaction(ta);
        int intID = (int) id;
        if (obj != null) {
            Object oldObject = getByID(id);
            if (oldObject != null) {
                ObjectReference yo = getYapObject(intID);
                if (yo != null) {
                    if (ta.reflector().forObject(obj) == yo.getYapClass().classReflector()) {
                        bind2(yo, obj);
                    } else {
                        throw new RuntimeException(Messages.get(57));
                    }
                }
            }
        }
    }
    
    public final void bind2(ObjectReference a_yapObject, Object obj){
        int id = a_yapObject.getID();
        removeReference(a_yapObject);
        a_yapObject = new ObjectReference(getYapClass(reflector().forObject(obj)),
            id);
        a_yapObject.setObjectWeak(_this, obj);
        a_yapObject.setStateDirty();
        addToReferenceSystem(a_yapObject);
    }
    
    public byte blockSize() {
        return 1;
    }

    public int blocksFor(long bytes) {
        int blockLen = blockSize();
        int result = (int)(bytes / blockLen);
        if (bytes % blockLen != 0) result++;
        return result;
    }
    
    private final boolean breakDeleteForEnum(ObjectReference reference, boolean userCall){
        if(Deploy.csharp){
            return false;
        }
        if(userCall){
            return false;
        }
        if(reference == null){
            return false;
        }
        return Platform4.jdk().isEnum(reflector(), reference.getYapClass().classReflector());
    }

    boolean canUpdate() {
        return true;
    }

    public final void checkClosed() {
        if (_classCollection == null) {
            Exceptions4.throwRuntimeException(20, toString());
        }
    }

    final void checkNeededUpdates() {
        if (i_needsUpdate != null) {
            Iterator4 i = new Iterator4Impl(i_needsUpdate);
            while (i.moveNext()) {
                ClassMetadata yapClass = (ClassMetadata) i.current();
                yapClass.setStateDirty();
                yapClass.write(i_systemTrans);
            }
            i_needsUpdate = null;
        }
    }

    public final Transaction checkTransaction(Transaction ta) {
        checkClosed();
        if (ta != null) {
            return ta;
        }
        return getTransaction();
    }

    public boolean close() {
		synchronized (i_lock) {
			boolean ret = close1();
			return ret;
		}
	}

    final boolean close1() {
        // this is set to null in close2 and is therefore our check for down.
        if (_classCollection == null) {
            return true;
        }
        Platform4.preClose(_this);
        checkNeededUpdates();
        if (stateMessages()) {
            logMsg(2, toString());
        }
        boolean closeResult = close2();
        return closeResult;
    }

    protected boolean close2() {
    	stopSession();
        i_hcTree = null;
        i_idTree = null;
        i_systemTrans = null;
        i_trans = null;
        if (stateMessages()) {
            logMsg(3, toString());
        }
        if(DTrace.enabled){
            DTrace.CLOSE.log();
        }
        return true;
    }

    public Db4oCollections collections() {
        synchronized (i_lock) {
            if (i_handlers.i_collections == null) {
                i_handlers.i_collections = Platform4.collections(this);
            }
            return i_handlers.i_collections;
        }
    }

    public void commit() {
    	if(i_config.isReadOnly()) {
    		// TODO: throws ReadOnlyException after exception handling.
    		return;
    	}
        synchronized (i_lock) {
            if(DTrace.enabled){
                DTrace.COMMIT.log();
            }
            beginTopLevelCall();
            try{
            	commit1();
            }finally{
            	endTopLevelCall();
            }
        }
    }

    public abstract void commit1();

    public Configuration configure() {
        return configImpl();
    }
    
    public Config4Impl config(){
        return configImpl();
    }
    
    public abstract int converterVersion();

    public abstract AbstractQueryResult newQueryResult(Transaction trans, QueryEvaluationMode mode);

    protected void createStringIO(byte encoding) {
    	setStringIo(LatinStringIO.forEncoding(encoding));
    }

    final protected void initializeTransactions() {
        i_systemTrans = newTransaction(null);
        i_trans = newTransaction();
    }

	public abstract Transaction newTransaction(Transaction parentTransaction);
	
	public Transaction newTransaction() {
		return newTransaction(i_systemTrans);
	}

    public abstract long currentVersion();
    
    public boolean createYapClass(ClassMetadata a_yapClass, ReflectClass a_class, ClassMetadata a_superYapClass) {
        return a_yapClass.init(_this, a_superYapClass, a_class);
    }

    /**
     * allows special handling for all Db4oType objects.
     * Redirected here from #set() so only instanceof check is necessary
     * in the #set() method. 
     * @return object if handled here and #set() should not continue processing
     */
    public Db4oType db4oTypeStored(Transaction a_trans, Object a_object) {
        if (a_object instanceof Db4oDatabase) {
            Db4oDatabase database = (Db4oDatabase) a_object;
            if (getYapObject(a_object) != null) {
                return database;
            }
            showInternalClasses(true);
            Db4oDatabase res = database.query(a_trans);
            showInternalClasses(false);
            return res;
        }
        return null;
    }

    public void deactivate(Object a_deactivate, int a_depth) {
        synchronized (i_lock) {
        	beginTopLevelCall();
        	try{
        		deactivate1(a_deactivate, a_depth);
        	}catch (Throwable t) {
        		fatalException(t);
        	}finally{
        		endTopLevelCall();
        	}
        }
    }

    private final void deactivate1(Object a_activate, int a_depth) {
        stillToDeactivate(a_activate, a_depth, true);
        while (i_stillToDeactivate != null) {
            Iterator4 i = new Iterator4Impl(i_stillToDeactivate);
            i_stillToDeactivate = null;
            while (i.moveNext()) {
                ObjectReference currentObject = (ObjectReference) i.current();
                
                i.moveNext();
				Integer currentInteger = ((Integer) i.current());
				
				currentObject.deactivate(i_trans, currentInteger.intValue());
            }
        }
    }

    public void delete(Object a_object) {
    	generateCallIDOnTopLevel();
        delete(null, a_object);
    }
    
    public void delete(Transaction trans, Object obj) {
    	if(i_config.isReadOnly()) {
    		// TODO: throws ReadOnlyException after exception handling.
    		return;
    	}
        synchronized (i_lock) {
        	trans = checkTransaction(trans);
            delete1(trans, obj, true);
            trans.processDeletes();
        }
    }

    public final void delete1(Transaction trans, Object obj, boolean userCall) {
        if (obj == null) {
        	return;
        }
        ObjectReference ref = getYapObject(obj);
        if(ref == null){
        	return;
        }
        
        if (Deploy.debug) {
            delete2(trans, ref, obj, 0, userCall);
            return;
        } 
        
        try {
        	delete2(trans, ref, obj, 0, userCall);
        } catch (Throwable t) {
            fatalException(t);
        }
    }
    
    public final void delete2(Transaction trans, ObjectReference ref, Object obj, int cascade, boolean userCall) {
        
        // This check is performed twice, here and in delete3, intentionally.
        if(breakDeleteForEnum(ref, userCall)){
            return;
        }
        
        if(obj instanceof SecondClass){
        	if(! flagForDelete(ref)){
        		return;
        	}
            delete3(trans, ref, cascade, userCall);
            return;
        }
        
        trans.delete(ref, ref.getID(), cascade);
    }

    final void delete3(Transaction trans, ObjectReference ref, int cascade, boolean userCall) {
    	
        // The passed reference can be null, when calling from Transaction.
        if(ref == null  || ! ref.beginProcessing()){
        	return;
        }
                
        // This check is performed twice, here and in delete2, intentionally.
        if(breakDeleteForEnum(ref, userCall)){
        	ref.endProcessing();
            return;
        }
        
        if(! ref.isFlaggedForDelete()){
        	ref.endProcessing();
        	return;
        }
        
        ClassMetadata yc = ref.getYapClass();
        Object obj = ref.getObject();
        
        // We have to end processing temporarily here, otherwise the can delete callback
        // can't do anything at all with this object.
        
        ref.endProcessing();
        
        if (!objectCanDelete(yc, obj)) {
            return;
        }
        
        ref.beginProcessing();

        if(DTrace.enabled){
            DTrace.DELETE.log(ref.getID());
        }
        
        if(delete4(trans, ref, cascade, userCall)){
        	objectOnDelete(yc, obj);
            if (configImpl().messageLevel() > Const4.STATE) {
                message("" + ref.getID() + " delete " + ref.getYapClass().getName());
            }
        }
        
        ref.endProcessing();
    }
    
	private boolean objectCanDelete(ClassMetadata yc, Object obj) {
		return _this.callbacks().objectCanDelete(obj)
			&& yc.dispatchEvent(_this, obj, EventDispatcher.CAN_DELETE);
	}
	
	private void objectOnDelete(ClassMetadata yc, Object obj) {
		_this.callbacks().objectOnDelete(obj);
		yc.dispatchEvent(_this, obj, EventDispatcher.DELETE);
	}
	
    public abstract boolean delete4(Transaction ta, ObjectReference yapObject, int a_cascade, boolean userCall);
    
    public Object descend(Object obj, String[] path){
        synchronized (i_lock) {
            return descend1(checkTransaction(null), obj, path);
        }
    }
    
    private Object descend1(Transaction trans, Object obj, String[] path){
        ObjectReference yo = getYapObject(obj);
        if(yo == null){
            return null;
        }
        
        Object child = null;
        
        final String fieldName = path[0];
        if(fieldName == null){
            return null;
        }
        ClassMetadata yc = yo.getYapClass();
        final FieldMetadata[] field = new FieldMetadata[]{null};
        yc.forEachYapField(new Visitor4() {
            public void visit(Object yf) {
                FieldMetadata yapField = (FieldMetadata)yf;
                if(yapField.canAddToQuery(fieldName)){
                    field[0] = yapField;
                }
            }
        });
        if(field[0] == null){
            return null;
        }
        if(yo.isActive()){
            child = field[0].get(obj);
        }else{
            Buffer reader = readReaderByID(trans, yo.getID());
            if(reader == null){
                return null;
            }
            MarshallerFamily mf = yc.findOffset(reader, field[0]);
            if(mf == null){
                return null;
            }
            try {
                child = field[0].readQuery(trans, mf, reader);
            } catch (CorruptionException e) {
            }
        }
        if(path.length == 1){
            return child;
        }
        if(child == null){
            return null;
        }
        String[] subPath = new String[path.length - 1];
        System.arraycopy(path, 1, subPath, 0, path.length - 1);
        return descend1(trans, child, subPath);
    }

    public boolean detectSchemaChanges() {
        // overriden in YapClient
        return configImpl().detectSchemaChanges();
    }
    
    public boolean dispatchsEvents() {
        return true;
    }

    protected boolean doFinalize() {
    	return true;
    }
    
    void emergencyClose() {
    	stopSession();
    }

    public ExtObjectContainer ext() {
        return _this;
    }

    void failedToShutDown() {
		if (_classCollection == null) {
			return;
		}
		if (i_amDuringFatalExit) {
			return;
		}
		if (_stackDepth == 0) {
			Messages.logErr(configImpl(), 50, toString(), null);
			while (!close()) {
			}
		} else {
			emergencyClose();
			if (_stackDepth > 0) {
				Messages.logErr(configImpl(), 24, null, null);
			}
		}
	}

    void fatalException(int msgID) {
		fatalException(null,msgID);
    }

	void fatalException(Throwable t) {
		fatalException(t,Messages.FATAL_MSG_ID);
    }

    void fatalException(Throwable t, int msgID) {
        if (!i_amDuringFatalExit) {
            i_amDuringFatalExit = true;
            emergencyClose();
			
            Messages.logErr(configImpl(), (msgID==Messages.FATAL_MSG_ID ? 18 : msgID), null, t);
        }
        throw new RuntimeException(Messages.get(msgID));
    }

	
    protected void finalize() {
		if (doFinalize() && (configImpl() == null || configImpl().automaticShutDown())) {
			failedToShutDown();
		}
	}

    void gc() {
        i_references.pollReferenceQueue();
    }

	public ObjectSet get(Object template) {
	    synchronized (i_lock) {
	    	return get1(null, template);
	    }
	}

    ObjectSetFacade get1(Transaction ta, Object template) {
        ta = checkTransaction(ta);
        QueryResult res = null;
        if (Deploy.debug) {
            res = get2(ta, template);
        } else {
            try {
                res = get2(ta, template);
            } catch (Throwable t) {
            	Exceptions4.catchAllExceptDb4oException(t);
                fatalException(t);
            }
        }
        return new ObjectSetFacade(res);
    }

    private final QueryResult get2(Transaction ta, Object template) {
        if (template == null || template.getClass() == Const4.CLASS_OBJECT) {
            return getAll(ta);
        } 
        Query q = query(ta);
        q.constrain(template);
        return executeQuery((QQuery)q);
    }
    
    public abstract AbstractQueryResult getAll(Transaction ta);

    public Object getByID(long id) {
        synchronized (i_lock) {
            return getByID1(null, id);
        }
    }

    public final Object getByID1(Transaction ta, long id) {
        ta = checkTransaction(ta);
        try {
            return getByID2(ta, (int) id);
        } catch (Exception e) {
            return null;
        }
    }
    
    final Object getByID2(Transaction ta, int a_id) {
        if (a_id > 0) {
            Object obj = objectForIDFromCache(a_id);
            if(obj != null){
                
                // Take care about handling the returned candidate reference.
                // If you loose the reference, weak reference management might also.
                return obj;
                
            }
            try {
                return new ObjectReference(a_id).read(ta, null, null, 0,Const4.ADD_TO_ID_TREE, true);
            } catch (Throwable t) {
                if (Debug.atHome) {
                    t.printStackTrace();
                }
            }
        }
        return null;
    }
    
    public final Object getActivatedObjectFromCache(Transaction ta, int id){
        Object obj = objectForIDFromCache(id);
        if(obj == null){
            return null;
        }
        activate1(ta, obj, configImpl().activationDepth());
        return obj;
    }
    
    public final Object readActivatedObjectNotInCache(Transaction ta, int id){
        Object obj = null;
    	beginTopLevelCall();
        try {
            obj = new ObjectReference(id).read(ta, null, null, configImpl().activationDepth(),Const4.ADD_TO_ID_TREE, true);
        } catch (Throwable t) {
            if (Debug.atHome) {
                t.printStackTrace();
            }
        } finally{
        	endTopLevelCall();
        }
        activate3CheckStill(ta);
        return obj;
    }
    
    public final Object getByUUID(Db4oUUID uuid){
        synchronized (i_lock) {
            if(uuid == null){
                return null;
            }
            Transaction ta = checkTransaction(null);
            Object[] arr = ta.objectAndYapObjectBySignature(
            					uuid.getLongPart(),
            					uuid.getSignaturePart());
            return arr[0]; 
        }
    }

    public long getID(Object obj) {
        synchronized (i_lock) {
            return getID1(obj);
        }
    }

    public final int getID1(Object obj) {
        checkClosed();

        if(obj == null){
            return 0;
        }

        ObjectReference yo = getYapObject(obj);
        if (yo != null) {
            return yo.getID();
        }
        return 0;
    }
    
    public ObjectInfo getObjectInfo(Object obj){
        synchronized(i_lock){
            return getYapObject(obj);
        }
    }

    public final Object[] getObjectAndYapObjectByID(Transaction ta, int a_id) {
        Object[] arr = new Object[2];
        if (a_id > 0) {
            ObjectReference yo = getYapObject(a_id);
            if (yo != null) {

                // Take care about handling the returned candidate reference.
                // If you loose the reference, weak reference management might also.

                Object candidate = yo.getObject();
                if (candidate != null) {
                    arr[0] = candidate;
                    arr[1] = yo;
                    return arr;
                }
                removeReference(yo);
            }
            try {
                yo = new ObjectReference(a_id);
                arr[0] = yo.read(ta, null, null, 0, Const4.ADD_TO_ID_TREE, true);
                
                if(arr[0] == null){
                    return arr;
                }
                
                // check class creation side effect and simply retry recursively
                // if it hits:
                if(arr[0] != yo.getObject()){
                    return getObjectAndYapObjectByID(ta, a_id);
                }
                
                arr[1] = yo;
                
            } catch (Throwable t) {
                if (Debug.atHome) {
                    t.printStackTrace();
                }
            }
        }
        return arr;
    }

    public final StatefulBuffer getWriter(Transaction a_trans, int a_address, int a_length) {
        if (Debug.exceedsMaximumBlockSize(a_length)) {
            return null;
        }
        return new StatefulBuffer(a_trans, a_address, a_length);
    }

    public final Transaction getSystemTransaction() {
        return i_systemTrans;
    }

    public final Transaction getTransaction() {
        return i_trans;
    }
    
    public final ClassMetadata getYapClass(ReflectClass claxx){
    	if(cantGetYapClass(claxx)){
    		return null;
    	}
        ClassMetadata yc = i_handlers.getYapClassStatic(claxx);
        if (yc != null) {
            return yc;
        }
        return _classCollection.getYapClass(claxx);
    }
    
    // TODO: Some ReflectClass implementations could hold a 
    // reference to YapClass to improve lookup performance here.
    public final ClassMetadata produceYapClass(ReflectClass claxx) {
    	if(cantGetYapClass(claxx)){
    		return null;
    	}
        ClassMetadata yc = i_handlers.getYapClassStatic(claxx);
        if (yc != null) {
            return yc;
        }
        
        return _classCollection.produceYapClass(claxx);
    }
    
    /**
     * Differentiating getActiveYapClass from getYapClass is a tuning 
     * optimization: If we initialize a YapClass, #set3() has to check for
     * the possibility that class initialization associates the currently
     * stored object with a previously stored static object, causing the
     * object to be known afterwards.
     * 
     * In this call we only return active YapClasses, initialization
     * is not done on purpose
     */
    final ClassMetadata getActiveYapClass(ReflectClass claxx) {
    	if(cantGetYapClass(claxx)){
    		return null;
    	}
        ClassMetadata yc = i_handlers.getYapClassStatic(claxx);
        if (yc != null) {
            return yc;
        }
        return _classCollection.getActiveYapClass(claxx);
    }
    
    private final boolean cantGetYapClass(ReflectClass claxx){
        if (claxx == null) {
            return true;
        }
        if ((!showInternalClasses()) && i_handlers.ICLASS_INTERNAL.isAssignableFrom(claxx)) {
            return true;
        }
        return false;
    }

    public ClassMetadata getYapClass(int id) {
    	if(DTrace.enabled){
    		DTrace.YAPCLASS_BY_ID.log(id);
    	}
        if (id == 0) {
            return null;
        }
        ClassMetadata yc = i_handlers.getYapClassStatic(id);
        if (yc != null) {
            return yc;
        }
        return _classCollection.getYapClass(id);
    }
    
    public Object objectForIDFromCache(int id){
        ObjectReference yo = getYapObject(id);
        if (yo == null) {
            return null;
        }
        Object candidate = yo.getObject();
        if(candidate == null){
            removeReference(yo);
        }
        return candidate;
    }

    public final ObjectReference getYapObject(int id) {
        if(DTrace.enabled){
            DTrace.GET_YAPOBJECT.log(id);
        }
        if(id <= 0){
            return null;
        }
        return i_idTree.id_find(id);
    }

    public final ObjectReference getYapObject(Object a_object) {
        return i_hcTree.hc_find(a_object);
    }
    
    public HandlerRegistry handlers(){
    	return i_handlers;
    }

    public boolean needsLockFileThread() {
		if(! Debug.lockFile){
			return false;
		}
        if (!Platform4.hasLockFileThread()) {
            return false;
        }
        if (Platform4.hasNio()) {
            return false;
        }
        if (configImpl().isReadOnly()) {
            return false;
        }
        return configImpl().lockFile();
    }

    protected boolean hasShutDownHook() {
        return configImpl().automaticShutDown();
    }

    final void hcTreeAdd(ObjectReference ref) {
        if(Debug.checkSychronization){
            i_lock.notify();
        }
        if (Deploy.debug) {
            Object obj = ref.getObject();
            if (obj != null) {
                ObjectReference yo = getYapObject(obj);
                if (yo != null) {
                    System.out.println("Duplicate alarm hc_Tree");
                }
            }
        }
        i_hcTree = i_hcTree.hc_add(ref);
    }

    final void idTreeAdd(ObjectReference a_yo) {
        if(Debug.checkSychronization){
            i_lock.notify();
        }
        if(DTrace.enabled){
            DTrace.ID_TREE_ADD.log(a_yo.getID());
        }
        if (Deploy.debug) {
            ObjectReference yo = getYapObject(a_yo.getID());
            if (yo != null) {
                System.out.println("Duplicate alarm id_Tree:" + a_yo.getID());
            }
        }
        i_idTree = i_idTree.id_add(a_yo);
    }

    protected void initialize1(Configuration config) {

        i_config = initializeConfig(config);
        i_handlers = new HandlerRegistry(_this, configImpl().encoding(), configImpl().reflector());
        
        if (i_references != null) {
            gc();
            i_references.stopTimer();
        }

        i_references = new WeakReferenceCollector(_this);

        if (hasShutDownHook()) {
            Platform4.addShutDownHook(this, i_lock);
        }
        i_handlers.initEncryption(configImpl());
        initialize2();
        i_stillToSet = null;
    }

	private Config4Impl initializeConfig(Configuration config) {
		Config4Impl impl=((Config4Impl)config);
		impl.stream(_this);
		impl.reflector().setTransaction(getSystemTransaction());
		return impl;
	}

    /**
     * before file is open
     */
    void initialize2() {

        // This is our one master root YapObject for the tree,
        // to allow us to ignore null.
        i_idTree = new ObjectReference(0);
        i_idTree.setObject(new Object());
        i_hcTree = i_idTree;

        initialize2NObjectCarrier();
    }

    /**
     * overridden in YapObjectCarrier
     */
    void initialize2NObjectCarrier() {
        _classCollection = new ClassMetadataRepository(i_systemTrans);
        i_references.startTimer();
    }

    protected void initialize3() {
        i_showInternalClasses = 100000;
        initialize4NObjectCarrier();
        i_showInternalClasses = 0;
    }
    
    void initialize4NObjectCarrier() {
        initializeEssentialClasses();
        rename(configImpl());
        _classCollection.initOnUp(i_systemTrans);
        if (configImpl().detectSchemaChanges()) {
            i_systemTrans.commit();
        }
    }

    void initializeEssentialClasses(){
        for (int i = 0; i < Const4.ESSENTIAL_CLASSES.length; i++) {
            produceYapClass(reflector().forClass(Const4.ESSENTIAL_CLASSES[i]));    
        }
    }

    final void instantiating(boolean flag) {
        i_instantiating = flag;
    }

    public boolean isActive(Object obj) {
        synchronized (i_lock) {
            return isActive1(obj);
        }
    }

    final boolean isActive1(Object obj) {
        checkClosed();
        if (obj != null) {
            ObjectReference yo = getYapObject(obj);
            if (yo != null) {
                return yo.isActive();
            }
        }
        return false;
    }

    public boolean isCached(long a_id) {
        synchronized (i_lock) {
            return objectForIDFromCache((int)a_id) != null;
        }
    }

    /**
     * overridden in YapClient
     * This method will make it easier to refactor than
     * an "instanceof YapClient" check.
     */
    public boolean isClient() {
        return false;
    }

    public boolean isClosed() {
        synchronized (i_lock) {
            return _classCollection == null;
        }
    }

    final boolean isInstantiating() {
        return i_instantiating;
    }

    boolean isServer() {
        return false;
    }

    public boolean isStored(Object obj) {
        synchronized (i_lock) {
            return isStored1(obj);
        }
    }

    final boolean isStored1(Object obj) {
        Transaction ta = checkTransaction(null);
        if (obj == null) {
            return false;
        }
        ObjectReference yo = getYapObject(obj);
        if (yo == null) {
            return false;
        }
        return !ta.isDeleted(yo.getID());
    }
    
    public ReflectClass[] knownClasses(){
        synchronized(i_lock){
            checkClosed();
            return reflector().knownClasses();
        }
    }
    
    public TypeHandler4 handlerByID(int id) {
        if (id < 1) {
            return null;
        }
        if (i_handlers.isSystemHandler(id)) {
            return i_handlers.getHandler(id);
        } 
        return getYapClass(id);
    }

    public Object lock() {
        return i_lock;
    }

    public final void logMsg(int code, String msg) {
        Messages.logMsg(configImpl(), code, msg);
    }

    public boolean maintainsIndices() {
        return true;
    }

    protected StatefulBuffer marshall(Transaction ta, Object obj) {
        // TODO: How about reuse of the MemoryFile here?
        int[] id = { 0};
        byte[] bytes = marshall(obj, id);
        StatefulBuffer yapBytes = new StatefulBuffer(ta, bytes.length);
        yapBytes.append(bytes);
        yapBytes.useSlot(id[0], 0, bytes.length);
        return yapBytes;
    }

    public byte[] marshall(Object obj, int[] id) {
        MemoryFile memoryFile = new MemoryFile();
        memoryFile.setInitialSize(223);
        memoryFile.setIncrementSizeBy(300);
        produceYapClass(reflector().forObject(obj));
        TransportObjectContainer carrier = new TransportObjectContainer(config(),_this, memoryFile);
        carrier.i_showInternalClasses = i_showInternalClasses;
        carrier.set(obj);
        id[0] = (int) carrier.getID(obj);
        carrier.close();
        return memoryFile.getBytes();
    }

    void message(String msg) {
        new Message(_this, msg);
    }

    public void migrateFrom(ObjectContainer objectContainer) {
        if(objectContainer == null){
            if(_replicationCallState == Const4.NONE){
                return;
            }
            _replicationCallState = Const4.NONE;
            if(i_handlers.i_migration != null){
                i_handlers.i_migration.terminate();
            }
            i_handlers.i_migration = null;
        }else{
            ObjectContainerBase peer = (ObjectContainerBase)objectContainer;
            _replicationCallState = Const4.OLD;
            peer._replicationCallState = Const4.OLD;
            i_handlers.i_migration = new MigrationConnection(_this, (ObjectContainerBase)objectContainer);
            peer.i_handlers.i_migration = i_handlers.i_migration;
        }
    }

    public final void needsUpdate(ClassMetadata a_yapClass) {
        i_needsUpdate = new List4(i_needsUpdate, a_yapClass);
    }
    
    public long generateTimeStampId() {
        return _timeStampIdGenerator.next();
    }

    public abstract int newUserObject();

    public Object peekPersisted(Object obj, int depth, boolean committed) {
    	
    	// TODO: peekPersisted is not stack overflow safe, if depth is too high. 
    	
        synchronized (i_lock) {
            beginTopLevelCall();
            try{
                i_justPeeked = null;
                Transaction ta = committed ? i_systemTrans
                    : checkTransaction(null);
                Object cloned = null;
                ObjectReference yo = getYapObject(obj);
                if (yo != null) {
                    cloned = peekPersisted1(ta, yo.getID(), depth);
                }
                i_justPeeked = null;
                return cloned;
            }finally{
                endTopLevelCall();
            }
        }
    }

    Object peekPersisted1(Transaction a_ta, int a_id, int a_depth) {
        if(a_depth < 0){
            return null;
        }
        TreeInt ti = new TreeInt(a_id);
        TreeIntObject tio = (TreeIntObject) Tree.find(i_justPeeked, ti);
        if (tio == null) {
            return new ObjectReference(a_id).read(a_ta, null, null, a_depth,
                Const4.TRANSIENT, false);
    
        } 
        return tio._object;
    }

    void peeked(int a_id, Object a_object) {
        i_justPeeked = Tree
            .add(i_justPeeked, new TreeIntObject(a_id, a_object));
    }

    public void purge() {
        synchronized (i_lock) {
            purge1();
        }
    }

    public void purge(Object obj) {
        synchronized (i_lock) {
            purge1(obj);
        }
    }

    final void purge1() {
        checkClosed();
        System.gc();
        System.runFinalization();
        System.gc();
        gc();
        _classCollection.purge();
    }

    final void purge1(Object obj) {
        if (obj == null || i_hcTree == null) {
        	return;
        }
        
        if (obj instanceof ObjectReference) {
            removeReference((ObjectReference) obj);
            return;
        }
        
        ObjectReference ref = getYapObject(obj);
        if (ref != null) {
            removeReference(ref);
        }
    }
    
    public final NativeQueryHandler getNativeQueryHandler() {
    	if (null == _nativeQueryHandler) {
    		_nativeQueryHandler = new NativeQueryHandler(_this);
    	}
    	return _nativeQueryHandler;
    }
    
    public final ObjectSet query(Predicate predicate){
    	return query(predicate,(QueryComparator)null);
    }

    public final ObjectSet query(Predicate predicate,QueryComparator comparator){
        synchronized (i_lock) {
            return getNativeQueryHandler().execute(predicate,comparator);
        }
    }

	public Query query() {
		synchronized (i_lock) {
			return query((Transaction)null);
    	}
    }
    
    public final ObjectSet query(Class clazz) {
        return get(clazz);
    }

    public final Query query(Transaction ta) {
        return new QQuery(checkTransaction(ta), null, null);
    }

    public abstract void raiseVersion(long a_minimumVersion);

    public abstract void readBytes(byte[] a_bytes, int a_address, int a_length);

    public abstract void readBytes(byte[] bytes, int address, int addressOffset, int length);

    public final Buffer readReaderByAddress(int a_address, int a_length) {
        if (a_address > 0) {

            // TODO: possibly load from cache here

            Buffer reader = new Buffer(a_length);
            readBytes(reader._buffer, a_address, a_length);
            i_handlers.decrypt(reader);
            return reader;
        }
        return null;
    }

    public final StatefulBuffer readWriterByAddress(Transaction a_trans,
        int a_address, int a_length) {
        if (a_address > 0) {
            // TODO:
            // load from cache here
            StatefulBuffer reader = getWriter(a_trans, a_address, a_length);
            reader.readEncrypt(_this, a_address);
            return reader;
        }
        return null;
    }

    public abstract StatefulBuffer readWriterByID(Transaction a_ta, int a_id);

    public abstract Buffer readReaderByID(Transaction a_ta, int a_id);
    
    public abstract StatefulBuffer[] readWritersByIDs(Transaction a_ta, int[] ids);

    private void reboot() {
        commit();
        int ccID = _classCollection.getID();
        i_references.stopTimer();
        initialize2();
        _classCollection.setID(ccID);
        _classCollection.read(i_systemTrans);
    }
    
    public GenericReflector reflector(){
        return i_handlers._reflector;
    }

    public void refresh(Object a_refresh, int a_depth) {
        synchronized (i_lock) {
            i_refreshInsteadOfActivate = true;
            try {
            	activate1(null, a_refresh, a_depth);
            } finally {
            	i_refreshInsteadOfActivate = false;
            }
        }
    }

    final void refreshClasses() {
        synchronized (i_lock) {
            _classCollection.refreshClasses();
        }
    }

    public abstract void releaseSemaphore(String name);
    
    public void flagAsHandled(ObjectReference ref){
    	ref.flagAsHandled(_topLevelCallId);
    }
    
    boolean flagForDelete(ObjectReference ref){
    	if(ref == null){
    		return false;
    	}
    	if(handledInCurrentTopLevelCall(ref)){
    		return false;
    	}
    	ref.flagForDelete(_topLevelCallId);
    	return true;
    }
    
    public abstract void releaseSemaphores(Transaction ta);

    void rename(Config4Impl config) {
        boolean renamedOne = false;
        if (config.rename() != null) {
            renamedOne = rename1(config);
        }
        _classCollection.checkChanges();
        if (renamedOne) {
            reboot();
        }
    }

    protected boolean rename1(Config4Impl config) {
        boolean renamedOne = false;
        try {
            Iterator4 i = config.rename().iterator();
            while (i.moveNext()) {
                Rename ren = (Rename) i.current();
                if (get(ren).size() == 0) {
                    boolean renamed = false;

                    boolean isField = ren.rClass.length() > 0;
                    ClassMetadata yapClass = _classCollection
                        .getYapClass(isField ? ren.rClass : ren.rFrom);
                    if (yapClass != null) {
                        if (isField) {
                            renamed = yapClass.renameField(ren.rFrom, ren.rTo);
                        } else {
                            ClassMetadata existing = _classCollection
                                .getYapClass(ren.rTo);
                            if (existing == null) {
                                yapClass.setName(ren.rTo);
                                renamed = true;
                            } else {
                                logMsg(9, "class " + ren.rTo);
                            }
                        }
                    }
                    if (renamed) {
                        renamedOne = true;
                        setDirtyInSystemTransaction(yapClass);

                        logMsg(8, ren.rFrom + " to " + ren.rTo);

                        // delete all that rename from the new name
                        // to allow future backswitching
                        ObjectSet backren = get(new Rename(ren.rClass, null,
                            ren.rFrom));
                        while (backren.hasNext()) {
                            delete(backren.next());
                        }

                        // store the rename, so we only do it once
                        set(ren);
                    }
                }
            }
        } catch (Throwable t) {
            Messages.logErr(configImpl(), 10, null, t);
        }
        return renamedOne;
    }

    public ReplicationProcess replicationBegin(ObjectContainer peerB, ReplicationConflictHandler conflictHandler) {
        return new ReplicationImpl(_this, peerB,conflictHandler);
    }
    
    public final int oldReplicationHandles(Object obj){
        
        // The double check on i_migrateFrom is necessary:
        // i_handlers.i_replicateFrom may be set in YapObjectCarrier for parent YapStream 
        if(_replicationCallState != Const4.OLD){
            return 0;
        }
        
        if(i_handlers.i_replication == null){
            return 0;
        }
        
        if(obj instanceof Internal4){
            return 0;
        }
        
        ObjectReference reference = getYapObject(obj);
        if(reference != null  && handledInCurrentTopLevelCall(reference)){
        	return reference.getID();
        }
        
        return i_handlers.i_replication.tryToHandle(_this, obj);        
    }
    
    public final boolean handledInCurrentTopLevelCall(ObjectReference ref){
    	return ref.isFlaggedAsHandled(_topLevelCallId);
    }

    void reserve(int byteCount) {
        // virtual: do nothing
    }

    public void rollback() {
        synchronized (i_lock) {
        	rollback1();
        }
    }

    public abstract void rollback1();

    public void send(Object obj) {
        // TODO: implement
        // so far this only works from YapClient
    }

    public void set(Object a_object) {
        set(a_object, Const4.UNSPECIFIED);
    }
    
    public final void set(Transaction trans, Object obj) {
    	set(trans, obj, Const4.UNSPECIFIED);
    }    
    
    public final void set(Object obj, int depth) {
        set(i_trans, obj, depth);
    }

	public void set(Transaction trans, Object obj, int depth) {
		synchronized (i_lock) {
            setInternal(trans, obj, depth, true);
        }
	}
    
    public final int setInternal(Transaction trans, Object obj, boolean checkJustSet) {
       return setInternal(trans, obj, Const4.UNSPECIFIED, checkJustSet);
    }
    
    public final int setInternal(Transaction trans, Object obj, int depth,  boolean checkJustSet) {
    	if(i_config.isReadOnly()) {
    		// TODO: throws ReadOnlyException after exception handling.
    		return 0;
    	}
    	
    	beginTopLevelSet();
    	try{
	        int id = oldReplicationHandles(obj); 
	        if (id != 0){
	            if(id < 0){
	                return 0;
	            }
	            return id;
	        }
	        return setAfterReplication(trans, obj, depth, checkJustSet);
    	}finally{
    		endTopLevelSet(trans);
    	}
    }
    
    public final int setAfterReplication(Transaction trans, Object obj, int depth,  boolean checkJust) {
        
        if (obj instanceof Db4oType) {
            Db4oType db4oType = db4oTypeStored(trans, obj);
            if (db4oType != null) {
                return getID1(db4oType);
            }
        }
        
        if (Deploy.debug) {
            return set2(trans, obj, depth, checkJust);
        }
        
        try {
            return set2(trans, obj, depth, checkJust);
        } catch (ObjectNotStorableException e) {
            throw e;
        } catch (Db4oException exc) {
            throw exc;
        } catch (Throwable t) {
            fatalException(t);
            return 0;
        }
    }
    
    public final void setByNewReplication(Db4oReplicationReferenceProvider referenceProvider, Object obj){
        synchronized(i_lock){
            _replicationCallState = Const4.NEW;
            i_handlers._replicationReferenceProvider = referenceProvider;
            
            set2(checkTransaction(null), obj, 1, false);
            
            _replicationCallState = Const4.NONE;
            i_handlers._replicationReferenceProvider = null;
        }
    }
    
    private final int set2(Transaction trans, Object obj, int depth, boolean checkJust) {
        int id = set3(trans, obj, depth, checkJust);
        if(stackIsSmall()){
            checkStillToSet();
        }
        return id;
    }
    
    public void checkStillToSet() {
        List4 postponedStillToSet = null;
        while (i_stillToSet != null) {
            Iterator4 i = new Iterator4Impl(i_stillToSet);
            i_stillToSet = null;
            while (i.moveNext()) {
                Integer updateDepth = (Integer)i.current();
                
                i.moveNext();
                ObjectReference ref = (ObjectReference)i.current();
                
                i.moveNext();
                Transaction trans = (Transaction)i.current();
                
                if(! ref.continueSet(trans, updateDepth.intValue())){
                    postponedStillToSet = new List4(postponedStillToSet, trans);
                    postponedStillToSet = new List4(postponedStillToSet, ref);
                    postponedStillToSet = new List4(postponedStillToSet, updateDepth);
                }
            }
        }
        i_stillToSet = postponedStillToSet;
    }
    
    private void notStorable(ReflectClass claxx, Object obj){
        if(! configImpl().exceptionsOnNotStorable()){
            return;
        }
        
        // FIXME:   Exceptions configuration setting cant be modified
        //          from running ObjectContainer. 
        //          Right now all tests fail, if we don't jump out here.
        
        //          The StorePrimitiveDirectly test case documents the err.
        
        if(true){
            return;
        }
        
        if(claxx != null){
            throw new ObjectNotStorableException(claxx);
        }
        
        throw new ObjectNotStorableException(obj.toString());
    }
    

    public final int set3(Transaction trans, Object obj, int updateDepth, boolean checkJustSet) {
        if (obj == null || (obj instanceof TransientClass)) {
            return 0;
        }
        	
        if (obj instanceof Db4oTypeImpl) {
            ((Db4oTypeImpl) obj).storedTo(trans);
        }
        
        ClassMetadata yc = null;
        ObjectReference ref = getYapObject(obj);
        if (ref == null) {
        	
            ReflectClass claxx = reflector().forObject(obj);
            
            if(claxx == null){
                notStorable(claxx, obj);
                return 0;
            }
            
            yc = getActiveYapClass(claxx);
            
            if (yc == null) {
                yc = produceYapClass(claxx);
                if ( yc == null){
                    notStorable(claxx, obj);
                    return 0;
                }
                
                // The following may return a reference if the object is held
                // in a static variable somewhere ( often: Enums) that gets
                // stored or associated on initialization of the YapClass.
                
                ref = getYapObject(obj);
                
            }
            
        } else {
            yc = ref.getYapClass();
        }
        
        if (isPlainObjectOrPrimitive(yc) ) {
            notStorable(yc.classReflector(), obj);
            return 0;
        }
        
        if (ref == null) {
            if (!objectCanNew(yc, obj)) {
                return 0;
            }
            ref = new ObjectReference();
            ref.store(trans, yc, obj);
			addToReferenceSystem(ref);
			if(obj instanceof Db4oTypeImpl){
			    ((Db4oTypeImpl)obj).setTrans(trans);
			}
			if (configImpl().messageLevel() > Const4.STATE) {
				message("" + ref.getID() + " new " + ref.getYapClass().getName());
			}
			
			flagAsHandled(ref);
			stillToSet(trans, ref, updateDepth);

        } else {
            if (canUpdate()) {
                if(checkJustSet){
                    if( (! ref.isNew())  && handledInCurrentTopLevelCall(ref)){
                        return ref.getID();
                    }
                }
                if (updateDepthSufficient(updateDepth)) {
                    flagAsHandled(ref);
                    ref.writeUpdate(trans, updateDepth);
                }
            }
        }
        checkNeededUpdates();
        return ref.getID();
    }

	private void addToReferenceSystem(ObjectReference ref) {
		idTreeAdd(ref);
		hcTreeAdd(ref);
	}
    
    private final boolean updateDepthSufficient(int updateDepth){
    	return (updateDepth == Const4.UNSPECIFIED) || (updateDepth > 0);
    }

    private final boolean isPlainObjectOrPrimitive(ClassMetadata yc) {
        return yc.getID() == HandlerRegistry.ANY_ID  || yc.isPrimitive();
    }

	private boolean objectCanNew(ClassMetadata yc, Object a_object) {
		return callbacks().objectCanNew(a_object)
			&& yc.dispatchEvent(_this, a_object, EventDispatcher.CAN_NEW);
	}

    public abstract void setDirtyInSystemTransaction(PersistentBase a_object);

    public abstract boolean setSemaphore(String name, int timeout);

    void setStringIo(LatinStringIO a_io) {
        i_handlers.i_stringHandler.setStringIo(a_io);
    }

    final boolean showInternalClasses() {
        return isServer() || i_showInternalClasses > 0;
    }

    /**
     * Objects implementing the "Internal4" marker interface are
     * not visible to queries, unless this flag is set to true.
     * The caller should reset the flag after the call.
     */
    public synchronized void showInternalClasses(boolean show) {
        if (show) {
            i_showInternalClasses++;
        } else {
            i_showInternalClasses--;
        }
        if (i_showInternalClasses < 0) {
            i_showInternalClasses = 0;
        }
    }
    
    private final boolean stackIsSmall(){
        return _stackDepth < Const4.MAX_STACK_DEPTH;
    }

    boolean stateMessages() {
        return true; // overridden to do nothing in YapObjectCarrier
    }

    /**
     * returns true in case an unknown single object is passed
     * This allows deactivating objects before queries are called.
     */
    final List4 stillTo1(List4 still, Object obj, int depth, boolean forceUnknownDeactivate) {
    	
        if (obj == null || depth <= 0) {
        	return still;
        }
        
        ObjectReference ref = getYapObject(obj);
        if (ref != null) {
        	if(handledInCurrentTopLevelCall(ref)){
        		return still;
        	}
        	flagAsHandled(ref);
            return new List4(new List4(still, new Integer(depth)), ref);
        } 
        final ReflectClass clazz = reflector().forObject(obj);
		if (clazz.isArray()) {
			if (!clazz.getComponentType().isPrimitive()) {
                Object[] arr = ArrayHandler.toArray(_this, obj);
                for (int i = 0; i < arr.length; i++) {
                    still = stillTo1(still, arr[i],
                        depth, forceUnknownDeactivate);
                }
			}
        } else {
            if (obj instanceof Entry) {
                still = stillTo1(still, ((Entry) obj).key, depth, false);
                still = stillTo1(still, ((Entry) obj).value, depth, false);
            } else {
                if (forceUnknownDeactivate) {
                    // Special handling to deactivate Top-Level unknown objects only.
                    ClassMetadata yc = getYapClass(reflector().forObject(obj));
                    if (yc != null) {
                        yc.deactivate(i_trans, obj, depth);
                    }
                }
            }
        }
        return still;
    }

    public void stillToActivate(Object a_object, int a_depth) {

        // TODO: We don't want the simple classes to search the hc_tree
        // Kick them out here.

        //		if (a_object != null) {
        //			Class clazz = a_object.getClass();
        //			if(! clazz.isPrimitive()){

        i_stillToActivate = stillTo1(i_stillToActivate, a_object, a_depth, false);

        //			}
        //		}
    }

    public void stillToDeactivate(Object a_object, int a_depth,
        boolean a_forceUnknownDeactivate) {
        i_stillToDeactivate = stillTo1(i_stillToDeactivate, a_object, a_depth, a_forceUnknownDeactivate);
    }

    void stillToSet(Transaction a_trans, ObjectReference a_yapObject, int a_updateDepth) {
        if(stackIsSmall()){
            if(a_yapObject.continueSet(a_trans, a_updateDepth)){
                return;
            }
        }
        i_stillToSet = new List4(i_stillToSet, a_trans);
        i_stillToSet = new List4(i_stillToSet, a_yapObject);
        i_stillToSet = new List4(i_stillToSet, new Integer(a_updateDepth));
    }

    protected void stopSession() {
        if (hasShutDownHook()) {
            Platform4.removeShutDownHook(this, i_lock);
        }
        _classCollection = null;
        i_references.stopTimer();
    }

    public StoredClass storedClass(Object clazz) {
        synchronized (i_lock) {
            checkClosed();
            ReflectClass claxx = configImpl().reflectorFor(clazz);
            if (claxx == null) {
            	return null;
            }
            return getYapClass(claxx);
        }
    }

    public StoredClass[] storedClasses() {
        synchronized (i_lock) {
            checkClosed();
            return _classCollection.storedClasses();
        }
    }
		
    public LatinStringIO stringIO(){
    	return i_handlers.i_stringHandler.i_stringIo;
    }
    
    public abstract SystemInfo systemInfo();
    
    public final void beginTopLevelCall(){
    	if(DTrace.enabled){
    		DTrace.BEGIN_TOP_LEVEL_CALL.log();
    	}
    	checkClosed();
    	generateCallIDOnTopLevel();
    	_stackDepth++;
    }
    
    public final void beginTopLevelSet(){
    	beginTopLevelCall();
    }
    
    public final void endTopLevelCall(){
    	if(DTrace.enabled){
    		DTrace.END_TOP_LEVEL_CALL.log();
    	}
    	_stackDepth--;
    	generateCallIDOnTopLevel();
    }
    
    public final void endTopLevelSet(Transaction trans){
    	endTopLevelCall();
    	if(_stackDepth == 0){
    		trans.processDeletes();
    	}
    }
    
    private final void generateCallIDOnTopLevel(){
    	if(_stackDepth == 0){
    		_topLevelCallId = _topLevelCallIdGenerator.next();
    	}
    }
    
    public int stackDepth(){
    	return _stackDepth;
    }
    
    public void stackDepth(int depth){
    	_stackDepth = depth;
    }
    
    public int topLevelCallId(){
    	return _topLevelCallId;
    }
    
    public void topLevelCallId(int id){
    	_topLevelCallId = id;
    }

    public Object unmarshall(StatefulBuffer yapBytes) {
        return unmarshall(yapBytes._buffer, yapBytes.getID());
    }

    public Object unmarshall(byte[] bytes, int id) {
        MemoryFile memoryFile = new MemoryFile(bytes);
        TransportObjectContainer carrier = new TransportObjectContainer(configure(),_this, memoryFile);
        Object obj = carrier.getByID(id);
        carrier.activate(obj, Integer.MAX_VALUE);
        carrier.close();
        return obj;
    }

    public long version(){
    	synchronized(i_lock){
    		return currentVersion();
    	}
    }

    public abstract void write(boolean shuttingDown);

    public abstract void writeDirty();

    public abstract void writeEmbedded(StatefulBuffer a_parent, StatefulBuffer a_child);

    public abstract void writeNew(ClassMetadata a_yapClass, StatefulBuffer aWriter);

    public abstract void writeTransactionPointer(int a_address);

    public abstract void writeUpdate(ClassMetadata a_yapClass, StatefulBuffer a_bytes);

    public final void removeReference(ObjectReference ref) {
        if(DTrace.enabled){
            DTrace.REFERENCE_REMOVED.log(ref.getID());
        }

        i_hcTree = i_hcTree.hc_remove(ref);
        i_idTree = i_idTree.id_remove(ref.getID());
        
        // setting the ID to minus 1 ensures that the
        // gc mechanism does not kill the new YapObject
        ref.setID(-1);
        Platform4.killYapRef(ref.getObjectReference());
    }
    
    // cheat emulating '(YapStream)this'
    private static ObjectContainerBase cast(PartialObjectContainer obj) {
    	return (ObjectContainerBase)obj;
    }
    
    public Callbacks callbacks() {
    		return _callbacks;
    }
    
    public void callbacks(Callbacks cb) {
    		if (cb == null) {
    			throw new IllegalArgumentException();
    		}
    		_callbacks = cb;
    }

    public Config4Impl configImpl() {
        return i_config;
    }
    
	public UUIDFieldMetadata getUUIDIndex() {
		return i_handlers.i_indexes.i_fieldUUID;
	}
	
	public VersionFieldMetadata getVersionIndex() {
		return i_handlers.i_indexes.i_fieldVersion;
	}

    public ClassMetadataRepository classCollection() {
        return _classCollection;
    }
    
    public ClassInfoHelper getClassMetaHelper() {
    	return _classMetaHelper;
    }
    
    public abstract long[] getIDsForClass(Transaction trans, ClassMetadata clazz);
    
	public abstract QueryResult classOnlyQuery(Transaction trans, ClassMetadata clazz);
	
	public abstract QueryResult executeQuery(QQuery query);
	
	public void replicationCallState(int state) {
		_replicationCallState = state;
	}


}