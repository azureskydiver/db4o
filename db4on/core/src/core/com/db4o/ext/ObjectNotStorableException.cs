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
	/// <seealso cref="com.db4o.config.Configuration.exceptionsOnNotStorable">com.db4o.config.Configuration.exceptionsOnNotStorable
	/// 	</seealso>
	public class ObjectNotStorableException : j4o.lang.RuntimeException
	{
		public ObjectNotStorableException(com.db4o.reflect.ReflectClass a_class) : base(com.db4o.Messages
			.get(a_class.isPrimitive() ? 59 : 45, a_class.getName()))
		{
		}
	}
}
