namespace com.db4o.query
{
	/// <summary>Extend this class and add your #match() method to run native queries.</summary>
	/// <remarks>
	/// Extend this class and add your #match() method to run native queries.
	/// <br /><br />
	/// A class that extends Predicate is required to implement the method
	/// #match() following the native query conventions:<br />
	/// - The name of the method is "match".<br />
	/// - The method is public.<br />
	/// - The method returns a boolean.<br />
	/// - The method takes one parameter.<br />
	/// - The type (Class) of the parameter specifies the extent.<br />
	/// - For all instances of the extent that are to be included into the
	/// resultset of the query, the method returns true. For all instances
	/// that are not to be included the method returns false. <br /><br />
	/// Here is an example of a #match method that follows these conventions:<br />
	/// <pre><code>
	/// public boolean match(Cat cat){<br />
	/// return cat.name.equals("Occam");<br />
	/// }<br />
	/// </code></pre><br /><br />
	/// Native queries for Java JDK5 and above define a #match method in the
	/// abstract Predicate class to ensure these conventions, using generics.
	/// Without generics the method is not definable in the Predicate class
	/// since alternative method parameter classes would not be possible.
	/// </remarks>
	public abstract class Predicate : j4o.io.Serializable
	{
		public static readonly string PREDICATEMETHOD_NAME = "match";

		internal static readonly j4o.lang.Class OBJECT_CLASS = j4o.lang.Class.getClassForType
			(typeof(object));

		[com.db4o.Transient]
		private j4o.lang.reflect.Method cachedFilterMethod = null;

		internal virtual j4o.lang.reflect.Method getFilterMethod()
		{
			if (cachedFilterMethod != null)
			{
				return cachedFilterMethod;
			}
			j4o.lang.reflect.Method[] methods = j4o.lang.Class.getClassForObject(this).getMethods
				();
			for (int methodIdx = 0; methodIdx < methods.Length; methodIdx++)
			{
				j4o.lang.reflect.Method method = methods[methodIdx];
				if (isFilterMethod(method))
				{
					if (!OBJECT_CLASS.Equals(method.getParameterTypes()[0]))
					{
						cachedFilterMethod = method;
						return method;
					}
				}
			}
			throw new System.ArgumentException("Invalid predicate.");
		}

		private bool isFilterMethod(j4o.lang.reflect.Method method)
		{
			if (method.getParameterTypes().Length != 1)
			{
				return false;
			}
			return j4o.lang.JavaSystem.equalsIgnoreCase(method.getName(), PREDICATEMETHOD_NAME
				);
		}

		public virtual j4o.lang.Class extentType()
		{
			return getFilterMethod().getParameterTypes()[0];
		}

		public virtual bool appliesTo(object candidate)
		{
			try
			{
				j4o.lang.reflect.Method filterMethod = getFilterMethod();
				com.db4o.Platform4.setAccessible(filterMethod);
				object ret = filterMethod.invoke(this, new object[] { candidate });
				return ((bool)ret);
			}
			catch (System.Exception e)
			{
				return false;
			}
		}
	}
}
