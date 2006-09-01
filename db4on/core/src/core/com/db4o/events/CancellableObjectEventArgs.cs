namespace com.db4o.events
{
	/// <summary>Argument for object related events which can be cancelled.</summary>
	/// <remarks>Argument for object related events which can be cancelled.</remarks>
	/// <seealso cref="com.db4o.events.EventRegistry">com.db4o.events.EventRegistry</seealso>
	/// <seealso cref="com.db4o.events.CancellableEventArgs">com.db4o.events.CancellableEventArgs
	/// 	</seealso>
	public class CancellableObjectEventArgs : com.db4o.events.ObjectEventArgs, com.db4o.events.CancellableEventArgs
	{
		private bool _cancelled;

		public CancellableObjectEventArgs(object obj) : base(obj)
		{
		}

		/// <seealso cref="com.db4o.events.CancellableEventArgs.Cancel">com.db4o.events.CancellableEventArgs.Cancel
		/// 	</seealso>
		public virtual void Cancel()
		{
			_cancelled = true;
		}

		/// <seealso cref="com.db4o.events.CancellableEventArgs.IsCancelled">com.db4o.events.CancellableEventArgs.IsCancelled
		/// 	</seealso>
		public virtual bool IsCancelled
		{
			get
			{
				return _cancelled;
			}
		}
	}
}
