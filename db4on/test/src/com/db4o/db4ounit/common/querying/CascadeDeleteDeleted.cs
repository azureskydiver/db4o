namespace com.db4o.db4ounit.common.querying
{
	public class CascadeDeleteDeleted : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public class CddMember
		{
			public string name;
		}

		public string name;

		public object untypedMember;

		public com.db4o.db4ounit.common.querying.CascadeDeleteDeleted.CddMember typedMember;

		public CascadeDeleteDeleted()
		{
		}

		public CascadeDeleteDeleted(string name)
		{
			this.name = name;
		}

		protected override void Configure(com.db4o.config.Configuration config)
		{
			config.ObjectClass(this).CascadeOnDelete(true);
		}

		protected override void Store()
		{
			MembersFirst("membersFirst commit");
			MembersFirst("membersFirst");
			TwoRef("twoRef");
			TwoRef("twoRef commit");
			TwoRef("twoRef delete");
			TwoRef("twoRef delete commit");
		}

		private void MembersFirst(string name)
		{
			com.db4o.db4ounit.common.querying.CascadeDeleteDeleted cdd = new com.db4o.db4ounit.common.querying.CascadeDeleteDeleted
				(name);
			cdd.untypedMember = new com.db4o.db4ounit.common.querying.CascadeDeleteDeleted.CddMember
				();
			cdd.typedMember = new com.db4o.db4ounit.common.querying.CascadeDeleteDeleted.CddMember
				();
			Db().Set(cdd);
		}

		private void TwoRef(string name)
		{
			com.db4o.db4ounit.common.querying.CascadeDeleteDeleted cdd = new com.db4o.db4ounit.common.querying.CascadeDeleteDeleted
				(name);
			cdd.untypedMember = new com.db4o.db4ounit.common.querying.CascadeDeleteDeleted.CddMember
				();
			cdd.typedMember = new com.db4o.db4ounit.common.querying.CascadeDeleteDeleted.CddMember
				();
			com.db4o.db4ounit.common.querying.CascadeDeleteDeleted cdd2 = new com.db4o.db4ounit.common.querying.CascadeDeleteDeleted
				(name);
			cdd2.untypedMember = cdd.untypedMember;
			cdd2.typedMember = cdd.typedMember;
			Db().Set(cdd);
			Db().Set(cdd2);
		}

		public virtual void Test()
		{
			TMembersFirst("membersFirst commit");
			TMembersFirst("membersFirst");
			TTwoRef("twoRef");
			TTwoRef("twoRef commit");
			TTwoRef("twoRef delete");
			TTwoRef("twoRef delete commit");
			Db4oUnit.Assert.AreEqual(0, CountOccurences(typeof(com.db4o.db4ounit.common.querying.CascadeDeleteDeleted.CddMember)
				));
		}

		private void TMembersFirst(string name)
		{
			bool commit = name.IndexOf("commit") > 1;
			com.db4o.query.Query q = NewQuery(this.GetType());
			q.Descend("name").Constrain(name);
			com.db4o.ObjectSet objectSet = q.Execute();
			com.db4o.db4ounit.common.querying.CascadeDeleteDeleted cdd = (com.db4o.db4ounit.common.querying.CascadeDeleteDeleted
				)objectSet.Next();
			Db().Delete(cdd.untypedMember);
			Db().Delete(cdd.typedMember);
			if (commit)
			{
				Db().Commit();
			}
			Db().Delete(cdd);
			if (!commit)
			{
				Db().Commit();
			}
		}

		private void TTwoRef(string name)
		{
			bool commit = name.IndexOf("commit") > 1;
			bool delete = name.IndexOf("delete") > 1;
			com.db4o.query.Query q = NewQuery(this.GetType());
			q.Descend("name").Constrain(name);
			com.db4o.ObjectSet objectSet = q.Execute();
			com.db4o.db4ounit.common.querying.CascadeDeleteDeleted cdd = (com.db4o.db4ounit.common.querying.CascadeDeleteDeleted
				)objectSet.Next();
			com.db4o.db4ounit.common.querying.CascadeDeleteDeleted cdd2 = (com.db4o.db4ounit.common.querying.CascadeDeleteDeleted
				)objectSet.Next();
			if (delete)
			{
				Db().Delete(cdd.untypedMember);
				Db().Delete(cdd.typedMember);
			}
			Db().Delete(cdd);
			if (commit)
			{
				Db().Commit();
			}
			Db().Delete(cdd2);
			if (!commit)
			{
				Db().Commit();
			}
		}
	}
}
