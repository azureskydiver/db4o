namespace com.db4o
{
	/// <exclude></exclude>
	#if NET_2_0
	public abstract partial class YapStream : com.db4o.ObjectContainer, com.db4o.ext.ExtObjectContainer
		, com.db4o.types.TransientClass
	#else
	public abstract class YapStream : com.db4o.ObjectContainer, com.db4o.ext.ExtObjectContainer
		, com.db4o.types.TransientClass
	#endif
	{
		public const int HEADER_LENGTH = 2 + (com.db4o.YapConst.YAPINT_LENGTH * 4);

		private bool i_amDuringFatalExit = false;

		public com.db4o.YapClassCollection i_classCollection;

		public com.db4o.Config4Impl i_config;

		protected int i_entryCounter;

		internal com.db4o.Tree i_freeOnCommit;

		private com.db4o.YapObject i_hcTree;

		private com.db4o.YapObject i_idTree;

		private com.db4o.Tree[] i_justActivated;

		private com.db4o.Tree[] i_justDeactivated;

		private com.db4o.Tree i_justPeeked;

		private com.db4o.Tree i_justSet;

		internal readonly object i_lock = new object();

		private com.db4o.foundation.List4 i_needsUpdate;

		internal readonly com.db4o.YapStream i_parent;

		internal bool i_refreshInsteadOfActivate;

		internal int i_showInternalClasses = 0;

		internal long i_startTime;

		private com.db4o.foundation.List4 i_stillToActivate;

		private com.db4o.foundation.List4 i_stillToDeactivate;

		private com.db4o.foundation.List4 i_stillToSet;

		public com.db4o.Transaction i_systemTrans;

		internal com.db4o.Transaction i_trans;

		private bool i_instantiating;

		public com.db4o.YapHandlers i_handlers;

		internal com.db4o.YapStream i_migrateFrom;

		internal com.db4o.YapReferences i_references;

		private com.db4o.inside.query.NativeQueryHandler _nativeQueryHandler;

		internal YapStream(com.db4o.YapStream a_parent)
		{
			i_parent = a_parent == null ? this : a_parent;
			initialize0();
			createTransaction();
			initialize1();
		}

		public virtual void activate(object a_activate, int a_depth)
		{
			lock (i_lock)
			{
				activate1(null, a_activate, a_depth);
			}
		}

		internal void activate1(com.db4o.Transaction ta, object a_activate)
		{
			activate1(ta, a_activate, i_config.i_activationDepth);
		}

		internal void activate1(com.db4o.Transaction ta, object a_activate, int a_depth)
		{
			ta = checkTransaction(ta);
			beginEndActivation();
			activate2(ta, a_activate, a_depth);
			beginEndActivation();
		}

		internal void beginEndActivation()
		{
			i_justActivated[0] = null;
		}

		internal void beginEndSet(com.db4o.Transaction ta)
		{
			i_justSet = null;
			if (ta != null)
			{
				ta.beginEndSet();
			}
		}

		/// <summary>internal call interface, does not reset i_justActivated</summary>
		internal void activate2(com.db4o.Transaction ta, object a_activate, int a_depth)
		{
			i_entryCounter++;
			try
			{
				stillToActivate(a_activate, a_depth);
				activate3CheckStill(ta);
			}
			catch (System.Exception t)
			{
				fatalException(t);
			}
			i_entryCounter--;
		}

		internal void activate3CheckStill(com.db4o.Transaction ta)
		{
			while (i_stillToActivate != null)
			{
				com.db4o.foundation.Iterator4 i = new com.db4o.foundation.Iterator4Impl(i_stillToActivate
					);
				i_stillToActivate = null;
				while (i.hasNext())
				{
					com.db4o.YapObject yo = (com.db4o.YapObject)i.next();
					int depth = ((int)i.next());
					object obj = yo.getObject();
					if (obj == null)
					{
						yapObjectGCd(yo);
					}
					else
					{
						yo.activate1(ta, obj, depth, i_refreshInsteadOfActivate);
					}
				}
			}
		}

		public virtual void bind(object obj, long id)
		{
			lock (i_lock)
			{
				bind1(null, obj, id);
			}
		}

		/// <summary>TODO: This is not transactional yet.</summary>
		/// <remarks>TODO: This is not transactional yet.</remarks>
		internal void bind1(com.db4o.Transaction ta, object obj, long id)
		{
			ta = checkTransaction(ta);
			int intID = (int)id;
			if (obj != null)
			{
				object oldObject = getByID(id);
				if (oldObject != null)
				{
					com.db4o.YapObject yo = getYapObject(intID);
					if (yo != null)
					{
						if (ta.reflector().forObject(obj) == yo.getYapClass().classReflector())
						{
							bind2(yo, obj);
						}
						else
						{
							throw new j4o.lang.RuntimeException(com.db4o.Messages.get(57));
						}
					}
				}
			}
		}

		internal void bind2(com.db4o.YapObject a_yapObject, object obj)
		{
			int id = a_yapObject.getID();
			yapObjectGCd(a_yapObject);
			a_yapObject = new com.db4o.YapObject(getYapClass(reflector().forObject(obj), false
				), id);
			a_yapObject.setObjectWeak(this, obj);
			a_yapObject.setStateDirty();
			idTreeAdd(a_yapObject);
			hcTreeAdd(a_yapObject);
		}

		public abstract com.db4o.PBootRecord bootRecord();

		private bool breakDeleteForEnum(com.db4o.YapObject reference, bool userCall)
		{
			return false;
			if (userCall)
			{
				return false;
			}
			if (reference == null)
			{
				return false;
			}
			return com.db4o.Platform4.jdk().isEnum(reflector(), reference.getYapClass().classReflector
				());
		}

		internal virtual bool canUpdate()
		{
			return true;
		}

		internal void checkClosed()
		{
			if (i_classCollection == null)
			{
				com.db4o.inside.Exceptions4.throwRuntimeException(20, ToString());
			}
		}

		internal void checkNeededUpdates()
		{
			if (i_needsUpdate != null)
			{
				com.db4o.foundation.Iterator4 i = new com.db4o.foundation.Iterator4Impl(i_needsUpdate
					);
				while (i.hasNext())
				{
					com.db4o.YapClass yapClass = (com.db4o.YapClass)i.next();
					yapClass.setStateDirty();
					yapClass.write(this, i_systemTrans);
				}
				i_needsUpdate = null;
			}
		}

		internal com.db4o.Transaction checkTransaction(com.db4o.Transaction ta)
		{
			checkClosed();
			if (ta != null)
			{
				return ta;
			}
			return getTransaction();
		}

		public virtual bool close()
		{
			lock (com.db4o.Db4o.Lock)
			{
				lock (i_lock)
				{
					bool ret = close1();
					return ret;
				}
			}
		}

		internal bool close1()
		{
			if (i_classCollection == null)
			{
				return true;
			}
			com.db4o.Platform4.preClose(this);
			checkNeededUpdates();
			if (stateMessages())
			{
				logMsg(2, ToString());
			}
			bool closeResult = close2();
			return closeResult;
		}

		internal virtual bool close2()
		{
			if (hasShutDownHook())
			{
				com.db4o.Platform4.removeShutDownHook(this, i_lock);
			}
			i_classCollection = null;
			i_references.stopTimer();
			i_hcTree = null;
			i_idTree = null;
			i_systemTrans = null;
			i_trans = null;
			if (stateMessages())
			{
				logMsg(3, ToString());
			}
			return true;
		}

		public virtual com.db4o.types.Db4oCollections collections()
		{
			lock (i_lock)
			{
				if (i_handlers.i_collections == null)
				{
					i_handlers.i_collections = com.db4o.Platform4.collections(this);
				}
				return i_handlers.i_collections;
			}
		}

		public virtual void commit()
		{
			lock (i_lock)
			{
				commit1();
			}
		}

		internal abstract void commit1();

		public virtual com.db4o.config.Configuration configure()
		{
			return i_config;
		}

		internal abstract com.db4o.ClassIndex createClassIndex(com.db4o.YapClass a_yapClass
			);

		internal abstract com.db4o.QueryResultImpl createQResult(com.db4o.Transaction a_ta
			);

		internal virtual void createStringIO(byte encoding)
		{
			setStringIo(com.db4o.YapStringIO.forEncoding(encoding));
		}

		internal virtual void createTransaction()
		{
			i_systemTrans = new com.db4o.Transaction(this, null);
			i_trans = new com.db4o.Transaction(this, i_systemTrans);
		}

		internal abstract long currentVersion();

		internal virtual bool createYapClass(com.db4o.YapClass a_yapClass, com.db4o.reflect.ReflectClass
			 a_class, com.db4o.YapClass a_superYapClass)
		{
			return a_yapClass.init(this, a_superYapClass, a_class, false);
		}

		/// <summary>allows special handling for all Db4oType objects.</summary>
		/// <remarks>
		/// allows special handling for all Db4oType objects.
		/// Redirected here from #set() so only instanceof check is necessary
		/// in the #set() method.
		/// </remarks>
		/// <returns>object if handled here and #set() should not continue processing</returns>
		internal virtual com.db4o.types.Db4oType db4oTypeStored(com.db4o.Transaction a_trans
			, object a_object)
		{
			if (a_object is com.db4o.ext.Db4oDatabase)
			{
				com.db4o.ext.Db4oDatabase database = (com.db4o.ext.Db4oDatabase)a_object;
				if (getYapObject(a_object) != null)
				{
					return database;
				}
				showInternalClasses(true);
				com.db4o.query.Query q = querySharpenBug();
				q.constrain(j4o.lang.Class.getClassForObject(database));
				q.descend("i_uuid").constrain(database.i_uuid);
				com.db4o.ObjectSet objectSet = q.execute();
				while (objectSet.hasNext())
				{
					com.db4o.ext.Db4oDatabase storedDatabase = (com.db4o.ext.Db4oDatabase)objectSet.next
						();
					activate1(null, storedDatabase, 4);
					if (storedDatabase.Equals(a_object))
					{
						showInternalClasses(false);
						return storedDatabase;
					}
				}
				showInternalClasses(false);
			}
			return null;
		}

		public virtual void deactivate(object a_deactivate, int a_depth)
		{
			lock (i_lock)
			{
				deactivate1(a_deactivate, a_depth);
			}
		}

		internal void deactivate1(object a_deactivate, int a_depth)
		{
			checkClosed();
			i_entryCounter++;
			try
			{
				i_justDeactivated[0] = null;
				deactivate2(a_deactivate, a_depth);
				i_justDeactivated[0] = null;
			}
			catch (System.Exception t)
			{
				fatalException(t);
			}
			i_entryCounter--;
		}

		private void deactivate2(object a_activate, int a_depth)
		{
			stillToDeactivate(a_activate, a_depth, true);
			while (i_stillToDeactivate != null)
			{
				com.db4o.foundation.Iterator4 i = new com.db4o.foundation.Iterator4Impl(i_stillToDeactivate
					);
				i_stillToDeactivate = null;
				while (i.hasNext())
				{
					((com.db4o.YapObject)i.next()).deactivate(i_trans, ((int)i.next()));
				}
			}
		}

		public virtual void delete(object a_object)
		{
			lock (i_lock)
			{
				com.db4o.Transaction ta = delete1(null, a_object, true);
				ta.beginEndSet();
			}
		}

		internal com.db4o.Transaction delete1(com.db4o.Transaction ta, object a_object, bool
			 userCall)
		{
			ta = checkTransaction(ta);
			if (a_object != null)
			{
				i_entryCounter++;
				try
				{
					delete2(ta, a_object, userCall);
				}
				catch (System.Exception t)
				{
					fatalException(t);
				}
				i_entryCounter--;
			}
			return ta;
		}

		private void delete2(com.db4o.Transaction ta, object a_object, bool userCall)
		{
			com.db4o.YapObject yo = getYapObject(a_object);
			if (yo != null)
			{
				delete3(ta, yo, a_object, 0, userCall);
			}
		}

		internal void delete3(com.db4o.Transaction ta, com.db4o.YapObject yo, object a_object
			, int a_cascade, bool userCall)
		{
			if (breakDeleteForEnum(yo, userCall))
			{
				return;
			}
			if (a_object is com.db4o.types.SecondClass)
			{
				delete4(ta, yo, a_object, a_cascade, userCall);
			}
			else
			{
				ta.delete(yo, a_cascade);
			}
		}

		internal void delete4(com.db4o.Transaction ta, com.db4o.YapObject yo, object a_object
			, int a_cascade, bool userCall)
		{
			if (yo != null)
			{
				if (yo.beginProcessing())
				{
					if (breakDeleteForEnum(yo, userCall))
					{
						return;
					}
					com.db4o.YapClass yc = yo.getYapClass();
					object obj = yo.getObject();
					if (yc.dispatchEvent(this, obj, com.db4o.EventDispatcher.CAN_DELETE))
					{
						if (delete5(ta, yo, a_cascade, userCall))
						{
							yc.dispatchEvent(this, obj, com.db4o.EventDispatcher.DELETE);
							if (i_config.i_messageLevel > com.db4o.YapConst.STATE)
							{
								message("" + yo.getID() + " delete " + yo.getYapClass().getName());
							}
						}
					}
					yo.endProcessing();
				}
			}
		}

		internal abstract bool delete5(com.db4o.Transaction ta, com.db4o.YapObject yapObject
			, int a_cascade, bool userCall);

		internal virtual bool detectSchemaChanges()
		{
			return i_config.i_detectSchemaChanges;
		}

		public virtual bool dispatchsEvents()
		{
			return true;
		}

		internal virtual void emergencyClose()
		{
			i_classCollection = null;
			i_references.stopTimer();
		}

		public virtual com.db4o.ext.ExtObjectContainer ext()
		{
			return this;
		}

		internal virtual void failedToShutDown()
		{
			lock (com.db4o.Db4o.Lock)
			{
				if (i_classCollection != null)
				{
					if (i_entryCounter == 0)
					{
						com.db4o.Messages.logErr(i_config, 50, ToString(), null);
						while (!close())
						{
						}
					}
					else
					{
						emergencyClose();
						if (i_entryCounter > 0)
						{
							com.db4o.Messages.logErr(i_config, 24, null, null);
						}
					}
				}
			}
		}

		internal virtual void fatalException(int msgID)
		{
			fatalException(null, msgID);
		}

		internal virtual void fatalException(System.Exception t)
		{
			fatalException(t, com.db4o.Messages.FATAL_MSG_ID);
		}

		internal virtual void fatalException(System.Exception t, int msgID)
		{
			if (!i_amDuringFatalExit)
			{
				i_amDuringFatalExit = true;
				i_classCollection = null;
				emergencyClose();
				com.db4o.Messages.logErr(i_config, (msgID == com.db4o.Messages.FATAL_MSG_ID ? 18 : 
					msgID), null, t);
			}
			throw new j4o.lang.RuntimeException(com.db4o.Messages.get(msgID));
		}

		~YapStream()
		{
			if (i_config == null || i_config.i_automaticShutDown)
			{
				failedToShutDown();
			}
		}

		internal virtual void gc()
		{
			i_references.pollReferenceQueue();
		}

		public virtual com.db4o.ObjectSet get(object template)
		{
			lock (i_lock)
			{
				return get1(null, template);
			}
		}

		internal virtual com.db4o.inside.query.ObjectSetFacade get1(com.db4o.Transaction 
			ta, object template)
		{
			ta = checkTransaction(ta);
			com.db4o.QueryResultImpl res = createQResult(ta);
			i_entryCounter++;
			try
			{
				get2(ta, template, res);
			}
			catch (System.Exception t)
			{
				fatalException(t);
			}
			i_entryCounter--;
			res.reset();
			return new com.db4o.inside.query.ObjectSetFacade(res);
		}

		private void get2(com.db4o.Transaction ta, object template, com.db4o.QueryResultImpl
			 res)
		{
			if (template == null || j4o.lang.Class.getClassForObject(template) == com.db4o.YapConst
				.CLASS_OBJECT)
			{
				getAll(ta, res);
			}
			else
			{
				com.db4o.query.Query q = querySharpenBug(ta);
				q.constrain(template);
				((com.db4o.QQuery)q).execute1(res);
			}
		}

		internal abstract void getAll(com.db4o.Transaction ta, com.db4o.QueryResultImpl a_res
			);

		public virtual object getByID(long id)
		{
			lock (i_lock)
			{
				return getByID1(null, id);
			}
		}

		internal object getByID1(com.db4o.Transaction ta, long id)
		{
			ta = checkTransaction(ta);
			try
			{
				return getByID2(ta, (int)id);
			}
			catch (System.Exception e)
			{
				return null;
			}
		}

		internal object getByID2(com.db4o.Transaction ta, int a_id)
		{
			if (a_id > 0)
			{
				com.db4o.YapObject yo = getYapObject(a_id);
				if (yo != null)
				{
					object candidate = yo.getObject();
					if (candidate != null)
					{
						return candidate;
					}
					yapObjectGCd(yo);
				}
				try
				{
					return new com.db4o.YapObject(a_id).read(ta, null, null, 0, com.db4o.YapConst.ADD_TO_ID_TREE
						, true);
				}
				catch (System.Exception t)
				{
				}
			}
			return null;
		}

		public object getByUUID(com.db4o.ext.Db4oUUID uuid)
		{
			lock (i_lock)
			{
				com.db4o.Transaction ta = checkTransaction(null);
				object[] arr = ta.objectAndYapObjectBySignature(uuid.getLongPart(), uuid.getSignaturePart
					());
				return arr[0];
			}
		}

		public virtual long getID(object a_object)
		{
			lock (i_lock)
			{
				return getID1(null, a_object);
			}
		}

		internal int getID1(com.db4o.Transaction ta, object a_object)
		{
			checkClosed();
			com.db4o.YapObject yo = i_hcTree.hc_find(a_object);
			if (yo != null)
			{
				return yo.getID();
			}
			return 0;
		}

		public virtual com.db4o.ext.ObjectInfo getObjectInfo(object obj)
		{
			lock (i_lock)
			{
				return getYapObject(obj);
			}
		}

		internal object[] getObjectAndYapObjectByID(com.db4o.Transaction ta, int a_id)
		{
			object[] arr = new object[2];
			if (a_id > 0)
			{
				com.db4o.YapObject yo = getYapObject(a_id);
				if (yo != null)
				{
					object candidate = yo.getObject();
					if (candidate != null)
					{
						arr[0] = candidate;
						arr[1] = yo;
						return arr;
					}
					yapObjectGCd(yo);
				}
				try
				{
					yo = new com.db4o.YapObject(a_id);
					arr[0] = yo.read(ta, null, null, 0, com.db4o.YapConst.ADD_TO_ID_TREE, true);
					if (arr[0] != yo.getObject())
					{
						return getObjectAndYapObjectByID(ta, a_id);
					}
					arr[1] = yo;
				}
				catch (System.Exception t)
				{
				}
			}
			return arr;
		}

		internal com.db4o.YapWriter getWriter(com.db4o.Transaction a_trans, int a_length)
		{
			return new com.db4o.YapWriter(a_trans, a_length);
		}

		public com.db4o.YapWriter getWriter(com.db4o.Transaction a_trans, int a_address, 
			int a_length)
		{
			if (com.db4o.Debug.exceedsMaximumBlockSize(a_length))
			{
				return null;
			}
			return new com.db4o.YapWriter(a_trans, a_address, a_length);
		}

		public virtual com.db4o.Transaction getSystemTransaction()
		{
			return i_systemTrans;
		}

		internal virtual com.db4o.Transaction getTransaction()
		{
			return i_trans;
		}

		internal com.db4o.YapClass getYapClass(com.db4o.reflect.ReflectClass a_class, bool
			 a_create)
		{
			if (a_class == null)
			{
				return null;
			}
			if ((!showInternalClasses()) && i_handlers.ICLASS_INTERNAL.isAssignableFrom(a_class
				))
			{
				return null;
			}
			com.db4o.YapClass yc = i_handlers.getYapClassStatic(a_class);
			if (yc != null)
			{
				return yc;
			}
			return i_classCollection.getYapClass(a_class, a_create);
		}

		internal virtual com.db4o.YapClass getYapClass(int a_id)
		{
			if (a_id == 0)
			{
				return null;
			}
			com.db4o.YapClass yc = i_handlers.getYapClassStatic(a_id);
			if (yc != null)
			{
				return yc;
			}
			return i_classCollection.getYapClass(a_id);
		}

		internal com.db4o.YapObject getYapObject(int a_id)
		{
			return i_idTree.id_find(a_id);
		}

		public com.db4o.YapObject getYapObject(object a_object)
		{
			return i_hcTree.hc_find(a_object);
		}

		public virtual com.db4o.YapHandlers handlers()
		{
			return i_handlers;
		}

		internal virtual bool needsLockFileThread()
		{
			if (!com.db4o.Platform4.hasLockFileThread())
			{
				return false;
			}
			if (com.db4o.Platform4.hasNio())
			{
				return false;
			}
			if (i_config.i_readonly)
			{
				return false;
			}
			return i_config.i_lockFile;
		}

		internal virtual bool hasShutDownHook()
		{
			return i_config.i_automaticShutDown;
		}

		internal void hcTreeAdd(com.db4o.YapObject a_yo)
		{
			i_hcTree = i_hcTree.hc_add(a_yo);
		}

		internal void hcTreeRemove(com.db4o.YapObject a_yo)
		{
			i_hcTree = i_hcTree.hc_remove(a_yo);
		}

		internal void idTreeAdd(com.db4o.YapObject a_yo)
		{
			i_idTree = i_idTree.id_add(a_yo);
		}

		internal void idTreeRemove(int a_id)
		{
			i_idTree = i_idTree.id_remove(a_id);
		}

		internal virtual void initialize0()
		{
			initialize0b();
			i_stillToSet = null;
			i_justActivated = new com.db4o.Tree[1];
		}

		internal virtual void initialize0b()
		{
			i_justDeactivated = new com.db4o.Tree[1];
		}

		internal virtual void initialize1()
		{
			i_config = (com.db4o.Config4Impl)((com.db4o.foundation.DeepClone)com.db4o.Db4o.configure
				()).deepClone(this);
			i_handlers = new com.db4o.YapHandlers(this, i_config.i_encoding);
			if (i_references != null)
			{
				gc();
				i_references.stopTimer();
			}
			i_references = new com.db4o.YapReferences(this);
			if (hasShutDownHook())
			{
				com.db4o.Platform4.addShutDownHook(this, i_lock);
			}
			i_handlers.initEncryption(i_config);
			initialize2();
			i_stillToSet = null;
		}

		/// <summary>before file is open</summary>
		internal virtual void initialize2()
		{
			i_idTree = new com.db4o.YapObject(0);
			i_idTree.setObject(new object());
			i_hcTree = i_idTree;
			initialize2NObjectCarrier();
		}

		/// <summary>overridden in YapObjectCarrier</summary>
		internal virtual void initialize2NObjectCarrier()
		{
			i_classCollection = new com.db4o.YapClassCollection(i_systemTrans);
			i_references.startTimer();
		}

		internal virtual void initialize3()
		{
			i_showInternalClasses = 100000;
			initialize4NObjectCarrier();
			i_showInternalClasses = 0;
		}

		internal virtual void initialize4NObjectCarrier()
		{
			initializeEssentialClasses();
			rename(i_config);
			i_classCollection.initOnUp(i_systemTrans);
			if (i_config.i_detectSchemaChanges)
			{
				i_systemTrans.commit();
			}
		}

		internal virtual void initializeEssentialClasses()
		{
			for (int i = 0; i < com.db4o.YapConst.ESSENTIAL_CLASSES.Length; i++)
			{
				getYapClass(reflector().forClass(com.db4o.YapConst.ESSENTIAL_CLASSES[i]), true);
			}
		}

		internal void instantiating(bool flag)
		{
			i_instantiating = flag;
		}

		public virtual bool isActive(object obj)
		{
			lock (i_lock)
			{
				return isActive1(obj);
			}
		}

		internal bool isActive1(object obj)
		{
			checkClosed();
			if (obj != null)
			{
				com.db4o.YapObject yo = getYapObject(obj);
				if (yo != null)
				{
					return yo.isActive();
				}
			}
			return false;
		}

		public virtual bool isCached(long a_id)
		{
			lock (i_lock)
			{
				if (a_id > 0)
				{
					com.db4o.YapObject yo = getYapObject((int)a_id);
					if (yo != null)
					{
						object candidate = yo.getObject();
						if (candidate != null)
						{
							return true;
						}
					}
				}
				return false;
			}
		}

		/// <summary>
		/// overridden in YapClient
		/// This method will make it easier to refactor than
		/// an "instanceof YapClient" check.
		/// </summary>
		/// <remarks>
		/// overridden in YapClient
		/// This method will make it easier to refactor than
		/// an "instanceof YapClient" check.
		/// </remarks>
		internal virtual bool isClient()
		{
			return false;
		}

		public virtual bool isClosed()
		{
			lock (i_lock)
			{
				return i_classCollection == null;
			}
		}

		internal bool isInstantiating()
		{
			return i_instantiating;
		}

		internal virtual bool isServer()
		{
			return false;
		}

		public virtual bool isStored(object obj)
		{
			lock (i_lock)
			{
				return isStored1(obj);
			}
		}

		internal bool isStored1(object obj)
		{
			com.db4o.Transaction ta = checkTransaction(null);
			if (obj == null)
			{
				return false;
			}
			com.db4o.YapObject yo = getYapObject(obj);
			if (yo == null)
			{
				return false;
			}
			return !ta.isDeleted(yo.getID());
		}

		public virtual com.db4o.reflect.ReflectClass[] knownClasses()
		{
			lock (i_lock)
			{
				checkClosed();
				return reflector().knownClasses();
			}
		}

		public virtual object Lock()
		{
			return i_lock;
		}

		internal void logMsg(int code, string msg)
		{
			com.db4o.Messages.logMsg(i_config, code, msg);
		}

		internal virtual bool maintainsIndices()
		{
			return true;
		}

		internal virtual com.db4o.YapWriter marshall(com.db4o.Transaction ta, object obj)
		{
			int[] id = { 0 };
			byte[] bytes = marshall(obj, id);
			com.db4o.YapWriter yapBytes = new com.db4o.YapWriter(ta, bytes.Length);
			yapBytes.append(bytes);
			yapBytes.useSlot(id[0], 0, bytes.Length);
			return yapBytes;
		}

		internal virtual byte[] marshall(object obj, int[] id)
		{
			com.db4o.ext.MemoryFile memoryFile = new com.db4o.ext.MemoryFile();
			memoryFile.setInitialSize(223);
			memoryFile.setIncrementSizeBy(300);
			getYapClass(reflector().forObject(obj), true);
			com.db4o.YapObjectCarrier carrier = new com.db4o.YapObjectCarrier(this, memoryFile
				);
			carrier.i_showInternalClasses = i_showInternalClasses;
			carrier.set(obj);
			id[0] = (int)carrier.getID(obj);
			carrier.close();
			return memoryFile.getBytes();
		}

		internal virtual void message(string msg)
		{
			new com.db4o.Message(this, msg);
		}

		public virtual void migrateFrom(com.db4o.ObjectContainer objectContainer)
		{
			if (objectContainer == null)
			{
				com.db4o.YapStream migratedFrom = i_migrateFrom;
				i_migrateFrom = null;
				if (migratedFrom != null && migratedFrom.i_migrateFrom == this)
				{
					migratedFrom.migrateFrom(null);
				}
				i_handlers.i_migration = null;
			}
			else
			{
				i_migrateFrom = (com.db4o.YapStream)objectContainer;
				i_handlers.i_migration = new com.db4o.MigrationConnection();
				i_migrateFrom.i_handlers.i_migration = i_handlers.i_migration;
			}
		}

		internal void needsUpdate(com.db4o.YapClass a_yapClass)
		{
			i_needsUpdate = new com.db4o.foundation.List4(i_needsUpdate, a_yapClass);
		}

		internal abstract com.db4o.YapWriter newObject(com.db4o.Transaction a_trans, com.db4o.YapMeta
			 a_object);

		internal abstract int newUserObject();

		public virtual object peekPersisted(object a_object, int a_depth, bool a_committed
			)
		{
			lock (i_lock)
			{
				checkClosed();
				i_entryCounter++;
				i_justPeeked = null;
				com.db4o.Transaction ta = a_committed ? i_systemTrans : checkTransaction(null);
				object cloned = null;
				com.db4o.YapObject yo = getYapObject(a_object);
				if (yo != null)
				{
					cloned = peekPersisted1(ta, yo.getID(), a_depth);
				}
				i_justPeeked = null;
				i_entryCounter--;
				return cloned;
			}
		}

		internal virtual object peekPersisted1(com.db4o.Transaction a_ta, int a_id, int a_depth
			)
		{
			com.db4o.TreeInt ti = new com.db4o.TreeInt(a_id);
			com.db4o.TreeIntObject tio = (com.db4o.TreeIntObject)com.db4o.Tree.find(i_justPeeked
				, ti);
			if (tio == null)
			{
				return new com.db4o.YapObject(a_id).read(a_ta, null, null, a_depth, com.db4o.YapConst
					.TRANSIENT, false);
			}
			else
			{
				return tio.i_object;
			}
		}

		internal virtual void peeked(int a_id, object a_object)
		{
			i_justPeeked = com.db4o.Tree.add(i_justPeeked, new com.db4o.TreeIntObject(a_id, a_object
				));
		}

		public virtual void purge()
		{
			lock (i_lock)
			{
				purge1();
			}
		}

		public virtual void purge(object obj)
		{
			lock (i_lock)
			{
				purge1(obj);
			}
		}

		internal void purge1()
		{
			checkClosed();
			j4o.lang.JavaSystem.gc();
			j4o.lang.JavaSystem.runFinalization();
			j4o.lang.JavaSystem.gc();
			gc();
			i_classCollection.purge();
		}

		internal void purge1(object obj)
		{
			if (obj != null)
			{
				if (i_hcTree != null)
				{
					com.db4o.YapObject yo = null;
					if (obj is com.db4o.YapObject)
					{
						yo = (com.db4o.YapObject)obj;
					}
					else
					{
						yo = i_hcTree.hc_find(obj);
					}
					if (yo != null)
					{
						yapObjectGCd(yo);
					}
				}
			}
		}

		public com.db4o.inside.query.NativeQueryHandler getNativeQueryHandler()
		{
			if (null == _nativeQueryHandler)
			{
				_nativeQueryHandler = new com.db4o.inside.query.NativeQueryHandler(this);
			}
			return _nativeQueryHandler;
		}

		public com.db4o.ObjectSet query(com.db4o.query.Predicate predicate)
		{
			lock (i_lock)
			{
				return getNativeQueryHandler().execute(predicate);
			}
		}

		public virtual com.db4o.query.Query query()
		{
			lock (i_lock)
			{
				return query((com.db4o.Transaction)null);
			}
		}

		internal com.db4o.query.Query query(com.db4o.Transaction ta)
		{
			i_entryCounter++;
			com.db4o.query.Query q = new com.db4o.QQuery(checkTransaction(ta), null, null);
			i_entryCounter--;
			return q;
		}

		internal virtual com.db4o.query.Query querySharpenBug()
		{
			return query();
		}

		internal virtual com.db4o.query.Query querySharpenBug(com.db4o.Transaction ta)
		{
			return query(ta);
		}

		internal abstract void raiseVersion(long a_minimumVersion);

		internal abstract void readBytes(byte[] a_bytes, int a_address, int a_length);

		internal abstract void readBytes(byte[] bytes, int address, int addressOffset, int
			 length);

		internal com.db4o.YapReader readObjectReaderByAddress(int a_address, int a_length
			)
		{
			if (a_address > 0)
			{
				com.db4o.YapReader reader = new com.db4o.YapReader(a_length);
				readBytes(reader._buffer, a_address, a_length);
				i_handlers.decrypt(reader);
				return reader;
			}
			return null;
		}

		internal com.db4o.YapWriter readObjectWriterByAddress(com.db4o.Transaction a_trans
			, int a_address, int a_length)
		{
			if (a_address > 0)
			{
				com.db4o.YapWriter reader = getWriter(a_trans, a_address, a_length);
				reader.readEncrypt(this, a_address);
				return reader;
			}
			return null;
		}

		public abstract com.db4o.YapWriter readWriterByID(com.db4o.Transaction a_ta, int 
			a_id);

		internal abstract com.db4o.YapReader readReaderByID(com.db4o.Transaction a_ta, int
			 a_id);

		private void reboot()
		{
			commit();
			int ccID = i_classCollection.getID();
			i_references.stopTimer();
			initialize2();
			i_classCollection.setID(this, ccID);
			i_classCollection.read(i_systemTrans);
		}

		public virtual com.db4o.reflect.generic.GenericReflector reflector()
		{
			return i_config.reflector();
		}

		public virtual void refresh(object a_refresh, int a_depth)
		{
			lock (i_lock)
			{
				i_refreshInsteadOfActivate = true;
				try
				{
					activate1(null, a_refresh, a_depth);
				}
				finally
				{
					i_refreshInsteadOfActivate = false;
				}
			}
		}

		internal void refreshClasses()
		{
			lock (i_lock)
			{
				i_classCollection.refreshClasses();
			}
		}

		public abstract void releaseSemaphore(string name);

		internal virtual void rememberJustSet(int id)
		{
			if (i_justSet == null)
			{
				i_justSet = new com.db4o.TreeInt(id);
			}
			else
			{
				i_justSet = i_justSet.add(new com.db4o.TreeInt(id));
			}
		}

		internal abstract void releaseSemaphores(com.db4o.Transaction ta);

		internal virtual void rename(com.db4o.Config4Impl config)
		{
			bool renamedOne = false;
			if (config.i_rename != null)
			{
				renamedOne = rename1(config);
			}
			i_classCollection.checkChanges();
			if (renamedOne)
			{
				reboot();
			}
		}

		protected virtual bool rename1(com.db4o.Config4Impl config)
		{
			bool renamedOne = false;
			try
			{
				com.db4o.foundation.Iterator4 i = config.i_rename.iterator();
				while (i.hasNext())
				{
					com.db4o.Rename ren = (com.db4o.Rename)i.next();
					if (get(ren).size() == 0)
					{
						bool renamed = false;
						bool isField = j4o.lang.JavaSystem.getLengthOf(ren.rClass) > 0;
						com.db4o.YapClass yapClass = i_classCollection.getYapClass(isField ? ren.rClass : 
							ren.rFrom);
						if (yapClass != null)
						{
							if (isField)
							{
								renamed = yapClass.renameField(ren.rFrom, ren.rTo);
							}
							else
							{
								com.db4o.YapClass existing = i_classCollection.getYapClass(ren.rTo);
								if (existing == null)
								{
									yapClass.setName(ren.rTo);
									renamed = true;
								}
								else
								{
									logMsg(9, "class " + ren.rTo);
								}
							}
						}
						if (renamed)
						{
							renamedOne = true;
							setDirty(yapClass);
							logMsg(8, ren.rFrom + " to " + ren.rTo);
							com.db4o.ObjectSet backren = get(new com.db4o.Rename(ren.rClass, null, ren.rFrom)
								);
							while (backren.hasNext())
							{
								delete(backren.next());
							}
							set(ren);
						}
					}
				}
			}
			catch (System.Exception t)
			{
				com.db4o.Messages.logErr(i_config, 10, null, t);
			}
			return renamedOne;
		}

		public virtual com.db4o.replication.ReplicationProcess replicationBegin(com.db4o.ObjectContainer
			 peerB, com.db4o.replication.ReplicationConflictHandler conflictHandler)
		{
			return new com.db4o.ReplicationImpl(this, peerB, conflictHandler);
		}

		internal int replicationHandles(object obj)
		{
			if (i_migrateFrom == null || i_handlers.i_replication == null)
			{
				return 0;
			}
			if (obj is com.db4o.Internal4)
			{
				return 0;
			}
			com.db4o.YapObject reference = getYapObject(obj);
			if (reference != null)
			{
				int id = reference.getID();
				if (id > 0 && (com.db4o.TreeInt.find(i_justSet, id) != null))
				{
					return id;
				}
			}
			return i_handlers.i_replication.tryToHandle(this, obj);
		}

		internal virtual void reserve(int byteCount)
		{
		}

		public virtual void rollback()
		{
			lock (i_lock)
			{
				rollback1();
			}
		}

		internal abstract void rollback1();

		public virtual void send(object obj)
		{
		}

		public virtual void set(object a_object)
		{
			lock (i_lock)
			{
				setExternal(null, a_object, com.db4o.YapConst.UNSPECIFIED);
			}
		}

		public virtual void set(object a_object, int a_depth)
		{
			lock (i_lock)
			{
				setExternal(null, a_object, a_depth);
			}
		}

		internal void setExternal(com.db4o.Transaction ta, object a_object, int a_depth)
		{
			ta = checkTransaction(ta);
			beginEndSet(ta);
			setInternal(ta, a_object, a_depth, true);
			beginEndSet(ta);
		}

		internal int setInternal(com.db4o.Transaction ta, object a_object, bool a_checkJustSet
			)
		{
			return setInternal(ta, a_object, com.db4o.YapConst.UNSPECIFIED, a_checkJustSet);
		}

		public int setInternal(com.db4o.Transaction ta, object a_object, int a_depth, bool
			 a_checkJustSet)
		{
			ta = checkTransaction(ta);
			int id = replicationHandles(a_object);
			if (id != 0)
			{
				if (id < 0)
				{
					return 0;
				}
				return id;
			}
			return setAfterReplication(ta, a_object, a_depth, a_checkJustSet);
		}

		internal int setAfterReplication(com.db4o.Transaction ta, object obj, int depth, 
			bool checkJust)
		{
			if (obj is com.db4o.types.Db4oType)
			{
				com.db4o.types.Db4oType db4oType = db4oTypeStored(ta, obj);
				if (db4oType != null)
				{
					return (int)getID1(ta, db4oType);
				}
			}
			int id;
			i_entryCounter++;
			try
			{
				id = set2(ta, obj, depth, checkJust);
			}
			catch (com.db4o.ext.ObjectNotStorableException e)
			{
				i_entryCounter--;
				throw e;
			}
			catch (System.Exception t)
			{
				id = 0;
				fatalException(t);
			}
			i_entryCounter--;
			return id;
		}

		private int set2(com.db4o.Transaction ta, object obj, int depth, bool checkJust)
		{
			int id = set3(ta, obj, depth, checkJust);
			if (i_entryCounter < com.db4o.YapConst.MAX_STACK_DEPTH)
			{
				checkStillToSet();
			}
			return id;
		}

		internal virtual void checkStillToSet()
		{
			com.db4o.foundation.List4 postponedStillToSet = null;
			while (i_stillToSet != null)
			{
				com.db4o.foundation.Iterator4 i = new com.db4o.foundation.Iterator4Impl(i_stillToSet
					);
				i_stillToSet = null;
				while (i.hasNext())
				{
					int updateDepth = (int)i.next();
					com.db4o.YapObject yo = (com.db4o.YapObject)i.next();
					com.db4o.Transaction trans = (com.db4o.Transaction)i.next();
					if (!yo.continueSet(trans, updateDepth))
					{
						postponedStillToSet = new com.db4o.foundation.List4(postponedStillToSet, trans);
						postponedStillToSet = new com.db4o.foundation.List4(postponedStillToSet, yo);
						postponedStillToSet = new com.db4o.foundation.List4(postponedStillToSet, updateDepth
							);
					}
				}
			}
			i_stillToSet = postponedStillToSet;
		}

		internal int set3(com.db4o.Transaction a_trans, object a_object, int a_updateDepth
			, bool a_checkJustSet)
		{
			if (a_object != null & !(a_object is com.db4o.types.TransientClass))
			{
				if (a_object is com.db4o.Db4oTypeImpl)
				{
					((com.db4o.Db4oTypeImpl)a_object).storedTo(a_trans);
				}
				com.db4o.YapClass yc = null;
				com.db4o.YapObject yapObject = i_hcTree.hc_find(a_object);
				if (yapObject == null)
				{
					com.db4o.reflect.ReflectClass claxx = reflector().forObject(a_object);
					yc = getYapClass(claxx, false);
					if (yc == null)
					{
						yc = getYapClass(claxx, true);
						if (yc == null)
						{
							return 0;
						}
					}
					yapObject = i_hcTree.hc_find(a_object);
				}
				else
				{
					yc = yapObject.getYapClass();
				}
				bool dontDelete = true;
				if (yapObject == null)
				{
					if (!yc.dispatchEvent(this, a_object, com.db4o.EventDispatcher.CAN_NEW))
					{
						return 0;
					}
					yapObject = new com.db4o.YapObject(0);
					if (yapObject.store(a_trans, yc, a_object, a_updateDepth))
					{
						idTreeAdd(yapObject);
						hcTreeAdd(yapObject);
						if (a_object is com.db4o.Db4oTypeImpl)
						{
							((com.db4o.Db4oTypeImpl)a_object).setTrans(a_trans);
						}
						if (i_config.i_messageLevel > com.db4o.YapConst.STATE)
						{
							message("" + yapObject.getID() + " new " + yapObject.getYapClass().getName());
						}
						stillToSet(a_trans, yapObject, a_updateDepth);
					}
				}
				else
				{
					if (canUpdate())
					{
						int oid = yapObject.getID();
						if (a_checkJustSet)
						{
							if (oid > 0 && (com.db4o.TreeInt.find(i_justSet, oid) != null))
							{
								return oid;
							}
						}
						bool doUpdate = (a_updateDepth == com.db4o.YapConst.UNSPECIFIED) || (a_updateDepth
							 > 0);
						if (doUpdate)
						{
							dontDelete = false;
							a_trans.dontDelete(yapObject.getYapClass().getID(), oid);
							if (a_checkJustSet)
							{
								a_checkJustSet = false;
								rememberJustSet(oid);
							}
							yapObject.writeUpdate(a_trans, a_updateDepth);
						}
					}
				}
				checkNeededUpdates();
				int id = yapObject.getID();
				if (a_checkJustSet && canUpdate())
				{
					if (!yapObject.getYapClass().isPrimitive())
					{
						rememberJustSet(id);
					}
				}
				if (dontDelete)
				{
					a_trans.dontDelete(yapObject.getYapClass().getID(), id);
				}
				return id;
			}
			return 0;
		}

		internal abstract void setDirty(com.db4o.UseSystemTransaction a_object);

		public abstract bool setSemaphore(string name, int timeout);

		internal virtual void setStringIo(com.db4o.YapStringIO a_io)
		{
			i_handlers.i_stringHandler.setStringIo(a_io);
		}

		internal bool showInternalClasses()
		{
			return isServer() || i_showInternalClasses > 0;
		}

		/// <summary>
		/// Objects implementing the "Internal" marker interface are
		/// not visible to queries, unless this flag is set to true.
		/// </summary>
		/// <remarks>
		/// Objects implementing the "Internal" marker interface are
		/// not visible to queries, unless this flag is set to true.
		/// The caller should reset the flag after the call.
		/// </remarks>
		public virtual void showInternalClasses(bool show)
		{
			lock (this)
			{
				if (show)
				{
					i_showInternalClasses++;
				}
				else
				{
					i_showInternalClasses--;
				}
				if (i_showInternalClasses < 0)
				{
					i_showInternalClasses = 0;
				}
			}
		}

		internal virtual bool stateMessages()
		{
			return true;
		}

		/// <summary>
		/// returns true in case an unknown single object is passed
		/// This allows deactivating objects before queries are called.
		/// </summary>
		/// <remarks>
		/// returns true in case an unknown single object is passed
		/// This allows deactivating objects before queries are called.
		/// </remarks>
		internal virtual com.db4o.foundation.List4 stillTo1(com.db4o.foundation.List4 a_still
			, com.db4o.Tree[] a_just, object a_object, int a_depth, bool a_forceUnknownDeactivate
			)
		{
			if (a_object != null)
			{
				if (a_depth > 0)
				{
					com.db4o.YapObject yapObject = i_hcTree.hc_find(a_object);
					if (yapObject != null)
					{
						int id = yapObject.getID();
						if (a_just[0] != null)
						{
							if (((com.db4o.TreeInt)a_just[0]).find(id) != null)
							{
								return a_still;
							}
							a_just[0] = a_just[0].add(new com.db4o.TreeInt(id));
						}
						else
						{
							a_just[0] = new com.db4o.TreeInt(id);
						}
						return new com.db4o.foundation.List4(new com.db4o.foundation.List4(a_still, a_depth
							), yapObject);
					}
					else
					{
						com.db4o.reflect.ReflectClass clazz = reflector().forObject(a_object);
						if (clazz.isArray())
						{
							if (!clazz.getComponentType().isPrimitive())
							{
								object[] arr = com.db4o.YapArray.toArray(this, a_object);
								for (int i = 0; i < arr.Length; i++)
								{
									a_still = stillTo1(a_still, a_just, arr[i], a_depth, a_forceUnknownDeactivate);
								}
							}
						}
						else
						{
							if (a_object is com.db4o.config.Entry)
							{
								a_still = stillTo1(a_still, a_just, ((com.db4o.config.Entry)a_object).key, a_depth
									, false);
								a_still = stillTo1(a_still, a_just, ((com.db4o.config.Entry)a_object).value, a_depth
									, false);
							}
							else
							{
								if (a_forceUnknownDeactivate)
								{
									com.db4o.YapClass yc = getYapClass(reflector().forObject(a_object), false);
									if (yc != null)
									{
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

		internal virtual void stillToActivate(object a_object, int a_depth)
		{
			i_stillToActivate = stillTo1(i_stillToActivate, i_justActivated, a_object, a_depth
				, false);
		}

		internal virtual void stillToDeactivate(object a_object, int a_depth, bool a_forceUnknownDeactivate
			)
		{
			i_stillToDeactivate = stillTo1(i_stillToDeactivate, i_justDeactivated, a_object, 
				a_depth, a_forceUnknownDeactivate);
		}

		internal virtual void stillToSet(com.db4o.Transaction a_trans, com.db4o.YapObject
			 a_yapObject, int a_updateDepth)
		{
			i_stillToSet = new com.db4o.foundation.List4(i_stillToSet, a_trans);
			i_stillToSet = new com.db4o.foundation.List4(i_stillToSet, a_yapObject);
			i_stillToSet = new com.db4o.foundation.List4(i_stillToSet, a_updateDepth);
		}

		internal virtual void stopSession()
		{
			i_classCollection = null;
		}

		public virtual com.db4o.ext.StoredClass storedClass(object clazz)
		{
			lock (i_lock)
			{
				checkClosed();
				return storedClass1(clazz);
			}
		}

		internal virtual com.db4o.YapClass storedClass1(object clazz)
		{
			try
			{
				com.db4o.reflect.ReflectClass claxx = i_config.reflectorFor(clazz);
				if (claxx != null)
				{
					return getYapClass(claxx, false);
				}
			}
			catch (System.Exception e)
			{
			}
			return null;
		}

		public virtual com.db4o.ext.StoredClass[] storedClasses()
		{
			lock (i_lock)
			{
				checkClosed();
				return i_classCollection.storedClasses();
			}
		}

		public virtual com.db4o.YapStringIO stringIO()
		{
			return i_handlers.i_stringHandler.i_stringIo;
		}

		internal virtual object unmarshall(com.db4o.YapWriter yapBytes)
		{
			return unmarshall(yapBytes._buffer, yapBytes.getID());
		}

		internal virtual object unmarshall(byte[] bytes, int id)
		{
			com.db4o.ext.MemoryFile memoryFile = new com.db4o.ext.MemoryFile(bytes);
			com.db4o.YapObjectCarrier carrier = new com.db4o.YapObjectCarrier(this, memoryFile
				);
			object obj = carrier.getByID(id);
			carrier.activate(obj, int.MaxValue);
			carrier.close();
			return obj;
		}

		internal abstract com.db4o.YapWriter updateObject(com.db4o.Transaction a_trans, com.db4o.YapMeta
			 a_object);

		public virtual long version()
		{
			lock (i_lock)
			{
				return currentVersion();
			}
		}

		internal abstract void write(bool shuttingDown);

		internal abstract void writeDirty();

		internal abstract void writeEmbedded(com.db4o.YapWriter a_parent, com.db4o.YapWriter
			 a_child);

		internal abstract void writeNew(com.db4o.YapClass a_yapClass, com.db4o.YapWriter 
			aWriter);

		internal abstract void writeTransactionPointer(int a_address);

		internal abstract void writeUpdate(com.db4o.YapClass a_yapClass, com.db4o.YapWriter
			 a_bytes);

		internal void yapObjectGCd(com.db4o.YapObject yo)
		{
			hcTreeRemove(yo);
			idTreeRemove(yo.getID());
			yo.setID(this, -1);
			com.db4o.Platform4.killYapRef(yo.i_object);
		}

		public abstract void backup(string arg1);

		public abstract com.db4o.ext.Db4oDatabase identity();
	}
}
