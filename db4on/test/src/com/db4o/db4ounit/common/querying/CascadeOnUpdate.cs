namespace com.db4o.db4ounit.common.querying
{
	public class CascadeOnUpdate : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public class Atom
		{
			public com.db4o.db4ounit.common.querying.CascadeOnUpdate.Atom child;

			public string name;

			public Atom()
			{
			}

			public Atom(com.db4o.db4ounit.common.querying.CascadeOnUpdate.Atom child)
			{
				this.child = child;
			}

			public Atom(string name)
			{
				this.name = name;
			}

			public Atom(com.db4o.db4ounit.common.querying.CascadeOnUpdate.Atom child, string 
				name) : this(child)
			{
				this.name = name;
			}
		}

		public object child;

		protected override void Configure(com.db4o.config.Configuration conf)
		{
			conf.ObjectClass(this).CascadeOnUpdate(true);
		}

		protected override void Store()
		{
			com.db4o.db4ounit.common.querying.CascadeOnUpdate cou = new com.db4o.db4ounit.common.querying.CascadeOnUpdate
				();
			cou.child = new com.db4o.db4ounit.common.querying.CascadeOnUpdate.Atom(new com.db4o.db4ounit.common.querying.CascadeOnUpdate.Atom
				("storedChild"), "stored");
			Db().Set(cou);
		}

		public virtual void Test()
		{
			Foreach(GetType(), new _AnonymousInnerClass48(this));
			Reopen();
			Foreach(GetType(), new _AnonymousInnerClass59(this));
		}

		private sealed class _AnonymousInnerClass48 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass48(CascadeOnUpdate _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object obj)
			{
				com.db4o.db4ounit.common.querying.CascadeOnUpdate cou = (com.db4o.db4ounit.common.querying.CascadeOnUpdate
					)obj;
				((com.db4o.db4ounit.common.querying.CascadeOnUpdate.Atom)cou.child).name = "updated";
				((com.db4o.db4ounit.common.querying.CascadeOnUpdate.Atom)cou.child).child.name = 
					"updated";
				this._enclosing.Db().Set(cou);
			}

			private readonly CascadeOnUpdate _enclosing;
		}

		private sealed class _AnonymousInnerClass59 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass59(CascadeOnUpdate _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object obj)
			{
				com.db4o.db4ounit.common.querying.CascadeOnUpdate cou = (com.db4o.db4ounit.common.querying.CascadeOnUpdate
					)obj;
				com.db4o.db4ounit.common.querying.CascadeOnUpdate.Atom atom = (com.db4o.db4ounit.common.querying.CascadeOnUpdate.Atom
					)cou.child;
				Db4oUnit.Assert.AreEqual("updated", atom.name);
				Db4oUnit.Assert.AreNotEqual("updated", atom.child.name);
			}

			private readonly CascadeOnUpdate _enclosing;
		}
	}
}
