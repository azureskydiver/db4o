/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
namespace Db4objects.Db4odoc.NQCollection
{
    class Car
    {
        private string _model;

        private Pilot _pilot;

        public Car(string model, Pilot pilot)
        {
            _model = model;
            _pilot = pilot;
        }

        public Pilot Pilot
        {
            get
            {
                return _pilot;
            }
        }

        public string Model
        {
            get
            {
                return _model;
            }
        }

        public override string ToString()
        {
            return string.Format("{0}[{1}]", _model, _pilot);
        }
    }
}
