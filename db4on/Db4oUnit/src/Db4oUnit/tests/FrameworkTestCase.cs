namespace Db4oUnit.tests
{
	public class FrameworkTestCase : Db4oUnit.TestCase
	{
		public static readonly System.Exception EXCEPTION = new System.Exception();

		public virtual void TestRunsGreen()
		{
			Db4oUnit.TestResult result = new Db4oUnit.TestResult();
			new Db4oUnit.tests.RunsGreen().Run(result);
			Db4oUnit.Assert.IsTrue(result.Failures().Size() == 0, "not green");
		}

		public virtual void TestRunsRed()
		{
			Db4oUnit.TestResult result = new Db4oUnit.TestResult();
			new Db4oUnit.tests.RunsRed(EXCEPTION).Run(result);
			Db4oUnit.Assert.IsTrue(result.Failures().Size() == 1, "not red");
		}

		public virtual void TestTestSuite()
		{
			RunTestAndExpect(new Db4oUnit.TestSuite(new Db4oUnit.Test[] { new Db4oUnit.tests.RunsGreen
				() }), 0);
			RunTestAndExpect(new Db4oUnit.TestSuite(new Db4oUnit.Test[] { new Db4oUnit.tests.RunsRed
				(EXCEPTION) }), 1);
			RunTestAndExpect(new Db4oUnit.TestSuite(new Db4oUnit.Test[] { new Db4oUnit.tests.RunsGreen
				(), new Db4oUnit.tests.RunsRed(EXCEPTION) }), 1);
			RunTestAndExpect(new Db4oUnit.TestSuite(new Db4oUnit.Test[] { new Db4oUnit.tests.RunsRed
				(EXCEPTION), new Db4oUnit.tests.RunsRed(EXCEPTION) }), 2);
			RunTestAndExpect(new Db4oUnit.TestSuite(new Db4oUnit.Test[] { new Db4oUnit.tests.RunsRed
				(EXCEPTION), new Db4oUnit.tests.RunsGreen() }), 1);
			RunTestAndExpect(new Db4oUnit.TestSuite(new Db4oUnit.Test[] { new Db4oUnit.tests.RunsGreen
				(), new Db4oUnit.tests.RunsGreen() }), 0);
		}

		public static void RunTestAndExpect(Db4oUnit.Test test, int expFailures)
		{
			RunTestAndExpect(test, expFailures, true);
		}

		public static void RunTestAndExpect(Db4oUnit.Test test, int expFailures, bool checkException
			)
		{
			Db4oUnit.TestResult result = new Db4oUnit.TestResult();
			test.Run(result);
			if (expFailures != result.Failures().Size())
			{
				Db4oUnit.Assert.Fail(result.Failures().ToString());
			}
			if (checkException)
			{
				for (System.Collections.IEnumerator iter = result.Failures().Iterator(); iter.MoveNext
					(); )
				{
					Db4oUnit.TestFailure failure = (Db4oUnit.TestFailure)iter.Current;
					Db4oUnit.Assert.IsTrue(EXCEPTION.Equals(failure.GetFailure()));
				}
			}
		}
	}
}
