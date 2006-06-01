using System.Collections;
using System.Reflection;

namespace Db4oAdmin.Tests.Framework
{
	public class TestMethod : ITest
	{
		private TestCase _testCase;
		private MethodInfo _method;

		public TestMethod(TestCase testCase, MethodInfo method)
		{
			_testCase = testCase;
			_method = method;
		}
		
		public string Name
		{
			get { return _method.Name; }
		}
		
		public void Run()
		{
			try
			{
				_testCase.SetUp();
				_method.Invoke(_testCase, null);
			}
			finally
			{
				_testCase.TearDown();
			}
		}
	}
	
	public abstract class TestCase : ITestSuiteBuilder
	{
		public virtual void SetUp()
		{
		}

		public virtual void TearDown()
		{
		}

		public ITest[] Build()
		{
			ArrayList tests = new ArrayList();
			foreach (MethodInfo method in GetType().GetMethods(BindingFlags.Public | BindingFlags.Instance))
			{
				if (!method.Name.StartsWith("Test")) continue;
				tests.Add(new TestMethod(this, method));
			}
			return (ITest[])tests.ToArray(typeof (ITest));
		}
	}
}