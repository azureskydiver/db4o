using System;
using com.db4o.config;

namespace com.db4o.test.cs
{
	public class OnActivateEventStrategy
	{
		public event EventHandler Crash;

		public void objectOnActivate(ObjectContainer container)
		{
			Test.ensureEquals(null, Crash);
			Crash += new EventHandler(Boom);
		}

		public void RaiseCrash()
		{
			if (null != Crash)
			{
				Crash(this, EventArgs.Empty);
			}
		}

		static string Message = null;

		public static void Prepare()
		{
			Message = null;
		}

		public static void Assert()
		{
			Test.ensureEquals("Boom!!!!", OnActivateEventStrategy.Message);
		}

		static void Boom(object sender, EventArgs args)
		{
			Message = "Boom!!!!";
		}
	}

	public class CsDelegate
	{
        public event EventHandler Bang;

		static string Message = null;

		public void RaiseBang()
		{
			Bang(this, EventArgs.Empty);
		}

        public void storeOne()
		{
			this.Bang += new EventHandler(theHandler);
        }

        public void testOne()
		{
			// delegate fields are simply not stored
			Test.ensureEquals(null, Bang);
        }

		public void testTSerializableForDelegate()
		{
			Db4o.configure().objectClass(typeof(EventHandler)).translate(new TSerializable());			
			try
			{
				Test.deleteAllInstances(typeof(CsDelegate));
				Test.reOpen();

				CsDelegate.Message = null;
				CsDelegate obj = new CsDelegate();
				obj.Bang += new EventHandler(theHandler);
				Test.store(obj);

				obj = (CsDelegate)Test.getOne(typeof(CsDelegate));
				obj.RaiseBang();
				Test.ensureEquals("Bang!!!!", CsDelegate.Message);
			}
			finally
			{
				Db4o.configure().objectClass(typeof(EventHandler)).translate(new TNull());
			}
		}

		/* TODO: See why this is not working
		public void testOnActivateEventStrategy()
		{
			Test.deleteAllInstances(typeof(OnActivateEventStrategy));
			Test.store(new OnActivateEventStrategy());
            
			OnActivateEventStrategy.Prepare();
			OnActivateEventStrategy obj = (OnActivateEventStrategy)Test.objectContainer().get(typeof(OnActivateEventStrategy)).next();
			obj.RaiseCrash();
			OnActivateEventStrategy.Assert();
		}
		*/

		static void theHandler(object sender, EventArgs args) 
		{
			CsDelegate.Message = "Bang!!!!";
		}
	}
}
