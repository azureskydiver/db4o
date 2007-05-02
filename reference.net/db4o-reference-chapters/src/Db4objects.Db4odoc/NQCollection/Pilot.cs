/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

namespace Db4objects.Db4odoc.NQCollection
{
    class Pilot : Person
    {
        private string _name;
        private int _points;

        public Pilot(string name, int points)
        {
            _name = name;
            _points = points;
        }

        public string Name
        {
            get
            {
                return _name;
            }
            set
            {
                _name = value;
            }
        }

        public int Points
        {
            get
            {
                return _points;
            }
        }

        public override bool Equals(object obj)
        {
            if (obj is Pilot)
            {
                return (((Pilot)obj).Name.Equals(_name) &&
                        ((Pilot)obj).Points == _points);
            }
            return false;
        }

        public override int GetHashCode()
        {
            return _name.GetHashCode() + _points;
        }

        public override string ToString()
        {
            return string.Format("{0}/{1}", _name, _points);
        }
    }
}
