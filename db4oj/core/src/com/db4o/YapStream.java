/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.query.*;
import com.db4o.reflect.*;
import com.db4o.types.*;

abstract class YapStream implements ObjectContainer, ExtObjectContainer,
    TransientClass {

    static final int        HEADER_LENGTH         = 2 + (YapConst.YAPINT_LENGTH * 4);

    // The header is:

    // Old format
    // -------------------------
    // {
    // Y
    // [Rest]

    
    // New format
    // -------------------------
    // (byte)4
    // block size in bytes 1 to 127
    // [Rest]
    

    // Rest (only ints)
    // -------------------
    // address of the extended configuration block, see YapConfigBlock
    // headerLock
    // YapClassCollection ID
    // FreeBySize ID
    

    private boolean         i_amDuringFatalExit   = false;

    // Collection of all classes
    // if (i_classCollection == null) the engine is down.
    YapClassCollection      i_classCollection;

    // the Configuration context for this ObjectContainer
    Config4Impl             i_config;

    // Increments and decrements for outside calls into YapStream
    // A value > 0 signals that the engine crashed with an uncaught exception.
    // and prevents the finalizer.
    int                     i_entryCounter        = -100; // negative during startup
    Tree                    i_freeOnCommit;

    // Tree of all YapObject references, sorted by IdentityHashCode
    private YapObject       i_hcTree;

    // Tree of all YapObject references, sorted by ID
    private YapObject       i_idTree;
    private Tree[]          i_justActivated;
    private Tree[]          i_justDeactivated;
    private Tree            i_justPeeked;
    private Tree            i_justSet;

    final Object            i_lock                = new Object();

    // currently used to resolve self-linking concurrency problems
    // in cylic links, stores only YapClass objects
    private List4           i_needsUpdate;

    //  the parent ObjectContainer for YapObjectCarrier or this for all
    //  others. Allows identifying the responsible Objectcontainer for IDs
    final YapStream         i_parent;

    //  allowed adding refresh with little code changes.
    boolean                 i_refreshInsteadOfActivate;

    // a value greater than 0 indicates class implementing the
    // "Internal" interface are visible in queries and can
    // be used.
    int                     i_showInternalClasses = 0;
    long                    i_startTime;
    private List4           i_stillToActivate;
    private List4           i_stillToDeactivate;

    private List4           i_stillToSet;

    // String translator
    YapStringIO             i_stringIo;

    // used for YapClass and YapClassCollection
    // may be parent or equal to i_trans
    Transaction             i_systemTrans;

    // used for Objects
    Transaction             i_trans;

    private boolean         i_instantiating;

    // all the per-YapStream references that we don't
    // want created in YapobjectCarrier
    YapHandlers             i_handlers;

    YapStream               i_migrateFrom;

    // weak reference management
    YapReferences           i_references;

    YapStream(YapStream a_parent) {
        i_parent = a_parent == null ? this : a_parent;
        initialize0();
        createTransaction();
        initialize1();
    }

    public void activate(Object a_activate, int a_depth) {
        synchronized (i_lock) {
            activate1(null, a_activate, a_depth);
        }
    }

    final void activate1(Transaction ta, Object a_activate) {
        activate1(ta, a_activate, i_config.i_activationDepth);
    }

    final void activate1(Transaction ta, Object a_activate, int a_depth) {
        ta = checkTransaction(ta);
        beginEndActivation();
        activate2(ta, a_activate, a_depth);
        beginEndActivation();
    }

    final void beginEndActivation() {
        i_justActivated[0] = null;
    }
    
    final void beginEndSet(Transaction ta){
        i_justSet = null;
        if(ta != null){
            ta.beginEndSet();
        }
    }

    /**
     * internal call interface, does not reset i_justActivated
     */
    final void activate2(Transaction ta, Object a_activate, int a_depth) {
        try {
            i_entryCounter++;
            stillToActivate(a_activate, a_depth);
            activate3CheckStill(ta);
        } catch (Throwable t) {
            fatalException(t);
        }
        i_entryCounter--;
    }
    
    final void activate3CheckStill(Transaction ta){
        while (i_stillToActivate != null) {

            // TODO: Optimize!  A lightweight int array would be faster.

            Iterator4 i = new Iterator4(i_stillToActivate);
            i_stillToActivate = null;

            while (i.hasNext()) {
                YapObject yo = (YapObject) i.next();
                int depth = ((Integer) i.next()).intValue();
                Object obj = yo.getObject();
                if (obj == null) {
                    yapObjectGCd(yo);
                } else {
                    yo.activate1(ta, obj, depth, i_refreshInsteadOfActivate);
                }
            }
        }
    }

    public void bind(Object obj, long id) {
        synchronized (i_lock) {
            bind1(null, obj, id);
        }
    }

    /** TODO: This is not transactional yet. */
    final void bind1(Transaction ta, Object obj, long id) {
        
        if(DTrace.enabled){
            DTrace.BIND.log(id, " ihc " + System.identityHashCode(obj));
        }
        
        ta = checkTransaction(ta);
        int intID = (int) id;
        if (obj != null) {
            Object oldObject = getByID(id);
            if (oldObject != null) {
                YapObject yo = getYapObject(intID);
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
    
    final void bind2(YapObject a_yapObject, Object obj){
        int id = a_yapObject.getID();
        yapObjectGCd(a_yapObject);
        a_yapObject = new YapObject(getYapClass(reflector().forObject(obj), false),
            id);
        a_yapObject.setObjectWeak(this, obj);
        a_yapObject.setStateDirty();
        idTreeAdd(a_yapObject);
        hcTreeAdd(a_yapObject);
    }

    boolean canUpdate() {
        return true;
    }

    final void checkClosed() {
        if (i_classCollection == null) {
            Db4o.throwRuntimeException(20, toString());
        }
    }

    final void checkNeededUpdates() {
        if (i_needsUpdate != null) {
            Iterator4 i = new Iterator4(i_needsUpdate);
            while (i.hasNext()) {
                YapClass yapClass = (YapClass) i.next();
                yapClass.setStateDirty();
                yapClass.write(this, i_systemTrans);
            }
            i_needsUpdate = null;
        }
    }

    final Transaction checkTransaction(Transaction ta) {
        checkClosed();
        if (ta != null) {
            return ta;
        }
        return getTransaction();
    }

    public boolean close() {
        synchronized (Db4o.lock) {
            synchronized (i_lock) {
                boolean ret = close1();
                return ret;
            }
        }
    }

    final boolean close1() {
        // this is set to null in close2 and is therefore our check for down.
        if (i_classCollection == null) {
            return true;
        }
        Platform.preClose(this);
        checkNeededUpdates();
        if (stateMessages()) {
            logMsg(2, toString());
        }
        boolean closeResult = close2();
        return closeResult;
    }

    boolean close2() {
        if (hasShutDownHook()) {
            Platform.removeShutDownHook(this, i_lock);
        }
        i_classCollection = null;
        i_references.stopTimer();
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
                i_handlers.i_collections = Platform.collections(this);
            }
            return i_handlers.i_collections;
        }
    }

    public void commit() {
        synchronized (i_lock) {
            if(DTrace.enabled){
                DTrace.COMMIT.log();
            }
            commit1();
        }
    }

    abstract void commit1();

    public Configuration configure() {
        return i_config;
    }

    abstract ClassIndex createClassIndex(YapClass a_yapClass);

    abstract QResult createQResult(Transaction a_ta);

    void createStringIO(byte encoding) {
        switch (encoding) {
            case YapConst.ISO8859:
                setStringIo(new YapStringIO());
                break;
            case YapConst.UNICODE:
                setStringIo(new YapStringIOUnicode());
                break;
        }
    }

    void createTransaction() {
        i_systemTrans = new Transaction(this, null);
        i_trans = new Transaction(this, i_systemTrans);
    }

    abstract long currentVersion();
    
    boolean createYapClass(YapClass a_yapClass, ReflectClass a_class, YapClass a_superYapClass) {
        return a_yapClass.init(this, a_superYapClass, a_class, false);
    }


    /**
     * allows special handling for all Db4oType objects.
     * Redirected here from #set() so only instanceof check is necessary
     * in the #set() method. 
     * @return true if handled here and #set() should not continue processing
     */
    Object db4oTypeStored(Transaction a_trans, Object a_object) {
        if (a_object instanceof Db4oDatabase) {
            Db4oDatabase database = (Db4oDatabase) a_object;
            if (getYapObject(a_object) != null) {
                return a_object;
            }
            Query q = querySharpenBug();
            q.constrain(database.getClass());
            q.descend("i_uuid").constrain(new Long(database.i_uuid));
            ObjectSet objectSet = q.execute();
            while (objectSet.hasNext()) {
                Db4oDatabase storedDatabase = (Db4oDatabase) objectSet.next();
                activate1(null, storedDatabase, 4);
                if (storedDatabase.equals(a_object)) {
                    return storedDatabase;
                }
            }
        }
        return null;
    }

    public void deactivate(Object a_deactivate, int a_depth) {
        synchronized (i_lock) {
            deactivate1(a_deactivate, a_depth);
        }
    }

    final void deactivate1(Object a_deactivate, int a_depth) {
        checkClosed();
        try {
            i_entryCounter++;
            i_justDeactivated[0] = null;
            deactivate2(a_deactivate, a_depth);
            i_justDeactivated[0] = null;
        } catch (Throwable t) {
            fatalException(t);
        }
        i_entryCounter--;
    }

    private final void deactivate2(Object a_activate, int a_depth) {
        stillToDeactivate(a_activate, a_depth, true);
        while (i_stillToDeactivate != null) {
            Iterator4 i = new Iterator4(i_stillToDeactivate);
            i_stillToDeactivate = null;
            while (i.hasNext()) {
                ((YapObject) i.next()).deactivate(i_trans, ((Integer) i.next())
                    .intValue());
            }
        }
    }

    public void delete(Object a_object) {
        synchronized (i_lock) {
            Transaction ta = delete1(null, a_object);
            ta.beginEndSet();
        }
    }

    final Transaction delete1(Transaction ta, Object a_object) {
        ta = checkTransaction(ta);
        if (a_object != null) {
            if (Deploy.debug) {
                delete2(ta, a_object);
            } else {
                try {
                    delete2(ta, a_object);
                } catch (Throwable t) {
                    fatalException(t);
                }
            }
            i_entryCounter--;
        }
        return ta;
    }

    private final void delete2(Transaction ta, Object a_object) {
        i_entryCounter++;
        YapObject yo = getYapObject(a_object);
        if (yo != null) {
            int id = yo.getID();
            delete3(ta, yo, a_object, 0);
        }
    }
    
    final void delete3(Transaction ta, YapObject yo, Object a_object,  int a_cascade) {
        if(a_object instanceof SecondClass){
            delete4(ta, yo, a_object, a_cascade);
        }else{
            ta.delete(yo, a_object, a_cascade, true);
        }
    }

    final void delete4(Transaction ta, YapObject yo, Object a_object, int a_cascade) {
        boolean success = false;
        if (yo.beginProcessing()) {
            YapClass yc = yo.getYapClass();
            Object obj = yo.getObject();
            if (yc.dispatchEvent(this, obj,
                EventDispatcher.CAN_DELETE)) {
                if(delete5(ta, yo, a_cascade)){
                	yc.dispatchEvent(this, obj, EventDispatcher.DELETE);
                    if (i_config.i_messageLevel > YapConst.STATE) {
                        message("" + yo.getID() + " delete " + yo.getYapClass().getName());
                    }
                }
            }
            yo.endProcessing();
        }
    }

    abstract boolean delete5(Transaction ta, YapObject yapObject, int a_cascade);

    boolean detectSchemaChanges() {
        // overriden in YapClient
        return i_config.i_detectSchemaChanges;
    }
    
    public boolean dispatchsEvents() {
        return true;
    }

    void emergencyClose() {
        i_classCollection = null;
        i_references.stopTimer();
    }

    public ExtObjectContainer ext() {
        return this;
    }

    void failedToShutDown() {
        synchronized (Db4o.lock) {
            if (i_classCollection != null) {
                if (i_entryCounter == 0) {
                    Db4o.logErr(i_config, 50, toString(), null);
                    while (!close()) {

                    }
                } else {
                    emergencyClose();
                    if (i_entryCounter > 0) {
                        Db4o.logErr(i_config, 24, null, null);
                    }
                }
            }
        }
    }

    void fatalException(Throwable t) {
        if (!i_amDuringFatalExit) {
            i_amDuringFatalExit = true;
            i_classCollection = null;
            emergencyClose();
            Db4o.logErr(i_config, 18, null, t);
        }
        throw new RuntimeException(Messages.get(44));
    }

    protected void finalize() {
        if (i_config == null || i_config.i_automaticShutDown) {
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

    ObjectSet get1(Transaction ta, Object template) {
        ta = checkTransaction(ta);
        QResult res = createQResult(ta);
        if (Deploy.debug) {
            get2(ta, template, res);
        } else {
            try {
                get2(ta, template, res);
            } catch (Throwable t) {
                fatalException(t);
            }
        }
        i_entryCounter--;
        res.reset();
        return res;
    }

    private final void get2(Transaction ta, Object template, QResult res) {
        i_entryCounter++;
        if (template == null || template.getClass() == YapConst.CLASS_OBJECT) {
            getAll(ta, res);
        } else {
            Query q = querySharpenBug(ta);
            q.constrain(template);
            ((QQuery) q).execute1(res);
        }
    }

    abstract void getAll(Transaction ta, QResult a_res);

    public Object getByID(long id) {
        synchronized (i_lock) {
            return getByID1(null, id);
        }
    }

    final Object getByID1(Transaction ta, long id) {
        ta = checkTransaction(ta);
        try {
            return getByID2(ta, (int) id);
        } catch (Exception e) {
            return null;
        }
    }

    final Object getByID2(Transaction ta, int a_id) {
        if (a_id > 0) {
            YapObject yo = getYapObject(a_id);
            if (yo != null) {

                // Take care about handling the returned candidate reference.
                // If you loose the reference, weak reference management might also.

                Object candidate = yo.getObject();
                if (candidate != null) {
                    return candidate;
                }
                yapObjectGCd(yo);
            }
            try {
                return new YapObject(a_id).read(ta, null, null, 0,
                    YapConst.ADD_TO_ID_TREE);
            } catch (Throwable t) {
                if (Debug.atHome) {
                    t.printStackTrace();
                }
            }
        }
        return null;
    }
    
    public final Object getByUUID(Db4oUUID uuid){
        synchronized (i_lock) {
            Transaction ta = checkTransaction(null);
            Object[] arr = ta.objectAndYapObjectBySignature(uuid.getLongPart(), uuid.getSignaturePart());
            return arr[0]; 
        }
    }

    public long getID(Object a_object) {
        synchronized (i_lock) {
            return getID1(null, a_object);
        }
    }

    final int getID1(Transaction ta, Object a_object) {
        checkClosed();

        // TODO: this doesn't look transactional at all.
        // Should it be transactional?

        YapObject yo = i_hcTree.hc_find(a_object);
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

    final Object[] getObjectAndYapObjectByID(Transaction ta, int a_id) {
        Object[] arr = new Object[2];
        if (a_id > 0) {
            YapObject yo = getYapObject(a_id);
            if (yo != null) {

                // Take care about handling the returned candidate reference.
                // If you loose the reference, weak reference management might also.

                Object candidate = yo.getObject();
                if (candidate != null) {
                    arr[0] = candidate;
                    arr[1] = yo;
                    return arr;
                }
                yapObjectGCd(yo);
            }
            try {
                yo = new YapObject(a_id);
                arr[0] = yo.read(ta, null, null, 0, YapConst.ADD_TO_ID_TREE);
                arr[1] = yo;
            } catch (Throwable t) {
                if (Debug.atHome) {
                    t.printStackTrace();
                }
            }
        }
        return arr;
    }

    final YapWriter getWriter(Transaction a_trans, int a_length) {
        return new YapWriter(a_trans, a_length);
    }

    final YapWriter getWriter(Transaction a_trans, int a_address, int a_length) {
        if (Debug.exceedsMaximumBlockSize(a_length)) {
            return null;
        }
        return new YapWriter(a_trans, a_address, a_length);
    }

    Transaction getSystemTransaction() {
        return i_systemTrans;
    }

    Transaction getTransaction() {
        return i_trans;
    }
    
    // FIXME: REFLECTOR an IClass could also hold a reference to
    // a YapClass so we would improve considerably on lookup
    // performance here.
    final YapClass getYapClass(ReflectClass a_class, boolean a_create) {
        if (a_class == null) {
            return null;
        }
        if ((!showInternalClasses())
            && i_handlers.ICLASS_INTERNAL.isAssignableFrom(a_class)) {
            return null;
        }
        YapClass yc = i_handlers.getYapClassStatic(a_class);
        if (yc != null) {
            return yc;
        }
        
        return i_classCollection.getYapClass(a_class, a_create);
    }

    YapClass getYapClass(int a_id) {
    	if(DTrace.enabled){
    		DTrace.YAPCLASS_BY_ID.log(a_id);
    	}
        if (a_id == 0) {
            return null;
        }
        YapClass yc = i_handlers.getYapClassStatic(a_id);
        if (yc != null) {
            return yc;
        }
        return i_classCollection.getYapClass(a_id);
    }

    final YapObject getYapObject(int a_id) {
        return i_idTree.id_find(a_id);
    }

    final YapObject getYapObject(Object a_object) {
        return i_hcTree.hc_find(a_object);
    }
    
    public YapHandlers handlers(){
    	return i_handlers;
    }

    boolean needsLockFileThread() {
        if (!Platform.hasLockFileThread()) {
            return false;
        }
        if (Platform.hasNio()) {
            return false;
        }
        if (i_config.i_readonly) {
            return false;
        }
        return i_config.i_lockFile;
    }

    boolean hasShutDownHook() {
        return i_config.i_automaticShutDown;
    }

    final void hcTreeAdd(YapObject a_yo) {
        if (Deploy.debug) {
            Object obj = a_yo.getObject();
            if (obj != null) {
                YapObject yo = i_hcTree.hc_find(obj);
                if (yo != null) {
                    System.out.println("Duplicate alarm hc_Tree");
                }
            }
        }
        i_hcTree = i_hcTree.hc_add(a_yo);
    }

    final void hcTreeRemove(YapObject a_yo) {
        i_hcTree = i_hcTree.hc_remove(a_yo);
    }

    final void idTreeAdd(YapObject a_yo) {
        if (Deploy.debug) {
            YapObject yo = getYapObject(a_yo.getID());
            if (yo != null) {
                System.out.println("Duplicate alarm id_Tree:" + a_yo.getID());
            }
        }
        i_idTree = i_idTree.id_add(a_yo);
    }

    final void idTreeRemove(int a_id) {
        i_idTree = i_idTree.id_remove(a_id);
    }

    void initialize0() {
        initialize0b();
        i_stillToSet = null;
        i_justActivated = new Tree[1];
    }

    void initialize0b() {

        // this method allows overriding in YapObjectCarrier

        // TODO: lightweight YapObjectCarrier by moving all
        // variables that are not needed there to a delegate

        i_justDeactivated = new Tree[1];
    }

    void initialize1() {

        try {
            i_config = (Config4Impl) ((DeepClone) Db4o.configure())
                .deepClone(this);
        } catch (CloneNotSupportedException e) {
        }
        i_handlers = new YapHandlers(this);
        createStringIO(i_config.i_encoding);
        
        if (i_references != null) {
            gc();
            i_references.stopTimer();
        }

        i_references = new YapReferences(this);

        if (hasShutDownHook()) {
            Platform.addShutDownHook(this, i_lock);
        }
        i_handlers.initEncryption(i_config);
        initialize2();
        i_stillToSet = null;
    }

    /**
     * before file is open
     */
    void initialize2() {

        // This is our one master root YapObject for the tree,
        // to allow us to ignore null.
        i_idTree = new YapObject(0);
        i_idTree.setObject(new Object());
        i_hcTree = i_idTree;

        initialize2b();
    }

    /**
     * overridden in YapObjectCarrier
     */
    void initialize2b() {
        i_classCollection = new YapClassCollection(i_systemTrans);
        i_references.startTimer();
    }

    void initialize3() {
        i_showInternalClasses = 100000;
        initialize4NObjectCarrier();
        i_showInternalClasses = 0;
        i_entryCounter = 0;
    }
    
    void initialize4NObjectCarrier() {
        initializeEssentialClasses();
        rename(i_config);
        i_classCollection.initOnUp(i_systemTrans);
        if (i_config.i_detectSchemaChanges) {
            i_systemTrans.commit();
        }
    }

    void initializeEssentialClasses(){
        for (int i = 0; i < YapConst.ESSENTIAL_CLASSES.length; i++) {
            getYapClass(reflector().forClass(YapConst.ESSENTIAL_CLASSES[i]), true);    
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
            YapObject yo = getYapObject(obj);
            if (yo != null) {
                return yo.isActive();
            }
        }
        return false;
    }

    public boolean isCached(long a_id) {
        synchronized (i_lock) {
            if (a_id > 0) {
                YapObject yo = getYapObject((int) a_id);
                if (yo != null) {
                    Object candidate = yo.getObject();
                    if (candidate != null) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /**
     * overridden in YapClient
     * This method will make it easier to refactor than
     * an "instanceof YapClient" check.
     */
    boolean isClient() {
        return false;
    }

    public boolean isClosed() {
        synchronized (i_lock) {
            return i_classCollection == null;
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
        YapObject yo = getYapObject(obj);
        if (yo == null) {
            return false;
        }
        return !ta.isDeleted(yo.getID());
    }

    public Object lock() {
        return i_lock;
    }

    final void logMsg(int code, String msg) {
        Db4o.logMsg(i_config, code, msg);
    }

    boolean maintainsIndices() {
        return true;
    }

    YapWriter marshall(Transaction ta, Object obj) {
        // TODO: How about reuse of the MemoryFile here?
        int[] id = { 0};
        byte[] bytes = marshall(obj, id);
        YapWriter yapBytes = new YapWriter(ta, bytes.length);
        yapBytes.append(bytes);
        yapBytes.useSlot(id[0], 0, bytes.length);
        return yapBytes;
    }

    byte[] marshall(Object obj, int[] id) {
        MemoryFile memoryFile = new MemoryFile();
        memoryFile.setInitialSize(223);
        memoryFile.setIncrementSizeBy(300);
        getYapClass(reflector().forObject(obj), true);
        YapObjectCarrier carrier = new YapObjectCarrier(this, memoryFile);
        carrier.i_showInternalClasses = i_showInternalClasses;
        carrier.set(obj);
        id[0] = (int) carrier.getID(obj);
        carrier.close();
        return memoryFile.getBytes();
    }

    void message(String msg) {
        new Message(this, msg);
    }

    public void migrateFrom(ObjectContainer objectContainer) {
        i_migrateFrom = (YapStream) objectContainer;
    }

    final void needsUpdate(YapClass a_yapClass) {
        i_needsUpdate = new List4(i_needsUpdate, a_yapClass);
    }

    abstract YapWriter newObject(Transaction a_trans, YapMeta a_object);

    abstract int newUserObject();

    public Object peekPersisted(Object a_object, int a_depth,
        boolean a_committed) {
        synchronized (i_lock) {
            checkClosed();
            i_entryCounter++;
            i_justPeeked = null;
            Transaction ta = a_committed ? i_systemTrans
                : checkTransaction(null);
            Object cloned = null;
            YapObject yo = getYapObject(a_object);
            if (yo != null) {
                cloned = peekPersisted1(ta, yo.getID(), a_depth);
            }
            i_justPeeked = null;
            i_entryCounter--;
            return cloned;
        }
    }

    Object peekPersisted1(Transaction a_ta, int a_id, int a_depth) {
        TreeInt ti = new TreeInt(a_id);
        TreeIntObject tio = (TreeIntObject) Tree.find(i_justPeeked, ti);
        if (tio == null) {
            return new YapObject(a_id).read(a_ta, null, null, a_depth,
                YapConst.TRANSIENT);

        } else {
            return tio.i_object;
        }
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
        i_classCollection.purge();
    }

    final void purge1(Object obj) {
        if (obj != null) {
            if (i_hcTree != null) {
                YapObject yo = null;
                if (obj instanceof YapObject) {
                    yo = (YapObject) obj;
                } else {
                    yo = i_hcTree.hc_find(obj);
                }
                if (yo != null) {
                    yapObjectGCd(yo);
                }
            }
        }
    }

    public Query query() {
        synchronized (i_lock) {
            return query(null);
        }
    }

    final Query query(Transaction ta) {
        i_entryCounter++;
        Query q = new QQuery(checkTransaction(ta), null, null);
        i_entryCounter--;
        return q;
    }

    Query querySharpenBug() {
        // A bug in the CSharp converter makes this redirection necessary.
        return query();
    }

    Query querySharpenBug(Transaction ta) {
        // A bug in the CSharp converter makes this redirection necessary.
        return query(ta);
    }
    
    abstract void raiseVersion(long a_minimumVersion);

    abstract void readBytes(byte[] a_bytes, int a_address, int a_length);

    abstract void readBytes(byte[] bytes, int address, int addressOffset, int length);

    final YapReader readObjectReaderByAddress(int a_address, int a_length) {
        if (a_address > 0) {

            // TODO: possibly load from cache here

            YapReader reader = new YapReader(a_length);
            readBytes(reader._buffer, a_address, a_length);
            i_handlers.decrypt(reader);
            return reader;
        }
        return null;
    }

    final YapWriter readObjectWriterByAddress(Transaction a_trans,
        int a_address, int a_length) {
        if (a_address > 0) {
            // TODO:
            // load from cache here
            YapWriter reader = getWriter(a_trans, a_address, a_length);
            reader.readEncrypt(this, a_address);
            return reader;
        }
        return null;
    }

    abstract YapWriter readWriterByID(Transaction a_ta, int a_id);

    abstract YapReader readReaderByID(Transaction a_ta, int a_id);

    private void reboot() {
        commit();
        int ccID = i_classCollection.getID();
        i_references.stopTimer();
        initialize2();
        i_classCollection.setID(this, ccID);
        i_classCollection.read(i_systemTrans);
    }
    
    Reflector reflector(){
        return i_config.reflector();
    }

    public void refresh(Object a_refresh, int a_depth) {
        synchronized (i_lock) {
            i_refreshInsteadOfActivate = true;
            activate1(null, a_refresh, a_depth);
            i_refreshInsteadOfActivate = false;
        }
    }

    final void refreshClasses() {
        synchronized (i_lock) {
            i_classCollection.refreshClasses();
        }
    }

    public abstract void releaseSemaphore(String name);

    abstract void releaseSemaphores(Transaction ta);

    void rename(Config4Impl config) {
        boolean renamedOne = false;
        if (config.i_rename != null) {
            renamedOne = rename1(config);
        }
        i_classCollection.checkChanges();
        if (renamedOne) {
            reboot();
        }
        i_entryCounter--; // denotes
    }

    protected boolean rename1(Config4Impl config) {
        boolean renamedOne = false;
        try {
            Iterator4 i = config.i_rename.iterator();
            while (i.hasNext()) {
                Rename ren = (Rename) i.next();
                if (get(ren).size() == 0) {
                    boolean renamed = false;

                    boolean isField = ren.rClass.length() > 0;
                    YapClass yapClass = i_classCollection
                        .getYapClass(isField ? ren.rClass : ren.rFrom);
                    if (yapClass != null) {
                        if (isField) {
                            renamed = yapClass.renameField(ren.rFrom, ren.rTo);
                        } else {
                            YapClass existing = i_classCollection
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
                        setDirty(yapClass);

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
            Db4o.logErr(i_config, 10, null, t);
        }
        return renamedOne;
    }

    public Db4oReplication replicateTo(ObjectContainer a_destination) {
        return new ReplicationImpl(this, a_destination);
    }

    void reserve(int byteCount) {
        // virtual: do nothing
    }

    public void rollback() {
        synchronized (i_lock) {
            rollback1();
        }
    }

    abstract void rollback1();

    public void send(Object obj) {
        // TODO: implement
        // so far this only works from YapClient
    }

    public void set(Object a_object) {
        synchronized (i_lock) {
            setExternal(null, a_object, YapConst.UNSPECIFIED);
        }
    }

    public void set(Object a_object, int a_depth) {
        synchronized (i_lock) {
            setExternal(null, a_object, a_depth);
        }
    }
    
    final void setExternal(Transaction ta, Object a_object, int a_depth) {
        beginEndSet(ta);
        ta = setInternal(ta, a_object, a_depth, true);
        beginEndSet(ta);
    }
    
    final Transaction setInternal(Transaction ta, Object a_object, boolean a_checkJustSet) {
       return setInternal(ta, a_object, YapConst.UNSPECIFIED, a_checkJustSet);
    }
    
    final Transaction setInternal(Transaction ta, Object a_object, int a_depth,  boolean a_checkJustSet) {
        ta = checkTransaction(ta);
        
        // The double check on i_migrateFrom is necessary:
        // i_handlers.i_replicateFrom may be set in YapObjectCarrier for parent YapStream 
        if(i_migrateFrom != null  && i_handlers.i_replication != null){
            
            if(i_handlers.i_replication.toDestination(a_object)){
                return ta;
            }
        }
        return setNoReplication(ta, a_object, a_depth, a_checkJustSet);
    }
    
    final Transaction setNoReplication(Transaction ta, Object a_object, int a_depth,  boolean a_checkJustSet) {
        if (a_object instanceof Db4oType) {
            if (db4oTypeStored(ta, a_object) != null) {
                return ta;
            }
        }
        if (Deploy.debug) {
            set2(ta, a_object, a_depth, a_checkJustSet);
        } else {
            try {
                set2(ta, a_object, a_depth, a_checkJustSet);
            } catch (ObjectNotStorableException e) {
                throw e;
            } catch (Throwable t) {
                fatalException(t);
            }
        }
        i_entryCounter--;
        return ta;
    }

    private final void set2(Transaction a_trans, Object a_object, int depth, boolean a_checkJustSet) {
        i_entryCounter++;
        set3(a_trans, a_object, depth, a_checkJustSet);
        checkStillToSet();
    }

    void checkStillToSet() {
        List4 postponedStillToSet = null;
        while (i_stillToSet != null) {
            Iterator4 i = new Iterator4(i_stillToSet);
            i_stillToSet = null;
            while (i.hasNext()) {
                Integer updateDepth = (Integer)i.next();
                YapObject yo = (YapObject)i.next();
                Transaction trans = (Transaction)i.next();
                if(! yo.continueSet(trans, updateDepth.intValue())){
                    postponedStillToSet = new List4(postponedStillToSet, trans);
                    postponedStillToSet = new List4(postponedStillToSet, yo);
                    postponedStillToSet = new List4(postponedStillToSet, updateDepth);
                }
            }
        }
        i_stillToSet = postponedStillToSet;
    }

    final int set3(Transaction a_trans, Object a_object, int a_updateDepth, boolean a_checkJustSet) {
        if (a_object != null & !(a_object instanceof TransientClass)) {
        	
            if (a_object instanceof Db4oTypeImpl) {
                ((Db4oTypeImpl) a_object).storedTo(a_trans);
            }
            YapClass yc = null;
            YapObject yapObject = i_hcTree.hc_find(a_object);
            if (yapObject == null) {
            	
                ReflectClass claxx = reflector().forObject(a_object);
                yc = getYapClass(claxx, false);
                if (yc == null) {
                    yc = getYapClass(claxx, true);
                    if (yc == null) {
                        return 0;
                    }

                    // The following may return a yapObject if the object is held
                    // in a static variable somewhere that gets stored on creation
                    // of the YapClass.
                }

                yapObject = i_hcTree.hc_find(a_object);
                
            } else {
                yc = yapObject.getYapClass();
            }
            boolean dontDelete = true;
            if (yapObject == null) {
                if (!yc.dispatchEvent(this, a_object, EventDispatcher.CAN_NEW)) {
                    return 0;
                }
                yapObject = new YapObject(0);
                if(i_migrateFrom != null  && i_handlers.i_replication != null){
                    i_handlers.i_replication.destinationOnNew(yapObject);
                }
                if(yapObject.store(a_trans, yc, a_object, a_updateDepth)){
    				idTreeAdd(yapObject);
    				hcTreeAdd(yapObject);
    				if(a_object instanceof Db4oTypeImpl){
    				    ((Db4oTypeImpl)a_object).setTrans(a_trans);
    				}
    				if (i_config.i_messageLevel > YapConst.STATE) {
    					message("" + yapObject.getID() + " new " + yapObject.getYapClass().getName());
    				}
    				stillToSet(a_trans, yapObject, a_updateDepth);
                }

            } else {
                if (canUpdate()) {
                    int oid = yapObject.getID();
                    if(a_checkJustSet && i_justSet != null){
                        if(oid > 0 && (TreeInt.find(i_justSet, yapObject.getID()) != null)){
                            return oid;
                        }
                    }
                    boolean doUpdate = (a_updateDepth == YapConst.UNSPECIFIED) || (a_updateDepth > 0);
                    if (doUpdate) {
                        dontDelete = false;
                        a_trans.dontDelete(oid, true);
                        yapObject.writeUpdate(a_trans, a_updateDepth);
                    }
                }
            }
            checkNeededUpdates();
            int id = yapObject.getID();
            if(canUpdate() && a_checkJustSet){
                if(! yapObject.getYapClass().isPrimitive()){
    	            if(i_justSet == null){
    	                i_justSet = new TreeInt(id);
    	            }else{
    	                i_justSet = i_justSet.add(new TreeInt(id));
    	            }
                }
            }
            if(dontDelete){
                
                // TODO: do we want primitive types added here?
                
                a_trans.dontDelete(id, false);
            }
            return id;
        }
        return 0;
    }

    abstract void setDirty(UseSystemTransaction a_object);

    public abstract boolean setSemaphore(String name, int timeout);

    void setStringIo(YapStringIO a_io) {
        i_stringIo = a_io;
        i_handlers.i_stringHandler.setStringIo(a_io);
    }

    boolean showInternalClasses() {
        return isServer() || i_showInternalClasses > 0;
    }

    /**
     * Objects implementing the "Internal" marker interface are
     * not visible to queries, unless this flag is set to true.
     * The caller should reset the flag after the call.
     */
    synchronized void showInternalClasses(boolean show) {
        if (show) {
            i_showInternalClasses++;
        } else {
            i_showInternalClasses--;
        }
        if (i_showInternalClasses < 0) {
            i_showInternalClasses = 0;
        }

    }

    boolean stateMessages() {
        return true; // overridden to do nothing in YapObjectCarrier
    }

    /**
     * returns true in case an unknown single object is passed
     * This allows deactivating objects before queries are called.
     */
    List4 stillTo1(List4 a_still, Tree[] a_just, Object a_object, int a_depth,
        boolean a_forceUnknownDeactivate) {
        if (a_object != null) {
            if (a_depth > 0) {
                YapObject yapObject = i_hcTree.hc_find(a_object);
                if (yapObject != null) {
                    int id = yapObject.getID();
                    if(a_just[0] != null){
                        if(((TreeInt)a_just[0]).find(id) != null){
                            return a_still;
                        }
                        a_just[0] = a_just[0].add(new TreeInt(id));
                    }else{
                        a_just[0] = new TreeInt(id);
                    }
                    return new List4(new List4(a_still, new Integer(a_depth)), yapObject);
                } else {
                    if (a_object.getClass().isArray()) {
                        Object[] arr = YapArray.toArray(this, a_object);
                        for (int i = 0; i < arr.length; i++) {
                            a_still = stillTo1(a_still, a_just, arr[i],
                                a_depth, a_forceUnknownDeactivate);
                        }
                    } else {
                        if (a_object instanceof Entry) {
                            a_still = stillTo1(a_still, a_just,
                                ((Entry) a_object).key, a_depth, false);
                            a_still = stillTo1(a_still, a_just,
                                ((Entry) a_object).value, a_depth, false);
                        } else {
                            if (a_forceUnknownDeactivate) {
                                // Special handling to deactivate Top-Level unknown objects only.
                                YapClass yc = getYapClass(reflector().forObject(a_object),
                                    false);
                                if (yc != null) {
                                    yc.deactivate(i_trans, a_object, a_depth);
                                }
                            }
                        }
                    }
                }
            }
        }
        return a_still;
    }

    void stillToActivate(Object a_object, int a_depth) {

        // TODO: We don't want the simple classes to search the hc_tree
        // Kick them out here.

        //		if (a_object != null) {
        //			Class clazz = a_object.getClass();
        //			if(! clazz.isPrimitive()){

        i_stillToActivate = stillTo1(i_stillToActivate, i_justActivated,
            a_object, a_depth, false);

        //			}
        //		}
    }

    void stillToDeactivate(Object a_object, int a_depth,
        boolean a_forceUnknownDeactivate) {
        i_stillToDeactivate = stillTo1(i_stillToDeactivate, i_justDeactivated,
            a_object, a_depth, a_forceUnknownDeactivate);
    }

    void stillToSet(Transaction a_trans, YapObject a_yapObject, int a_updateDepth) {
        i_stillToSet = new List4(i_stillToSet, a_trans);
        i_stillToSet = new List4(i_stillToSet, a_yapObject);
        i_stillToSet = new List4(i_stillToSet, new Integer(a_updateDepth));
    }

    void stopSession() {
        i_classCollection = null;
    }

    public StoredClass storedClass(Object clazz) {
        synchronized (i_lock) {
            checkClosed();
            return storedClass1(clazz);
        }
    }

    YapClass storedClass1(Object clazz) {
        try {
            
            ReflectClass claxx = i_config.reflectorFor(clazz);
            if (claxx != null) {
                return getYapClass(claxx, false);
            }
        } catch (Exception e) {
        }
        return null;
    }

    public StoredClass[] storedClasses() {
        synchronized (i_lock) {
            checkClosed();
            return i_classCollection.storedClasses();
        }
    }

    Object unmarshall(YapWriter yapBytes) {
        return unmarshall(yapBytes._buffer, yapBytes.getID());
    }

    Object unmarshall(byte[] bytes, int id) {
        MemoryFile memoryFile = new MemoryFile(bytes);
        YapObjectCarrier carrier = new YapObjectCarrier(this, memoryFile);
        Object obj = carrier.getByID(id);
        carrier.activate(obj, Integer.MAX_VALUE);
        carrier.close();
        return obj;
    }

    abstract YapWriter updateObject(Transaction a_trans, YapMeta a_object);

    abstract void write(boolean shuttingDown);

    abstract void writeDirty();

    abstract void writeEmbedded(YapWriter a_parent, YapWriter a_child);

    abstract void writeNew(YapClass a_yapClass, YapWriter aWriter);

    abstract void writeTransactionPointer(int a_address);

    abstract void writeUpdate(YapClass a_yapClass, YapWriter a_bytes);

    final void yapObjectGCd(YapObject yo) {
        
        // TODO: rename to removeReference 
        
        if(DTrace.enabled){
            DTrace.REFERENCE_REMOVED.log(yo.getID());
        }

        hcTreeRemove(yo);
        idTreeRemove(yo.getID());

        // setting the ID to minus 1 ensures that the
        // gc mechanism does not kill the new YapObject
        yo.setID(this, -1);
        Platform.killYapRef(yo.i_object);
    }


}