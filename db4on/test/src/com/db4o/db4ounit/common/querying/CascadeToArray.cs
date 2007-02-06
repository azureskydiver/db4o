namespace com.db4o.db4ounit.common.querying
{
	public class CascadeToArray : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public class Atom
		{
			public com.db4o.db4ounit.common.querying.CascadeToArray.Atom child;

			public string name;

			public Atom()
			{
			}

			public Atom(com.db4o.db4ounit.common.querying.CascadeToArray.Atom child)
			{
				this.child = child;
			}

			public Atom(string name)
			{
				this.name = name;
			}

			public Atom(com.db4o.db4ounit.common.querying.CascadeToArray.Atom child, string name
				) : this(child)
			{
				this.name = name;
			}
		}

		public object[] objects;

		protected override void Configure(com.db4o.config.Configuration conf)
		{
			conf.ObjectClass(this).CascadeOnUpdate(true);
			conf.ObjectClass(this).CascadeOnDelete(true);
		}

		protected override void Store()
		{
			com.db4o.db4ounit.common.querying.CascadeToArray cta = new com.db4o.db4ounit.common.querying.CascadeToArray
				();
			cta.objects = new object[] { new com.db4o.db4ounit.common.querying.CascadeToArray.Atom
				("stored1"), new com.db4o.db4ounit.common.querying.CascadeToArray.Atom(new com.db4o.db4ounit.common.querying.CascadeToArray.Atom
				("storedChild1"), "stored2") };
			Db().Set(cta);
		}

		public virtual void Test()
		{
			Foreach(GetType(), new _AnonymousInnerClass52(this));
			Reopen();
			Foreach(GetType(), new _AnonymousInnerClass69(this));
			Db().Commit();
			Reopen();
			com.db4o.ObjectSet os = NewQuery(GetType()).Execute();
			while (os.HasNext())
			{
				Db().Delete(os.Next());
			}
			Db4oUnit.Assert.AreEqual(1, CountOccurences(typeof(com.db4o.db4ounit.common.querying.CascadeToArray.Atom)
				));
		}

		private sealed class _AnonymousInnerClass52 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass52(CascadeToArray _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object obj)
			{
				com.db4o.db4ounit.common.querying.CascadeToArray cta = (com.db4o.db4ounit.common.querying.CascadeToArray
					)obj;
				for (int i = 0; i < cta.objects.Length; i++)
				{
					com.db4o.db4ounit.common.querying.CascadeToArray.Atom atom = (com.db4o.db4ounit.common.querying.CascadeToArray.Atom
						)cta.objects[i];
					atom.name = "updated";
					if (atom.child != null)
					{
						atom.child.name = "updated";
					}
				}
				this._enclosing.Db().Set(cta);
			}

			private readonly CascadeToArray _enclosing;
		}

		private sealed class _AnonymousInnerClass69 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass69(CascadeToArray _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object obj)
			{
				com.db4o.db4ounit.common.querying.CascadeToArray cta = (com.db4o.db4ounit.common.querying.CascadeToArray
					)obj;
				for (int i = 0; i < cta.objects.Length; i++)
				{
					com.db4o.db4ounit.common.querying.CascadeToArray.Atom atom = (com.db4o.db4ounit.common.querying.CascadeToArray.Atom
						)cta.objects[i];
					Db4oUnit.Assert.AreEqual("updated", atom.name);
					if (atom.child != null)
					{
						Db4oUnit.Assert.AreNotEqual("updated", atom.child.name);
					}
				}
			}

			private readonly CascadeToArray _enclosing;
		}
	}
}
