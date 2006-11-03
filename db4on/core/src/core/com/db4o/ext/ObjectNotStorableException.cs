namespace com.db4o.ext
{
	/// <summary>
	/// this Exception is thrown, if objects can not be stored and if
	/// db4o is configured to throw Exceptions on storage failures.
	/// </summary>
	/// <remarks>
	/// this Exception is thrown, if objects can not be stored and if
	/// db4o is configured to throw Exceptions on storage failures.
	/// </remarks>
	/// <seealso cref="com.db4o.config.Configuration.ExceptionsOnNotStorable">com.db4o.config.Configuration.ExceptionsOnNotStorable
	/// 	</seealso>
	[System.Serializable]
	public class ObjectNotStorableException : System.Exception
	{
		public ObjectNotStorableException(com.db4o.reflect.ReflectClass a_class) : base(com.db4o.Messages
			.Get(a_class.IsPrimitive() ? 59 : 45, a_class.GetName()))
		{
		}

		public ObjectNotStorableException(string message) : base(message)
		{
		}
	}
}
