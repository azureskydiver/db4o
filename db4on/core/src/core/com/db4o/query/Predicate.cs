namespace com.db4o.query
{
	/// <summary>Extend this class and add your #match() method to run native queries.</summary>
	/// <remarks>
	/// Extend this class and add your #match() method to run native queries.
	/// <br /><br /><b>! The functionality of this class is not available before db4o version 5.0.
	/// It is present in 4.x builds for maintenance purposes only !</b><br /><br />
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
	/// return cat.name.equals("Frizz");<br />
	/// }<br />
	/// </code></pre><br /><br />
	/// Native queries for Java JDK5 and above define a #match method in the
	/// abstract Predicate class to ensure these conventions, using generics.
	/// Without generics the method is not definable in the Predicate class
	/// since alternative method parameter classes would not be possible.
	/// </remarks>
	public abstract class Predicate : j4o.io.Serializable
	{
		[com.db4o.Transient]
		private j4o.lang.reflect.Method _matchMethod;

		[com.db4o.Transient]
		private j4o.lang.Class _extent;

		[com.db4o.Transient]
		private bool _failed;

		public Predicate()
		{
			findMatchMethod();
			if (_matchMethod == null)
			{
				com.db4o.inside.Exceptions4.throwRuntimeException(64);
			}
		}

		/// <summary>public for implementation reasons.</summary>
		/// <remarks>public for implementation reasons. Do not call.</remarks>
		public j4o.lang.Class getExtent()
		{
			return _extent;
		}

		private void findMatchMethod()
		{
			j4o.lang.reflect.Method[] methods = j4o.lang.Class.getClassForObject(this).getMethods
				();
			for (int methodIdx = 0; methodIdx < methods.Length; methodIdx++)
			{
				j4o.lang.reflect.Method curMethod = methods[methodIdx];
				string name = curMethod.getName();
				if ((name.Equals("match") || name.Equals("Match")) && curMethod.getReturnType().Equals
					(j4o.lang.Class.getClassForType(typeof(bool))))
				{
					j4o.lang.Class[] paramTypes = curMethod.getParameterTypes();
					if (paramTypes != null && paramTypes.Length == 1)
					{
						_extent = paramTypes[0];
						_matchMethod = curMethod;
						com.db4o.Platform4.setAccessible(curMethod);
						return;
					}
				}
			}
			_failed = true;
		}

		/// <summary>public for implementation reasons.</summary>
		/// <remarks>public for implementation reasons. Do not call.</remarks>
		public bool invoke(object obj)
		{
			if (_failed)
			{
				return false;
			}
			try
			{
				if (_matchMethod == null)
				{
					findMatchMethod();
				}
				if (_matchMethod == null)
				{
					return false;
				}
				object res = _matchMethod.invoke(this, new object[] { obj });
				return (((bool)res));
			}
			catch (System.Exception ex)
			{
				j4o.lang.JavaSystem.printStackTrace(ex);
			}
			return false;
		}
	}
}
