﻿/* Copyright (C) 2007 - 2008  Versant Inc.  http://www.db4o.com */

using System;
using System.Collections.Generic;
using System.Reflection;

using Db4objects.Db4o;
using Db4objects.Db4o.Internal.Caching;
using Db4objects.Db4o.Linq.Caching;
using Db4objects.Db4o.Query;

using Cecil.FlowAnalysis;
using Cecil.FlowAnalysis.ActionFlow;
using Cecil.FlowAnalysis.CodeStructure;

using Mono.Cecil;
using Db4objects.Db4o.Linq.Internals;

namespace Db4objects.Db4o.Linq.CodeAnalysis
{
	internal class MethodAnalyser
	{
		private static ICache4<MethodDefinition, ActionFlowGraph> _graphCache =
			CacheFactory<MethodDefinition, ActionFlowGraph>.For(CacheFactory.New2QXCache(5));

		private ActionFlowGraph _graph;
		private Expression _queryExpression;
		private object[] _parameters;

		public bool IsFieldAccess
		{
			get { return _queryExpression != null && _queryExpression is FieldReferenceExpression; }
		}

		private MethodAnalyser(ActionFlowGraph graph, object[] parameters)
		{
			if (graph == null) throw new ArgumentNullException("graph");
			if (parameters == null) throw new ArgumentNullException("parameters");

			_graph = graph;
			_parameters = parameters;
			_queryExpression = QueryExpressionFinder.FindIn(graph);
		}

		public void AugmentQuery(QueryBuilderRecorder recorder)
		{
			if (_queryExpression == null) throw new QueryOptimizationException("No query expression");

			_queryExpression.Accept(new CodeQueryBuilder(recorder));
		}

		public static MethodAnalyser FromMethod(MethodInfo info, object[] parameters)
		{
			return GetAnalyserFor(ResolveMethod(info), parameters);
		}

		private static MethodDefinition ResolveMethod(MethodInfo info)
		{
			if (info == null) throw new ArgumentNullException("info");

			var method = MetadataResolver.Instance.ResolveMethod(info);

			if (method == null) throw new QueryOptimizationException(
				string.Format("Cannot resolve method {0}", info));

			return method;
		}

		private static MethodAnalyser GetAnalyserFor(MethodDefinition method, object[] parameters)
		{
			var graph = _graphCache.Produce(method, CreateActionFlowGraph);
			return new MethodAnalyser(graph, parameters);
		}

		private static ActionFlowGraph CreateActionFlowGraph(MethodDefinition method)
		{
			return FlowGraphFactory.CreateActionFlowGraph(FlowGraphFactory.CreateControlFlowGraph(method));
		}
	}
}
