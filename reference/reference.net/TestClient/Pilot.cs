/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
namespace Db4objects.Db4odoc.NoClasses.Client
{
    public class Pilot
    {
        private string _name;
        private int _points;

        public Pilot(string name, int points)
        {
            _name = name;
            _points = points;
        }

        public int Points
        {
            get
            {
                return _points;
            }
            set
            {
                _points = value;
            }
        }

        public void AddPoints(int points)
        {
            _points += points;
        }

        public string Name
        {
            get
            {
                return _name;
            }
        }

        public override string ToString()
        {
            return string.Format("{0}/{1}", _name, _points);
        }
    }
}