namespace com.db4o.events
{
	/// <summary>Arguments for object related events.</summary>
	/// <remarks>Arguments for object related events.</remarks>
	/// <seealso cref="com.db4o.events.EventRegistry">com.db4o.events.EventRegistry</seealso>
	public class ObjectEventArgs : System.EventArgs
	{
		private object _obj;

		public ObjectEventArgs(object obj)
		{
			_obj = obj;
		}

		/// <summary>The object that triggered this event.</summary>
		/// <remarks>The object that triggered this event.</remarks>
		public virtual object Object
		{
			get
			{
				return _obj;
			}
		}
	}
}
