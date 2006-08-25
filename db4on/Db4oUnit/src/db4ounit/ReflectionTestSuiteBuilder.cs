namespace Db4oUnit
{
	public class ReflectionTestSuiteBuilder : Db4oUnit.TestSuiteBuilder
	{
		private System.Type[] _classes;

		public ReflectionTestSuiteBuilder(System.Type clazz)
		{
			if (null == clazz)
			{
				throw new System.ArgumentException("clazz");
			}
			_classes = new System.Type[] { clazz };
		}

		public ReflectionTestSuiteBuilder(System.Type[] classes)
		{
			if (null == classes)
			{
				throw new System.ArgumentException("classes");
			}
			_classes = classes;
		}

		public virtual Db4oUnit.TestSuite Build()
		{
			return (1 == _classes.Length) ? FromClass(_classes[0]) : FromClasses(_classes);
		}

		protected internal virtual Db4oUnit.TestSuite FromClasses(System.Type[] classes)
		{
			Db4oUnit.TestSuite[] suites = new Db4oUnit.TestSuite[classes.Length];
			for (int i = 0; i < classes.Length; i++)
			{
				suites[i] = FromClass(classes[i]);
			}
			return new Db4oUnit.TestSuite(suites);
		}

		protected internal virtual Db4oUnit.TestSuite FromClass(System.Type clazz)
		{
			object instance = NewInstance(clazz);
			return FromInstance(instance);
		}

		private Db4oUnit.TestSuite FromInstance(object instance)
		{
			if (instance is Db4oUnit.TestSuiteBuilder)
			{
				return ((Db4oUnit.TestSuiteBuilder)instance).Build();
			}
			if (instance is Db4oUnit.Test)
			{
				return new Db4oUnit.TestSuite(instance.GetType().FullName, new Db4oUnit.Test[] { 
					(Db4oUnit.Test)instance });
			}
			if (!(instance is Db4oUnit.TestCase))
			{
				throw new System.ArgumentException("" + instance.GetType() + " is not marked as "
					 + typeof(Db4oUnit.TestCase));
			}
			System.Collections.ArrayList tests = new System.Collections.ArrayList();
			System.Reflection.MethodInfo[] methods = instance.GetType().GetMethods();
			for (int i = 0; i < methods.Length; i++)
			{
				System.Reflection.MethodInfo method = methods[i];
				if (!IsTestMethod(method))
				{
					continue;
				}
				tests.Add(CreateTest(instance, method));
			}
			return new Db4oUnit.TestSuite(instance.GetType().FullName, ToArray(tests));
		}

		protected internal virtual bool IsTestMethod(System.Reflection.MethodInfo method)
		{
			return Db4oUnit.TestPlatform.IsTestMethod(method);
		}

		private Db4oUnit.Test[] ToArray(System.Collections.ArrayList tests)
		{
			Db4oUnit.Test[] array = new Db4oUnit.Test[tests.Count];
			tests.CopyTo(array);
			return array;
		}

		protected internal virtual object NewInstance(System.Type clazz)
		{
			try
			{
				return System.Activator.CreateInstance(clazz);
			}
			catch (System.Exception e)
			{
				throw new Db4oUnit.TestException(e);
			}
		}

		protected internal virtual Db4oUnit.Test CreateTest(object instance, System.Reflection.MethodInfo
			 method)
		{
			return new Db4oUnit.TestMethod(instance, method);
		}
	}
}
