/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System.Collections.Generic;
using System.Collections;

using Db4objects.Db4o;
using Db4objects.Db4o.Activation;
using Db4objects.Db4o.TA;
using Db4objects.Db4o.Collections;

namespace Db4ojects.Db4odoc.TAExamples
{
    public class Team : IActivatable
    {

        IList<Pilot> _pilots = new ArrayList4<Pilot>();

      
        
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
        public void Activate(ActivationPurpose purpose)
        {
            if (_activator == null) return;
            _activator.Activate(purpose);
        }

        public void AddPilot(Pilot pilot)
        {
			// activate before adding new pilots
			Activate(ActivationPurpose.Read);
			_pilots.Add(pilot);
        }

        public int Size()
        {
            // activate before returning
            Activate(ActivationPurpose.Read);
            return _pilots.Count;
        }
        // end Size

        public IList<Pilot> Pilots
        {
            get 
            {
                Activate(ActivationPurpose.Read);
                return _pilots; 
            }
        }
        // end Pilots

    }
}
