namespace com.db4o.test.nativequeries.cats
{
	public abstract class SodaCatPredicate : com.db4o.query.Predicate
	{
		private int _count;

		public virtual void sodaQuery(com.db4o.ObjectContainer oc)
		{
			com.db4o.query.Query q = oc.query();
			q.constrain(j4o.lang.Class.getClassForType(typeof(com.db4o.test.nativequeries.cats.Cat
				)));
			constrain(q);
			q.execute();
		}

		public abstract void constrain(com.db4o.query.Query q);

		public virtual void setCount(int count)
		{
			_count = count;
		}

		public virtual int lower()
		{
			return _count / 2 - 1;
		}

		public virtual int upper()
		{
			return _count / 2 + 1;
		}
	}
}
