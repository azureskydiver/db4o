namespace com.db4o.db4ounit.common.config
{
	public class ObjectTranslatorTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public class Thing
		{
			public string name;

			public Thing(string name)
			{
				this.name = name;
			}
		}

		public class ThingCounterTranslator : com.db4o.config.ObjectConstructor
		{
			private com.db4o.foundation.Hashtable4 _countCache = new com.db4o.foundation.Hashtable4
				();

			public virtual void OnActivate(com.db4o.ObjectContainer container, object applicationObject
				, object storedObject)
			{
			}

			public virtual object OnStore(com.db4o.ObjectContainer container, object applicationObject
				)
			{
				com.db4o.db4ounit.common.config.ObjectTranslatorTestCase.Thing t = (com.db4o.db4ounit.common.config.ObjectTranslatorTestCase.Thing
					)applicationObject;
				AddToCache(t);
				return t.name;
			}

			private void AddToCache(com.db4o.db4ounit.common.config.ObjectTranslatorTestCase.Thing
				 t)
			{
				object o = (object)_countCache.Get(t.name);
				if (o == null)
				{
					o = 0;
				}
				_countCache.Put(t.name, ((int)o) + 1);
			}

			public virtual int GetCount(com.db4o.db4ounit.common.config.ObjectTranslatorTestCase.Thing
				 t)
			{
				object o = (int)_countCache.Get(t.name);
				if (o == null)
				{
					return 0;
				}
				return ((int)o);
			}

			public virtual object OnInstantiate(com.db4o.ObjectContainer container, object storedObject
				)
			{
				string name = (string)storedObject;
				return new com.db4o.db4ounit.common.config.ObjectTranslatorTestCase.Thing(name);
			}

			public virtual j4o.lang.Class StoredClass()
			{
			    return j4o.lang.Class.GetClassForType(typeof(string));
			}
		}

		private com.db4o.db4ounit.common.config.ObjectTranslatorTestCase.ThingCounterTranslator
			 _trans;

		protected override void Configure(com.db4o.config.Configuration config)
		{
			config.ObjectClass(typeof(com.db4o.db4ounit.common.config.ObjectTranslatorTestCase.Thing)
				).Translate(_trans = new com.db4o.db4ounit.common.config.ObjectTranslatorTestCase.ThingCounterTranslator
				());
		}

		protected override void Store()
		{
			Db().Set(new com.db4o.db4ounit.common.config.ObjectTranslatorTestCase.Thing("jbe"
				));
		}

		public virtual void _testTranslationCount()
		{
			com.db4o.db4ounit.common.config.ObjectTranslatorTestCase.Thing t = (com.db4o.db4ounit.common.config.ObjectTranslatorTestCase.Thing
				)RetrieveOnlyInstance(typeof(com.db4o.db4ounit.common.config.ObjectTranslatorTestCase.Thing)
				);
			Db4oUnit.Assert.IsNotNull(t);
			Db4oUnit.Assert.AreEqual("jbe", t.name);
			Db4oUnit.Assert.AreEqual(1, _trans.GetCount(t));
		}

		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.config.ObjectTranslatorTestCase().RunSolo();
		}
	}
}
