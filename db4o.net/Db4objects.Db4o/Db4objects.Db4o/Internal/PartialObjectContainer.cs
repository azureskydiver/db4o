/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

using System;
using System.Collections;
using Db4objects.Db4o;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Ext;
using Db4objects.Db4o.Foundation;
using Db4objects.Db4o.Internal;
using Db4objects.Db4o.Internal.Activation;
using Db4objects.Db4o.Internal.CS;
using Db4objects.Db4o.Internal.Callbacks;
using Db4objects.Db4o.Internal.Handlers;
using Db4objects.Db4o.Internal.Marshall;
using Db4objects.Db4o.Internal.Query;
using Db4objects.Db4o.Internal.Query.Processor;
using Db4objects.Db4o.Internal.Query.Result;
using Db4objects.Db4o.Internal.Replication;
using Db4objects.Db4o.Internal.Slots;
using Db4objects.Db4o.Query;
using Db4objects.Db4o.Reflect;
using Db4objects.Db4o.Reflect.Generic;
using Db4objects.Db4o.Types;
using Sharpen;

namespace Db4objects.Db4o.Internal
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
	public abstract partial class PartialObjectContainer : ITransientClass, IInternal4
		, IObjectContainerSpec
	{
		protected ClassMetadataRepository _classCollection;

		protected ClassInfoHelper _classMetaHelper = new ClassInfoHelper();

		protected Config4Impl _config;

		private int _stackDepth;

		private readonly Db4objects.Db4o.Internal.ReferenceSystemRegistry _referenceSystemRegistry
			 = new Db4objects.Db4o.Internal.ReferenceSystemRegistry();

		private Tree _justPeeked;

		public readonly object _lock;

		private List4 _pendingClassUpdates;

		internal readonly ObjectContainerBase _parent;

		internal int _showInternalClasses = 0;

		private List4 _stillToActivate;

		private List4 _stillToDeactivate;

		private List4 _stillToSet;

		private Db4objects.Db4o.Internal.Transaction _systemTransaction;

		protected Db4objects.Db4o.Internal.Transaction _transaction;

		private bool _instantiating;

		public HandlerRegistry _handlers;

		internal int _replicationCallState;

		internal WeakReferenceCollector _references;

		private NativeQueryHandler _nativeQueryHandler;

		private readonly ObjectContainerBase _this;

		private ICallbacks _callbacks = new NullCallbacks();

		protected readonly PersistentTimeStampIdGenerator _timeStampIdGenerator = new PersistentTimeStampIdGenerator
			();

		private int _topLevelCallId = 1;

		private IntIdGenerator _topLevelCallIdGenerator = new IntIdGenerator();

		private bool _topLevelCallCompleted;

		protected PartialObjectContainer(IConfiguration config, ObjectContainerBase parent
			)
		{
			_this = Cast(this);
			_parent = parent == null ? _this : parent;
			_lock = parent == null ? new object() : parent._lock;
			_config = (Config4Impl)config;
		}

		/// <exception cref="OldFormatException"></exception>
		public void Open()
		{
			bool ok = false;
			lock (_lock)
			{
				try
				{
					InitializeTransactions();
					Initialize1(_config);
					OpenImpl();
					InitializePostOpen();
					ok = true;
				}
				finally
				{
					if (!ok)
					{
						ShutdownObjectContainer();
					}
				}
			}
		}

		/// <exception cref="Db4oIOException"></exception>
		protected abstract void OpenImpl();

		public virtual IActivationDepth DefaultActivationDepth(ClassMetadata classMetadata
			)
		{
			return ActivationDepthProvider().ActivationDepthFor(classMetadata, ActivationMode
				.Activate);
		}

		public virtual IActivationDepthProvider ActivationDepthProvider()
		{
			return ConfigImpl().ActivationDepthProvider();
		}

		public void Activate(Db4objects.Db4o.Internal.Transaction trans, object obj)
		{
			lock (_lock)
			{
				Activate(CheckTransaction(trans), obj, DefaultActivationDepthForObject(obj));
			}
		}

		private IActivationDepth DefaultActivationDepthForObject(object obj)
		{
			ClassMetadata classMetadata = ClassMetadataForObject(obj);
			return DefaultActivationDepth(classMetadata);
		}

		public void Activate(Db4objects.Db4o.Internal.Transaction trans, object obj, IActivationDepth
			 depth)
		{
			lock (_lock)
			{
				trans = CheckTransaction(trans);
				BeginTopLevelCall();
				try
				{
					StillToActivate(trans, obj, depth);
					ActivatePending(trans);
					CompleteTopLevelCall();
				}
				catch (Db4oException e)
				{
					CompleteTopLevelCall(e);
				}
				finally
				{
					EndTopLevelCall();
				}
			}
		}

		internal sealed class PendingActivation
		{
			public readonly ObjectReference @ref;

			public readonly IActivationDepth depth;

			public PendingActivation(ObjectReference ref_, IActivationDepth depth_)
			{
				this.@ref = ref_;
				this.depth = depth_;
			}
		}

		internal void ActivatePending(Transaction ta)
		{
			while (_stillToActivate != null)
			{
				IEnumerator i = new Iterator4Impl(_stillToActivate);
				_stillToActivate = null;
				while (i.MoveNext())
				{
					PartialObjectContainer.PendingActivation item = (PartialObjectContainer.PendingActivation
						)i.Current;
					ObjectReference @ref = item.@ref;
					object obj = @ref.GetObject();
					if (obj == null)
					{
						ta.RemoveReference(@ref);
					}
					else
					{
						@ref.ActivateInternal(ta, obj, item.depth);
					}
				}
			}
		}

		/// <exception cref="ArgumentNullException"></exception>
		/// <exception cref="ArgumentException"></exception>
		public void Bind(Transaction trans, object obj, long id)
		{
			lock (_lock)
			{
				if (obj == null)
				{
					throw new ArgumentNullException();
				}
				if (DTrace.enabled)
				{
					DTrace.Bind.Log(id, " ihc " + Runtime.IdentityHashCode(obj));
				}
				trans = CheckTransaction(trans);
				int intID = (int)id;
				object oldObject = GetByID(trans, id);
				if (oldObject == null)
				{
					throw new ArgumentException("id");
				}
				ObjectReference yo = trans.ReferenceForId(intID);
				if (yo == null)
				{
					throw new ArgumentException("obj");
				}
				if (trans.Reflector().ForObject(obj) == yo.ClassMetadata().ClassReflector())
				{
					ObjectReference newRef = Bind2(trans, yo, obj);
					newRef.VirtualAttributes(trans);
				}
				else
				{
					throw new Exception(Db4objects.Db4o.Internal.Messages.Get(57));
				}
			}
		}

		public ObjectReference Bind2(Transaction trans, ObjectReference oldRef, object obj
			)
		{
			int id = oldRef.GetID();
			trans.RemoveReference(oldRef);
			ObjectReference newRef = new ObjectReference(ClassMetadataForObject(obj), id);
			newRef.SetObjectWeak(_this, obj);
			newRef.SetStateDirty();
			trans.ReferenceSystem().AddExistingReference(newRef);
			return newRef;
		}

		public virtual ClassMetadata ClassMetadataForObject(object obj)
		{
			return ClassMetadataForReflectClass(Reflector().ForObject(obj));
		}

		public abstract byte BlockSize();

		public int BytesToBlocks(long bytes)
		{
			int blockLen = BlockSize();
			return (int)((bytes + blockLen - 1) / blockLen);
		}

		public int BlockAlignedBytes(int bytes)
		{
			return BytesToBlocks(bytes) * BlockSize();
		}

		public int BlocksToBytes(int blocks)
		{
			return blocks * BlockSize();
		}

		private bool BreakDeleteForEnum(ObjectReference reference, bool userCall)
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
			return Platform4.IsEnum(Reflector(), reference.ClassMetadata().ClassReflector());
		}

		internal virtual bool CanUpdate()
		{
			return true;
		}

		/// <exception cref="DatabaseClosedException"></exception>
		public void CheckClosed()
		{
			if (_classCollection == null)
			{
				throw new DatabaseClosedException();
			}
		}

		/// <exception cref="DatabaseReadOnlyException"></exception>
		protected void CheckReadOnly()
		{
			if (_config.IsReadOnly())
			{
				throw new DatabaseReadOnlyException();
			}
		}

		internal void ProcessPendingClassUpdates()
		{
			if (_pendingClassUpdates == null)
			{
				return;
			}
			IEnumerator i = new Iterator4Impl(_pendingClassUpdates);
			while (i.MoveNext())
			{
				ClassMetadata yapClass = (ClassMetadata)i.Current;
				yapClass.SetStateDirty();
				yapClass.Write(_systemTransaction);
			}
			_pendingClassUpdates = null;
		}

		public Transaction CheckTransaction()
		{
			return CheckTransaction(null);
		}

		public Transaction CheckTransaction(Transaction ta)
		{
			CheckClosed();
			if (ta != null)
			{
				return ta;
			}
			return Transaction();
		}

		public bool Close()
		{
			lock (_lock)
			{
				Callbacks().CloseOnStarted(Cast(this));
				if (DTrace.enabled)
				{
					DTrace.CloseCalled.LogStack(this.ToString());
				}
				Close1();
				return true;
			}
		}

		protected virtual void HandleExceptionOnClose(Exception exc)
		{
			FatalException(exc);
		}

		private void Close1()
		{
			if (_classCollection == null)
			{
				return;
			}
			ProcessPendingClassUpdates();
			if (StateMessages())
			{
				LogMsg(2, ToString());
			}
			Close2();
		}

		protected abstract void Close2();

		public void ShutdownObjectContainer()
		{
			if (DTrace.enabled)
			{
				DTrace.Close.Log();
			}
			LogMsg(3, ToString());
			lock (_lock)
			{
				StopSession();
				ShutdownDataStorage();
			}
		}

		protected abstract void ShutdownDataStorage();

		[System.ObsoleteAttribute]
		public virtual IDb4oCollections Collections(Transaction trans)
		{
			lock (_lock)
			{
				return Platform4.Collections(CheckTransaction(trans));
			}
		}

		/// <exception cref="DatabaseReadOnlyException"></exception>
		/// <exception cref="DatabaseClosedException"></exception>
		public void Commit(Transaction trans)
		{
			lock (_lock)
			{
				if (DTrace.enabled)
				{
					DTrace.Commit.Log();
				}
				trans = CheckTransaction(trans);
				CheckReadOnly();
				BeginTopLevelCall();
				try
				{
					Commit1(trans);
					trans.CommitReferenceSystem();
					CompleteTopLevelCall();
				}
				catch (Db4oException e)
				{
					CompleteTopLevelCall(e);
				}
				finally
				{
					EndTopLevelCall();
				}
			}
		}

		public abstract void Commit1(Transaction trans);

		public virtual IConfiguration Configure()
		{
			return ConfigImpl();
		}

		public virtual Config4Impl Config()
		{
			return ConfigImpl();
		}

		public abstract int ConverterVersion();

		public abstract AbstractQueryResult NewQueryResult(Transaction trans, QueryEvaluationMode
			 mode);

		protected virtual void CreateStringIO(byte encoding)
		{
			StringIO(LatinStringIO.ForEncoding(encoding));
		}

		protected void InitializeTransactions()
		{
			_systemTransaction = NewTransaction(null, CreateReferenceSystem());
			_transaction = NewUserTransaction();
		}

		public abstract Transaction NewTransaction(Transaction parentTransaction, TransactionalReferenceSystem
			 referenceSystem);

		public virtual Transaction NewUserTransaction()
		{
			return NewTransaction(SystemTransaction(), null);
		}

		public abstract long CurrentVersion();

		public virtual bool CreateClassMetadata(ClassMetadata classMeta, IReflectClass clazz
			, ClassMetadata superClassMeta)
		{
			return classMeta.Init(_this, superClassMeta, clazz);
		}

		/// <summary>allows special handling for all Db4oType objects.</summary>
		/// <remarks>
		/// allows special handling for all Db4oType objects.
		/// Redirected here from #set() so only instanceof check is necessary
		/// in the #set() method.
		/// </remarks>
		/// <returns>object if handled here and #set() should not continue processing</returns>
		public virtual IDb4oType Db4oTypeStored(Transaction trans, object obj)
		{
			if (!(obj is Db4oDatabase))
			{
				return null;
			}
			Db4oDatabase database = (Db4oDatabase)obj;
			if (trans.ReferenceForObject(obj) != null)
			{
				return database;
			}
			ShowInternalClasses(true);
			try
			{
				return database.Query(trans);
			}
			finally
			{
				ShowInternalClasses(false);
			}
		}

		/// <exception cref="DatabaseClosedException"></exception>
		public void Deactivate(Transaction trans, object obj, int depth)
		{
			lock (_lock)
			{
				trans = CheckTransaction(trans);
				BeginTopLevelCall();
				try
				{
					DeactivateInternal(trans, obj, ActivationDepthProvider().ActivationDepth(depth, ActivationMode
						.Deactivate));
					CompleteTopLevelCall();
				}
				catch (Db4oException e)
				{
					CompleteTopLevelCall(e);
				}
				finally
				{
					EndTopLevelCall();
				}
			}
		}

		private void DeactivateInternal(Transaction trans, object obj, IActivationDepth depth
			)
		{
			StillToDeactivate(trans, obj, depth, true);
			DeactivatePending(trans);
		}

		private void DeactivatePending(Transaction trans)
		{
			while (_stillToDeactivate != null)
			{
				IEnumerator i = new Iterator4Impl(_stillToDeactivate);
				_stillToDeactivate = null;
				while (i.MoveNext())
				{
					PartialObjectContainer.PendingActivation item = (PartialObjectContainer.PendingActivation
						)i.Current;
					item.@ref.Deactivate(trans, item.depth);
				}
			}
		}

		/// <exception cref="DatabaseReadOnlyException"></exception>
		/// <exception cref="DatabaseClosedException"></exception>
		public void Delete(Transaction trans, object obj)
		{
			lock (_lock)
			{
				trans = CheckTransaction(trans);
				CheckReadOnly();
				Delete1(trans, obj, true);
				trans.ProcessDeletes();
			}
		}

		public void Delete1(Transaction trans, object obj, bool userCall)
		{
			if (obj == null)
			{
				return;
			}
			ObjectReference @ref = trans.ReferenceForObject(obj);
			if (@ref == null)
			{
				return;
			}
			if (userCall)
			{
				GenerateCallIDOnTopLevel();
			}
			try
			{
				BeginTopLevelCall();
				Delete2(trans, @ref, obj, 0, userCall);
				CompleteTopLevelCall();
			}
			catch (Db4oException e)
			{
				CompleteTopLevelCall(e);
			}
			finally
			{
				EndTopLevelCall();
			}
		}

		public void Delete2(Transaction trans, ObjectReference @ref, object obj, int cascade
			, bool userCall)
		{
			if (BreakDeleteForEnum(@ref, userCall))
			{
				return;
			}
			if (obj is ISecondClass)
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

		internal void Delete3(Transaction trans, ObjectReference @ref, int cascade, bool 
			userCall)
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
			ClassMetadata yc = @ref.ClassMetadata();
			object obj = @ref.GetObject();
			@ref.EndProcessing();
			ActivateForDeletionCallback(trans, yc, obj);
			if (!ObjectCanDelete(trans, yc, obj))
			{
				return;
			}
			@ref.BeginProcessing();
			if (DTrace.enabled)
			{
				DTrace.Delete.Log(@ref.GetID());
			}
			if (Delete4(trans, @ref, cascade, userCall))
			{
				ObjectOnDelete(trans, yc, obj);
				if (ConfigImpl().MessageLevel() > Const4.State)
				{
					Message(string.Empty + @ref.GetID() + " delete " + @ref.ClassMetadata().GetName()
						);
				}
			}
			@ref.EndProcessing();
		}

		private void ActivateForDeletionCallback(Transaction trans, ClassMetadata yc, object
			 obj)
		{
			if (!IsActive(trans, obj) && (CaresAboutDeleting(yc) || CaresAboutDeleted(yc)))
			{
				Activate(trans, obj, new FixedActivationDepth(1));
			}
		}

		private bool CaresAboutDeleting(ClassMetadata yc)
		{
			return this._callbacks.CaresAboutDeleting() || yc.HasEventRegistered(_this, EventDispatcher
				.CanDelete);
		}

		private bool CaresAboutDeleted(ClassMetadata yc)
		{
			return this._callbacks.CaresAboutDeleted() || yc.HasEventRegistered(_this, EventDispatcher
				.Delete);
		}

		private bool ObjectCanDelete(Transaction transaction, ClassMetadata yc, object obj
			)
		{
			return _this.Callbacks().ObjectCanDelete(transaction, obj) && yc.DispatchEvent(_this
				, obj, EventDispatcher.CanDelete);
		}

		private void ObjectOnDelete(Transaction transaction, ClassMetadata yc, object obj
			)
		{
			_this.Callbacks().ObjectOnDelete(transaction, obj);
			yc.DispatchEvent(_this, obj, EventDispatcher.Delete);
		}

		public abstract bool Delete4(Transaction ta, ObjectReference yapObject, int a_cascade
			, bool userCall);

		internal virtual object Descend(Transaction trans, object obj, string[] path)
		{
			lock (_lock)
			{
				trans = CheckTransaction(trans);
				ObjectReference @ref = trans.ReferenceForObject(obj);
				if (@ref == null)
				{
					return null;
				}
				string fieldName = path[0];
				if (fieldName == null)
				{
					return null;
				}
				ClassMetadata classMetadata = @ref.ClassMetadata();
				FieldMetadata[] field = new FieldMetadata[] { null };
				classMetadata.ForEachFieldMetadata(new _IVisitor4_609(this, fieldName, field));
				if (field[0] == null)
				{
					return null;
				}
				object child = @ref.IsActive() ? field[0].Get(trans, obj) : DescendMarshallingContext
					(trans, @ref).ReadFieldValue(field[0]);
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
				return Descend(trans, child, subPath);
			}
		}

		private sealed class _IVisitor4_609 : IVisitor4
		{
			public _IVisitor4_609(PartialObjectContainer _enclosing, string fieldName, FieldMetadata
				[] field)
			{
				this._enclosing = _enclosing;
				this.fieldName = fieldName;
				this.field = field;
			}

			public void Visit(object yf)
			{
				FieldMetadata yapField = (FieldMetadata)yf;
				if (yapField.CanAddToQuery(fieldName))
				{
					field[0] = yapField;
				}
			}

			private readonly PartialObjectContainer _enclosing;

			private readonly string fieldName;

			private readonly FieldMetadata[] field;
		}

		private UnmarshallingContext DescendMarshallingContext(Transaction trans, ObjectReference
			 @ref)
		{
			UnmarshallingContext context = new UnmarshallingContext(trans, @ref, Const4.AddToIdTree
				, false);
			context.ActivationDepth(ActivationDepthProvider().ActivationDepth(1, ActivationMode
				.Activate));
			return context;
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

		internal void ShutdownHook()
		{
			if (IsClosed())
			{
				return;
			}
			if (AllOperationsCompleted())
			{
				Db4objects.Db4o.Internal.Messages.LogErr(ConfigImpl(), 50, ToString(), null);
				Close();
			}
			else
			{
				ShutdownObjectContainer();
				if (OperationIsProcessing())
				{
					Db4objects.Db4o.Internal.Messages.LogErr(ConfigImpl(), 24, null, null);
				}
			}
		}

		private bool OperationIsProcessing()
		{
			return _stackDepth > 0;
		}

		private bool AllOperationsCompleted()
		{
			return _stackDepth == 0;
		}

		internal virtual void FatalException(int msgID)
		{
			FatalException(null, msgID);
		}

		internal void FatalException(Exception t)
		{
			FatalException(t, Db4objects.Db4o.Internal.Messages.FatalMsgId);
		}

		internal void FatalException(Exception t, int msgID)
		{
			if (DTrace.enabled)
			{
				DTrace.FatalException.Log(t.ToString());
			}
			Db4objects.Db4o.Internal.Messages.LogErr(ConfigImpl(), (msgID == Db4objects.Db4o.Internal.Messages
				.FatalMsgId ? 18 : msgID), null, t);
			if (!IsClosed())
			{
				ShutdownObjectContainer();
			}
			throw new Exception(Db4objects.Db4o.Internal.Messages.Get(msgID));
		}

		private bool ConfiguredForAutomaticShutDown()
		{
			return (ConfigImpl() == null || ConfigImpl().AutomaticShutDown());
		}

		internal virtual void Gc()
		{
			_references.PollReferenceQueue();
		}

		public IObjectSet QueryByExample(Transaction trans, object template)
		{
			lock (_lock)
			{
				trans = CheckTransaction(trans);
				IQueryResult res = null;
				try
				{
					BeginTopLevelCall();
					res = QueryByExampleInternal(trans, template);
					CompleteTopLevelCall();
				}
				catch (Db4oException e)
				{
					CompleteTopLevelCall(e);
				}
				finally
				{
					EndTopLevelCall();
				}
				return new ObjectSetFacade(res);
			}
		}

		private IQueryResult QueryByExampleInternal(Transaction trans, object template)
		{
			if (template == null || template.GetType() == Const4.ClassObject)
			{
				return QueryAllObjects(trans);
			}
			IQuery q = Query(trans);
			q.Constrain(template);
			return ExecuteQuery((QQuery)q);
		}

		public abstract AbstractQueryResult QueryAllObjects(Transaction ta);

		/// <exception cref="DatabaseClosedException"></exception>
		/// <exception cref="InvalidIDException"></exception>
		public object GetByID(Transaction ta, long id)
		{
			lock (_lock)
			{
				if (id <= 0 || id >= int.MaxValue)
				{
					throw new ArgumentException();
				}
				CheckClosed();
				ta = CheckTransaction(ta);
				BeginTopLevelCall();
				try
				{
					object obj = GetByID2(ta, (int)id);
					CompleteTopLevelCall();
					return obj;
				}
				catch (Db4oException e)
				{
					CompleteTopLevelCall(new InvalidIDException(e));
				}
				finally
				{
					EndTopLevelCall();
				}
				return null;
			}
		}

		public virtual object GetByID2(Transaction ta, int id)
		{
			object obj = ta.ObjectForIdFromCache(id);
			if (obj != null)
			{
				return obj;
			}
			return new ObjectReference(id).Read(ta, new LegacyActivationDepth(0), Const4.AddToIdTree
				, true);
		}

		public object GetActivatedObjectFromCache(Transaction ta, int id)
		{
			object obj = ta.ObjectForIdFromCache(id);
			if (obj == null)
			{
				return null;
			}
			Activate(ta, obj);
			return obj;
		}

		public object ReadActivatedObjectNotInCache(Transaction ta, int id)
		{
			object obj = null;
			BeginTopLevelCall();
			try
			{
				obj = new ObjectReference(id).Read(ta, UnknownActivationDepth.Instance, Const4.AddToIdTree
					, true);
				CompleteTopLevelCall();
			}
			catch (Db4oException e)
			{
				CompleteTopLevelCall(e);
			}
			finally
			{
				EndTopLevelCall();
			}
			ActivatePending(ta);
			return obj;
		}

		public object GetByUUID(Transaction trans, Db4oUUID uuid)
		{
			lock (_lock)
			{
				if (uuid == null)
				{
					return null;
				}
				trans = CheckTransaction(trans);
				HardObjectReference hardRef = trans.GetHardReferenceBySignature(uuid.GetLongPart(
					), uuid.GetSignaturePart());
				return hardRef._object;
			}
		}

		public int GetID(Transaction trans, object obj)
		{
			lock (_lock)
			{
				trans = CheckTransaction(trans);
				CheckClosed();
				if (obj == null)
				{
					return 0;
				}
				ObjectReference yo = trans.ReferenceForObject(obj);
				if (yo != null)
				{
					return yo.GetID();
				}
				return 0;
			}
		}

		public IObjectInfo GetObjectInfo(Transaction trans, object obj)
		{
			lock (_lock)
			{
				trans = CheckTransaction(trans);
				return trans.ReferenceForObject(obj);
			}
		}

		public HardObjectReference GetHardObjectReferenceById(Transaction trans, int id)
		{
			if (id <= 0)
			{
				return HardObjectReference.Invalid;
			}
			ObjectReference @ref = trans.ReferenceForId(id);
			if (@ref != null)
			{
				object candidate = @ref.GetObject();
				if (candidate != null)
				{
					return new HardObjectReference(@ref, candidate);
				}
				trans.RemoveReference(@ref);
			}
			@ref = new ObjectReference(id);
			object readObject = @ref.Read(trans, new LegacyActivationDepth(0), Const4.AddToIdTree
				, true);
			if (readObject == null)
			{
				return HardObjectReference.Invalid;
			}
			if (readObject != @ref.GetObject())
			{
				return GetHardObjectReferenceById(trans, id);
			}
			return new HardObjectReference(@ref, readObject);
		}

		public StatefulBuffer GetWriter(Transaction a_trans, int a_address, int a_length)
		{
			if (Debug.ExceedsMaximumBlockSize(a_length))
			{
				return null;
			}
			return new StatefulBuffer(a_trans, a_address, a_length);
		}

		public Transaction SystemTransaction()
		{
			return _systemTransaction;
		}

		public Transaction Transaction()
		{
			return _transaction;
		}

		public ClassMetadata ClassMetadataForReflectClass(IReflectClass claxx)
		{
			if (CantGetClassMetadata(claxx))
			{
				return null;
			}
			ClassMetadata yc = _handlers.ClassMetadataForClass(claxx);
			if (yc != null)
			{
				return yc;
			}
			return _classCollection.ClassMetadataForReflectClass(claxx);
		}

		public virtual ClassMetadata ProduceClassMetadata(IReflectClass claxx)
		{
			if (CantGetClassMetadata(claxx))
			{
				return null;
			}
			ClassMetadata classMetadata = _handlers.ClassMetadataForClass(claxx);
			if (classMetadata != null)
			{
				return classMetadata;
			}
			return _classCollection.ProduceClassMetadata(claxx);
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
		internal ClassMetadata GetActiveClassMetadata(IReflectClass claxx)
		{
			if (CantGetClassMetadata(claxx))
			{
				return null;
			}
			ClassMetadata yc = _handlers.ClassMetadataForClass(claxx);
			if (yc != null)
			{
				return yc;
			}
			return _classCollection.GetActiveClassMetadata(claxx);
		}

		private bool CantGetClassMetadata(IReflectClass claxx)
		{
			if (claxx == null)
			{
				return true;
			}
			if ((!ShowInternalClasses()) && _handlers.IclassInternal.IsAssignableFrom(claxx))
			{
				return true;
			}
			return false;
		}

		public virtual int ClassMetadataIdForName(string name)
		{
			return _classCollection.ClassMetadataIdForName(name);
		}

		public virtual ClassMetadata ClassMetadataForName(string name)
		{
			return ClassMetadataForId(ClassMetadataIdForName(name));
		}

		public virtual ClassMetadata ClassMetadataForId(int id)
		{
			if (DTrace.enabled)
			{
				DTrace.YapclassById.Log(id);
			}
			if (id == 0)
			{
				return null;
			}
			ClassMetadata yc = _handlers.ClassMetadataForId(id);
			if (yc != null)
			{
				return yc;
			}
			return _classCollection.GetClassMetadata(id);
		}

		public virtual HandlerRegistry Handlers()
		{
			return _handlers;
		}

		public virtual bool NeedsLockFileThread()
		{
			if (!Platform4.HasLockFileThread())
			{
				return false;
			}
			if (Platform4.HasNio())
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

		protected virtual void Initialize1(IConfiguration config)
		{
			_config = InitializeConfig(config);
			_handlers = new HandlerRegistry(_this, ConfigImpl().Encoding(), ConfigImpl().Reflector
				());
			if (_references != null)
			{
				Gc();
				_references.StopTimer();
			}
			_references = new WeakReferenceCollector(_this);
			if (HasShutDownHook())
			{
				Platform4.AddShutDownHook(this);
			}
			_handlers.InitEncryption(ConfigImpl());
			Initialize2();
			_stillToSet = null;
		}

		private Config4Impl InitializeConfig(IConfiguration config)
		{
			Config4Impl impl = ((Config4Impl)config);
			impl.Stream(_this);
			impl.Reflector().SetTransaction(SystemTransaction());
			return impl;
		}

		/// <summary>before file is open</summary>
		internal virtual void Initialize2()
		{
			Initialize2NObjectCarrier();
		}

		public TransactionalReferenceSystem CreateReferenceSystem()
		{
			TransactionalReferenceSystem referenceSystem = new TransactionalReferenceSystem();
			_referenceSystemRegistry.AddReferenceSystem(referenceSystem);
			return referenceSystem;
		}

		/// <summary>overridden in YapObjectCarrier</summary>
		internal virtual void Initialize2NObjectCarrier()
		{
			_classCollection = new ClassMetadataRepository(_systemTransaction);
			_references.StartTimer();
		}

		private void InitializePostOpen()
		{
			_showInternalClasses = 100000;
			InitializePostOpenExcludingTransportObjectContainer();
			_showInternalClasses = 0;
		}

		protected virtual void InitializePostOpenExcludingTransportObjectContainer()
		{
			InitializeEssentialClasses();
			Rename(ConfigImpl());
			_classCollection.InitOnUp(_systemTransaction);
			if (ConfigImpl().DetectSchemaChanges())
			{
				_systemTransaction.Commit();
			}
			ConfigImpl().ApplyConfigurationItems(Cast(_this));
		}

		internal virtual void InitializeEssentialClasses()
		{
			for (int i = 0; i < Const4.EssentialClasses.Length; i++)
			{
				ProduceClassMetadata(Reflector().ForClass(Const4.EssentialClasses[i]));
			}
		}

		internal void Instantiating(bool flag)
		{
			_instantiating = flag;
		}

		internal bool IsActive(Transaction trans, object obj)
		{
			lock (_lock)
			{
				trans = CheckTransaction(trans);
				if (obj != null)
				{
					ObjectReference @ref = trans.ReferenceForObject(obj);
					if (@ref != null)
					{
						return @ref.IsActive();
					}
				}
				return false;
			}
		}

		public virtual bool IsCached(Transaction trans, long id)
		{
			lock (_lock)
			{
				trans = CheckTransaction(trans);
				return trans.ObjectForIdFromCache((int)id) != null;
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

		public bool IsClosed()
		{
			lock (_lock)
			{
				return _classCollection == null;
			}
		}

		public bool IsInstantiating()
		{
			return _instantiating;
		}

		internal virtual bool IsServer()
		{
			return false;
		}

		public bool IsStored(Transaction trans, object obj)
		{
			lock (_lock)
			{
				trans = CheckTransaction(trans);
				if (obj == null)
				{
					return false;
				}
				ObjectReference @ref = trans.ReferenceForObject(obj);
				if (@ref == null)
				{
					return false;
				}
				return !trans.IsDeleted(@ref.GetID());
			}
		}

		public virtual IReflectClass[] KnownClasses()
		{
			lock (_lock)
			{
				CheckClosed();
				return Reflector().KnownClasses();
			}
		}

		public virtual ITypeHandler4 HandlerByID(int id)
		{
			if (id < 1)
			{
				return null;
			}
			if (_handlers.IsSystemHandler(id))
			{
				return _handlers.HandlerForID(id);
			}
			return ClassMetadataForId(id);
		}

		public virtual object Lock()
		{
			return _lock;
		}

		public void LogMsg(int code, string msg)
		{
			Db4objects.Db4o.Internal.Messages.LogMsg(ConfigImpl(), code, msg);
		}

		public virtual bool MaintainsIndices()
		{
			return true;
		}

		internal virtual void Message(string msg)
		{
			new Message(_this, msg);
		}

		public virtual void MigrateFrom(IObjectContainer objectContainer)
		{
			if (objectContainer == null)
			{
				if (_replicationCallState == Const4.None)
				{
					return;
				}
				_replicationCallState = Const4.None;
				if (_handlers.i_migration != null)
				{
					_handlers.i_migration.Terminate();
				}
				_handlers.i_migration = null;
			}
			else
			{
				ObjectContainerBase peer = (ObjectContainerBase)objectContainer;
				_replicationCallState = Const4.Old;
				peer._replicationCallState = Const4.Old;
				_handlers.i_migration = new MigrationConnection(_this, (ObjectContainerBase)objectContainer
					);
				peer._handlers.i_migration = _handlers.i_migration;
			}
		}

		public void NeedsUpdate(ClassMetadata a_yapClass)
		{
			_pendingClassUpdates = new List4(_pendingClassUpdates, a_yapClass);
		}

		public virtual long GenerateTimeStampId()
		{
			return _timeStampIdGenerator.Next();
		}

		public abstract int NewUserObject();

		/// <exception cref="DatabaseClosedException"></exception>
		public object PeekPersisted(Transaction trans, object obj, IActivationDepth depth
			, bool committed)
		{
			lock (_lock)
			{
				CheckClosed();
				BeginTopLevelCall();
				try
				{
					trans = CheckTransaction(trans);
					ObjectReference @ref = trans.ReferenceForObject(obj);
					trans = committed ? _systemTransaction : trans;
					object cloned = null;
					if (@ref != null)
					{
						cloned = PeekPersisted(trans, @ref.GetID(), depth, true);
					}
					CompleteTopLevelCall();
					return cloned;
				}
				catch (Db4oException e)
				{
					CompleteTopLevelCall(e);
					return null;
				}
				finally
				{
					EndTopLevelCall();
				}
			}
		}

		public object PeekPersisted(Transaction trans, int id, IActivationDepth depth, bool
			 resetJustPeeked)
		{
			if (resetJustPeeked)
			{
				_justPeeked = null;
			}
			else
			{
				TreeInt ti = new TreeInt(id);
				TreeIntObject tio = (TreeIntObject)Tree.Find(_justPeeked, ti);
				if (tio != null)
				{
					return tio._object;
				}
			}
			object res = new ObjectReference(id).PeekPersisted(trans, depth);
			if (resetJustPeeked)
			{
				_justPeeked = null;
			}
			return res;
		}

		internal virtual void Peeked(int id, object obj)
		{
			_justPeeked = Tree.Add(_justPeeked, new TreeIntObject(id, obj));
		}

		public virtual void Purge()
		{
			lock (_lock)
			{
				CheckClosed();
				Runtime.Gc();
				Runtime.RunFinalization();
				Runtime.Gc();
				Gc();
				_classCollection.Purge();
			}
		}

		public void Purge(Transaction trans, object obj)
		{
			lock (_lock)
			{
				trans = CheckTransaction(trans);
				trans.RemoveObjectFromReferenceSystem(obj);
			}
		}

		internal void RemoveFromAllReferenceSystems(object obj)
		{
			if (obj == null)
			{
				return;
			}
			if (obj is ObjectReference)
			{
				_referenceSystemRegistry.RemoveReference((ObjectReference)obj);
				return;
			}
			_referenceSystemRegistry.RemoveObject(obj);
		}

		public NativeQueryHandler GetNativeQueryHandler()
		{
			lock (_lock)
			{
				if (null == _nativeQueryHandler)
				{
					_nativeQueryHandler = new NativeQueryHandler(Cast(_this));
				}
				return _nativeQueryHandler;
			}
		}

		public IObjectSet Query(Transaction trans, Predicate predicate)
		{
			return Query(trans, predicate, (IQueryComparator)null);
		}

		public IObjectSet Query(Transaction trans, Predicate predicate, IQueryComparator 
			comparator)
		{
			lock (_lock)
			{
				trans = CheckTransaction(trans);
				return GetNativeQueryHandler().Execute(Query(trans), predicate, comparator);
			}
		}

		public IObjectSet Query(Transaction trans, Type clazz)
		{
			return QueryByExample(trans, clazz);
		}

		public IQuery Query(Transaction ta)
		{
			return new QQuery(CheckTransaction(ta), null, null);
		}

		public abstract void RaiseVersion(long a_minimumVersion);

		/// <exception cref="Db4oIOException"></exception>
		public abstract void ReadBytes(byte[] a_bytes, int a_address, int a_length);

		/// <exception cref="Db4oIOException"></exception>
		public abstract void ReadBytes(byte[] bytes, int address, int addressOffset, int 
			length);

		/// <exception cref="Db4oIOException"></exception>
		public BufferImpl BufferByAddress(int address, int length)
		{
			CheckAddress(address);
			BufferImpl reader = new BufferImpl(length);
			ReadBytes(reader._buffer, address, length);
			_handlers.Decrypt(reader);
			return reader;
		}

		/// <exception cref="ArgumentException"></exception>
		private void CheckAddress(int address)
		{
			if (address <= 0)
			{
				throw new ArgumentException("Invalid address offset: " + address);
			}
		}

		/// <exception cref="Db4oIOException"></exception>
		public StatefulBuffer ReadWriterByAddress(Transaction a_trans, int address, int length
			)
		{
			CheckAddress(address);
			StatefulBuffer reader = GetWriter(a_trans, address, length);
			reader.ReadEncrypt(_this, address);
			return reader;
		}

		public abstract StatefulBuffer ReadWriterByID(Transaction a_ta, int a_id);

		public abstract BufferImpl ReadReaderByID(Transaction a_ta, int a_id);

		public abstract StatefulBuffer[] ReadWritersByIDs(Transaction a_ta, int[] ids);

		private void Reboot()
		{
			Commit(null);
			Close();
			Open();
		}

		public virtual GenericReflector Reflector()
		{
			return _handlers._reflector;
		}

		public void Refresh(Transaction trans, object obj, int depth)
		{
			lock (_lock)
			{
				Activate(trans, obj, RefreshActivationDepth(depth));
			}
		}

		private IActivationDepth RefreshActivationDepth(int depth)
		{
			return ActivationDepthProvider().ActivationDepth(depth, ActivationMode.Refresh);
		}

		internal void RefreshClasses()
		{
			lock (_lock)
			{
				_classCollection.RefreshClasses();
			}
		}

		public abstract void ReleaseSemaphore(string name);

		public virtual void FlagAsHandled(ObjectReference @ref)
		{
			@ref.FlagAsHandled(_topLevelCallId);
		}

		internal virtual bool FlagForDelete(ObjectReference @ref)
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

		public abstract void ReleaseSemaphores(Transaction ta);

		internal virtual void Rename(Config4Impl config)
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

		protected virtual bool Rename1(Config4Impl config)
		{
			bool renamedOne = false;
			IEnumerator i = config.Rename().GetEnumerator();
			while (i.MoveNext())
			{
				Rename ren = (Rename)i.Current;
				if (QueryByExample(SystemTransaction(), ren).Size() == 0)
				{
					bool renamed = false;
					bool isField = ren.rClass.Length > 0;
					ClassMetadata yapClass = _classCollection.GetClassMetadata(isField ? ren.rClass : 
						ren.rFrom);
					if (yapClass != null)
					{
						if (isField)
						{
							renamed = yapClass.RenameField(ren.rFrom, ren.rTo);
						}
						else
						{
							ClassMetadata existing = _classCollection.GetClassMetadata(ren.rTo);
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
						IObjectSet backren = QueryByExample(SystemTransaction(), new Rename(ren.rClass, null
							, ren.rFrom));
						while (backren.HasNext())
						{
							Delete(SystemTransaction(), backren.Next());
						}
						Store(SystemTransaction(), ren);
					}
				}
			}
			return renamedOne;
		}

		public bool HandledInCurrentTopLevelCall(ObjectReference @ref)
		{
			return @ref.IsFlaggedAsHandled(_topLevelCallId);
		}

		public abstract void Reserve(int byteCount);

		public void Rollback(Transaction trans)
		{
			lock (_lock)
			{
				trans = CheckTransaction(trans);
				CheckReadOnly();
				Rollback1(trans);
				trans.RollbackReferenceSystem();
			}
		}

		public abstract void Rollback1(Transaction trans);

		/// <param name="obj"></param>
		public virtual void Send(object obj)
		{
			throw new NotSupportedException();
		}

		/// <exception cref="DatabaseClosedException"></exception>
		/// <exception cref="DatabaseReadOnlyException"></exception>
		public void Store(Transaction trans, object obj)
		{
			Store(trans, obj, Const4.Unspecified);
		}

		/// <exception cref="DatabaseClosedException"></exception>
		/// <exception cref="DatabaseReadOnlyException"></exception>
		public void Store(Transaction trans, object obj, int depth)
		{
			lock (_lock)
			{
				StoreInternal(trans, obj, depth, true);
			}
		}

		/// <exception cref="DatabaseClosedException"></exception>
		/// <exception cref="DatabaseReadOnlyException"></exception>
		public int StoreInternal(Transaction trans, object obj, bool checkJustSet)
		{
			return StoreInternal(trans, obj, Const4.Unspecified, checkJustSet);
		}

		/// <exception cref="DatabaseClosedException"></exception>
		/// <exception cref="DatabaseReadOnlyException"></exception>
		public virtual int StoreInternal(Transaction trans, object obj, int depth, bool checkJustSet
			)
		{
			trans = CheckTransaction(trans);
			CheckReadOnly();
			BeginTopLevelSet();
			try
			{
				int id = StoreAfterReplication(trans, obj, depth, checkJustSet);
				CompleteTopLevelSet();
				return id;
			}
			catch (Db4oException e)
			{
				CompleteTopLevelSet(e);
				return 0;
			}
			finally
			{
				EndTopLevelSet(trans);
			}
		}

		public int StoreAfterReplication(Transaction trans, object obj, int depth, bool checkJust
			)
		{
			if (obj is IDb4oType)
			{
				IDb4oType db4oType = Db4oTypeStored(trans, obj);
				if (db4oType != null)
				{
					return GetID(trans, db4oType);
				}
			}
			return Store2(trans, obj, depth, checkJust);
		}

		public void StoreByNewReplication(IDb4oReplicationReferenceProvider referenceProvider
			, object obj)
		{
			lock (_lock)
			{
				_replicationCallState = Const4.New;
				_handlers._replicationReferenceProvider = referenceProvider;
				Store2(CheckTransaction(), obj, 1, false);
				_replicationCallState = Const4.None;
				_handlers._replicationReferenceProvider = null;
			}
		}

		private int Store2(Transaction trans, object obj, int depth, bool checkJust)
		{
			int id = Store3(trans, obj, depth, checkJust);
			if (StackIsSmall())
			{
				CheckStillToSet();
			}
			return id;
		}

		public virtual void CheckStillToSet()
		{
			List4 postponedStillToSet = null;
			while (_stillToSet != null)
			{
				IEnumerator i = new Iterator4Impl(_stillToSet);
				_stillToSet = null;
				while (i.MoveNext())
				{
					PartialObjectContainer.PendingSet item = (PartialObjectContainer.PendingSet)i.Current;
					ObjectReference @ref = item.@ref;
					Transaction trans = item.transaction;
					if (!@ref.ContinueSet(trans, item.depth))
					{
						postponedStillToSet = new List4(postponedStillToSet, item);
					}
				}
			}
			_stillToSet = postponedStillToSet;
		}

		internal virtual void NotStorable(IReflectClass claxx, object obj)
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
				throw new ObjectNotStorableException(claxx);
			}
			throw new ObjectNotStorableException(obj.ToString());
		}

		public int Store3(Transaction trans, object obj, int updateDepth, bool checkJustSet
			)
		{
			if (obj == null || (obj is ITransientClass))
			{
				return 0;
			}
			if (obj is IDb4oTypeImpl)
			{
				((IDb4oTypeImpl)obj).StoredTo(trans);
			}
			ObjectAnalyzer analyzer = new ObjectAnalyzer(this, obj);
			analyzer.Analyze(trans);
			if (analyzer.NotStorable())
			{
				return 0;
			}
			ObjectReference @ref = analyzer.ObjectReference();
			if (@ref == null)
			{
				ClassMetadata classMetadata = analyzer.ClassMetadata();
				if (!ObjectCanNew(trans, classMetadata, obj))
				{
					return 0;
				}
				@ref = new ObjectReference();
				@ref.Store(trans, classMetadata, obj);
				trans.AddNewReference(@ref);
				if (obj is IDb4oTypeImpl)
				{
					((IDb4oTypeImpl)obj).SetTrans(trans);
				}
				if (ConfigImpl().MessageLevel() > Const4.State)
				{
					Message(string.Empty + @ref.GetID() + " new " + @ref.ClassMetadata().GetName());
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
			ProcessPendingClassUpdates();
			return @ref.GetID();
		}

		private bool UpdateDepthSufficient(int updateDepth)
		{
			return (updateDepth == Const4.Unspecified) || (updateDepth > 0);
		}

		private bool ObjectCanNew(Transaction transaction, ClassMetadata yc, object obj)
		{
			return Callbacks().ObjectCanNew(transaction, obj) && yc.DispatchEvent(_this, obj, 
				EventDispatcher.CanNew);
		}

		public abstract void SetDirtyInSystemTransaction(PersistentBase a_object);

		public abstract bool SetSemaphore(string name, int timeout);

		internal virtual void StringIO(LatinStringIO io)
		{
			_handlers.StringIO(io);
		}

		internal bool ShowInternalClasses()
		{
			return IsServer() || _showInternalClasses > 0;
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
					_showInternalClasses++;
				}
				else
				{
					_showInternalClasses--;
				}
				if (_showInternalClasses < 0)
				{
					_showInternalClasses = 0;
				}
			}
		}

		private bool StackIsSmall()
		{
			return _stackDepth < Const4.MaxStackDepth;
		}

		internal virtual bool StateMessages()
		{
			return true;
		}

		internal List4 StillTo1(Transaction trans, List4 still, object obj, IActivationDepth
			 depth, bool forceUnknownDeactivate)
		{
			if (obj == null || !depth.RequiresActivation())
			{
				return still;
			}
			ObjectReference @ref = trans.ReferenceForObject(obj);
			if (@ref != null)
			{
				if (HandledInCurrentTopLevelCall(@ref))
				{
					return still;
				}
				FlagAsHandled(@ref);
				return new List4(still, new PartialObjectContainer.PendingActivation(@ref, depth)
					);
			}
			IReflectClass clazz = Reflector().ForObject(obj);
			if (clazz.IsArray())
			{
				if (!clazz.GetComponentType().IsPrimitive())
				{
					IEnumerator arr = ArrayHandler.Iterator(clazz, obj);
					while (arr.MoveNext())
					{
						object current = arr.Current;
						if (current == null)
						{
							continue;
						}
						ClassMetadata classMetadata = ClassMetadataForObject(current);
						still = StillTo1(trans, still, current, depth.Descend(classMetadata), forceUnknownDeactivate
							);
					}
				}
			}
			else
			{
				if (obj is Entry)
				{
					still = StillTo1(trans, still, ((Entry)obj).key, depth, false);
					still = StillTo1(trans, still, ((Entry)obj).value, depth, false);
				}
				else
				{
					if (forceUnknownDeactivate)
					{
						ClassMetadata yc = ClassMetadataForObject(obj);
						if (yc != null)
						{
							yc.Deactivate(trans, obj, depth);
						}
					}
				}
			}
			return still;
		}

		public void StillToActivate(Transaction trans, object a_object, IActivationDepth 
			a_depth)
		{
			_stillToActivate = StillTo1(trans, _stillToActivate, a_object, a_depth, false);
		}

		public void StillToDeactivate(Transaction trans, object a_object, IActivationDepth
			 a_depth, bool a_forceUnknownDeactivate)
		{
			_stillToDeactivate = StillTo1(trans, _stillToDeactivate, a_object, a_depth, a_forceUnknownDeactivate
				);
		}

		internal class PendingSet
		{
			public readonly Transaction transaction;

			public readonly ObjectReference @ref;

			public readonly int depth;

			public PendingSet(Transaction transaction_, ObjectReference ref_, int depth_)
			{
				this.transaction = transaction_;
				this.@ref = ref_;
				this.depth = depth_;
			}
		}

		internal virtual void StillToSet(Transaction transaction, ObjectReference @ref, int
			 updateDepth)
		{
			if (StackIsSmall())
			{
				if (@ref.ContinueSet(transaction, updateDepth))
				{
					return;
				}
			}
			_stillToSet = new List4(_stillToSet, new PartialObjectContainer.PendingSet(transaction
				, @ref, updateDepth));
		}

		protected void StopSession()
		{
			if (HasShutDownHook())
			{
				Platform4.RemoveShutDownHook(this);
			}
			_classCollection = null;
			if (_references != null)
			{
				_references.StopTimer();
			}
			_systemTransaction = null;
			_transaction = null;
		}

		public IStoredClass StoredClass(Transaction trans, object clazz)
		{
			lock (_lock)
			{
				trans = CheckTransaction(trans);
				IReflectClass claxx = ReflectorUtils.ReflectClassFor(Reflector(), clazz);
				if (claxx == null)
				{
					return null;
				}
				ClassMetadata classMetadata = ClassMetadataForReflectClass(claxx);
				if (classMetadata == null)
				{
					return null;
				}
				return new StoredClassImpl(trans, classMetadata);
			}
		}

		public virtual IStoredClass[] StoredClasses(Transaction trans)
		{
			lock (_lock)
			{
				trans = CheckTransaction(trans);
				IStoredClass[] classMetadata = _classCollection.StoredClasses();
				IStoredClass[] storedClasses = new IStoredClass[classMetadata.Length];
				for (int i = 0; i < classMetadata.Length; i++)
				{
					storedClasses[i] = new StoredClassImpl(trans, (ClassMetadata)classMetadata[i]);
				}
				return storedClasses;
			}
		}

		public virtual LatinStringIO StringIO()
		{
			return _handlers.StringIO();
		}

		public abstract ISystemInfo SystemInfo();

		private void BeginTopLevelCall()
		{
			if (DTrace.enabled)
			{
				DTrace.BeginTopLevelCall.Log();
			}
			GenerateCallIDOnTopLevel();
			if (_stackDepth == 0)
			{
				_topLevelCallCompleted = false;
			}
			_stackDepth++;
		}

		public void BeginTopLevelSet()
		{
			BeginTopLevelCall();
		}

		private void CompleteTopLevelCall()
		{
			if (_stackDepth == 1)
			{
				_topLevelCallCompleted = true;
			}
		}

		/// <exception cref="Db4oException"></exception>
		private void CompleteTopLevelCall(Db4oException e)
		{
			CompleteTopLevelCall();
			throw e;
		}

		public void CompleteTopLevelSet()
		{
			CompleteTopLevelCall();
		}

		public void CompleteTopLevelSet(Db4oException e)
		{
			CompleteTopLevelCall();
			throw e;
		}

		private void EndTopLevelCall()
		{
			if (DTrace.enabled)
			{
				DTrace.EndTopLevelCall.Log();
			}
			_stackDepth--;
			GenerateCallIDOnTopLevel();
			if (_stackDepth == 0)
			{
				if (!_topLevelCallCompleted)
				{
					ShutdownObjectContainer();
				}
			}
		}

		public void EndTopLevelSet(Transaction trans)
		{
			EndTopLevelCall();
			if (_stackDepth == 0 && _topLevelCallCompleted)
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

		public virtual long Version()
		{
			lock (_lock)
			{
				return CurrentVersion();
			}
		}

		public abstract void Shutdown();

		public abstract void WriteDirty();

		public abstract void WriteNew(Transaction trans, Pointer4 pointer, ClassMetadata 
			classMetadata, BufferImpl buffer);

		public abstract void WriteTransactionPointer(int address);

		public abstract void WriteUpdate(Transaction trans, Pointer4 pointer, ClassMetadata
			 classMetadata, BufferImpl buffer);

		private static ExternalObjectContainer Cast(PartialObjectContainer obj)
		{
			return (ExternalObjectContainer)obj;
		}

		public virtual ICallbacks Callbacks()
		{
			return _callbacks;
		}

		public virtual void Callbacks(ICallbacks cb)
		{
			if (cb == null)
			{
				throw new ArgumentException();
			}
			_callbacks = cb;
		}

		public virtual Config4Impl ConfigImpl()
		{
			return _config;
		}

		public virtual UUIDFieldMetadata UUIDIndex()
		{
			return _handlers.Indexes()._uUID;
		}

		public virtual VersionFieldMetadata VersionIndex()
		{
			return _handlers.Indexes()._version;
		}

		public virtual ClassMetadataRepository ClassCollection()
		{
			return _classCollection;
		}

		public virtual ClassInfoHelper GetClassMetaHelper()
		{
			return _classMetaHelper;
		}

		public abstract long[] GetIDsForClass(Transaction trans, ClassMetadata clazz);

		public abstract IQueryResult ClassOnlyQuery(Transaction trans, ClassMetadata clazz
			);

		public abstract IQueryResult ExecuteQuery(QQuery query);

		public virtual void ReplicationCallState(int state)
		{
			_replicationCallState = state;
		}

		public abstract void OnCommittedListener();

		public virtual ReferenceSystemRegistry ReferenceSystemRegistry()
		{
			return _referenceSystemRegistry;
		}

		public virtual ObjectContainerBase Container()
		{
			return _this;
		}

		public virtual void DeleteByID(Transaction transaction, int id, int cascadeDeleteDepth
			)
		{
			if (id <= 0)
			{
				return;
			}
			if (cascadeDeleteDepth <= 0)
			{
				return;
			}
			object obj = GetByID2(transaction, id);
			if (obj == null)
			{
				return;
			}
			cascadeDeleteDepth--;
			IReflectClass claxx = Reflector().ForObject(obj);
			if (claxx.IsCollection())
			{
				cascadeDeleteDepth += Reflector().CollectionUpdateDepth(claxx) - 1;
			}
			ObjectReference @ref = transaction.ReferenceForId(id);
			if (@ref == null)
			{
				return;
			}
			Delete2(transaction, @ref, obj, cascadeDeleteDepth, false);
		}
	}
}
