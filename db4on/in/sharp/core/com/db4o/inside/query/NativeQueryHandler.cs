/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */
using System;
using com.db4o.foundation;
using com.db4o.nativequery.expr;
using com.db4o.nativequery.optimization;
using com.db4o.query;

namespace com.db4o.inside.query
{
#if NET_2_0 || CF_2_0
    /// <summary>
    /// Supplies the information missing in the CompactFramework System.Delegate API: Target and Method.
    /// </summary>
    /// <typeparam name="DelegateType"></typeparam>
    public class MetaDelegate<DelegateType>
    {
        public readonly DelegateType Delegate;
        public readonly object Target;
        public readonly System.Reflection.MethodBase Method;

        // IMPORTANT: don't change the order of parameters here because it is
        // assumed by the instrumentation tool to be exactly like this:
        //  1) target object
        //  2) delegate reference
        //  3) method info object
        public MetaDelegate(object target, DelegateType delegateRef, System.Reflection.MethodBase method)
        {
            this.Target = target;
            this.Method = method;
            this.Delegate = delegateRef;
        }
    }
#endif
    
	public class NativeQueryHandler
	{
		private ObjectContainer _container;

		private Db4oNQOptimizer _enhancer;

		private ExpressionBuilder _builder;

		public event QueryExecutionHandler QueryExecution;

		public event QueryOptimizationFailureHandler QueryOptimizationFailure;

		public NativeQueryHandler(com.db4o.ObjectContainer container)
		{
			_container = container;
		}

		public virtual com.db4o.ObjectSet execute(com.db4o.query.Predicate predicate, com.db4o.query.QueryComparator comparator)
		{
		    com.db4o.query.Query q = configureQuery(predicate);
		    q.sortBy(comparator);
			return q.execute();
		}

#if NET_2_0 || CF_2_0
        public virtual System.Collections.Generic.IList<Extent> execute<Extent>(System.Predicate<Extent> match,
                                                                                com.db4o.query.QueryComparator comparator)
        {
    #if CF_2_0
            return executeUnoptimized<Extent>(queryForExtent<Extent>(comparator), match);
    #else
            // XXX: check GetDelegateList().Length
            // only 1 delegate must be allowed
            // although we could use it as a filter chain
            // (and)
            return ExecuteImpl<Extent>(match, match.Target, match.Method, match, comparator);
    #endif
        }
#endif

#if NET_2_0 || CF_2_0
	    /// <summary>
	    /// ExecuteMeta should not generally be called by user's code. Calls to ExecuteMeta should
	    /// be inserted by an instrumentation tool.
	    /// </summary>
	    /// <typeparam name="Extent"></typeparam>
	    /// <param name="predicate"></param>
	    /// <param name="comparator"></param>
	    /// <returns></returns>
        public System.Collections.Generic.IList<Extent> ExecuteMeta<Extent>(MetaDelegate<System.Predicate<Extent>> predicate, com.db4o.query.QueryComparator comparator)
	    {
            return ExecuteImpl<Extent>(predicate, predicate.Target, predicate.Method, predicate.Delegate, comparator);
	    }
	    
	    public static System.Collections.Generic.IList<Extent> ExecuteInstrumentedStaticDelegateQuery<Extent>(ObjectContainer container,
                                                                                    System.Predicate<Extent> predicate,
                                                                                    RuntimeMethodHandle predicateMethodHandle)
	    {
            return ExecuteInstrumentedDelegateQuery(container, null, predicate, predicateMethodHandle);
	    }

        public static System.Collections.Generic.IList<Extent> ExecuteInstrumentedDelegateQuery<Extent>(ObjectContainer container,
                                                                                    object target,
                                                                                    System.Predicate<Extent> predicate,
                                                                                    RuntimeMethodHandle predicateMethodHandle)
        {
            return ((YapStream)container).getNativeQueryHandler().ExecuteMeta(
                new MetaDelegate<Predicate<Extent>>(
                    target,
                    predicate,
                    System.Reflection.MethodBase.GetMethodFromHandle(predicateMethodHandle)),
                null);
        }
	    
        private System.Collections.Generic.IList<Extent> ExecuteImpl<Extent>(
                                                                        object originalPredicate,
                                                                        object matchTarget,
                                                                        System.Reflection.MethodBase matchMethod,
                                                                        System.Predicate<Extent> match,
                                                                        com.db4o.query.QueryComparator comparator)
        {
            com.db4o.query.Query q = queryForExtent<Extent>(comparator);
            try
            {
                if (OptimizeNativeQueries())
                {
                    optimizeQuery(q, matchTarget, matchMethod);
                    OnQueryExecution(originalPredicate, QueryExecutionKind.DynamicallyOptimized);

                    return WrapQueryResult<Extent>(q);
                }
            }
            catch (System.Exception e)
            {
                OnQueryOptimizationFailure(e);
            }
            return executeUnoptimized(q, match);
        }

        private System.Collections.Generic.IList<Extent> executeUnoptimized<Extent>(Query q, Predicate<Extent> match)
	    {
	        q.constrain(new GenericPredicateEvaluation<Extent>(match));
	        OnQueryExecution(match, QueryExecutionKind.Unoptimized);
	        return WrapQueryResult<Extent>(q);
	    }

	    private com.db4o.query.Query queryForExtent<Extent>(com.db4o.query.QueryComparator comparator)
	    {
            com.db4o.query.Query q = _container.query();
            q.constrain(typeof(Extent));
            q.sortBy(comparator);
            return q;
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

		void optimizeQuery(Query q, object predicate, System.Reflection.MethodBase filterMethod)
		{
			if (_builder == null)
				_builder = ExpressionBuilderFactory.CreateExpressionBuilder();
	
			// TODO: cache predicate expressions here
			Expression expression = _builder.FromMethod(filterMethod);
			new SODAQueryBuilder().optimizeQuery(expression, q, predicate);
		}

		private void OnQueryExecution(object predicate, QueryExecutionKind kind)
		{
            if (null == QueryExecution) return;
		    QueryExecution(this, new QueryExecutionEventArgs(predicate, kind));
		}

		private void OnQueryOptimizationFailure(System.Exception e)
		{
            if (null == QueryOptimizationFailure) return;
		    QueryOptimizationFailure(this, new QueryOptimizationFailureEventArgs(e));
		}
	}

#if NET_2_0 || CF_2_0
    class GenericPredicateEvaluation<T> : DelegateEnvelope, com.db4o.query.Evaluation
    {
        public GenericPredicateEvaluation(System.Predicate<T> predicate)
            : base(predicate)
        {
        }

        public void evaluate(com.db4o.query.Candidate candidate)
        {
            // use starting _ for PascalCase conversion purposes
            System.Predicate<T> _predicate = (System.Predicate<T>)GetContent();
            candidate.include(_predicate((T)candidate.getObject()));
        }
    }
#endif
}


