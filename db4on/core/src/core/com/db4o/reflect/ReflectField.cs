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
		object get(object onObject);

		string getName();

		com.db4o.reflect.ReflectClass getType();

		bool isPublic();

		bool isStatic();

		bool isTransient();

		void set(object onObject, object value);

		void setAccessible();
	}
}
