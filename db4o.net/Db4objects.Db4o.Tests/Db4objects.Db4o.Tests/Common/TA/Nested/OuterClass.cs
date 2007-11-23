/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

using Db4objects.Db4o.Tests.Common.TA;
using Db4objects.Db4o.Tests.Common.TA.Nested;

namespace Db4objects.Db4o.Tests.Common.TA.Nested
{
	public class OuterClass : ActivatableImpl
	{
		public int _foo;

		public virtual int Foo()
		{
			Activate();
			return _foo;
		}

		public virtual OuterClass.InnerClass CreateInnerObject()
		{
			return new OuterClass.InnerClass(this);
		}

		public class InnerClass : ActivatableImpl
		{
			public virtual OuterClass GetOuterObject()
			{
				this.Activate();
				return this._enclosing;
			}

			public virtual OuterClass GetOuterObjectWithoutActivation()
			{
				return this._enclosing;
			}

			internal InnerClass(OuterClass _enclosing)
			{
				this._enclosing = _enclosing;
			}

			private readonly OuterClass _enclosing;
		}
	}
}
