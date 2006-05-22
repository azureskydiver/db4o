namespace com.db4o.test.nativequeries.cats
{
	public class TestCatSpeed
	{
		private static readonly string FILENAME = "catspeed.yap";

		private static readonly int[] COUNT = { 1000 };

		private const int NUMRUNS = 5;

		private const int ONLY_RUN_PREDICATE_NR = -1;

		private const bool NQ_NOPT = true;

		private const bool NQ_OPT = true;

		private const bool SODA = true;

		private sealed class AgeSmaller800 : SodaCatPredicate
		{
			public bool Match(com.db4o.test.nativequeries.cats.Cat cat)
			{
				return cat.GetAge() < 800;
			}

			public override void Constrain(com.db4o.query.Query q)
			{
				q.Descend("_age").Constrain(800).Smaller();
			}

#if NET_2_0
            public override void DelegateNQ(ObjectContainer oc)
            {
                oc.Query<Cat>(delegate(Cat cat)
                {
                    return cat.GetAge() < 800;
                });

            }
#endif

		}

		private sealed class AgeInRange : SodaCatPredicate
		{
			public bool Match(com.db4o.test.nativequeries.cats.Cat cat)
			{
				return cat.GetAge() > this.Lower() && cat.GetAge() < this.Upper();
			}

			public override void Constrain(com.db4o.query.Query q)
			{
				com.db4o.query.Query qa = q.Descend("_age");
				qa.Constrain(this.Lower()).Greater().And(qa.Constrain(this.Upper()).Smaller());
			}
#if NET_2_0
            public override void DelegateNQ(ObjectContainer oc)
            {
                oc.Query<Cat>(delegate(Cat cat)
                {
                    return cat.GetAge() > this.Lower() && cat.GetAge() < this.Upper();
                });

            }
#endif

        }

		private sealed class NameIs : SodaCatPredicate
		{
			public bool Match(com.db4o.test.nativequeries.cats.Cat cat)
			{
				return cat.GetFirstName()=="SpeedyClone991";
			}

			public override void Constrain(com.db4o.query.Query q)
			{
				q.Descend("_firstName").Constrain("SpeedyClone991");
			}
#if NET_2_0
            public override void DelegateNQ(ObjectContainer oc)
            {
                oc.Query<Cat>(delegate(Cat cat)
                {
                    return cat.GetFirstName() == "SpeedyClone991";
                });

            }
#endif

        }

		private sealed class AgeRangeName : com.db4o.test.nativequeries.cats.SodaCatPredicate
		{
			public bool Match(com.db4o.test.nativequeries.cats.Cat cat)
			{
				return cat.GetAge() < 750 
                    || cat.GetAge() > 900 
                    || cat.GetFirstName()=="SpeedyClone888";
			}

			public override void Constrain(com.db4o.query.Query q)
			{
				com.db4o.query.Query qa = q.Descend("_age");
				com.db4o.query.Constraint ca1 = qa.Constrain(750).Smaller();
				com.db4o.query.Constraint ca2 = qa.Constrain(900).Greater();
				com.db4o.query.Constraint cn = q.Descend("_firstName").Constrain("SpeedyClone888"
					);
				ca1.Or(ca2).Or(cn);
			}
#if NET_2_0
            public override void DelegateNQ(ObjectContainer oc)
            {
                oc.Query<Cat>(delegate(Cat cat)
                {
                    return cat.GetAge() < 750
                        || cat.GetAge() > 900
                        || cat.GetFirstName() == "SpeedyClone888";
                });

            }
#endif

        }

		private sealed class FatherAgeSmaller : SodaCatPredicate
		{
			public bool Match(com.db4o.test.nativequeries.cats.Cat cat)
			{
				return cat.GetFather() != null 
                    && cat.GetFather().GetAge() < 900;
			}

			public override void Constrain(com.db4o.query.Query q)
			{
				com.db4o.query.Query qf = q.Descend("_father");
				qf.Constrain(null).Not();
				qf.Descend("_age").Constrain(900).Smaller();
			}
#if NET_2_0
            public override void DelegateNQ(ObjectContainer oc)
            {
                oc.Query<Cat>(delegate(Cat cat)
                {
                    return cat.GetFather() != null
                        && cat.GetFather().GetAge() < 900;
                });

            }
#endif

        }

		private sealed class FatherAgeFirstname : SodaCatPredicate
		{

			public bool Match(com.db4o.test.nativequeries.cats.Cat cat)
			{
				return cat.GetFather() != null 
                        && (cat.GetFather().GetAge() < 900 
                        || cat.GetFather().GetFirstName()=="SpeedyClone933");
			}

			public override void Constrain(com.db4o.query.Query q)
			{
				com.db4o.query.Query qf = q.Descend("_father");
				com.db4o.query.Constraint c1 = qf.Constrain(null).Not();
				com.db4o.query.Constraint c2 = qf.Descend("_age").Constrain(900).Smaller();
				com.db4o.query.Constraint c3 = qf.Descend("_firstName").Constrain("SpeedyClone933"
					);
				c2.Or(c3);
			}
#if NET_2_0
            public override void DelegateNQ(ObjectContainer oc)
            {
                oc.Query<Cat>(delegate(Cat cat)
                {
                    return cat.GetFather() != null
                            && (cat.GetFather().GetAge() < 900
                            || cat.GetFather().GetFirstName() == "SpeedyClone933");
                });

            }
#endif

        }

		private sealed class MultiRangeFirstname : SodaCatPredicate
		{

			public bool Match(com.db4o.test.nativequeries.cats.Cat cat)
			{
				return cat.GetAge() < 100 || (cat.GetAge() > 200 && cat.GetAge() < 300) || cat.getAge
					() < 400 && cat.GetFirstName()=="SpeedyClone150";
			}

			public override void Constrain(com.db4o.query.Query q)
			{
				com.db4o.query.Query qf = q.Descend("_age");
				com.db4o.query.Constraint c1 = qf.Constrain(100).Smaller();
				com.db4o.query.Constraint c2 = qf.Constrain(200).Greater().And(qf.Constrain(300).
					Smaller());
				com.db4o.query.Constraint c3 = qf.Constrain(400).Smaller().And(q.Descend("_firstName"
					).Constrain("SpeedyClone150"));
				c1.Or(c2).Or(c3);
			}
#if NET_2_0
            public override void DelegateNQ(ObjectContainer oc)
            {
                oc.Query<Cat>(delegate(Cat cat)
                {
                    return true;
                });

            }
#endif

        }

		private static readonly com.db4o.test.nativequeries.cats.SodaCatPredicate[] PREDICATES = 
			{ 
				new AgeSmaller800(), 
//				new AgeInRange(), 
				new NameIs(),
//				new AgeRangeName(),
				new FatherAgeSmaller(),
				new FatherAgeFirstname(), 
				new MultiRangeFirstname() 
			};


		public static void Main(string[] args)
		{
			com.db4o.Db4o.Configure().Freespace().UseRamSystem();
			com.db4o.config.ObjectClass objectClass = com.db4o.Db4o.Configure().ObjectClass(j4o.lang.Class.getClassForType
				(typeof(com.db4o.test.nativequeries.cats.Cat)));
			objectClass.ObjectField("_firstName").Indexed(true);
			objectClass.ObjectField("_lastName").Indexed(true);
			objectClass.ObjectField("_age").Indexed(true);
			objectClass.ObjectField("_father").Indexed(true);
			objectClass.ObjectField("_mother").Indexed(true);
			for (int countIdx = 0; countIdx < COUNT.Length; countIdx++)
			{
				StoreCats(COUNT[countIdx]);
				for (int predIdx = 0; predIdx < PREDICATES.Length; predIdx++)
				{
					PREDICATES[predIdx].SetCount(COUNT[countIdx]);
				}
				if (ONLY_RUN_PREDICATE_NR > 0)
				{
					QueryCats(ONLY_RUN_PREDICATE_NR - 1);
				}
				else
				{
					QueryCats();
				}
			}
		}

		public static void QueryCats()
		{
			for (int predIdx = 0; predIdx < PREDICATES.Length; predIdx++)
			{
				QueryCats(predIdx);
			}
		}

		private static void QueryCats(int predIdx)
		{
			long timeUnopt = 0;
			long timeOpt = 0;
			long timeSoda = 0;
			for (int run = 0; run <= NUMRUNS; run++)
			{
				bool warmup = (run == 0);
                if(NQ_NOPT){
                    timeUnopt += TimeQuery(PREDICATES[predIdx], false, warmup);
                }
                if(NQ_OPT){
                    timeOpt += TimeQuery(PREDICATES[predIdx], true, warmup);
                }
                if(SODA){
                    timeSoda += TestCatSpeed.TimeSoda(PREDICATES[predIdx], warmup);
                }
			}
            println
			("PREDICATE #" + (predIdx + 1) + ": " + (timeUnopt
				 / NUMRUNS) + " / " + (timeOpt / NUMRUNS) + " / " + (timeSoda / NUMRUNS));
		}

		public static long TimeQuery(SodaCatPredicate predicate, bool optimize, bool
			 warmup)
		{
			com.db4o.Db4o.Configure().OptimizeNativeQueries(optimize);
			com.db4o.ObjectContainer db = com.db4o.Db4o.OpenFile(FILENAME);
			long start = Now();
#if NET_2_0
            predicate.DelegateNQ(db);
#else
			db.Query(predicate);
#endif
            long time = (warmup ? 0 : Now() - start);
			db.Close();
			return time;
		}

		public static long TimeSoda(com.db4o.test.nativequeries.cats.SodaCatPredicate predicate
			, bool warmup)
		{
			com.db4o.ObjectContainer db = com.db4o.Db4o.OpenFile(FILENAME);
			long start = Now();
			predicate.SodaQuery(db);
			long time = (warmup ? 0 : Now() - start);
			db.Close();
			return time;
		}

		public static void StoreCats(int count)
		{
			Println("STORING " + count + " CATS");
			new j4o.io.File(FILENAME).Delete();
			com.db4o.ObjectContainer db = com.db4o.Db4o.OpenFile(FILENAME);
			com.db4o.test.nativequeries.cats.Cat lastCat = null;
			for (int i = 0; i < count; i++)
			{
				com.db4o.test.nativequeries.cats.Cat fastCat = new com.db4o.test.nativequeries.cats.Cat
					();
				fastCat._firstName = "SpeedyClone" + i;
				fastCat._age = i;
				fastCat._father = lastCat;
				db.Set(fastCat);
				if (i % 50000 == 0)
				{
					db.Commit();
				}
				lastCat = fastCat;
			}
			db.Close();
		}

        public static void Println(string str){
#if CF_1_0 || CF_2_0
            com.db4o.Console.WriteLine(str);
#endif
#if NET_1_0 || NET_2_0
            System.Console.WriteLine(str);
#endif
        }

        public static long Now(){
            return System.Environment.TickCount;
        }
	}
}
