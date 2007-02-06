namespace com.db4o.@internal
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
	public abstract partial class PartialObjectContainer : com.db4o.types.TransientClass
		, com.db4o.Internal4, com.db4o.@internal.ObjectContainerSpec
	#else
	public abstract class PartialObjectContainer : com.db4o.types.TransientClass, com.db4o.Internal4
		, com.db4o.@internal.ObjectContainerSpec
	#endif
	{
		private bool i_amDuringFatalExit = false;

		protected com.db4o.@internal.ClassMetadataRepository _classCollection;

		protected com.db4o.@internal.cs.ClassInfoHelper _classMetaHelper = new com.db4o.@internal.cs.ClassInfoHelper
			();

		protected com.db4o.@internal.Config4Impl i_config;

		private int _stackDepth;

		private com.db4o.@internal.ObjectReference i_hcTree;

		private com.db4o.@internal.ObjectReference i_idTree;

		private com.db4o.foundation.Tree i_justPeeked;

		public readonly object i_lock;

		private com.db4o.foundation.List4 i_needsUpdate;

		internal readonly com.db4o.@internal.ObjectContainerBase i_parent;

		internal bool i_refreshInsteadOfActivate;

		internal int i_showInternalClasses = 0;

		private com.db4o.foundation.List4 i_stillToActivate;

		private com.db4o.foundation.List4 i_stillToDeactivate;

		private com.db4o.foundation.List4 i_stillToSet;

		protected com.db4o.@internal.Transaction i_systemTrans;

		protected com.db4o.@internal.Transaction i_trans;

		private bool i_instantiating;

		public com.db4o.@internal.HandlerRegistry i_handlers;

		internal int _replicationCallState;

		internal com.db4o.@internal.WeakReferenceCollector i_references;

		private com.db4o.@internal.query.NativeQueryHandler _nativeQueryHandler;

		private readonly com.db4o.@internal.ObjectContainerBase _this;

		private com.db4o.@internal.callbacks.Callbacks _callbacks = new com.db4o.@internal.callbacks.NullCallbacks
			();

		protected readonly com.db4o.foundation.PersistentTimeStampIdGenerator _timeStampIdGenerator
			 = new com.db4o.foundation.PersistentTimeStampIdGenerator();

		private int _topLevelCallId = 1;

		private com.db4o.foundation.IntIdGenerator _topLevelCallIdGenerator = new com.db4o.foundation.IntIdGenerator
			();

		protected PartialObjectContainer(com.db4o.config.Configuration config, com.db4o.@internal.ObjectContainerBase
			 a_parent)
		{
			_this = Cast(this);
			i_parent = a_parent == null ? _this : a_parent;
			i_lock = a_parent == null ? new object() : a_parent.i_lock;
			InitializeTransactions();
			Initialize1(config);
		}

		public virtual void Activate(object a_activate, int a_depth)
		{
			lock (i_lock)
			{
				Activate1(null, a_activate, a_depth);
			}
		}

		public void Activate1(com.db4o.@internal.Transaction ta, object a_activate)
		{
			Activate1(ta, a_activate, ConfigImpl().ActivationDepth());
		}

		public void Activate1(com.db4o.@internal.Transaction ta, object a_activate, int a_depth
			)
		{
			Activate2(CheckTransaction(ta), a_activate, a_depth);
		}

		internal void Activate2(com.db4o.@internal.Transaction ta, object a_activate, int
			 a_depth)
		{
			BeginTopLevelCall();
			try
			{
				StillToActivate(a_activate, a_depth);
				Activate3CheckStill(ta);
			}
			catch (System.Exception t)
			{
				FatalException(t);
			}
			finally
			{
				EndTopLevelCall();
			}
		}

		internal void Activate3CheckStill(com.db4o.@internal.Transaction ta)
		{
			while (i_stillToActivate != null)
			{
				System.Collections.IEnumerator i = new com.db4o.foundation.Iterator4Impl(i_stillToActivate
					);
				i_stillToActivate = null;
				while (i.MoveNext())
				{
					com.db4o.@internal.ObjectReference yo = (com.db4o.@internal.ObjectReference)i.Current;
					i.MoveNext();
					int depth = ((int)i.Current);
					object obj = yo.GetObject();
					if (obj == null)
					{
						RemoveReference(yo);
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
		public void Bind1(com.db4o.@internal.Transaction ta, object obj, long id)
		{
			ta = CheckTransaction(ta);
			int intID = (int)id;
			if (obj != null)
			{
				object oldObject = GetByID(id);
				if (oldObject != null)
				{
					com.db4o.@internal.ObjectReference yo = GetYapObject(intID);
					if (yo != null)
					{
						if (ta.Reflector().ForObject(obj) == yo.GetYapClass().ClassReflector())
						{
							Bind2(yo, obj);
						}
						else
						{
							throw new System.Exception(com.db4o.@internal.Messages.Get(57));
						}
					}
				}
			}
		}

		public void Bind2(com.db4o.@internal.ObjectReference a_yapObject, object obj)
		{
			int id = a_yapObject.GetID();
			RemoveReference(a_yapObject);
			a_yapObject = new com.db4o.@internal.ObjectReference(GetYapClass(Reflector().ForObject
				(obj)), id);
			a_yapObject.SetObjectWeak(_this, obj);
			a_yapObject.SetStateDirty();
			AddToReferenceSystem(a_yapObject);
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

		private bool BreakDeleteForEnum(com.db4o.@internal.ObjectReference reference, bool
			 userCall)
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
			return com.db4o.@internal.Platform4.Jdk().IsEnum(Reflector(), reference.GetYapClass
				().ClassReflector());
		}

		internal virtual bool CanUpdate()
		{
			return true;
		}

		public void CheckClosed()
		{
			if (_classCollection == null)
			{
				com.db4o.@internal.Exceptions4.ThrowRuntimeException(20, ToString());
			}
		}

		internal void CheckNeededUpdates()
		{
			if (i_needsUpdate != null)
			{
				System.Collections.IEnumerator i = new com.db4o.foundation.Iterator4Impl(i_needsUpdate
					);
				while (i.MoveNext())
				{
					com.db4o.@internal.ClassMetadata yapClass = (com.db4o.@internal.ClassMetadata)i.Current;
					yapClass.SetStateDirty();
					yapClass.Write(i_systemTrans);
				}
				i_needsUpdate = null;
			}
		}

		public com.db4o.@internal.Transaction CheckTransaction(com.db4o.@internal.Transaction
			 ta)
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
			lock (com.db4o.@internal.Global4.Lock)
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
			if (_classCollection == null)
			{
				return true;
			}
			com.db4o.@internal.Platform4.PreClose(_this);
			CheckNeededUpdates();
			if (StateMessages())
			{
				LogMsg(2, ToString());
			}
			bool closeResult = Close2();
			return closeResult;
		}

		protected virtual bool Close2()
		{
			StopSession();
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
					i_handlers.i_collections = com.db4o.@internal.Platform4.Collections(this);
				}
				return i_handlers.i_collections;
			}
		}

		public virtual void Commit()
		{
			lock (i_lock)
			{
				BeginTopLevelCall();
				try
				{
					Commit1();
				}
				finally
				{
					EndTopLevelCall();
				}
			}
		}

		public abstract void Commit1();

		public virtual com.db4o.config.Configuration Configure()
		{
			return ConfigImpl();
		}

		public virtual com.db4o.@internal.Config4Impl Config()
		{
			return ConfigImpl();
		}

		public abstract int ConverterVersion();

		public abstract com.db4o.@internal.query.result.AbstractQueryResult NewQueryResult
			(com.db4o.@internal.Transaction trans, com.db4o.config.QueryEvaluationMode mode);

		protected virtual void CreateStringIO(byte encoding)
		{
			SetStringIo(com.db4o.@internal.LatinStringIO.ForEncoding(encoding));
		}

		protected void InitializeTransactions()
		{
			i_systemTrans = NewTransaction(null);
			i_trans = NewTransaction();
		}

		public abstract com.db4o.@internal.Transaction NewTransaction(com.db4o.@internal.Transaction
			 parentTransaction);

		public virtual com.db4o.@internal.Transaction NewTransaction()
		{
			return NewTransaction(i_systemTrans);
		}

		public abstract long CurrentVersion();

		public virtual bool CreateYapClass(com.db4o.@internal.ClassMetadata a_yapClass, com.db4o.reflect.ReflectClass
			 a_class, com.db4o.@internal.ClassMetadata a_superYapClass)
		{
			return a_yapClass.Init(_this, a_superYapClass, a_class);
		}

		/// <summary>allows special handling for all Db4oType objects.</summary>
		/// <remarks>
		/// allows special handling for all Db4oType objects.
		/// Redirected here from #set() so only instanceof check is necessary
		/// in the #set() method.
		/// </remarks>
		/// <returns>object if handled here and #set() should not continue processing</returns>
		public virtual com.db4o.types.Db4oType Db4oTypeStored(com.db4o.@internal.Transaction
			 a_trans, object a_object)
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
				BeginTopLevelCall();
				try
				{
					Deactivate1(a_deactivate, a_depth);
				}
				catch (System.Exception t)
				{
					FatalException(t);
				}
				finally
				{
					EndTopLevelCall();
				}
			}
		}

		private void Deactivate1(object a_activate, int a_depth)
		{
			StillToDeactivate(a_activate, a_depth, true);
			while (i_stillToDeactivate != null)
			{
				System.Collections.IEnumerator i = new com.db4o.foundation.Iterator4Impl(i_stillToDeactivate
					);
				i_stillToDeactivate = null;
				while (i.MoveNext())
				{
					com.db4o.@internal.ObjectReference currentObject = (com.db4o.@internal.ObjectReference
						)i.Current;
					i.MoveNext();
					int currentInteger = ((int)i.Current);
					currentObject.Deactivate(i_trans, currentInteger);
				}
			}
		}

		public virtual void Delete(object a_object)
		{
			Delete(null, a_object);
		}

		public virtual void Delete(com.db4o.@internal.Transaction trans, object obj)
		{
			lock (i_lock)
			{
				trans = CheckTransaction(trans);
				Delete1(trans, obj, true);
				trans.ProcessDeletes();
			}
		}

		public void Delete1(com.db4o.@internal.Transaction trans, object obj, bool userCall
			)
		{
			if (obj == null)
			{
				return;
			}
			com.db4o.@internal.ObjectReference @ref = GetYapObject(obj);
			if (@ref == null)
			{
				return;
			}
			try
			{
				Delete2(trans, @ref, obj, 0, userCall);
			}
			catch (System.Exception t)
			{
				FatalException(t);
			}
		}

		public void Delete2(com.db4o.@internal.Transaction trans, com.db4o.@internal.ObjectReference
			 @ref, object obj, int cascade, bool userCall)
		{
			if (BreakDeleteForEnum(@ref, userCall))
			{
				return;
			}
			if (obj is com.db4o.types.SecondClass)
			{
				if (!FlagForDelete(@ref))
				{
					return;
				}
				Delete3(trans, @ref, cascade, userCall);
				return;
			}
			trans.Delete(@ref, @ref.GetID(), cascade);
		}

		internal void Delete3(com.db4o.@internal.Transaction trans, com.db4o.@internal.ObjectReference
			 @ref, int cascade, bool userCall)
		{
			if (@ref == null || !@ref.BeginProcessing())
			{
				return;
			}
			if (BreakDeleteForEnum(@ref, userCall))
			{
				@ref.EndProcessing();
				return;
			}
			if (!@ref.IsFlaggedForDelete())
			{
				@ref.EndProcessing();
				return;
			}
			com.db4o.@internal.ClassMetadata yc = @ref.GetYapClass();
			object obj = @ref.GetObject();
			@ref.EndProcessing();
			if (!ObjectCanDelete(yc, obj))
			{
				return;
			}
			@ref.BeginProcessing();
			if (Delete4(trans, @ref, cascade, userCall))
			{
				ObjectOnDelete(yc, obj);
				if (ConfigImpl().MessageLevel() > com.db4o.@internal.Const4.STATE)
				{
					Message(string.Empty + @ref.GetID() + " delete " + @ref.GetYapClass().GetName());
				}
			}
			@ref.EndProcessing();
		}

		private bool ObjectCanDelete(com.db4o.@internal.ClassMetadata yc, object obj)
		{
			return _this.Callbacks().ObjectCanDelete(obj) && yc.DispatchEvent(_this, obj, com.db4o.@internal.EventDispatcher
				.CAN_DELETE);
		}

		private void ObjectOnDelete(com.db4o.@internal.ClassMetadata yc, object obj)
		{
			_this.Callbacks().ObjectOnDelete(obj);
			yc.DispatchEvent(_this, obj, com.db4o.@internal.EventDispatcher.DELETE);
		}

		public abstract bool Delete4(com.db4o.@internal.Transaction ta, com.db4o.@internal.ObjectReference
			 yapObject, int a_cascade, bool userCall);

		public virtual object Descend(object obj, string[] path)
		{
			lock (i_lock)
			{
				return Descend1(CheckTransaction(null), obj, path);
			}
		}

		private object Descend1(com.db4o.@internal.Transaction trans, object obj, string[]
			 path)
		{
			com.db4o.@internal.ObjectReference yo = GetYapObject(obj);
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
			com.db4o.@internal.ClassMetadata yc = yo.GetYapClass();
			com.db4o.@internal.FieldMetadata[] field = new com.db4o.@internal.FieldMetadata[]
				 { null };
			yc.ForEachYapField(new _AnonymousInnerClass564(this, fieldName, field));
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
				com.db4o.@internal.Buffer reader = ReadReaderByID(trans, yo.GetID());
				if (reader == null)
				{
					return null;
				}
				com.db4o.@internal.marshall.MarshallerFamily mf = yc.FindOffset(reader, field[0]);
				if (mf == null)
				{
					return null;
				}
				try
				{
					child = field[0].ReadQuery(trans, mf, reader);
				}
				catch (com.db4o.CorruptionException)
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

		private sealed class _AnonymousInnerClass564 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass564(PartialObjectContainer _enclosing, string fieldName
				, com.db4o.@internal.FieldMetadata[] field)
			{
				this._enclosing = _enclosing;
				this.fieldName = fieldName;
				this.field = field;
			}

			public void Visit(object yf)
			{
				com.db4o.@internal.FieldMetadata yapField = (com.db4o.@internal.FieldMetadata)yf;
				if (yapField.CanAddToQuery(fieldName))
				{
					field[0] = yapField;
				}
			}

			private readonly PartialObjectContainer _enclosing;

			private readonly string fieldName;

			private readonly com.db4o.@internal.FieldMetadata[] field;
		}

		public virtual bool DetectSchemaChanges()
		{
			return ConfigImpl().DetectSchemaChanges();
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
			StopSession();
		}

		public virtual com.db4o.ext.ExtObjectContainer Ext()
		{
			return _this;
		}

		internal virtual void FailedToShutDown()
		{
			lock (com.db4o.@internal.Global4.Lock)
			{
				if (_classCollection == null)
				{
					return;
				}
				if (i_amDuringFatalExit)
				{
					return;
				}
				if (_stackDepth == 0)
				{
					com.db4o.@internal.Messages.LogErr(ConfigImpl(), 50, ToString(), null);
					while (!Close())
					{
					}
				}
				else
				{
					EmergencyClose();
					if (_stackDepth > 0)
					{
						com.db4o.@internal.Messages.LogErr(ConfigImpl(), 24, null, null);
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
			FatalException(t, com.db4o.@internal.Messages.FATAL_MSG_ID);
		}

		internal virtual void FatalException(System.Exception t, int msgID)
		{
			if (!i_amDuringFatalExit)
			{
				i_amDuringFatalExit = true;
				EmergencyClose();
				com.db4o.@internal.Messages.LogErr(ConfigImpl(), (msgID == com.db4o.@internal.Messages
					.FATAL_MSG_ID ? 18 : msgID), null, t);
			}
			throw new System.Exception(com.db4o.@internal.Messages.Get(msgID));
		}

		~PartialObjectContainer()
		{
			if (DoFinalize() && (ConfigImpl() == null || ConfigImpl().AutomaticShutDown()))
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

		internal virtual com.db4o.@internal.query.ObjectSetFacade Get1(com.db4o.@internal.Transaction
			 ta, object template)
		{
			ta = CheckTransaction(ta);
			com.db4o.@internal.query.result.QueryResult res = null;
			try
			{
				res = Get2(ta, template);
			}
			catch (System.Exception t)
			{
				com.db4o.@internal.Exceptions4.CatchAllExceptDb4oException(t);
				FatalException(t);
			}
			return new com.db4o.@internal.query.ObjectSetFacade(res);
		}

		private com.db4o.@internal.query.result.QueryResult Get2(com.db4o.@internal.Transaction
			 ta, object template)
		{
			if (template == null || j4o.lang.JavaSystem.GetClassForObject(template) == com.db4o.@internal.Const4
				.CLASS_OBJECT)
			{
				return GetAll(ta);
			}
			com.db4o.query.Query q = Query(ta);
			q.Constrain(template);
			return ExecuteQuery((com.db4o.@internal.query.processor.QQuery)q);
		}

		public abstract com.db4o.@internal.query.result.AbstractQueryResult GetAll(com.db4o.@internal.Transaction
			 ta);

		public virtual object GetByID(long id)
		{
			lock (i_lock)
			{
				return GetByID1(null, id);
			}
		}

		public object GetByID1(com.db4o.@internal.Transaction ta, long id)
		{
			ta = CheckTransaction(ta);
			try
			{
				return GetByID2(ta, (int)id);
			}
			catch
			{
				return null;
			}
		}

		internal object GetByID2(com.db4o.@internal.Transaction ta, int a_id)
		{
			if (a_id > 0)
			{
				object obj = ObjectForIDFromCache(a_id);
				if (obj != null)
				{
					return obj;
				}
				try
				{
					return new com.db4o.@internal.ObjectReference(a_id).Read(ta, null, null, 0, com.db4o.@internal.Const4
						.ADD_TO_ID_TREE, true);
				}
				catch (System.Exception t)
				{
				}
			}
			return null;
		}

		public object GetActivatedObjectFromCache(com.db4o.@internal.Transaction ta, int 
			id)
		{
			object obj = ObjectForIDFromCache(id);
			if (obj == null)
			{
				return null;
			}
			Activate1(ta, obj, ConfigImpl().ActivationDepth());
			return obj;
		}

		public object ReadActivatedObjectNotInCache(com.db4o.@internal.Transaction ta, int
			 id)
		{
			object obj = null;
			BeginTopLevelCall();
			try
			{
				obj = new com.db4o.@internal.ObjectReference(id).Read(ta, null, null, ConfigImpl(
					).ActivationDepth(), com.db4o.@internal.Const4.ADD_TO_ID_TREE, true);
			}
			catch (System.Exception t)
			{
			}
			finally
			{
				EndTopLevelCall();
			}
			Activate3CheckStill(ta);
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
				com.db4o.@internal.Transaction ta = CheckTransaction(null);
				object[] arr = ta.ObjectAndYapObjectBySignature(uuid.GetLongPart(), uuid.GetSignaturePart
					());
				return arr[0];
			}
		}

		public virtual long GetID(object obj)
		{
			lock (i_lock)
			{
				return GetID1(obj);
			}
		}

		public int GetID1(object obj)
		{
			CheckClosed();
			if (obj == null)
			{
				return 0;
			}
			com.db4o.@internal.ObjectReference yo = GetYapObject(obj);
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

		public object[] GetObjectAndYapObjectByID(com.db4o.@internal.Transaction ta, int 
			a_id)
		{
			object[] arr = new object[2];
			if (a_id > 0)
			{
				com.db4o.@internal.ObjectReference yo = GetYapObject(a_id);
				if (yo != null)
				{
					object candidate = yo.GetObject();
					if (candidate != null)
					{
						arr[0] = candidate;
						arr[1] = yo;
						return arr;
					}
					RemoveReference(yo);
				}
				try
				{
					yo = new com.db4o.@internal.ObjectReference(a_id);
					arr[0] = yo.Read(ta, null, null, 0, com.db4o.@internal.Const4.ADD_TO_ID_TREE, true
						);
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

		public com.db4o.@internal.StatefulBuffer GetWriter(com.db4o.@internal.Transaction
			 a_trans, int a_address, int a_length)
		{
			if (com.db4o.Debug.ExceedsMaximumBlockSize(a_length))
			{
				return null;
			}
			return new com.db4o.@internal.StatefulBuffer(a_trans, a_address, a_length);
		}

		public com.db4o.@internal.Transaction GetSystemTransaction()
		{
			return i_systemTrans;
		}

		public com.db4o.@internal.Transaction GetTransaction()
		{
			return i_trans;
		}

		public com.db4o.@internal.ClassMetadata GetYapClass(com.db4o.reflect.ReflectClass
			 claxx)
		{
			if (CantGetYapClass(claxx))
			{
				return null;
			}
			com.db4o.@internal.ClassMetadata yc = i_handlers.GetYapClassStatic(claxx);
			if (yc != null)
			{
				return yc;
			}
			return _classCollection.GetYapClass(claxx);
		}

		public com.db4o.@internal.ClassMetadata ProduceYapClass(com.db4o.reflect.ReflectClass
			 claxx)
		{
			if (CantGetYapClass(claxx))
			{
				return null;
			}
			com.db4o.@internal.ClassMetadata yc = i_handlers.GetYapClassStatic(claxx);
			if (yc != null)
			{
				return yc;
			}
			return _classCollection.ProduceYapClass(claxx);
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
		internal com.db4o.@internal.ClassMetadata GetActiveYapClass(com.db4o.reflect.ReflectClass
			 claxx)
		{
			if (CantGetYapClass(claxx))
			{
				return null;
			}
			com.db4o.@internal.ClassMetadata yc = i_handlers.GetYapClassStatic(claxx);
			if (yc != null)
			{
				return yc;
			}
			return _classCollection.GetActiveYapClass(claxx);
		}

		private bool CantGetYapClass(com.db4o.reflect.ReflectClass claxx)
		{
			if (claxx == null)
			{
				return true;
			}
			if ((!ShowInternalClasses()) && i_handlers.ICLASS_INTERNAL.IsAssignableFrom(claxx
				))
			{
				return true;
			}
			return false;
		}

		public virtual com.db4o.@internal.ClassMetadata GetYapClass(int id)
		{
			if (id == 0)
			{
				return null;
			}
			com.db4o.@internal.ClassMetadata yc = i_handlers.GetYapClassStatic(id);
			if (yc != null)
			{
				return yc;
			}
			return _classCollection.GetYapClass(id);
		}

		public virtual object ObjectForIDFromCache(int id)
		{
			com.db4o.@internal.ObjectReference yo = GetYapObject(id);
			if (yo == null)
			{
				return null;
			}
			object candidate = yo.GetObject();
			if (candidate == null)
			{
				RemoveReference(yo);
			}
			return candidate;
		}

		public com.db4o.@internal.ObjectReference GetYapObject(int id)
		{
			if (id <= 0)
			{
				return null;
			}
			return i_idTree.Id_find(id);
		}

		public com.db4o.@internal.ObjectReference GetYapObject(object a_object)
		{
			return i_hcTree.Hc_find(a_object);
		}

		public virtual com.db4o.@internal.HandlerRegistry Handlers()
		{
			return i_handlers;
		}

		public virtual bool NeedsLockFileThread()
		{
			if (!com.db4o.@internal.Platform4.HasLockFileThread())
			{
				return false;
			}
			if (com.db4o.@internal.Platform4.HasNio())
			{
				return false;
			}
			if (ConfigImpl().IsReadOnly())
			{
				return false;
			}
			return ConfigImpl().LockFile();
		}

		protected virtual bool HasShutDownHook()
		{
			return ConfigImpl().AutomaticShutDown();
		}

		internal void HcTreeAdd(com.db4o.@internal.ObjectReference @ref)
		{
			i_hcTree = i_hcTree.Hc_add(@ref);
		}

		internal void IdTreeAdd(com.db4o.@internal.ObjectReference a_yo)
		{
			i_idTree = i_idTree.Id_add(a_yo);
		}

		protected virtual void Initialize1(com.db4o.config.Configuration config)
		{
			i_config = InitializeConfig(config);
			i_handlers = new com.db4o.@internal.HandlerRegistry(_this, ConfigImpl().Encoding(
				), ConfigImpl().Reflector());
			if (i_references != null)
			{
				Gc();
				i_references.StopTimer();
			}
			i_references = new com.db4o.@internal.WeakReferenceCollector(_this);
			if (HasShutDownHook())
			{
				com.db4o.@internal.Platform4.AddShutDownHook(this, i_lock);
			}
			i_handlers.InitEncryption(ConfigImpl());
			Initialize2();
			i_stillToSet = null;
		}

		private com.db4o.@internal.Config4Impl InitializeConfig(com.db4o.config.Configuration
			 config)
		{
			com.db4o.@internal.Config4Impl impl = ((com.db4o.@internal.Config4Impl)config);
			impl.Stream(_this);
			impl.Reflector().SetTransaction(GetSystemTransaction());
			return impl;
		}

		/// <summary>before file is open</summary>
		internal virtual void Initialize2()
		{
			i_idTree = new com.db4o.@internal.ObjectReference(0);
			i_idTree.SetObject(new object());
			i_hcTree = i_idTree;
			Initialize2NObjectCarrier();
		}

		/// <summary>overridden in YapObjectCarrier</summary>
		internal virtual void Initialize2NObjectCarrier()
		{
			_classCollection = new com.db4o.@internal.ClassMetadataRepository(i_systemTrans);
			i_references.StartTimer();
		}

		protected virtual void Initialize3()
		{
			i_showInternalClasses = 100000;
			Initialize4NObjectCarrier();
			i_showInternalClasses = 0;
		}

		internal virtual void Initialize4NObjectCarrier()
		{
			InitializeEssentialClasses();
			Rename(ConfigImpl());
			_classCollection.InitOnUp(i_systemTrans);
			if (ConfigImpl().DetectSchemaChanges())
			{
				i_systemTrans.Commit();
			}
		}

		internal virtual void InitializeEssentialClasses()
		{
			for (int i = 0; i < com.db4o.@internal.Const4.ESSENTIAL_CLASSES.Length; i++)
			{
				ProduceYapClass(Reflector().ForClass(com.db4o.@internal.Const4.ESSENTIAL_CLASSES[
					i]));
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
				com.db4o.@internal.ObjectReference yo = GetYapObject(obj);
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
				return ObjectForIDFromCache((int)a_id) != null;
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
				return _classCollection == null;
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
			com.db4o.@internal.Transaction ta = CheckTransaction(null);
			if (obj == null)
			{
				return false;
			}
			com.db4o.@internal.ObjectReference yo = GetYapObject(obj);
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

		public virtual com.db4o.@internal.TypeHandler4 HandlerByID(int id)
		{
			if (id < 1)
			{
				return null;
			}
			if (i_handlers.IsSystemHandler(id))
			{
				return i_handlers.GetHandler(id);
			}
			return GetYapClass(id);
		}

		public virtual object Lock()
		{
			return i_lock;
		}

		public void LogMsg(int code, string msg)
		{
			com.db4o.@internal.Messages.LogMsg(ConfigImpl(), code, msg);
		}

		public virtual bool MaintainsIndices()
		{
			return true;
		}

		protected virtual com.db4o.@internal.StatefulBuffer Marshall(com.db4o.@internal.Transaction
			 ta, object obj)
		{
			int[] id = { 0 };
			byte[] bytes = Marshall(obj, id);
			com.db4o.@internal.StatefulBuffer yapBytes = new com.db4o.@internal.StatefulBuffer
				(ta, bytes.Length);
			yapBytes.Append(bytes);
			yapBytes.UseSlot(id[0], 0, bytes.Length);
			return yapBytes;
		}

		public virtual byte[] Marshall(object obj, int[] id)
		{
			com.db4o.ext.MemoryFile memoryFile = new com.db4o.ext.MemoryFile();
			memoryFile.SetInitialSize(223);
			memoryFile.SetIncrementSizeBy(300);
			ProduceYapClass(Reflector().ForObject(obj));
			com.db4o.@internal.TransportObjectContainer carrier = new com.db4o.@internal.TransportObjectContainer
				(Config(), _this, memoryFile);
			carrier.i_showInternalClasses = i_showInternalClasses;
			carrier.Set(obj);
			id[0] = (int)carrier.GetID(obj);
			carrier.Close();
			return memoryFile.GetBytes();
		}

		internal virtual void Message(string msg)
		{
			new com.db4o.@internal.Message(_this, msg);
		}

		public virtual void MigrateFrom(com.db4o.ObjectContainer objectContainer)
		{
			if (objectContainer == null)
			{
				if (_replicationCallState == com.db4o.@internal.Const4.NONE)
				{
					return;
				}
				_replicationCallState = com.db4o.@internal.Const4.NONE;
				if (i_handlers.i_migration != null)
				{
					i_handlers.i_migration.Terminate();
				}
				i_handlers.i_migration = null;
			}
			else
			{
				com.db4o.@internal.ObjectContainerBase peer = (com.db4o.@internal.ObjectContainerBase
					)objectContainer;
				_replicationCallState = com.db4o.@internal.Const4.OLD;
				peer._replicationCallState = com.db4o.@internal.Const4.OLD;
				i_handlers.i_migration = new com.db4o.@internal.replication.MigrationConnection(_this
					, (com.db4o.@internal.ObjectContainerBase)objectContainer);
				peer.i_handlers.i_migration = i_handlers.i_migration;
			}
		}

		public void NeedsUpdate(com.db4o.@internal.ClassMetadata a_yapClass)
		{
			i_needsUpdate = new com.db4o.foundation.List4(i_needsUpdate, a_yapClass);
		}

		public virtual long GenerateTimeStampId()
		{
			return _timeStampIdGenerator.Next();
		}

		public abstract int NewUserObject();

		public virtual object PeekPersisted(object obj, int depth, bool committed)
		{
			lock (i_lock)
			{
				BeginTopLevelCall();
				try
				{
					i_justPeeked = null;
					com.db4o.@internal.Transaction ta = committed ? i_systemTrans : CheckTransaction(
						null);
					object cloned = null;
					com.db4o.@internal.ObjectReference yo = GetYapObject(obj);
					if (yo != null)
					{
						cloned = PeekPersisted1(ta, yo.GetID(), depth);
					}
					i_justPeeked = null;
					return cloned;
				}
				finally
				{
					EndTopLevelCall();
				}
			}
		}

		internal virtual object PeekPersisted1(com.db4o.@internal.Transaction a_ta, int a_id
			, int a_depth)
		{
			if (a_depth < 0)
			{
				return null;
			}
			com.db4o.@internal.TreeInt ti = new com.db4o.@internal.TreeInt(a_id);
			com.db4o.@internal.TreeIntObject tio = (com.db4o.@internal.TreeIntObject)com.db4o.foundation.Tree
				.Find(i_justPeeked, ti);
			if (tio == null)
			{
				return new com.db4o.@internal.ObjectReference(a_id).Read(a_ta, null, null, a_depth
					, com.db4o.@internal.Const4.TRANSIENT, false);
			}
			return tio._object;
		}

		internal virtual void Peeked(int a_id, object a_object)
		{
			i_justPeeked = com.db4o.foundation.Tree.Add(i_justPeeked, new com.db4o.@internal.TreeIntObject
				(a_id, a_object));
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
			_classCollection.Purge();
		}

		internal void Purge1(object obj)
		{
			if (obj == null || i_hcTree == null)
			{
				return;
			}
			if (obj is com.db4o.@internal.ObjectReference)
			{
				RemoveReference((com.db4o.@internal.ObjectReference)obj);
				return;
			}
			com.db4o.@internal.ObjectReference @ref = GetYapObject(obj);
			if (@ref != null)
			{
				RemoveReference(@ref);
			}
		}

		public com.db4o.@internal.query.NativeQueryHandler GetNativeQueryHandler()
		{
			if (null == _nativeQueryHandler)
			{
				_nativeQueryHandler = new com.db4o.@internal.query.NativeQueryHandler(_this);
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
				return Query((com.db4o.@internal.Transaction)null);
			}
		}

		public com.db4o.ObjectSet Query(j4o.lang.Class clazz)
		{
			return Get(clazz);
		}

		public com.db4o.query.Query Query(com.db4o.@internal.Transaction ta)
		{
			return new com.db4o.@internal.query.processor.QQuery(CheckTransaction(ta), null, 
				null);
		}

		public abstract void RaiseVersion(long a_minimumVersion);

		public abstract void ReadBytes(byte[] a_bytes, int a_address, int a_length);

		public abstract void ReadBytes(byte[] bytes, int address, int addressOffset, int 
			length);

		public com.db4o.@internal.Buffer ReadReaderByAddress(int a_address, int a_length)
		{
			if (a_address > 0)
			{
				com.db4o.@internal.Buffer reader = new com.db4o.@internal.Buffer(a_length);
				ReadBytes(reader._buffer, a_address, a_length);
				i_handlers.Decrypt(reader);
				return reader;
			}
			return null;
		}

		public com.db4o.@internal.StatefulBuffer ReadWriterByAddress(com.db4o.@internal.Transaction
			 a_trans, int a_address, int a_length)
		{
			if (a_address > 0)
			{
				com.db4o.@internal.StatefulBuffer reader = GetWriter(a_trans, a_address, a_length
					);
				reader.ReadEncrypt(_this, a_address);
				return reader;
			}
			return null;
		}

		public abstract com.db4o.@internal.StatefulBuffer ReadWriterByID(com.db4o.@internal.Transaction
			 a_ta, int a_id);

		public abstract com.db4o.@internal.Buffer ReadReaderByID(com.db4o.@internal.Transaction
			 a_ta, int a_id);

		public abstract com.db4o.@internal.StatefulBuffer[] ReadWritersByIDs(com.db4o.@internal.Transaction
			 a_ta, int[] ids);

		private void Reboot()
		{
			Commit();
			int ccID = _classCollection.GetID();
			i_references.StopTimer();
			Initialize2();
			_classCollection.SetID(ccID);
			_classCollection.Read(i_systemTrans);
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
				_classCollection.RefreshClasses();
			}
		}

		public abstract void ReleaseSemaphore(string name);

		public virtual void FlagAsHandled(com.db4o.@internal.ObjectReference @ref)
		{
			@ref.FlagAsHandled(_topLevelCallId);
		}

		internal virtual bool FlagForDelete(com.db4o.@internal.ObjectReference @ref)
		{
			if (@ref == null)
			{
				return false;
			}
			if (HandledInCurrentTopLevelCall(@ref))
			{
				return false;
			}
			@ref.FlagForDelete(_topLevelCallId);
			return true;
		}

		public abstract void ReleaseSemaphores(com.db4o.@internal.Transaction ta);

		internal virtual void Rename(com.db4o.@internal.Config4Impl config)
		{
			bool renamedOne = false;
			if (config.Rename() != null)
			{
				renamedOne = Rename1(config);
			}
			_classCollection.CheckChanges();
			if (renamedOne)
			{
				Reboot();
			}
		}

		protected virtual bool Rename1(com.db4o.@internal.Config4Impl config)
		{
			bool renamedOne = false;
			try
			{
				System.Collections.IEnumerator i = config.Rename().GetEnumerator();
				while (i.MoveNext())
				{
					com.db4o.Rename ren = (com.db4o.Rename)i.Current;
					if (Get(ren).Size() == 0)
					{
						bool renamed = false;
						bool isField = ren.rClass.Length > 0;
						com.db4o.@internal.ClassMetadata yapClass = _classCollection.GetYapClass(isField ? 
							ren.rClass : ren.rFrom);
						if (yapClass != null)
						{
							if (isField)
							{
								renamed = yapClass.RenameField(ren.rFrom, ren.rTo);
							}
							else
							{
								com.db4o.@internal.ClassMetadata existing = _classCollection.GetYapClass(ren.rTo);
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
							SetDirtyInSystemTransaction(yapClass);
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
				com.db4o.@internal.Messages.LogErr(ConfigImpl(), 10, null, t);
			}
			return renamedOne;
		}

		public virtual com.db4o.replication.ReplicationProcess ReplicationBegin(com.db4o.ObjectContainer
			 peerB, com.db4o.replication.ReplicationConflictHandler conflictHandler)
		{
			return new com.db4o.ReplicationImpl(_this, peerB, conflictHandler);
		}

		public int OldReplicationHandles(object obj)
		{
			if (_replicationCallState != com.db4o.@internal.Const4.OLD)
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
			com.db4o.@internal.ObjectReference reference = GetYapObject(obj);
			if (reference != null && HandledInCurrentTopLevelCall(reference))
			{
				return reference.GetID();
			}
			return i_handlers.i_replication.TryToHandle(_this, obj);
		}

		public bool HandledInCurrentTopLevelCall(com.db4o.@internal.ObjectReference @ref)
		{
			return @ref.IsFlaggedAsHandled(_topLevelCallId);
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

		public abstract void Rollback1();

		public virtual void Send(object obj)
		{
		}

		public virtual void Set(object a_object)
		{
			Set(a_object, com.db4o.@internal.Const4.UNSPECIFIED);
		}

		public void Set(com.db4o.@internal.Transaction trans, object obj)
		{
			Set(trans, obj, com.db4o.@internal.Const4.UNSPECIFIED);
		}

		public void Set(object obj, int depth)
		{
			Set(i_trans, obj, depth);
		}

		public virtual void Set(com.db4o.@internal.Transaction trans, object obj, int depth
			)
		{
			lock (i_lock)
			{
				SetInternal(trans, obj, depth, true);
			}
		}

		public int SetInternal(com.db4o.@internal.Transaction trans, object obj, bool checkJustSet
			)
		{
			return SetInternal(trans, obj, com.db4o.@internal.Const4.UNSPECIFIED, checkJustSet
				);
		}

		public int SetInternal(com.db4o.@internal.Transaction trans, object obj, int depth
			, bool checkJustSet)
		{
			BeginTopLevelSet();
			try
			{
				int id = OldReplicationHandles(obj);
				if (id != 0)
				{
					if (id < 0)
					{
						return 0;
					}
					return id;
				}
				return SetAfterReplication(trans, obj, depth, checkJustSet);
			}
			finally
			{
				EndTopLevelSet(trans);
			}
		}

		public int SetAfterReplication(com.db4o.@internal.Transaction trans, object obj, 
			int depth, bool checkJust)
		{
			if (obj is com.db4o.types.Db4oType)
			{
				com.db4o.types.Db4oType db4oType = Db4oTypeStored(trans, obj);
				if (db4oType != null)
				{
					return GetID1(db4oType);
				}
			}
			try
			{
				return Set2(trans, obj, depth, checkJust);
			}
			catch (com.db4o.ext.ObjectNotStorableException e)
			{
				throw;
			}
			catch (com.db4o.ext.Db4oException exc)
			{
				throw;
			}
			catch (System.Exception t)
			{
				FatalException(t);
				return 0;
			}
		}

		public void SetByNewReplication(com.db4o.@internal.replication.Db4oReplicationReferenceProvider
			 referenceProvider, object obj)
		{
			lock (i_lock)
			{
				_replicationCallState = com.db4o.@internal.Const4.NEW;
				i_handlers._replicationReferenceProvider = referenceProvider;
				Set2(CheckTransaction(null), obj, 1, false);
				_replicationCallState = com.db4o.@internal.Const4.NONE;
				i_handlers._replicationReferenceProvider = null;
			}
		}

		private int Set2(com.db4o.@internal.Transaction trans, object obj, int depth, bool
			 checkJust)
		{
			int id = Set3(trans, obj, depth, checkJust);
			if (StackIsSmall())
			{
				CheckStillToSet();
			}
			return id;
		}

		public virtual void CheckStillToSet()
		{
			com.db4o.foundation.List4 postponedStillToSet = null;
			while (i_stillToSet != null)
			{
				System.Collections.IEnumerator i = new com.db4o.foundation.Iterator4Impl(i_stillToSet
					);
				i_stillToSet = null;
				while (i.MoveNext())
				{
					int updateDepth = (int)i.Current;
					i.MoveNext();
					com.db4o.@internal.ObjectReference @ref = (com.db4o.@internal.ObjectReference)i.Current;
					i.MoveNext();
					com.db4o.@internal.Transaction trans = (com.db4o.@internal.Transaction)i.Current;
					if (!@ref.ContinueSet(trans, updateDepth))
					{
						postponedStillToSet = new com.db4o.foundation.List4(postponedStillToSet, trans);
						postponedStillToSet = new com.db4o.foundation.List4(postponedStillToSet, @ref);
						postponedStillToSet = new com.db4o.foundation.List4(postponedStillToSet, updateDepth
							);
					}
				}
			}
			i_stillToSet = postponedStillToSet;
		}

		private void NotStorable(com.db4o.reflect.ReflectClass claxx, object obj)
		{
			if (!ConfigImpl().ExceptionsOnNotStorable())
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

		public int Set3(com.db4o.@internal.Transaction trans, object obj, int updateDepth
			, bool checkJustSet)
		{
			if (obj == null || (obj is com.db4o.types.TransientClass))
			{
				return 0;
			}
			if (obj is com.db4o.@internal.Db4oTypeImpl)
			{
				((com.db4o.@internal.Db4oTypeImpl)obj).StoredTo(trans);
			}
			com.db4o.@internal.ClassMetadata yc = null;
			com.db4o.@internal.ObjectReference @ref = GetYapObject(obj);
			if (@ref == null)
			{
				com.db4o.reflect.ReflectClass claxx = Reflector().ForObject(obj);
				if (claxx == null)
				{
					NotStorable(claxx, obj);
					return 0;
				}
				yc = GetActiveYapClass(claxx);
				if (yc == null)
				{
					yc = ProduceYapClass(claxx);
					if (yc == null)
					{
						NotStorable(claxx, obj);
						return 0;
					}
					@ref = GetYapObject(obj);
				}
			}
			else
			{
				yc = @ref.GetYapClass();
			}
			if (IsPlainObjectOrPrimitive(yc))
			{
				NotStorable(yc.ClassReflector(), obj);
				return 0;
			}
			if (@ref == null)
			{
				if (!ObjectCanNew(yc, obj))
				{
					return 0;
				}
				@ref = new com.db4o.@internal.ObjectReference();
				@ref.Store(trans, yc, obj);
				AddToReferenceSystem(@ref);
				if (obj is com.db4o.@internal.Db4oTypeImpl)
				{
					((com.db4o.@internal.Db4oTypeImpl)obj).SetTrans(trans);
				}
				if (ConfigImpl().MessageLevel() > com.db4o.@internal.Const4.STATE)
				{
					Message(string.Empty + @ref.GetID() + " new " + @ref.GetYapClass().GetName());
				}
				FlagAsHandled(@ref);
				StillToSet(trans, @ref, updateDepth);
			}
			else
			{
				if (CanUpdate())
				{
					if (checkJustSet)
					{
						if ((!@ref.IsNew()) && HandledInCurrentTopLevelCall(@ref))
						{
							return @ref.GetID();
						}
					}
					if (UpdateDepthSufficient(updateDepth))
					{
						FlagAsHandled(@ref);
						@ref.WriteUpdate(trans, updateDepth);
					}
				}
			}
			CheckNeededUpdates();
			return @ref.GetID();
		}

		private void AddToReferenceSystem(com.db4o.@internal.ObjectReference @ref)
		{
			IdTreeAdd(@ref);
			HcTreeAdd(@ref);
		}

		private bool UpdateDepthSufficient(int updateDepth)
		{
			return (updateDepth == com.db4o.@internal.Const4.UNSPECIFIED) || (updateDepth > 0
				);
		}

		private bool IsPlainObjectOrPrimitive(com.db4o.@internal.ClassMetadata yc)
		{
			return yc.GetID() == com.db4o.@internal.HandlerRegistry.ANY_ID || yc.IsPrimitive(
				);
		}

		private bool ObjectCanNew(com.db4o.@internal.ClassMetadata yc, object a_object)
		{
			return Callbacks().ObjectCanNew(a_object) && yc.DispatchEvent(_this, a_object, com.db4o.@internal.EventDispatcher
				.CAN_NEW);
		}

		public abstract void SetDirtyInSystemTransaction(com.db4o.@internal.PersistentBase
			 a_object);

		public abstract bool SetSemaphore(string name, int timeout);

		internal virtual void SetStringIo(com.db4o.@internal.LatinStringIO a_io)
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
			return _stackDepth < com.db4o.@internal.Const4.MAX_STACK_DEPTH;
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
		internal com.db4o.foundation.List4 StillTo1(com.db4o.foundation.List4 still, object
			 obj, int depth, bool forceUnknownDeactivate)
		{
			if (obj == null || depth <= 0)
			{
				return still;
			}
			com.db4o.@internal.ObjectReference @ref = GetYapObject(obj);
			if (@ref != null)
			{
				if (HandledInCurrentTopLevelCall(@ref))
				{
					return still;
				}
				FlagAsHandled(@ref);
				return new com.db4o.foundation.List4(new com.db4o.foundation.List4(still, depth), 
					@ref);
			}
			com.db4o.reflect.ReflectClass clazz = Reflector().ForObject(obj);
			if (clazz.IsArray())
			{
				if (!clazz.GetComponentType().IsPrimitive())
				{
					object[] arr = com.db4o.@internal.handlers.ArrayHandler.ToArray(_this, obj);
					for (int i = 0; i < arr.Length; i++)
					{
						still = StillTo1(still, arr[i], depth, forceUnknownDeactivate);
					}
				}
			}
			else
			{
				if (obj is com.db4o.config.Entry)
				{
					still = StillTo1(still, ((com.db4o.config.Entry)obj).key, depth, false);
					still = StillTo1(still, ((com.db4o.config.Entry)obj).value, depth, false);
				}
				else
				{
					if (forceUnknownDeactivate)
					{
						com.db4o.@internal.ClassMetadata yc = GetYapClass(Reflector().ForObject(obj));
						if (yc != null)
						{
							yc.Deactivate(i_trans, obj, depth);
						}
					}
				}
			}
			return still;
		}

		public virtual void StillToActivate(object a_object, int a_depth)
		{
			i_stillToActivate = StillTo1(i_stillToActivate, a_object, a_depth, false);
		}

		public virtual void StillToDeactivate(object a_object, int a_depth, bool a_forceUnknownDeactivate
			)
		{
			i_stillToDeactivate = StillTo1(i_stillToDeactivate, a_object, a_depth, a_forceUnknownDeactivate
				);
		}

		internal virtual void StillToSet(com.db4o.@internal.Transaction a_trans, com.db4o.@internal.ObjectReference
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

		protected virtual void StopSession()
		{
			if (HasShutDownHook())
			{
				com.db4o.@internal.Platform4.RemoveShutDownHook(this, i_lock);
			}
			_classCollection = null;
			i_references.StopTimer();
		}

		public virtual com.db4o.ext.StoredClass StoredClass(object clazz)
		{
			lock (i_lock)
			{
				CheckClosed();
				com.db4o.reflect.ReflectClass claxx = ConfigImpl().ReflectorFor(clazz);
				if (claxx == null)
				{
					return null;
				}
				return GetYapClass(claxx);
			}
		}

		public virtual com.db4o.ext.StoredClass[] StoredClasses()
		{
			lock (i_lock)
			{
				CheckClosed();
				return _classCollection.StoredClasses();
			}
		}

		public virtual com.db4o.@internal.LatinStringIO StringIO()
		{
			return i_handlers.i_stringHandler.i_stringIo;
		}

		public abstract com.db4o.ext.SystemInfo SystemInfo();

		public void BeginTopLevelCall()
		{
			CheckClosed();
			GenerateCallIDOnTopLevel();
			_stackDepth++;
		}

		public void BeginTopLevelSet()
		{
			BeginTopLevelCall();
		}

		public void EndTopLevelCall()
		{
			_stackDepth--;
			GenerateCallIDOnTopLevel();
		}

		public void EndTopLevelSet(com.db4o.@internal.Transaction trans)
		{
			EndTopLevelCall();
			if (_stackDepth == 0)
			{
				trans.ProcessDeletes();
			}
		}

		private void GenerateCallIDOnTopLevel()
		{
			if (_stackDepth == 0)
			{
				_topLevelCallId = _topLevelCallIdGenerator.Next();
			}
		}

		public virtual int StackDepth()
		{
			return _stackDepth;
		}

		public virtual void StackDepth(int depth)
		{
			_stackDepth = depth;
		}

		public virtual int TopLevelCallId()
		{
			return _topLevelCallId;
		}

		public virtual void TopLevelCallId(int id)
		{
			_topLevelCallId = id;
		}

		public virtual object Unmarshall(com.db4o.@internal.StatefulBuffer yapBytes)
		{
			return Unmarshall(yapBytes._buffer, yapBytes.GetID());
		}

		public virtual object Unmarshall(byte[] bytes, int id)
		{
			com.db4o.ext.MemoryFile memoryFile = new com.db4o.ext.MemoryFile(bytes);
			com.db4o.@internal.TransportObjectContainer carrier = new com.db4o.@internal.TransportObjectContainer
				(Configure(), _this, memoryFile);
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

		public abstract void Write(bool shuttingDown);

		public abstract void WriteDirty();

		public abstract void WriteEmbedded(com.db4o.@internal.StatefulBuffer a_parent, com.db4o.@internal.StatefulBuffer
			 a_child);

		public abstract void WriteNew(com.db4o.@internal.ClassMetadata a_yapClass, com.db4o.@internal.StatefulBuffer
			 aWriter);

		public abstract void WriteTransactionPointer(int a_address);

		public abstract void WriteUpdate(com.db4o.@internal.ClassMetadata a_yapClass, com.db4o.@internal.StatefulBuffer
			 a_bytes);

		public void RemoveReference(com.db4o.@internal.ObjectReference @ref)
		{
			i_hcTree = i_hcTree.Hc_remove(@ref);
			i_idTree = i_idTree.Id_remove(@ref.GetID());
			@ref.SetID(-1);
			com.db4o.@internal.Platform4.KillYapRef(@ref.GetObjectReference());
		}

		private static com.db4o.@internal.ObjectContainerBase Cast(com.db4o.@internal.PartialObjectContainer
			 obj)
		{
			return (com.db4o.@internal.ObjectContainerBase)obj;
		}

		public virtual com.db4o.@internal.callbacks.Callbacks Callbacks()
		{
			return _callbacks;
		}

		public virtual void Callbacks(com.db4o.@internal.callbacks.Callbacks cb)
		{
			if (cb == null)
			{
				throw new System.ArgumentException();
			}
			_callbacks = cb;
		}

		public virtual com.db4o.@internal.Config4Impl ConfigImpl()
		{
			return i_config;
		}

		public virtual com.db4o.@internal.UUIDFieldMetadata GetUUIDIndex()
		{
			return i_handlers.i_indexes.i_fieldUUID;
		}

		public virtual com.db4o.@internal.VersionFieldMetadata GetVersionIndex()
		{
			return i_handlers.i_indexes.i_fieldVersion;
		}

		public virtual com.db4o.@internal.ClassMetadataRepository ClassCollection()
		{
			return _classCollection;
		}

		public virtual com.db4o.@internal.cs.ClassInfoHelper GetClassMetaHelper()
		{
			return _classMetaHelper;
		}

		public abstract long[] GetIDsForClass(com.db4o.@internal.Transaction trans, com.db4o.@internal.ClassMetadata
			 clazz);

		public abstract com.db4o.@internal.query.result.QueryResult ClassOnlyQuery(com.db4o.@internal.Transaction
			 trans, com.db4o.@internal.ClassMetadata clazz);

		public abstract com.db4o.@internal.query.result.QueryResult ExecuteQuery(com.db4o.@internal.query.processor.QQuery
			 query);

		public virtual void ReplicationCallState(int state)
		{
			_replicationCallState = state;
		}
	}
}
