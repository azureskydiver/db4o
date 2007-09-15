/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

namespace Db4objects.Db4odoc.QBE
{
    class Pilot1
    {
        private string _name;
        private int _points;

        public Pilot1(string name, int points)
        {
            _name = name;
            _points = 200;
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

        public override string ToString()
        {
            return string.Format("{0}/{1}", _name, _points);
        }
    }
}
