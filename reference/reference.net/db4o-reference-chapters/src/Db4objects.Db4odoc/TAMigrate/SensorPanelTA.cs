/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using Db4objects.Db4o;
using Db4objects.Db4o.Activation;
using Db4objects.Db4o.TA;

namespace Db4objects.Db4odoc.TAMigrate
{
    public class SensorPanelTA /*must implement Activatable for TA*/: IActivatable
    {
        private object _sensor;

        private SensorPanelTA _next;

        /*activator registered for this class*/
        [System.NonSerialized]
        IActivator _activator;

        public SensorPanelTA()
        {
            // default constructor for instantiation
        }
        // end SensorPanelTA

        public SensorPanelTA(int value)
        {
            _sensor = value;
        }
        // end SensorPanelTA

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

        public SensorPanelTA Next
        {
            get
            {
                /*activate direct members*/
                Activate(ActivationPurpose.Read);
                return _next;
            }
        }
        // end Next

        public object Sensor
        {
            get
            {
                /*activate direct members*/
                Activate(ActivationPurpose.Read);
                return _sensor;
            }
        }
        // end Sensor

        public SensorPanelTA CreateList(int length)
        {
            return CreateList(length, 1);
        }
        // end CreateList

        public SensorPanelTA CreateList(int length, int first)
        {
            int val = first;
            SensorPanelTA root = NewElement(first);
            SensorPanelTA list = root;
            while (--length > 0)
            {
                list._next = NewElement(++val);
                list = list.Next;
            }
            return root;
        }
        // end CreateList

        protected SensorPanelTA NewElement(int value)
        {
            return new SensorPanelTA(value);
        }
        // end NewElement

        public override string ToString()
        {
            return "Sensor #" + Sensor;
        }
        // end ToString
    }

}
