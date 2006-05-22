using System;
using com.db4o.config;

namespace com.db4o.test.cs
{
	public class OnActivateEventStrategy
	{
		public event EventHandler Crash;

		public void ObjectOnActivate(ObjectContainer container)
		{
			Tester.EnsureEquals(null, Crash);
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
			Tester.EnsureEquals("Boom!!!!", OnActivateEventStrategy.Message);
		}

		static void Boom(object sender, EventArgs args)
		{
			Message = "Boom!!!!";
		}
	}

	public class CsDelegate
	{
        public event EventHandler Bang;

		object UntypedDelegate;

		static string Message = null;

		public void RaiseBang()
		{
			Bang(this, EventArgs.Empty);
		}

        public void StoreOne()
		{
			this.Bang += new EventHandler(OnBang);
			this.UntypedDelegate = new EventHandler(OnBang);
        }

        public void TestOne()
		{
			// delegate fields are simply not stored
			Tester.EnsureEquals(null, Bang);
			Tester.EnsureEquals(null, UntypedDelegate);
        }
		
		public void TestOnActivateEventStrategy()
		{
			Tester.DeleteAllInstances(typeof(OnActivateEventStrategy));
			Tester.Store(new OnActivateEventStrategy());
            Tester.ReOpenAll();
            
			OnActivateEventStrategy.Prepare();
			OnActivateEventStrategy obj = (OnActivateEventStrategy)Tester.ObjectContainer().Get(typeof(OnActivateEventStrategy)).Next();
			obj.RaiseCrash();
			OnActivateEventStrategy.Assert();
		}

		static void OnBang(object sender, EventArgs args) 
		{
			CsDelegate.Message = "Bang!!!!";
		}
	}
}
