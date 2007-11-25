/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
namespace Db4objects.Db4odoc.CommitCallbacks
{
    class Car
    {
        private string _name;

        private int _model;

        private Pilot _pilot;

        public Car(string name, int model, Pilot pilot)
        {
            _name = name;
            _model = model;
            _pilot = pilot;
        }

        public int Model
        {
            set
            {
                _model = value;
            }
        }

        public Pilot Pilot
        {
            set
            {
                _pilot = value;
            }
        }

        public override string ToString()
        {
            return string.Format("Car: {0} {1} Pilot: {2} ", _name, _model, _pilot.Name);
        }
    }
}
