using System;
using System.Collections;
using com.db4o.events;
using com.db4o.ext;

namespace com.db4o.test.events
{
	public class EventRegistryTestCase
	{
		class Item
		{	
		}
		
		class EventRecorder
		{
			ArrayList _events = new ArrayList();

			public EventRecorder(ExtObjectContainer container)
			{
				EventRegistry registry = EventRegistryFactory.ForObjectContainer(container);
				registry.Creating += new CancellableObjectEventHandler(OnCreating);
			}
			
			public string this[int index]
			{
				get { return (string)_events[index];  }
			}

			void OnCreating(object sender, CancellableObjectEventArgs args)
			{
				_events.Add("Creating");
				Tester.Ensure(!args.IsCancelled);
				args.Cancel();
			}
		}
		
		public void TestCreating()
		{
			ExtObjectContainer container = Tester.ObjectContainer();
			EventRecorder recorder = new EventRecorder(container);

			container.Set(new Item());

			Tester.EnsureEquals(0, container.Get(typeof(Item)).Count);
			Tester.EnsureEquals("Creating", recorder[0]);
		}
	}
}
