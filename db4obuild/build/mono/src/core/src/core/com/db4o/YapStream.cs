/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA  02111-1307, USA. */

using System;
using j4o.lang;
using j4o.io;
using com.db4o.config;
using com.db4o.ext;
using com.db4o.query;
using com.db4o.types;
namespace com.db4o {

   abstract internal class YapStream : ObjectContainer, ExtObjectContainer, TransientClass {
      static internal int HEADER_LENGTH = 18;
      private bool i_amDuringFatalExit = false;
      internal YapClassCollection i_classCollection;
      internal Config4Impl i_config;
      internal int i_entryCounter = -100;
      internal Tree i_freeOnCommit;
      private YapObject i_hcTree;
      private YapObject i_idTree;
      private Tree[] i_justActivated;
      private Tree[] i_justDeactivated;
      private Tree i_justPeeked;
      private Tree i_justSet;
      internal Object i_lock = new Object();
      private List4 i_needsUpdate;
      internal YapStream i_parent;
      internal bool i_refreshInsteadOfActivate;
      internal int i_showInternalClasses = 0;
      internal long i_startTime;
      private List4 i_stillToActivate;
      private List4 i_stillToDeactivate;
      private List4 i_stillToSet;
      internal YapStringIO i_stringIo;
      internal Transaction i_systemTrans;
      internal Transaction i_trans;
      private bool i_instantiating;
      internal YapHandlers i_handlers;
      internal YapStream i_migrateFrom;
      internal YapReferences i_references;
      
      internal YapStream(YapStream yapstream_0_) : base() {
         i_parent = yapstream_0_ == null ? this : yapstream_0_;
         initialize0();
         createTransaction();
         initialize1();
      }
      
      public void activate(Object obj, int i) {
         lock (i_lock) {
            activate1(null, obj, i);
         }
      }
      
      internal void activate1(Transaction transaction, Object obj) {
         activate1(transaction, obj, i_config.i_activationDepth);
      }
      
      internal void activate1(Transaction transaction, Object obj, int i) {
         transaction = checkTransaction(transaction);
         beginEndActivation();
         activate2(transaction, obj, i);
         beginEndActivation();
      }
      
      internal void beginEndActivation() {
         i_justActivated[0] = null;
      }
      
      internal void beginEndSet(Transaction transaction) {
         i_justSet = null;
         if (transaction != null) transaction.beginEndSet();
      }
      
      internal void activate2(Transaction transaction, Object obj, int i) {
         try {
            {
               i_entryCounter++;
               stillToActivate(obj, i);
               activate3CheckStill(transaction);
            }
         }  catch (Exception throwable) {
            {
               fatalException(throwable);
            }
         }
         i_entryCounter--;
      }
      
      internal void activate3CheckStill(Transaction transaction) {
         while (i_stillToActivate != null) {
            Iterator4 iterator41 = new Iterator4(i_stillToActivate);
            i_stillToActivate = null;
            while (iterator41.hasNext()) {
               YapObject yapobject1 = (YapObject)iterator41.next();
               int i1 = System.Convert.ToInt32((Int32)iterator41.next());
               Object obj1 = yapobject1.getObject();
               if (obj1 == null) yapObjectGCd(yapobject1); else yapobject1.activate1(transaction, obj1, i1, i_refreshInsteadOfActivate);
            }
         }
      }
      
      public void bind(Object obj, long l) {
         lock (i_lock) {
            bind1(null, obj, l);
         }
      }
      
      internal void bind1(Transaction transaction, Object obj, long l) {
         transaction = checkTransaction(transaction);
         int i1 = (int)l;
         if (obj != null) {
            Object obj_1_1 = getByID(l);
            if (obj_1_1 != null) {
               YapObject yapobject1 = getYapObject(i1);
               if (yapobject1 != null) {
                  if (j4o.lang.Class.getClassForObject(obj) == yapobject1.getJavaClass()) bind2(yapobject1, obj); else throw new RuntimeException(Messages.get(57));
               }
            }
         }
      }
      
      internal void bind2(YapObject yapobject, Object obj) {
         int i1 = yapobject.getID();
         yapObjectGCd(yapobject);
         yapobject = new YapObject(getYapClass(j4o.lang.Class.getClassForObject(obj), false), i1);
         yapobject.setObjectWeak(this, obj);
         yapobject.setStateDirty();
         idTreeAdd(yapobject);
         hcTreeAdd(yapobject);
      }
      
      internal virtual bool canUpdate() {
         return true;
      }
      
      internal void checkClosed() {
         if (i_classCollection == null) Db4o.throwRuntimeException(20, this.ToString());
      }
      
      internal void checkNeededUpdates() {
         if (i_needsUpdate != null) {
            Iterator4 iterator41 = new Iterator4(i_needsUpdate);
            while (iterator41.hasNext()) {
               YapClass yapclass1 = (YapClass)iterator41.next();
               yapclass1.setStateDirty();
               yapclass1.write(this, i_systemTrans);
            }
            i_needsUpdate = null;
         }
      }
      
      internal Transaction checkTransaction(Transaction transaction) {
         checkClosed();
         if (transaction != null) return transaction;
         return getTransaction();
      }
      
      public virtual bool close() {
         lock (Db4o.Lock) {
            lock (i_lock) {
               bool xbool1 = close1();
               return xbool1;
            }
         }
      }
      
      internal bool close1() {
         if (i_classCollection == null) return true;
         Platform.preClose(this);
         checkNeededUpdates();
         if (stateMessages()) logMsg(2, this.ToString());
         bool xbool1 = close2();
         return xbool1;
      }
      
      internal virtual bool close2() {
         if (hasShutDownHook()) Platform.removeShutDownHook(this, i_lock);
         i_classCollection = null;
         i_references.stopTimer();
         i_hcTree = null;
         i_idTree = null;
         i_systemTrans = null;
         i_trans = null;
         if (stateMessages()) logMsg(3, this.ToString());
         return true;
      }
      
      public Db4oCollections collections() {
         lock (i_lock) {
            if (i_handlers.i_collections == null) i_handlers.i_collections = Platform.collections(this);
            return i_handlers.i_collections;
         }
      }
      
      public void commit() {
         lock (i_lock) {
            commit1();
         }
      }
      
      abstract internal void commit1();
      
      public Configuration configure() {
         return i_config;
      }
      
      abstract internal ClassIndex createClassIndex(YapClass yapclass);
      
      abstract internal QResult createQResult(Transaction transaction);
      
      internal void createStringIO(byte i) {
         switch (i) {
         case 1: 
            setStringIo(new YapStringIO());
            break;
         
         case 2: 
            setStringIo(new YapStringIOUnicode());
            break;
         
         }
      }
      
      internal virtual void createTransaction() {
         i_systemTrans = new Transaction(this, null);
         i_trans = new Transaction(this, i_systemTrans);
      }
      
      abstract internal long currentVersion();
      
      internal virtual bool createYapClass(YapClass yapclass, Class var_class, YapClass yapclass_2_) {
         if (YapConst.CLASS_TRANSIENTCLASS.isAssignableFrom(var_class)) return false;
         Config4Class config4class1 = i_config.configClass(var_class.getName());
         yapclass.i_config = config4class1;
         yapclass.i_ancestor = yapclass_2_;
         Object obj1 = null;
         YapConstructor yapconstructor1;
         if (config4class1 != null && config4class1.instantiates()) yapconstructor1 = new YapConstructor(this, var_class, null, null, true, false); else {
            yapconstructor1 = i_handlers.createConstructorStatic(this, yapclass, var_class);
            if (yapconstructor1 == null) return false;
         }
         yapclass.init(this, yapclass_2_, yapconstructor1);
         return true;
      }
      
      internal Object db4oTypeStored(Transaction transaction, Object obj) {
         if (obj is Db4oDatabase) {
            Db4oDatabase db4odatabase1 = (Db4oDatabase)obj;
            if (getYapObject(obj) != null) return obj;
            Query query1 = querySharpenBug();
            query1.constrain(j4o.lang.Class.getClassForObject(db4odatabase1));
            query1.descend("i_uuid").constrain(System.Convert.ToInt64(db4odatabase1.i_uuid));
            ObjectSet objectset1 = query1.execute();
            while (objectset1.hasNext()) {
               Db4oDatabase db4odatabase_3_1 = (Db4oDatabase)objectset1.next();
               activate1(null, db4odatabase_3_1, 4);
               if (db4odatabase_3_1.Equals(obj)) return db4odatabase_3_1;
            }
         }
         return null;
      }
      
      public void deactivate(Object obj, int i) {
         lock (i_lock) {
            deactivate1(obj, i);
         }
      }
      
      internal void deactivate1(Object obj, int i) {
         checkClosed();
         try {
            {
               i_entryCounter++;
               i_justDeactivated[0] = null;
               deactivate2(obj, i);
               i_justDeactivated[0] = null;
            }
         }  catch (Exception throwable) {
            {
               fatalException(throwable);
            }
         }
         i_entryCounter--;
      }
      
      private void deactivate2(Object obj, int i) {
         stillToDeactivate(obj, i, true);
         while (i_stillToDeactivate != null) {
            Iterator4 iterator41 = new Iterator4(i_stillToDeactivate);
            i_stillToDeactivate = null;
            while (iterator41.hasNext()) ((YapObject)iterator41.next()).deactivate(i_trans, System.Convert.ToInt32((Int32)iterator41.next()));
         }
      }
      
      public void delete(Object obj) {
         lock (i_lock) {
            Transaction transaction1 = delete1(null, obj);
            transaction1.beginEndSet();
         }
      }
      
      internal Transaction delete1(Transaction transaction, Object obj) {
         transaction = checkTransaction(transaction);
         if (obj != null) {
            try {
               {
                  delete2(transaction, obj);
               }
            }  catch (Exception throwable) {
               {
                  fatalException(throwable);
               }
            }
            i_entryCounter--;
         }
         return transaction;
      }
      
      private void delete2(Transaction transaction, Object obj) {
         i_entryCounter++;
         YapObject yapobject1 = getYapObject(obj);
         if (yapobject1 != null) {
            int i1 = yapobject1.getID();
            delete3(transaction, yapobject1, obj, 0);
         }
      }
      
      internal void delete3(Transaction transaction, YapObject yapobject, Object obj, int i) {
         if (obj is SecondClass) delete4(transaction, yapobject, obj, i); else transaction.delete(yapobject, obj, i, true);
      }
      
      internal void delete4(Transaction transaction, YapObject yapobject, Object obj, int i) {
         bool xbool1 = false;
         if (yapobject.beginProcessing()) {
            YapClass yapclass1 = yapobject.getYapClass();
            Object obj_4_1 = yapobject.getObject();
            if (yapclass1.dispatchEvent(this, obj_4_1, 0) && delete5(transaction, yapobject, i)) {
               yapclass1.dispatchEvent(this, obj_4_1, 1);
               if (i_config.i_messageLevel > 1) message("" + yapobject.getID() + " delete " + yapobject.getYapClass().getName());
            }
            yapobject.endProcessing();
         }
      }
      
      abstract internal bool delete5(Transaction transaction, YapObject yapobject, int i);
      
      internal virtual bool detectSchemaChanges() {
         return i_config.i_detectSchemaChanges;
      }
      
      public virtual bool dispatchsEvents() {
         return true;
      }
      
      internal virtual void emergencyClose() {
         i_classCollection = null;
         i_references.stopTimer();
      }
      
      public ExtObjectContainer ext() {
         return this;
      }
      
      internal void failedToShutDown() {
         lock (Db4o.Lock) {
            if (i_classCollection != null) {
               if (i_entryCounter == 0) {
                  Db4o.logErr(i_config, 50, this.ToString(), null);
                  while (!close()) {
                  }
               } else {
                  emergencyClose();
                  if (i_entryCounter > 0) Db4o.logErr(i_config, 24, null, null);
               }
            }
         }
      }
      
      internal void fatalException(Exception throwable) {
         if (!i_amDuringFatalExit) {
            i_amDuringFatalExit = true;
            i_classCollection = null;
            emergencyClose();
            Db4o.logErr(i_config, 18, null, throwable);
         }
         throw new RuntimeException(Messages.get(44));
      }
      
      protected void finalize() {
         if (i_config == null || i_config.i_automaticShutDown) failedToShutDown();
      }
      
      internal void gc() {
         i_references.run();
      }
      
      public ObjectSet get(Object obj) {
         lock (i_lock) {
            return get1(null, obj);
         }
      }
      
      internal ObjectSet get1(Transaction transaction, Object obj) {
         transaction = checkTransaction(transaction);
         QResult qresult1 = createQResult(transaction);
         try {
            {
               get2(transaction, obj, qresult1);
            }
         }  catch (Exception throwable) {
            {
               fatalException(throwable);
            }
         }
         i_entryCounter--;
         qresult1.reset();
         return qresult1;
      }
      
      private void get2(Transaction transaction, Object obj, QResult qresult) {
         i_entryCounter++;
         if (obj == null || j4o.lang.Class.getClassForObject(obj) == YapConst.CLASS_OBJECT) getAll(transaction, qresult); else {
            Query query1 = querySharpenBug(transaction);
            query1.constrain(obj);
            ((QQuery)query1).execute1(qresult);
         }
      }
      
      abstract internal void getAll(Transaction transaction, QResult qresult);
      
      public Object getByID(long l) {
         lock (i_lock) {
            return getByID1(null, l);
         }
      }
      
      internal Object getByID1(Transaction transaction, long l) {
         transaction = checkTransaction(transaction);
         try {
            {
               return getByID2(transaction, (int)l);
            }
         }  catch (Exception exception) {
            {
               return null;
            }
         }
      }
      
      internal Object getByID2(Transaction transaction, int i) {
         if (i > 0) {
            YapObject yapobject1 = getYapObject(i);
            if (yapobject1 != null) {
               Object obj1 = yapobject1.getObject();
               if (obj1 != null) return obj1;
               yapObjectGCd(yapobject1);
            }
            try {
               {
                  return new YapObject(i).read(transaction, null, null, 0, 1);
               }
            }  catch (Exception throwable) {
               {
               }
            }
         }
         return null;
      }
      
      public Object getByUUID(Db4oUUID db4ouuid) {
         lock (i_lock) {
            Transaction transaction1 = checkTransaction(null);
            Object[] objs1 = transaction1.objectAndYapObjectBySignature(db4ouuid.getLongPart(), db4ouuid.getSignaturePart());
            return objs1[0];
         }
      }
      
      public long getID(Object obj) {
         lock (i_lock) {
            return (long)getID1(null, obj);
         }
      }
      
      internal int getID1(Transaction transaction, Object obj) {
         checkClosed();
         YapObject yapobject1 = i_hcTree.hc_find(obj);
         if (yapobject1 != null) return yapobject1.getID();
         return 0;
      }
      
      public ObjectInfo getObjectInfo(Object obj) {
         lock (i_lock) {
            return getYapObject(obj);
         }
      }
      
      internal Object[] getObjectAndYapObjectByID(Transaction transaction, int i) {
         Object[] objs1 = new Object[2];
         if (i > 0) {
            YapObject yapobject1 = getYapObject(i);
            if (yapobject1 != null) {
               Object obj1 = yapobject1.getObject();
               if (obj1 != null) {
                  objs1[0] = obj1;
                  objs1[1] = yapobject1;
                  return objs1;
               }
               yapObjectGCd(yapobject1);
            }
            try {
               {
                  yapobject1 = new YapObject(i);
                  objs1[0] = yapobject1.read(transaction, null, null, 0, 1);
                  objs1[1] = yapobject1;
               }
            }  catch (Exception throwable) {
               {
               }
            }
         }
         return objs1;
      }
      
      internal YapWriter getWriter(Transaction transaction, int i) {
         return new YapWriter(transaction, i);
      }
      
      internal YapWriter getWriter(Transaction transaction, int i, int i_5_) {
         if (Debug.exceedsMaximumBlockSize(i_5_)) return null;
         return new YapWriter(transaction, i, i_5_);
      }
      
      internal Transaction getSystemTransaction() {
         return i_systemTrans;
      }
      
      internal Transaction getTransaction() {
         return i_trans;
      }
      
      internal YapClass getYapClass(Class var_class, bool xbool) {
         if (var_class == null) return null;
         if (!showInternalClasses() && YapConst.CLASS_INTERNAL.isAssignableFrom(var_class)) return null;
         YapClass yapclass1 = i_handlers.getYapClassStatic(var_class);
         if (yapclass1 != null) return yapclass1;
         return i_classCollection.getYapClass(var_class, xbool);
      }
      
      internal virtual YapClass getYapClass(int i) {
         if (i == 0) return null;
         YapClass yapclass1 = i_handlers.getYapClassStatic(i);
         if (yapclass1 != null) return yapclass1;
         return i_classCollection.getYapClass(i);
      }
      
      internal YapObject getYapObject(int i) {
         return i_idTree.id_find(i);
      }
      
      internal YapObject getYapObject(Object obj) {
         return i_hcTree.hc_find(obj);
      }
      
      public YapHandlers handlers() {
         return i_handlers;
      }
      
      internal virtual bool needsLockFileThread() {
         if (!Platform.hasLockFileThread()) return false;
         if (Platform.hasNio()) return false;
         if (i_config.i_readonly) return false;
         return i_config.i_lockFile;
      }
      
      internal virtual bool hasShutDownHook() {
         return i_config.i_automaticShutDown;
      }
      
      internal void hcTreeAdd(YapObject yapobject) {
         i_hcTree = i_hcTree.hc_add(yapobject);
      }
      
      internal void hcTreeRemove(YapObject yapobject) {
         i_hcTree = i_hcTree.hc_remove(yapobject);
      }
      
      internal void idTreeAdd(YapObject yapobject) {
         i_idTree = i_idTree.id_add(yapobject);
      }
      
      internal void idTreeRemove(int i) {
         i_idTree = i_idTree.id_remove(i);
      }
      
      internal void initialize0() {
         initialize0b();
         i_stillToSet = null;
         i_justActivated = new Tree[1];
      }
      
      internal virtual void initialize0b() {
         i_justDeactivated = new Tree[1];
         i_handlers = new YapHandlers(this);
         setStringIo(new YapStringIOUnicode());
      }
      
      internal virtual void initialize1() {
         try {
            {
               i_config = (Config4Impl)((DeepClone)Db4o.configure()).deepClone(this);
               createStringIO(i_config.i_encoding);
            }
         }  catch (Exception exception) {
            {
            }
         }
         if (i_references != null) {
            gc();
            i_references.stopTimer();
         }
         i_references = new YapReferences(this);
         if (hasShutDownHook()) Platform.addShutDownHook(this, i_lock);
         i_handlers.initEncryption(i_config);
         initialize2();
         i_stillToSet = null;
      }
      
      internal virtual void initialize2() {
         i_idTree = new YapObject(0);
         i_idTree.setObject(new Object());
         i_hcTree = i_idTree;
         initialize2b();
      }
      
      internal virtual void initialize2b() {
         i_classCollection = new YapClassCollection(i_systemTrans);
         i_references.startTimer();
      }
      
      internal void initialize3() {
         i_showInternalClasses = 100000;
         initialize4NObjectCarrier();
         i_showInternalClasses = 0;
         i_entryCounter = 0;
      }
      
      internal virtual void initialize4NObjectCarrier() {
         initializeEssentialClasses();
         rename(i_config);
         i_classCollection.initOnUp(i_systemTrans);
         if (i_config.i_detectSchemaChanges) i_systemTrans.commit();
      }
      
      internal virtual void initializeEssentialClasses() {
         for (int i1 = 0; i1 < YapConst.ESSENTIAL_CLASSES.Length; i1++) getYapClass(YapConst.ESSENTIAL_CLASSES[i1], true);
      }
      
      internal void instantiating(bool xbool) {
         i_instantiating = xbool;
      }
      
      public bool isActive(Object obj) {
         lock (i_lock) {
            return isActive1(obj);
         }
      }
      
      internal bool isActive1(Object obj) {
         checkClosed();
         if (obj != null) {
            YapObject yapobject1 = getYapObject(obj);
            if (yapobject1 != null) return yapobject1.isActive();
         }
         return false;
      }
      
      public bool isCached(long l) {
         lock (i_lock) {
            if (l > 0L) {
               YapObject yapobject1 = getYapObject((int)l);
               if (yapobject1 != null) {
                  Object obj1 = yapobject1.getObject();
                  if (obj1 != null) return true;
               }
            }
            return false;
         }
      }
      
      internal virtual bool isClient() {
         return false;
      }
      
      public bool isClosed() {
         lock (i_lock) {
            return i_classCollection == null;
         }
      }
      
      internal bool isInstantiating() {
         return i_instantiating;
      }
      
      internal virtual bool isServer() {
         return false;
      }
      
      public bool isStored(Object obj) {
         lock (i_lock) {
            return isStored1(obj);
         }
      }
      
      internal bool isStored1(Object obj) {
         Transaction transaction1 = checkTransaction(null);
         if (obj == null) return false;
         YapObject yapobject1 = getYapObject(obj);
         if (yapobject1 == null) return false;
         return !transaction1.isDeleted(yapobject1.getID());
      }
      
      public Object Lock() {
         return i_lock;
      }
      
      internal void logMsg(int i, String xstring) {
         Db4o.logMsg(i_config, i, xstring);
      }
      
      internal virtual bool maintainsIndices() {
         return true;
      }
      
      internal YapWriter marshall(Transaction transaction, Object obj) {
         int[] xis1 = {
            0         };
         byte[] is_6_1 = marshall(obj, xis1);
         YapWriter yapwriter1 = new YapWriter(transaction, is_6_1.Length);
         yapwriter1.append(is_6_1);
         yapwriter1.useSlot(xis1[0], 0, is_6_1.Length);
         return yapwriter1;
      }
      
      internal byte[] marshall(Object obj, int[] xis) {
         MemoryFile memoryfile1 = new MemoryFile();
         memoryfile1.setInitialSize(223);
         memoryfile1.setIncrementSizeBy(300);
         getYapClass(j4o.lang.Class.getClassForObject(obj), true);
         YapObjectCarrier yapobjectcarrier1 = new YapObjectCarrier(this, memoryfile1);
         yapobjectcarrier1.i_showInternalClasses = i_showInternalClasses;
         yapobjectcarrier1.set(obj);
         xis[0] = (int)yapobjectcarrier1.getID(obj);
         yapobjectcarrier1.close();
         return memoryfile1.getBytes();
      }
      
      internal virtual void message(String xstring) {
         new Message(this, xstring);
      }
      
      public void migrateFrom(ObjectContainer objectcontainer) {
         i_migrateFrom = (YapStream)objectcontainer;
      }
      
      internal void needsUpdate(YapClass yapclass) {
         i_needsUpdate = new List4(i_needsUpdate, yapclass);
      }
      
      abstract internal YapWriter newObject(Transaction transaction, YapMeta yapmeta);
      
      abstract internal int newUserObject();
      
      public Object peekPersisted(Object obj, int i, bool xbool) {
         lock (i_lock) {
            checkClosed();
            i_entryCounter++;
            i_justPeeked = null;
            Transaction transaction1 = xbool ? i_systemTrans : checkTransaction(null);
            Object obj_7_1 = null;
            YapObject yapobject1 = getYapObject(obj);
            if (yapobject1 != null) obj_7_1 = peekPersisted1(transaction1, yapobject1.getID(), i);
            i_justPeeked = null;
            i_entryCounter--;
            return obj_7_1;
         }
      }
      
      internal Object peekPersisted1(Transaction transaction, int i, int i_8_) {
         TreeInt treeint1 = new TreeInt(i);
         TreeIntObject treeintobject1 = (TreeIntObject)Tree.find(i_justPeeked, treeint1);
         if (treeintobject1 == null) return new YapObject(i).read(transaction, null, null, i_8_, -1);
         return treeintobject1.i_object;
      }
      
      internal void peeked(int i, Object obj) {
         i_justPeeked = Tree.add(i_justPeeked, new TreeIntObject(i, obj));
      }
      
      public void purge() {
         lock (i_lock) {
            purge1();
         }
      }
      
      public void purge(Object obj) {
         lock (i_lock) {
            purge1(obj);
         }
      }
      
      internal void purge1() {
         checkClosed();
         j4o.lang.JavaSystem.gc();
         j4o.lang.JavaSystem.runFinalization();
         j4o.lang.JavaSystem.gc();
         gc();
         i_classCollection.purge();
      }
      
      internal void purge1(Object obj) {
         if (obj != null && i_hcTree != null) {
            Object obj_9_1 = null;
            YapObject yapobject1;
            if (obj is YapObject) yapobject1 = (YapObject)obj; else yapobject1 = i_hcTree.hc_find(obj);
            if (yapobject1 != null) yapObjectGCd(yapobject1);
         }
      }
      
      public Query query() {
         lock (i_lock) {
            return query(null);
         }
      }
      
      internal Query query(Transaction transaction) {
         i_entryCounter++;
         QQuery qquery1 = new QQuery(checkTransaction(transaction), null, null);
         i_entryCounter--;
         return qquery1;
      }
      
      internal Query querySharpenBug() {
         return query();
      }
      
      internal Query querySharpenBug(Transaction transaction) {
         return query(transaction);
      }
      
      abstract internal void raiseVersion(long l);
      
      abstract internal void readBytes(byte[] xis, int i, int i_10_);
      
      abstract internal void readBytes(byte[] xis, int i, int i_11_, int i_12_);
      
      internal YapReader readObjectReaderByAddress(int i, int i_13_) {
         if (i > 0) {
            YapReader yapreader1 = new YapReader(i_13_);
            readBytes(yapreader1._buffer, i, i_13_);
            i_handlers.decrypt(yapreader1);
            return yapreader1;
         }
         return null;
      }
      
      internal YapWriter readObjectWriterByAddress(Transaction transaction, int i, int i_14_) {
         if (i > 0) {
            YapWriter yapwriter1 = getWriter(transaction, i, i_14_);
            yapwriter1.readEncrypt(this, i);
            return yapwriter1;
         }
         return null;
      }
      
      abstract internal YapWriter readWriterByID(Transaction transaction, int i);
      
      abstract internal YapReader readReaderByID(Transaction transaction, int i);
      
      private void reboot() {
         commit();
         int i1 = i_classCollection.getID();
         i_references.stopTimer();
         initialize2();
         i_classCollection.setID(this, i1);
         i_classCollection.read(i_systemTrans);
      }
      
      public void refresh(Object obj, int i) {
         lock (i_lock) {
            i_refreshInsteadOfActivate = true;
            activate1(null, obj, i);
            i_refreshInsteadOfActivate = false;
         }
      }
      
      internal void refreshClasses() {
         lock (i_lock) {
            i_classCollection.refreshClasses();
         }
      }
      
      public abstract void releaseSemaphore(String xstring);
      
      abstract internal void releaseSemaphores(Transaction transaction);
      
      internal void rename(Config4Impl config4impl) {
         bool xbool1 = false;
         if (config4impl.i_rename != null) xbool1 = rename1(config4impl);
         i_classCollection.checkChanges();
         if (xbool1) reboot();
         i_entryCounter--;
      }
      
      protected virtual bool rename1(Config4Impl config4impl) {
         bool xbool1 = false;
         try {
            {
               Iterator4 iterator41 = config4impl.i_rename.iterator();
               while (iterator41.hasNext()) {
                  Rename rename1 = (Rename)iterator41.next();
                  if (get(rename1).size() == 0) {
                     bool bool_15_1 = false;
                     bool bool_16_1 = j4o.lang.JavaSystem.getLengthOf(rename1.rClass) > 0;
                     YapClass yapclass1 = i_classCollection.getYapClass(bool_16_1 ? rename1.rClass : rename1.rFrom);
                     if (yapclass1 != null) {
                        if (bool_16_1) bool_15_1 = yapclass1.renameField(rename1.rFrom, rename1.rTo); else {
                           YapClass yapclass_17_1 = i_classCollection.getYapClass(rename1.rTo);
                           if (yapclass_17_1 == null) {
                              yapclass1.setName(rename1.rTo);
                              bool_15_1 = true;
                           } else logMsg(9, "class " + rename1.rTo);
                        }
                     }
                     if (bool_15_1) {
                        xbool1 = true;
                        setDirty(yapclass1);
                        logMsg(8, rename1.rFrom + " to " + rename1.rTo);
                        ObjectSet objectset1 = get(new Rename(rename1.rClass, null, rename1.rFrom));
                        while (objectset1.hasNext()) delete(objectset1.next());
                        set(rename1);
                     }
                  }
               }
            }
         }  catch (Exception throwable) {
            {
               Db4o.logErr(i_config, 10, null, throwable);
            }
         }
         return xbool1;
      }
      
      public Db4oReplication replicateTo(ObjectContainer objectcontainer) {
         return new ReplicationImpl(this, objectcontainer);
      }
      
      internal virtual void reserve(int i) {
      }
      
      public void rollback() {
         lock (i_lock) {
            rollback1();
         }
      }
      
      abstract internal void rollback1();
      
      public virtual void send(Object obj) {
      }
      
      public void set(Object obj) {
         lock (i_lock) {
            setExternal(null, obj, -2147483548);
         }
      }
      
      public void set(Object obj, int i) {
         lock (i_lock) {
            setExternal(null, obj, i);
         }
      }
      
      internal void setExternal(Transaction transaction, Object obj, int i) {
         beginEndSet(transaction);
         transaction = setInternal(transaction, obj, i, true);
         beginEndSet(transaction);
      }
      
      internal Transaction setInternal(Transaction transaction, Object obj, bool xbool) {
         return setInternal(transaction, obj, -2147483548, xbool);
      }
      
      internal Transaction setInternal(Transaction transaction, Object obj, int i, bool xbool) {
         transaction = checkTransaction(transaction);
         if (i_migrateFrom != null && i_handlers.i_replication != null && i_handlers.i_replication.toDestination(obj)) return transaction;
         return setNoReplication(transaction, obj, i, xbool);
      }
      
      internal Transaction setNoReplication(Transaction transaction, Object obj, int i, bool xbool) {
         if (obj is Db4oType && db4oTypeStored(transaction, obj) != null) return transaction;
         try {
            {
               set2(transaction, obj, i, xbool);
            }
         }  catch (ObjectNotStorableException objectnotstorableexception) {
            {
               throw objectnotstorableexception;
            }
         } catch (Exception throwable) {
            {
               fatalException(throwable);
            }
         }
         i_entryCounter--;
         return transaction;
      }
      
      private void set2(Transaction transaction, Object obj, int i, bool xbool) {
         i_entryCounter++;
         set3(transaction, obj, i, xbool);
         checkStillToSet();
      }
      
      internal void checkStillToSet() {
         List4 list41 = null;
         while (i_stillToSet != null) {
            Iterator4 iterator41 = new Iterator4(i_stillToSet);
            i_stillToSet = null;
            while (iterator41.hasNext()) {
               Int32 integer1 = (Int32)iterator41.next();
               YapObject yapobject1 = (YapObject)iterator41.next();
               Transaction transaction1 = (Transaction)iterator41.next();
               if (!yapobject1.continueSet(transaction1, System.Convert.ToInt32(integer1))) {
                  list41 = new List4(list41, transaction1);
                  list41 = new List4(list41, yapobject1);
                  list41 = new List4(list41, integer1);
               }
            }
         }
         i_stillToSet = list41;
      }
      
      internal int set3(Transaction transaction, Object obj, int i, bool xbool) {
         if (obj != null & !(obj is TransientClass)) {
            if (obj is Db4oTypeImpl) ((Db4oTypeImpl)obj).storedTo(transaction);
            Object obj_18_1 = null;
            YapObject yapobject1 = i_hcTree.hc_find(obj);
            YapClass yapclass1;
            if (yapobject1 == null) {
               Class var_class1 = j4o.lang.Class.getClassForObject(obj);
               yapclass1 = getYapClass(var_class1, false);
               if (yapclass1 == null) {
                  yapclass1 = getYapClass(var_class1, true);
                  if (yapclass1 == null) return 0;
               }
               yapobject1 = i_hcTree.hc_find(obj);
            } else yapclass1 = yapobject1.getYapClass();
            bool bool_19_1 = true;
            if (yapobject1 == null) {
               if (!yapclass1.dispatchEvent(this, obj, 8)) return 0;
               yapobject1 = new YapObject(0);
               if (i_migrateFrom != null && i_handlers.i_replication != null) i_handlers.i_replication.destinationOnNew(yapobject1);
               if (yapobject1.store(transaction, yapclass1, obj, i)) {
                  idTreeAdd(yapobject1);
                  hcTreeAdd(yapobject1);
                  if (obj is Db4oTypeImpl) ((Db4oTypeImpl)obj).setTrans(transaction);
                  if (i_config.i_messageLevel > 1) message("" + yapobject1.getID() + " new " + yapobject1.getYapClass().getName());
                  stillToSet(transaction, yapobject1, i);
               }
            } else if (canUpdate()) {
               int i_20_1 = yapobject1.getID();
               if (xbool && i_justSet != null && i_20_1 > 0 && TreeInt.find(i_justSet, yapobject1.getID()) != null) return i_20_1;
               bool bool_21_1 = i == -2147483548 || i > 0;
               if (bool_21_1) {
                  bool_19_1 = false;
                  transaction.dontDelete(i_20_1, true);
                  yapobject1.writeUpdate(transaction, i);
               }
            }
            checkNeededUpdates();
            int i_22_1 = yapobject1.getID();
            if (canUpdate() && xbool) {
               if (i_justSet == null) i_justSet = new TreeInt(i_22_1); else i_justSet = i_justSet.add(new TreeInt(i_22_1));
            }
            if (bool_19_1) transaction.dontDelete(i_22_1, false);
            return i_22_1;
         }
         return 0;
      }
      
      abstract internal void setDirty(UseSystemTransaction usesystemtransaction);
      
      public abstract bool setSemaphore(String xstring, int i);
      
      internal void setStringIo(YapStringIO yapstringio) {
         i_stringIo = yapstringio;
         i_handlers.i_stringHandler.setStringIo(yapstringio);
      }
      
      internal bool showInternalClasses() {
         return isServer() || i_showInternalClasses > 0;
      }
      
      internal void showInternalClasses(bool xbool) {
         if (xbool) i_showInternalClasses++; else i_showInternalClasses--;
         if (i_showInternalClasses < 0) i_showInternalClasses = 0;
      }
      
      internal virtual bool stateMessages() {
         return true;
      }
      
      internal List4 stillTo1(List4 list4, Tree[] trees, Object obj, int i, bool xbool) {
         if (obj != null && i > 0) {
            YapObject yapobject1 = i_hcTree.hc_find(obj);
            if (yapobject1 != null) {
               int i_23_1 = yapobject1.getID();
               if (trees[0] != null) {
                  if (((TreeInt)trees[0]).find(i_23_1) != null) return list4;
                  trees[0] = trees[0].add(new TreeInt(i_23_1));
               } else trees[0] = new TreeInt(i_23_1);
               return new List4(new List4(list4, System.Convert.ToInt32(i)), yapobject1);
            }
            if (j4o.lang.Class.getClassForObject(obj).isArray()) {
               Object[] objs1 = YapArray.toArray(obj);
               for (int i_24_1 = 0; i_24_1 < objs1.Length; i_24_1++) list4 = stillTo1(list4, trees, objs1[i_24_1], i, xbool);
            } else if (obj is Entry) {
               list4 = stillTo1(list4, trees, ((Entry)obj).key, i, false);
               list4 = stillTo1(list4, trees, ((Entry)obj).value, i, false);
            } else if (xbool) {
               YapClass yapclass1 = getYapClass(j4o.lang.Class.getClassForObject(obj), false);
               if (yapclass1 != null) yapclass1.deactivate(i_trans, obj, i);
            }
         }
         return list4;
      }
      
      internal void stillToActivate(Object obj, int i) {
         i_stillToActivate = stillTo1(i_stillToActivate, i_justActivated, obj, i, false);
      }
      
      internal void stillToDeactivate(Object obj, int i, bool xbool) {
         i_stillToDeactivate = stillTo1(i_stillToDeactivate, i_justDeactivated, obj, i, xbool);
      }
      
      internal void stillToSet(Transaction transaction, YapObject yapobject, int i) {
         i_stillToSet = new List4(i_stillToSet, transaction);
         i_stillToSet = new List4(i_stillToSet, yapobject);
         i_stillToSet = new List4(i_stillToSet, System.Convert.ToInt32(i));
      }
      
      internal void stopSession() {
         i_classCollection = null;
      }
      
      public StoredClass storedClass(Object obj) {
         lock (i_lock) {
            checkClosed();
            return storedClass1(obj);
         }
      }
      
      internal YapClass storedClass1(Object obj) {
         try {
            {
               String xstring1 = Config4Impl.classNameFor(obj);
               if (xstring1 != null) return getYapClass(Db4o.classForName(this, xstring1), false);
            }
         }  catch (Exception exception) {
            {
            }
         }
         return null;
      }
      
      public StoredClass[] storedClasses() {
         lock (i_lock) {
            checkClosed();
            return i_classCollection.storedClasses();
         }
      }
      
      internal Object unmarshall(YapWriter yapwriter) {
         return unmarshall(yapwriter._buffer, yapwriter.getID());
      }
      
      internal Object unmarshall(byte[] xis, int i) {
         MemoryFile memoryfile1 = new MemoryFile(xis);
         YapObjectCarrier yapobjectcarrier1 = new YapObjectCarrier(this, memoryfile1);
         Object obj1 = yapobjectcarrier1.getByID((long)i);
         yapobjectcarrier1.activate(obj1, 2147483647);
         yapobjectcarrier1.close();
         return obj1;
      }
      
      abstract internal YapWriter updateObject(Transaction transaction, YapMeta yapmeta);
      
      abstract internal void write(bool xbool);
      
      abstract internal void writeDirty();
      
      abstract internal void writeEmbedded(YapWriter yapwriter, YapWriter yapwriter_25_);
      
      abstract internal void writeNew(YapClass yapclass, YapWriter yapwriter);
      
      abstract internal void writeTransactionPointer(int i);
      
      abstract internal void writeUpdate(YapClass yapclass, YapWriter yapwriter);
      
      internal void yapObjectGCd(YapObject yapobject) {
         hcTreeRemove(yapobject);
         idTreeRemove(yapobject.getID());
         yapobject.setID(this, -1);
         Platform.killYapRef(yapobject.i_object);
      }
      
      public abstract Db4oDatabase identity();
      
      public abstract void backup(String xstring);
   }
}