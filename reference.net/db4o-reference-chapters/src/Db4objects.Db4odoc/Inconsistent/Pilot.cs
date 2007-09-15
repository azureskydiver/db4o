/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
namespace Db4objects.Db4odoc.Inconsistent
{
    class Pilot
    {
        private string _name;

        public string Name
        {
            get
            {
                return _name;
            }
        }

        public Pilot(string name)
        {
            _name = name;
        }

        public override string ToString()
        {
            return _name;
        }
    }
}
