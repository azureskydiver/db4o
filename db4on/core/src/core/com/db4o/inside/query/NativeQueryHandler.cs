/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */
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

#if NET_2_0 || CF_2_0
        public virtual System.Collections.Generic.IList<Extent> execute<Extent>(com.db4o.Predicate<Extent> match)
        {
            com.db4o.query.Query q = _container.query();
            q.constrain(typeof(Extent));
            try
            {
                if (OptimizeNativeQueries())
                {
					// XXX: check GetDelegateList().Length
					// only 1 delegate must be allowed
					// although we could use it as a filter chain
					// (and)
                    optimizeQuery(q, match.Target, match.Method);
                    notifyListeners(match, NativeQueryHandler.DYNOPTIMIZED);

                    return WrapQueryResult<Extent>(q);
                }
            }
            catch (System.Exception e)
            {
                OptimizationError(e);
            }
            q.constrain(new GenericPredicateEvaluation<Extent>(match));
            notifyListeners(match, NativeQueryHandler.UNOPTIMIZED);

            return WrapQueryResult<Extent>(q);
        }

        private static System.Collections.Generic.IList<Extent> WrapQueryResult<Extent>(com.db4o.query.Query q)
        {
            com.db4o.inside.query.QueryResult qr = ((QQuery)q).getQueryResult();
            return new com.db4o.inside.query.GenericObjectSetFacade<Extent>(qr);
        }
#endif

		private Query configureQuery(com.db4o.query.Predicate predicate)
		{
			Query q = _container.query();
			q.constrain(predicate.extentType());
			
			try
			{
                if (OptimizeNativeQueries())
				{
					optimizeQuery(q, predicate, predicate.getFilterMethod().MethodInfo);
					notifyListeners(predicate, NativeQueryHandler.DYNOPTIMIZED);
					return q;
				}
			}
			catch (System.Exception e)
			{
                OptimizationError(e);
			}
			q.constrain(new com.db4o.inside.query.PredicateEvaluation(predicate));
            notifyListeners(predicate, NativeQueryHandler.UNOPTIMIZED);
			return q;
		}

        private static void OptimizationError(System.Exception e)
        {
            // XXX: need to inform the user of the exception
            // somehow
            //j4o.lang.JavaSystem.printStackTrace(e);
        }

        private bool OptimizeNativeQueries()
        {
            return _container.ext().configure().optimizeNativeQueries();
        }

		void optimizeQuery(Query q, object predicate, System.Reflection.MethodInfo filterMethod)
		{
			// TODO: cache predicate expressions here
			Expression expression = QueryExpressionBuilder.FromMethod(filterMethod);
			new SODAQueryBuilder().optimizeQuery(expression, q, predicate);
		}

		private void notifyListeners(object predicate, string msg)
		{
			for (Iterator4 iter = new Iterator4Impl(_listeners
				); iter.hasNext(); )
			{
				((Db4oQueryExecutionListener)iter.next()).notifyQueryExecuted
					(predicate, msg);
			}
		}
	}

#if NET_2_0 || CF_2_0
    class GenericPredicateEvaluation<T> : DelegateEnvelope, com.db4o.query.Evaluation
    {
        public GenericPredicateEvaluation(com.db4o.Predicate<T> predicate)
            : base(predicate)
        {
        }

        public void evaluate(com.db4o.query.Candidate candidate)
        {
            // use starting _ for PascalCase conversion purposes
            com.db4o.Predicate<T> _predicate = (com.db4o.Predicate<T>)GetContent();
            candidate.include(_predicate((T)candidate.getObject()));
        }
    }
#endif
}


