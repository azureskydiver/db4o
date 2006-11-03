namespace com.db4o.db4ounit.common.soda.classes.untypedhierarchy
{
	/// <summary>
	/// epaul:
	/// Shows a bug.
	/// </summary>
	/// <remarks>
	/// epaul:
	/// Shows a bug.
	/// carlrosenberger:
	/// Fixed!
	/// The error was due to the the behaviour of STCompare.java.
	/// It compared the syntetic fields in inner classes also.
	/// I changed the behaviour to neglect all fields that
	/// contain a "$".
	/// </remarks>
	/// <author><a href="mailto:Paul-Ebermann@gmx.de">Paul Ebermann</a></author>
	/// <version>0.1</version>
	public class STInnerClassesTestCase : com.db4o.db4ounit.common.soda.util.SodaBaseTestCase
	{
		public class Parent
		{
			public object child;

			public Parent(STInnerClassesTestCase _enclosing, object o)
			{
				this._enclosing = _enclosing;
				this.child = o;
			}

			public override string ToString()
			{
				return "Parent[" + this.child + "]";
			}

			public Parent(STInnerClassesTestCase _enclosing)
			{
				this._enclosing = _enclosing;
			}

			private readonly STInnerClassesTestCase _enclosing;
		}

		public class FirstClass
		{
			public object childFirst;

			public FirstClass(STInnerClassesTestCase _enclosing, object o)
			{
				this._enclosing = _enclosing;
				this.childFirst = o;
			}

			public override string ToString()
			{
				return "First[" + this.childFirst + "]";
			}

			public FirstClass(STInnerClassesTestCase _enclosing)
			{
				this._enclosing = _enclosing;
			}

			private readonly STInnerClassesTestCase _enclosing;
		}

		public STInnerClassesTestCase()
		{
		}

		public override object[] CreateData()
		{
			return new object[] { new com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STInnerClassesTestCase.Parent
				(this, new com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STInnerClassesTestCase.FirstClass
				(this, "Example")), new com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STInnerClassesTestCase.Parent
				(this, new com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STInnerClassesTestCase.FirstClass
				(this, "no Example")) };
		}

		/// <summary>Only</summary>
		public virtual void TestNothing()
		{
			com.db4o.query.Query q = NewQuery();
			q.Descend("child");
			com.db4o.db4ounit.common.soda.util.SodaTestUtil.Expect(q, _array);
		}
	}
}
