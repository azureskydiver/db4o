/* Copyright (C) 2004 - 2008 db4objects Inc. http://www.db4o.com */
using Db4objects.Db4o;
using Db4objects.Db4o.Activation;
using Db4objects.Db4o.TA;

namespace Db4objects.Db4odoc.TP.Rollback
{
    public class Id : IActivatable
    {
        int _number = 0;

        [System.NonSerialized]
        IActivator _activator;

        public Id(int number)
        {
            _number = number;
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

        public void Change(int number)
        {
            Activate(ActivationPurpose.Write);
            _number = number;
        }

        public override string ToString()
        {
            Activate(ActivationPurpose.Read);
            return _number.ToString();
        }
    }

}
