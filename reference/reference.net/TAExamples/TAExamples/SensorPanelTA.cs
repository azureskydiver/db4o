/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using Db4objects.Db4o;
using Db4objects.Db4o.Activation;
using Db4objects.Db4o.TA;

namespace Db4ojects.Db4odoc.TAExamples
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

        public SensorPanelTA(int value)
        {
            _sensor = value;
        }

        /*Bind the class to the specified object container, create the activator*/
        public void Bind(IActivator activator)
        {
            if (null != _activator)
            {
                throw new System.InvalidOperationException();
            }
            _activator = activator;
        }

        /*Call the registered activator to activate the next level,
         * the activator remembers the objects that were already 
         * activated and won't activate them twice. 
         */
        public void Activate()
        {
            if (_activator == null)
                return;
            _activator.Activate();
        }

        public SensorPanelTA Next
        {
            get
            {
                /*activate direct members*/
                Activate();
                return _next;
            }
        }

        public object Sensor
        {
            get
            {
                /*activate direct members*/
                Activate();
                return _sensor;
            }
        }

        public SensorPanelTA CreateList(int length)
        {
            return CreateList(length, 1);
        }

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

        protected SensorPanelTA NewElement(int value)
        {
            return new SensorPanelTA(value);
        }

        public override string ToString()
        {
            return "Sensor #" + Sensor;
        }
    }

}
