/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;
using Db4objects.Db4o.TA;
using Db4objects.Db4o.Activation;

namespace Db4objects.Db4odoc.TPClone
{
    public class Car : IActivatable, ICloneable
    {
        private string _model;
        private Pilot _pilot;
        /*activator registered for this class*/
        [System.NonSerialized]
        public IActivator _activator;


        public Car(string model, Pilot pilot)
        {
            _model = model;
            _pilot = pilot;
        }
        // end Car

        /*Bind the class to the specified object container, create the activator*/
        public void Bind(IActivator activator)
        {
            if (_activator == activator)
            {
                return;
            }
            if (activator != null && null != _activator)
            {
                throw new System.InvalidOperationException();
            }
            _activator = activator;
        }
        // end Bind

        /*Call the registered activator to activate the next level,
         * the activator remembers the objects that were already 
         * activated and won't activate them twice. 
         */
        public void Activate(ActivationPurpose purpose)
        {
            if (_activator == null)
                return;
            _activator.Activate(purpose);
        }
        // end Activate

        public object Clone()
        {
            return base.MemberwiseClone();
        }
        // end Clone


        override public string ToString()
        {
            Activate(ActivationPurpose.Read);
            return string.Format("{0}[{1}]", _model, _pilot);
        }
        // end ToString
    }
}
