namespace com.db4o.db4ounit.common.fieldindex
{
	public abstract class FieldIndexTestCaseBase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public FieldIndexTestCaseBase() : base()
		{
		}

		protected override void Configure()
		{
			Index(typeof(com.db4o.db4ounit.common.fieldindex.FieldIndexItem), "foo");
		}

		protected virtual void Index(System.Type clazz, string fieldName)
		{
			com.db4o.Db4o.Configure().ObjectClass(clazz).ObjectField(fieldName).Indexed(true);
		}

		protected abstract override void Store();

		protected virtual void StoreItems(int[] foos)
		{
			for (int i = 0; i < foos.Length; i++)
			{
				Store(new com.db4o.db4ounit.common.fieldindex.FieldIndexItem(foos[i]));
			}
		}

		protected virtual void Store(object item)
		{
			Db().Set(item);
		}

		protected virtual com.db4o.query.Query CreateQuery(int id)
		{
			com.db4o.query.Query q = CreateItemQuery();
			q.Descend("foo").Constrain(id);
			return q;
		}

		protected virtual com.db4o.query.Query CreateItemQuery()
		{
			return CreateQuery(typeof(com.db4o.db4ounit.common.fieldindex.FieldIndexItem));
		}

		protected virtual com.db4o.query.Query CreateQuery(System.Type clazz)
		{
			return CreateQuery(Trans(), clazz);
		}

		protected virtual com.db4o.query.Query CreateQuery(com.db4o.Transaction trans, System.Type
			 clazz)
		{
			com.db4o.query.Query q = CreateQuery(trans);
			q.Constrain(clazz);
			return q;
		}

		protected virtual com.db4o.query.Query CreateItemQuery(com.db4o.Transaction trans
			)
		{
			com.db4o.query.Query q = CreateQuery(trans);
			q.Constrain(typeof(com.db4o.db4ounit.common.fieldindex.FieldIndexItem));
			return q;
		}

		private com.db4o.query.Query CreateQuery(com.db4o.Transaction trans)
		{
			return Stream().Query(trans);
		}
	}
}
