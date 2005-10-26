using com.db4o.foundation;
using com.db4o.nativequery.expr;
using com.db4o.nativequery.optimization;
using com.db4o.query;

namespace com.db4o.inside.query
{
	public class NativeQueryHandler
	{
		public static readonly string UNOPTIMIZED = "UNOPTIMIZED";

		public static readonly string DYNOPTIMIZED = "DYNOPTIMIZED";

		private ObjectContainer _container;

		private Db4oNQOptimizer _enhancer;

		private List4 _listeners;

		public NativeQueryHandler(com.db4o.ObjectContainer container)
		{
			_container = container;
		}

		public virtual void addListener(com.db4o.inside.query.Db4oQueryExecutionListener 
			listener)
		{
			_listeners = new List4(_listeners, listener);
		}

		public virtual void clearListeners()
		{
			_listeners = null;
		}

		public virtual com.db4o.ObjectSet execute(com.db4o.query.Predicate predicate)
		{
			return configureQuery(predicate).execute();
		}

		private Query configureQuery(com.db4o.query.Predicate predicate)
		{
			Query q = _container.query();
			q.constrain(predicate.extentType());
			
			try
			{
				if (_container.ext().configure().optimizeNativeQueries())
				{
					optimizeQuery(q, predicate);
					notifyListeners(predicate, NativeQueryHandler.DYNOPTIMIZED);
					return q;
				}
			}
			catch (System.Exception e)
			{
				// XXX: need to inform the user of the exception
				// somehow
				//j4o.lang.JavaSystem.printStackTrace(e);
			}
			q.constrain(new com.db4o.inside.query.PredicateEvaluation(predicate));
			notifyListeners(predicate, NativeQueryHandler.UNOPTIMIZED);
			return q;
		}

		void optimizeQuery(Query q, Predicate predicate)
		{
			// TODO: cache predicate expressions here
			Expression expression = QueryExpressionBuilder.FromMethod(predicate.getFilterMethod().MethodInfo);
			new SODAQueryBuilder().optimizeQuery(expression, q, predicate);
		}

		private void notifyListeners(com.db4o.query.Predicate predicate, string msg)
		{
			for (Iterator4 iter = new Iterator4Impl(_listeners
				); iter.hasNext(); )
			{
				((Db4oQueryExecutionListener)iter.next()).notifyQueryExecuted
					(predicate, msg);
			}
		}
	}
}

