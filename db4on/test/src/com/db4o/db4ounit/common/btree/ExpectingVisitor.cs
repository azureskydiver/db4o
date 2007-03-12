namespace com.db4o.db4ounit.common.btree
{
	public class ExpectingVisitor : com.db4o.foundation.Visitor4
	{
		private const bool DEBUG = false;

		private readonly object[] _expected;

		private readonly bool _obeyOrder;

		private readonly com.db4o.foundation.Collection4 _unexpected = new com.db4o.foundation.Collection4
			();

		private bool _ignoreUnexpected;

		private int _cursor;

		private sealed class _AnonymousInnerClass24 : object
		{
			public _AnonymousInnerClass24()
			{
			}

			public override string ToString()
			{
				return "[FOUND]";
			}
		}

		private static readonly object FOUND = new _AnonymousInnerClass24();

		public ExpectingVisitor(object[] results, bool obeyOrder, bool ignoreUnexpected)
		{
			_expected = new object[results.Length];
			System.Array.Copy(results, 0, _expected, 0, results.Length);
			_obeyOrder = obeyOrder;
			_ignoreUnexpected = ignoreUnexpected;
		}

		public ExpectingVisitor(object[] results) : this(results, false, false)
		{
		}

		public ExpectingVisitor(object singleObject) : this(new object[] { singleObject }
			)
		{
		}

		public ExpectingVisitor() : this(new object[0])
		{
		}

		public virtual void Visit(object obj)
		{
			if (_obeyOrder)
			{
				VisitOrdered(obj);
			}
			else
			{
				VisitUnOrdered(obj);
			}
		}

		private void VisitOrdered(object obj)
		{
			if (_cursor < _expected.Length)
			{
				if (AreEqual(_expected[_cursor], obj))
				{
					Ods("Expected OK: " + obj.ToString());
					_expected[_cursor] = FOUND;
					_cursor++;
					return;
				}
			}
			Unexpected(obj);
		}

		private void Unexpected(object obj)
		{
			if (_ignoreUnexpected)
			{
				return;
			}
			_unexpected.Add(obj);
			Ods("Unexpected: " + obj);
		}

		private void VisitUnOrdered(object obj)
		{
			for (int i = 0; i < _expected.Length; i++)
			{
				object expectedItem = _expected[i];
				if (AreEqual(obj, expectedItem))
				{
					Ods("Expected OK: " + obj);
					_expected[i] = FOUND;
					return;
				}
			}
			Unexpected(obj);
		}

		private bool AreEqual(object obj, object expectedItem)
		{
			return expectedItem == obj || (expectedItem != null && obj != null && expectedItem
				.Equals(obj));
		}

		private static void Ods(string message)
		{
		}

		public virtual void AssertExpectations()
		{
			if (_unexpected.Size() > 0)
			{
				Db4oUnit.Assert.Fail("UNEXPECTED: " + _unexpected.ToString());
			}
			for (int i = 0; i < _expected.Length; i++)
			{
				Db4oUnit.Assert.AreSame(FOUND, _expected[i]);
			}
		}
	}
}
