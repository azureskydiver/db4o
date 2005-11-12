
using com.db4o;

namespace com.db4o.test.nativequeries.cats
{
	public abstract class SodaCatPredicate : com.db4o.query.Predicate
	{
		private int _count;

		public virtual void sodaQuery(ObjectContainer oc)
		{
			com.db4o.query.Query q = oc.query();
			q.constrain(typeof(Cat));
			constrain(q);
			q.execute();
		}

		public abstract void constrain(com.db4o.query.Query q);

        public abstract void delegateNQ(ObjectContainer oc);

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
