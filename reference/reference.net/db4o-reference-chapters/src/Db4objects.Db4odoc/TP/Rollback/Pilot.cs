/* Copyright (C) 2004 - 2008 db4objects Inc. http://www.db4o.com */
using Db4objects.Db4o;
using Db4objects.Db4o.Activation;
using Db4objects.Db4o.TA;

namespace Db4objects.Db4odoc.TP.Rollback
{
    public class Pilot : IActivatable
    {
        private string _name;
        private Id _id;

        [System.NonSerialized]
        IActivator _activator;

        public Pilot(string name, int id)
        {
            _name = name;
            _id = new Id(id);
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

        public Id Id
        {
            get
            {
                Activate(ActivationPurpose.Read);
                return _id;
            }
            set
            {
                Activate(ActivationPurpose.Write);
                _id = value;
            }
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
            return string.Format("{0}[{1}]",Name, Id) ;
        }
    }

}
