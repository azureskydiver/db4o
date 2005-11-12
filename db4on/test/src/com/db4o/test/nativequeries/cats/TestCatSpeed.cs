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
			public bool match(com.db4o.test.nativequeries.cats.Cat cat)
			{
				return cat.getAge() < 800;
			}

			public override void constrain(com.db4o.query.Query q)
			{
				q.descend("_age").constrain(800).smaller();
			}

#if NET_2_0
            public override void delegateNQ(ObjectContainer oc)
            {
                oc.query<Cat>(delegate(Cat cat)
                {
                    return cat.getAge() < 800;
                });

            }
#endif

		}

		private sealed class AgeInRange : SodaCatPredicate
		{
			public bool match(com.db4o.test.nativequeries.cats.Cat cat)
			{
				return cat.getAge() > this.lower() && cat.getAge() < this.upper();
			}

			public override void constrain(com.db4o.query.Query q)
			{
				com.db4o.query.Query qa = q.descend("_age");
				qa.constrain(this.lower()).greater().and(qa.constrain(this.upper()).smaller());
			}
#if NET_2_0
            public override void delegateNQ(ObjectContainer oc)
            {
                oc.query<Cat>(delegate(Cat cat)
                {
                    return cat.getAge() > this.lower() && cat.getAge() < this.upper();
                });

            }
#endif

        }

		private sealed class NameIs : SodaCatPredicate
		{
			public bool match(com.db4o.test.nativequeries.cats.Cat cat)
			{
				return cat.getFirstName()=="SpeedyClone991";
			}

			public override void constrain(com.db4o.query.Query q)
			{
				q.descend("_firstName").constrain("SpeedyClone991");
			}
#if NET_2_0
            public override void delegateNQ(ObjectContainer oc)
            {
                oc.query<Cat>(delegate(Cat cat)
                {
                    return cat.getFirstName() == "SpeedyClone991";
                });

            }
#endif

        }

		private sealed class AgeRangeName : com.db4o.test.nativequeries.cats.SodaCatPredicate
		{
			public bool match(com.db4o.test.nativequeries.cats.Cat cat)
			{
				return cat.getAge() < 750 
                    || cat.getAge() > 900 
                    || cat.getFirstName()=="SpeedyClone888";
			}

			public override void constrain(com.db4o.query.Query q)
			{
				com.db4o.query.Query qa = q.descend("_age");
				com.db4o.query.Constraint ca1 = qa.constrain(750).smaller();
				com.db4o.query.Constraint ca2 = qa.constrain(900).greater();
				com.db4o.query.Constraint cn = q.descend("_firstName").constrain("SpeedyClone888"
					);
				ca1.or(ca2).or(cn);
			}
#if NET_2_0
            public override void delegateNQ(ObjectContainer oc)
            {
                oc.query<Cat>(delegate(Cat cat)
                {
                    return cat.getAge() < 750
                        || cat.getAge() > 900
                        || cat.getFirstName() == "SpeedyClone888";
                });

            }
#endif

        }

		private sealed class FatherAgeSmaller : SodaCatPredicate
		{
			public bool match(com.db4o.test.nativequeries.cats.Cat cat)
			{
				return cat.getFather() != null 
                    && cat.getFather().getAge() < 900;
			}

			public override void constrain(com.db4o.query.Query q)
			{
				com.db4o.query.Query qf = q.descend("_father");
				qf.constrain(null).not();
				qf.descend("_age").constrain(900).smaller();
			}
#if NET_2_0
            public override void delegateNQ(ObjectContainer oc)
            {
                oc.query<Cat>(delegate(Cat cat)
                {
                    return cat.getFather() != null
                        && cat.getFather().getAge() < 900;
                });

            }
#endif

        }

		private sealed class FatherAgeFirstname : SodaCatPredicate
		{

			public bool match(com.db4o.test.nativequeries.cats.Cat cat)
			{
				return cat.getFather() != null 
                        && (cat.getFather().getAge() < 900 
                        || cat.getFather().getFirstName()=="SpeedyClone933");
			}

			public override void constrain(com.db4o.query.Query q)
			{
				com.db4o.query.Query qf = q.descend("_father");
				com.db4o.query.Constraint c1 = qf.constrain(null).not();
				com.db4o.query.Constraint c2 = qf.descend("_age").constrain(900).smaller();
				com.db4o.query.Constraint c3 = qf.descend("_firstName").constrain("SpeedyClone933"
					);
				c2.or(c3);
			}
#if NET_2_0
            public override void delegateNQ(ObjectContainer oc)
            {
                oc.query<Cat>(delegate(Cat cat)
                {
                    return cat.getFather() != null
                            && (cat.getFather().getAge() < 900
                            || cat.getFather().getFirstName() == "SpeedyClone933");
                });

            }
#endif

        }

		private sealed class MultiRangeFirstname : SodaCatPredicate
		{

			public bool match(com.db4o.test.nativequeries.cats.Cat cat)
			{
				return cat.getAge() < 100 || (cat.getAge() > 200 && cat.getAge() < 300) || cat.getAge
					() < 400 && cat.getFirstName()=="SpeedyClone150";
			}

			public override void constrain(com.db4o.query.Query q)
			{
				com.db4o.query.Query qf = q.descend("_age");
				com.db4o.query.Constraint c1 = qf.constrain(100).smaller();
				com.db4o.query.Constraint c2 = qf.constrain(200).greater().and(qf.constrain(300).
					smaller());
				com.db4o.query.Constraint c3 = qf.constrain(400).smaller().and(q.descend("_firstName"
					).constrain("SpeedyClone150"));
				c1.or(c2).or(c3);
			}
#if NET_2_0
            public override void delegateNQ(ObjectContainer oc)
            {
                oc.query<Cat>(delegate(Cat cat)
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
			com.db4o.Db4o.configure().freespace().useRamSystem();
			com.db4o.config.ObjectClass objectClass = com.db4o.Db4o.configure().objectClass(j4o.lang.Class.getClassForType
				(typeof(com.db4o.test.nativequeries.cats.Cat)));
			objectClass.objectField("_firstName").indexed(true);
			objectClass.objectField("_lastName").indexed(true);
			objectClass.objectField("_age").indexed(true);
			objectClass.objectField("_father").indexed(true);
			objectClass.objectField("_mother").indexed(true);
			for (int countIdx = 0; countIdx < COUNT.Length; countIdx++)
			{
				storeCats(COUNT[countIdx]);
				for (int predIdx = 0; predIdx < PREDICATES.Length; predIdx++)
				{
					PREDICATES[predIdx].setCount(COUNT[countIdx]);
				}
				if (ONLY_RUN_PREDICATE_NR > 0)
				{
					queryCats(ONLY_RUN_PREDICATE_NR - 1);
				}
				else
				{
					queryCats();
				}
			}
		}

		public static void queryCats()
		{
			for (int predIdx = 0; predIdx < PREDICATES.Length; predIdx++)
			{
				queryCats(predIdx);
			}
		}

		private static void queryCats(int predIdx)
		{
			long timeUnopt = 0;
			long timeOpt = 0;
			long timeSoda = 0;
			for (int run = 0; run <= NUMRUNS; run++)
			{
				bool warmup = (run == 0);
                if(NQ_NOPT){
                    timeUnopt += timeQuery(PREDICATES[predIdx], false, warmup);
                }
                if(NQ_OPT){
                    timeOpt += timeQuery(PREDICATES[predIdx], true, warmup);
                }
                if(SODA){
                    timeSoda += TestCatSpeed.timeSoda(PREDICATES[predIdx], warmup);
                }
			}
            println
			("PREDICATE #" + (predIdx + 1) + ": " + (timeUnopt
				 / NUMRUNS) + " / " + (timeOpt / NUMRUNS) + " / " + (timeSoda / NUMRUNS));
		}

		public static long timeQuery(SodaCatPredicate predicate, bool optimize, bool
			 warmup)
		{
			com.db4o.Db4o.configure().optimizeNativeQueries(optimize);
			com.db4o.ObjectContainer db = com.db4o.Db4o.openFile(FILENAME);
			long start = now();
#if NET_2_0
            predicate.delegateNQ(db);
#else
			db.query(predicate);
#endif
            long time = (warmup ? 0 : now() - start);
			db.close();
			return time;
		}

		public static long timeSoda(com.db4o.test.nativequeries.cats.SodaCatPredicate predicate
			, bool warmup)
		{
			com.db4o.ObjectContainer db = com.db4o.Db4o.openFile(FILENAME);
			long start = now();
			predicate.sodaQuery(db);
			long time = (warmup ? 0 : now() - start);
			db.close();
			return time;
		}

		public static void storeCats(int count)
		{
			println("STORING " + count + " CATS");
			new j4o.io.File(FILENAME).delete();
			com.db4o.ObjectContainer db = com.db4o.Db4o.openFile(FILENAME);
			com.db4o.test.nativequeries.cats.Cat lastCat = null;
			for (int i = 0; i < count; i++)
			{
				com.db4o.test.nativequeries.cats.Cat fastCat = new com.db4o.test.nativequeries.cats.Cat
					();
				fastCat._firstName = "SpeedyClone" + i;
				fastCat._age = i;
				fastCat._father = lastCat;
				db.set(fastCat);
				if (i % 50000 == 0)
				{
					db.commit();
				}
				lastCat = fastCat;
			}
			db.close();
		}

        public static void println(string str){
#if CF_1_0 || CF_2_0
            com.db4o.Console.WriteLine(str);
#endif
#if NET_1_0 || NET_2_0
            System.Console.WriteLine(str);
#endif
        }

        public static long now(){
            return System.Environment.TickCount;
        }
	}
}
