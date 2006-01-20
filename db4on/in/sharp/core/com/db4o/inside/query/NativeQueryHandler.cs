/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */
using com.db4o.foundation;
using com.db4o.nativequery.expr;
using com.db4o.nativequery.optimization;
using com.db4o.query;

namespace com.db4o.inside.query
{
	public class NativeQueryHandler
	{
		private ObjectContainer _container;

		private Db4oNQOptimizer _enhancer;

		public event QueryExecutionHandler QueryExecution;

		public event QueryOptimizationFailureHandler QueryOptimizationFailure;

		public NativeQueryHandler(com.db4o.ObjectContainer container)
		{
			_container = container;
		}

		public virtual com.db4o.ObjectSet execute(com.db4o.query.Predicate predicate)
		{
			return configureQuery(predicate).execute();
		}

#if NET_2_0
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
                    OnQueryExecution(match, QueryExecutionKind.Unoptimized);

                    return WrapQueryResult<Extent>(q);
                }
            }
            catch (System.Exception e)
            {
                OnQueryOptimizationFailure(e);
            }
            q.constrain(new GenericPredicateEvaluation<Extent>(match));
            OnQueryExecution(match, QueryExecutionKind.DynamicallyOptimized);

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
					OnQueryExecution(predicate, QueryExecutionKind.DynamicallyOptimized);
					return q;
				}
			}
			catch (System.Exception e)
			{
                OnQueryOptimizationFailure(e);
			}
			q.constrain(new com.db4o.inside.query.PredicateEvaluation(predicate));
            OnQueryExecution(predicate, QueryExecutionKind.Unoptimized);
			return q;
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

		private void OnQueryExecution(object predicate, QueryExecutionKind kind)
		{
			if (null != QueryExecution)
            {
				QueryExecution(this, new QueryExecutionEventArgs(predicate, kind));
            }
		}

		private void OnQueryOptimizationFailure(System.Exception e)
		{
			if (null != QueryOptimizationFailure)
			{
				QueryOptimizationFailure(this, new QueryOptimizationFailureEventArgs(e));
			}
		}
	}

#if NET_2_0
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


