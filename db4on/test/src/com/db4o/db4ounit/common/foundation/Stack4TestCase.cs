namespace com.db4o.db4ounit.common.foundation
{
	public class Stack4TestCase : Db4oUnit.TestCase
	{
		public static void Main(string[] args)
		{
			new Db4oUnit.TestRunner(typeof(com.db4o.db4ounit.common.foundation.Stack4TestCase)
				).Run();
		}

		public virtual void TestPushPop()
		{
			com.db4o.foundation.Stack4 stack = new com.db4o.foundation.Stack4();
			AssertEmpty(stack);
			stack.Push("a");
			stack.Push("b");
			stack.Push("c");
			Db4oUnit.Assert.IsFalse(stack.IsEmpty());
			Db4oUnit.Assert.AreEqual("c", stack.Peek());
			Db4oUnit.Assert.AreEqual("c", stack.Peek());
			Db4oUnit.Assert.AreEqual("c", stack.Pop());
			Db4oUnit.Assert.AreEqual("b", stack.Pop());
			Db4oUnit.Assert.AreEqual("a", stack.Peek());
			Db4oUnit.Assert.AreEqual("a", stack.Pop());
			AssertEmpty(stack);
		}

		private void AssertEmpty(com.db4o.foundation.Stack4 stack)
		{
			Db4oUnit.Assert.IsTrue(stack.IsEmpty());
			Db4oUnit.Assert.IsNull(stack.Peek());
			Db4oUnit.Assert.Expect(typeof(System.InvalidOperationException), new _AnonymousInnerClass35
				(this, stack));
		}

		private sealed class _AnonymousInnerClass35 : Db4oUnit.CodeBlock
		{
			public _AnonymousInnerClass35(Stack4TestCase _enclosing, com.db4o.foundation.Stack4
				 stack)
			{
				this._enclosing = _enclosing;
				this.stack = stack;
			}

			public void Run()
			{
				stack.Pop();
			}

			private readonly Stack4TestCase _enclosing;

			private readonly com.db4o.foundation.Stack4 stack;
		}
	}
}
