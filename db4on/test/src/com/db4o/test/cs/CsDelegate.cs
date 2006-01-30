using System;
using com.db4o.config;

namespace com.db4o.test.cs
{
	public class OnActivateEventStrategy
	{
		public event EventHandler Crash;

		public void objectOnActivate(ObjectContainer container)
		{
			Tester.ensureEquals(null, Crash);
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
			Tester.ensureEquals("Boom!!!!", OnActivateEventStrategy.Message);
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
			this.Bang += new EventHandler(OnBang);
        }

        public void testOne()
		{
			// delegate fields are simply not stored
			Tester.ensureEquals(null, Bang);
        }
		
		public void testOnActivateEventStrategy()
		{
			Tester.deleteAllInstances(typeof(OnActivateEventStrategy));
			Tester.store(new OnActivateEventStrategy());
            
			OnActivateEventStrategy.Prepare();
			OnActivateEventStrategy obj = (OnActivateEventStrategy)Tester.objectContainer().get(typeof(OnActivateEventStrategy)).next();
			obj.RaiseCrash();
			OnActivateEventStrategy.Assert();
		}

		static void OnBang(object sender, EventArgs args) 
		{
			CsDelegate.Message = "Bang!!!!";
		}
	}
}
