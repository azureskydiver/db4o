
using com.db4o;

namespace com.db4o.test.nativequeries.cats
{
	public abstract class SodaCatPredicate : com.db4o.query.Predicate
	{
		private int _count;

		public virtual void SodaQuery(ObjectContainer oc)
		{
			com.db4o.query.Query q = oc.Query();
			q.Constrain(typeof(Cat));
			Constrain(q);
			q.Execute();
		}

		public abstract void Constrain(com.db4o.query.Query q);

#if NET_2_0
        public abstract void DelegateNQ(ObjectContainer oc);
#endif

		public virtual void SetCount(int count)
		{
			_count = count;
		}

		public virtual int Lower()
		{
			return _count / 2 - 1;
		}

		public virtual int Upper()
		{
			return _count / 2 + 1;
		}

	}
}
