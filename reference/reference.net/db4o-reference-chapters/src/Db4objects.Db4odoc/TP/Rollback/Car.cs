/* Copyright (C) 2004 - 2008 db4objects Inc. http://www.db4o.com */
using System;
using Db4objects.Db4o.TA;
using Db4objects.Db4o.Activation;

namespace Db4objects.Db4odoc.TP.Rollback
{
    public class Car : IActivatable
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

        public void Activate(ActivationPurpose purpose)
        {
            if (_activator == null)
                return;
            _activator.Activate(purpose);
        }
        // end Activate

        public string Model
        {
            get 
            {
                Activate(ActivationPurpose.Read);
                return _model;
            }
            set
            {
                Activate(ActivationPurpose.Write);
                _model = value;
            }
        }

        public Pilot Pilot
        {
            get
            {
                Activate(ActivationPurpose.Read);
                return _pilot;
            }
            set
            {
                Activate(ActivationPurpose.Write);
                _pilot = value;
            }
        }

        public void ChangePilot(String name, int id)
        {
            _pilot.Name = name;
            _pilot.Id.Change(id);
        }

        override public string ToString()
        {
            Activate(ActivationPurpose.Read);
            return string.Format("{0}[{1}]", _model, _pilot);
        }
        // end ToString
    }
}
