/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

namespace Db4objects.Db4odoc.ReplicationEvents
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
        override public string ToString()
        {
            return _name + "/" + _points ;
        }
    }
}
