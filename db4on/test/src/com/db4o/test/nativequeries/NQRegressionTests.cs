using com.db4o.@internal.query;
using com.db4o.@internal;

namespace com.db4o.test.nativequeries
{
	public class NQRegressionTests
	{
		private abstract class Base
		{
			internal int id;

			public Base(int id)
			{
				this.id = id;
			}

			public virtual int GetId()
			{
				return id;
			}
		}

		private class Data : com.db4o.test.nativequeries.NQRegressionTests.Base
		{
			internal float value;

			internal string name;

			internal com.db4o.test.nativequeries.NQRegressionTests.Data prev;

			public Data(int id, float value, string name, com.db4o.test.nativequeries.NQRegressionTests.Data
				 prev) : base(id)
			{
				this.value = value;
				this.name = name;
				this.prev = prev;
			}

			public virtual float GetValue()
			{
				return value;
			}

			public virtual string GetName()
			{
				return name;
			}

			public virtual com.db4o.test.nativequeries.NQRegressionTests.Data GetPrev()
			{
				return prev;
			}
		}

		public virtual void Store()
		{
			com.db4o.test.nativequeries.NQRegressionTests.Data a = new com.db4o.test.nativequeries.NQRegressionTests.Data
				(1, 1.1f, "Aa", null);
			com.db4o.test.nativequeries.NQRegressionTests.Data b = new com.db4o.test.nativequeries.NQRegressionTests.Data
				(2, 1.1f, "Bb", a);
			com.db4o.test.nativequeries.NQRegressionTests.Data c = new com.db4o.test.nativequeries.NQRegressionTests.Data
				(3, 2.2f, "Cc", b);
			com.db4o.test.nativequeries.NQRegressionTests.Data cc = new com.db4o.test.nativequeries.NQRegressionTests.Data
				(3, 3.3f, "Cc", null);
			com.db4o.test.Tester.Store(a);
			com.db4o.test.Tester.Store(b);
			com.db4o.test.Tester.Store(c);
			com.db4o.test.Tester.Store(cc);
		}

		private abstract class ExpectingPredicate : com.db4o.query.Predicate
		{
			public abstract int Expected();
		}

		private sealed class _AnonymousInnerClass72 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass72()
			{
			}

			public override int Expected()
			{
				return 0;
			}

			public bool Match(object candidate)
			{
				return true;
			}
		}

		private sealed class _AnonymousInnerClass79 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass79()
			{
			}

			public override int Expected()
			{
				return 4;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return true;
			}
		}

		private sealed class _AnonymousInnerClass92 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass92()
			{
			}

			public override int Expected()
			{
				return 1;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.id == 1;
			}
		}

		private sealed class _AnonymousInnerClass98 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass98()
			{
			}

			public override int Expected()
			{
				return 2;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.id == 3;
			}
		}

		private sealed class _AnonymousInnerClass104 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass104()
			{
			}

			public override int Expected()
			{
				return 2;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.value == 1.1f;
			}
		}

		private sealed class _AnonymousInnerClass110 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass110()
			{
			}

			public override int Expected()
			{
				return 1;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.value == 3.3f;
			}
		}

		private sealed class _AnonymousInnerClass117 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass117()
			{
			}

			public override int Expected()
			{
				return 1;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.name=="Aa";
			}
		}

		private sealed class _AnonymousInnerClass123 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass123()
			{
			}

			public override int Expected()
			{
				return 2;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.name=="Cc";
			}
		}

		private sealed class _AnonymousInnerClass130 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass130()
			{
			}

			public override int Expected()
			{
				return 1;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.id < 2;
			}
		}

		private sealed class _AnonymousInnerClass136 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass136()
			{
			}

			public override int Expected()
			{
				return 2;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.id > 2;
			}
		}

		private sealed class _AnonymousInnerClass142 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass142()
			{
			}

			public override int Expected()
			{
				return 2;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.id <= 2;
			}
		}

		private sealed class _AnonymousInnerClass148 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass148()
			{
			}

			public override int Expected()
			{
				return 3;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.id >= 2;
			}
		}

		private sealed class _AnonymousInnerClass155 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass155()
			{
			}

			public override int Expected()
			{
				return 1;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.value > 2.9f;
			}
		}

		private sealed class _AnonymousInnerClass162 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass162()
			{
			}

			public override int Expected()
			{
				return 2;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.GetPrev() != null && candidate.GetPrev().GetId() >= 1;
			}
		}

		private sealed class _AnonymousInnerClass168 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass168()
			{
			}

			public override int Expected()
			{
				return 1;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return (candidate.GetPrev() != null) && ("Bb"==candidate.GetPrev().GetName(
					));
			}
		}

		private sealed class _AnonymousInnerClass174 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass174()
			{
			}

			public override int Expected()
			{
				return 0;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.GetPrev() != null && candidate.GetPrev().GetName()=="";
			}
		}

		private sealed class _AnonymousInnerClass181 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass181()
			{
			}

			public override int Expected()
			{
				return 1;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.GetId() == 2;
			}
		}

		private sealed class _AnonymousInnerClass187 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass187()
			{
			}

			public override int Expected()
			{
				return 1;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.GetId() < 2;
			}
		}

		private sealed class _AnonymousInnerClass193 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass193()
			{
			}

			public override int Expected()
			{
				return 2;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.GetId() > 2;
			}
		}

		private sealed class _AnonymousInnerClass199 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass199()
			{
			}

			public override int Expected()
			{
				return 2;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.GetId() <= 2;
			}
		}

		private sealed class _AnonymousInnerClass205 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass205()
			{
			}

			public override int Expected()
			{
				return 3;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.GetId() >= 2;
			}
		}

		private sealed class _AnonymousInnerClass211 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass211()
			{
			}

			public override int Expected()
			{
				return 2;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.GetName()=="Cc";
			}
		}

		private sealed class _AnonymousInnerClass218 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass218()
			{
			}

			public override int Expected()
			{
				return 3;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return !(candidate.id == 1);
			}
		}

		private sealed class _AnonymousInnerClass224 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass224()
			{
			}

			public override int Expected()
			{
				return 2;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return !(candidate.GetId() > 2);
			}
		}

		private sealed class _AnonymousInnerClass230 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass230()
			{
			}

			public override int Expected()
			{
				return 2;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return !(candidate.GetName()=="Cc");
			}
		}

		private sealed class _AnonymousInnerClass237 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass237()
			{
			}

			public override int Expected()
			{
				return 2;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return (candidate.id > 1) && candidate.GetName()=="Cc";
			}
		}

		private sealed class _AnonymousInnerClass243 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass243()
			{
			}

			public override int Expected()
			{
				return 1;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return (candidate.id > 1) && (candidate.GetId() <= 2);
			}
		}

		private sealed class _AnonymousInnerClass249 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass249()
			{
			}

			public override int Expected()
			{
				return 0;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return (candidate.id > 1) && (candidate.GetId() < 1);
			}
		}

		private sealed class _AnonymousInnerClass256 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass256()
			{
			}

			public override int Expected()
			{
				return 3;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return (candidate.id == 1) || candidate.GetName()=="Cc";
			}
		}

		private sealed class _AnonymousInnerClass262 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass262()
			{
			}

			public override int Expected()
			{
				return 4;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return (candidate.id > 1) || (candidate.GetId() <= 2);
			}
		}

		private sealed class _AnonymousInnerClass268 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass268()
			{
			}

			public override int Expected()
			{
				return 3;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return (candidate.id <= 1) || (candidate.GetId() >= 3);
			}
		}

		private sealed class _AnonymousInnerClass275 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass275()
			{
			}

			public override int Expected()
			{
				return 2;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return ((candidate.id >= 1) || candidate.GetName()=="Cc") && candidate.GetId
					() < 3;
			}
		}

		private sealed class _AnonymousInnerClass281 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass281()
			{
			}

			public override int Expected()
			{
				return 1;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return ((candidate.id == 2) || candidate.GetId() <= 1) && !(candidate.GetName()=="Bb");
			}
		}

		private sealed class _AnonymousInnerClass288 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass288()
			{
			}

			private int id = 2;

			public override int Expected()
			{
				return 3;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.id >= this.id;
			}
		}

		private sealed class _AnonymousInnerClass296 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass296()
			{
			}

			private string name = "Bb";

			public override int Expected()
			{
				return 1;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.GetName()==this.name;
			}
		}

		private sealed class _AnonymousInnerClass305 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass305()
			{
			}

			private int id = 2;

			public override int Expected()
			{
				return 2;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.id >= this.id + 1;
			}
		}

		private sealed class _AnonymousInnerClass313 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass313()
			{
			}

			private int factor = 2;

			private int Calc()
			{
				return this.factor + 1;
			}

			public override int Expected()
			{
				return 2;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.id >= this.Calc();
			}
		}

		private sealed class _AnonymousInnerClass325 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass325()
			{
			}

			private float predFactor = 2.0f;

			private float Calc()
			{
				return this.predFactor * 1.1f;
			}

			public override int Expected()
			{
				return 1;
			}

			public bool Match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.GetValue() == this.Calc();
			}
		}

		private static com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate[] PREDICATES
			= { 
			  	// TODO: unconditional
				// new _AnonymousInnerClass72(), // untyped/unconditional
				// new _AnonymousInnerClass79(), // unconditional
				
				new _AnonymousInnerClass92(),
				new _AnonymousInnerClass98(),
				new _AnonymousInnerClass104(), // float
				new _AnonymousInnerClass110(), // float
				new _AnonymousInnerClass117(),
				new _AnonymousInnerClass123(),
				new _AnonymousInnerClass130(),
				new _AnonymousInnerClass136(),
				new _AnonymousInnerClass142(),
				new _AnonymousInnerClass148(),
				new _AnonymousInnerClass155(), // float
				new _AnonymousInnerClass162(),
				new _AnonymousInnerClass168(),
				new _AnonymousInnerClass174(),
				new _AnonymousInnerClass181(),
				new _AnonymousInnerClass187(),
				new _AnonymousInnerClass193(),
				new _AnonymousInnerClass199(),
				new _AnonymousInnerClass205(),
				new _AnonymousInnerClass211(),
				new _AnonymousInnerClass218(),
				new _AnonymousInnerClass224(),
				new _AnonymousInnerClass230(),
				new _AnonymousInnerClass237(),
				new _AnonymousInnerClass243(),
				new _AnonymousInnerClass249(),
				new _AnonymousInnerClass256(),
				new _AnonymousInnerClass262(), // (candidate.id > 1) || (candidate.GetId() <= 2)
				new _AnonymousInnerClass268(),
				new _AnonymousInnerClass275(),
				new _AnonymousInnerClass281(), // ((candidate.id == 2) || candidate.GetId() <= 1) && !(candidate.GetName()=="Bb")
				new _AnonymousInnerClass288(),
				new _AnonymousInnerClass296(),
				
				// TODO: arithmetics
				// new _AnonymousInnerClass305(), // arithmetics
				// new _AnonymousInnerClass313(), // arithmetics
				// new _AnonymousInnerClass325() // arithmetics/float
			};

		public virtual void TestAll()
		{
			for (int predIdx = 0; predIdx < PREDICATES.Length; predIdx++)
			{
				com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate predicate = PREDICATES
					[predIdx];
				AssertNQResult(predicate);
			}
		}

		private void AssertNQResult(com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
			 filter)
		{
			// System.Console.WriteLine(filter.GetType());
			com.db4o.ObjectContainer db = com.db4o.test.Tester.ObjectContainer();
			
			QueryExecutionListener listener = new QueryExecutionListener(filter);
			NativeQueryHandler handler = ((ObjectContainerBase)db).GetNativeQueryHandler();
			handler.QueryExecution += new QueryExecutionHandler(listener.OnQueryExecution);
			try
			{
				db.Ext().Configure().OptimizeNativeQueries(false);
				com.db4o.ObjectSet raw = db.Query(filter);
				db.Ext().Configure().OptimizeNativeQueries(true);
				com.db4o.ObjectSet optimized = db.Query(filter);
				com.db4o.test.Tester.EnsureEquals(raw.Size(),optimized.Size());
				for(int resultIdx=0;resultIdx<raw.Size();resultIdx++) 
				{
					com.db4o.test.Tester.EnsureEquals(raw.Ext().Get(resultIdx),optimized.Ext().Get(resultIdx));
				}
				com.db4o.test.Tester.EnsureEquals(filter.Expected(), raw.Size());
			}
			finally
			{
				handler.QueryExecution -= new QueryExecutionHandler(listener.OnQueryExecution);
			}
		}

		private sealed class QueryExecutionListener
		{
			private readonly com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate filter;

			private int run = 0;

			public QueryExecutionListener(com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
				 filter)
			{
				this.filter = filter;
			}

			public void OnQueryExecution(object sender, QueryExecutionEventArgs args)
			{
				com.db4o.test.Tester.EnsureEquals(args.Predicate, filter);
				switch (this.run)
				{
					case 0:
					{
						Tester.EnsureEquals(QueryExecutionKind.Unoptimized, args.ExecutionKind);
						break;
					}

					case 1:
					{
						Tester.EnsureEquals(QueryExecutionKind.DynamicallyOptimized, args.ExecutionKind);
						break;
					}
				}
				this.run++;
			}
		}
	}
}
