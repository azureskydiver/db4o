/* Copyright (C) 2004 - 2008  Versant Inc.  http://www.db4o.com */

using System;
using System.Collections;
using Db4objects.Db4o;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Ext;
using Db4objects.Db4o.Foundation;
using Db4objects.Db4o.IO;
using Db4objects.Db4o.Internal;
using Db4objects.Db4o.Internal.Activation;
using Db4objects.Db4o.Internal.Callbacks;
using Db4objects.Db4o.Internal.Encoding;
using Db4objects.Db4o.Internal.Handlers.Array;
using Db4objects.Db4o.Internal.Marshall;
using Db4objects.Db4o.Internal.Query;
using Db4objects.Db4o.Internal.Query.Processor;
using Db4objects.Db4o.Internal.Query.Result;
using Db4objects.Db4o.Internal.Replication;
using Db4objects.Db4o.Internal.Slots;
using Db4objects.Db4o.Query;
using Db4objects.Db4o.Reflect;
using Db4objects.Db4o.Reflect.Core;
using Db4objects.Db4o.Reflect.Generic;
using Db4objects.Db4o.Replication;
using Db4objects.Db4o.Typehandlers;
using Db4objects.Db4o.Types;
using Sharpen;
using Sharpen.Lang;

namespace Db4objects.Db4o.Internal
{
	/// <exclude></exclude>
	public abstract partial class ObjectContainerBase : System.IDisposable, ITransientClass
		, IInternal4, IObjectContainerSpec, IInternalObjectContainer
	{
		protected ClassMetadataRepository _classCollection;

		protected Config4Impl _config;

		private int _stackDepth;

		private readonly Db4objects.Db4o.Internal.ReferenceSystemRegistry _referenceSystemRegistry
			 = new Db4objects.Db4o.Internal.ReferenceSystemRegistry();

		private Tree _justPeeked;

		protected object _lock;

		private List4 _pendingClassUpdates;

		internal int _showInternalClasses = 0;

		private List4 _stillToActivate;

		private List4 _stillToDeactivate;

		private List4 _stillToSet;

		private Db4objects.Db4o.Internal.Transaction _systemTransaction;

		protected Db4objects.Db4o.Internal.Transaction _transaction;

		public HandlerRegistry _handlers;

		internal int _replicationCallState;

		internal WeakReferenceCollector _references;

		private NativeQueryHandler _nativeQueryHandler;

		private ICallbacks _callbacks = new NullCallbacks();

		protected readonly PersistentTimeStampIdGenerator _timeStampIdGenerator = new PersistentTimeStampIdGenerator
			();

		private int _topLevelCallId = 1;

		private IntIdGenerator _topLevelCallIdGenerator = new IntIdGenerator();

		private bool _topLevelCallCompleted;

		private readonly IEnvironment _environment = Environments.NewConventionBasedEnvironment
			();

		protected ObjectContainerBase(IConfiguration config)
		{
			// Collection of all classes
			// if (_classCollection == null) the engine is down.
			// the Configuration context for this ObjectContainer
			// Counts the number of toplevel calls into YapStream
			// currently used to resolve self-linking concurrency problems
			// in cylic links, stores only YapClass objects
			// a value greater than 0 indicates class implementing the
			// "Internal" interface are visible in queries and can
			// be used.
			// used for ClassMetadata and ClassMetadataRepository
			// may be parent or equal to i_trans
			// used for Objects
			// all the per-YapStream references that we don't
			// want created in YapobjectCarrier
			// One of three constants in ReplicationHandler: NONE, OLD, NEW
			// Detailed replication variables are stored in i_handlers.
			// Call state has to be maintained here, so YapObjectCarrier (who shares i_handlers) does
			// not accidentally think it operates in a replication call. 
			// weak reference management
			_lock = new object();
			_config = (Config4Impl)config;
		}

		/// <exception cref="Db4objects.Db4o.Ext.OldFormatException"></exception>
		protected void Open()
		{
			WithEnvironment(new _IRunnable_104(this));
		}

		private sealed class _IRunnable_104 : IRunnable
		{
			public _IRunnable_104(ObjectContainerBase _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Run()
			{
				bool ok = false;
				lock (this._enclosing._lock)
				{
					try
					{
						this._enclosing.InitializeTransactions();
						this._enclosing.Initialize1(this._enclosing._config);
						this._enclosing.OpenImpl();
						this._enclosing.InitializePostOpen();
						ok = true;
					}
					finally
					{
						//				} catch (Exception e) {
						//					e.printStackTrace();
						//					throw new Db4oException(e);
						if (!ok)
						{
							this._enclosing.ShutdownObjectContainer();
						}
					}
				}
			}

			private readonly ObjectContainerBase _enclosing;
		}

		protected virtual void WithEnvironment(IRunnable runnable)
		{
			Environments.RunWith(_environment, runnable);
		}

		/// <exception cref="Db4objects.Db4o.Ext.Db4oIOException"></exception>
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

		public void Deactivate(Db4objects.Db4o.Internal.Transaction trans, object obj)
		{
			lock (_lock)
			{
				Deactivate(CheckTransaction(trans), obj, 1);
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
					StillToActivate(ActivationContextFor(trans, obj, depth));
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
				// TODO: Optimize!  A lightweight int array would be faster.
				IEnumerator i = new Iterator4Impl(_stillToActivate);
				_stillToActivate = null;
				while (i.MoveNext())
				{
					ObjectContainerBase.PendingActivation item = (ObjectContainerBase.PendingActivation
						)i.Current;
					ObjectReference @ref = item.@ref;
					object obj = @ref.GetObject();
					if (obj == null)
					{
						ta.RemoveReference(@ref);
					}
					else
					{
						@ref.ActivateInternal(ActivationContextFor(ta, obj, item.depth));
					}
				}
			}
		}

		/// <exception cref="Db4objects.Db4o.Ext.DatabaseClosedException"></exception>
		/// <exception cref="Db4objects.Db4o.Ext.Db4oIOException"></exception>
		public virtual void Backup(string path)
		{
			Backup(ConfigImpl().Storage, path);
		}

		public virtual ActivationContext4 ActivationContextFor(Transaction ta, object obj
			, IActivationDepth depth)
		{
			return new ActivationContext4(ta, obj, depth);
		}

		/// <exception cref="System.ArgumentNullException"></exception>
		/// <exception cref="System.ArgumentException"></exception>
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
				ObjectReference @ref = trans.ReferenceForId(intID);
				if (@ref == null)
				{
					throw new ArgumentException("obj");
				}
				if (ReflectorForObject(obj) == @ref.ClassMetadata().ClassReflector())
				{
					ObjectReference newRef = Bind2(trans, @ref, obj);
					newRef.VirtualAttributes(trans, false);
				}
				else
				{
					throw new Db4oException(Db4objects.Db4o.Internal.Messages.Get(57));
				}
			}
		}

		public ObjectReference Bind2(Transaction trans, ObjectReference oldRef, object obj
			)
		{
			int id = oldRef.GetID();
			trans.RemoveReference(oldRef);
			ObjectReference newRef = new ObjectReference(ClassMetadataForObject(obj), id);
			newRef.SetObjectWeak(this, obj);
			newRef.SetStateDirty();
			trans.ReferenceSystem().AddExistingReference(newRef);
			return newRef;
		}

		public virtual ClassMetadata ClassMetadataForObject(object obj)
		{
			return ProduceClassMetadata(ReflectorForObject(obj));
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

		/// <exception cref="Db4objects.Db4o.Ext.DatabaseClosedException"></exception>
		public void CheckClosed()
		{
			if (_classCollection == null)
			{
				throw new DatabaseClosedException();
			}
		}

		/// <exception cref="Db4objects.Db4o.Ext.DatabaseReadOnlyException"></exception>
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
				Callbacks().CloseOnStarted(this);
				if (DTrace.enabled)
				{
					DTrace.CloseCalled.Log(this.ToString());
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
			// this is set to null in close2 and is therefore our check for down.
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
				CloseSystemTransaction();
				StopSession();
				ShutdownDataStorage();
			}
		}

		protected abstract void CloseSystemTransaction();

		protected abstract void ShutdownDataStorage();

		/// <exception cref="Db4objects.Db4o.Ext.DatabaseReadOnlyException"></exception>
		/// <exception cref="Db4objects.Db4o.Ext.DatabaseClosedException"></exception>
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

		protected void CreateStringIO(byte encoding)
		{
			StringIO(BuiltInStringEncoding.StringIoForEncoding(encoding, ConfigImpl().StringEncoding
				()));
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
			return classMeta.Init(superClassMeta);
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

		/// <exception cref="Db4objects.Db4o.Ext.DatabaseClosedException"></exception>
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
					ObjectContainerBase.PendingActivation item = (ObjectContainerBase.PendingActivation
						)i.Current;
					item.@ref.Deactivate(trans, item.depth);
				}
			}
		}

		/// <exception cref="Db4objects.Db4o.Ext.DatabaseReadOnlyException"></exception>
		/// <exception cref="Db4objects.Db4o.Ext.DatabaseClosedException"></exception>
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
			// This check is performed twice, here and in delete3, intentionally.
			if (BreakDeleteForEnum(@ref, userCall))
			{
				return;
			}
			if (obj is Entry)
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
			// The passed reference can be null, when calling from Transaction.
			if (@ref == null || !@ref.BeginProcessing())
			{
				return;
			}
			// This check is performed twice, here and in delete2, intentionally.
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
			// We have to end processing temporarily here, otherwise the can delete callback
			// can't do anything at all with this object.
			@ref.EndProcessing();
			ActivateForDeletionCallback(trans, yc, @ref, obj);
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

		private void ActivateForDeletionCallback(Transaction trans, ClassMetadata classMetadata
			, ObjectReference @ref, object obj)
		{
			if (!@ref.IsActive() && (CaresAboutDeleting(classMetadata) || CaresAboutDeleted(classMetadata
				)))
			{
				// Activate Objects for Callbacks, because in C/S mode Objects are not activated on the Server
				// FIXME: [TA] review activation depth
				int depth = classMetadata.AdjustCollectionDepthToBorders(1);
				Activate(trans, obj, new FixedActivationDepth(depth));
			}
		}

		private bool CaresAboutDeleting(ClassMetadata yc)
		{
			return this._callbacks.CaresAboutDeleting() || yc.HasEventRegistered(SystemTransaction
				(), EventDispatchers.CanDelete);
		}

		private bool CaresAboutDeleted(ClassMetadata yc)
		{
			return this._callbacks.CaresAboutDeleted() || yc.HasEventRegistered(SystemTransaction
				(), EventDispatchers.Delete);
		}

		private bool ObjectCanDelete(Transaction transaction, ClassMetadata yc, object obj
			)
		{
			return Callbacks().ObjectCanDelete(transaction, obj) && yc.DispatchEvent(transaction
				, obj, EventDispatchers.CanDelete);
		}

		private void ObjectOnDelete(Transaction transaction, ClassMetadata yc, object obj
			)
		{
			Callbacks().ObjectOnDelete(transaction, obj);
			yc.DispatchEvent(transaction, obj, EventDispatchers.Delete);
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
				ByRef foundField = new ByRef();
				classMetadata.ForEachField(new _IProcedure4_621(fieldName, foundField));
				FieldMetadata field = (FieldMetadata)foundField.value;
				if (field == null)
				{
					return null;
				}
				object child = @ref.IsActive() ? field.Get(trans, obj) : DescendMarshallingContext
					(trans, @ref).ReadFieldValue(field);
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

		private sealed class _IProcedure4_621 : IProcedure4
		{
			public _IProcedure4_621(string fieldName, ByRef foundField)
			{
				this.fieldName = fieldName;
				this.foundField = foundField;
			}

			public void Apply(object arg)
			{
				FieldMetadata fieldMetadata = (FieldMetadata)arg;
				if (fieldMetadata.CanAddToQuery(fieldName))
				{
					foundField.value = fieldMetadata;
				}
			}

			private readonly string fieldName;

			private readonly ByRef foundField;
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
			// overriden in YapClient
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
			throw new Db4oException(Db4objects.Db4o.Internal.Messages.Get(msgID));
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
			q.Constrain(template).ByExample();
			return ExecuteQuery((QQuery)q);
		}

		public abstract AbstractQueryResult QueryAllObjects(Transaction ta);

		/// <exception cref="Db4objects.Db4o.Ext.DatabaseClosedException"></exception>
		public object TryGetByID(Transaction ta, long id)
		{
			try
			{
				return GetByID(ta, id);
			}
			catch (InvalidSlotException)
			{
			}
			catch (InvalidIDException)
			{
			}
			// can happen return null
			// can happen return null
			return null;
		}

		/// <exception cref="Db4objects.Db4o.Ext.DatabaseClosedException"></exception>
		/// <exception cref="Db4objects.Db4o.Ext.InvalidIDException"></exception>
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
				catch (IndexOutOfRangeException aiobe)
				{
					CompleteTopLevelCall(new InvalidIDException(aiobe));
				}
				finally
				{
					// Never shut down for getById()
					// There may be OutOfMemoryErrors or similar
					// The user may want to catch and continue working.
					_topLevelCallCompleted = true;
					EndTopLevelCall();
				}
				// only to make the compiler happy
				return null;
			}
		}

		public virtual object GetByID2(Transaction ta, int id)
		{
			object obj = ta.ObjectForIdFromCache(id);
			if (obj != null)
			{
				// Take care about handling the returned candidate reference.
				// If you loose the reference, weak reference management might
				// also.
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
				// Take care about handling the returned candidate reference.
				// If you loose the reference, weak reference management might also.
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
			// check class creation side effect and simply retry recursively
			// if it hits:
			if (readObject != @ref.GetObject())
			{
				return GetHardObjectReferenceById(trans, id);
			}
			return new HardObjectReference(@ref, readObject);
		}

		public StatefulBuffer GetWriter(Transaction a_trans, int a_address, int a_length)
		{
			if (Debug4.ExceedsMaximumBlockSize(a_length))
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
			if (null == claxx)
			{
				throw new ArgumentNullException();
			}
			if (HideClassForExternalUse(claxx))
			{
				return null;
			}
			ClassMetadata classMetadata = _handlers.ClassMetadataForClass(claxx);
			if (classMetadata != null)
			{
				return classMetadata;
			}
			return _classCollection.ClassMetadataForReflectClass(claxx);
		}

		// TODO: Some ReflectClass implementations could hold a 
		// reference to ClassMetadata to improve lookup performance here.
		public virtual ClassMetadata ProduceClassMetadata(IReflectClass claxx)
		{
			if (null == claxx)
			{
				throw new ArgumentNullException();
			}
			if (HideClassForExternalUse(claxx))
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
		/// Differentiating getActiveClassMetadata from getYapClass is a tuning
		/// optimization: If we initialize a YapClass, #set3() has to check for
		/// the possibility that class initialization associates the currently
		/// stored object with a previously stored static object, causing the
		/// object to be known afterwards.
		/// </summary>
		/// <remarks>
		/// Differentiating getActiveClassMetadata from getYapClass is a tuning
		/// optimization: If we initialize a YapClass, #set3() has to check for
		/// the possibility that class initialization associates the currently
		/// stored object with a previously stored static object, causing the
		/// object to be known afterwards.
		/// In this call we only return active YapClasses, initialization
		/// is not done on purpose
		/// </remarks>
		internal ClassMetadata GetActiveClassMetadata(IReflectClass claxx)
		{
			if (HideClassForExternalUse(claxx))
			{
				return null;
			}
			return _classCollection.GetActiveClassMetadata(claxx);
		}

		private bool HideClassForExternalUse(IReflectClass claxx)
		{
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
			return ClassMetadataForID(ClassMetadataIdForName(name));
		}

		public virtual ClassMetadata ClassMetadataForID(int id)
		{
			if (DTrace.enabled)
			{
				DTrace.ClassmetadataById.Log(id);
			}
			if (id == 0)
			{
				return null;
			}
			ClassMetadata classMetadata = _handlers.ClassMetadataForId(id);
			if (classMetadata != null)
			{
				return classMetadata;
			}
			return _classCollection.ClassMetadataForId(id);
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
			_handlers = new HandlerRegistry(this, ConfigImpl().Encoding(), ConfigImpl().Reflector
				());
			if (_references != null)
			{
				Gc();
				_references.StopTimer();
			}
			_references = new WeakReferenceCollector(this);
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
			impl.Stream(this);
			impl.Reflector().SetTransaction(SystemTransaction());
			impl.Reflector().Configuration(new ReflectorConfigurationImpl(impl));
			impl.Taint();
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
			ConfigImpl().ApplyConfigurationItems(this);
		}

		internal virtual void InitializeEssentialClasses()
		{
			for (int i = 0; i < Const4.EssentialClasses.Length; i++)
			{
				ProduceClassMetadata(Reflector().ForClass(Const4.EssentialClasses[i]));
			}
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
		/// overridden in ClientObjectContainer
		/// The method allows checking whether will make it easier to refactor than
		/// an "instanceof YapClient" check.
		/// </summary>
		/// <remarks>
		/// overridden in ClientObjectContainer
		/// The method allows checking whether will make it easier to refactor than
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

		public virtual ITypeHandler4 TypeHandlerForClass(IReflectClass claxx)
		{
			if (HideClassForExternalUse(claxx))
			{
				return null;
			}
			ITypeHandler4 typeHandler = _handlers.TypeHandlerForClass(claxx);
			if (typeHandler != null)
			{
				return typeHandler;
			}
			return _classCollection.ProduceClassMetadata(claxx).TypeHandler();
		}

		public virtual ITypeHandler4 TypeHandlerForClassMetadataID(int id)
		{
			if (id < 1)
			{
				return null;
			}
			ClassMetadata classMetadata = ClassMetadataForID(id);
			if (classMetadata == null)
			{
				return null;
			}
			return classMetadata.TypeHandler();
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
			new Message(this, msg);
		}

		public void NeedsUpdate(ClassMetadata classMetadata)
		{
			_pendingClassUpdates = new List4(_pendingClassUpdates, classMetadata);
		}

		public virtual long GenerateTimeStampId()
		{
			return _timeStampIdGenerator.Next();
		}

		public abstract int NewUserObject();

		/// <exception cref="Db4objects.Db4o.Ext.DatabaseClosedException"></exception>
		public object PeekPersisted(Transaction trans, object obj, IActivationDepth depth
			, bool committed)
		{
			// TODO: peekPersisted is not stack overflow safe, if depth is too high. 
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
					_nativeQueryHandler = new NativeQueryHandler(this);
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

		/// <exception cref="Db4objects.Db4o.Ext.Db4oIOException"></exception>
		public abstract void ReadBytes(byte[] a_bytes, int a_address, int a_length);

		/// <exception cref="Db4objects.Db4o.Ext.Db4oIOException"></exception>
		public abstract void ReadBytes(byte[] bytes, int address, int addressOffset, int 
			length);

		/// <exception cref="Db4objects.Db4o.Ext.Db4oIOException"></exception>
		public ByteArrayBuffer DecryptedBufferByAddress(int address, int length)
		{
			ByteArrayBuffer reader = RawBufferByAddress(address, length);
			_handlers.Decrypt(reader);
			return reader;
		}

		public virtual ByteArrayBuffer RawBufferByAddress(int address, int length)
		{
			CheckAddress(address);
			ByteArrayBuffer reader = new ByteArrayBuffer(length);
			ReadBytes(reader._buffer, address, length);
			return reader;
		}

		/// <exception cref="System.ArgumentException"></exception>
		private void CheckAddress(int address)
		{
			if (address <= 0)
			{
				throw new ArgumentException("Invalid address offset: " + address);
			}
		}

		/// <exception cref="Db4objects.Db4o.Ext.Db4oIOException"></exception>
		public StatefulBuffer ReadWriterByAddress(Transaction a_trans, int address, int length
			)
		{
			CheckAddress(address);
			StatefulBuffer reader = GetWriter(a_trans, address, length);
			reader.ReadEncrypt(this, address);
			return reader;
		}

		public abstract StatefulBuffer ReadWriterByID(Transaction a_ta, int a_id);

		public abstract StatefulBuffer ReadWriterByID(Transaction a_ta, int a_id, bool lastCommitted
			);

		public abstract ByteArrayBuffer ReadReaderByID(Transaction a_ta, int a_id);

		public abstract ByteArrayBuffer ReadReaderByID(Transaction a_ta, int a_id, bool lastCommitted
			);

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
				renamedOne = ApplyRenames(config);
			}
			_classCollection.CheckChanges();
			if (renamedOne)
			{
				Reboot();
			}
		}

		protected virtual bool ApplyRenames(Config4Impl config)
		{
			bool renamed = false;
			IEnumerator i = config.Rename().GetEnumerator();
			while (i.MoveNext())
			{
				Rename ren = (Rename)i.Current;
				if (AlreadyApplied(ren))
				{
					continue;
				}
				if (ApplyRename(ren))
				{
					renamed = true;
				}
			}
			return renamed;
		}

		private bool ApplyRename(Rename ren)
		{
			if (ren.IsField())
			{
				return ApplyFieldRename(ren);
			}
			return ApplyClassRename(ren);
		}

		private bool ApplyClassRename(Rename ren)
		{
			ClassMetadata classToRename = _classCollection.GetClassMetadata(ren.rFrom);
			if (classToRename == null)
			{
				return false;
			}
			ClassMetadata existing = _classCollection.GetClassMetadata(ren.rTo);
			if (existing != null)
			{
				LogMsg(9, "class " + ren.rTo);
				return false;
			}
			classToRename.SetName(ren.rTo);
			CommitRenameFor(ren, classToRename);
			return true;
		}

		private bool ApplyFieldRename(Rename ren)
		{
			ClassMetadata parentClass = _classCollection.GetClassMetadata(ren.rClass);
			if (parentClass == null)
			{
				return false;
			}
			if (!parentClass.RenameField(ren.rFrom, ren.rTo))
			{
				return false;
			}
			CommitRenameFor(ren, parentClass);
			return true;
		}

		private void CommitRenameFor(Rename rename, ClassMetadata classMetadata)
		{
			SetDirtyInSystemTransaction(classMetadata);
			LogMsg(8, rename.rFrom + " to " + rename.rTo);
			DeleteInverseRenames(rename);
			// store the rename, so we only do it once
			Store(SystemTransaction(), rename);
		}

		private void DeleteInverseRenames(Rename rename)
		{
			// delete all that rename from the new name
			// to allow future backswitching
			IObjectSet inverseRenames = QueryInverseRenames(rename);
			while (inverseRenames.HasNext())
			{
				Delete(SystemTransaction(), inverseRenames.Next());
			}
		}

		private IObjectSet QueryInverseRenames(Rename ren)
		{
			return QueryByExample(SystemTransaction(), Renames.ForInverseQBE(ren));
		}

		private bool AlreadyApplied(Rename ren)
		{
			return QueryByExample(SystemTransaction(), ren).Count != 0;
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
			// TODO: implement
			throw new NotSupportedException();
		}

		/// <exception cref="Db4objects.Db4o.Ext.DatabaseClosedException"></exception>
		/// <exception cref="Db4objects.Db4o.Ext.DatabaseReadOnlyException"></exception>
		public void Store(Transaction trans, object obj)
		{
			Store(trans, obj, Const4.Unspecified);
		}

		/// <exception cref="Db4objects.Db4o.Ext.DatabaseClosedException"></exception>
		/// <exception cref="Db4objects.Db4o.Ext.DatabaseReadOnlyException"></exception>
		public int Store(Transaction trans, object obj, int depth)
		{
			lock (_lock)
			{
				return StoreInternal(trans, obj, depth, true);
			}
		}

		/// <exception cref="Db4objects.Db4o.Ext.DatabaseClosedException"></exception>
		/// <exception cref="Db4objects.Db4o.Ext.DatabaseReadOnlyException"></exception>
		public int StoreInternal(Transaction trans, object obj, bool checkJustSet)
		{
			return StoreInternal(trans, obj, Const4.Unspecified, checkJustSet);
		}

		/// <exception cref="Db4objects.Db4o.Ext.DatabaseClosedException"></exception>
		/// <exception cref="Db4objects.Db4o.Ext.DatabaseReadOnlyException"></exception>
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
				CompleteTopLevelCall();
				throw;
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
				try
				{
					Store2(CheckTransaction(), obj, 1, false);
				}
				finally
				{
					_replicationCallState = Const4.None;
					_handlers._replicationReferenceProvider = null;
				}
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
					ObjectContainerBase.PendingSet item = (ObjectContainerBase.PendingSet)i.Current;
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
			return Callbacks().ObjectCanNew(transaction, obj) && yc.DispatchEvent(transaction
				, obj, EventDispatchers.CanNew);
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

		// overridden to do nothing in YapObjectCarrier
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
				return new List4(still, new ObjectContainerBase.PendingActivation(@ref, depth));
			}
			IReflectClass clazz = ReflectorForObject(obj);
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
						// Special handling to deactivate Top-Level unknown objects only.
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

		public void StillToActivate(IActivationContext context)
		{
			// TODO: We don't want the simple classes to search the hc_tree
			// Kick them out here.
			//		if (a_object != null) {
			//			Class clazz = a_object.getClass();
			//			if(! clazz.isPrimitive()){
			if (ProcessedByImmediateActivation(context))
			{
				return;
			}
			_stillToActivate = StillTo1(context.Transaction(), _stillToActivate, context.TargetObject
				(), context.Depth(), false);
		}

		private bool ProcessedByImmediateActivation(IActivationContext context)
		{
			if (!StackIsSmall())
			{
				return false;
			}
			if (!context.Depth().RequiresActivation())
			{
				return true;
			}
			ObjectReference @ref = context.Transaction().ReferenceForObject(context.TargetObject
				());
			if (@ref == null)
			{
				return false;
			}
			if (HandledInCurrentTopLevelCall(@ref))
			{
				return true;
			}
			FlagAsHandled(@ref);
			_stackDepth++;
			try
			{
				@ref.ActivateInternal(context);
			}
			finally
			{
				_stackDepth--;
			}
			return true;
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
			_stillToSet = new List4(_stillToSet, new ObjectContainerBase.PendingSet(transaction
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

		public void CompleteTopLevelCall()
		{
			if (_stackDepth == 1)
			{
				_topLevelCallCompleted = true;
			}
		}

		/// <exception cref="Db4objects.Db4o.Ext.Db4oException"></exception>
		private void CompleteTopLevelCall(Db4oException e)
		{
			CompleteTopLevelCall();
			throw e;
		}

		public void CompleteTopLevelSet()
		{
			CompleteTopLevelCall();
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
			classMetadata, ByteArrayBuffer buffer);

		public abstract void WriteTransactionPointer(int address);

		public abstract void WriteUpdate(Transaction trans, Pointer4 pointer, ClassMetadata
			 classMetadata, ArrayType arrayType, ByteArrayBuffer buffer);

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
			return this;
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
			IReflectClass claxx = ReflectorForObject(obj);
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

		internal virtual IReflectClass ReflectorForObject(object obj)
		{
			return Reflector().ForObject(obj);
		}

		public virtual object SyncExec(IClosure4 block)
		{
			lock (_lock)
			{
				CheckClosed();
				return block.Run();
			}
		}

		public virtual void StoreAll(Transaction transaction, IEnumerator objects)
		{
			while (objects.MoveNext())
			{
				Store(transaction, objects.Current);
			}
		}

		public virtual void WithTransaction(Transaction transaction, IRunnable runnable)
		{
			lock (_lock)
			{
				Transaction old = transaction;
				_transaction = transaction;
				try
				{
					runnable.Run();
				}
				finally
				{
					_transaction = old;
				}
			}
		}

		public abstract void Activate(object arg1, int arg2);

		public abstract void Commit();

		public abstract void Deactivate(object arg1, int arg2);

		public abstract void Delete(object arg1);

		public abstract IExtObjectContainer Ext();

		public abstract IObjectSet Get(object arg1);

		public abstract IQuery Query();

		public abstract IObjectSet Query(Type arg1);

		public abstract IObjectSet Query(Predicate arg1);

		public abstract IObjectSet Query(Predicate arg1, IQueryComparator arg2);

		public abstract IObjectSet QueryByExample(object arg1);

		public abstract void Rollback();

		public abstract void Set(object arg1);

		public abstract void Store(object arg1);

		public abstract void Activate(object arg1);

		public abstract void Backup(IStorage arg1, string arg2);

		public abstract void Bind(object arg1, long arg2);

		public abstract void Deactivate(object arg1);

		public abstract object Descend(object arg1, string[] arg2);

		public abstract object GetByID(long arg1);

		public abstract object GetByUUID(Db4oUUID arg1);

		public abstract long GetID(object arg1);

		public abstract IObjectInfo GetObjectInfo(object arg1);

		public abstract Db4oDatabase Identity();

		public abstract bool IsActive(object arg1);

		public abstract bool IsCached(long arg1);

		public abstract bool IsStored(object arg1);

		public abstract object PeekPersisted(object arg1, int arg2, bool arg3);

		public abstract void Purge(object arg1);

		public abstract void Refresh(object arg1, int arg2);

		public abstract IReplicationProcess ReplicationBegin(IObjectContainer arg1, IReplicationConflictHandler
			 arg2);

		public abstract void Set(object arg1, int arg2);

		public abstract void Store(object arg1, int arg2);

		public abstract IStoredClass StoredClass(object arg1);

		public abstract IStoredClass[] StoredClasses();

		public abstract int InstanceCount(ClassMetadata arg1, Transaction arg2);
	}
}
