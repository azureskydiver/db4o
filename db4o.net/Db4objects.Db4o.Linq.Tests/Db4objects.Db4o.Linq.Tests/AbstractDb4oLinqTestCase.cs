﻿/* Copyright (C) 2007 - 2008  db4objects Inc.  http://www.db4o.com */

using System;
using System.Collections.Generic;
using System.Linq;

using Db4objects.Db4o;
using Db4objects.Db4o.Events;
using Db4objects.Db4o.Linq;
using Db4objects.Db4o.Query;

using Db4oUnit;
using Db4oUnit.Extensions;

using Db4objects.Db4o.Linq.Tests.Queries;

namespace Db4objects.Db4o.Linq.Tests
{
	public abstract class AbstractDb4oLinqTestCase : AbstractDb4oTestCase
	{
		public static void AssertSet<T>(IEnumerable<T> expected, IEnumerable<T> candidate)
		{
			var ex = new HashSet<T>(expected);
			var d = new HashSet<T>(candidate);

			Assert.AreEqual(ex.Count, d.Count);
			Assert.IsTrue(ex.SetEquals(d));
		}

		public static void AssertSequence<T>(IEnumerable<T> expected, IEnumerable<T> candidate)
		{
			Assert.IsTrue(expected.SequenceEqual(candidate));
		}

		protected void AssertQuery(string expected, Action action)
		{
			using (var recorder = new QueryRecorder(Db()))
			{
				action();

				Assert.IsNotNull(recorder.Query);
				Assert.AreEqual(expected, recorder.Query.ToQueryString());
			}
		}

		private class QueryRecorder : IDisposable
		{
			private IQuery _query;
			private IEventRegistry _registry;

			public IQuery Query
			{
				get { return _query; }
			}

			public QueryRecorder(IObjectContainer container)
			{
				_registry = EventRegistryFactory.ForObjectContainer(container);
				_registry.QueryStarted += OnQueryStarted;
			}

			private void OnQueryStarted(object sender, QueryEventArgs args)
			{
				_query = args.Query;
			}

			public void Dispose()
			{
				_registry.QueryStarted -= OnQueryStarted;
			}
		}
	}
}
