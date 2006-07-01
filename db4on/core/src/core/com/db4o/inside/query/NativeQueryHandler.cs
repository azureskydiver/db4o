/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */
using System;
using com.db4o.nativequery.expr;
using com.db4o.nativequery.optimization;
using com.db4o.query;
using com.db4o.inside.diagnostic;

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

		public virtual com.db4o.ObjectSet Execute(com.db4o.query.Predicate predicate, com.db4o.query.QueryComparator comparator)
		{
			com.db4o.query.Query q = ConfigureQuery(predicate);
			q.SortBy(comparator);
			return q.Execute();
		}

#if NET_2_0 || CF_2_0
		public virtual System.Collections.Generic.IList<Extent> Execute<Extent>(System.Predicate<Extent> match,
																				com.db4o.query.QueryComparator comparator)
		{
#if CF_2_0
			return ExecuteUnoptimized<Extent>(QueryForExtent<Extent>(comparator), match);
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
			return ((YapStream)container).GetNativeQueryHandler().ExecuteMeta(
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
			com.db4o.query.Query q = QueryForExtent<Extent>(comparator);
			try
			{
				if (OptimizeNativeQueries())
				{
					OptimizeQuery(q, matchTarget, matchMethod);
					OnQueryExecution(originalPredicate, QueryExecutionKind.DynamicallyOptimized);

					return WrapQueryResult<Extent>(q);
				}
			}
			catch (System.Exception e)
			{
				OnQueryOptimizationFailure(e);
			}
			return ExecuteUnoptimized(q, match);
		}

		private System.Collections.Generic.IList<Extent> ExecuteUnoptimized<Extent>(Query q, Predicate<Extent> match)
		{
			q.Constrain(new GenericPredicateEvaluation<Extent>(match));
			OnQueryExecution(match, QueryExecutionKind.Unoptimized);
			return WrapQueryResult<Extent>(q);
		}

		private com.db4o.query.Query QueryForExtent<Extent>(com.db4o.query.QueryComparator comparator)
		{
			com.db4o.query.Query q = _container.Query();
			q.Constrain(typeof(Extent));
			q.SortBy(comparator);
			return q;
		}

		private static System.Collections.Generic.IList<Extent> WrapQueryResult<Extent>(com.db4o.query.Query q)
		{
			com.db4o.inside.query.QueryResult qr = ((QQuery)q).GetQueryResult();
			return new com.db4o.inside.query.GenericObjectSetFacade<Extent>(qr);
		}
#endif

		private Query ConfigureQuery(com.db4o.query.Predicate predicate)
		{
			Query q = _container.Query();
			Db4oEnhancedFilter filter = predicate as Db4oEnhancedFilter;
			if (null != filter)
			{
				filter.OptimizeQuery(q);
				OnQueryExecution(predicate, QueryExecutionKind.PreOptimized);
				return q;
			}
			
			q.Constrain(predicate.ExtentType());

			try
			{
				if (OptimizeNativeQueries())
				{
					OptimizeQuery(q, predicate, predicate.GetFilterMethod().MethodInfo);
					OnQueryExecution(predicate, QueryExecutionKind.DynamicallyOptimized);
					return q;
				}
			}
			catch (System.Exception e)
			{
				OnQueryOptimizationFailure(e);
			}
            if (OptimizeNativeQueries()) 
            {
                DiagnosticProcessor dp = ((YapStream)_container).i_handlers._diagnosticProcessor;
                if(dp.Enabled()){
                    dp.NativeQueryUnoptimized(predicate);
                }

            }
			q.Constrain(new com.db4o.inside.query.PredicateEvaluation(predicate));
			OnQueryExecution(predicate, QueryExecutionKind.Unoptimized);
			return q;
		}

		private bool OptimizeNativeQueries()
		{
			return _container.Ext().Configure().OptimizeNativeQueries();
		}

		void OptimizeQuery(Query q, object predicate, System.Reflection.MethodBase filterMethod)
		{
			if (_builder == null)
				_builder = ExpressionBuilderFactory.CreateExpressionBuilder();

			// TODO: cache predicate expressions here
			Expression expression = _builder.FromMethod(filterMethod);
			new SODAQueryBuilder().OptimizeQuery(expression, q, predicate);
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

		public void Evaluate(com.db4o.query.Candidate candidate)
		{
			// use starting _ for PascalCase conversion purposes
			System.Predicate<T> _predicate = (System.Predicate<T>)GetContent();
			candidate.Include(_predicate((T)candidate.GetObject()));
		}
	}
#endif
}


