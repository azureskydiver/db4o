namespace com.db4o
{
	/// <summary>
	/// NOTE: This is just a 'partial' base class to allow for variant implementations
	/// in db4oj and db4ojdk1.2.
	/// </summary>
	/// <remarks>
	/// NOTE: This is just a 'partial' base class to allow for variant implementations
	/// in db4oj and db4ojdk1.2. It assumes that itself is an instance of YapStream
	/// and should never be used explicitly.
	/// </remarks>
	/// <exclude></exclude>
	#if NET_2_0
	public abstract partial class YapStreamBase : com.db4o.types.TransientClass, com.db4o.Internal4
		, com.db4o.YapStreamSpec
	#else
	public abstract class YapStreamBase : com.db4o.types.TransientClass, com.db4o.Internal4
		, com.db4o.YapStreamSpec
	#endif
	{
		public const int HEADER_LENGTH = 2 + (com.db4o.YapConst.YAPINT_LENGTH * 4);

		private bool i_amDuringFatalExit = false;

		public com.db4o.YapClassCollection i_classCollection;

		public com.db4o.Config4Impl i_config;

		protected int i_entryCounter;

		private com.db4o.YapObject i_hcTree;

		private com.db4o.YapObject i_idTree;

		private com.db4o.Tree[] i_justActivated;

		private com.db4o.Tree[] i_justDeactivated;

		private com.db4o.Tree i_justPeeked;

		private com.db4o.Tree i_justSet;

		internal readonly object i_lock;

		private com.db4o.foundation.List4 i_needsUpdate;

		internal readonly com.db4o.YapStream i_parent;

		internal bool i_refreshInsteadOfActivate;

		internal int i_showInternalClasses = 0;

		private com.db4o.foundation.List4 i_stillToActivate;

		private com.db4o.foundation.List4 i_stillToDeactivate;

		private com.db4o.foundation.List4 i_stillToSet;

		internal com.db4o.Transaction i_systemTrans;

		internal com.db4o.Transaction i_trans;

		private bool i_instantiating;

		public com.db4o.YapHandlers i_handlers;

		internal int _replicationCallState;

		internal com.db4o.YapReferences i_references;

		private com.db4o.inside.query.NativeQueryHandler _nativeQueryHandler;

		private readonly com.db4o.YapStream _this;

		protected YapStreamBase(com.db4o.YapStream a_parent)
		{
			_this = Cast(this);
			i_parent = a_parent == null ? _this : a_parent;
			i_lock = a_parent == null ? new object() : a_parent.i_lock;
			Initialize0();
			CreateTransaction();
			Initialize1();
		}

		public virtual void Activate(object a_activate, int a_depth)
		{
			lock (i_lock)
			{
				Activate1(null, a_activate, a_depth);
			}
		}

		internal void Activate1(com.db4o.Transaction ta, object a_activate)
		{
			Activate1(ta, a_activate, i_config.ActivationDepth());
		}

		public void Activate1(com.db4o.Transaction ta, object a_activate, int a_depth)
		{
			ta = CheckTransaction(ta);
			BeginEndActivation();
			Activate2(ta, a_activate, a_depth);
			BeginEndActivation();
		}

		internal void BeginEndActivation()
		{
			i_justActivated[0] = null;
		}

		internal void BeginEndSet(com.db4o.Transaction ta)
		{
			i_justSet = null;
			if (ta != null)
			{
				ta.BeginEndSet();
			}
		}

		/// <summary>internal call interface, does not reset i_justActivated</summary>
		internal void Activate2(com.db4o.Transaction ta, object a_activate, int a_depth)
		{
			i_entryCounter++;
			try
			{
				StillToActivate(a_activate, a_depth);
				Activate3CheckStill(ta);
			}
			catch (System.Exception t)
			{
				FatalException(t);
			}
			i_entryCounter--;
		}

		internal void Activate3CheckStill(com.db4o.Transaction ta)
		{
			while (i_stillToActivate != null)
			{
				com.db4o.foundation.Iterator4 i = new com.db4o.foundation.Iterator4Impl(i_stillToActivate
					);
				i_stillToActivate = null;
				while (i.HasNext())
				{
					com.db4o.YapObject yo = (com.db4o.YapObject)i.Next();
					int depth = ((int)i.Next());
					object obj = yo.GetObject();
					if (obj == null)
					{
						YapObjectGCd(yo);
					}
					else
					{
						yo.Activate1(ta, obj, depth, i_refreshInsteadOfActivate);
					}
				}
			}
		}

		public virtual int AlignToBlockSize(int length)
		{
			return BlocksFor(length) * BlockSize();
		}

		public virtual void Bind(object obj, long id)
		{
			lock (i_lock)
			{
				Bind1(null, obj, id);
			}
		}

		/// <summary>TODO: This is not transactional yet.</summary>
		/// <remarks>TODO: This is not transactional yet.</remarks>
		internal void Bind1(com.db4o.Transaction ta, object obj, long id)
		{
			ta = CheckTransaction(ta);
			int intID = (int)id;
			if (obj != null)
			{
				object oldObject = GetByID(id);
				if (oldObject != null)
				{
					com.db4o.YapObject yo = GetYapObject(intID);
					if (yo != null)
					{
						if (ta.Reflector().ForObject(obj) == yo.GetYapClass().ClassReflector())
						{
							Bind2(yo, obj);
						}
						else
						{
							throw new j4o.lang.RuntimeException(com.db4o.Messages.Get(57));
						}
					}
				}
			}
		}

		internal void Bind2(com.db4o.YapObject a_yapObject, object obj)
		{
			int id = a_yapObject.GetID();
			YapObjectGCd(a_yapObject);
			a_yapObject = new com.db4o.YapObject(GetYapClass(Reflector().ForObject(obj), false
				), id);
			a_yapObject.SetObjectWeak(_this, obj);
			a_yapObject.SetStateDirty();
			IdTreeAdd(a_yapObject);
			HcTreeAdd(a_yapObject);
		}

		public virtual byte BlockSize()
		{
			return 1;
		}

		public virtual int BlocksFor(long bytes)
		{
			int blockLen = BlockSize();
			int result = (int)(bytes / blockLen);
			if (bytes % blockLen != 0)
			{
				result++;
			}
			return result;
		}

		public abstract com.db4o.PBootRecord BootRecord();

		private bool BreakDeleteForEnum(com.db4o.YapObject reference, bool userCall)
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
			return com.db4o.Platform4.Jdk().IsEnum(Reflector(), reference.GetYapClass().ClassReflector
				());
		}

		internal virtual bool CanUpdate()
		{
			return true;
		}

		internal void CheckClosed()
		{
			if (i_classCollection == null)
			{
				com.db4o.inside.Exceptions4.ThrowRuntimeException(20, ToString());
			}
		}

		internal void CheckNeededUpdates()
		{
			if (i_needsUpdate != null)
			{
				com.db4o.foundation.Iterator4 i = new com.db4o.foundation.Iterator4Impl(i_needsUpdate
					);
				while (i.HasNext())
				{
					com.db4o.YapClass yapClass = (com.db4o.YapClass)i.Next();
					yapClass.SetStateDirty();
					yapClass.Write(i_systemTrans);
				}
				i_needsUpdate = null;
			}
		}

		internal com.db4o.Transaction CheckTransaction(com.db4o.Transaction ta)
		{
			CheckClosed();
			if (ta != null)
			{
				return ta;
			}
			return GetTransaction();
		}

		public virtual bool Close()
		{
			lock (com.db4o.Db4o.Lock)
			{
				lock (i_lock)
				{
					bool ret = Close1();
					return ret;
				}
			}
		}

		internal bool Close1()
		{
			if (i_classCollection == null)
			{
				return true;
			}
			com.db4o.Platform4.PreClose(_this);
			CheckNeededUpdates();
			if (StateMessages())
			{
				LogMsg(2, ToString());
			}
			bool closeResult = Close2();
			return closeResult;
		}

		internal virtual bool Close2()
		{
			if (HasShutDownHook())
			{
				com.db4o.Platform4.RemoveShutDownHook(this, i_lock);
			}
			i_classCollection = null;
			i_references.StopTimer();
			i_hcTree = null;
			i_idTree = null;
			i_systemTrans = null;
			i_trans = null;
			if (StateMessages())
			{
				LogMsg(3, ToString());
			}
			return true;
		}

		public virtual com.db4o.types.Db4oCollections Collections()
		{
			lock (i_lock)
			{
				if (i_handlers.i_collections == null)
				{
					i_handlers.i_collections = com.db4o.Platform4.Collections(this);
				}
				return i_handlers.i_collections;
			}
		}

		public virtual void Commit()
		{
			lock (i_lock)
			{
				Commit1();
			}
		}

		internal abstract void Commit1();

		public virtual com.db4o.config.Configuration Configure()
		{
			return i_config;
		}

		internal abstract com.db4o.ClassIndex CreateClassIndex(com.db4o.YapClass a_yapClass
			);

		internal abstract com.db4o.QueryResultImpl CreateQResult(com.db4o.Transaction a_ta
			);

		internal virtual void CreateStringIO(byte encoding)
		{
			SetStringIo(com.db4o.YapStringIO.ForEncoding(encoding));
		}

		internal virtual void CreateTransaction()
		{
			i_systemTrans = new com.db4o.Transaction(_this, null);
			i_trans = new com.db4o.Transaction(_this, i_systemTrans);
		}

		internal abstract long CurrentVersion();

		internal virtual bool CreateYapClass(com.db4o.YapClass a_yapClass, com.db4o.reflect.ReflectClass
			 a_class, com.db4o.YapClass a_superYapClass)
		{
			return a_yapClass.Init(_this, a_superYapClass, a_class, false);
		}

		/// <summary>allows special handling for all Db4oType objects.</summary>
		/// <remarks>
		/// allows special handling for all Db4oType objects.
		/// Redirected here from #set() so only instanceof check is necessary
		/// in the #set() method.
		/// </remarks>
		/// <returns>object if handled here and #set() should not continue processing</returns>
		public virtual com.db4o.types.Db4oType Db4oTypeStored(com.db4o.Transaction a_trans
			, object a_object)
		{
			if (a_object is com.db4o.ext.Db4oDatabase)
			{
				com.db4o.ext.Db4oDatabase database = (com.db4o.ext.Db4oDatabase)a_object;
				if (GetYapObject(a_object) != null)
				{
					return database;
				}
				ShowInternalClasses(true);
				com.db4o.ext.Db4oDatabase res = database.Query(a_trans);
				ShowInternalClasses(false);
				return res;
			}
			return null;
		}

		public virtual void Deactivate(object a_deactivate, int a_depth)
		{
			lock (i_lock)
			{
				Deactivate1(a_deactivate, a_depth);
			}
		}

		internal void Deactivate1(object a_deactivate, int a_depth)
		{
			CheckClosed();
			i_entryCounter++;
			try
			{
				i_justDeactivated[0] = null;
				Deactivate2(a_deactivate, a_depth);
				i_justDeactivated[0] = null;
			}
			catch (System.Exception t)
			{
				FatalException(t);
			}
			i_entryCounter--;
		}

		private void Deactivate2(object a_activate, int a_depth)
		{
			StillToDeactivate(a_activate, a_depth, true);
			while (i_stillToDeactivate != null)
			{
				com.db4o.foundation.Iterator4 i = new com.db4o.foundation.Iterator4Impl(i_stillToDeactivate
					);
				i_stillToDeactivate = null;
				while (i.HasNext())
				{
					((com.db4o.YapObject)i.Next()).Deactivate(i_trans, ((int)i.Next()));
				}
			}
		}

		public virtual void Delete(object a_object)
		{
			lock (i_lock)
			{
				com.db4o.Transaction ta = Delete1(null, a_object, true);
				ta.BeginEndSet();
			}
		}

		internal com.db4o.Transaction Delete1(com.db4o.Transaction ta, object a_object, bool
			 userCall)
		{
			ta = CheckTransaction(ta);
			if (a_object != null)
			{
				i_entryCounter++;
				try
				{
					Delete2(ta, a_object, userCall);
				}
				catch (System.Exception t)
				{
					FatalException(t);
				}
				i_entryCounter--;
			}
			return ta;
		}

		private void Delete2(com.db4o.Transaction ta, object a_object, bool userCall)
		{
			com.db4o.YapObject yo = GetYapObject(a_object);
			if (yo != null)
			{
				Delete3(ta, yo, a_object, 0, userCall);
			}
		}

		internal void Delete3(com.db4o.Transaction ta, com.db4o.YapObject yo, object a_object
			, int a_cascade, bool userCall)
		{
			if (BreakDeleteForEnum(yo, userCall))
			{
				return;
			}
			if (a_object is com.db4o.types.SecondClass)
			{
				Delete4(ta, yo, a_object, a_cascade, userCall);
			}
			else
			{
				ta.Delete(yo, a_cascade);
			}
		}

		internal void Delete4(com.db4o.Transaction ta, com.db4o.YapObject yo, object a_object
			, int a_cascade, bool userCall)
		{
			if (yo != null)
			{
				if (yo.BeginProcessing())
				{
					if (BreakDeleteForEnum(yo, userCall))
					{
						return;
					}
					com.db4o.YapClass yc = yo.GetYapClass();
					object obj = yo.GetObject();
					yo.EndProcessing();
					if (!yc.DispatchEvent(_this, obj, com.db4o.EventDispatcher.CAN_DELETE))
					{
						return;
					}
					yo.BeginProcessing();
					if (Delete5(ta, yo, a_cascade, userCall))
					{
						yc.DispatchEvent(_this, obj, com.db4o.EventDispatcher.DELETE);
						if (i_config.MessageLevel() > com.db4o.YapConst.STATE)
						{
							Message("" + yo.GetID() + " delete " + yo.GetYapClass().GetName());
						}
					}
					yo.EndProcessing();
				}
			}
		}

		internal abstract bool Delete5(com.db4o.Transaction ta, com.db4o.YapObject yapObject
			, int a_cascade, bool userCall);

		public virtual object Descend(object obj, string[] path)
		{
			lock (i_lock)
			{
				return Descend1(CheckTransaction(null), obj, path);
			}
		}

		private object Descend1(com.db4o.Transaction trans, object obj, string[] path)
		{
			com.db4o.YapObject yo = GetYapObject(obj);
			if (yo == null)
			{
				return null;
			}
			object child = null;
			string fieldName = path[0];
			if (fieldName == null)
			{
				return null;
			}
			com.db4o.YapClass yc = yo.GetYapClass();
			com.db4o.YapField[] field = new com.db4o.YapField[] { null };
			yc.ForEachYapField(new _AnonymousInnerClass547(this, fieldName, field));
			if (field[0] == null)
			{
				return null;
			}
			if (yo.IsActive())
			{
				child = field[0].Get(obj);
			}
			else
			{
				com.db4o.YapReader reader = ReadReaderByID(trans, yo.GetID());
				if (reader == null)
				{
					return null;
				}
				com.db4o.inside.marshall.MarshallerFamily mf = yc.FindOffset(reader, field[0]);
				if (mf == null)
				{
					return null;
				}
				try
				{
					child = field[0].ReadQuery(trans, mf, reader);
				}
				catch (com.db4o.CorruptionException e)
				{
				}
			}
			if (path.Length == 1)
			{
				return child;
			}
			if (child == null)
			{
				return null;
			}
			string[] subPath = new string[path.Length - 1];
			System.Array.Copy(path, 1, subPath, 0, path.Length - 1);
			return Descend1(trans, child, subPath);
		}

		private sealed class _AnonymousInnerClass547 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass547(YapStreamBase _enclosing, string fieldName, com.db4o.YapField[]
				 field)
			{
				this._enclosing = _enclosing;
				this.fieldName = fieldName;
				this.field = field;
			}

			public void Visit(object obj)
			{
				com.db4o.YapField yf = (com.db4o.YapField)obj;
				if (yf.CanAddToQuery(fieldName))
				{
					field[0] = yf;
				}
			}

			private readonly YapStreamBase _enclosing;

			private readonly string fieldName;

			private readonly com.db4o.YapField[] field;
		}

		internal virtual bool DetectSchemaChanges()
		{
			return i_config.DetectSchemaChanges();
		}

		public virtual bool DispatchsEvents()
		{
			return true;
		}

		protected virtual bool DoFinalize()
		{
			return true;
		}

		internal virtual void EmergencyClose()
		{
			i_classCollection = null;
			i_references.StopTimer();
		}

		public virtual com.db4o.ext.ExtObjectContainer Ext()
		{
			return _this;
		}

		internal virtual void FailedToShutDown()
		{
			lock (com.db4o.Db4o.Lock)
			{
				if (i_classCollection != null)
				{
					if (i_entryCounter == 0)
					{
						com.db4o.Messages.LogErr(i_config, 50, ToString(), null);
						while (!Close())
						{
						}
					}
					else
					{
						EmergencyClose();
						if (i_entryCounter > 0)
						{
							com.db4o.Messages.LogErr(i_config, 24, null, null);
						}
					}
				}
			}
		}

		internal virtual void FatalException(int msgID)
		{
			FatalException(null, msgID);
		}

		internal virtual void FatalException(System.Exception t)
		{
			FatalException(t, com.db4o.Messages.FATAL_MSG_ID);
		}

		internal virtual void FatalException(System.Exception t, int msgID)
		{
			if (!i_amDuringFatalExit)
			{
				i_amDuringFatalExit = true;
				i_classCollection = null;
				EmergencyClose();
				com.db4o.Messages.LogErr(i_config, (msgID == com.db4o.Messages.FATAL_MSG_ID ? 18 : 
					msgID), null, t);
			}
			throw new j4o.lang.RuntimeException(com.db4o.Messages.Get(msgID));
		}

		~YapStreamBase()
		{
			if (DoFinalize() && (i_config == null || i_config.AutomaticShutDown()))
			{
				FailedToShutDown();
			}
		}

		internal virtual void Gc()
		{
			i_references.PollReferenceQueue();
		}

		public virtual com.db4o.ObjectSet Get(object template)
		{
			lock (i_lock)
			{
				return Get1(null, template);
			}
		}

		internal virtual com.db4o.inside.query.ObjectSetFacade Get1(com.db4o.Transaction 
			ta, object template)
		{
			ta = CheckTransaction(ta);
			com.db4o.QueryResultImpl res = CreateQResult(ta);
			i_entryCounter++;
			try
			{
				Get2(ta, template, res);
			}
			catch (System.Exception t)
			{
				com.db4o.inside.Exceptions4.CatchAll(t);
				FatalException(t);
			}
			i_entryCounter--;
			res.Reset();
			return new com.db4o.inside.query.ObjectSetFacade(res);
		}

		private void Get2(com.db4o.Transaction ta, object template, com.db4o.QueryResultImpl
			 res)
		{
			if (template == null || j4o.lang.Class.GetClassForObject(template) == com.db4o.YapConst
				.CLASS_OBJECT)
			{
				GetAll(ta, res);
			}
			else
			{
				com.db4o.query.Query q = QuerySharpenBug(ta);
				q.Constrain(template);
				((com.db4o.QQuery)q).Execute1(res);
			}
		}

		internal abstract void GetAll(com.db4o.Transaction ta, com.db4o.QueryResultImpl a_res
			);

		public virtual object GetByID(long id)
		{
			lock (i_lock)
			{
				return GetByID1(null, id);
			}
		}

		internal object GetByID1(com.db4o.Transaction ta, long id)
		{
			ta = CheckTransaction(ta);
			try
			{
				return GetByID2(ta, (int)id);
			}
			catch (System.Exception e)
			{
				return null;
			}
		}

		internal object GetByID2(com.db4o.Transaction ta, int a_id)
		{
			if (a_id > 0)
			{
				com.db4o.YapObject yo = GetYapObject(a_id);
				if (yo != null)
				{
					object candidate = yo.GetObject();
					if (candidate != null)
					{
						return candidate;
					}
					YapObjectGCd(yo);
				}
				try
				{
					return new com.db4o.YapObject(a_id).Read(ta, null, null, 0, com.db4o.YapConst.ADD_TO_ID_TREE
						, true);
				}
				catch (System.Exception t)
				{
				}
			}
			return null;
		}

		internal object GetActivatedObjectFromCache(com.db4o.Transaction ta, int id)
		{
			object obj = GetObjectFromCache(ta, id);
			if (obj == null)
			{
				return null;
			}
			BeginEndActivation();
			Activate2(ta, obj, i_config.ActivationDepth());
			BeginEndActivation();
			return obj;
		}

		internal object GetObjectFromCache(com.db4o.Transaction ta, int id)
		{
			if (id <= 0)
			{
				return null;
			}
			com.db4o.YapObject yo = GetYapObject(id);
			if (yo == null)
			{
				return null;
			}
			object obj = yo.GetObject();
			if (obj == null)
			{
				YapObjectGCd(yo);
				return null;
			}
			return obj;
		}

		internal object ReadActivatedObjectNotInCache(com.db4o.Transaction ta, int id)
		{
			object obj = null;
			BeginEndActivation();
			try
			{
				obj = new com.db4o.YapObject(id).Read(ta, null, null, i_config.ActivationDepth(), 
					com.db4o.YapConst.ADD_TO_ID_TREE, true);
			}
			catch (System.Exception t)
			{
			}
			Activate3CheckStill(ta);
			BeginEndActivation();
			return obj;
		}

		public object GetByUUID(com.db4o.ext.Db4oUUID uuid)
		{
			lock (i_lock)
			{
				if (uuid == null)
				{
					return null;
				}
				com.db4o.Transaction ta = CheckTransaction(null);
				object[] arr = ta.ObjectAndYapObjectBySignature(uuid.GetLongPart(), uuid.GetSignaturePart
					());
				return arr[0];
			}
		}

		public virtual long GetID(object a_object)
		{
			lock (i_lock)
			{
				return GetID1(null, a_object);
			}
		}

		public int GetID1(com.db4o.Transaction ta, object a_object)
		{
			CheckClosed();
			if (a_object == null)
			{
				return 0;
			}
			com.db4o.YapObject yo = i_hcTree.Hc_find(a_object);
			if (yo != null)
			{
				return yo.GetID();
			}
			return 0;
		}

		public virtual com.db4o.ext.ObjectInfo GetObjectInfo(object obj)
		{
			lock (i_lock)
			{
				return GetYapObject(obj);
			}
		}

		internal object[] GetObjectAndYapObjectByID(com.db4o.Transaction ta, int a_id)
		{
			object[] arr = new object[2];
			if (a_id > 0)
			{
				com.db4o.YapObject yo = GetYapObject(a_id);
				if (yo != null)
				{
					object candidate = yo.GetObject();
					if (candidate != null)
					{
						arr[0] = candidate;
						arr[1] = yo;
						return arr;
					}
					YapObjectGCd(yo);
				}
				try
				{
					yo = new com.db4o.YapObject(a_id);
					arr[0] = yo.Read(ta, null, null, 0, com.db4o.YapConst.ADD_TO_ID_TREE, true);
					if (arr[0] == null)
					{
						return arr;
					}
					if (arr[0] != yo.GetObject())
					{
						return GetObjectAndYapObjectByID(ta, a_id);
					}
					arr[1] = yo;
				}
				catch (System.Exception t)
				{
				}
			}
			return arr;
		}

		internal com.db4o.YapWriter GetWriter(com.db4o.Transaction a_trans, int a_length)
		{
			return new com.db4o.YapWriter(a_trans, a_length);
		}

		public com.db4o.YapWriter GetWriter(com.db4o.Transaction a_trans, int a_address, 
			int a_length)
		{
			if (com.db4o.Debug.ExceedsMaximumBlockSize(a_length))
			{
				return null;
			}
			return new com.db4o.YapWriter(a_trans, a_address, a_length);
		}

		public com.db4o.Transaction GetSystemTransaction()
		{
			return i_systemTrans;
		}

		public com.db4o.Transaction GetTransaction()
		{
			return i_trans;
		}

		internal com.db4o.YapClass GetYapClass(com.db4o.reflect.ReflectClass a_class, bool
			 a_create)
		{
			if (a_class == null)
			{
				return null;
			}
			if ((!ShowInternalClasses()) && i_handlers.ICLASS_INTERNAL.IsAssignableFrom(a_class
				))
			{
				return null;
			}
			com.db4o.YapClass yc = i_handlers.GetYapClassStatic(a_class);
			if (yc != null)
			{
				return yc;
			}
			return i_classCollection.GetYapClass(a_class, a_create);
		}

		/// <summary>
		/// Differentiating getActiveYapClass from getYapClass is a tuning
		/// optimization: If we initialize a YapClass, #set3() has to check for
		/// the possibility that class initialization associates the currently
		/// stored object with a previously stored static object, causing the
		/// object to be known afterwards.
		/// </summary>
		/// <remarks>
		/// Differentiating getActiveYapClass from getYapClass is a tuning
		/// optimization: If we initialize a YapClass, #set3() has to check for
		/// the possibility that class initialization associates the currently
		/// stored object with a previously stored static object, causing the
		/// object to be known afterwards.
		/// In this call we only return active YapClasses, initialization
		/// is not done on purpose
		/// </remarks>
		internal com.db4o.YapClass GetActiveYapClass(com.db4o.reflect.ReflectClass a_class
			)
		{
			if (a_class == null)
			{
				return null;
			}
			if ((!ShowInternalClasses()) && i_handlers.ICLASS_INTERNAL.IsAssignableFrom(a_class
				))
			{
				return null;
			}
			com.db4o.YapClass yc = i_handlers.GetYapClassStatic(a_class);
			if (yc != null)
			{
				return yc;
			}
			return i_classCollection.GetActiveYapClass(a_class);
		}

		public virtual com.db4o.YapClass GetYapClass(int a_id)
		{
			if (a_id == 0)
			{
				return null;
			}
			com.db4o.YapClass yc = i_handlers.GetYapClassStatic(a_id);
			if (yc != null)
			{
				return yc;
			}
			return i_classCollection.GetYapClass(a_id);
		}

		internal com.db4o.YapObject GetYapObject(int a_id)
		{
			return i_idTree.Id_find(a_id);
		}

		public com.db4o.YapObject GetYapObject(object a_object)
		{
			return i_hcTree.Hc_find(a_object);
		}

		public virtual com.db4o.YapHandlers Handlers()
		{
			return i_handlers;
		}

		internal virtual bool NeedsLockFileThread()
		{
			if (!com.db4o.Platform4.HasLockFileThread())
			{
				return false;
			}
			if (com.db4o.Platform4.HasNio())
			{
				return false;
			}
			if (i_config.IsReadOnly())
			{
				return false;
			}
			return i_config.LockFile();
		}

		internal virtual bool HasShutDownHook()
		{
			return i_config.AutomaticShutDown();
		}

		internal void HcTreeAdd(com.db4o.YapObject a_yo)
		{
			i_hcTree = i_hcTree.Hc_add(a_yo);
		}

		internal void HcTreeRemove(com.db4o.YapObject a_yo)
		{
			i_hcTree = i_hcTree.Hc_remove(a_yo);
		}

		internal void IdTreeAdd(com.db4o.YapObject a_yo)
		{
			i_idTree = i_idTree.Id_add(a_yo);
		}

		internal void IdTreeRemove(int a_id)
		{
			i_idTree = i_idTree.Id_remove(a_id);
		}

		internal virtual void Initialize0()
		{
			Initialize0b();
			i_stillToSet = null;
			i_justActivated = new com.db4o.Tree[1];
		}

		internal virtual void Initialize0b()
		{
			i_justDeactivated = new com.db4o.Tree[1];
		}

		internal virtual void Initialize1()
		{
			i_config = (com.db4o.Config4Impl)((com.db4o.foundation.DeepClone)com.db4o.Db4o.Configure
				()).DeepClone(this);
			i_handlers = new com.db4o.YapHandlers(_this, i_config.Encoding(), i_config.Reflector
				());
			if (i_references != null)
			{
				Gc();
				i_references.StopTimer();
			}
			i_references = new com.db4o.YapReferences(_this);
			if (HasShutDownHook())
			{
				com.db4o.Platform4.AddShutDownHook(this, i_lock);
			}
			i_handlers.InitEncryption(i_config);
			Initialize2();
			i_stillToSet = null;
		}

		/// <summary>before file is open</summary>
		internal virtual void Initialize2()
		{
			i_idTree = new com.db4o.YapObject(0);
			i_idTree.SetObject(new object());
			i_hcTree = i_idTree;
			Initialize2NObjectCarrier();
		}

		/// <summary>overridden in YapObjectCarrier</summary>
		internal virtual void Initialize2NObjectCarrier()
		{
			i_classCollection = new com.db4o.YapClassCollection(i_systemTrans);
			i_references.StartTimer();
		}

		internal virtual void Initialize3()
		{
			i_showInternalClasses = 100000;
			Initialize4NObjectCarrier();
			i_showInternalClasses = 0;
		}

		internal virtual void Initialize4NObjectCarrier()
		{
			InitializeEssentialClasses();
			Rename(i_config);
			i_classCollection.InitOnUp(i_systemTrans);
			if (i_config.DetectSchemaChanges())
			{
				i_systemTrans.Commit();
			}
		}

		internal virtual void InitializeEssentialClasses()
		{
			for (int i = 0; i < com.db4o.YapConst.ESSENTIAL_CLASSES.Length; i++)
			{
				GetYapClass(Reflector().ForClass(com.db4o.YapConst.ESSENTIAL_CLASSES[i]), true);
			}
		}

		internal void Instantiating(bool flag)
		{
			i_instantiating = flag;
		}

		public virtual bool IsActive(object obj)
		{
			lock (i_lock)
			{
				return IsActive1(obj);
			}
		}

		internal bool IsActive1(object obj)
		{
			CheckClosed();
			if (obj != null)
			{
				com.db4o.YapObject yo = GetYapObject(obj);
				if (yo != null)
				{
					return yo.IsActive();
				}
			}
			return false;
		}

		public virtual bool IsCached(long a_id)
		{
			lock (i_lock)
			{
				if (a_id > 0)
				{
					com.db4o.YapObject yo = GetYapObject((int)a_id);
					if (yo != null)
					{
						object candidate = yo.GetObject();
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
		public virtual bool IsClient()
		{
			return false;
		}

		public virtual bool IsClosed()
		{
			lock (i_lock)
			{
				return i_classCollection == null;
			}
		}

		internal bool IsInstantiating()
		{
			return i_instantiating;
		}

		internal virtual bool IsServer()
		{
			return false;
		}

		public virtual bool IsStored(object obj)
		{
			lock (i_lock)
			{
				return IsStored1(obj);
			}
		}

		internal bool IsStored1(object obj)
		{
			com.db4o.Transaction ta = CheckTransaction(null);
			if (obj == null)
			{
				return false;
			}
			com.db4o.YapObject yo = GetYapObject(obj);
			if (yo == null)
			{
				return false;
			}
			return !ta.IsDeleted(yo.GetID());
		}

		public virtual com.db4o.reflect.ReflectClass[] KnownClasses()
		{
			lock (i_lock)
			{
				CheckClosed();
				return Reflector().KnownClasses();
			}
		}

		public virtual object Lock()
		{
			return i_lock;
		}

		internal void LogMsg(int code, string msg)
		{
			com.db4o.Messages.LogMsg(i_config, code, msg);
		}

		internal virtual bool MaintainsIndices()
		{
			return true;
		}

		internal virtual com.db4o.YapWriter Marshall(com.db4o.Transaction ta, object obj)
		{
			int[] id = { 0 };
			byte[] bytes = Marshall(obj, id);
			com.db4o.YapWriter yapBytes = new com.db4o.YapWriter(ta, bytes.Length);
			yapBytes.Append(bytes);
			yapBytes.UseSlot(id[0], 0, bytes.Length);
			return yapBytes;
		}

		internal virtual byte[] Marshall(object obj, int[] id)
		{
			com.db4o.ext.MemoryFile memoryFile = new com.db4o.ext.MemoryFile();
			memoryFile.SetInitialSize(223);
			memoryFile.SetIncrementSizeBy(300);
			GetYapClass(Reflector().ForObject(obj), true);
			com.db4o.YapObjectCarrier carrier = new com.db4o.YapObjectCarrier(_this, memoryFile
				);
			carrier.i_showInternalClasses = i_showInternalClasses;
			carrier.Set(obj);
			id[0] = (int)carrier.GetID(obj);
			carrier.Close();
			return memoryFile.GetBytes();
		}

		internal virtual void Message(string msg)
		{
			new com.db4o.Message(_this, msg);
		}

		public virtual void MigrateFrom(com.db4o.ObjectContainer objectContainer)
		{
			if (objectContainer == null)
			{
				if (_replicationCallState == com.db4o.YapConst.NONE)
				{
					return;
				}
				_replicationCallState = com.db4o.YapConst.NONE;
				if (i_handlers.i_migration != null)
				{
					i_handlers.i_migration.Terminate();
				}
				i_handlers.i_migration = null;
			}
			else
			{
				com.db4o.YapStream peer = (com.db4o.YapStream)objectContainer;
				_replicationCallState = com.db4o.YapConst.OLD;
				peer._replicationCallState = com.db4o.YapConst.OLD;
				i_handlers.i_migration = new com.db4o.inside.replication.MigrationConnection(_this
					, (com.db4o.YapStream)objectContainer);
				peer.i_handlers.i_migration = i_handlers.i_migration;
			}
		}

		internal void NeedsUpdate(com.db4o.YapClass a_yapClass)
		{
			i_needsUpdate = new com.db4o.foundation.List4(i_needsUpdate, a_yapClass);
		}

		public abstract int NewUserObject();

		public virtual object PeekPersisted(object a_object, int a_depth, bool a_committed
			)
		{
			lock (i_lock)
			{
				CheckClosed();
				i_entryCounter++;
				i_justPeeked = null;
				com.db4o.Transaction ta = a_committed ? i_systemTrans : CheckTransaction(null);
				object cloned = null;
				com.db4o.YapObject yo = GetYapObject(a_object);
				if (yo != null)
				{
					cloned = PeekPersisted1(ta, yo.GetID(), a_depth);
				}
				i_justPeeked = null;
				i_entryCounter--;
				return cloned;
			}
		}

		internal virtual object PeekPersisted1(com.db4o.Transaction a_ta, int a_id, int a_depth
			)
		{
			if (a_depth < 0)
			{
				return null;
			}
			com.db4o.TreeInt ti = new com.db4o.TreeInt(a_id);
			com.db4o.TreeIntObject tio = (com.db4o.TreeIntObject)com.db4o.Tree.Find(i_justPeeked
				, ti);
			if (tio == null)
			{
				return new com.db4o.YapObject(a_id).Read(a_ta, null, null, a_depth, com.db4o.YapConst
					.TRANSIENT, false);
			}
			else
			{
				return tio._object;
			}
		}

		internal virtual void Peeked(int a_id, object a_object)
		{
			i_justPeeked = com.db4o.Tree.Add(i_justPeeked, new com.db4o.TreeIntObject(a_id, a_object
				));
		}

		public virtual void Purge()
		{
			lock (i_lock)
			{
				Purge1();
			}
		}

		public virtual void Purge(object obj)
		{
			lock (i_lock)
			{
				Purge1(obj);
			}
		}

		internal void Purge1()
		{
			CheckClosed();
			j4o.lang.JavaSystem.Gc();
			j4o.lang.JavaSystem.RunFinalization();
			j4o.lang.JavaSystem.Gc();
			Gc();
			i_classCollection.Purge();
		}

		internal void Purge1(object obj)
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
						yo = i_hcTree.Hc_find(obj);
					}
					if (yo != null)
					{
						YapObjectGCd(yo);
					}
				}
			}
		}

		public com.db4o.inside.query.NativeQueryHandler GetNativeQueryHandler()
		{
			if (null == _nativeQueryHandler)
			{
				_nativeQueryHandler = new com.db4o.inside.query.NativeQueryHandler(_this);
			}
			return _nativeQueryHandler;
		}

		public com.db4o.ObjectSet Query(com.db4o.query.Predicate predicate)
		{
			return Query(predicate, (com.db4o.query.QueryComparator)null);
		}

		public com.db4o.ObjectSet Query(com.db4o.query.Predicate predicate, com.db4o.query.QueryComparator
			 comparator)
		{
			lock (i_lock)
			{
				return GetNativeQueryHandler().Execute(predicate, comparator);
			}
		}

		public virtual com.db4o.query.Query Query()
		{
			lock (i_lock)
			{
				return Query((com.db4o.Transaction)null);
			}
		}

		public com.db4o.ObjectSet Query(j4o.lang.Class clazz)
		{
			return Get(clazz);
		}

		internal com.db4o.query.Query Query(com.db4o.Transaction ta)
		{
			i_entryCounter++;
			com.db4o.query.Query q = new com.db4o.QQuery(CheckTransaction(ta), null, null);
			i_entryCounter--;
			return q;
		}

		internal virtual com.db4o.query.Query QuerySharpenBug()
		{
			return Query();
		}

		public virtual com.db4o.query.Query QuerySharpenBug(com.db4o.Transaction ta)
		{
			return Query(ta);
		}

		public abstract void RaiseVersion(long a_minimumVersion);

		internal abstract void ReadBytes(byte[] a_bytes, int a_address, int a_length);

		internal abstract void ReadBytes(byte[] bytes, int address, int addressOffset, int
			 length);

		public com.db4o.YapReader ReadObjectReaderByAddress(int a_address, int a_length)
		{
			if (a_address > 0)
			{
				com.db4o.YapReader reader = new com.db4o.YapReader(a_length);
				ReadBytes(reader._buffer, a_address, a_length);
				i_handlers.Decrypt(reader);
				return reader;
			}
			return null;
		}

		public com.db4o.YapWriter ReadObjectWriterByAddress(com.db4o.Transaction a_trans, 
			int a_address, int a_length)
		{
			if (a_address > 0)
			{
				com.db4o.YapWriter reader = GetWriter(a_trans, a_address, a_length);
				reader.ReadEncrypt(_this, a_address);
				return reader;
			}
			return null;
		}

		public abstract com.db4o.YapWriter ReadWriterByID(com.db4o.Transaction a_ta, int 
			a_id);

		public abstract com.db4o.YapReader ReadReaderByID(com.db4o.Transaction a_ta, int 
			a_id);

		private void Reboot()
		{
			Commit();
			int ccID = i_classCollection.GetID();
			i_references.StopTimer();
			Initialize2();
			i_classCollection.SetID(ccID);
			i_classCollection.Read(i_systemTrans);
		}

		public virtual com.db4o.reflect.generic.GenericReflector Reflector()
		{
			return i_handlers._reflector;
		}

		public virtual void Refresh(object a_refresh, int a_depth)
		{
			lock (i_lock)
			{
				i_refreshInsteadOfActivate = true;
				try
				{
					Activate1(null, a_refresh, a_depth);
				}
				finally
				{
					i_refreshInsteadOfActivate = false;
				}
			}
		}

		internal void RefreshClasses()
		{
			lock (i_lock)
			{
				i_classCollection.RefreshClasses();
			}
		}

		public abstract void ReleaseSemaphore(string name);

		internal virtual void RememberJustSet(int id)
		{
			if (i_justSet == null)
			{
				i_justSet = new com.db4o.TreeInt(id);
			}
			else
			{
				i_justSet = i_justSet.Add(new com.db4o.TreeInt(id));
			}
		}

		internal abstract void ReleaseSemaphores(com.db4o.Transaction ta);

		internal virtual void Rename(com.db4o.Config4Impl config)
		{
			bool renamedOne = false;
			if (config.Rename() != null)
			{
				renamedOne = Rename1(config);
			}
			i_classCollection.CheckChanges();
			if (renamedOne)
			{
				Reboot();
			}
		}

		protected virtual bool Rename1(com.db4o.Config4Impl config)
		{
			bool renamedOne = false;
			try
			{
				com.db4o.foundation.Iterator4 i = config.Rename().Iterator();
				while (i.HasNext())
				{
					com.db4o.Rename ren = (com.db4o.Rename)i.Next();
					if (Get(ren).Size() == 0)
					{
						bool renamed = false;
						bool isField = ren.rClass.Length > 0;
						com.db4o.YapClass yapClass = i_classCollection.GetYapClass(isField ? ren.rClass : 
							ren.rFrom);
						if (yapClass != null)
						{
							if (isField)
							{
								renamed = yapClass.RenameField(ren.rFrom, ren.rTo);
							}
							else
							{
								com.db4o.YapClass existing = i_classCollection.GetYapClass(ren.rTo);
								if (existing == null)
								{
									yapClass.SetName(ren.rTo);
									renamed = true;
								}
								else
								{
									LogMsg(9, "class " + ren.rTo);
								}
							}
						}
						if (renamed)
						{
							renamedOne = true;
							SetDirty(yapClass);
							LogMsg(8, ren.rFrom + " to " + ren.rTo);
							com.db4o.ObjectSet backren = Get(new com.db4o.Rename(ren.rClass, null, ren.rFrom)
								);
							while (backren.HasNext())
							{
								Delete(backren.Next());
							}
							Set(ren);
						}
					}
				}
			}
			catch (System.Exception t)
			{
				com.db4o.Messages.LogErr(i_config, 10, null, t);
			}
			return renamedOne;
		}

		public virtual com.db4o.replication.ReplicationProcess ReplicationBegin(com.db4o.ObjectContainer
			 peerB, com.db4o.replication.ReplicationConflictHandler conflictHandler)
		{
			return new com.db4o.ReplicationImpl(_this, peerB, conflictHandler);
		}

		internal int OldReplicationHandles(object obj)
		{
			if (_replicationCallState != com.db4o.YapConst.OLD)
			{
				return 0;
			}
			if (i_handlers.i_replication == null)
			{
				return 0;
			}
			if (obj is com.db4o.Internal4)
			{
				return 0;
			}
			com.db4o.YapObject reference = GetYapObject(obj);
			if (reference != null)
			{
				int id = reference.GetID();
				if (id > 0 && (com.db4o.TreeInt.Find(i_justSet, id) != null))
				{
					return id;
				}
			}
			return i_handlers.i_replication.TryToHandle(_this, obj);
		}

		internal virtual void Reserve(int byteCount)
		{
		}

		public virtual void Rollback()
		{
			lock (i_lock)
			{
				Rollback1();
			}
		}

		internal abstract void Rollback1();

		public virtual void Send(object obj)
		{
		}

		public virtual void Set(object a_object)
		{
			Set(a_object, com.db4o.YapConst.UNSPECIFIED);
		}

		public void Set(object a_object, int a_depth)
		{
			lock (i_lock)
			{
				CheckClosed();
				BeginEndSet(i_trans);
				SetInternal(i_trans, a_object, a_depth, true);
				BeginEndSet(i_trans);
			}
		}

		internal int SetInternal(com.db4o.Transaction ta, object a_object, bool a_checkJustSet
			)
		{
			return SetInternal(ta, a_object, com.db4o.YapConst.UNSPECIFIED, a_checkJustSet);
		}

		public int SetInternal(com.db4o.Transaction ta, object a_object, int a_depth, bool
			 a_checkJustSet)
		{
			int id = OldReplicationHandles(a_object);
			if (id != 0)
			{
				if (id < 0)
				{
					return 0;
				}
				return id;
			}
			return SetAfterReplication(ta, a_object, a_depth, a_checkJustSet);
		}

		internal int SetAfterReplication(com.db4o.Transaction ta, object obj, int depth, 
			bool checkJust)
		{
			if (obj is com.db4o.types.Db4oType)
			{
				com.db4o.types.Db4oType db4oType = Db4oTypeStored(ta, obj);
				if (db4oType != null)
				{
					return (int)GetID1(ta, db4oType);
				}
			}
			int id;
			i_entryCounter++;
			try
			{
				id = Set2(ta, obj, depth, checkJust);
			}
			catch (com.db4o.ext.ObjectNotStorableException e)
			{
				i_entryCounter--;
				throw e;
			}
			catch (com.db4o.ext.Db4oException exc)
			{
				id = 0;
				throw exc;
			}
			catch (System.Exception t)
			{
				id = 0;
				FatalException(t);
			}
			i_entryCounter--;
			return id;
		}

		public void SetByNewReplication(com.db4o.inside.replication.Db4oReplicationReferenceProvider
			 referenceProvider, object obj)
		{
			lock (i_lock)
			{
				_replicationCallState = com.db4o.YapConst.NEW;
				i_handlers._replicationReferenceProvider = referenceProvider;
				Set2(CheckTransaction(null), obj, 1, false);
				_replicationCallState = com.db4o.YapConst.NONE;
				i_handlers._replicationReferenceProvider = null;
			}
		}

		private int Set2(com.db4o.Transaction ta, object obj, int depth, bool checkJust)
		{
			int id = Set3(ta, obj, depth, checkJust);
			if (StackIsSmall())
			{
				CheckStillToSet();
			}
			return id;
		}

		internal virtual void CheckStillToSet()
		{
			com.db4o.foundation.List4 postponedStillToSet = null;
			while (i_stillToSet != null)
			{
				com.db4o.foundation.Iterator4 i = new com.db4o.foundation.Iterator4Impl(i_stillToSet
					);
				i_stillToSet = null;
				while (i.HasNext())
				{
					int updateDepth = (int)i.Next();
					com.db4o.YapObject yo = (com.db4o.YapObject)i.Next();
					com.db4o.Transaction trans = (com.db4o.Transaction)i.Next();
					if (!yo.ContinueSet(trans, updateDepth))
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

		private void NotStorable(com.db4o.reflect.ReflectClass claxx, object obj)
		{
			if (!i_config.ExceptionsOnNotStorable())
			{
				return;
			}
			if (true)
			{
				return;
			}
			if (claxx != null)
			{
				throw new com.db4o.ext.ObjectNotStorableException(claxx);
			}
			throw new com.db4o.ext.ObjectNotStorableException(obj.ToString());
		}

		public int Set3(com.db4o.Transaction a_trans, object a_object, int a_updateDepth, 
			bool a_checkJustSet)
		{
			if (a_object == null || (a_object is com.db4o.types.TransientClass))
			{
				return 0;
			}
			if (a_object is com.db4o.Db4oTypeImpl)
			{
				((com.db4o.Db4oTypeImpl)a_object).StoredTo(a_trans);
			}
			com.db4o.YapClass yc = null;
			com.db4o.YapObject yapObject = i_hcTree.Hc_find(a_object);
			if (yapObject == null)
			{
				com.db4o.reflect.ReflectClass claxx = Reflector().ForObject(a_object);
				if (claxx == null)
				{
					NotStorable(claxx, a_object);
					return 0;
				}
				yc = GetActiveYapClass(claxx);
				if (yc == null)
				{
					yc = GetYapClass(claxx, true);
					if (yc == null)
					{
						NotStorable(claxx, a_object);
						return 0;
					}
					yapObject = i_hcTree.Hc_find(a_object);
				}
			}
			else
			{
				yc = yapObject.GetYapClass();
			}
			if (yc.GetID() == com.db4o.YapHandlers.ANY_ID || yc.IsPrimitive() && !com.db4o.inside.marshall.MarshallerFamily
				.LEGACY)
			{
				NotStorable(yc.ClassReflector(), a_object);
				return 0;
			}
			bool dontDelete = true;
			if (yapObject == null)
			{
				if (!yc.DispatchEvent(_this, a_object, com.db4o.EventDispatcher.CAN_NEW))
				{
					return 0;
				}
				yapObject = new com.db4o.YapObject(0);
				if (yapObject.Store(a_trans, yc, a_object, a_updateDepth))
				{
					IdTreeAdd(yapObject);
					HcTreeAdd(yapObject);
					if (a_object is com.db4o.Db4oTypeImpl)
					{
						((com.db4o.Db4oTypeImpl)a_object).SetTrans(a_trans);
					}
					if (i_config.MessageLevel() > com.db4o.YapConst.STATE)
					{
						Message("" + yapObject.GetID() + " new " + yapObject.GetYapClass().GetName());
					}
					if (a_checkJustSet && CanUpdate())
					{
						if (!yapObject.GetYapClass().IsPrimitive())
						{
							RememberJustSet(yapObject.GetID());
							a_checkJustSet = false;
						}
					}
					StillToSet(a_trans, yapObject, a_updateDepth);
				}
			}
			else
			{
				if (CanUpdate())
				{
					int oid = yapObject.GetID();
					if (a_checkJustSet)
					{
						if (oid > 0 && (com.db4o.TreeInt.Find(i_justSet, oid) != null))
						{
							return oid;
						}
					}
					bool doUpdate = (a_updateDepth == com.db4o.YapConst.UNSPECIFIED) || (a_updateDepth
						 > 0);
					if (doUpdate)
					{
						dontDelete = false;
						a_trans.DontDelete(yapObject.GetYapClass().GetID(), oid);
						if (a_checkJustSet)
						{
							a_checkJustSet = false;
							RememberJustSet(oid);
						}
						yapObject.WriteUpdate(a_trans, a_updateDepth);
					}
				}
			}
			CheckNeededUpdates();
			int id = yapObject.GetID();
			if (a_checkJustSet && CanUpdate())
			{
				if (!yapObject.GetYapClass().IsPrimitive())
				{
					RememberJustSet(id);
				}
			}
			if (dontDelete)
			{
				a_trans.DontDelete(yapObject.GetYapClass().GetID(), id);
			}
			return id;
		}

		internal abstract void SetDirty(com.db4o.UseSystemTransaction a_object);

		public abstract bool SetSemaphore(string name, int timeout);

		internal virtual void SetStringIo(com.db4o.YapStringIO a_io)
		{
			i_handlers.i_stringHandler.SetStringIo(a_io);
		}

		internal bool ShowInternalClasses()
		{
			return IsServer() || i_showInternalClasses > 0;
		}

		/// <summary>
		/// Objects implementing the "Internal4" marker interface are
		/// not visible to queries, unless this flag is set to true.
		/// </summary>
		/// <remarks>
		/// Objects implementing the "Internal4" marker interface are
		/// not visible to queries, unless this flag is set to true.
		/// The caller should reset the flag after the call.
		/// </remarks>
		public virtual void ShowInternalClasses(bool show)
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

		private bool StackIsSmall()
		{
			return i_entryCounter < com.db4o.YapConst.MAX_STACK_DEPTH;
		}

		internal virtual bool StateMessages()
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
		internal virtual com.db4o.foundation.List4 StillTo1(com.db4o.foundation.List4 a_still
			, com.db4o.Tree[] a_just, object a_object, int a_depth, bool a_forceUnknownDeactivate
			)
		{
			if (a_object != null)
			{
				if (a_depth > 0)
				{
					com.db4o.YapObject yapObject = i_hcTree.Hc_find(a_object);
					if (yapObject != null)
					{
						int id = yapObject.GetID();
						if (a_just[0] != null)
						{
							if (((com.db4o.TreeInt)a_just[0]).Find(id) != null)
							{
								return a_still;
							}
							a_just[0] = a_just[0].Add(new com.db4o.TreeInt(id));
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
						com.db4o.reflect.ReflectClass clazz = Reflector().ForObject(a_object);
						if (clazz.IsArray())
						{
							if (!clazz.GetComponentType().IsPrimitive())
							{
								object[] arr = com.db4o.YapArray.ToArray(_this, a_object);
								for (int i = 0; i < arr.Length; i++)
								{
									a_still = StillTo1(a_still, a_just, arr[i], a_depth, a_forceUnknownDeactivate);
								}
							}
						}
						else
						{
							if (a_object is com.db4o.config.Entry)
							{
								a_still = StillTo1(a_still, a_just, ((com.db4o.config.Entry)a_object).key, a_depth
									, false);
								a_still = StillTo1(a_still, a_just, ((com.db4o.config.Entry)a_object).value, a_depth
									, false);
							}
							else
							{
								if (a_forceUnknownDeactivate)
								{
									com.db4o.YapClass yc = GetYapClass(Reflector().ForObject(a_object), false);
									if (yc != null)
									{
										yc.Deactivate(i_trans, a_object, a_depth);
									}
								}
							}
						}
					}
				}
			}
			return a_still;
		}

		internal virtual void StillToActivate(object a_object, int a_depth)
		{
			i_stillToActivate = StillTo1(i_stillToActivate, i_justActivated, a_object, a_depth
				, false);
		}

		internal virtual void StillToDeactivate(object a_object, int a_depth, bool a_forceUnknownDeactivate
			)
		{
			i_stillToDeactivate = StillTo1(i_stillToDeactivate, i_justDeactivated, a_object, 
				a_depth, a_forceUnknownDeactivate);
		}

		internal virtual void StillToSet(com.db4o.Transaction a_trans, com.db4o.YapObject
			 a_yapObject, int a_updateDepth)
		{
			if (StackIsSmall())
			{
				if (a_yapObject.ContinueSet(a_trans, a_updateDepth))
				{
					return;
				}
			}
			i_stillToSet = new com.db4o.foundation.List4(i_stillToSet, a_trans);
			i_stillToSet = new com.db4o.foundation.List4(i_stillToSet, a_yapObject);
			i_stillToSet = new com.db4o.foundation.List4(i_stillToSet, a_updateDepth);
		}

		internal virtual void StopSession()
		{
			i_classCollection = null;
		}

		public virtual com.db4o.ext.StoredClass StoredClass(object clazz)
		{
			lock (i_lock)
			{
				CheckClosed();
				return StoredClass1(clazz);
			}
		}

		internal virtual com.db4o.YapClass StoredClass1(object clazz)
		{
			try
			{
				com.db4o.reflect.ReflectClass claxx = i_config.ReflectorFor(clazz);
				if (claxx != null)
				{
					return GetYapClass(claxx, false);
				}
			}
			catch (System.Exception e)
			{
			}
			return null;
		}

		public virtual com.db4o.ext.StoredClass[] StoredClasses()
		{
			lock (i_lock)
			{
				CheckClosed();
				return i_classCollection.StoredClasses();
			}
		}

		public virtual com.db4o.YapStringIO StringIO()
		{
			return i_handlers.i_stringHandler.i_stringIo;
		}

		internal virtual object Unmarshall(com.db4o.YapWriter yapBytes)
		{
			return Unmarshall(yapBytes._buffer, yapBytes.GetID());
		}

		internal virtual object Unmarshall(byte[] bytes, int id)
		{
			com.db4o.ext.MemoryFile memoryFile = new com.db4o.ext.MemoryFile(bytes);
			com.db4o.YapObjectCarrier carrier = new com.db4o.YapObjectCarrier(_this, memoryFile
				);
			object obj = carrier.GetByID(id);
			carrier.Activate(obj, int.MaxValue);
			carrier.Close();
			return obj;
		}

		public virtual long Version()
		{
			lock (i_lock)
			{
				return CurrentVersion();
			}
		}

		internal abstract void Write(bool shuttingDown);

		internal abstract void WriteDirty();

		public abstract void WriteEmbedded(com.db4o.YapWriter a_parent, com.db4o.YapWriter
			 a_child);

		public abstract void WriteNew(com.db4o.YapClass a_yapClass, com.db4o.YapWriter aWriter
			);

		internal abstract void WriteTransactionPointer(int a_address);

		public abstract void WriteUpdate(com.db4o.YapClass a_yapClass, com.db4o.YapWriter
			 a_bytes);

		internal void YapObjectGCd(com.db4o.YapObject yo)
		{
			HcTreeRemove(yo);
			IdTreeRemove(yo.GetID());
			yo.SetID(-1);
			com.db4o.Platform4.KillYapRef(yo.i_object);
		}

		private static com.db4o.YapStream Cast(com.db4o.YapStreamBase obj)
		{
			return (com.db4o.YapStream)obj;
		}
	}
}
