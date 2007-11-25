/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System.Collections;

using Db4objects.Db4o;
using Db4objects.Db4o.Activation;
using Db4objects.Db4o.TA;
using Db4objects.Db4o.Tests.Common.TA;

namespace Db4ojects.Db4odoc.TAExamples
{
    public class Team : IActivatable
    {

        IList _pilots = new ArrayList4();

        string _name;

        //TA Activator
        [System.NonSerialized]
        IActivator _activator;

        //	Bind the class to an object container
        public void Bind(IActivator activator)
        {
            if (null != _activator)
            {
                throw new System.InvalidOperationException();
            }
            _activator = activator;
        }

        // activate object fields 
        public void Activate()
        {
            if (_activator == null) return;
            _activator.Activate();
        }

        public void AddPilot(Pilot pilot)
        {
			// activate before adding new pilots
			activate();
			_pilots.Add(pilot);
        }

        public void ListAllPilots()
        {
            // activate before printing the collection members
            Activate();

            for (IEnumerator iter = _pilots.GetEnumerator(); iter.MoveNext(); )
            {
                Pilot pilot = (Pilot)iter.Current;
                System.Console.WriteLine(pilot);
            }
        }
    }
}
