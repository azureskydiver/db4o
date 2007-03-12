namespace com.db4o.events
{
	/// <summary>Arguments for commit time related events.</summary>
	/// <remarks>Arguments for commit time related events.</remarks>
	/// <seealso cref="com.db4o.events.EventRegistry">com.db4o.events.EventRegistry</seealso>
	public class CommitEventArgs : System.EventArgs
	{
		private readonly com.db4o.ext.ObjectInfoCollection _added;

		private readonly com.db4o.ext.ObjectInfoCollection _deleted;

		private readonly com.db4o.ext.ObjectInfoCollection _updated;

		public CommitEventArgs(com.db4o.ext.ObjectInfoCollection added, com.db4o.ext.ObjectInfoCollection
			 deleted, com.db4o.ext.ObjectInfoCollection updated)
		{
			_added = added;
			_deleted = deleted;
			_updated = updated;
		}

		/// <summary>Returns a iteration</summary>
		public virtual com.db4o.ext.ObjectInfoCollection Added
		{
			get
			{
				return _added;
			}
		}

		public virtual com.db4o.ext.ObjectInfoCollection Deleted
		{
			get
			{
				return _deleted;
			}
		}

		public virtual com.db4o.ext.ObjectInfoCollection Updated
		{
			get
			{
				return _updated;
			}
		}
	}
}
