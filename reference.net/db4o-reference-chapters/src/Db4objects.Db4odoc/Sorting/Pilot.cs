/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
namespace Db4objects.Db4odoc.Sorting
{
    class Pilot
    {
        private string _name;
        private int _points;

        public Pilot(string name)
        {
            _name = name;
        }

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
            if (_points == 0)
            {
                return _name;
            }
            else
            {
                return string.Format("{0}/{1}", _name, _points);
            }
        }
    }
}
