
namespace com.db4o
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
	public class YapObjectCarrier : com.db4o.YapMemoryFile
	{
		internal YapObjectCarrier(com.db4o.YapStream a_callingStream, com.db4o.ext.MemoryFile
			 memoryFile) : base(a_callingStream, memoryFile)
		{
		}

		internal override void initialize0b()
		{
		}

		internal override void initialize1()
		{
			i_handlers = i_parent.i_handlers;
			i_classCollection = i_parent.i_classCollection;
			i_config = i_parent.i_config;
			i_references = new com.db4o.YapReferences(this);
			initialize2();
		}

		internal override void initialize2b()
		{
		}

		internal override void initializeEssentialClasses()
		{
		}

		internal override void initialize4NObjectCarrier()
		{
		}

		internal override void initNewClassCollection()
		{
		}

		internal override bool canUpdate()
		{
			return false;
		}

		internal override void configureNewFile()
		{
			i_writeAt = HEADER_LENGTH;
		}

		public override bool close()
		{
			lock (i_lock)
			{
				bool ret = close1();
				if (ret)
				{
					i_config = null;
				}
				return ret;
			}
		}

		internal override void createTransaction()
		{
			i_trans = new com.db4o.TransactionObjectCarrier(this, null);
			i_systemTrans = i_trans;
		}

		internal override long currentVersion()
		{
			return 0;
		}

		public override bool dispatchsEvents()
		{
			return false;
		}

		~YapObjectCarrier()
		{
		}

		internal sealed override void free(int a_address, int a_length)
		{
		}

		internal override int getSlot(int a_length)
		{
			int address = i_writeAt;
			i_writeAt += a_length;
			return address;
		}

		public override com.db4o.ext.Db4oDatabase identity()
		{
			return i_parent.identity();
		}

		internal override bool maintainsIndices()
		{
			return false;
		}

		internal override void message(string msg)
		{
		}

		internal override bool needsLockFileThread()
		{
			return false;
		}

		internal override void raiseVersion(long a_minimumVersion)
		{
		}

		internal override void readThis()
		{
		}

		internal override bool stateMessages()
		{
			return false;
		}

		internal override void write(bool shuttingDown)
		{
			checkNeededUpdates();
			writeDirty();
			getTransaction().commit();
		}

		internal sealed override void writeHeader(bool shuttingDown)
		{
		}

		internal override void writeBootRecord()
		{
		}
	}
}
