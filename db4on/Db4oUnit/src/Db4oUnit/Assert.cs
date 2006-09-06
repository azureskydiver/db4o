namespace Db4oUnit
{
	public sealed class Assert
	{
		public static void Expect(System.Type exception, Db4oUnit.CodeBlock block)
		{
			try
			{
				block.Run();
			}
			catch (System.Exception e)
			{
				if (exception.IsInstanceOfType(e))
				{
					return;
				}
				Fail("Expecting '" + exception.FullName + "' but got '" + e.GetType().FullName + 
					"'");
			}
			Fail("Exception '" + exception.FullName + "' expected");
		}

		public static void Fail()
		{
			Fail("FAILURE");
		}

		public static void Fail(string msg)
		{
			throw new Db4oUnit.AssertionException(msg);
		}

		public static void IsTrue(bool condition)
		{
			IsTrue(condition, "FAILURE");
		}

		public static void IsTrue(bool condition, string msg)
		{
			if (condition)
			{
				return;
			}
			Fail(msg);
		}

		public static void IsNull(object reference)
		{
			if (reference != null)
			{
				Fail("FAILURE");
			}
		}

		public static void IsNotNull(object reference)
		{
			if (reference == null)
			{
				Fail("FAILURE");
			}
		}

		public static void AreEqual(bool expected, bool actual)
		{
			if (expected == actual)
			{
				return;
			}
			Fail(FailureMessage(expected, actual));
		}

		public static void AreEqual(int expected, int actual)
		{
			if (expected == actual)
			{
				return;
			}
			Fail(FailureMessage(expected, actual));
		}

		public static void AreEqual(int expected, int actual, string message)
		{
			if (expected == actual)
			{
				return;
			}
			Fail(message);
		}

		public static void AreEqual(long expected, long actual)
		{
			if (expected == actual)
			{
				return;
			}
			Fail(FailureMessage(expected, actual));
		}

		public static void AreEqual(object expected, object actual)
		{
			if (ObjectsAreEqual(expected, actual))
			{
				return;
			}
			Fail(FailureMessage(expected, actual));
		}

		public static void AreSame(object expected, object actual)
		{
			if (expected == actual)
			{
				return;
			}
			Fail(FailureMessage(expected, actual));
		}

		public static void AreNotSame(object expected, object actual)
		{
			if (expected != actual)
			{
				return;
			}
			Fail("Expecting not '" + expected + "'.");
		}

		private static string FailureMessage(object expected, object actual)
		{
			return "Expected '" + expected + "' but was '" + actual + "'";
		}

		private static bool ObjectsAreEqual(object expected, object actual)
		{
			return expected == actual || (expected != null && actual != null && expected.Equals
				(actual));
		}

		public static void IsFalse(bool condition)
		{
			IsTrue(!condition);
		}

		public static void IsInstanceOf(System.Type expectedClass, object actual)
		{
			IsTrue(expectedClass.IsInstanceOfType(actual), FailureMessage(expectedClass, actual
				 == null ? null : actual.GetType()));
		}
	}
}
