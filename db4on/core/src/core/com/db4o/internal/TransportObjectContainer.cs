namespace com.db4o.@internal
{
	/// <summary>
	/// no reading
	/// no writing
	/// no updates
	/// no weak references
	/// navigation by ID only both sides need synchronised ClassCollections and
	/// MetaInformationCaches
	/// </summary>
	/// <exclude></exclude>
	public class TransportObjectContainer : com.db4o.@internal.InMemoryObjectContainer
	{
		internal TransportObjectContainer(com.db4o.config.Configuration config, com.db4o.@internal.ObjectContainerBase
			 a_callingStream, com.db4o.ext.MemoryFile memoryFile) : base(config, a_callingStream
			, memoryFile)
		{
		}

		protected override void Initialize1(com.db4o.config.Configuration config)
		{
			i_handlers = i_parent.i_handlers;
			_classCollection = i_parent.ClassCollection();
			i_config = i_parent.ConfigImpl();
			i_references = new com.db4o.@internal.WeakReferenceCollector(this);
			Initialize2();
		}

		internal override void Initialize2NObjectCarrier()
		{
		}

		internal override void InitializeEssentialClasses()
		{
		}

		internal override void Initialize4NObjectCarrier()
		{
		}

		internal override void InitNewClassCollection()
		{
		}

		internal override bool CanUpdate()
		{
			return false;
		}

		internal override void ConfigureNewFile()
		{
		}

		public override int ConverterVersion()
		{
			return com.db4o.@internal.convert.Converter.VERSION;
		}

		public override bool Close()
		{
			lock (i_lock)
			{
				bool ret = Close1();
				if (ret)
				{
					i_config = null;
				}
				return ret;
			}
		}

		public sealed override com.db4o.@internal.Transaction NewTransaction(com.db4o.@internal.Transaction
			 parentTransaction)
		{
			if (null != parentTransaction)
			{
				return parentTransaction;
			}
			return new com.db4o.@internal.TransactionObjectCarrier(this, null);
		}

		public override long CurrentVersion()
		{
			return 0;
		}

		public override com.db4o.types.Db4oType Db4oTypeStored(com.db4o.@internal.Transaction
			 a_trans, object a_object)
		{
			return null;
		}

		public override bool DispatchsEvents()
		{
			return false;
		}

		~TransportObjectContainer()
		{
		}

		public sealed override void Free(int a_address, int a_length)
		{
		}

		public override int GetSlot(int length)
		{
			return AppendBlocks(length);
		}

		public override com.db4o.ext.Db4oDatabase Identity()
		{
			return i_parent.Identity();
		}

		public override bool MaintainsIndices()
		{
			return false;
		}

		internal override void Message(string msg)
		{
		}

		public override void RaiseVersion(long a_minimumVersion)
		{
		}

		internal override void ReadThis()
		{
		}

		internal override bool StateMessages()
		{
			return false;
		}

		public override void Write(bool shuttingDown)
		{
			CheckNeededUpdates();
			WriteDirty();
			GetTransaction().Commit();
		}

		internal sealed override void WriteHeader(bool shuttingDown)
		{
		}

		protected override void WriteVariableHeader()
		{
		}
	}
}
