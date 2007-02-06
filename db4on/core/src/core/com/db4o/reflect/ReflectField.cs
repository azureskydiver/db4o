namespace com.db4o.reflect
{
	/// <summary>representation for java.lang.reflect.Field.</summary>
	/// <remarks>
	/// representation for java.lang.reflect.Field.
	/// <br /><br />See the respective documentation in the JDK API.
	/// </remarks>
	/// <seealso cref="com.db4o.reflect.Reflector">com.db4o.reflect.Reflector</seealso>
	public interface ReflectField
	{
		object Get(object onObject);

		string GetName();

		com.db4o.reflect.ReflectClass GetFieldType();

		bool IsPublic();

		bool IsStatic();

		bool IsTransient();

		void Set(object onObject, object value);

		void SetAccessible();

		com.db4o.reflect.ReflectClass IndexType();

		object IndexEntry(object orig);
	}
}
