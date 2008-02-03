/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using Db4objects.Db4o;
using Db4objects.Db4o.Activation;
using Db4objects.Db4o.TA;

namespace Db4objects.Db4odoc.TPClone
{
    public class Pilot : IActivatable
    {
        private string _name;

        [System.NonSerialized]
        IActivator _activator;

        public Pilot(string name)
        {
            _name = name;
        }

        // Bind the class to an object container
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

        // activate the object fields
        public void Activate(ActivationPurpose purpose)
        {
            if (_activator == null)
                return;
            _activator.Activate(purpose);
        }

        public string Name
        {
            get
            {
                // even simple string needs to be activated
                Activate(ActivationPurpose.Read);
                return _name;
            }
            set
            {
                Activate(ActivationPurpose.Write);
                _name = value;
            }
        }

        public override string ToString()
        {
            // use Name property, which already contains activation call
            return Name;
        }
    }

}
